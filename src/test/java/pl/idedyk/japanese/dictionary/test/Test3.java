package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.api.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class Test3 {

	public static void main(String[] args) throws Exception {
		
		System.out.println("generateKanjiEntries: readKradFile");
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile("../JapaneseDictionary_additional/kradfile");

		System.out.println("generateKanjiEntries: readKanjiDic2");
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2("../JapaneseDictionary_additional/kanjidic2.xml", kradFileMap);

		System.out.println("generateKanjiEntries: parseKanjiEntriesFromCsv");
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv("input/kanji.csv", readKanjiDic2, true);
		
		System.out.println("jmedictName");
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		System.out.println("JMdict_e");
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		Set<Character> alreadyKanjiSet = new TreeSet<Character>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			alreadyKanjiSet.add(new Character(currentKanjiEntry.getKanji().charAt(0)));
		}
		
		final Map<Character, Integer> charCounter = new TreeMap<Character, Integer>();
		
		processJMEDict(alreadyKanjiSet, charCounter, jmedict);
		processJMEDict(alreadyKanjiSet, charCounter, jmedictName);
				
		List<Character> charList = new ArrayList<Character>(charCounter.keySet());
		
		Collections.sort(charList, new Comparator<Character>() {

			@Override
			public int compare(Character o1, Character o2) {
				return -1 * charCounter.get(o1).compareTo(charCounter.get(o2));
			}
		});
		
		for (Character character : charList) {
			
			if (isKanji(character) == false) {
				continue;
			}
			
			if (alreadyKanjiSet.contains(character) == true) {
				continue;
			}
			
			System.out.println(character + "\t" + charCounter.get(character));
		}
		
		System.out.println("-----");
		Collection<KanjiDic2Entry> kanjiDiv2Values = readKanjiDic2.values();
		
		for (KanjiDic2Entry kanjiDic2Entry : kanjiDiv2Values) {
			
			Character kanjiChar = kanjiDic2Entry.getKanji().charAt(0);
			
			if (alreadyKanjiSet.contains(kanjiChar) == true) {
				continue;
			}
			
			if (charCounter.containsKey(kanjiChar) == false) {
				System.out.println(kanjiChar);
			}
		}
	}
	
	private static void processJMEDict(Set<Character> alreadyKanjiSet, final Map<Character, Integer> charCounter, TreeMap<String, List<JMEDictEntry>> jmedict) throws Exception {
		
		Collection<List<JMEDictEntry>> values = jmedict.values();
		
		for (List<JMEDictEntry> currentList : values) {
			
			for (JMEDictEntry jmeDictEntry : currentList) {
				List<String> kanjiList = jmeDictEntry.getKanji();
				
				for (String currentKanji : kanjiList) {
					
					for (int idx = 0; idx < currentKanji.length(); ++idx) {
						Character currentKanjiChar = new Character(currentKanji.charAt(idx));
						
						Integer currentKanjiCharCounter = charCounter.get(currentKanjiChar);
						
						if (currentKanjiCharCounter == null) {
							currentKanjiCharCounter = Integer.valueOf(0);
						}
						
						currentKanjiCharCounter = currentKanjiCharCounter.intValue() + 1;
						
						charCounter.put(currentKanjiChar, currentKanjiCharCounter);
					}
				}
			}			
		}		
	}
	
	private static boolean isKanji(Character c) {		
		return (c>=0x4e00 && c<0xa000) || c == '\u3005';		
	}
}
