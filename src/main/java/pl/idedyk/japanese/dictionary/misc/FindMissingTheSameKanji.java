package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.common.Helper.CreatePolishJapaneseEntryResult;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class FindMissingTheSameKanji {

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
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji);
			
			if (groupEntryList == null) {
				continue;
			}
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				List<GroupEntry> groupEntryListForGroupEntry = groupEntry.getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
				
				List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryListForGroupEntry);
								
				for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
					
					GroupEntry groupEntryInGroup = theSameTranslateGroupEntryList.get(0);
											
					String groupEntryKanji = groupEntryInGroup.getKanji();
					String groupEntryKana = groupEntryInGroup.getKana();
					
					List<PolishJapaneseEntry> findPolishJapaneseEntryList = 
							pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
	
					if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
						
						String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntryInGroup);
						
						if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
							
							alreadyAddedGroupEntry.add(keyForGroupEntry);
							
							//
							
							CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryInGroup, 
									polishJapaneseEntry.getId(), null);
							
							PolishJapaneseEntry newPolishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;								
	
							newWordList.add(newPolishJapaneseEntry);						
						}
					}
				}
			}			
		}
		
		CsvReaderWriter.generateCsv("input/missing-the-same-kanji.csv", newWordList, true, true, false);
	}
	
	private static String getKeyForAlreadyAddedGroupEntrySet(GroupEntry groupEntry) {
		
		String key = groupEntry.getGroup().getId() + "." + groupEntry.getWordTypeList().toString() + "." + groupEntry.getKanji() + "." + groupEntry.getKana() + "." + 
				groupEntry.getTranslateList().toString();
		
		return key;
	}
}
