package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class Merge2JMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
				
		List<PolishJapaneseEntry> waitingWordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/WORD_PLACE-oczekujace.csv");
		
		Map<String, PolishJapaneseEntry> waitingWordPlaceMap = new TreeMap<String, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentWaitingPolishJapaneseEntry : waitingWordPlaceList) {
			waitingWordPlaceMap.put(getKey(currentWaitingPolishJapaneseEntry), currentWaitingPolishJapaneseEntry);
		}
		
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/WORD_PLACE.csv");
		
		List<PolishJapaneseEntry> newWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			PolishJapaneseEntry waitingPolishJapaneseEntry = waitingWordPlaceMap.get(getKey(currentPolishJapaneseEntry));
			
			if (waitingPolishJapaneseEntry != null) {
				
				waitingPolishJapaneseEntry.setId(currentPolishJapaneseEntry.getId());
				
				newWordPlaceList.add(waitingPolishJapaneseEntry);
				
			} else {
				newWordPlaceList.add(currentPolishJapaneseEntry);
				
			}
		}
		
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE.csv", newWordPlaceList, false);
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji();
	}
}
