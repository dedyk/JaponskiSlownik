package pl.idedyk.japanese.dictionary.tools;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String polishJapaneseInfo = polishJapaneseEntry.getInfo();
			
			DictionaryEntryType newDictionaryEntry = null;
			
			if (polishJapaneseInfo != null && polishJapaneseInfo.equals("") == false) {
				
				
				if (polishJapaneseInfo.indexOf("ru-czasownik") != -1) {					
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("ru-czasownik", "");
					
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_VERB_RU;
				} else if (polishJapaneseInfo.indexOf("u-czasownik") != -1) {
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("u-czasownik", "");
				
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_VERB_U;
				} else if (polishJapaneseInfo.indexOf("czasownik nieregularny") != -1) {
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("czasownik nieregularny", "");
					
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_VERB_IRREGULAR;
				} else if (polishJapaneseInfo.indexOf("forma te") != -1) {
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("forma te", "");
					
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_VERB_TE;
				} else if (polishJapaneseInfo.indexOf("i-przymiotnik") != -1) {
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("i-przymiotnik", "");
					
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_ADJECTIVE_I;
				} else if (polishJapaneseInfo.indexOf("na-przymiotnik") != -1) {
					polishJapaneseInfo = polishJapaneseInfo.replaceAll("na-przymiotnik", "");
					
					polishJapaneseInfo = fixInfo(polishJapaneseInfo);
					checkInfo(polishJapaneseInfo); // !!!!!!!!!!!!!!!
					
					newDictionaryEntry = DictionaryEntryType.WORD_ADJECTIVE_NA;
				}
				
				polishJapaneseEntry.setInfo(polishJapaneseInfo);
			}
			
//			if (newDictionaryEntry == null) {
//				newDictionaryEntry = DictionaryEntryType.UNKNOWN;
//			}
			
			if (newDictionaryEntry != null) {
				polishJapaneseEntry.setDictionaryEntryType(newDictionaryEntry);
			}
		}
		
		CsvReaderWriter.generateCsv("input/word-temp.csv", polishJapaneseEntries);
	}

	private static String fixInfo(String polishJapaneseInfo) {
		
		polishJapaneseInfo = polishJapaneseInfo.trim();
		
		if (polishJapaneseInfo.startsWith(",") == true) {
			polishJapaneseInfo = polishJapaneseInfo.substring(1);
		}
		
		if (polishJapaneseInfo.endsWith(",") == true) {
			polishJapaneseInfo = polishJapaneseInfo.substring(0, polishJapaneseInfo.length() - 1);
		}
		
		polishJapaneseInfo = polishJapaneseInfo.trim();
		
		return polishJapaneseInfo;
	}

	private static void checkInfo(String polishJapaneseInfo) {
		
		if (polishJapaneseInfo.length() > 0) {
			System.out.println(polishJapaneseInfo);
		}
		
	}
}
