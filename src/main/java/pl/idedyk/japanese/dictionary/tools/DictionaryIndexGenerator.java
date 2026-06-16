package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections4.ListUtils;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.DictionaryIndexGenerator.DictionaryIndex.EntryListIndex;
import pl.idedyk.japanese.dictionary.tools.DictionaryIndexGenerator.DictionaryIndex.KanjiKanaPairWrapper;
import pl.idedyk.japanese.dictionary.tools.DictionaryIndexGenerator.DictionaryIndex.NameEntryListIndex;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2NameHelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2NameHelperCommon.NameKanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2NameHelper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.EntryIndex;
import pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.SectionEntry;
import pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.SectionEntryIndexEntry;
import pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.SectionIndex;
import pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.SectionIndexMetadata;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.JMnedict;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.TranslationalInfo;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.TranslationalInfoTransDet;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class DictionaryIndexGenerator {
	
	public static void main(String[] args) throws Exception {
				
		// INFO: wersja common musi byc generowana z pelnym slownikiem, ktory wyszedl z AndroidDictionaryGenerator
		// INFO2: to tylko test		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		JMdict polishJMdict = dictionary2Helper.getPolishJMdict();
		
		List<JMdict.Entry> entryList = new ArrayList<>();		
		entryList.addAll(polishJMdict.getEntryList()); //.subList(0, 10000));
		
		//
		
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();		
		JMnedict jmnedict = dictionary2NameHelper.getJMnedict();
		
		List<JMnedict.Entry> nameEntryList = new ArrayList<>();
		nameEntryList.addAll(jmnedict.getEntryList()); //.subList(0, 10000));
		
		//
		
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		Kanjidic2 kanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();
		
		List<KanjiCharacterInfo> kanjiList = new ArrayList<>(kanjidic2.getCharacterList()); //.stream().filter(f -> f.getId() == 13113).collect(Collectors.toList()));
		
		//
		
		DictionaryIndex dictionaryIndex = generateDictionaryIndex(entryList, nameEntryList, kanjiList);
		
		//
		
		for (java.util.Map.Entry<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> japaneseIndexMapEntry : dictionaryIndex.getEntryListIndex().getJapaneseIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");
			
			for (java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntryId());
			}
			
			System.out.println("-----------");
		}
		
		System.out.println("===================");
		
		for (java.util.Map.Entry<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> japaneseIndexMapEntry : dictionaryIndex.getEntryListIndex().getPolishIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");	
			
			for (java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntryId());
			}
			
			System.out.println("-----------");
		}
		
		System.out.println("===================");
		
		for (java.util.Map.Entry<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> japaneseIndexMapEntry : dictionaryIndex.getNameEntryListIndex().getJapaneseIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");
			
			for (java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntryId());
			}
			
			System.out.println("-----------");
		}
		
		System.out.println("===================");
		
		for (java.util.Map.Entry<String, List<Map.Entry<String, List<KanjiKanaPairWrapper>>>> japaneseIndexMapEntry : dictionaryIndex.getNameEntryListIndex().getTranslateIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<String, List<KanjiKanaPairWrapper>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");	
			
			for (java.util.Map.Entry<String, List<KanjiKanaPairWrapper>> currentSectionItem : sectionItemList) {
				System.out.println("\t" + currentSectionItem.getValue().get(0).getEntryId());
			}
			
			System.out.println("-----------");
		}

		System.out.println("===================");
		
		for (java.util.Map.Entry<String, List<Map.Entry<String, List<KanjiCharacterInfo>>>> japaneseIndexMapEntry : dictionaryIndex.getKanjiCharacterInfoListIndex().getTranslateIndexSectionMap().entrySet()) {
			
			String section = japaneseIndexMapEntry.getKey();
			List<java.util.Map.Entry<String, List<KanjiCharacterInfo>>> sectionItemList = japaneseIndexMapEntry.getValue();
			
			System.out.println(section + " (" + sectionItemList.size() + ")");	
			
			for (java.util.Map.Entry<String, List<KanjiCharacterInfo>> currentSectionItem : sectionItemList) {
				for (KanjiCharacterInfo characterInbfo : currentSectionItem.getValue()) {
					System.out.println("\t" + characterInbfo.getId());
				}
			}
			
			System.out.println("-----------");
		}
		
		//
		
		DictionaryIndexGenerator.saveAsDictionaryIndexConfigXml(dictionaryIndex, new File("/home/fmazurek/tmp/a/index"));
	}
	
	public static DictionaryIndex generateDictionaryIndex(List<JMdict.Entry> entryList, List<JMnedict.Entry> nameEntryList, List<KanjiCharacterInfo> kanjiList) {
		
		// index
		DictionaryIndex dictionaryIndex = new DictionaryIndex();
		
		// tworzymy indeksy
		generateEntryListIndex(dictionaryIndex, entryList);
		generateNameEntryListIndex(dictionaryIndex, nameEntryList);
		generateKanjiListIndex(dictionaryIndex, kanjiList);
		
		//
		
		return dictionaryIndex;
	}

	public static void generateEntryListIndex(DictionaryIndex dictionaryIndex, List<Entry> entryList) {
		
		if (entryList == null) {
			return;
		}
		
		// INFO: gdy cos tutaj zmieniasz zmienic rowniez w generateNameEntryListIndex
		
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
				KanaRomajiPolishWordKey kanaRomajiKey = new KanaRomajiPolishWordKey(kana, romaji);
				
				// indeks dla danego klucza
				List<KanjiKanaPairWrapper> kanjiKanaPairListForKanaRomajiKey = dictionaryIndex.entryListIndex.japaneseIndexMap.get(kanaRomajiKey);
				
				// gdy nie ma tworzymy wpis
				if (kanjiKanaPairListForKanaRomajiKey == null) {
					kanjiKanaPairListForKanaRomajiKey = new ArrayList<KanjiKanaPairWrapper>();
					
					dictionaryIndex.entryListIndex.japaneseIndexMap.put(kanaRomajiKey, kanjiKanaPairListForKanaRomajiKey);
				}
				
				// dodajemy wpis
				kanjiKanaPairListForKanaRomajiKey.add(new KanjiKanaPairWrapper(kanjiKanaPair));
			}			
		}
		
		//
		
		// generowanie sekcji, gdzie nazwa sekcji to najczesciej dwie pierwsze litery czytania
		dictionaryIndex.entryListIndex.japaneseIndexSectionMap = new TreeMap<>();
		
		for (Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> japaneseIndexMapEntry : dictionaryIndex.entryListIndex.japaneseIndexMap.entrySet()) {
			
			KanaRomajiPolishWordKey kanaRomajiKey = japaneseIndexMapEntry.getKey();

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
			List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> entrySetListForSection = dictionaryIndex.entryListIndex.japaneseIndexSectionMap.get(section);
			
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
		dictionaryIndex.entryListIndex.polishIndexMap = new TreeMap<>(new Comparator<KanaRomajiPolishWordKey>() {

			@Override
			public int compare(KanaRomajiPolishWordKey o1, KanaRomajiPolishWordKey o2) {
				return polishCollator.compare(o1.getPolishWord(), o2.getPolishWord());
			}
		});
		
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
					
					KanaRomajiPolishWordKey kanaRomajiPolishWordKey = new KanaRomajiPolishWordKey(normalizeGlossValue(polishGloss.getValue()));
										
					// indeks dla danego klucza
					List<KanjiKanaPairWrapper> kanjiKanaPairListForPolishGlossValue = dictionaryIndex.entryListIndex.polishIndexMap.get(kanaRomajiPolishWordKey);
					
					// gdy nie ma tworzymy wpis
					if (kanjiKanaPairListForPolishGlossValue == null) {
						kanjiKanaPairListForPolishGlossValue = new ArrayList<KanjiKanaPairWrapper>();
						
						dictionaryIndex.entryListIndex.polishIndexMap.put(kanaRomajiPolishWordKey, kanjiKanaPairListForPolishGlossValue);
					}
					
					// dodajemy wpisy
					for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
						kanjiKanaPairListForPolishGlossValue.add(new KanjiKanaPairWrapper(kanjiKanaPair));
					}
				}
			}
		}
		
		// grupowanie po sekcjach dla polskiego znaczenia
		dictionaryIndex.entryListIndex.polishIndexSectionMap = new TreeMap<>(polishCollator);
		
		for (Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> polishIndexMapEntry : dictionaryIndex.entryListIndex.polishIndexMap.entrySet()) {
			
			KanaRomajiPolishWordKey kanaRomajiPolishWordKey = polishIndexMapEntry.getKey();
			String section;
			
			if (kanaRomajiPolishWordKey.getPolishWord() == DictionaryIndex.otherSectionName) {
				section = DictionaryIndex.otherSectionName;
				
			} else {
				if (kanaRomajiPolishWordKey.getPolishWord().length() > 1 && isSpecialChar(kanaRomajiPolishWordKey.getPolishWord().charAt(1)) == false &&
						Character.getType(kanaRomajiPolishWordKey.getPolishWord().charAt(0))  != Character.DECIMAL_DIGIT_NUMBER && // czy to cyfra
						Character.getType(kanaRomajiPolishWordKey.getPolishWord().charAt(1))  != Character.DECIMAL_DIGIT_NUMBER) {  // czy to cyfra
					
					section = kanaRomajiPolishWordKey.getPolishWord().substring(0, 2).trim().toLowerCase();
				} else {
					section = kanaRomajiPolishWordKey.getPolishWord().substring(0, 1).trim().toLowerCase();	
				}
			}
						
			// czy taka sekcja wystepuje
			List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> entrySetListForSection = dictionaryIndex.entryListIndex.polishIndexSectionMap.get(section);
			
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
					groupedByKey = entry.getReadingInfoList().get(0).getKana().getRomaji().substring(0, 2).trim().toLowerCase();
				} else {
					groupedByKey = entry.getReadingInfoList().get(0).getKana().getRomaji().substring(0, 1).trim().toLowerCase();	
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
	
	private static String normalizeGlossValue(String glossValue) {
		
		glossValue = Normalizer.normalize(glossValue, Normalizer.Form.NFKC);
		
		// jezeli byla jakas zawartosc w namiasie to usuwamy to
		glossValue = glossValue.replaceAll("\\s*\\([^()]*\\)", "").trim();
		glossValue = glossValue.replaceAll("\\[", "");
		glossValue = glossValue.replaceAll("\\}", "");
		glossValue = glossValue.replaceAll("\\'", "");
		glossValue = glossValue.replaceAll("\\“", "");
		glossValue = glossValue.replaceAll("\\”", "");
		glossValue = glossValue.replaceAll("\\„", "");
		glossValue = glossValue.replaceAll("\\_", "");
		
		glossValue = glossValue.replaceAll("\\,", "");
		glossValue = glossValue.replaceAll("\\~", "");
		glossValue = glossValue.replaceAll("\\〜", "");
		glossValue = glossValue.replaceAll("\\(", "");
		glossValue = glossValue.replaceAll("\\)", "");
		glossValue = glossValue.replaceAll("\\@", "");
		glossValue = glossValue.replaceAll("\\*", "");
		glossValue = glossValue.replaceAll("\\…", "");
							
		// jezeli zaczyna sie od znaku "-" usuwamy to
		while(true) {
			if (glossValue.startsWith("-") == true) {
				glossValue = glossValue.substring(1);
			} else {
				break;
			}
		}
		
		// jezeli zaczyna sie od znaku "." usuwamy to
		while(true) {
			if (glossValue.startsWith(".") == true) {
				glossValue = glossValue.substring(1);
			} else {
				break;
			}
		}
		
		if (glossValue.startsWith("?") == true) {
			glossValue = DictionaryIndex.otherSectionName;
		}

		if (glossValue.startsWith("%") == true) {
			glossValue = DictionaryIndex.otherSectionName;
		}

		if (glossValue.startsWith("α") == true) {
			glossValue = DictionaryIndex.otherSectionName;
		}

		if (glossValue.startsWith("β") == true) {
			glossValue = DictionaryIndex.otherSectionName;
		}
		
		if (glossValue.startsWith("ß") == true) {
			glossValue = DictionaryIndex.otherSectionName;
		}
		
		if (glossValue.length() == 0) {
			glossValue = DictionaryIndex.otherSectionName;
		}
		
		glossValue = glossValue.trim();

		return glossValue;		
	}

	private static void generateNameEntryListIndex(DictionaryIndex dictionaryIndex, List<JMnedict.Entry> nameEntryList) {
		
		if (nameEntryList == null) {
			return;
		}
		
		// INFO: gdy cos tutaj zmieniasz zmienic rowniez w generateEntryListIndex
		
		KanaHelper kanaHelper = new KanaHelper();
		Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache(true);
		
		// indeks slowek
		dictionaryIndex.nameEntryListIndex = new DictionaryIndex.NameEntryListIndex(nameEntryList);		
		
		// czesc japonska
		
		// generowanie mapy ze wszystkimi japonskimi slowami
		dictionaryIndex.nameEntryListIndex.japaneseIndexMap = new TreeMap<>();
				
		// chodzimy po wszystkich slowach
		for (JMnedict.Entry entry : nameEntryList) {
			
			// pobieramy tylko widoczne czytania
			List<NameKanjiKanaPair> nameKanjiKanaPairList = Dictionary2NameHelperCommon.getNameKanjiKanaPairListStatic(entry);
			
			// chodzimy po wszystkich czytania i dodajemy do mapy
			for (NameKanjiKanaPair nameKanjiKanaPair : nameKanjiKanaPairList) {
				String kana = nameKanjiKanaPair.getKana();
				String romaji = nameKanjiKanaPair.getRomaji();
				
				if (kana == null || kana.length() == 0 || kana.equals("-") == true) {
					kana = DictionaryIndex.otherSectionName;
					romaji = DictionaryIndex.otherSectionName;
				}
				
				// generowanie klucza do mapy
				KanaRomajiPolishWordKey kanaRomajiKey = new KanaRomajiPolishWordKey(kana, romaji);
				
				// indeks dla danego klucza
				List<KanjiKanaPairWrapper> nameKanjiKanaPairListForKanaRomajiKey = dictionaryIndex.nameEntryListIndex.japaneseIndexMap.get(kanaRomajiKey);
				
				// gdy nie ma tworzymy wpis
				if (nameKanjiKanaPairListForKanaRomajiKey == null) {
					nameKanjiKanaPairListForKanaRomajiKey = new ArrayList<KanjiKanaPairWrapper>();
					
					dictionaryIndex.nameEntryListIndex.japaneseIndexMap.put(kanaRomajiKey, nameKanjiKanaPairListForKanaRomajiKey);
				}
				
				// dodajemy wpis
				nameKanjiKanaPairListForKanaRomajiKey.add(new KanjiKanaPairWrapper(nameKanjiKanaPair));
			}			
		}
		
		//
		
		// generowanie sekcji, gdzie nazwa sekcji to najczesciej dwie pierwsze litery czytania
		dictionaryIndex.nameEntryListIndex.japaneseIndexSectionMap = new TreeMap<>();
		
		for (Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> japaneseIndexMapEntry : dictionaryIndex.nameEntryListIndex.japaneseIndexMap.entrySet()) {
			
			KanaRomajiPolishWordKey kanaRomajiKey = japaneseIndexMapEntry.getKey();

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
			List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> entrySetListForSection = dictionaryIndex.nameEntryListIndex.japaneseIndexSectionMap.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				dictionaryIndex.nameEntryListIndex.japaneseIndexSectionMap.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(japaneseIndexMapEntry);
		}
		
		// czesc polska
		
		// sortowanie po polsku
		Collator polishCollator = Collator.getInstance(new Locale("pl", "PL"));
		polishCollator.setStrength(Collator.SECONDARY);
		
		// mapowania do generowania indeksu
		dictionaryIndex.nameEntryListIndex.translateIndexMap = new TreeMap<>(polishCollator);
				
		// chodzimy po wszystkich slowach
		for (JMnedict.Entry entry : nameEntryList) {
			
			// pobieramy tylko widoczne czytania
			List<NameKanjiKanaPair> nameKanjiKanaPairList = Dictionary2NameHelperCommon.getNameKanjiKanaPairListStatic(entry);
			
			// oraz chodzimy po wszystkich znaczeniach
			List<TranslationalInfo> translationalInfoList = entry.getTranslationInfo();
			
			for (TranslationalInfo translationalInfo : translationalInfoList) {
				// pobieramy wszystkie polskie znaczenia (jezeli istnieja)
				List<TranslationalInfoTransDet> englishOrPolishTranslationalInfoTransDetList = Dictionary2NameHelperCommon.getEnglishOrPolishTranslationalInfoTransDet(translationalInfo.getTransDet());
								
				// chodzimy po wszystkich polskich znaczeniach
				for (TranslationalInfoTransDet translationalInfoTransDet : englishOrPolishTranslationalInfoTransDetList) {
					
					String translationalInfoTransDetValue = normalizeGlossValue(translationalInfoTransDet.getValue());
										
					// indeks dla danego klucza
					List<KanjiKanaPairWrapper> kanjiKanaPairListForTranslationalInfoTransDetValue = dictionaryIndex.nameEntryListIndex.translateIndexMap.get(translationalInfoTransDetValue);
					
					// gdy nie ma tworzymy wpis
					if (kanjiKanaPairListForTranslationalInfoTransDetValue == null) {
						kanjiKanaPairListForTranslationalInfoTransDetValue = new ArrayList<KanjiKanaPairWrapper>();
						
						dictionaryIndex.nameEntryListIndex.translateIndexMap.put(translationalInfoTransDetValue, kanjiKanaPairListForTranslationalInfoTransDetValue);
					}
					
					// dodajemy wpisy
					for (NameKanjiKanaPair nameKanjiKanaPair : nameKanjiKanaPairList) {
						kanjiKanaPairListForTranslationalInfoTransDetValue.add(new KanjiKanaPairWrapper(nameKanjiKanaPair));
					}
				}
			}
		}
		
		// grupowanie po sekcjach dla polskiego znaczenia
		dictionaryIndex.nameEntryListIndex.translateIndexSectionMap = new TreeMap<>(polishCollator);
		
		for (Map.Entry<String, List<KanjiKanaPairWrapper>> polishIndexMapEntry : dictionaryIndex.nameEntryListIndex.translateIndexMap.entrySet()) {
			
			String translationalInfoTransDetValueKey = polishIndexMapEntry.getKey();
			String section;
			
			if (translationalInfoTransDetValueKey == DictionaryIndex.otherSectionName) {
				section = DictionaryIndex.otherSectionName;
				
			} else {
				if (translationalInfoTransDetValueKey.length() > 1 && isSpecialChar(translationalInfoTransDetValueKey.charAt(1)) == false &&
						Character.getType(translationalInfoTransDetValueKey.charAt(0))  != Character.DECIMAL_DIGIT_NUMBER && // czy to cyfra
						Character.getType(translationalInfoTransDetValueKey.charAt(1))  != Character.DECIMAL_DIGIT_NUMBER) {  // czy to cyfra
					
					section = translationalInfoTransDetValueKey.substring(0, 2).trim().toLowerCase();
				} else {
					section = translationalInfoTransDetValueKey.substring(0, 1).trim().toLowerCase();	
				}
			}
						
			// czy taka sekcja wystepuje
			List<Map.Entry<String, List<KanjiKanaPairWrapper>>> entrySetListForSection = dictionaryIndex.nameEntryListIndex.translateIndexSectionMap.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				dictionaryIndex.nameEntryListIndex.translateIndexSectionMap.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(polishIndexMapEntry);
		}
	}
	
	private static void generateKanjiListIndex(DictionaryIndex dictionaryIndex, List<KanjiCharacterInfo> kanjiList) {
		
		if (kanjiList == null) {
			return;
		}
		
		// indeks slowek
		dictionaryIndex.kanjiCharacterInfoListIndex = new DictionaryIndex.KanjiCharacterInfoListIndex(kanjiList);		
				
		// czesc polska
		
		// sortowanie po polsku
		Collator polishCollator = Collator.getInstance(new Locale("pl", "PL"));
		polishCollator.setStrength(Collator.SECONDARY);
		
		// mapowania do generowania indeksu
		dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexMap = new TreeMap<>(polishCollator);
				
		// chodzimy po wszystkich slowach
		for (KanjiCharacterInfo kanjiCharacterInfo : kanjiList) {
			
			// pobieramy polskie tlumaczenia
			List<String> polishTranslateList = pl.idedyk.japanese.dictionary.api.dictionary.Utils.getPolishTranslates(kanjiCharacterInfo);
			
			if (polishTranslateList.size() == 0) { // jezeli nie ma polskiego znaczenia to tworzymy wirtualny
				polishTranslateList = Arrays.asList("?");
			}
			
			for (String polishTranslate : polishTranslateList) {
				
				// normalizacja zapisu
				polishTranslate = normalizeGlossValue(polishTranslate);
				
				// indeks dla danego klucza
				List<KanjiCharacterInfo> kanjiCharacterInfoForPolishTranslateList = dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexMap.get(polishTranslate);
				
				// gdy nie ma tworzymy wpis
				if (kanjiCharacterInfoForPolishTranslateList == null) {
					kanjiCharacterInfoForPolishTranslateList = new ArrayList<KanjiCharacterInfo>();
					
					dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexMap.put(polishTranslate, kanjiCharacterInfoForPolishTranslateList);
				}
				
				// dodajemy wpis
				if (kanjiCharacterInfoForPolishTranslateList.contains(kanjiCharacterInfo) == false) {
					kanjiCharacterInfoForPolishTranslateList.add(kanjiCharacterInfo);
				}
			}
		}
		
		// grupowanie po sekcjach dla polskiego znaczenia
		dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexSectionMap = new TreeMap<>(polishCollator);
		
		for (Map.Entry<String, List<KanjiCharacterInfo>> polishIndexMapEntry : dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexMap.entrySet()) {
			
			String polishTranslateKey = polishIndexMapEntry.getKey();
			String section;
			
			if (polishTranslateKey == DictionaryIndex.otherSectionName) {
				section = DictionaryIndex.otherSectionName;
				
			} else {
				if (polishTranslateKey.length() > 1 && isSpecialChar(polishTranslateKey.charAt(1)) == false &&
						Character.getType(polishTranslateKey.charAt(0))  != Character.DECIMAL_DIGIT_NUMBER && // czy to cyfra
						Character.getType(polishTranslateKey.charAt(1))  != Character.DECIMAL_DIGIT_NUMBER) {  // czy to cyfra
					
					section = polishTranslateKey.substring(0, 2).trim().toLowerCase();
				} else {
					section = polishTranslateKey.substring(0, 1).trim().toLowerCase();	
				}
			}
						
			// czy taka sekcja wystepuje
			List<Map.Entry<String, List<KanjiCharacterInfo>>> entrySetListForSection = dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexSectionMap.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				dictionaryIndex.kanjiCharacterInfoListIndex.translateIndexSectionMap.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(polishIndexMapEntry);
		}		
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
	
	public static void saveAsDictionaryIndexConfigXml(DictionaryIndex dictionaryIndex, File outputDirectory) throws Exception {
		
		pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.DictionaryIndex dictionaryIndexXml = new pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.DictionaryIndex();
		
		// przetworzenie entryListIndex
		EntryListIndex entryListIndex = dictionaryIndex.getEntryListIndex();
		
		if (entryListIndex != null) {			
			dictionaryIndexXml.setEntryIndex(new EntryIndex());
			
			// indeks japonskich slowek
			createIndexSectionMap(outputDirectory, entryListIndex.getJapaneseIndexSectionMap(), "entryIndex", "japaneseIndexSectionIndex",
					(s) -> dictionaryIndexXml.getEntryIndex().getJapaneseIndexSectionIndex().add(s)); 

			// indeks polskich slowek
			createIndexSectionMap(outputDirectory, entryListIndex.getPolishIndexSectionMap(), "entryIndex", "polishIndexSectionIndex",
					(s) -> dictionaryIndexXml.getEntryIndex().getPolishIndexSectionIndex().add(s)); 			
		}
		
		// przetworzenie nameEntryListIndex
		NameEntryListIndex nameEntryListIndex = dictionaryIndex.getNameEntryListIndex();
		
		if (nameEntryListIndex != null) {
			dictionaryIndexXml.setNameEntryIndex(new EntryIndex());
			
			// indeks japonskich slowek
			createIndexSectionMap(outputDirectory, nameEntryListIndex.getJapaneseIndexSectionMap(), "nameEntryIndex", "japaneseIndexSectionIndex",
					(s) -> dictionaryIndexXml.getNameEntryIndex().getJapaneseIndexSectionIndex().add(s)); 			
		}
		
		// zapis ogolnego spisu
		File dictionaryIndexFile = new File(outputDirectory, "dictionaryindex.xml");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(pl.idedyk.japanese.dictionary2.dictionaryindex.xsd.DictionaryIndex.class);              
		
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		jaxbMarshaller.marshal(dictionaryIndexXml, dictionaryIndexFile);
	}
	
	private static void createIndexSectionMap(File outputDirectory, 
			Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> sectionMap,
			String mainIndexName, String sectionIndexName,
			Consumer<SectionIndexMetadata> sectionIndexMetadataAdder) throws JAXBException {
		
		final int MAX_SECTION_SIZE = 1000;
		
		Set<java.util.Map.Entry<String, List<java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>>> sectionMapEntrySet = sectionMap.entrySet();
		
		for (Map.Entry<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> sectionMapEntry : sectionMapEntrySet) {
			
			SectionIndex sectionIndex = new SectionIndex();
			
			String sectionName = sectionMapEntry.getKey();
			List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>> sectionMapEntryList = sectionMapEntry.getValue();
			
			// podzielenie listy na mniejsze kawalki
			List<List<java.util.Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> sectionMapEntryListPartitionList = ListUtils.partition(sectionMapEntryList, MAX_SECTION_SIZE);
			
			for (int partitionNo = 0; partitionNo < sectionMapEntryListPartitionList.size(); partitionNo++) {
				
				// nazwa sekcji
				sectionIndex.setSectionName(sectionName);
				sectionIndex.setPartNo(partitionNo + 1);
				
				// zawartosc sekcji				
				for (Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> sectionMapEntryListEntry : sectionMapEntryList) {
					
					// utworzenie sekcji, ktora zapisujemy do pliku
					SectionEntry sectionEntry = new SectionEntry();
					
					sectionEntry.setKana(sectionMapEntryListEntry.getKey().getKana());
					sectionEntry.setRomaji(sectionMapEntryListEntry.getKey().getRomaji());					
					sectionEntry.setPolishWord(sectionMapEntryListEntry.getKey().getPolishWord());
					
					// poszczegolne slowka sekcji
					for (KanjiKanaPairWrapper kanjiKanaPairWrapper : sectionMapEntryListEntry.getValue()) {
						
						SectionEntryIndexEntry sectionEntryIndexEntry = new SectionEntryIndexEntry();
												
						sectionEntryIndexEntry.setKanji(kanjiKanaPairWrapper.getKanji());
						sectionEntryIndexEntry.setKana(kanjiKanaPairWrapper.getKana());
						sectionEntryIndexEntry.setEntryId(kanjiKanaPairWrapper.getEntryId());
						
						sectionEntry.getEntries().add(sectionEntryIndexEntry);
					}
					
					sectionIndex.getSectionEntry().add(sectionEntry);
				}
				
				// zapis do pliku xml
				// ustalenie nazwy pliku
				File japaneseIndexSectionIndexFile = new File(outputDirectory, mainIndexName + "_" + sectionIndexName + "_" + 
						sectionIndex.getSectionName() + "_" + sectionIndex.getPartNo() + ".xml");
				
				JAXBContext jaxbContext = JAXBContext.newInstance(SectionIndex.class);              
				
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
				jaxbMarshaller.marshal(sectionIndex, japaneseIndexSectionIndexFile);
				
				// jeszcze dodanie do spisu
				SectionIndexMetadata sectionIndexMetadata = new SectionIndexMetadata();
				
				sectionIndexMetadata.setSectionName(sectionIndex.getSectionName());
				sectionIndexMetadata.setPartNo(sectionIndex.getPartNo());
				sectionIndexMetadata.setFileName(japaneseIndexSectionIndexFile.getName());					
				
				sectionIndexMetadataAdder.accept(sectionIndexMetadata);
			}
		}		
	}

	//

	public static class DictionaryIndex {
		final static public String otherSectionName = "Inne";
		
		private EntryListIndex entryListIndex;
		private NameEntryListIndex nameEntryListIndex;
		private KanjiCharacterInfoListIndex kanjiCharacterInfoListIndex;
		
		public EntryListIndex getEntryListIndex() {
			return entryListIndex;
		}
				
		public NameEntryListIndex getNameEntryListIndex() {
			return nameEntryListIndex;
		}

		public KanjiCharacterInfoListIndex getKanjiCharacterInfoListIndex() {
			return kanjiCharacterInfoListIndex;
		}

		//
		
		public static class KanjiKanaPairWrapper {
			
			private KanjiKanaPair kanjiKanaPair;
			private NameKanjiKanaPair nameKanjiKanaPair;
			
			public KanjiKanaPairWrapper(KanjiKanaPair kanjiKanaPair) {
				this.kanjiKanaPair = kanjiKanaPair;
			}

			public KanjiKanaPairWrapper(NameKanjiKanaPair nameKanjiKanaPair) {
				this.nameKanjiKanaPair = nameKanjiKanaPair;
			}
			
			public Integer getEntryId() {
				return kanjiKanaPair != null ? kanjiKanaPair.getEntry().getEntryId() : nameKanjiKanaPair.getEntry().getEntryId();
			}
			
			public String getKanji() {
				return kanjiKanaPair != null ? kanjiKanaPair.getKanji() : nameKanjiKanaPair.getKanji();
			}

			public String getKana() {
				return kanjiKanaPair != null ? kanjiKanaPair.getKana() : nameKanjiKanaPair.getKana();
			}
		}
		
		public static class EntryListIndex {
			
			// dane zrodlowe
			private List<JMdict.Entry> entryList;
			
			// mapa ze wszystkimi unikalnymi japonskimi slowami (kana, romaji)
			private Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> japaneseIndexMap;
			
			// mapa ze wszystkimi sekcjami (to bedzie pierwszy znak kana w romaji lub otherSectionName (Inny)
			private Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> japaneseIndexSectionMap;
			
			// mapa ze wszystkimi unikalnymi polskimi znaczenia
			private Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> polishIndexMap;
			
			// mapa ze wszystkimi sekcjami (to najczesciej beda dwa pierwsze znaki znaczenia)
			private Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> polishIndexSectionMap;
			
			// mapa ze spisem wszystkich slow (potrzebne tylko dla generator latex ze spisem wszystkich slow) 
			private Map<String, List<JMdict.Entry>> entriesListGroupedBy;
			
			public EntryListIndex(List<Entry> entryList) {
				this.entryList = entryList;
			}

			public List<JMdict.Entry> getEntryList() {
				return entryList;
			}

			public Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> getJapaneseIndexMap() {
				return japaneseIndexMap;
			}

			public Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> getJapaneseIndexSectionMap() {
				return japaneseIndexSectionMap;
			}

			public Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> getPolishIndexMap() {
				return polishIndexMap;
			}

			public Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> getPolishIndexSectionMap() {
				return polishIndexSectionMap;
			}

			public Map<String, List<JMdict.Entry>> getEntriesListGroupedBy() {
				return entriesListGroupedBy;
			}
		}
		
		public static class NameEntryListIndex {
			
			// dane zrodlowe
			private List<JMnedict.Entry> nameEntryList;
			
			// mapa ze wszystkimi unikalnymi japonskimi slowami (kana, romaji)
			private Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> japaneseIndexMap;
			
			// mapa ze wszystkimi sekcjami (to bedzie pierwszy znak kana w romaji lub otherSectionName (Inny)
			private Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> japaneseIndexSectionMap;
			
			// mapa ze wszystkimi unikalnymi polskimi znaczenia
			private Map<String, List<KanjiKanaPairWrapper>> translateIndexMap;
			
			// mapa ze wszystkimi sekcjami (to najczesciej beda dwa pierwsze znaki znaczenia)
			private Map<String, List<Map.Entry<String, List<KanjiKanaPairWrapper>>>> translateIndexSectionMap;
						
			public NameEntryListIndex(List<JMnedict.Entry> nameEntryList) {
				this.nameEntryList = nameEntryList;
			}

			public List<JMnedict.Entry> getNameEntryList() {
				return nameEntryList;
			}

			public Map<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>> getJapaneseIndexMap() {
				return japaneseIndexMap;
			}

			public Map<String, List<Map.Entry<KanaRomajiPolishWordKey, List<KanjiKanaPairWrapper>>>> getJapaneseIndexSectionMap() {
				return japaneseIndexSectionMap;
			}

			public Map<String, List<KanjiKanaPairWrapper>> getTranslateIndexMap() {
				return translateIndexMap;
			}

			public Map<String, List<Map.Entry<String, List<KanjiKanaPairWrapper>>>> getTranslateIndexSectionMap() {
				return translateIndexSectionMap;
			}
		}
		
		public static class KanjiCharacterInfoListIndex {
			
			// dane zrodlowe
			private List<KanjiCharacterInfo> kanjiList;
						
			// mapa ze wszystkimi unikalnymi polskimi znaczenia
			private Map<String, List<KanjiCharacterInfo>> translateIndexMap;
			
			public List<KanjiCharacterInfo> getKanjiList() {
				return kanjiList;
			}

			// mapa ze wszystkimi sekcjami (to najczesciej beda dwa pierwsze znaki znaczenia)
			private Map<String, List<Map.Entry<String, List<KanjiCharacterInfo>>>> translateIndexSectionMap;
						
			public KanjiCharacterInfoListIndex(List<KanjiCharacterInfo> kanjiList) {
				this.kanjiList = kanjiList;
			}
			
			public Map<String, List<KanjiCharacterInfo>> getTranslateIndexMap() {
				return translateIndexMap;
			}

			public Map<String, List<Map.Entry<String, List<KanjiCharacterInfo>>>> getTranslateIndexSectionMap() {
				return translateIndexSectionMap;
			}
		}
	}
		
	public static class KanaRomajiPolishWordKey implements Comparable<KanaRomajiPolishWordKey> {
		private String kana;
		private String romaji;
		private String polishWord;
				
		public KanaRomajiPolishWordKey(String kana, String romaji) {
			this.kana = kana;
			this.romaji = romaji;
		}

		public KanaRomajiPolishWordKey(String polishWord) {
			this.polishWord = polishWord;
		}

		@Override
		public int compareTo(KanaRomajiPolishWordKey o2) {
			String o1Kana = kana != null ? kana : "<null>";
			String o1Romaji = romaji != null ? romaji : "<null>";
			String o1PolishWord = polishWord != null ? polishWord : "<null>";

			String o2Kana = o2.kana != null ? o2.kana : "<null>";
			String o2Romaji = o2.romaji != null ? o2.romaji : "<null>";
			String o2PolishWord = o2.polishWord != null ? o2.polishWord : "<null>";

			int result = o1Romaji.compareTo(o2Romaji);
						
			if (result != 0) {
				return result;
			}
			
			result = o1Kana.compareTo(o2Kana);

			if (result != 0) {
				return result;
			}

			result = o1PolishWord.compareTo(o2PolishWord);
			
			return result;
		}

		public String getKana() {
			return kana;
		}

		public String getRomaji() {
			return romaji;
		}

		public String getPolishWord() {
			return polishWord;
		}

		@Override
		public int hashCode() {
			return Objects.hash(kana, romaji, polishWord);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			KanaRomajiPolishWordKey other = (KanaRomajiPolishWordKey) obj;
			
			return Objects.equals(kana, other.kana) && Objects.equals(romaji, other.romaji) && Objects.equals(polishWord, other.polishWord);
		}
	}
}
