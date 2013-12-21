package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class RegenerateDictionary {

	public static void main(String[] args) throws Exception {

		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, false);

		List<PolishJapaneseEntry> resultPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();

		int id = 1;

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			currentPolishJapaneseEntry.setId(id);

			resultPolishJapaneseEntries.add(currentPolishJapaneseEntry);

			id++;
		}

		for (PolishJapaneseEntry currentPolishJapaneseEntry : resultPolishJapaneseEntries) {

			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();

			Set<Integer> knownDuplicatedIds = generateKnownDuplicatedIdForKanji(polishJapaneseEntries,
					currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji());

			for (String currentKana : kanaList) {

				generateKnownDuplicatedIdFormKanjiAndKana(knownDuplicatedIds, polishJapaneseEntries,
						currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji(), currentKana);
			}

			currentPolishJapaneseEntry.setKnownDuplicatedId(knownDuplicatedIds);
		}

		CsvReaderWriter.generateCsv("input/word-wynik.csv", resultPolishJapaneseEntries, true);
	}

	private static Set<Integer> generateKnownDuplicatedIdForKanji(List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			int id, String kanji) {

		Set<Integer> result = new TreeSet<Integer>();

		if (kanji.equals("-") == true) {
			return result;
		}

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKanji().equals(kanji)) {
				result.add(polishJapaneseEntry.getId());
			}
		}

		return result;
	}

	private static void generateKnownDuplicatedIdFormKanjiAndKana(Set<Integer> result,
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, String kanji, String kana) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			boolean differentKanji = !kanji.equals(polishJapaneseEntry.getKanji());

			if (kanji.equals("-") == true && polishJapaneseEntry.getKanji().equals("-") == false) {
				differentKanji = false;
			}

			if (kanji.equals("-") == false && polishJapaneseEntry.getKanji().equals("-") == true) {
				differentKanji = false;
			}

			if (polishJapaneseEntry.getId() != id && differentKanji == false
					&& polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry.getId());
			}
		}
	}
}
