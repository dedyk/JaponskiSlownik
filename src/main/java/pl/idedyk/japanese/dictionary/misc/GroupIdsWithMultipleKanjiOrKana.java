package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;

public class GroupIdsWithMultipleKanjiOrKana {

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();
		
		JMdict jmdict = dictionaryHelper.getJMdict();
		
		List<Entry> entryList = jmdict.getEntryList();
		
		Map<String, List<Entry>> kanjiOrKanaGroupByMap = new TreeMap<>();
		
		for (Entry entry : entryList) {
			
			List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {
				addKanjiOrKanaGroupByMap(kanjiOrKanaGroupByMap, entry, kanjiInfo.getKanji());
			}
			
			//
			
			List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : readingInfoList) {
				addKanjiOrKanaGroupByMap(kanjiOrKanaGroupByMap, entry, readingInfo.getKana().getValue());
			}			
		}
		
		//
		
		Set<Integer> result = new TreeSet<Integer>();
		
		Set<String> kanjiOrKanaGroupByMapKeySet = kanjiOrKanaGroupByMap.keySet();
		
		for (String currentKanjiOdKana : kanjiOrKanaGroupByMapKeySet) {
			
			List<Entry> listForKanjiOrKana = kanjiOrKanaGroupByMap.get(currentKanjiOdKana);
			
			if (listForKanjiOrKana.size() > 1) {
				// System.out.println(currentKanjiOdKana + " - " + listForKanjiOrKana);
				
				for (Entry entry : listForKanjiOrKana) {
					result.add(entry.getEntryId());
				}
			}
		}
		
		//
		
		for (Integer entryId : result) {
			System.out.println(entryId);
		}
	}
	
	private static void addKanjiOrKanaGroupByMap(Map<String, List<Entry>> kanjiOrKanaGroupByMap, Entry entry, String kanjiKana) {
		
		List<Entry> listForKanjiOrKana = kanjiOrKanaGroupByMap.get(kanjiKana);
		
		if (listForKanjiOrKana == null) {
			
			listForKanjiOrKana = new ArrayList<>();
			
			kanjiOrKanaGroupByMap.put(kanjiKana, listForKanjiOrKana);			
		}
		
		if (listForKanjiOrKana.contains(entry) == false) {
			listForKanjiOrKana.add(entry);
		}
	}
}
