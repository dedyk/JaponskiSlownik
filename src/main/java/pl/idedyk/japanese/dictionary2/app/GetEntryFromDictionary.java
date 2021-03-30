package pl.idedyk.japanese.dictionary2.app;

import java.util.ArrayList;
import java.util.HashSet;
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
		
		options.addOption("e", "entry-ids", true, "Entry ids");
		options.addOption("p", "polish-dictionary", false, "Use polish dictionary");
		options.addOption("j", "jmdict-dictionary", false, "Use JMdict dictionary");
		options.addOption("awwadeiod", "add-words-which-also-doesnt-exist-in-old-dictionary", false, "Add words which also doesn't exist in old dictionary");
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
		
		Set<Integer> entryIds = new HashSet<>();
				
		boolean usePolishDictionary = false;
		boolean useJMdictDictionary = false;
		
		boolean addWordsWithAlsoDoesntExistInOldDictionary = false;
		
		//
		
		if (commandLine.hasOption("entry-ids") == true) {
			
			String entryIdsValue = commandLine.getOptionValue("entry-ids");
			
			String[] entryIdsValueSplited = entryIdsValue.split(",");
			
			for (String currentEntryId : entryIdsValueSplited) {
				entryIds.add(new Integer(currentEntryId));
			}
			
		} else {
			
			printHelp(options);
			
			System.exit(1);			
		}
		
		if (commandLine.hasOption("polish-dictionary") == true) {
			usePolishDictionary = true;			
		}
		
		if (commandLine.hasOption("jmdict-dictionary") == true) {
			useJMdictDictionary = true;			
		}
		
		if ((usePolishDictionary == false && useJMdictDictionary == false) || (usePolishDictionary == true && useJMdictDictionary == true)) {
			
			printHelp(options);
			
			System.exit(1);	
		}
		
		if (commandLine.hasOption("add-words-which-also-doesnt-exist-in-old-dictionary") == true) {
			addWordsWithAlsoDoesntExistInOldDictionary = true;
		}
		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		if (useJMdictDictionary == true) {			
			saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
			saveEntryListAsHumanCsvConfig.markRomaji = true;
			saveEntryListAsHumanCsvConfig.shiftCells = true;
			saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
		}		
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();
		
		List<Entry> result = new ArrayList<>();
		
		Set<Integer> alreadyMetEntrySet = new TreeSet<Integer>();
		
		// dodatkowe informacje
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (Integer currentEntryId : entryIds) {
			
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
				
				boolean existsInOldPolishJapaneseDictionary = dictionaryHelper.isExistsInOldPolishJapaneseDictionary(jmdictEntry);

				if (existsInOldPolishJapaneseDictionary == false && addWordsWithAlsoDoesntExistInOldDictionary == false) { // kiedys to zmieni sie, ale obecnie kazde slowo juz byc rowniez w starym slowniku (no chyba ze zdecydowano inaczej)
					System.out.println("{Warning} Entry id " + jmdictEntry.getEntryId() + " doesn't exist in old polish dictionary!");
					
					continue;
				}
				
				// uzupelnienie o puste polskie tlumaczenie
				dictionaryHelper.createEmptyPolishSense(jmdictEntry);
				
				// pobranie ze starego slownika interesujacych danych (np. romaji)
				dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(jmdictEntry, entryAdditionalData);
				
				result.add(jmdictEntry);
			}
		}
		
		// zapisanie wyniku pod postacia csv		
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word-new-test.csv", result, entryAdditionalData);
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GenerateMissingWordListApp.class.getSimpleName(), options);
	}
}
