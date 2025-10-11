package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.EdictReader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test9 {

	public static void main(String[] args) throws Exception {
		String word2XmlFileTemplate = "/tmp/a/test.xml_%d";
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");
		
		List<PolishJapaneseEntry> polishJapaneseEntries = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		Helper.generateAdditionalInfoFromEdict(dictionaryHelper, jmedictCommon, polishJapaneseEntries);
		Helper.generateUniqueKeys(polishJapaneseEntries);
		
		dictionaryHelper.generateMissingPolishEntriesFromOldPolishJapaneseDictionary(polishJapaneseEntries);
		dictionaryHelper.addAdditionDataFromOldPolishJapaneseEntriesForGeneratingFinalDictionary(polishJapaneseEntries);
		
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		dictionaryHelper.sortJMdict(allPolishDictionaryEntryList);
		
		Map<Integer,  List<JMdict.Entry>> allPolishDictionaryEntryListPartedMap = new LinkedHashMap<>();
		
		for (Entry entry : allPolishDictionaryEntryList) {
			Integer firstEntryIdDigit = entry.getEntryId() / 100000;
			
			allPolishDictionaryEntryListPartedMap.computeIfAbsent(firstEntryIdDigit, f -> new ArrayList<Entry>()).add(entry);			
		}
		
		for (Integer firstEntryIdDigit : allPolishDictionaryEntryListPartedMap.keySet()) {
			String fileName = String.format(word2XmlFileTemplate, firstEntryIdDigit);
			
			JMdict newJmdict = new JMdict();
			
			newJmdict.getEntryList().addAll(allPolishDictionaryEntryListPartedMap.get(firstEntryIdDigit));
			
			dictionaryHelper.saveJMdictAsXml(newJmdict, fileName);
		}
	}
}
