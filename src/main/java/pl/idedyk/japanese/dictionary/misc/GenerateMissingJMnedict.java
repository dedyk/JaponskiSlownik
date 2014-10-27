package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class GenerateMissingJMnedict {

	public static void main(String[] args) throws Exception {
				
		File previousDirFile = new File("input_names2");
		File newDirFile = new File("input_names3");
		
		File differentDirFile = new File("input_names4");
		
		File[] previousDirFileListFiles = previousDirFile.listFiles();

		for (File currentPreviousFile : previousDirFileListFiles) {
			
			File currentNewFile = new File(newDirFile, currentPreviousFile.getName());
			
			// poprzednia lista
			List<PolishJapaneseEntry> previousPolishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentPreviousFile.getAbsolutePath());			
			
			Map<String, PolishJapaneseEntry> previousPolishJapaneseEntriesMap = new TreeMap<String, PolishJapaneseEntry>();
			
			for (PolishJapaneseEntry currentPreviousPolishJapaneseEntry : previousPolishJapaneseEntries) {
				previousPolishJapaneseEntriesMap.put(getKey(currentPreviousPolishJapaneseEntry), currentPreviousPolishJapaneseEntry);
			}
			
			// nowa lista
			List<PolishJapaneseEntry> newPolishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentNewFile.getAbsolutePath());
			
			// porownanie
			List<PolishJapaneseEntry> missingPolishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();
			
			for (PolishJapaneseEntry currentNewPolishJapaneseEntry : newPolishJapaneseEntries) {
				
				if (previousPolishJapaneseEntriesMap.containsKey(getKey(currentNewPolishJapaneseEntry)) == false) {
					missingPolishJapaneseEntries.add(currentNewPolishJapaneseEntry);
				}				
			}
			
			// zapis
			File currentMissingFile = new File(differentDirFile, currentPreviousFile.getName());
			
			CsvReaderWriter.generateCsv(currentMissingFile.getAbsolutePath(), missingPolishJapaneseEntries, false);
		}
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKanaList().get(0) + "." + polishJapaneseEntry.getRomajiList().get(0);
	}
}
