package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class DetectDuplicate {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
				
		Validator.detectDuplicatePolishJapaneseKanjiEntries(polishJapaneseEntries);		
	}
}
