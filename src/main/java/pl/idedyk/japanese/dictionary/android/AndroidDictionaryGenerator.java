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
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.GroupWithTatoebaSentenceList;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.api.dto.TatoebaSentence;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke.Point;
import pl.idedyk.japanese.dictionary.dto.TransitiveIntransitivePair;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;
import pl.idedyk.japanese.dictionary.tools.KanjiUtils;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;
import pl.idedyk.japanese.dictionary.tools.LatexDictionaryGenerator;
import pl.idedyk.japanese.dictionary.tools.TatoebaSentencesParser;
import pl.idedyk.japanese.dictionary.tools.TomoeReader;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2NameHelper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

import com.csvreader.CsvReader;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		boolean fullMode = true;
		
		if (args.length > 0) {
			fullMode = Boolean.parseBoolean(args[0]);
		}
		
		System.out.println("readEdictCommon");

		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");

		// read new jmedict
		System.out.println("new jmedict");
				
		File kanjivgSingleXmlFile = new File("../JapaneseDictionary_additional/kanjivg/kanjivg.xml");
		File kanjivgPatchDirFile = new File("../JapaneseDictionary_additional/kanjivg/patch");
		
		Map<String, KanjivgEntry> kanjivgEntryMap = KanjivgReader.readKanjivgSingleXmlFile(kanjivgSingleXmlFile, kanjivgPatchDirFile);
		
		List<PolishJapaneseEntry> dictionary = checkAndSavePolishJapaneseEntries(jmedictCommon,
				new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" } , "input/transitive_intransitive_pairs.csv", "output/word.csv", "output/word.json", "output/word-power.csv",
				"output/transitive_intransitive_pairs.csv", "output/word2.xml"); //, "output/word_group.csv");
				
		generateKanaEntries(kanjivgEntryMap, "output/kana.csv");

		generateKanjiRadical("../JapaneseDictionary_additional/radkfile", "output/radical.csv");

		final String zinniaTomoeSlimBinaryFile = "output/kanji_recognizer.model.db";
		
		List<KanjiEntryForDictionary> kanjiEntries = generateKanjiEntries(dictionary, jmedictCommon, kanjivgEntryMap, "input/kanji.csv",
				"../JapaneseDictionary_additional/kanjidic2.xml", "../JapaneseDictionary_additional/kradfile",
				"output/kanji.csv");

		generateNamePolishJapaneseEntries("output/names.csv");
		
		if (fullMode == true) {
		
			generateZinniaTomoeSlimBinaryFile(kanjiEntries, kanjivgSingleXmlFile, "output/kanjivgTomoeFile.xml",
					"../JapaneseDictionary_additional/zinnia-0.06-app/bin/zinnia_learn",
					"output/kanji_recognizer_handwriting-ja-slim.s", zinniaTomoeSlimBinaryFile);
			
			generatePdfDictionary(dictionary, "pdf_dictionary/dictionary.tex", "output");
		}
	}

	private static List<PolishJapaneseEntry> checkAndSavePolishJapaneseEntries(
			TreeMap<String, EDictEntry> jmedictCommon,
			String[] sourceFileNames,
			String transitiveIntransitivePairsFileName, String destinationFileName, String destinationJSONFileName, String destinationPowerFileName,
			String transitiveIntransitivePairsOutputFile, String word2XmlFile /*, String wordGroupOutputFile */) throws Exception {

		System.out.println("checkAndSavePolishJapaneseEntries");

		KanaHelper kanaHelper = new KanaHelper();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();

		// katakana
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();

		System.out.println("checkAndSavePolishJapaneseEntries: parsePolishJapaneseEntriesFromCsv");

		// parse csv
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileNames);
		
		// word 2 - dictionary
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		// word 2 name - dictionary
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
		
		// validate		
		System.out.println("checkAndSavePolishJapaneseEntries: validatePolishJapaneseEntries");
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, dictionaryHelper, dictionary2NameHelper, true);

		System.out.println("checkAndSavePolishJapaneseEntries: detectDuplicatePolishJapaneseKanjiEntries");
		Validator.detectDuplicatePolishJapaneseKanjiEntries(polishJapaneseEntries, "input/word-duplicate.csv");

		// System.out.println("checkAndSavePolishJapaneseEntries: validateUseNoEntryPolishJapaneseKanjiEntries");
		// Validator.validateUseNoEntryPolishJapaneseKanjiEntries(polishJapaneseEntries);
		
		// generate groups
		System.out.println("checkAndSavePolishJapaneseEntries: generateGroups");

		List<PolishJapaneseEntry> result = Helper.generateGroups(polishJapaneseEntries, true);		
		
		System.out.println("checkAndSavePolishJapaneseEntries: generateAdditionalInfoFromEdict");

		List<TransitiveIntransitivePair> readTransitiveIntransitivePair = readTransitiveIntransitivePair(transitiveIntransitivePairsFileName);

		// generate additional data from edict
		Helper.generateAdditionalInfoFromEdict(dictionaryHelper, jmedictCommon, polishJapaneseEntries);
		
		// sprawdzenie, czy slowa w tych samych grupach, maja dokladnie to samo romaji
		Validator.validateEdictGroupRomaji(polishJapaneseEntries);
		
		// generate transitive intransitive pairs
		Helper.generateTransitiveIntransitivePairs(readTransitiveIntransitivePair, result,
				transitiveIntransitivePairsOutputFile);

		// generate names
		// Helper.generateNames(jmedictName, result);	
		
		// wygenerowanie unikalny kluczy
		Helper.generateUniqueKeys(polishJapaneseEntries);
		
		System.out.println("checkAndSavePolishJapaneseEntries: generateExampleSentence");
		generateExampleSentence(result, "../JapaneseDictionary_additional/tatoeba", "output/sentences.csv", "output/sentences_groups.csv");

		System.out.println("checkAndSavePolishJapaneseEntries: generateCsv");

		CsvReaderWriter.generateCsv(new String[] { destinationFileName }, result, true, false, true, false, null);
		
		// zapisanie w formacie JSON		
		//JSONArray resultJSONArray = JSONReaderWriter.createDictionaryOutputJSON(jmeNewDictionary, polishJapaneseEntries);		

		//JSONReaderWriter.writeJSONArrayToFile(new File(destinationJSONFileName), resultJSONArray);
		
		// generowanie mocy slow
		System.out.println("checkAndSavePolishJapaneseEntries: generateWordPowerCsv");

		FileOutputStream outputPowerStream = new FileOutputStream(new File(destinationPowerFileName));

		CsvReaderWriter.generateWordPowerCsv(outputPowerStream, result);
		
		// generowanie grup slow - wylaczone
		/*
		System.out.println("checkAndSavePolishJapaneseEntries: generateWordGroupCsv");
		
		List<DictionaryEntryGroup> generateWordGroupList = generateWordGroups(result);
		
		FileOutputStream outputWordGroupStream = new FileOutputStream(new File(wordGroupOutputFile));
		
		CsvReaderWriter.generateWordGroupCsv(outputWordGroupStream, generateWordGroupList);
		*/
				
		dictionaryHelper.validateAllPolishDictionaryEntryList();
		
		// pobieramy wszystkie slowa, ktore sa w nowym slowniku
		List<Entry> allPolishDictionary2EntryList = dictionaryHelper.getAllPolishDictionaryEntryList();

		JMdict newJmdict = new JMdict();
		
		newJmdict.getEntryList().addAll(allPolishDictionary2EntryList);
		
		dictionaryHelper.sortJMdict(newJmdict);
		dictionaryHelper.saveJMdictAsXml(newJmdict, word2XmlFile);
		
		return result;
	}

	private static void generateExampleSentence(List<PolishJapaneseEntry> dictionary, String tatoebaSentencesDir, 
			String sentencesDestinationFileName, String sentencesGroupsDestinationFileName) throws Exception {
		
		System.out.println("generateExampleSentence");
		
		TatoebaSentencesParser tatoebaSentencesParser = new TatoebaSentencesParser(tatoebaSentencesDir);
		
		tatoebaSentencesParser.parse();
		
		List<TatoebaSentence> uniqueSentences = new ArrayList<TatoebaSentence>();
		List<GroupWithTatoebaSentenceList> uniqueSentencesWithGroupList = new ArrayList<GroupWithTatoebaSentenceList>();
		
		Set<String> uniqueSentenceIds = new TreeSet<String>();
		Set<String> uniqueGroupIds = new TreeSet<String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : dictionary) {
			
			List<String> groupIds = new ArrayList<String>();
			
			String word = null;
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				word = polishJapaneseEntry.getKanji();
				
			} else {
				word = polishJapaneseEntry.getKana();
			}
			
			List<GroupWithTatoebaSentenceList> exampleSentencesList = tatoebaSentencesParser.getExampleSentences(null, word, 10);
			
			if (exampleSentencesList == null) {
				polishJapaneseEntry.setExampleSentenceGroupIdsList(groupIds);
				
				continue;
			}
			
			for (GroupWithTatoebaSentenceList currentExampleSentencesGroup : exampleSentencesList) {
				
				groupIds.add(currentExampleSentencesGroup.getGroupId());
				
				if (uniqueGroupIds.contains(currentExampleSentencesGroup.getGroupId()) == false) {	
					
					uniqueGroupIds.add(currentExampleSentencesGroup.getGroupId());					
					uniqueSentencesWithGroupList.add(currentExampleSentencesGroup);					
				}				
				
				for (TatoebaSentence currentTatoebaSentenceInGroup : currentExampleSentencesGroup.getTatoebaSentenceList()) {
										
					if (uniqueSentenceIds.contains(currentTatoebaSentenceInGroup.getId()) == false) {
						
						uniqueSentenceIds.add(currentTatoebaSentenceInGroup.getId());
						uniqueSentences.add(currentTatoebaSentenceInGroup);
					}					
				}				
			}
			
			polishJapaneseEntry.setExampleSentenceGroupIdsList(groupIds);
		}
		
		Collections.sort(uniqueSentences, new Comparator<TatoebaSentence>() {

			@Override
			public int compare(TatoebaSentence o1, TatoebaSentence o2) {
				return new Long(o1.getId()).compareTo(new Long(o2.getId()));
			}
		});
		
		Collections.sort(uniqueSentencesWithGroupList, new Comparator<GroupWithTatoebaSentenceList>() {

			@Override
			public int compare(GroupWithTatoebaSentenceList o1, GroupWithTatoebaSentenceList o2) {
				return new Long(o1.getGroupId()).compareTo(new Long(o2.getGroupId()));
			}
		});
		
		CsvReaderWriter.writeTatoebaSentenceList(sentencesDestinationFileName, uniqueSentences);
		CsvReaderWriter.writeTatoebaSentenceGroupsList(sentencesGroupsDestinationFileName, uniqueSentencesWithGroupList);
	}

	private static void generateKanaEntries(Map<String, KanjivgEntry> kanjivgEntryMap, String destinationFileName) throws Exception {

		System.out.println("generateKanaEntries");
		
		KanaHelper kanaHelper = new KanaHelper();
		
		List<KanaEntry> kanaEntries = new ArrayList<KanaEntry>();

		// hiragana
		kanaEntries.addAll(kanaHelper.getAllHiraganaKanaEntries());

		// katakana
		kanaEntries.addAll(kanaHelper.getAllKatakanaKanaEntries());

		// additional
		kanaEntries.addAll(kanaHelper.getAllAdditionalKanaEntries());

		for (KanaEntry currentKanaEntry : kanaEntries) {

			List<KanjivgEntry> kanaStrokePaths = new ArrayList<KanjivgEntry>();

			String kanaJapanese = currentKanaEntry.getKanaJapanese();

			for (int kanaJapaneseCharIdx = 0; kanaJapaneseCharIdx < kanaJapanese.length(); ++kanaJapaneseCharIdx) {

				String currentKanaJapaneseChar = String.valueOf(kanaJapanese.charAt(kanaJapaneseCharIdx));

				KanjivgEntry kanjivgEntryInCache = kanjivgEntryMap.get(currentKanaJapaneseChar);

				if (kanjivgEntryInCache == null) {
					throw new RuntimeException("kanjivgEntryInCache == null");
				}

				kanaStrokePaths.add(kanjivgEntryInCache);
			}

			if (kanaStrokePaths == null || kanaStrokePaths.size() <= 0 || kanaStrokePaths.size() > 2) {
				throw new RuntimeException(
						"kanaStrokePaths == null || kanaStrokePaths.size() <= 0 || kanaStrokePaths.size() > 2");
			}

			currentKanaEntry.setStrokePaths(kanaStrokePaths);
		}

		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));

		CsvReaderWriter.generateKanaEntriesCsvWithStrokePaths(outputStream, kanaEntries);

	}

	private static List<KanjiEntryForDictionary> generateKanjiEntries(List<PolishJapaneseEntry> dictionary,
			TreeMap<String, EDictEntry> jmedictCommon,
			Map<String, KanjivgEntry> kanjivgEntryMap, String sourceKanjiName, String sourceKanjiDic2FileName,
			String sourceKradFileName, String destinationFileName) throws Exception {

		System.out.println("generateKanjiEntries");

		System.out.println("generateKanjiEntries: readKradFile");
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);

		System.out.println("generateKanjiEntries: readKanjiDic2");
		Map<String, KanjiDic2EntryForDictionary> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);

		System.out.println("generateKanjiEntries: parseKanjiEntriesFromCsv");
		List<KanjiEntryForDictionary> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2, true);

		System.out.println("generateKanjiEntries: validateDuplicateKanjiEntriesList");
		Validator.validateDuplicateKanjiEntriesList(kanjiEntries);
		
		System.out.println("generateKanjiEntries: validateDuplicateMeansKanjiEntriesList");
		Validator.validateDuplicateMeansKanjiEntriesList(kanjiEntries);

		System.out.println("generateKanjiEntries: generateAdditionalKanjiEntries");
		generateAdditionalKanjiEntries(dictionary, kanjiEntries, readKanjiDic2, "input/osjp.csv", jmedictCommon);

		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {

			String kanji = currentKanjiEntry.getKanji();

			KanjivgEntry kanjivgEntry = kanjivgEntryMap.get(kanji);

			currentKanjiEntry.setKanjivgEntry(kanjivgEntry);
		}

		System.out.println("generateKanjiEntries: generateKanjiCsv");

		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));

		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries, true, null);

		return kanjiEntries;
	}

	private static void generateAdditionalKanjiEntries(List<PolishJapaneseEntry> dictionary,
			List<KanjiEntryForDictionary> kanjiEntries, Map<String, KanjiDic2EntryForDictionary> readKanjiDic2, String osjpFile,
			TreeMap<String, EDictEntry> jmedictCommon) throws Exception {

		Set<String> alreadySetKanji = new HashSet<String>();
		Set<String> alreadySetKanjiSource = new HashSet<String>();

		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {
			alreadySetKanji.add(currentKanjiEntry.getKanji());
			alreadySetKanjiSource.add(currentKanjiEntry.getKanji());
		}

		final Map<String, Integer> kanjiCountMap = new HashMap<String, Integer>();

		final Map<String, Integer> additionalKanjiIds = new HashMap<String, Integer>();

		for (PolishJapaneseEntry currentPolishJapaneseEntry : dictionary) {

			String kanji = currentPolishJapaneseEntry.getKanji();

			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

				KanjiDic2EntryForDictionary kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);

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

					KanjiEntryForDictionary newKanjiEntry = generateKanjiEntry(currentKanjiChar, kanjiDic2Entry,
							kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);

					kanjiEntries.add(newKanjiEntry);

					additionalKanjiIds.put(currentKanjiChar, newKanjiEntry.getId());
				}
			}
		}
		
		// generate all remaining kanjis
		Iterator<String> readKanjiDic2KeySetIterator = readKanjiDic2.keySet().iterator();

		while (readKanjiDic2KeySetIterator.hasNext()) {

			String readKanjiDic2KeySetIteratorCurrentKanji = readKanjiDic2KeySetIterator.next();

			KanjiDic2EntryForDictionary kanjiDic2Entry = readKanjiDic2.get(readKanjiDic2KeySetIteratorCurrentKanji);
			
			if (alreadySetKanji.contains(readKanjiDic2KeySetIteratorCurrentKanji)) {
				continue;
			}

			alreadySetKanji.add(readKanjiDic2KeySetIteratorCurrentKanji);

			KanjiEntryForDictionary newKanjiEntry = generateKanjiEntry(readKanjiDic2KeySetIteratorCurrentKanji, kanjiDic2Entry,
					kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);

			kanjiEntries.add(newKanjiEntry);

			additionalKanjiIds.put(readKanjiDic2KeySetIteratorCurrentKanji, newKanjiEntry.getId());

			Integer kanjiCountMapInteger = kanjiCountMap.get(readKanjiDic2KeySetIteratorCurrentKanji);

			if (kanjiCountMapInteger == null) {
				kanjiCountMapInteger = new Integer(0);
			}

			kanjiCountMap.put(readKanjiDic2KeySetIteratorCurrentKanji, kanjiCountMapInteger);
		}		

		// generate additional top 2500 kanji
		/*
		Iterator<String> readKanjiDic2KeySetIterator = readKanjiDic2.keySet().iterator();

		while (readKanjiDic2KeySetIterator.hasNext()) {

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

			KanjiEntry newKanjiEntry = generateKanjiEntry(readKanjiDic2KeySetIteratorCurrentKanji, kanjiDic2Entry,
					kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);

			kanjiEntries.add(newKanjiEntry);

			additionalKanjiIds.put(readKanjiDic2KeySetIteratorCurrentKanji, newKanjiEntry.getId());

			Integer kanjiCountMapInteger = kanjiCountMap.get(readKanjiDic2KeySetIteratorCurrentKanji);

			if (kanjiCountMapInteger == null) {
				kanjiCountMapInteger = new Integer(0);
			}

			kanjiCountMap.put(readKanjiDic2KeySetIteratorCurrentKanji, kanjiCountMapInteger);
		}
		*/

		// top 2500 end

		// generate common word additional kanji
		/*
		Collection<EDictEntry> jmedictCommonValues = jmedictCommon.values();
		Iterator<EDictEntry> jmedictCommonValuesIterator = jmedictCommonValues.iterator();

		while (jmedictCommonValuesIterator.hasNext()) {

			String kanji = jmedictCommonValuesIterator.next().getKanji();

			if (kanji == null) {
				continue;
			}

			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);

				if (kanjiDic2Entry != null) {

					if (alreadySetKanjiSource.contains(currentKanjiChar) == false) {

						Integer kanjiCountMapInteger = kanjiCountMap.get(currentKanjiChar);

						if (kanjiCountMapInteger == null) {
							kanjiCountMapInteger = new Integer(0);
						}

						kanjiCountMap.put(currentKanjiChar, kanjiCountMapInteger);
					}
				}

				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}

				if (kanjiDic2Entry != null) {

					alreadySetKanji.add(currentKanjiChar);

					KanjiEntry newKanjiEntry = generateKanjiEntry(currentKanjiChar, kanjiDic2Entry,
							kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);

					kanjiEntries.add(newKanjiEntry);

					additionalKanjiIds.put(currentKanjiChar, newKanjiEntry.getId());
				}
			}
		}
		*/
		// generate common word additional kanji end

		String[] kanjiArray = new String[kanjiCountMap.size()];

		kanjiCountMap.keySet().toArray(kanjiArray);

		Arrays.sort(kanjiArray, new Comparator<String>() {

			@Override
			public int compare(String kanji1, String kanji2) {

				Integer kanji2Count = kanjiCountMap.get(kanji2);
				Integer kanji1Count = kanjiCountMap.get(kanji1);

				int compareResult = kanji2Count.compareTo(kanji1Count);

				if (compareResult != 0) {
					return compareResult;
				} else {
					return additionalKanjiIds.get(kanji1).compareTo(additionalKanjiIds.get(kanji2));
				}

				//return additionalKanjiIds.get(kanji1).compareTo(additionalKanjiIds.get(kanji2));
			}
		});

		System.out.println("\n---\n");

		for (int kanjiArrayIdx = 0; kanjiArrayIdx < kanjiArray.length /* && kanjiArrayIdx < 10 */; ++kanjiArrayIdx) {

			String currentKanji = kanjiArray[kanjiArrayIdx];

			System.out.println(currentKanji + " - " + kanjiCountMap.get(currentKanji));
		}

		System.out.println("\nKanji: " + kanjiArray.length);

		System.out.println("\n---\n");
	}

	private static KanjiEntryForDictionary generateKanjiEntry(String kanji, KanjiDic2EntryForDictionary kanjiDic2Entry, int id) {

		KanjiEntryForDictionary newKanjiEntry = new KanjiEntryForDictionary();

		newKanjiEntry.setId(id);
		newKanjiEntry.setKanji(kanji);
		newKanjiEntry.setKanjiDic2Entry(kanjiDic2Entry);

		List<String> polishTranslates = new ArrayList<String>();

		polishTranslates.add("-");

		newKanjiEntry.setPolishTranslates(polishTranslates);
		newKanjiEntry.setInfo("");

		//newKanjiEntry.setGenerated(true);

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

		newKanjiEntry.setGroups(GroupEnum.convertToListGroupEnum(groupsList));

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

	private static void generateZinniaTomoeSlimBinaryFile(List<KanjiEntryForDictionary> kanjiEntries, File kanjivgSingleXmlFile,
			String tomoeFileFromKanjivg, String zinniaLearnPath, String zinniaTomoeLearnSlimFile,
			String zinniaTomoeSlimBinaryFile) throws Exception {

		System.out.println("generateZinniaTomoeSlimBinaryFile");

		Set<String> kanjiSet = new TreeSet<String>();

		for (KanjiEntryForDictionary currentKanjiEntry : kanjiEntries) {

			String kanji = currentKanjiEntry.getKanji();

			kanjiSet.add(kanji);
		}

		//File kvgToolFileFromKanjivgFile = new File(kvgToolFileFromKanjivg);
		File tomoeFileFromKanjivgFile = new File(tomoeFileFromKanjivg);

		kanjiEntries = new ArrayList<KanjiEntryForDictionary>(kanjiEntries);

		Collections.sort(kanjiEntries, new Comparator<KanjiEntryForDictionary>() {

			@Override
			public int compare(KanjiEntryForDictionary o1, KanjiEntryForDictionary o2) {
				return o1.getKanji().compareTo(o2.getKanji());
			}
		});

		/*
		BufferedWriter tomoeFileFromKanjivgWriter = new BufferedWriter(new FileWriter(kvgToolFileFromKanjivgFile));

		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			KanjivgEntry kanjivgEntry = currentKanjiEntry.getKanjivgEntry();

			if (kanjivgEntry == null) {
				continue;
			}
			
			tomoeFileFromKanjivgWriter.write(currentKanjiEntry.getKanji());
			tomoeFileFromKanjivgWriter.write(" ");

			List<String> strokePaths = kanjivgEntry.getStrokePaths();

			for (String currentStrokePath : strokePaths) {
				tomoeFileFromKanjivgWriter.write(currentStrokePath);
				tomoeFileFromKanjivgWriter.write(";");
			}

			tomoeFileFromKanjivgWriter.write("\n");
		}

		tomoeFileFromKanjivgWriter.close();
		*/

		Runtime runtime = Runtime.getRuntime();

		Process process = runtime.exec(
				new String[] { "ruby", "xml_all_kanji_fm.rb", kanjivgSingleXmlFile.getAbsolutePath(),
						tomoeFileFromKanjivgFile.getAbsolutePath() }, null, new File("../KVG-Tools/"));

		BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		int exitVal = process.waitFor();

		System.out.println("Generate kvg tool exited with error code: " + exitVal);

		List<TomoeEntry> tomoeEntries = TomoeReader.readTomoeXmlHandwritingDatabase(tomoeFileFromKanjivgFile
				.getAbsolutePath());

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

		process = runtime.exec(new String[] { zinniaLearnPath, zinniaTomoeLearnSlimFile, zinniaTomoeSlimBinaryFile });

		stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		exitVal = process.waitFor();

		System.out.println("Generate zinnia tomoe db exited with error code: " + exitVal);

		//kvgToolFileFromKanjivgFile.delete();
		
		tomoeFileFromKanjivgFile.delete();
		
		new File(zinniaTomoeLearnSlimFile).delete();
		new File(zinniaTomoeSlimBinaryFile + ".txt").delete();
	}

	private static List<TransitiveIntransitivePair> readTransitiveIntransitivePair(
			String transitiveIntransitivePairsFileName) throws Exception {

		System.out.println("readTransitiveIntransitivePair");

		CsvReader csvReader = new CsvReader(new FileReader(transitiveIntransitivePairsFileName), ',');

		List<TransitiveIntransitivePair> transitiveIntransitivePairList = new ArrayList<TransitiveIntransitivePair>();

		while (csvReader.readRecord()) {

			// transitive
			String transitiveKanji = csvReader.get(0);
			String transitiveKana = csvReader.get(1);

			// intransitive
			String intransitiveKanji = csvReader.get(5);
			String intransitiveKana = csvReader.get(6);

			TransitiveIntransitivePair transitiveIntransitivePair = new TransitiveIntransitivePair();

			transitiveIntransitivePair.setTransitiveKanji(transitiveKanji);
			transitiveIntransitivePair.setTransitiveKana(transitiveKana);

			transitiveIntransitivePair.setIntransitiveKanji(intransitiveKanji);
			transitiveIntransitivePair.setIntransitiveKana(intransitiveKana);

			transitiveIntransitivePairList.add(transitiveIntransitivePair);
		}

		csvReader.close();

		return transitiveIntransitivePairList;
	}
		
	private static void generatePdfDictionary(List<PolishJapaneseEntry> polishJapaneseEntries, String mainTexFilename, String outputDir) throws Exception {
		
		File mainTexFile = new File(mainTexFilename);
		File outputMainTexFile = new File(outputDir, mainTexFile.getName()); 
		
		// kopiowanie glownego pliku slowniku
		FileUtils.copyFile(mainTexFile, outputMainTexFile);
		
		// uruchomienie generatora slow
		List<String> generatedLatexDictonaryEntries = LatexDictionaryGenerator.generateLatexDictonaryEntries(polishJapaneseEntries);
		
		// zapisanie wygenerowanych slow
		FileWriter dictionaryEntriesFileWriter = new FileWriter(new File(outputDir, "dictionary_entries.tex"));
		
		for (String latexString : generatedLatexDictonaryEntries) {			
			dictionaryEntriesFileWriter.write(latexString);
		}		
		
		dictionaryEntriesFileWriter.close();
		
		// W przypadku błędu:
		// ! TeX capacity exceeded, sorry [pool size=5815276]
		//
		// należy w pliku /usr/share/texmf-dist/web2c/texmf.cnf
		//
		// Zwiększyc pool_size, np:
		// pool_size = 9250000
		//
		// A następnie uruchomić z root-a:
		// fmtutil-sys --all
		
		// uruchomienie xelatex
		Runtime runtime = Runtime.getRuntime();

		Process process = runtime.exec(
				new String[] { "xelatex", "dictionary.tex" }, null, new File(outputDir));

		BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		line = null;

		while ((line = stream.readLine()) != null) {
			System.out.println(line);
		}

		int exitVal = process.waitFor();

		System.out.println("xelatex exited with error code: " + exitVal);
		
		// kasowanie niepotrzebnych plikow
		new File(outputDir, "dictionary.aux").delete();
		new File(outputDir, "dictionary.log").delete();
		new File(outputDir, "dictionary.out").delete();
		new File(outputDir, "dictionary.tex").delete();
		new File(outputDir, "dictionary_entries.tex").delete();
		new File(outputDir, "dictionary.out").delete();
	}
	
	private static void generateNamePolishJapaneseEntries(String output) throws Exception {
				
		System.out.println("generateNamePolishJapaneseEntries");
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(dictionary2Helper, dictionary2NameHelper);
		
		//
		
		int id = 0;
		
		for (PolishJapaneseEntry polishJapaneseEntry : generatedNames) {
			polishJapaneseEntry.setId(id);
			
			//id++;
		}
		
		final int maxPos = 300000;
		
		int counter = 1;
				
		List<PolishJapaneseEntry> partialPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : generatedNames) {
			
			partialPolishJapaneseEntryList.add(polishJapaneseEntry);

			if (partialPolishJapaneseEntryList.size() >= maxPos) {				
				CsvReaderWriter.generateCsv(new String[] { output + "_" + counter }, partialPolishJapaneseEntryList, true, false, true, false, null);
				
				partialPolishJapaneseEntryList.clear();
				
				counter++;
			}			
		}
		
		if (partialPolishJapaneseEntryList.size() > 0) {
			CsvReaderWriter.generateCsv(new String[] { output + "_" + counter }, partialPolishJapaneseEntryList, true, false, true, false, null);
		}
	}
}
