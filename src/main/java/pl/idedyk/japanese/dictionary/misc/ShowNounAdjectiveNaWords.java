package pl.idedyk.japanese.dictionary.misc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ShowNounAdjectiveNaWords {

	public static void main(String[] args) throws Exception {

		// read polish japanese entries
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Set<String> alreadyWordSet = new HashSet<String>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String kanji = polishJapaneseEntry.getKanji();

			if (kanji != null && kanji.equals("-") == true) {
				kanji = "";
			}

			List<String> kanaList = polishJapaneseEntry.getKanaList();

			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			DictionaryEntryType dictionaryEntryType = null;

			if (dictionaryEntryTypeList.size() == 1) {
				dictionaryEntryType = dictionaryEntryTypeList.get(0);
			}

			if (dictionaryEntryType == DictionaryEntryType.WORD_NOUN
					|| dictionaryEntryType == DictionaryEntryType.WORD_ADJECTIVE_NA) {

				PolishJapaneseEntry foundPolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntries, kanji,
						kanaList,
						dictionaryEntryType == DictionaryEntryType.WORD_NOUN ? DictionaryEntryType.WORD_ADJECTIVE_NA
								: DictionaryEntryType.WORD_NOUN);

				if (foundPolishJapaneseEntry != null) {

					String uniqueKey = createUniqueKey(polishJapaneseEntry, foundPolishJapaneseEntry);

					if (alreadyWordSet.contains(uniqueKey) == false) {
						System.out.println(polishJapaneseEntry + "\n" + foundPolishJapaneseEntry + "\n");

						alreadyWordSet.add(uniqueKey);
					}
				}
			}
		}

	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			String kanji, List<String> kanaList, DictionaryEntryType dictionaryEntryType) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
			List<String> polishJapaneseEntryKanaList = polishJapaneseEntry.getKanaList();

			List<DictionaryEntryType> polishJapaneseEntryDictionaryEntryTypeList = polishJapaneseEntry
					.getDictionaryEntryTypeList();

			DictionaryEntryType polishJapaneseEntryDictionaryEntryType = null;

			if (polishJapaneseEntryDictionaryEntryTypeList.size() == 1) {
				polishJapaneseEntryDictionaryEntryType = polishJapaneseEntryDictionaryEntryTypeList.get(0);
			}

			if (polishJapaneseEntryKanji != null && polishJapaneseEntryKanji.equals("-") == true) {
				polishJapaneseEntryKanji = "";
			}

			if (polishJapaneseEntryKanji.equals(kanji) == true && polishJapaneseEntryKanaList.equals(kanaList) == true
					&& dictionaryEntryType == polishJapaneseEntryDictionaryEntryType) {
				return polishJapaneseEntry;
			}
		}

		return null;
	}

	private static String createUniqueKey(PolishJapaneseEntry polishJapaneseEntry1,
			PolishJapaneseEntry polishJapaneseEntry2) {

		PolishJapaneseEntry nounPolishJapaneseEntry = null;
		PolishJapaneseEntry adjectiveNaPolishJapaneseEntry = null;

		if (polishJapaneseEntry1.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_NOUN) {
			nounPolishJapaneseEntry = polishJapaneseEntry1;
			adjectiveNaPolishJapaneseEntry = polishJapaneseEntry2;

		} else {
			nounPolishJapaneseEntry = polishJapaneseEntry2;
			adjectiveNaPolishJapaneseEntry = polishJapaneseEntry1;

		}

		return nounPolishJapaneseEntry.getId() + "." + adjectiveNaPolishJapaneseEntry.getId();
	}
}
