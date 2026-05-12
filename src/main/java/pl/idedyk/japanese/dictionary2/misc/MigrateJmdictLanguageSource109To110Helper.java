package pl.idedyk.japanese.dictionary2.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class MigrateJmdictLanguageSource109To110Helper {

	public static void main(String[] args) throws Exception {
		// pomocnik
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		
		// wczytanie polskiego slownika
		List<Entry> allPolishDictionaryEntryList = dictionary2Helper.getAllPolishDictionaryEntryList();
		
		// przeniesienie language source z sense na poziom entry
		for (Entry entry : allPolishDictionaryEntryList) {
			List<LanguageSource> languageSourceList = new ArrayList<>();
			
			for (Sense sense : entry.getSenseList()) {
				List<LanguageSource> senseLanguageSourceList = sense.getLanguageSourceListFMFIXME();
				
				if (languageSourceList.size() == 0 && senseLanguageSourceList.size() > 0) { // mamy cos
					languageSourceList.addAll(senseLanguageSourceList);
				}
				
				senseLanguageSourceList.clear();				
			}
			
			entry.getLanguageSourceList().addAll(languageSourceList);
		}		
		
		// zapis zmienionego slownika		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		dictionary2Helper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", allPolishDictionaryEntryList, new EntryAdditionalData());
	}
}
