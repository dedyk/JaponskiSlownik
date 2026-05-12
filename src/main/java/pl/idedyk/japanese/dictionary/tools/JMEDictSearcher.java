package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Info;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.SenseAdditionalInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Xref;

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
				
				Entry entry = dictionary2Helper.getJMdictEntry(groupNo);
								
				if (entry == null) {
					System.out.println("Brak grupy o podanym identyfikatorze");
					
					continue;
				}
				
				System.out.println("=========================\n");
				
				List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
				
				for (int idx = 0; idx < kanjiKanaPairList.size(); ++idx) {
					printKanjiKanaPair(entry, kanjiKanaPairList.get(idx), idx + 1);
				}
				
			} else {
				
				List<Entry> entryList = dictionary2Helper.findInJMdict(searchWord);
				
				System.out.println("=========================\n");
				
				for (Entry entry : entryList) {
					
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
					
					for (int idx = 0; idx < kanjiKanaPairList.size(); ++idx) {
						
						KanjiKanaPair kanjiKanaPair = kanjiKanaPairList.get(idx);

						// dodatkowe filtrowanie, ale tylko, gdy wyszukiwane jest japonskie slowo
						boolean showKanjiKanaPair = true;
						
						if (Utils.isAllJapaneseChars(searchWord) == true) {
							
							showKanjiKanaPair = false;
							
							if (kanjiKanaPair.getKanji() != null && kanjiKanaPair.getKanji().equals(searchWord) == true) {
								showKanjiKanaPair = true;
								
							} else if (kanjiKanaPair.getKana().equals(searchWord) == true) {
								showKanjiKanaPair = true;
								
							}
						}
						
						if (showKanjiKanaPair == true) {
							printKanjiKanaPair(entry, kanjiKanaPair, idx + 1);
						}
					}
				}
			}
		}

		consoleReader.close();
	}
	
	private static void printKanjiKanaPair(Entry emtry, KanjiKanaPair kanjiKanaPair, int counter) {
		
		System.out.println("Identyfikator słowa: " + emtry.getEntryId() + "/" + counter + "\n");
		
		KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
				
		if (kanjiInfo != null) {
			System.out.println("Kanji info"); //\n\t\t\t" + kanji + " - " + kana + " - " + romaji);
			
			System.out.println("\tKanji: " + kanjiInfo.getKanji());
			
			if (kanjiInfo.getKanjiAdditionalInfoList().size() > 0) {
				System.out.println("\tInformacje dodatkowe: " + kanjiInfo.getKanjiAdditionalInfoList());
			}
			
			if (kanjiInfo.getRelativePriorityList().size() > 0) {
				System.out.println("\tCzęstotliwość występowania: " + kanjiInfo.getRelativePriorityList());
			}
		} 
		
		//
		
		ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
				
		System.out.println("\nCzytanie");
		
		System.out.println("\tKana, wartość: " + readingInfo.getKana().getValue());
		System.out.println("\tKana, romaji: " + getRomaji(readingInfo.getKana().getValue()));
		System.out.println("\tKana, typ kana: " + Dictionary2Helper.getKanaType(readingInfo.getKana().getValue()));

		if (readingInfo.getReadingAdditionalInfoList().size() > 0) {
			System.out.println("\tInformacje dodatkowe: " + readingInfo.getReadingAdditionalInfoList());
		}
		
		if (readingInfo.getRelativePriorityList().size() > 0) {
			System.out.println("\tCzęstotliwość występowania: " + readingInfo.getRelativePriorityList());
		}
		
		// info
		List<Info> infoList = kanjiKanaPair.getEntry().getInfoList();
		
		if (infoList.size() > 0) {
			System.out.println("\nInfo");
			
			for (Info info : infoList) {
				System.out.println("\tInfo, typ: " + info.getInfType().value());
				System.out.println("\tInfo, wartość: " + info.getValue());
			}			
		}

		List<LanguageSource> languageSourceList = kanjiKanaPair.getEntry().getLanguageSourceList();
		
		if (languageSourceList.size() > 0) {
			System.out.println("\n\tJęzykowe źródło");
			
			for (LanguageSource languageSource : languageSourceList) {
				System.out.println("\t\tWartość: " + languageSource.getValue());
				System.out.println("\t\tJęzyk: " + languageSource.getLang());
				
				if (languageSource.getLsType() != null) {
					System.out.println("\t\tTyp: " + languageSource.getLsType());
				}
				
				if (languageSource.getLsWasei() != null) {
					System.out.println("\t\tWasei: " + languageSource.getLsWasei());
				}
			}
		}
		
		//
		
		List<Sense> senseList = kanjiKanaPair.getSenseList();
		
		System.out.println("\nZnaczenie");
		
		for (int senseListIdx = 0; senseListIdx < senseList.size(); ++senseListIdx) {
			
			System.out.println("\t" + (senseListIdx + 1));
			
			Sense sense = senseList.get(senseListIdx);
			
			System.out.println("\t\tCzęść mowy: " + sense.getPartOfSpeechList());
			
			if (sense.getDialectList().size() > 0) {
				System.out.println("\t\tDialekt: " + sense.getDialectList());
			}
			
			if (sense.getFieldList().size() > 0) {
				System.out.println("\t\tDziedzina: " + sense.getFieldList());
			}
			
			if (sense.getMiscList().size() > 0) {
				System.out.println("\t\tRóżne informacje: " + sense.getMiscList());
			}		
					
			System.out.println("\n\t\tZnaczenie");
			
			List<Gloss> glossList = sense.getGlossList();
			
			for (Gloss gloss : glossList) {
				System.out.println("\t\t\t" + gloss.getValue() + (gloss.getGType() != null ? " (" + gloss.getGType() + ")" : ""));
			}
						
			List<SenseAdditionalInfo> additionalInfoList = sense.getAdditionalInfoList();
			
			if (additionalInfoList.size() > 0) {
				System.out.println("\n\t\tInformacje dodatkowe");
				
				for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {
					System.out.println("\t\t\t" + senseAdditionalInfo.getValue());
				}				
			}
						
			if (sense.getReferenceToAnotherKanjiKanaList().size() > 0) {
				System.out.println("\n\t\tOdniesienie do innego słowa");
				
				for (Xref xref : sense.getReferenceToAnotherKanjiKanaList()) {
					System.out.println("\t\t\tTyp: " + xref.getType().value());
					System.out.println("\t\t\tSłownik: " + xref.getDict());
					System.out.println("\t\t\tIdentyfikator słowa: " + xref.getSeq());
					System.out.println("\t\t\tNumer sensu: " + xref.getSno());
					System.out.println("\t\t\tKanji: " + xref.getXKanji());
					System.out.println("\t\t\tKana: " + xref.getXKana());
					System.out.println("\t\t\tWartość: " + xref.getValue());
				}
			}
			
			if (sense.getAntonymList().size() > 0) {
				System.out.println("\n\t\tPrzeciwieństwo");
				
				for (String antonym : sense.getAntonymList()) {
					System.out.println("\t\t\t" + antonym);
				}
			}
		}		
		
		System.out.println("\n---\n\n");
	}
	
	private static String getRomaji(String kana) {
		return kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true));
	}
}
