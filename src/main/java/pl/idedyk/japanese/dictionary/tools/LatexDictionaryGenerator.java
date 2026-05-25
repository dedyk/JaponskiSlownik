package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Info;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Xref;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.XrefType;

public class LatexDictionaryGenerator {
	
	final static String otherSectionName = "Inne";

	public static void main(String[] args) throws Exception {
		
		// INFO: normalnie plik latex generuje sie z pliku jmdict wygenerowanego przez AndroidDictionaryGenerator
		// wiec zawiera wszystkie slowa lacznie ze slowami, ktore tylko wystepuja w starej strukturze
		// jednakze na potrzeby testu to nie jest potrzebne
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		
		JMdict polishJMdict = dictionary2Helper.getPolishJMdict();
		
		// FM_FIXME: mala czesc
		JMdict testPolishJMdict = new JMdict();
		
		testPolishJMdict.getEntryList().addAll(polishJMdict.getEntryList().subList(0, 10000));
		
		PolishJapaneseLatexContent latexDictonaryEntries = generateLatexDictonaryEntries(testPolishJMdict);
		
		// zapis plikow
		File dictionaryJapaneseIndexFile = new File("pdf_dictionary/dictionary_japanese_index.tex");
		dictionaryJapaneseIndexFile.delete();
				
		Files.write(dictionaryJapaneseIndexFile.toPath(), latexDictonaryEntries.japaneseIndex.getBytes(), StandardOpenOption.CREATE_NEW);
		
		/*
		

		//
		
        TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");
		List<PolishJapaneseEntry> polishJapaneseEntries = dictionary2Helper.getOldPolishJapaneseEntriesList();
		
		//
        
        Helper.generateAdditionalInfoFromEdict(dictionary2Helper, jmedictCommon, polishJapaneseEntries);
		
        //
        
		List<String> latexDictonaryEntries = generateLatexDictonaryEntries(polishJapaneseEntries);
				
		FileWriter fileWriter = new FileWriter("pdf_dictionary/dictionary_entries.tex");
		
		for (String latexString : latexDictonaryEntries) {
			
			System.out.print(latexString);
			
			fileWriter.write(latexString);
		}		
		
		fileWriter.close();
		*/
	}
	
	public static PolishJapaneseLatexContent generateLatexDictonaryEntries(JMdict polishJMdict) {
		
		PolishJapaneseLatexContent polishJapaneseLatexContent = new PolishJapaneseLatexContent();
		
		// generowanie indeksu japonskiego
		generateJapaneseIndex(polishJapaneseLatexContent, polishJMdict);
		
		return polishJapaneseLatexContent;
	}
	
	
	private static void generateJapaneseIndex(PolishJapaneseLatexContent polishJapaneseLatexContent, JMdict polishJMdict) {
		
		// klasa pomocnicza
		class KanaRomajiKey implements Comparable<KanaRomajiKey> {
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

				int result = o1Kana.compareTo(o2Kana);
				
				if (result != 0) {
					return result;
				}
				
				result = o1Romaji.compareTo(o2Romaji);
				
				return result;
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
		
		// mapowania do generowania indeksu
		Map<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMap = new TreeMap<>();
		
		// pobieramy wszystkie slowa
		List<pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry> entryList = polishJMdict.getEntryList();
		
		// chodzimy po wszystkich slowach
		for (pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry entry : entryList) {
			
			// pobieramy tylko widoczne czytania
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
			
			// chodzimy po wszystkich czytania i dodajemy do mapy
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				String kana = kanjiKanaPair.getKana();
				String romaji = kanjiKanaPair.getRomaji();
				
				if (romaji == null || romaji.length() == 0) {
					kana = otherSectionName;
					romaji = otherSectionName; // FM_FIXME: generowanie tej sekcji
				}
				
				// generowanie klucza do mapy
				KanaRomajiKey kanaRomajiKey = new KanaRomajiKey(kana, romaji);
				
				// indeks dla danego klucza
				List<KanjiKanaPair> kanjiKanaPairListForKanaRomajiKey = japaneseIndexMap.get(kanaRomajiKey);
				
				// gdy nie ma tworzymy wpis
				if (kanjiKanaPairListForKanaRomajiKey == null) {
					kanjiKanaPairListForKanaRomajiKey = new ArrayList<KanjiKanaPair>();
					
					japaneseIndexMap.put(kanaRomajiKey, kanjiKanaPairListForKanaRomajiKey);
				}
				
				// dodajemy wpis
				if (kanjiKanaPairListForKanaRomajiKey.contains(kanjiKanaPair) == false) {
					kanjiKanaPairListForKanaRomajiKey.add(kanjiKanaPair);
				}
			}			
		}
		
		// grupowanie po sekcjach
		Map<String, List<Map.Entry<KanaRomajiKey, List<KanjiKanaPair>>>> indexSection = new TreeMap<>();
		
		for (Map.Entry<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMapEntry : japaneseIndexMap.entrySet()) {
			
			KanaRomajiKey kanaRomajiKey = japaneseIndexMapEntry.getKey();

			String section = null;
			
			if (kanaRomajiKey.kana == otherSectionName || kanaRomajiKey.romaji == otherSectionName) {
				section = otherSectionName;
				
			} else {
				section = kanaRomajiKey.romaji.substring(0, 1).toUpperCase();
			}
			
			// czy taka sekcja wystepuje
			List<Entry<KanaRomajiKey, List<KanjiKanaPair>>> entrySetListForSection = indexSection.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				indexSection.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(japaneseIndexMapEntry);
		}
		
		// generowanie zawartosci dokumentu		
		StringBuffer latexContent = new StringBuffer();
		
		latexContent.append("\\chapter*{Indeks słów japońskich}\n");		
		
		for (Entry<String, List<Entry<KanaRomajiKey, List<KanjiKanaPair>>>> indexSectionEntrySet : indexSection.entrySet()) {
			
			String section = indexSectionEntrySet.getKey();
			List<Entry<KanaRomajiKey, List<KanjiKanaPair>>> indexSectionList = indexSectionEntrySet.getValue();
			
			// na razie nie generujemy sekcji dla innych
			if (section == otherSectionName) {
				continue;
			}
			
			latexContent.append("\\section*{" + section + "}\n");
			latexContent.append("\\begin{multicols}{3}\n");
			
			for (Entry<KanaRomajiKey, List<KanjiKanaPair>> currentSectionIndexEntry : indexSectionList) {
				
				// lista slow w danym miejscu
				List<KanjiKanaPair> kanjiKanaPairList = currentSectionIndexEntry.getValue();
				
				latexContent.append("\\noindent"); // \\begin{minipage}{\\linewidth}\n");
				
				latexContent.append("\\footnotesize\n");
				// FM_FIXME: pogrubienie i upiekszenie
				latexContent.append("\\textbf{" + currentSectionIndexEntry.getKey().romaji + "}, " + currentSectionIndexEntry.getKey().kana);
				
				if (kanjiKanaPairList.size() == 666) {
					
					if (kanjiKanaPairList.get(0).getKanjiInfo() != null) {
						latexContent.append(", " + kanjiKanaPairList.get(0).getKanji());
					}
					
					latexContent.append("\\dotfill");
					latexContent.append("666");
					latexContent.append("\n\n");					
					
				} else {
					latexContent.append("\n\n");
					// latexContent.append("\\textbf{" + currentSectionIndexEntry.getKey().romaji + "}, \\textbf{" + currentSectionIndexEntry.getKey().kana + "}\n\n");
				}
				
				// latexContent.append("\\dotfill");
				// latexContent.append("666");
								
				if (kanjiKanaPairList.size() > 0) {
					
					for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
						// latexContent.append("\\hspace*{1.5em} ");
						
						// FM_FIXME: ulepszenia, np. pogrubienie
						if (kanjiKanaPair.getKanjiInfo() != null) {
							latexContent.append("\n" + kanjiKanaPair.getKanji());
						} else {
							latexContent.append("\n" + kanjiKanaPair.getKana());
						}
						
						latexContent.append("\\dotfill");
						latexContent.append("666");
						
						// FM_FIXME: \pageref{latex_test1}
						
						latexContent.append("\n\n");
					}
					
					// FM_FIXME: \pageref{latex_test1}					
				}
				
				
				/*
				fizyka \newline
				\hspace*{1.5em} grawitacja \dotfill \pageref{latex_test1} \newline
				\hspace*{1.5em} grawitacja \dotfill \pageref{latex_test3} \newline
				\end{minipage}
				*/
				// latexContent.append("\\end{minipage}\n");	
			}
			
			// FM_FIXME: \markboth !!!!
			
			
			latexContent.append("\\end{multicols}\n");
		}
		
		polishJapaneseLatexContent.japaneseIndex = latexContent.toString();
	}

	//////// Stary kod
	
	public static List<String> generateLatexDictonaryEntries(List<PolishJapaneseEntry> polishJapaneseEntries) throws Exception {
		
		List<String> result = new ArrayList<String>();
		
		//
				
		// utworzenie sekcji
		Map<String, List<PolishJapaneseEntry>> sectionMap = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			//String kana = polishJapaneseEntry.getKana();
			String romaji = polishJapaneseEntry.getRomaji();
			
			//String sectionName = kana.substring(0, 1);
			String sectionName = null;
			
			if (romaji.length() != 0) {
				sectionName = romaji.substring(0, 1).toUpperCase();
				
			} else {
				sectionName = otherSectionName;
			}

			/*
			if (Utils.isKana(sectionName.charAt(0)) == false || sectionName.equals("ゝ") == true || sectionName.equals("ゞ") == true
					|| sectionName.equals("ヶ") == true || sectionName.equals("ー") == true || sectionName.equals("ヽ") == true ||
					sectionName.equals("ヾ") == true) {
				sectionName = otherSectionName;
			}
			*/
			
			if (sectionName.matches("^[A-Z]+$") == false) {
				sectionName = otherSectionName;
			}			
			
			List<PolishJapaneseEntry> section = sectionMap.get(sectionName);
			
			if (section == null) {
				section = new ArrayList<PolishJapaneseEntry>();
				
				sectionMap.put(sectionName, section);
			}
			
			section.add(polishJapaneseEntry);			
		}
		
		// generowanie sekcji
		Iterator<Entry<String, List<PolishJapaneseEntry>>> sectionMapIterator = sectionMap.entrySet().iterator();
		
		Entry<String, List<PolishJapaneseEntry>> otherEntry = null;
		
		while(sectionMapIterator.hasNext() == true) {
			
			Entry<String, List<PolishJapaneseEntry>> sectionEntry = sectionMapIterator.next();
			
			// sekcja inne na koncu
			if (sectionEntry.getKey() == otherSectionName) {
				
				otherEntry = sectionEntry;
				
				continue;
			}
			
			generateSection(result, sectionEntry.getKey(), sectionEntry.getValue());
		}
		
		// generowanie sekcji inne
		if (otherEntry != null) {
			generateSection(result, otherEntry.getKey(), otherEntry.getValue());
		}		
		
		return result;
	}
	
	private static void generateSection(List<String> result, String key, List<PolishJapaneseEntry> keyList) throws Exception {
		
		//KanaHelper kanaHelper = new KanaHelper();
		
		String sectionName = null;
		
		/*
		if (key != otherSectionName) {
			sectionName = key + " (" + convertToRomaji(key, kanaHelper) + ")";
			
		} else {
			sectionName = otherSectionName;
		}
		*/
		
		sectionName = key;
		
		result.add("%----------------------------------------------------------------------------------------\n");
		result.add(String.format("%s\tSekcja: %s\n", "%", sectionName));
		result.add("%----------------------------------------------------------------------------------------\n\n");
		result.add(String.format("\\section*{%s}\n", sectionName));
		
		result.add("\\begin{multicols}{2}\n\n");
		
		List<PolishJapaneseEntry> polishJapaneseEntryList = keyList;
		
		Collections.sort(polishJapaneseEntryList, new Comparator<PolishJapaneseEntry>() {

			@Override
			public int compare(PolishJapaneseEntry o1, PolishJapaneseEntry o2) {
				return o1.getRomaji().compareToIgnoreCase(o2.getRomaji());
			}
		});
		
		//int counter = 0;
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {
			
			//if (counter > 10) {
			//	break;
			//}
						
			// sprawdzenie, czy wystepuje slowo w formacie JMdict
			// pobieramy entry id
			KanjiKanaPair kanjiKanaPair = null;
			
			Integer entryId = polishJapaneseEntry.getJmdictEntryId();
			
			if (entryId != null) {
				pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry jmdictEntry = dictionaryHelper.getEntryFromPolishDictionary(entryId);
				
				List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(jmdictEntry, false);
				
				// szukamy odpowiednika KanjiKanaPair
				kanjiKanaPair = Dictionary2HelperCommon.findKanjiKanaPair(kanjiKanaPairList, polishJapaneseEntry);
				
				// sprawdzenie, czy to nie jest search only kanji lub kana, takich nie pokazujemy
				if (kanjiKanaPair.getKanjiInfo() != null && kanjiKanaPair.getKanjiInfo().getKanjiAdditionalInfoList().contains(KanjiAdditionalInfoEnum.SEARCH_ONLY_KANJI_FORM) == true) {
					continue;
				}
				
				if (kanjiKanaPair.getReadingInfo().getReadingAdditionalInfoList().contains(ReadingAdditionalInfoEnum.SEARCH_ONLY_KANA_FORM) == true) {
					continue;
				}				
			}
						
			result.add(generateDictionaryEntry(polishJapaneseEntry, kanjiKanaPair));
			
			//counter++;			
		}			
		
		result.add("\\end{multicols}\n\n");
	}
	
	private static String generateDictionaryEntry(PolishJapaneseEntry polishJapaneseEntry, KanjiKanaPair kanjiKanaPair) {
		
		StringBuffer result = new StringBuffer();
				
		if (kanjiKanaPair == null) { // stary sposob generowania
						
			// translates, info
			
			String kanji = polishJapaneseEntry.getKanji();
			
			String kana = polishJapaneseEntry.getKana();
			String romaji = polishJapaneseEntry.getRomaji();
			
			//result.append("\\noindent ");
			
			// pogrubienie romaji
			result.append(textbf(romaji)).append(" ");
			
			if (kanji.equals("-") == false) {
			
				// na gorze strony slowa kanji
				// result.append(markBoth(kanji)).append(" ");
				
				// kanji
				result.append(cjkFakeBold(kanji)).append(" ");
				
			} else {
				// na gorze strony kana
				result.append(markBoth(textbf(romaji) + " (" + kana + ")")).append(" ");
			}
			
			// kana
			result.append("(" + cjkFakeBold(kana) + ")").append(" ");
			
			// na gorze strony romaji + kana
			result.append(markBoth(textbf(romaji) + " (" + kana + ")")).append(" ");

			// rodzaje slowa
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			boolean wasAddableDictionaryEntryType = false;
			
			for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeList) {
				
				if (DictionaryEntryType.isAddableDictionaryEntryTypeInfo(dictionaryEntryType) == false) {
					continue;
				}
				
				if (wasAddableDictionaryEntryType == true) {
					result.append(textit(", "));
				}
				
				result.append(textit(dictionaryEntryType.getName()));
				
				wasAddableDictionaryEntryType = true;
			}
			
			// pobranie atrybutów
			AttributeList attributeList = polishJapaneseEntry.getAttributeList();
			
			List<Attribute> attributeListList = attributeList.getAttributeList();
			
			boolean wasShowAttribute = false;
			
			for (Attribute attribute : attributeListList) {
				
				AttributeType attributeType = attribute.getAttributeType();
				
				if (attributeType.isShow() == false) {
					continue;
				}
				
				if (wasShowAttribute == false) {
					result.append(" ").append(cdot()).append(" ");
					
				} else {
					result.append(textit(", "));
				}
				
				result.append(textit(attributeType.getName()));
				
				wasShowAttribute = true;
			}
			
			if (wasShowAttribute == true) {
				result.append(" ");
			}
			
			// tlumaczenie
			result.append(" ").append(bullet()).append(" ");
			
			List<String> translates = polishJapaneseEntry.getTranslates();
			
			boolean wasTranslate = false;
			
			for (String translate : translates) {
				
				if (wasTranslate == true) {
					result.append(", ");
				}
				
				result.append(textbf(escapeLatexChars(translate)));
				
				wasTranslate = true;
			}
			
			// informacje dodatkowe
			String info = polishJapaneseEntry.getInfo();
			
			if (info != null && info.equals("") == false) {
				
				result.append(" ").append(cdot()).append(" ");
				
				result.append(escapeLatexChars(info));			
			}
			
		} else if (kanjiKanaPair != null) { // nowy sposob generowania
			
			// generowanie znaczenia
			KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
			ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
			
			// pogrubienie romaji
			result.append(textbf(readingInfo.getKana().getRomaji())).append(" ");

			if (kanjiInfo != null && kanjiInfo.getKanji() != null) {
								
				// kanji
				result.append(cdot()).append(cjkFakeBold(kanjiInfo.getKanji())).append(" ");
				
				// informacje dodatkowe do kanji
				List<String> kanjiAdditionalInfoPolishList = Dictionary2Helper.translateToPolishKanjiAdditionalInfoEnum(kanjiInfo.getKanjiAdditionalInfoList());
				
				for (int idx = 0; idx < kanjiAdditionalInfoPolishList.size(); ++idx) {
					
					if (idx == 0) {
						result.append("(");
					}
					
					result.append(textit(kanjiAdditionalInfoPolishList.get(idx)));
					
					if (idx != kanjiAdditionalInfoPolishList.size() - 1) {
						result.append("; ");
					}
					
					if (idx == kanjiAdditionalInfoPolishList.size() - 1) {
						result.append(") ");
					}
				}
				
			} else {
				// na gorze strony kana
				result.append(markBoth(textbf(readingInfo.getKana().getRomaji()) + " (" + readingInfo.getKana().getValue() + ")")).append(" ");
			}

			// kana
			result.append(cdot()).append(cjkFakeBold(readingInfo.getKana().getValue())).append(" ");
			
			// informacje dodatkowe do kana
			List<String> kanaReadingAdditionalInfoPolishList = Dictionary2Helper.translateToPolishReadingAdditionalInfoEnum(readingInfo.getReadingAdditionalInfoList());
			
			for (int idx = 0; idx < kanaReadingAdditionalInfoPolishList.size(); ++idx) {
				
				if (idx == 0) {
					result.append("(");
				}
				
				result.append(textit(kanaReadingAdditionalInfoPolishList.get(idx)));
				
				if (idx != kanaReadingAdditionalInfoPolishList.size() - 1) {
					result.append("; ");
				}
				
				if (idx == kanaReadingAdditionalInfoPolishList.size() - 1) {
					result.append(") ");
				}
			}
			
			// na gorze strony romaji + kana
			result.append(markBoth(textbf(readingInfo.getKana().getRomaji()) + " (" + readingInfo.getKana().getValue() + ")")).append(" ");
			
			// znaczenie
			//result.append(" ").append(bullet()).append(" ");

			List<Sense> senseList = kanjiKanaPair.getSenseList();
			
			for (int idx = 0; idx < senseList.size(); ++idx) {
				
				Sense sense = senseList.get(idx);
				
				result.append("\\circled{" + (idx + 1) + "}\\ ");
				
				// czesc mowy				
				List<String> polishPartOfSpeechEnumPolishList = Dictionary2Helper.translateToPolishPartOfSpeechEnum(sense.getPartOfSpeechList());
				
				for (int polishPartOfSpeechEnumPolishListIdx = 0; polishPartOfSpeechEnumPolishListIdx < polishPartOfSpeechEnumPolishList.size(); ++polishPartOfSpeechEnumPolishListIdx) {
										
					result.append(textit(polishPartOfSpeechEnumPolishList.get(polishPartOfSpeechEnumPolishListIdx)));
					
					if (polishPartOfSpeechEnumPolishListIdx != polishPartOfSpeechEnumPolishList.size() - 1) {
						result.append("; ");
					}
					
					if (polishPartOfSpeechEnumPolishListIdx == polishPartOfSpeechEnumPolishList.size() - 1) {
						result.append(" ").append(cdot()).append(" ");
					}
				}
				
				// dziedzina nauki
				List<String> fieldPolishList = Dictionary2Helper.translateToPolishFieldEnumList(sense.getFieldList());

				for (int fieldPolishListIdx = 0; fieldPolishListIdx < fieldPolishList.size(); ++fieldPolishListIdx) {
										
					result.append(textit(fieldPolishList.get(fieldPolishListIdx)));
					
					if (fieldPolishListIdx != fieldPolishList.size() - 1) {
						result.append("; ");
					}
					
					if (fieldPolishListIdx == fieldPolishList.size() - 1) {
						result.append(" ").append(cdot()).append(" ");
					}
				}
				
				// rozne informacje
				List<String> miscPolishList = Dictionary2Helper.translateToPolishMiscEnumList(sense.getMiscList());

				for (int miscPolishListIdx = 0; miscPolishListIdx < miscPolishList.size(); ++miscPolishListIdx) {
										
					result.append(textit(miscPolishList.get(miscPolishListIdx)));
					
					if (miscPolishListIdx != miscPolishList.size() - 1) {
						result.append("; ");
					}
					
					if (miscPolishListIdx == miscPolishList.size() - 1) {
						result.append(" ").append(cdot()).append(" ");
					}
				}
				
				// dialekt
				List<String> dialectPolishList = Dictionary2Helper.translateToPolishDialectEnumList(sense.getDialectList());

				for (int dialectPolishListIdx = 0; dialectPolishListIdx < dialectPolishList.size(); ++dialectPolishListIdx) {
					
					result.append(textit(dialectPolishList.get(dialectPolishListIdx)));
					
					if (dialectPolishListIdx != dialectPolishList.size() - 1) {
						result.append("; ");
					}
					
					if (dialectPolishListIdx == dialectPolishList.size() - 1) {
						result.append(" ").append(cdot()).append(" ");
					}
				}

				// tlumaczenie polskie
				List<Gloss> glossPolList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				for (int glossPolListIdx = 0; glossPolListIdx < glossPolList.size(); ++glossPolListIdx) {
					
					Gloss currentGloss = glossPolList.get(glossPolListIdx);
					
					result.append(textbf(escapeLatexChars(currentGloss.getValue())));
					
					if (currentGloss.getGType() != null) {
						result.append(" (").append(Dictionary2Helper.translateToPolishGlossType(currentGloss.getGType())).append(")");
					}
					
					if (glossPolListIdx != glossPolList.size() - 1) {
						result.append("; ");
					}
					
					if (glossPolListIdx == glossPolList.size() - 1) {
						result.append(" ");
					}
				}
				
				// informacje dodatkowe do tlumaczenia				
				List<String> senseAdditionalInfoList = new ArrayList<>();				
				senseAdditionalInfoList.addAll(sense.getAdditionalInfoList().stream().filter(additionalInfo -> (additionalInfo.getLang().equals("pol") == true)).map(m -> m.getValue()).collect(Collectors.toList()));

				for (int senseAdditionalInfoListIdx = 0; senseAdditionalInfoListIdx < senseAdditionalInfoList.size(); ++senseAdditionalInfoListIdx) {
					
					String senseAdditionalInfo = senseAdditionalInfoList.get(senseAdditionalInfoListIdx);
					
					result.append(" ").append(cdot()).append(" ");
					result.append(escapeLatexChars(senseAdditionalInfo)).append(" ");
				}
								
				// referencja do innego slowa
				List<Xref> referenceToAnotherKanjiKanaList = sense.getReferenceToAnotherKanjiKanaList();
				
				// grupowanie po typie odnosnika
				Map<XrefType, List<Xref>> referenceToAnotherKanjiKanaListGroupedBy = new LinkedHashMap<XrefType, List<Xref>>();
				
				for (Xref xref : referenceToAnotherKanjiKanaList) {
					List<Xref> xrefForTypeList = referenceToAnotherKanjiKanaListGroupedBy.get(xref.getType());
					
					if (xrefForTypeList == null) {
						xrefForTypeList = new ArrayList<Xref>();
						
						referenceToAnotherKanjiKanaListGroupedBy.put(xref.getType(), xrefForTypeList);
					}
					
					xrefForTypeList.add(xref);
				}
				
				for (Entry<XrefType, List<Xref>> currentXrefTypeListEntry : referenceToAnotherKanjiKanaListGroupedBy.entrySet()) {
					
					List<Xref> xrefForTypeList = currentXrefTypeListEntry.getValue();
					
					for (int xrefForTypeListIdx = 0; xrefForTypeListIdx < xrefForTypeList.size(); ++xrefForTypeListIdx) {
						
						Xref referenceToAnotherKanjiKana = xrefForTypeList.get(xrefForTypeListIdx);
						
						if (xrefForTypeListIdx == 0) {
							result.append(" ").append(cdot()).append(" ");
							
							if (referenceToAnotherKanjiKana.getType() == XrefType.SEE || referenceToAnotherKanjiKana.getType() == XrefType.SYN) {
								result.append(rightarrow());
								
							} else if (referenceToAnotherKanjiKana.getType() == XrefType.ANT) {
								result.append(leftRightArrow());
								
							} else {
								throw new RuntimeException("Unknown xref type: " + referenceToAnotherKanjiKana.getType());
							}
							
							result.append(" ");
						}
						
						result.append(referenceToAnotherKanjiKana.getValue());

						if (xrefForTypeListIdx != xrefForTypeList.size() - 1) {
							result.append("; ");
						}
						
						if (xrefForTypeListIdx == xrefForTypeList.size() - 1) {
							result.append(" ");
						}
					}
					
				}				
			}	
			
			// info dodatkowe
			for (Info info : kanjiKanaPair.getEntry().getInfoList().stream().filter(info -> (info.getLang().equals("pol") == true)).collect(Collectors.toList())) {
				result.append(" ").append(cdot()).append(" ");
				result.append(escapeLatexChars(info.getValue())).append(" ");
			}
			
			// informacja o pochodzeniu slowa z innego jezyka
			List<LanguageSource> languageSourceList = new ArrayList<>();
			languageSourceList.addAll(kanjiKanaPair.getEntry().getLanguageSourceList());
							
			for (int senseLanguageSourceListIdx = 0; senseLanguageSourceListIdx < languageSourceList.size(); ++senseLanguageSourceListIdx) {
				
				LanguageSource languageSource = languageSourceList.get(senseLanguageSourceListIdx);
				
				if (senseLanguageSourceListIdx == 0) {
					result.append(" ").append(cdot()).append(" ");
				}
				
				String languageCodeInPolish = Dictionary2HelperCommon.translateToPolishLanguageCode(languageSource.getLang());
				String languageValue = languageSource.getValue();
				String languageLsWasei = Dictionary2HelperCommon.translateToPolishLanguageSourceLsWaseiEnum(languageSource.getLsWasei());
				
				if (languageValue != null && languageValue.trim().equals("") == false) {
					result.append(escapeLatexChars(languageCodeInPolish + ": " + languageValue));
					
				} else {
					result.append(escapeLatexChars(Dictionary2HelperCommon.translateToPolishLanguageCodeWithoutValue(languageSource.getLang())));
				}
				
				if (languageLsWasei != null) {
					result.append(" ").append(cdot()).append(" ").append(textit(languageLsWasei));
				}
				
				if (senseLanguageSourceListIdx != languageSourceList.size() - 1) {
					result.append("; ");
				}
				
				if (senseLanguageSourceListIdx == languageSourceList.size() - 1) {
					result.append(" ");
				}
			}

		}
				
		result.append("\\vspace{0.3cm} \n\n");
		
		return result.toString();
	}
	
	private static String escapeLatexChars(String text) {
		
		text = text.replaceAll("\\^", "\\\\^{}");
		text = text.replaceAll("\\&", "\\\\&");
		text = text.replaceAll("\\#", "\\\\#");
		text = text.replaceAll("\\$", "\\\\\\$");
		text = text.replaceAll("\\_", "\\\\_");
		text = text.replaceAll("\\%", "\\\\%");
		
		return text;
	}

	private static String markBoth(String text) {
		return String.format("\\markboth{%s}{%s}", text, text);
	}
	
	private static String textbf(String text) {
		return String.format("\\textbf{%s}", text);
	}

	private static String textit(String text) {
		return String.format("\\textit{%s}", text);
	}

	private static String cjkFakeBold(String text) {
		return String.format("\\CJKfakebold{%s}", textbf(text));
	}
	
	private static String cdot() {
		return "$\\cdot$";
	}

	private static String bullet() {
		return "$\\bullet$";
	}
	
	private static String rightarrow() {
		return "$\\rightarrow$";
	}

	private static String leftRightArrow() {
		return "$\\leftrightarrow$";
	}

	/*
	private static String convertToRomaji(String kana, KanaHelper kanaHelper) {
		
		KanaWord kanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(true), false);
		
		return kanaHelper.createRomajiString(kanaWord);
	}
	*/
	
	public static class PolishJapaneseLatexContent {
		private String japaneseIndex;
		
	}
}
