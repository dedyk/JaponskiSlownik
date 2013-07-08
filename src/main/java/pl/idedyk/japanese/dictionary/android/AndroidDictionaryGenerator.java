package pl.idedyk.japanese.dictionary.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke.Point;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;
import pl.idedyk.japanese.dictionary.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;
import pl.idedyk.japanese.dictionary.tools.KanjiUtils;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;
import pl.idedyk.japanese.dictionary.tools.TomoeReader;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> dictionary = checkAndSavePolishJapaneseEntries("input/word.csv", 
				"../JaponskiSlownik_dodatki/JMdict_e",
				"../JaponskiSlownik_dodatki/edict_sub-utf8",
				"../JaponskiSlownik_dodatki/enamdict-utf8",
				"output/word.csv");
		
		generateKanaEntries("../JaponskiSlownik_dodatki/kanjivg", "output/kana.csv");
		
		generateKanjiRadical("../JaponskiSlownik_dodatki/radkfile", "output/radical.csv");
		
		final String zinniaTomoeSlimBinaryFile = "output/kanji_recognizer.model.db";
		
		List<KanjiEntry> kanjiEntries = generateKanjiEntries(dictionary, "input/kanji.csv", "../JaponskiSlownik_dodatki/kanjidic2.xml", 
				"../JaponskiSlownik_dodatki/kradfile", "../JaponskiSlownik_dodatki/kanjivg",
				"output/kanji.csv");
				
		generateZinniaTomoeSlimBinaryFile(kanjiEntries, "output/kanjivgTomoeFile.txt", "output/kanjivgTomoeFile.xml",
				"../JaponskiSlownik_dodatki/zinnia-0.06-app/bin/zinnia_learn",
				"output/kanji_recognizer_handwriting-ja-slim.s",
				zinniaTomoeSlimBinaryFile
				);
	}

	private static List<PolishJapaneseEntry> checkAndSavePolishJapaneseEntries(String sourceFileName, 
			String jmedictFileName, String edictCommonFileName, String edictNameFileName, String destinationFileName) throws Exception {
		
		System.out.println("checkAndSavePolishJapaneseEntries");
				
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		
		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();
		
		System.out.println("checkAndSavePolishJapaneseEntries: parsePolishJapaneseEntriesFromCsv");
		
		// parse csv
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName);
		
		System.out.println("checkAndSavePolishJapaneseEntries: readEdict");
		
		// read edict
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict(jmedictFileName);
		
		System.out.println("checkAndSavePolishJapaneseEntries: readEdictCommon");
		    
		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict(edictCommonFileName);     
						
		// validate
		
		System.out.println("checkAndSavePolishJapaneseEntries: validatePolishJapaneseEntries");		
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, jmedict);
		
		System.out.println("checkAndSavePolishJapaneseEntries: detectDuplicatePolishJapaneseKanjiEntries");
		Validator.detectDuplicatePolishJapaneseKanjiEntries(polishJapaneseEntries);
		
		System.out.println("checkAndSavePolishJapaneseEntries: validateUseNoEntryPolishJapaneseKanjiEntries");
		Validator.validateUseNoEntryPolishJapaneseKanjiEntries(polishJapaneseEntries);
		
		// generate groups
		System.out.println("checkAndSavePolishJapaneseEntries: generateGroups");
		
		List<PolishJapaneseEntry> result = Helper.generateGroups(polishJapaneseEntries, true, true);
		
		System.out.println("checkAndSavePolishJapaneseEntries: generateAdditionalInfoFromEdict");
		
		// generate additional data from edict
		Helper.generateAdditionalInfoFromEdict(jmedict, jmedictCommon, result);
		
		// test !
		
		// read edict name
		// TreeMap<String, EDictEntry> jmedictName = EdictReader.readEdict(edictNameFileName);
		
		// generate names
		// Helper.generateNames(jmedictName, result);		
		
		System.out.println("checkAndSavePolishJapaneseEntries: generateCsv");
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));
		
		CsvReaderWriter.generateCsv(outputStream, result, false);
		
		return result;
	}
	
	private static void generateKanaEntries(String kanjivgDir, String destinationFileName) throws Exception {
		
		System.out.println("generateKanaEntries");
		
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
		
	private static List<KanjiEntry> generateKanjiEntries(
			List<PolishJapaneseEntry> dictionary, String sourceKanjiName,
			String sourceKanjiDic2FileName,
			String sourceKradFileName,
			String kanjivgDir,
			String destinationFileName) throws Exception {
		
		System.out.println("generateKanjiEntries");
		
		System.out.println("generateKanjiEntries: readKradFile");
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		System.out.println("generateKanjiEntries: readKanjiDic2");
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		System.out.println("generateKanjiEntries: parseKanjiEntriesFromCsv");
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		System.out.println("generateKanjiEntries: validateDuplicateKanjiEntriesList");
		Validator.validateDuplicateKanjiEntriesList(kanjiEntries);
		
		System.out.println("generateKanjiEntries: generateAdditionalKanjiEntries");
		generateAdditionalKanjiEntries(dictionary, kanjiEntries, readKanjiDic2);
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			String kanji = currentKanjiEntry.getKanji();
			
			String kanjivgId = KanjivgReader.getKanjivgId(kanji);
			
			KanjivgEntry kanjivgEntry = KanjivgReader.readKanjivgFile(new File(kanjivgDir, kanjivgId + ".svg"));
			
			currentKanjiEntry.setKanjivgEntry(kanjivgEntry);			
		}
		
		System.out.println("generateKanjiEntries: generateKanjiCsv");
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries);
		
		return kanjiEntries;
	}

	private static void generateAdditionalKanjiEntries(List<PolishJapaneseEntry> dictionary,
			List<KanjiEntry> kanjiEntries, Map<String, KanjiDic2Entry> readKanjiDic2) {
		
		Set<String> alreadySetKanji = new HashSet<String>();
		Set<String> alreadySetKanjiSource = new HashSet<String>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			alreadySetKanji.add(currentKanjiEntry.getKanji());
			alreadySetKanjiSource.add(currentKanjiEntry.getKanji());
		}
		
		final Map<String, Integer> kanjiCountMap = new HashMap<String, Integer>();
		
		final Map<String, Integer> additionalKanjiIds = new HashMap<String, Integer>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : dictionary) {
			
			String kanji = currentPolishJapaneseEntry.getKanji();
			
			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {
				
				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));
				
				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);
				
				if (kanjiDic2Entry != null) {
					
					if (alreadySetKanjiSource.contains(currentKanjiChar) == false) {
						
						Integer kanjiCountMapInteger = kanjiCountMap.get(currentKanjiChar);
						
						if (kanjiCountMapInteger == null) {
							kanjiCountMapInteger = new Integer(0);
						}
						
						kanjiCountMapInteger = kanjiCountMapInteger.intValue() + 1;
						
						kanjiCountMap.put(currentKanjiChar, kanjiCountMapInteger);
					}
				}
				
				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}
				
				if (kanjiDic2Entry != null) {
					
					alreadySetKanji.add(currentKanjiChar);
					
					KanjiEntry newKanjiEntry = generateKanjiEntry(currentKanjiChar, kanjiDic2Entry, kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);					
					
					kanjiEntries.add(newKanjiEntry);
					
					additionalKanjiIds.put(currentKanjiChar, newKanjiEntry.getId());
				}
			}
		}
		
		// generate additional top 2500 kanji
		
		Iterator<String> readKanjiDic2KeySetIterator = readKanjiDic2.keySet().iterator();		
				
		while(readKanjiDic2KeySetIterator.hasNext()) {
			
			String readKanjiDic2KeySetIteratorCurrentKanji = readKanjiDic2KeySetIterator.next();
						
			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(readKanjiDic2KeySetIteratorCurrentKanji);
			
			Integer freq = kanjiDic2Entry.getFreq();
			
			if (freq == null) {
				continue;
			}
			
			if (alreadySetKanji.contains(readKanjiDic2KeySetIteratorCurrentKanji)) {
				continue;
			}
			
			alreadySetKanji.add(readKanjiDic2KeySetIteratorCurrentKanji);
			
			KanjiEntry newKanjiEntry = generateKanjiEntry(readKanjiDic2KeySetIteratorCurrentKanji, kanjiDic2Entry, kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);					
			
			kanjiEntries.add(newKanjiEntry);
			
			additionalKanjiIds.put(readKanjiDic2KeySetIteratorCurrentKanji, newKanjiEntry.getId());
			
			Integer kanjiCountMapInteger = kanjiCountMap.get(readKanjiDic2KeySetIteratorCurrentKanji);
			
			if (kanjiCountMapInteger == null) {
				kanjiCountMapInteger = new Integer(0);
			}
			
			kanjiCountMap.put(readKanjiDic2KeySetIteratorCurrentKanji, kanjiCountMapInteger);
		}
		
		// end
		
		String[] kanjiArray = new String[kanjiCountMap.size()];
		
		kanjiCountMap.keySet().toArray(kanjiArray);
		
		Arrays.sort(kanjiArray, new Comparator<String>() {

			@Override
			public int compare(String kanji1, String kanji2) {
				
				/*
				Integer kanji2Count = kanjiCountMap.get(kanji2);
				Integer kanji1Count = kanjiCountMap.get(kanji1);
				
				int compareResult = kanji2Count.compareTo(kanji1Count);
				
				if (compareResult != 0) {
					return compareResult;
				} else {					
					return additionalKanjiIds.get(kanji1).compareTo(additionalKanjiIds.get(kanji2));
				}
				*/
				
				return additionalKanjiIds.get(kanji1).compareTo(additionalKanjiIds.get(kanji2));
			}
		});
		
		System.out.println("\n---\n");
		
		for (int kanjiArrayIdx = 0; kanjiArrayIdx < kanjiArray.length && kanjiArrayIdx < 10; ++kanjiArrayIdx) {
			
			String currentKanji = kanjiArray[kanjiArrayIdx];
			
			System.out.println(currentKanji + " - " + kanjiCountMap.get(currentKanji));			
		}
		
		System.out.println("\nKanji: " + kanjiArray.length);
				
		System.out.println("\n---\n");
	}
	
	private static KanjiEntry generateKanjiEntry(String kanji, KanjiDic2Entry kanjiDic2Entry, int id) {
		
		KanjiEntry newKanjiEntry = new KanjiEntry();
		
		newKanjiEntry.setId(id);
		newKanjiEntry.setKanji(kanji);
		newKanjiEntry.setKanjiDic2Entry(kanjiDic2Entry);
		
		List<String> polishTranslates = new ArrayList<String>();
		
		polishTranslates.add("nieznane znaczenie");
		
		newKanjiEntry.setPolishTranslates(polishTranslates);
		newKanjiEntry.setInfo("");
		
		newKanjiEntry.setGenerated(true);
		
		List<String> groupsList = new ArrayList<String>();
		
		String jlpt = KanjiUtils.getJlpt(kanji);
		
		if (jlpt != null) {
			groupsList.add(jlpt);
		}
		
		/*
		if (kanjiDic2Entry != null) {
			Integer jlpt = kanjiDic2Entry.getJlpt();
			
			if (jlpt != null) {
				groupsList.add("JLPT " + jlpt);
			}
		}
		*/
		
		newKanjiEntry.setGroups(groupsList);	
		
		return newKanjiEntry;
	}
	
	private static void generateKanjiRadical(String radfile, String radicalDestination) throws Exception {
		
		System.out.println("generateKanjiRadical");
		
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
	
	private static void generateZinniaTomoeSlimBinaryFile(List<KanjiEntry> kanjiEntries, 
			String kvgToolFileFromKanjivg, String tomoeFileFromKanjivg, String zinniaLearnPath, String zinniaTomoeLearnSlimFile, String zinniaTomoeSlimBinaryFile) throws Exception {
		
		System.out.println("generateZinniaTomoeSlimBinaryFile");
		
		Set<String> kanjiSet = new HashSet<String>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			String kanji = currentKanjiEntry.getKanji();
			
			kanjiSet.add(kanji);
		}
				
		File kvgToolFileFromKanjivgFile = new File(kvgToolFileFromKanjivg);
		File tomoeFileFromKanjivgFile = new File(tomoeFileFromKanjivg);
		
		kanjiEntries = new ArrayList<KanjiEntry>(kanjiEntries);
		
		Collections.sort(kanjiEntries, new Comparator<KanjiEntry>() {

			@Override
			public int compare(KanjiEntry o1, KanjiEntry o2) {
				return o1.getKanji().compareTo(o2.getKanji());
			}
		});
		
		BufferedWriter tomoeFileFromKanjivgWriter = new BufferedWriter(new FileWriter(kvgToolFileFromKanjivgFile));
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			tomoeFileFromKanjivgWriter.write(currentKanjiEntry.getKanji());
			tomoeFileFromKanjivgWriter.write(" ");
			
			List<String> strokePaths = currentKanjiEntry.getKanjivgEntry().getStrokePaths();
			
			for (String currentStrokePath : strokePaths) {
				tomoeFileFromKanjivgWriter.write(currentStrokePath);
				tomoeFileFromKanjivgWriter.write(";");
			}
			
			tomoeFileFromKanjivgWriter.write("\n");			
		}
		
		tomoeFileFromKanjivgWriter.close();
		
		Runtime runtime = Runtime.getRuntime();
		
        Process process = runtime.exec(new String[] { "ruby", "xml_all_kanji.rb", kvgToolFileFromKanjivgFile.getAbsolutePath(), tomoeFileFromKanjivgFile.getAbsolutePath() }, null, new File("../KVG-Tools/" ));
        
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
        
        System.out.println("Generate kvg tool exited with error code: " + exitVal);
		
		List<TomoeEntry> tomoeEntries = TomoeReader.readTomoeXmlHandwritingDatabase(tomoeFileFromKanjivgFile.getAbsolutePath());
		
		BufferedWriter zinniaTomoeSlimFileWriter = new BufferedWriter(new FileWriter(zinniaTomoeLearnSlimFile));
		
		for (TomoeEntry currentTomoeEntry : tomoeEntries) {
			
			String kanji = currentTomoeEntry.getKanji();
			
			if (kanjiSet.contains(kanji) == false) {
				continue;
			}
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("(character (value ");
			
			sb.append(kanji);
			
			sb.append(")(width 1000)(height 1000)(strokes ");
			
			List<Stroke> strokeList = currentTomoeEntry.getStrokeList();
			
			for (Stroke currentStroke : strokeList) {
				
				sb.append("(");
				
				List<Point> pointList = currentStroke.getPointList();
				
				for (Point currentPoint : pointList) {
					
					sb.append("(").append(currentPoint.getX()).append(" ").append(currentPoint.getY()).append(")");
				}
				
				sb.append(")");				
			}
			
			sb.append(")");
			
			sb.append(")\n");
			
			zinniaTomoeSlimFileWriter.write(sb.toString());
		}
		
		zinniaTomoeSlimFileWriter.close();
		
		runtime = Runtime.getRuntime();
		
        process = runtime.exec( new String[] { zinniaLearnPath, zinniaTomoeLearnSlimFile, zinniaTomoeSlimBinaryFile });
        
        stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        line = null;

        while((line = stream.readLine()) != null) {
            System.out.println(line);
        }

        stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        line = null;

        while((line = stream.readLine()) != null) {
            System.out.println(line);
        }
        
        exitVal = process.waitFor();
        
        System.out.println("Generate zinnia tomoe db exited with error code: " + exitVal);
        
		kvgToolFileFromKanjivgFile.delete();
		tomoeFileFromKanjivgFile.delete();
        new File(zinniaTomoeLearnSlimFile).delete();
        new File(zinniaTomoeSlimBinaryFile + ".txt").delete();
	}
}
