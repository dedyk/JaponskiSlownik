package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class UnknownWord {

	public static void main(String[] args) throws Exception {

		// read polish japanese entries
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		// read edict
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader
				.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		TreeMap<String, List<PolishJapaneseEntry>> uniquePos = new TreeMap<String, List<PolishJapaneseEntry>>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryTypeList().get(0);

			if (dictionaryEntryType != DictionaryEntryType.UNKNOWN) {
				continue;
			}

			List<JMEDictEntry> foundJMEDictList = findJMEdictEntry(jmedict, polishJapaneseEntry);

			if (foundJMEDictList != null) {

				for (JMEDictEntry foundJMEDict : foundJMEDictList) {

					List<String> pos = foundJMEDict.getPos();

					Collections.sort(pos);

					String currentUniquePos = pos.toString();

					List<PolishJapaneseEntry> uniquePosPolishJapaneseEntryList = uniquePos.get(currentUniquePos);

					if (uniquePosPolishJapaneseEntryList == null) {
						uniquePosPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();

						uniquePos.put(currentUniquePos, uniquePosPolishJapaneseEntryList);
					}

					uniquePosPolishJapaneseEntryList.add(polishJapaneseEntry);

					/*
					if (currentUniquePos.equals("[adj-no, n]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_NOUN);

						System.out.println(polishJapaneseEntry.getId());
					}

					if (currentUniquePos.equals("[adv]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_ADVERB);
					}
					
					*/

					if (currentUniquePos.equals("[n, n-pref]") == true) {
						System.out.println(polishJapaneseEntry.getId());
					}
				}
			}
		}

		Iterator<String> uniquePosIterator = uniquePos.keySet().iterator();

		while (uniquePosIterator.hasNext()) {

			String next = uniquePosIterator.next();

			List<PolishJapaneseEntry> list = uniquePos.get(next);

			System.out.print(next + ": ");

			for (PolishJapaneseEntry polishJapaneseEntry : list) {
				System.out.print(polishJapaneseEntry.getId() + " ");
			}

			System.out.println();
		}

		CsvReaderWriter.generateCsv("input/word-wynik.csv", polishJapaneseEntries, true);
	}

	private static List<JMEDictEntry> findJMEdictEntry(TreeMap<String, List<JMEDictEntry>> jmedict,
			PolishJapaneseEntry polishJapaneseEntry) {

		String kanji = polishJapaneseEntry.getKanji();

		if (kanji != null && kanji.equals("-") == true) {
			kanji = null;
		}

		List<String> kanaList = polishJapaneseEntry.getKanaList();

		List<JMEDictEntry> foundEdict = null;

		for (String currentKana : kanaList) {

			foundEdict = jmedict.get(JMEDictReader.getMapKey(kanji, currentKana));

			if (foundEdict != null) {
				break;
			}
		}

		return foundEdict;
	}
}
