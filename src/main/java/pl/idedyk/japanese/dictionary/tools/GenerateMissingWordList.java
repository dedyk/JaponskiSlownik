package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.lucene.LuceneAnalyzer;

public class GenerateMissingWordList {

	public static void main(String[] args) throws Exception {
		
		String fileName = args[0];
		
		System.out.println("Wczytywanie brakujących słów...");
		
		List<String> missingWords = readMissingFile(fileName);
		
		System.out.println("Wczytywanie slownika...");

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);

		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		JMEDictEntityMapper jmEDictEntityMapper = new JMEDictEntityMapper();

		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		System.out.println("Indeksowanie...");

		// tworzenie indeksu lucene
		Directory index = new RAMDirectory();

		// tworzenie analizatora lucene
		LuceneAnalyzer analyzer = new LuceneAnalyzer(Version.LUCENE_47);

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE);

		IndexWriter indexWriter = new IndexWriter(index, indexWriterConfig);

		for (Group group : jmeNewDictionary.getGroupList()) {

			List<GroupEntry> groupEntryList = group.getGroupEntryList();

			for (GroupEntry groupEntry : groupEntryList) {

				Document document = new Document();

				Set<String> wordTypeList = groupEntry.getWordTypeList();

				String kanji = groupEntry.getKanji();
				List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

				String kana = groupEntry.getKana();
				List<String> kanaInfoList = groupEntry.getKanaInfoList();

				String romaji = groupEntry.getRomaji();

				List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
				
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
						
						translate.append(jmEDictEntityMapper.getDesc(miscInfoList.get(idx)));
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
				
				addFieldToDocument(document, "wordTypeList", wordTypeList);

				addFieldToDocument(document, "kanji", kanji);
				addFieldToDocument(document, "kanjiInfoList", kanjiInfoList);

				addFieldToDocument(document, "kana", kana);
				addFieldToDocument(document, "kanaInfoList", kanaInfoList);

				addFieldToDocument(document, "romaji", romaji);
				
				addFieldToDocument(document, "translateList", translateList2);
				//addFieldToDocument(document, "additionalInfoList", additionalInfoList);

				indexWriter.addDocument(document);
			}			
		}

		indexWriter.close();
		
		List<PolishJapaneseEntry> foundWordList = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> alreadyAddedWordList = new ArrayList<PolishJapaneseEntry>();
		
		List<String> foundWordSearchList = new ArrayList<String>();		
		
		List<PolishJapaneseEntry> notFoundWordList = new ArrayList<PolishJapaneseEntry>();
		List<String> notFoundWordSearchList = new ArrayList<String>();
		
		// stworzenie wyszukiwacza
		IndexReader reader = DirectoryReader.open(index);

		IndexSearcher searcher = new IndexSearcher(reader);

		int counter = 0;
		
		Set<Integer> alreadyFoundDocument = new TreeSet<Integer>();
		
		for (String currentMissingWord : missingWords) {
			
			if (currentMissingWord.equals("") == true) {
				continue;
			}
			
			counter++;

			Query query = createQuery(currentMissingWord);

			ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;
			
			if (scoreDocs.length > 0) {
				
				foundWordSearchList.add(currentMissingWord);
				
				for (ScoreDoc scoreDoc : scoreDocs) {
					
					if (alreadyFoundDocument.contains(scoreDoc.doc) == true) {
						continue;
						
					} else {
						alreadyFoundDocument.add(scoreDoc.doc);
						
					}

					Document foundDocument = searcher.doc(scoreDoc.doc);

					GroupEntry groupEntry = createGroupEntry(foundDocument);

					Set<String> wordTypeList = groupEntry.getWordTypeList();
					
					String kanji = groupEntry.getKanji();
					List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

					String kana = groupEntry.getKana();
					List<String> kanaInfoList = groupEntry.getKanaInfoList();

					String romaji = groupEntry.getRomaji();

					List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();			
					List<String> additionalInfoList = new ArrayList<String>(); //groupEntry.getAdditionalInfoList();
					
					PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
					
					polishJapaneseEntry.setId(counter);
					
					List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
										
					for (String currentEntity : wordTypeList) {
						
						DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
						
						if (dictionaryEntryType != null && dictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
							dictionaryEntryTypeList.add(dictionaryEntryType);
						}
					}
									
					polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);
					
					polishJapaneseEntry.setAttributeList(new AttributeList());
					
					polishJapaneseEntry.setWordType(getWordType(kana));
					
					polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
					
					if (kanji == null || kanji.equals("") == true) {
						kanji = "-";
					}
					
					polishJapaneseEntry.setKanji(kanji);
					polishJapaneseEntry.setKana(kana);
					polishJapaneseEntry.setRomaji(romaji);
									
					polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

					List<String> newTranslateList = new ArrayList<String>();
					
					List<PolishJapaneseEntry> findPolishJapaneseEntry = pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntry(
							cachePolishJapaneseEntryList, kanji, kana);
					
					newTranslateList.add("_");
					newTranslateList.add("-----------");
					
					boolean alreadyAddedPolishJapaneseEntry = false;
					
					if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
						newTranslateList.add("JUZ JEST");
						newTranslateList.add("-----------");
						
						alreadyAddedPolishJapaneseEntry = true;
					}
					
					newTranslateList.add(currentMissingWord);
					newTranslateList.add("-----------");
					
					for (GroupEntryTranslate groupEntryTranslate : translateList) {
						newTranslateList.add(groupEntryTranslate.getTranslate());
					}					
					
					polishJapaneseEntry.setTranslates(newTranslateList);
					
					StringBuffer additionalInfoSb = new StringBuffer();

					if (kanjiInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(kanjiInfoList));
						additionalInfoSb.append("\n");
					}

					if (kanaInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(kanaInfoList));
						additionalInfoSb.append("\n");
					}
					
					if (additionalInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(additionalInfoList));
						additionalInfoSb.append("\n");
					}
					
					polishJapaneseEntry.setInfo(additionalInfoSb.toString());
					
					if (alreadyAddedPolishJapaneseEntry == false) {
						foundWordList.add(polishJapaneseEntry);
						
					} else {
						alreadyAddedWordList.add(polishJapaneseEntry);
					}
				}				
				
			} else {
				
				notFoundWordSearchList.add(currentMissingWord);
				
				PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
				
				polishJapaneseEntry.setId(counter);
				
				List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
				
				dictionaryEntryTypeList.add(DictionaryEntryType.UNKNOWN);
												
				polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);				
				polishJapaneseEntry.setAttributeList(new AttributeList());
				
				polishJapaneseEntry.setWordType(WordType.KATAKANA);
				
				polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
								
				polishJapaneseEntry.setKanji("-");
				polishJapaneseEntry.setKana("-");
				polishJapaneseEntry.setRomaji("-");
								
				polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

				List<String> newTranslateList = new ArrayList<String>();
				
				newTranslateList.add(currentMissingWord);
								
				polishJapaneseEntry.setTranslates(newTranslateList);				
				polishJapaneseEntry.setInfo("");
								
				notFoundWordList.add(polishJapaneseEntry);
			}
		}

		reader.close();
		
		System.out.println("Zapisywanie słownika...");
		
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		
		newWordList.addAll(foundWordList);
		newWordList.addAll(alreadyAddedWordList);
		newWordList.addAll(notFoundWordList);
		
		CsvReaderWriter.generateCsv("input/word-new.csv", newWordList, true, true, false);
		
		FileWriter searchResultFileWriter = new FileWriter(fileName + "-new");
		
		searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));
		searchResultFileWriter.write("\n---------\n");
		searchResultFileWriter.write(Utils.convertListToString(notFoundWordSearchList));
		
		searchResultFileWriter.close();
	}
	
	private static List<String> readMissingFile(String fileName) {

		List<String> result = new ArrayList<String>();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

			while (true) {

				String line = br.readLine();

				if (line == null) {
					break;
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
	
	private static void addFieldToDocument(Document document, String fieldName, String value) {

		if (value != null) {
			document.add(new TextField(fieldName, value, Field.Store.YES));
		}
	}

	private static void addFieldToDocument(Document document, String fieldName, Collection<String> collection) {

		if (collection == null) {
			return;
		}

		for (String string : collection) {
			document.add(new TextField(fieldName, string, Field.Store.YES));

		}		
	}
	
	private static Query createQuery(String word) {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = word.split("\\s+");

		BooleanQuery wordBooleanQuery = new BooleanQuery();

		wordBooleanQuery.add(createQuery(wordSplited, "kanji"), Occur.SHOULD);
		wordBooleanQuery.add(createQuery(wordSplited, "kana"), Occur.SHOULD);				

		wordBooleanQuery.add(createQuery(wordSplited, "romaji"), Occur.SHOULD);

		wordBooleanQuery.add(createQuery(wordSplited, "translateList"), Occur.SHOULD);
		wordBooleanQuery.add(createQuery(wordSplited, "additionalInfoList"), Occur.SHOULD);

		query.add(wordBooleanQuery, Occur.MUST);

		return query;
	}
	
	private static Query createQuery(String[] wordSplited, String fieldName) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (String currentWord : wordSplited) {
			//booleanQuery.add(new PrefixQuery(new Term(fieldName, currentWord)), Occur.MUST);
			booleanQuery.add(new TermQuery(new Term(fieldName, currentWord)), Occur.MUST);
		}

		return booleanQuery;
	}
	
	private static GroupEntry createGroupEntry(Document document) {

		GroupEntry groupEntry = new GroupEntry(null, null);

		groupEntry.setWordTypeList(new LinkedHashSet<String>(Arrays.asList(document.getValues("wordTypeList"))));

		groupEntry.setKanji(document.get("kanji"));
		groupEntry.setKanjiInfoList(Arrays.asList(document.getValues("kanjiInfoList")));

		groupEntry.setKana(document.get("kana"));
		groupEntry.setKanaInfoList(Arrays.asList(document.getValues("kanaInfoList")));

		groupEntry.setRomaji(document.get("romaji"));
		
		List<GroupEntryTranslate> translateList = new ArrayList<GroupEntryTranslate>();
		
		for (String currentTranslate : Arrays.asList(document.getValues("translateList"))) {
			
			GroupEntryTranslate translate = new GroupEntryTranslate();
			
			translate.setTranslate(currentTranslate);
			
			translateList.add(translate);
		}
				
		groupEntry.setTranslateList(translateList);
		
		//groupEntry.setAdditionalInfoList(Arrays.asList(document.getValues("additionalInfoList")));

		return groupEntry;
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
}
