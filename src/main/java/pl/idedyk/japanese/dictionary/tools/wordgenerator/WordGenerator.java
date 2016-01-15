package pl.idedyk.japanese.dictionary.tools.wordgenerator;

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
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class WordGenerator {
	
	public static void main(String[] args) throws Exception {
		
		int fixme = 1;
		
		//args = new String[] { "get-common-part-list", "lista" };
		args = new String[] { "help" };
		
		///
		
		String operationString = null;
		Operation operation = null;
		
		// wstepne sprawdzenie argumentow		
		if (args.length == 0) {
			
			System.err.println("Brak operacji");
			
			return;
		}
		
		operationString = args[0];
		
		// wykrycie operacji
		operation = Operation.findOperation(operationString);
		
		if (operation == null) {
			
			System.err.println("Nieznana operacja: " + operationString);
			
			return;
		}
		
		// utworzenie helper'a
		WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper("input/word.csv", "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e");

		// przetwarzanie operacji
		switch (operation) {
			
			case GET_COMMON_PART_LIST: {
				
				// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -1
				// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l
				
				if (args.length != 2) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// nazwa pliku z numerkami
				String fileName = args[1];
				
				// czytanie identyfikatorow common'owych slow
				List<String> commonWordIds = readFile(fileName);
				
				// pobranie cache ze slowami
				Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();

				// czytanie listy common'owych plikow
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// przygotowywane slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
				
				// przegladanie identyfikatorow
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
						
						System.out.println("Nie znaleziono slowa o identyfikatorze: " + currentCommonWordId);
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
				
				break;
			}
			
			case HELP: {
				
				// pobranie listy mozliwych operacji
				Operation[] operationList = Operation.values();
				
				System.out.println("Lista dostępnych operacji:xj\n");
				
				for (Operation currentOperation : operationList) {
					System.out.println(currentOperation.getOperation() + " - " + currentOperation.getDescription());
				}
				
				break;
			}
			
			default:
				
				throw new Exception("Brak implementacji dla operacji: " + operation);				
		}		
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
