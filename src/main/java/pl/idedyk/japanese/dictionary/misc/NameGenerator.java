package pl.idedyk.japanese.dictionary.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanaHelper.KanaWord;

public class NameGenerator {

	public static void main(String[] args) throws Exception {
		
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
		BufferedReader bufferedReader = null;
		
		List<NameItem> nameItemList = new ArrayList<NameItem>();
		
		try {
			bufferedReader = new BufferedReader(new FileReader("../JapaneseDictionary_additional/names/female_japanese_names.txt"));
						
			while(true) {				
				String line = bufferedReader.readLine();
				
				if (line == null) {
					break;
				}
				
				line = line.trim();
				
				if (line.equals("") == true) {
					continue;
				}
				
				int spaceIndex = line.indexOf(" ");
				
				if (spaceIndex == -1) {
					throw new Exception(line);
				}
				
				String name = line.substring(0, spaceIndex);
				
				name = name.toLowerCase();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				
				int bracketStartIndex = line.indexOf("(", spaceIndex);
				int bracketStopIndex = -1;
				
				if (bracketStartIndex != -1) {
					bracketStopIndex = line.indexOf(")", bracketStartIndex);
				}
				
				if (name.endsWith(":") == true) {
					name = name.substring(0, name.length() - 1);
					
					bracketStartIndex = -1;
					bracketStopIndex = -1;
				}
				
				if (bracketStartIndex == -1 || bracketStopIndex == -1) {
					
					NameItem nameItem = new NameItem();
					
					nameItem.name = name;
					nameItem.kanji = null;
					
					KanaWord kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, name);
					nameItem.kana = KanaHelper.createKanaString(kanaWord);
					
					nameItem.romaji = name;
					
					nameItemList.add(nameItem);
					
				} else {
					String bracketBody = line.substring(bracketStartIndex + 1, bracketStopIndex);
					
					String[] kanji = bracketBody.split(",");
					
					for (String currentKanji : kanji) {
						
						NameItem nameItem = new NameItem();
						
						nameItem.name = name;
						nameItem.kanji = currentKanji.trim();
						
						KanaWord kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, name);
						nameItem.kana = KanaHelper.createKanaString(kanaWord);
						
						nameItem.romaji = name;
						
						nameItemList.add(nameItem);
					}
				}
			}
			
			TreeMap<String, EDictEntry> jmedictName = EdictReader.readEdict("../JaponskiSlownik_dodatki/enamdict-utf8");
			
			for (NameItem nameItem : nameItemList) {
				
				EDictEntry eDictEntry = jmedictName.get(EdictReader.getMapKey(nameItem.kanji, nameItem.kana));
				
				boolean foundJMEdictName = eDictEntry != null; 
				
				System.out.format("%s\t%s\t%s\t%s\t%b\n", nameItem.name, nameItem.kanji, nameItem.kana, nameItem.romaji, foundJMEdictName);
			}
			
		} finally {
			
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
	
	private static class NameItem {
		
		public String name;
		
		public String kanji;
		
		public String kana;
		
		public String romaji;	
		
		
	}
}
