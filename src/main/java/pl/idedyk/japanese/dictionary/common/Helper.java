package pl.idedyk.japanese.dictionary.common;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.AttributeList;
import pl.idedyk.japanese.dictionary.dto.AttributeType;
import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.TransitiveIntransitivePair;
import pl.idedyk.japanese.dictionary.dto.WordType;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;

import com.csvreader.CsvWriter;

public class Helper {

	public static List<PolishJapaneseEntry> generateGroups(List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addOtherGroup) {

		// generate groups

		Map<String, List<String>> polishJapaneseEntriesAndGroups = new HashMap<String, List<String>>();

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			List<String> currentPolishJapaneseEntryGroups = currentPolishJapaneseEntry.getGroups();

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

			currentPolishJapaneseEntry.setGroups(groupsForCurrentPolishJapaneseEntry);
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

	public static void generateNames(TreeMap<String, EDictEntry> jmedictName,
			List<PolishJapaneseEntry> polishJapaneseEntries) {

		Iterator<EDictEntry> iterator = jmedictName.values().iterator();

		int counter = 1000000;

		// [Thailand, former, Dalai, SNK, Niigata, Sony, st, f, g, uk, d, c, or, n, o, abbr, ik, m, Nintendo, h, Sega, Republic, u, co, 1, NEC, s, Bandai, deity, p, pr]

		// surname -> s
		// masc -> m
		// fem -> f
		// given -> g

		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> kitakanaEntries = KanaHelper.getAllKatakanaKanaEntries();

		while (iterator.hasNext()) {
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

			List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();

			if (pos.contains("f") == true) {
				dictionaryEntryTypeList.add(DictionaryEntryType.WORD_FEMALE_NAME);

			} else if (pos.contains("m") == true) {
				dictionaryEntryTypeList.add(DictionaryEntryType.WORD_MALE_NAME);

			} else if (pos.contains("g") == true) {
				dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NAME);

			} else if (pos.contains("s") == true) {
				dictionaryEntryTypeList.add(DictionaryEntryType.WORD_SURNAME_NAME);

			} else {
				continue;
			}

			newPolishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);

			newPolishJapaneseEntry.setWordType(WordType.HIRAGANA_KATAKANA);

			newPolishJapaneseEntry.setAttributeList(new AttributeList());
			newPolishJapaneseEntry.setGroups(new ArrayList<String>());

			newPolishJapaneseEntry.setKanji(kanji != null ? kanji : "-");

			List<String> kanaList = new ArrayList<String>();
			kanaList.add(kana);

			newPolishJapaneseEntry.setKanaList(kanaList);

			try {
				List<String> romajiList = new ArrayList<String>();
				romajiList.add(KanaHelper.createRomajiString(KanaHelper.convertKanaStringIntoKanaWord(kana,
						hiraganaEntries, kitakanaEntries)));

				newPolishJapaneseEntry.setRomajiList(romajiList);

			} catch (RuntimeException e) {
				continue;
			}

			List<String> polishTranslateList = new ArrayList<String>();
			polishTranslateList.add(name);

			newPolishJapaneseEntry.setPolishTranslates(polishTranslateList);

			newPolishJapaneseEntry.setParseAdditionalInfoList(new ArrayList<ParseAdditionalInfo>());

			//newPolishJapaneseEntry.setUseEntry(true);

			polishJapaneseEntries.add(newPolishJapaneseEntry);

			counter++;
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
