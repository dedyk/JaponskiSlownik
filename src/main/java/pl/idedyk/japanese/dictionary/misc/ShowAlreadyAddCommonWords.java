package pl.idedyk.japanese.dictionary.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ShowAlreadyAddCommonWords {

	public static void main(String[] args) throws Exception {
		
		// czytanie pliku ze slownikiem
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = 
				Helper.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		
		// czytanie listy common'owych plikow
		Map<Integer, CommonWord> commonWordMap = CsvReaderWriter.readCommonWordFile("input/common_word.csv");
				
		// przegladanie listy common'owych plikow i sprawdzanie, czy nie jest juz dodany
		Collection<CommonWord> commonWordValues = commonWordMap.values();
		
		Iterator<CommonWord> commonWordValuesIterator = commonWordValues.iterator();
		
		Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
		
		while (commonWordValuesIterator.hasNext() == true) {
			
			CommonWord currentCommonWord = commonWordValuesIterator.next();
			
			if (currentCommonWord.isDone() == false) {
				
				List<PolishJapaneseEntry> findPolishJapaneseEntry = 
						Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, currentCommonWord.getKanji(), currentCommonWord.getKana());
				
				// ta pozycja jej juz dodana
				if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
					
					newCommonWordMap.put(currentCommonWord.getId(), currentCommonWord);				
				}
			}
		}
		
		// zapis juz dodanych common'owych slow
		CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/already-added-common_word.csv");		
	}
}
