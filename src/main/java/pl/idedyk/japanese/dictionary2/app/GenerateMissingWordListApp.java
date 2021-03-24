package pl.idedyk.japanese.dictionary2.app;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector.JapaneseWord;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

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
		
		boolean checkInJishoOrg = false;
		
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
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.init();
		
		Set<Integer> alreadyMetEntrySet = new TreeSet<Integer>();
		
		// konektor do jisho.org
		JishoOrgConnector jishoOrgConnector = new JishoOrgConnector();
		
		// rozne listy
		List<String> foundWordSearchList = new ArrayList<>();
		List<String> notFoundJishoFoundWordSearchList = new ArrayList<>();
		List<String> notFoundWordSearchList = new ArrayList<>();
		
		// lista wynikowa
		List<Entry> result = new ArrayList<>();
		
		// wyszukiwanie slow
		for (int currentWordIdx = 0; currentWordIdx < wordList.size(); ++currentWordIdx) {
			
			float percent = 100.0f * ((currentWordIdx + 1) / (float)wordList.size());
			
			System.out.println("Progress: " + (currentWordIdx + 1) + " / " + wordList.size() + " (" + percent + "%)");

			//
			
			String currentWord = wordList.get(currentWordIdx);
			
			List<Entry> jmdictResult = dictionaryHelper.findInJMdict(currentWord);
			
			if (jmdictResult.size() > 0) { // cos zostalo odnalezione
				
				foundWordSearchList.add(currentWord);
								
				for (Entry entry : jmdictResult) {
					
					if (alreadyMetEntrySet.contains(entry.getEntryId()) == true) { // to slowo juz odwiedzalismy
						continue;
						
					} else {
						alreadyMetEntrySet.add(entry.getEntryId());
					}
					
					// sprawdzenie, czy takie slowo juz wystepuje w moim slowniku
					Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entry);
					
					if (entryFromPolishDictionary != null) { // taki wpis juz jest
						continue;
					}
										
					// uzupelnienie o puste polskie tlumaczenie
					dictionaryHelper.createEmptyPolishSense(entry);
					
					// pobranie ze starego slownika interesujacych danych (np. romaji)
					dictionaryHelper.fillDataFromOldPolishJapaneseDictionary(entry);
					
					// dodanie do listy wynikowej
					result.add(entry);
				}				
				
			} else { // nic nie znaleziono
				
				// ewentualnie sprawdzamy w jisho
				if (checkInJishoOrg == true) {
					
					System.out.println("Checking in jisho.org: " + currentWord);
					List<JapaneseWord> jishoJapaneseWordList = jishoOrgConnector.getJapaneseWords(currentWord);
					
					if (jishoJapaneseWordList.size() > 0) {
						notFoundJishoFoundWordSearchList.add(currentWord);
						
					} else {
						notFoundWordSearchList.add(currentWord);	
					}
					
				} else {
					notFoundWordSearchList.add(currentWord);
				}
			}
		}
				
		// zapisanie wyniku pod postacia csv
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		
		saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
		
		dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word-new-test.csv", result);
		
		// zapisywanie list
		FileWriter searchResultFileWriter = new FileWriter(wordListFileName + "-new");
		
		searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));
		searchResultFileWriter.write("\n---------\n");
		searchResultFileWriter.write(Utils.convertListToString(notFoundJishoFoundWordSearchList));
		searchResultFileWriter.write("\n---------\n");
		searchResultFileWriter.write(Utils.convertListToString(notFoundWordSearchList));
		
		searchResultFileWriter.close();
		
		// zakonczenie
		dictionaryHelper.close();
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GenerateMissingWordListApp.class.getSimpleName(), options);
	}
}
