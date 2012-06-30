package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanaHelper.KanaWord;
import pl.idedyk.japanese.dictionary.tools.KanjiImageWriter;

public class GenkiBookWords {

	public static void main(String[] args) throws IOException, JapaneseDictionaryException {

		String kanjiOutputDir = "output";
		Map<String, String> charsCache = new HashMap<String, String>();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		generateHiraganaImages(hiraganaEntries, charsCache, kanjiOutputDir);
		
		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		generateKatakanaImages(katakanaEntries, charsCache, kanjiOutputDir);
		
		// Słowniczek
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries);	
		generateKanjiImages(polishJapaneseEntries, charsCache, kanjiOutputDir);
		
		// kanji dictionary
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/kanji_word.csv");
		validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries);
		generateKanjiImages(polishJapaneseKanjiEntries, charsCache, kanjiOutputDir);
		
		// validate dictionary and kanji dictionary
		List<PolishJapaneseEntry> joinedDictionary = new ArrayList<PolishJapaneseEntry>();
		joinedDictionary.addAll(polishJapaneseEntries);
		joinedDictionary.addAll(polishJapaneseKanjiEntries);
		
		//validateDictionaryAndKanjiDictionary(joinedDictionary);
		
		CsvReaderWriter.generateDictionaryApplicationResult("output/japanese_polish_dictionary.properties", polishJapaneseEntries, true);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/hiragana.properties", hiraganaEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/katakana.properties", katakanaEntries);
		CsvReaderWriter.generateDictionaryApplicationResult(kanjiOutputDir + "/kanji_dictionary.properties", polishJapaneseKanjiEntries, true);
				
		System.out.println("Done");
	}
	private static void generateHiraganaImages(List<KanaEntry> hiraganaEntries, Map<String, String> kanjiCache, String kanjiOutputDir) throws JapaneseDictionaryException {
		
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			String image = KanjiImageWriter.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());
			
			kanaEntry.setImage(image);
		}
	}

	private static void generateKatakanaImages(List<KanaEntry> katakanaEntries, Map<String, String> kanjiCache, String kanjiOutputDir) throws JapaneseDictionaryException {		
		for (KanaEntry kanaEntry : katakanaEntries) {
			String image = KanjiImageWriter.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());
			
			kanaEntry.setImage(image);
		}
	}
	
	private static void generateKanjiImages(List<PolishJapaneseEntry> polishJapaneseEntries, Map<String, String> kanjiCache, String imageDir) throws JapaneseDictionaryException {
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			KanjiImageWriter.createNewKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
		}
	}
		
	private static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, List<KanaEntry> hiraganaEntries,
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
			
			List<String> kanaList = polishJapaneseEntry.getKanaList();
			List<String> romajiList = polishJapaneseEntry.getRomajiList();
			String prefix = polishJapaneseEntry.getPrefix();
			
			for (int idx = 0; idx < romajiList.size(); ++idx) {
				
				String currentRomaji = romajiList.get(idx);
				String currentKana = kanaList.get(idx);
				
				KanaWord kanaWord = null;
				KanaWord currentKanaAsKanaAsKanaWord = KanaHelper.convertKanaStringIntoKanaWord(currentKana, hiraganaEntries, katakanaEntries);
				
				String currentKanaAsRomaji = KanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);
				
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
				
				if (currentRomaji.equals("ajiakenkyuu") == true ||
						currentRomaji.equals("pinku iro no") == true ||
						currentRomaji.equals("saboru") == true ||
						currentRomaji.equals("daietto suru") == true ||
						currentRomaji.equals("niyaniya suru") == true ||
						currentRomaji.equals("puropoozu suru") == true ||
						currentRomaji.equals("to tsu") == true ||
						currentRomaji.equals("ki tsu") == true ||
						currentRomaji.equals("ga tsu") == true) {
					continue;
				}
				
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
								
				if ((prefix + currentKana).equals(KanaHelper.createKanaString(kanaWord)) == false) {
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
