package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class TheSameKanjiKanaList {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Map<String, List<PolishJapaneseEntry>> theSameKanjiKanaMap = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String key = polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana();
			
			List<PolishJapaneseEntry> listForKey = theSameKanjiKanaMap.get(key);
			
			if (listForKey == null) {
				listForKey = new ArrayList<PolishJapaneseEntry>();
				
				theSameKanjiKanaMap.put(key, listForKey);
			}
			
			listForKey.add(polishJapaneseEntry);			
		}
		
		Iterator<String> theSameKanjiKanaMapKeyIterator = theSameKanjiKanaMap.keySet().iterator();
		
		while (theSameKanjiKanaMapKeyIterator.hasNext() == true) {
			
			String currentKey = theSameKanjiKanaMapKeyIterator.next();
			
			List<PolishJapaneseEntry> listForKey = theSameKanjiKanaMap.get(currentKey);
			
			if (listForKey.size() > 1) {
				
				System.out.println("--- " + currentKey + " ---");
				
				for (PolishJapaneseEntry polishJapaneseEntry : listForKey) {
					
					int errorId = polishJapaneseEntry.getId();
					String errorKanji = polishJapaneseEntry.getKanji();
					String errorKana = polishJapaneseEntry.getKana();
					String errorDictionaryEntryType = polishJapaneseEntry.getDictionaryEntryTypeList().toString();
					String errorAttributeList = toAttributeListString(polishJapaneseEntry.getAttributeList());
					String errorTranslate = polishJapaneseEntry.getTranslates().toString();
					String errorInfo = polishJapaneseEntry.getInfo();
					String errorPrefix = polishJapaneseEntry.getPrefixKana() + "-" + polishJapaneseEntry.getPrefixRomaji();
					
					System.out.println("id: " + errorId);
					System.out.println("dictionaryEntryType: " + errorDictionaryEntryType);
					System.out.println("attributeList: " + errorAttributeList);
					System.out.println("prefix: " + errorPrefix);
					System.out.println("kanji: " + errorKanji);
					System.out.println("kana: " + errorKana);
					System.out.println("translate: " + errorTranslate);
					System.out.println("info: " + errorInfo);
					
					System.out.println("---\n");					
				}	
				
				System.out.println("\n=============\n");
			}
		}
	}
	
	private static String toAttributeListString(AttributeList attributeList) {
		
		StringBuffer sb = new StringBuffer();
		
		List<Attribute> attributeListList = attributeList.getAttributeList();
		
		for (Attribute attribute : attributeListList) {
			
			if (attribute.getAttributeType() == AttributeType.ALTERNATIVE) {
				continue;
			}
			
			if (sb.length() > 0) {
				sb.append(", ");
			}
			
			sb.append(attribute.getAttributeType());
			
			if (attribute.getAttributeValue() != null) {
				sb.append(": " + attribute.getAttributeValue().toString());
			}
		}
		
		return sb.toString();
	}
}
