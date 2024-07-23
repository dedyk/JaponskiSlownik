package pl.idedyk.japanese.kanji2.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.app.GetEntryFromDictionary;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;

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
		
		List<String> kanjis = new ArrayList<>();
		
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

			for (String currentKanji : fileKanjis) {
				kanjis.add(currentKanji);
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

		fixme();

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
