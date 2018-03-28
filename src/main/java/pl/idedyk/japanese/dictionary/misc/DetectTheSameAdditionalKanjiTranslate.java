package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.AdditionalKanjiEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.AdditionalKanjiReaderWriter;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class DetectTheSameAdditionalKanjiTranslate {

	public static void main(String[] args) throws Exception {
		
		String kradfile = "../JapaneseDictionary_additional/kradfile";
		String kanjidic2 = "../JapaneseDictionary_additional/kanjidic2.xml";
		
		String additionalKanjiFile = "input/additional_kanji.csv";
		String additionalKanjiOuputFile = "input/additional_kanji_output.csv";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(kradfile);		
		Map<String, KanjiDic2EntryForDictionary> kanjiDic2Map = KanjiDic2Reader.readKanjiDic2(kanjidic2, kradFileMap);
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = AdditionalKanjiReaderWriter.readAdditionalKanjiEntry(additionalKanjiFile);
		
		Map<String, List<KanjiDic2EntryForDictionary>> theSameEngMeaning = detectTheSameEngMeaning(kradFileMap, kanjiDic2Map);

		Iterator<String> theSameEngMeaningIterator = theSameEngMeaning.keySet().iterator();
		
		while (theSameEngMeaningIterator.hasNext() == true) {
			
			String key = theSameEngMeaningIterator.next();
			
			if (key.trim().equals("[]") == true) {
				continue;
			}
			
			List<KanjiDic2EntryForDictionary> theSameEngMeaningKanjiDic2EntryList = theSameEngMeaning.get(key);

			if (theSameEngMeaningKanjiDic2EntryList.size() <= 1) {
				continue;
			}
						
			List<AdditionalKanjiEntry> foundAdditionalKanjiEntryList = new ArrayList<AdditionalKanjiEntry>();
			
			for (KanjiDic2EntryForDictionary currentKanjiDic2Entry : theSameEngMeaningKanjiDic2EntryList) {
				
				AdditionalKanjiEntry additionalKanjiEntry = AdditionalKanjiReaderWriter.findAdditionalKanjiEntry(additionalKanjiEntryList, currentKanjiDic2Entry.getKanji());
				
				if (additionalKanjiEntry != null) {
					foundAdditionalKanjiEntryList.add(additionalKanjiEntry);
				}				
			}
			
			if (foundAdditionalKanjiEntryList.size() <= 1) {
				continue;
			}
			
			String theSamePolishTranslate = null;
			String theSamePolishInfo = null;
			
			for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
				
				String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();
				String currentAdditionalKanjiEntryInfo = additionalKanjiEntry.getInfo();
				
				if (currentAdditionalKanjiEntryTranslate.equals("") == false && theSamePolishTranslate == null) {
					
					theSamePolishTranslate = currentAdditionalKanjiEntryTranslate;
					theSamePolishInfo = currentAdditionalKanjiEntryInfo;
					
				} else if (theSamePolishTranslate != null && currentAdditionalKanjiEntryTranslate.equals("") == false && 
						currentAdditionalKanjiEntryTranslate.startsWith("---") == false &&
						currentAdditionalKanjiEntryTranslate.equals(theSamePolishTranslate) == false) {
					
					for (AdditionalKanjiEntry additionalKanjiEntry2 : foundAdditionalKanjiEntryList) {
						System.err.println(additionalKanjiEntry2);
					}
					
					
					System.err.println("Error");
					
					throw new Exception();
				}				
			}
			
			if (theSamePolishTranslate != null) {			

				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					
					String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();
	
					if (currentAdditionalKanjiEntryTranslate.equals("") == true) {
						additionalKanjiEntry.setTranslate(theSamePolishTranslate);
						additionalKanjiEntry.setInfo(theSamePolishInfo);
						additionalKanjiEntry.setDone("0");
					}					
				}
				
			} else {
				
				StringBuffer allKanji = new StringBuffer();
				
				allKanji.append("---\n");
				
				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					allKanji.append(additionalKanjiEntry.getKanji() + "\n");					
				}
				
				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					
					String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();

					if (currentAdditionalKanjiEntryTranslate.equals("") == false) {
						throw new Exception();
					}
					
					additionalKanjiEntry.setTranslate(allKanji.toString().replaceAll(additionalKanjiEntry.getKanji() + "\n", ""));				
				}				
			}
		}
		
		AdditionalKanjiReaderWriter.writeAdditionalKanjiList(additionalKanjiEntryList, additionalKanjiOuputFile);
	}
	
	private static Map<String, List<KanjiDic2EntryForDictionary>> detectTheSameEngMeaning(Map<String, List<String>> kradFileMap, Map<String, KanjiDic2EntryForDictionary> readKanjiDic2) {
		
		Collection<KanjiDic2EntryForDictionary> readKanjiDic2Values = readKanjiDic2.values();
		
		Map<String, List<KanjiDic2EntryForDictionary>> theSameEngMeaning = new TreeMap<String, List<KanjiDic2EntryForDictionary>>();
		
		for (KanjiDic2EntryForDictionary kanjiDic2Entry : readKanjiDic2Values) {
			
			List<String> engMeaning = kanjiDic2Entry.getEngMeaning();
			
			Collections.sort(engMeaning);
			
			String key = engMeaning.toString();
			
			List<KanjiDic2EntryForDictionary> list = theSameEngMeaning.get(key);
			
			if (list == null) {
				list = new ArrayList<KanjiDic2EntryForDictionary>();
			}
			
			list.add(kanjiDic2Entry);
			
			theSameEngMeaning.put(key, list);			
		}	
		
		return theSameEngMeaning;
	}		
}
