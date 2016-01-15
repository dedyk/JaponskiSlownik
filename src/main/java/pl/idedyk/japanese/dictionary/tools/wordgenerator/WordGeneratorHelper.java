package pl.idedyk.japanese.dictionary.tools.wordgenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.Directory;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class WordGeneratorHelper {
	
	private String wordCsvFile;
	private String wordCommonCsvFile;
	
	private String jmdicteFile;
	
	//
	
	private JMEDictNewReader jmedictNewReader;
	
	private List<PolishJapaneseEntry> polishJapaneseEntries;	
	private Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList;
	
	private Map<Integer, CommonWord> commonWordMap;
	
	private List<JMEDictNewNativeEntry> jmedictNativeList;
	
	private JMENewDictionary jmeNewDictionary;

	private Directory index;
	
	public WordGeneratorHelper(String wordCsvFile, String wordCommonCsvFile, String jmdicteFile) {
		
		this.wordCsvFile = wordCsvFile;		
		this.wordCommonCsvFile = wordCommonCsvFile;
		
		this.jmdicteFile = jmdicteFile;		
	}
	
	public List<PolishJapaneseEntry> getPolishJapaneseEntriesList() throws IOException, JapaneseDictionaryException {
		
		if (polishJapaneseEntries == null) {
			
			System.out.println("Wczytywanie pliku: " + wordCsvFile);
			
			polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(wordCsvFile);
		}
		
		return polishJapaneseEntries;		
	}
	
	public Map<String, List<PolishJapaneseEntry>> getPolishJapaneseEntriesCache() throws IOException, JapaneseDictionaryException {
		
		if (cachePolishJapaneseEntryList == null) {
			
			List<PolishJapaneseEntry> polishJapaneseEntriesListLocal = getPolishJapaneseEntriesList();
			
			System.out.println("Cache'owanie słownika");
			
			cachePolishJapaneseEntryList = Helper.cachePolishJapaneseEntryList(polishJapaneseEntriesListLocal);			
		}
		
		return cachePolishJapaneseEntryList;
	}
	
	public Map<Integer, CommonWord> getCommonWordMap() throws Exception {
		
		if (commonWordMap == null) {
			
			System.out.println("Wczytywanie pliku: " + wordCommonCsvFile);
			
			commonWordMap = CsvReaderWriter.readCommonWordFile(wordCommonCsvFile);			
		}
		
		return commonWordMap;		
	}
	
	public JMEDictNewReader getJMEDictNewReader() {
		
		if (jmedictNewReader == null) {
			jmedictNewReader = new JMEDictNewReader();
		}
		
		return jmedictNewReader;
	}
	
	public List<JMEDictNewNativeEntry> getJmedictNativeList() throws DictionaryException {
		
		if (jmedictNativeList == null) {
			
			System.out.println("Wczytywanie pliku: " + jmdicteFile);
			
			jmedictNativeList = getJMEDictNewReader().readJMEdict(jmdicteFile);			
		}
		
		return jmedictNativeList;
	}
	
	public JMENewDictionary getJMENewDictionary() throws DictionaryException {
		
		if (jmeNewDictionary == null) {
			
			System.out.println("Przygotowywanie słownika JMEdict");
			
			jmeNewDictionary = getJMEDictNewReader().createJMENewDictionary(getJmedictNativeList());			
		}
		
		return jmeNewDictionary;		
	}
	
	public Directory getLuceneIndex() throws IOException, DictionaryException {
		
		if (index == null) {
			
			System.out.println("Tworzenie indeksu lucene");
			
			index = Helper.createLuceneDictionaryIndex(getJMENewDictionary());			
		}
		
		return index;
	}
}
