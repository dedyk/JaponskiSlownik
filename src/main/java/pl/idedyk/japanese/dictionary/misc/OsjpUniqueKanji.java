package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

import com.csvreader.CsvReader;

public class OsjpUniqueKanji {

	public static void main(String[] args) throws Exception {		
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile("../JaponskiSlownik_dodatki/kradfile");
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2("../JaponskiSlownik_dodatki/kanjidic2.xml", kradFileMap);
	
		CsvReader csvReader = new CsvReader(new FileReader("input/osjp.csv"), ',');
		
		Set<String> alreadySetKanji = new HashSet<String>();
		
		while(csvReader.readRecord()) {
			
			String kanji = csvReader.get(2);
						
			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {
				
				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));

				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);
				
				if (kanjiDic2Entry != null && alreadySetKanji.contains(currentKanjiChar) == false) {
					System.out.println(currentKanjiChar);
					
					alreadySetKanji.add(currentKanjiChar);
				}
			}
		}
		
		csvReader.close();
	}
}
