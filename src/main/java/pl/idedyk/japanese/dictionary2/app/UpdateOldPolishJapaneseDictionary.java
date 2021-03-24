package pl.idedyk.japanese.dictionary2.app;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class UpdateOldPolishJapaneseDictionary {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		// pobieramy wszystkie slowa, ktore sa w nowym slowniku
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		
		JMdict jmDict = new JMdict();
		
		jmDict.getEntryList().addAll(allPolishDictionaryEntryList);
		
		dictionaryHelper.saveJMdictAsXml(jmDict, "/tmp/a/test.xml");
	}
}
