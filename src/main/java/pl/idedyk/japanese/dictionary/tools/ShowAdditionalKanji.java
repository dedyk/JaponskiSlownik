package pl.idedyk.japanese.dictionary.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class ShowAdditionalKanji {

	public static void main(String[] args) throws Exception {

		String sourceWordFileName = "input/word.csv";
		String sourceKradFileName = "../JapaneseDictionary_additional/kradfile";
		String sourceKanjiDic2FileName = "../JapaneseDictionary_additional/kanjidic2.xml";
		String sourceKanjiName = "input/kanji.csv";
		String edictCommonFileName = "../JapaneseDictionary_additional/edict_sub-utf8";

		System.out.println("generateKanjiEntries");

		System.out.println("generateKanjiEntries: readKradFile");
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);

		System.out.println("generateKanjiEntries: readKanjiDic2");
		final Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName,
				kradFileMap);

		System.out.println("generateKanjiEntries: parseKanjiEntriesFromCsv");
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);

		System.out.println("generateKanjiEntries: parsePolishJapaneseEntriesFromCsv");
		// read polish japanese entries
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv(sourceWordFileName);

		System.out.println("generateKanjiEntries: readEdict");
		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict(edictCommonFileName);

		/*
		Set<String> kanjiEntriesSet = new HashSet<String>();

		for (KanjiEntry kanjiEntry : kanjiEntries) {
			kanjiEntriesSet.add(kanjiEntry.getKanji());
		}

		Iterator<String> kradFileMapKeyIterator = kradFileMap.keySet().iterator();

		TreeSet<KanjiDic2Entry> result = new TreeSet<KanjiDic2Entry>(new Comparator<KanjiDic2Entry>() {

			@Override
			public int compare(KanjiDic2Entry o1, KanjiDic2Entry o2) {

				return o1.getFreq().compareTo(o2.getFreq());
			}
		});

		while (kradFileMapKeyIterator.hasNext()) {

			String kradFileMapKeyIteratorCurrentKanji = kradFileMapKeyIterator.next();

			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(kradFileMapKeyIteratorCurrentKanji);

			Integer freq = kanjiDic2Entry.getFreq();

			if (freq == null) {
				continue;
			}

			if (kanjiEntriesSet.contains(kradFileMapKeyIteratorCurrentKanji) == true) {
				continue;
			}

			result.add(kanjiDic2Entry);
		}

		Iterator<KanjiDic2Entry> resultIterator = result.iterator();

		while (resultIterator.hasNext()) {

			KanjiDic2Entry currentKanjiDic2Entry = resultIterator.next();

			System.out.println(currentKanjiDic2Entry.getKanji() + " - " + currentKanjiDic2Entry.getFreq());
		}

		System.out.println("\nSize: " + result.size());
		*/

		Set<String> alreadySetKanji = new HashSet<String>();
		Set<String> alreadySetKanjiSource = new HashSet<String>();

		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			alreadySetKanji.add(currentKanjiEntry.getKanji());
			alreadySetKanjiSource.add(currentKanjiEntry.getKanji());
		}

		final Map<String, Integer> kanjiCountMap = new HashMap<String, Integer>();

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			String kanji = currentPolishJapaneseEntry.getKanji();

			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);

				if (kanjiDic2Entry != null) {

					if (alreadySetKanjiSource.contains(currentKanjiChar) == false) {

						Integer kanjiCountMapInteger = kanjiCountMap.get(currentKanjiChar);

						if (kanjiCountMapInteger == null) {
							kanjiCountMapInteger = new Integer(0);
						}

						kanjiCountMapInteger = kanjiCountMapInteger.intValue() + 1;

						kanjiCountMap.put(currentKanjiChar, kanjiCountMapInteger);
					}
				}

				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}

				if (kanjiDic2Entry != null) {
					alreadySetKanji.add(currentKanjiChar);
				}
			}
		}

		// generate additional top 2500 kanji
		Iterator<String> readKanjiDic2KeySetIterator = readKanjiDic2.keySet().iterator();

		while (readKanjiDic2KeySetIterator.hasNext()) {

			String readKanjiDic2KeySetIteratorCurrentKanji = readKanjiDic2KeySetIterator.next();

			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(readKanjiDic2KeySetIteratorCurrentKanji);

			Integer freq = kanjiDic2Entry.getFreq();

			if (freq == null) {
				continue;
			}

			if (alreadySetKanji.contains(readKanjiDic2KeySetIteratorCurrentKanji)) {
				continue;
			}

			alreadySetKanji.add(readKanjiDic2KeySetIteratorCurrentKanji);

			Integer kanjiCountMapInteger = kanjiCountMap.get(readKanjiDic2KeySetIteratorCurrentKanji);

			if (kanjiCountMapInteger == null) {
				kanjiCountMapInteger = new Integer(0);
			}

			kanjiCountMap.put(readKanjiDic2KeySetIteratorCurrentKanji, kanjiCountMapInteger);
		}

		// top 2500 end

		// generate common word additional kanji
		Collection<EDictEntry> jmedictCommonValues = jmedictCommon.values();
		Iterator<EDictEntry> jmedictCommonValuesIterator = jmedictCommonValues.iterator();

		while (jmedictCommonValuesIterator.hasNext()) {

			String kanji = jmedictCommonValuesIterator.next().getKanji();

			if (kanji == null) {
				continue;
			}

			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);

				if (kanjiDic2Entry != null) {

					if (alreadySetKanjiSource.contains(currentKanjiChar) == false) {

						Integer kanjiCountMapInteger = kanjiCountMap.get(currentKanjiChar);

						if (kanjiCountMapInteger == null) {
							kanjiCountMapInteger = new Integer(0);
						}

						kanjiCountMap.put(currentKanjiChar, kanjiCountMapInteger);
					}
				}

				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}

				if (kanjiDic2Entry != null) {

					alreadySetKanji.add(currentKanjiChar);
				}
			}
		}

		// generate common word additional kanji end

		String[] kanjiArray = new String[kanjiCountMap.size()];

		kanjiCountMap.keySet().toArray(kanjiArray);

		Arrays.sort(kanjiArray, new Comparator<String>() {

			@Override
			public int compare(String kanji1, String kanji2) {

				Integer kanji2Count = kanjiCountMap.get(kanji2);
				Integer kanji1Count = kanjiCountMap.get(kanji1);

				return kanji2Count.compareTo(kanji1Count);
			}
		});

		System.out.println("\n---\n");

		for (int kanjiArrayIdx = 0; kanjiArrayIdx < kanjiArray.length; ++kanjiArrayIdx) {

			String currentKanji = kanjiArray[kanjiArrayIdx];

			System.out.println(currentKanji + " - " + kanjiCountMap.get(currentKanji));
		}

		System.out.println("\nKanji: " + kanjiArray.length);

		System.out.println("\n---\n");

	}
}
