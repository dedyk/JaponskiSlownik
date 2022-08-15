package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;


public class DetectMultiGroup {
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		List<Integer> idList = new ArrayList<>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			List<Entry> foundEntryList = dictionaryHelper.findEntryListInJmdict(polishJapaneseEntry, false);
			
			if (foundEntryList.size() > 1) {				
				idList.add(polishJapaneseEntry.getId());
				
				System.out.println(polishJapaneseEntry.getId());
			}			
		}
		
		System.out.print("./word-generator.sh find-words-with-jmedict-change -s 888888 --ignore-dictionary-filled-raw-data -wid ");
		
		for (Integer id : idList) {
			System.out.print(id + ",");			
		}
		
		System.out.println();
	}
}
