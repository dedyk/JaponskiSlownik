package pl.idedyk.japanese.dictionary.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanaHelper.KanaWord;

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

		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv", null);
		
		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, true);
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {
			
			String prefixKana = currentPolishJapaneseEntry.getPrefixKana();
			
			if (prefixKana != null && prefixKana.equals("お") == true) {
				System.out.println(currentPolishJapaneseEntry.getId());
			}
		}
		
		CsvReaderWriter.generateCsv("input/word-wynik.csv", polishJapaneseEntries, true);
		*/

		// TreeMap<String, EDictEntry> jmedict = EdictReader.readEdict("../JaponskiSlownik_dodatki/edict-utf8");
		/*
		System.out.println(jmedict.get(EdictReader.getMapKey("食べる", "たべる")));
		System.out.println(jmedict.get(EdictReader.getMapKey("集中", "しゅうちゅう")));
		System.out.println(jmedict.get(EdictReader.getMapKey(null, "デート")));
		
		System.out.println();
		*/
		/*
		TreeMap<String, EDictEntry> jmenamdict = EdictReader.readEdict("../JaponskiSlownik_dodatki/enamdict-utf8");
		
		Iterator<EDictEntry> iterator = jmenamdict.values().iterator();
		
		while(iterator.hasNext()) {
			EDictEntry edictEntry = iterator.next();
			
			System.out.println(edictEntry);			
		}
		*/
		
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		
		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
		KanaWord currentKanaAsKanaAsKanaWord = KanaHelper.convertKanaStringIntoKanaWord("けんあく", hiraganaEntries, katakanaEntries);
		
		String currentKanaAsRomaji = KanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);

		/*
		KanaWord kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, "ken'aku");
		String kanaString = KanaHelper.createKanaString(kanaWord);
		*/
		
		System.out.println(currentKanaAsRomaji);

	}
}
