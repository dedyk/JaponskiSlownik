package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test9 {

	public static void main(String[] args) throws Exception {
		String word2XmlFileTemplate = "/tmp/a/test.xml_%d";
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		dictionaryHelper.generateMissingPolishEntriesFromOldPolishJapaneseDictionary();
		
		List<Entry> allPolishDictionaryEntryList = dictionaryHelper.getAllPolishDictionaryEntryList();
		dictionaryHelper.sortJMdict(allPolishDictionaryEntryList);
		
		// podzielenie na pod listy
		List<List<Entry>> allPolishDictionaryEntryListPartitionsList = ListUtils.partition(allPolishDictionaryEntryList, 30000);
		
		for (int partNo = 0; partNo < allPolishDictionaryEntryListPartitionsList.size(); ++partNo) {
			String word2XmlFileName = String.format(word2XmlFileTemplate, partNo);
			
			JMdict newJmdict = new JMdict();
			
			newJmdict.getEntryList().addAll(allPolishDictionaryEntryListPartitionsList.get(partNo));
			
			dictionaryHelper.saveJMdictAsXml(newJmdict, word2XmlFileName);						
		}
	}
}
