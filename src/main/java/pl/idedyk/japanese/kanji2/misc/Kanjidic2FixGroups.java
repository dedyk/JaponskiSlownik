package pl.idedyk.japanese.kanji2.misc;

import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Misc2Info;

public class Kanjidic2FixGroups {

	public static void main(String[] args) throws Exception {
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		// wczytanie polskiego slownika kanji
		Kanjidic2 polishDictionaryKanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();

		// chodzimy po wszystkich znakach i usuwamy niepotrzebne grupy
		for (KanjiCharacterInfo characterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			// pobieramy grupy
			Misc2Info misc2 = characterInfo.getMisc2();
			
			// usuwamy niepotrzebne grupy
			if (misc2 != null) {
				/*
				misc2.getGroups().removeAll(Arrays.asList(Misc2InfoGroup.JŌYŌ_1, Misc2InfoGroup.JŌYŌ_2, Misc2InfoGroup.JŌYŌ_3, Misc2InfoGroup.JŌYŌ_4,
						Misc2InfoGroup.JŌYŌ_5, Misc2InfoGroup.JŌYŌ_6, Misc2InfoGroup.JŌYŌ_7, Misc2InfoGroup.JŌYŌ_8, Misc2InfoGroup.JŌYŌ_9,
						Misc2InfoGroup.JŌYŌ_10, Misc2InfoGroup.JINMEIYOU, Misc2InfoGroup.JINMEIYOU_WARIANT_JŌYŌ));
				*/
			}			
		}

		// zapisanie docelowej postaci
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();

		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig,  "input/kanji2-update.csv", polishDictionaryKanjidic2, entryAdditionalData);
	}

}
