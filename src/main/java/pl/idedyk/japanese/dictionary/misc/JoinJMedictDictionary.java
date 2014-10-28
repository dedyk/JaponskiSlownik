package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class JoinJMedictDictionary {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> allPolishJapaneseNamesList = new ArrayList<PolishJapaneseEntry>();
		
		readDir(allPolishJapaneseNamesList, new File("input_names"));
		readDir(allPolishJapaneseNamesList, new File("input_names/miss1"));
		
		int id = 1;
		
		for (PolishJapaneseEntry polishJapaneseEntry : allPolishJapaneseNamesList) {
			polishJapaneseEntry.setId(id);
			
			id++;
		}
		
		final int maxPos = 300000;
		
		int counter = 1;
				
		List<PolishJapaneseEntry> partialPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : allPolishJapaneseNamesList) {
			
			partialPolishJapaneseEntryList.add(polishJapaneseEntry);

			if (partialPolishJapaneseEntryList.size() >= maxPos) {				
				CsvReaderWriter.generateCsv("input_names/names.csv_" + counter, partialPolishJapaneseEntryList, false);
				
				partialPolishJapaneseEntryList.clear();
				
				counter++;
			}			
		}
		
		if (partialPolishJapaneseEntryList.size() > 0) {
			CsvReaderWriter.generateCsv("input_names/names.csv_" + counter, partialPolishJapaneseEntryList, false);
		}		
	}
	
	private static void readDir(List<PolishJapaneseEntry> allPolishJapaneseNamesList, File dir) throws Exception {
				
		File[] dirFileList = dir.listFiles();		
		
		for (File currentCsvDictionaryFile : dirFileList) {	
			
			if (currentCsvDictionaryFile.isFile() == true) {
				allPolishJapaneseNamesList.addAll(CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentCsvDictionaryFile.getAbsolutePath()));
			}
			
		}		
	}
}
