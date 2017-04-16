package pl.idedyk.japanese.dictionary.tools;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper.KanaWord;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class LatexDictionaryGenerator {
	
	final static String otherSectionName = "Inne";

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv" });
		
		List<String> latexDictonaryEntries = generateLatexDictonaryEntries(polishJapaneseEntries);
				
		FileWriter fileWriter = new FileWriter("pdf_dictionary/dictionary_entries.tex");
		
		for (String latexString : latexDictonaryEntries) {
			
			System.out.print(latexString);
			
			fileWriter.write(latexString);
		}		
		
		fileWriter.close();
	}
	
	public static List<String> generateLatexDictonaryEntries(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		List<String> result = new ArrayList<String>();
		
		//
				
		// utworzenie sekcji
		Map<String, List<PolishJapaneseEntry>> sectionMap = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kana = polishJapaneseEntry.getKana();
			
			String sectionName = kana.substring(0, 1);
			
			if (Utils.isKana(sectionName.charAt(0)) == false || sectionName.equals("ゝ") == true || sectionName.equals("ゞ") == true
					|| sectionName.equals("ヶ") == true || sectionName.equals("ー") == true || sectionName.equals("ヽ") == true ||
					sectionName.equals("ヾ") == true) {
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
	
	private static void generateSection(List<String> result, String key, List<PolishJapaneseEntry> keyList) {
		
		KanaHelper kanaHelper = new KanaHelper();
		
		String sectionName = null;
		
		if (key != otherSectionName) {
			sectionName = key + " (" + convertToRomaji(key, kanaHelper) + ")";
			
		} else {
			sectionName = otherSectionName;
		}
		
		result.add("%----------------------------------------------------------------------------------------\n");
		result.add(String.format("%s\tSekcja: %s\n", "%", sectionName));
		result.add("%----------------------------------------------------------------------------------------\n\n");
		result.add(String.format("\\section*{%s}\n", sectionName));
		
		result.add("\\begin{multicols}{2}\n\n");
		
		List<PolishJapaneseEntry> polishJapaneseEntryList = keyList;
		
		Collections.sort(polishJapaneseEntryList, new Comparator<PolishJapaneseEntry>() {

			@Override
			public int compare(PolishJapaneseEntry o1, PolishJapaneseEntry o2) {
				return o1.getKana().compareTo(o2.getKana());
			}
		});
		
		//int counter = 0;
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {
			
			//if (counter > 10) {
			//	break;
			//}
			
			result.add(generateDictionaryEntry(polishJapaneseEntry));
			
			//counter++;			
		}			
		
		result.add("\\end{multicols}\n\n");
	}
	
	private static String generateDictionaryEntry(PolishJapaneseEntry polishJapaneseEntry) {
		
		StringBuffer result = new StringBuffer();
		
		// translates, info
		
		String kanji = polishJapaneseEntry.getKanji();
		
		String kana = polishJapaneseEntry.getKana();
		String romaji = polishJapaneseEntry.getRomaji();
		
		//result.append("\\noindent ");
		
		if (kanji.equals("-") == false) {
		
			// na gorze strony slowa kanji
			// result.append(markBoth(kanji)).append(" ");
			
			// kanji
			result.append(kanji).append(" ");
			
		} else {
			// na gorze slowa kana
			result.append(markBoth(kana)).append(" ");
		}
		
		// pogrubienie kana
		result.append(cjkFakeBold(kana)).append(" ");
		result.append(markBoth(kana)).append(" ");
		
		// pogrubienie romaji
		result.append(textbf(romaji)).append(" ");

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
		}		
		
		result.append("\\vspace{0.3cm} \n\n");
		
		return result.toString();
	}
	
	private static String escapeLatexChars(String text) {
		
		text = text.replaceAll("\\^", "\\\\^{}");
		text = text.replaceAll("\\&", "\\\\&");
		
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

	private static String convertToRomaji(String kana, KanaHelper kanaHelper) {
		
		KanaWord kanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(true), false);
		
		return kanaHelper.createRomajiString(kanaWord);
	}
}
