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
		
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_MALE_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_PERSON.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_STATION_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_FEMALE_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_SURNAME_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/WORD_PLACE.csv"));
		
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1/WORD_MALE_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1/WORD_PERSON.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1/WORD_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1/WORD_FEMALE_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss1/WORD_SURNAME_NAME.csv"));
		
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss2/WORD_PLACE.csv"));
		
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss3/WORD_UNCLASS_NAME.csv"));
		
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss4/WORD_PRODUCT_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss4/WORD_COMPANY_NAME.csv"));
		readFile(allPolishJapaneseNamesList, allPolishJapaneseNamesListMap, new File("input_names/miss4/WORD_ORGANIZATION_NAME.csv"));
				
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
				CsvReaderWriter.generateCsv(new String[] { "input_names/names.csv_" + counter }, partialPolishJapaneseEntryList, true, false, true, false, null);
				
				partialPolishJapaneseEntryList.clear();
				
				counter++;
			}			
		}
		
		if (partialPolishJapaneseEntryList.size() > 0) {
			CsvReaderWriter.generateCsv(new String[] { "input_names/names.csv_" + counter }, partialPolishJapaneseEntryList, true, false, true, false, null);
		}		
	}
	
	private static void readFile(List<PolishJapaneseEntry> allPolishJapaneseNamesList, Map<String, PolishJapaneseEntry> allPolishJapaneseNamesListMap, 
			File file) throws Exception {
		
		List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { file.getAbsolutePath() });
		
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
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji() + 
				polishJapaneseEntry.getTranslates().toString();
	}

}
