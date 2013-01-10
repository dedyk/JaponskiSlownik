package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		/*
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
		KanaWord kanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, "Oosutoraria");
		
		System.out.println(KanaHelper.createKanaString(kanaWord));
		*/
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv", null);
		
		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, true);
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {
			
			String prefixKana = currentPolishJapaneseEntry.getPrefixKana();
			
			if (prefixKana != null && prefixKana.equals("お") == true) {
				System.out.println(currentPolishJapaneseEntry.getId());
			}
		}
		
		CsvReaderWriter.generateCsv("input/word-wynik.csv", polishJapaneseEntries);
	}
}
