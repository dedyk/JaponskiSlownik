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
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.common.Helper.CreatePolishJapaneseEntryResult;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;

public class GenerateJMEDictGroupWordList2 {

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
				
		final Map<String, PolishJapaneseEntry> newWordListAndPolishJapaneseEntryMap = new HashMap<String, PolishJapaneseEntry>();
		
		Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
		
		final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);
				
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
				continue;
			}

			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_JMEDICT_ALTERNATIVE) == true) {
				continue;
			}
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
									
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				groupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
				
				List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
				
				for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
					
					for (GroupEntry groupEntry : theSameTranslateGroupEntryList) {
						
						String groupEntryKanji = groupEntry.getKanji();
						String groupEntryKana = groupEntry.getKana();

						PolishJapaneseEntry findPolishJapaneseEntry = 
								pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
								groupEntryKanji, groupEntryKana);

						if (findPolishJapaneseEntry == null) {
							
							String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntry);
							
							if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
								
								alreadyAddedGroupEntry.add(keyForGroupEntry);

								//
								
								CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, 
										polishJapaneseEntry.getId(), null);
								
								PolishJapaneseEntry newPolishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;								

								newWordList.add(newPolishJapaneseEntry);
								
								newWordListAndPolishJapaneseEntryMap.put(getKeyForNewWordListAndGroupEntry(newPolishJapaneseEntry), polishJapaneseEntry);
							}							
						}
					}
				}
			}
		}
		
		System.out.println(newWordList.size());
				
		System.out.println("Zapisywanie słownika...");
		
		CsvReaderWriter.generateCsv("input/word-new2.csv", newWordList, true, true, false,
				new ICustomAdditionalCsvWriter() {
					
					@Override
					public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
						
						String key = getKeyForNewWordListAndGroupEntry(polishJapaneseEntry);
						
						PolishJapaneseEntry sourcePolishJapaneseEntry = newWordListAndPolishJapaneseEntryMap.get(key);
						
						if (sourcePolishJapaneseEntry == null) {
							throw new RuntimeException(key);
						}
												
						csvWriter.write(Utils.convertListToString(sourcePolishJapaneseEntry.getTranslates()));
						csvWriter.write(sourcePolishJapaneseEntry.getInfo());
					}
				}
		);
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
