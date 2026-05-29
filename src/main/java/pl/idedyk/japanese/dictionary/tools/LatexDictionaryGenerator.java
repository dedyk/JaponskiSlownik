package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
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
import pl.idedyk.japanese.dictionary2.jmdict.xsd.MiscInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.OldPolishJapaneseDictionaryInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.SenseAdditionalInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Xref;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.XrefType;

public class LatexDictionaryGenerator {
	
	final static String otherSectionName = "Inne";

	public static void main(String[] args) throws Exception {
		
		// INFO: normalnie plik latex generuje sie z pliku jmdict wygenerowanego przez AndroidDictionaryGenerator
		// wiec zawiera wszystkie slowa lacznie ze slowami, ktore tylko wystepuja w starej strukturze
		// jednakze na potrzeby testu to nie jest potrzebne
		
		// INFO2: wersja common musi byc generowana z pelnym slownikiem, ktory wyszedl z AndroidDictionaryGenerator
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		
		JMdict polishJMdict = dictionary2Helper.getPolishJMdict();
		
		List<JMdict.Entry> entryList = new ArrayList<>();
		
		entryList.addAll(polishJMdict.getEntryList().subList(0, 10000));
		// entryList.addAll(polishJMdict.getEntryList().stream().filter(f -> f.getInfoList().size() > 0).collect(Collectors.toList()));
		// entryList.addAll(polishJMdict.getEntryList().stream().filter(f -> f.getEntryId().intValue() == 2054830).collect(Collectors.toList()));
		
		// wygenerowanie plikow
		generateLatexDictonaryEntries(entryList, new File("pdf_dictionary"), false);
				
		/*
		// stary kod
		
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
	
	public static void clearLatexDictonaryEntries(File destinationDir) {
		File dictionaryTitleFile = new File(destinationDir, "dictionary_title.tex");
		File dictionaryJapaneseIndexFile = new File(destinationDir, "dictionary_japanese_index.tex");
		File dictionaryPolishIndexFile = new File(destinationDir, "dictionary_polish_index.tex");
		File dictionaryJmdictEntriesFile = new File(destinationDir, "dictionary_jmdict_entries.tex");
		
		dictionaryTitleFile.delete();
		dictionaryJapaneseIndexFile.delete();
		dictionaryPolishIndexFile.delete();
		dictionaryJmdictEntriesFile.delete();
	}
	
	public static PolishJapaneseLatexContent generateLatexDictonaryEntries(List<JMdict.Entry> entryList, File destinationDir, boolean commonOnly) throws IOException {
		
		PolishJapaneseLatexContent polishJapaneseLatexContent = new PolishJapaneseLatexContent();
		
		// generowanie tytulu i filtrowanie
		if (commonOnly == false) { // wszystkie slowa
			polishJapaneseLatexContent.title = "\\newcommand*\\dictionaryTitle{\n"
					+ "{\\huge\\bfseries Mały skromny japoński słownik \\par}\n"
					+ "\\vspace{2cm} \\normalsize{Wersja pełna \\par}\n"
					+ "}";
						
		} else {
			polishJapaneseLatexContent.title = "\\newcommand*\\dictionaryTitle{\n"
					+ "{\\huge\\bfseries Mały skromny japoński słownik \\par}\n"
					+ "\\vspace{2cm} \\normalsize{Wersja z powszechnie używanymi słowami \\par}\n"
					+ "}";
			
			// filtrowanie
			entryList = entryList.stream().filter(f -> {
				MiscInfo misc = f.getMisc();
				
				if (misc != null) {
					OldPolishJapaneseDictionaryInfo oldPolishJapaneseDictionary = misc.getOldPolishJapaneseDictionary();
					
					if (oldPolishJapaneseDictionary != null) {
						return oldPolishJapaneseDictionary.getAttributeList().stream().filter(f2 -> AttributeType.COMMON_WORD.name().equals(f2.getType()) == true).count() > 0;
					}
				}
				
				return false;
			}).collect(Collectors.toList());
		}		
		
		// generowanie indeksu japonskiego
		generateJapaneseIndex(polishJapaneseLatexContent, entryList);
		
		// generowanie indeksu polskiego
		generatePolishIndex(polishJapaneseLatexContent, entryList);
		
		// generowanie slow
		generateJMDictEntries(polishJapaneseLatexContent, entryList);
		
		// wyczyszczenie starych wygenerowanych plikow
		clearLatexDictonaryEntries(destinationDir);
		
		// zapis do pliku
		File dictionaryTitleFile = new File(destinationDir, "dictionary_title.tex");
		Files.write(dictionaryTitleFile.toPath(), polishJapaneseLatexContent.title.getBytes(), StandardOpenOption.CREATE_NEW);		
		
		File dictionaryJapaneseIndexFile = new File(destinationDir, "dictionary_japanese_index.tex");
		Files.write(dictionaryJapaneseIndexFile.toPath(), polishJapaneseLatexContent.japaneseIndex.getBytes(), StandardOpenOption.CREATE_NEW);

		File dictionaryPolishIndexFile = new File(destinationDir, "dictionary_polish_index.tex");
		Files.write(dictionaryPolishIndexFile.toPath(), polishJapaneseLatexContent.polishIndex.getBytes(), StandardOpenOption.CREATE_NEW);
		
		File dictionaryJmdictEntriesFile = new File(destinationDir, "dictionary_jmdict_entries.tex");
		Files.write(dictionaryJmdictEntriesFile.toPath(), polishJapaneseLatexContent.jmdictEntries.getBytes(), StandardOpenOption.CREATE_NEW);
		
		return polishJapaneseLatexContent;
	}

	private static void generateJapaneseIndex(PolishJapaneseLatexContent polishJapaneseLatexContent, List<JMdict.Entry> entryList) {
				
		// mapowania do generowania indeksu
		Map<KanaRomajiKey, List<KanjiKanaPair>> japaneseIndexMap = new TreeMap<>();
				
		// chodzimy po wszystkich slowach
		for (pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry entry : entryList) {
			
			// pobieramy tylko widoczne czytania
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
			
			// chodzimy po wszystkich czytania i dodajemy do mapy
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				String kana = kanjiKanaPair.getKana();
				String romaji = kanjiKanaPair.getRomaji();
				
				if (romaji == null || romaji.length() == 0 || romaji.equals("-") == true) {
					kana = otherSectionName;
					romaji = otherSectionName;
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
		
		latexContent.append("\\chapter{Indeks słów japońskich}\n");		
		
		for (Entry<String, List<Entry<KanaRomajiKey, List<KanjiKanaPair>>>> indexSectionEntrySet : indexSection.entrySet()) {
			
			String section = indexSectionEntrySet.getKey();
			List<Entry<KanaRomajiKey, List<KanjiKanaPair>>> indexSectionList = indexSectionEntrySet.getValue();
			
			// na razie nie generujemy sekcji dla innych
			if (section == otherSectionName) {
				continue;
			}
			
			// generowanie sekcji
			generateJapaneseIndexSection(latexContent, section, indexSectionList);
		}
		
		// generowanie dla sekcji inne
		generateJapaneseIndexSection(latexContent, otherSectionName, indexSection.get(otherSectionName));
		
		polishJapaneseLatexContent.japaneseIndex = latexContent.toString();
	}
	
	private static void generatePolishIndex(PolishJapaneseLatexContent polishJapaneseLatexContent, List<JMdict.Entry> entryList) {		
		
		// sortowanie po polsku
		Collator polishCollator = Collator.getInstance(new Locale("pl", "PL"));
		polishCollator.setStrength(Collator.SECONDARY);
		
		// mapowania do generowania indeksu
		Map<String, List<KanjiKanaPair>> polishIndexMap = new TreeMap<>(polishCollator);
				
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
						polishGlossValue = otherSectionName;
					}
					
					if (polishGlossValue.startsWith("α") == true) {
						polishGlossValue = otherSectionName;
					}

					if (polishGlossValue.startsWith("β") == true) {
						polishGlossValue = otherSectionName;
					}
					
					if (polishGlossValue.startsWith("ß") == true) {
						polishGlossValue = otherSectionName;
					}
					
					if (polishGlossValue.length() == 0) {
						polishGlossValue = otherSectionName;
					}
					
					polishGlossValue = polishGlossValue.trim();
					
					// indeks dla danego klucza
					List<KanjiKanaPair> kanjiKanaPairListForPolishGlossValue = polishIndexMap.get(polishGlossValue);
					
					// gdy nie ma tworzymy wpis
					if (kanjiKanaPairListForPolishGlossValue == null) {
						kanjiKanaPairListForPolishGlossValue = new ArrayList<KanjiKanaPair>();
						
						polishIndexMap.put(polishGlossValue, kanjiKanaPairListForPolishGlossValue);
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
		
		// grupowanie po sekcjach
		Map<String, List<Map.Entry<String, List<KanjiKanaPair>>>> indexSection = new TreeMap<>(polishCollator);
		
		for (Map.Entry<String, List<KanjiKanaPair>> polishIndexMapEntry : polishIndexMap.entrySet()) {
			
			String polishGlossValueKey = polishIndexMapEntry.getKey();
			String section;
			
			if (polishGlossValueKey == otherSectionName) {
				section = otherSectionName;
				
			} else {
				section = polishGlossValueKey.substring(0, 1).toUpperCase();
			}
			
			// czy taka sekcja wystepuje
			List<Entry<String, List<KanjiKanaPair>>> entrySetListForSection = indexSection.get(section);
			
			if (entrySetListForSection == null) {
				entrySetListForSection = new ArrayList<>();
				
				indexSection.put(section, entrySetListForSection);
			}
			
			entrySetListForSection.add(polishIndexMapEntry);
		}
		
		// generowanie zawartosci dokumentu		
		StringBuffer latexContent = new StringBuffer();
		
		latexContent.append("\\chapter{Indeks słów polskich}\n");		
		
		for (Entry<String, List<Entry<String, List<KanjiKanaPair>>>> indexSectionEntrySet : indexSection.entrySet()) {
			
			String section = indexSectionEntrySet.getKey();
			List<Entry<String, List<KanjiKanaPair>>> indexSectionList = indexSectionEntrySet.getValue();
						
			// na razie nie generujemy sekcji dla innych
			if (section == otherSectionName) {
				continue;
			}
			
			// generowanie sekcji
			generatePolishIndexSection(latexContent, section, indexSectionList);
		}
		
		// generowanie dla sekcji inne
		generatePolishIndexSection(latexContent, otherSectionName, indexSection.get(otherSectionName));
				
		polishJapaneseLatexContent.polishIndex = latexContent.toString();		
	}
	
	private static void generateJapaneseIndexSection(StringBuffer latexContent, String section, List<Entry<KanaRomajiKey, List<KanjiKanaPair>>> indexSectionList) {
		if (indexSectionList == null) {
			return;
		}
		
		if (section != otherSectionName) {
			latexContent.append("\\section[" + section + "]{" + section + "}\n");
		} else {
			latexContent.append("\\section[Inne]{Inne}\n");
		}
		latexContent.append("\\begin{spacing}{1}\n");
		latexContent.append("\\begin{multicols}{3}\n");
		
		for (Entry<KanaRomajiKey, List<KanjiKanaPair>> currentSectionIndexEntry : indexSectionList) {
			
			// lista slow w danym miejscu
			List<KanjiKanaPair> kanjiKanaPairList = currentSectionIndexEntry.getValue();
			
			latexContent.append("\\noindent");			
			latexContent.append("\\footnotesize\n");
			
			if (section != otherSectionName) {
				latexContent.append("\\textbf{" + currentSectionIndexEntry.getKey().romaji + "}, " + currentSectionIndexEntry.getKey().kana);
				latexContent.append(markBoth(currentSectionIndexEntry.getKey().romaji + ", " + currentSectionIndexEntry.getKey().kana)).append(" ");
			}
			
			latexContent.append("\n\n");
										
			if (kanjiKanaPairList.size() > 0) {
				
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {

					if (kanjiKanaPair.getKanjiInfo() != null) {
						latexContent.append("\n" + kanjiKanaPair.getKanji());
					} else {
						latexContent.append("\n" + kanjiKanaPair.getKana());
					}
					
					latexContent.append("\\dotfill");
					latexContent.append("\\pageref{" + getEntryLabelKey(kanjiKanaPair.getEntry().getEntryId()) + "}");
								
					latexContent.append("\n\n");
				}					
			}	
		}

		latexContent.append("\\end{multicols}\n");
		latexContent.append("\\end{spacing}\n");
	}

	private static void generatePolishIndexSection(StringBuffer latexContent, String section, List<Entry<String, List<KanjiKanaPair>>> indexSectionList) {
		if (indexSectionList == null) {
			return;
		}
		
		if (section != otherSectionName) {
			latexContent.append("\\section[" + section + "]{" + section + "}\n");
		} else {
			latexContent.append("\\section[Inne]{Inne}\n");
		}
		
		latexContent.append("\\begin{spacing}{1}\n");
		latexContent.append("\\begin{multicols}{3}\n");
		
		for (Entry<String, List<KanjiKanaPair>> currentSectionIndexEntry : indexSectionList) {
			
			// lista slow w danym miejscu
			List<KanjiKanaPair> kanjiKanaPairList = currentSectionIndexEntry.getValue();
			
			// posortowanie
			Collections.sort(kanjiKanaPairList, new Comparator<KanjiKanaPair>() {

				@Override
				public int compare(KanjiKanaPair o1, KanjiKanaPair o2) {
					return o1.getRomaji().compareTo(o2.getRomaji());
				}
			});
			
			latexContent.append("\\noindent");			
			latexContent.append("\\footnotesize\n");
			
			latexContent.append("\\textbf{" + escapeLatexChars(currentSectionIndexEntry.getKey()) + "}");
			latexContent.append(markBoth(escapeLatexChars(currentSectionIndexEntry.getKey()))).append(" ");			
			latexContent.append("\n\n");
										
			if (kanjiKanaPairList.size() > 0) {
				
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
					
					latexContent.append(kanjiKanaPair.getKana());
					
					if (kanjiKanaPair.getKanjiInfo() != null) {
						latexContent.append(", " + kanjiKanaPair.getKanji());
					}
					
					// latexContent.append(", " + kanjiKanaPair.getRomaji());					
					
					latexContent.append("\\dotfill");
					latexContent.append("\\pageref{" + getEntryLabelKey(kanjiKanaPair.getEntry().getEntryId()) + "}");
								
					latexContent.append("\n\n");
				}					
			}	
		}

		latexContent.append("\\end{multicols}\n");
		latexContent.append("\\end{spacing}\n");
	}

	private static void generateJMDictEntries(PolishJapaneseLatexContent polishJapaneseLatexContent, List<JMdict.Entry> entryList) {
		
		// generowanie zawartosci dokumentu		
		StringBuffer latexContent = new StringBuffer();
				
		// pobranie listy wszystkich slowek
		List<JMdict.Entry> entriesList = new ArrayList<>(entryList);
		
		// podzielenie listy na mniejsze kawalki
		Map<String, List<JMdict.Entry>> entriesListGroupedBy = new TreeMap<>();
		
		String groupedByKey;
		
		for (JMdict.Entry entry : entriesList) {
			
			// tworzenie klucza grupowania
			if (	entry.getReadingInfoList().get(0).getKana().getRomaji() == null ||
					entry.getReadingInfoList().get(0).getKana().getRomaji().equals("") == true ||
					entry.getReadingInfoList().get(0).getKana().getRomaji().startsWith("-") == true) {
				groupedByKey = otherSectionName;
			} else {
				groupedByKey = entry.getReadingInfoList().get(0).getKana().getRomaji().substring(0, 1).toUpperCase();
			}
						
			// pobranie listy dla danej grupy
			List<JMdict.Entry> groupedByKeyEntriesList = entriesListGroupedBy.get(groupedByKey);
			
			if (groupedByKeyEntriesList == null) { // nowa grupa, tworzymy nowa
				groupedByKeyEntriesList = new ArrayList<JMdict.Entry>();
				
				entriesListGroupedBy.put(groupedByKey, groupedByKeyEntriesList);
			}
			
			// dodanie wpisu do danej grupy
			groupedByKeyEntriesList.add(entry);			
		}
		
		latexContent.append("\\chapter{Spis słów}\n");
		latexContent.append("\\begin{spacing}{0.1}\n");
		
		for (Map.Entry<String, List<JMdict.Entry>> groupedByKeyEntriesListEntry : entriesListGroupedBy.entrySet()) {
			
			// pobieramy wszystkie wpisy z danej grupy
			List<JMdict.Entry> groupedByKeyEntriesList = groupedByKeyEntriesListEntry.getValue();
			
			// posortowanie jej
			Collections.sort(groupedByKeyEntriesList, new Comparator<JMdict.Entry>() {

				@Override
				public int compare(JMdict.Entry o1, JMdict.Entry o2) {
					String o1S = o1.getReadingInfoList().get(0).getKana().getRomaji();
					String o2S = o2.getReadingInfoList().get(0).getKana().getRomaji();
					
					return o1S.compareTo(o2S);
				}
			});
						
			// generowanie tytulu grupy
			String sectionName = groupedByKeyEntriesListEntry.getKey(); 

			// na razie nie generujemy sekcji dla innych
			if (sectionName == otherSectionName) {
				continue;
			}
			
			// generowanie sekcji			
			generateJMDictEntriesSection(latexContent, sectionName, groupedByKeyEntriesList);
		}
		
		// i na koniec jeszcze sekcja inne
		generateJMDictEntriesSection(latexContent, otherSectionName, entriesListGroupedBy.get(otherSectionName));
		
		latexContent.append("\\end{spacing}\n");
				
		polishJapaneseLatexContent.jmdictEntries = latexContent.toString();
	}
	
	private static void generateJMDictEntriesSection(StringBuffer latexContent, String sectionName, List<JMdict.Entry> entriesList) {
		
		if (entriesList == null) {
			return;
		}
		
		if (sectionName == otherSectionName) {
			latexContent.append("\\section[Inne]{Inne}\n");	
		} else {
			latexContent.append("\\section{" + sectionName + "}\n");
		}
		
		// latexContent.append("\\begin{multicols}{2}\n");
		
		// lista wszystkich znanych entry (potrzebne do generowania linkow)
		Set<Integer> knownEntryIds = new TreeSet<>();
		
		for (JMdict.Entry entry : entriesList) {
			knownEntryIds.add(entry.getEntryId());
		}
		
		for (JMdict.Entry entry : entriesList) {
			
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
			
			latexContent.append("\\phantomsection\n");
			latexContent.append("\\label{" + getEntryLabelKey(entry.getEntryId()) + "}");
			
			latexContent.append("\\begin{description}[style=multiline, leftmargin=4.5cm]\n\n");
			latexContent.append("    \\item[Słowo] \n");
			latexContent.append("    \\begin{itemize}[label={}]\n");
			
			for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				latexContent.append("        \\item ");
				
				boolean wasItemData = false;
				
				// kanji
				if (kanjiKanaPair.getKanji() != null) {
					wasItemData = true;
					
					latexContent.append(cjkFakeBold(kanjiKanaPair.getKanji()));
					
					KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
					
					List<KanjiAdditionalInfoEnum> kanjiAdditionalInfoList = kanjiInfo.getKanjiAdditionalInfoList();
					
					if (kanjiAdditionalInfoList.size() > 0) {
						List<String> translateToPolishKanjiAdditionalInfoEnum = Dictionary2HelperCommon.translateToPolishKanjiAdditionalInfoEnum(kanjiAdditionalInfoList);
						
						latexContent.append("(\\textit{");
						latexContent.append(translateToPolishKanjiAdditionalInfoEnum.stream().collect(Collectors.joining (", ")));
						latexContent.append("})");
					}											
				}
				
				// kana
				ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
				
				if (StringUtils.isBlank(kanjiKanaPair.getKana()) == false) {
					
					if (wasItemData == true) {
						latexContent.append(", ");
					}
					
					wasItemData = true;
					
					latexContent.append(cjkFakeBold(kanjiKanaPair.getKana()));
				
					List<ReadingAdditionalInfoEnum> readingAdditionalInfoList = readingInfo.getReadingAdditionalInfoList();
					
					if (readingAdditionalInfoList.size() > 0) {
						List<String> translateToPolishReadingAdditionalInfoEnum = Dictionary2HelperCommon.translateToPolishReadingAdditionalInfoEnum(readingAdditionalInfoList);
						
						latexContent.append("(\\textit{");
						latexContent.append( translateToPolishReadingAdditionalInfoEnum.stream().collect(Collectors.joining (", ")));
						latexContent.append("})");
					}					
				}
				
				if (StringUtils.isBlank(kanjiKanaPair.getRomaji()) == false) {
					
					if (wasItemData == true) {
						latexContent.append(", ");
					}
					
					wasItemData = true;

					latexContent.append("\\textbf{" + kanjiKanaPair.getRomaji() + "}");	
				}
				
				latexContent.append("\n");
			}
			
			latexContent.append("    \\end{itemize}\n");
					
			// informacje ogolne do calosci slowa
			List<Info> infoList = entry.getInfoList();
			
			if (infoList.size() > 0) {
				List<Info> polishInfoList = Dictionary2HelperCommon.getPolishInfoList(infoList);
				
				latexContent.append("    \\item[Informacje dodatkowe] \n");
				latexContent.append("    \\begin{itemize}[label={}]\n");

				for (Info info : polishInfoList) {
					latexContent.append("        \\item " + info.getValue() + "\n");
				}
				
				latexContent.append("    \\end{itemize}\n");
			}
			
			// znaczenie
			List<Sense> senseList = entry.getSenseList();
			
			if (senseList.size() > 0) {

				latexContent.append("    \\item[Znaczenie] \n");
				latexContent.append("    \\begin{itemize}[label={}]\n");

				for (int senseIdx = 0; senseIdx < senseList.size(); ++senseIdx) {
					
					Sense sense = senseList.get(senseIdx);
										
					latexContent.append("        \\item[\\circled{" + (senseIdx + 1) + "}] ");
					
					if (senseIdx != 0) {
						latexContent.append("\\vspace{5pt} ");
					}
					
					// informacje dodatkowe (szczegoly), ktore wyswietla sie pod danym znaczeniem
					List<String> latexContentDetails = new ArrayList<>();
					
					// polskie znaczenia
					List<Gloss> polishGlossList = Dictionary2HelperCommon.getPolishGlossList(sense.getGlossList());
					SenseAdditionalInfo polishAdditionalInfo = Dictionary2HelperCommon.findFirstPolishAdditionalInfo(sense.getAdditionalInfoList());
					
		            boolean existsGtypeNull = polishGlossList.stream().filter(f -> f.getGType() == null).count() > 0;
					
					if (polishGlossList.size() > 0) {
						
						boolean wasGloss = false;
						
						if (existsGtypeNull == true) { // jezeli istnieje gType rowne null, wiec pokazujemy tlumaczenia i wszelkie wyjasnienia osobno
							
							// lista tlumaczen - gtype = null
			    			for (int currentGlossIdx = 0; currentGlossIdx < polishGlossList.size(); ++currentGlossIdx) {
			    				
			    				Gloss polishGross = polishGlossList.get(currentGlossIdx);
			    				
			    				if (polishGross.getGType() == null) {
			    					
									if (wasGloss == true) {
										latexContent.append("        \\item ");
									}

									latexContent.append(escapeLatexChars(polishGross.getValue()) + 
											(polishGross.getGType() != null ? " (" + Dictionary2HelperCommon.translateToPolishGlossType(polishGross.getGType()) + ")" : "") + "\n");
									
									wasGloss = true;
			    				}				
			    			}
			    			
			                // lista tlumaczen - gtype != null jako informacje dodatkowe
			    			for (int currentGlossIdx = 0; currentGlossIdx < polishGlossList.size(); ++currentGlossIdx) {
			    				
			    				Gloss polishGross = polishGlossList.get(currentGlossIdx);
			    							    				
			    				Gloss gloss = polishGlossList.get(currentGlossIdx);
			    				
			    				if (gloss.getGType() != null) {			    					
			    					latexContentDetails.add("\\textit{" + escapeLatexChars(polishGross.getValue()) + 
											(polishGross.getGType() != null ? " (" + Dictionary2HelperCommon.translateToPolishGlossType(polishGross.getGType()) + ")" : "") + "}\n");						
			    				}
			    			}
			    			
						} else { // jezeli nie ma gType = null, wiec pokazujemy po staremu
							
			    			for (int currentGlossIdx = 0; currentGlossIdx < polishGlossList.size(); ++currentGlossIdx) {
			    				
			    				Gloss polishGross = polishGlossList.get(currentGlossIdx);			    				
			    					
								if (wasGloss == true) {
									latexContent.append("        \\item ");
								}

								latexContent.append(escapeLatexChars(polishGross.getValue()) + 
										(polishGross.getGType() != null ? " (" + Dictionary2HelperCommon.translateToPolishGlossType(polishGross.getGType()) + ")" : "") + "\n");
								
								wasGloss = true;
			    			}
						}
					}
										
					// informacje dodatkowe												
					if (polishAdditionalInfo != null) {
						latexContentDetails.add("\\textit{" + escapeLatexChars(polishAdditionalInfo.getValue() + "}"));						
					}
					
					// ograniczone do kanji/kana					
					if (sense.getRestrictedToKanjiList().size() > 0 || sense.getRestrictedToKanaList().size() > 0) {
						List<String> restrictedToKanjiKanaList = new ArrayList<>();
						
						restrictedToKanjiKanaList.addAll(sense.getRestrictedToKanjiList());
						restrictedToKanjiKanaList.addAll(sense.getRestrictedToKanaList());
																		
						// zamiana na przetlumaczona postac
						String restrictedToKanjiKanaString = "tylko dla: " + String.join("; ", restrictedToKanjiKanaList);
						
						latexContentDetails.add("\\textit{" + escapeLatexChars(restrictedToKanjiKanaString + "}"));
					}
					
					// czesci mowy
					if (sense.getPartOfSpeechList().size() > 0) { 						
						// zamiana na przetlumaczona postac
						String translatedToPolishPartOfSpeechEnum = String.join("; ", Dictionary2HelperCommon.translateToPolishPartOfSpeechEnum(sense.getPartOfSpeechList()));
								    			
						latexContentDetails.add("\\textit{" + escapeLatexChars(translatedToPolishPartOfSpeechEnum + "}"));				
					}
					
					// kategoria slowa
					if (sense.getFieldList().size() > 0) {						
						// zamiana na przetlumaczona postac
						String translatedfieldEnum = String.join("; ", Dictionary2HelperCommon.translateToPolishFieldEnumList(sense.getFieldList()));
								    			
						latexContentDetails.add("\\textit{" + escapeLatexChars(translatedfieldEnum + "}"));						
					}

					// roznosci
					if (sense.getMiscList().size() > 0) {						
						// zamiana na przetlumaczona postac
						String translatedMiscEnum = String.join("; ", Dictionary2HelperCommon.translateToPolishMiscEnumList(sense.getMiscList()));
		    			
						latexContentDetails.add("\\textit{" + escapeLatexChars(translatedMiscEnum + "}"));						
					}	
					
					// dialekt
					if (sense.getDialectList().size() > 0) {						
						// zamiana na przetlumaczona postac
						String translatedDialectEnum = String.join("; ", Dictionary2HelperCommon.translateToPolishDialectEnumList(sense.getDialectList()));
								    			
						latexContentDetails.add("\\textit{" + escapeLatexChars(translatedDialectEnum + "}"));				
					}	
					
					// zagraniczne pochodzenie slowa
					List<LanguageSource> languageSourceList = entry.getLanguageSourceList();
					
					if (languageSourceList.size() > 0) {						
						for (LanguageSource languageSource : languageSourceList) {
							StringBuffer singleLanguageSource = new StringBuffer();
							
							String languageCodeInPolish = Dictionary2HelperCommon.translateToPolishLanguageCode(languageSource.getLang());
							String languageValue = languageSource.getValue();
							String languageLsWasei = Dictionary2HelperCommon.translateToPolishLanguageSourceLsWaseiEnum(languageSource.getLsWasei());
							
							if (languageValue != null && languageValue.equals("") == false) {
								singleLanguageSource.append(languageCodeInPolish + ": " + languageValue);
								
							} else {
								singleLanguageSource.append(Dictionary2HelperCommon.translateToPolishLanguageCodeWithoutValue(languageSource.getLang()));
							}
							
							if (languageLsWasei != null && languageLsWasei.equals("") == false) {
								singleLanguageSource.append(", ").append(languageLsWasei);
							}

							latexContentDetails.add(singleLanguageSource.toString());
						}											
					}
					
					// odnosniki do innych slow (przejmuje obsluge dwoch elementow powyzej zakomentowanych)
					List<Xref> referenceToAnotherKanjiKanaList = sense.getReferenceToAnotherKanjiKanaList();
					
					if (referenceToAnotherKanjiKanaList.size() > 0) {
						
						for (int referenceToAnotherKanjiKanaListIdx = 0; referenceToAnotherKanjiKanaListIdx < referenceToAnotherKanjiKanaList.size(); ++referenceToAnotherKanjiKanaListIdx) {
							
							Xref currentXRef = referenceToAnotherKanjiKanaList.get(referenceToAnotherKanjiKanaListIdx);
							
							StringBuffer xrefText = new StringBuffer();
							
							xrefText.append(Dictionary2HelperCommon.translateXrefType(currentXRef.getType()) + " ");
											
							if (currentXRef.getDict() != null) {
								xrefText.append("\\textbf{" + currentXRef.getDict() + "}: ");
							}
							
							if (currentXRef.getXKanji() != null) {
								xrefText.append(cjkFakeBold(currentXRef.getXKanji()));
							}
							
							if (currentXRef.getXKana() != null) {
								if (currentXRef.getXKanji() != null) {
									xrefText.append(" / ");
								}
								xrefText.append(cjkFakeBold(currentXRef.getXKana()));
							}							
														
							if (currentXRef.getSeq() != null && knownEntryIds.contains(currentXRef.getSeq()) == true) {
								xrefText.append(" - \\pageref{" + getEntryLabelKey(currentXRef.getSeq()) + "}");
							}
														
				    		latexContentDetails.add(xrefText.toString());	
						}
					}					
					
					// dopisanie szczegolow
					if (latexContentDetails.size() > 0) {
						latexContent.append("    \\begin{itemize}[label={$\\cdot$}]\n");
						
						for (String currentDetails : latexContentDetails) {
							latexContent.append("    \\item " + currentDetails + "\n");	
						}
						
						latexContent.append("    \\end{itemize}\n");
					}					
				}
				
				latexContent.append("    \\end{itemize}\n");
			}			
							
			latexContent.append("\\end{description}\n");						
			latexContent.append("\\noindent\\makebox[\\linewidth]{\\rule{\\linewidth}{0.4pt}}\n\n");
		}
		
		// latexContent.append("\\end{multicols}\n");
	}
	
	private static String getEntryLabelKey(Integer entryId) {
		return "entry_" + entryId;
	}

	//////// Stary kod	
	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
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
		private String title;
		private String japaneseIndex;
		private String polishIndex;		
		private String jmdictEntries;		
	}
	
	// klasa pomocnicza
	private static class KanaRomajiKey implements Comparable<KanaRomajiKey> {
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
