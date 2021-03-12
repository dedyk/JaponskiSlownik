package pl.idedyk.japanese.dictionary2.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.common.DictionaryHelper;
import pl.idedyk.japanese.dictionary2.common.Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;

public class GenerateMissingWordListApp {

	public static void main(String[] args) throws Exception {
				
		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();

		// opcje
		Options options = new Options();
		
		options.addOption("cijo", "check-in-jisho-org", false, "Only kanji");		
		options.addOption("f", "file", true, "Word list file name");		
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
		
		String wordListFileName = null;
		
		int fixme = 1;
		boolean checkInJishoOrg = true;
		
		//
		
		if (commandLine.hasOption("file") == true) {					
			wordListFileName = commandLine.getOptionValue("file");					
		}
		
		if (commandLine.hasOption("check-in-jisho-org") == true) {
			checkInJishoOrg = true;
		}
		
		// plik nie zostal podany		
		if (wordListFileName == null) {
			
			printHelp(options);
			
			System.exit(1);
		}
		
		// wczytywanie zawartosci pliku		
		System.out.println("Reading word list file name");
		
		List<String> wordList = Helper.readFile(wordListFileName, true);
		
		// wczytywanie pomocnika slownikowego
		DictionaryHelper dictionaryHelper = DictionaryHelper.init();
		
		Set<Integer> alreadyMetEntrySet = new TreeSet<Integer>();
		
		// lista wynikowa
		List<Entry> result = new ArrayList<>();
		
		// wyszukiwanie slow
		for (String currentWord : wordList) {
			
			List<Entry> jmdictResult = dictionaryHelper.findInJMdict(currentWord);
			
			if (jmdictResult.size() > 0) { // cos zostalo odnalezione
								
				for (Entry entry : jmdictResult) {
					
					if (alreadyMetEntrySet.contains(entry.getEntryId()) == true) { // to slowo juz odwiedzalismy
						continue;
						
					} else {
						alreadyMetEntrySet.add(entry.getEntryId());
					}
					
					// sprawdzenie, czy takie slowo juz wystepuje w moim slowniku
					int fixme3 = 1;
					
					// dodanie do listy wynikowej
					result.add(entry);
				}				
				
			} else { // nic nie znaleziono
				
				int fixme2 = 1;
				
			}
		}
		
		// zapisanie wyniku pod postacia csv
		dictionaryHelper.saveEntryListAsHumanCsv("input/word-new-test.csv", result);
		
		// zakonczenie
		dictionaryHelper.close();
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GenerateMissingWordListApp.class.getSimpleName(), options);
	}
}
