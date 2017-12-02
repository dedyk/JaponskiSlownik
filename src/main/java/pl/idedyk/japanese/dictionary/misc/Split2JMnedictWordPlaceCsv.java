package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class Split2JMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
		
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)\\ \\(.*\\)$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)*\\ \\(.*\\)$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-| )*\\(.*\\)$";
		//String matchTemplate = ".* de .*";
		//String matchTemplate = ".* di .*";
		//String matchTemplate = ".* du .*";
		//String matchTemplate = ".* University$";
		//String matchTemplate = ".*大学$";
		//String matchTemplate = ".*Republic.*";
		//String matchTemplate = ".*Sea.*";
		//String matchTemplate = ".*Range.*";
		//String matchTemplate = ".*Canal.*";
		//String matchTemplate = ".*\\(park\\).*";
		//String matchTemplate = ".*Gulf.*";
		//String matchTemplate = ".*Desert.*";
		//String matchTemplate = ".*Peninsula.*";
		//String matchTemplate = ".*Poland.*";
		//String matchTemplate = ".*France.*";
		//String matchTemplate = ".*Russia\\)$";
		//String matchTemplate = ".*\\(Australia\\)$";
		//String matchTemplate = ".*\\(China\\)$";
		//String matchTemplate = ".*\\(Italy\\)$";
		//String matchTemplate = ".*\\(Britain\\)$";
		//String matchTemplate = ".*\\(Germany\\)$";
		//String matchTemplate = ".*\\(.*\\)$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)*\\ \\(([A-Z]|[a-z]|'|-)*\\)$";
		//String matchTemplate = "^([A-Z]|[a-z]|)*\\ ([A-Z]|[a-z]|)*$";
		//String matchTemplate = "^.* von .*$";
		//String matchTemplate = "^.* de .*$";
		//String matchTemplate = "^.* der .*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-| )* Corporation$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-| )* Institute$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-| )* Association$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		//String matchTemplate = "^([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)* ([A-Z]|[a-z]|'|-)*$";
		String matchTemplate = "^([A-Z]|[a-z]|'|-| |,)*$";
		
		List<PolishJapaneseEntry> waitingWordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input_names/miss4/WORD_ORGANIZATION_NAME-oczekujace.csv" });
		//List<PolishJapaneseEntry> waitingWordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names2/WORD_PLACE.csv");
		
		List<PolishJapaneseEntry> processingWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		// Random random = new Random();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : waitingWordPlaceList) {
			
			//String kanji = currentPolishJapaneseEntry.getKanji();
			String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			//if (kanji.matches(matchTemplate) == true) {
			if (translate.matches(matchTemplate) == true) {			
			//if (random.nextInt(30) < 10) {
			
				System.out.println(translate);
				
				processingWordPlaceList.add(currentPolishJapaneseEntry);
			}			
		}		
		
		CsvReaderWriter.generateCsv(new String[] { "input_names/miss4/WORD_ORGANIZATION_NAME-processing.csv" }, processingWordPlaceList, true, false, true, false, null);
	}
}
