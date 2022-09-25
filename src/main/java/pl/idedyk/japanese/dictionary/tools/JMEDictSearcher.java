package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;

public class JMEDictSearcher {
	
	private static Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
	
	private static KanaHelper kanaHelper = new KanaHelper();
	
	//

	public static void main(String[] args) throws Exception {

		System.out.println("Wczytywanie slownika...");
		
		System.out.println("Indeksowanie...");

		// tworzenie indeksu lucene
		dictionary2Helper.findInJMdict("indexing");

		System.out.println("Gotowe...");

		System.out.println();
		
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			System.out.print("Szukane słowo lub numer grupy (format: grupa: [numer]): ");

			String searchWord = consoleReader.readLine();

			if (searchWord == null || searchWord.equals("koniec") == true) {
				break;
			}
			
			if (searchWord.equals("") == true) {
				continue;
			}
			
			if (searchWord.startsWith("grupa:") == true) {
				
				String groupNoString = searchWord.substring(6).trim();
				
				Integer groupNo;
				
				try {
					groupNo = Integer.parseInt(groupNoString);
					
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawny numer grupy\n");
					
					continue;
				}
				
				Entry entry = dictionary2Helper.getEntryFromPolishDictionary(groupNo);
								
				if (entry == null) {
					System.out.println("Brak grupy o podanym identyfikatorze");
					
					continue;
				}
				
				System.out.println("=========================\n");
				
				List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry);
								
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
					printKanjiKanaPair(entry, kanjiKanaPair);
				}				
				
			} else {
				
				List<Entry> entryList = dictionary2Helper.findInJMdict(searchWord);
				
				System.out.println("=========================\n");
				
				for (Entry entry : entryList) {
					
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry);
					
					for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
						printKanjiKanaPair(entry, kanjiKanaPair);
					}				
				}
			}
		}

		consoleReader.close();
	}
	
	private static void printKanjiKanaPair(Entry emtry, KanjiKanaPair kanjiKanaPair) {
		
		System.out.println("Identyfikator grupy: " + emtry.getEntryId() + "\n");
		
		KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
		
		System.out.println("Kanji info"); //\n\t\t\t" + kanji + " - " + kana + " - " + romaji);
		
		if (kanjiInfo != null) {		
			System.out.println("\tKanji: " + kanjiInfo.getKanji());
			System.out.println("\tInformacje dodatkowe: " + kanjiInfo.getKanjiAdditionalInfoList());
			System.out.println("\tCzęstotliwość występowania: " + kanjiInfo.getRelativePriorityList() + "\n");
			
		} else {
			System.out.println("\t-\n");
		}
		
		//
		
		ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
				
		System.out.println("Czytanie");
		
		System.out.println("\tKana, wartość: " + readingInfo.getKana().getValue());
		System.out.println("\tKana, romaji: " + getRomaji(readingInfo.getKana().getValue()));
		System.out.println("\tKana, typ kana: " + Dictionary2Helper.getKanaType(readingInfo.getKana().getValue()) + "\n");

		System.out.println("\tInformacje dodatkowe: " + readingInfo.getReadingAdditionalInfoList());
		System.out.println("\tCzęstotliwość występowania: " + readingInfo.getRelativePriorityList() + "\n");
		
		//
		
		System.out.println("\n---\n\n");

		
		/*
		Set<String> wordTypeList = groupEntry.getWordTypeList();

		Integer groupEntryGroupId = groupEntry.getGroup().getId();
		
		String kanji = groupEntry.getKanji();
		List<String> kanjiInfoList = groupEntry.getKanjiInfoList() != null ? groupEntry.getKanjiInfoList() : new ArrayList<String>();

		String kana = groupEntry.getKana();
		List<String> kanaInfoList = groupEntry.getKanaInfoList() != null ? groupEntry.getKanaInfoList() : new ArrayList<String>();

		String romaji = groupEntry.getRomaji();

		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList() != null ? groupEntry.getTranslateList() : new ArrayList<GroupEntryTranslate>();
		List<String> additionalInfoList = new ArrayList<String>(); //groupEntry.getAdditionalInfoList();

		System.out.println("Czytanie:\n\t\t\t" + kanji + " - " + kana + " - " + romaji);

		System.out.println("Rodzaj słowa:");

		for (String currentWordTypeList : wordTypeList) {				
			System.out.println("\t\t\t" + currentWordTypeList + " - " + entityMapper.getDesc(currentWordTypeList));				
		}

		System.out.println("Info do kanji:");

		for (String currentKanjiInfo : kanjiInfoList) {				
			System.out.println("\t\t\t" + currentKanjiInfo + " - " + entityMapper.getDesc(currentKanjiInfo));				
		}

		System.out.println("Info do kana:");

		for (String currentKanaInfo : kanaInfoList) {				
			System.out.println("\t\t\t" + currentKanaInfo + " - " + entityMapper.getDesc(currentKanaInfo));				
		}

		System.out.println("Tłumaczenie:");

		for (GroupEntryTranslate groupEntryTranslate : translateList) {
			System.out.println("\t\t\t" + groupEntryTranslate.getTranslate());
		}

		System.out.println("Dodatkowe informacje:\t");

		for (String currentAdditionalInfo : additionalInfoList) {
			System.out.println("\t\t\t" + currentAdditionalInfo);
		}
		
		*/
	}
	
	private static String getRomaji(String kana) {
		return kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true));
	}
}
