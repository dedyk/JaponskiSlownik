package pl.idedyk.japanese.dictionary.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class FastDictionaryChecker {

	public static void main(String[] args) throws Exception {
		
		KanaHelper kanaHelper = new KanaHelper();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();

		// katakana
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		System.out.println("checkAndSavePolishJapaneseEntries: parsePolishJapaneseEntriesFromCsv");

		// parse csv
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		// validate

		System.out.println("checkAndSavePolishJapaneseEntries: validatePolishJapaneseEntries");
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, null, null, null);
	}
}
