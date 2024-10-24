package pl.idedyk.japanese.dictionary2.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class GetEntryFromDictionary {

	public static void main(String[] args) throws Exception {
		
		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();
		
		// opcje
		Options options = new Options();
		
		options.addOption("c", "check-only-max-ids", true, "Check only max ids");
		options.addOption("e", "entry-ids", true, "Entry ids");
		options.addOption("f", "file-entry-ids", true, "File entry ids");
		options.addOption("p", "polish-dictionary", false, "Use polish dictionary");
		options.addOption("j", "jmdict-dictionary", false, "Use JMdict dictionary");
		options.addOption("jp", "jmdict-polish-dictionary", false, "Use JMdict or polish dictionary");
		//options.addOption("awwadeiod", "add-words-which-also-doesnt-exist-in-old-dictionary", false, "Add words which also doesn't exist in old dictionary");
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
		
		List<Integer> entryIds = new ArrayList<>();
				
		boolean usePolishDictionary = false;
		boolean useJMdictDictionary = false;
		boolean useJMdictPolishDictionary = false;
		
		Integer checkOnlyMaxIds = null;
		
		//boolean addWordsWithAlsoDoesntExistInOldDictionary = false;
		
		//
		
		if (commandLine.hasOption("entry-ids") == true && commandLine.hasOption("file-entry-ids") == true) {
			
			printHelp(options);
			
			System.exit(1);			
			
		} else if (commandLine.hasOption("entry-ids") == true) {
			
			String entryIdsValue = commandLine.getOptionValue("entry-ids");
			
			String[] entryIdsValueSplited = entryIdsValue.split(",");
			
			for (String currentEntryId : entryIdsValueSplited) {
				entryIds.add(new Integer(currentEntryId));
			}
			
		} else if (commandLine.hasOption("file-entry-ids") == true) { 
			
			String fileNameEntryIds = commandLine.getOptionValue("file-entry-ids");
			
			List<String> fileEntryIds = readFile(fileNameEntryIds);

			for (String currentEntryId : fileEntryIds) {
				entryIds.add(new Integer(currentEntryId));
			}
		
		} else {
			
			printHelp(options);
			
			System.exit(1);			
		}
		
		int checkCounter = 0;
		
		if (commandLine.hasOption("polish-dictionary") == true) {
			usePolishDictionary = true;	
			checkCounter++;
		}
		
		if (commandLine.hasOption("jmdict-dictionary") == true) {
			useJMdictDictionary = true;
			checkCounter++;
		}

		if (commandLine.hasOption("jmdict-polish-dictionary") == true) {
			useJMdictPolishDictionary = true;
			checkCounter++;
		}

		if (commandLine.hasOption("check-only-max-ids") == true) {			
			checkOnlyMaxIds = new Integer(commandLine.getOptionValue("check-only-max-ids"));			
		}
		
		if (checkCounter != 1) {			
			printHelp(options);
			
			System.exit(1);	
		}
		
		/*
		if (commandLine.hasOption("add-words-which-also-doesnt-exist-in-old-dictionary") == true) {
			addWordsWithAlsoDoesntExistInOldDictionary = true;
		}
		*/
		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		if (useJMdictDictionary == true || useJMdictPolishDictionary == true) {			
			saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
			saveEntryListAsHumanCsvConfig.markRomaji = true;
			saveEntryListAsHumanCsvConfig.shiftCells = true;
			saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
		}		
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		List<Entry> result = new ArrayList<>();
		
		Set<Integer> alreadyMetEntrySet = new TreeSet<Integer>();
		
		// dodatkowe informacje
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		int entryIdsCounter = 0;
		
		for (Integer currentEntryId : entryIds) {
			
			entryIdsCounter++;
						
			if (alreadyMetEntrySet.contains(currentEntryId) == true) { // to slowo juz odwiedzalismy
				continue;
				
			} else {
				alreadyMetEntrySet.add(currentEntryId);
			}
			
			if (usePolishDictionary == true) { // szukamy w polskim slowniku
				
				Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(currentEntryId);
				
				if (entryFromPolishDictionary == null) { // nie znaleziono
					
					System.out.println("[Error] Can't find entry id in polish dictionary: " + currentEntryId);
					
					continue;					
				}
				
				result.add(entryFromPolishDictionary);				
			}
			
			if (useJMdictDictionary == true) { // szukamy w jmdict
				
				Entry jmdictEntry = dictionaryHelper.getJMdictEntry(currentEntryId);
				
				if (jmdictEntry == null) { // nie znaleziono
					System.out.println("[Errpr] Can't find entry id in jmdict dictionary: " + currentEntryId);
					
					continue;										
				}
				
				Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(jmdictEntry.getEntryId());
				
				if (entryFromPolishDictionary != null) { // taki wpis juz jest w polskim slowniku					
					System.out.println("[Error] Entry already exists in polish dictionary: " + currentEntryId);
					
					continue;					
				}
				
				/*
				boolean existsInOldPolishJapaneseDictionary = dictionaryHelper.isExistsInOldPolishJapaneseDictionary(jmdictEntry);

				if (existsInOldPolishJapaneseDictionary == false && addWordsWithAlsoDoesntExistInOldDictionary == false) { // kiedys to zmieni sie, ale obecnie kazde slowo juz byc rowniez w starym slowniku (no chyba ze zdecydowano inaczej)
					System.out.println("[Warning] Entry id " + jmdictEntry.getEntryId() + " doesn't exist in old polish dictionary!");
					
					continue;
				}
				*/
								
				// uzupelnienie o puste polskie tlumaczenie
				dictionaryHelper.createEmptyPolishSense(jmdictEntry);
				
				// pobranie ze starego slownika interesujacych danych (np. romaji)
				dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(jmdictEntry, entryAdditionalData);
				
				result.add(jmdictEntry);				
			}
			
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
			
			if (checkOnlyMaxIds != null && result.size() >= checkOnlyMaxIds) {
				break;
			}
		}
		
		if (checkOnlyMaxIds == null) {
			
			// zapisanie wyniku pod postacia csv		
			dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", result, entryAdditionalData);
			
		} else {
			
			// liczba pozycji do pobrania
			System.out.println("Ids number: " + entryIdsCounter + " (new ids: " + result.size() + ", missing: " + ( checkOnlyMaxIds - result.size()) + ")");
						
		}		
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GetEntryFromDictionary.class.getSimpleName(), options);
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
