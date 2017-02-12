package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryGroup;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class ValidateTheSameRomaji {

	public static void main(String[] args) throws Exception {
		
		// to jest klasa pomocnicza, tymczasowa
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
		List<PolishJapaneseEntry> polishJapaneseEntriesSource = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
		
		// utworz mape z id'kami
		TreeMap<Integer, PolishJapaneseEntry> polishJapaneseEntriesSourceIdMap = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesSource) {
			polishJapaneseEntriesSourceIdMap.put(polishJapaneseEntry.getId(), polishJapaneseEntry);
		}
		
		//
		
		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader
				.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");

		// read new jmedict
		System.out.println("new jmedict");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);

		
		Helper.generateAdditionalInfoFromEdict(jmeNewDictionary, jmedictCommon, polishJapaneseEntries);
		
		//
		
		List<PolishJapaneseEntry> result = new ArrayList<>();
		
		// kod bardzo podobny do Validator
		List<DictionaryEntryGroup> dictionaryEntryGroupList = Helper.generateDictionaryEntryGroup(polishJapaneseEntries);
		
		for (DictionaryEntryGroup dictionaryEntryGroup : dictionaryEntryGroupList) {
			
			// pobranie wszystkie slowa wchodzace w sklad grupy
			List<DictionaryEntry> dictionaryEntryList = dictionaryEntryGroup.getDictionaryEntryList();
			
			Map<String, List<DictionaryEntry>> romajiWithoutSpaceToDictionaryEntryListMap = new HashMap<>();
			
			for (DictionaryEntry dictionaryEntry : dictionaryEntryList) {
				
				// pobranie romaji
				String romaji = dictionaryEntry.getRomaji();
				
				// romaji bez spacji
				String romajiWithoutSpace = romaji.replaceAll(" ", "");
				
				List<DictionaryEntry> romajiWithoutSpaceToDictionaryEntryList = romajiWithoutSpaceToDictionaryEntryListMap.get(romajiWithoutSpace);
				
				if (romajiWithoutSpaceToDictionaryEntryList == null) {
					
					romajiWithoutSpaceToDictionaryEntryList = new ArrayList<>();
					
					romajiWithoutSpaceToDictionaryEntryListMap.put(romajiWithoutSpace, romajiWithoutSpaceToDictionaryEntryList);
				}
				
				romajiWithoutSpaceToDictionaryEntryList.add(dictionaryEntry);				
			}
			
			// sprawdzamy, czy wszystkie romaji bez spacji sa takie same
			Iterator<Entry<String, List<DictionaryEntry>>> romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator = romajiWithoutSpaceToDictionaryEntryListMap.entrySet().iterator();
			
			while (romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator.hasNext() == true) {
				
				Entry<String, List<DictionaryEntry>> romajiWithoutSpaceToDictionaryEntryListMapEntry = romajiWithoutSpaceToDictionaryEntryListMapEntrySetIterator.next();
								
				List<DictionaryEntry> romajiDictionaryEntryList = romajiWithoutSpaceToDictionaryEntryListMapEntry.getValue();
				
				//
				
				if (romajiDictionaryEntryList.size() > 1) {
					
					Set<String> uniqueRomaji = new HashSet<String>();
					
					for (DictionaryEntry dictionaryEntry : romajiDictionaryEntryList) {
						uniqueRomaji.add(dictionaryEntry.getRomaji());
					}
					
					if (uniqueRomaji.size() > 1) { // jest blad
												
						for (DictionaryEntry dictionaryEntry : romajiDictionaryEntryList) {							
							result.add((PolishJapaneseEntry)dictionaryEntry);
						}					
					}
				}
			}
		}

		CsvReaderWriter.generateCsv(new String[] { "input/word01-te-same-romaji-wynik.csv", "input/word02-te-same-romaji-wynik.csv" }, result, true, true, false);

	}
}
