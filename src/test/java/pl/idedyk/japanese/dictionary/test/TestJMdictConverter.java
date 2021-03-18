package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.util.List;

import pl.idedyk.japanese.dictionary2.common.DictionaryHelper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class TestJMdictConverter {

	public static void main(String[] args) throws Exception {
		
		DictionaryHelper dictionaryHelper = DictionaryHelper.init();

		File csvFile = new File("/tmp/a/entry-list-test.csv");
		
		JMdict jmdict = dictionaryHelper.getJMdict();
		
		//
		
		dictionaryHelper.saveEntryListAsHumanCsv(csvFile.getAbsolutePath(), jmdict.getEntryList());
		dictionaryHelper.saveJMdictAsXml(jmdict, "/tmp/a/org-entry-list-test.xml");
		
		//
		
		List<Entry> readJMdictEntryList = dictionaryHelper.readEntryListFromHumanCsv(csvFile.getAbsolutePath());
		
		JMdict newJMdict = new JMdict();
		
		newJMdict.getEntryList().addAll(readJMdictEntryList);
		
		//
		
		dictionaryHelper.saveJMdictAsXml(newJMdict, "/tmp/a/entry-list-test.xml");
	}
}
