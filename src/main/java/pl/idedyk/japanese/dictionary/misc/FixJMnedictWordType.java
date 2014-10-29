package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class FixJMnedictWordType {

	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml", true);
		
		List<PolishJapaneseEntry> allPolishJapaneseEntryList = Helper.generateNames(jmedictName);

		Map<String, PolishJapaneseEntry> allPolishJapaneseEntryListMap = new TreeMap<String, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntryList : allPolishJapaneseEntryList) {
			allPolishJapaneseEntryListMap.put(getKey(currentPolishJapaneseEntryList), currentPolishJapaneseEntryList);
		}
		
		String[] namesDir = { "input_names", "input_names/miss1", "input_names/miss2", "input_names/miss3" };
		
		for (String currentNameDir : namesDir) {	
			
			File[] currentNameDirFileList = new File(currentNameDir).listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});
			
			for (File currentNameFile : currentNameDirFileList) {
				
				List<PolishJapaneseEntry> currentNameFilePolishJapaneseEntryList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(currentNameFile.getAbsolutePath());
				
				/*
				for (PolishJapaneseEntry currentPolishJapaneseEntry : currentNameFilePolishJapaneseEntryList) {
					
					String key = getKey(currentPolishJapaneseEntry);
					
					currentPolishJapaneseEntry.setDictionaryEntryTypeList(allPolishJapaneseEntryListMap.get(key).getDictionaryEntryTypeList());					
				} */
				
				CsvReaderWriter.generateCsv(currentNameFile.getAbsolutePath(), currentNameFilePolishJapaneseEntryList, false);
			}
		}		
	}
	
	private static String getKey(PolishJapaneseEntry polishJapaneseEntry) {
		return polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKanaList().get(0) + "." + polishJapaneseEntry.getRomajiList().get(0);
	}
}
