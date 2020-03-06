package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class DetectMultipleKanaList {

	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		//
		
		Set<Integer> groupIdSetInPolishJapaneseEntriesListSet = new TreeSet<>();
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			Integer groupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
			
			if (groupIdFromJmedictRawDataList != null) {				
				groupIdSetInPolishJapaneseEntriesListSet.add(groupIdFromJmedictRawDataList);
			}
		}
		
		//
		
		JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
		
		List<Group> groupList = jmeNewDictionary.getGroupList();
		
		Map<String, Map<String, List<GroupEntry>>> kanjiSet = new TreeMap<>();
		
		for (Group group : groupList) {
						
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				String kanji = groupEntry.getKanji();
				
				if (kanji == null) {
					kanji = "-";
				}
				
				Map<String, List<GroupEntry>> kanaMap = kanjiSet.get(kanji);
				
				if (kanaMap == null) {
					kanaMap = new TreeMap<>();
					
					kanjiSet.put(kanji, kanaMap);
				}
				
				//
				
				String kana = groupEntry.getKana();
				
				List<GroupEntry> kanaGroupEntryList = kanaMap.get(kana);
				
				if (kanaGroupEntryList == null) {
					kanaGroupEntryList = new ArrayList<>();
					
					kanaMap.put(kana, kanaGroupEntryList);
				}
				
				//
				
				kanaGroupEntryList.add(groupEntry);
			}			
		}
		
		//
		
		Iterator<String> kanjiIterator = kanjiSet.keySet().iterator();
		
		while (kanjiIterator.hasNext() == true) {
			
			String kanji = kanjiIterator.next();
			
			//
			
			Map<String, List<GroupEntry>> kanaMap = kanjiSet.get(kanji);
			
			//
			
			Iterator<String> kanaMapIterator = kanaMap.keySet().iterator();
			
			while (kanaMapIterator.hasNext() == true) {
				
				String kana = kanaMapIterator.next();
				
				List<GroupEntry> groupEntryList = kanaMap.get(kana);
				
				if (groupEntryList.size() > 1) { // mamy kandydatow
					
					for (GroupEntry groupEntry : groupEntryList) {
						
						if (groupIdSetInPolishJapaneseEntriesListSet.contains(groupEntry.getGroup().getId()) == false) { // mamy							
							System.out.println(groupEntry.getGroup().getId() + " - " + groupEntry.getKanji() + " - " + groupEntry.getKana());			
						}
					}
				}
			}
		}
	}
}
