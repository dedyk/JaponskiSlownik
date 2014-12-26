package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.KanjiEntry;
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
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;

public class Validator {

	public static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			List<KanaEntry> hiraganaEntries, List<KanaEntry> katakanaEntries,
			JMENewDictionary jmeNewDictionary, JMENewDictionary jmeNewNameDictionary)
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

			String kana = polishJapaneseEntry.getKana().replaceAll("・", "");
			String romaji = polishJapaneseEntry.getRomaji();
			String prefixKana = polishJapaneseEntry.getPrefixKana();
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();

			String realRomaji = null;

			boolean ignoreError = false;

			String currentRomajiWithPrefix = prefixRomaji + romaji;

			KanaWord currentKanaAsKanaAsKanaWord = null;
			
			try {
				currentKanaAsKanaAsKanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana,
						kanaCache, false);
				
			} catch (Exception e) {
				if (polishJapaneseEntry.getWordType() != WordType.HIRAGANA_EXCEPTION && polishJapaneseEntry.getWordType() != WordType.KATAKANA_EXCEPTION) {
					throw new RuntimeException(e);
					
				} else {
					continue;
				}
			}

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
		
		// wyliczanie form gramatycznych i przykladow
		countGrammaFormAndExamples(polishJapaneseKanjiEntries);		
				
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

				String kanji = currentPolishJapaneseEntry.getKanji();
				String kana = currentPolishJapaneseEntry.getKana();

				List<GroupEntry> groupEntryList = jmeNewNameDictionary.getGroupEntryList(kanji, kana);
				
				if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
					
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
		
		// wyliczanie form gramatycznych i przykladow
		countGrammaFormAndExamples(polishJapaneseKanjiEntries);		
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

			if (checkKnownDuplicated == true && polishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.DUPLICATE, id) == true) {
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
}
