package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class ShowTheBiggerMissingJMdictEntries {

	private static final int MIN_SENSE_SIZE = 2;
	private static final int[] RATIO = { 5, 5}; // proporcje duze do malych
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		JMdict jmdict = dictionaryHelper.getJMdict();
		List<JMdict.Entry> entryList = jmdict.getEntryList();

		// lista wzystkich odnalezionych wpisow
		List<JMdict.Entry> entryListBig = new ArrayList<>();
		
		for (JMdict.Entry entry : entryList) {
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
			
			if (entryFromPolishDictionary == null && entry.getSenseList().size() >= MIN_SENSE_SIZE) {
				entryListBig.add(entry);
			}
		}
		
		// posortowanie w kolejnosci po entryId
		LinkedList<JMdict.Entry> entryListBigOrderByEntryId = new LinkedList<>(entryListBig);
		
		Collections.sort(entryListBigOrderByEntryId, new Comparator<JMdict.Entry>() {

			@Override
			public int compare(JMdict.Entry o1, JMdict.Entry o2) {
				if (o1.getSenseList().size() > o2.getSenseList().size()) {
					return -1;
					
				} else if (o1.getSenseList().size() < o2.getSenseList().size()) {
					return 1;
					
				} else {					
					return o1.getEntryId().compareTo(o2.getEntryId());
				}
			}
		});
		
		// posortowanie w kolejnosci od najwiekszych slowek
		LinkedList<JMdict.Entry> entryListBigOrderByTheBiggest = new LinkedList<>(entryListBig);
				
		Collections.sort(entryListBigOrderByTheBiggest, new Comparator<JMdict.Entry>() {

			@Override
			public int compare(JMdict.Entry o1, JMdict.Entry o2) {
				if (o1.getSenseList().size() > o2.getSenseList().size()) {
					return -1;
					
				} else if (o1.getSenseList().size() < o2.getSenseList().size()) {
					return 1;
					
				} else {
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
				}
			}
		});
		
		// wygenerowanie kolejek
		
		// wygenerowanie listy wedlug podanych proporcji
		List<JMdict.Entry> entryListBigOrderByRatio = new ArrayList<>();
		
		BEFORE_WHILE:
		while (true) {
			// pobieramy duze wpisy
			for (int bigIdx = 0; bigIdx < RATIO[0]; ++bigIdx) {
				// pobieramy duzy wpis
				JMdict.Entry entry = entryListBigOrderByTheBiggest.poll();
				
				if (entry == null) {
					break BEFORE_WHILE;
				}
				
				// dodajemy do listy wynikowej i usuwamy z malych slowek
				entryListBigOrderByRatio.add(entry);
				entryListBigOrderByEntryId.remove(entry);
			}
			
			// pobieramy male wpisy
			for (int bigIdx = 0; bigIdx < RATIO[1]; ++bigIdx) {
				// pobieramy duzy wpis
				JMdict.Entry entry = entryListBigOrderByEntryId.poll();
				
				if (entry == null) {
					break BEFORE_WHILE;
				}
				
				// dodajemy do listy wynikowej i usuwamy z malych slowek
				entryListBigOrderByRatio.add(entry);
				entryListBigOrderByTheBiggest.remove(entry);
			}
		}

		//
				
		for (Entry entry : entryListBigOrderByRatio) {
			System.out.println(entry.getEntryId());
		}
	}
}
