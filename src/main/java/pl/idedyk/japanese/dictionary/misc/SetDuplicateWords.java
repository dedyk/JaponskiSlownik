package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.csvreader.CsvReader;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class SetDuplicateWords {

	public static void main(String[] args) throws Exception {
				
		// wczytywanie pliku slownika
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		Map<Integer, PolishJapaneseEntry> polishJapaneseEntriesMap = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			polishJapaneseEntriesMap.put(polishJapaneseEntry.getId(), polishJapaneseEntry);			
		}
		
		// wczytywanie pliku z lista duplikatow
		
		CsvReader csvReader = new CsvReader(new FileReader("input/word-duplicate.csv"), ',');
		
		while (csvReader.readRecord()) {
			
			String status = csvReader.get(0);
			
			if (status != null && status.equals("1") == true) {
								
				String ids = csvReader.get(3);
				
				List<String> idsList = Utils.parseStringIntoList(ids, false);
				
				for (String currentId : idsList) {
					
					PolishJapaneseEntry polishJapaneseEntryForCurrentId = polishJapaneseEntriesMap.get(Integer.parseInt(currentId));

					// usuwanie innych niz DUPLICATE wpisow
					List<KnownDuplicate> knownDuplicatedList = polishJapaneseEntryForCurrentId.getKnownDuplicatedList();
					
					List<KnownDuplicate> newKnownDuplicatedList = new ArrayList<PolishJapaneseEntry.KnownDuplicate>();
					
					for (KnownDuplicate knownDuplicate : knownDuplicatedList) {
						
						if (knownDuplicate.getKnownDuplicateType() != KnownDuplicateType.DUPLICATE) {
							newKnownDuplicatedList.add(knownDuplicate);
						}				
					}
					
					generateKnownDuplicatedIdForKanji(newKnownDuplicatedList, polishJapaneseEntries,
							polishJapaneseEntryForCurrentId.getId(), polishJapaneseEntryForCurrentId.getKanji());

					generateKnownDuplicatedIdFormKanjiAndKana(newKnownDuplicatedList, polishJapaneseEntries,
							polishJapaneseEntryForCurrentId.getId(), polishJapaneseEntryForCurrentId.getKanji(), polishJapaneseEntryForCurrentId.getKana());

					polishJapaneseEntryForCurrentId.setKnownDuplicatedList(newKnownDuplicatedList);
				}				
			}
		}
				
		csvReader.close();

		CsvReaderWriter.generateCsv("input/word-wynik.csv", polishJapaneseEntries, true, true, false);
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
