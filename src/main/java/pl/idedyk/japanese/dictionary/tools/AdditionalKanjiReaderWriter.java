package pl.idedyk.japanese.dictionary.tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.AdditionalKanjiEntry;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class AdditionalKanjiReaderWriter {

	public static List<AdditionalKanjiEntry> readAdditionalKanjiEntry(String additionalKanjiFile) throws Exception {
		
		List<AdditionalKanjiEntry> result = new ArrayList<AdditionalKanjiEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(additionalKanjiFile), ',');

		boolean useKanji = true;
		
		while (csvReader.readRecord()) {
			
			AdditionalKanjiEntry additionalKanjiEntry = new AdditionalKanjiEntry();
			
			String id = csvReader.get(0);
			
			additionalKanjiEntry.setId(id);
			additionalKanjiEntry.setDone(csvReader.get(1));
			additionalKanjiEntry.setKanji(csvReader.get(2));
			additionalKanjiEntry.setStrokeCount(csvReader.get(3));
			additionalKanjiEntry.setTranslate(csvReader.get(4));
			additionalKanjiEntry.setInfo(csvReader.get(5));
			
			if (useKanji == true && id.equals("X") == true) {
				useKanji = false;
			}
			
			additionalKanjiEntry.setUseKanji(useKanji);
			
			result.add(additionalKanjiEntry);			
		}
		
		csvReader.close();
		
		return result;		
	}
	
	public static AdditionalKanjiEntry findAdditionalKanjiEntry(List<AdditionalKanjiEntry> additionalKanjiEntryList, String kanji) {
		
		for (AdditionalKanjiEntry currentAdditionalKanjiEntry : additionalKanjiEntryList) {
			
			if (currentAdditionalKanjiEntry.getKanji().equals(kanji) == true) {
				return currentAdditionalKanjiEntry;
			}
		}
		
		return null;		
	}
	
	public static void writeAdditionalKanjiList(List<AdditionalKanjiEntry> additionalKanjiEntryList, String additionalKanjiOutputFile) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(additionalKanjiOutputFile), ',');
		
		for (AdditionalKanjiEntry additionalKanjiEntry : additionalKanjiEntryList) {
			
			csvWriter.write(additionalKanjiEntry.getId());
			csvWriter.write(additionalKanjiEntry.getDone());
			csvWriter.write(additionalKanjiEntry.getKanji());
			csvWriter.write(additionalKanjiEntry.getStrokeCount());
			csvWriter.write(additionalKanjiEntry.getTranslate());
			csvWriter.write(additionalKanjiEntry.getInfo());
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}
}
