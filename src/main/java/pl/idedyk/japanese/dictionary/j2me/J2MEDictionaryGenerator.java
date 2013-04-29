package pl.idedyk.japanese.dictionary.j2me;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanjiImageWriter;

public class J2MEDictionaryGenerator {

	public static void main(String[] args) throws IOException, JapaneseDictionaryException, Exception {

		String kanjiOutputDir = "output";
		Map<String, String> charsCache = new HashMap<String, String>();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		generateHiraganaImages(hiraganaEntries, charsCache, kanjiOutputDir);
		
		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		generateKatakanaImages(katakanaEntries, charsCache, kanjiOutputDir);
		
		// read edict
		TreeMap<String, EDictEntry> jmedict = EdictReader.readEdict("../JaponskiSlownik_dodatki/edict-utf8");
		
		// Słowniczek
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv", null);
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, jmedict);	
		generateKanjiImages(polishJapaneseEntries, charsCache, kanjiOutputDir);
		
		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, false);
		
		// kanji dictionary
		//List<PolishJapaneseEntry> polishJapaneseKanjiEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv", null);
		//Validator.validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries);
		//generateKanjiImages(polishJapaneseKanjiEntries, charsCache, kanjiOutputDir);
		
		// validate dictionary and kanji dictionary
		/*
		List<PolishJapaneseEntry> joinedDictionary = new ArrayList<PolishJapaneseEntry>();
		joinedDictionary.addAll(polishJapaneseEntries);
		joinedDictionary.addAll(polishJapaneseKanjiEntries);
		
		Validator.validateDictionaryAndKanjiDictionary(joinedDictionary);
		*/
		
		CsvReaderWriter.generateDictionaryApplicationResult("output/japanese_polish_dictionary.properties", polishJapaneseEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/hiragana.properties", hiraganaEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/katakana.properties", katakanaEntries);
				
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
}
