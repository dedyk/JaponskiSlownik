package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
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
		
		// kanji dictionary
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/kanji_word.csv");
		generateKanjiImages(polishJapaneseKanjiEntries, charsCache, kanjiOutputDir);
		validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries);
		
		CsvReaderWriter.generateDictionaryApplicationResult("output/japanese_polish_dictionary.properties", polishJapaneseEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/hiragana.properties", hiraganaEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/katakana.properties", katakanaEntries);
		CsvReaderWriter.generateDictionaryApplicationResult(kanjiOutputDir + "/kanji_dictionary.properties", polishJapaneseKanjiEntries);
				
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
			List<KanaEntry> kitakanaEntries) throws JapaneseDictionaryException {
		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : kitakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			List<String> romajiList = polishJapaneseEntry.getRomajiList();
			
			for (String currentRomaji : romajiList) {
				
				KanaWord kanaWord = null;
				
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
			}
		}
	}
}
