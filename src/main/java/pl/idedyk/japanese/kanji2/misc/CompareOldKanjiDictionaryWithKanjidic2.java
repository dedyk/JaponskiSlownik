package pl.idedyk.japanese.kanji2.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class CompareOldKanjiDictionaryWithKanjidic2 {

	public static void main(String[] args) throws Exception {
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();

		// sprawdzenie, ktore znaki sa w starym slowniku, a nie ma ich w angielskim slowniku kanjidic2
		List<KanjiEntryForDictionary> oldKanjiPolishDictionaryList = kanji2Helper.getOldKanjiPolishDictionaryList();
		
		for (KanjiEntryForDictionary kanjiEntryForDictionary : oldKanjiPolishDictionaryList) {
			
			KanjiCharacterInfo kanjiFromKanjidic2 = kanji2Helper.getKanjiFromKanjidic2(kanjiEntryForDictionary.getKanji());
			
			if (kanjiFromKanjidic2 == null) {
				System.out.println("Istnieje w starym słowniku, a nie ma go w angielskim slowniku kanjidic2: " + kanjiEntryForDictionary.getId() + " - " + kanjiEntryForDictionary.getKanji());
			}
		}
		
		// sprawdzenie, ktore znaki sa w starym slowniku, a nie ma ich w angielskim slowniku kanjidic2		
		for (KanjiEntryForDictionary kanjiEntryForDictionary : oldKanjiPolishDictionaryList) {
			
			KanjiCharacterInfo kanjiFromKanjidic2 = kanji2Helper.getKanjiFromPolishDictionaryKanjidic2(kanjiEntryForDictionary.getKanji());
			
			if (kanjiFromKanjidic2 == null) {
				System.out.println("Istnieje w starym słowniku, a nie ma go w polskim slowniku kanjidic2: " + kanjiEntryForDictionary.getId() + " - " + kanjiEntryForDictionary.getKanji());
			}
		}
		
		
		// sprawdzenie, ktore znaki sa w angielskim slowniku kanjidic2, a nie ma ich starym slowniku
		Kanjidic2 englishKanjidic2 = kanji2Helper.getKanjidic2();
		
		for (KanjiCharacterInfo kanjiCharacterInfo : englishKanjidic2.getCharacterList()) {
			
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(kanjiCharacterInfo.getKanji());
			
			if (oldKanjiEntryForDictionary == null) {
				System.out.println("Istnieje w angielskim słowniku, a nie ma go w starym slowniku: " + kanjiCharacterInfo.getId() + " - " + kanjiCharacterInfo.getKanji());
			}
		}
		
		// sprawdzenie, ktore znaki sa w polskim slowniku kanjidic2, a nie ma ich starym slowniku
		Kanjidic2 polishJanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();
		
		for (KanjiCharacterInfo kanjiCharacterInfo : polishJanjidic2.getCharacterList()) {
			
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(kanjiCharacterInfo.getKanji());
			
			if (oldKanjiEntryForDictionary == null) {
				System.out.println("Istnieje w polskim słowniku, a nie ma go w starym slowniku: " + kanjiCharacterInfo.getId() + " - " + kanjiCharacterInfo.getKanji());
			}
		}	
	}
}
