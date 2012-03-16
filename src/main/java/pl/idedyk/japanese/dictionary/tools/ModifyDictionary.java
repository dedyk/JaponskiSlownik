package pl.idedyk.japanese.dictionary.tools;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
		
		checkPolishJapaneseEntries("input/word.csv");
	}
	
	private static void checkPolishJapaneseEntries(String sourceFileName) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName);
		
		int counter = 0;
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			
			if (kanji.equals("") == true) {
				counter++;
			}			
		}
		
		System.out.println(counter);
		
		CsvReaderWriter.generateCsv("input/word-temp.csv", polishJapaneseEntries);
	}
}
