package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ShowAlreadyAddCommonWords {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = 
				pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		CsvReader csvReader = new CsvReader(new FileReader(new File("input/common_word.csv")));
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(new File("input/already-added-common_word.csv")), ',');
		
		while (csvReader.readRecord()) {
			
			String id = csvReader.get(0);
			
			boolean done = csvReader.get(1).equals("1");
			
			String kanji = csvReader.get(2);
			String kana = csvReader.get(3);
			
			String commonTranslate = csvReader.get(5);
			
			if (done == false) {
				
				List<PolishJapaneseEntry> findPolishJapaneseEntry = 
						pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanji, kana);
				
				if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
					
					csvWriter.write(id);
					
					csvWriter.write(kanji);
					csvWriter.write(kana);
										
					csvWriter.write(Utils.convertListToString(findPolishJapaneseEntry.get(0).getTranslates()));
					
					csvWriter.write(commonTranslate);
					
					csvWriter.write(findPolishJapaneseEntry.get(0).getInfo());
					
					csvWriter.endRecord();
				}
			}			
		}		
		
		csvReader.close();
		csvWriter.close();
		
	}
}
