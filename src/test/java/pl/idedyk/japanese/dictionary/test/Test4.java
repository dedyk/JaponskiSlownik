package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class Test4 {

	public static void main(String[] args) throws Exception {
		
		//List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");;

		//generateExampleSentence(polishJapaneseEntries, "../JapaneseDictionary_additional/tatoeba", "output/sentences.csv", "output/sentences_groups.csv");

		// TreeSet<String> uniqueTrans = new TreeSet<String>();
		
		/*
		Iterator<List<JMEDictEntry>> jmedictNameValuesIterator = jmedictName.values().iterator();
		
		while(jmedictNameValuesIterator.hasNext()) {
			
			List<JMEDictEntry> jmedictValueList = jmedictNameValuesIterator.next();
			
			for (JMEDictEntry jmedictEntry : jmedictValueList) {				
				List<String> trans = jmedictEntry.getTrans();
				
				/ *
				for (String currentTrans : trans) {
					uniqueTrans.add(currentTrans);
				}
				* /
				
				if (trans.contains("unclass") == true) {
					System.out.println(jmedictEntry);
				}
			}
		}
		*/
		
		/*
		for (String currentUniqueTran : uniqueTrans) {
			System.out.println(currentUniqueTran);
		}
		*/		
		
		/*
		company -
		fem +
		given +
		masc +
		organization -
		person +
		place * ?
		product -
		station +
		surname +
		unclass * ?
		*/
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict-TEST.xml");
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmedictName);
		
		CsvReaderWriter.generateCsv("input_names3/test.csv", generatedNames, false);
		
		/*
		List<PolishJapaneseEntry> namesList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names2/names.csv");
		
		List<PolishJapaneseEntry> smallNamesList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_COMPANY_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRODUCT_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ORGANIZATION_NAME) == true) {
				
				if (dictionaryEntryTypeList.size() > 1) {					
					smallNamesList.add(polishJapaneseEntry);					
				}				
			}			
		}
		
		CsvReaderWriter.generateCsv("input_names2/names2.csv", smallNamesList, false);
		*/
	}
}
