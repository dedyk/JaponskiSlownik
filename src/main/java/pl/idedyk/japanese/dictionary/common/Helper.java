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
import pl.idedyk.japanese.dictionary.tools.EdictReader;

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
			
			if (kanji != null && kanji.equals("-") == true) {
				kanji = null;
			}
			
			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();
			
			EDictEntry foundEdict = null;
			
			for (String currentKana : kanaList) {
				
				foundEdict = jmedict.get(EdictReader.getMapKey(kanji, currentKana));
				
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
				
				if (dictionaryEntryType == DictionaryEntryType.WORD_VERB_U || dictionaryEntryType == DictionaryEntryType.WORD_VERB_RU ||
						dictionaryEntryType == DictionaryEntryType.WORD_VERB_IRREGULAR) {
					
					if (	attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == true &&
							attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == false &&
							foundEdict.getPos().contains("vi") == true) {
						
						System.err.println(currentPolishJapaneseEntry);
						System.err.println(foundEdict);
						
						throw new RuntimeException("Different verb transitivity for: " + currentPolishJapaneseEntry);
					}

					if (	attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == true && 
							attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == false &&							
							foundEdict.getPos().contains("vt") == true) {
						
						System.err.println(currentPolishJapaneseEntry);
						System.err.println(foundEdict);
						
						throw new RuntimeException("Different verb intransitivity for: " + currentPolishJapaneseEntry);
					}
					
					if (	attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == false &&
							attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == false) {
						
						if (foundEdict.getPos().contains("vt") == true) {
							attributeTypeList.add(AttributeType.VERB_TRANSITIVITY);
							
						} else if (foundEdict.getPos().contains("vi") == true) {
							attributeTypeList.add(AttributeType.VERB_INTRANSITIVITY);
						}						
					}						
				}
			}			
		}
	}
}
