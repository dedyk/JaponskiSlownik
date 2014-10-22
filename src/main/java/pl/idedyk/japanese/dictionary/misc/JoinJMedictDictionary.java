package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class JoinJMedictDictionary {

	public static void main(String[] args) throws Exception {
		
		File inputNamesDirFile = new File("input_names");
		
		File[] namesCsvDictionaryFileList = inputNamesDirFile.listFiles();
		
		List<PolishJapaneseEntry> allPolishJapaneseNamesList = new ArrayList<PolishJapaneseEntry>();
		
		for (File currentCsvDictionaryFile : namesCsvDictionaryFileList) {			
			allPolishJapaneseNamesList.addAll(CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentCsvDictionaryFile.getAbsolutePath()));			
		}
		
		int id = 1;
		
		for (PolishJapaneseEntry polishJapaneseEntry : allPolishJapaneseNamesList) {
			polishJapaneseEntry.setId(id);
			
			id++;
		}
		
		CsvReaderWriter.generateCsv("input_names/names.csv", allPolishJapaneseNamesList, false);
	}
}
