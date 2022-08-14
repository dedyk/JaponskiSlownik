package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test7 {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		/*
		JMdict jmdict = dictionaryHelper.getJMdict();
		
		List<Entry> entryList = jmdict.getEntryList();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (Entry entry : entryList) {
			dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(entry, entryAdditionalData);
		}
		*/
		
		List<PolishJapaneseEntry> oldPolishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : oldPolishJapaneseEntriesList) {
			List<Entry> foundEntryList = dictionaryHelper.findEntryListInJmdict(polishJapaneseEntry);
			
			if (foundEntryList.size() > 1) {
				System.out.println("ID: " + polishJapaneseEntry.getId() + " - " + foundEntryList);
			}
		}
	}

}
