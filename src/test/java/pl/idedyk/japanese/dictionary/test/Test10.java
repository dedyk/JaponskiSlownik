package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test10 {

	public static void main(String[] args) throws Exception {
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		List<Entry> entryList = dictionaryHelper.getJMdict().getEntryList();
		
		for (Entry entry : entryList) {
			
            List<Dictionary2HelperCommon.KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
            
            if (kanjiKanaPairList.size() == 0) {
            	System.out.println("Brak par dla: " + entry.getEntryId());
            }
		}
	}
}
