package pl.idedyk.japanese.dictionary.misc;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ValidateTheSameRomajiFix {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
				
		List<PolishJapaneseEntry> polishJapaneseEntriesWithRomajiCorrected = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01-te-same-romaji-wynik.csv", "input/word02-te-same-romaji-wynik.csv" });
		
		// utworz mape z id'kami
		TreeMap<Integer, PolishJapaneseEntry> polishJapaneseEntriesWithRomajiCorrectedIdMap = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesWithRomajiCorrected) {
			polishJapaneseEntriesWithRomajiCorrectedIdMap.put(polishJapaneseEntry.getId(), polishJapaneseEntry);
		}

		// chodzenie po slowach i sprawdzamy, czy wystepuje to w mapie. jesli tak zmieniamy romaji na to z mapy
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			int id = polishJapaneseEntry.getId();
			
			PolishJapaneseEntry polishJapaneseEntryInMap = polishJapaneseEntriesWithRomajiCorrectedIdMap.get(id);
			
			if (polishJapaneseEntryInMap != null) {	
				
				System.out.println(polishJapaneseEntry.getRomaji() + " - " + polishJapaneseEntryInMap.getRomaji());
				
				polishJapaneseEntry.setRomaji(polishJapaneseEntryInMap.getRomaji());				
			}
		}		
		
		CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv" }, polishJapaneseEntries, true, true, false, true, null);
	}
}
