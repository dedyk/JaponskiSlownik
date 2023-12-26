package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class Test5 {

	public static void main(String[] args) throws Exception {

		// JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		// List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");

		/*
		List<JMEDictNewNativeEntry> jmedictNativeTestList = new ArrayList<JMEDictNewNativeEntry>();

		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {

			if (jmeDictNewNativeEntry.getEnt_seq().intValue() == 5000037) {

				jmedictNativeTestList.add(jmeDictNewNativeEntry);

				System.out.println(jmeDictNewNativeEntry);
			}
		}
		
		System.out.println();
		*/
		
		// JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
				
		/*
		List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList("水瓜", "すいか");
		
		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
		
		//List<GroupEntry> groupEntryList = jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList("月立ち", "つきたち");
		
		/*
		List<Group> groupList = jmeNewDictionary.getGroupList("正身", "そうじみ");
		
		for (Group group : groupList) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {							
				printGroupEntry(groupEntry);
			}
		}
		*/
				
		/*
		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
		
		
		/*
		for (Group group : jmeNewDictionary.getGroupList()) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				printGroupEntry(groupEntry);
			}
		}
		*/
		
		/*
		List<GroupEntry> groupEntryList = jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList("大社", "おおやしろ");

		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
		
		//List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" });
		
		/*
		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, true);
		
		FileOutputStream outputPowerStream = new FileOutputStream(new File("output/word-power-TEST.csv"));

		CsvReaderWriter.generateWordPowerCsv(outputPowerStream, jmeNewDictionary, polishJapaneseEntries);
		*/
		
		/*
		TreeSet<String> uniquePolishTranslatesTreeSet = new TreeSet<String>();
		TreeSet<String> uniquePolishTranslatesHashSet = new TreeSet<String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			List<String> translates = polishJapaneseEntry.getTranslates();
			
			for (String currentTranslate : translates) {
				uniquePolishTranslatesTreeSet.add(currentTranslate);
				uniquePolishTranslatesHashSet.add(currentTranslate);
			}
		}
		
		System.out.println(uniquePolishTranslatesTreeSet.size());
		System.out.println(uniquePolishTranslatesHashSet.size());
		*/
		
		/*
		String a = "\u305b\u304d";
		String b = "\u3059\u308b";
		
		System.out.println(a + " - " + a.hashCode());
		System.out.println(b + " - " + b.hashCode());
		*/
		
		/*
		KanaHelper kanaHelper = new KanaHelper();
		
		final Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache();
		
		String lhsRomaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(
				"ー", kanaCache, true));
		
		System.out.println(lhsRomaji);
		*/
		
		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" });
		
		List<DictionaryEntryGroup> generateWordGroupList = AndroidDictionaryGenerator.generateWordGroups(polishJapaneseEntries);
		for (DictionaryEntryGroup dictionaryEntryGroup : generateWordGroupList) {
			
			System.out.println(dictionaryEntryGroup.getId() + " - " + dictionaryEntryGroup.getDictionaryEntryList());
			
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File("/tmp/a/word_group.csv"));
		
		CsvReaderWriter.generateWordGroupCsv(outputStream, generateWordGroupList);
		*/
		
		// List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" });
		
		/*
		for (int i = 0; i < 50; ++i) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(i);
			
			System.out.format("\\entry{%s}{%s}{%s}{%s}\n\n", currentPolishJapaneseEntry.getKanji(), currentPolishJapaneseEntry.getKana(), "test", currentPolishJapaneseEntry.getTranslates().get(0));
		}
		*/
		
		/*
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		//
		
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");

		//
		
		Helper.generateAdditionalInfoFromEdict(jmeNewDictionary, jmedictCommon, polishJapaneseEntries);
		*/
		
		/*
		JSONArray jsonArray = JSONReaderWriter.createDictionaryOutputJSON(jmeNewDictionary, polishJapaneseEntries);
				
		JSONReaderWriter.writeJSONArrayToFile(new File("/tmp/a/test.json"), jsonArray);
		*/
		
		/*
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			if (polishJapaneseEntry.getId() != 21817) {
				continue;
			}
			
			//Integer polishJapaneseEntryGroupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
			
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryListInOnlyKana(kana);
			
			if (groupEntryList != null && groupEntryList.size() > 0 && JMENewDictionary.isMultiGroup(groupEntryList) == true) {
				
				for (GroupEntry groupEntry : groupEntryList) {
					
					System.out.println(groupEntry.getTranslateList());
					
				}
			}			
		}
		*/
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		/*
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
		
		JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
			
			if (groupEntryList == null) {
				continue;
			}
			
			boolean multiGroup = JMENewDictionary.isMultiGroup(groupEntryList);
			
			if (multiGroup == true) {
				System.out.println(polishJapaneseEntry.getId());
			}			
		}
		*/
		
		List<KanjiEntryForDictionary> kanjiEntries = wordGeneratorHelper.getKanjiEntries();
		
		final Map<String, Integer> kanjiFreqMap = new TreeMap<String, Integer>();
		
		for (KanjiEntryForDictionary kanjiEntryForDictionary : kanjiEntries) {
			
			KanjiDic2Entry kanjiDic2Entry = kanjiEntryForDictionary.getKanjiDic2Entry();
			
			if (kanjiDic2Entry == null) {
				continue;
			}
			
			Integer freq = kanjiDic2Entry.getFreq();
			
			if (freq == null) {
				continue;
			}
			
			kanjiFreqMap.put(kanjiDic2Entry.getKanji(), freq);
		}
		
		List<String> kanjiList = new ArrayList<String>(kanjiFreqMap.keySet());
		
		Collections.sort(kanjiList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return kanjiFreqMap.get(o1).compareTo(kanjiFreqMap.get(o2));
			}
		});	
		
		for (String kanji : kanjiList) {			
			System.out.println(kanji + "," + kanjiFreqMap.get(kanji));			
		}
	}
	
	/*
	private static void printGroupEntry(GroupEntry groupEntry) {

		Set<String> wordTypeList = groupEntry.getWordTypeList();
		
		String kanji = groupEntry.getKanji();
		List<String> kanjiInfoList = groupEntry.getKanjiInfoList();
		
		String kana = groupEntry.getKana();
		List<String> kanaInfoList = groupEntry.getKanaInfoList();
		
		String romaji = groupEntry.getRomaji();
		
		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();		
		
		System.out.println("WordTypeList: " + wordTypeList);
		
		System.out.println("Kanji: " + kanji);
		System.out.println("KanjiInfoList: " + kanjiInfoList);
		
		System.out.println("Kana: " + kana);
		System.out.println("KanaInfoList: " + kanaInfoList);
		
		System.out.println("Romaji: " + romaji);
		
		System.out.println("TranslateList: " + translateList);
		
		System.out.println("---\n");		
	}
	*/
}
