package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;

public class Test9 {

	public static void main(String[] args) throws Exception {
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");
		
		List<PolishJapaneseEntry> polishJapaneseEntries = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		Helper.generateAdditionalInfoFromEdict(dictionaryHelper, jmedictCommon, polishJapaneseEntries);
		Helper.generateUniqueKeys(polishJapaneseEntries);
		
		dictionaryHelper.generateMissingPolishEntriesFromOldPolishJapaneseDictionary(polishJapaneseEntries);
		dictionaryHelper.addAdditionDataFromOldPolishJapaneseEntriesForGeneratingFinalDictionary(polishJapaneseEntries);
		
		JMdict newJmdict = new JMdict();
		
		newJmdict.getEntryList().addAll(dictionaryHelper.getAllPolishDictionaryEntryList());
		
		dictionaryHelper.sortJMdict(newJmdict);
		dictionaryHelper.saveJMdictAsXml(newJmdict, "/tmp/a/test.xml");
	}

}
