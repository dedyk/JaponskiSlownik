package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class RegenerateDictionary {

	public static void main(String[] args) throws Exception {

		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });

		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, false);

		List<PolishJapaneseEntry> resultPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();

		int id = 1;

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			currentPolishJapaneseEntry.setId(id);

			resultPolishJapaneseEntries.add(currentPolishJapaneseEntry);

			id++;
		}

		for (PolishJapaneseEntry currentPolishJapaneseEntry : resultPolishJapaneseEntries) {

			System.out.println(currentPolishJapaneseEntry.getId());
			
			String kana = currentPolishJapaneseEntry.getKana();
			
			List<KnownDuplicate> knownDuplicatedList = currentPolishJapaneseEntry.getKnownDuplicatedList();
			
			List<KnownDuplicate> newKnownDuplicatedList = new ArrayList<PolishJapaneseEntry.KnownDuplicate>();
			
			for (KnownDuplicate knownDuplicate : knownDuplicatedList) {
				
				if (knownDuplicate.getKnownDuplicateType() != KnownDuplicateType.DUPLICATE) {
					newKnownDuplicatedList.add(knownDuplicate);
				}				
			}
			
			generateKnownDuplicatedIdForKanji(newKnownDuplicatedList, polishJapaneseEntries,
					currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji());

			generateKnownDuplicatedIdFormKanjiAndKana(newKnownDuplicatedList, polishJapaneseEntries,
					currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji(), kana);

			currentPolishJapaneseEntry.setKnownDuplicatedList(newKnownDuplicatedList);
		}

		CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv" }, resultPolishJapaneseEntries, true, true, false, true, null);
	}

	private static List<KnownDuplicate> generateKnownDuplicatedIdForKanji(List<KnownDuplicate> result,
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries,
			int id, String kanji) {

		if (kanji.equals("-") == true) {
			return result;
		}

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKanji().equals(kanji)) {
				
				KnownDuplicate knownDuplicate = new KnownDuplicate(KnownDuplicateType.DUPLICATE, polishJapaneseEntry.getId());
				
				if (result.contains(knownDuplicate) == false) {
					result.add(knownDuplicate);
				}				
			}
		}

		return result;
	}

	private static void generateKnownDuplicatedIdFormKanjiAndKana(List<KnownDuplicate> result,
			List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, String kanji, String kana) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			boolean differentKanji = !kanji.equals(polishJapaneseEntry.getKanji());

			if (kanji.equals("-") == true && polishJapaneseEntry.getKanji().equals("-") == false) {
				differentKanji = false;
			}

			if (kanji.equals("-") == false && polishJapaneseEntry.getKanji().equals("-") == true) {
				differentKanji = false;
			}

			if (polishJapaneseEntry.getId() != id && differentKanji == false && polishJapaneseEntry.getKana().equals(kana) == true) {
				
				KnownDuplicate knownDuplicate = new KnownDuplicate(KnownDuplicateType.DUPLICATE, polishJapaneseEntry.getId());
				
				if (result.contains(knownDuplicate) == false) {
					result.add(knownDuplicate);
				}
			}
		}
	}
}
