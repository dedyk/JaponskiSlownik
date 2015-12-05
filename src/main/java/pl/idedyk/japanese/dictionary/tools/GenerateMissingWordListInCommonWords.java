package pl.idedyk.japanese.dictionary.tools;

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
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;

public class GenerateMissingWordListInCommonWords {

	public static void main(String[] args) throws Exception {
		
		String fileName = args[0];
		
		System.out.println("Wczytywanie brakujących słów...");
		
		List<String> missingWords = readMissingFile(fileName);
		
		System.out.println("Wczytywanie slownika...");

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
				
		// czytanie listy common'owych plikow
		
		System.out.println("Wczytywanie plików common'owych");
		
		Map<Integer, CommonWord> commonWordMap = CsvReaderWriter.readCommonWordFile("input/common_word.csv");
		
		System.out.println("Indeksowanie...");

		// tworzenie indeksu lucene
		Directory index = Helper.createLuceneDictionaryIndex(jmeNewDictionary);
				
		List<String> foundWordSearchList = new ArrayList<String>();		
		
		// stworzenie wyszukiwacza
		IndexReader reader = DirectoryReader.open(index);

		IndexSearcher searcher = new IndexSearcher(reader);
				
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
		
		System.out.println("Zapisywanie słownika...");
				
		FileWriter searchResultFileWriter = new FileWriter(fileName + "-new");
		
		searchResultFileWriter.write(Utils.convertListToString(foundWordSearchList));
		
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
