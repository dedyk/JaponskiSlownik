package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class ShowTheBiggerMissingJMdictEntries {

	private static final int MIN_SENSE_SIZE = 3;
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		JMdict jmdict = dictionaryHelper.getJMdict();
		List<JMdict.Entry> entryList = jmdict.getEntryList();

		List<JMdict.Entry> entryListBig = new ArrayList<>();
		
		for (JMdict.Entry entry : entryList) {
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
			
			if (entryFromPolishDictionary == null && entry.getSenseList().size() >= MIN_SENSE_SIZE) {
				entryListBig.add(entry);
			}
		}
		
		//
		
		Collections.sort(entryListBig, new Comparator<JMdict.Entry>() {

			@Override
			public int compare(JMdict.Entry o1, JMdict.Entry o2) {
				if (o1.getSenseList().size() > o2.getSenseList().size()) {
					return -1;
					
				} else if (o1.getSenseList().size() < o2.getSenseList().size()) {
					return 1;
					
				} else {
					/*
					AtomicInteger o1SumSenseList = new AtomicInteger();
					AtomicInteger o2SumSenseList = new AtomicInteger();
					
					o1.getSenseList().stream().forEach(c -> o1SumSenseList.addAndGet(c.getGlossList().size()) );
					o2.getSenseList().stream().forEach(c -> o2SumSenseList.addAndGet(c.getGlossList().size()) );
					
					if (o1SumSenseList.get() > o2SumSenseList.get()) {
						return -1;
						
					} else if (o1SumSenseList.get() < o2SumSenseList.get()) {
						return 1;
						
					} else {
						return o1.getEntryId().compareTo(o2.getEntryId());
					}
					*/
					
					return o1.getEntryId().compareTo(o2.getEntryId());
				}
			}
		});
		
		for (Entry entry : entryListBig) {
			System.out.println(entry.getEntryId());
		}
	}
}
