package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class Test {

	public static void main(String[] args) throws Exception {

		/*
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		KanaWord kanaWord = KanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, "Oosutoraria");

		System.out.println(KanaHelper.createKanaString(kanaWord));
		 */

		// TreeMap<String, EDictEntry> jmedict = EdictReader.readEdict("../JaponskiSlownik_dodatki/edict-utf8");
		/*
		System.out.println(jmedict.get(EdictReader.getMapKey("食べる", "たべる")));
		System.out.println(jmedict.get(EdictReader.getMapKey("集中", "しゅうちゅう")));
		System.out.println(jmedict.get(EdictReader.getMapKey(null, "デート")));

		System.out.println();
		 */
		/*
		TreeMap<String, EDictEntry> jmenamdict = EdictReader.readEdict("../JaponskiSlownik_dodatki/enamdict-utf8");

		Iterator<EDictEntry> iterator = jmenamdict.values().iterator();

		while(iterator.hasNext()) {
			EDictEntry edictEntry = iterator.next();

			System.out.println(edictEntry);			
		}
		 */

		/*
		// hiragana
		List<KanaEntry> hiraganaEntries = KanaHelper.getAllHiraganaKanaEntries();

		// katakana
		List<KanaEntry> katakanaEntries = KanaHelper.getAllKatakanaKanaEntries();

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		KanaWord currentKanaAsKanaAsKanaWord = KanaHelper.convertKanaStringIntoKanaWord("きんようび", hiraganaEntries, katakanaEntries);

		String currentKanaAsRomaji = KanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);

		/ *
		KanaWord kanaWord = KanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, "ken'aku");
		String kanaString = KanaHelper.createKanaString(kanaWord);
		 * /

		System.out.println(currentKanaAsRomaji);
		 */

		//TreeMap<String, JMEDictEntry> jmedict = JMEDictReader.readJMEdict("../JaponskiSlownik_dodatki/JMdict_e");

		/*
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict("/home/fmazurek/tmp2/e6/jm/test.xml");
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JaponskiSlownik_dodatki/edict_sub-utf8");
		*/
		/*
		System.out.println(jmedict.get(JMEDictReader.getMapKey("食べる", "たべる")));
		System.out.println(jmedict.get(JMEDictReader.getMapKey("集中", "しゅうちゅう")));
		System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "ぺらぺら")));
		*/
		//System.out.println(jmedict.get(JMEDictReader.getMapKey("開ける","あける")));

		//System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "コーヒー")));
		//System.out.println(jmedictCommon.get(EdictReader.getMapKey(null, "コーヒー")));

		/*
		System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "カレー")));
		System.out.println(jmedictCommon.get(EdictReader.getMapKey(null, "カレー")));
		*/

		/*
		SAXReader reader = new SAXReader();

		reader.addHandler("/JMdict/entry", new ElementHandler() {

			public void onStart(ElementPath path) {

			}

			public void onEnd(ElementPath path) {

				Element row = path.getCurrent();
				// Element rowSet = row.getParent();
				// Document document = row.getDocument();
				
				System.out.println(row.asXML());
				
				
				// prune the tree
				row.detach();
			}
		});

		reader.read(new File("../JaponskiSlownik_dodatki/JMdict_e"));
		*/
		/*
		List<?> selectNodes = document.selectNodes("/JMdict/entry/k_ele/keb");

		for (Object object : selectNodes) {

			System.out.println(object);			
		}
		 */

		//TreeMap<String, EDictEntry> jmedictName = EdictReader.readEdict("../JaponskiSlownik_dodatki/enamdict-utf8");

		/*
		Collection<EDictEntry> values = jmedictName.values();
		
		for (EDictEntry eDictEntry : values) {
			
			System.out.println(eDictEntry);
		}
		*/

		//System.out.println(jmedictName.get(EdictReader.getMapKey("誠", "まこと")));

		//TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader
		//		.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		/*
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader
						.readJMEdict("../JapaneseDictionary_additional/JMdict_e_TEST");

		List<JMEDictEntry> list = jmedict.get(JMEDictReader.getMapKey("一週間", "いっしゅうかん"));

		System.out.println(list);
		*/
		
		/*
		KanaHelper kanaHelper = new KanaHelper();

		//String word = "ハーモニカをふく";
		
		// hiragana
		List<KanaEntry> hiraganaEntries = kanaHelper.getAllHiraganaKanaEntries();

		// katakana
		List<KanaEntry> katakanaEntries = kanaHelper.getAllKatakanaKanaEntries();

		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : katakanaEntries) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		System.out.println("checkAndSavePolishJapaneseEntries: parsePolishJapaneseEntriesFromCsv");

		//KanaWord kanaWord = KanaHelper.convertKanaStringIntoKanaWord(word, hiraganaEntries, katakanaEntries);

		//System.out.println(KanaHelper.createRomajiString(kanaWord));

		// parse csv
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		// validate

		System.out.println("checkAndSavePolishJapaneseEntries: validatePolishJapaneseEntries");
		Validator.validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries, null, null);
		*/
		
		// List<RadicalInfo> radicalList = KanjiDic2Reader.readRadkfile("../JapaneseDictionary_additional/radkfile");
		
		// radicalList.getClass();		
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			List<String> translates = polishJapaneseEntry.getTranslates();
			
			LinkedHashSet<String> translatesSet = new LinkedHashSet<String>(translates);
			
			polishJapaneseEntry.setTranslates(new ArrayList<String>(translatesSet));;
		}
		
		CsvReaderWriter.generateCsv("input/word-new.csv", polishJapaneseEntries, true, true, false);
	}
}
