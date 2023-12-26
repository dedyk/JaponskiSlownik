package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class RegenerateDictionary {

	public static void main(String[] args) throws Exception {

		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" });

		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, false);

		/*
		List<PolishJapaneseEntry> resultPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();

		int id = 1;

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			currentPolishJapaneseEntry.setId(id);

			resultPolishJapaneseEntries.add(currentPolishJapaneseEntry);

			id++;
		}
		*/
		
		Map<String, List<PolishJapaneseEntry>> kanjiCache = new TreeMap<>();
		Map<String, List<PolishJapaneseEntry>> kanaCache = new TreeMap<>();
		
		// generowanie cache'u z kanji jako kluczem
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {
			
			// kanji cache
			if (currentPolishJapaneseEntry.isKanjiExists() == true) {
				
				List<PolishJapaneseEntry> listForKanji = kanjiCache.get(currentPolishJapaneseEntry.getKanji());
				
				if (listForKanji == null) {
					
					listForKanji = new ArrayList<>();
					
					kanjiCache.put(currentPolishJapaneseEntry.getKanji(), listForKanji);
				}
				
				listForKanji.add(currentPolishJapaneseEntry);				
			}
			
			// kana cache
			List<PolishJapaneseEntry> listForKana = kanaCache.get(currentPolishJapaneseEntry.getKana());
			
			if (listForKana == null) {
				
				listForKana = new ArrayList<>();
				
				kanaCache.put(currentPolishJapaneseEntry.getKana(), listForKana);
			}
			
			listForKana.add(currentPolishJapaneseEntry);			
		}
		
		//

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			System.out.println(currentPolishJapaneseEntry.getId());
			
			String kana = currentPolishJapaneseEntry.getKana();
			
			List<KnownDuplicate> knownDuplicatedList = currentPolishJapaneseEntry.getKnownDuplicatedList();
			
			List<KnownDuplicate> newKnownDuplicatedList = new ArrayList<PolishJapaneseEntry.KnownDuplicate>();
			
			for (KnownDuplicate knownDuplicate : knownDuplicatedList) {
				
				if (knownDuplicate.getKnownDuplicateType() != KnownDuplicateType.DUPLICATE) {
					newKnownDuplicatedList.add(knownDuplicate);
				}				
			}
			
			generateKnownDuplicatedIdForKanji(newKnownDuplicatedList, kanjiCache,
					currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji());

			generateKnownDuplicatedIdFormKanjiAndKana(newKnownDuplicatedList, kanaCache,
					currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji(), kana);
			
			currentPolishJapaneseEntry.setKnownDuplicatedList(newKnownDuplicatedList);
		}

		CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv", "input/word03-wynik.csv", "input/word04-wynik.csv" }, polishJapaneseEntries, true, true, false, true, null);
	}

	private static List<KnownDuplicate> generateKnownDuplicatedIdForKanji(List<KnownDuplicate> result,
			Map<String, List<PolishJapaneseEntry>> kanjiCache,
			int id, String kanji) {

		if (kanji.equals("-") == true) {
			return result;
		}
		
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = kanjiCache.get(kanji);

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
			Map<String, List<PolishJapaneseEntry>> kanaCache, int id, String kanji, String kana) {
		
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = kanaCache.get(kana);

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
