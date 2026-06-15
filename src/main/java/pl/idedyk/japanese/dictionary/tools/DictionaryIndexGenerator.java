package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.JMnedict;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;

public class DictionaryIndexGenerator {
	
	public static void main(String[] args) throws Exception {
				
		// INFO: wersja common musi byc generowana z pelnym slownikiem, ktory wyszedl z AndroidDictionaryGenerator
		// INFO2: to tylko test		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		
		JMdict polishJMdict = dictionary2Helper.getPolishJMdict();
		
		List<JMdict.Entry> entryList = new ArrayList<>();
		
		entryList.addAll(polishJMdict.getEntryList()); // .subList(0, 10000));
		
		//
		
		DictionaryIndex dictionaryIndex = generateDictionaryIndex(entryList, null, null);
		
		//
		
		Map<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> japaneseIndexSectionMap = dictionaryIndex.getEntryListIndex().getJapaneseIndexSectionMap();
		
		for (java.util.Map.Entry<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> japaneseIndexMapEntry : japaneseIndexSectionMap.entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");			
			System.out.println("-----------");
		}
	}
	
	public static DictionaryIndex generateDictionaryIndex(List<JMdict.Entry> entryList, List<JMnedict.Entry> nameEntryList, List<KanjiCharacterInfo> kanjiList) {
		
		// index
		DictionaryIndex dictionaryIndex = new DictionaryIndex();
		
		// tworzymy indeksy
		generateEntryListIndex(dictionaryIndex, entryList);
		generateNameEntryListIndex(dictionaryIndex, entryList);
		generateKanjiListIndex(dictionaryIndex, entryList);
		
		//
		
		return dictionaryIndex;
	}

	public static void generateEntryListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		
		KanaHelper kanaHelper = new KanaHelper();
		Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache(true);
		
		// indeks slowek
		dictionaryIndex.entryListIndex = new DictionaryIndex.EntryListIndex(entryList);		
		
		// generowanie mapy ze wszystkimi japonskimi slowami
		dictionaryIndex.entryListIndex.japaneseIndexMap = new TreeMap<>();
				
		// chodzimy po wszystkich slowach
		for (pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry entry : entryList) {
			
			// pobieramy tylko widoczne czytania
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
			
			// chodzimy po wszystkich czytania i dodajemy do mapy
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				String kana = kanjiKanaPair.getKana();
				String romaji = kanjiKanaPair.getRomaji();
				
				if (romaji == null || romaji.length() == 0 || romaji.equals("-") == true) {
					kana = DictionaryIndex.EntryListIndex.otherSectionName;
					romaji = DictionaryIndex.EntryListIndex.otherSectionName;
				}
				
				// generowanie klucza do mapy
				KanaRomajiKey kanaRomajiKey = new KanaRomajiKey(kana, romaji);
				
				// indeks dla danego klucza
				List<KanjiKanaPair> kanjiKanaPairListForKanaRomajiKey = dictionaryIndex.entryListIndex.japaneseIndexMap.get(kanaRomajiKey);
				
				// gdy nie ma tworzymy wpis
				if (kanjiKanaPairListForKanaRomajiKey == null) {
					kanjiKanaPairListForKanaRomajiKey = new ArrayList<KanjiKanaPair>();
					
					dictionaryIndex.entryListIndex.japaneseIndexMap.put(kanaRomajiKey, kanjiKanaPairListForKanaRomajiKey);
				}
				
				// dodajemy wpis
				if (kanjiKanaPairListForKanaRomajiKey.contains(kanjiKanaPair) == false) {
					kanjiKanaPairListForKanaRomajiKey.add(kanjiKanaPair);
				}
			}			
		}
		
		// generowanie sekcji, gdzie nazwa sekcji to najczesciej dwie pierwsze litery czytania
		dictionaryIndex.entryListIndex.japaneseIndexSectionMap = new TreeMap<>();
		
		for (Map.Entry<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMapEntry : dictionaryIndex.entryListIndex.japaneseIndexMap.entrySet()) {
			
			KanaRomajiKey kanaRomajiKey = japaneseIndexMapEntry.getKey();

			String section = null;
			
			if (kanaRomajiKey.kana == DictionaryIndex.EntryListIndex.otherSectionName || kanaRomajiKey.romaji == DictionaryIndex.EntryListIndex.otherSectionName) {
				section = DictionaryIndex.EntryListIndex.otherSectionName;
				
			} else {
				KanaEntry kanaEntry = kanaCache.get(kanaRomajiKey.kana.substring(0, 1));
				
				if (kanaEntry == null) {
					section = DictionaryIndex.EntryListIndex.otherSectionName;
					
				} else {
					section = kanaEntry.getKana();	
				}				
			}
			
			// czy taka sekcja wystepuje
			List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>> entrySetListForSection = dictionaryIndex.entryListIndex.japaneseIndexSectionMap.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				dictionaryIndex.entryListIndex.japaneseIndexSectionMap.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(japaneseIndexMapEntry);
		}
	}

	private static void generateNameEntryListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		int todo = 1; // !!!!!!!!!!!
	}

	
	private static void generateKanjiListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		int todo = 1; // !!!!!!!!!!!		
	}

	//

	public static class DictionaryIndex {
		private EntryListIndex entryListIndex;
		
		public EntryListIndex getEntryListIndex() {
			return entryListIndex;
		}
		
		//
		
		public static class EntryListIndex {
			
			final static public String otherSectionName = "Inne";
			
			// dane zrodlowe
			private List<JMdict.Entry> entryList;
			
			// mapa ze wszystkimi unikalnymi japonskimi slowami (kana, romaji)
			private Map<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMap = new TreeMap<>();
			
			// mapa ze wszystkimi sekcjami (to bedzie pierwszy znak kana w romaji lub otherSectionName (Inny)
			private Map<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> japaneseIndexSectionMap;
			
			public EntryListIndex(List<Entry> entryList) {
				this.entryList = entryList;
			}

			public List<JMdict.Entry> getEntryList() {
				return entryList;
			}

			public Map<KanaRomajiKey, List<KanjiKanaPair>> getJapaneseIndexMap() {
				return japaneseIndexMap;
			}

			public Map<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> getJapaneseIndexSectionMap() {
				return japaneseIndexSectionMap;
			}
		}
	}
		
	public static class KanaRomajiKey implements Comparable<KanaRomajiKey> {
		private String kana;
		private String romaji;
				
		public KanaRomajiKey(String kana, String romaji) {
			this.kana = kana;
			this.romaji = romaji;
		}

		@Override
		public int compareTo(KanaRomajiKey o2) {
			String o1Kana = kana != null ? kana : "<null>";
			String o1Romaji = romaji != null ? romaji : "<null>";

			String o2Kana = o2.kana != null ? o2.kana : "<null>";
			String o2Romaji = o2.romaji != null ? o2.romaji : "<null>";

			int result = o1Romaji.compareTo(o2Romaji);
						
			if (result != 0) {
				return result;
			}
			
			result = o1Kana.compareTo(o2Kana);
			
			return result;
		}

		public String getKana() {
			return kana;
		}

		public String getRomaji() {
			return romaji;
		}

		@Override
		public int hashCode() {
			return Objects.hash(kana, romaji);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			KanaRomajiKey other = (KanaRomajiKey) obj;
			
			return Objects.equals(kana, other.kana) && Objects.equals(romaji, other.romaji);
		}
	}
}
