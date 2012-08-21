package pl.idedyk.japanese.dictionary.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.WordType;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanaHelper.KanaWord;

public class Validator {

	public static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, List<KanaEntry> hiraganaEntries,
			List<KanaEntry> katakanaEntries) throws JapaneseDictionaryException {
		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
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
			
			for (int idx = 0; idx < romajiList.size(); ++idx) {
				
				String currentRomaji = prefixRomaji + romajiList.get(idx);
				String currentKana = kanaList.get(idx);
				
				if (	currentRomaji.equals("ajiakenkyuu") == true ||
						currentRomaji.equals("pinku iro no") == true ||
						currentRomaji.equals("saboru") == true ||
						currentRomaji.equals("daietto suru") == true ||
						currentRomaji.equals("niyaniya suru") == true ||
						currentRomaji.equals("puropoozu suru") == true ||
						currentRomaji.equals("to tsu") == true ||
						currentRomaji.equals("ki tsu") == true ||
						currentRomaji.equals("ga tsu") == true ||
						currentRomaji.equals("tonkatsu") == true ||
						currentRomaji.equals("basutei") == true ||
						currentRomaji.equals("keshigomu") == true ||
						currentRomaji.equals("anzen pin") == true ||
						currentRomaji.equals("denshi meeru") == true) {
					continue;
				}
								
				KanaWord kanaWord = null;
				KanaWord currentKanaAsKanaAsKanaWord = KanaHelper.convertKanaStringIntoKanaWord(currentKana, hiraganaEntries, katakanaEntries);
				
				String currentKanaAsRomaji = KanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);
				
				if (currentKanaAsRomaji.equals("ajiakenkyuu") == true ||
						currentKanaAsRomaji.equals("pinkuirono") == true ||
						currentKanaAsRomaji.equals("saboru") == true ||
						currentKanaAsRomaji.equals("daiettosuru") == true ||
						currentKanaAsRomaji.equals("niyaniyasuru") == true ||
						currentKanaAsRomaji.equals("puropoozusuru") == true ||
						currentKana.equals("とっ") == true ||
						currentKana.equals("きっ") == true ||
						currentKana.equals("がっ") == true) {
					continue;
				}
				
				if (polishJapaneseEntry.getWordType() == WordType.HIRAGANA) { 
					kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, currentRomaji);
				} else if (polishJapaneseEntry.getWordType() == WordType.KATAKANA) { 
					kanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, currentRomaji);
				} else {
					throw new RuntimeException("Bard word type");
				}
				
				if (kanaWord.remaingRestChars.equals("") == false) {
					throw new JapaneseDictionaryException("Validate error for word: " + currentRomaji + ", remaing: " + kanaWord.remaingRestChars);
				}
												
				if ((prefixKana + currentKana).equals(KanaHelper.createKanaString(kanaWord)) == false) {
					throw new JapaneseDictionaryException("Validate error for word: " + currentRomaji + ": " + currentKana + " - " + KanaHelper.createKanaString(kanaWord));
				}
				
				// is hiragana word
				KanaWord currentKanaAsRomajiAsHiraganaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, currentKanaAsRomaji);
				String currentKanaAsRomajiAsHiraganaWordAsAgainKana = KanaHelper.createKanaString(currentKanaAsRomajiAsHiraganaWord);

				// is katakana word
				KanaWord currentKanaAsRomajiAsKatakanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, currentKanaAsRomaji);
				String currentKanaAsRomajiAsKatakanaWordAsAgainKana = KanaHelper.createKanaString(currentKanaAsRomajiAsKatakanaWord);

				if (currentKana.equals(currentKanaAsRomajiAsHiraganaWordAsAgainKana) == false &&
						currentKana.equals(currentKanaAsRomajiAsKatakanaWordAsAgainKana) == false) {

					throw new JapaneseDictionaryException("Validate error for word: " + currentKana + " (" + currentKanaAsRomaji + ") vs " + currentKanaAsRomajiAsHiraganaWordAsAgainKana + " or " + currentKanaAsRomajiAsKatakanaWordAsAgainKana);					
				}
			}
		}
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
	
	private static List<PolishJapaneseEntry> findPolishJapaneseKanjiEntry(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, String kanji) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			
			if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			if (polishJapaneseEntry.getKanji().equals(kanji)) {
				result.add(polishJapaneseEntry);
			}
		}
		
		return result;
	}
	*/	
}
