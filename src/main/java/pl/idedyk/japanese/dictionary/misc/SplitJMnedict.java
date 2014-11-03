package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class SplitJMnedict {

	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmedictName);
		
		TreeMap<DictionaryEntryType, List<PolishJapaneseEntry>> groupedPolishJapaneseEntryList = new 
				TreeMap<DictionaryEntryType, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : generatedNames) {			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			List<PolishJapaneseEntry> dictionaryEntryTypeList = groupedPolishJapaneseEntryList.get(dictionaryEntryType);
			
			if (dictionaryEntryTypeList == null) {
				dictionaryEntryTypeList = new ArrayList<PolishJapaneseEntry>();
				
				groupedPolishJapaneseEntryList.put(dictionaryEntryType, dictionaryEntryTypeList);
			}
			
			dictionaryEntryTypeList.add(polishJapaneseEntry);			
		}
		
		Iterator<DictionaryEntryType> dictionaryEntryTypeiterator = groupedPolishJapaneseEntryList.keySet().iterator();
		
		while (dictionaryEntryTypeiterator.hasNext()) {
			
			DictionaryEntryType dictionaryEntryType = dictionaryEntryTypeiterator.next();
			
			List<PolishJapaneseEntry> dictionaryEntryTypeList = groupedPolishJapaneseEntryList.get(dictionaryEntryType);
			
			CsvReaderWriter.generateCsv("input_names3/" + dictionaryEntryType + ".csv", dictionaryEntryTypeList, false);			
		}		
	}
}
