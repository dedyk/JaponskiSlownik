package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class Helper {

	public static List<PolishJapaneseEntry> generateGroups(List<PolishJapaneseEntry> polishJapaneseEntries, boolean checkUseEntry) {
		
		// generate groups
		
		Map<String, List<String>> polishJapaneseEntriesAndGroups = new HashMap<String, List<String>>();
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);
			
			if (currentPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			List<String> currentPolishJapaneseEntryGroups = currentPolishJapaneseEntry.getGroups();

			if (currentPolishJapaneseEntryGroups == null || currentPolishJapaneseEntryGroups.size() == 0) {
				continue;
			}
						
			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();
			
			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups.get(entryPrefixKanaKanjiKanaKey);
			
			if (groupsForCurrentPolishJapaneseEntry == null) {
				groupsForCurrentPolishJapaneseEntry = new ArrayList<String>();
			}
			
			for (String currentEntryOfCurrentPolishJapaneseEntryGroups : currentPolishJapaneseEntryGroups) {
				
				if (groupsForCurrentPolishJapaneseEntry.contains(currentEntryOfCurrentPolishJapaneseEntryGroups) == false) {
					groupsForCurrentPolishJapaneseEntry.add(currentEntryOfCurrentPolishJapaneseEntryGroups);
				}
			}
			
			polishJapaneseEntriesAndGroups.put(entryPrefixKanaKanjiKanaKey, groupsForCurrentPolishJapaneseEntry);			
		}
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);
			
			if (currentPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();
			
			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups.get(entryPrefixKanaKanjiKanaKey);
			
			if (groupsForCurrentPolishJapaneseEntry == null) {
				groupsForCurrentPolishJapaneseEntry = new ArrayList<String>();
				
				groupsForCurrentPolishJapaneseEntry.add("Inne");
			}
			
			currentPolishJapaneseEntry.setGroups(groupsForCurrentPolishJapaneseEntry);
		}		
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
				
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			if (polishJapaneseEntries.get(idx).getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			if (checkUseEntry == false || polishJapaneseEntries.get(idx).isUseEntry() == true) {
				polishJapaneseEntries.get(idx).setId(result.size() + 1);
				
				result.add(polishJapaneseEntries.get(idx));
			}
		}	
		
		return result;
	}
}
