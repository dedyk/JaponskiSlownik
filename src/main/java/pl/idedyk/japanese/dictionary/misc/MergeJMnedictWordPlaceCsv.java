package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class MergeJMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
				
		List<PolishJapaneseEntry> waitingWordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input_names/miss4/WORD_ORGANIZATION_NAME-oczekujace.csv" });
		
		Map<Integer, PolishJapaneseEntry> waitingWordPlaceMap = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentWaitingPolishJapaneseEntry : waitingWordPlaceList) {
			waitingWordPlaceMap.put(currentWaitingPolishJapaneseEntry.getId(), currentWaitingPolishJapaneseEntry);
		}
		
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input_names/miss4/WORD_ORGANIZATION_NAME.csv" });
		
		List<PolishJapaneseEntry> newWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			PolishJapaneseEntry waitingPolishJapaneseEntry = waitingWordPlaceMap.get(currentPolishJapaneseEntry.getId());
			
			if (waitingPolishJapaneseEntry != null) {
				newWordPlaceList.add(waitingPolishJapaneseEntry);
				
			} else {
				newWordPlaceList.add(currentPolishJapaneseEntry);
				
			}
		}
		
		CsvReaderWriter.generateCsv(new String[] { "input_names/miss4/WORD_ORGANIZATION_NAME.csv" }, newWordPlaceList, true, false, true, false, null);
	}
}
