package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test7 {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		JMdict jmdict = dictionaryHelper.getJMdict();

		List<Entry> entryList = jmdict.getEntryList();
		
		for (Entry entry : entryList) {
			if (entry.getEntryId() >= 5000000) {
				
				Entry polishEntry = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
				
				if (polishEntry != null) {
					continue;
				}
				
				System.out.println(entry.getEntryId());
			}
		}

		
		/*
		
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (Entry entry : entryList) {
			dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(entry, entryAdditionalData);
		}
		*/
		
		/*
		List<PolishJapaneseEntry> oldPolishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : oldPolishJapaneseEntriesList) {
			List<Entry> foundEntryList = dictionaryHelper.findEntryListInJmdict(polishJapaneseEntry, false);
			
			if (foundEntryList.size() > 1) {
				System.out.println("ID: " + polishJapaneseEntry.getId() + " - " + foundEntryList);
			}
		}
		*/
		
		// Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
		
		/*
		KanaHelper kanaHelper = new KanaHelper();
		
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();
		
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		Validator.validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries, dictionaryHelper, dictionary2NameHelper, false);
		*/
		
		// AndroidDictionaryGenerator.generateNamePolishJapaneseEntries("output/n/names.csv");
		
	}

}
