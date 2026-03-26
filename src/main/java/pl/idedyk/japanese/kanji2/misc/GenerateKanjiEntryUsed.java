package pl.idedyk.japanese.kanji2.misc;

import java.io.File;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2NameHelper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.JMnedict;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;

public class GenerateKanjiEntryUsed {

	public static void main(String[] args) throws Exception {
		
		// pomocnicy
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		// wczytanie polskiego slownika kanjiDic2
		Kanjidic2 polishDictionaryKanjidic2 = kanji2Helper.getPolishDictionaryKanjidic2();
		
		// wczytanie slownikow ze wszystkimi slowami
		JMdict jmdict = dictionary2Helper.getJMdict();
		JMnedict jmnedict = dictionary2NameHelper.getJMnedict();

		// wczytanie rysowan
		File kanjivgSingleXmlFile = new File("../JapaneseDictionary_additional/kanjivg/kanjivg.xml");
		File kanjivgPatchDirFile = new File("../JapaneseDictionary_additional/kanjivg/patch");
		
		Map<String, KanjivgEntry> kanjivgEntryMap = KanjivgReader.readKanjivgSingleXmlFile(kanjivgSingleXmlFile, kanjivgPatchDirFile);
		
		// resetowanie uzycia
		for (KanjiCharacterInfo currentPolishKanjiCharaterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			currentPolishKanjiCharaterInfo.getMisc2().setUsed(false);
		}
				
		// chodzenie po slowach
		for (JMdict.Entry jmdictentry : jmdict.getEntryList()) {			
			
			// chodzenie po kanji
			List<KanjiInfo> kanjiInfoList = jmdictentry.getKanjiInfoList();
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {
				processKanjiToFoundUsedKanji(kanji2Helper, kanjiInfo.getKanji());
			}			
		}
		
		// chodzenie po slowniku nazw
		for (JMnedict.Entry jmnedictEntry : jmnedict.getEntryList()) {			
			
			// chodzenie po kanji
			List<pl.idedyk.japanese.dictionary2.jmnedict.xsd.KanjiInfo> kanjiInfoList = jmnedictEntry.getKanjiInfoList();
			
			for (pl.idedyk.japanese.dictionary2.jmnedict.xsd.KanjiInfo kanjiInfo : kanjiInfoList) {
				processKanjiToFoundUsedKanji(kanji2Helper, kanjiInfo.getKanji());
			}			
		}
		
		// jezeli nie ma dane rysowania to zaznaczamy uzycie na false
		for (KanjiCharacterInfo currentPolishKanjiCharaterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			// pobieramy rysowania
			KanjivgEntry kanjivgEntry2 = kanjivgEntryMap.get(currentPolishKanjiCharaterInfo.getKanji());
						
			if (kanjivgEntry2 == null) {
				currentPolishKanjiCharaterInfo.getMisc2().setUsed(false);
			}			
		}
		
		// zapis zmian
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig saveKanjiDic2AsHumanCsvConfig = new Kanji2Helper.SaveKanjiDic2AsHumanCsvConfig();

		kanji2Helper.saveKanjidic2AsHumanCsv(saveKanjiDic2AsHumanCsvConfig,  "input/kanji2-update.csv", polishDictionaryKanjidic2, entryAdditionalData);		
	}
	
	private static void processKanjiToFoundUsedKanji(Kanji2Helper kanji2Helper, String kanji) throws Exception {
		
		// chodzimy po kazdy znaku i sprawdzamy, czy to kanji
		for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {

			String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

			KanjiCharacterInfo polishKanjiCharacterInfo = kanji2Helper.getKanjiFromPolishDictionaryKanjidic2(currentKanjiChar);
			
			// jezeli cos znaleziono to zaznaczamy, ze jest uzywany
			if (polishKanjiCharacterInfo != null) {
				polishKanjiCharacterInfo.getMisc2().setUsed(true);
			}						
		}
	}
}
