package pl.idedyk.japanese.dictionary.misc;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;

import com.csvreader.CsvWriter;

public class ShowMissingCommonWords {

	public static void main(String[] args) throws Exception {

		String edictCommonFileName = "../JapaneseDictionary_additional/edict_sub-utf8";

		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict(edictCommonFileName);

		// read polish japanese entries
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		// jmedict common iterator
		Iterator<EDictEntry> jmedictCommonIterator = jmedictCommon.values().iterator();

		// common word writer
		CsvWriter csvWriter = new CsvWriter(new FileWriter("input/common_word.csv"), ',');

		int csvId = 1;

		while (jmedictCommonIterator.hasNext()) {

			EDictEntry currentEdictEntry = jmedictCommonIterator.next();

			PolishJapaneseEntry findPolishJapaneseEntryResult = findPolishJapaneseEntry(polishJapaneseEntries,
					currentEdictEntry);

			if (findPolishJapaneseEntryResult == null) {
				System.out.println(currentEdictEntry);

				csvWriter.write(String.valueOf(csvId));
				csvWriter.write("");
				csvWriter.write(currentEdictEntry.getKanji());
				csvWriter.write(currentEdictEntry.getKana());
				csvWriter.write(currentEdictEntry.getPos().toString());
				csvWriter.write(currentEdictEntry.getRawLine());

				csvWriter.endRecord();

				csvId++;
			}
		}

		csvWriter.close();
	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			EDictEntry edictEntry) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
			List<String> polishJapaneseEntryKanaList = polishJapaneseEntry.getKanaList();

			String edictEntryKanji = edictEntry.getKanji();
			String edictEntryKana = edictEntry.getKana();

			if (findPolishJapaneseEntrySameKanji(polishJapaneseEntryKanji, edictEntryKanji) == true
					&& findPolishJapaneseEntrySameKana(polishJapaneseEntryKanaList, edictEntryKana) == true) {

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

	private static boolean findPolishJapaneseEntrySameKana(List<String> kanaList, String kana) {

		for (String currentKana : kanaList) {

			if (currentKana.equals(kana) == true) {
				return true;
			}
		}

		return false;
	}
}
