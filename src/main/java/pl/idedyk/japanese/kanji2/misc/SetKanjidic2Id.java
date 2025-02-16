package pl.idedyk.japanese.kanji2.misc;

import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class SetKanjidic2Id {

	public static void main(String[] args) throws Exception {
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		// wczytanie polskiego slownika kanji
		Kanjidic2 polishDictionaryKanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();

		// chodzimy po wszystkich znakach i usuwamy niepotrzebne grupy
		for (CharacterInfo characterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			// szukanie kanji w starym slowniku
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(characterInfo.getKanji());
			
			// zabieramy id
			characterInfo.setId(oldKanjiEntryForDictionary.getId());			
		}

		// zapisanie docelowej postaci
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();

		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig,  "input/kanji2-update.csv", polishDictionaryKanjidic2, entryAdditionalData);
	}
}
