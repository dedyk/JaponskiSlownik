package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;

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
		Directory index = Helper.createLuceneDictionaryIndex(jmeNewDictionary);

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

			Query query = Helper.createLuceneDictionaryIndexTermQuery(searchWord);

			ScoreDoc[] scoreDocs = searcher.search(query, null, 30).scoreDocs;

			System.out.println("=========================\n");
			
			for (ScoreDoc scoreDoc : scoreDocs) {

				Document foundDocument = searcher.doc(scoreDoc.doc);

				GroupEntry groupEntry = Helper.createGroupEntry(foundDocument);

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
}
