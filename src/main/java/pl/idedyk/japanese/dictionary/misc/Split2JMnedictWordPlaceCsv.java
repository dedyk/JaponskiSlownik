package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class Split2JMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
		
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)\\ \\(.*\\)$";
		String matchTemplate = "^([A-Z]|[a-z]|'|-)*\\ \\(.*\\)$";
				
		List<PolishJapaneseEntry> waitingWordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/WORD_PLACE-oczekujace.csv");
		
		List<PolishJapaneseEntry> processingWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : waitingWordPlaceList) {
			
			String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			if (translate.matches(matchTemplate) == true) {
				
				System.out.println(translate);
				
				processingWordPlaceList.add(currentPolishJapaneseEntry);
			}			
		}		
		
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE-processing.csv", processingWordPlaceList, false);
	}
}
