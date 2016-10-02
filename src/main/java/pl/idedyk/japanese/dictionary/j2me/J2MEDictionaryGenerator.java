package pl.idedyk.japanese.dictionary.j2me;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;
import pl.idedyk.japanese.dictionary.tools.KanjiImageWriter;

public class J2MEDictionaryGenerator {

	public static void main(String[] args) throws IOException, JapaneseDictionaryException, Exception {

		String kanjiOutputDir = "output";
		Map<String, String> charsCache = new HashMap<String, String>();

		KanaHelper kanaHelper = new KanaHelper();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();
		generateHiraganaImages(hiraganaEntries, charsCache, kanjiOutputDir);

		// katakana
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();
		generateKatakanaImages(katakanaEntries, charsCache, kanjiOutputDir);

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		// read name edict
		List<JMEDictNewNativeEntry> jmedictNameNativeList = jmedictNewReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		JMENewDictionary jmeNewNameDictionary = jmedictNewReader.createJMENewDictionary(jmedictNameNativeList);

		// read new jmedict
		System.out.println("new jmedict");
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		// Słowniczek
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
		
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, jmeNewDictionary, jmeNewNameDictionary);
		
		generateKanjiImages(polishJapaneseEntries, charsCache, kanjiOutputDir);

		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, true);

		// kanji dictionary
		//List<PolishJapaneseEntry> polishJapaneseKanjiEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" }, null);
		//Validator.validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries);
		//generateKanjiImages(polishJapaneseKanjiEntries, charsCache, kanjiOutputDir);

		// validate dictionary and kanji dictionary
		/*
		List<PolishJapaneseEntry> joinedDictionary = new ArrayList<PolishJapaneseEntry>();
		joinedDictionary.addAll(polishJapaneseEntries);
		joinedDictionary.addAll(polishJapaneseKanjiEntries);
		
		Validator.validateDictionaryAndKanjiDictionary(joinedDictionary);
		*/

		CsvReaderWriter.generateDictionaryApplicationResult("output/japanese_polish_dictionary.properties",
				polishJapaneseEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/hiragana.properties", hiraganaEntries);
		CsvReaderWriter.generateKanaEntriesCsv(kanjiOutputDir + "/katakana.properties", katakanaEntries);

		System.out.println("Done");
	}

	private static void generateHiraganaImages(List<KanaEntry> hiraganaEntries, Map<String, String> kanjiCache,
			String kanjiOutputDir) throws JapaneseDictionaryException {
		for (KanaEntry kanaEntry : hiraganaEntries) {
			String image = KanjiImageWriter
					.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());

			kanaEntry.setImage(image);
		}
	}

	private static void generateKatakanaImages(List<KanaEntry> katakanaEntries, Map<String, String> kanjiCache,
			String kanjiOutputDir) throws JapaneseDictionaryException {
		for (KanaEntry kanaEntry : katakanaEntries) {
			String image = KanjiImageWriter
					.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());

			kanaEntry.setImage(image);
		}
	}

	private static void generateKanjiImages(List<PolishJapaneseEntry> polishJapaneseEntries,
			Map<String, String> kanjiCache, String imageDir) throws JapaneseDictionaryException {
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			KanjiImageWriter.createNewKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
		}
	}
}
