package pl.idedyk.japanese.dictionary.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		
		generateKanaEntries("../JaponskiSlownik_dodatki/kanjivg", "output/kana.csv");
		
		generateKanjiRadical("../JaponskiSlownik_dodatki/radkfile", "output/radical.csv");
		
		final String zinniaTomoeSlimFile = "output/kanji_handwriting-ja.slim.model.txt";
		final String zinniaTomoeSlimBinaryFile = "output/kanji_recognizer.model.db";
		
		generateKanjiEntries(dictionary, "input/kanji.csv", "../JaponskiSlownik_dodatki/kanjidic2.xml", 
				"../JaponskiSlownik_dodatki/kradfile", "../JaponskiSlownik_dodatki/kanjivg",
				"../JaponskiSlownik_dodatki/zinnia-tomoe-0.6.0-20080911/handwriting-ja.model.txt",
				zinniaTomoeSlimFile,
				"output/kanji.csv");
		
		generateZinniaTomoeSlimBinaryFile("../JaponskiSlownik_dodatki/zinnia-0.06-app/bin/zinnia_convert",
				zinniaTomoeSlimFile, zinniaTomoeSlimBinaryFile);
		
		new File(zinniaTomoeSlimFile).delete();
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
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));
		
		CsvReaderWriter.generateCsv(outputStream, result);
		
		return result;
	}
	
	private static void generateKanaEntries(String kanjivgDir, String destinationFileName) throws Exception {
		
		// hiragana
		List<KanaEntry> kanaEntries = KanaHelper.getAllHiraganaKanaEntries();
		
		// katakana
		kanaEntries.addAll(KanaHelper.getAllKatakanaKanaEntries());
		
		// additional
		kanaEntries.addAll(KanaHelper.getAdditionalKanaEntries());
		
		Map<String, KanjivgEntry> kanaJapaneseKanjiEntryCache = new HashMap<String, KanjivgEntry>();

		for (KanaEntry currentKanaEntry : kanaEntries) {
			
			List<KanjivgEntry> kanaStrokePaths = new ArrayList<KanjivgEntry>();
			
			String kanaJapanese = currentKanaEntry.getKanaJapanese();
			
			for (int kanaJapaneseCharIdx = 0; kanaJapaneseCharIdx < kanaJapanese.length(); ++kanaJapaneseCharIdx) {
				
				String currentKanaJapaneseChar = String.valueOf(kanaJapanese.charAt(kanaJapaneseCharIdx));
				
				KanjivgEntry kanjivgEntryInCache = kanaJapaneseKanjiEntryCache.get(currentKanaJapaneseChar);
				
				if (kanjivgEntryInCache == null) {
					
					String kanjivgId = KanjivgReader.getKanjivgId(currentKanaJapaneseChar);
					
					kanjivgEntryInCache = KanjivgReader.readKanjivgFile(new File(kanjivgDir, kanjivgId + ".svg"));
					
					if (kanjivgEntryInCache == null) {
						throw new RuntimeException("kanjivgEntryInCache == null");
					}
					
					kanaJapaneseKanjiEntryCache.put(currentKanaJapaneseChar, kanjivgEntryInCache);
				} 
				
				kanaStrokePaths.add(kanjivgEntryInCache);
			}
			
			if (kanaStrokePaths == null || kanaStrokePaths.size() <= 0 || kanaStrokePaths.size() > 2) {
				throw new RuntimeException("kanaStrokePaths == null || kanaStrokePaths.size() <= 0 || kanaStrokePaths.size() > 2");
			}
			
			currentKanaEntry.setStrokePaths(kanaStrokePaths);
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));
		
		CsvReaderWriter.generateKanaEntriesCsvWithStrokePaths(outputStream, kanaEntries);		

	}
		
	private static void generateKanjiEntries(
			List<PolishJapaneseEntry> dictionary, String sourceKanjiName,
			String sourceKanjiDic2FileName,
			String sourceKradFileName,
			String kanjivgDir,
			String zinniaTomoeFile, String zinniaTomoeSlimFile,
			String destinationFileName) throws Exception {
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		generateAdditionalKanjiEntries(dictionary, kanjiEntries, readKanjiDic2);
		
		Set<String> kanjiSet = new HashSet<String>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			String kanji = currentKanjiEntry.getKanji();
			
			kanjiSet.add(kanji);
			
			String kanjivgId = KanjivgReader.getKanjivgId(kanji);
			
			KanjivgEntry kanjivgEntry = KanjivgReader.readKanjivgFile(new File(kanjivgDir, kanjivgId + ".svg"));
			
			currentKanjiEntry.setKanjivgEntry(kanjivgEntry);			
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries);
		
		// kanji for zinnia
		BufferedReader zinniaTomoeFileReader = new BufferedReader(new FileReader(zinniaTomoeFile));
		BufferedWriter zinniaTomoeSlimFileWriter = new BufferedWriter(new FileWriter(zinniaTomoeSlimFile));
		
		while(true) {
			String currentZinniaTomoeLine = zinniaTomoeFileReader.readLine();
			
			if (currentZinniaTomoeLine == null) {
				break;
			}
			
			String currentzinniaTomoeKanji = String.valueOf(currentZinniaTomoeLine.charAt(0));
			
			if (kanjiSet.contains(currentzinniaTomoeKanji) == true) {
				zinniaTomoeSlimFileWriter.write(currentZinniaTomoeLine + "\n");
			}
		}
		
		zinniaTomoeFileReader.close();
		zinniaTomoeSlimFileWriter.close();
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
					alreadySetKanji.add(currentKanjiChar);
					
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
		
		OutputStream outputStream = new FileOutputStream(new File(radicalDestination));

		CsvReaderWriter.generateKanjiRadicalCsv(outputStream, radicalList);	
	}
	
	/*
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
	*/
	
	private static void generateZinniaTomoeSlimBinaryFile(String zinniaConvertExecPath,
			String zinniaTomoeSlimFile,
			String zinniaTomoeSlimBinaryFile) throws Exception {
		
		Runtime runtime = Runtime.getRuntime();
		
        Process process = runtime.exec(new String[] { zinniaConvertExecPath, zinniaTomoeSlimFile, zinniaTomoeSlimBinaryFile });
		//Process process = runtime.exec(zinniaConvertExecPath);
        
        BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;

        while((line = stream.readLine()) != null) {
            System.out.println(line);
        }

        stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        line = null;

        while((line = stream.readLine()) != null) {
            System.out.println(line);
        }
        
        int exitVal = process.waitFor();
        
        System.out.println("Generate zinnia tomoe db exited with error code: " + exitVal);
	}
}
