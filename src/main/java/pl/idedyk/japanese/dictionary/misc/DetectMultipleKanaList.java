package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class DetectMultipleKanaList {

	public static void main(String[] args) throws Exception {
		
		// word 2 - dictionary
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		//
		
		Set<Integer> groupIdSetInPolishJapaneseEntriesListSet = new TreeSet<>();
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			Integer groupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
			
			if (groupIdFromJmedictRawDataList != null) {				
				groupIdSetInPolishJapaneseEntriesListSet.add(groupIdFromJmedictRawDataList);
			}
		}
		
		//
		
		List<Entry> jmdictEntryList = dictionaryHelper.getJMdict().getEntryList();
		
		Map<String, Map<String, List<Entry>>> kanjiSet = new TreeMap<>();
		
		// chodzimy po wszystkich entry
		for (Entry entry : jmdictEntryList) {
			
			// wyliczamy pary
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
			
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				
				String kanji = kanjiKanaPair.getKanji();
				
				if (kanji == null) {
					kanji = "-";
				}
				
				Map<String, List<Entry>> kanaMap = kanjiSet.get(kanji);
				
				if (kanaMap == null) {
					kanaMap = new TreeMap<>();
					
					kanjiSet.put(kanji, kanaMap);
				}
				
				//
				
				String kana = kanjiKanaPair.getKana();
				
				List<Entry> kanaGroupEntryList = kanaMap.get(kana);
				
				if (kanaGroupEntryList == null) {
					kanaGroupEntryList = new ArrayList<>();
					
					kanaMap.put(kana, kanaGroupEntryList);
				}
				
				//
				
				kanaGroupEntryList.add(entry);
			}
		}
		
		//
		
		Iterator<String> kanjiIterator = kanjiSet.keySet().iterator();
		
		while (kanjiIterator.hasNext() == true) {
			
			String kanji = kanjiIterator.next();
			
			//
			
			Map<String, List<Entry>> kanaMap = kanjiSet.get(kanji);
			
			//
			
			Iterator<String> kanaMapIterator = kanaMap.keySet().iterator();
			
			while (kanaMapIterator.hasNext() == true) {
				
				String kana = kanaMapIterator.next();
				
				List<Entry> groupEntryList = kanaMap.get(kana);
				
				if (groupEntryList.size() > 1) { // mamy kandydatow
					
					Set<String> uniqueKanjiAndKanaSetInGroupEntry = new TreeSet<>();
					
					// sprawdzamy, co siedzi w tych grupach - INFO: ponizszy kod jest troche nadmiarowy, ale niech zostanie
					// wystarczylo jedynie
					// for (Entry entry : groupEntryList) {							
					//	if (groupIdSetInPolishJapaneseEntriesListSet.contains(entry.getEntryId()) == false) { // czy nie mamy							
					//		System.out.println(entry.getEntryId() + " - " + kanji + " - " + kana);			
					//	}
					// }
					for (Entry entry : groupEntryList) {
						
						// wyliczamy pary
						List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
						
						for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
							
							String kanjiKanaPairKanji = kanjiKanaPair.getKanji();					
							String kanjiKanaPairKana = kanjiKanaPair.getKana();
							
							String key = kanjiKanaPairKanji + "." + kanjiKanaPairKana;
							
							uniqueKanjiAndKanaSetInGroupEntry.add(key);							
						}
					}
					
					if (uniqueKanjiAndKanaSetInGroupEntry.size() == 1) { // gdy cala zawartosc grupy jest taka sama w droch lub wiecej grupach
						
						for (Entry entry : groupEntryList) {			
							if (groupIdSetInPolishJapaneseEntriesListSet.contains(entry.getEntryId()) == false) { // czy nie mamy							
								System.out.println("(*) " + entry.getEntryId() + " - " + kanji + " - " + kana);			
							}
						}
						
					} else {
						
						for (Entry entry : groupEntryList) {							
							if (groupIdSetInPolishJapaneseEntriesListSet.contains(entry.getEntryId()) == false) { // czy nie mamy							
								System.out.println(entry.getEntryId() + " - " + kanji + " - " + kana);			
							}
						}
					}
				}
			}
		}
	}
}
