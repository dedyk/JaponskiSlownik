package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class GenerateMultiTranslateNameList {
	
	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		List<PolishJapaneseEntry> namesList = Helper.generateNames(jmedictName);
				
		List<PolishJapaneseEntry> multiName = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			if (polishJapaneseEntry.getTranslates().size() > 1) {
				multiName.add(polishJapaneseEntry);
			}
		}
		
		CsvReaderWriter.generateCsv("input_names2/multiTranslateName.csv", multiName, false);
	}
}
