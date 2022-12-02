package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class ShowTheBiggerMissingJMdictEntries {

	private static final int MIN_SENSE_SIZE = 7;
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		JMdict jmdict = dictionaryHelper.getJMdict();
		List<JMdict.Entry> entryList = jmdict.getEntryList();

		for (JMdict.Entry entry : entryList) {
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
			
			if (entryFromPolishDictionary == null && entry.getSenseList().size() >= MIN_SENSE_SIZE) {
				System.out.println(entry.getEntryId());
			}
		}
	}
}
