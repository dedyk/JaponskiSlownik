package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;

public class GenerateJMEDictGroupWordList {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Wczytywanie słownika edict...");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		System.out.println("Wczytywanie słownika...");
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		System.out.println("Walidowanie słownika...");
		
		Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);
		
		System.out.println("Generowanie słów...");
		
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		
		KanaHelper kanaHelper = new KanaHelper();
		
		final Map<String, GroupEntry> newWordListAndGroupEntryMap = new HashMap<String, GroupEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
				continue;
			}
			
			List<PolishJapaneseEntry> smallNewWordList = new ArrayList<PolishJapaneseEntry>();
			
			boolean canAdd = true;
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKanaList().get(0);
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
						
			if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
					
					List<GroupEntry> groupEntryList2 = jmeNewDictionary.getGroupEntryList(groupEntryKanji, groupEntryKana);
					
					if (isMultiGroup(groupEntryList2) == false) {
						
						PolishJapaneseEntry findPolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntries, 
								groupEntryKanji, groupEntryKana);
						
						if (findPolishJapaneseEntry != null) {
							
							if (findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
								canAdd = false;
							}
							
						} else {
							PolishJapaneseEntry newPolishJapaneseEntry = (PolishJapaneseEntry)polishJapaneseEntry.clone();
							
							if (groupEntryKanji == null || groupEntryKanji.equals("") == true) {
								groupEntryKanji = "-";
							}
							
							newPolishJapaneseEntry.setKanji(groupEntryKanji);
							
							List<String> kanaList = new ArrayList<String>();
							kanaList.add(groupEntryKana);
							
							newPolishJapaneseEntry.setKanaList(kanaList);
							
							newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
							
							List<String> romajiList = new ArrayList<String>();
							
							WordType wordType = newPolishJapaneseEntry.getWordType();
							
							if (wordType == WordType.HIRAGANA || wordType == WordType.KATAKANA || wordType == WordType.HIRAGANA_KATAKANA || wordType == WordType.KATAKANA_HIRAGANA) {								
								romajiList.add(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(groupEntryKana, kanaHelper.getKanaCache(), true)));
								
							} else {
								romajiList.add("FIXME");
							}
							
							newPolishJapaneseEntry.setRomajiList(romajiList);
							
							newPolishJapaneseEntry.setKnownDuplicatedId(new HashSet<Integer>());
							
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
						
						csvWriter.write(Utils.convertListToString(groupEntry.getTranslateList()));						
					}
				}
		);
	}	
	
	private static boolean isMultiGroup(List<GroupEntry> groupEntryList) {
		
		Set<Integer> uniqueGroupIds = new HashSet<Integer>();
		
		for (GroupEntry groupEntry : groupEntryList) {
			uniqueGroupIds.add(groupEntry.getGroup().getId());
		}
		
		if (uniqueGroupIds.size() == 1) {			
			return false;
			
		} else {
			return true;
		}
	}
	
	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			String findKanji, String findKana) {
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKanaList().get(0);
			
			if (kanji == null || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}

			if (findKanji == null || findKanji.equals("-") == true) {
				findKanji = "$$$NULL$$$";
			}
			
			if (kanji.equals(findKanji) == true && kana.equals(findKana) == true) {
				return polishJapaneseEntry;
			}
		}
		
		return null;
	}
	
	private static String getKeyForNewWordListAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry) {
		
		String key = polishJapaneseEntry.getId() + "." + polishJapaneseEntry.getDictionaryEntryTypeList().toString() + "." + 
				polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKanaList().get(0) + "." + polishJapaneseEntry.getRomajiList().get(0) +
				polishJapaneseEntry.getTranslates().toString();
		
		return key;		
	}
}
