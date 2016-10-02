package pl.idedyk.japanese.dictionary.misc;

import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class FixPrefixSuffixWords {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
		
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			//AttributeList attributeList = polishJapaneseEntry.getAttributeList();
			
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			boolean didOperation = false;
			
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				GroupEntry groupEntry = groupEntryList.get(0);
				
				Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
				
				if (groupEntryWordTypeList.size() == 0) {
					continue;
				}
				
				/*
				ETAP I
				
				if (attributeList.contains(AttributeType.PREFIX) == true) {	
					
					attributeList.remove(AttributeType.PREFIX);
					
					if (groupEntryWordTypeList.contains("pref") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PREFIX) == false) {
						
						didOperation = true;
						
						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_PREFIX);						
					}
					
					if (groupEntryWordTypeList.contains("n-pref") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN_PREFIX) == false) {
						
						didOperation = true;
						
						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NOUN_PREFIX);						
					}
				}
				
				if (attributeList.contains(AttributeType.SUFFIX) == true) {	
					
					attributeList.remove(AttributeType.SUFFIX);
					
					if (groupEntryWordTypeList.contains("suf") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_SUFFIX) == false) {
						
						didOperation = true;
						
						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_SUFFIX);						
					}
					
					if (groupEntryWordTypeList.contains("n-suf") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN_SUFFIX) == false) {
						
						didOperation = true;
						
						dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NOUN_SUFFIX);						
					}
				}
				*/	
				
				// ETAP II
				/*
				if (groupEntryWordTypeList.contains("pref") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PREFIX) == false) {
					
					dictionaryEntryTypeList.add(DictionaryEntryType.WORD_PREFIX);
					
					didOperation = true;					
				}
				
				if (groupEntryWordTypeList.contains("suf") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_SUFFIX) == false) {
					
					dictionaryEntryTypeList.add(DictionaryEntryType.WORD_SUFFIX);
					
					didOperation = true;					
				}

				if (groupEntryWordTypeList.contains("n-pref") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN_PREFIX) == false) {
					
					dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NOUN_PREFIX);
					
					didOperation = true;					
				}

				if (groupEntryWordTypeList.contains("n-suf") == true && dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN_SUFFIX) == false) {
					
					dictionaryEntryTypeList.add(DictionaryEntryType.WORD_NOUN_SUFFIX);
					
					didOperation = true;					
				}
				*/
				
				// ETAP III
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_NOUN) == true && groupEntryWordTypeList.contains("n") == false && groupEntryWordTypeList.contains("vs") == false) {					
					dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_NOUN);					
				}
				

				
				
				
				
			} else if (groupEntryList == null) {
				
				/*
				ETAP I
				 
				if (attributeList.contains(AttributeType.PREFIX) == true) {	
					
					attributeList.remove(AttributeType.PREFIX);
					
					didOperation = true;
				}
				
				if (attributeList.contains(AttributeType.SUFFIX) == true) {	
					
					attributeList.remove(AttributeType.SUFFIX);
					
					didOperation = true;
				}
				*/
			}
			
			if (didOperation == true) {
				
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_EMPTY) == true) {						
					dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_EMPTY);
				}
			}
		}

		CsvReaderWriter.generateCsv(new String[] { "input/word01-new.csv", "input/word02-new.csv" }, polishJapaneseEntries, true, true, false);
	}
	
}
