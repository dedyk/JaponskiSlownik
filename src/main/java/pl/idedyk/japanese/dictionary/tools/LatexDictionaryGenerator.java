package pl.idedyk.japanese.dictionary.tools;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class LatexDictionaryGenerator {
	
	final static String otherSectionName = "Inne";

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" });
		
		//
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
        TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");

        Helper.generateAdditionalInfoFromEdict(jmeNewDictionary, jmedictCommon, polishJapaneseEntries);
		
        //
        
		List<String> latexDictonaryEntries = generateLatexDictonaryEntries(polishJapaneseEntries);
				
		FileWriter fileWriter = new FileWriter("pdf_dictionary/dictionary_entries.tex");
		
		for (String latexString : latexDictonaryEntries) {
			
			System.out.print(latexString);
			
			fileWriter.write(latexString);
		}		
		
		fileWriter.close();
	}
	
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
			
			JMdict.Entry jmdictEntry = null;
			
			// sprawdzenie, czy wystepuje slowo w formacie JMdict
			List<Attribute> jmdictEntryIdAttributeList = polishJapaneseEntry.getAttributeList().getAttributeList(AttributeType.JMDICT_ENTRY_ID);
			
			if (jmdictEntryIdAttributeList != null && jmdictEntryIdAttributeList.size() > 0) { // cos jest
				
				// pobieramy entry id
				Integer entryId = Integer.parseInt(jmdictEntryIdAttributeList.get(0).getAttributeValue().get(0));
				
				// pobieramy z bazy danych
				jmdictEntry = dictionaryHelper.getEntryFromPolishDictionary(entryId);				
			}

			
			result.add(generateDictionaryEntry(polishJapaneseEntry, jmdictEntry));
			
			//counter++;			
		}			
		
		result.add("\\end{multicols}\n\n");
	}
	
	private static String generateDictionaryEntry(PolishJapaneseEntry polishJapaneseEntry, JMdict.Entry jmdictEntry) {
		
		StringBuffer result = new StringBuffer();
				
		// FIXME: tymczasowo
		// jmdictEntry = null;
		
		if (jmdictEntry == null) { // stary sposob generowania
			/*
			// FIXME: to jest ok !!!!!!!!!!!!!!!!!!!!!!!!
						
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
				
				result.append(escapeLatexChars(translate));
				
				wasTranslate = true;
			}
			
			// informacje dodatkowe
			String info = polishJapaneseEntry.getInfo();
			
			if (info != null && info.equals("") == false) {
				
				result.append(" ").append(cdot()).append(" ");
				
				result.append(escapeLatexChars(info));			
			}		*/
			
		} else if (jmdictEntry != null) { // nowy sposob generowania
			
			// laczenie kanji, kana i znaczen w pary
			List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(jmdictEntry);
			
			// wyszukanie konkretnego znaczenia dla naszeo slowka
			KanjiKanaPair kanjiKanaPair = Dictionary2HelperCommon.findKanjiKanaPair(kanjiKanaPairList, polishJapaneseEntry);

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
						result.append(" ").append(cdot()).append(" ");
					}
				}
				
				//result.append("test ");				
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

	/*
	private static String convertToRomaji(String kana, KanaHelper kanaHelper) {
		
		KanaWord kanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(true), false);
		
		return kanaHelper.createRomajiString(kanaWord);
	}
	*/
}
