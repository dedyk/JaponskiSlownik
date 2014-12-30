package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class RemoveNames {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		final int from = 8512;
		
		final int to = 8946;
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			int id = polishJapaneseEntry.getId();
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (id >= from && id <= to) {
				
				if (dictionaryEntryType != DictionaryEntryType.WORD_FEMALE_NAME && 
						dictionaryEntryType != DictionaryEntryType.WORD_MALE_NAME) {
					
					throw new RuntimeException("" + id);
				}
				
				result.add(null);
				
			} else {
				
				List<ParseAdditionalInfo> parseAdditionalInfoList = polishJapaneseEntry.getParseAdditionalInfoList();
				
				List<KnownDuplicate> knownDuplicatedList = polishJapaneseEntry.getKnownDuplicatedList();
				
				List<KnownDuplicate> newKnownDuplicatedList = new ArrayList<PolishJapaneseEntry.KnownDuplicate>();
				
				for (KnownDuplicate knownDuplicate : knownDuplicatedList) {
					
					int id2 = knownDuplicate.getId();
					
					if (id2 >= from && id2 <= to) {
						
						parseAdditionalInfoList.remove(ParseAdditionalInfo.NO_ALTERNATIVE);
						parseAdditionalInfoList.remove(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF);						
						
					} else {						
						newKnownDuplicatedList.add(knownDuplicate);						
					}					
				}
				
				polishJapaneseEntry.setKnownDuplicatedList(newKnownDuplicatedList);
								
				result.add(polishJapaneseEntry);
			}			
		}		
		
		CsvReaderWriter.generateCsv("input/word-nowy.csv", result, true, true, false);
	}
}
