package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

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

		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		polishJapaneseEntries = Helper.generateGroups(polishJapaneseEntries, true, false);

		List<PolishJapaneseEntry> resultPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();

		int id = 1;

		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {

			if (currentPolishJapaneseEntry.isUseEntry() == false) {
				continue;
			}

			currentPolishJapaneseEntry.setId(id);

			resultPolishJapaneseEntries.add(currentPolishJapaneseEntry);

			id++;
		}

		for (PolishJapaneseEntry currentPolishJapaneseEntry : resultPolishJapaneseEntries) {

			if (currentPolishJapaneseEntry.isUseEntry() == false) {
				continue;
			}

			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();

			Set<Integer> knownDuplicatedIds = generateKnownDuplicatedIdForKanji(polishJapaneseEntries, currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji());

			for (String currentKana : kanaList) {

				generateKnownDuplicatedIdFormKanjiAndKana(knownDuplicatedIds, polishJapaneseEntries, currentPolishJapaneseEntry.getId(), currentPolishJapaneseEntry.getKanji(),
						currentKana);
			}

			currentPolishJapaneseEntry.setKnownDuplicatedId(knownDuplicatedIds);
		}

		CsvReaderWriter.generateCsv("input/word-wynik.csv", resultPolishJapaneseEntries, true);
		 */
		
		//TreeMap<String, JMEDictEntry> jmedict = JMEDictReader.readJMEdict("../JaponskiSlownik_dodatki/JMdict_e");
		
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict("/home/fmazurek/tmp2/e6/jm/test.xml");
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JaponskiSlownik_dodatki/edict_sub-utf8");
		
		/*
		System.out.println(jmedict.get(JMEDictReader.getMapKey("食べる", "たべる")));
		System.out.println(jmedict.get(JMEDictReader.getMapKey("集中", "しゅうちゅう")));
		System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "ぺらぺら")));
		*/
		//System.out.println(jmedict.get(JMEDictReader.getMapKey("開ける","あける")));
		
		//System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "コーヒー")));
		//System.out.println(jmedictCommon.get(EdictReader.getMapKey(null, "コーヒー")));
		
		System.out.println(jmedict.get(JMEDictReader.getMapKey(null, "カレー")));
		System.out.println(jmedictCommon.get(EdictReader.getMapKey(null, "カレー")));
		
		
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

	}

	/*
	private static Set<Integer> generateKnownDuplicatedIdForKanji(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, String kanji) {

		Set<Integer> result = new TreeSet<Integer>();

		if (kanji.equals("-") == true) {
			return result;
		}

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (polishJapaneseEntry.isUseEntry() == false) {
				continue;
			}

			if (polishJapaneseEntry.getId() != id && polishJapaneseEntry.getKanji().equals(kanji)) {
				result.add(polishJapaneseEntry.getId());				
			}
		}

		return result;
	}

	private static void generateKnownDuplicatedIdFormKanjiAndKana(Set<Integer> result, List<PolishJapaneseEntry> polishJapaneseKanjiEntries, int id, String kanji, String kana) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {

			if (polishJapaneseEntry.isUseEntry() == false) {
				continue;
			}

			boolean differentKanji = ! kanji.equals(polishJapaneseEntry.getKanji());

			if (kanji.equals("-") == true && polishJapaneseEntry.getKanji().equals("-") == false) {
				differentKanji = false;
			}

			if (kanji.equals("-") == false && polishJapaneseEntry.getKanji().equals("-") == true) {
				differentKanji = false;
			}

			if (polishJapaneseEntry.getId() != id && differentKanji == false && polishJapaneseEntry.getKanaList().contains(kana) == true) {
				result.add(polishJapaneseEntry.getId());				
			}
		}
	}
	 */
}
