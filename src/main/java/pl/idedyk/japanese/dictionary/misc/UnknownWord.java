package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
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
			
			/*
			if (dictionaryEntryType == DictionaryEntryType.WORD_GREETING) {
				polishJapaneseEntry.getDictionaryEntryTypeList().remove(DictionaryEntryType.WORD_GREETING);
				
				if (polishJapaneseEntry.getDictionaryEntryTypeList().size() == 0) {				
					polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.UNKNOWN);
				}
				
				dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryTypeList().get(0);
				
				polishJapaneseEntry.getGroups().add("Zwroty grzecznościowe");
			}
			*/
			

			if (dictionaryEntryType != DictionaryEntryType.UNKNOWN) {
				//continue;
			}

			/*
			if (polishJapaneseEntry.getDictionaryEntryTypeList().contains(DictionaryEntryType.WORD_NOUN) == false) {
				continue;
			}
			*/

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
					/*
					if (currentUniquePos.equals("[n-t]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_NOUN);
					}
					*/

					/*
					if (currentUniquePos.equals("[pref]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_EMPTY);

						if (polishJapaneseEntry.getAttributeList().contains(AttributeType.PREFIX) == false) {
							polishJapaneseEntry.getAttributeList().add(AttributeType.PREFIX);
						}
					}
					*/

					/*
					if (currentUniquePos.equals("[suf]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_EMPTY);

						if (polishJapaneseEntry.getAttributeList().contains(AttributeType.SUFFIX) == false) {
							polishJapaneseEntry.getAttributeList().add(AttributeType.SUFFIX);
						}
					}
					*/

					/*
					if (currentUniquePos.equals("[n, n-adv]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_NOUN);
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_ADVERBIAL_NOUN);
					}

					if (currentUniquePos.equals("[n-adv]") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_ADVERBIAL_NOUN);
					}
					*/

					/*
					if (currentUniquePos.equals("[n-suf]") == true) {
						polishJapaneseEntry.getDictionaryEntryTypeList().clear();
						polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_NOUN);

						if (polishJapaneseEntry.getAttributeList().contains(AttributeType.SUFFIX) == false) {
							polishJapaneseEntry.getAttributeList().add(AttributeType.SUFFIX);
						}
					}
					*/

					/*
					if (pos.contains("n") == false && pos.contains("n-t") == true) {

						polishJapaneseEntry.getDictionaryEntryTypeList()
								.set(polishJapaneseEntry.getDictionaryEntryTypeList().indexOf(
										DictionaryEntryType.WORD_NOUN), DictionaryEntryType.WORD_TEMPORAL_NOUN);
					}
					*/
					
					/*
					if (pos.contains("adj-f") == true) {
						//System.out.println(polishJapaneseEntry.getKanji() + " - " + polishJapaneseEntry.getKanaList());
						
						if (pos.contains("n") == false) {
							polishJapaneseEntry.getDictionaryEntryTypeList().remove(DictionaryEntryType.WORD_NOUN);
						}
						
						if (polishJapaneseEntry.getDictionaryEntryTypeList().contains(DictionaryEntryType.WORD_ADJECTIVE_F) == false) {
							polishJapaneseEntry.getDictionaryEntryTypeList().add(DictionaryEntryType.WORD_ADJECTIVE_F);
						}
					}
					*/
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

		String kana = polishJapaneseEntry.getKana();

		List<JMEDictEntry> foundEdict = jmedict.get(JMEDictReader.getMapKey(kanji, kana));

		return foundEdict;
	}
}
