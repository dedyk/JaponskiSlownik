package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class SplitJMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
		
		final String[] matches = {
			"^([A-Z]|[a-z])*$"
				
				
		};
		
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/WORD_PLACE.csv");
		
		List<PolishJapaneseEntry> readyWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> waitingWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			//System.out.println(translate);
			
			boolean ok = false;
			
			for (String currentMatch : matches) {
				
				if (translate.matches(currentMatch) == true) {
					ok = true;
					
					break;
				}				
			}
			
			if (ok == true) {
				readyWordPlaceList.add(currentPolishJapaneseEntry);
			} else {
				waitingWordPlaceList.add(currentPolishJapaneseEntry);
			}
		}		
		
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE-gotowe.csv", readyWordPlaceList, false);
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE-oczekujace.csv", waitingWordPlaceList, false);
	}
}
