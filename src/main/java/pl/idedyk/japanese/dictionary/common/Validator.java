package pl.idedyk.japanese.dictionary.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper.KanaWord;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;

public class Validator {

	public static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			List<KanaEntry> hiraganaEntries, List<KanaEntry> katakanaEntries,
			JMENewDictionary jmeNewDictionary, TreeMap<String, List<JMEDictEntry>> jmedict, 
			TreeMap<String, List<JMEDictEntry>> jmedictName)
			throws DictionaryException {

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
				
		KanaHelper kanaHelper = new KanaHelper();
		final Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache();
		
		boolean wasDuplicateTranslateError = false;
		
		// walidacja duplikatow tlumaczen
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			
			List<String> translates = polishJapaneseEntry.getTranslates();
			
			Set<String> translatesSet = new TreeSet<String>(translates);
			
			if (translates.size() != translatesSet.size()) {
				
				wasDuplicateTranslateError = true;
				
				System.out.println("Duplicate translate: " + polishJapaneseEntry + "\n");
			}
			
		}
		
		if (wasDuplicateTranslateError == true) {
			throw new DictionaryException("Error");
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

			String kana = polishJapaneseEntry.getKana();
			String romaji = polishJapaneseEntry.getRomaji();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			String realRomaji = null;

			boolean ignoreError = false;

			String currentRomajiWithPrefix = prefixRomaji + romaji;

			KanaWord currentKanaAsKanaAsKanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana,
					kanaCache, false);

			String currentKanaAsRomaji = kanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);

			KanaWord kanaWord = createKanaWord(kanaHelper, currentRomajiWithPrefix, polishJapaneseEntry.getWordType(),
					hiraganaCache, katakanaCache);

			if (kanaWord == null) {
				ignoreError = true;
			}

			if (ignoreError == true
					|| (prefixKana + kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {

				if (prefixKana.equals("を") == true && prefixRomaji.equals("o") == true) {
					polishJapaneseEntry.setRealPrefixRomaji("wo");
				} else if (prefixRomaji != null) {
					polishJapaneseEntry.setRealPrefixRomaji(prefixRomaji);
				}

				if (polishJapaneseEntry.getRealPrefixRomaji() == null) {
					polishJapaneseEntry.setRealPrefixRomaji("");
				}

				kanaWord = createKanaWord(kanaHelper, polishJapaneseEntry.getRealPrefixRomaji() + romaji,
						polishJapaneseEntry.getWordType(), hiraganaCache, katakanaCache);

				if (ignoreError == true
						|| (prefixKana + kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {

					romaji = romaji.replaceAll(" o ", " wo ");
					romaji = romaji.replaceAll(" e ", " he ");

					realRomaji = romaji;

					kanaWord = createKanaWord(kanaHelper, polishJapaneseEntry.getRealPrefixRomaji() + romaji,
							polishJapaneseEntry.getWordType(), hiraganaCache, katakanaCache);

					if (ignoreError == false
							&& (prefixKana + kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {
						throw new DictionaryException("Validate error for word: " + romaji + ": "
								+ (prefixKana + kana) + " - " + kanaHelper.createKanaString(kanaWord));
					}
				}
			}

			if (polishJapaneseEntry.getWordType() != WordType.HIRAGANA_EXCEPTION && polishJapaneseEntry.getWordType() != WordType.KATAKANA_EXCEPTION) {

				// is hiragana word
				KanaWord currentKanaAsRomajiAsHiraganaWord = kanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache,
						currentKanaAsRomaji);
				String currentKanaAsRomajiAsHiraganaWordAsAgainKana = kanaHelper
						.createKanaString(currentKanaAsRomajiAsHiraganaWord);

				// is katakana word
				KanaWord currentKanaAsRomajiAsKatakanaWord = kanaHelper.convertRomajiIntoKatakanaWord(katakanaCache,
						currentKanaAsRomaji);
				String currentKanaAsRomajiAsKatakanaWordAsAgainKana = kanaHelper
						.createKanaString(currentKanaAsRomajiAsKatakanaWord);

				if (ignoreError == false && kana.equals(currentKanaAsRomajiAsHiraganaWordAsAgainKana) == false
						&& kana.equals(currentKanaAsRomajiAsKatakanaWordAsAgainKana) == false) {

					throw new DictionaryException("Validate error for word: " + kana + " ("
							+ currentKanaAsRomaji + ") vs " + currentKanaAsRomajiAsHiraganaWordAsAgainKana + " or "
							+ currentKanaAsRomajiAsKatakanaWordAsAgainKana);
				}
			}

			if (realRomaji != null) {
				polishJapaneseEntry.setRealRomaji(realRomaji);
			}
		}

		// walidacja typow hiragana_katakana i katakana_hiragana
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			WordType wordType = polishJapaneseEntry.getWordType();

			if (wordType != WordType.HIRAGANA_KATAKANA && wordType != WordType.KATAKANA_HIRAGANA) {
				continue;
			}

			String kana = polishJapaneseEntry.getKana();
			String romaji = polishJapaneseEntry.getRomaji();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			String currentRomaji = romaji;
			String currentRomajiWithPrefix = prefixRomaji + currentRomaji;

			String currentKana = kana;
			String currentKanaWithPrefix = prefixKana + currentKana;
			
			KanaWord kanaWord = kanaHelper.convertKanaStringIntoKanaWord(currentKanaWithPrefix, kanaCache, false);

			String createdRomaji = kanaHelper.createRomajiString(kanaWord);

			if (currentRomajiWithPrefix.replaceAll(" ", "").equals(createdRomaji) == true) {

				// ok

			} else {

				currentRomajiWithPrefix = currentRomajiWithPrefix.replaceAll(" o ", " wo ");
				currentRomajiWithPrefix = currentRomajiWithPrefix.replaceAll(" e ", " he ");

				if (currentRomajiWithPrefix.replaceAll(" ", "").equals(createdRomaji) == true) {
					// ok 2

				} else {
					throw new DictionaryException("Validate error for word: " + currentKana);
				}
			}

		
		}
				
		int fixme2 = 1;
		final Map<Integer, List<DictionaryEntryType>> ttt = new HashMap<Integer, List<DictionaryEntryType>>();
		
		if (jmeNewDictionary != null) {
			
			boolean wasError = false;
			
			DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
			
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
			
				if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
						ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
					
					continue;
				}
				
				String kanji = currentPolishJapaneseEntry.getKanji();
				String kana = currentPolishJapaneseEntry.getKana();
				
				List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
				
				if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
					
					GroupEntry groupEntry = groupEntryList.get(0);
					
					Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
					
					if (groupEntryWordTypeList.size() == 0) {
						continue;
					}
					
					List<DictionaryEntryType> polishJapaneseEntryDictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();
					
					for (DictionaryEntryType currentDictionaryEntryType : polishJapaneseEntryDictionaryEntryTypeList) {

						List<String> entityList = dictionaryEntryJMEdictEntityMapper.getEntity(currentDictionaryEntryType);
						
						boolean wasOk = false;
						
						for (String currentEntity : entityList) {
							
							if (groupEntryWordTypeList.contains(currentEntity) == true) {
								wasOk = true;
							}
						}
						
						if (wasOk == false) {						
							
							wasError = true;
																					
							System.out.println("Błąd walidacji typów(1) dla: " + currentPolishJapaneseEntry + " - " + groupEntryWordTypeList + "\n");																												
						}					
					}
				}
			}
			
			if (wasError == true) {
				throw new DictionaryException("Error");
			}
			
			//
			
			wasError = false;
						
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
			
				if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
						ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
					
					continue;
				}
				
				boolean abc = false;
				
				String kanji = currentPolishJapaneseEntry.getKanji();
				String kana = currentPolishJapaneseEntry.getKana();
				
				List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
				
				if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {

					GroupEntry groupEntry = groupEntryList.get(0);
					
					Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
					
					if (groupEntryWordTypeList.size() == 0) {
						continue;
					}
					
					List<DictionaryEntryType> polishJapaneseEntryDictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();
					
					for (String currentEntity : groupEntryWordTypeList) {
						
						DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
						
						if (dictionaryEntryType == null) {
							continue;
						}
						
						if (polishJapaneseEntryDictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
							
							List<DictionaryEntryType> ddd = ttt.get(currentPolishJapaneseEntry.getId());
							
							if (ddd == null) {
								ddd = new ArrayList<DictionaryEntryType>();
								
								ttt.put(currentPolishJapaneseEntry.getId(), ddd);
							}
							
							ddd.add(dictionaryEntryType);
							
							wasError = true;
							
							System.out.println("Błąd walidacji typów(2) dla: " + currentPolishJapaneseEntry + " - " + groupEntryWordTypeList + "\n");
							
							abc = true;
														
						} else {
							
							List<DictionaryEntryType> ddd = ttt.get(currentPolishJapaneseEntry.getId());
							
							if (ddd == null) {
								ddd = new ArrayList<DictionaryEntryType>();
								
								ttt.put(currentPolishJapaneseEntry.getId(), ddd);
							}
							
							ddd.add(dictionaryEntryType);

							
						}
					}					
				}
				
				if (abc == false) {
					
					
					ttt.remove(currentPolishJapaneseEntry.getId());
				}
				
			}
			
			
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
				
				List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();
				
				List<DictionaryEntryType> list = ttt.get(currentPolishJapaneseEntry.getId());
				
				if (list != null) {
					
					list = new ArrayList<DictionaryEntryType>(new LinkedHashSet<DictionaryEntryType>(list));
					
					/*
					if (dictionaryEntryTypeList.size() == 1 && dictionaryEntryTypeList.get(0) == DictionaryEntryType.WORD_NOUN) {
						
						if (list.size() == 2 && list.get(0) == DictionaryEntryType.WORD_NOUN && list.get(1) == DictionaryEntryType.WORD_ADJECTIVE_NO) {
							
							currentPolishJapaneseEntry.setDictionaryEntryTypeList(list);
							
							System.out.println("AAAAA");
						}						
					}
					*/
					
					if (dictionaryEntryTypeList.get(0) == list.get(0)) {
						
						currentPolishJapaneseEntry.setDictionaryEntryTypeList(list);
						
						System.out.println("AAAAA");
					}
					
					
					
					
				}				
			}
			
			
			
			
			try {
				
				CsvReaderWriter.generateCsv("input/word-new2.csv", polishJapaneseKanjiEntries, true, true, false);
				
				/*
				CsvReaderWriter.generateCsv("input/word-new2.csv", polishJapaneseKanjiEntries, true, true, false,
						new ICustomAdditionalCsvWriter() {
		
							@Override
							public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {

								List<DictionaryEntryType> list = ttt.get(polishJapaneseEntry.getId());
								
								if (list != null) {
									
									if (list.contains(DictionaryEntryType.WORD_EXPRESSION) == true) {
										
										list.remove(DictionaryEntryType.WORD_EXPRESSION);
										
										list.add(DictionaryEntryType.WORD_EXPRESSION);										
									}									
									
									list = new ArrayList<DictionaryEntryType>(new LinkedHashSet<DictionaryEntryType>(list));
									
									csvWriter.write(convertListToString(list));
									
								} else {
									
									csvWriter.write("");
								}							
							}
							
							private String convertListToString(List<?> list) {
								StringBuffer sb = new StringBuffer();

								if (list == null) {
									list = new ArrayList<String>();
								}
								
								for (int idx = 0; idx < list.size(); ++idx) {
									sb.append(list.get(idx));

									if (idx != list.size() - 1) {
										sb.append("\n");
									}
								}

								return sb.toString();
							}
						}
					
					
					
				);
				*/			
				

			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
			
			if (wasError == true) {
				throw new DictionaryException("Error");
			}
		}
		
		
		
		System.exit(1);
		
		// uzyc nowego edict'a
		// skasowac NO_TYPE_CHECK i sprawdzic wyniki

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
			mapEdictTypeToDictionaryEntryType.put("adj-f", DictionaryEntryType.WORD_ADJECTIVE_F);
			mapEdictTypeToDictionaryEntryType.put("adj-no", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("vs", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-suf", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("n-pref", DictionaryEntryType.WORD_NOUN);
			mapEdictTypeToDictionaryEntryType.put("pn", DictionaryEntryType.WORD_PRONOUN);
			mapEdictTypeToDictionaryEntryType.put("int", DictionaryEntryType.WORD_INTERJECTION);
			mapEdictTypeToDictionaryEntryType.put("adj-i", DictionaryEntryType.WORD_ADJECTIVE_I);
			mapEdictTypeToDictionaryEntryType.put("adj-ix", DictionaryEntryType.WORD_ADJECTIVE_I);
			mapEdictTypeToDictionaryEntryType.put("adj-na", DictionaryEntryType.WORD_ADJECTIVE_NA);
			mapEdictTypeToDictionaryEntryType.put("adv", DictionaryEntryType.WORD_ADVERB);
			mapEdictTypeToDictionaryEntryType.put("n-adv", DictionaryEntryType.WORD_ADVERB);
			mapEdictTypeToDictionaryEntryType.put("conj", DictionaryEntryType.WORD_CONJUNCTION);
			mapEdictTypeToDictionaryEntryType.put("vz", DictionaryEntryType.WORD_VERB_ZURU);
			mapEdictTypeToDictionaryEntryType.put("aux-adj", DictionaryEntryType.WORD_AUX_ADJECTIVE_I);
			mapEdictTypeToDictionaryEntryType.put("adv-to", DictionaryEntryType.WORD_ADVERB_TO);
			mapEdictTypeToDictionaryEntryType.put("n-adv", DictionaryEntryType.WORD_ADVERBIAL_NOUN);
			mapEdictTypeToDictionaryEntryType.put("aux", DictionaryEntryType.WORD_AUX);

			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {

				String kanji = currentPolishJapaneseEntry.getKanji();

				if (kanji != null && kanji.equals("-") == true) {
					kanji = null;
				}

				String kana = currentPolishJapaneseEntry.getKana();

				List<JMEDictEntry> foundJMEDict = jmedict.get(JMEDictReader.getMapKey(kanji, kana));

				if (foundJMEDict != null) {

					List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry
							.getDictionaryEntryTypeList();

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_I) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_NA) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_F) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERB) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRONOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_CONJUNCTION) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_INTERJECTION) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_TEMPORAL_NOUN) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_ZURU) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_AUX_ADJECTIVE_I) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERB_TO) == true
							|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADVERBIAL_NOUN) == true
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
		
		System.exit(1);

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

				String kana = currentPolishJapaneseEntry.getKana();

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
		
		if (jmeNewDictionary != null) {
			
			// walidacja grup edict
			validateEdictGroup(jmeNewDictionary, polishJapaneseKanjiEntries);
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

	private static KanaWord createKanaWord(KanaHelper kanaHelper, String romaji, WordType wordType, Map<String, KanaEntry> hiraganaCache,
			Map<String, KanaEntry> katakanaCache) throws DictionaryException {

		KanaWord kanaWord = null;

		if (wordType == WordType.HIRAGANA) {
			kanaWord = kanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, romaji);
		} else if (wordType == WordType.KATAKANA) {
			kanaWord = kanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, romaji);
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
			throw new DictionaryException("Validate error for word: " + romaji + ", remaing: "
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
			String kana = polishJapaneseEntry.getKana();

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
					polishJapaneseKanjiEntries, id, true, kanji, kana);

			if (findPolishJapaneseKanjiEntry.size() > 0) {

				findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
						polishJapaneseKanjiEntries, id, false, kanji, kana);

				TreeSet<PolishJapaneseEntry> polishJapaneseEntryKanaTreeSet = duplicatedKana.get(kana);

				if (polishJapaneseEntryKanaTreeSet == null) {
					polishJapaneseEntryKanaTreeSet = new TreeSet<PolishJapaneseEntry>();
				}

				polishJapaneseEntryKanaTreeSet.addAll(findPolishJapaneseKanjiEntry);

				duplicatedKana.put(kana, polishJapaneseEntryKanaTreeSet);
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
					&& polishJapaneseEntry.getKana().equals(kana) == true) {
				result.add(polishJapaneseEntry);
			} else if (checkKnownDuplicated == false && differentKanji == false
					&& polishJapaneseEntry.getKana().equals(kana) == true) {
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

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKana().equals(kana) == true) {
				result.add(polishJapaneseEntry);
			} else if (checkKnownDuplicated == false && polishJapaneseEntry.getKana().equals(kana) == true) {
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
			String kana = currentPolishJapaneseEntry.getKana();

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKana(
					polishJapaneseEntries, id, false, kana);

			if (findPolishJapaneseKanjiEntry.size() == 0) {
				report.append("Kana: " + kana).append(": ");

				report.append(currentPolishJapaneseEntry).append("\n");
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

		result = prime * result + polishJapaneseEntry.getKana().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getKana().toString().getBytes());

		result = prime * result + polishJapaneseEntry.getPrefixKana().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getPrefixKana().getBytes());

		return result;
	}

	private static int getSummary2PolishJapaneseEntryHashCode(PolishJapaneseEntry polishJapaneseEntry) {

		int prime = 31;

		int result = 1;

		result = prime * result + polishJapaneseEntry.getDictionaryEntryTypeList().hashCode();
		result = prime * result + polishJapaneseEntry.getAttributeList().hashCode();

		result = prime * result + polishJapaneseEntry.getTranslates().hashCode();
		result = prime * result + Arrays.hashCode(polishJapaneseEntry.getTranslates().toString().getBytes());

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
	
	public static void validateEdictGroup(JMENewDictionary jmeNewDictionary, List<PolishJapaneseEntry> polishJapaneseEntries) throws DictionaryException {
		boolean validateResult = true;
		
		Set<String> alreadyValidateErrorResultGroupIds = new HashSet<String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			List<PolishJapaneseEntry> foundPolishJapaneseEntryGroupList = new ArrayList<PolishJapaneseEntry>();
			
			if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
					
					List<GroupEntry> groupEntryList2 = jmeNewDictionary.getGroupEntryList(groupEntryKanji, groupEntryKana);
					
					if (isMultiGroup(groupEntryList2) == false) {
						
						PolishJapaneseEntry findPolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntries, 
								groupEntryKanji, groupEntryKana);
						
						if (findPolishJapaneseEntry != null) {
							foundPolishJapaneseEntryGroupList.add(findPolishJapaneseEntry);
						}
						
					}
				}
			}
			
			// sprawdzanie, czy wszystkie elementy maja to samo tlumaczenie. Powinno tak byc, powinny byc w tej samej grupie
			if (foundPolishJapaneseEntryGroupList.size() > 1) {
				
				String firstTranslate = null;
				String firstInfo = null;
				String firstDictionaryEntryType = null;
				String firstAttributeList = null;
				String firstPrefix = null;
								
				boolean localValidationError = false;
				
				for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
					
					String currentFoundTranslate = currentFoundPolishJapaneseEntry.getTranslates().toString();
					String currentFoundInfo = currentFoundPolishJapaneseEntry.getInfo();
					String currentFoundDictionaryEntryType = currentFoundPolishJapaneseEntry.getDictionaryEntryTypeList().toString();
					String currentFoundAttributeList = toAttributeListString(currentFoundPolishJapaneseEntry.getAttributeList());
					String currentFoundPrefix = currentFoundPolishJapaneseEntry.getPrefixKana() + "-" + currentFoundPolishJapaneseEntry.getPrefixRomaji();			
					
					if (firstTranslate == null) {
						
						firstTranslate = currentFoundTranslate;						
						firstInfo = currentFoundInfo;
						firstDictionaryEntryType = currentFoundDictionaryEntryType;
						firstAttributeList = currentFoundAttributeList;
						firstPrefix = currentFoundPrefix;
						
					} else { // sprawdzenie
												
						if (	currentFoundTranslate.equals(firstTranslate) == false ||
								currentFoundInfo.equals(firstInfo) == false ||
								currentFoundDictionaryEntryType.equals(firstDictionaryEntryType) == false ||
								currentFoundAttributeList.equals(firstAttributeList) == false ||
								currentFoundPrefix.equals(firstPrefix) == false) { 
							
							localValidationError = true;
														
							break;
						}						
					}
				}
				
				if (localValidationError == true) { // jest blad
					
					int edictTranslateInfoGroupDiffCounter = 0;
					
					for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
						
						if (currentFoundPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
							edictTranslateInfoGroupDiffCounter++;
						}						
					}
					
					if (edictTranslateInfoGroupDiffCounter != foundPolishJapaneseEntryGroupList.size()) {
						
						validateResult = false;
						
						StringBuffer errorGroupIds = new StringBuffer();
						
						for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
							errorGroupIds.append(currentFoundPolishJapaneseEntry.getId() + ".");								
						}
						
						if (alreadyValidateErrorResultGroupIds.contains(errorGroupIds.toString()) == false) {
							
							alreadyValidateErrorResultGroupIds.add(errorGroupIds.toString());
							
							System.out.println("Błąd walidacji dla: \n");
							
							for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
								
								int errorId = currentFoundPolishJapaneseEntry.getId();
								String errorKanji = currentFoundPolishJapaneseEntry.getKanji();
								String errorKana = currentFoundPolishJapaneseEntry.getKana();
								String errorDictionaryEntryType = currentFoundPolishJapaneseEntry.getDictionaryEntryTypeList().toString();
								String errorAttributeList = toAttributeListString(currentFoundPolishJapaneseEntry.getAttributeList());
								String errorTranslate = currentFoundPolishJapaneseEntry.getTranslates().toString();
								String errorInfo = currentFoundPolishJapaneseEntry.getInfo();
								String errorPrefix = currentFoundPolishJapaneseEntry.getPrefixKana() + "-" + currentFoundPolishJapaneseEntry.getPrefixRomaji();
								
								System.out.println("id: " + errorId);
								System.out.println("dictionaryEntryType: " + errorDictionaryEntryType);
								System.out.println("attributeList: " + errorAttributeList);
								System.out.println("prefix: " + errorPrefix);
								System.out.println("kanji: " + errorKanji);
								System.out.println("kana: " + errorKana);
								System.out.println("translate: " + errorTranslate);
								System.out.println("info: " + errorInfo);
								
								System.out.println("---\n");
							}
						}
						
					}					
				}
			}
		}	
		
		if (validateResult == false) { // jesli jest blad walidacji
			
			throw new DictionaryException("Error");
		}		
	}
	
	private static boolean isMultiGroup(List<GroupEntry> groupEntryList) {
		
		Set<Integer> uniqueGroupIds = new HashSet<Integer>();
		
		for (GroupEntry groupEntry : groupEntryList) {
			uniqueGroupIds.add(groupEntry.getGroup().getId());
		}
		
		if (uniqueGroupIds.size() == 1) {			
			return false;
			
		} else {
			return true;
		}
	}
	
	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			String findKanji, String findKana) {
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			if (kanji == null || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}

			if (findKanji == null || findKanji.equals("-") == true) {
				findKanji = "$$$NULL$$$";
			}
			
			if (kanji.equals(findKanji) == true && kana.equals(findKana) == true) {
				return polishJapaneseEntry;
			}
		}
		
		return null;
	}
	
	private static String toAttributeListString(AttributeList attributeList) {
		
		StringBuffer sb = new StringBuffer();
		
		List<Attribute> attributeListList = attributeList.getAttributeList();
		
		for (Attribute attribute : attributeListList) {
			
			if (sb.length() > 0) {
				sb.append(", ");
			}
			
			sb.append(attribute.getAttributeType());
			
			if (attribute.getAttributeValue() != null) {
				sb.append(": " + attribute.getAttributeValue().toString());
			}
		}
		
		return sb.toString();
	}
}
