package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.WordType;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanaHelper.KanaWord;

public class Validator {

	public static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			List<KanaEntry> hiraganaEntries, List<KanaEntry> katakanaEntries,
			TreeMap<String, List<JMEDictEntry>> jmedict, TreeMap<String, List<JMEDictEntry>> jmedictName)
			throws JapaneseDictionaryException {

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		// walidacja typow hiragana i katakana
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			/*
			String kanji = polishJapaneseEntry.getKanji();
			
			for (int idxKanji = 0; idxKanji < kanji.length(); ++idxKanji) {
				String kanjiChar = String.valueOf(kanji.charAt(idxKanji));
				
				if (containsCharInKanaJapaneseiKanaEntryList(katakanaEntries, kanjiChar) == true) {
					System.out.println("Kanji with katakana: " + kanji);
					
					break;
				}
			}
			*/

			List<String> kanaList = polishJapaneseEntry.getKanaList();
			List<String> romajiList = polishJapaneseEntry.getRomajiList();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			List<String> realRomajiList = new ArrayList<String>();

			boolean ignoreError = false;

			for (int idx = 0; idx < romajiList.size(); ++idx) {

				String currentRomaji = romajiList.get(idx);

				String currentRomajiWithPrefix = prefixRomaji + currentRomaji;

				String currentKana = kanaList.get(idx);

				KanaWord currentKanaAsKanaAsKanaWord = KanaHelper.convertKanaStringIntoKanaWord(currentKana,
						hiraganaEntries, katakanaEntries);

				String currentKanaAsRomaji = KanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);

				KanaWord kanaWord = createKanaWord(currentRomajiWithPrefix, polishJapaneseEntry.getWordType(),
						hiraganaCache, katakanaCache);

				if (kanaWord == null) {
					ignoreError = true;
				}

				if (ignoreError == true
						|| (prefixKana + currentKana).equals(KanaHelper.createKanaString(kanaWord)) == false) {

					if (prefixKana.equals("を") == true && prefixRomaji.equals("o") == true) {
						polishJapaneseEntry.setRealPrefixRomaji("wo");
					} else if (prefixRomaji != null) {
						polishJapaneseEntry.setRealPrefixRomaji(prefixRomaji);
					}

					if (polishJapaneseEntry.getRealPrefixRomaji() == null) {
						polishJapaneseEntry.setRealPrefixRomaji("");
					}

					kanaWord = createKanaWord(polishJapaneseEntry.getRealPrefixRomaji() + currentRomaji,
							polishJapaneseEntry.getWordType(), hiraganaCache, katakanaCache);

					if (ignoreError == true
							|| (prefixKana + currentKana).equals(KanaHelper.createKanaString(kanaWord)) == false) {

						currentRomaji = currentRomaji.replaceAll(" o ", " wo ");
						currentRomaji = currentRomaji.replaceAll(" e ", " he ");

						realRomajiList.add(currentRomaji);

						kanaWord = createKanaWord(polishJapaneseEntry.getRealPrefixRomaji() + currentRomaji,
								polishJapaneseEntry.getWordType(), hiraganaCache, katakanaCache);

						if (ignoreError == false
								&& (prefixKana + currentKana).equals(KanaHelper.createKanaString(kanaWord)) == false) {
							throw new JapaneseDictionaryException("Validate error for word: " + currentRomaji + ": "
									+ (prefixKana + currentKana) + " - " + KanaHelper.createKanaString(kanaWord));
						}
					}
				}

				// is hiragana word
				KanaWord currentKanaAsRomajiAsHiraganaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache,
						currentKanaAsRomaji);
				String currentKanaAsRomajiAsHiraganaWordAsAgainKana = KanaHelper
						.createKanaString(currentKanaAsRomajiAsHiraganaWord);

				// is katakana word
				KanaWord currentKanaAsRomajiAsKatakanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache,
						currentKanaAsRomaji);
				String currentKanaAsRomajiAsKatakanaWordAsAgainKana = KanaHelper
						.createKanaString(currentKanaAsRomajiAsKatakanaWord);

				if (ignoreError == false && currentKana.equals(currentKanaAsRomajiAsHiraganaWordAsAgainKana) == false
						&& currentKana.equals(currentKanaAsRomajiAsKatakanaWordAsAgainKana) == false) {

					throw new JapaneseDictionaryException("Validate error for word: " + currentKana + " ("
							+ currentKanaAsRomaji + ") vs " + currentKanaAsRomajiAsHiraganaWordAsAgainKana + " or "
							+ currentKanaAsRomajiAsKatakanaWordAsAgainKana);
				}
			}

			if (realRomajiList.size() > 0 && romajiList.size() != realRomajiList.size()) {
				throw new JapaneseDictionaryException(
						"realRomajiList.size() > 0 && romajiList.size() != realRomajiList.size()");
			}

			if (realRomajiList.size() > 0) {
				polishJapaneseEntry.setRealRomajiList(realRomajiList);
			}
		}

		// walidacja typow hiragana_katakana i katakana_hiragana
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			WordType wordType = polishJapaneseEntry.getWordType();

			if (wordType != WordType.HIRAGANA_KATAKANA && wordType != WordType.KATAKANA_HIRAGANA) {
				continue;
			}

			List<String> kanaList = polishJapaneseEntry.getKanaList();
			List<String> romajiList = polishJapaneseEntry.getRomajiList();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			for (int idx = 0; idx < romajiList.size(); ++idx) {

				String currentRomaji = romajiList.get(idx);
				String currentRomajiWithPrefix = prefixRomaji + currentRomaji;

				String currentKana = kanaList.get(idx);
				String currentKanaWithPrefix = prefixKana + currentKana;

				KanaWord kanaWord = KanaHelper.convertKanaStringIntoKanaWord(currentKanaWithPrefix, hiraganaEntries,
						katakanaEntries);

				String createdRomaji = KanaHelper.createRomajiString(kanaWord);

				if (currentRomajiWithPrefix.replaceAll(" ", "").equals(createdRomaji) == true) {

					// ok

				} else {

					currentRomajiWithPrefix = currentRomajiWithPrefix.replaceAll(" o ", " wo ");
					currentRomajiWithPrefix = currentRomajiWithPrefix.replaceAll(" e ", " he ");

					if (currentRomajiWithPrefix.replaceAll(" ", "").equals(createdRomaji) == true) {
						// ok 2

					} else {
						throw new JapaneseDictionaryException("Validate error for word: " + currentKana);
					}
				}

			}
		}

		if (jmedict != null) {

			// validate word
			boolean wasError = false;

			final Map<String, DictionaryEntryType> mapEdictTypeToDictionaryEntryType = new HashMap<String, DictionaryEntryType>();

			mapEdictTypeToDictionaryEntryType.put("v1", DictionaryEntryType.WORD_VERB_RU);
			mapEdictTypeToDictionaryEntryType.put("v5k", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5k-s", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5r", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5m", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5s", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("vk", DictionaryEntryType.WORD_VERB_IRREGULAR);
			mapEdictTypeToDictionaryEntryType.put("v4r", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5u", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5r-i", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5t", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5g", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5b", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5n", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("vs-i", DictionaryEntryType.WORD_VERB_IRREGULAR);
			mapEdictTypeToDictionaryEntryType.put("ateji", DictionaryEntryType.WORD_VERB_U); // ???
			mapEdictTypeToDictionaryEntryType.put("io", DictionaryEntryType.WORD_VERB_U); // ???
			mapEdictTypeToDictionaryEntryType.put("oK", DictionaryEntryType.WORD_VERB_U); // ???
			mapEdictTypeToDictionaryEntryType.put("ik", DictionaryEntryType.WORD_VERB_U); // ???
			mapEdictTypeToDictionaryEntryType.put("vs-s", DictionaryEntryType.WORD_VERB_IRREGULAR);
			mapEdictTypeToDictionaryEntryType.put("v5aru", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("v5u-s", DictionaryEntryType.WORD_VERB_U);
			mapEdictTypeToDictionaryEntryType.put("n", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-adv", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-t", DictionaryEntryType.WORD_TEMPORAL_NOUN);
			mapEdictTypeToDictionaryEntryType.put("adj-f", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("adj-no", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("vs", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-suf", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-pref", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("pn", DictionaryEntryType.WORD_PRONOUN);
			mapEdictTypeToDictionaryEntryType.put("int", DictionaryEntryType.WORD_INTERJECTION);
			mapEdictTypeToDictionaryEntryType.put("adj-i", DictionaryEntryType.WORD_ADJECTIVE_I);
			mapEdictTypeToDictionaryEntryType.put("adj-na", DictionaryEntryType.WORD_ADJECTIVE_NA);
			mapEdictTypeToDictionaryEntryType.put("adv", DictionaryEntryType.WORD_ADVERB);
			mapEdictTypeToDictionaryEntryType.put("n-adv", DictionaryEntryType.WORD_ADVERB);
			mapEdictTypeToDictionaryEntryType.put("conj", DictionaryEntryType.WORD_CONJUNCTION);
			mapEdictTypeToDictionaryEntryType.put("vz", DictionaryEntryType.WORD_VERB_ZURU);
			mapEdictTypeToDictionaryEntryType.put("aux-adj", DictionaryEntryType.WORD_AUX_ADJECTIVE_I);
			mapEdictTypeToDictionaryEntryType.put("adv-to", DictionaryEntryType.WORD_ADVERB_TO);
			mapEdictTypeToDictionaryEntryType.put("n-adv", DictionaryEntryType.WORD_ADVERBIAL_NOUN);
			mapEdictTypeToDictionaryEntryType.put("adj-pn", DictionaryEntryType.WORD_PRE_NOUN_ADVERBIAL);
			mapEdictTypeToDictionaryEntryType.put("aux", DictionaryEntryType.WORD_AUX);

			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {

				String kanji = currentPolishJapaneseEntry.getKanji();

				if (kanji != null && kanji.equals("-") == true) {
					kanji = null;
				}

				List<String> kanaList = currentPolishJapaneseEntry.getKanaList();

				List<JMEDictEntry> foundJMEDict = null;

				for (String currentKana : kanaList) {

					foundJMEDict = jmedict.get(JMEDictReader.getMapKey(kanji, currentKana));

					if (foundJMEDict != null) {
						break;
					}
				}

				if (foundJMEDict != null) {

					List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry
							.getDictionaryEntryTypeList();

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_I) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_NA) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERB) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRONOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_CONJUNCTION) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_INTERJECTION) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_TEMPORAL_NOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_ZURU) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_AUX_ADJECTIVE_I) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERB_TO) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERBIAL_NOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRE_NOUN_ADVERBIAL) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_AUX) == true) {

						boolean noFound = true;

						for (JMEDictEntry currentFoundJMEDict : foundJMEDict) {

							List<DictionaryEntryType> dictionaryEntryTypeFromEdictPos = getDictionaryEntryTypeFromEdictPos(
									mapEdictTypeToDictionaryEntryType, currentFoundJMEDict.getPos());

							for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeList) {

								if (dictionaryEntryTypeFromEdictPos.contains(dictionaryEntryType) == true) {
									noFound = false;

									break;
								}
							}
						}

						if (noFound == true
								&& currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
										ParseAdditionalInfo.NO_TYPE_CHECK) == false) {

							System.out.println("Dictionary entry type edict different for: "
									+ currentPolishJapaneseEntry);
							System.out.println("Available types: " + foundJMEDict + "\n");

							wasError = true;
						}
					}
				}
			}

			if (wasError == true) {
				System.exit(1);
			}
		}

		if (jmedictName != null) {

			// validate names
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {

				List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry
						.getDictionaryEntryTypeList();

				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NAME) == false
						&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_MALE_NAME) == false
						&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_FEMALE_NAME) == false) {

					continue;
				}

				String kanji = currentPolishJapaneseEntry.getKanji();

				if (kanji.equals("-") == true) {
					kanji = null;
				}

				if (currentPolishJapaneseEntry.getKanaList().size() > 1) {
					throw new JapaneseDictionaryException("currentPolishJapaneseEntry.getKanaList().size() > 1");
				}

				String kana = currentPolishJapaneseEntry.getKanaList().get(0); // nie powinno byc wiecej kany

				List<JMEDictEntry> jmedictEntryList = jmedictName.get(JMEDictReader.getMapKey(kanji, kana));

				if (jmedictEntryList == null || jmedictEntryList.size() == 0) {
					System.out.println("Warning jmedict not found for: " + currentPolishJapaneseEntry + "\n");

				} else {

					if (jmedictEntryList.size() == 1) {

						JMEDictEntry jmeDictEntry = jmedictEntryList.get(0);

						List<String> trans = jmeDictEntry.getTrans();

						if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NAME) == true
								&& (trans.contains("given") == false && trans.contains("masc") == false && trans
										.contains("fem") == false)) {
							System.out.println("Warning jmedict name type not found for: " + currentPolishJapaneseEntry
									+ " - " + trans + "\n");
						}

						if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_MALE_NAME) == true
								&& (trans.contains("given") == false && trans.contains("masc") == false)) {
							System.out.println("Warning jmedict name male type not found for: "
									+ currentPolishJapaneseEntry + " - " + trans + "\n");
						}

						if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_FEMALE_NAME) == true
								&& (trans.contains("given") == false && trans.contains("fem") == false)) {
							System.out.println("Warning jmedict name female type not found for: "
									+ currentPolishJapaneseEntry + " - " + trans + "\n");
						}

					} else {

						boolean wasOk = false;

						for (JMEDictEntry jmeDictEntry : jmedictEntryList) {

							List<String> trans = jmeDictEntry.getTrans();

							if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NAME) == true
									&& (trans.contains("given") == true || trans.contains("masc") == true || trans
											.contains("fem") == true)) {
								wasOk = true;

								break;
							}

							if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_MALE_NAME) == true
									&& (trans.contains("given") == true || trans.contains("masc") == true)) {
								wasOk = true;

								break;
							}

							if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_FEMALE_NAME) == true
									&& (trans.contains("given") == true || trans.contains("fem") == true)) {
								wasOk = true;

								break;
							}
						}

						if (wasOk == false) {
							System.out.println("Warning jmedict name type error for: " + currentPolishJapaneseEntry
									+ "\n");
						}
					}
				}
			}
		}
	}

	private static List<DictionaryEntryType> getDictionaryEntryTypeFromEdictPos(
			Map<String, DictionaryEntryType> mapEdictTypeToDictionaryEntryType, List<String> pos) {

		List<DictionaryEntryType> result = new ArrayList<DictionaryEntryType>();

		for (String currentPos : pos) {

			DictionaryEntryType dictionaryEntryType = mapEdictTypeToDictionaryEntryType.get(currentPos);

			if (dictionaryEntryType != null) {
				result.add(dictionaryEntryType);
			}
		}

		return result;
	}

	private static KanaWord createKanaWord(String romaji, WordType wordType, Map<String, KanaEntry> hiraganaCache,
			Map<String, KanaEntry> katakanaCache) throws JapaneseDictionaryException {

		KanaWord kanaWord = null;

		if (wordType == WordType.HIRAGANA) {
			kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, romaji);
		} else if (wordType == WordType.KATAKANA) {
			kanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, romaji);
		} else if (wordType == WordType.HIRAGANA_KATAKANA) {
			return null;
		} else if (wordType == WordType.KATAKANA_HIRAGANA) {
			return null;
		} else if (wordType == WordType.HIRAGANA_EXCEPTION) {
			return null;
		} else if (wordType == WordType.KATAKANA_EXCEPTION) {
			return null;
		} else {
			throw new RuntimeException("Bad word type");
		}

		if (kanaWord.remaingRestChars.equals("") == false) {
			throw new JapaneseDictionaryException("Validate error for word: " + romaji + ", remaing: "
					+ kanaWord.remaingRestChars);
		}

		return kanaWord;
	}

	/*
	private static boolean containsCharInKanaJapaneseiKanaEntryList(List<KanaEntry> kanaEntryList, String kanaChar) {
		
		for (KanaEntry kanaEntry : kanaEntryList) {
			
			String kanaJapanese = kanaEntry.getKanaJapanese();
			
			if (kanaJapanese.equals(kanaChar) == true) {
				return true;
			}
		}
		
		return false;
	}
	*/

	/*
	private static void validateDictionaryAndKanjiDictionary(List<PolishJapaneseEntry> japaneseEntries) {
		
		Map<String, List<PolishJapaneseEntry>> groupByKanji = groupByKanji(japaneseEntries);
		
		Iterator<String> groupByKanjiKeySetIterator = groupByKanji.keySet().iterator();
		
		while(groupByKanjiKeySetIterator.hasNext()) {
			
			String currentKanji = groupByKanjiKeySetIterator.next();
			
			if (currentKanji.equals("-") == true || currentKanji.equals("?") == true) {
				continue;
			}
			
			List<PolishJapaneseEntry> kanjiPolishJapanaeseEntries = groupByKanji.get(currentKanji);
			
			if (kanjiPolishJapanaeseEntries.size() > 1) {
				validateTheSameKanji(kanjiPolishJapanaeseEntries);
			}
			
		}
		
		/*		
		for (PolishJapaneseEntry currentDictionaryPolishJapaneseEntry : polishJapaneseEntries) {
			
			if (currentDictionaryPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_VERB_TE) {
				continue;
			}
			
			List<PolishJapaneseEntry> foundPolishJapaneseEntries = 
					findPolishJapaneseKanjiEntry(polishJapaneseKanjiEntries, currentDictionaryPolishJapaneseEntry.getKanji());
			
			if (foundPolishJapaneseEntries.size() > 0) {
				
				for (PolishJapaneseEntry currentFoundPolishJapaneseEntries : foundPolishJapaneseEntries) {
					
					boolean wasError = comparePolishJapaneseEntries(currentDictionaryPolishJapaneseEntry, currentFoundPolishJapaneseEntries);
					
					if (wasError == true) {
						counter++;
					}
				}
				
			}
		}
		* /
	}
	
	private static Map<String, List<PolishJapaneseEntry>> groupByKanji(List<PolishJapaneseEntry> japaneseEntries) {
		
		Map<String, List<PolishJapaneseEntry>> groupByKanji = new HashMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : japaneseEntries) {
			
			if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			String kanji = polishJapaneseEntry.getKanji();
			
			List<PolishJapaneseEntry> kanjiPolishJapaneseEntries = groupByKanji.get(kanji);
			
			if (kanjiPolishJapaneseEntries == null) {
				kanjiPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();
			}
			
			kanjiPolishJapaneseEntries.add(polishJapaneseEntry);
			
			groupByKanji.put(kanji, kanjiPolishJapaneseEntries);
		}
		
		return groupByKanji;		
	}
	
	private static void validateTheSameKanji(List<PolishJapaneseEntry> polishJapaneseEntries) {
				
		for (PolishJapaneseEntry polishJapaneseEntry1 : polishJapaneseEntries) {
			
			for (PolishJapaneseEntry polishJapaneseEntry2 : polishJapaneseEntries) {
				
				comparePolishJapaneseEntries(polishJapaneseEntry1, polishJapaneseEntry2);
			}
		}
	}
	
	private static boolean comparePolishJapaneseEntries(PolishJapaneseEntry entry1, PolishJapaneseEntry entry2) {
		
		boolean wasError = false;
		
		if (entry1.getDictionaryEntryType().equals(entry2.getDictionaryEntryType()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getDictionaryEntryType() + " != " + entry2.getDictionaryEntryType());
		}
		
		if (entry1.getWordType().equals(entry2.getWordType()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getWordType() + " != " + entry2.getWordType());
		}
	/*
		if (entry1.getKanaList().equals(entry2.getKanaList()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getKanaList() + " != " + entry2.getKanaList());
		}

		if (entry1.getRomajiList().equals(entry2.getRomajiList()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getRomajiList() + " != " + entry2.getRomajiList());
		}

		if (entry1.getPolishTranslates().equals(entry2.getPolishTranslates()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getPolishTranslates() + " != " + entry2.getPolishTranslates());
		}

		if (entry1.getInfo().equals(entry2.getInfo()) == false) {
			wasError = true;
			System.out.println(entry1.getKanji() + ": " + entry1.getInfo() + " != " + entry2.getInfo());
		}
	* /
		
		if (wasError == true) {
			System.out.println();
		}
		
		return wasError;
	}
	
	*/

	public static void detectDuplicatePolishJapaneseKanjiEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries) {

		StringBuffer report = new StringBuffer();

		// kanji
		TreeMap<String, TreeSet<PolishJapaneseEntry>> duplicatedKanji = new TreeMap<String, TreeSet<PolishJapaneseEntry>>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			int id = polishJapaneseEntry.getId();
			String kanji = polishJapaneseEntry.getKanji();

			if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
				continue;
			}

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanji(
					polishJapaneseKanjiEntries, id, true, kanji);

			if (findPolishJapaneseKanjiEntry.size() > 0) {

				findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanji(polishJapaneseKanjiEntries, id,
						false, kanji);

				TreeSet<PolishJapaneseEntry> polishJapaneseEntryKanjiTreeSet = duplicatedKanji.get(kanji);

				if (polishJapaneseEntryKanjiTreeSet == null) {
					polishJapaneseEntryKanjiTreeSet = new TreeSet<PolishJapaneseEntry>();
				}

				polishJapaneseEntryKanjiTreeSet.addAll(findPolishJapaneseKanjiEntry);

				duplicatedKanji.put(kanji, polishJapaneseEntryKanjiTreeSet);
			}
		}

		Iterator<String> duplicatedKanjiIterator = duplicatedKanji.keySet().iterator();

		while (duplicatedKanjiIterator.hasNext()) {

			String key = duplicatedKanjiIterator.next();

			TreeSet<PolishJapaneseEntry> treeSetForKanji = duplicatedKanji.get(key);

			report.append("Kanji: " + key).append(": ");

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : treeSetForKanji) {
				report.append(currentPolishJapaneseEntryInTreeSetForKanji.getId()).append(" ");
			}

			report.append("\n");

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : treeSetForKanji) {
				report.append("\t" + currentPolishJapaneseEntryInTreeSetForKanji).append("\n");
			}

			report.append("---\n\n");
		}

		// kanji && kana

		TreeMap<String, TreeSet<PolishJapaneseEntry>> duplicatedKana = new TreeMap<String, TreeSet<PolishJapaneseEntry>>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			int id = polishJapaneseEntry.getId();
			String kanji = polishJapaneseEntry.getKanji();
			List<String> kanaList = polishJapaneseEntry.getKanaList();

			if (kanaList == null || kanaList.size() == 0) {
				continue;
			}

			for (String currentKana : kanaList) {

				List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
						polishJapaneseKanjiEntries, id, true, kanji, currentKana);

				if (findPolishJapaneseKanjiEntry.size() > 0) {

					findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
							polishJapaneseKanjiEntries, id, false, kanji, currentKana);

					TreeSet<PolishJapaneseEntry> polishJapaneseEntryKanaTreeSet = duplicatedKana.get(currentKana);

					if (polishJapaneseEntryKanaTreeSet == null) {
						polishJapaneseEntryKanaTreeSet = new TreeSet<PolishJapaneseEntry>();
					}

					polishJapaneseEntryKanaTreeSet.addAll(findPolishJapaneseKanjiEntry);

					duplicatedKana.put(currentKana, polishJapaneseEntryKanaTreeSet);
				}
			}
		}

		Iterator<String> duplicatedKanaIterator = duplicatedKana.keySet().iterator();

		while (duplicatedKanaIterator.hasNext()) {

			String key = duplicatedKanaIterator.next();

			TreeSet<PolishJapaneseEntry> treeSetForKana = duplicatedKana.get(key);

			report.append("Kana: " + key).append(": ");

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : treeSetForKana) {
				report.append(currentPolishJapaneseEntryInTreeSetForKana.getId()).append(" ");
			}

			report.append("\n");

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : treeSetForKana) {
				report.append("\t" + currentPolishJapaneseEntryInTreeSetForKana).append("\n");
			}

			report.append("---\n\n");
		}

		if (report.length() > 0) {

			System.out.println(report.toString());

			System.exit(1);
		}
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKanji(
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, boolean checkKnownDuplicated, String kanji) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (checkKnownDuplicated == true && polishJapaneseEntry.getKnownDuplicatedId().contains(id) == true) {
				continue;
			}

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKanji().equals(kanji)) {
				result.add(polishJapaneseEntry);
			} else if (checkKnownDuplicated == false && polishJapaneseEntry.getKanji().equals(kanji)) {
				result.add(polishJapaneseEntry);
			}
		}

		return result;
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKanjiAndKana(
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, boolean checkKnownDuplicated, String kanji,
			String kana) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (checkKnownDuplicated == true && polishJapaneseEntry.getKnownDuplicatedId().contains(id) == true) {
				continue;
			}

			boolean differentKanji = !kanji.equals(polishJapaneseEntry.getKanji());

			if (kanji.equals("-") == true && polishJapaneseEntry.getKanji().equals("-") == false) {
				differentKanji = false;
			}

			if (kanji.equals("-") == false && polishJapaneseEntry.getKanji().equals("-") == true) {
				differentKanji = false;
			}

			if (polishJapaneseEntry.getId() != id && differentKanji == false
					&& polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry);
			} else if (checkKnownDuplicated == false && differentKanji == false
					&& polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry);
			}
		}

		return result;
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKana(
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, boolean checkKnownDuplicated, String kana) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (checkKnownDuplicated == true && polishJapaneseEntry.getKnownDuplicatedId().contains(id) == true) {
				continue;
			}

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry);
			} else if (checkKnownDuplicated == false && polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry);
			}
		}

		return result;
	}

	public static void validateUseNoEntryPolishJapaneseKanjiEntries(List<PolishJapaneseEntry> polishJapaneseEntries) {

		StringBuffer report = new StringBuffer();

		// kanji
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			int id = currentPolishJapaneseEntry.getId();
			String kanji = currentPolishJapaneseEntry.getKanji();

			if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
				continue;
			}

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanji(
					polishJapaneseEntries, id, false, kanji);

			if (findPolishJapaneseKanjiEntry.size() == 0) {
				report.append("Kanji: " + kanji).append(": ");

				report.append(currentPolishJapaneseEntry).append("\n");
			}
		}

		if (report.length() > 0) {
			report.append("\n");
		}

		// kana
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			int id = currentPolishJapaneseEntry.getId();
			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();

			for (String currentKana : kanaList) {

				List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKana(
						polishJapaneseEntries, id, false, currentKana);

				if (findPolishJapaneseKanjiEntry.size() == 0) {
					report.append("Kana: " + currentKana).append(": ");

					report.append(currentPolishJapaneseEntry).append("\n");
				}
			}
		}

		// summary
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			int summaryPolishJapaneseEntryHashCode = getSummaryPolishJapaneseEntryHashCode(polishJapaneseEntry);

			List<PolishJapaneseEntry> polishJapaneseEntryListBySummaryHashCodeResult = findPolishJapaneseEntryListBySummaryHashCode(
					polishJapaneseEntries, polishJapaneseEntry.getId(), summaryPolishJapaneseEntryHashCode);

			if (polishJapaneseEntryListBySummaryHashCodeResult.size() == 0) {
				throw new RuntimeException("Summary: " + polishJapaneseEntry);
			}

			if (polishJapaneseEntryListBySummaryHashCodeResult.size() == 1) {
				continue;
			}

			boolean added = false;
			int summary2PolishJapaneseEntryHashCode = getSummary2PolishJapaneseEntryHashCode(polishJapaneseEntryListBySummaryHashCodeResult
					.get(0));

			boolean wasError = false;

			for (PolishJapaneseEntry currentPolishJapaneseEntryListBySummaryHashCodeResultItem : polishJapaneseEntryListBySummaryHashCodeResult) {

				if (getSummary2PolishJapaneseEntryHashCode(currentPolishJapaneseEntryListBySummaryHashCodeResultItem) != summary2PolishJapaneseEntryHashCode
						&& polishJapaneseEntry.getId() != currentPolishJapaneseEntryListBySummaryHashCodeResultItem
								.getId()
						&& polishJapaneseEntry.getKnownDuplicatedId().contains(
								currentPolishJapaneseEntryListBySummaryHashCodeResultItem.getId()) == false) {

					if (added == false) {
						report.append("*Summary*: " + polishJapaneseEntry).append("\n\n");

						added = true;
					}

					report.append(" Summary : " + currentPolishJapaneseEntryListBySummaryHashCodeResultItem).append(
							"\n");

					wasError = true;
				}
			}

			if (wasError == true) {
				report.append("\n---\n\n");
			}
		}

		if (report.length() > 0) {

			System.out.println(report.toString());

			System.exit(1);
		}
	}

	private static int getSummaryPolishJapaneseEntryHashCode(PolishJapaneseEntry polishJapaneseEntry) {

		int prime = 31;

		int result = 1;

		result = prime * result + polishJapaneseEntry.getKanji().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getKanji().getBytes());

		result = prime * result + polishJapaneseEntry.getKanaList().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getKanaList().toString().getBytes());

		result = prime * result + polishJapaneseEntry.getPrefixKana().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getPrefixKana().getBytes());

		return result;
	}

	private static int getSummary2PolishJapaneseEntryHashCode(PolishJapaneseEntry polishJapaneseEntry) {

		int prime = 31;

		int result = 1;

		result = prime * result + polishJapaneseEntry.getDictionaryEntryTypeList().hashCode();
		result = prime * result + polishJapaneseEntry.getAttributeList().hashCode();

		result = prime * result + polishJapaneseEntry.getPolishTranslates().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getPolishTranslates().toString().getBytes());

		result = prime * result + polishJapaneseEntry.getInfo().hashCode();

		return result;
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseEntryListBySummaryHashCode(
			List<PolishJapaneseEntry> polishJapaneseEntries, int id, int summaryPolishJapaneseEntryHashCode) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			int summaryPolishJapaneseEntryHashCode2 = getSummaryPolishJapaneseEntryHashCode(polishJapaneseEntry);

			if (summaryPolishJapaneseEntryHashCode == summaryPolishJapaneseEntryHashCode2) {
				result.add(polishJapaneseEntry);
			}
		}

		return result;
	}

	public static void validateDuplicateKanjiEntriesList(List<KanjiEntry> kanjiEntries)
			throws JapaneseDictionaryException {

		Map<String, KanjiEntry> alreadyKanjiEntryMap = new HashMap<String, KanjiEntry>();

		for (KanjiEntry currentKanjiEntry : kanjiEntries) {

			KanjiEntry kanjiEntryInMap = alreadyKanjiEntryMap.get(currentKanjiEntry.getKanji());

			if (kanjiEntryInMap == null) {

				alreadyKanjiEntryMap.put(currentKanjiEntry.getKanji(), currentKanjiEntry);

			} else {
				throw new JapaneseDictionaryException("Duplicate kanji entry: \n\t" + kanjiEntryInMap + "\n\t"
						+ currentKanjiEntry + "\n");
			}
		}
	}
}
