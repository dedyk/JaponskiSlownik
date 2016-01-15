package pl.idedyk.japanese.dictionary.tools.wordgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
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
		//args = new String[] { "help" };
		args = new String[] { "generate-missing-word-list-in-common-words", "lista" };
		
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

				// czytanie common'owego pliku
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
			
			case GENERATE_MISSING_WORD_LIST_IN_COMMON_WORDS: {
				
				if (args.length != 2) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				String fileName = args[1];
								
				// wczytywanie pliku z lista slow
				System.out.println("Wczytywanie brakujących słów...");
				
				List<String> missingWords = readFile(fileName);
				
				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);
				
				// tworzenie listy wynikowej				
				List<String> foundWordSearchList = new ArrayList<String>();
				
				System.out.println("Szukanie...");
				
				for (String currentMissingWord : missingWords) {
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					Query query = Helper.createLuceneDictionaryIndexQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;
					
					if (scoreDocs.length > 0) {
						
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							GroupEntry groupEntry = Helper.createGroupEntry(foundDocument);
							
							String groupEntryKanji = groupEntry.getKanji();
							String groupEntryKana = groupEntry.getKana();
							
							boolean existsInCommonWords = existsInCommonWords(commonWordMap, groupEntryKanji, groupEntryKana);
							
							if (existsInCommonWords == false) {
								continue;
							}
							
							if (foundWordSearchList.contains(currentMissingWord) == false) {
								foundWordSearchList.add(currentMissingWord);
							}
						}				
					}
				}

				reader.close();
				
				System.out.println("Zapisywanie pliku...");
						
				FileWriter searchResultFileWriter = new FileWriter(fileName + "-new");
				
				searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));
				
				searchResultFileWriter.close();
				
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
	
	private static boolean existsInCommonWords(Map<Integer, CommonWord> commonWordMap, String kanji, String kana) {
		
		if (kanji == null || kanji.equals("") == true) {
			kanji = "-";
		}
		
		Collection<CommonWord> commonWordValues = commonWordMap.values();
		
		Iterator<CommonWord> commonWordValuesIterator = commonWordValues.iterator();
				
		while (commonWordValuesIterator.hasNext() == true) {
			
			CommonWord currentCommonWord = commonWordValuesIterator.next();
			
			if (currentCommonWord.isDone() == false) {
				
				String currentCommonWordKanji = currentCommonWord.getKanji();
				
				if (currentCommonWordKanji == null || currentCommonWordKanji.equals("") == true) {
					currentCommonWordKanji = "-";
				}
				
				String currentCommonWordKana = currentCommonWord.getKana();
				
				if (kanji.equals(currentCommonWordKanji) == true && kana.equals(currentCommonWordKana) == true) {
					return true;
				}
			}
		}
		
		return false;
	}
}
