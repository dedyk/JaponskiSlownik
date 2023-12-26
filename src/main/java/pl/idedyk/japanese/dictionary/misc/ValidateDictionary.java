package pl.idedyk.japanese.dictionary.misc;

import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

public class ValidateDictionary {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Wczytywanie słownika...");
				
		// utworzenie helper'a
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.init(wordGeneratorHelper);
		
		System.out.println("Wczytywanie słownika edict...");
				
		System.out.println("Walidacja edict...");
		
		Validator.validateEdictGroup(dictionary2Helper, wordGeneratorHelper.getPolishJapaneseEntriesList());		
		
		System.out.println("Walidacja duplikatów...");
		
		Validator.detectDuplicatePolishJapaneseKanjiEntries(wordGeneratorHelper.getPolishJapaneseEntriesList(), "input/word-duplicate.csv");		
	}
}
