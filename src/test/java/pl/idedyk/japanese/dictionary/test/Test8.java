package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test8 {

	public static void main(String[] args) throws Exception {
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		List<PolishJapaneseEntry> oldPolishJapaneseEntriesList = dictionaryHelper.getOldPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : oldPolishJapaneseEntriesList) {
			
			Integer groupId = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
			
			if (groupId == null) {
				continue;
			}
			
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(groupId);
			
			if (entryFromPolishDictionary == null) {
				continue;
			}
			
            List<Dictionary2HelperCommon.KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entryFromPolishDictionary);

            // szukamy konkretnego znaczenia dla naszego slowa
            Dictionary2HelperCommon.KanjiKanaPair dictionaryEntry2KanjiKanaPair = Dictionary2HelperCommon.findKanjiKanaPair(kanjiKanaPairList, polishJapaneseEntry);
            
            if (dictionaryEntry2KanjiKanaPair == null) {
            	System.out.println("NPE: " + groupId);
            }			
		}
	}
}
