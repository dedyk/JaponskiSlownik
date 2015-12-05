package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class GetCommonPartList {

	public static void main(String[] args) throws Exception {
		
		// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -1
		// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l
		
		String fileName = args[0];
		
		// czytanie pliku ze slownikiem
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = Helper.cachePolishJapaneseEntryList(polishJapaneseEntries);

		// czytanie listy common'owych plikow
		Map<Integer, CommonWord> commonWordMap = CsvReaderWriter.readCommonWordFile("input/common_word.csv");
		
		// czytanie identyfikatorow common'owych slow
		List<String> commonWordIds = readCommonWordIds(fileName);
		
		// wczytywanie slownika edict
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
				
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		
		for (String currentCommonWordId : commonWordIds) {
			
			CommonWord commonWord = commonWordMap.get(Integer.parseInt(currentCommonWordId));
			
			String commonKanji = null;
			String commonKana = null;
			
			List<GroupEntry> groupEntryList = null;
			
			if (commonWord != null) {
				
				commonKanji = commonWord.getKanji();
				commonKana = commonWord.getKana();
				
				groupEntryList = jmeNewDictionary.getGroupEntryList(commonKanji, commonKana);
				
				commonWord.setDone(true);
				
			} else {
				
				System.out.println("Nie znaleziono slowo o identyfikatorze: " + currentCommonWordId);
			}
						
			if (groupEntryList != null && groupEntryList.size() > 0) {
				
				for (GroupEntry groupEntry : groupEntryList) {
					
					PolishJapaneseEntry polishJapaneseEntry = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, Integer.valueOf(currentCommonWordId), null).polishJapaneseEntry;
									
					newWordList.add(polishJapaneseEntry);
				}				
				
			} else {
				
				PolishJapaneseEntry polishJapaneseEntry = Helper.createEmptyPolishJapaneseEntry(null, Integer.valueOf(currentCommonWordId));
												
				newWordList.add(polishJapaneseEntry);				
			}			
		}
		
		// zapis porcji slow
		CsvReaderWriter.generateCsv("input/word-common-new.csv", newWordList, true, true, false);
		
		// zapis nowego pliku common
		CsvReaderWriter.writeCommonWordFile(commonWordMap, "input/common_word-nowy.csv");
	}	
	
	private static List<String> readCommonWordIds(String fileName) {

		List<String> result = new ArrayList<String>();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

			while (true) {

				String line = br.readLine();

				if (line == null) {
					break;
				}
				
				if (line.startsWith("---") == true) {
					continue;
				}
				
				int tabIndex = line.indexOf("\t");
				
				if (tabIndex != -1) {
					line = line.substring(0, tabIndex);
				}
				
				line = line.trim();

				result.add(line);
			}

			br.close();

			return result;

		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
}
