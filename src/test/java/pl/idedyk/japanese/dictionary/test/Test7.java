package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test7 {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		JMdict jmdict = dictionaryHelper.getJMdict();
		
		List<Entry> entryList = jmdict.getEntryList();
		
		for (Entry entry : entryList) {
			dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(entry);
		}
	}

}
