package pl.idedyk.japanese.dictionary.test;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;

public class Test9 {

	public static void main(String[] args) throws Exception {
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		dictionaryHelper.generateMissingPolishEntriesFromOldPolishJapaneseDictionary();
		
		JMdict newJmdict = new JMdict();
		
		newJmdict.getEntryList().addAll(dictionaryHelper.getAllPolishDictionaryEntryList());
		
		dictionaryHelper.sortJMdict(newJmdict);
		dictionaryHelper.saveJMdictAsXml(newJmdict, "/tmp/a/test.xml");
	}

}
