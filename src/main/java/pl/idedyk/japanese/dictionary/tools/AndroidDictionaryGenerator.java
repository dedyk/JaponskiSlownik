package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		checkPolishJapaneseEntries("input/word.csv", "output/word.csv");
	}
	
	private static void checkPolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
		
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName, null);
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			if (polishJapaneseEntries.get(idx).isUseEntry() == true) {
				result.add(polishJapaneseEntries.get(idx));
			}
	
		}
		
		CsvReaderWriter.generateCsv(destinationFileName, result);
	}
}
