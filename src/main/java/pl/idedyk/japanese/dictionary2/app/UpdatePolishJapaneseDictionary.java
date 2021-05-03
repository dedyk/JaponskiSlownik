package pl.idedyk.japanese.dictionary2.app;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
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

		// lista zmienionych elementow
		List<Entry> entryManuallyChangeList = new ArrayList<>();
		
		// lista skasowanych elementow
		List<Entry> entryDeletedList = new ArrayList<>();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		// chodzimy po wszystkich elementach
		for (Entry currentPolishEntry : allPolishDictionaryEntryList) {
			
			// szukamy wpisu w angielskim slowniku
			Entry jmdictEntry = dictionaryHelper.getJMdictEntry(currentPolishEntry.getEntryId());
			
			if (jmdictEntry == null) { // ten element zostal skasowany
				
				System.out.println("Deleted entry: " + currentPolishEntry.getEntryId());
				
				dictionaryHelper.deleteEntryFromPolishDictionary(currentPolishEntry.getEntryId());
				
				entryDeletedList.add(currentPolishEntry);
				
				continue;
			}
			
			// wykonanie aktualizacji wpisu
			boolean needManuallyChange = dictionaryHelper.updatePolishJapaneseEntry(currentPolishEntry, jmdictEntry, entryAdditionalData);
			
			if (needManuallyChange == true) {
				entryManuallyChangeList.add(currentPolishEntry);
			}
		}
		
		// chodzenie po starym slowniku i szukanie, czy jakies slowo nie powinno juz byc w tym slowniku
		List<PolishJapaneseEntry> wordListFromOldDictionaryWhichCanBeReplaced = dictionaryHelper.detectEntriesWhichShouldBeDeletedInOldPolishJapaneseDictionary();
		
		// zapis
		
		allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		
		SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new SaveEntryListAsHumanCsvConfig();
		
		// dodajemy stare znaczenia
		saveEntryListAsHumanCsvConfig.addOldPolishTranslatesDuringDictionaryUpdate = true;
		saveEntryListAsHumanCsvConfig.addDeleteSenseDuringDictionaryUpdate = false;
		
		// zapisanie czesciowo zmienionego polskiego slownika
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update.csv", allPolishDictionaryEntryList, entryAdditionalData);
		
		saveEntryListAsHumanCsvConfig.shiftCells = true;
		saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
		
		saveEntryListAsHumanCsvConfig.addDeleteSenseDuringDictionaryUpdate = true;
		
		// zapisanie elementow, ktore nalezy manualnie zmodyfikowac
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update-manually.csv", entryManuallyChangeList, entryAdditionalData);
		
		// zapisanie elementow, ktore zostaly skasowane
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update-delete.csv", entryDeletedList, entryAdditionalData);
		
		// zapisanie pozycji ze starego slownika, ktore moga byc nadpisane, gdyz zostaly skasowane podczas aktualizacji
		CsvReaderWriter.generateCsv(new String[] { "input/word-can-be-replaced.csv" }, wordListFromOldDictionaryWhichCanBeReplaced, true, true, false, true, null);
	}
}
