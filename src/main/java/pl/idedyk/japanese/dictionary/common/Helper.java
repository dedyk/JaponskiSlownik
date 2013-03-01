package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.AttributeType;
import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.JMEdictReader;

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
	
	public static void generateAdditionalInfoFromEdict(TreeMap<String, EDictEntry> jmedict, List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			String kanji = currentPolishJapaneseEntry.getKanji();
			
			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();
			
			EDictEntry foundEdict = null;
			
			for (String currentKana : kanaList) {
				foundEdict = jmedict.get(JMEdictReader.getMapKey(kanji, currentKana));
				
				if (foundEdict != null) {
					break;
				}
			}
			
			if (foundEdict != null) {
				
				DictionaryEntryType dictionaryEntryType = currentPolishJapaneseEntry.getDictionaryEntryType();
				
				List<AttributeType> attributeTypeList = currentPolishJapaneseEntry.getAttributeTypeList();
				
				if (dictionaryEntryType == DictionaryEntryType.WORD_NOUN) {
					
					if (	foundEdict.getPos().contains("n") == true && 
							foundEdict.getPos().contains("vs") == true &&
							attributeTypeList.contains(AttributeType.SURU_VERB) == false) {
												
						attributeTypeList.add(AttributeType.SURU_VERB);						
					}					
				}				
			}			
		}
	}
}
