package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class ValidateDictionary {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Wczytywanie słownika...");
				
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		System.out.println("Wczytywanie słownika edict...");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);		
		
		System.out.println("Walidacja edict...");
		
		Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);		
		
		System.out.println("Walidacja duplikatów...");
		
		Validator.detectDuplicatePolishJapaneseKanjiEntries(polishJapaneseEntries, "input/word-duplicate.csv");		
	}
}
