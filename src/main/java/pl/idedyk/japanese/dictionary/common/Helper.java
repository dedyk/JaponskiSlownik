package pl.idedyk.japanese.dictionary.common;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.TransitiveIntransitivePair;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

import com.csvreader.CsvWriter;

public class Helper {

	public static List<PolishJapaneseEntry> generateGroups(List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addOtherGroup) {

		// generate groups

		Map<String, List<String>> polishJapaneseEntriesAndGroups = new HashMap<String, List<String>>();

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			List<String> currentPolishJapaneseEntryGroups = GroupEnum.convertToValues(currentPolishJapaneseEntry.getGroups());

			if (currentPolishJapaneseEntryGroups == null || currentPolishJapaneseEntryGroups.size() == 0) {
				continue;
			}

			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();

			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups
					.get(entryPrefixKanaKanjiKanaKey);

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

			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();

			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups
					.get(entryPrefixKanaKanjiKanaKey);

			if (groupsForCurrentPolishJapaneseEntry == null) {
				groupsForCurrentPolishJapaneseEntry = new ArrayList<String>();

				if (addOtherGroup == true) {
					groupsForCurrentPolishJapaneseEntry.add("Inne");
				}

			}

			currentPolishJapaneseEntry.setGroups(GroupEnum.convertToListGroupEnum(groupsForCurrentPolishJapaneseEntry));
		}

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			polishJapaneseEntries.get(idx).setId(result.size() + 1);

			result.add(polishJapaneseEntries.get(idx));
		}

		return result;
	}

	public static void generateAdditionalInfoFromEdict(TreeMap<String, List<JMEDictEntry>> jmedict,
			TreeMap<String, EDictEntry> jmedictCommon, List<PolishJapaneseEntry> polishJapaneseEntries) {

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			List<JMEDictEntry> foundJMEDictList = findJMEdictEntry(jmedict, currentPolishJapaneseEntry);
			EDictEntry foundEdictCommon = findEdictEntry(jmedictCommon, currentPolishJapaneseEntry);

			AttributeList attributeList = currentPolishJapaneseEntry.getAttributeList();

			if (foundJMEDictList != null) {

				for (JMEDictEntry foundJMEDict : foundJMEDictList) {

					// common word
					if (foundEdictCommon != null) {

						if (attributeList.contains(AttributeType.COMMON_WORD) == false) {
							attributeList.add(0, AttributeType.COMMON_WORD);
						}
					}

					// suru verb
					List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry
							.getDictionaryEntryTypeList();

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == true) {

						if (foundJMEDict.getPos().contains("n") == true && foundJMEDict.getPos().contains("vs") == true
								&& attributeList.contains(AttributeType.SURU_VERB) == false) {

							attributeList.add(AttributeType.SURU_VERB);
						}
					}

					// transitivity, intransitivity
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true) {

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

						if (attributeList.contains(AttributeType.VERB_TRANSITIVITY) == false
								&& attributeList.contains(AttributeType.VERB_INTRANSITIVITY) == false) {

							if (foundJMEDict.getPos().contains("vt") == true) {
								attributeList.add(AttributeType.VERB_TRANSITIVITY);

							} else if (foundJMEDict.getPos().contains("vi") == true) {
								attributeList.add(AttributeType.VERB_INTRANSITIVITY);
							}
						}
					}

					// noun/na-adjective
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == true
							&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_NA) == false
							&& foundJMEDict.getPos().contains("adj-na") == true) {

						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_ADJECTIVE_NA);
					}

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_NA) == true
							&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == false
							&& foundJMEDict.getPos().contains("n") == true) {

						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NOUN);
					}

					// kanji/kana alone
					if (attributeList.contains(AttributeType.KANJI_ALONE) == false
							&& foundJMEDict.getMisc().contains("uK") == true) {
						attributeList.add(AttributeType.KANJI_ALONE);
					}

					if (attributeList.contains(AttributeType.KANA_ALONE) == false
							&& foundJMEDict.getMisc().contains("uk") == true) {
						attributeList.add(AttributeType.KANA_ALONE);
					}

					// archaism
					if (attributeList.contains(AttributeType.ARCHAISM) == false
							&& foundJMEDict.getMisc().contains("arch") == true) {
						attributeList.add(AttributeType.ARCHAISM);
					}

					// obsolete
					if (attributeList.contains(AttributeType.OBSOLETE) == false
							&& (foundJMEDict.getMisc().contains("obs") == true || foundJMEDict.getMisc().contains("ok") == true)) {
						attributeList.add(AttributeType.OBSOLETE);
					}

					// obsure
					if (attributeList.contains(AttributeType.OBSCURE) == false
							&& foundJMEDict.getMisc().contains("obsc") == true) {
						attributeList.add(AttributeType.OBSCURE);
					}

					// suffix
					if (attributeList.contains(AttributeType.SUFFIX) == false
							&& foundJMEDict.getPos().contains("suf") == true) {
						attributeList.add(AttributeType.SUFFIX);
					}

					// noun suffix
					if (attributeList.contains(AttributeType.NOUN_SUFFIX) == false
							&& foundJMEDict.getPos().contains("n-suf") == true) {
						attributeList.add(AttributeType.NOUN_SUFFIX);
					}

					// prefix
					if (attributeList.contains(AttributeType.PREFIX) == false
							&& foundJMEDict.getPos().contains("pref") == true) {
						attributeList.add(AttributeType.PREFIX);
					}

					// noun prefix
					if (attributeList.contains(AttributeType.NOUN_PREFIX) == false
							&& foundJMEDict.getPos().contains("n-pref") == true) {
						attributeList.add(AttributeType.NOUN_PREFIX);
					}
					
					// adjective-no
					if (attributeList.contains(AttributeType.NOUN_ADJECTIVE_NO) == false
							&& foundJMEDict.getPos().contains("adj-no") == true) {
						attributeList.add(AttributeType.NOUN_ADJECTIVE_NO);
					}
					
					// onamatopoeic or mimetic word
					if (attributeList.contains(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD) == false
							&& foundJMEDict.getMisc().contains("on-mim") == true) {
						attributeList.add(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD);
					}

				}
			}
		}
	}

	private static EDictEntry findEdictEntry(TreeMap<String, EDictEntry> jmedict,
			PolishJapaneseEntry polishJapaneseEntry) {

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

	private static List<JMEDictEntry> findJMEdictEntry(TreeMap<String, List<JMEDictEntry>> jmedict,
			PolishJapaneseEntry polishJapaneseEntry) {

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

	public static List<PolishJapaneseEntry> generateNames(TreeMap<String, List<JMEDictEntry>> jmedictName) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		Iterator<List<JMEDictEntry>> jmedictNameValuesIterator = jmedictName.values().iterator();

		/*
		- company -
		+ fem +
		+ given +
		+ masc +
		- organization -
		+ person +
		+ place * ?
		- product -
		+ station +
		+ surname +
		- unclass * ?
		*/

		// mapowanie typow
		Map<String, DictionaryEntryType> nameTypeMapper = new HashMap<String, DictionaryEntryType>();
		
		nameTypeMapper.put("fem", DictionaryEntryType.WORD_FEMALE_NAME);
		nameTypeMapper.put("masc", DictionaryEntryType.WORD_MALE_NAME);
		nameTypeMapper.put("given", DictionaryEntryType.WORD_NAME);
		nameTypeMapper.put("surname", DictionaryEntryType.WORD_SURNAME_NAME);
		nameTypeMapper.put("person", DictionaryEntryType.WORD_PERSON);		
		nameTypeMapper.put("station", DictionaryEntryType.WORD_STATION_NAME);
		nameTypeMapper.put("place", DictionaryEntryType.WORD_PLACE);
		
		int counter = 1;

		KanaHelper kanaHelper = new KanaHelper();
		
		while(jmedictNameValuesIterator.hasNext()) {
			
			List<JMEDictEntry> jmedictValueList = jmedictNameValuesIterator.next();

			for (JMEDictEntry jmedictEntry : jmedictValueList) {
				
				if (jmedictEntry.getTrans().size() == 0) {
					continue;
				}
				
				List<DictionaryEntryType> nameDictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
				
				for (String currentTran : jmedictEntry.getTrans()) {
					
					DictionaryEntryType nameDictionaryEntryType = nameTypeMapper.get(currentTran);
					
					if (nameDictionaryEntryType == null) {
						continue;
					}
					
					nameDictionaryEntryTypeList.add(nameDictionaryEntryType);
				}
				
				if (nameDictionaryEntryTypeList.size() == 0) {
					continue;
				}
				
				if (nameDictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PERSON) == true) {
					
					nameDictionaryEntryTypeList.remove(DictionaryEntryType.WORD_FEMALE_NAME);					
				}
				
				for (int idx = 0; idx < jmedictEntry.getKanji().size(); ++idx) {
					
					String kanji = jmedictEntry.getKanji().get(idx);
					String kana = jmedictEntry.getKana().get(idx);
					String transDet = jmedictEntry.getTransDet().get(idx);
					
					//System.out.println(kanji + " - " + kana + " - " + transDet);
					
					PolishJapaneseEntry newPolishJapaneseEntry = new PolishJapaneseEntry();
					
					newPolishJapaneseEntry.setId(counter);
					counter++;
					
					newPolishJapaneseEntry.setDictionaryEntryTypeList(nameDictionaryEntryTypeList);
					
					newPolishJapaneseEntry.setWordType(WordType.HIRAGANA_KATAKANA);
					
					newPolishJapaneseEntry.setAttributeList(new AttributeList());
					newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());

					newPolishJapaneseEntry.setKanji(kanji != null ? kanji : "-");

					List<String> kanaList = new ArrayList<String>();
					kanaList.add(kana);

					newPolishJapaneseEntry.setKanaList(kanaList);

					List<String> romajiList = new ArrayList<String>();
					romajiList.add(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true)));

					newPolishJapaneseEntry.setRomajiList(romajiList);

					List<String> polishTranslateList = new ArrayList<String>();
					polishTranslateList.add(transDet);

					newPolishJapaneseEntry.setTranslates(polishTranslateList);

					newPolishJapaneseEntry.setParseAdditionalInfoList(new ArrayList<ParseAdditionalInfo>());

					//newPolishJapaneseEntry.setUseEntry(true);

					newPolishJapaneseEntry.setExampleSentenceGroupIdsList(new ArrayList<String>());
					
					fixPolishJapaneseEntryName(newPolishJapaneseEntry);
					
					result.add(newPolishJapaneseEntry);
				}
			}
		}
		
		return result;
	}
	
	private static void fixPolishJapaneseEntryName(PolishJapaneseEntry newPolishJapaneseEntry) {
				
		if (newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_FEMALE_NAME ||
				newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_MALE_NAME ||
				newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_PERSON) {
			
			String translate = newPolishJapaneseEntry.getTranslates().get(0);
			
			List<String> romajiList = newPolishJapaneseEntry.getRomajiList();
			
			List<String> newRomajiList = new ArrayList<String>();
			
			for (String currentRomaji : romajiList) {
				newRomajiList.add(fixRomajiForNames(currentRomaji, translate));
			}
			
			newPolishJapaneseEntry.setRomajiList(newRomajiList);			
		}
		
		if (newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_STATION_NAME) {
			
			String translate = newPolishJapaneseEntry.getTranslates().get(0);
			
			translate = translate.replaceAll("Station", "(nazwa stacji)");
			
			List<String> newTranslateList = new ArrayList<String>();
			newTranslateList.add(translate);
			
			newPolishJapaneseEntry.setTranslates(newTranslateList);
		}
		
		
		
	}

	private static String fixRomajiForNames(String romaji, String transDet) {
				
		int transAdd = 0;
		
		StringBuffer result = new StringBuffer();
		
		for (int romajiIdx = 0; romajiIdx < romaji.length(); ++romajiIdx) {
			
			String currentRomajiChar = ("" + romaji.charAt(romajiIdx)).toLowerCase();
			
			String currentTransDetChar = null;
			
			if (romajiIdx + transAdd < transDet.length()) {
				currentTransDetChar = ("" + transDet.charAt(romajiIdx + transAdd)).toLowerCase();
			}
			
			if (currentTransDetChar == null) {
				result = null;
				
				break;
			}
			
			if (currentTransDetChar.equals(" ") == true || currentTransDetChar.equals("-") == true) {
				
				result.append(" ");
				
				transAdd++;
				
				if (romajiIdx + transAdd < transDet.length()) {
					currentTransDetChar = ("" + transDet.charAt(romajiIdx + transAdd)).toLowerCase();
				}				
			}
			
			if (currentTransDetChar == null) {
				result = null;
				
				break;
			}
			
			if (currentRomajiChar.equals(currentTransDetChar) == true) {
				result.append(currentRomajiChar);
				
			} else {
				result = null;
				
				break;
			}
		}
		
		if (result != null) {
			return result.toString();
			
		} else {
			return romaji;
		}		
	}

	public static void generateTransitiveIntransitivePairs(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList,
			List<PolishJapaneseEntry> polishJapaneseEntryList, String transitiveIntransitivePairsOutputFile)
			throws Exception {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {

			String kanji = polishJapaneseEntry.getKanji();
			List<String> kanaList = polishJapaneseEntry.getKanaList();

			AttributeList attributeList = polishJapaneseEntry.getAttributeList();

			if (attributeList.contains(AttributeType.VERB_TRANSITIVITY) == true) {

				TransitiveIntransitivePair transitiveIntransitivePair = null;

				for (String currentKana : kanaList) {

					transitiveIntransitivePair = findTransitiveIntransitivePairFromTransitiveVerb(
							transitiveIntransitivePairList, kanji, currentKana);

					if (transitiveIntransitivePair != null) {
						break;
					}
				}

				if (transitiveIntransitivePair != null) {

					PolishJapaneseEntry intransitivePolishJapaneseEntry = findPolishJapaneseEntry(
							polishJapaneseEntryList, transitiveIntransitivePair.getIntransitiveKanji(),
							transitiveIntransitivePair.getIntransitiveKana());

					if (intransitivePolishJapaneseEntry != null) {

						attributeList.addAttributeValue(AttributeType.VERB_INTRANSITIVITY_PAIR,
								String.valueOf(intransitivePolishJapaneseEntry.getId()));
					}
				}

			} else if (attributeList.contains(AttributeType.VERB_INTRANSITIVITY) == true) {

				TransitiveIntransitivePair transitiveIntransitivePair = null;

				for (String currentKana : kanaList) {

					transitiveIntransitivePair = findTransitiveIntransitivePairFromIntransitiveVerb(
							transitiveIntransitivePairList, kanji, currentKana);

					if (transitiveIntransitivePair != null) {
						break;
					}
				}

				if (transitiveIntransitivePair != null) {

					PolishJapaneseEntry transitivePolishJapaneseEntry = findPolishJapaneseEntry(
							polishJapaneseEntryList, transitiveIntransitivePair.getTransitiveKanji(),
							transitiveIntransitivePair.getTransitiveKana());

					if (transitivePolishJapaneseEntry != null) {

						attributeList.addAttributeValue(AttributeType.VERB_TRANSITIVITY_PAIR,
								String.valueOf(transitivePolishJapaneseEntry.getId()));
					}
				}
			}
		}

		CsvWriter csvWriter = new CsvWriter(new FileWriter(transitiveIntransitivePairsOutputFile), ',');

		for (TransitiveIntransitivePair currentTransitiveIntransitivePair : transitiveIntransitivePairList) {

			PolishJapaneseEntry transitivePolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntryList,
					currentTransitiveIntransitivePair.getTransitiveKanji(),
					currentTransitiveIntransitivePair.getTransitiveKana());

			if (transitivePolishJapaneseEntry == null) {
				continue;
			}

			PolishJapaneseEntry intransitivePolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntryList,
					currentTransitiveIntransitivePair.getIntransitiveKanji(),
					currentTransitiveIntransitivePair.getIntransitiveKana());

			if (intransitivePolishJapaneseEntry == null) {
				continue;
			}

			csvWriter.write(String.valueOf(transitivePolishJapaneseEntry.getId()));
			csvWriter.write(String.valueOf(intransitivePolishJapaneseEntry.getId()));

			csvWriter.endRecord();
		}

		csvWriter.close();
	}

	private static TransitiveIntransitivePair findTransitiveIntransitivePairFromTransitiveVerb(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList, String transitiveKanjiToFound,
			String transitiveKanaToFound) {

		for (TransitiveIntransitivePair transitiveIntransitivePair : transitiveIntransitivePairList) {

			String transitiveKanji = transitiveIntransitivePair.getTransitiveKanji();
			String transitiveKana = transitiveIntransitivePair.getTransitiveKana();

			if (transitiveKanji.equals("") == true) {
				transitiveKanji = "-";
			}

			if (transitiveKanji.equals(transitiveKanjiToFound) == true
					&& transitiveKana.equals(transitiveKanaToFound) == true) {

				return transitiveIntransitivePair;
			}
		}

		return null;
	}

	private static TransitiveIntransitivePair findTransitiveIntransitivePairFromIntransitiveVerb(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList, String intransitiveKanjiToFound,
			String intransitiveKanaToFound) {

		for (TransitiveIntransitivePair transitiveIntransitivePair : transitiveIntransitivePairList) {

			String intransitiveKanji = transitiveIntransitivePair.getIntransitiveKanji();
			String intransitiveKana = transitiveIntransitivePair.getIntransitiveKana();

			if (intransitiveKanji.equals("") == true) {
				intransitiveKanji = "-";
			}

			if (intransitiveKanji.equals(intransitiveKanjiToFound) == true
					&& intransitiveKana.equals(intransitiveKanaToFound) == true) {

				return transitiveIntransitivePair;
			}
		}

		return null;
	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			String kanji, String kana) {

		if (kanji.equals("") == true) {
			kanji = "-";
		}

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();

			if (polishJapaneseEntryKanji.equals("") == true || polishJapaneseEntryKanji.equals("-") == true) {
				polishJapaneseEntryKanji = "-";
			}

			if (kanji.equals(polishJapaneseEntryKanji) == true) {

				List<String> polishJapaneseEntryKanaList = polishJapaneseEntry.getKanaList();

				for (String currentPolishJapaneseEntryKana : polishJapaneseEntryKanaList) {

					if (kana.equals(currentPolishJapaneseEntryKana) == true) {
						return polishJapaneseEntry;
					}
				}

			}
		}

		return null;
	}
}
