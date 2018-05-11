package pl.idedyk.japanese.dictionary.tools.wordgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.common.Helper.CreatePolishJapaneseEntryResult;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector.JapaneseWord;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;

public class WordGenerator {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
				
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
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		// przetwarzanie operacji
		switch (operation) {
			
			case GET_COMMON_PART_LIST: {
				
				// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -1
				// cat input/common_word.csv | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l
				// cat input/common_word.csv | head -6051 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -25 | sort -n
				
				if (args.length != 2 && args.length != 3) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// nazwa pliku z numerkami
				String fileName = args[1];
				
				// czy sprawdzac w jisho.org
				boolean checkInJishoOrg = true;
				
				if (args.length > 2) {
					checkInJishoOrg = Boolean.parseBoolean(args[2]);
				}
				
				// czytanie identyfikatorow common'owych slow
				List<String> commonWordIds = readFile(fileName);
				
				// pobranie cache ze slowami
				Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();

				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// przygotowywane slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				System.out.println("Sprawdzanie w jisho.org: " + checkInJishoOrg);
				
				JishoOrgConnector jishoOrgConnector = new JishoOrgConnector();

				Map<String, Boolean> jishoOrgConnectorWordCheckCache = new TreeMap<String, Boolean>();
				
				//
				
				File additionalWordtoCheckFile = new File("input/additional_word_to_check");
				
                LinkedHashSet<String> newAdditionalWordToCheckWordList = new LinkedHashSet<String>();
				
				newAdditionalWordToCheckWordList.addAll(readFile(additionalWordtoCheckFile.getAbsolutePath()));
				
				//
				
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
							
							//
							
							if (checkInJishoOrg == true && polishJapaneseEntry.isKanjiExists() == true) {
								searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
										"Szukanie w jisho.org (znaleziono kanji): " + polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKanji());
								
							} else if (checkInJishoOrg == true && polishJapaneseEntry.getKana() != null) {
								searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
										"Szukanie w jisho.org (znaleziono kana): " + polishJapaneseEntry.getKana(), polishJapaneseEntry.getKana());
							}

						}				
						
					} else {
						
						PolishJapaneseEntry polishJapaneseEntry = Helper.createEmptyPolishJapaneseEntry(null, Integer.valueOf(currentCommonWordId));
														
						newWordList.add(polishJapaneseEntry);				
					}			
				}
				
				// zapis porcji slow
				CsvReaderWriter.generateCsv(new String[] { "input/word-common-new.csv" }, newWordList, true, true, false, true, null);
				
				// zapis nowego pliku common
				CsvReaderWriter.writeCommonWordFile(commonWordMap, "input/common_word-nowy.csv");
				
				// dodatkowa lista slow
				
				FileWriter additionalWordtoCheckFileWriter = new FileWriter(additionalWordtoCheckFile);
				
				for (String currentWord : newAdditionalWordToCheckWordList) {
					additionalWordtoCheckFileWriter.write(currentWord + "\n");
				}
				
				additionalWordtoCheckFileWriter.close();
				
				break;
			}
			
			case GENERATE_MISSING_WORD_LIST: {
								
				if (args.length != 2 && args.length != 3 && args.length != 4 && args.length != 5) {
					
					System.err.println("Niepoprawna liczba argumentów. Poprawne wywołanie: [plik z lista słów] [czy sprawdzać w jisho.org] [czy zapis w formacie common] [czy dodawać tylko słowa, których nie ma w pliku common]");
					
					return;
				}
				
				String fileName = args[1];
				
				boolean checkInJishoOrg = true;
				boolean saveInCommonFormat = false;
				boolean addOnlyWordsWhichDoesntExistInCommonFile = false;
				
				if (args.length > 2) {
					checkInJishoOrg = Boolean.parseBoolean(args[2]);
				}
				
				if (args.length > 3) {
					saveInCommonFormat = Boolean.parseBoolean(args[3]);
				}
				
				if (args.length > 4) {
					addOnlyWordsWhichDoesntExistInCommonFile = Boolean.parseBoolean(args[4]);
				}
								
				// wczytywanie pliku z lista slow
				System.out.println("Wczytywanie brakujących słów...");
				
				List<String> missingWords = readFile(fileName);
				
				// pobranie cache ze slowami
				Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();

				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);

				// generowanie slow
				List<PolishJapaneseEntry> foundWordList = new ArrayList<PolishJapaneseEntry>();
				Map<Integer, CommonWord> foundWordListInCommonWordMap = new TreeMap<>();
				
				List<PolishJapaneseEntry> alreadyAddedWordList = new ArrayList<PolishJapaneseEntry>();
				
				List<String> foundWordSearchList = new ArrayList<String>();		
				
				List<PolishJapaneseEntry> notFoundWordList = new ArrayList<PolishJapaneseEntry>();
				List<String> notFoundWordSearchList = new ArrayList<String>();

				List<PolishJapaneseEntry> notFoundJishoFoundWordList = new ArrayList<PolishJapaneseEntry>();
				List<String> notFoundJishoFoundWordSearchList = new ArrayList<String>();
				
				AtomicInteger counter = new AtomicInteger();
				
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				System.out.println("Sprawdzanie w jisho.org: " + checkInJishoOrg);
				
				JishoOrgConnector jishoOrgConnector = new JishoOrgConnector();

				Map<String, Boolean> jishoOrgConnectorWordCheckCache = new TreeMap<String, Boolean>();

				//
				
				File additionalWordtoCheckFile = new File("input/additional_word_to_check");
								
				LinkedHashSet<String> newAdditionalWordToCheckWordList = new LinkedHashSet<String>();
								
				newAdditionalWordToCheckWordList.addAll(readFile(additionalWordtoCheckFile.getAbsolutePath()));
				
				//
								
				System.out.println("Szukanie...");
				
				for (String currentMissingWord : missingWords) {
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 30).scoreDocs;
					
					if (scoreDocs.length > 0) {
						
						foundWordSearchList.add(currentMissingWord);
						
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							// znaleziony obiekt od lucene
							GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
							
							Integer groupId = groupEntryFromLucene.getGroup().getId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedGroupId.contains(groupId) == true) {
								continue;
								
							} else {
								alreadyCheckedGroupId.add(groupId);
								
							}
							
							// szukamy pelnej grupy
							Group groupInDictionary = jmeNewDictionary.getGroupById(groupId);
							
							List<GroupEntry> groupEntryList = groupInDictionary.getGroupEntryList();
																
							// grupujemy po tych samych tlumaczenia
							List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
										
							for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
								
								GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy
								
								int counterValue = counter.incrementAndGet();
								
								CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, counterValue, currentMissingWord);
																
								PolishJapaneseEntry polishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;
								CommonWord commonWord = Helper.convertGroupEntryToCommonWord(counterValue, groupEntry);					
								
								if (createPolishJapaneseEntryResult.alreadyAddedPolishJapaneseEntry == false) {
									
									boolean existsInCommonWords = existsInCommonWords(commonWordMap, groupEntry.getKanji(), groupEntry.getKana(), false);
									
									if (addOnlyWordsWhichDoesntExistInCommonFile == false || existsInCommonWords == false) {
										
										foundWordList.add(polishJapaneseEntry);
										
										foundWordListInCommonWordMap.put(commonWord.getId(), commonWord);
									}		
									
									//
																		
									if (checkInJishoOrg == true && polishJapaneseEntry.isKanjiExists() == true) {
										searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
												"Szukanie w jisho.org (znaleziono kanji): " + polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKanji());
										
									} else if (checkInJishoOrg == true && polishJapaneseEntry.getKana() != null) {
										searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
												"Szukanie w jisho.org (znaleziono kana): " + polishJapaneseEntry.getKana(), polishJapaneseEntry.getKana());
									}
									
								} else {
									alreadyAddedWordList.add(polishJapaneseEntry);
								}								
							}								
						}	
						
						// dodatkowe sprawdzenie, w celu poszukiwania dodatkowych slow
						if (checkInJishoOrg == true) {	
							
							searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
									"Szukanie w jisho.org (znaleziono): " + currentMissingWord, currentMissingWord);
							
						}
						
					} else {
						
						int counterValue = counter.incrementAndGet();
						
						PolishJapaneseEntry polishJapaneseEntry = Helper.createEmptyPolishJapaneseEntry(currentMissingWord, counterValue);
												
						boolean wordExistsInJishoOrg = false;
						
						if (checkInJishoOrg == true) {								
							wordExistsInJishoOrg = searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
									"Szukanie w jisho.org (nie znaleziono): " + currentMissingWord, currentMissingWord);
						}
						
						if (wordExistsInJishoOrg == true) {
							
							notFoundJishoFoundWordSearchList.add(currentMissingWord);								
							
							notFoundJishoFoundWordList.add(polishJapaneseEntry);
							
						} else {
							
							notFoundWordSearchList.add(currentMissingWord);								
							
							notFoundWordList.add(polishJapaneseEntry);
						}
					}
				}

				reader.close();
				
				System.out.println("Zapisywanie słownika...");
								
				if (saveInCommonFormat == false) {
					
					List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
					
					newWordList.addAll(foundWordList);
					newWordList.addAll(alreadyAddedWordList);
					newWordList.addAll(notFoundJishoFoundWordList);
					newWordList.addAll(notFoundWordList);
					
					CsvReaderWriter.generateCsv(new String[] { "input/word-new.csv" }, newWordList, true, true, false, true, null);
					
				} else {
					CsvReaderWriter.writeCommonWordFile(foundWordListInCommonWordMap, "input/word-new.csv");
					
				}
				
				FileWriter searchResultFileWriter = new FileWriter(fileName + "-new");
				
				searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));
				searchResultFileWriter.write("\n---------\n");
				searchResultFileWriter.write(Utils.convertListToString(notFoundJishoFoundWordSearchList));
				searchResultFileWriter.write("\n---------\n");
				searchResultFileWriter.write(Utils.convertListToString(notFoundWordSearchList));
				
				searchResultFileWriter.close();
				
				//				
				
				FileWriter additionalWordtoCheckFileWriter = new FileWriter(additionalWordtoCheckFile);
				
				for (String currentWord : newAdditionalWordToCheckWordList) {
					additionalWordtoCheckFileWriter.write(currentWord + "\n");
				}
				
				additionalWordtoCheckFileWriter.close();
				
				break;
			}
			
			case GENERATE_MISSING_WORD_LIST_NUMBER: {
				
				if (args.length != 3) {
					
					System.err.println("Niepoprawna liczba argumentów. Poprawne wywołanie: [plik z lista słów] [oczekiwana liczba nowych słów]");
					
					return;
				}
				
				String fileName = args[1];
				
				Integer expectedNewWordSize = Integer.parseInt(args[2]);
												
				// wczytywanie pliku z lista slow
				System.out.println("Wczytywanie brakujących słów...");
				
				List<String> missingWords = readFile(fileName);
				
				// pobranie cache ze slowami
				Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);

				// generowanie slow
				List<PolishJapaneseEntry> newPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
				
				int result = 0;
				
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				AtomicInteger counter = new AtomicInteger();
				
				//

				System.out.println("Szukanie...");
				
				boolean reachedLimit = false;
				
				BEFORE_LOOP:
				for (String currentMissingWord : missingWords) {
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					result++;
										
					//
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 30).scoreDocs;
					
					if (scoreDocs.length > 0) {
												
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							// znaleziony obiekt od lucene
							GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
							
							Integer groupId = groupEntryFromLucene.getGroup().getId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedGroupId.contains(groupId) == true) {
								continue;
								
							} else {
								alreadyCheckedGroupId.add(groupId);
								
							}
							
							// szukamy pelnej grupy
							Group groupInDictionary = jmeNewDictionary.getGroupById(groupId);
							
							List<GroupEntry> groupEntryList = groupInDictionary.getGroupEntryList();
																
							// grupujemy po tych samych tlumaczenia
							List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
										
							for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
								
								GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy
								
								int counterValue = counter.incrementAndGet();
								
								CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, counterValue, currentMissingWord);
																
								PolishJapaneseEntry polishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;					
																
								if (createPolishJapaneseEntryResult.alreadyAddedPolishJapaneseEntry == false) {
									
									if (reachedLimit == true) {
										result--;
										
										break BEFORE_LOOP;
									}
									
									newPolishJapaneseEntryList.add(polishJapaneseEntry);																		
								}								
							}								
						}
					}
					
					if (newPolishJapaneseEntryList.size() >= expectedNewWordSize) {
						reachedLimit = true;
					}
				}

				reader.close();
				
				System.out.println("Liczba słów: " + result + " (liczba nowych słów: " + newPolishJapaneseEntryList.size() + ")");	
				
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
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 30).scoreDocs;
					
					if (scoreDocs.length > 0) {
						
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							GroupEntry groupEntry = Helper.createGroupEntry(foundDocument);
							
							String groupEntryKanji = groupEntry.getKanji();
							String groupEntryKana = groupEntry.getKana();
							
							boolean existsInCommonWords = existsInCommonWords(commonWordMap, groupEntryKanji, groupEntryKana, true);
							
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
			
			case GENERATE_MISSING_WORD_LIST_FROM_TEXT: {
								
				if (args.length != 2) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}

				// nazwa pliku do wczytania
				String fileName = args[1];
				
				// wczytanie pliku
				String text = readFullyFile(fileName);

				// stworzenie tokenizer'a
				Tokenizer tokenizer = new Tokenizer();
				
				// stokonowanie tekstu
				List<Token> tokenList = tokenizer.tokenize(text);
				
				// lista slow do sprawdzenia
				Set<String> wordsToCheck = new TreeSet<String>();
								
		        for (Token token : tokenList) {
		        	
		        	String surface = token.getSurface();
		        	String baseForm = token.getBaseForm();
		        	
		        	if (surface != null && surface.trim().equals("") == false && surface.equals("*") == false) {
		        		
		        		if (Utils.isAllJapaneseChars(surface) == true) {
			        		wordsToCheck.add(surface);
		        			
		        		}		        		
		        	}
		        	
		        	if (baseForm != null && baseForm.trim().equals("") == false && baseForm.equals("*") == false) {
		        		
		        		if (Utils.isAllJapaneseChars(baseForm) == true) {
		        			wordsToCheck.add(baseForm);
		        			
		        		}
		        	}
		        }
		        
				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);
		        
				// zmienne pomocnicze
				int csvId = 1;
				
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				// generowanie slow
				System.out.println("Generowanie słów");
				
		        // sprawdzanie slow
		        for (String word : wordsToCheck) {
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(word);

					ScoreDoc[] scoreDocs = searcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
					
					if (scoreDocs.length > 0) {
						
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							// znaleziony obiekt od lucene
							GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
							
							Integer groupId = groupEntryFromLucene.getGroup().getId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedGroupId.contains(groupId) == true) {
								continue;
								
							} else {
								alreadyCheckedGroupId.add(groupId);
								
							}
							
							// szukamy pelnej grupy
							Group groupInDictionary = jmeNewDictionary.getGroupById(groupId);
							
							List<GroupEntry> groupEntryList = groupInDictionary.getGroupEntryList();
																
							// grupujemy po tych samych tlumaczenia
							List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
										
							for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
								
								GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy

								String groupEntryKanji = groupEntry.getKanji();
								String groupEntryKana = groupEntry.getKana();
																
								List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
								
								if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
																		
									CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
									
									if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
									
										newCommonWordMap.put(commonWord.getId(), commonWord);
									
										csvId++;
									}
								}
							}
						}													
					}
				}
		        
				reader.close();
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/text-word-new.csv");				
				
				break;
			}
			
			case GENERATE_JMEDICT_GROUP_WORD_LIST: {
				
				if (args.length != 1) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// pobranie entity mapper'a
				final JMEDictEntityMapper entityMapper = wordGeneratorHelper.getJmedictEntityMapper();
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
				
				KanaHelper kanaHelper = new KanaHelper();
				
				final Map<String, GroupEntry> newWordListAndGroupEntryMap = new HashMap<String, GroupEntry>();
				
				Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
						continue;
					}

					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_JMEDICT_ALTERNATIVE) == true) {
						continue;
					}
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
					
					List<PolishJapaneseEntry> smallNewWordList = new ArrayList<PolishJapaneseEntry>();
					
					boolean canAdd = true;
					
					String kanji = polishJapaneseEntry.getKanji();
					String kana = polishJapaneseEntry.getKana();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
											
					if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
										
						for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
							
							String groupEntryKanji = groupEntry.getKanji();
							String groupEntryKana = groupEntry.getKana();
													
							PolishJapaneseEntry findPolishJapaneseEntry = 
									Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
									groupEntryKanji, groupEntryKana);
							
							if (findPolishJapaneseEntry != null) {
								
								if (findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
									canAdd = false;
								}
								
							} else {
								
								String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntry);
								
								if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
									
									alreadyAddedGroupEntry.add(keyForGroupEntry);
									
									PolishJapaneseEntry newPolishJapaneseEntry = (PolishJapaneseEntry)polishJapaneseEntry.clone();
									
									if (groupEntryKanji == null || groupEntryKanji.equals("") == true) {
										groupEntryKanji = "-";
									}
									
									newPolishJapaneseEntry.setKanji(groupEntryKanji);
																	
									newPolishJapaneseEntry.setKana(groupEntryKana);
									
									newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
									
									newPolishJapaneseEntry.setWordType(getWordType(groupEntryKana));
									
									String romaji = null;
									
									WordType wordType = newPolishJapaneseEntry.getWordType();
									
									if (wordType == WordType.HIRAGANA || wordType == WordType.KATAKANA || wordType == WordType.HIRAGANA_KATAKANA || wordType == WordType.KATAKANA_HIRAGANA) {								
										romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(groupEntryKana, kanaHelper.getKanaCache(), true));
										
									} else {
										romaji = "FIXME";
									}
									
									newPolishJapaneseEntry.setRomaji(romaji);
									
									newPolishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());
									
									smallNewWordList.add(newPolishJapaneseEntry);
									
									newWordListAndGroupEntryMap.put(getKeyForNewWordListAndGroupEntry(newPolishJapaneseEntry), groupEntry);
								}							
							}
							
						}
					}
					
					if (canAdd == true) {
						newWordList.addAll(smallNewWordList);
					}
				}
				
				// zapisywanie slownika
				System.out.println("Zapisywanie słownika...");
				
				CsvReaderWriter.generateCsv(new String[] { "input/word-new.csv" }, newWordList, true, true, false, true,
						new ICustomAdditionalCsvWriter() {
							
							@Override
							public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
								
								String key = getKeyForNewWordListAndGroupEntry(polishJapaneseEntry);
								
								GroupEntry groupEntry = newWordListAndGroupEntryMap.get(key);
								
								if (groupEntry == null) {
									throw new RuntimeException(key);
								}
								
								List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
								
								///
								
								List<String> translateList2 = new ArrayList<String>();
								
								for (GroupEntryTranslate groupEntryTranslate : translateList) {
									
									StringBuffer translate = new StringBuffer(groupEntryTranslate.getTranslate());
									
									List<String> miscInfoList = groupEntryTranslate.getMiscInfoList();
									List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
									
									boolean wasMiscOrAdditionalInfo = false;
									
									for (int idx = 0; miscInfoList != null && idx < miscInfoList.size(); ++idx) {
										
										if (wasMiscOrAdditionalInfo == false) {
											translate.append(" (");
											
											wasMiscOrAdditionalInfo = true;
											
										} else {
											translate.append(", ");
										}
										
										translate.append(entityMapper.getDesc(miscInfoList.get(idx)));
									}
									
									for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {
										
										if (wasMiscOrAdditionalInfo == false) {
											translate.append(" (");
											
											wasMiscOrAdditionalInfo = true;
											
										} else {
											translate.append(", ");
										}
									}
									
									if (wasMiscOrAdditionalInfo == true) {
										translate.append(")");
									}
									
									translateList2.add(translate.toString());
								}
								
								csvWriter.write(Utils.convertListToString(translateList2));						
							}

							@Override
							public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry)
									throws IOException {								
								throw new UnsupportedOperationException();								
							}
						}
				);				
				
				break;
			}
			
			case GENERATE_JMEDICT_GROUP_WORD_LIST2: {
				
				if (args.length != 1) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
								
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
				
				final Map<String, PolishJapaneseEntry> newWordListAndPolishJapaneseEntryMap = new HashMap<String, PolishJapaneseEntry>();
				
				Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
										
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
						continue;
					}

					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_JMEDICT_ALTERNATIVE) == true) {
						continue;
					}
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
								
					String kanji = polishJapaneseEntry.getKanji();
					String kana = polishJapaneseEntry.getKana();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
											
					if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
						
						groupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
						
						List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
						
						for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
							
							for (GroupEntry groupEntry : theSameTranslateGroupEntryList) {
								
								String groupEntryKanji = groupEntry.getKanji();
								String groupEntryKana = groupEntry.getKana();

								PolishJapaneseEntry findPolishJapaneseEntry = 
										Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
										groupEntryKanji, groupEntryKana);

								if (findPolishJapaneseEntry == null) {
									
									String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntry);
									
									if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
										
										alreadyAddedGroupEntry.add(keyForGroupEntry);

										//
										
										CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, 
												polishJapaneseEntry.getId(), null);
										
										PolishJapaneseEntry newPolishJapaneseEntry = createPolishJapaneseEntryResult.polishJapaneseEntry;								

										newWordList.add(newPolishJapaneseEntry);
										
										newWordListAndPolishJapaneseEntryMap.put(getKeyForNewWordListAndGroupEntry(newPolishJapaneseEntry), polishJapaneseEntry);
									}							
								}
							}
						}
					}
				}
				
				System.out.println(newWordList.size());
						
				System.out.println("Zapisywanie słownika...");
				
				CsvReaderWriter.generateCsv(new String[] { "input/word-new2.csv" }, newWordList, true, true, false, true,
						new ICustomAdditionalCsvWriter() {
							
							@Override
							public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
								
								String key = getKeyForNewWordListAndGroupEntry(polishJapaneseEntry);
								
								PolishJapaneseEntry sourcePolishJapaneseEntry = newWordListAndPolishJapaneseEntryMap.get(key);
								
								if (sourcePolishJapaneseEntry == null) {
									throw new RuntimeException(key);
								}
														
								csvWriter.write(Utils.convertListToString(sourcePolishJapaneseEntry.getTranslates()));
								csvWriter.write(sourcePolishJapaneseEntry.getInfo());
							}
							
							@Override
							public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry)
									throws IOException {								
								throw new UnsupportedOperationException();								
							}
						}
				);

				break;
			}
			
			case SHOW_MISSING_PRIORITY_WORDS: {
				
				if (args.length != 1) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// wczytanie i cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// generowanie priorytetowych slow
				List<Group> groupList = jmeNewDictionary.getGroupList();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				int csvId = 1;
				
				for (Group group : groupList) {
					
					List<GroupEntry> groupEntryList = group.getGroupEntryList();
					
					List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
								
					for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
						
						GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0);
										
						List<String> priority = groupEntry.getPriority();
						
						if (priority.size() == 0) {
							continue;
						}
														
						String groupEntryKanji = groupEntry.getKanji();
						String groupEntryKana = groupEntry.getKana();
											
						List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
							
						if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
							System.out.println(groupEntry);
							
							CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
							
							if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
								
								newCommonWordMap.put(commonWord.getId(), commonWord);
								
								csvId++;
							}
						}				
					}
				}	
				
				// zapis do pliku
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/missing_priority_common_word.csv");
				
				break;
			}
			
			case SHOW_ALL_MISSING_WORDS: {
								
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
				
				Options options = new Options();

				options.addOption("mnknj", "min-kanji-length", true, "Min kanji length");
				options.addOption("mnkaj", "min-kana-length", true, "Min kana length");
				
				options.addOption("maknj", "max-kanji-length", true, "Max kanji length");
				options.addOption("makaj", "max-kana-length", true, "Max kanji length");
				
				options.addOption("okn", "only-kanji", false, "Only kanji");
				options.addOption("oka", "only-kana", false, "Only kana");
				
				options.addOption("aig", "all-in-group", false, "All in group");
				options.addOption("dcicf", "dont-check-in-common-file", false, "Don't check in common file");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.SHOW_ALL_MISSING_WORDS.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.SHOW_ALL_MISSING_WORDS.getOperation(), options );
					
					System.exit(1);
				}
				
				// parametry
				Integer minKanjiLength = null;
				Integer minKanaLength = null;				
				
				Integer maxKanjiLength = null;
				Integer maxKanaLength = null;

				Boolean onlyKanji = null;
				Boolean onlyKana = null;
				
				boolean allInGroup = false;
				boolean dontCheckInCommonFile = false;
				
				if (commandLine.hasOption("min-kanji-length") == true) {
					minKanjiLength = Integer.parseInt(commandLine.getOptionValue("min-kanji-length"));
				}

				if (commandLine.hasOption("max-kanji-length") == true) {
					maxKanjiLength = Integer.parseInt(commandLine.getOptionValue("max-kanji-length"));
				}

				if (commandLine.hasOption("min-kana-length") == true) {
					minKanaLength = Integer.parseInt(commandLine.getOptionValue("min-kana-length"));
				}

				if (commandLine.hasOption("max-kana-length") == true) {
					maxKanaLength = Integer.parseInt(commandLine.getOptionValue("max-kana-length"));
				}
				
				if (commandLine.hasOption("only-kanji") == true) {
					onlyKanji = true;	
				}

				if (commandLine.hasOption("only-kana") == true) {
					onlyKana = true;	
				}
				
				if (commandLine.hasOption("all-in-group") == true) {
					allInGroup = true;
				}
				
				if (commandLine.hasOption("dont-check-in-common-file") == true) {
					dontCheckInCommonFile = true;
				}				

				//				
				
				// wczytanie i cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// generowanie brakujacych slow
				List<Group> groupList = jmeNewDictionary.getGroupList();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				int csvId = 1;

				for (Group group : groupList) {
					
					List<GroupEntry> groupEntryList = group.getGroupEntryList();
					
					List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
								
					for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
						
						for (int groupEntryListTheSameTranslateIdx = 0; groupEntryListTheSameTranslateIdx < groupEntryListTheSameTranslate.size(); ++groupEntryListTheSameTranslateIdx) {
							
							if (groupEntryListTheSameTranslateIdx == 1 && allInGroup == false) {
								break;
							}
							
							GroupEntry groupEntry = groupEntryListTheSameTranslate.get(groupEntryListTheSameTranslateIdx);
							
							String groupEntryKanji = groupEntry.getKanji();
							String groupEntryKana = groupEntry.getKana();
							
							// czy jest znak kanji
							if (onlyKanji != null && onlyKanji.booleanValue() == true && groupEntryKanji == null) {
								continue;
							}
							
							// czy tylko kana
							if (onlyKana != null && onlyKana.booleanValue() == true && groupEntryKanji != null) {
								continue;
							}

							// filtrowanie po dlugosci												
							if (minKanjiLength != null && groupEntryKanji != null && groupEntryKanji.length() < minKanjiLength) {
								continue;
							}
							
							if (minKanaLength != null && groupEntryKana != null && groupEntryKana.length() < minKanaLength) {
								continue;
							}

							
							if (maxKanjiLength != null && groupEntryKanji != null && groupEntryKanji.length() > maxKanjiLength) {
								continue;
							}

							if (maxKanaLength != null && groupEntryKana.length() > maxKanaLength) {
								continue;
							}
												
							List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
								
							if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
									
								System.out.println(groupEntry);
								
								CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
								
								if (dontCheckInCommonFile == true || wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
								
									newCommonWordMap.put(commonWord.getId(), commonWord);
								
									csvId++;
								}
							}				
						}
					}
				}	
				
				// zapis do pliku
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/all_missing_word.csv");
				
				break;
			}
			
			case SHOW_ALL_MISSING_WORDS_FROM_GROUP_ID: {
				
				// lista slow
				List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				Map<Integer, Integer> groupIdsAlreadyAddCount = new TreeMap<Integer, Integer>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
					
					if (jmedictRawDataList != null && jmedictRawDataList.size() > 0) {
						
						String groupIdString = jmedictRawDataList.get(0).substring(9);
						
						Integer groupId = new Integer(groupIdString);
						
						Integer groupIdCount = groupIdsAlreadyAddCount.get(groupId);
						
						if (groupIdCount == null) {
							groupIdCount = 0;
						}
						
						groupIdCount = groupIdCount + 1;
						
						groupIdsAlreadyAddCount.put(groupId, groupIdCount);
					}
				}
				
				Map<Integer, CommonWord> missingPartialCommonMap = new TreeMap<>();
				Map<Integer, CommonWord> missingFullCommonMap = new TreeMap<>();
				Map<Integer, CommonWord> missingOverfullCommonMap = new TreeMap<>();
								
				// generowanie brakujacych slow
				List<Group> groupList = jmeNewDictionary.getGroupList();
				
				for (Group group : groupList) {
					
					Integer groupId = group.getId();
					
					int groupIdCount = group.getGroupEntryList().size();
					
					//
					
					Integer groupIdsAlreadyAddForGroupId = groupIdsAlreadyAddCount.get(groupId);
					
					if (groupIdsAlreadyAddForGroupId == null) { // nie ma takiego slowa w moim slowniku
						
						int counter = 0;
						
						for (GroupEntry groupEntry : group.getGroupEntryList()) {
							
							int csvId = (groupEntry.getGroup().getId() * 100) + counter;
							
							CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
							
							missingFullCommonMap.put(commonWord.getId(), commonWord);
							
							counter++;
						}
						
					} else { // jest slowko, sprawdzamy ilosc

						if (groupIdsAlreadyAddForGroupId == groupIdCount) { // jest ok
							// noop
							
						} else { // nie zgadza sie ilosc, powinny byc wszystkie
							
							int counter = 0;
							
							for (GroupEntry groupEntry : group.getGroupEntryList()) {
								
								int csvId = (groupEntry.getGroup().getId() * 100) + counter;
								
								CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
								
								if (groupIdsAlreadyAddForGroupId < groupIdCount) {
									missingPartialCommonMap.put(commonWord.getId(), commonWord);
									
								} else {
									missingOverfullCommonMap.put(commonWord.getId(), commonWord);
								}
								
								counter++;
							}
						}

					}
					
				}
				
				//System.out.println(groupIdsAlreadyAddCount);
				
				// zapis do pliku
				CsvReaderWriter.writeCommonWordFile(missingPartialCommonMap, "input/all_missing_word_from_group_id_partial.csv");

				CsvReaderWriter.writeCommonWordFile(missingFullCommonMap, "input/all_missing_word_from_group_id_full.csv");
				
				CsvReaderWriter.writeCommonWordFile(missingOverfullCommonMap, "input/all_missing_word_from_group_id_overfull.csv");
				
				break;
			}
			
			case SHOW_ALREADY_ADD_WORDS: {
				
				if (args.length != 2) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				String fileName = args[1];
								
				// wczytywanie pliku z lista slow
				System.out.println("Wczytywanie brakujących słów...");
				
				List<String> missingWords = readFile(fileName);
				
				// pobranie cache ze slowami
				Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();

				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);

				// generowanie slow				
				List<String> foundWordSearchList = new ArrayList<String>();		
								
				int counter = 0;
				
				Set<Integer> alreadyFoundDocument = new TreeSet<Integer>();
								
				System.out.println("Szukanie...");
								
				for (String currentMissingWord : missingWords) {
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
										
					counter++;

					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 30).scoreDocs;
					
					if (scoreDocs.length > 0) {
						
						Boolean currentMissingWordAlreadyFound = null;
						
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							if (alreadyFoundDocument.contains(scoreDoc.doc) == true) {
								continue;
								
							} else {
								alreadyFoundDocument.add(scoreDoc.doc);
								
							}
							
							if (currentMissingWordAlreadyFound == null) {
								currentMissingWordAlreadyFound = true;
							}							

							Document foundDocument = searcher.doc(scoreDoc.doc);

							GroupEntry groupEntry = Helper.createGroupEntry(foundDocument);
							
							CreatePolishJapaneseEntryResult createPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, counter, currentMissingWord);
																			
							if (createPolishJapaneseEntryResult.alreadyAddedPolishJapaneseEntry == false) {
								currentMissingWordAlreadyFound = false;
								
								break;								
							}
						}	
						
						if (currentMissingWordAlreadyFound != null && currentMissingWordAlreadyFound.booleanValue() == true) {
							foundWordSearchList.add(currentMissingWord);
						}
					}
				}

				reader.close();
				
				System.out.println("Zapisywanie słownika...");
								
				FileWriter searchResultFileWriter = new FileWriter(fileName + "-new");
				
				searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));				
				searchResultFileWriter.close();
				
				break;
			}
			
			case SHOW_ALREADY_ADD_COMMON_WORDS: {
				
				// wczytanie i cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();

				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// przegladanie listy common'owych plikow i sprawdzanie, czy nie jest juz dodany
				Collection<CommonWord> commonWordValues = commonWordMap.values();
				
				Iterator<CommonWord> commonWordValuesIterator = commonWordValues.iterator();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				while (commonWordValuesIterator.hasNext() == true) {
					
					CommonWord currentCommonWord = commonWordValuesIterator.next();
					
					if (currentCommonWord.isDone() == false) {
						
						List<PolishJapaneseEntry> findPolishJapaneseEntry = 
								Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, currentCommonWord.getKanji(), currentCommonWord.getKana());
						
						// ta pozycja jej juz dodana
						if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {							
							newCommonWordMap.put(currentCommonWord.getId(), currentCommonWord);
							
							System.out.println(currentCommonWord.getId());
						}
					}
				}
				
				// zapis juz dodanych common'owych slow
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/already-added-common_word.csv");				
				
				break;
			}
			
			case FIND_MISSING_THE_SAME_KANJI: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
						
				Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
								
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
						continue;
					}

					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_JMEDICT_ALTERNATIVE) == true) {
						continue;
					}
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
								
					String kanji = polishJapaneseEntry.getKanji();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryListInOnlyKanji(kanji);
					
					if (groupEntryList == null) {
						continue;
					}
					
					for (GroupEntry groupEntry : groupEntryList) {
						
						List<GroupEntry> groupEntryListForGroupEntry = groupEntry.getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
						
						List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryListForGroupEntry);
										
						for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
							
							GroupEntry groupEntryInGroup = theSameTranslateGroupEntryList.get(0);
													
							String groupEntryKanji = groupEntryInGroup.getKanji();
							String groupEntryKana = groupEntryInGroup.getKana();
							
							List<PolishJapaneseEntry> findPolishJapaneseEntryList = 
									Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
			
							if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
								String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntryInGroup);
								
								if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
									
									alreadyAddedGroupEntry.add(keyForGroupEntry);
									
									//
									
									CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntryInGroup);
									
									if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
									
										newCommonWordMap.put(commonWord.getId(), commonWord);
									
										csvId++;
									}
								}
							}
						}
					}			
				}
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/missing-the-same-kanji.csv");				
				
				break;
			}
			
			case FIND_MISSING_THE_SAME_KANA: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(jmeNewDictionary, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
						
				Set<String> alreadyAddedGroupEntry = new TreeSet<String>();
								
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
						continue;
					}

					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_JMEDICT_ALTERNATIVE) == true) {
						continue;
					}
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
								
					String kana = polishJapaneseEntry.getKana();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryListInOnlyKana(kana);
					
					if (groupEntryList == null) {
						continue;
					}
					
					for (GroupEntry groupEntry : groupEntryList) {
						
						List<GroupEntry> groupEntryListForGroupEntry = groupEntry.getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
						
						List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryListForGroupEntry);
										
						for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
							
							GroupEntry groupEntryInGroup = theSameTranslateGroupEntryList.get(0);
													
							String groupEntryKanji = groupEntryInGroup.getKanji();
							String groupEntryKana = groupEntryInGroup.getKana();
							
							List<PolishJapaneseEntry> findPolishJapaneseEntryList = 
									Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
			
							if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
								String keyForGroupEntry = getKeyForAlreadyAddedGroupEntrySet(groupEntryInGroup);
								
								if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {
									
									alreadyAddedGroupEntry.add(keyForGroupEntry);
									
									//
									
									CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntryInGroup);
									
									if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {

										newCommonWordMap.put(commonWord.getId(), commonWord);
									
										csvId++;
									}
								}
							}
						}
					}			
				}
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/missing-the-same-kana.csv");				
				
				break;
			}
			
			case GENERATE_PREFIX_WORD_LIST: {
				
				if (args.length != 4) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// sprawdzenie parametrow	
				String additionalCustomFileName = null;
				
				Integer minKanjiPrefixLength = null;
				Integer minKanaPrefixLength = null;
				
				if (args[1].equals("-") == false) {
					additionalCustomFileName = args[1];
				}
				
				if (args[2].equals("-") == false) {
				
					try {
						minKanjiPrefixLength = Integer.parseInt(args[2]);
						
					} catch (NumberFormatException e) {
						
						System.out.println("Niepoprawna minimalna długość prefiksu kanji");
						
						return;					
					}
					
					if (minKanjiPrefixLength.intValue() < 1) {
	
						System.out.println("Niepoprawna minimalna długość prefiksu kanji");
						
						return;
					}
				}
				
				if (args[3].equals("-") == false) {
					
					try {
						minKanaPrefixLength = Integer.parseInt(args[3]);
						
					} catch (NumberFormatException e) {
						
						System.out.println("Niepoprawna minimalna długość prefiksu kana");
						
						return;					
					}
					
					if (minKanaPrefixLength.intValue() < 1) {

						System.out.println("Niepoprawna minimalna długość prefiksu kana");
						
						return;
					}
				}				
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				// wczytanie slownika jmedict
				final JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				final IndexSearcher searcher = new IndexSearcher(reader);
								
				// generowanie slow
				System.out.println("Generowanie słów...");
												
				final Set<String> allPrefixes = new TreeSet<String>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					if (minKanjiPrefixLength != null) {
						
						String kanji = polishJapaneseEntry.getKanji();
						
						for (int startIdx = 0; startIdx < kanji.length(); ++startIdx) {
							
							for (int endIdx = 0; endIdx <= kanji.length(); ++endIdx) {
								
								if (endIdx <= startIdx) {
									continue;
								}
								
								if (endIdx - startIdx < minKanjiPrefixLength) {
									continue;
								}
								
								allPrefixes.add(kanji.substring(startIdx, endIdx));
							}			
						}
					}
					
					if (minKanaPrefixLength != null) {
						
						String kana = polishJapaneseEntry.getKana();
												
						for (int startIdx = 0; startIdx < kana.length(); ++startIdx) {
							
							for (int endIdx = 0; endIdx <= kana.length(); ++endIdx) {
								
								if (endIdx <= startIdx) {
									continue;
								}
								
								if (endIdx - startIdx < minKanaPrefixLength) {
									continue;
								}
								
								allPrefixes.add(kana.substring(startIdx, endIdx));
							}			
						}
					}
				}
				
				// wczytywanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				for (CommonWord commonWord : commonWordMap.values()) {
					
					if (minKanjiPrefixLength != null) {
						
						String kanji = commonWord.getKanji();
						
						for (int startIdx = 0; startIdx < kanji.length(); ++startIdx) {
							
							for (int endIdx = 0; endIdx <= kanji.length(); ++endIdx) {
								
								if (endIdx <= startIdx) {
									continue;
								}
								
								if (endIdx - startIdx < minKanjiPrefixLength) {
									continue;
								}
								
								allPrefixes.add(kanji.substring(startIdx, endIdx));
							}			
						}
					}
					
					if (minKanaPrefixLength != null) {
						
						String kana = commonWord.getKana();
												
						for (int startIdx = 0; startIdx < kana.length(); ++startIdx) {
							
							for (int endIdx = 0; endIdx <= kana.length(); ++endIdx) {
								
								if (endIdx <= startIdx) {
									continue;
								}
								
								if (endIdx - startIdx < minKanaPrefixLength) {
									continue;
								}
								
								allPrefixes.add(kana.substring(startIdx, endIdx));
							}			
						}
					}
				}
				
				// wczytywanie dodatkowego pliku
				
				if (additionalCustomFileName != null) {
					
					File customFile = new File(additionalCustomFileName);
					
					if (customFile.isFile() == true) {
						
						// wczytywanie dodatkowego pliku
						
						List<String> customFileLines = readFile(customFile.getAbsolutePath());
						
						for (String currentCustomFileLine : customFileLines) {
													
							for (int startIdx = 0; startIdx < currentCustomFileLine.length(); ++startIdx) {
								
								for (int endIdx = 0; endIdx <= currentCustomFileLine.length(); ++endIdx) {
									
									if (endIdx <= startIdx) {
										continue;
									}
									
									String substring = currentCustomFileLine.substring(startIdx, endIdx);
									
									boolean isAllJapaneseChars = Utils.isAllJapaneseChars(substring);
																	
									if (isAllJapaneseChars == true) {
										allPrefixes.add(substring);									
									}
								}			
							}
						}
											
					} else {					
						System.out.println("Brak pliku: " + customFile);
						
						Thread.sleep(3000);					
					}
				}				
				
				// wczytanie pliku z cache'em juz sprawdzonych prefiksow
				final File prefixCacheFile = new File("cache/prefix_word_list.cache");
				
				Set<String> prefixCacheMap = null;
				
				if (prefixCacheFile.isFile() == true) {
					
					FileInputStream prefixCacheFileInputStream = new FileInputStream(prefixCacheFile);
					
					ObjectInputStream objectInputStream = new ObjectInputStream(prefixCacheFileInputStream);
					
					prefixCacheMap = (Set<String>) objectInputStream.readObject();
					
					objectInputStream.close();
					
				} else {
					prefixCacheMap = new TreeSet<>();
				}
				
				final Set<String> prefixCacheMap2 = prefixCacheMap;
				
				//////
				
				final Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				final Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				final AtomicInteger csvId = new AtomicInteger(0);
				
				//
				
				final ConcurrentLinkedQueue<String> allPrefixesConcurrentLinkedQueue = new ConcurrentLinkedQueue<>(allPrefixes);
								
				final AtomicInteger currentPrefixCounter = new AtomicInteger(1);
				
				class GeneratePrefixWordListThread extends Thread {

					@Override
					public void run() {
						
						try {
						
							while (true) {
								
								String currentPrefix = allPrefixesConcurrentLinkedQueue.poll();
								
								if (currentPrefix == null) {
									break;
								}
								
								System.out.format("%s - %d / %d / %d\n", currentPrefix, currentPrefixCounter.intValue(), allPrefixes.size(), newCommonWordMap.size());
								
								currentPrefixCounter.incrementAndGet();
								
								//
								
								// sprawdzamy, czy sprawdzalismy juz ten prefiks wczesniej
								synchronized (prefixCacheMap2) {
									
									if (prefixCacheMap2.contains(currentPrefix) == true) {
										continue;
										
									} else {
										prefixCacheMap2.add(currentPrefix);
									}
								}								
								
								Query query = Helper.createLuceneDictionaryIndexTermQuery(currentPrefix);
	
								ScoreDoc[] scoreDocs = searcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
	
								if (scoreDocs.length > 0) {
									
									for (ScoreDoc scoreDoc : scoreDocs) {
										
										Document foundDocument = searcher.doc(scoreDoc.doc);
	
										// znaleziony obiekt od lucene
										GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
										
										Integer groupId = groupEntryFromLucene.getGroup().getId();
										
										// czy ta grupa byla juz sprawdzana
										synchronized (alreadyCheckedGroupId) {
											
											if (alreadyCheckedGroupId.contains(groupId) == true) {
												continue;
												
											} else {
												alreadyCheckedGroupId.add(groupId);
												
											}
										}									
										
										// szukamy pelnej grupy
										Group groupInDictionary = jmeNewDictionary.getGroupById(groupId);
										
										List<GroupEntry> groupEntryList = groupInDictionary.getGroupEntryList();
																			
										// grupujemy po tych samych tlumaczenia
										List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
													
										for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
											
											GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy
	
											String groupEntryKanji = groupEntry.getKanji();
											String groupEntryKana = groupEntry.getKana();
																			
											List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
											
											if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
													
												//System.out.println(groupEntry);
												
												CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId.incrementAndGet(), groupEntry);
												
												synchronized (wordGeneratorHelper) {
													
													if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
														
														newCommonWordMap.put(commonWord.getId(), commonWord);
													}									
												}											
											}
										}
									}													
								}
							}
							
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
				
				// stworzenie watkow
				GeneratePrefixWordListThread[] generatePrefixWordListThreads = new GeneratePrefixWordListThread[6];
				
				for (int idx = 0; idx < generatePrefixWordListThreads.length; ++idx) {
					generatePrefixWordListThreads[idx] = new GeneratePrefixWordListThread();
				}
				
				// uruchomienie watkow
				for (int idx = 0; idx < generatePrefixWordListThreads.length; ++idx) {
					generatePrefixWordListThreads[idx].start();
				}
				
				// poczekanie na wszystkie watki
				for (int idx = 0; idx < generatePrefixWordListThreads.length; ++idx) {
					generatePrefixWordListThreads[idx].join();
				}
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/prefix-word-list.csv");	
				
				// zapisanie cache
				ObjectOutputStream cacheObjectOutputStream = new ObjectOutputStream(new FileOutputStream(prefixCacheFile));
				
				cacheObjectOutputStream.writeObject(prefixCacheMap2);
				
				cacheObjectOutputStream.close();
				
				break;
			}
			
			case GENERATE_PREFIX2_WORD_LIST: {
				
				if (args.length != 3) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// sprawdzenie parametrow	
				Integer minKanjiLength = null;
				Integer minKanaLength = null;				
				
				Integer maxKanjiLength = null;
				Integer maxKanaLength = null;
								
				if (args[1].equals("-") == false) {
					
					String kanjiStringRange = args[1];
					
					String[] kanjiStringRangeSplited = kanjiStringRange.split("-");
					
					if (kanjiStringRangeSplited.length != 2) {
						
						System.out.println("Niepoprawny przedział dla znaków kanji");
						
						return;
					}
					
					try {						
						minKanjiLength = Integer.parseInt(kanjiStringRangeSplited[0]);						
						maxKanjiLength = Integer.parseInt(kanjiStringRangeSplited[1]);
												
					} catch (NumberFormatException e) {
						
						System.out.println("Niepoprawny przedział dla znaków kanji");
						
						return;
						
					}
				}
				
				if (args[2].equals("-") == false) {
					
					String kanaStringRange = args[2];
					
					String[] kanaStringRangeSplited = kanaStringRange.split("-");
					
					if (kanaStringRangeSplited.length != 2) {
						
						System.out.println("Niepoprawny przedział dla znaków kana");
						
						return;
					}
					
					try {						
						minKanaLength = Integer.parseInt(kanaStringRangeSplited[0]);						
						maxKanaLength = Integer.parseInt(kanaStringRangeSplited[1]);
												
					} catch (NumberFormatException e) {
						
						System.out.println("Niepoprawny przedział dla znaków kana");
						
						return;
						
					}
				}				
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
				
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);
				
				// generowanie slow
				System.out.println("Generowanie słów...");
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
								
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				Set<String> allPrefixes = new TreeSet<String>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
															
					String kanji = polishJapaneseEntry.getKanji();
					
					if (maxKanjiLength != null) {
						allPrefixes.add(kanji);	
					}
										
					String kana = polishJapaneseEntry.getKana();
					
					if (maxKanaLength != null) {
						allPrefixes.add(kana);
					}
				}				
				
				int currentPrefixCounter = 1;
				
				for (String currentPrefix : allPrefixes) {
					
					System.out.format("%s - %d / %d / %d\n", currentPrefix, currentPrefixCounter, allPrefixes.size(), newCommonWordMap.size());
					
					currentPrefixCounter++;
						
					Query query = Helper.createLuceneDictionaryIndexPrefixQuery(currentPrefix);

					ScoreDoc[] scoreDocs = searcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
					
					if (scoreDocs.length > 0) {
													
						for (ScoreDoc scoreDoc : scoreDocs) {
							
							Document foundDocument = searcher.doc(scoreDoc.doc);

							// znaleziony obiekt od lucene
							GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
							
							Integer groupId = groupEntryFromLucene.getGroup().getId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedGroupId.contains(groupId) == true) {
								continue;
								
							} else {
								alreadyCheckedGroupId.add(groupId);
								
							}
							
							// szukamy pelnej grupy
							Group groupInDictionary = jmeNewDictionary.getGroupById(groupId);
							
							List<GroupEntry> groupEntryList = groupInDictionary.getGroupEntryList();
																
							// grupujemy po tych samych tlumaczenia
							List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
										
							for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
								
								GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy

								String groupEntryKanji = groupEntry.getKanji();
								String groupEntryKana = groupEntry.getKana();
								
								if (minKanjiLength != null && groupEntryKanji != null && groupEntryKanji.length() < minKanjiLength) {
									continue;
								}
								
								if (minKanaLength != null && groupEntryKana != null && groupEntryKana.length() < minKanaLength) {
									continue;
								}

								
								if (maxKanjiLength != null && groupEntryKanji != null && groupEntryKanji.length() > maxKanjiLength) {
									continue;
								}

								if (maxKanaLength != null && groupEntryKana.length() > maxKanaLength) {
									continue;
								}
								
								List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
								
								if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
										
									//System.out.println(groupEntry);
									
									CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, groupEntry);
									
									if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
									
										newCommonWordMap.put(commonWord.getId(), commonWord);
									
										csvId++;
									}
								}
							}
						}													
					}
				}
							
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/prefix2-word-list.csv");				
				
				break;
			}
			
			case FIX_DICTIONARY_WORD_TYPE: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// klasa do mapowania typow
				DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = wordGeneratorHelper.getDictionaryEntryJMEdictEntityMapper();
				
				for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {
					
					if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
							ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
						
						continue;
					}
					
					String kanji = currentPolishJapaneseEntry.getKanji();
					String kana = currentPolishJapaneseEntry.getKana();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
					
					if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
						
						GroupEntry groupEntry = groupEntryList.get(0);
						
						Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
						
						if (groupEntryWordTypeList.size() == 0) {
							continue;
						}
						
						List<DictionaryEntryType> polishJapaneseEntryDictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();
						
						List<DictionaryEntryType> dictionaryEntryTypeToDelete = new ArrayList<DictionaryEntryType>();
						List<DictionaryEntryType> dictionaryEntryTypeToAdd = new ArrayList<DictionaryEntryType>();
						
						// czy nalezy usunac jakis typ
						for (DictionaryEntryType currentDictionaryEntryType : polishJapaneseEntryDictionaryEntryTypeList) {

							List<String> entityList = dictionaryEntryJMEdictEntityMapper.getEntity(currentDictionaryEntryType);
							
							boolean isOneContains = false;
							
							for (String currentEntity : entityList) {
								
								if (groupEntryWordTypeList.contains(currentEntity) == true) {
									
									isOneContains = true;
									
									break;
								}
							}
							
							if (isOneContains == false) {
								dictionaryEntryTypeToDelete.add(currentDictionaryEntryType);
							}
						}						
						
						// czy nalezy dodac jakis typ
						for (String currentEntity : groupEntryWordTypeList) {
							
							DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
							
							if (dictionaryEntryType == null) {
								continue;
							}
							
							if (polishJapaneseEntryDictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
								dictionaryEntryTypeToAdd.add(dictionaryEntryType);
							}
						}

						// usuwamy typy
						for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeToDelete) {
							polishJapaneseEntryDictionaryEntryTypeList.remove(dictionaryEntryType);
						}
						
						// dodajemy typy
						for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeToAdd) {
							polishJapaneseEntryDictionaryEntryTypeList.add(dictionaryEntryType);
						}
						
						// jeszcze jedno dodatkowe sprawdzenie (potrzebne do liczenia form i przykladow) (moze nie byc wszystkich, wtedy trzeba je dodac)
						if (	polishJapaneseEntryDictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true && 
								polishJapaneseEntryDictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_VERB_U) {
							
							polishJapaneseEntryDictionaryEntryTypeList.remove(DictionaryEntryType.WORD_VERB_U);
							
							polishJapaneseEntryDictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_VERB_U);
						}

						if (	polishJapaneseEntryDictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_I) == true && 
								polishJapaneseEntryDictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_ADJECTIVE_I) {
							
							polishJapaneseEntryDictionaryEntryTypeList.remove(DictionaryEntryType.WORD_ADJECTIVE_I);
							
							polishJapaneseEntryDictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_ADJECTIVE_I);
						}
						
						/*
						if (	polishJapaneseEntryDictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_NA) == true && 
								polishJapaneseEntryDictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_ADJECTIVE_NA &&
								polishJapaneseEntryDictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_NOUN &&
								polishJapaneseEntryDictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_ADJECTIVE_NO) {
							
							polishJapaneseEntryDictionaryEntryTypeList.remove(DictionaryEntryType.WORD_ADJECTIVE_NA);
							
							polishJapaneseEntryDictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_ADJECTIVE_NA);
						}
						*/
					}
				}	
				
				// zapis nowego slownika
				CsvReaderWriter.generateCsv(new String[] { "input/word01-new.csv", "input/word02-new.csv" }, polishJapaneseEntries, true, true, false, true, null);
				
				break;
			}
			
			case SHOW_SIMILAR_RELATED_WORDS: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				// wczytanie slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();				
				
				// tworzenie indeksu lucene
				Directory luceneIndex = wordGeneratorHelper.getLuceneIndex();
								
				// stworzenie wyszukiwacza
				IndexReader reader = DirectoryReader.open(luceneIndex);

				IndexSearcher searcher = new IndexSearcher(reader);
				
				// generowanie slow
				System.out.println("Generowanie słów...");
								
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
				
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					String kanji = polishJapaneseEntry.getKanji();
					String kana = polishJapaneseEntry.getKana();
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
					
					if (groupEntryList != null && groupEntryList.size() > 0) {
						
						for (GroupEntry groupEntry : groupEntryList) {
							
							List<String> similarRelatedList = groupEntry.getSimilarRelatedList();
							
							for (String currentSimilarRelated : similarRelatedList) {
								
								int pointIdx = currentSimilarRelated.indexOf("・");
								
								if (pointIdx != -1) {
									currentSimilarRelated = currentSimilarRelated.substring(0, pointIdx);
								}
								
								Query query = Helper.createLuceneDictionaryIndexTermQuery(currentSimilarRelated);

								ScoreDoc[] scoreDocs = searcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
								
								if (scoreDocs.length > 0) {
																
									for (ScoreDoc scoreDoc : scoreDocs) {
										
										Document foundDocument = searcher.doc(scoreDoc.doc);

										// znaleziony obiekt od lucene
										GroupEntry groupEntryFromLucene = Helper.createGroupEntry(foundDocument);
										
										Integer groupId = groupEntryFromLucene.getGroup().getId();
										
										// czy ta grupa byla juz sprawdzana
										if (alreadyCheckedGroupId.contains(groupId) == true) {
											continue;
											
										} else {
											alreadyCheckedGroupId.add(groupId);
											
										}
										
										// szukamy pelnej grupy
										Group foundGroupInDictionary = jmeNewDictionary.getGroupById(groupId);
										
										List<GroupEntry> foundGroupEntryList = foundGroupInDictionary.getGroupEntryList();
																			
										// grupujemy po tych samych tlumaczenia
										List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(foundGroupEntryList);
													
										for (List<GroupEntry> foundGroupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
											
											GroupEntry foundGroupEntry = foundGroupEntryListTheSameTranslate.get(0); // pierwszy element z grupy

											String foundGroupEntryKanji = foundGroupEntry.getKanji();
											String foundGroupEntryKana = foundGroupEntry.getKana();
																			
											List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, foundGroupEntryKanji, foundGroupEntryKana);
											
											if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
												
												//System.out.println(groupEntry);
												
												CommonWord commonWord = Helper.convertGroupEntryToCommonWord(csvId, foundGroupEntry);
												
												if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {

													newCommonWordMap.put(commonWord.getId(), commonWord);
													
													csvId++;										
												}
											}
										}
									}
								}
							}							
						}
					}
				}
				
				// zapisywanie slownika
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/similar-related-word-list.csv");
				
				break;
			}
			
			/*
			case SHOW_MISSING_WORDS_AND_COMPARE_TO_JMEDICT: {
				
				// przygotowywane slownika jmedict
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				// lista wszystkich slow
				List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
				
				// lista brakujacych slow
				List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}					
					
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
					
					if (groupEntryList == null || groupEntryList.size() == 0) {
						
						groupEntryList = jmeNewDictionary.getGroupEntryListInOnlyKana(polishJapaneseEntry.getKana());
						
						if (groupEntryList == null || groupEntryList.size() == 0) {

							if (polishJapaneseEntry.getKnownDuplicatedList().size() > 0 && polishJapaneseEntry.getGroups().size() == 0) {
								result.add(polishJapaneseEntry);
							}							
						}						
					}					
				}
				
				// zapis porcji slow
				CsvReaderWriter.generateCsv(new String[] { "input/missing-words-and-compare-to-jmedict.csv" }, result, true, true, false, true, null);

				break;
			}
			*/
			
			case FIND_WORDS_WITH_JMEDICT_CHANGE: {
				
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
				
				Integer findWordsSize = null;
				
				Boolean ignoreJmedictEmptyRawData = false;
				Boolean randomWords = false;
				Boolean force = false;
				
				boolean setWords = false;
				
				Set<Integer> wordsIdsSet = null;
				Set<Integer> groupsIdsSet = null;
				
				//
				
				Options options = new Options();
				
				options.addOption("s", "size", true, "Size of find words");
				options.addOption("r", "random", false, "Random words");
				options.addOption("ijerd", "ignore-jmedict-empty-raw-data", false, "Ignore jmedict empty raw data");
				options.addOption("set", "set-words", false, "Set words");
				options.addOption("wid", "word-ids", true, "Word ids");
				options.addOption("gid", "group-ids", true, "Group ids");
				options.addOption("f", "force", false, "Force");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_WORDS_WITH_JMEDICT_CHANGE.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_WORDS_WITH_JMEDICT_CHANGE.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("set-words") == true) {
					setWords = true;
				}
								
				//
				
				final String findWordsWithJmedictChangeFilename = "input/find-words-with-jmedict-change.csv";
				
				//
				
				if (setWords == false) {
					
					if (new File(findWordsWithJmedictChangeFilename).exists() == true) {
						
						System.out.println("Plik " + findWordsWithJmedictChangeFilename + " już istnieje");
						
						System.exit(1);
					}
				
					//
					
					if (commandLine.hasOption("size") == true) {
						findWordsSize = Integer.parseInt(commandLine.getOptionValue("size"));
					}
					
					if (commandLine.hasOption("ignore-jmedict-empty-raw-data") == true) {
						ignoreJmedictEmptyRawData = true;
					}
					
					if (commandLine.hasOption("random") == true) {
						randomWords = true;
					}
					
					if (commandLine.hasOption("word-ids") == true) {
						
						wordsIdsSet = new HashSet<>();
						
						String wordsIdsString = commandLine.getOptionValue("word-ids");
						
						String[] wordsIdsStringSplited = wordsIdsString.split(",");
						
						for (String currentWordId : wordsIdsStringSplited) {
							wordsIdsSet.add(Integer.parseInt(currentWordId.trim()));							
						}
					}
					
					if (commandLine.hasOption("group-ids") == true) {
						
						groupsIdsSet = new HashSet<>();
						
						String groupsIdsString = commandLine.getOptionValue("group-ids");
						
						String[] groupsIdsStringSplited = groupsIdsString.split(",");
						
						for (String currentGroupId : groupsIdsStringSplited) {
							groupsIdsSet.add(Integer.parseInt(currentGroupId.trim()));							
						}
					}
					
					if (commandLine.hasOption("force") == true) {
						force = true;
					}
					
	
					if (findWordsSize == null) {
						System.err.println("No size of find words");
						
						System.exit(1);
					}
					
					int wordsCounter = 0;
					
					//
										
					// przygotowywane slownika jmedict
					JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
					
					// lista wszystkich slow
					List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
	
					// pobranie cache ze slowami
					final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
					
					if (randomWords == true) {
						polishJapaneseEntriesList = new ArrayList<>(polishJapaneseEntriesList);
						
						Collections.shuffle(polishJapaneseEntriesList);
					}
					
					//
					
					class PolishJapaneseEntryAndGroupEntryListWrapper {
						
						PolishJapaneseEntry polishJapaneseEntry;
						
						List<GroupEntry> groupEntryList;
						
						String additionalInfo;
	
						public PolishJapaneseEntryAndGroupEntryListWrapper(PolishJapaneseEntry polishJapaneseEntry, List<GroupEntry> groupEntryList, String additionalInfo) {
							this.polishJapaneseEntry = polishJapaneseEntry;
							this.groupEntryList = groupEntryList;
							this.additionalInfo = additionalInfo;
						}
					}
					
					//
										
					List<PolishJapaneseEntryAndGroupEntryListWrapper> result = new ArrayList<PolishJapaneseEntryAndGroupEntryListWrapper>();
					
					for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
						
						if (wordsIdsSet != null && wordsIdsSet.contains(polishJapaneseEntry.getId()) == false) {
							continue;
						}
						
						if (groupsIdsSet != null) {
							
							List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
							
							if (jmedictRawDataList != null && jmedictRawDataList.size() > 0) {
								
								String groupIdString = jmedictRawDataList.get(0).substring(9);
								
								Integer polishJapaneseEntryGroupId = new Integer(groupIdString);
								
								if (groupsIdsSet.contains(polishJapaneseEntryGroupId) == false) {
									continue;
								}								

							} else {
								continue;
							}
						}
																		
						DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
						
						if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
							continue;
						}
						
						// szukanie slow
						List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
												
						if (groupEntryList != null && groupEntryList.size() != 0) {
							
							List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
							
							// ignorojemy puste wpisy
							if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0) && ignoreJmedictEmptyRawData == true) {							
								continue;
							}
							
							//
													
							List<GroupEntry> groupEntryListForPolishJapaneseEntry = null;
							
							// szukamy grupy na podstawie id
							for (GroupEntry currentGroupEntry : groupEntryList) {
								
								String groupIdString = currentGroupEntry.getGroup().getGroupIdString();
								
								// czy ta sama grupa
								if (jmedictRawDataList.contains(groupIdString) == true) {
									groupEntryListForPolishJapaneseEntry = Arrays.asList(currentGroupEntry);
									
									break;								
								}
							}
							
							// jezeli nie udalo sie znalezc grupy
							if (groupEntryListForPolishJapaneseEntry == null) {								
								groupEntryListForPolishJapaneseEntry = groupEntryList;
							}
							
							if (JMENewDictionary.isMultiGroup(groupEntryListForPolishJapaneseEntry) == false || force == true) { // grupa pojedyncza
								
								/*
								// porownujemy tlumaczenia
								List<GroupEntryTranslate> groupEntryTranslateList = groupEntryListForPolishJapaneseEntry.get(0).getTranslateList();
								
								List<String> newJmedictRawDataList = new ArrayList<String>();
								
								for (GroupEntryTranslate groupEntryTranslate : groupEntryTranslateList) {
									groupEntryTranslate.fillJmedictRawData(newJmedictRawDataList);
								}
								
								if (jmedictRawDataList.equals(newJmedictRawDataList) == false) { // jest roznica								
									result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, groupEntryListForPolishJapaneseEntry));
								}
								*/
								
								// szukamy wszystkich slow w tej samej grupie tlumaczen
								groupEntryListForPolishJapaneseEntry = groupEntryListForPolishJapaneseEntry.get(0).getGroup().getGroupEntryList(); // podmiana na wszystkie elementy z grupy
								
								List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryListForPolishJapaneseEntry);
																
								//
								
								class PolishJapaneseEntryAndGroupEntry {
									
									PolishJapaneseEntry polishJapaneseEntry;
									
									GroupEntry groupEntry;

									public PolishJapaneseEntryAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry, GroupEntry groupEntry) {
										this.polishJapaneseEntry = polishJapaneseEntry;
										this.groupEntry = groupEntry;
									}
								}
								
								//
								
								List<PolishJapaneseEntryAndGroupEntry> allPolishJapaneseEntryListForGroupEntry = new ArrayList<>();
								
								boolean isDifferent = false;
								
								//
								
								for (List<GroupEntry> theSameTranslateGroupEntryList : groupByTheSameTranslateGroupEntryList) {
																																				
									for (GroupEntry groupEntry : theSameTranslateGroupEntryList) {
										
										String groupEntryKanji = groupEntry.getKanji();
										String groupEntryKana = groupEntry.getKana();

										PolishJapaneseEntry findPolishJapaneseEntry = 
												Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
												groupEntryKanji, groupEntryKana);
										
										if (findPolishJapaneseEntry != null) {
											allPolishJapaneseEntryListForGroupEntry.add(new PolishJapaneseEntryAndGroupEntry(findPolishJapaneseEntry, groupEntry));
										}
									}
									
									for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
										
										PolishJapaneseEntry findPolishJapaneseEntry = polishJapaneseEntryAndGroupEntry.polishJapaneseEntry;
										GroupEntry groupEntry = polishJapaneseEntryAndGroupEntry.groupEntry;
										
										//
																					
										jmedictRawDataList = findPolishJapaneseEntry.getJmedictRawDataList();
										
										// ignorojemy puste wpisy
										if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0) && ignoreJmedictEmptyRawData == true) {							
											continue;
										}											
										
										List<GroupEntryTranslate> groupEntryTranslateList = groupEntry.getTranslateList();
										
										List<String> newJmedictRawDataList = new ArrayList<String>();											
										
										for (GroupEntryTranslate groupEntryTranslate : groupEntryTranslateList) {
											groupEntryTranslate.fillJmedictRawData(newJmedictRawDataList);
										}

										if (jmedictRawDataList.equals(newJmedictRawDataList) == false) { // jest roznica												
											isDifferent = true;												
										}
									}									
								}
								
								if (isDifferent == true || force == true) {
									
									wordsCounter += groupByTheSameTranslateGroupEntryList.size();
																		
									for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
										
										PolishJapaneseEntry findPolishJapaneseEntry = polishJapaneseEntryAndGroupEntry.polishJapaneseEntry;
										GroupEntry groupEntry = polishJapaneseEntryAndGroupEntry.groupEntry;

										result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(findPolishJapaneseEntry, Arrays.asList(groupEntry), null));
									}
									
								} /*else {
									
									// sprawdzamy, czy aktualne slowo znajduje sie na liscie allPolishJapaneseEntryListForGroupEntry, jesli nie ma to mozliwy zbedny duplikat do sprawdzenia i usuniecia
									boolean existsInAllPolishJapaneseEntryListForGroupEntry = false;
									
									for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
										
										if (polishJapaneseEntryAndGroupEntry.polishJapaneseEntry.getId() == polishJapaneseEntry.getId()) {
											existsInAllPolishJapaneseEntryListForGroupEntry = true;
											
											break;
										}										
									}
									
									if (existsInAllPolishJapaneseEntryListForGroupEntry == false) {
										
										result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, null, "CHECK DUPLICATE ???"));
										
										alreadyAddPolishJapaneseEntriesId.add(polishJapaneseEntry.getId());
										
										//
										
										for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
											
											PolishJapaneseEntry findPolishJapaneseEntry = polishJapaneseEntryAndGroupEntry.polishJapaneseEntry;
											GroupEntry groupEntry = polishJapaneseEntryAndGroupEntry.groupEntry;

											result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(findPolishJapaneseEntry, Arrays.asList(groupEntry), "CHECK DUPLICATE, OK ???"));
											
											alreadyAddPolishJapaneseEntriesId.add(findPolishJapaneseEntry.getId());
										}
									}									
								} */
																
							} else { // multi grupa
								
								// dodajemy do manualnego sprawdzenia
								result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, groupEntryListForPolishJapaneseEntry, null));
								
								wordsCounter++;
							}
							
						} else { // nie znaleziono GroupEntry
														
							List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
							
							// ignorojemy puste wpisy
							if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0) && ignoreJmedictEmptyRawData == true) {							
								continue;
							}
							
							boolean ignoreNoJmedict = polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT);
							
							if (ignoreNoJmedict == false) {
								result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, null, "IGNORE_NO_JMEDICT ???"));
								
								wordsCounter++;
							}
						}
						
						// sprawdzamy ilosc znalezionych slow
						if (wordsCounter >= findWordsSize) {
							break;
						}
					}
					
					/*
					Collections.sort(result, new Comparator<PolishJapaneseEntryAndGroupEntryListWrapper>() {
	
						@Override
						public int compare(PolishJapaneseEntryAndGroupEntryListWrapper o1, PolishJapaneseEntryAndGroupEntryListWrapper o2) {
							return new Integer(o1.polishJapaneseEntry.getId()).compareTo(o2.polishJapaneseEntry.getId());
						}
					});
					*/
					
					//
					
					final Map<Integer, PolishJapaneseEntryAndGroupEntryListWrapper> idPolishJapaneseEntryAndGroupEntryListWrapperMap = new TreeMap<Integer, PolishJapaneseEntryAndGroupEntryListWrapper>();
					
					List<PolishJapaneseEntry> resultAsPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
										
					for (PolishJapaneseEntryAndGroupEntryListWrapper polishJapaneseEntryAndGroupEntryListWrapper : result) {
						
						if (idPolishJapaneseEntryAndGroupEntryListWrapperMap.containsKey(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getId()) == false) {

							idPolishJapaneseEntryAndGroupEntryListWrapperMap.put(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getId(), polishJapaneseEntryAndGroupEntryListWrapper);
							
							resultAsPolishJapaneseEntryList.add(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry);
						}						
					}
					
					//
									
					ICustomAdditionalCsvWriter customAdditionalCsvWriter = new ICustomAdditionalCsvWriter() {
						
						@Override
						public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
							
							PolishJapaneseEntryAndGroupEntryListWrapper polishJapaneseEntryAndGroupEntryListWrapper = idPolishJapaneseEntryAndGroupEntryListWrapperMap.get(polishJapaneseEntry.getId());
							
							if (polishJapaneseEntryAndGroupEntryListWrapper.additionalInfo != null) {
								csvWriter.write(polishJapaneseEntryAndGroupEntryListWrapper.additionalInfo);
							}
							
							if (polishJapaneseEntryAndGroupEntryListWrapper.groupEntryList != null) {
								
								if (polishJapaneseEntryAndGroupEntryListWrapper.groupEntryList.size() == 1) {
									csvWriter.write("SINGLEGROUP");
									
								} else {
									csvWriter.write("MULTIGROUP");
								}
								
								for (GroupEntry groupEntry : polishJapaneseEntryAndGroupEntryListWrapper.groupEntryList) {
									
									CreatePolishJapaneseEntryResult newPolishJapaneseEntryResult = null;
									
									try {
										newPolishJapaneseEntryResult = Helper.createPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry, -1, "<null>");
										
									} catch (DictionaryException e) {
										
										throw new IOException(e);
									}
									
									//
									
									csvWriter.write(Helper.convertListToString(newPolishJapaneseEntryResult.polishJapaneseEntry.getTranslates()));
									csvWriter.write(newPolishJapaneseEntryResult.polishJapaneseEntry.getInfo());
		
									//
									
									List<String> newJmedictRawDataList = new ArrayList<String>();
									
									for (GroupEntryTranslate groupEntryTranslate : groupEntry.getTranslateList()) {
										groupEntryTranslate.fillJmedictRawData(newJmedictRawDataList);
									}
									
									csvWriter.write(Helper.convertListToString(newJmedictRawDataList));
								}
							}							
						}
						
						@Override
						public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry)
								throws IOException {								
							throw new UnsupportedOperationException();								
						}
					};				
					
					// zapis
					CsvReaderWriter.generateCsv(new String[] { findWordsWithJmedictChangeFilename }, resultAsPolishJapaneseEntryList, true, true, false, true, customAdditionalCsvWriter);
					
				} else { // ustawienie slow
					
					// lista wszystkich slow
					List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
										
					// wczytanie zmienionych slow					
					List<PolishJapaneseEntry> newChangedPolishJapaneseEntriesList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { findWordsWithJmedictChangeFilename });

					// utworz mape z id'kami zmienionych slow
					TreeMap<Integer, PolishJapaneseEntry> newChangedPolishJapaneseEntriesListIdMap = new TreeMap<Integer, PolishJapaneseEntry>();
					
					for (PolishJapaneseEntry newChangedPolishJapaneseEntry : newChangedPolishJapaneseEntriesList) {
						
						if (newChangedPolishJapaneseEntriesListIdMap.containsKey(newChangedPolishJapaneseEntry.getId()) == true) {
							throw new RuntimeException("containsKey = " + newChangedPolishJapaneseEntry.getId());
						}
						
						newChangedPolishJapaneseEntriesListIdMap.put(newChangedPolishJapaneseEntry.getId(), newChangedPolishJapaneseEntry);
					}
					
					// lista wynikowa
					List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
					
					for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
						
						// sprawdzamy, czy ten wpis jest na liscie zmienionych wpisow
						PolishJapaneseEntry changedPolishJapaneseEntry = newChangedPolishJapaneseEntriesListIdMap.get(polishJapaneseEntry.getId());
						
						if (changedPolishJapaneseEntry == null) {
							result.add(polishJapaneseEntry);
							
						} else {
							result.add(changedPolishJapaneseEntry);
						}
					}					
					
					// zapis docelowego slownika
					CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv" }, result, true, true, false, true, null);
				}
				
				break;
			}
			
			case FIND_WORDS_NO_EXIST_IN_JMEDICT: {
				
				// lista wszystkich slow
				List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();

				List<PolishJapaneseEntry> result = new ArrayList<>();
				
				JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
					
					// szukanie slow
					List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
											
					if (groupEntryList == null || groupEntryList.size() == 0) {						
						result.add(polishJapaneseEntry);
					}
				}
				
				CsvReaderWriter.generateCsv(new String[] { "input/find-words-no-exist-in-jmedict.csv" }, result, true, true, false, true, null);
				
				break;
			}
			
			case GET_WORDS_BY_ID: {
				
				final String wordIdsFileName = "input/word-ids.csv";
				
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
								
				boolean setWords = false;
				
				Set<Integer> wordsIdsSet = null;
				
				//
				
				Options options = new Options();
				
				options.addOption("wid", "word-ids", true, "Word ids");
				options.addOption("set", "set-words", false, "Set words");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.GET_WORDS_BY_ID.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.GET_WORDS_BY_ID.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("set-words") == true) {
					setWords = true;
					
				}

				if (setWords == false) {
					
					List<PolishJapaneseEntry> result = new ArrayList<>();
					
					//
										
					if (commandLine.hasOption("word-ids") == true) {
						
						wordsIdsSet = new HashSet<>();
						
						String wordsIdsString = commandLine.getOptionValue("word-ids");
						
						String[] wordsIdsStringSplited = wordsIdsString.split(",");
						
						for (String currentWordId : wordsIdsStringSplited) {
							wordsIdsSet.add(Integer.parseInt(currentWordId.trim()));							
						}
						
					} else {
						
						HelpFormatter formatter = new HelpFormatter();
						
						formatter.printHelp( Operation.GET_WORDS_BY_ID.getOperation(), options );
						
						System.exit(1);
					}
					
					// lista wszystkich slow
					List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
					
					for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
						
						if (wordsIdsSet.contains(polishJapaneseEntry.getId()) == true) {
							result.add(polishJapaneseEntry);
						}						
					}
					
					CsvReaderWriter.generateCsv(new String[] { wordIdsFileName }, result, true, true, false, true, null);
					
				} else {
					
					// lista wszystkich slow
					List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
										
					// wczytanie zmienionych slow					
					List<PolishJapaneseEntry> newChangedPolishJapaneseEntriesList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { wordIdsFileName });

					// utworz mape z id'kami zmienionych slow
					TreeMap<Integer, PolishJapaneseEntry> newChangedPolishJapaneseEntriesListIdMap = new TreeMap<Integer, PolishJapaneseEntry>();
					
					for (PolishJapaneseEntry newChangedPolishJapaneseEntry : newChangedPolishJapaneseEntriesList) {
						
						if (newChangedPolishJapaneseEntriesListIdMap.containsKey(newChangedPolishJapaneseEntry.getId()) == true) {
							throw new RuntimeException("containsKey = " + newChangedPolishJapaneseEntry.getId());
						}
						
						newChangedPolishJapaneseEntriesListIdMap.put(newChangedPolishJapaneseEntry.getId(), newChangedPolishJapaneseEntry);
					}
					
					// lista wynikowa
					List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
					
					for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
						
						// sprawdzamy, czy ten wpis jest na liscie zmienionych wpisow
						PolishJapaneseEntry changedPolishJapaneseEntry = newChangedPolishJapaneseEntriesListIdMap.get(polishJapaneseEntry.getId());
						
						if (changedPolishJapaneseEntry == null) {
							result.add(polishJapaneseEntry);
							
						} else {
							result.add(changedPolishJapaneseEntry);
						}
					}					
					
					// zapis docelowego slownika
					CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv" }, result, true, true, false, true, null);
				}
								
				break;
			}
			
			case FIND_KANJIS_WITH_KANJIDIC2_CHANGE: {
				
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
				
				Integer findKanjisSize = null;
				
				Boolean ignoreKanjiDic2EmptyRawData = false;
				Boolean randomKanjis = false;
				
				boolean setKanjis = false;
				
				Set<Integer> kanjisIdsSet = null;
				
				//
				
				Options options = new Options();
				
				options.addOption("s", "size", true, "Size of find kanjis");
				options.addOption("r", "random", false, "Random kanjis");
				options.addOption("ijerd", "ignore-kanjidic2-empty-raw-data", false, "Ignore kanjidic2 empty raw data");
				options.addOption("set", "set-kanjis", false, "Set kanjis");
				options.addOption("kid", "kanji-ids", true, "Kanji ids");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_KANJIS_WITH_KANJIDIC2_CHANGE.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_KANJIS_WITH_KANJIDIC2_CHANGE.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("set-kanjis") == true) {
					setKanjis = true;
				}
								
				//
				
				final String findKanjisWithKanjiDic2ChangeFilename = "input/find-kanjis-with-kanjidic2-change.csv";
				
				//
				
				if (setKanjis == false) {
					
					if (new File(findKanjisWithKanjiDic2ChangeFilename).exists() == true) {
						
						System.out.println("Plik " + findKanjisWithKanjiDic2ChangeFilename + " już istnieje");
						
						System.exit(1);
					}
					
					//
					
					if (commandLine.hasOption("size") == true) {
						findKanjisSize = Integer.parseInt(commandLine.getOptionValue("size"));
					}
					
					if (commandLine.hasOption("ignore-kanjidic2-empty-raw-data") == true) {
						ignoreKanjiDic2EmptyRawData = true;
					}
					
					if (commandLine.hasOption("random") == true) {
						randomKanjis = true;
					}
					
					if (commandLine.hasOption("kanji-ids") == true) {
						
						kanjisIdsSet = new HashSet<>();
						
						String kanjisIdsString = commandLine.getOptionValue("kanji-ids");
						
						String[] kanjisIdsStringSplited = kanjisIdsString.split(",");
						
						for (String currentKanjiId : kanjisIdsStringSplited) {
							kanjisIdsSet.add(Integer.parseInt(currentKanjiId.trim()));							
						}
					}
	
					if (findKanjisSize == null) {
						System.err.println("No size of find words");
						
						System.exit(1);
					}
					
					List<KanjiEntryForDictionary> kanjiEntries = wordGeneratorHelper.getKanjiEntries();

					if (randomKanjis == true) {
						
						kanjiEntries = new ArrayList<>(kanjiEntries);
						
						Collections.shuffle(kanjiEntries);
					}
					
					List<KanjiEntryForDictionary> result = new ArrayList<KanjiEntryForDictionary>();
					
					for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
						
						if (kanjisIdsSet != null && kanjisIdsSet.contains(kanjiEntry.getId()) == false) {
							continue;
						}
						
						KanjiDic2EntryForDictionary kanjiDic2Entry = (KanjiDic2EntryForDictionary)kanjiEntry.getKanjiDic2Entry();
						
						if (kanjiDic2Entry != null) {
							
							String kanjiDic2RawData = kanjiEntry.getKanjiDic2RawData();
							
							// ignorojemy puste wpisy
							if ((kanjiDic2RawData == null || kanjiDic2RawData.trim().equals("") == true) && ignoreKanjiDic2EmptyRawData == true) {							
								continue;
							}
							
							String newKanjiDic2RawData = kanjiDic2Entry.getKanjiDic2RawData();
							
							if (kanjiDic2RawData.equals(newKanjiDic2RawData) == false) { // jest roznica												
								
								// dodajemy do listy								
								result.add(kanjiEntry);
							}							
						}
						
						// sprawdzamy ilosc znalezionych slow
						if (result.size() >= findKanjisSize) {
							break;
						}						
					}
					
					FileOutputStream outputStream = new FileOutputStream(new File(findKanjisWithKanjiDic2ChangeFilename));
					
					CsvReaderWriter.generateKanjiCsv(outputStream, result, false, new ICustomAdditionalCsvWriter() {
						
						@Override
						public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry) throws IOException {
							
							KanjiDic2EntryForDictionary kanjiDic2EntryForDictionary = (KanjiDic2EntryForDictionary)kanjiEntry.getKanjiDic2Entry();
							
							csvWriter.write(kanjiDic2EntryForDictionary.getKanjiDic2RawData());
						}
						
						@Override
						public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
							throw new UnsupportedOperationException();
						}
					});
				
				} else {
					
					// lista wszystkich kanji
					List<KanjiEntryForDictionary> kanjiEntries = wordGeneratorHelper.getKanjiEntries();
					
					// wczytanie zmienionych kanji
					List<KanjiEntryForDictionary> newKanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(findKanjisWithKanjiDic2ChangeFilename, wordGeneratorHelper.getKanjiDic2EntryMap(), false);
					
					// utworz mape z id'kami zmienionych slow
					TreeMap<Integer, KanjiEntryForDictionary> newChangedKanjiEntriesIdMap = new TreeMap<Integer, KanjiEntryForDictionary>();
					
					for (KanjiEntryForDictionary newChangedKanjiEntry : newKanjiEntries) {
						
						if (newChangedKanjiEntriesIdMap.containsKey(newChangedKanjiEntry.getId()) == true) {
							throw new RuntimeException("containsKey = " + newChangedKanjiEntry.getId());
						}
						
						newChangedKanjiEntriesIdMap.put(newChangedKanjiEntry.getId(), newChangedKanjiEntry);
					}

					// lista wynikowa
					List<KanjiEntryForDictionary> result = new ArrayList<KanjiEntryForDictionary>();
					
					for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
						
						// sprawdzamy, czy ten wpis jest na liscie zmienionych wpisow
						KanjiEntryForDictionary changedKanjiEntry = newChangedKanjiEntriesIdMap.get(kanjiEntry.getId());
						
						if (changedKanjiEntry == null) {
							result.add(kanjiEntry);
							
						} else {
							result.add(changedKanjiEntry);
						}
					}					
					
					FileOutputStream outputStream = new FileOutputStream(new File("input/kanji-wynik.csv"));
					
					CsvReaderWriter.generateKanjiCsv(outputStream, result, false, null);
				}
				
				
				break;
			}
			
			case HELP: {
				
				// pobranie listy mozliwych operacji
				Operation[] operationList = Operation.values();
				
				System.out.println("Lista dostępnych operacji:\n");
				
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
	
	private static String readFullyFile(String fileName) {

		StringBuffer result = new StringBuffer();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

			while (true) {

				String line = br.readLine();

				if (line == null) {
					break;
				}
				
				result.append(line + "\n");
			}

			br.close();

			return result.toString();

		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
	
	private static boolean existsInCommonWords(Map<Integer, CommonWord> commonWordMap, String kanji, String kana, boolean checkIsDone) {
		
		if (kanji == null || kanji.equals("") == true) {
			kanji = "-";
		}
		
		Collection<CommonWord> commonWordValues = commonWordMap.values();
		
		Iterator<CommonWord> commonWordValuesIterator = commonWordValues.iterator();
				
		while (commonWordValuesIterator.hasNext() == true) {
			
			CommonWord currentCommonWord = commonWordValuesIterator.next();
			
			//
			
			String currentCommonWordKanji = currentCommonWord.getKanji();
			
			if (currentCommonWordKanji == null || currentCommonWordKanji.equals("") == true) {
				currentCommonWordKanji = "-";
			}
			
			String currentCommonWordKana = currentCommonWord.getKana();

			//
			
			if (checkIsDone == false) {
				
				if (kanji.equals(currentCommonWordKanji) == true && kana.equals(currentCommonWordKana) == true) {
					return true;
				}
				
			} else {

				if (currentCommonWord.isDone() == false) {

					if (kanji.equals(currentCommonWordKanji) == true && kana.equals(currentCommonWordKana) == true) {
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	private static WordType getWordType(String kana) {
		
		WordType wordType = null;
				
		for (int idx = 0; idx < kana.length(); ++idx) {
			
			char c = kana.charAt(idx);
			
			boolean currentCIsHiragana = Utils.isHiragana(c);
			boolean currentCIsKatakana = Utils.isKatakana(c);
			
			if (currentCIsHiragana == true) {
				
				if (wordType == null) {
					wordType = WordType.HIRAGANA;
					
				} else if (wordType == WordType.KATAKANA) {
					wordType = WordType.KATAKANA_HIRAGANA;					
				}				
			}

			if (currentCIsKatakana == true) {
				
				if (wordType == null) {
					wordType = WordType.KATAKANA;
					
				} else if (wordType == WordType.HIRAGANA) {
					wordType = WordType.HIRAGANA_KATAKANA;					
				}				
			}			
		}	
		
		return wordType;
	}
		
	private static String getKeyForAlreadyAddedGroupEntrySet(GroupEntry groupEntry) {
		
		String key = groupEntry.getGroup().getId() + "." + groupEntry.getWordTypeList().toString() + "." + groupEntry.getKanji() + "." + groupEntry.getKana() + "." + 
				groupEntry.getTranslateList().toString();
		
		return key;
	}
	
	private static String getKeyForNewWordListAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry) {
		
		String key = polishJapaneseEntry.getId() + "." + polishJapaneseEntry.getDictionaryEntryTypeList().toString() + "." + 
				polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji() +
				polishJapaneseEntry.getTranslates().toString();
		
		return key;		
	}
	
	private static boolean searchInJishoForAdditionalWords(WordGeneratorHelper wordGeneratorHelper, LinkedHashSet<String> newAdditionalWordToCheckWordList, 
			JishoOrgConnector jishoOrgConnector, Map<String, Boolean> jishoOrgConnectorWordCheckCache, 
			String messageTemplate, String word) throws Exception {
		
		Boolean result = jishoOrgConnectorWordCheckCache.get(word);
		
		if (result != null) {
			return result.booleanValue();
		}
		
		//
		
		JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
		
		Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
		
		//
		
		System.out.println(messageTemplate);
		
		//
		
		List<JapaneseWord> japaneseWords = jishoOrgConnector.getJapaneseWords(word);
		
		for (JapaneseWord japaneseWord : japaneseWords) {
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(japaneseWord.kanji, japaneseWord.kana);
			
			if (groupEntryList != null) {
				
				groupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList();
				
				boolean isAdd = true;
				
				for (GroupEntry groupEntry : groupEntryList) {
					
					if (	Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntry.getKanji(), groupEntry.getKana()) != null ||
							existsInCommonWords(commonWordMap, groupEntry.getKanji(), groupEntry.getKana(), false) == true) {
																	
						isAdd = false;
						
						break;
					}
				}
								
				if (isAdd == true) {
					
					if (japaneseWord.kanji != null) {
						newAdditionalWordToCheckWordList.add(japaneseWord.kanji);
						
					} else if (japaneseWord.kana != null) {
						newAdditionalWordToCheckWordList.add(japaneseWord.kana);
					}
				}
			}
		}
		
		//
		
		result = japaneseWords.size() > 0;
		
		jishoOrgConnectorWordCheckCache.put(word, result);
		
		return result.booleanValue();
	}
}
