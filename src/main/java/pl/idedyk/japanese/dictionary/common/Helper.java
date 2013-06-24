package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.AttributeType;
import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.WordType;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;

public class Helper {

	public static List<PolishJapaneseEntry> generateGroups(List<PolishJapaneseEntry> polishJapaneseEntries, boolean checkUseEntry, boolean addOtherGroup) {
		
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
				
				if (addOtherGroup == true) {
					groupsForCurrentPolishJapaneseEntry.add("Inne");
				}
				
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
	
	public static void generateAdditionalInfoFromEdict(TreeMap<String, List<JMEDictEntry>> jmedict, TreeMap<String, EDictEntry> jmedictCommon, List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);
			
			List<JMEDictEntry> foundJMEDictList = findJMEdictEntry(jmedict, currentPolishJapaneseEntry);
			EDictEntry foundEdictCommon = findEdictEntry(jmedictCommon, currentPolishJapaneseEntry); 
			
			List<AttributeType> attributeTypeList = currentPolishJapaneseEntry.getAttributeTypeList();
			
			if (foundJMEDictList != null) {
				
				for (JMEDictEntry foundJMEDict : foundJMEDictList) {
					
					// common word
					if (foundEdictCommon != null) {
						
						if (attributeTypeList.contains(AttributeType.COMMON_WORD) == false) {						
							attributeTypeList.add(0, AttributeType.COMMON_WORD);						
						}					
					}			
					
					// suru verb
					DictionaryEntryType dictionaryEntryType = currentPolishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_NOUN) {
						
						if (	foundJMEDict.getPos().contains("n") == true && 
								foundJMEDict.getPos().contains("vs") == true &&
								attributeTypeList.contains(AttributeType.SURU_VERB) == false) {
													
							attributeTypeList.add(AttributeType.SURU_VERB);						
						}					
					}
					
					// transitivity, intransitivity
					if (dictionaryEntryType == DictionaryEntryType.WORD_VERB_U || dictionaryEntryType == DictionaryEntryType.WORD_VERB_RU ||
							dictionaryEntryType == DictionaryEntryType.WORD_VERB_IRREGULAR) {
						
						/*
						if (	attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == true &&
								attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == false &&
								foundJMEDict.getPos().contains("vi") == true) {
							
							System.err.println(currentPolishJapaneseEntry);
							System.err.println(foundJMEDict);
							
							throw new RuntimeException("Different verb transitivity for: " + currentPolishJapaneseEntry);
						}

						if (	attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == true && 
								attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == false &&							
								foundJMEDict.getPos().contains("vt") == true) {
							
							System.err.println(currentPolishJapaneseEntry);
							System.err.println(foundJMEDict);
							
							throw new RuntimeException("Different verb intransitivity for: " + currentPolishJapaneseEntry);
						}
						*/
						
						if (	attributeTypeList.contains(AttributeType.VERB_TRANSITIVITY) == false &&
								attributeTypeList.contains(AttributeType.VERB_INTRANSITIVITY) == false) {
							
							if (foundJMEDict.getPos().contains("vt") == true) {
								attributeTypeList.add(AttributeType.VERB_TRANSITIVITY);
								
							} else if (foundJMEDict.getPos().contains("vi") == true) {
								attributeTypeList.add(AttributeType.VERB_INTRANSITIVITY);
							}						
						}						
					}					
				}				
			}			
		}
	}
	
	private static EDictEntry findEdictEntry(TreeMap<String, EDictEntry> jmedict, PolishJapaneseEntry polishJapaneseEntry) {

		String kanji = polishJapaneseEntry.getKanji();

		if (kanji != null && kanji.equals("-") == true) {
			kanji = null;
		}

		List<String> kanaList = polishJapaneseEntry.getKanaList();

		EDictEntry foundEdict = null;

		for (String currentKana : kanaList) {

			foundEdict = jmedict.get(EdictReader.getMapKey(kanji, currentKana));

			if (foundEdict != null) {
				break;
			}
		}

		return foundEdict;
	}
	
	private static List<JMEDictEntry> findJMEdictEntry(TreeMap<String, List<JMEDictEntry>> jmedict, PolishJapaneseEntry polishJapaneseEntry) {
		
		String kanji = polishJapaneseEntry.getKanji();
		
		if (kanji != null && kanji.equals("-") == true) {
			kanji = null;
		}
		
		List<String> kanaList = polishJapaneseEntry.getKanaList();
		
		List<JMEDictEntry> foundEdict = null;
		
		for (String currentKana : kanaList) {
			
			foundEdict = jmedict.get(JMEDictReader.getMapKey(kanji, currentKana));
			
			if (foundEdict != null) {
				break;
			}
		}
		
		return foundEdict;
	}
	
	public static void generateNames(TreeMap<String, EDictEntry> jmedictName, List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		Iterator<EDictEntry> iterator = jmedictName.values().iterator();
		
		int counter = 1000000;
				
		// [Thailand, former, Dalai, SNK, Niigata, Sony, st, f, g, uk, d, c, or, n, o, abbr, ik, m, Nintendo, h, Sega, Republic, u, co, 1, NEC, s, Bandai, deity, p, pr]
		
		// surname -> s
		// masc -> m
		// fem -> f
		// given -> g
		
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> kitakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		
		while(iterator.hasNext()) {
			EDictEntry edictNameEntry = iterator.next();
			
			String kanji = edictNameEntry.getKanji();
			String kana = edictNameEntry.getKana();
			String name = edictNameEntry.getName();			
			List<String> pos = edictNameEntry.getPos();
			
			if (name == null) {
				continue;
			}
			
			PolishJapaneseEntry newPolishJapaneseEntry = new PolishJapaneseEntry();
			
			newPolishJapaneseEntry.setId(counter);
			
			if (pos.contains("f") == true) {
				newPolishJapaneseEntry.setDictionaryEntryType(DictionaryEntryType.WORD_FEMALE_NAME);
				
			} else if (pos.contains("m") == true) {
				newPolishJapaneseEntry.setDictionaryEntryType(DictionaryEntryType.WORD_MALE_NAME);
				
			} else if (pos.contains("g") == true) {
				newPolishJapaneseEntry.setDictionaryEntryType(DictionaryEntryType.WORD_NAME);
				
			} else if (pos.contains("s") == true) {
				newPolishJapaneseEntry.setDictionaryEntryType(DictionaryEntryType.WORD_SURNAME_NAME);
				
			} else {
				continue;
			}
			
			newPolishJapaneseEntry.setWordType(WordType.HIRAGANA_KATAKANA);
			
			newPolishJapaneseEntry.setAttributeTypeList(new ArrayList<AttributeType>());
			newPolishJapaneseEntry.setGroups(new ArrayList<String>());
			
			newPolishJapaneseEntry.setKanji(kanji != null ? kanji : "-");
			
			List<String> kanaList = new ArrayList<String>();
			kanaList.add(kana);
			
			newPolishJapaneseEntry.setKanaList(kanaList);
			
			try {
				List<String> romajiList = new ArrayList<String>();
				romajiList.add(KanaHelper.createRomajiString(KanaHelper.convertKanaStringIntoKanaWord(kana, hiraganaEntries, kitakanaEntries)));
				
				newPolishJapaneseEntry.setRomajiList(romajiList);
				
			} catch (RuntimeException e) {
				continue;
			}
			
			List<String> polishTranslateList = new ArrayList<String>();
			polishTranslateList.add(name);
			
			newPolishJapaneseEntry.setPolishTranslates(polishTranslateList);
			
			newPolishJapaneseEntry.setUseEntry(true);
			
			polishJapaneseEntries.add(newPolishJapaneseEntry);
			
			counter++;
		}	
	}
}
