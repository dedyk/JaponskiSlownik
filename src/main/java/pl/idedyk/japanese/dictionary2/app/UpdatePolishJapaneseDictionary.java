package pl.idedyk.japanese.dictionary2.app;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.SaveEntryListAsHumanCsvConfig;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class UpdatePolishJapaneseDictionary {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		// wczytanie polskiego slownika
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();

		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		// chodzimy po wszystkich elementach
		for (Entry currentPolishEntry : allPolishDictionaryEntryList) {
			
			// szukamy wpisu w angielskim slowniku
			Entry jmdictEntry = dictionaryHelper.getJMdictEntry(currentPolishEntry.getEntryId());
			
			if (jmdictEntry == null) { // ten element zostal skasowany
				
				System.out.println("Please delete entry: " + currentPolishEntry.getEntryId());
				
				continue;
			}
			
			// wykonanie aktualizacji wpisu
			dictionaryHelper.updatePolishJapaneseEntry(currentPolishEntry, jmdictEntry, entryAdditionalData);
			
			int fixme = 1;
			// !!!!!!!!!!!!		
			
			int fixme2 = 1;
			// generowanie listy zmienionych elementow
		}
		
		SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new SaveEntryListAsHumanCsvConfig();
		
		// dodajemy stare znaczenia
		saveEntryListAsHumanCsvConfig.addOldPolishTranslatesDuringDictionaryUpdate = true;
		
		int fixme3 = 1;
		// zapisanie polskiego slownika
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update-test.csv", allPolishDictionaryEntryList, entryAdditionalData);
	}
}
