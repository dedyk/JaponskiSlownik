package pl.idedyk.japanese.dictionary.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryGroup;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.example.ExampleManager;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.gramma.GrammaConjugaterManager;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateResult;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateResultType;
import pl.idedyk.japanese.dictionary.api.keigo.KeigoHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper.KanaWord;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

public class Validator {

	public static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			List<KanaEntry> hiraganaEntries, List<KanaEntry> katakanaEntries,
			JMENewDictionary jmeNewNameDictionary)
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
			
			if (translates.size() != translatesSet.size() && polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
				
				wasDuplicateTranslateError = true;
				
				System.out.println("Duplicate translate: " + polishJapaneseEntry + "\n");
			}
			
		}
		
		if (wasDuplicateTranslateError == true) {
			throw new DictionaryException("Error");
		}
		
		// walidacja typow hiragana i katakana
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
				continue;
			}

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

			String kana = polishJapaneseEntry.getKana().replaceAll("・", "");
			String romaji = polishJapaneseEntry.getRomaji();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			String realRomaji = null;

			boolean ignoreError = false;

			String currentRomajiWithPrefix = prefixRomaji + romaji;

			KanaWord currentKanaAsKanaAsKanaWord = null;
			
			if (polishJapaneseEntry.getWordType() != WordType.HIRAGANA_EXCEPTION && polishJapaneseEntry.getWordType() != WordType.KATAKANA_EXCEPTION) {
				
				try {
					currentKanaAsKanaAsKanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaCache, false);
					
				} catch (Exception e) {
					throw new RuntimeException(polishJapaneseEntry.getId() + ": " + e);
				}
			}			

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
						throw new DictionaryException("Validate error for word(" + polishJapaneseEntry.getId() + "): " + romaji + ": "
								+ (prefixKana + kana) + " - " + kanaHelper.createKanaString(kanaWord));
					}
				}
			}

			if (polishJapaneseEntry.getWordType() != WordType.HIRAGANA_EXCEPTION && polishJapaneseEntry.getWordType() != WordType.KATAKANA_EXCEPTION) {

				String currentKanaAsRomaji = kanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);
				
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

					throw new DictionaryException("Validate error for word (" + polishJapaneseEntry.getId() + "): " + kana + " ("
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
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
				continue;
			}

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
					throw new DictionaryException("Validate error for word(" + polishJapaneseEntry.getId() + "): " + currentKana);
				}
			}		
		}
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
						
		if (dictionaryHelper != null) {
			
			boolean wasError = false;
						
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
			
				if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
						ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
					
					continue;
				}
				
				
				// FIXME: szukanie entry wedlug PolishJapaneseEntry
				
				tutaj();
								
				List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(currentPolishJapaneseEntry);
				
				if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
					
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
									
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
			
				if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
						ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
					
					continue;
				}
								
				List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(currentPolishJapaneseEntry);
				
				if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {

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
														
							wasError = true;
							
							System.out.println("Błąd walidacji typów(2) dla: " + currentPolishJapaneseEntry + " - " + groupEntryWordTypeList + "\n");
														
						}
					}					
				}				
			}
			
			if (wasError == true) {
				throw new DictionaryException("Error");
			}
		}
		
		if (jmeNewNameDictionary != null) {
			
			boolean wasError = false;

			// validate names
			for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseKanjiEntries) {
				
				List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry
						.getDictionaryEntryTypeList();

				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NAME) == false
						&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_MALE_NAME) == false
						&& dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_FEMALE_NAME) == false) {

					continue;
				}
				
				if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
						ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
					
					continue;
				}
				
				List<GroupEntry> groupEntryList = jmeNewNameDictionary.getGroupEntryList(currentPolishJapaneseEntry);
				
				if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
					
					boolean wasOk = false;
					
					GroupEntry groupEntry = groupEntryList.get(0);
					
					Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
					
					if (groupEntryWordTypeList.size() == 0) {
						continue;
					}
					
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NAME) == true
							&& (	groupEntryWordTypeList.contains("given") == true || 
									groupEntryWordTypeList.contains("masc") == true || 
									groupEntryWordTypeList.contains("fem") == true)) {
						
						wasOk = true;
					}

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_MALE_NAME) == true
							&& (groupEntryWordTypeList.contains("given") == true || groupEntryWordTypeList.contains("masc") == true)) {
						
						wasOk = true;
					}

					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_FEMALE_NAME) == true
							&& (groupEntryWordTypeList.contains("given") == true || groupEntryWordTypeList.contains("fem") == true)) {
						
						wasOk = true;
					}

					if (wasOk == false) {
						
						wasError = true;
						
						System.out.println("Warning jmedict name type error for: " + currentPolishJapaneseEntry
								+ "\n");
					}					
					
				} else {					
					//System.out.println("Name not found or multi group: " + currentPolishJapaneseEntry);					
				}
			}
			
			if (wasError == true) {
				throw new DictionaryException("Error");
			}
		}
		
		if (jmeNewDictionary != null) {
			
			// walidacja grup edict
			validateEdictGroup(jmeNewDictionary, polishJapaneseKanjiEntries);
		}
		
		if (jmeNewDictionary != null) {
			
			// wyliczanie form gramatycznych i przykladow
			countGrammaFormAndExamples(polishJapaneseKanjiEntries);				
		}		
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

	public static void detectDuplicatePolishJapaneseKanjiEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, String wordDuplicateFileName) throws IOException {
		
		List<PolishJapaneseEntryDuplicate> polishJapaneseEntryDuplicateList = new ArrayList<Validator.PolishJapaneseEntryDuplicate>();
		
		// cache'owanie
		Map<String, List<PolishJapaneseEntry>> theSameKanjiPolishJapaneseListMap = new TreeMap<String, List<PolishJapaneseEntry>>();
		Map<String, List<PolishJapaneseEntry>> theSameKanaPolishJapaneseListMap = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			
			boolean kanjiExists = polishJapaneseEntry.isKanjiExists();		
			
			if (kanjiExists == true) {
				
				List<PolishJapaneseEntry> theSameKanjiPolishJapaneseList = theSameKanjiPolishJapaneseListMap.get(kanji);
				
				if (theSameKanjiPolishJapaneseList == null) {					
					theSameKanjiPolishJapaneseList = new ArrayList<PolishJapaneseEntry>();
					
					theSameKanjiPolishJapaneseListMap.put(kanji, theSameKanjiPolishJapaneseList);					
				}
				
				theSameKanjiPolishJapaneseList.add(polishJapaneseEntry);				
			}	
			
			String kana = polishJapaneseEntry.getKana();
			
			List<PolishJapaneseEntry> theSameKanaPolishJapaneseList = theSameKanaPolishJapaneseListMap.get(kana);
			
			if (theSameKanaPolishJapaneseList == null) {					
				theSameKanaPolishJapaneseList = new ArrayList<PolishJapaneseEntry>();
				
				theSameKanaPolishJapaneseListMap.put(kana, theSameKanaPolishJapaneseList);					
			}
			
			theSameKanaPolishJapaneseList.add(polishJapaneseEntry);
		}
		
		// kanji
		TreeMap<String, TreeSet<PolishJapaneseEntry>> duplicatedKanji = new TreeMap<String, TreeSet<PolishJapaneseEntry>>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			int id = polishJapaneseEntry.getId();
			String kanji = polishJapaneseEntry.getKanji();
			
			if (polishJapaneseEntry.isKanjiExists() == false) {
				continue;
			}

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanji(theSameKanjiPolishJapaneseListMap, id, true, kanji);

			if (findPolishJapaneseKanjiEntry.size() > 0) {

				findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanji(theSameKanjiPolishJapaneseListMap, id,
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
			
			PolishJapaneseEntryDuplicate polishJapaneseEntryDuplicate = new PolishJapaneseEntryDuplicate(key, null);

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : treeSetForKanji) {
				polishJapaneseEntryDuplicate.addPolishJapaneseEntry(currentPolishJapaneseEntryInTreeSetForKanji);
			}
			
			polishJapaneseEntryDuplicateList.add(polishJapaneseEntryDuplicate);
		}

		// kanji && kana

		TreeMap<String, TreeSet<PolishJapaneseEntry>> duplicatedKana = new TreeMap<String, TreeSet<PolishJapaneseEntry>>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			int id = polishJapaneseEntry.getId();
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();

			List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
					theSameKanaPolishJapaneseListMap, id, true, kanji, kana);

			if (findPolishJapaneseKanjiEntry.size() > 0) {

				findPolishJapaneseKanjiEntry = findPolishJapaneseKanjiEntryInKanjiAndKana(
						theSameKanaPolishJapaneseListMap, id, false, kanji, kana);

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
			
			PolishJapaneseEntryDuplicate polishJapaneseEntryDuplicate = new PolishJapaneseEntryDuplicate(null, key);

			for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : treeSetForKana) {
				polishJapaneseEntryDuplicate.addPolishJapaneseEntry(currentPolishJapaneseEntryInTreeSetForKana);
			}
			
			polishJapaneseEntryDuplicateList.add(polishJapaneseEntryDuplicate);			
		}

		if (polishJapaneseEntryDuplicateList.size() > 0) {
			
			CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(new FileOutputStream(wordDuplicateFileName)), ',');
			
			StringBuffer report = new StringBuffer();
			
			for (PolishJapaneseEntryDuplicate polishJapaneseEntryDuplicate : polishJapaneseEntryDuplicateList) {
				
				csvWriter.write(""); // miejsce na zaznaczenie
				
				if (polishJapaneseEntryDuplicate.getKanji() != null) { // duplikat kanji
					
					csvWriter.write("KANJI");
					
					csvWriter.write(polishJapaneseEntryDuplicate.getKanji());
					
					report.append("Kanji: " + polishJapaneseEntryDuplicate.getKanji()).append(": ");
					
					List<String> ids = new ArrayList<String>();
					List<String> details = new ArrayList<String>();

					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						
						report.append(currentPolishJapaneseEntryInTreeSetForKanji.getId()).append(" ");
						
						ids.add(String.valueOf(currentPolishJapaneseEntryInTreeSetForKanji.getId()));
						
						details.add(currentPolishJapaneseEntryInTreeSetForKanji.getKanji() + " - " + currentPolishJapaneseEntryInTreeSetForKanji.getKana() + " - " + 
								currentPolishJapaneseEntryInTreeSetForKanji.getTranslates() + " - " + currentPolishJapaneseEntryInTreeSetForKanji.getInfo());
					}
					
					csvWriter.write(pl.idedyk.japanese.dictionary.api.dictionary.Utils.convertListToString(ids));
					csvWriter.write(pl.idedyk.japanese.dictionary.api.dictionary.Utils.convertListToString(details));

					report.append("\n");
					
					report.append("--\n");
					
					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						report.append("DUPLICATE " + currentPolishJapaneseEntryInTreeSetForKanji.getId()).append("\n");
					}
					
					report.append("--\n");

					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKanji : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						report.append("\t" + currentPolishJapaneseEntryInTreeSetForKanji).append("\n");
					}

					report.append("---\n\n");
					
				} else { // duplikat kana
					
					csvWriter.write("KANA");
					
					csvWriter.write(polishJapaneseEntryDuplicate.getKana());
					
					report.append("Kana: " + polishJapaneseEntryDuplicate.getKana()).append(": ");

					List<String> ids = new ArrayList<String>();
					List<String> details = new ArrayList<String>();
					
					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						
						report.append(currentPolishJapaneseEntryInTreeSetForKana.getId()).append(" ");
						
						ids.add(String.valueOf(currentPolishJapaneseEntryInTreeSetForKana.getId()));
						
						details.add(currentPolishJapaneseEntryInTreeSetForKana.getKanji() + " - " + currentPolishJapaneseEntryInTreeSetForKana.getKana() + " - " + 
								currentPolishJapaneseEntryInTreeSetForKana.getTranslates() + " - " + currentPolishJapaneseEntryInTreeSetForKana.getInfo());

					}
					
					csvWriter.write(pl.idedyk.japanese.dictionary.api.dictionary.Utils.convertListToString(ids));
					csvWriter.write(pl.idedyk.japanese.dictionary.api.dictionary.Utils.convertListToString(details));
					
					report.append("\n");
					
					report.append("--\n");
					
					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						report.append("DUPLICATE " + currentPolishJapaneseEntryInTreeSetForKana.getId()).append("\n");
					}
					
					report.append("--\n");

					report.append("\n");

					for (PolishJapaneseEntry currentPolishJapaneseEntryInTreeSetForKana : polishJapaneseEntryDuplicate.getPolishJapaneseEntryList()) {
						report.append("\t" + currentPolishJapaneseEntryInTreeSetForKana).append("\n");
					}

					report.append("---\n\n");					
				}
				
				csvWriter.endRecord();
			}
			
			csvWriter.close();

			System.out.println(report.toString());

			System.exit(1);
		}
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKanji(
			Map<String, List<PolishJapaneseEntry>> theSameKanjiPolishJapaneseListMap, int id, boolean checkKnownDuplicated, String kanji) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		List<PolishJapaneseEntry> theSameKanjiPolishJapaneseList = theSameKanjiPolishJapaneseListMap.get(kanji);
		
		for (PolishJapaneseEntry polishJapaneseEntry : theSameKanjiPolishJapaneseList) {
			
			if (checkKnownDuplicated == true && polishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.DUPLICATE, id) == true) {
				continue;
			}

			if (polishJapaneseEntry.getId() != id) {
				result.add(polishJapaneseEntry);
				
			} else if (checkKnownDuplicated == false) {
				result.add(polishJapaneseEntry);
				
			}
		}		

		return result;
	}

	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKanjiAndKana(
			Map<String, List<PolishJapaneseEntry>> theSameKanaPolishJapaneseListMap, int id, boolean checkKnownDuplicated, String kanji,
			String kana) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		List<PolishJapaneseEntry> theSameKanaPolishJapaneseList = theSameKanaPolishJapaneseListMap.get(kana);
		
		for (PolishJapaneseEntry polishJapaneseEntry : theSameKanaPolishJapaneseList) {

			if (checkKnownDuplicated == true && polishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.DUPLICATE, id) == true) {
				continue;
			}

			boolean differentKanji = !kanji.equals(polishJapaneseEntry.getKanji());

			if (kanji.equals("-") == true && polishJapaneseEntry.getKanji().equals("-") == false) {
				differentKanji = false;
			}

			if (kanji.equals("-") == false && polishJapaneseEntry.getKanji().equals("-") == true) {
				differentKanji = false;
			}

			if (polishJapaneseEntry.getId() != id && differentKanji == false) {
				
				result.add(polishJapaneseEntry);
				
			} else if (checkKnownDuplicated == false && differentKanji == false) {
				
				result.add(polishJapaneseEntry);
			}
		}

		return result;
	}

	/*
	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntryInKana(
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, boolean checkKnownDuplicated, String kana) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (checkKnownDuplicated == true && polishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.DUPLICATE, id) == true) {
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
						&& polishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.DUPLICATE, 
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
	*/

	public static void validateDuplicateKanjiEntriesList(List<KanjiEntryForDictionary> kanjiEntries)
			throws JapaneseDictionaryException {

		Map<String, KanjiEntryForDictionary> alreadyKanjiEntryMap = new HashMap<String, KanjiEntryForDictionary>();

		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {

			KanjiEntryForDictionary kanjiEntryInMap = alreadyKanjiEntryMap.get(currentKanjiEntry.getKanji());

			if (kanjiEntryInMap == null) {

				alreadyKanjiEntryMap.put(currentKanjiEntry.getKanji(), currentKanjiEntry);

			} else {
				throw new JapaneseDictionaryException("Duplicate kanji entry: \n\t" + kanjiEntryInMap + "\n\t"
						+ currentKanjiEntry + "\n");
			}
		}
	}
	
	public static void validateDuplicateMeansKanjiEntriesList(List<KanjiEntryForDictionary> kanjiEntries)
			throws DictionaryException {
		
		boolean wasDuplicateTranslateError = false;
		
		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {
			
			List<String> translates = currentKanjiEntry.getPolishTranslates();
			
			Set<String> translatesSet = new TreeSet<String>(translates);
			
			if (translates.size() != translatesSet.size()) {
				
				wasDuplicateTranslateError = true;
				
				System.out.println("Duplicate translate: " + currentKanjiEntry + "\n");
			}
		}
		
		if (wasDuplicateTranslateError == true) {
			throw new DictionaryException("Error");
		}		
	}
	
	public static void validateEdictGroup(Dictionary2Helper dictionary2Helper, List<PolishJapaneseEntry> polishJapaneseEntries) throws DictionaryException {
		
		System.out.println("FIXME !!!!!!");
		
		if (1 == 1) {
			return;
		}
		
		boolean validateResult = true;
		
		Set<String> alreadyValidateErrorResultGroupIds = new HashSet<String>();
		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = Helper.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry);
			
			List<PolishJapaneseEntry> foundPolishJapaneseEntryGroupList = new ArrayList<PolishJapaneseEntry>();
			
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				List<GroupEntry> fullGroupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList();
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(fullGroupEntryList, kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
																
					PolishJapaneseEntry findPolishJapaneseEntry = Helper.findPolishJapaneseEntryWithEdictDuplicate(
							polishJapaneseEntry, cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
					
					if (findPolishJapaneseEntry != null) {
						foundPolishJapaneseEntryGroupList.add(findPolishJapaneseEntry);
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
	
	public static void validateEdictGroupRomaji(List<PolishJapaneseEntry> polishJapaneseEntries) throws DictionaryException {
		
		// !!! INFO: aby to dzialalo prawidlowo, dane musza byc wzbogacone o metode Helper.generateAdditionalInfoFromEdict(jmeNewDictionary, jmedictCommon, polishJapaneseEntries); !!!
				
		// walidowanie, czy wszystkie slowa w tej samej grupie sa pisane w taki sam sposob w romaji
		boolean validateResult = true;
		
		List<DictionaryEntryGroup> dictionaryEntryGroupList = Helper.generateDictionaryEntryGroup(polishJapaneseEntries);
				
		for (DictionaryEntryGroup dictionaryEntryGroup : dictionaryEntryGroupList) {
			
			// pobranie wszystkie slowa wchodzace w sklad grupy
			List<DictionaryEntry> dictionaryEntryList = dictionaryEntryGroup.getDictionaryEntryList();
			
			Map<String, List<DictionaryEntry>> romajiWithoutSpaceToDictionaryEntryListMap = new HashMap<>();
			
			for (DictionaryEntry dictionaryEntry : dictionaryEntryList) {
				
				// pobranie romaji
				String romaji = dictionaryEntry.getRomaji();
				
				// romaji bez spacji
				String romajiWithoutSpace = romaji.replaceAll(" ", "");
				
				List<DictionaryEntry> romajiWithoutSpaceToDictionaryEntryList = romajiWithoutSpaceToDictionaryEntryListMap.get(romajiWithoutSpace);
				
				if (romajiWithoutSpaceToDictionaryEntryList == null) {
					
					romajiWithoutSpaceToDictionaryEntryList = new ArrayList<>();
					
					romajiWithoutSpaceToDictionaryEntryListMap.put(romajiWithoutSpace, romajiWithoutSpaceToDictionaryEntryList);
				}
				
				romajiWithoutSpaceToDictionaryEntryList.add(dictionaryEntry);				
			}
			
			// sprawdzamy, czy wszystkie romaji bez spacji sa takie same
			Iterator<Entry<String, List<DictionaryEntry>>> romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator = romajiWithoutSpaceToDictionaryEntryListMap.entrySet().iterator();
			
			while (romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator.hasNext() == true) {
				
				Entry<String, List<DictionaryEntry>> romajiWithoutSpaceToDictionaryEntryListMapEntry = romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator.next();
								
				List<DictionaryEntry> romajiDictionaryEntryList = romajiWithoutSpaceToDictionaryEntryListMapEntry.getValue();
				
				//
				
				if (romajiDictionaryEntryList.size() > 1) {
					
					Set<String> uniqueRomaji = new HashSet<String>();
					
					for (DictionaryEntry dictionaryEntry : romajiDictionaryEntryList) {
						uniqueRomaji.add(dictionaryEntry.getRomaji());
					}
					
					if (uniqueRomaji.size() > 1) { // jest blad
						
						validateResult = false;
						
						System.out.println("Błąd walidacji romaji dla: \n");
						
						for (DictionaryEntry dictionaryEntry : romajiDictionaryEntryList) {
							
							System.out.println("id: " + dictionaryEntry.getId());
							System.out.println("kanji: " + dictionaryEntry.getKanji());
							System.out.println("kana: " + dictionaryEntry.getKana());
							System.out.println("romaji: " + dictionaryEntry.getRomaji());
							System.out.println("translate: " + dictionaryEntry.getTranslates());
							System.out.println("info: " + dictionaryEntry.getInfo());
							
							System.out.println("---\n");							
						}					
					}
				}
			}			
		}	
		
		if (validateResult == false) { // jesli jest blad walidacji
			
			throw new DictionaryException("Error");
		}
	}
			
	private static String toAttributeListString(AttributeList attributeList) {
		
		StringBuffer sb = new StringBuffer();
		
		List<Attribute> attributeListList = attributeList.getAttributeList();
		
		for (Attribute attribute : attributeListList) {
			
			if (attribute.getAttributeType() == AttributeType.ALTERNATIVE) {
				continue;
			}
			
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
	
	private static void countGrammaFormAndExamples(List<PolishJapaneseEntry> polishJapaneseKanjiEntries) throws DictionaryException {
		
		boolean wasError = false;
		
		KeigoHelper keigoHelper = new KeigoHelper();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			
			if (kanji.equals("-") == true) {
				polishJapaneseEntry.setKanji(null);
			}
			
			try {
				
				Map<GrammaFormConjugateResultType, GrammaFormConjugateResult> grammaFormCache = new HashMap<GrammaFormConjugateResultType, GrammaFormConjugateResult>();
				
				GrammaConjugaterManager.getGrammaConjufateResult(keigoHelper, polishJapaneseEntry, grammaFormCache, null, true);
				
				for (DictionaryEntryType currentDictionaryEntryType : polishJapaneseEntry.getDictionaryEntryTypeList()) {
					GrammaConjugaterManager.getGrammaConjufateResult(keigoHelper, polishJapaneseEntry, grammaFormCache, currentDictionaryEntryType, true);
				}
				
				ExampleManager.getExamples(keigoHelper, polishJapaneseEntry, grammaFormCache, null, true);
				
				for (DictionaryEntryType currentDictionaryEntryType : polishJapaneseEntry.getDictionaryEntryTypeList()) {
					ExampleManager.getExamples(keigoHelper, polishJapaneseEntry, grammaFormCache, currentDictionaryEntryType, true);
				}
				
			} catch (Exception e) {
				
				System.out.println("Błąd wyliczania form gramatycznych i przykładów dla: " + polishJapaneseEntry + ": " + e.getMessage() + "\n");
								
				wasError = true;
			}	
			
			// przywrocenie
			polishJapaneseEntry.setKanji(kanji);
		}	
		
		if (wasError == true) { // jesli jest blad walidacji			
			throw new DictionaryException("Error");
		}		
	}
	
	private static class PolishJapaneseEntryDuplicate {
				
		private String kanji;
		
		private String kana;
		
		private List<PolishJapaneseEntry> polishJapaneseEntryList;

		public PolishJapaneseEntryDuplicate(String kanji, String kana) {
			this.kanji = kanji;
			this.kana = kana;
		}

		public String getKanji() {
			return kanji;
		}

		@SuppressWarnings("unused")
		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public String getKana() {
			return kana;
		}

		@SuppressWarnings("unused")
		public void setKana(String kana) {
			this.kana = kana;
		}

		public List<PolishJapaneseEntry> getPolishJapaneseEntryList() {
			return polishJapaneseEntryList;
		}

		@SuppressWarnings("unused")
		public void setPolishJapaneseEntryList(List<PolishJapaneseEntry> polishJapaneseEntryList) {
			this.polishJapaneseEntryList = polishJapaneseEntryList;
		}
		
		public void addPolishJapaneseEntry(PolishJapaneseEntry polishJapaneseEntry) {
			
			if (polishJapaneseEntryList == null) {
				polishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
			}
			
			if (polishJapaneseEntryList.contains(polishJapaneseEntry) == false) {
				polishJapaneseEntryList.add(polishJapaneseEntry);
			}			
		}
	}
}
