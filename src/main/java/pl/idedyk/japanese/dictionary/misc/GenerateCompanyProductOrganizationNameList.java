package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class GenerateCompanyProductOrganizationNameList {
	
	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml", true);
		
		List<PolishJapaneseEntry> namesList = Helper.generateNames(jmedictName);
				
		List<PolishJapaneseEntry> newCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> alreadyAddedCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_COMPANY_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRODUCT_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ORGANIZATION_NAME) == true) {
				
				if (dictionaryEntryTypeList.size() == 1) {
					newCompanyProductOrganizationName.add(polishJapaneseEntry);
					
				} else {
					alreadyAddedCompanyProductOrganizationName.add(polishJapaneseEntry);
				}				
			}			
		}
		
		CsvReaderWriter.generateCsv("input_names2/newCompanyProductOrganizationName.csv", newCompanyProductOrganizationName, false);
		CsvReaderWriter.generateCsv("input_names2/alreadyAddedCompanyProductOrganizationName.csv", alreadyAddedCompanyProductOrganizationName, false);
	}
}
