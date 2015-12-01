package pl.idedyk.japanese.dictionary.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;

public class Utils {
	
	public static Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList(List<PolishJapaneseEntry> polishJapaneseEntries) {
				
		Map<String, List<PolishJapaneseEntry>> result = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}
			
			String key = kanji + "." + kana;
			
			List<PolishJapaneseEntry> keyPolishJapaneseEntryList = result.get(key);
			
			if (keyPolishJapaneseEntryList == null) {				
				keyPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
				
				result.put(key, keyPolishJapaneseEntryList);
			}
			
			keyPolishJapaneseEntryList.add(polishJapaneseEntry);			
		}
		
		return result;
	}
	
	public static List<PolishJapaneseEntry> findPolishJapaneseEntry(
			Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryMap, String findKanji, String findKana) {
		
		if (findKanji == null || findKanji.equals("") == true || findKanji.equals("-") == true) {
			findKanji = "$$$NULL$$$";
		}

		String foundKey = findKanji + "." + findKana;
		
		List<PolishJapaneseEntry> polishJapaneseEntries = cachePolishJapaneseEntryMap.get(foundKey);
		
		if (polishJapaneseEntries == null) {
			return null;
		}
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}		
			
			result.add(polishJapaneseEntry);
		}
		
		return result;
	}	
	
	public static PolishJapaneseEntry findPolishJapaneseEntryWithEdictDuplicate(PolishJapaneseEntry parentPolishJapaneseEntry,
			Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryMap, String findKanji, String findKana) {
		
		if (findKanji == null || findKanji.equals("") == true || findKanji.equals("-") == true) {
			findKanji = "$$$NULL$$$";
		}

		String foundKey = findKanji + "." + findKana;
		
		List<PolishJapaneseEntry> polishJapaneseEntries = cachePolishJapaneseEntryMap.get(foundKey);
		
		if (polishJapaneseEntries == null) {
			return null;
		}
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}
							
			if (parentPolishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.EDICT_DUPLICATE, polishJapaneseEntry.getId()) == false) {
				return polishJapaneseEntry;
			}			
		}
		
		return null;
	}
	
	public static CommonWord convertGroupEntryToCommonWord(int id, GroupEntry groupEntry) {
				
		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
		
		List<String> translateStringList = new ArrayList<String>();
		
		for (GroupEntryTranslate groupEntryTranslate : translateList) {
			translateStringList.add(groupEntryTranslate.getTranslate());
		}
		
		CommonWord commonWord = new CommonWord(id, false, groupEntry.getKanji(), groupEntry.getKana(), groupEntry.getWordTypeList().toString(), translateStringList.toString());
		
		return commonWord;
	}
	
	public static CommonWord convertEDictEntryToCommonWord(int id, EDictEntry edictEntry) {
		
		CommonWord commonWord = new CommonWord(id, false, edictEntry.getKanji(), edictEntry.getKana(), edictEntry.getPos().toString(), edictEntry.getRawLine());
		
		return commonWord;		
	}
}
