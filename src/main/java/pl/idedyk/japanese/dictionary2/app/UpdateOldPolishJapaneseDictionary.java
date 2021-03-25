package pl.idedyk.japanese.dictionary2.app;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class UpdateOldPolishJapaneseDictionary {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		// pobieramy wszystkie slowa, ktore sa w nowym slowniku
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		
		// uaktualniamy tlumaczenia w starym slowniku
		for (Entry entry : allPolishDictionaryEntryList) {
			dictionaryHelper.updatePolishJapaneseEntryInOldDictionary(entry);
		}
		
		int fixme = 1;
		// walidacja, np. romaji, duplikat w ramach jednego sense
		// inne walidacje, sprawdz w starym jakie
	}
}
