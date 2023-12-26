package pl.idedyk.japanese.dictionary.misc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class GenerateCharHexCode {

	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");

		//
				
		KanaHelper kanaHelper = new KanaHelper();
		
		// pobranie wszystkich znakow kana
		List<KanaEntry> hiraganaKanaEntries = kanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaKanaEntries = kanaHelper.getAllKatakanaKanaEntries();
		List<KanaEntry> additionalKanaEntries = kanaHelper.getAllAdditionalKanaEntries();
		
		List<KanaEntry> kanaEntryList = new ArrayList<KanaEntry>();

		kanaEntryList.addAll(hiraganaKanaEntries);
		kanaEntryList.addAll(katakanaKanaEntries);
		kanaEntryList.addAll(additionalKanaEntries);
		
		TreeSet<String> uniqueCharacters = new TreeSet<String>();
		
		for (KanaEntry currentKanaEntry : kanaEntryList) {
			
			String kanaJapanese = currentKanaEntry.getKanaJapanese();
			
			addUniqueChars(uniqueCharacters, kanaJapanese);
		}
		
		//
		
		Map<String, List<String>> kradFileMap = wordGeneratorHelper.getKradFileMap();
		
		Collection<List<String>> kradFileMapValues = kradFileMap.values();
		
		for (List<String> currentkradFileMapValuesList : kradFileMapValues) {
			
			for (String currentCurrentkradFileMapValues : currentkradFileMapValuesList) {
				
				currentCurrentkradFileMapValues = currentCurrentkradFileMapValues.replaceAll("_", "");
				
				uniqueCharacters.add(currentCurrentkradFileMapValues);
				
				addUniqueChars(uniqueCharacters, currentCurrentkradFileMapValues);
			}
		}
		
		//
				
		Map<String, String> radicalToCorrectRadical = KanjiDic2Reader.getRadicalToCorrectRadical();
		
		Iterator<Entry<String, String>> radicalToCorrectRadicalIterator = radicalToCorrectRadical.entrySet().iterator();
		
		while (radicalToCorrectRadicalIterator.hasNext() == true) {
			
			Entry<String, String> radicalToCorrectRadicalEntry = radicalToCorrectRadicalIterator.next();
			
			String radicalToCorrectRadicalEntryKey = radicalToCorrectRadicalEntry.getKey().replaceAll("_", "");
			String radicalToCorrectRadicalEntryValue = radicalToCorrectRadicalEntry.getValue().replaceAll("_", "");
			
			uniqueCharacters.add(radicalToCorrectRadicalEntryKey);
			uniqueCharacters.add(radicalToCorrectRadicalEntryValue);
			
			addUniqueChars(uniqueCharacters, radicalToCorrectRadicalEntryKey);
			addUniqueChars(uniqueCharacters, radicalToCorrectRadicalEntryValue);
		}
		
		boolean full = false;
		
		if (full == true) {
			
			List<KanjiEntryForDictionary> kanjiEntries = wordGeneratorHelper.getKanjiEntries();
			
			for (KanjiEntryForDictionary kanjiEntryForDictionary : kanjiEntries) {
				
				String kanji = kanjiEntryForDictionary.getKanji();
				
				addUniqueChars(uniqueCharacters, kanji);				
			}
			
			//
			/*
			List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
			
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
				
				if (polishJapaneseEntry.isKanjiExists() == true) {
					
					String kanji = polishJapaneseEntry.getKanji();
					
					addUniqueChars(uniqueCharacters, kanji);
				}
				
				addUniqueChars(uniqueCharacters, polishJapaneseEntry.getKana());
				addUniqueChars(uniqueCharacters, polishJapaneseEntry.getRomaji());
				
				for (String translate : polishJapaneseEntry.getTranslates()) {
					addUniqueChars(uniqueCharacters, translate);
				}
				
				addUniqueChars(uniqueCharacters, polishJapaneseEntry.getInfo());
			}
			
			//
			
			JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
			
			List<Group> groupList = jmeNewDictionary.getGroupList();
			
			for (Group group : groupList) {
				
				List<GroupEntry> groupEntryList = group.getGroupEntryList();
				
				List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
							
				for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
					
					for (int groupEntryListTheSameTranslateIdx = 0; groupEntryListTheSameTranslateIdx < groupEntryListTheSameTranslate.size(); ++groupEntryListTheSameTranslateIdx) {
						
						if (groupEntryListTheSameTranslateIdx == 1) {
							break;
						}
						
						GroupEntry groupEntry = groupEntryListTheSameTranslate.get(groupEntryListTheSameTranslateIdx);
						
						String groupEntryKanji = groupEntry.getKanji();
						String groupEntryKana = groupEntry.getKana();
						String groupEntryRomaji = groupEntry.getRomaji();						
						List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
						
						addUniqueChars(uniqueCharacters, groupEntryKanji);
						addUniqueChars(uniqueCharacters, groupEntryKana);
						addUniqueChars(uniqueCharacters, groupEntryRomaji);
						
						for (GroupEntryTranslate groupEntryTranslate : translateList) {
							
							addUniqueChars(uniqueCharacters, groupEntryTranslate.getTranslate());
							
							List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
							
							for (String currentAdditionalInfo : additionalInfoList) {
								addUniqueChars(uniqueCharacters, currentAdditionalInfo);
							}
						}
					}
				}
			}
			*/			
		}
				
		//
		
		StringBuffer result = new StringBuffer();
		
		for (String character : uniqueCharacters) {
			
			String characterAsUTF32 = getUTF32ByteHex(character);
			
			System.out.println(character + " - " + characterAsUTF32);
			
			result.append("U+" + characterAsUTF32).append(" \\\n");
		}
		
		System.out.println("-----");
		
		System.out.println(result.toString());
	}
	
	private static void addUniqueChars(TreeSet<String> uniqueCharacters, String text) {
		
		if (text == null) {
			return;
		}
		
		for (int i = 0; i < text.length(); ++i) {
			uniqueCharacters.add(String.valueOf(text.charAt(i)));
		}		
	}

	private static String getUTF32ByteHex(String text) throws UnsupportedEncodingException {
		
		byte[] textBytes = text.getBytes("UTF-32");
		
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < textBytes.length; ++idx) {			
			sb.append(String.format("%1$02X", (textBytes[idx] & 0xFF)));			
		}
		
		Integer sbAsInteger = Integer.parseInt(sb.toString(), 16);
		
		return Integer.toHexString(sbAsInteger).toUpperCase();
	}
}
