package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;

public class GenerateJMEDictGroupWordList {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Wczytywanie słownika edict...");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		final JMEDictEntityMapper entityMapper = new JMEDictEntityMapper();
		
		System.out.println("Wczytywanie słownika...");
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		System.out.println("Walidowanie słownika...");
		
		Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);
		
		System.out.println("Generowanie słów...");
		
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		//List<PolishJapaneseEntry> newMultiWordList = new ArrayList<PolishJapaneseEntry>();
		
		//int newMultiWordListCounter = 0;
		
		KanaHelper kanaHelper = new KanaHelper();
		
		final Map<String, GroupEntry> newWordListAndGroupEntryMap = new HashMap<String, GroupEntry>();
		
		Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
		
		final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
				continue;
			}
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}
			
			List<PolishJapaneseEntry> smallNewWordList = new ArrayList<PolishJapaneseEntry>();
			
			boolean canAdd = true;
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			/*
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == true) {
				
				newMultiWordListCounter++;
				
				for (GroupEntry groupEntry : groupEntryList) {
					
					CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(groupEntry, newMultiWordListCounter);
					
					PolishJapaneseEntry newPolishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;
					
					newMultiWordList.add(newPolishJapaneseEntry);
				}
			}
			*/			
						
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
											
					PolishJapaneseEntry findPolishJapaneseEntry = 
							pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
							groupEntryKanji, groupEntryKana);
					
					if (findPolishJapaneseEntry != null) {
						
						if (findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
							canAdd = false;
						}
						
					} else {
						
						String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntry);
						
						if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
							
							alreadyAddedGroupEntry.add(keyForGroupEntry);
							
							PolishJapaneseEntry newPolishJapaneseEntry = (PolishJapaneseEntry)polishJapaneseEntry.clone();
							
							if (groupEntryKanji == null || groupEntryKanji.equals("") == true) {
								groupEntryKanji = "-";
							}
							
							newPolishJapaneseEntry.setKanji(groupEntryKanji);
															
							newPolishJapaneseEntry.setKana(groupEntryKana);
							
							newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
							
							newPolishJapaneseEntry.setWordType(getWordType(groupEntryKana));
							
							String romaji = null;
							
							WordType wordType = newPolishJapaneseEntry.getWordType();
							
							if (wordType == WordType.HIRAGANA || wordType == WordType.KATAKANA || wordType == WordType.HIRAGANA_KATAKANA || wordType == WordType.KATAKANA_HIRAGANA) {								
								romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(groupEntryKana, kanaHelper.getKanaCache(), true));
								
							} else {
								romaji = "FIXME";
							}
							
							newPolishJapaneseEntry.setRomaji(romaji);
							
							newPolishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());
							
							smallNewWordList.add(newPolishJapaneseEntry);
							
							newWordListAndGroupEntryMap.put(getKeyForNewWordListAndGroupEntry(newPolishJapaneseEntry), groupEntry);
						}							
					}
					
				}
			}
			
			if (canAdd == true) {
				newWordList.addAll(smallNewWordList);
			}
		}
				
		System.out.println("Zapisywanie słownika...");
		
		CsvReaderWriter.generateCsv("input/word-new.csv", newWordList, true, true, false,
				new ICustomAdditionalCsvWriter() {
					
					@Override
					public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
						
						String key = getKeyForNewWordListAndGroupEntry(polishJapaneseEntry);
						
						GroupEntry groupEntry = newWordListAndGroupEntryMap.get(key);
						
						if (groupEntry == null) {
							throw new RuntimeException(key);
						}
						
						List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
						
						///
						
						List<String> translateList2 = new ArrayList<String>();
						
						for (GroupEntryTranslate groupEntryTranslate : translateList) {
							
							StringBuffer translate = new StringBuffer(groupEntryTranslate.getTranslate());
							
							List<String> miscInfoList = groupEntryTranslate.getMiscInfoList();
							List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
							
							boolean wasMiscOrAdditionalInfo = false;
							
							for (int idx = 0; miscInfoList != null && idx < miscInfoList.size(); ++idx) {
								
								if (wasMiscOrAdditionalInfo == false) {
									translate.append(" (");
									
									wasMiscOrAdditionalInfo = true;
									
								} else {
									translate.append(", ");
								}
								
								translate.append(entityMapper.getDesc(miscInfoList.get(idx)));
							}
							
							for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {
								
								if (wasMiscOrAdditionalInfo == false) {
									translate.append(" (");
									
									wasMiscOrAdditionalInfo = true;
									
								} else {
									translate.append(", ");
								}
							}
							
							if (wasMiscOrAdditionalInfo == true) {
								translate.append(")");
							}
							
							translateList2.add(translate.toString());
						}
						
						csvWriter.write(Utils.convertListToString(translateList2));						
					}
				}
		);
		
		/*
		CsvReaderWriter.generateCsv("input/word-multi.csv", newMultiWordList, true, true, false,
				new ICustomAdditionalCsvWriter() {
					
					@Override
					public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
						
						PolishJapaneseEntry findOtherPolishJapaneseEntry = 
								pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
										polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
						
						
						csvWriter.write(Utils.convertListToString(findOtherPolishJapaneseEntry.getTranslates()));						
					}
		});
		*/
	}
	
	private static WordType getWordType(String kana) {
		
		WordType wordType = null;
				
		for (int idx = 0; idx < kana.length(); ++idx) {
			
			char c = kana.charAt(idx);
			
			boolean currentCIsHiragana = Utils.isHiragana(c);
			boolean currentCIsKatakana = Utils.isKatakana(c);
			
			if (currentCIsHiragana == true) {
				
				if (wordType == null) {
					wordType = WordType.HIRAGANA;
					
				} else if (wordType == WordType.KATAKANA) {
					wordType = WordType.KATAKANA_HIRAGANA;					
				}				
			}

			if (currentCIsKatakana == true) {
				
				if (wordType == null) {
					wordType = WordType.KATAKANA;
					
				} else if (wordType == WordType.HIRAGANA) {
					wordType = WordType.HIRAGANA_KATAKANA;					
				}				
			}			
		}	
		
		return wordType;
	}
		
	private static String getKeyForAlreadyAddedGroupEntrySet(GroupEntry groupEntry) {
		
		String key = groupEntry.getGroup().getId() + "." + groupEntry.getWordTypeList().toString() + "." + groupEntry.getKanji() + "." + groupEntry.getKana() + "." + 
				groupEntry.getTranslateList().toString();
		
		return key;
	}
	
	private static String getKeyForNewWordListAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry) {
		
		String key = polishJapaneseEntry.getId() + "." + polishJapaneseEntry.getDictionaryEntryTypeList().toString() + "." + 
				polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji() +
				polishJapaneseEntry.getTranslates().toString();
		
		return key;		
	}
}
