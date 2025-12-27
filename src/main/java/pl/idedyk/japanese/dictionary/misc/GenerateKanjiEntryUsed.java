package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;

@Deprecated
public class GenerateKanjiEntryUsed {

	public static void main(String[] args) throws Exception {
		
		// Stary kod. Prosze uzyc z kanji2.misc
		
		String sourceKanjiName = "input/kanji.csv";
		String sourceKanjiDic2FileName = "../JapaneseDictionary_additional/kanjidic2.xml";
		String sourceKradFileName = "../JapaneseDictionary_additional/kradfile";
		
		File kanjivgSingleXmlFile = new File("../JapaneseDictionary_additional/kanjivg/kanjivg.xml");
		File kanjivgPatchDirFile = new File("../JapaneseDictionary_additional/kanjivg/patch");
		
		String destinationFileName = "input/kanji-new.csv";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);		
		Map<String, KanjiDic2EntryForDictionary> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		Map<String, KanjivgEntry> kanjivgEntryMap = KanjivgReader.readKanjivgSingleXmlFile(kanjivgSingleXmlFile, kanjivgPatchDirFile);

		List<KanjiEntryForDictionary> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2, false);
		
		Map<String, KanjiEntryForDictionary> kanjiEntriesMap = new TreeMap<String, KanjiEntryForDictionary>();
		
		for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
			
			String kanji = kanjiEntry.getKanji();
			
			kanjiEntriesMap.put(kanji, kanjiEntry);
		}
		
		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {

			String kanji = currentKanjiEntry.getKanji();
						
			KanjivgEntry kanjivgEntry = kanjivgEntryMap.get(kanji);

			currentKanjiEntry.setKanjivgEntry(kanjivgEntry);
		}
		
		// top 2500
		for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
			
			String kanji = kanjiEntry.getKanji();
			
			KanjiDic2EntryForDictionary kanjiDic2Entry = readKanjiDic2.get(kanji);
			
			if (kanjiDic2Entry != null) {
				
				Integer freq = kanjiDic2Entry.getFreq();
				
				if (freq != null) {
					kanjiEntry.setUsed(true);
				}				
			}
		}
		
		processJMEDictEntries(kanjiEntriesMap, jmedict);
		processJMEDictEntries(kanjiEntriesMap, jmedictName);

		for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
						
			KanjiDic2EntryForDictionary kanjiDic2Entry = (KanjiDic2EntryForDictionary)kanjiEntry.getKanjiDic2Entry();
			
			if (kanjiDic2Entry == null) {
				kanjiEntry.setUsed(false);
				
				continue;
			}
			
			KanjivgEntry kanjivgEntry = kanjiEntry.getKanjivgEntry();
			
			if (kanjivgEntry == null) {
				kanjiEntry.setUsed(false);
				
				continue;
			}			
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));

		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries, false, null);

	}
	
	private static void processJMEDictEntries(Map<String, KanjiEntryForDictionary> kanjiEntriesMap, TreeMap<String, List<JMEDictEntry>> jmedict) {
		
		Collection<List<JMEDictEntry>> jmedictValues = jmedict.values();
		Iterator<List<JMEDictEntry>> jmedictCommonValuesIterator = jmedictValues.iterator();

		while (jmedictCommonValuesIterator.hasNext()) {
			
			List<JMEDictEntry> currentJMEDictEntryList = jmedictCommonValuesIterator.next();
			
			for (JMEDictEntry jmeDictEntry : currentJMEDictEntryList) {
				
				List<String> kanjiList = jmeDictEntry.getKanji();
				
				for (String kanji : kanjiList) {

					for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

						String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

						KanjiEntryForDictionary kanjiEntry = kanjiEntriesMap.get(currentKanjiChar);
						
						if (kanjiEntry != null) {
							kanjiEntry.setUsed(true);
						}						
					}
				}
			}
		}
	}
}
