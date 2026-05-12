package pl.idedyk.japanese.dictionary.test;

import java.io.File;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;

public class TestJMdictConverter {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		File csvFile = new File("/tmp/a/entry-list-test.csv");
		
		JMdict jmdict = dictionaryHelper.getJMdict();
		
		//
		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		saveEntryListAsHumanCsvConfig.addOldPolishTranslates = false;
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, csvFile.getAbsolutePath(), jmdict.getEntryList(), entryAdditionalData);
		dictionaryHelper.saveJMdictAsXml(jmdict, "/tmp/a/orginal-entry-list.xml");
		
		//
		
		JMdict newJMdict = dictionaryHelper.readEntryListFromHumanCsv(csvFile.getAbsolutePath());
		
		//
		
		dictionaryHelper.saveJMdictAsXml(newJMdict, "/tmp/a/new-entry-list-test.xml");
	}
}
