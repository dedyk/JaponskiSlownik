package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class FixJMnedictMultiTranslate {

	public static void main(String[] args) throws Exception {

		List<PolishJapaneseEntry> multiTranslateNameList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input_names2/multiTranslateName.csv" });
		
		Map<String, PolishJapaneseEntry> multiTranslateNameListMap = new TreeMap<String, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : multiTranslateNameList) {			
			multiTranslateNameListMap.put(getKey(polishJapaneseEntry), polishJapaneseEntry);			
		}
				
		String[] namesDir = { "input_names", "input_names/miss1", "input_names/miss2", "input_names/miss3" };
		
		for (String currentNameDir : namesDir) {	
			
			File[] currentNameDirFileList = new File(currentNameDir).listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().endsWith(".csv");
				}
			});
			
			for (File currentNameFile : currentNameDirFileList) {
				
				List<PolishJapaneseEntry> currentNameFilePolishJapaneseEntryList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { currentNameFile.getAbsolutePath() });				
				
				for (PolishJapaneseEntry polishJapaneseEntry : currentNameFilePolishJapaneseEntryList) {
					
					String key = getKey(polishJapaneseEntry);
					
					PolishJapaneseEntry polishJapaneseEntryInMap = multiTranslateNameListMap.get(key);
					
					if (polishJapaneseEntryInMap != null) {						
						polishJapaneseEntryInMap.setInfo("Wykonano");
						
						polishJapaneseEntry.getTranslates().add("");
						polishJapaneseEntry.getTranslates().addAll(polishJapaneseEntryInMap.getTranslates());
						
						polishJapaneseEntry.setInfo("Do przetworzenia");
					}					
				}
				
				CsvReaderWriter.generateCsv(new String[] { currentNameFile.getAbsolutePath() }, currentNameFilePolishJapaneseEntryList, false);
			}
		}
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/multiTranslateName-operation.csv" }, multiTranslateNameList, false);
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji();
	}
}
