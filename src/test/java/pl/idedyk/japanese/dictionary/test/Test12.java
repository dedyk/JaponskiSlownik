package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.RelativePriorityEnum;

public class Test12 {
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		
		JMdict jmdict = dictionary2Helper.getJMdict();
		
		ENTRY_FOR:
		for (JMdict.Entry entry : jmdict.getEntryList()) {
			
			List<KanjiKanaPair> kanjiKanaPairListStatic = Dictionary2Helper.getKanjiKanaPairListStatic(entry, true);
			
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListStatic) {
				Set<RelativePriorityEnum> allRelativePriorityEnum = new TreeSet<>();
				
				KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
				ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
				
				if (kanjiInfo != null) {
					allRelativePriorityEnum.addAll(kanjiInfo.getRelativePriorityList());
				}
				
				if (readingInfo != null) {
					allRelativePriorityEnum.addAll(readingInfo.getRelativePriorityList());
				}
				
				//
				
				if (	// allRelativePriorityEnum.contains(RelativePriorityEnum.ICHI_2) == false &&
						//allRelativePriorityEnum.contains(RelativePriorityEnum.ICHI_2) == true) {
						allRelativePriorityEnum.size() != 0) {
					System.out.println("" + entry.getEntryId());
					continue ENTRY_FOR;
				}				
			}			
		}
		
	}
}
