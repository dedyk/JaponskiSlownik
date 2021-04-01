package pl.idedyk.japanese.dictionary2.app;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class SetEntryToPolishDictionary {

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
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();
		
		// wczytywanie listy zmienionych elementow
		List<Entry> entryListFromFileName = dictionaryHelper.readEntryListFromHumanCsv(fileName.getAbsolutePath());

		for (Entry currentNewEntry : entryListFromFileName) {
			
			// pobieramy slowo w starszej wersji
			Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(currentNewEntry.getEntryId());
			
			if (entryFromPolishDictionary == null) { // nowe slowko
				
				System.out.println("Add new entry: " + currentNewEntry.getEntryId());
				
				dictionaryHelper.addEntryToPolishDictionary(currentNewEntry);
				
			} else { // aktualizacja
				
				System.out.println("Update entry: " + currentNewEntry.getEntryId());
				
				dictionaryHelper.updateEntryInPolishDictionary(currentNewEntry);
			}
		}
		
		// zapisanie docelowej postaci
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
				
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig,  "input/word2-new.csv", dictionaryHelper.getAllPolishDictionaryEntryList(), new EntryAdditionalData());
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(SetEntryToPolishDictionary.class.getSimpleName(), options);
	}
}
