package pl.idedyk.japanese.dictionary.misc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.dto.AdditionalKanjiEntry;
import pl.idedyk.japanese.dictionary.tools.AdditionalKanjiReaderWriter;

public class AdditionalKanjiMisc {

	public static void main(String[] args) throws Exception{
		
		//String kradfile = "../JapaneseDictionary_additional/kradfile";
		//String kanjidic2 = "../JapaneseDictionary_additional/kanjidic2.xml";
		
		String additionalKanjiFile = "input/additional_kanji.csv";

		//Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(kradfile);		
		//final Map<String, KanjiDic2Entry> kanjiDic2Map = KanjiDic2Reader.readKanjiDic2(kanjidic2, kradFileMap);
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = AdditionalKanjiReaderWriter.readAdditionalKanjiEntry(additionalKanjiFile);
		
		/*
		 * Liczba tlumaczen
		 * 
		Collections.sort(additionalKanjiEntryList, new Comparator<AdditionalKanjiEntry>() {

			@Override
			public int compare(AdditionalKanjiEntry o1, AdditionalKanjiEntry o2) {
				
				if (o1.isUseKanji() == false) {
					return -1;
				}

				if (o2.isUseKanji() == false) {
					return 1;
				}
				
				KanjiDic2Entry o1KanjiDic2Entry = kanjiDic2Map.get(o1.getKanji());
				
				if (o1KanjiDic2Entry == null) {
					System.err.println("o1KanjiDic2Entry: " + o1.getKanji());
				}
				
				KanjiDic2Entry o2KanjiDic2Entry = kanjiDic2Map.get(o2.getKanji());

				if (o2KanjiDic2Entry == null) {
					System.err.println("o2KanjiDic2Entry: " + o2.getKanji());
				}
				
				int result = new Integer(o2KanjiDic2Entry.getEngMeaning().size()).compareTo(o1KanjiDic2Entry.getEngMeaning().size());
				
				if (result != 0) {
					return result;
				}
				
				result = new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
				
				return result;
			}
		});
		
		// wyswietlenie
		
		for (int idx = 0; idx < additionalKanjiEntryList.size(); ++idx) {
			
			AdditionalKanjiEntry additionalKanjiEntry = additionalKanjiEntryList.get(idx);
			
			if (additionalKanjiEntry.getDone().equals("1") == true) {
				continue;
			}
			
			System.out.println(additionalKanjiEntry.getId());
		}

		*/
		
		/*
		 * Te same znaczenia
		 */
		
		Set<String> uniqueKanjiGroups = new HashSet<String>();
		
		for (AdditionalKanjiEntry additionalKanjiEntry : additionalKanjiEntryList) {
			
			String translate = additionalKanjiEntry.getTranslate();
			
			if (translate.startsWith("---") == false) {
				continue;
			}
			
			List<String> kanjiTheSameTranslateList = new ArrayList<String>();
			
			kanjiTheSameTranslateList.add(additionalKanjiEntry.getKanji());
			
			String[] translateSplited = translate.split("\n");
			
			for (int translateSplitedIdx = 1; translateSplitedIdx < translateSplited.length; translateSplitedIdx++) {
				kanjiTheSameTranslateList.add(translateSplited[translateSplitedIdx]);
			}
			
			Collections.sort(kanjiTheSameTranslateList);
			
			if (uniqueKanjiGroups.contains(kanjiTheSameTranslateList.toString()) == false) {				
				uniqueKanjiGroups.add(kanjiTheSameTranslateList.toString());				
			}			
		}
		
		String[] uniqueKanjiGroupsArray = uniqueKanjiGroups.toArray(new String[] { });
		
		Arrays.sort(uniqueKanjiGroupsArray, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				Integer o1length = o1.length();
				Integer o2length = o2.length();
				
				int result = o2length.compareTo(o1length);
				
				if (result != 0) {
					return result;
				}
				
				return o2.compareTo(o1);				
			}
		});
		
		for (String currentUniqueKanjiGroup : uniqueKanjiGroupsArray) {
			System.out.println(currentUniqueKanjiGroup);
		}
	}	
}
