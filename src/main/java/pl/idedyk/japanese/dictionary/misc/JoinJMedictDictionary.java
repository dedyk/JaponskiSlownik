package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class JoinJMedictDictionary {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> allPolishJapaneseNamesList = new ArrayList<PolishJapaneseEntry>();
		
		Map<String, PolishJapaneseEntry> allPolishJapaneseNamesListMap = new TreeMap<String, PolishJapaneseEntry>();
		
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
	
	private static void readDir(List<PolishJapaneseEntry> allPolishJapaneseNamesList, Map<String, PolishJapaneseEntry> allPolishJapaneseNamesListMap, 
			File dir) throws Exception {
				
		File[] dirFileList = dir.listFiles();		
		
		for (File currentCsvDictionaryFile : dirFileList) {	
			
			if (currentCsvDictionaryFile.isFile() == true) {
				
				List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentCsvDictionaryFile.getAbsolutePath());
				
				for (PolishJapaneseEntry polishJapaneseEntry : parsePolishJapaneseEntriesFromCsv) {
					
					String key = getKey(polishJapaneseEntry);
					
					PolishJapaneseEntry polishJapaneseEntryInMap = allPolishJapaneseNamesListMap.get(key);
					
					if (polishJapaneseEntryInMap == null) {
						
						allPolishJapaneseNamesListMap.put(key, polishJapaneseEntry);
						
						allPolishJapaneseNamesList.add(polishJapaneseEntry);
						
					} else {
						
						Set<DictionaryEntryType> dictionaryEntryUniqueList = new TreeSet<DictionaryEntryType>();
						
						dictionaryEntryUniqueList.addAll(polishJapaneseEntryInMap.getDictionaryEntryTypeList());
						dictionaryEntryUniqueList.addAll(polishJapaneseEntry.getDictionaryEntryTypeList());
						
						polishJapaneseEntryInMap.setDictionaryEntryTypeList(new ArrayList<DictionaryEntryType>(dictionaryEntryUniqueList));
					}
				}
			}			
		}		
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKanaList().toString() + "." + polishJapaneseEntry.getRomajiList().toString() + 
				polishJapaneseEntry.getTranslates().toString();
	}

}
