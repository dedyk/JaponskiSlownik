package pl.idedyk.japanese.dictionary.misc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ShowVocabList {

	public static void main(String[] args) throws Exception {
		
		// wczytanie listy slow
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		// stworzenie cache
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = Helper.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		Map<Integer, CommonWord> commonWordMap = CsvReaderWriter.readCommonWordFile("../JapaneseDictionary_additional/tanos/vocabList.N5.csv");
				
		// pobranie slow
		Collection<CommonWord> values = commonWordMap.values();
		
		for (CommonWord currentCommonWord : values) {
						
			String commonKanji = currentCommonWord.getKanji();
			
			if (commonKanji.equals("") == true) {
				commonKanji = "-";
			}
			
			String commonKana = currentCommonWord.getKana();
			
			List<PolishJapaneseEntry> findPolishJapaneseEntry = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, commonKanji, commonKana);

			if (findPolishJapaneseEntry == null || findPolishJapaneseEntry.size() == 0) {
				System.out.println(commonKanji + " - " + commonKana);
			}
		}
	}
}
