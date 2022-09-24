package pl.idedyk.japanese.dictionary.tools.wordgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.store.Directory;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class WordGeneratorHelper {
	
	private String[] wordCsvFiles;
	private String wordCommonCsvFile;
	
	private String jmdicteFile;
	
	private String kanjiFile;
	
	private String kradFile;
	private String kanjiDic2File;
	
	//
	
	@Deprecated
	private pl.idedyk.japanese.dictionary.tools.JMEDictNewReader jmedictNewReader;
	
	private List<PolishJapaneseEntry> polishJapaneseEntries;	
	private Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList;
	
	private Map<Integer, CommonWord> commonWordMap;
	private Map<String, List<CommonWord>> commonWordExistsMap;
	
	@Deprecated
	private List<pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry> jmedictNativeList;
	
	@Deprecated
	private pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper jmedictEntityMapper;
	
	private DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper;
	
	@Deprecated
	private pl.idedyk.japanese.dictionary.dto.JMENewDictionary jmeNewDictionary;

	private Directory index;
	
	private List<KanjiEntryForDictionary> kanjiEntries;
	
	private Map<String, List<String>> kradFileMap;
	private Map<String, KanjiDic2EntryForDictionary> kanjiDic2EntryMap;
	
	public WordGeneratorHelper(String[] wordCsvFiles, String wordCommonCsvFile, String jmdicteFile, String kanjiFile, String kradFile, String kanjiDic2File) {
		
		this.wordCsvFiles = wordCsvFiles;		
		this.wordCommonCsvFile = wordCommonCsvFile;
		
		this.jmdicteFile = jmdicteFile;	
		
		this.kanjiFile = kanjiFile;
		
		this.kradFile = kradFile;
		this.kanjiDic2File = kanjiDic2File;
	}
	
	public List<PolishJapaneseEntry> getPolishJapaneseEntriesList() throws IOException, JapaneseDictionaryException {
		
		if (polishJapaneseEntries == null) {
			
			System.out.println("Wczytywanie pliku: " + Arrays.toString(wordCsvFiles));
			
			polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(wordCsvFiles);
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
	
	public Map<String, List<CommonWord>> getCommonWordExistsMap() throws Exception {
		
		if (commonWordExistsMap == null) {
			
			commonWordExistsMap = new TreeMap<>();
			
			Map<Integer, CommonWord> commonWordMap = getCommonWordMap();
			
			for (CommonWord commonWord : commonWordMap.values()) {
				
				String key = getCommonWordKey(commonWord);
				
				List<CommonWord> commonWordList = commonWordExistsMap.get(key);
				
				if (commonWordList == null) {
					
					commonWordList = new ArrayList<>();
					
					commonWordExistsMap.put(key, commonWordList);
				}
				
				commonWordList.add(commonWord);				
			}
		}
		
		return commonWordExistsMap;
	}
	
	private String getCommonWordKey(CommonWord commonWord) {
		
		String kanji = commonWord.getKanji();
		
		if (kanji == null) {
			kanji = "-";
		}
		
		return kanji + "." + commonWord.getKana(); // + "." + commonWord.getTranslate();
	}
	
	public boolean isCommonWordExists(CommonWord commonWord) throws Exception {
		
		Map<String, List<CommonWord>> commonWordExistsMap = getCommonWordExistsMap();
		
		String key = getCommonWordKey(commonWord);
		
		return commonWordExistsMap.containsKey(key);
	}
	
	@Deprecated
	public pl.idedyk.japanese.dictionary.tools.JMEDictNewReader getJMEDictNewReader() {
		
		if (jmedictNewReader == null) {
			jmedictNewReader = new pl.idedyk.japanese.dictionary.tools.JMEDictNewReader();
		}
		
		return jmedictNewReader;
	}
	
	@Deprecated
	public List<pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry> getJmedictNativeList() throws DictionaryException {
		
		if (jmedictNativeList == null) {
			
			System.out.println("Wczytywanie pliku: " + jmdicteFile);
			
			jmedictNativeList = getJMEDictNewReader().readJMEdict(jmdicteFile);			
		}
		
		return jmedictNativeList;
	}
	
	@Deprecated
	public pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper getJmedictEntityMapper() {
		
		if (jmedictEntityMapper == null) {
			jmedictEntityMapper = new pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper();
		}
		
		return jmedictEntityMapper;
	}
	
	public DictionaryEntryJMEdictEntityMapper getDictionaryEntryJMEdictEntityMapper() {
		
		if (dictionaryEntryJMEdictEntityMapper == null) {
			dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		}
		
		return dictionaryEntryJMEdictEntityMapper;		
	}
	
	@Deprecated
	public pl.idedyk.japanese.dictionary.dto.JMENewDictionary getJMENewDictionary() throws DictionaryException {
		
		if (jmeNewDictionary == null) {
			
			System.out.println("Przygotowywanie słownika JMEdict");
			
			jmeNewDictionary = getJMEDictNewReader().createJMENewDictionary(getJmedictNativeList());			
		}
		
		return jmeNewDictionary;		
	}
	
	@Deprecated
	public Directory getLuceneIndex() throws IOException, DictionaryException {
		
		if (index == null) {
			
			System.out.println("Tworzenie indeksu lucene");
			
			index = Helper.createLuceneDictionaryIndex(getJMENewDictionary());			
		}
		
		return index;
	}
	
	public List<KanjiEntryForDictionary> getKanjiEntries() throws Exception {
		
		if (kanjiEntries == null) {
			
			System.out.println("Wczytywanie pliku kanji");
			
			kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(kanjiFile, getKanjiDic2EntryMap(), false);
		}
		
		return kanjiEntries;
	}
	
	public Map<String, KanjiDic2EntryForDictionary> getKanjiDic2EntryMap() throws Exception {
		
		if (kanjiDic2EntryMap == null) {
			
			System.out.println("Wczytywanie pliku kanjiDic2.xml");
			
			kanjiDic2EntryMap = KanjiDic2Reader.readKanjiDic2(kanjiDic2File, getKradFileMap());
		}
		
		return kanjiDic2EntryMap;
	}

	public Map<String, List<String>> getKradFileMap() throws Exception {
		
		if (kanjiDic2EntryMap == null) {
			
			System.out.println("Wczytywanie pliku kradFile");
			
			kradFileMap = KanjiDic2Reader.readKradFile(kradFile);
		}	
		
		return kradFileMap;
	}	
}
