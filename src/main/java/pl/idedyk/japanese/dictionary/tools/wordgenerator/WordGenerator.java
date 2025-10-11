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
import java.io.StringWriter;
import java.util.ArrayList;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.common.Validator;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector;
import pl.idedyk.japanese.dictionary.tools.JishoOrgConnector.JapaneseWord;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.RelativePriorityEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter.ICustomAdditionalCsvWriter;

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
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		// wczytywanie pomocnika slownikowego
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.init(wordGeneratorHelper);
		
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
				
				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
								
				System.out.println("Sprawdzanie w jisho.org: " + checkInJishoOrg);
				
				JishoOrgConnector jishoOrgConnector = new JishoOrgConnector();

				Map<String, Boolean> jishoOrgConnectorWordCheckCache = new TreeMap<String, Boolean>();
				
				//
				
				File additionalWordtoCheckFile = new File("input/additional_word_to_check");
				
                LinkedHashSet<String> newAdditionalWordToCheckWordList = new LinkedHashSet<String>();
				
				newAdditionalWordToCheckWordList.addAll(readFile(additionalWordtoCheckFile.getAbsolutePath()));
				
				//
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
				
				// lista identyfikatorow group (entry id): potrzebne dla zapisu w nowym formacie
				Set<Integer> entryIdSet = new LinkedHashSet<>();
				
				// przegladanie identyfikatorow
				for (String currentCommonWordId : commonWordIds) {
					
					CommonWord commonWord = commonWordMap.get(Integer.parseInt(currentCommonWordId));
					
					String commonKanji = null;
					String commonKana = null;
					
					List<Entry> entryList = null;
					
					if (commonWord != null) {
						
						commonKanji = commonWord.getKanji();
						
						if (commonKanji.equals("-") == true) {
							commonKanji = null;
						}
						
						commonKana = commonWord.getKana();
						
						entryList = dictionary2Helper.findEntryListByKanjiAndKana(commonKanji, commonKana);
												
						commonWord.setDone(true);
						
					} else {
						
						System.out.println("Nie znaleziono slowa o identyfikatorze: " + currentCommonWordId);
					}
								
					if (entryList != null && entryList.size() > 0) {
						
						for (Entry entry : entryList) {
							
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
							
							PolishJapaneseEntry polishJapaneseEntry = dictionary2Helper.generateOldPolishJapaneseEntry(entry, kanjiKanaPairList.get(0), Integer.valueOf(currentCommonWordId), null);
											
							newWordList.add(polishJapaneseEntry);
							
							//
							
							if (checkInJishoOrg == true && polishJapaneseEntry.isKanjiExists() == true) {
								searchInJishoForAdditionalWords(wordGeneratorHelper, dictionary2Helper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
										"Szukanie w jisho.org (znaleziono kanji): " + polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKanji());
								
							} else if (checkInJishoOrg == true && polishJapaneseEntry.getKana() != null) {
								searchInJishoForAdditionalWords(wordGeneratorHelper, dictionary2Helper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
										"Szukanie w jisho.org (znaleziono kana): " + polishJapaneseEntry.getKana(), polishJapaneseEntry.getKana());
							}
							
							entryIdSet.add(entry.getEntryId());
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
				
				//
				
				// zapis w nowym formacie
				{						
					// lista wynikowa
					List<Entry> resultDictionary2EntryList = new ArrayList<>();
					
					// dodatkowe informacje
					EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
					
					// chodzimy po wszystkich elementach
					for (Integer currentEntryId : entryIdSet) {
						
						Entry jmdictEntry = dictionary2Helper.getJMdictEntry(currentEntryId);
						
						if (jmdictEntry == null) { // nie znaleziono
							throw new RuntimeException(); // to nigdy nie powinno zdarzyc sie
						}
						
						Entry entryFromPolishDictionary = dictionary2Helper.getEntryFromPolishDictionary(jmdictEntry.getEntryId());
						
						if (entryFromPolishDictionary != null) { // taki wpis juz jest w polskim slowniku
							
							System.out.println("[Error] Entry already exists in polish dictionary: " + currentEntryId);
							
							continue;					
						}
						
						// uzupelnienie o puste polskie tlumaczenie
						dictionary2Helper.createEmptyPolishSense(jmdictEntry);
						
						// pobranie ze starego slownika interesujacych danych (np. romaji)
						dictionary2Helper.fillDataFromOldPolishJapaneseDictionaryForWordGenerating(jmdictEntry, entryAdditionalData);

						// dodajemy do listy
						resultDictionary2EntryList.add(jmdictEntry);
					}

					Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
					
						
					saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
					saveEntryListAsHumanCsvConfig.markRomaji = true;
					saveEntryListAsHumanCsvConfig.shiftCells = true;
					saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
											
					// zapisanie slow w nowym formacie
					dictionary2Helper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", resultDictionary2EntryList, entryAdditionalData);
				}					
				
				//
				
				break;
			}
			
			case GENERATE_MISSING_WORD_LIST: {
								
				if (args.length != 2 && args.length != 3 && args.length != 4 && args.length != 5 && args.length != 6) {
					
					System.err.println("Niepoprawna liczba argumentów. Poprawne wywołanie: [plik z lista słów] [czy sprawdzać w jisho.org] [czy zapis w formacie common] [czy dodawać tylko słowa, których nie ma w pliku common] [czy dodać wszystkie (również istniejące słowa w starym słowniku) do słownika w nowym formacie]");
					
					return;
				}
				
				String fileName = args[1];
				
				boolean checkInJishoOrg = true;
				boolean saveInCommonFormat = false;
				boolean addOnlyWordsWhichDoesntExistInCommonFile = false;
				boolean addAllFoundWordsToWordDictionary2 = false;
				
				if (args.length > 2) {
					checkInJishoOrg = Boolean.parseBoolean(args[2]);
				}
				
				if (args.length > 3) {
					saveInCommonFormat = Boolean.parseBoolean(args[3]);
				}
				
				if (args.length > 4) {
					addOnlyWordsWhichDoesntExistInCommonFile = Boolean.parseBoolean(args[4]);
				}

				if (args.length > 5) {
					addAllFoundWordsToWordDictionary2 = Boolean.parseBoolean(args[5]);
				}

				// wczytywanie pliku z lista slow
				System.out.println("Wczytywanie brakujących słów...");
				
				List<String> missingWords = readFile(fileName);
				
				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
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
				
				// lista identyfikatorow group (entry id): potrzebne dla zapisu w nowym formacie
				Set<Integer> entryIdSet = new LinkedHashSet<>();
				
				System.out.println("Sprawdzanie w jisho.org: " + checkInJishoOrg);
				
				JishoOrgConnector jishoOrgConnector = new JishoOrgConnector();

				Map<String, Boolean> jishoOrgConnectorWordCheckCache = new TreeMap<String, Boolean>();

				//
				
				File additionalWordtoCheckFile = new File("input/additional_word_to_check");
								
				LinkedHashSet<String> newAdditionalWordToCheckWordList = new LinkedHashSet<String>();
								
				newAdditionalWordToCheckWordList.addAll(readFile(additionalWordtoCheckFile.getAbsolutePath()));
								
				//
								
				System.out.println("Szukanie...");
				
				for (int idxMissingWords = 0; idxMissingWords < missingWords.size(); ++idxMissingWords) {
					
					String currentMissingWord = missingWords.get(idxMissingWords);					
						
					float percent = 100.0f * ((idxMissingWords + 1) / (float)missingWords.size());
					
					System.out.println("Postęp: " + (idxMissingWords + 1) + " / " + missingWords.size() + " (" + percent + "%)");
					
					//										
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					List<Entry> foundEntryList = dictionary2Helper.findInJMdict(currentMissingWord);
										
					if (foundEntryList.size() > 0) {
						
						foundWordSearchList.add(currentMissingWord);
						
						for (Entry foundEntry : foundEntryList) {
														
							Integer groupId = foundEntry.getEntryId();
							
							/*
							Set<Integer> filteringGroupIdSet = new HashSet<Integer>(Arrays.asList(
							1111111,
							2222222,
							3333333));
							
							if (filteringGroupIdSet.contains(groupId) == false) {
								continue;
							}
							*/
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedGroupId.contains(groupId) == true) {
								continue;
								
							} else {
								alreadyCheckedGroupId.add(groupId);
								
							}
							
							// generowanie par kanji, kana na entry
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(foundEntry, false);
																
							// grupujemy po tych samych tlumaczenia
							List<List<KanjiKanaPair>> groupByTheSameTranslate = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																	
							for (List<KanjiKanaPair> kanjiKanaPairListTheSameTranslate : groupByTheSameTranslate) {
								
								KanjiKanaPair kanjiKanaPair = kanjiKanaPairListTheSameTranslate.get(0); // pierwszy element z grupy
								
								int counterValue = counter.incrementAndGet();
								
								// generujemy wpis
								PolishJapaneseEntry polishJapaneseEntry = dictionary2Helper.generateOldPolishJapaneseEntry(foundEntry, kanjiKanaPair, counterValue, currentMissingWord);
																
								//
								
								CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(counterValue, kanjiKanaPair);					
								
								// sprawdzenie, czy to slowo juz wystepuje
								List<PolishJapaneseEntry> alreadyExistsPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(wordGeneratorHelper.getPolishJapaneseEntriesCache(), kanjiKanaPair.getKanji(), kanjiKanaPair.getKana());;
								
								if (alreadyExistsPolishJapaneseEntryList == null || alreadyExistsPolishJapaneseEntryList.size() == 0) {
																		
									if (	addOnlyWordsWhichDoesntExistInCommonFile == false || 
											existsInCommonWords(commonWordMap, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana(), false) == false) {
										
										foundWordList.add(polishJapaneseEntry);
										
										foundWordListInCommonWordMap.put(commonWord.getId(), commonWord);
										
										entryIdSet.add(foundEntry.getEntryId());
									}		
									
									//
															
									/*
									if (checkInJishoOrg == true && polishJapaneseEntry.isKanjiExists() == true) {
										searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
												"Szukanie w jisho.org (znaleziono kanji): " + polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKanji());
										
									} else if (checkInJishoOrg == true && polishJapaneseEntry.getKana() != null) {
										searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
												"Szukanie w jisho.org (znaleziono kana): " + polishJapaneseEntry.getKana(), polishJapaneseEntry.getKana());
									}
									*/
									
								} else {
									alreadyAddedWordList.add(polishJapaneseEntry);									
								}
								
								//
								
								if (addAllFoundWordsToWordDictionary2 == true) {
									entryIdSet.add(foundEntry.getEntryId());
								}
							}								
						}	
						
						/*
						// dodatkowe sprawdzenie, w celu poszukiwania dodatkowych slow
						if (checkInJishoOrg == true) {	
							
							searchInJishoForAdditionalWords(wordGeneratorHelper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
									"Szukanie w jisho.org (znaleziono): " + currentMissingWord, currentMissingWord);
							
						}
						*/
						
					} else {
						
						int counterValue = counter.incrementAndGet();
						
						PolishJapaneseEntry polishJapaneseEntry = Helper.createEmptyPolishJapaneseEntry(currentMissingWord, counterValue);
												
						boolean wordExistsInJishoOrg = false;
						
						if (checkInJishoOrg == true) {								
							wordExistsInJishoOrg = searchInJishoForAdditionalWords(wordGeneratorHelper, dictionary2Helper, newAdditionalWordToCheckWordList, jishoOrgConnector, jishoOrgConnectorWordCheckCache,
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
				
				// zapis w nowym formacie
				{						
					// lista wynikowa
					List<Entry> resultDictionary2EntryList = new ArrayList<>();
					
					// dodatkowe informacje
					EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
					
					// chodzimy po wszystkich elementach
					for (Integer currentEntryId : entryIdSet) {
						
						Entry jmdictEntry = dictionary2Helper.getJMdictEntry(currentEntryId);
						
						if (jmdictEntry == null) { // nie znaleziono
							throw new RuntimeException(); // to nigdy nie powinno zdarzyc sie
						}
						
						Entry entryFromPolishDictionary = dictionary2Helper.getEntryFromPolishDictionary(jmdictEntry.getEntryId());
						
						if (entryFromPolishDictionary != null) { // taki wpis juz jest w polskim slowniku
							
							System.out.println("[Error] Entry already exists in polish dictionary: " + currentEntryId);
							
							continue;					
						}
						
						// uzupelnienie o puste polskie tlumaczenie
						dictionary2Helper.createEmptyPolishSense(jmdictEntry);
						
						// pobranie ze starego slownika interesujacych danych (np. romaji)
						dictionary2Helper.fillDataFromOldPolishJapaneseDictionaryForWordGenerating(jmdictEntry, entryAdditionalData);

						// dodajemy do listy
						resultDictionary2EntryList.add(jmdictEntry);
					}

					Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
					
						
					saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
					saveEntryListAsHumanCsvConfig.markRomaji = true;
					saveEntryListAsHumanCsvConfig.shiftCells = true;
					saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
											
					// zapisanie slow w nowym formacie
					dictionary2Helper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", resultDictionary2EntryList, entryAdditionalData);
				}
				
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
								
				// generowanie slow
				List<PolishJapaneseEntry> newPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
				
				int result = 0;
				
				Set<Integer> alreadyCheckedEntryIds = new TreeSet<Integer>();
								
				//

				System.out.println("Szukanie...");
				
				boolean reachedLimit = false;
				
				BEFORE_LOOP:
				for (int idxMissingWords = 0; idxMissingWords < missingWords.size(); ++idxMissingWords) {
					
					String currentMissingWord = missingWords.get(idxMissingWords);					
											
					float percent = 100.0f * ((idxMissingWords + 1) / (float)missingWords.size());
					
					System.out.println("Postęp: " + (idxMissingWords + 1) + " / " + missingWords.size() + " (" + percent + "%)");

					//
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					result++;
										
					//
					
					List<Entry> foundEntryList = dictionary2Helper.findInJMdict(currentMissingWord);
										
					if (foundEntryList.size() > 0) {
												
						for (Entry foundEntry : foundEntryList) {
														
							Integer entryId = foundEntry.getEntryId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedEntryIds.contains(entryId) == true) {
								continue;
								
							} else {
								alreadyCheckedEntryIds.add(entryId);
								
							}
							
							// generowanie par kanji, kana na entry
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(foundEntry, false);
																
							// grupujemy po tych samych tlumaczenia
							List<List<KanjiKanaPair>> groupByTheSameTranslate = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																	
							for (List<KanjiKanaPair> kanjiKanaPairListTheSameTranslate : groupByTheSameTranslate) {
								
								KanjiKanaPair kanjiKanaPair = kanjiKanaPairListTheSameTranslate.get(0); // pierwszy element z grupy
																
								// generujemy wpis
								PolishJapaneseEntry polishJapaneseEntry = dictionary2Helper.generateOldPolishJapaneseEntry(foundEntry, kanjiKanaPair, -1, null);

								// sprawdzenie, czy to slowo juz wystepuje
								List<PolishJapaneseEntry> alreadyExistsPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(wordGeneratorHelper.getPolishJapaneseEntriesCache(), kanjiKanaPair.getKanji(), kanjiKanaPair.getKana());;
								
								if (alreadyExistsPolishJapaneseEntryList == null || alreadyExistsPolishJapaneseEntryList.size() == 0) {
									
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
				
				System.out.println("Liczba słów: " + result + " (liczba nowych słów: " + newPolishJapaneseEntryList.size() + ", brakuje: " + ( expectedNewWordSize - newPolishJapaneseEntryList.size()) + ")");	
				
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
								
				// tworzenie listy wynikowej				
				List<String> foundWordSearchList = new ArrayList<String>();
				
				System.out.println("Szukanie...");
				
				for (int idxMissingWords = 0; idxMissingWords < missingWords.size(); ++idxMissingWords) {
					
					String currentMissingWord = missingWords.get(idxMissingWords);					
											
					float percent = 100.0f * ((idxMissingWords + 1) / (float)missingWords.size());
					
					System.out.println("Postęp: " + (idxMissingWords + 1) + " / " + missingWords.size() + " (" + percent + "%)");

					//
						
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
					
					List<Entry> foundEntryList = dictionary2Helper.findInJMdict(currentMissingWord);
					
					if (foundEntryList != null && foundEntryList.size() > 0) {
						
						for (Entry foundEntry : foundEntryList) {
							
							// generowanie par kanji, kana na entry
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(foundEntry, false);
									
							BEFORE_KANJI_KANA_PAIR_LIST:
							for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
																
								boolean existsInCommonWords = existsInCommonWords(commonWordMap, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana(), true);
								
								if (existsInCommonWords == false) {
									continue;
								}
								
								if (foundWordSearchList.contains(currentMissingWord) == false) {
									foundWordSearchList.add(currentMissingWord);
									
									break BEFORE_KANJI_KANA_PAIR_LIST;
								}
							}							
						}				
					}
				}
				
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
		        		        
				// zmienne pomocnicze
				int csvId = 1;
				
				Set<Integer> alreadyCheckedEntryId = new TreeSet<Integer>();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				// generowanie slow
				System.out.println("Generowanie słów");
				
		        // sprawdzanie slow
		        for (String word : wordsToCheck) {
		        	
					List<Entry> foundEntryList = dictionary2Helper.findInJMdict(word);
					
					if (foundEntryList != null && foundEntryList.size() > 0) {
						
						for (Entry foundEntry : foundEntryList) {
														
							Integer entryId = foundEntry.getEntryId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedEntryId.contains(entryId) == true) {
								continue;
								
							} else {
								alreadyCheckedEntryId.add(entryId);								
							}
							
							// generowanie par kanji, kana na entry
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(foundEntry, false);
							
							// grupujemy po tych samych tlumaczenia
							List<List<KanjiKanaPair>> groupByTheSameTranslate = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
							
							for (List<KanjiKanaPair>  kanjiKanaPairListTheSameTranslate : groupByTheSameTranslate) {
								
								KanjiKanaPair kanjiKanaPair = kanjiKanaPairListTheSameTranslate.get(0); // pierwszy element z grupy

								String groupEntryKanji = kanjiKanaPair.getKanji();
								String groupEntryKana = kanjiKanaPair.getKana();
																
								List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
								
								if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
																		
									CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
									
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
				CsvReaderWriter.writeCommonWordFile(newCommonWordMap, "input/text-word-new.csv");				
				
				break;
			}
			
			case GENERATE_JMEDICT_GROUP_WORD_LIST: {
				
				if (args.length != 1) {
					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
								
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(dictionary2Helper, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
								
				final Map<String, KanjiKanaPair> newWordListAndKanjiKanaPairMap = new HashMap<String, KanjiKanaPair>();
				
				Set<String> alreadyAddedEntryKanjiKanaPair = new TreeSet<String>();
				
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
										
					KanjiKanaPair kanjiKanaPairForPolishJapaneseEntry = dictionary2Helper.findKanjiKanaPair(polishJapaneseEntry);
					
					if (kanjiKanaPairForPolishJapaneseEntry != null) {
						
						Integer groupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
						
						if (groupIdFromJmedictRawDataList == null) {
							System.out.println("!!! Please manually add entry id: " + kanjiKanaPairForPolishJapaneseEntry.getEntry().getEntryId());
							System.out.println("!!! Possible unnecessary ignore: " + polishJapaneseEntry.getId());
						}
						
						Entry entry = kanjiKanaPairForPolishJapaneseEntry.getEntry();
						
						List<KanjiKanaPair> kanjiKanaPairListGroupByTheSameTranslateListForPolishJapanaeseEntry = dictionary2Helper.getAllKanjiKanaPairListWithTheSameTranslate(entry, kanjiKanaPairForPolishJapaneseEntry.getKanji(), kanjiKanaPairForPolishJapaneseEntry.getKana());
																			
						for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListGroupByTheSameTranslateListForPolishJapanaeseEntry) {
						
							String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
							String kanjiKanaPairKana = kanjiKanaPair.getKana();
													
							PolishJapaneseEntry findPolishJapaneseEntry = 
									Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
											kanjiKanaPairKanji, kanjiKanaPairKana);
							
							if (findPolishJapaneseEntry != null) {
								
								if (findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
									canAdd = false;
								}
								
							} else {
								
								String keyForAlreadyAddedEntryKanjiKanaPair = getKeyForAlreadyAddedEntryKanjiKanaPairSet(entry, kanjiKanaPair);
								
								if (alreadyAddedEntryKanjiKanaPair.contains(keyForAlreadyAddedEntryKanjiKanaPair) == false) {
									
									alreadyAddedEntryKanjiKanaPair.add(keyForAlreadyAddedEntryKanjiKanaPair);
																		
									// generujemy nowy wpis
									PolishJapaneseEntry newPolishJapaneseEntry = dictionary2Helper.generateOldPolishJapaneseEntry(entry, kanjiKanaPair, polishJapaneseEntry.getId(), "");
									
									// i podmieniamy tlumaczenie i informacje dodatkowe z naszego wyjsciowego slowka
									newPolishJapaneseEntry.setTranslates(new ArrayList<String>(polishJapaneseEntry.getTranslates()));
									newPolishJapaneseEntry.setInfo(polishJapaneseEntry.getInfo());
									newPolishJapaneseEntry.setJmedictRawDataList(new ArrayList<String>(polishJapaneseEntry.getJmedictRawDataList()));
									
									smallNewWordList.add(newPolishJapaneseEntry);
									
									newWordListAndKanjiKanaPairMap.put(getKeyForNewWordListAndGroupEntry(newPolishJapaneseEntry), kanjiKanaPair);
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
								
								KanjiKanaPair kanjiKanaPair = newWordListAndKanjiKanaPairMap.get(key);
								
								if (kanjiKanaPair == null) {
									throw new RuntimeException(key);
								}
								
								try {
									List<String> generateTranslatesInOldFormat = dictionary2Helper.generateTranslatesInOldFormat(kanjiKanaPair, null);
																
									csvWriter.write(Utils.convertListToString(generateTranslatesInOldFormat));
									
								} catch (Exception e) {
									throw new RuntimeException(key);
								}
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
												
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(dictionary2Helper, polishJapaneseEntries);

				// generowanie slow
				System.out.println("Generowanie słów...");
				
				List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
				
				final Map<String, PolishJapaneseEntry> newWordListAndPolishJapaneseEntryMap = new HashMap<String, PolishJapaneseEntry>();
				
				Set<String> alreadyAddedEntryKanjiKanaPair = new TreeSet<String>();
										
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
										
					KanjiKanaPair kanjiKanaPairForPolishJapaneseEntry = dictionary2Helper.findKanjiKanaPair(polishJapaneseEntry);
											
					if (kanjiKanaPairForPolishJapaneseEntry != null) {
						
						Entry entry = dictionary2Helper.getJMdictEntry(polishJapaneseEntry.getGroupIdFromJmedictRawDataList());
						
						List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
						
						List<List<KanjiKanaPair>> kanjiKanaPairListGroupByTheSameTranslateListList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
						
						for (List<KanjiKanaPair> kanjiKanaPairListForTheSameTranslateList : kanjiKanaPairListGroupByTheSameTranslateListList) {
							
							for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListForTheSameTranslateList) {
								
								String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
								String kanjiKanaPairKana = kanjiKanaPair.getKana();
														
								PolishJapaneseEntry findPolishJapaneseEntry = 
										Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
												kanjiKanaPairKanji, kanjiKanaPairKana);

								if (findPolishJapaneseEntry == null) {
									
									String keyForAlreadyAddedEntryKanjiKanaPair = getKeyForAlreadyAddedEntryKanjiKanaPairSet(entry, kanjiKanaPair);
									
									if (alreadyAddedEntryKanjiKanaPair.contains(keyForAlreadyAddedEntryKanjiKanaPair) == false) {
										
										alreadyAddedEntryKanjiKanaPair.add(keyForAlreadyAddedEntryKanjiKanaPair);

										//
										
										// generujemy nowy wpis
										PolishJapaneseEntry newPolishJapaneseEntry = dictionary2Helper.generateOldPolishJapaneseEntry(entry, kanjiKanaPair, polishJapaneseEntry.getId(), "");
																				
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
				List<Entry> entryList = dictionary2Helper.getJMdict().getEntryList();
								
				// generowanie priorytetowych slow				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				int csvId = 1;
				
				for (Entry entry : entryList) {
					
					// generowanie par kanji, kana na entry
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, false);
					
					List<List<KanjiKanaPair>> groupByTheSameTranslateKanjiKanaList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
													
					for (List<KanjiKanaPair> groupByTheSameTranslateKanjiKana : groupByTheSameTranslateKanjiKanaList) {
						
						KanjiKanaPair kanjiKanaPair = groupByTheSameTranslateKanjiKana.get(0);
						
						//
						
						List<RelativePriorityEnum> allPriority = new ArrayList<>();
						
						if (kanjiKanaPair.getKanjiInfo() != null) {
							allPriority.addAll(kanjiKanaPair.getKanjiInfo().getRelativePriorityList());
						}
						
						allPriority.addAll(kanjiKanaPair.getReadingInfo().getRelativePriorityList());
												
						if (allPriority.size() == 0) {
							continue;
						}
														
						String entryKanji = kanjiKanaPair.getKanji();
						String entryKana = kanjiKanaPair.getKana();
											
						List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, entryKanji, entryKana);
							
						if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
							System.out.println(entry);
							
							CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
							
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
				options.addOption("makaj", "max-kana-length", true, "Max kana length");
				
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
				JMdict jmdict = dictionary2Helper.getJMdict();
				
				// generowanie brakujacych slow
				List<Entry> entryList = jmdict.getEntryList();
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();
				
				int csvId = 1;

				for (Entry entry : entryList) {
					
					// wyliczamy pary
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
										
					List<List<KanjiKanaPair>> groupByTheSameTranslateGroupEntryList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
								
					for (List<KanjiKanaPair> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
						
						for (int groupEntryListTheSameTranslateIdx = 0; groupEntryListTheSameTranslateIdx < groupEntryListTheSameTranslate.size(); ++groupEntryListTheSameTranslateIdx) {
							
							if (groupEntryListTheSameTranslateIdx == 1 && allInGroup == false) {
								break;
							}
							
							KanjiKanaPair kanjiKanaPair = groupEntryListTheSameTranslate.get(groupEntryListTheSameTranslateIdx);
							
							String groupEntryKanji = kanjiKanaPair.getKanji();
							String groupEntryKana = kanjiKanaPair.getKana();
							
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
									
								System.out.println(kanjiKanaPairList);
								
								CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
								
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
				
				Map<Integer, CommonWord> missingInDictionary2FormatCommonMap = new TreeMap<>();
				Map<Integer, CommonWord> missingPartialCommonMap = new TreeMap<>();
				Map<Integer, CommonWord> missingFullCommonMap = new TreeMap<>();
				Map<Integer, CommonWord> missingOverfullCommonMap = new TreeMap<>();
				
				// wczytanie slownika jmedict
				List<Entry> entryList = dictionary2Helper.getJMdict().getEntryList();
				
				// generowanie brakujacych slow				
				for (Entry entry : entryList) {
					
					Integer entryId = entry.getEntryId();
					
					// generowanie wszystkich slow
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
					
					int kanjiKanaPairListCount = kanjiKanaPairList.size();
					
					//
					
					Integer entryIdsAlreadyAddForEntryId = groupIdsAlreadyAddCount.get(entryId);
					
					if (entryIdsAlreadyAddForEntryId == null) { // nie ma takiego slowa w moim slowniku
						
						int counter = 0;
						
						for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
							
							int csvId = (entryId * 100) + counter;
							
							CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
							
							missingFullCommonMap.put(commonWord.getId(), commonWord);
							
							counter++;
						}
						
					} else { // jest slowko, sprawdzamy ilosc

						if (entryIdsAlreadyAddForEntryId == kanjiKanaPairListCount) { // jest ok
							// noop
							
						} else { // nie zgadza sie ilosc, powinny byc wszystkie
							
							int counter = 0;
							
							for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
								
								int csvId = (entryId * 100) + counter;
								
								CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
								
								if (entryIdsAlreadyAddForEntryId < kanjiKanaPairListCount) {
									missingPartialCommonMap.put(commonWord.getId(), commonWord);
									
								} else {
									missingOverfullCommonMap.put(commonWord.getId(), commonWord);
								}
								
								counter++;
							}
						}
					}
					
					// sprawdzenie, czy slowo wystepuje jeszcze w formacie dictionary 2
					Entry entryFromPolishDictionary = dictionary2Helper.getEntryFromPolishDictionary(entryId);
					
					if (entryFromPolishDictionary == null) { // nie ma w moim slowniku
						
						int counter = 0;
						
						for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
							
							int csvId = (entryId * 100) + counter;
							
							CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
							
							missingInDictionary2FormatCommonMap.put(commonWord.getId(), commonWord);
							
							counter++;
						}
					}					
				}
				
				// szukamy jeszcze slow, ktore powinny zniknac ze slownika, gdyz zostaly usuniete
				List<PolishJapaneseEntry> wordsNoExistInJmdict = new ArrayList<>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
					
					if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == true) {
						continue;
					}
					
					// szukanie slow
					List<Entry> entryListForPolishJapaneseEntry = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, true);
																
					if (entryListForPolishJapaneseEntry == null || entryListForPolishJapaneseEntry.size() == 0) {						
						wordsNoExistInJmdict.add(polishJapaneseEntry);
					}					
				}				
				
				//System.out.println(groupIdsAlreadyAddCount);
				
				// zapis do pliku
				CsvReaderWriter.writeCommonWordFile(missingPartialCommonMap, "input/all_missing_word_from_group_id_partial.csv");
				CsvReaderWriter.writeCommonWordFile(missingFullCommonMap, "input/all_missing_word_from_group_id_full.csv");
				CsvReaderWriter.writeCommonWordFile(missingOverfullCommonMap, "input/all_missing_word_from_group_id_overfull.csv");
				CsvReaderWriter.writeCommonWordFile(missingInDictionary2FormatCommonMap, "input/all_missing_word_from_group_id_in_dictionary2_format.csv");
				CsvReaderWriter.generateCsv(new String[] { "input/all_missing_word_from_group_id_words_no_exist_in_jmedict.csv" }, wordsNoExistInJmdict, true, true, false, true, null);
				
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
				
				// generowanie slow				
				List<String> foundWordSearchList = new ArrayList<String>();		
								
				Set<Integer> alreadyFoundDocument = new TreeSet<Integer>();
								
				System.out.println("Szukanie...");
								
				for (int idxMissingWords = 0; idxMissingWords < missingWords.size(); ++idxMissingWords) {
					
					String currentMissingWord = missingWords.get(idxMissingWords);					
											
					float percent = 100.0f * ((idxMissingWords + 1) / (float)missingWords.size());
					
					System.out.println("Postęp: " + (idxMissingWords + 1) + " / " + missingWords.size() + " (" + percent + "%)");

					//
					
					if (currentMissingWord.equals("") == true) {
						continue;
					}
					
					if (currentMissingWord.length() > 512) {
                        continue;
                    }
										
					List<Entry> foundEntryList = dictionary2Helper.findInJMdict(currentMissingWord);
					
					if (foundEntryList != null && foundEntryList.size() > 0) {
						
						Boolean currentMissingWordAlreadyFound = null;
						
						for (Entry entry : foundEntryList) {
							
							if (alreadyFoundDocument.contains(entry.getEntryId()) == true) {
								continue;
								
							} else {
								alreadyFoundDocument.add(entry.getEntryId());
								
							}
							
							if (currentMissingWordAlreadyFound == null) {
								currentMissingWordAlreadyFound = true;
							}							
							
							// generowanie par kanji, kana na entry
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, false);
														
							// sprawdzenie, czy to slowo juz wystepuje
							List<PolishJapaneseEntry> alreadyExistsPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(wordGeneratorHelper.getPolishJapaneseEntriesCache(), 
									kanjiKanaPairList.get(0).getKanji(), kanjiKanaPairList.get(0).getKana());;
																			
							if (alreadyExistsPolishJapaneseEntryList == null || alreadyExistsPolishJapaneseEntryList.size() == 0) {
								currentMissingWordAlreadyFound = false;
								
								break;								
							}
						}	
						
						if (currentMissingWordAlreadyFound != null && currentMissingWordAlreadyFound.booleanValue() == true) {
							foundWordSearchList.add(currentMissingWord);
						}
					}
				}
				
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
								
				// walidacja slownika
				System.out.println("Walidowanie słownika...");
				
				Validator.validateEdictGroup(dictionary2Helper, polishJapaneseEntries);

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
					
					if (polishJapaneseEntry.isKanjiExists() == false) {
						continue;
					}
					
					String kanji = polishJapaneseEntry.getKanji();
					
					List<Entry> entryListForKanji = dictionary2Helper.findEntryListByKanjiOnly(kanji);					
					
					if (entryListForKanji == null) {
						continue;
					}
					
					for (Entry entry : entryListForKanji) {
						
						List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
						
						List<List<KanjiKanaPair>> groupByTheSameTranslateListList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																
						for (List<KanjiKanaPair> theSameTranslateGroupKanjiKanaList : groupByTheSameTranslateListList) {
							
							KanjiKanaPair kanjiKanaPair = theSameTranslateGroupKanjiKanaList.get(0);
													
							String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
							String kanjiKanaPairKana = kanjiKanaPair.getKana();
							
							List<PolishJapaneseEntry> findPolishJapaneseEntryList = 
									Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanjiKanaPairKanji, kanjiKanaPairKana);
			
							if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
								String keyForGroupEntry = getKeyForAlreadyAddedEntryKanjiKanaPairSet(entry, kanjiKanaPair);
								
								if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {									
									alreadyAddedGroupEntry.add(keyForGroupEntry);
									
									//
									
									CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
									
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
								
				Validator.validateEdictGroup(dictionary2Helper, polishJapaneseEntries);

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
					
					List<Entry> entryListForKana = dictionary2Helper.findEntryListByKanaOnly(kana);					
					
					if (entryListForKana == null) {
						continue;
					}
					
					for (Entry entry : entryListForKana) {
						
						List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
						
						List<List<KanjiKanaPair>> groupByTheSameTranslateListList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																
						for (List<KanjiKanaPair> theSameTranslateGroupKanjiKanaList : groupByTheSameTranslateListList) {
							
							KanjiKanaPair kanjiKanaPair = theSameTranslateGroupKanjiKanaList.get(0);
													
							String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
							String kanjiKanaPairKana = kanjiKanaPair.getKana();
							
							List<PolishJapaneseEntry> findPolishJapaneseEntryList = 
									Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanjiKanaPairKanji, kanjiKanaPairKana);
			
							if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
								
								String keyForGroupEntry = getKeyForAlreadyAddedEntryKanjiKanaPairSet(entry, kanjiKanaPair);
								
								if (alreadyAddedGroupEntry.contains(keyForGroupEntry) == false) {									
									alreadyAddedGroupEntry.add(keyForGroupEntry);
									
									//
									
									CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
									
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
				
				final Set<Integer> alreadyCheckedEntryId = new TreeSet<Integer>();
				
				final AtomicInteger csvId = new AtomicInteger(0);
				
				//
				
				final ConcurrentLinkedQueue<String> allPrefixesConcurrentLinkedQueue = new ConcurrentLinkedQueue<>(allPrefixes);
								
				final AtomicInteger currentPrefixCounter = new AtomicInteger(1);
				
				// inicjalizacja lucene, przed odpaleniem w wielu watkow				
				dictionary2Helper.findInJMdict("init");
				
				//
				
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
								
								List<Entry> entryList = dictionary2Helper.findInJMdict(currentPrefix);
									
								if (entryList != null && entryList.size() > 0) {
									
									for (Entry entry : entryList) {
																				
										Integer entryId = entry.getEntryId();
										
										// czy ta grupa byla juz sprawdzana
										synchronized (alreadyCheckedEntryId) {
											
											if (alreadyCheckedEntryId.contains(entryId) == true) {
												continue;
												
											} else {
												alreadyCheckedEntryId.add(entryId);
												
											}
										}
										
										List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);

										// grupujemy po tych samych tlumaczenia
										List<List<KanjiKanaPair>> groupByTheSameTranslateKanjiKanaList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
													
										for (List<KanjiKanaPair> groupEntryListTheSameTranslate : groupByTheSameTranslateKanjiKanaList) {
											
											KanjiKanaPair kanjiKanaPair = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy
												
											String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
											String kanjiKanaPairKana = kanjiKanaPair.getKana();
																			
											List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanjiKanaPairKanji, kanjiKanaPairKana);
											
											if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
													
												//System.out.println(groupEntry);
												
												CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId.incrementAndGet(), kanjiKanaPair);
												
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
				GeneratePrefixWordListThread[] generatePrefixWordListThreads = new GeneratePrefixWordListThread[4];
				
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
								
				// generowanie slow
				System.out.println("Generowanie słów...");
				
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
								
				Set<Integer> alreadyCheckedEntryId = new TreeSet<Integer>();
				
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
					
					List<Entry> entryList = dictionary2Helper.findInJMdictPrefix(currentPrefix);
										
					if (entryList != null && entryList.size() > 0) {
													
						for (Entry entry : entryList) {
														
							Integer entryId = entry.getEntryId();
							
							// czy ta grupa byla juz sprawdzana
							if (alreadyCheckedEntryId.contains(entryId) == true) {
								continue;
								
							} else {
								alreadyCheckedEntryId.add(entryId);
								
							}
														
							List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
							
							// grupujemy po tych samych tlumaczenia
							List<List<KanjiKanaPair>> groupByTheSameTranslateEntryList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
										
							for (List<KanjiKanaPair> groupEntryListTheSameTranslate : groupByTheSameTranslateEntryList) {
								
								KanjiKanaPair kanjiKanaPair = groupEntryListTheSameTranslate.get(0); // pierwszy element z grupy

								String kanjiKanaPairKanji = kanjiKanaPair.getKanji();							
								String kanjiKanaPairKana = kanjiKanaPair.getKana();
								
								if (minKanjiLength != null && kanjiKanaPairKanji != null && kanjiKanaPairKanji.length() < minKanjiLength) {
									continue;
								}
								
								if (minKanaLength != null && kanjiKanaPairKana != null && kanjiKanaPairKana.length() < minKanaLength) {
									continue;
								}
								
								if (maxKanjiLength != null && kanjiKanaPairKanji != null && kanjiKanaPairKanji.length() > maxKanjiLength) {
									continue;
								}

								if (maxKanaLength != null && kanjiKanaPairKana.length() > maxKanaLength) {
									continue;
								}
								
								List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanjiKanaPairKanji, kanjiKanaPairKana);
								
								if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
										
									//System.out.println(groupEntry);
									
									CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, kanjiKanaPair);
									
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
								
				for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapaneseEntries) {
					
					if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(
							ParseAdditionalInfo.NO_TYPE_CHECK) == true) {
						
						continue;
					}
					
					KanjiKanaPair kanjiKanaPairForPolishJapaneseEntry = dictionary2Helper.findKanjiKanaPair(currentPolishJapaneseEntry);
															
					if (kanjiKanaPairForPolishJapaneseEntry != null) {
						
						List<DictionaryEntryType> allDictionaryEntryTypeList = dictionary2Helper.getOldDictionaryEntryTypeFromKanjiKanaPair(kanjiKanaPairForPolishJapaneseEntry);
												
						if (allDictionaryEntryTypeList.size() == 0) {
							continue;
						}
						
						List<DictionaryEntryType> polishJapaneseEntryDictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();
						
						List<DictionaryEntryType> dictionaryEntryTypeToDelete = new ArrayList<DictionaryEntryType>();
						List<DictionaryEntryType> dictionaryEntryTypeToAdd = new ArrayList<DictionaryEntryType>();
						
						// czy nalezy usunac jakis typ
						for (DictionaryEntryType currentDictionaryEntryType : polishJapaneseEntryDictionaryEntryTypeList) {
							
							if (allDictionaryEntryTypeList.contains(currentDictionaryEntryType) == false) {
								dictionaryEntryTypeToDelete.add(currentDictionaryEntryType);
							}
						}						
						
						// czy nalezy dodac jakis typ
						for (DictionaryEntryType dictionaryEntryType : allDictionaryEntryTypeList) {
														
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
				CsvReaderWriter.generateCsv(new String[] { "input/word01-new.csv", "input/word02-new.csv", "input/word03-new.csv", "input/word04-new.csv" }, polishJapaneseEntries, true, true, false, true, null);
				
				break;
			}
			
			case SHOW_SIMILAR_RELATED_WORDS: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				// generowanie slow
				System.out.println("Generowanie słów...");
								
				Map<Integer, CommonWord> newCommonWordMap = new TreeMap<>();

				int csvId = 1;
				
				Set<Integer> alreadyCheckedGroupId = new TreeSet<Integer>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
					
					List<Entry> entryListForPolishJapaneseEntry = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, true);
					
					if (entryListForPolishJapaneseEntry != null && entryListForPolishJapaneseEntry.size() > 0) {
						
						for (Entry entry : entryListForPolishJapaneseEntry) {
							
							List<Sense> senseList = entry.getSenseList();
							
							for (Sense currentSense : senseList) {
								
								List<String> referenceToAnotherKanjiKanaList = currentSense.getReferenceToAnotherKanjiKanaList();
								
								for (String currentSimilarRelated : referenceToAnotherKanjiKanaList) {
								
									int pointIdx = currentSimilarRelated.indexOf("・");
									
									if (pointIdx != -1) {
										currentSimilarRelated = currentSimilarRelated.substring(0, pointIdx);
									}
									
									List<Entry> foundSimilarRelatedEntryList = dictionary2Helper.findInJMdict(currentSimilarRelated);
																		
									if (foundSimilarRelatedEntryList != null && foundSimilarRelatedEntryList.size() > 0) {
																	
										for (Entry currentFoundEntry : foundSimilarRelatedEntryList) {
																						
											Integer foundEntryId = currentFoundEntry.getEntryId();
											
											// czy ta grupa byla juz sprawdzana
											if (alreadyCheckedGroupId.contains(foundEntryId) == true) {
												continue;
												
											} else {
												alreadyCheckedGroupId.add(foundEntryId);
												
											}
											
											List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(currentFoundEntry, false);
																															
											// grupujemy po tych samych tlumaczenia
											List<List<KanjiKanaPair>> groupByTheSameTranslateKanjiKanaListList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																									
											for (List<KanjiKanaPair> foundGroupByTheSameTranslateKanjiKanaList : groupByTheSameTranslateKanjiKanaListList) {
												
												KanjiKanaPair foundKanjiKanaPair = foundGroupByTheSameTranslateKanjiKanaList.get(0); // pierwszy element z grupy
	
												String foundKanjiKanaPairKanji = foundKanjiKanaPair.getKanji();
												String foundKanjiKanaPairKana = foundKanjiKanaPair.getKana();
																				
												List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, foundKanjiKanaPairKanji, foundKanjiKanaPairKana);
												
												if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
													
													//System.out.println(groupEntry);
													
													CommonWord commonWord = dictionary2Helper.convertKanjiKanaPairToCommonWord(csvId, foundKanjiKanaPair); 
													
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
				Boolean ignoreDictionaryFilledRawData = false;
				Boolean onlyNotFoundInJmedict = false;
				Boolean onlyAlreadyIgnoredButExistsInJmedict = false;
				Boolean randomWords = false;
				Boolean force = false;
				Boolean forceOnlyInOldFormat = false;
				
				boolean setWords = false;
				
				Set<Integer> wordsIdsSet = null;
				Set<Integer> groupsIdsSet = null;
				
				Integer minJmdictSenseListSize = null;
				Integer maxJmdictSenseListSize = null;
				
				//
				
				Options options = new Options();
				
				options.addOption("s", "size", true, "Size of find words");
				options.addOption("r", "random", false, "Random words");
				options.addOption("ijerd", "ignore-jmedict-empty-raw-data", false, "Ignore jmedict empty raw data");
				options.addOption("idfrd", "ignore-dictionary-filled-raw-data", false, "Ignore dictionary filled raw data");
				options.addOption("ontij", "only-not-found-in-jmedict", false, "Only not found in jmedict");
				options.addOption("oaibeij", "only-already-ignored-but-exists-in-jmedict", false, "Only already ignored but exists in jmedict");
				options.addOption("set", "set-words", true, "Set words");
				options.addOption("wid", "word-ids", true, "Word ids");
				options.addOption("gid", "group-ids", true, "Group ids");
				options.addOption("f", "force", false, "Force");
				options.addOption("foiof", "force-only-in-old-format", false, "Force only in old format");
				options.addOption("minjmdsls", "min-jmdict-sense-list-size", true, "Min jmdict sense list size");
				options.addOption("maxjmdsls", "max-jmdict-sense-list-size", true, "Max jmdict sense list size");
				
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
								
				if (setWords == false) {
					
					final String findWordsWithJmedictChangeFilename = "input/find-words-with-jmedict-change.csv";
					
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

					if (commandLine.hasOption("ignore-dictionary-filled-raw-data") == true) {
						ignoreDictionaryFilledRawData = true;
					}
					
					if (commandLine.hasOption("only-not-found-in-jmedict") == true) {
						onlyNotFoundInJmedict = true;
					}
					
					if (commandLine.hasOption("only-already-ignored-but-exists-in-jmedict") == true) {
						onlyAlreadyIgnoredButExistsInJmedict = true;
					}
					
					if (commandLine.hasOption("random") == true) {
						randomWords = true;
					}
										
					if (commandLine.hasOption("word-ids") == true) {
						
						wordsIdsSet = new HashSet<>();
						
						String wordsIdsString = commandLine.getOptionValue("word-ids");
						
						String[] wordsIdsStringSplited = wordsIdsString.split(",");
						
						for (String currentWordId : wordsIdsStringSplited) {
							
							currentWordId = currentWordId.trim();
							
							boolean containsColon = currentWordId.contains(":");
							
							if (containsColon == false) {
								wordsIdsSet.add(Integer.parseInt(currentWordId));
								
							} else {
								
								String[] currentWordIdSplited = currentWordId.split(":");
								
								Integer startWordIdRange = Integer.parseInt(currentWordIdSplited[0]);
								Integer stopWordIdRange = Integer.parseInt(currentWordIdSplited[1]);
								
								for (int id = startWordIdRange; id <= stopWordIdRange; ++id) {
									wordsIdsSet.add(id);
								}
							}
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

					if (commandLine.hasOption("force-only-in-old-format") == true) {
						forceOnlyInOldFormat = true;
					}
										
					if (commandLine.hasOption("min-jmdict-sense-list-size") == true) {
						minJmdictSenseListSize = Integer.parseInt(commandLine.getOptionValue("min-jmdict-sense-list-size"));
					}

					if (commandLine.hasOption("max-jmdict-sense-list-size") == true) {
						maxJmdictSenseListSize = Integer.parseInt(commandLine.getOptionValue("max-jmdict-sense-list-size"));
					}
	
					if (findWordsSize == null) {
						System.err.println("No size of find words");
						
						System.exit(1);
					}
					
					int wordsCounter = 0;
					
					//
										
					// lista wszystkich slow
					List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
	
					// pobranie cache ze slowami
					final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
					
					if (randomWords == true) {
						polishJapaneseEntriesList = new ArrayList<>(polishJapaneseEntriesList);
						
						Collections.shuffle(polishJapaneseEntriesList);
					}
					
					// lista identyfikatorow group (entry id): potrzebne dla zapisu w nowym formacie
					Set<Integer> entryIdSet = new LinkedHashSet<>();
					
					//
					
					class PolishJapaneseEntryAndGroupEntryListWrapper {
						
						PolishJapaneseEntry polishJapaneseEntry;
						
						List<Entry> entryList;
						
						String additionalInfo;
	
						public PolishJapaneseEntryAndGroupEntryListWrapper(PolishJapaneseEntry polishJapaneseEntry, List<Entry> entryList, String additionalInfo) {
							this.polishJapaneseEntry = polishJapaneseEntry;
							this.entryList = entryList;
							this.additionalInfo = additionalInfo;
						}
					}
					
					//
										
					List<PolishJapaneseEntryAndGroupEntryListWrapper> result = new ArrayList<PolishJapaneseEntryAndGroupEntryListWrapper>();
					
					for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
						
						if (wordsIdsSet != null && wordsIdsSet.contains(polishJapaneseEntry.getId()) == false) {
							continue;
						}
						
						/*
						if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
							continue;
						}
						*/
						
						if (onlyAlreadyIgnoredButExistsInJmedict == true && polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == false) {
							continue;
						}
						
						if (groupsIdsSet != null) {
							
							Integer polishJapaneseEntryGroupId = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
							
							if (polishJapaneseEntryGroupId != null) {																
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
						List<Entry> entryListForPolishJapaneseEntry = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, true);
												
						if (entryListForPolishJapaneseEntry != null && entryListForPolishJapaneseEntry.size() != 0) {
							
							List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
							
							// ignorojemy puste wpisy
							if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0 || jmedictRawDataList.contains("xxx") == true) && ignoreJmedictEmptyRawData == true) {							
								continue;
							}
							
							// ignorujemy wpisy w slowniku, ktore sa juz wypelnione
							if (jmedictRawDataList != null && jmedictRawDataList.size() > 0 && jmedictRawDataList.contains("xxx") == false && ignoreDictionaryFilledRawData == true) {
								continue;
							}
							
							//
							
							if (entryListForPolishJapaneseEntry.size() > 1 && force == true) {
								throw new RuntimeException("MultiGroup for: " + polishJapaneseEntry.getId() + " and force = true");
							}
														
							if (entryListForPolishJapaneseEntry.size() == 1 || force == true) { // grupa pojedyncza
								
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
								
								if (minJmdictSenseListSize != null && entryListForPolishJapaneseEntry.get(0).getSenseList().size() < minJmdictSenseListSize) {
									continue;
								}

								if (maxJmdictSenseListSize != null && entryListForPolishJapaneseEntry.get(0).getSenseList().size() > maxJmdictSenseListSize) {
									continue;
								}

								List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entryListForPolishJapaneseEntry.get(0), false);
								
								List<List<KanjiKanaPair>> groupByTheSameTranslateList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																								
								//
								
								class PolishJapaneseEntryAndGroupEntry {
									
									PolishJapaneseEntry polishJapaneseEntry;
									
									KanjiKanaPair kanjiKanaPair;

									public PolishJapaneseEntryAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry, KanjiKanaPair kanjiKanaPair) {
										this.polishJapaneseEntry = polishJapaneseEntry;
										this.kanjiKanaPair = kanjiKanaPair;
									}
								}
								
								//
								
								List<PolishJapaneseEntryAndGroupEntry> allPolishJapaneseEntryListForGroupEntry = new ArrayList<>();
								
								boolean isDifferent = false;
								
								//
								
								for (List<KanjiKanaPair> theSameTranslateKanjiKanaList : groupByTheSameTranslateList) {
																																				
									for (KanjiKanaPair kanjiKanaPair : theSameTranslateKanjiKanaList) {
										
										String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
										String kanjiKanaPairKana = kanjiKanaPair.getKana();

										PolishJapaneseEntry findPolishJapaneseEntry = 
												Helper.findPolishJapaneseEntryWithEdictDuplicate(polishJapaneseEntry, cachePolishJapaneseEntryList, 
														kanjiKanaPairKanji, kanjiKanaPairKana);
										
										if (findPolishJapaneseEntry != null) {
											allPolishJapaneseEntryListForGroupEntry.add(new PolishJapaneseEntryAndGroupEntry(findPolishJapaneseEntry, kanjiKanaPair));
										}
									}
									
									for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
										
										PolishJapaneseEntry findPolishJapaneseEntry = polishJapaneseEntryAndGroupEntry.polishJapaneseEntry;
										KanjiKanaPair kanjiKanaPair = polishJapaneseEntryAndGroupEntry.kanjiKanaPair;
										
										//
																					
										jmedictRawDataList = findPolishJapaneseEntry.getJmedictRawDataList();
										
										// ignorojemy puste wpisy
										if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0 || jmedictRawDataList.contains("xxx") == true) && ignoreJmedictEmptyRawData == true) {							
											continue;
										}											
																				
										List<String> newJmedictRawDataList = new ArrayList<String>();											
										
										dictionary2Helper.fillJmedictRawDataInOldFormat(entryListForPolishJapaneseEntry.get(0), kanjiKanaPair, newJmedictRawDataList);
										
										if (jmedictRawDataList.equals(newJmedictRawDataList) == false && findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) { // jest roznica i to slowo nie ma swojego zrodla w nowym slowniku
											isDifferent = true;
											
										} else if (forceOnlyInOldFormat == true && findPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
											isDifferent = true;
										}
									}									
								}
								
								if ((isDifferent == true || force == true) && onlyNotFoundInJmedict == false) {
									
									// liczenie roznych tlumaczen
									//wordsCounter += groupByTheSameTranslateGroupEntryList.size();
									
									// liczba slow wybrana przez uzytkownika
									wordsCounter++;
									
                                    for (Entry currentEntry : entryListForPolishJapaneseEntry) {
                                        entryIdSet.add(currentEntry.getEntryId());
                                    }
									
									boolean currentPolishJapaneseEntryInAllPolishJapaneseEntryListForGroupEntry = false;
																		
									for (PolishJapaneseEntryAndGroupEntry polishJapaneseEntryAndGroupEntry : allPolishJapaneseEntryListForGroupEntry) {
										
										PolishJapaneseEntry findPolishJapaneseEntry = polishJapaneseEntryAndGroupEntry.polishJapaneseEntry;
										
										if (currentPolishJapaneseEntryInAllPolishJapaneseEntryListForGroupEntry == false && polishJapaneseEntry.getId() == findPolishJapaneseEntry.getId()) {
											currentPolishJapaneseEntryInAllPolishJapaneseEntryListForGroupEntry = true;
										}

										result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(findPolishJapaneseEntry, entryListForPolishJapaneseEntry, null));
									}
									
									// oznacza to, ze jest pewien problem z EDICT_DUPLICATE (brakuje) i slowo, z ktorego wszystko zaczelo sie nie zostalo dodane do listy, dodajemy je
									if (currentPolishJapaneseEntryInAllPolishJapaneseEntryListForGroupEntry == false) { 
										result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, null, "!!! EDICT_DUPLICATE !!!"));
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
								
								if (onlyNotFoundInJmedict == false) {
								
									// dodajemy do manualnego sprawdzenia
									result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, entryListForPolishJapaneseEntry, null));
									
									wordsCounter++;
									
                                    for (Entry currentEntry : entryListForPolishJapaneseEntry) {
                                        entryIdSet.add(currentEntry.getEntryId());
                                    }
								}
							}
							
						} else { // nie znaleziono Entry
														
							List<String> jmedictRawDataList = polishJapaneseEntry.getJmedictRawDataList();
							
							// ignorojemy puste wpisy
							if ((jmedictRawDataList == null || jmedictRawDataList.size() == 0 || jmedictRawDataList.contains("xxx") == true) && ignoreJmedictEmptyRawData == true && force == false) {							
								continue;
							}
							
							// ignorujemy wpisy w slowniku, ktore sa juz wypelnione
							if (jmedictRawDataList != null && jmedictRawDataList.size() > 0 && jmedictRawDataList.contains("xxx") == false && ignoreDictionaryFilledRawData == true) {
								continue;
							}
							
							boolean ignoreNoJmedict = polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT);
							
							if (ignoreNoJmedict == false || force == true) {
								result.add(new PolishJapaneseEntryAndGroupEntryListWrapper(polishJapaneseEntry, null, "IGNORE_NO_JMEDICT ???"));
								
								wordsCounter++;
								
								// to jest chyba niepotrzebne
								if (entryListForPolishJapaneseEntry != null) {
                                    for (Entry currentEntry : entryListForPolishJapaneseEntry) {
                                        entryIdSet.add(currentEntry.getEntryId());
                                    }
                                }
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
							
							System.out.println(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getId());

							idPolishJapaneseEntryAndGroupEntryListWrapperMap.put(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getId(), polishJapaneseEntryAndGroupEntryListWrapper);
							
							resultAsPolishJapaneseEntryList.add(polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry);
						}						
					}
					
					//
									
					ICustomAdditionalCsvWriter customAdditionalCsvWriter = new ICustomAdditionalCsvWriter() {
						
						@Override
						public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
							
							try {
							
								PolishJapaneseEntryAndGroupEntryListWrapper polishJapaneseEntryAndGroupEntryListWrapper = idPolishJapaneseEntryAndGroupEntryListWrapperMap.get(polishJapaneseEntry.getId());
								
								if (polishJapaneseEntryAndGroupEntryListWrapper.additionalInfo != null) {
									csvWriter.write(polishJapaneseEntryAndGroupEntryListWrapper.additionalInfo);
								}
								
								if (polishJapaneseEntryAndGroupEntryListWrapper.entryList != null) {
									
									if (polishJapaneseEntryAndGroupEntryListWrapper.entryList.size() == 1) {
										csvWriter.write("SINGLEGROUP");
										
									} else {
										csvWriter.write("MULTIGROUP");
									}
									
									for (Entry entry : polishJapaneseEntryAndGroupEntryListWrapper.entryList) {
										
										List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
										
										KanjiKanaPair kanjiKanaPair = Dictionary2HelperCommon.findKanjiKanaPair(kanjiKanaPairList, 
												polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getKanji(), polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getKana());
										
										List<String> newTranslatesInOldFormat = dictionary2Helper.generateTranslatesInOldFormat(kanjiKanaPair, null);
										List<String> additionalInfoInOldFormat = dictionary2Helper.generateAdditionalInfoInOldFormat(kanjiKanaPair, polishJapaneseEntryAndGroupEntryListWrapper.polishJapaneseEntry.getWordType());
										
										//
										
										csvWriter.write(Helper.convertListToString(newTranslatesInOldFormat));
										csvWriter.write(Helper.convertListToString(additionalInfoInOldFormat));
			
										//
										
										List<String> newJmedictRawDataList = new ArrayList<String>();
										
										dictionary2Helper.fillJmedictRawDataInOldFormat(entry, kanjiKanaPair, newJmedictRawDataList);
																			
										csvWriter.write(Helper.convertListToString(newJmedictRawDataList));
									}
								}
								
							} catch (Exception e) {
								throw new IOException(e);
							}
						}
						
						@Override
						public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry)
								throws IOException {								
							throw new UnsupportedOperationException();								
						}
					};
					
					// zapis (stary format)
					CsvReaderWriter.generateCsv(new String[] { findWordsWithJmedictChangeFilename }, resultAsPolishJapaneseEntryList, true, true, false, true, customAdditionalCsvWriter);

					//
					
					// zapis w nowym formacie
					{
						// lista wynikowa
						List<Entry> resultDictionary2EntryList = new ArrayList<>();
						
						// dodatkowe informacje
						EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
						
						// chodzimy po wszystkich elementach
						for (Integer currentEntryId : entryIdSet) {
							
							Entry jmdictEntry = dictionary2Helper.getJMdictEntry(currentEntryId);
							
							if (jmdictEntry == null) { // nie znaleziono
								throw new RuntimeException(); // to nigdy nie powinno zdarzyc sie
							}
							
							Entry entryFromPolishDictionary = dictionary2Helper.getEntryFromPolishDictionary(jmdictEntry.getEntryId());
							
							if (entryFromPolishDictionary != null) { // taki wpis juz jest w polskim slowniku
								
								System.out.println("[Error] Entry already exists in polish dictionary: " + currentEntryId);
								
								continue;					
							}
							
							// uzupelnienie o puste polskie tlumaczenie
							dictionary2Helper.createEmptyPolishSense(jmdictEntry);
							
							// pobranie ze starego slownika interesujacych danych (np. romaji)
							dictionary2Helper.fillDataFromOldPolishJapaneseDictionaryForWordGenerating(jmdictEntry, entryAdditionalData);

							// dodajemy do listy
							resultDictionary2EntryList.add(jmdictEntry);
						}

						Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
						
							
						saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
						saveEntryListAsHumanCsvConfig.markRomaji = true;
						saveEntryListAsHumanCsvConfig.shiftCells = true;
						saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
												
						// zapisanie slow w nowym formacie
						dictionary2Helper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", resultDictionary2EntryList, entryAdditionalData);
					}					
					
				} else { // ustawienie slow
					
					final String findWordsWithJmedictChangeFilename = commandLine.getOptionValue("set");
					
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
					CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv", "input/word03-wynik.csv", "input/word04-wynik.csv" }, result, true, true, false, true, null);
				}
				
				break;
			}
			
			case FIND_WORDS_NO_EXIST_IN_JMEDICT: {
				
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
				
				Integer findWordsSize = null;
				Boolean randomWords = false;
				Boolean respectIgnoreNoJmedict = false;
				
				//
				
				Options options = new Options();
				
				options.addOption("s", "size", true, "Size of find words");
				options.addOption("r", "random", false, "Random words");
				options.addOption("rinj", "respect-ignore-no-jmedict", false, "Ignore jmedict empty raw data");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_WORDS_NO_EXIST_IN_JMEDICT.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FIND_WORDS_NO_EXIST_IN_JMEDICT.getOperation(), options );
					
					System.exit(1);
				}			
				
				if (commandLine.hasOption("random") == true) {
					randomWords = true;
				}
				
				if (commandLine.hasOption("size") == true) {
					findWordsSize = Integer.parseInt(commandLine.getOptionValue("size"));
				}
				
				if (commandLine.hasOption("respect-ignore-no-jmedict") == true) {
					respectIgnoreNoJmedict = true;
				}

				//
				
				// lista wszystkich slow
				List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
				
				if (randomWords == true) {
					polishJapaneseEntriesList = new ArrayList<>(polishJapaneseEntriesList);
					
					Collections.shuffle(polishJapaneseEntriesList);
				}

				List<PolishJapaneseEntry> result = new ArrayList<>();
								
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
					
					if (respectIgnoreNoJmedict == true && polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == true) {
						continue;
					}
					
					// szukanie slow
					List<Entry> entryList = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, true);
																
					if (entryList == null || entryList.size() == 0) {						
						result.add(polishJapaneseEntry);
					}
					
					//
					
					if (findWordsSize != null && result.size() >= findWordsSize) {
						break;
					}
				}
				
				CsvReaderWriter.generateCsv(new String[] { "input/find-words-no-exist-in-jmedict.csv" }, result, true, true, false, true, null);
				
				break;
			}
			
			case FIND_WORDS_WITH_JMEDICT_GROUP_CHANGE: {
												
				// lista wszystkich slow
				List<PolishJapaneseEntry> polishJapaneseEntriesList = dictionary2Helper.getOldPolishJapaneseEntriesList();
				
				List<Integer> result = new ArrayList<>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
					
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
					
					Integer polishJapaneseEntryGroupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
					
					if (polishJapaneseEntryGroupIdFromJmedictRawDataList == null) {
						continue;
					}
										
					// szukanie slow
					List<Entry> entryListForPolishJapaneseEntry = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, true);
					
					if (entryListForPolishJapaneseEntry.size() == 1) {
						
						if (entryListForPolishJapaneseEntry.get(0).getEntryId().intValue() != polishJapaneseEntryGroupIdFromJmedictRawDataList.intValue()) {						
							if (result.contains(polishJapaneseEntryGroupIdFromJmedictRawDataList) == false) {
								result.add(polishJapaneseEntryGroupIdFromJmedictRawDataList);
							}
						}

					} else if (entryListForPolishJapaneseEntry.size() > 1) {
						throw new RuntimeException("MultiGroup for: " + polishJapaneseEntry.getId());
					}
				}
				
				if (result.size() > 0) {
					
					System.out.print("./word-generator.sh find-words-with-jmedict-change -s 88888 -f -gid ");
					
					for (int i = 0; i < result.size(); ++i) {
						
						if (i != 0) {
							System.out.print(",");
						}
						
						System.out.print(result.get(i));
					}
					
					System.out.println();
				}				
				
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
					CsvReaderWriter.generateCsv(new String[] { "input/word01-wynik.csv", "input/word02-wynik.csv", "input/word03-wynik.csv", "input/word04-wynik.csv" }, result, true, true, false, true, null);
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
							
							currentKanjiId = currentKanjiId.trim();
							
							boolean containsColon = currentKanjiId.contains(":");
							
							if (containsColon == false) {
								kanjisIdsSet.add(Integer.parseInt(currentKanjiId));
								
							} else {
								
								String[] currentKanjiIdSplited = currentKanjiId.split(":");
								
								Integer startKanjiIdRange = Integer.parseInt(currentKanjiIdSplited[0]);
								Integer stopKanjiIdRange = Integer.parseInt(currentKanjiIdSplited[1]);
								
								for (int id = startKanjiIdRange; id <= stopKanjiIdRange; ++id) {
									kanjisIdsSet.add(id);
								}
							}
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
			
			case SHOW_EMPTY_COMMON_WORDS: {
				
				// czytanie common'owego pliku
				Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
				
				// przegladanie listy common'owych plikow i sprawdzanie, czy nie jest juz dodany
				Collection<CommonWord> commonWordValues = commonWordMap.values();
								
				Iterator<CommonWord> commonWordValuesIterator = commonWordValues.iterator();
								
				while (commonWordValuesIterator.hasNext() == true) {
					
					CommonWord currentCommonWord = commonWordValuesIterator.next();
					
					if (currentCommonWord.isDone() == false) {

						String commonKanji = currentCommonWord.getKanji();
						
						if (commonKanji.equals("-") == true) {
							commonKanji = null;
						}
						
						String commonKana = currentCommonWord.getKana();
						
						List<Entry> entryList = dictionary2Helper.findEntryListByKanjiAndKana(commonKanji, commonKana);
						
						if (entryList == null || entryList.size() == 0) {
							System.out.println(currentCommonWord.getId());
						}						
					}
				}
				
				break;
			}
			
			case FILTER_WORD_LIST: {
				
				CommandLineParser commandLineParser = new DefaultParser();
				
				//
								
				Boolean onlyKanji = null;
				Boolean onlyKana = null;
				Boolean onlyJapanese = null;
				
				String fileName = null;
				
				//
				
				Options options = new Options();
				
				options.addOption("okanji", "only-kanji", false, "Only kanji");
				options.addOption("okana", "only-kana", false, "Only kana");
				options.addOption("ojap", "only-japanese-words", false, "Only japanese words");
				
				options.addOption("f", "file", true, "Word list file name");
				
				options.addOption("h", "help", false, "Help");
				
				//
				
				CommandLine commandLine = null;
				
				try {
					commandLine = commandLineParser.parse(options, args);
					
				} catch (UnrecognizedOptionException e) {
					
					System.out.println(e.getMessage() + "\n");
					
					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FILTER_WORD_LIST.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("help") == true) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FILTER_WORD_LIST.getOperation(), options );
					
					System.exit(1);
				}
				
				if (commandLine.hasOption("only-kanji") == true) {
					onlyKanji = true;
				}

				if (commandLine.hasOption("only-kana") == true) {
					onlyKana = true;
				}

				if (commandLine.hasOption("only-japanese-words") == true) {
					onlyJapanese = true;
				}
				
				if (commandLine.hasOption("file") == true) {					
					fileName = commandLine.getOptionValue("file");					
				}
				
				if (fileName == null || (onlyKanji == null && onlyKana == null && onlyJapanese == null)) {

					HelpFormatter formatter = new HelpFormatter();
					
					formatter.printHelp( Operation.FILTER_WORD_LIST.getOperation(), options );
					
					System.exit(1);
				}
				
				List<String> fileList = readFile(fileName);
				
				for (String currentWord : fileList) {
					
					boolean isAdd = false;
					
					if (onlyJapanese != null && onlyJapanese.booleanValue() == true && Utils.isAllJapaneseChars(currentWord) == true) {
						isAdd = true;
					}
					
					if (onlyKanji != null && onlyKanji.booleanValue() == true && Utils.isAllKanjiChars(currentWord) == true) {
						isAdd = true;
					}
					
					if (onlyKana != null && onlyKana.booleanValue() == true && Utils.isAllKanaChars(currentWord) == true) {
						isAdd = true;
					}
					
					if (isAdd == true) {
						System.out.println(currentWord);
					}					
				}				
				
				break;
			}
			
			case FIND_PARTIAL_TRANSLATE_WORDS: {
				
				// wczytanie slownika
				List<PolishJapaneseEntry> polishJapaneseEntries = wordGeneratorHelper.getPolishJapaneseEntriesList();

				// cache'owanie slownika
				final Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
								
				class PolishJapaneseEntryAndKanjiKanaPairWrapper {
					
					PolishJapaneseEntry polishJapaneseEntry;
					
					KanjiKanaPair kanjiKanaPair;
					
					public PolishJapaneseEntryAndKanjiKanaPairWrapper(PolishJapaneseEntry polishJapaneseEntry, KanjiKanaPair kanjiKanaPair) {
						this.polishJapaneseEntry = polishJapaneseEntry;
						this.kanjiKanaPair = kanjiKanaPair;
					}
				}
				
				//

				final Map<Integer, PolishJapaneseEntryAndKanjiKanaPairWrapper> result = new TreeMap<>();			
				
				BEFORE_MAIN_FOR:
				for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
										
					DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
						continue;
					}
										
					// szukanie slow
					List<Entry> entryList = dictionary2Helper.findEntryListInJmdict(polishJapaneseEntry, false);
										
					if ((entryList == null | entryList.size() == 0) && checkForPartialTranslates(polishJapaneseEntry) == true && result.containsKey(polishJapaneseEntry.getId()) == false) {
												
						result.put(polishJapaneseEntry.getId(), new PolishJapaneseEntryAndKanjiKanaPairWrapper(polishJapaneseEntry, null));
						
						continue BEFORE_MAIN_FOR;			
					}
					
					if (entryList != null && entryList.size() > 0) {
						
						if (entryList.size() > 1) {
							throw new RuntimeException(); // to raczej nie powinno zdarzyc sie
						}
						
						Entry entry = entryList.get(0);
						
						List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);

						// grupujemy po tych samych tlumaczeniach
						List<List<KanjiKanaPair>> groupByTheSameTranslateKanjiKanaList = dictionary2Helper.groupByTheSameTranslate(kanjiKanaPairList);
																
						// chodzimy po tych samych tlumaczeniach
						for (List<KanjiKanaPair> theSameTranslateKanjiKanaList : groupByTheSameTranslateKanjiKanaList) {
								
							KanjiKanaPair kanjiKanaPair = theSameTranslateKanjiKanaList.get(0); // pierwszy element z grupy

							String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
							String kanjiKanaPairKana = kanjiKanaPair.getKana();
															
							PolishJapaneseEntry polishJapaneseEntryInGroup = Helper.findPolishJapaneseEntryWithEdictDuplicate(
									polishJapaneseEntry, cachePolishJapaneseEntryList, kanjiKanaPairKanji, kanjiKanaPairKana);

							if (polishJapaneseEntryInGroup == null) {								
								continue;
							}
							
							if (result.containsKey(polishJapaneseEntryInGroup.getId()) == false && checkForPartialTranslates(polishJapaneseEntryInGroup) == true) {								
								
								result.put(polishJapaneseEntryInGroup.getId(), new PolishJapaneseEntryAndKanjiKanaPairWrapper(polishJapaneseEntryInGroup, kanjiKanaPair));
																
								continue BEFORE_MAIN_FOR;								
							}							
						}
					}
				}
				
				// zapis slownika
				List<PolishJapaneseEntry> polishJapaneseEntryList = new ArrayList<>();
				
				for (PolishJapaneseEntryAndKanjiKanaPairWrapper polishJapaneseEntryAndGroupEntryWrapper : result.values()) {
					polishJapaneseEntryList.add(polishJapaneseEntryAndGroupEntryWrapper.polishJapaneseEntry);
				}
				
				CsvReaderWriter.generateCsv(new String[] { "input/word-partial-translates.csv" }, polishJapaneseEntryList, true, true, false, true,
					new ICustomAdditionalCsvWriter() {
						
						@Override
						public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException {
							
							PolishJapaneseEntryAndKanjiKanaPairWrapper polishJapaneseEntryAndGroupEntryWrapper = result.get(polishJapaneseEntry.getId());
							
							if (polishJapaneseEntryAndGroupEntryWrapper.kanjiKanaPair == null) {
								return;
							}
							
							try {
								List<String> generateTranslatesInOldFormat = dictionary2Helper.generateTranslatesInOldFormat(polishJapaneseEntryAndGroupEntryWrapper.kanjiKanaPair, null);
															
								csvWriter.write(Utils.convertListToString(generateTranslatesInOldFormat));
								
							} catch (Exception e) {
								throw new RuntimeException("" + polishJapaneseEntry.getId());
							}
						}
						
						@Override
						public void write(CsvWriter csvWriter, KanjiEntryForDictionary kanjiEntry)
								throws IOException {								
							throw new UnsupportedOperationException();								
						}
				});
				
				break;
			}
			
			case SHOW_ALL_MULTIPLE_KANJI_KANA: {
								
				Map<String, List<Entry>> kanjiAndEntryListMap = new TreeMap<>();
				Map<String, List<Entry>> kanaAndEntryListMap = new TreeMap<>();
								
				// pobieramy wszystkie wpisy ze slownika JMdict
				List<Entry> entryList = dictionary2Helper.getJMdict().getEntryList();

				// dla kazdego wpisu
				for (Entry entry : entryList) {
					
					// pobieramy liste kanji
					List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
					
					for (KanjiInfo kanjiInfo : kanjiInfoList) {
						
						// pobieramy kanji
						String kanji = kanjiInfo.getKanji();
						
						List<Entry> entryListForKanji = kanjiAndEntryListMap.get(kanji);
						
						if (entryListForKanji == null) {
							entryListForKanji = new ArrayList<>();
							
							kanjiAndEntryListMap.put(kanji, entryListForKanji);
						}
						
						if (entryListForKanji.contains(entry) == false) {
							entryListForKanji.add(entry);
						}						
					}
					
					// pobieramy liste czytan
					List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
					
					for (ReadingInfo readingInfo : readingInfoList) {
						
						// pobieramy kana
						String kana = readingInfo.getKana().getValue();
						
						List<Entry> entryListForKana = kanaAndEntryListMap.get(kana);
						
						if (entryListForKana == null) {
							entryListForKana = new ArrayList<>();
							
							kanaAndEntryListMap.put(kana, entryListForKana);
						}
						
						if (entryListForKana.contains(entry) == false) {
							entryListForKana.add(entry);
						}						
					}
				}
				
				// pobieranie listy pogrupowanych kanji	
				Set<java.util.Map.Entry<String, List<Entry>>> kanjiKanaAndEntryListMapEntrySet = kanjiAndEntryListMap.entrySet();
				
				for (java.util.Map.Entry<String, List<Entry>> kanjiKanaAndEntryListMapEntrySetEntry : kanjiKanaAndEntryListMapEntrySet) {
					
					// mamy kanji, ktore wystepuje w kilku grupach
					if (kanjiKanaAndEntryListMapEntrySetEntry.getValue().size() > 1) {
						
						for (Entry entry : kanjiKanaAndEntryListMapEntrySetEntry.getValue()) {
							
							if (dictionary2Helper.getEntryFromPolishDictionary(entry.getEntryId()) == null) {
								System.out.println("ENTRY_ID: " + entry.getEntryId());
								System.out.println("KANJI: " + kanjiKanaAndEntryListMapEntrySetEntry.getKey());								
							}
						}
					}
				}				
				
				System.out.println("--------------");
				
				Set<java.util.Map.Entry<String, List<Entry>>> kanaAndEntryListMapEntrySet = kanaAndEntryListMap.entrySet();
				
				for (java.util.Map.Entry<String, List<Entry>> kanaAndEntryListMapEntrySetEntry : kanaAndEntryListMapEntrySet) {
					
					// mamy kana, ktore wystepuje w kilku grupach
					if (kanaAndEntryListMapEntrySetEntry.getValue().size() > 1) {
						
						for (Entry entry : kanaAndEntryListMapEntrySetEntry.getValue()) {
							
							if (dictionary2Helper.getEntryFromPolishDictionary(entry.getEntryId()) == null) {
								System.out.println("ENTRY_ID: " + entry.getEntryId());
								System.out.println("KANA: " + kanaAndEntryListMapEntrySetEntry.getKey());
							}
						}
					}
				}				
				
				
				break;
			}
			
			case SHOW_ALL_MISSING_WORDS2: {
				
				// wczytywanie pomocnika slownikowego
				Dictionary2Helper dictionaryHelper = Dictionary2Helper.init(wordGeneratorHelper);
				
				// pobieramy wszystkie wpisy ze slownika JMdict
				List<Entry> entryList = dictionaryHelper.getJMdict().getEntryList();

				// lista wynikowa
				List<Entry> resultDictionary2EntryList = new ArrayList<>();
				
				// dodatkowe informacje
				EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
				
				for (Entry entry : entryList) {
					
					// pobieramy polski wpis
					Entry entryFromPolishDictionary = dictionaryHelper.getEntryFromPolishDictionary(entry.getEntryId());
					
					// tego slowka nie ma, dodajemy
					if (entryFromPolishDictionary == null) {
						
						// uzupelnienie o puste polskie tlumaczenie
						dictionaryHelper.createEmptyPolishSense(entry);
						
						// pobranie ze starego slownika interesujacych danych (np. romaji)
						dictionaryHelper.fillDataFromOldPolishJapaneseDictionaryForWordGenerating(entry, entryAdditionalData);
						
						// dodajemy do listy
						resultDictionary2EntryList.add(entry);
					}
				}
				
				// zapisujemy
				Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
								
				saveEntryListAsHumanCsvConfig.addOldPolishTranslates = true;
				saveEntryListAsHumanCsvConfig.markRomaji = true;
				saveEntryListAsHumanCsvConfig.shiftCells = true;
				saveEntryListAsHumanCsvConfig.shiftCellsGenerateIds = true;
										
				// zapisanie slow w nowym formacie
				dictionaryHelper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "input/word2-new.csv", resultDictionary2EntryList, entryAdditionalData);				
				
				break;
			}
			
			case COMPARE_TWO_JMDICTS: {
				
				if (args.length != 3) {					
					System.err.println("Niepoprawna liczba argumentów");
					
					return;
				}
				
				// pliki do porownania
				File oldJmdictFile = new File(args[1]);
				File newJmdictFile = new File(args[2]);

				// przygotowanie kontekstu jaxb
				JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);
				
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				
				// wczytanie plikow
				JMdict oldJmdict = (JMdict) jaxbUnmarshaller.unmarshal(oldJmdictFile);
				JMdict newJmdict = (JMdict) jaxbUnmarshaller.unmarshal(newJmdictFile);

				// cahceowanie
				Map<Integer, JMdict.Entry> oldJmdictCache = cacheJmdict(oldJmdict);
				Map<Integer, JMdict.Entry> newJmdictCache = cacheJmdict(newJmdict);
				
				// chodzimy po starych i sprawdzamy, czy zostaly usuniete w nowym
				for (Entry oldEntry : oldJmdictCache.values()) {					
					
					// pobieramy nowy
					Entry newEntry = newJmdictCache.get(oldEntry.getEntryId());
					
					// w nowym tego slowa juz nie ma
					if (newEntry == null) {
						System.out.println("Usunięto: " + oldEntry.getEntryId());
					}					
				}

				// chodzimy po starych wpisach i sprawdzamy, czy zmienily sie w nowym
				for (Entry oldEntry : oldJmdictCache.values()) {					
					
					// pobieramy nowy
					Entry newEntry = newJmdictCache.get(oldEntry.getEntryId());
					
					// w nowym tego slowa juz nie ma
					if (newEntry == null) {						
						continue;
					}
					
					String oldEntryXml = marshall(jaxbMarshaller, oldEntry);
					String newEntryXml = marshall(jaxbMarshaller, newEntry);
					
					if (oldEntryXml.equals(newEntryXml) == false) {
						System.out.println("Zmieniono: " + oldEntry.getEntryId());
					}
				}
				
				// chodzenie po nowych wpisach i sprawdzanie, czy nie zostaly dodane
				for (Entry newEntry : newJmdictCache.values()) {
					
					// pobieramy stary
					Entry oldEntry = oldJmdictCache.get(newEntry.getEntryId());
					
					// w nowym tego slowa juz nie ma
					if (oldEntry == null) {
						System.out.println("Dodano: " + newEntry.getEntryId());
						
						continue;
					}					
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
	
	private static boolean checkForPartialTranslates(PolishJapaneseEntry polishJapaneseEntry) {
		
		List<String> translates = polishJapaneseEntry.getTranslates();
		
		// do wyboru
		/*
		if (translates.contains("???") == false) {
			return false;
		}
		*/
		
		/*
		if (translates.size() != 1 || translates.get(0).equals("???") == false) {
			return false;
		}
		*/
		
		for (String currentTranslate : translates) {
			if (currentTranslate.contains("??") == true) {
				return true;
			}
		}
		
		return false;
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
	
	/*
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
	*/
		
	private static String getKeyForAlreadyAddedEntryKanjiKanaPairSet(Entry entry, KanjiKanaPair kanjiKanaPair) {
		
		String key = entry.getEntryId() + "." + "." + kanjiKanaPair.getKanji() + "." + kanjiKanaPair.getKana();
		
		return key;
	}
	
	private static String getKeyForNewWordListAndGroupEntry(PolishJapaneseEntry polishJapaneseEntry) {
		
		String key = polishJapaneseEntry.getId() + "." + polishJapaneseEntry.getDictionaryEntryTypeList().toString() + "." + 
				polishJapaneseEntry.getKanji() + "." + polishJapaneseEntry.getKana() + "." + polishJapaneseEntry.getRomaji() +
				polishJapaneseEntry.getTranslates().toString();
		
		return key;		
	}
	
	private static boolean searchInJishoForAdditionalWords(WordGeneratorHelper wordGeneratorHelper, Dictionary2Helper dictionary2Helper, LinkedHashSet<String> newAdditionalWordToCheckWordList, 
			JishoOrgConnector jishoOrgConnector, Map<String, Boolean> jishoOrgConnectorWordCheckCache, 
			String messageTemplate, String word) throws Exception {
		
		Boolean result = jishoOrgConnectorWordCheckCache.get(word);
		
		if (result != null) {
			return result.booleanValue();
		}
		
		//
				
		Map<Integer, CommonWord> commonWordMap = wordGeneratorHelper.getCommonWordMap();
		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = wordGeneratorHelper.getPolishJapaneseEntriesCache();
		
		//
		
		System.out.println(messageTemplate);
		
		//
		
		List<JapaneseWord> japaneseWords = jishoOrgConnector.getJapaneseWords(word);
		
		for (JapaneseWord japaneseWord : japaneseWords) {
			
			List<Entry> foundEntryList = dictionary2Helper.findEntryListByKanjiAndKana(japaneseWord.kanji, japaneseWord.kana);
						
			if (foundEntryList != null) {
								
				boolean isAdd = true;
				
				BEFORE_FOR:
				for (Entry entry : foundEntryList) {
					
					List<KanjiKanaPair> kanjiKanaPairList = Dictionary2Helper.getKanjiKanaPairListStatic(entry, false);
					
					for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
						
						if (	Helper.findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana()) != null ||
								existsInCommonWords(commonWordMap, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana(), false) == true) {
																		
							isAdd = false;
							
							break BEFORE_FOR;
						}
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
	
	private static Map<Integer, JMdict.Entry> cacheJmdict(JMdict jmdict) {
		Map<Integer, JMdict.Entry> cacheMap = new TreeMap<Integer, JMdict.Entry>();
		
		for (Entry entry : jmdict.getEntryList()) {
			cacheMap.put(entry.getEntryId(), entry);			
		}
		
		return cacheMap;
	}
	
	private static String marshall(Marshaller jaxbMarshaller, Entry entry) throws JAXBException {
		StringWriter sw = new StringWriter();
		
		JAXBElement<Entry> jaxbElement = new JAXBElement<Entry>(new QName("", "entry"), Entry.class, entry);
		
		jaxbMarshaller.marshal(jaxbElement, sw);
		
		return sw.toString();
	}
}
