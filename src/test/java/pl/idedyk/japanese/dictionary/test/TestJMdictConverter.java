package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class TestJMdictConverter {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();

		File csvFile = new File("/tmp/a/entry-list-test.csv");
		
		JMdict jmdict = dictionaryHelper.getJMdict();
		
		//
		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		saveEntryListAsHumanCsvConfig.addOldPolishTranslates = false;
		
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, csvFile.getAbsolutePath(), jmdict.getEntryList());
		dictionaryHelper.saveJMdictAsXml(jmdict, "/tmp/a/orginal-entry-list.xml");
		
		//
		
		List<Entry> readJMdictEntryList = dictionaryHelper.readEntryListFromHumanCsv(csvFile.getAbsolutePath());
		
		JMdict newJMdict = new JMdict();
		
		newJMdict.getEntryList().addAll(readJMdictEntryList);
		
		//
		
		dictionaryHelper.saveJMdictAsXml(newJMdict, "/tmp/a/new-entry-list-test.xml");
	}
}
