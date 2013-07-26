package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.util.List;

import com.csvreader.CsvReader;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class TransitiveIntransitivePairNotExistsInDictionary {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
				
		CsvReader csvReader = new CsvReader(new FileReader("input/transitive_intransitive_pairs.csv"), ',');
		
		while(csvReader.readRecord()) {
			
			// transitive
			String transitiveKanji = csvReader.get(0);
			String transitiveKana = csvReader.get(1);
			String transitiveRomaji = csvReader.get(2);
			String transitiveEnglishTranslate = csvReader.get(3);

			PolishJapaneseEntry transitivePolishJapaneseEntry = 
					findPolishJapaneseEntry(polishJapaneseEntries, transitiveKanji, transitiveKana);
			
			if (transitivePolishJapaneseEntry == null) {
				System.out.format("%s\t%s\t%s\t%s\n", transitiveKanji, transitiveKana, transitiveRomaji, transitiveEnglishTranslate);
			}
			
			// intransitive
			String intransitiveKanji = csvReader.get(5);
			String intransitiveKana = csvReader.get(6);
			String intransitiveRomaji = csvReader.get(7);
			String intransitiveEnglishTranslate = csvReader.get(8);
			
			PolishJapaneseEntry intransitivePolishJapaneseEntry = 
					findPolishJapaneseEntry(polishJapaneseEntries, intransitiveKanji, intransitiveKana);
			
			if (intransitivePolishJapaneseEntry == null) {
				System.out.format("%s\t%s\t%s\t%s\n", intransitiveKanji, intransitiveKana, intransitiveRomaji, intransitiveEnglishTranslate);
			}
		}
		
		csvReader.close();
	}
	
	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			String kanji, String kana) {
		
		if (kanji.equals("") == true) {
			kanji = "-";
		}
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
			
			if (polishJapaneseEntryKanji.equals("") == true || polishJapaneseEntryKanji.equals("-") == true) {
				polishJapaneseEntryKanji = "-";
			}
			
			if (kanji.equals(polishJapaneseEntryKanji) == true) {
				
				List<String> polishJapaneseEntryKanaList = polishJapaneseEntry.getKanaList();
				
				for (String currentPolishJapaneseEntryKana : polishJapaneseEntryKanaList) {
					
					if (kana.equals(currentPolishJapaneseEntryKana) == true) {
						return polishJapaneseEntry;
					}
				}
				
			}
		}
		
		return null;
	}
}
