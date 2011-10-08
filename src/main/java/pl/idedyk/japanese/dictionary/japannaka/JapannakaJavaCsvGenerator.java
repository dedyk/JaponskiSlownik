package pl.idedyk.japanese.dictionary.japannaka;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvGenerator;

public class JapannakaJavaCsvGenerator {
	
	public static void main(String[] args) throws Exception {
		
		// test
	
		List<PolishJapaneseEntry> japanesePolishDictionary = 
			JapannakaHtmlReader.readJapannakaHtmlDir("websites/www.japannaka.republika.pl");
		
		String result = CsvGenerator.generateCsv(japanesePolishDictionary);
		
		System.out.println(result);
	}
}
