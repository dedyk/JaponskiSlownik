package pl.idedyk.japanese.dictionary2.app;

import java.util.List;

import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class UpdateOldPolishJapaneseDictionary {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		// pobieramy wszystkie slowa, ktore sa w nowym slowniku
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		
		// walidacja slow
		dictionaryHelper.validateAllPolishDictionaryEntryList();
		
		// uaktualniamy tlumaczenia w starym slowniku
		for (Entry entry : allPolishDictionaryEntryList) {
			dictionaryHelper.updatePolishJapaneseEntryInOldDictionary(entry);
		}
				
		// zapisanie starego slownika
		CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv", "input/word03-wynik.csv" }, dictionaryHelper.getOldPolishJapaneseEntriesList(), true, true, false, true, null);
	}
}
