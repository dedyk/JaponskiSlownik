package pl.idedyk.japanese.dictionary.tools;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
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
				
		for (java.util.Map.Entry<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> japaneseIndexMapEntry : dictionaryIndex.getEntryListIndex().getJapaneseIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");
			
			for (java.util.Map.Entry<KanaRomajiKey, List<KanjiKanaPair>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntry().getEntryId());
			}
			
			System.out.println("-----------");
		}
		
		System.out.println("===================");
		
		for (java.util.Map.Entry<String, List<Map.Entry<String, List<KanjiKanaPair>>>> japaneseIndexMapEntry : dictionaryIndex.getEntryListIndex().getPolishIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<String, List<KanjiKanaPair>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");	
			
			for (java.util.Map.Entry<String, List<KanjiKanaPair>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntry().getEntryId());
			}
			
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
		
		// czesc japonska
		
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
					kana = DictionaryIndex.otherSectionName;
					romaji = DictionaryIndex.otherSectionName;
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
		
		//
		
		// generowanie sekcji, gdzie nazwa sekcji to najczesciej dwie pierwsze litery czytania
		dictionaryIndex.entryListIndex.japaneseIndexSectionMap = new TreeMap<>();
		
		for (Map.Entry<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMapEntry : dictionaryIndex.entryListIndex.japaneseIndexMap.entrySet()) {
			
			KanaRomajiKey kanaRomajiKey = japaneseIndexMapEntry.getKey();

			String section = null;
			
			if (kanaRomajiKey.kana == DictionaryIndex.otherSectionName || kanaRomajiKey.romaji == DictionaryIndex.otherSectionName) {
				section = DictionaryIndex.otherSectionName;
				
			} else {
				KanaEntry kanaEntry = kanaCache.get(kanaRomajiKey.kana.substring(0, 1));
				
				if (kanaEntry == null) {
					section = DictionaryIndex.otherSectionName;
					
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
		
		// czesc polska
		
		// sortowanie po polsku
		Collator polishCollator = Collator.getInstance(new Locale("pl", "PL"));
		polishCollator.setStrength(Collator.SECONDARY);
		
		// mapowania do generowania indeksu
		dictionaryIndex.entryListIndex.polishIndexMap = new TreeMap<>(polishCollator);
				
		// chodzimy po wszystkich slowach
		for (pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry entry : entryList) {
			
			// pobieramy tylko widoczne czytania
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
			
			// oraz chodzimy po wszystkich znaczeniach
			List<Sense> entrySenseList = entry.getSenseList();
			
			for (Sense sense : entrySenseList) {
				// pobieramy wszystkie polskie znaczenia
				List<Gloss> polishGlossList = Dictionary2HelperCommon.getPolishGlossList(sense.getGlossList());
				
				// chodzimy po wszystkich polskich znaczeniach
				for (Gloss polishGloss : polishGlossList) {
					
					String polishGlossValue = polishGloss.getValue();
					
					polishGlossValue = Normalizer.normalize(polishGlossValue, Normalizer.Form.NFKC);
										
					// jezeli byla jakas zawartosc w namiasie to usuwamy to
					polishGlossValue = polishGlossValue.replaceAll("\\s*\\([^()]*\\)", "").trim();
					polishGlossValue = polishGlossValue.replaceAll("\\[", "");
					polishGlossValue = polishGlossValue.replaceAll("\\}", "");
					polishGlossValue = polishGlossValue.replaceAll("\\'", "");
					polishGlossValue = polishGlossValue.replaceAll("\\“", "");
					polishGlossValue = polishGlossValue.replaceAll("\\”", "");
					polishGlossValue = polishGlossValue.replaceAll("\\„", "");
					polishGlossValue = polishGlossValue.replaceAll("\\_", "");
					
					polishGlossValue = polishGlossValue.replaceAll("\\,", "");
					polishGlossValue = polishGlossValue.replaceAll("\\~", "");
					polishGlossValue = polishGlossValue.replaceAll("\\〜", "");
					polishGlossValue = polishGlossValue.replaceAll("\\(", "");
					polishGlossValue = polishGlossValue.replaceAll("\\)", "");
					polishGlossValue = polishGlossValue.replaceAll("\\@", "");
					polishGlossValue = polishGlossValue.replaceAll("\\*", "");
					polishGlossValue = polishGlossValue.replaceAll("\\…", "");
										
					// jezeli zaczyna sie od znaku "-" usuwamy to
					while(true) {
						if (polishGlossValue.startsWith("-") == true) {
							polishGlossValue = polishGlossValue.substring(1);
						} else {
							break;
						}
					}
					
					// jezeli zaczyna sie od znaku "." usuwamy to
					while(true) {
						if (polishGlossValue.startsWith(".") == true) {
							polishGlossValue = polishGlossValue.substring(1);
						} else {
							break;
						}
					}
					
					if (polishGlossValue.startsWith("??") == true) {
						polishGlossValue = DictionaryIndex.otherSectionName;
					}
					
					if (polishGlossValue.startsWith("α") == true) {
						polishGlossValue = DictionaryIndex.otherSectionName;
					}

					if (polishGlossValue.startsWith("β") == true) {
						polishGlossValue = DictionaryIndex.otherSectionName;
					}
					
					if (polishGlossValue.startsWith("ß") == true) {
						polishGlossValue = DictionaryIndex.otherSectionName;
					}
					
					if (polishGlossValue.length() == 0) {
						polishGlossValue = DictionaryIndex.otherSectionName;
					}
					
					polishGlossValue = polishGlossValue.trim();
					
					// indeks dla danego klucza
					List<KanjiKanaPair> kanjiKanaPairListForPolishGlossValue = dictionaryIndex.entryListIndex.polishIndexMap.get(polishGlossValue);
					
					// gdy nie ma tworzymy wpis
					if (kanjiKanaPairListForPolishGlossValue == null) {
						kanjiKanaPairListForPolishGlossValue = new ArrayList<KanjiKanaPair>();
						
						dictionaryIndex.entryListIndex.polishIndexMap.put(polishGlossValue, kanjiKanaPairListForPolishGlossValue);
					}
					
					// dodajemy wpisy
					for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
						if (kanjiKanaPairListForPolishGlossValue.contains(kanjiKanaPair) == false) {
							kanjiKanaPairListForPolishGlossValue.add(kanjiKanaPair);
						}
					}
				}
			}
		}
		
		// grupowanie po sekcjach dla polskiego znaczenia
		dictionaryIndex.entryListIndex.polishIndexSectionMap = new TreeMap<>(polishCollator);
		
		for (Map.Entry<String, List<KanjiKanaPair>> polishIndexMapEntry : dictionaryIndex.entryListIndex.polishIndexMap.entrySet()) {
			
			String polishGlossValueKey = polishIndexMapEntry.getKey();
			String section;
			
			if (polishGlossValueKey == DictionaryIndex.otherSectionName) {
				section = DictionaryIndex.otherSectionName;
				
			} else {
				if (polishGlossValueKey.length() > 1 && isSpecialChar(polishGlossValueKey.charAt(1)) == false &&
						Character.getType(polishGlossValueKey.charAt(0))  != Character.DECIMAL_DIGIT_NUMBER && // czy to cyfra
						Character.getType(polishGlossValueKey.charAt(1))  != Character.DECIMAL_DIGIT_NUMBER) {  // czy to cyfra
					
					section = polishGlossValueKey.substring(0, 2).trim().toUpperCase();
				} else {
					section = polishGlossValueKey.substring(0, 1).trim().toUpperCase();	
				}
			}
						
			// czy taka sekcja wystepuje
			List<Map.Entry<String, List<KanjiKanaPair>>> entrySetListForSection = dictionaryIndex.entryListIndex.polishIndexSectionMap.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				dictionaryIndex.entryListIndex.polishIndexSectionMap.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(polishIndexMapEntry);
		}
		
		// czesc dla spisu slowek (potrzebne tylko dla generatora latex)
		
		// podzielenie listy na mniejsze kawalki
		dictionaryIndex.entryListIndex.entriesListGroupedBy = new TreeMap<>();
		
		String groupedByKey;
		
		for (pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry entry : entryList) {
			
			// tworzenie klucza grupowania
			if (	entry.getReadingInfoList().get(0).getKana().getRomaji() == null ||
					entry.getReadingInfoList().get(0).getKana().getRomaji().equals("") == true ||
					entry.getReadingInfoList().get(0).getKana().getRomaji().startsWith("-") == true) {
				groupedByKey = DictionaryIndex.otherSectionName;
			} else {				
				if (entry.getReadingInfoList().get(0).getKana().getRomaji().length() > 1) {
					groupedByKey = entry.getReadingInfoList().get(0).getKana().getRomaji().substring(0, 2).trim().toUpperCase();
				} else {
					groupedByKey = entry.getReadingInfoList().get(0).getKana().getRomaji().substring(0, 1).trim().toUpperCase();	
				}
			}
						
			// pobranie listy dla danej grupy
			List<JMdict.Entry> groupedByKeyEntriesList = dictionaryIndex.entryListIndex.entriesListGroupedBy.get(groupedByKey);
			
			if (groupedByKeyEntriesList == null) { // nowa grupa, tworzymy nowa
				groupedByKeyEntriesList = new ArrayList<JMdict.Entry>();
				
				dictionaryIndex.entryListIndex.entriesListGroupedBy.put(groupedByKey, groupedByKeyEntriesList);
			}
			
			// dodanie wpisu do danej grupy
			groupedByKeyEntriesList.add(entry);			
		}
	}

	private static void generateNameEntryListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		int todo = 1; // !!!!!!!!!!!
	}

	
	private static void generateKanjiListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		int todo = 1; // !!!!!!!!!!!		
	}
	
	private static boolean isSpecialChar(Character char_) {
		int type = Character.getType(char_);

		if (type == Character.DASH_PUNCTUATION ||
			type == Character.OTHER_PUNCTUATION || 
		    type == Character.START_PUNCTUATION || 
		    type == Character.END_PUNCTUATION ||
		    type == Character.MATH_SYMBOL || 
		    type == Character.MODIFIER_SYMBOL || 
		    type == Character.OTHER_SYMBOL || 
		    type == Character.CURRENCY_SYMBOL) {
		    
		    return true;
		} else {
			return false;
		}
	}

	//

	public static class DictionaryIndex {
		final static public String otherSectionName = "Inne";
		
		private EntryListIndex entryListIndex;
		
		public EntryListIndex getEntryListIndex() {
			return entryListIndex;
		}
		
		//
		
		public static class EntryListIndex {
			
			// dane zrodlowe
			private List<JMdict.Entry> entryList;
			
			// mapa ze wszystkimi unikalnymi japonskimi slowami (kana, romaji)
			private Map<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMap;
			
			// mapa ze wszystkimi sekcjami (to bedzie pierwszy znak kana w romaji lub otherSectionName (Inny)
			private Map<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> japaneseIndexSectionMap;
			
			// mapa ze wszystkimi unikalnymi polskimi znaczenia
			private Map<String, List<KanjiKanaPair>> polishIndexMap;
			
			// mapa ze wszystkimi sekcjami (to najczesciej beda dwa pierwsze znaki znaczenia)
			private Map<String, List<Map.Entry<String, List<KanjiKanaPair>>>> polishIndexSectionMap;
			
			// mapa ze spisem wszystkich slow (potrzebne tylko dla generator latex ze spisem wszystkich slow) 
			private Map<String, List<JMdict.Entry>> entriesListGroupedBy;
			
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

			public Map<String, List<KanjiKanaPair>> getPolishIndexMap() {
				return polishIndexMap;
			}

			public Map<String, List<Map.Entry<String, List<KanjiKanaPair>>>> getPolishIndexSectionMap() {
				return polishIndexSectionMap;
			}

			public Map<String, List<JMdict.Entry>> getEntriesListGroupedBy() {
				return entriesListGroupedBy;
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
