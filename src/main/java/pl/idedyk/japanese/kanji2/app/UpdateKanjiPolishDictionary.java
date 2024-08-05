package pl.idedyk.japanese.kanji2.app;

import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class UpdateKanjiPolishDictionary {
	
	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		// wczytanie angielskiego slownika kanji
		Kanjidic2 sourceKanjidic2 = kanji2Helper.getKanjidic2();

		// wczytanie polskiego slownika kanji
		Kanjidic2 polishDictionaryKanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();

		// lista zmienionych elementow, ktore wymagaja recznej operacji
		Kanjidic2 kanjidic2ManuallyChangeList = kanji2Helper.createEmptyKanjidic2();
		
		// lista skasowanych elementow
		Kanjidic2 kanjidic2DeletedList = kanji2Helper.createEmptyKanjidic2();
		
		// aktualizacja naglowka
		kanji2Helper.updateHeaderKanjidic2(sourceKanjidic2, polishDictionaryKanjidic2);
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		// chodzimy po wszystkich elementach w polskim slowniku kanji i sprawdzamy, czy nie nastapila zmiana
		for (CharacterInfo currentPolishKanjiCharacterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			// szukamy wpisu w angielskim slowniku
			CharacterInfo englishKanjiCharacterInfo = kanji2Helper.getKanjiFromKanjidic2(currentPolishKanjiCharacterInfo.getKanji());
			
			if (englishKanjiCharacterInfo == null) { // ten element zostal skasowany
				
				System.out.println("Deleted kanji: " + currentPolishKanjiCharacterInfo.getKanji());
				
				kanji2Helper.deleteKanjiFromPolishDictionary(currentPolishKanjiCharacterInfo.getKanji());
				
				kanjidic2DeletedList.getCharacterList().add(currentPolishKanjiCharacterInfo);
				
				continue;
			}
			
			// wykonanie aktualizacji wpisu
			boolean needManuallyChange = kanji2Helper.updatePolishKanjiCharacterInfo(englishKanjiCharacterInfo, currentPolishKanjiCharacterInfo, entryAdditionalData);
			
			if (needManuallyChange == true) {
				kanjidic2ManuallyChangeList.getCharacterList().add(currentPolishKanjiCharacterInfo);
			}
		}

		// walidacja slow
		kanji2Helper.validateAllKanjisInPolishDictionaryList();
		
		// zapisanie docelowej postaci
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();
				
		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig,  "input/kanji2-update.csv", polishDictionaryKanjidic2, entryAdditionalData);
		
		// zapisanie elementow, ktore zostaly skasowane
		if (kanjidic2DeletedList.getCharacterList().size() > 0) {
			kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig, "input/kanji2-update-deleted.csv", kanjidic2DeletedList, entryAdditionalData);
		}
		
		// zapisanie elementow, ktore nalezy manualnie zmodyfikowac
		saveKanjiDic2AsHumanCsvConfig.shiftCells = true;
		saveKanjiDic2AsHumanCsvConfig.shiftCellsGenerateIds = true;
		
		if (kanjidic2ManuallyChangeList.getCharacterList().size() > 0) {
			kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig, "input/kanji2-update-manually.csv", kanjidic2ManuallyChangeList, entryAdditionalData);
		}
	}
}
