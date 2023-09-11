package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class Test7 {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		// lista wszystkich 5..., ktorych nie ma
		/*
		{
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
		}
		*/
		
		// lista wszystkich 5..., ktore maja to samo kanji i kana, co inne slowka
		/*
		{
			List<PolishJapaneseEntry> polishJapaneseEntryList = dictionaryHelper.getOldPolishJapaneseEntriesList();
			
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {
				
				// pobieramy kanji i kana
				String kanji = polishJapaneseEntry.getKanji();
				
				if (kanji != null && kanji.equals("-") == true) {
					kanji = null;
				}
				
				String kana = polishJapaneseEntry.getKana();
				
				if (kana != null && kana.equals("-") == true) {
					kana = null;
				}

				List<Entry> entryList = dictionaryHelper.findEntryListByKanjiAndKana(kanji, kana);
				
				if (entryList == null) {
					continue;
				}
				
				for (Entry entry : entryList) {
					if (entry.getEntryId() >= 5000000) {
						
						Entry polishEntry = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
						
						if (polishEntry != null) {
							continue;
						}
						
						System.out.println(entry.getEntryId());
					}
				}			
			}
		}
		*/
		
		// lista wszystkich 5..., ktore maja to xxx University
		{
			JMdict jmdict = dictionaryHelper.getJMdict();

			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				if (entry.getEntryId() >= 5000000) {
					
					Entry polishEntry = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
					
					if (polishEntry != null) {
						continue;
					}
					
					if (entry.getSenseList().size() == 1) { // jeden sens
						Sense sense = entry.getSenseList().get(0);
						
						if (sense.getGlossList().size() == 1) { // jedno znaczenie
							Gloss gloss = sense.getGlossList().get(0);
							
							String glossValue = gloss.getValue();
							
							String[] glossValueSplited = glossValue.split(" ");
							
							if (glossValueSplited.length == 2 && glossValueSplited[1].equals("University") == true) {
								System.out.println(entry.getEntryId());
							}							
						}						
					}
				}
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
