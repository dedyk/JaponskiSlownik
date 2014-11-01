package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class JoinJMedictDictionary {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> allPolishJapaneseNamesList = new ArrayList<PolishJapaneseEntry>();
		
		Set<String> allPolishJapaneseNamesListMap = new TreeSet<String>();
		
		readDir(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names"));
		readDir(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1"));
		readDir(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss2"));
		readDir(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss3"));
		
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
	
	private static void readDir(List<PolishJapaneseEntry> allPolishJapaneseNamesList, Set<String> allPolishJapaneseNamesListMap, 
			File dir) throws Exception {
				
		File[] dirFileList = dir.listFiles();		
		
		for (File currentCsvDictionaryFile : dirFileList) {	
			
			if (currentCsvDictionaryFile.isFile() == true) {
				
				List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentCsvDictionaryFile.getAbsolutePath());
				
				for (PolishJapaneseEntry polishJapaneseEntry : parsePolishJapaneseEntriesFromCsv) {
					
					String key = getKey(polishJapaneseEntry);
					
					if (allPolishJapaneseNamesListMap.contains(key) == false) {
						
						allPolishJapaneseNamesListMap.add(key);
						
						allPolishJapaneseNamesList.add(polishJapaneseEntry);
					}					
				}
			}			
		}		
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKanaList().get(0) + "." + polishJapaneseEntry.getRomajiList().get(0) + 
				polishJapaneseEntry.getTranslates().get(0);
	}

}
