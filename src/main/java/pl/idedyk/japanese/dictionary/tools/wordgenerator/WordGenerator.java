package pl.idedyk.japanese.dictionary.tools.wordgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
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
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;

public class WordGenerator {
	
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
			
			case GENERATE_MISSING_WORD_LIST: {
				
				if (args.length != 2 && args.length != 3 && args.length != 4) {
					
					System.err.println("Niepoprawna liczba argumentów. Poprawne wywołanie: [plik z lista słów] [czy sprawdzać w jisho.org] [czy zapis w formacie common]");
					
					return;
				}
				
				String fileName = args[1];
				
				boolean checkInJishoOrg = true;
				boolean saveInCommonFormat = false;
				
				if (args.length > 2) {
					checkInJishoOrg = Boolean.parseBoolean(args[2]);
				}
				
				if (args.length > 3) {
					saveInCommonFormat = Boolean.parseBoolean(args[3]);
				}
								
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
				
				System.out.println("Szukanie...");
				
				for (String currentMissingWord : missingWords) {
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;
					
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
									
									foundWordList.add(polishJapaneseEntry);
									
									foundWordListInCommonWordMap.put(commonWord.getId(), commonWord);
									
								} else {
									alreadyAddedWordList.add(polishJapaneseEntry);
								}								
							}								
						}				
						
					} else {
						
						int counterValue = counter.incrementAndGet();
						
						PolishJapaneseEntry polishJapaneseEntry = Helper.createEmptyPolishJapaneseEntry(currentMissingWord, counterValue);
												
						boolean wordExistsInJishoOrg = false;
						
						if (checkInJishoOrg == true) {	
							
							System.out.println("Szukanie w jisho.org: " + currentMissingWord);
							
							wordExistsInJishoOrg = jishoOrgConnector.isWordExists(currentMissingWord);
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
					
					CsvReaderWriter.generateCsv("input/word-new.csv", newWordList, true, true, false);
					
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
					
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

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
				
				CsvReaderWriter.generateCsv("input/word-new.csv", newWordList, true, true, false,
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
				
				CsvReaderWriter.generateCsv("input/word-new2.csv", newWordList, true, true, false,
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
						
						GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0);
																		
						String groupEntryKanji = groupEntry.getKanji();
						String groupEntryKana = groupEntry.getKana();

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
							
							if (wordGeneratorHelper.isCommonWordExists(commonWord) == false) {
							
								newCommonWordMap.put(commonWord.getId(), commonWord);
							
								csvId++;
							}
						}				
					}
				}	
				
				// zapis do pliku
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/all_missing_word.csv");
				
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
										
					counter++;

					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentMissingWord);

					ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;
					
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
				
				if (args.length != 3) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// sprawdzenie parametrow				
				Integer minKanjiPrefixLength = null;
				Integer minKanaPrefixLength = null;
				
				if (args[1].equals("-") == false) {
				
					try {
						minKanjiPrefixLength = Integer.parseInt(args[1]);
						
					} catch (NumberFormatException e) {
						
						System.out.println("Niepoprawna minimalna długość prefiksu kanji");
						
						return;					
					}
					
					if (minKanjiPrefixLength.intValue() < 1) {
	
						System.out.println("Niepoprawna minimalna długość prefiksu kanji");
						
						return;
					}
				}
				
				if (args[2].equals("-") == false) {
					
					try {
						minKanaPrefixLength = Integer.parseInt(args[2]);
						
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
				
				int currentPrefixCounter = 1;
				
				for (String currentPrefix : allPrefixes) {
					
					System.out.format("%s - %d / %d / %d\n", currentPrefix, currentPrefixCounter, allPrefixes.size(), newCommonWordMap.size());
					
					currentPrefixCounter++;
						
					Query query = Helper.createLuceneDictionaryIndexTermQuery(currentPrefix);

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
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/prefix-word-list.csv");				
				
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
				CsvReaderWriter.generateCsv("input/word-new.csv", polishJapaneseEntries, true, true, false);
				
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

				// tutaj
				
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
}
