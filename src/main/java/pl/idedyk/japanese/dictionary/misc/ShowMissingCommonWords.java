package pl.idedyk.japanese.dictionary.misc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;

public class ShowMissingCommonWords {

	public static void main(String[] args) throws Exception {

		String edictCommonFileName = "../JapaneseDictionary_additional/edict_sub-utf8";

		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict(edictCommonFileName);

		// read polish japanese entries
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });

		// jmedict common iterator
		Iterator<EDictEntry> jmedictCommonIterator = jmedictCommon.values().iterator();

		Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

		int csvId = 1;

		while (jmedictCommonIterator.hasNext()) {

			EDictEntry currentEdictEntry = jmedictCommonIterator.next();

			PolishJapaneseEntry findPolishJapaneseEntryResult = findPolishJapaneseEntry(polishJapaneseEntries,
					currentEdictEntry);

			if (findPolishJapaneseEntryResult == null) {
				
				System.out.println(currentEdictEntry);

				CommonWord commonWord = Helper.convertEDictEntryToCommonWord(csvId, currentEdictEntry);
				
				newCommonWordMap.put(commonWord.getId(), commonWord);
				
				csvId++;						
			}
		}
		
		// zapis do pliku
		CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/missing_common_word.csv");
	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			EDictEntry edictEntry) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
			String polishJapaneseEntryKana = polishJapaneseEntry.getKana();

			if (polishJapaneseEntryKanji != null && (polishJapaneseEntryKanji.equals("") == true || polishJapaneseEntryKanji.equals("-") == true)) {
				polishJapaneseEntryKanji = null;
			}

			String edictEntryKanji = edictEntry.getKanji();
			String edictEntryKana = edictEntry.getKana();

			if (findPolishJapaneseEntrySameKanji(polishJapaneseEntryKanji, edictEntryKanji) == true
					&& findPolishJapaneseEntrySameKana(polishJapaneseEntryKana, edictEntryKana) == true) {

				return polishJapaneseEntry;
			}
		}

		return null;
	}

	private static boolean findPolishJapaneseEntrySameKanji(String kanji1, String kanji2) {

		if (kanji1 == null && kanji2 == null) {
			return true;
		}

		if (kanji1 == null || kanji2 == null) {
			return false;
		}

		return kanji1.equals(kanji2);
	}

	private static boolean findPolishJapaneseEntrySameKana(String kana1, String kana2) {

		if (kana1.equals(kana2) == true) {
			return true;
		}

		return false;
	}
}
