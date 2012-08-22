package pl.idedyk.japanese.dictionary.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> dictionary = checkAndSavePolishJapaneseEntries("input/word.csv", "output/word.csv");
		
		generateKanjiRadical("../JaponskiSlownik_dodatki/radkfile", "output/radical.csv");
		
		generateKanjiEntries(dictionary, "input/kanji.csv", "../JaponskiSlownik_dodatki/kanjidic2.xml", 
				"../JaponskiSlownik_dodatki/kradfile", "../JaponskiSlownik_dodatki/kanjivg",		
				"output/kanji.csv");
	}

	private static List<PolishJapaneseEntry> checkAndSavePolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
				
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		
		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName, null);
		
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries);
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			if (polishJapaneseEntries.get(idx).getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			if (polishJapaneseEntries.get(idx).isUseEntry() == true) {
				result.add(polishJapaneseEntries.get(idx));
			}
	
		}
		
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(new File(destinationFileName)));
		
		CsvReaderWriter.generateCsv(gzipOutputStream, result);
		
		return result;
	}
		
	private static void generateKanjiEntries(
			List<PolishJapaneseEntry> dictionary, String sourceKanjiName,
			String sourceKanjiDic2FileName,
			String sourceKradFileName,
			String kanjivgDir,
			String destinationFileName) throws Exception {
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		generateAdditionalKanjiEntries(dictionary, kanjiEntries, readKanjiDic2);
		
		Map<String, KanjivgEntry> kanjivgEntryMap = readKanjivgDir(kanjivgDir);
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			String kanji = currentKanjiEntry.getKanji();
			
			KanjivgEntry kanjivgEntry = kanjivgEntryMap.get(kanji);
			
			currentKanjiEntry.setKanjivgEntry(kanjivgEntry);			
		}
		
		//OutputStream outputStream = new FileOutputStream(destinationFileName + "-normal.csv");
		OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(new File(destinationFileName)));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries);		
	}

	private static void generateAdditionalKanjiEntries(List<PolishJapaneseEntry> dictionary,
			List<KanjiEntry> kanjiEntries, Map<String, KanjiDic2Entry> readKanjiDic2) {
		
		Set<String> alreadySetKanji = new HashSet<String>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			alreadySetKanji.add(currentKanjiEntry.getKanji());	
		}
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : dictionary) {
			
			String kanji = currentPolishJapaneseEntry.getKanji();
			
			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {
				
				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));
				
				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}
				
				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);
				
				if (kanjiDic2Entry != null) {
					alreadySetKanji.add(kanji);
					
					KanjiEntry newKanjiEntry = new KanjiEntry();
					
					newKanjiEntry.setId(kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);
					newKanjiEntry.setKanji(currentKanjiChar);
					newKanjiEntry.setKanjiDic2Entry(kanjiDic2Entry);
					
					List<String> polishTranslates = new ArrayList<String>();
					
					polishTranslates.add("nieznane znaczenie; możesz pomóc, jeśli znasz znaczenie");
					
					newKanjiEntry.setPolishTranslates(polishTranslates);
					newKanjiEntry.setInfo("");
					
					kanjiEntries.add(newKanjiEntry);
				}
			}
		}
	}
	
	private static void generateKanjiRadical(String radfile, String radicalDestination) throws Exception {
		
		List<RadicalInfo> radicalList = KanjiDic2Reader.readRadkfile(radfile);
		
		//OutputStream outputStream = new FileOutputStream(radicalDestination + "-normal.csv");
		OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(new File(radicalDestination)));

		CsvReaderWriter.generateKanjiRadicalCsv(outputStream, radicalList);	
	}
	
	private static Map<String, KanjivgEntry> readKanjivgDir(String kanjivgDir) throws Exception {
		
		Map<String, KanjivgEntry> kanjivgEntryMap = new HashMap<String, KanjivgEntry>();
		
		File[] kanjivgDirFileList = new File(kanjivgDir).listFiles();
		
		for (File currentKanjivgDirFileList : kanjivgDirFileList) {
						
			KanjivgEntry kanjivgEntry = KanjivgReader.readKanjivgFile(currentKanjivgDirFileList);
			
			if (kanjivgEntry != null) {
				kanjivgEntryMap.put(kanjivgEntry.getKanji(), kanjivgEntry);
			}
		}
		
		return kanjivgEntryMap;
	}
}
