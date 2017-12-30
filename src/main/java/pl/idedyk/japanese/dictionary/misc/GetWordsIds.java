package pl.idedyk.japanese.dictionary.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class GetWordsIds {
	
	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e");

		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
		
		TreeMap<Integer, PolishJapaneseEntry> polishJapaneseEntryIds = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			polishJapaneseEntryIds.put(polishJapaneseEntry.getId(), polishJapaneseEntry);
		}
		
		List<String> wordIdsStringList = readFile("word-ids");
		
		Set<Integer> alreadyAddIdsSet = new HashSet<>();
		
		List<PolishJapaneseEntry> result = new ArrayList<>();
		
		for (String wordIdString : wordIdsStringList) {
			
			Integer id = Integer.parseInt(wordIdString);
			
			if (alreadyAddIdsSet.contains(id) == false) {
				
				alreadyAddIdsSet.add(id);
								
				result.add(polishJapaneseEntryIds.get(id));				
			}			
		}
		
		CsvReaderWriter.generateCsv(new String[] { "input/word-ids.csv"}, result, true, true, false, true, null);
	}
	
	private static List<String> readFile(String fileName) {

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
