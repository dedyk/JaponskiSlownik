package pl.idedyk.japanese.kanji2.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary2.app.GetEntryFromDictionary;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.HeaderInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class GetKanjiFromDictionary {
	
	public static void main(String[] args) throws Exception {
		
		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();

		// opcje
		Options options = new Options();

		int fixme = 1;
		
		options.addOption("k", "kanjis", true, "Kanjis ids");
		options.addOption("f", "file-kanjis", true, "File kanjis");
		options.addOption("p", "polish-dictionary", false, "Use polish dictionary");
		options.addOption("j", "kanjidic2-dictionary", false, "Use kanji dic2 dictionary");
		options.addOption("jp", "kanjidic2-polish-dictionary", false, "Use kanji dic2 or polish dictionary");

		options.addOption("h", "help", false, "Help");
		
		// parsowanie opcji		
		CommandLine commandLine = null;
		
		try {
			commandLine = commandLineParser.parse(options, args);
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage() + "\n");
			
			printHelp(options);
			
			System.exit(1);
		}	
		
		if (commandLine.hasOption("help") == true) {

			printHelp(options);
			
			System.exit(1);
		}
		
		//
		
		Set<String> kanjis = new LinkedHashSet<>();
		
		boolean usePolishDictionary = false;
		boolean useKanjidic2Dictionary = false;
		boolean useKanjidic2PolishDictionary = false;

		
		if (commandLine.hasOption("kanjis") == true && commandLine.hasOption("file-kanjis") == true) {
			
			printHelp(options);
			
			System.exit(1);			
			
		} else if (commandLine.hasOption("kanjis") == true) {
			
			String kanjisValue = commandLine.getOptionValue("kanjis");
			
			String[] kanjisValueSplitted = kanjisValue.split(",");
			
			for (String currentKanji : kanjisValueSplitted) {
				kanjis.add(currentKanji);
			}
			
		} else if (commandLine.hasOption("file-kanjis") == true) { 
			
			String fileNameEntryIds = commandLine.getOptionValue("file-kanjis");
			
			List<String> fileKanjis = readFile(fileNameEntryIds);

			for (String currentLine : fileKanjis) {
				for (int idx = 0; idx < currentLine.length(); ++idx) {
					kanjis.add(currentLine.substring(idx, idx + 1));
				}				
			}
		
		} else {
			
			printHelp(options);
			
			System.exit(1);			
		}
		
		//
		
		int checkCounter = 0;
		
		if (commandLine.hasOption("polish-dictionary") == true) {
			usePolishDictionary = true;	
			checkCounter++;
		}
		
		if (commandLine.hasOption("kanjidic2-dictionary") == true) {
			useKanjidic2Dictionary = true;
			checkCounter++;
		}

		if (commandLine.hasOption("kanjidic2-polish-dictionary") == true) {
			useKanjidic2PolishDictionary = true;
			checkCounter++;
		}
		
		if (checkCounter != 1) {			
			printHelp(options);
			
			System.exit(1);	
		}

		//
		
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();
		
		if (useKanjidic2Dictionary == true || useKanjidic2PolishDictionary == true) {			
			saveKanjiDic2AsHumanCsvConfig.addOldPolishTranslates = true;
			saveKanjiDic2AsHumanCsvConfig.shiftCells = true;
			saveKanjiDic2AsHumanCsvConfig.shiftCellsGenerateIds = true;
		}		

		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		Kanjidic2 result = kanji2Helper.createEmptyKanjidic2();
				
		Set<String> alreadyMetKanjiSet = new TreeSet<String>();
		
		// dodatkowe informacje
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (String currentKanji : kanjis) {
									
			if (alreadyMetKanjiSet.contains(currentKanji) == true) { // to slowo juz odwiedzalismy
				continue;
				
			} else {
				alreadyMetKanjiSet.add(currentKanji);
			}
			
			int fixme2 = 1;
			/*
			if (usePolishDictionary == true) { // szukamy w polskim slowniku
				
				
				
				Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(currentEntryId);
				
				if (entryFromPolishDictionary == null) { // nie znaleziono
					
					System.out.println("[Error] Can't find entry id in polish dictionary: " + currentEntryId);
					
					continue;					
				}
				
				result.add(entryFromPolishDictionary);				
			}
			*/
			
			if (useKanjidic2Dictionary == true) { // szukamy w slowniku angielskim
				
				CharacterInfo characterInfo = kanji2Helper.getKanjiFromKanjidic2(currentKanji);
				
				if (characterInfo == null) { // nie znaleziono
					System.out.println("[Errpr] Can't find kanji in kanji dic2: " + currentKanji);
					
					continue;										
				}
				
				CharacterInfo characterInfoFromPolishiDictionaryKanjidic2 = kanji2Helper.getKanjiFromPolishDictionaryKanjidic2(currentKanji);
				
				if (characterInfoFromPolishiDictionaryKanjidic2 != null) { // taki wpis juz jest w polskim slowniku					
					System.out.println("[Error] Entry already exists in kanji polish dictionary: " + currentKanji);
					
					continue;					
				}
				
				// pobieramy znaczenie ze starego slownika
				@SuppressWarnings("unused")
				KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(currentKanji);
				
				if (oldKanjiEntryForDictionary != null) {
					entryAdditionalData.setOldKanjiEntryForDictionary(characterInfo.getKanji(), oldKanjiEntryForDictionary);
				}
								
				result.getCharacterList().add(characterInfo);				
			}
			
			int fixme3 = 1;
			/*
			if (useJMdictPolishDictionary == true) { // szukamy w polskim slowniku lub jmdict
				Entry entry = dictionaryHelper.getEntryFromPolishDictionary(currentEntryId); // szukamy w polskim slowniku

				if (entry != null) {
					result.add(entry);
					
					// zaznaczamy, ze ten wpis to polskie slowo, aby romaji nie zaznaczalo sie 
					saveEntryListAsHumanCsvConfig.markAsPolishEntry(entry);
					
				} else { // szukamy w jmdict
					entry = dictionaryHelper.getJMdictEntry(currentEntryId);
					
					if (entry == null) { // nie znaleziono
						System.out.println("[Errpr] Can't find entry id in polish or jmdict dictionary: " + currentEntryId);
						
						continue;										
					}
														
					// uzupelnienie o puste polskie tlumaczenie
					dictionaryHelper.createEmptyPolishSense(entry);
					
					// pobranie ze starego slownika interesujacych danych (np. romaji)
					dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(entry, entryAdditionalData);
					
					result.add(entry);				
				}				
			}
			*/
		}
		
		// zapisanie wyniku pod postacia csv		
		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig, "input/kanji2-new.csv", result, entryAdditionalData);
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GetKanjiFromDictionary.class.getSimpleName(), options);
	}
	
	private static List<String> readFile(String fileName) {

		List<String> result = new ArrayList<String>();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

			while (true) {

				String line = br.readLine();

				if (line == null) {
					break;
				}
				
				line = line.trim();

				result.add(line);
			}

			br.close();

			return result;

		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
}
