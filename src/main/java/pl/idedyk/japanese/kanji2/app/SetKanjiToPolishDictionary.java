package pl.idedyk.japanese.kanji2.app;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class SetKanjiToPolishDictionary {

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
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
				
		// wczytywanie listy zmienionych elementow
		Kanjidic2 kanjidic2FromFileName = kanji2Helper.readKanjidic2FromHumanCsv(fileName.getAbsoluteFile());

		for (CharacterInfo currentNewCharacterInfo : kanjidic2FromFileName.getCharacterList()) {
			
			// pobieramy kanji w starszej wersji
			CharacterInfo kanjiFromPolishDictionary = kanji2Helper.getKanjiFromPolishDictionaryKanjidic2(currentNewCharacterInfo.getKanji());
			
			if (kanjiFromPolishDictionary == null) { // nowe kanji
				
				System.out.println("Add new kanji: " + currentNewCharacterInfo.getKanji());
				
				kanji2Helper.addKanjiToPolishDictionary(currentNewCharacterInfo);
				
			} else { // aktualizacja
				
				System.out.println("Update kanji: " + currentNewCharacterInfo.getKanji());
				
				kanji2Helper.updateKanjiInPolishDictionary(currentNewCharacterInfo);
			}
		}
		
		// walidacja slow
		kanji2Helper.validateAllKanjisInPolishDictionaryList();
		
		// zapisanie docelowej postaci
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();
				
		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig,  "input/kanji2-new-set.csv", kanji2Helper.getPolishDictionaryKanjidic2(), new EntryAdditionalData());
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(SetKanjiToPolishDictionary.class.getSimpleName(), options);
	}
}
