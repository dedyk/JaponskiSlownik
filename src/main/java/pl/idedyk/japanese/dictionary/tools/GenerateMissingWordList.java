package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.lucene.LuceneAnalyzer;

public class GenerateMissingWordList {

	public static void main(String[] args) throws Exception {
		
		String fileName = args[0];
		
		System.out.println("Wczytywanie brakujących słów...");
		
		List<String> missingWords = readMissingFile(fileName);
		
		System.out.println("Wczytywanie slownika...");

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);

		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();

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

				List<String> translateList = groupEntry.getTranslateList();				
				List<String> additionalInfoList = groupEntry.getAdditionalInfoList();

				addFieldToDocument(document, "wordTypeList", wordTypeList);

				addFieldToDocument(document, "kanji", kanji);
				addFieldToDocument(document, "kanjiInfoList", kanjiInfoList);

				addFieldToDocument(document, "kana", kana);
				addFieldToDocument(document, "kanaInfoList", kanaInfoList);

				addFieldToDocument(document, "romaji", romaji);

				addFieldToDocument(document, "translateList", translateList);
				addFieldToDocument(document, "additionalInfoList", additionalInfoList);

				indexWriter.addDocument(document);
			}			
		}

		indexWriter.close();
		
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		
		// stworzenie wyszukiwacza
		IndexReader reader = DirectoryReader.open(index);

		IndexSearcher searcher = new IndexSearcher(reader);

		int counter = 0;
		
		for (String currentMissingWord : missingWords) {
			
			if (currentMissingWord.equals("") == true) {
				continue;
			}
			
			counter++;

			Query query = createQuery(currentMissingWord);

			ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;
			
			if (scoreDocs.length > 0) {
				
				for (ScoreDoc scoreDoc : scoreDocs) {

					Document foundDocument = searcher.doc(scoreDoc.doc);

					GroupEntry groupEntry = createGroupEntry(foundDocument);

					Set<String> wordTypeList = groupEntry.getWordTypeList();
					
					String kanji = groupEntry.getKanji();
					List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

					String kana = groupEntry.getKana();
					List<String> kanaInfoList = groupEntry.getKanaInfoList();

					String romaji = groupEntry.getRomaji();

					List<String> translateList = groupEntry.getTranslateList();				
					List<String> additionalInfoList = groupEntry.getAdditionalInfoList();
					
					PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
					
					polishJapaneseEntry.setId(counter);
					
					List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
					
					for (String currentEntity : wordTypeList) {
						
						DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
						
						if (dictionaryEntryType != null) {
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
					
					newTranslateList.add("_");
					newTranslateList.add("-----------");
					newTranslateList.add(currentMissingWord);
					newTranslateList.add("-----------");
					newTranslateList.addAll(translateList);
					
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
									
					newWordList.add(polishJapaneseEntry);
				}				
				
			} else {
				
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
								
				newWordList.add(polishJapaneseEntry);				
			}
		}

		reader.close();
		
		System.out.println("Zapisywanie słownika...");
		
		CsvReaderWriter.generateCsv("input/word-new.csv", newWordList, true, true, false);

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

		groupEntry.setTranslateList(Arrays.asList(document.getValues("translateList")));
		groupEntry.setAdditionalInfoList(Arrays.asList(document.getValues("additionalInfoList")));

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
