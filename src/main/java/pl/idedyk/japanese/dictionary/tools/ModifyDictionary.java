package pl.idedyk.japanese.dictionary.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
		
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
		
		convertPolishJapaneseEntries("input/word.csv", "input/word-temp.csv", hiraganaCache, katakanaCache);
		convertPolishJapaneseEntries("input/kanji_word.csv", "input/kanj_word-temp.csv", hiraganaCache, katakanaCache);
	}
	
	private static void convertPolishJapaneseEntries(String sourceFileName, String destinationFileName, 
			Map<String, KanaEntry> hiraganaCache, Map<String, KanaEntry> katakanaCache) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName);
		
		/*
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			KanaWord kanaWord = null;
			
			List<String> kanaList = new ArrayList<String>();
							
			for (String currentRomaji : polishJapaneseEntry.getRomajiList()) {
				if (polishJapaneseEntry.getWordType() == WordType.HIRAGANA) {
					kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, currentRomaji);
				} else if (polishJapaneseEntry.getWordType() == WordType.KATAKANA) { 
					kanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, currentRomaji);
				}
								
				kanaList.add(KanaHelper.createKanaString(kanaWord));
			}
			
			polishJapaneseEntry.setKanaList(kanaList);
		}
		*/
		
		CsvReaderWriter.generateCsv(destinationFileName, polishJapaneseEntries);
	}
}
