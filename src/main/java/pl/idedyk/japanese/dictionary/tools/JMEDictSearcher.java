package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.lucene.LuceneAnalyzer;

public class JMEDictSearcher {

	public static void main(String[] args) throws Exception {

		System.out.println("Wczytywanie slownika...");

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);

		JMEDictEntityMapper entityMapper = new JMEDictEntityMapper();

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
							
						} else if (idx != miscInfoList.size() - 1) {
							translate.append(", ");
						}
						
						translate.append(entityMapper.getDesc(miscInfoList.get(idx)));
					}
					
					for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {
						
						if (wasMiscOrAdditionalInfo == false) {
							translate.append(" (");
							
							wasMiscOrAdditionalInfo = true;
							
						} else if (idx != additionalInfoList.size() - 1) {
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

		System.out.println("Gotowe...");

		System.out.println();

		// stworzenie wyszukiwacza
		IndexReader reader = DirectoryReader.open(index);

		IndexSearcher searcher = new IndexSearcher(reader);

		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			System.out.print("Szukane słowo: ");

			String searchWord = consoleReader.readLine();

			if (searchWord.equals("") == true) {
				continue;
			}
			
			if (searchWord.equals("koniec") == true) {
				break;
			}

			Query query = createQuery(searchWord);

			ScoreDoc[] scoreDocs = searcher.search(query, null, 10).scoreDocs;

			System.out.println("=========================\n");
			
			for (ScoreDoc scoreDoc : scoreDocs) {

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

				System.out.println("Czytanie:\n\t\t\t" + kanji + " - " + kana + " - " + romaji);

				System.out.println("Rodzaj słowa:");

				for (String currentWordTypeList : wordTypeList) {				
					System.out.println("\t\t\t" + currentWordTypeList + " - " + entityMapper.getDesc(currentWordTypeList));				
				}

				System.out.println("Info do kanji:");

				for (String currentKanjiInfo : kanjiInfoList) {				
					System.out.println("\t\t\t" + currentKanjiInfo + " - " + entityMapper.getDesc(currentKanjiInfo));				
				}

				System.out.println("Info do kana:");

				for (String currentKanaInfo : kanaInfoList) {				
					System.out.println("\t\t\t" + currentKanaInfo + " - " + entityMapper.getDesc(currentKanaInfo));				
				}

				System.out.println("Tłumaczenie:");

				for (GroupEntryTranslate groupEntryTranslate : translateList) {
					System.out.println("\t\t\t" + groupEntryTranslate.getTranslate());
				}

				System.out.println("Dodatkowe informacje:\t");

				for (String currentAdditionalInfo : additionalInfoList) {
					System.out.println("\t\t\t" + currentAdditionalInfo);
				}

				System.out.println("\n---\n\n");
			}
		}

		consoleReader.close();

		reader.close();
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
}
