package pl.idedyk.japanese.dictionary2.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.SaveEntryListAsHumanCsvConfig;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.MiscEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class JMDictFixer {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();

		SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new SaveEntryListAsHumanCsvConfig();
		
		// wczytanie slownika		
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		
		// naprawa
		for (Entry entry : allPolishDictionaryEntryList) {
			for (Sense sense : entry.getSenseList()) {				
				int archaismIndex = sense.getMiscList().indexOf(MiscEnum.ARCHAISM);
				
				if (archaismIndex != -1) {
					sense.getMiscList().set(archaismIndex, MiscEnum.ARCHAIC);
				}
				
				int colloquialism = sense.getMiscList().indexOf(MiscEnum.COLLOQUIALISM);
				
				if (colloquialism != -1) {
					sense.getMiscList().set(colloquialism, MiscEnum.COLLOQUIAL);
				}

				int obscureTermIndex = sense.getMiscList().indexOf(MiscEnum.OBSCURE_TERM);
				
				if (obscureTermIndex != -1) {
					sense.getMiscList().set(obscureTermIndex, MiscEnum.RARE_TERM);
				}
			}
		}
		
		
		// zapisanie czesciowo zmienionego polskiego slownika
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update.csv", allPolishDictionaryEntryList, entryAdditionalData);

	}

}
