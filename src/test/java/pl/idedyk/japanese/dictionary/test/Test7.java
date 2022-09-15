package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

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
		
		/*
		List<PolishJapaneseEntry> oldPolishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : oldPolishJapaneseEntriesList) {
			List<Entry> foundEntryList = dictionaryHelper.findEntryListInJmdict(polishJapaneseEntry, false);
			
			if (foundEntryList.size() > 1) {
				System.out.println("ID: " + polishJapaneseEntry.getId() + " - " + foundEntryList);
			}
		}
		*/
		
		KanaHelper kanaHelper = new KanaHelper();
		
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();
		
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		Validator.validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries, dictionaryHelper, null, false);
	}

}
