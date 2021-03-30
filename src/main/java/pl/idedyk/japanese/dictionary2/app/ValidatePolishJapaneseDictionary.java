package pl.idedyk.japanese.dictionary2.app;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

public class ValidatePolishJapaneseDictionary {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		// walidacja slow
		dictionaryHelper.validateAllPolishDictionaryEntryList();
	}
}
