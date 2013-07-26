package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.TreeMap;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class TransitiveIntransitivePairGenerator {
	
	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedict = JMEDictReader.readJMEdict("../JaponskiSlownik_dodatki/JMdict_e", true);
				
		CsvReader csvReader = new CsvReader(new FileReader("../JaponskiSlownik_dodatki/transitive_intransitive_pairs_raw.csv"), ',');
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter("input/transitive_intransitive_pairs.csv"), ',');
		
		while(csvReader.readRecord()) {
			
			// transitive
			String transitiveRomaji = csvReader.get(0);
			String transitiveKana = csvReader.get(1);
			String transitiveEnglishTranslate = csvReader.get(2);
			
			// intrasitive
			String intransitiveRomaji = csvReader.get(4);
			String intransitiveKana = csvReader.get(5);
			String intransitiveEnglishTranslate = csvReader.get(6);
			
			// kanji
			String kanjiStringList = csvReader.get(7);
			
			String[] kanjiStringListSplited = kanjiStringList.split(",");
			
			for (int idx = 0; idx < kanjiStringListSplited.length; ++idx) {
				kanjiStringListSplited[idx] = kanjiStringListSplited[idx].trim();
			}
			
			for (int idx = 0; idx < kanjiStringListSplited.length; ++idx) {
				
				String transitiveKanji = findKanjiFullWord(jmedict, kanjiStringListSplited[idx], transitiveKana);
				
				if (transitiveKanji == null) {
					transitiveKanji = "$$$NULL$$$";
				}
				
				String intransitiveKanji = findKanjiFullWord(jmedict, kanjiStringListSplited[idx], intransitiveKana);
				
				if (intransitiveKanji == null) {
					intransitiveKanji = "$$$NULL$$$";
				}
				
				// transitive				
				csvWriter.write(transitiveKanji);
				csvWriter.write(transitiveKana);
				csvWriter.write(transitiveRomaji);
				csvWriter.write(transitiveEnglishTranslate);
				
				csvWriter.write("");
				
				// intransitve
				csvWriter.write(intransitiveKanji);
				csvWriter.write(intransitiveKana);
				csvWriter.write(intransitiveRomaji);
				csvWriter.write(intransitiveEnglishTranslate);
				
				csvWriter.endRecord();
			}
		}
		
		csvReader.close();
		csvWriter.close();
	}
	
	private static String findKanjiFullWord(TreeMap<String, List<JMEDictEntry>> jmedict, String kanjiStart, String kana) {
		
		List<JMEDictEntry> jmeDictEntryList = jmedict.get(JMEDictReader.getMapKey(null, kana));
		
		if (jmeDictEntryList == null) {
			return null;
		}
		
		for (JMEDictEntry jmeDictEntry : jmeDictEntryList) {
			
			List<String> jmeDictEntryKanji = jmeDictEntry.getKanji();
			
			for (String currentJmeDictEntryKanji : jmeDictEntryKanji) {
				
				if (currentJmeDictEntryKanji.startsWith(kanjiStart) == true) {
					return currentJmeDictEntryKanji;
				}
			}
		}
		
		return null;
	}
}
