package pl.idedyk.japanese.kanji2.app;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class UpdateKanjiOldPolishDictionary {
	
	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		// wczytanie polskiego slownika kanji
		Kanjidic2 polishDictionaryKanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();
		
		// lista wynikowa w starej postaci
		List<KanjiEntryForDictionary> oldKanjiEntryForDictionaryList = kanji2Helper.getOldKanjiPolishDictionaryList();
		
		// chodzimy po wszystkich elementach w polskim slowniku kanji i sprawdzamy, czy nie nastapila zmiana
		for (CharacterInfo currentPolishKanjiCharacterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			// pobieramy kanji w starym formacie slownika
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(currentPolishKanjiCharacterInfo.getKanji());

			// aktualizacja kanji w starym formacie
			kanji2Helper.updateOldKanjiEntryForDictionaryFromCharacterInfo(oldKanjiEntryForDictionary, currentPolishKanjiCharacterInfo);

		}
		
		// zapisanie slownika w starej postaci
		FileOutputStream outputStream = new FileOutputStream(new File("input/kanji-wynik.csv"));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, oldKanjiEntryForDictionaryList, false, null);
	}
}
