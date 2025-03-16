package pl.idedyk.japanese.dictionary2.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.SaveEntryListAsHumanCsvConfig;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class UpdateOnlyPolishJapaneseTranslate {
	
	public static void main(String[] args) throws Exception {

		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();
		
		// opcje
		Options options = new Options();
		
		options.addOption("f", "file", true, "Source file name");
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
		
		File fileName = null;
		
		if (commandLine.hasOption("file") == true) {					
			fileName = new File(commandLine.getOptionValue("file"));					
		}

		if (fileName == null) {
			
			printHelp(options);
			
			System.exit(1);			
		}
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		// wczytywanie listy zmienionych elementow
		List<Entry> entryListFromFileName = dictionaryHelper.readEntryListFromHumanCsv(fileName.getAbsolutePath());
		
		List<Entry> resultList = new ArrayList<>();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (Entry entryToCompare : entryListFromFileName) {
			
			// pobieramy slowo z calego slownika
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entryToCompare.getEntryId());

			if (entryFromPolishDictionary == null) {
				System.out.println("WARNING. Can't find entry: " + entryToCompare.getEntryId());
				continue;
			}
			
			// wykonanie porownania
			Entry joinedEntry = dictionaryHelper.updateOnlyPolishJapaneseTranslate(entryFromPolishDictionary, entryToCompare, entryAdditionalData);
			
			resultList.add(joinedEntry);
		}		
		
		SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new SaveEntryListAsHumanCsvConfig();
		
		// dodajemy propozycje nowych polskich znaczen
		saveEntryListAsHumanCsvConfig.shiftCells = true;
		saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
		saveEntryListAsHumanCsvConfig.addProposalPolishTranslates = true;
		
		// zapisanie elementow, ktore nalezy manualnie zmodyfikowac
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-update-manually.csv", resultList, entryAdditionalData);
	}
	
	private static void printHelp(Options options) {		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(UpdateOnlyPolishJapaneseTranslate.class.getSimpleName(), options);
	}
}
