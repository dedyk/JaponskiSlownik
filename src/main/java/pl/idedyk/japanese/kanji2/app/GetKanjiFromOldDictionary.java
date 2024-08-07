package pl.idedyk.japanese.kanji2.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class GetKanjiFromOldDictionary {
	
	public static void main(String[] args) throws Exception {
		
		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();

		// opcje
		Options options = new Options();

		options.addOption("s", "size", true, "Kanji size");
		options.addOption("r", "random", false, "Randomize kanji to get");
		options.addOption("aoem", "add-only-empty-meanings", false, "Add only empty meanings");
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
		
		boolean random = false;
		Integer kanjiSize = null;
		boolean addOnlyEmptyMeanings = false;
		
		if (commandLine.hasOption("random") == true) {
			random = true;
		}
		
		if (commandLine.hasOption("size") == true) {
			kanjiSize = Integer.parseInt(commandLine.getOptionValue("size"));
		}
		
		if (commandLine.hasOption("add-only-empty-meanings") == true) {
			addOnlyEmptyMeanings = true;
		}
		
		if (kanjiSize == null) {
			printHelp(options);
			
			System.exit(1);
		}
		
		//
		
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();

		// pobranie listy kanji, ktorych jeszcze nie ma
		List<String> missingKanjiList = new ArrayList<>();
		
		Kanjidic2 kanjidic2 = kanji2Helper.getKanjidic2();
		
		for (CharacterInfo characterInfo : kanjidic2.getCharacterList()) {			
			// sprawdzenie, czy ten znak jest juz w slowniku w nowym formacie
			CharacterInfo kanjiFromPolishDictionaryKanjidic2 = kanji2Helper.getKanjiFromPolishDictionaryKanjidic2(characterInfo.getKanji());
			
			if (kanjiFromPolishDictionaryKanjidic2 == null) { // nie ma, wiec dodajemy go do listy oczekujacych znakow
				
				if (addOnlyEmptyMeanings == true) { // dodanie tylko tych z pustym znaczeniem					
					if (	characterInfo.getReadingMeaning() == null || 
							characterInfo.getReadingMeaning().getReadingMeaningGroup() == null ||
							characterInfo.getReadingMeaning().getReadingMeaningGroup().getMeaningList().size() != 0) {
						continue;
					}					
				}
				
				missingKanjiList.add(characterInfo.getKanji());				
			}
		}
		
		// czy losowac kolejnosc
		if (random == true) {
			Collections.shuffle(missingKanjiList);
		}
		
		// wygenerowanie listy wynikowej
		Kanjidic2 result = kanji2Helper.createEmptyKanjidic2();
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();
		
		saveKanjiDic2AsHumanCsvConfig.addOldPolishTranslates = true;
		saveKanjiDic2AsHumanCsvConfig.shiftCells = true;
		saveKanjiDic2AsHumanCsvConfig.shiftCellsGenerateIds = true;
		
		for (String currentKanji : missingKanjiList) {
			// pobieramy znak z angielskiego slownika
			CharacterInfo characterInfo = kanji2Helper.getKanjiFromKanjidic2(currentKanji);
			
			// pobieramy znaczenie ze starego slownika
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(currentKanji);
			
			if (oldKanjiEntryForDictionary != null) {
				entryAdditionalData.setOldKanjiEntryForDictionary(characterInfo.getKanji(), oldKanjiEntryForDictionary);
				
				// dodanie dodatkowych informacji ze starego slownika
				characterInfo = kanji2Helper.addDatasFromOldKanjiEntryForDictionary(characterInfo, oldKanjiEntryForDictionary);
			}
							
			result.getCharacterList().add(characterInfo);
			
			if (result.getCharacterList().size() >= kanjiSize) {
				break;
			}
		}

		// zapisanie wyniku pod postacia csv		
		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig, "input/kanji2-new.csv", result, entryAdditionalData);

	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GetKanjiFromOldDictionary.class.getSimpleName(), options);
	}
}
