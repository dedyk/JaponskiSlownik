package pl.idedyk.japanese.dictionary.tools;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
		
		convertPolishJapaneseEntries("input/word.csv", "input/word-temp.csv");
		convertPolishJapaneseEntries("input/kanji_word.csv", "input/kanj_word-temp.csv");
	}
	
	private static void convertPolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			
			
		}
		
		CsvReaderWriter.generateCsv(destinationFileName, polishJapaneseEntries);
	}
}
