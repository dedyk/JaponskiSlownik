package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper.KanaWord;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.DialectEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.FieldEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.GTypeEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSourceLsTypeEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSourceLsWaseiEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.MiscEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfoKana;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfoKanaType;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.RelativePriorityEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.SenseAdditionalInfo;

public class Dictionary2Helper {
	
	private static final int CSV_COLUMNS = 11; 
	
	private Dictionary2Helper() { }
	
	
	
	public static Dictionary2Helper init() {
				
		// stary pomocnik		
		WordGeneratorHelper oldWordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");

		return init(oldWordGeneratorHelper);
	}
	
	public static Dictionary2Helper init(WordGeneratorHelper oldWordGeneratorHelper) {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		Dictionary2Helper dictionaryHelper = new Dictionary2Helper();
		
		//
				
		dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict_e");
		
		//
		
		dictionaryHelper.polishDictionaryFile = new File("input/word2.csv");

		dictionaryHelper.oldWordGeneratorHelper = oldWordGeneratorHelper;
		
		//
		
		return dictionaryHelper;
	}
	
	private KanaHelper kanaHelper = new KanaHelper();
	
	//
	
	private File jmdictFile;	
	private JMdict jmdict = null;
	
	private Map<Integer, JMdict.Entry> jmdictEntryIdCache;
		
	//
	
	private File polishDictionaryFile;		
	private Map<Integer, JMdict.Entry> polishDictionaryEntryListMap;
	
	//
	
	// analizator lucynkowy
	private SimpleAnalyzer jmdictLuceneAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	
	private Directory jmdictLuceneIndex; 
	private IndexReader jmdictLuceneIndexReader; 
	private IndexSearcher jmdictLuceneIndexReaderSearcher;
	
	private WordGeneratorHelper oldWordGeneratorHelper;
	
	public void close() throws Exception {
		
		if (jmdictLuceneIndex != null) {
			jmdictLuceneIndex.close();
		}
		
		if (jmdictLuceneIndexReader != null) {
			jmdictLuceneIndexReader.close();
		}
	}
	
	public JMdict getJMdict() throws Exception {
		
		if (jmdict == null) {

			// walidacja xsd pliku JMdict
			System.out.println("Validating JMdict");
			
			// walidacja xsd
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			Schema schema = factory.newSchema(Dictionary2Helper.class.getResource("/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd"));
			
			Validator validator = schema.newValidator();
						
			validator.validate(new StreamSource(jmdictFile));			

			// wczytywanie pliku JMdict
			System.out.println("Reading JMdict");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);
			
			//
			
			// uzupelnienie o jezyk domyslny
			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				
				for (Sense sense : entry.getSenseList()) {
					
					for (Gloss gloss : sense.getGlossList()) {
						
						if (gloss.getLang() == null) {
							gloss.setLang("eng");
						}
					}
					
					for (SenseAdditionalInfo senseAdditionalInfo : sense.getAdditionalInfoList()) {
						
						if (senseAdditionalInfo.getLang() == null) {
							senseAdditionalInfo.setLang("eng");
						}
					}					
				}
			}
		}
		
		return jmdict;
	}
	
	private void initJMdictEntryIdCache() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();
		
		//
		
		if (jmdictEntryIdCache == null) {
			
			System.out.println("Caching JMdict");
			
			jmdictEntryIdCache = new TreeMap<>();
			
			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				jmdictEntryIdCache.put(entry.getEntryId(), entry);
			}
		}		
	}
	
	public JMdict.Entry getJMdictEntry(Integer entryId) throws Exception {
		
		initJMdictEntryIdCache();
		
		return jmdictEntryIdCache.get(entryId);		
	}
		
	private void createJMdictLuceneIndex() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();
		
		// inicjalizacja cache wpisow JMdict
		initJMdictEntryIdCache();
		
		//
		
		if (jmdictLuceneIndex == null) {
			
			System.out.println("Initializing JMdict lucene index");
									
			jmdictLuceneIndex = new RAMDirectory();
			
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, jmdictLuceneAnalyzer);
			indexWriterConfig.setOpenMode(OpenMode.CREATE);

			IndexWriter indexWriter = new IndexWriter(jmdictLuceneIndex, indexWriterConfig);			
			
			List<Entry> entryList = jmdict.getEntryList();
						
			// indeksowanie
			for (Entry entry : entryList) {
				
				Document document = new Document();
				
				//
				
				Integer entryId = entry.getEntryId();
				
				addIntFieldToDocument(document, JMdictLuceneFields.ENTRY_ID, entryId);
				
				List<KanjiInfo> entryKanjiInfoList = entry.getKanjiInfoList();
				
				for (KanjiInfo kanjiInfo : entryKanjiInfoList) {
					addStringFieldToDocument(document, JMdictLuceneFields.KANJI, kanjiInfo.getKanji());
				}
				
				//
				
				List<ReadingInfo> entryReadingInfoList = entry.getReadingInfoList();
				
				for (ReadingInfo readingInfo : entryReadingInfoList) {
					addStringFieldToDocument(document, JMdictLuceneFields.KANA, readingInfo.getKana().getValue());
				}

				for (ReadingInfo readingInfo : entryReadingInfoList) {
					
					String romaji = readingInfo.getKana().getRomaji();
					
					if (romaji == null) {
						try {
							romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(readingInfo.getKana().getValue(), kanaHelper.getKanaCache(), true));
						} catch (Exception e) {
							// noop
						}
					}
					
					addStringFieldToDocument(document, JMdictLuceneFields.ROMAJI, romaji);
				}

				//
				
				List<Sense> entrySenseList = entry.getSenseList();
				
				for (Sense sense : entrySenseList) {
					
					List<Gloss> senseGloss = sense.getGlossList();
					
					for (Gloss gloss : senseGloss) {
						
						if (Arrays.asList("eng", "pol").contains(gloss.getLang()) == false) {
							continue;
						}
						
						addTextFieldToDocument(document, JMdictLuceneFields.TRANSLATE, gloss.getValue());
					}
					
					//
					
					List<SenseAdditionalInfo> senseAdditionalInfoList = sense.getAdditionalInfoList();
					
					for (SenseAdditionalInfo senseAdditionalInfo : senseAdditionalInfoList) {

						if (Arrays.asList("eng", "pol").contains(senseAdditionalInfo.getLang()) == false) {
							continue;
						}
						
						addTextFieldToDocument(document, JMdictLuceneFields.SENSE_ADDITIONAL_INFO, senseAdditionalInfo.getValue());
					}
					
					//
					
					List<LanguageSource> languageSourceList = sense.getLanguageSourceList();
					
					for (LanguageSource languageSource : languageSourceList) {
						addTextFieldToDocument(document, JMdictLuceneFields.LANGUAGE_SOURCE, languageSource.getValue());
					}					
				}
				
				//
				
				indexWriter.addDocument(document);
			}
			
			indexWriter.close();
		}
	}
	
	public List<JMdict.Entry> findInJMdict(String word) throws Exception {
		
		// tworzenie cache'u
		initJMdictEntryIdCache();
		
		// tworzenie indeksu (jesli trzeba)
		createJMdictLuceneIndex();
		
		if (jmdictLuceneIndexReader == null) {			
			jmdictLuceneIndexReader = DirectoryReader.open(jmdictLuceneIndex);			
		}
		
		if (jmdictLuceneIndexReaderSearcher == null) {
			jmdictLuceneIndexReaderSearcher = new IndexSearcher(jmdictLuceneIndexReader);
		}
		
		// tworzymy zapytanie
		Query query = createLuceneDictionaryIndexTermQuery(word);
		
		// wyszukujemy
		ScoreDoc[] scoreDocs = jmdictLuceneIndexReaderSearcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
		
		List<JMdict.Entry> result = new ArrayList<>();
		
		if (scoreDocs.length > 0) {
									
			for (ScoreDoc scoreDoc : scoreDocs) {
				
				Document foundDocument = jmdictLuceneIndexReaderSearcher.doc(scoreDoc.doc);
				
				Integer entryId = new Integer(foundDocument.get(JMdictLuceneFields.ENTRY_ID));
				
				//
				
				result.add(jmdictEntryIdCache.get(entryId));
			}
		}
		
		return result;
	}
	
	private Query createLuceneDictionaryIndexTermQuery(String word) throws Exception {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = getTokenizedWords(word);

		BooleanQuery wordBooleanQuery = new BooleanQuery();

		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.KANJI), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.KANA), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.ROMAJI), Occur.SHOULD);

		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.TRANSLATE), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.LANGUAGE_SOURCE), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.SENSE_ADDITIONAL_INFO), Occur.SHOULD);

		query.add(wordBooleanQuery, Occur.MUST);

		return query;
	}
	
	private String[] getTokenizedWords(String text) throws Exception {
		
		List<String> tokenizedWordsList = new ArrayList<String>();
		
		TokenStream tokenStream = null;
		
		try {
			tokenStream = jmdictLuceneAnalyzer.tokenStream("fieldName", text);
			
			tokenStream.reset();
			
			while(true) {
				
				boolean incrementTokenResult = tokenStream.incrementToken();
				
				if (incrementTokenResult == false) {
					
					tokenStream.end();
										
					break;
				}
				
				tokenizedWordsList.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}			
		
		} finally {
			
			if (tokenStream != null) {
				tokenStream.close();
			}			
		}
				
		return tokenizedWordsList.toArray(new String[tokenizedWordsList.size()]);
	}

	private Query createTermQuery(String[] wordSplited, String fieldName) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (String currentWord : wordSplited) {
			booleanQuery.add(new TermQuery(new Term(fieldName, currentWord)), Occur.MUST);
		}

		return booleanQuery;
	}

	
	private void addTextFieldToDocument(Document document, String fieldName, String value) {

		if (value != null) {
			document.add(new TextField(fieldName, value, Field.Store.YES));
		}
	}

	private void addStringFieldToDocument(Document document, String fieldName, String value) {

		if (value != null) {
			document.add(new StringField(fieldName, value, Field.Store.YES));
		}
	}
		
	private void addIntFieldToDocument(Document document, String fieldName, Integer value) {

		if (value != null) {
			document.add(new IntField(fieldName, value, Field.Store.YES));
		}
	}
	
	public void saveEntryListAsHumanCsv(SaveEntryListAsHumanCsvConfig config, String fileName, List<Entry> entryList, EntryAdditionalData entryAdditionalData) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		for (Entry entry : entryList) {
			
			// zapisz rekord
			saveEntryAsHumanCsv(config, csvWriter, entry, entryAdditionalData);
			
			// rozdzielenie, aby zawartosc byla zabrdziej przjerzysta
			if (entry != entryList.get(entryList.size() - 1)) {
				
				boolean useTextQualifier = csvWriter.getUseTextQualifier();
				
				int columnsNo = 0;
				
				// wypelniacz 2
				csvWriter.setUseTextQualifier(false); // takie obejscie dziwnego zachowania
				
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
					csvWriter.write("");
				}
				
				csvWriter.endRecord();
				
				//
				
				columnsNo = 0;
				
				// wypelniacz 3			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
				
				//
				
				csvWriter.setUseTextQualifier(useTextQualifier);
			}			
		}		
		
		csvWriter.close();
	}
	
	private void saveEntryAsHumanCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry, EntryAdditionalData entryAdditionalData) throws Exception {
				
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(config, csvWriter, entry);
		
		// kanji
		new EntryPartConverterKanji().writeToCsv(config, csvWriter, entry);
		
		// reading
		new EntryPartConverterReading().writeToCsv(config, csvWriter, entry);
		
		// sense
		new EntryPartConverterSense().writeToCsv(config, csvWriter, entry, entryAdditionalData);
				
		// rekord koncowy
		new EntryPartConverterEnd().writeToCsv(config, csvWriter, entry);		
	}
	
	private static ReadingInfoKanaType getKanaType(String kana) {
		
		ReadingInfoKanaType kanaType = null;
				
		for (int idx = 0; idx < kana.length(); ++idx) {
			
			char c = kana.charAt(idx);
			
			boolean currentCIsHiragana = Utils.isHiragana(c);
			boolean currentCIsKatakana = Utils.isKatakana(c);
			
			if (currentCIsHiragana == true) {
				
				if (kanaType == null) {
					kanaType = ReadingInfoKanaType.HIRAGANA;
					
				} else if (kanaType == ReadingInfoKanaType.KATAKANA) {
					kanaType = ReadingInfoKanaType.KATAKANA_HIRAGANA;					
				}				
			}

			if (currentCIsKatakana == true) {
				
				if (kanaType == null) {
					kanaType = ReadingInfoKanaType.KATAKANA;
					
				} else if (kanaType == ReadingInfoKanaType.HIRAGANA) {
					kanaType = ReadingInfoKanaType.HIRAGANA_KATAKANA;					
				}				
			}			
		}	
		
		return kanaType;
	}
	
	public List<Entry> readEntryListFromHumanCsv(String fileName) throws Exception {
		
		EntryPartConverterBegin entryPartConverterBegin = new EntryPartConverterBegin();
		EntryPartConverterEnd entryPartConverterEnd = new EntryPartConverterEnd();
		EntryPartConverterKanji entryPartConverterKanji = new EntryPartConverterKanji();
		EntryPartConverterReading entryPartConverterReading = new EntryPartConverterReading();
		EntryPartConverterSense entryPartConverterSense = new EntryPartConverterSense();
		
		//
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
				
		//
		
		List<Entry> result = new ArrayList<>();
		
		Entry newEntry = null;
		
		while (csvReader.readRecord()) {
			
			String fieldTypeString = csvReader.get(0);
			
			if (fieldTypeString.equals("") == true) {
				continue;
			}

			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType == EntryHumanCsvFieldType.BEGIN) { // nowy rekord
				
				newEntry = new Entry();
				
				entryPartConverterBegin.parseCsv(csvReader, newEntry);
				
			} else if (fieldType == EntryHumanCsvFieldType.END) { // zakonczenie rekordu
				
				entryPartConverterEnd.parseCsv(csvReader, newEntry);
				
				result.add(newEntry);
				
			} else if (fieldType == EntryHumanCsvFieldType.KANJI) { // kanji
				
				entryPartConverterKanji.parseCsv(csvReader, newEntry);				
				
			} else if (fieldType == EntryHumanCsvFieldType.READING) { // reading
				
				entryPartConverterReading.parseCsv(csvReader, newEntry);
				
			} else if (fieldType == EntryHumanCsvFieldType.SENSE_COMMON) { // sense common 
				
				entryPartConverterSense.parseCsv(csvReader, newEntry);
			
			} else if (fieldType == EntryHumanCsvFieldType.SENSE_ENG || fieldType == EntryHumanCsvFieldType.SENSE_POL) { // sense eng/pol 
			
				entryPartConverterSense.parseCsv(csvReader, newEntry);
				
			} else {				
				throw new RuntimeException(fieldType.name());
			}			
		}
		
		csvReader.close();
		
		return result;
	}
	
	public void saveJMdictAsXml(JMdict newJMdict, String fileName) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

		//
				
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		//
		
		jaxbMarshaller.marshal(newJMdict, new File(fileName));
	}
	
	//
	
	private static class JMdictLuceneFields {
		
		private static final String ENTRY_ID = "entryId";
		
		private static final String KANJI = "kanji";		
		private static final String KANA = "kana";		
		private static final String ROMAJI = "romaji";
		
		private static final String TRANSLATE = "translate";		
		private static final String SENSE_ADDITIONAL_INFO = "senseAdditionalInfo";		
		private static final String LANGUAGE_SOURCE = "languageSource";		
	}
		
	//
	
	public static class SaveEntryListAsHumanCsvConfig {
		
		public boolean addOldPolishTranslates = false;
		public boolean addOldPolishTranslatesDuringDictionaryUpdate = false;
		public boolean addDeleteSenseDuringDictionaryUpdate = false;
		
		public boolean markRomaji = false;
		
		public boolean shiftCells = false;		
		public boolean shiftCellsGenerateIds = false;
		public Integer shiftCellsGenerateIdsId = 1;
		
	}
	
	private enum EntryHumanCsvFieldType {
		
		BEGIN,
		
		KANJI,
		READING,
		
		SENSE_COMMON,
		SENSE_ENG,
		SENSE_POL,
				
		END;
	}
	
	private static enum ReadingInfoNoKanji {		
		NO_KANJI;		
	}
	
	//
			
	private class EntryPartConverterBegin {

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws IOException {
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				
				if (config.shiftCellsGenerateIds == false) {
					csvWriter.write(""); columnsNo++;
					
				} else {
					csvWriter.write(String.valueOf(config.shiftCellsGenerateIdsId)); columnsNo++;
					
					config.shiftCellsGenerateIdsId++;
				}
			}
			
			csvWriter.write(EntryHumanCsvFieldType.BEGIN.name()); columnsNo++;
			csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}

		public void parseCsv(CsvReader csvReader, Entry entry) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.BEGIN) {
				throw new RuntimeException(fieldType.name());
			}
			
			entry.setEntryId(Integer.parseInt(csvReader.get(1)));			
		}
	}
	
	private class EntryPartConverterEnd {

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws IOException {
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(EntryHumanCsvFieldType.END.name()); columnsNo++;
			csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();			
		}

		public void parseCsv(CsvReader csvReader, Entry entry) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.END) {
				throw new RuntimeException(fieldType.name());
			}

			// noop
		}
	}
	
	private class EntryPartConverterKanji {

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws IOException {
						
			List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {
				
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}

				csvWriter.write(EntryHumanCsvFieldType.KANJI.name()); columnsNo++;		
				csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;

				csvWriter.write(kanjiInfo.getKanji()); columnsNo++;
				csvWriter.write(Helper.convertEnumListToString(kanjiInfo.getKanjiAdditionalInfoList())); columnsNo++;
				csvWriter.write(Helper.convertEnumListToString(kanjiInfo.getRelativePriorityList())); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}
		}
		
		public void parseCsv(CsvReader csvReader, Entry entry) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.KANJI) {
				throw new RuntimeException(fieldType.name());
			}
			
			KanjiInfo kanjiInfo = new KanjiInfo();
			
			kanjiInfo.setKanji(csvReader.get(2));
			
			//
			
			List<String> kanjiAdditionalInfoEnumStringList = Helper.convertStringToList(csvReader.get(3));
			
			for (String currentKanjiAdditionalInfoStringList : kanjiAdditionalInfoEnumStringList) {
				kanjiInfo.getKanjiAdditionalInfoList().add(KanjiAdditionalInfoEnum.fromValue(currentKanjiAdditionalInfoStringList));
			}
			
			//
			
			List<String> relativePriorityEnumStringList = Helper.convertStringToList(csvReader.get(4));
			
			for (String currentRelativePriorityEnumStringList : relativePriorityEnumStringList) {
				kanjiInfo.getRelativePriorityList().add(RelativePriorityEnum.fromValue(currentRelativePriorityEnumStringList));
			}
			
			//
			
			entry.getKanjiInfoList().add(kanjiInfo);
		}
	}
	
	private class EntryPartConverterReading {

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws IOException {
						
			List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : readingInfoList) {
				
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				csvWriter.write(EntryHumanCsvFieldType.READING.name()); columnsNo++;		
				csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;
				
				//
				
				generateKanaTypeAndRomaji(readingInfo, config.markRomaji);
				
				ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();				
				csvWriter.write(kanaType.name()); columnsNo++;

				csvWriter.write(readingInfo.getKana().getValue()); columnsNo++;
				
				String romaji = readingInfo.getKana().getRomaji();
				
				csvWriter.write(romaji); columnsNo++;
				
				csvWriter.write(readingInfo.getNoKanji() != null ? ReadingInfoNoKanji.NO_KANJI.name() : "-"); columnsNo++;			
				csvWriter.write(Helper.convertListToString(readingInfo.getKanjiRestrictionList())); columnsNo++;
								
				csvWriter.write(Helper.convertEnumListToString(readingInfo.getReadingAdditionalInfoList())); columnsNo++;
				csvWriter.write(Helper.convertEnumListToString(readingInfo.getRelativePriorityList())); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}
		}
				
		public void parseCsv(CsvReader csvReader, Entry entry) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.READING) {
				throw new RuntimeException(fieldType.name());
			}
			
			ReadingInfo readingInfo = new ReadingInfo();
			
			readingInfo.setKana(new ReadingInfoKana());
			
			readingInfo.getKana().setKanaType(ReadingInfoKanaType.fromValue(csvReader.get(2))); // moja modyfikacja
			readingInfo.getKana().setValue(csvReader.get(3));
			readingInfo.getKana().setRomaji(csvReader.get(4)); // moja modyfikacja
			
			String noKanji = csvReader.get(5);
			
			if (noKanji.equals(ReadingInfoNoKanji.NO_KANJI.name()) == true) {
				readingInfo.setNoKanji(new ReadingInfo.ReNokanji());
			}
			
			readingInfo.getKanjiRestrictionList().addAll(Helper.convertStringToList(csvReader.get(6)));
						
			//
			
			List<String> readingAdditionalInfoEnumStringList = Helper.convertStringToList(csvReader.get(7));
			
			for (String currentReadingAdditionalInfoList : readingAdditionalInfoEnumStringList) {
				readingInfo.getReadingAdditionalInfoList().add(ReadingAdditionalInfoEnum.fromValue(currentReadingAdditionalInfoList));
			}
			
			//
			
			List<String> relativePriorityEnumStringList = Helper.convertStringToList(csvReader.get(8));
			
			for (String currentRelativePriorityEnumStringList : relativePriorityEnumStringList) {
				readingInfo.getRelativePriorityList().add(RelativePriorityEnum.fromValue(currentRelativePriorityEnumStringList));
			}


			entry.getReadingInfoList().add(readingInfo);
		}
	}
	
	private class EntryPartConverterSense {

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry, EntryAdditionalData entryAdditionalData) throws IOException {
			
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense sense : senseList) {
				
				int columnsNo = 0;
				
				List<Gloss> glossList = sense.getGlossList();
				
				List<Gloss> glossEngList = glossList.stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				List<Gloss> glossPolList = glossList.stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());

				if (glossEngList.size() == 0) {
					continue;
				}
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				// czesc wspolna dla wszystkich jezykow
				csvWriter.write(EntryHumanCsvFieldType.SENSE_COMMON.name());  columnsNo++;
				csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;

				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanjiList())); columnsNo++;
				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanaList())); columnsNo++;
				
				csvWriter.write(Helper.convertEnumListToString(sense.getPartOfSpeechList())); columnsNo++;
				
				csvWriter.write(Helper.convertListToString(sense.getReferenceToAnotherKanjiKanaList())); columnsNo++;
				
				csvWriter.write(Helper.convertListToString(sense.getAntonymList())); columnsNo++;
				
				csvWriter.write(Helper.convertEnumListToString(sense.getFieldList())); columnsNo++;
				csvWriter.write(Helper.convertEnumListToString(sense.getMiscList())); columnsNo++;
				
				//
				
				List<LanguageSource> languageSourceList = sense.getLanguageSourceList();

				StringWriter languageSourceCsvWriterString = new StringWriter();
				
				CsvWriter languageSourceCsvWriter = new CsvWriter(languageSourceCsvWriterString, '|');
				
				for (LanguageSource languageSource : languageSourceList) {
										
					String languageSourceLsType = languageSource.getLsType() != null ? languageSource.getLsType().value() : "-";
					String languageSourceWasei = languageSource.getLsWasei() != null ? languageSource.getLsWasei().value() : "-";
					String languageSourceLang = languageSource.getLang() != null ? languageSource.getLang() : "-";
					String languageSourceValue = languageSource.getValue() != null ? languageSource.getValue() : "-";
																				
					languageSourceCsvWriter.write(languageSourceLsType);
					languageSourceCsvWriter.write(languageSourceWasei);
					languageSourceCsvWriter.write(languageSourceLang);
					languageSourceCsvWriter.write(languageSourceValue);
					
					languageSourceCsvWriter.endRecord();
				}
				
				languageSourceCsvWriter.close();
				
				csvWriter.write(languageSourceCsvWriterString.toString()); columnsNo++;
				
				csvWriter.write(Helper.convertEnumListToString(sense.getDialectList())); columnsNo++;
				
				csvWriter.endRecord();
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
					csvWriter.write(null);
				}
				
				// czesc specyficzna dla jezyka angielskiego i polskiego (tlumaczenia)
				
				writeToCsvLangSense(config, csvWriter, entry, entryAdditionalData, sense, EntryHumanCsvFieldType.SENSE_ENG, glossEngList);
				writeToCsvLangSense(config, csvWriter, entry, entryAdditionalData, sense, EntryHumanCsvFieldType.SENSE_POL, glossPolList);	
			}	
			
			// sprawdzamy, czy cos zostalo przygotowane
			EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.jmdictEntryAdditionalDataEntryMap.get(entry.getEntryId());
			
			// podczas aktualizacji slownika jakis sens zostal skasowany, tymczasowo wpisanie starych sense'ow
			if (config.addDeleteSenseDuringDictionaryUpdate == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary != null) { 				
								
				for (EntryAdditionalDataEntry$UpdateDictionarySense entryAdditionalDataEntry$UpdateDictionarySense : entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary) {
					
					int columnsNo = 0;
					
					if (config.shiftCells == true) {
						csvWriter.write(""); columnsNo++;
					}
					
					csvWriter.write(EntryHumanCsvFieldType.SENSE_POL.name() + "_DELETE"); columnsNo++;		
					csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;
					
					csvWriter.write("USUNIETE_TŁUMACZENIE\n" + "---\n---\n" + generateGlossWriterCellValue(entryAdditionalDataEntry$UpdateDictionarySense.oldPolishGlossList)); columnsNo++;

					//
					
					List<String> senseAdditionalInfoStringList = new ArrayList<>();

					for (SenseAdditionalInfo senseAdditionalInfo : entryAdditionalDataEntry$UpdateDictionarySense.oldPolishSenseAdditionalInfoList) {
						senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
					}

					csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList)); columnsNo++;
					
					// wypelniacz			
					for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
						csvWriter.write(null);
					}

					csvWriter.endRecord();
				}				
			}
		}
		
		private void writeToCsvLangSense(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry, EntryAdditionalData entryAdditionalData, Sense sense, EntryHumanCsvFieldType entryHumanCsvFieldType, List<Gloss> glossLangList) throws IOException {
			
			if (glossLangList.size() == 0) {
				return;
			}
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(entryHumanCsvFieldType.name()); columnsNo++;		
			csvWriter.write(String.valueOf(entry.getEntryId())); columnsNo++;
			
			csvWriter.write(generateGlossWriterCellValue(glossLangList)); columnsNo++;

			//

			List<SenseAdditionalInfo> additionalInfoList = sense.getAdditionalInfoList();

			List<String> senseAdditionalInfoStringList = new ArrayList<>();

			for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {

				String senseAdditionalInfoLang = senseAdditionalInfo.getLang();

				if (senseAdditionalInfoLang.equals(entryHumanCsvFieldType == EntryHumanCsvFieldType.SENSE_ENG ? "eng" : "pol") == true) {						
					senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
				}
			}

			csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList)); columnsNo++;
			
			//
			
			if (entryHumanCsvFieldType == EntryHumanCsvFieldType.SENSE_POL) { 
				
				// sprawdzamy, czy cos zostalo przygotowane
				EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.jmdictEntryAdditionalDataEntryMap.get(entry.getEntryId());

				BEFORE_IF:
				if (config.addOldPolishTranslates == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.oldPolishJapaneseEntryList != null) { // dodawanie tlumaczenia ze starego slownika

					// grupujemy po unikalnym tlumaczeniu
					Map<String, String> uniqueOldPolishJapaneseTranslates = new TreeMap<>();

					for (PolishJapaneseEntry polishJapaneseEntry : entryAdditionalDataEntry.oldPolishJapaneseEntryList) {

						String polishJapaneseEntryTranslate = Helper.convertListToString(polishJapaneseEntry.getTranslates());
						String polishJapaneseEntryInfo = polishJapaneseEntry.getInfo() != null ? polishJapaneseEntry.getInfo() : "";

						//

						String infoForPolishJapaneseEntryTranslate = uniqueOldPolishJapaneseTranslates.get(polishJapaneseEntryTranslate);

						if (infoForPolishJapaneseEntryTranslate == null) {
							uniqueOldPolishJapaneseTranslates.put(polishJapaneseEntryTranslate, polishJapaneseEntryInfo);

						}
					}

					if (uniqueOldPolishJapaneseTranslates.size() == 0) {
						break BEFORE_IF;
					}

					// dodajemy unikaln tlumaczenia i informacje dodatkowe
					Iterator<java.util.Map.Entry<String, String>> uniqueOldPolishJapaneseTranslatesEntryIterator = uniqueOldPolishJapaneseTranslates.entrySet().iterator();

					while (uniqueOldPolishJapaneseTranslatesEntryIterator.hasNext() == true) {

						java.util.Map.Entry<String, String> currentPolishJapaneseTranslateAndInfo = uniqueOldPolishJapaneseTranslatesEntryIterator.next();

						csvWriter.write("STARE_TŁUMACZENIE\n" + "---\n---\n" + currentPolishJapaneseTranslateAndInfo.getKey()); columnsNo++;

						if (currentPolishJapaneseTranslateAndInfo.getValue().equals("") == false) {
							csvWriter.write("STARE_INFO\n" + "---\n---\n" + currentPolishJapaneseTranslateAndInfo.getValue()); columnsNo++;
						}
					}
				}
				
				//
				
				if (config.addOldPolishTranslatesDuringDictionaryUpdate == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.updateDictionarySenseMap != null) { // podczas aktualizacji slownika jakis sens zmienil sie
					
					EntryAdditionalDataEntry$UpdateDictionarySense entryAdditionalDataEntry$UpdateDictionarySense = entryAdditionalDataEntry.updateDictionarySenseMap.get(System.identityHashCode(sense));
					
					if (entryAdditionalDataEntry$UpdateDictionarySense != null) { // podczas aktualizacji slownika, jakis sense zmienil sie, wpisanie starego polskiego znaczenia
						
						csvWriter.write("STARE_TŁUMACZENIE\n" + "---\n---\n" + generateGlossWriterCellValue(entryAdditionalDataEntry$UpdateDictionarySense.oldPolishGlossList)); columnsNo++;

						//
						
						senseAdditionalInfoStringList = new ArrayList<>();

						for (SenseAdditionalInfo senseAdditionalInfo : entryAdditionalDataEntry$UpdateDictionarySense.oldPolishSenseAdditionalInfoList) {
							senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
						}

						csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList)); columnsNo++;						
					}
				}				
			}			
			
			//////
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}
		
		private String generateGlossWriterCellValue(List<Gloss> glossLangList) throws IOException {
			
			StringWriter glossListCsvWriterString = new StringWriter();

			CsvWriter glossListCsvWriter = new CsvWriter(glossListCsvWriterString, '|');

			for (Gloss gloss : glossLangList) {

				GTypeEnum glossType = gloss.getGType();
				String glossValue = gloss.getValue();

				glossListCsvWriter.write(glossValue);

				if (glossType != null) {
					glossListCsvWriter.write(glossType.value());
				}

				glossListCsvWriter.endRecord();
			}					

			glossListCsvWriter.close();

			return glossListCsvWriterString.toString();			
		}
		
		public void parseCsv(CsvReader csvReader, Entry entry) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType == EntryHumanCsvFieldType.SENSE_COMMON) {
				
				Sense sense = new Sense();
				
				sense.getRestrictedToKanjiList().addAll(Helper.convertStringToList(csvReader.get(2)));
				sense.getRestrictedToKanaList().addAll(Helper.convertStringToList(csvReader.get(3)));
				
				//
				
				List<String> partOfSpeechStringList = Helper.convertStringToList(csvReader.get(4));
				
				for (String currentPartOfSpeechString : partOfSpeechStringList) {
					sense.getPartOfSpeechList().add(PartOfSpeechEnum.fromValue(currentPartOfSpeechString));
				}

				//
				
				sense.getReferenceToAnotherKanjiKanaList().addAll(Helper.convertStringToList(csvReader.get(5)));
				sense.getAntonymList().addAll(Helper.convertStringToList(csvReader.get(6)));
				
				//
				
				List<String> fieldStringList = Helper.convertStringToList(csvReader.get(7));
				
				for (String currentFieldString : fieldStringList) {
					sense.getFieldList().add(FieldEnum.fromValue(currentFieldString));
				}
				
				//
				
				List<String> miscStringList = Helper.convertStringToList(csvReader.get(8));
				
				for (String currentMiscString : miscStringList) {
					sense.getMiscList().add(MiscEnum.fromValue(currentMiscString));
				}
				
				//
								
				{
					String languageSourceListString = csvReader.get(9);
					
					CsvReader languageSourceCsvReader = new CsvReader(new StringReader(languageSourceListString), '|');
					
					while (languageSourceCsvReader.readRecord()) {
						
						LanguageSourceLsTypeEnum languageSourceLsType = languageSourceCsvReader.get(0).equals("-") == false ? LanguageSourceLsTypeEnum.fromValue(languageSourceCsvReader.get(0)) : null;
						LanguageSourceLsWaseiEnum languageSourceWasei = languageSourceCsvReader.get(1).equals("-") == false ? LanguageSourceLsWaseiEnum.fromValue(languageSourceCsvReader.get(1)) : null;
						String languageSourceLang = languageSourceCsvReader.get(2).equals("-") == false ? languageSourceCsvReader.get(2) : null;
						String languageSourceValue = languageSourceCsvReader.get(3);

						//
						
						LanguageSource languageSource = new LanguageSource();
						
						languageSource.setLsType(languageSourceLsType);
						languageSource.setLsWasei(languageSourceWasei);
						languageSource.setLang(languageSourceLang);
						languageSource.setValue(languageSourceValue);						
						
						//
						
						sense.getLanguageSourceList().add(languageSource);
					}
					
					languageSourceCsvReader.close();
				}
				
				//
				
				List<String> dialectList = Helper.convertStringToList(csvReader.get(10));
				
				for (String currentDialetList : dialectList) {
					sense.getDialectList().add(DialectEnum.fromValue(currentDialetList));
				}
				
				//
			
				entry.getSenseList().add(sense);
				
			} else if (fieldType == EntryHumanCsvFieldType.SENSE_ENG || fieldType == EntryHumanCsvFieldType.SENSE_POL) {
				
				Sense sense = entry.getSenseList().get(entry.getSenseList().size() - 1);
				
				{
					String glossListString = csvReader.get(2);
					
					CsvReader glossListStringCsvReader = new CsvReader(new StringReader(glossListString), '|');
					
					while (glossListStringCsvReader.readRecord()) {
						
						String glossValue = glossListStringCsvReader.get(0);
						String glossTypeString = glossListStringCsvReader.get(1).equals("") == false ? glossListStringCsvReader.get(1) : null;
						
						//
						
						Gloss gloss = new Gloss();
						
						gloss.setLang(fieldType == EntryHumanCsvFieldType.SENSE_ENG ? "eng" : "pol");
						gloss.setValue(glossValue);
						gloss.setGType(glossTypeString != null ? GTypeEnum.fromValue(glossTypeString) : null);
												
						//
						
						sense.getGlossList().add(gloss);
					}
					
					glossListStringCsvReader.close();
				}
				
				//
				
				List<String> additionalInfoStringList = Helper.convertStringToList(csvReader.get(3));
								
				for (String currentAdditionalInfoString : additionalInfoStringList) {
					
					SenseAdditionalInfo senseAdditionalInfo = new SenseAdditionalInfo();
					
					senseAdditionalInfo.setLang(fieldType == EntryHumanCsvFieldType.SENSE_ENG ? "eng" : "pol");
					senseAdditionalInfo.setValue(currentAdditionalInfoString);
					
					sense.getAdditionalInfoList().add(senseAdditionalInfo);
				}
				
			} else {
				throw new RuntimeException(fieldType.name());
			}			
		}
	}
	
	private void generateKanaTypeAndRomaji(ReadingInfo readingInfo, boolean markRomaji) {
		
		ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();
		
		if (kanaType == null) {
			kanaType = getKanaType(readingInfo.getKana().getValue());
		}
		
		readingInfo.getKana().setKanaType(kanaType);

		String romaji = readingInfo.getKana().getRomaji();
		
		if (romaji == null) {
			
			if (romaji == null) {
				try {
					romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(readingInfo.getKana().getValue(), kanaHelper.getKanaCache(), true));
				} catch (Exception e) {
					romaji = "FIXME";
				}
			}				
		}
		
		if (markRomaji == true) {
			romaji = romaji + " --- !!! SPRAWDŹ !!!";
		}
		
		readingInfo.getKana().setRomaji(romaji);
	}
		
	public void createEmptyPolishSense(Entry entry) {
		
		List<Sense> senseList = entry.getSenseList();
		
		for (Sense sense : senseList) {
			createEmptyPolishSense(sense);
		}
	}
	
	private void createEmptyPolishSense(Sense sense) {
						
		List<Gloss> glossList = sense.getGlossList();
		
		List<Gloss> glossEngList = glossList.stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());

		if (glossEngList.size() == 0) {
			return;
		}			
		
		List<Gloss> newPolishGlossList = new ArrayList<>();
		
		Gloss newPolishGlossStart1 = new Gloss();
		
		newPolishGlossStart1.setLang("pol");
		newPolishGlossStart1.setGType(null);
		newPolishGlossStart1.setValue("-");

		newPolishGlossList.add(newPolishGlossStart1);
		newPolishGlossList.add(newPolishGlossStart1);
		
		//
		
		Gloss newPolishGlossStart2 = new Gloss();
		
		newPolishGlossStart2.setLang("pol");
		newPolishGlossStart2.setGType(null);
		newPolishGlossStart2.setValue("UZUPEŁNIENIE");

		newPolishGlossList.add(newPolishGlossStart2);
		
		boolean glossTypeAnyExists = glossEngList.stream().anyMatch(gloss -> (gloss.getGType() != null));
		
		if (glossTypeAnyExists == true) {
			
			Gloss newPolishGlossTypeInfo = new Gloss();
			
			newPolishGlossTypeInfo.setLang("pol");
			newPolishGlossTypeInfo.setGType(null);
			newPolishGlossTypeInfo.setValue("!!! UWAGA NA DODATKOWY TYP !!!");

			newPolishGlossList.add(newPolishGlossTypeInfo);
		}
		
		//
		
		Gloss newPolishGlossStart3 = new Gloss();
		
		newPolishGlossStart3.setLang("pol");
		newPolishGlossStart3.setGType(null);
		newPolishGlossStart3.setValue("---");

		newPolishGlossList.add(newPolishGlossStart3);
		newPolishGlossList.add(newPolishGlossStart3);
		
		//
		
		for (Gloss currentGlossEng : glossEngList) {
			
			Gloss newPolishGloss = new Gloss();
			
			newPolishGloss.setLang("pol");
			newPolishGloss.setGType(currentGlossEng.getGType());
			newPolishGloss.setValue(currentGlossEng.getValue());
			
			newPolishGlossList.add(newPolishGloss);				
		}
					
		//
		
		glossList.addAll(newPolishGlossList);
		
		//
		
		List<SenseAdditionalInfo> additionalInfoList = sense.getAdditionalInfoList();
		
		List<SenseAdditionalInfo> additionalInfoEngList = additionalInfoList.stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
		
		if (additionalInfoEngList.size() == 0) {
			return;
		}
					
		List<SenseAdditionalInfo> newAdditionalInfoPolishList = new ArrayList<>();
		
		//
		
		SenseAdditionalInfo senseAdditionalInfoStart1 = new SenseAdditionalInfo();
		
		senseAdditionalInfoStart1.setLang("pol");
		senseAdditionalInfoStart1.setValue("-");
		
		newAdditionalInfoPolishList.add(senseAdditionalInfoStart1);
		newAdditionalInfoPolishList.add(senseAdditionalInfoStart1);
		
		SenseAdditionalInfo senseAdditionalInfoStart2 = new SenseAdditionalInfo();
		
		senseAdditionalInfoStart2.setLang("pol");
		senseAdditionalInfoStart2.setValue("UZUPEŁNIENIE");
		
		newAdditionalInfoPolishList.add(senseAdditionalInfoStart2);
		
		//
		
		SenseAdditionalInfo senseAdditionalInfoStart3 = new SenseAdditionalInfo();
		
		senseAdditionalInfoStart3.setLang("pol");
		senseAdditionalInfoStart3.setValue("---");
		
		newAdditionalInfoPolishList.add(senseAdditionalInfoStart3);
		newAdditionalInfoPolishList.add(senseAdditionalInfoStart3);
		
		//
		
		for (SenseAdditionalInfo currentSenseAdditionalInfoEng : additionalInfoEngList) {
			
			SenseAdditionalInfo newSenseAdditionalInfoPolish = new SenseAdditionalInfo();
			
			newSenseAdditionalInfoPolish.setLang("pol");
			newSenseAdditionalInfoPolish.setValue(currentSenseAdditionalInfoEng.getValue());
			
			newAdditionalInfoPolishList.add(newSenseAdditionalInfoPolish);
		}
		
		//
		
		additionalInfoList.addAll(newAdditionalInfoPolishList);
	}
	
	private void readPolishDictionary() throws Exception {
		
		// wczytywanie slownika
		if (polishDictionaryEntryListMap == null) {
			
			System.out.println("Reading polish dictionary file: " + polishDictionaryFile);
			
			polishDictionaryEntryListMap = new LinkedHashMap<>();
			
			List<Entry> polishDictionaryEntryList = readEntryListFromHumanCsv(polishDictionaryFile.getAbsolutePath());
						
			for (Entry entry : polishDictionaryEntryList) {
				polishDictionaryEntryListMap.put(entry.getEntryId(), entry);
			}
		}		
	}
	
	public Entry getEntryFromPolishDictionary(Integer entryId) throws Exception {
		
		readPolishDictionary();
		
		return polishDictionaryEntryListMap.get(entryId);
	}
	
	public void deleteEntryFromPolishDictionary(Integer entryId) throws Exception {
		
		readPolishDictionary();
		
		polishDictionaryEntryListMap.remove(entryId);
	}
	
	public void addEntryToPolishDictionary(Entry newEntry) throws Exception {
		
		readPolishDictionary();
		
		if (polishDictionaryEntryListMap.get(newEntry.getEntryId()) != null) {
			throw new Exception("Can't add already added entry with id: " + newEntry.getEntryId());
		}
				
		polishDictionaryEntryListMap.put(newEntry.getEntryId(), newEntry);
	}
	
	public void updateEntryInPolishDictionary(Entry entry) throws Exception {
		
		readPolishDictionary();
		
		if (polishDictionaryEntryListMap.get(entry.getEntryId()) == null) {
			throw new Exception("Can't update entry with id: " + entry.getEntryId());
		}
		
		polishDictionaryEntryListMap.put(entry.getEntryId(), entry);
	}
	
	public List<JMdict.Entry> getAllPolishDictionaryEntryList() throws Exception {
		
		// wczytywanie slownika
		readPolishDictionary();
		
		return new ArrayList<>(polishDictionaryEntryListMap.values());
	}
	
	public void validateAllPolishDictionaryEntryList() throws Exception {
				
		// wczytywanie slownika
		readPolishDictionary();

		boolean wasError = false;
		
		// hiragana i katakana cache		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : kanaHelper.getAllHiraganaKanaEntries()) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> katakanaCache = new HashMap<String, KanaEntry>();

		for (KanaEntry kanaEntry : kanaHelper.getAllKatakanaKanaEntries()) {
			katakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
				
		final Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache();

		//
		
		// walidacja wpisow
		Set<Integer> alreadyCheckedEntryId = new TreeSet<>();
		
		for (Entry entry : polishDictionaryEntryListMap.values()) {
			
			// sprawdzamy, czy taki wpis juz nie wystepu
			if (alreadyCheckedEntryId.contains(entry.getEntryId()) == true) {
				
				System.out.println("[Error] Duplicate entry id for " + entry.getEntryId());
				
				wasError = true;
				
				continue;
			}
			
			alreadyCheckedEntryId.add(entry.getEntryId());
			
			// walidacja duplikow tlumaczen w jednym sensie
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense currentSense : senseList) {				
				
				// pobieramy wszyskie polskie tlumaczenia z tego sensu
				List<Gloss> glossPolList = currentSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				Set<String> uniqueGlossPolSet = new TreeSet<>();
				List<String> allGlossPolList = new ArrayList<>();
				
				for (Gloss currentGlossPol : glossPolList) {
					uniqueGlossPolSet.add(currentGlossPol.getValue());
					allGlossPolList.add(currentGlossPol.getValue());
				}
				
				if (uniqueGlossPolSet.size() != glossPolList.size()) { // mamy duplikat
					
					Collections.sort(allGlossPolList);
					
					System.out.println("[Error] Sense gloss duplicate for " + entry.getEntryId() + " - " + allGlossPolList);
					
					wasError = true;
				}
			}
			
			// walidacja glossType: jesli jest w angielskim to musi byc w polskim
			for (Sense currentSense : senseList) {
				
				// pobieramy wszyskie polskie tlumaczenia z tego sensu
				List<Gloss> glossEngList = currentSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				List<Gloss> glossPolList = currentSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());

				// wyliczenie liczby glossType dla angielskiej i polskiej wersji
				Set<String> glossTypeForEngList = getGlossTypeListForValidate(glossEngList);
				Set<String> glossTypeForPolList = getGlossTypeListForValidate(glossPolList);
				
				if (	CollectionUtils.containsAll(glossTypeForEngList, glossTypeForPolList) == false ||
						CollectionUtils.containsAll(glossTypeForPolList, glossTypeForEngList) == false) {
					
					System.out.println("[Error] Gloss type list different for " + entry.getEntryId() + " - " + glossTypeForEngList + " vs " + glossTypeForPolList);
					
					wasError = true;
				}
			}
						
			// walidacja romaji
			List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
			
			// walidacja typow hiragana i katakana
			for (ReadingInfo currentReadingInfo : readingInfoList) {
				
				String kana = currentReadingInfo.getKana().getValue().replaceAll("・", "");
				String romaji = currentReadingInfo.getKana().getRomaji();
				
				ReadingInfoKanaType kanaType = currentReadingInfo.getKana().getKanaType();
				
				boolean ignoreError = false;

				KanaWord currentKanaAsKanaAsKanaWord = null;
				
				if (kanaType != ReadingInfoKanaType.HIRAGANA_EXCEPTION && kanaType != ReadingInfoKanaType.KATAKANA_EXCEPTION) {
					
					try {
						currentKanaAsKanaAsKanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaCache, false);
						
					} catch (Exception e) {
						
						System.out.println("[Error] Romaji validate (1) for " + entry.getEntryId() + " - " + kana + " - " + romaji);
						
						wasError = true;
						
						continue;
					}
				}			

				KanaWord kanaWord = createKanaWord(kanaHelper, romaji, kanaType, hiraganaCache, katakanaCache);

				if (kanaWord == null) {
					ignoreError = true;
				}

				if (ignoreError == true || (kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {
					
					kanaWord = createKanaWord(kanaHelper, romaji, kanaType, hiraganaCache, katakanaCache);

					if (ignoreError == true || (kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {

						romaji = romaji.replaceAll(" o ", " wo ");
						romaji = romaji.replaceAll(" e ", " he ");
						
						kanaWord = createKanaWord(kanaHelper, romaji, kanaType, hiraganaCache, katakanaCache);

						if (ignoreError == false && (kana).equals(kanaHelper.createKanaString(kanaWord)) == false) {
							
							System.out.println("[Error] Romaji validate (2) for " + entry.getEntryId() + " - " + kana + " - " + romaji + " - " + kanaHelper.createKanaString(kanaWord));
							
							wasError = true;
							
							continue;
						}
					}
				}

				if (kanaType != ReadingInfoKanaType.HIRAGANA_EXCEPTION && kanaType != ReadingInfoKanaType.KATAKANA_EXCEPTION) {

					String currentKanaAsRomaji = kanaHelper.createRomajiString(currentKanaAsKanaAsKanaWord);
					
					// is hiragana word
					KanaWord currentKanaAsRomajiAsHiraganaWord = kanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, currentKanaAsRomaji);
					String currentKanaAsRomajiAsHiraganaWordAsAgainKana = kanaHelper.createKanaString(currentKanaAsRomajiAsHiraganaWord);

					// is katakana word
					KanaWord currentKanaAsRomajiAsKatakanaWord = kanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, currentKanaAsRomaji);
					String currentKanaAsRomajiAsKatakanaWordAsAgainKana = kanaHelper.createKanaString(currentKanaAsRomajiAsKatakanaWord);

					if (ignoreError == false && kana.equals(currentKanaAsRomajiAsHiraganaWordAsAgainKana) == false
							&& kana.equals(currentKanaAsRomajiAsKatakanaWordAsAgainKana) == false) {
						
						System.out.println("[Error] Romaji validate (3) for " + entry.getEntryId() + " - " + kana + " - " + romaji + " - " + currentKanaAsRomajiAsHiraganaWordAsAgainKana +
								" vs " + currentKanaAsRomajiAsKatakanaWordAsAgainKana);
						
						wasError = true;
						
						continue;
					}
				}

				// walidacja typow hiragana_katakana i katakana_hiragana
				if (kanaType == ReadingInfoKanaType.HIRAGANA_KATAKANA || kanaType == ReadingInfoKanaType.KATAKANA_HIRAGANA) {

					kanaWord = kanaHelper.convertKanaStringIntoKanaWord(kana, kanaCache, false);

					String createdRomaji = kanaHelper.createRomajiString(kanaWord);

					if (romaji.replaceAll(" ", "").equals(createdRomaji) == true) {

						// ok

					} else {

						romaji = romaji.replaceAll(" o ", " wo ");
						romaji = romaji.replaceAll(" e ", " he ");

						if (romaji.replaceAll(" ", "").equals(createdRomaji) == true) {
							// ok 2

						} else {
							
							System.out.println("[Error] Romaji validate (4) for " + entry.getEntryId() + " - " + kana + " - " + romaji);
							
							wasError = true;
							
							continue;
						}
					}		
				}				
			}
		}				
		
		if (wasError == true) { // byl jakis blad			
			throw new Exception("Error");			
		}
	}
	
	private static KanaWord createKanaWord(KanaHelper kanaHelper, String romaji, ReadingInfoKanaType readingInfoKanaType, Map<String, KanaEntry> hiraganaCache,
			Map<String, KanaEntry> katakanaCache) throws Exception {

		KanaWord kanaWord = null;

		if (readingInfoKanaType == ReadingInfoKanaType.HIRAGANA) {
			kanaWord = kanaHelper.convertRomajiIntoHiraganaWord(hiraganaCache, romaji);
			
		} else if (readingInfoKanaType == ReadingInfoKanaType.KATAKANA) {
			kanaWord = kanaHelper.convertRomajiIntoKatakanaWord(katakanaCache, romaji);
			
		} else if (readingInfoKanaType == ReadingInfoKanaType.HIRAGANA_KATAKANA) {
			return null;
			
		} else if (readingInfoKanaType == ReadingInfoKanaType.KATAKANA_HIRAGANA) {
			return null;
			
		} else if (readingInfoKanaType == ReadingInfoKanaType.HIRAGANA_EXCEPTION) {
			return null;
			
		} else if (readingInfoKanaType == ReadingInfoKanaType.KATAKANA_EXCEPTION) {
			return null;
			
		} else {
			throw new RuntimeException("Bad word type");
		}

		if (kanaWord.remaingRestChars.equals("") == false) {
			throw new Exception("Validate error for word: " + romaji + ", remaining: "
					+ kanaWord.remaingRestChars);
		}

		return kanaWord;
	}

	
	private Set<String> getGlossTypeListForValidate(List<Gloss> glossList) {
		
		Set<String> result = new LinkedHashSet<>();
		
		for (Gloss gloss : glossList) {
			
			GTypeEnum glossType = gloss.getGType();
			
			if (glossType == null) {
				result.add("null");
				
			} else {
				result.add(glossType.name());
			}
		}
		
		return result;
	}

	public void fillDataFromOldPolishJapaneseDictionary(Entry entry, EntryAdditionalData entryAdditionalData) throws Exception {
				
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
		
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry);		
		
		if (allPolishJapaneseEntriesForEntry.size() > 0) { // jezeli dany wpis juz jest w starym slowniku, mozemy przetworzyc te dane
			
			// aktualizacja romaji
			List<ReadingInfo> entryReadingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : entryReadingInfoList) {
				
				Optional<PolishJapaneseEntry> polishJapaneseEntryForReadingKanaOptional = allPolishJapaneseEntriesForEntry.stream().filter((p) -> p.getKana().equals(readingInfo.getKana().getValue())).findFirst();
				
				if (polishJapaneseEntryForReadingKanaOptional.isPresent() == true) {
					
					PolishJapaneseEntry polishJapaneseEntryForReadingKana = polishJapaneseEntryForReadingKanaOptional.get();
					
					readingInfo.getKana().setKanaType(ReadingInfoKanaType.fromValue(polishJapaneseEntryForReadingKana.getWordType().name()));
					readingInfo.getKana().setRomaji(polishJapaneseEntryForReadingKana.getRomaji());					
				}				
			}
						
			// istniejace tlumaczenie (przygotowanie danych)
			EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.jmdictEntryAdditionalDataEntryMap.get(entry.getEntryId());
			
			if (entryAdditionalDataEntry == null) {
				entryAdditionalDataEntry = new EntryAdditionalDataEntry();
				
				entryAdditionalData.jmdictEntryAdditionalDataEntryMap.put(entry.getEntryId(), entryAdditionalDataEntry);
			}
			
			entryAdditionalDataEntry.oldPolishJapaneseEntryList = allPolishJapaneseEntriesForEntry;
		}		
	}
	
	private List<PolishJapaneseEntry> getPolishJapaneseEntryListFromOldDictionary(Entry entry, List<KanjiKanaPair> kanjiKanaPairListforEntry) throws Exception {
		
		// wczytanie starego slownika i sche'owanie go		
		Map<String, List<PolishJapaneseEntry>> polishJapaneseEntriesCache = oldWordGeneratorHelper.getPolishJapaneseEntriesCache();
		
		//
				
		// szukamy wszystkich slow ze starego slownika
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = new ArrayList<>();		
				
		for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
			
			// szukamy slowa ze starego slownika
			List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(polishJapaneseEntriesCache, kanjiKanaPair.kanji, kanjiKanaPair.kana);
			
			if (findPolishJapaneseEntryList == null) { // nie znaleziono
				continue;
			}
			
			PolishJapaneseEntry polishJapaneseEntryForKanjiKanaPair = null;
			
			if (findPolishJapaneseEntryList.size() == 1) {
				
				polishJapaneseEntryForKanjiKanaPair = findPolishJapaneseEntryList.get(0);

				// pobieramy identyfikator grupy ze slowa
				Integer polishJapaneseEntryForKanjiKanaPairEntryId = polishJapaneseEntryForKanjiKanaPair.getGroupIdFromJmedictRawDataList();
				
				if (polishJapaneseEntryForKanjiKanaPairEntryId != null && polishJapaneseEntryForKanjiKanaPairEntryId.intValue() != entry.getEntryId().intValue()) { // sprawdzamy grupe
					throw new Exception(kanjiKanaPair.kanji + " - " + kanjiKanaPair.kana); // jezeli to wydarzylo sie, oznacza to, ze dane slowo zmienilo swoja grupe, mozna to poprawic
				}
			}
			
			if (findPolishJapaneseEntryList.size() > 1) { // jezeli mamy kilka takich smaych slow, szukamy tego konkretnego
				
				for (PolishJapaneseEntry currentFindPolishJapaneseEntryList : findPolishJapaneseEntryList) {
					
					if (currentFindPolishJapaneseEntryList.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == true) {
						continue;
					}
					
					if (currentFindPolishJapaneseEntryList.getGroupIdFromJmedictRawDataList() == null) {
						throw new Exception(kanjiKanaPair.kanji + " - " + kanjiKanaPair.kana); // jezeli to wydarzylo sie, oznacza to, ze dane slowo jest potencjalnym duplikatem i powinien zostac recznie usuniety
					}
					
					if (currentFindPolishJapaneseEntryList.getGroupIdFromJmedictRawDataList().intValue() == entry.getEntryId().intValue()) { // mamy kandydata z naszej grup
						polishJapaneseEntryForKanjiKanaPair = currentFindPolishJapaneseEntryList;
						
						break;
					}
				}				
			}
			
			if (polishJapaneseEntryForKanjiKanaPair == null) { // nie udalo sie znalesc slowa w starym slowniku				
				throw new Exception(kanjiKanaPair.kanji + " - " + kanjiKanaPair.kana); // to chyba nigdy nie powinno zdarzyc sie
			}
			
			allPolishJapaneseEntriesForEntry.add(polishJapaneseEntryForKanjiKanaPair);
		}

		return allPolishJapaneseEntriesForEntry;
	}
		
	public boolean isExistsInOldPolishJapaneseDictionary(Entry entry) throws Exception {
		
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
		
		List<PolishJapaneseEntry> polishJapaneseEntryListFromOldDictionary = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry);
		
		if (polishJapaneseEntryListFromOldDictionary != null && polishJapaneseEntryListFromOldDictionary.size() > 0) {
			return true;
			
		} else {
			return false;
			
		}
	}
	
	private List<KanjiKanaPair> getKanjiKanaPairList(Entry entry) {
		
		List<KanjiKanaPair> result = new ArrayList<>();
		
		//
		
		List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
		List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
		
		// jesli nie ma kanji
		if (kanjiInfoList.size() == 0) {
			
			// wszystkie czytania do listy wynikowej
			for (ReadingInfo readingInfo : readingInfoList) {
				
				ReadingInfo.ReNokanji noKanji = readingInfo.getNoKanji();
				
				if (noKanji == null) {
										
					String kana = readingInfo.getKana().getValue();
					String romaji = readingInfo.getKana().getRomaji();
					
					ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();
					
					//
					
					result.add(new KanjiKanaPair(null, kana, kanaType, romaji));
				}
			}
			
		} else {			
			// zlaczenie kanji z kana
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {
				for (ReadingInfo readingInfo : readingInfoList) {
					
					// pobierz kanji
					String kanji = kanjiInfo.getKanji();
											
					ReadingInfo.ReNokanji noKanji = readingInfo.getNoKanji();
					
					// jest pozycja kana nie laczy sie ze znakiem kanji
					if (noKanji != null) { 
						continue;
					}
					
					// pobierz kana
					String kana = readingInfo.getKana().getValue();
					String romaji = readingInfo.getKana().getRomaji();
					
					ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();
					
					List<String> kanjiRestrictedListForKana = readingInfo.getKanjiRestrictionList();
					
					boolean isRestricted = true;
					
					// sprawdzanie, czy dany kana laczy sie z kanji
					if (kanjiRestrictedListForKana.size() == 0) { // brak restrykcji						
						isRestricted = false;
						
					} else { // sa jakies restrykcje, sprawdzamy, czy kanji znajduje sie na tej liscie					
						if (kanjiRestrictedListForKana.contains(kanji) == true) {
							isRestricted = false;
						}							
					}
					
					// to zlaczenie nie znajduje sie na liscie, omijamy je
					if (isRestricted == true) {
						continue; // omijamy to zlaczenie
					}
					
					// mamy pare
					result.add(new KanjiKanaPair(kanji, kana, kanaType, romaji));					
				}				
			}
		}
		
		// szukanie kana z no kanji
		for (ReadingInfo readingInfo : readingInfoList) {
			
			ReadingInfo.ReNokanji noKanji = readingInfo.getNoKanji();
			
			if (noKanji != null) {
								
				String kana = readingInfo.getKana().getValue();
				String romaji = readingInfo.getKana().getRomaji();
				
				ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();
				
				//
				
				result.add(new KanjiKanaPair(null, kana, kanaType, romaji));
			}
		}
		
		// dopasowanie listy sense do danego kanji i kana
		List<Sense> entrySenseList = entry.getSenseList();
				
		for (KanjiKanaPair kanjiKanaPair : result) {
			
			String kanji = kanjiKanaPair.kanji;
			String kana = kanjiKanaPair.kana;
			
			// chodzimy po wszystkich sense i sprawdzamy, czy mozemy je dodac do naszej pary kanji i kana
			for (Sense sense : entrySenseList) {
				
				boolean isKanjiRestricted = true;
				
				if (sense.getRestrictedToKanjiList().size() == 0) {
					isKanjiRestricted = false;
					
				} else {
					if (sense.getRestrictedToKanjiList().contains(kanji) == true) {
						isKanjiRestricted = false;
					}
				}
				
				if (isKanjiRestricted == true) {
					continue; // ten sens nie bedzie wchodzil w sklad tej pary
				}	
				
				boolean isKanaRestricted = true;
				
				if (sense.getRestrictedToKanaList().size() == 0) {
					isKanaRestricted = false;
					
				} else {
					if (sense.getRestrictedToKanaList().contains(kana) == true) {
						isKanaRestricted = false;
					}
				}
				
				if (isKanaRestricted == true) {
					continue; // ten sens nie bedzie wchodzil w sklad tej pary
				}
				
				// dodajemy ten sens do danej pary				
				kanjiKanaPair.getSenseList().add(sense);
			}
		}
		
		return result;
	}
	
	public List<PolishJapaneseEntry> updatePolishJapaneseEntryInOldDictionary(Entry entry) throws Exception {

		// lista nowych wpisow do dodania do starego slownika
		List<PolishJapaneseEntry> newOldPolishJapaneseEntryList = new ArrayList<>();
		
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
				
		// pobieramy liste 
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry);		

		// chodzenie po wszystkich kombinacjach kanji i kana
		for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
			
			// szukamy docelowego slowka
			Optional<PolishJapaneseEntry> polishJapaneseEntryOptional = allPolishJapaneseEntriesForEntry.stream().filter(polishJapaneseEntry -> {
				
				String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
				String polishJapaneseEntryKana = polishJapaneseEntry.getKana();
				
				String searchKanji = kanjiKanaPair.kanji;
				
				if (searchKanji == null) {
					searchKanji = "-";
				}
				
				String searchKana = kanjiKanaPair.kana;
				
				return polishJapaneseEntryKanji.equals(searchKanji) == true && polishJapaneseEntryKana.equals(searchKana) == true;				
			}).findFirst();
			
			PolishJapaneseEntry polishJapaneseEntry;
			
			if (polishJapaneseEntryOptional.isPresent() == false) { // tego elementu nie ma w starym slowniku, generowanie elementu
				
				System.out.println("[Warning] Can't find polish japanese entry for " + entry.getEntryId() + " - " + kanjiKanaPair.kanji + " - " + kanjiKanaPair.kana);
				
				polishJapaneseEntry = generateNewEmptyOldPolishJapaneseEntry(kanjiKanaPair);
				
				newOldPolishJapaneseEntryList.add(polishJapaneseEntry);
								
			} else {
				polishJapaneseEntry = polishJapaneseEntryOptional.get();
			}
						
			//
			
			// pobieranie wszystkich znaczen
			List<Sense> kanjiKanaPairSenseList = kanjiKanaPair.getSenseList();
			
			// czesc wspolna	
			
			Collection<FieldEnum> fieldCommonList = null;
			Collection<MiscEnum> miscCommonList = null;
			Collection<DialectEnum> dialectCommonList = null;
			Collection<String> languageSourceCommonList = null;
			Collection<String> additionalInfoCommonList = null;
			
			// generowanie wspolnej czesci dla wszystkich znaczen
			for (Sense currentSense : kanjiKanaPairSenseList) {

				if (fieldCommonList == null) {
					fieldCommonList = currentSense.getFieldList();
					
				} else {
					fieldCommonList = CollectionUtils.intersection(fieldCommonList, currentSense.getFieldList());
				}
				
				//
				
				if (miscCommonList == null) {
					miscCommonList = currentSense.getMiscList();
					
				} else {
					miscCommonList = CollectionUtils.intersection(miscCommonList, currentSense.getMiscList());
				}
				
				//
				
				if (dialectCommonList == null) {
					dialectCommonList = currentSense.getDialectList();
					
				} else {
					dialectCommonList = CollectionUtils.intersection(dialectCommonList, currentSense.getDialectList());
				}	
				
				//
				
				if (languageSourceCommonList == null) {
					languageSourceCommonList = translateToPolishLanguageSourceList(currentSense.getLanguageSourceList());
					
				} else {
					languageSourceCommonList = CollectionUtils.intersection(languageSourceCommonList, translateToPolishLanguageSourceList(currentSense.getLanguageSourceList()));
				}
				
				//
				
				List<SenseAdditionalInfo> additionalPolInfoList = currentSense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				if (additionalInfoCommonList == null) {
					additionalInfoCommonList = translateToPolishSenseAdditionalInfoList(additionalPolInfoList);
					
				} else {
					additionalInfoCommonList = CollectionUtils.intersection(additionalInfoCommonList, translateToPolishSenseAdditionalInfoList(additionalPolInfoList));
				}
			}
						
			// generowanie docelowego tlumaczenia i info dla starej pozycji w starym slowniku
			List<String> newPolishTranslateList = new ArrayList<>();			
			List<String> newPolishAdditionalInfoList = new ArrayList<>();
			
			for (Sense currentSense : kanjiKanaPairSenseList) {
				
				List<SenseAdditionalInfo> additionalPolInfoList = currentSense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				//

				List<FieldEnum> currentSenseFieldList = currentSense.getFieldList();
				List<MiscEnum> currentSenseMiscList = currentSense.getMiscList();
				List<DialectEnum> currentSenseDialectList = currentSense.getDialectList();
				List<String> currentSenseLanguageSourceList = translateToPolishLanguageSourceList(currentSense.getLanguageSourceList());
				List<String> currentSenseAdditionalInfoList = translateToPolishSenseAdditionalInfoList(additionalPolInfoList);
				
				// wyliczenie roznic miedzy obecnym znaczeniem, a czescia wspolna dla wszystkich znaczen
				Collection<FieldEnum> fieldEnumListUniqueForCurrentSense = CollectionUtils.subtract(currentSenseFieldList, fieldCommonList);
				Collection<MiscEnum> miscEnumListUniqueForCurrentSense = CollectionUtils.subtract(currentSenseMiscList, miscCommonList);
				Collection<DialectEnum> dialectEnumListUniqueForCurrentSense = CollectionUtils.subtract(currentSenseDialectList, dialectCommonList);
				Collection<String> languageSourceListUniqueForCurrentSense = CollectionUtils.subtract(currentSenseLanguageSourceList, languageSourceCommonList);
				Collection<String> senseAdditionalInfoListUniqueForCurrentSense = CollectionUtils.subtract(currentSenseAdditionalInfoList, additionalInfoCommonList);
				
				//
				
				List<Gloss> glossPolList = currentSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				for (Gloss currentPolGloss : glossPolList) {
					
					String currentPolGlossType = translateToPolishGlossType(currentPolGloss.getGType());
					String currentPolGlossValue = currentPolGloss.getValue();
					
					//
					
					List<String> currentPolGlossPolishTranslate = new ArrayList<>();
					
					// dodajemy tlumaczenie
					currentPolGlossPolishTranslate.add(currentPolGlossValue);
					
					// podtyp tlumaczenia
					if (currentPolGlossType != null) {
						currentPolGlossPolishTranslate.add(currentPolGlossType);
					}
					
					// dziedzina
					if (fieldEnumListUniqueForCurrentSense.size() > 0) {
						currentPolGlossPolishTranslate.addAll(translateToPolishFieldEnumList(fieldEnumListUniqueForCurrentSense));						
					}
					
					// rozne informacje
					if (miscEnumListUniqueForCurrentSense.size() > 0) {
						currentPolGlossPolishTranslate.addAll(translateToPolishMiscEnumList(miscEnumListUniqueForCurrentSense));
					}
					
					// dialekt
					if (dialectEnumListUniqueForCurrentSense.size() > 0) {
						currentPolGlossPolishTranslate.addAll(translateToPolishDialectEnumList(dialectEnumListUniqueForCurrentSense));
					}
					
					// informacje dodatkowe dla znaczenia
					if (senseAdditionalInfoListUniqueForCurrentSense.size() > 0) {
						currentPolGlossPolishTranslate.addAll(senseAdditionalInfoListUniqueForCurrentSense);
					}
					
					// jezyk zrodlowy
					if (languageSourceListUniqueForCurrentSense.size() > 0) {
						currentPolGlossPolishTranslate.addAll(languageSourceListUniqueForCurrentSense);
					}
					
					// generowanie tlumaczenia dla slowka
					
					// nowa pozycja w tlumaczeniu
					newPolishTranslateList.add(joinStringForOldPolishJapaneseEntry(currentPolGlossPolishTranslate, true));
				}
			}
			
			// informacje dodatkowe
						
			// dziedzina
			if (fieldCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishFieldEnumList(fieldCommonList));						
			}
			
			// rozne informacje
			if (miscCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishMiscEnumList(miscCommonList));
			}
			
			// dialekt
			if (dialectCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishDialectEnumList(dialectCommonList));
			}
			
			// informacje dodatkowe dla znaczenia
			if (additionalInfoCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(additionalInfoCommonList);
			}
			
			// jezyk zrodlowy
			if (languageSourceCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(languageSourceCommonList);
			}

			String newPolishAdditionalInfo = joinStringForOldPolishJapaneseEntry(newPolishAdditionalInfoList, false);

			// aktualizacja wpisu
			polishJapaneseEntry.setWordType(WordType.valueOf(kanjiKanaPair.kanaType.name()));			
			polishJapaneseEntry.setRomaji(kanjiKanaPair.romaji);
			
			polishJapaneseEntry.setTranslates(newPolishTranslateList);
			polishJapaneseEntry.setInfo(newPolishAdditionalInfo);
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
				polishJapaneseEntry.getParseAdditionalInfoList().add(ParseAdditionalInfo.DICTIONARY2_SOURCE);
			}
			
			// generowanie chudego GroupId
			polishJapaneseEntry.setJmedictRawDataList(Arrays.asList("GroupId: " + entry.getEntryId()));			
		}
		
		return newOldPolishJapaneseEntryList;
	}
	
	private PolishJapaneseEntry generateNewEmptyOldPolishJapaneseEntry(KanjiKanaPair kanjiKanaPair) throws Exception {
		
		// mapa z typami i ich odpowiednika w encjach
		Map<String, String> partOfSpeechValueAndEntityMap = new TreeMap<>();
		
		partOfSpeechValueAndEntityMap.put("noun or verb acting prenominally", "adj-f");
		partOfSpeechValueAndEntityMap.put("adjective (keiyoushi)", "adj-i");
		partOfSpeechValueAndEntityMap.put("adjective (keiyoushi) - yoi/ii class", "adj-ix");
		partOfSpeechValueAndEntityMap.put("'kari' adjective (archaic)", "adj-kari");
		partOfSpeechValueAndEntityMap.put("'ku' adjective (archaic)", "adj-ku");
		partOfSpeechValueAndEntityMap.put("adjectival nouns or quasi-adjectives (keiyodoshi)", "adj-na");
		partOfSpeechValueAndEntityMap.put("archaic/formal form of na-adjective", "adj-nari");
		partOfSpeechValueAndEntityMap.put("nouns which may take the genitive case particle 'no'", "adj-no");
		partOfSpeechValueAndEntityMap.put("pre-noun adjectival (rentaishi)", "adj-pn");
		partOfSpeechValueAndEntityMap.put("'shiku' adjective (archaic)", "adj-shiku");
		partOfSpeechValueAndEntityMap.put("'taru' adjective", "adj-t");
		partOfSpeechValueAndEntityMap.put("adverb (fukushi)", "adv");
		partOfSpeechValueAndEntityMap.put("adverb taking the 'to' particle", "adv-to");
		partOfSpeechValueAndEntityMap.put("auxiliary", "aux");
		partOfSpeechValueAndEntityMap.put("auxiliary adjective", "aux-adj");
		partOfSpeechValueAndEntityMap.put("auxiliary verb", "aux-v");
		partOfSpeechValueAndEntityMap.put("conjunction", "conj");
		partOfSpeechValueAndEntityMap.put("copula", "cop");
		partOfSpeechValueAndEntityMap.put("counter", "ctr");
		partOfSpeechValueAndEntityMap.put("expressions (phrases, clauses, etc.)", "exp");
		partOfSpeechValueAndEntityMap.put("interjection (kandoushi)", "int");
		partOfSpeechValueAndEntityMap.put("noun (common) (futsuumeishi)", "n");
		partOfSpeechValueAndEntityMap.put("adverbial noun (fukushitekimeishi)", "n-adv");
		partOfSpeechValueAndEntityMap.put("proper noun", "n-pr");
		partOfSpeechValueAndEntityMap.put("noun, used as a prefix", "n-pref");
		partOfSpeechValueAndEntityMap.put("noun, used as a suffix", "n-suf");
		partOfSpeechValueAndEntityMap.put("noun (temporal) (jisoumeishi)", "n-t");
		partOfSpeechValueAndEntityMap.put("numeric", "num");
		partOfSpeechValueAndEntityMap.put("pronoun", "pn");
		partOfSpeechValueAndEntityMap.put("prefix", "pref");
		partOfSpeechValueAndEntityMap.put("particle", "prt");
		partOfSpeechValueAndEntityMap.put("suffix", "suf");
		partOfSpeechValueAndEntityMap.put("unclassified", "unc");
		partOfSpeechValueAndEntityMap.put("verb unspecified", "v-unspec");
		partOfSpeechValueAndEntityMap.put("Ichidan verb", "v1");
		partOfSpeechValueAndEntityMap.put("Ichidan verb - kureru special class", "v1-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb with 'u' ending (archaic)", "v2a-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'bu' ending (archaic)", "v2b-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'bu' ending (archaic)", "v2b-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'dzu' ending (archaic)", "v2d-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'dzu' ending (archaic)", "v2d-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'gu' ending (archaic)", "v2g-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'gu' ending (archaic)", "v2g-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'hu/fu' ending (archaic)", "v2h-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'hu/fu' ending (archaic)", "v2h-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'ku' ending (archaic)", "v2k-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'ku' ending (archaic)", "v2k-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'mu' ending (archaic)", "v2m-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'mu' ending (archaic)", "v2m-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'nu' ending (archaic)", "v2n-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'ru' ending (archaic)", "v2r-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'ru' ending (archaic)", "v2r-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'su' ending (archaic)", "v2s-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'tsu' ending (archaic)", "v2t-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'tsu' ending (archaic)", "v2t-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'u' ending and 'we' conjugation (archaic)", "v2w-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (upper class) with 'yu' ending (archaic)", "v2y-k");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'yu' ending (archaic)", "v2y-s");
		partOfSpeechValueAndEntityMap.put("Nidan verb (lower class) with 'zu' ending (archaic)", "v2z-s");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'bu' ending (archaic)", "v4b");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'gu' ending (archaic)", "v4g");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'hu/fu' ending (archaic)", "v4h");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'ku' ending (archaic)", "v4k");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'mu' ending (archaic)", "v4m");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'nu' ending (archaic)", "v4n");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'ru' ending (archaic)", "v4r");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'su' ending (archaic)", "v4s");
		partOfSpeechValueAndEntityMap.put("Yodan verb with 'tsu' ending (archaic)", "v4t");
		partOfSpeechValueAndEntityMap.put("Godan verb - -aru special class", "v5aru");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'bu' ending", "v5b");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'gu' ending", "v5g");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'ku' ending", "v5k");
		partOfSpeechValueAndEntityMap.put("Godan verb - Iku/Yuku special class", "v5k-s");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'mu' ending", "v5m");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'nu' ending", "v5n");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'ru' ending", "v5r");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'ru' ending (irregular verb)", "v5r-i");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'su' ending", "v5s");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'tsu' ending", "v5t");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'u' ending", "v5u");
		partOfSpeechValueAndEntityMap.put("Godan verb with 'u' ending (special class)", "v5u-s");
		partOfSpeechValueAndEntityMap.put("Godan verb - Uru old class verb (old form of Eru)", "v5uru");
		partOfSpeechValueAndEntityMap.put("intransitive verb", "vi");
		partOfSpeechValueAndEntityMap.put("Kuru verb - special class", "vk");
		partOfSpeechValueAndEntityMap.put("irregular nu verb", "vn");
		partOfSpeechValueAndEntityMap.put("irregular ru verb, plain form ends with -ri", "vr");
		partOfSpeechValueAndEntityMap.put("noun or participle which takes the aux. verb suru", "vs");
		partOfSpeechValueAndEntityMap.put("su verb - precursor to the modern suru", "vs-c");
		partOfSpeechValueAndEntityMap.put("suru verb - included", "vs-i");
		partOfSpeechValueAndEntityMap.put("suru verb - special class", "vs-s");
		partOfSpeechValueAndEntityMap.put("transitive verb", "vt");
		partOfSpeechValueAndEntityMap.put("Ichidan verb - zuru verb (alternative form of -jiru verbs)", "vz");
				
		// generowanie wpisu
		
		PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
		
		polishJapaneseEntry.setId(-1);
		
		List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		for (Sense sense : kanjiKanaPair.senseList) {
			
			List<PartOfSpeechEnum> partOfSpeechList = sense.getPartOfSpeechList();
			
			for (PartOfSpeechEnum partOfSpeechEnum : partOfSpeechList) {
				
				String partOfSpeechEnumAsEntity = partOfSpeechValueAndEntityMap.get(partOfSpeechEnum.value());
				
				if (partOfSpeechEnumAsEntity == null) {
					throw new RuntimeException("partOfSpeechEnumAsEntity: " + partOfSpeechEnumAsEntity);
				}
				
				DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(partOfSpeechEnumAsEntity);
				
				if (dictionaryEntryType != null && dictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
					dictionaryEntryTypeList.add(dictionaryEntryType);
				}				
			}			
		}
		
		// mala popraweczka kolejnosci typow
		if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true && 
				dictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_VERB_U) {
			
			dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_VERB_U);
			
			dictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_VERB_U);
		}
		
		if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true && 
				dictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_VERB_RU) {
			
			dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_VERB_RU);
			
			dictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_VERB_RU);
		}

		if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true && 
				dictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_VERB_IRREGULAR) {
			
			dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_VERB_IRREGULAR);
			
			dictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_VERB_IRREGULAR);
		}
		
		if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_I) == true && 
				dictionaryEntryTypeList.get(0) != DictionaryEntryType.WORD_ADJECTIVE_I) {
			
			dictionaryEntryTypeList.remove(DictionaryEntryType.WORD_ADJECTIVE_I);
			
			dictionaryEntryTypeList.add(0, DictionaryEntryType.WORD_ADJECTIVE_I);
		}
								
		polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);
		
		polishJapaneseEntry.setAttributeList(new AttributeList());
		
		polishJapaneseEntry.setWordType(WordType.HIRAGANA); // zaraz wpisze sie poprawna wartosc
		
		polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
		
		String kanji = kanjiKanaPair.kanji;
		String kana = kanjiKanaPair.kana;
		
		if (kanji == null || kanji.equals("") == true) {
			kanji = "-";
		}
		
		polishJapaneseEntry.setKanji(kanji);
		polishJapaneseEntry.setKana(kana);
		polishJapaneseEntry.setRomaji(""); // zaraz wpisze sie poprawna wartosc
		
		polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());
		polishJapaneseEntry.setParseAdditionalInfoList(new ArrayList<>());				
		
		polishJapaneseEntry.setTranslates(new ArrayList<String>());		
		polishJapaneseEntry.setInfo("");		
		
		polishJapaneseEntry.setJmedictRawDataList(new ArrayList<String>()); // zaraz wpisze sie poprawna wartosc
				
		return polishJapaneseEntry;
	}

	private List<String> translateToPolishFieldEnumList(Collection<FieldEnum> fieldEnumList) {
		
		List<String> result = new ArrayList<>();

		for (FieldEnum fieldEnum : fieldEnumList) {
			
			switch (fieldEnum) {
			
			case COMPUTING:
				result.add("informatyka"); break;
			
			case SUMO:
				result.add("sumo"); break;
				
			case SPORTS:
				result.add("sport"); break;
				
			case LINGUISTICS:
				result.add("lingwistyka"); break;
				
			case ASTRONOMY:
				result.add("astronomia"); break;
				
			case BUDDHISM:
				result.add("buddyzm"); break;
				
			case MATHEMATICS:
				result.add("matematyka"); break;
				
			case MUSIC:
				result.add("muzyka"); break;
				
			case MARTIAL_ARTS:
				result.add("sztuki walki"); break;
				
			case MEDICINE:
				result.add("medycyna"); break;
				
			case BASEBALL:
				result.add("baseball"); break;
				
			case PHYSICS:
				result.add("fizyka"); break;
				
			case BIOLOGY:
				result.add("biologia"); break;
				
			case FOOD_COOKING:
				result.add("jedzenie, gotowanie"); break;
				
			case ARCHEOLOGY:
				result.add("archeologia"); break;
				
			case CHRISTIANITY:
				result.add("chrześcijaństwo"); break;
				
			case CHEMISTRY:
				result.add("chemia"); break;
			
			case GEOLOGY:
				result.add("geologia"); break;
				
			case ANATOMY:
				result.add("anatomia"); break;
				
			case BOTANY:
				result.add("botanika"); break;
				
			case MAHJONG:
				result.add("mahjong"); break;
				
			case MILITARY:
				result.add("wojskowość"); break;
				
			case LAW:
				result.add("prawo"); break;
				
			case FINANCE:
				result.add("finanse"); break;
				
			case ZOOLOGY:
				result.add("zoologia"); break;
				
			case ECONOMICS:
				result.add("ekonomia"); break;
				
			case SHOGI:
				result.add("shogi"); break;
				
			case PHARMACY:
				result.add("farmacja"); break;
				
			case STATISTICS:
				result.add("statystyka"); break;
				
			default:
				throw new RuntimeException("Unknown field enum: " + fieldEnum);
			
			}
		}
		
		return result;
	}

	private List<String> translateToPolishMiscEnumList(Collection<MiscEnum> miscEnumList) {
		
		List<String> result = new ArrayList<>();

		for (MiscEnum miscEnum : miscEnumList) {
			
			switch (miscEnum) {
			
			case WORD_USUALLY_WRITTEN_USING_KANA_ALONE:
				result.add("pisanie zwykle z użyciem kana"); break;			
			
			case OBSCURE_TERM:
				result.add("mało znane słowo"); break;
				
			case YOJIJUKUGO:
				result.add("słowo składające się z czterech znaków"); break;
				
			case ABBREVIATION:
				result.add("skrót"); break;
				
			case COLLOQUIALISM:
				result.add("kolokwializm"); break;
				
			case DEROGATORY:
				result.add("poniżająco"); break;
				
			case SLANG:
				result.add("slang"); break;
			
			case ONOMATOPOEIC_OR_MIMETIC_WORD:
				result.add("onomatopeiczne lub mimetyczne słowo"); break; 
				
			case IDIOMATIC_EXPRESSION:
				result.add("wyrażenie idiomatyczne"); break;
				
			case ARCHAISM:
				result.add("archaizm"); break;
				
			case HONORIFIC_OR_RESPECTFUL_SONKEIGO_LANGUAGE:
				result.add("honoryfikatywnie"); break;
				
			case OBSOLETE_TERM:
				result.add("przestarzałe słowo"); break;
				
			case JOCULAR_HUMOROUS_TERM:
				result.add("żartobliwie"); break;
				
			case PROVERB:
				result.add("przysłowie"); break;
				
			case VULGAR_EXPRESSION_OR_WORD:
				result.add("wulgarnie"); break;
				
			case MANGA_SLANG:
				result.add("slang mangowy"); break;
				
			case LITERARY_OR_FORMAL_TERM:
				result.add("literacki lub formalny termin"); break;
				
			case HISTORICAL_TERM:
				result.add("termin historyczny"); break;
				
			case CHILDREN_S_LANGUAGE:
				result.add("język dzieci"); break;
				
			case POLITE_TEINEIGO_LANGUAGE:
				result.add("uprzejmie"); break;
				
			case FAMILIAR_LANGUAGE:
				result.add("język poufały"); break;
				
			case MALE_TERM_OR_LANGUAGE:
				result.add("język męski"); break;
				
			case DATED_TERM:
				result.add("przestarzałe słowo"); break;
				
			default:
				throw new RuntimeException("Unknown misc enum: " + miscEnum);
			
			}
		}
		
		return result;
	}
	
	private List<String> translateToPolishDialectEnumList(Collection<DialectEnum> dialectEnumList) {
		
		List<String> result = new ArrayList<>();

		for (DialectEnum dialectEnum : dialectEnumList) {
			
			switch (dialectEnum) {
			
			case KANSAI_BEN:
				result.add("dialekt Kansai"); break;
				
			case HOKKAIDO_BEN:
				result.add("dialekt Hokkaido"); break;
				
			case RYUUKYUU_BEN:
				result.add("dialekt Ryuukyuu"); break;
				
			case KYUUSHUU_BEN:
				result.add("dialekt Kyuushuu"); break;
				
			default:
				throw new RuntimeException("Unknown dialect enum: " + dialectEnum);
				
			}
		}
		
		return result;
	}
	
	private List<String> translateToPolishLanguageSourceList(Collection<LanguageSource> languageSourceList) {
		
		List<String> result = new ArrayList<>();
		
		for (LanguageSource languageSource : languageSourceList) {
			
			String languageCodeInPolish = translateToPolishLanguageCode(languageSource.getLang());
			String languageValue = languageSource.getValue();
			
			if (StringUtils.isBlank(languageValue) == false) {
				result.add(languageCodeInPolish + ": " + languageValue);
				
			} else {
				result.add(translateToPolishLanguageCodeWithoutValue(languageSource.getLang()));
			}
		}
		
		return result;
	}
	
	private List<String> translateToPolishSenseAdditionalInfoList(List<SenseAdditionalInfo> additionalPolInfoList) {
				
		List<String> result = new ArrayList<>();
		
		for (SenseAdditionalInfo senseAdditionalInfo : additionalPolInfoList) {
			result.add(senseAdditionalInfo.getValue());
		}
		
		return result;
	}

	
	private String translateToPolishLanguageCode(String language) {
		
		switch (language) {
		
		case "eng":
			return "ang";
			
		case "ita":
			return "wło";
			
		case "ger":
			return "niem";
			
		case "por":
			return "por";
			
		case "rus":
			return "ros";
			
		case "fre":
			return "fra";
			
		case "lat":
			return "łać";
			
		case "dut":
			return "hol";
			
			default:
				throw new RuntimeException("Unknown language: " + language);
		}
	}
	
	private String translateToPolishLanguageCodeWithoutValue(String language) {
		
		switch (language) {
		
		case "eng":
			return "słowo pochodzenia angielskiego";
			
		case "ita":
			return "słowo pochodzenia włoskiego";
			
		case "ger":
			return "słowo pochodzenia niemieckiego";
			
		case "por":
			return "słowo pochodzenia portugalskiego";
			
		case "rus":
			return "słowo pochodzenia rosyjskiego";
			
		case "fre":
			return "słowo pochodzenia francuskiego";
			
		case "lat":
			return "słowo pochodzenia łacińskiego";
			
		case "dut":
			return "słowo pochodzenia holenderskiego";
			
			default:
				throw new RuntimeException("Unknown language: " + language);
		}
	}
	
	private String translateToPolishGlossType(GTypeEnum glossType) {
		
		if (glossType == null) {
			return null;
		}
		
		switch (glossType) {
		
		case EXPL:
			return "wyrażenie";
			
		case FIG:
			return "przenośna";
			
		case LIT:
			return "literacko";
		
			default:
				throw new RuntimeException("Unknown gloss type: " + glossType);
		}
	}
	
	private String joinStringForOldPolishJapaneseEntry(List<String> list, boolean addBracket) {
		
		StringBuffer result = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			
			String currentListElement = list.get(idx);
			
			if (idx == 0) {
				result.append(currentListElement);
				
			} else if (idx == 1 && addBracket == true) {
				result.append(" (");
				result.append(currentListElement);
				
			} else if (idx >= 2 || addBracket == false) {
				result.append("; ");
				result.append(currentListElement);
				
			} else {
				throw new RuntimeException();
			}
			
			if (idx != 0 && idx == list.size() - 1 && addBracket == true) {
				result.append(")");
			}
		}

		return result.toString();
	}
	
	public List<PolishJapaneseEntry> getOldPolishJapaneseEntriesList() throws Exception {
		return oldWordGeneratorHelper.getPolishJapaneseEntriesList();
	}
	
	public boolean updatePolishJapaneseEntry(Entry polishJapaneseEntry, Entry jmdictEntry, EntryAdditionalData entryAdditionalData) {
		
		boolean needManuallyChange = false;
		
		// kanji mozna zaktualizowac bezwarunkowo		
		polishJapaneseEntry.getKanjiInfoList().clear();
		
		polishJapaneseEntry.getKanjiInfoList().addAll(jmdictEntry.getKanjiInfoList());
		
		// aktualizacja czytania
		List<ReadingInfo> polishJapaneseEntryReadingInfoList = new ArrayList<>(polishJapaneseEntry.getReadingInfoList());
		
		List<ReadingInfo> jmdictEntryReadingInfoList = jmdictEntry.getReadingInfoList();
	
		// czyscimy stare czytania
		polishJapaneseEntry.getReadingInfoList().clear();
		
		// chodzimy po nowych czytaniach
		for (ReadingInfo jmdictEntryReadingInfo : jmdictEntryReadingInfoList) {
			
			// szukamy odpowiednika w polskiej wersji
			Optional<ReadingInfo> readingInfoInPolishJapaneseEntryOptional = polishJapaneseEntryReadingInfoList.stream().filter((r) -> r.getKana().getValue().equals(jmdictEntryReadingInfo.getKana().getValue())).findFirst();
			
			if (readingInfoInPolishJapaneseEntryOptional.isPresent() == true) {
				
				ReadingInfo readingInfoInPolishJapaneseEntry = readingInfoInPolishJapaneseEntryOptional.get();
				
				// aktualizujemy je z polskiego czytania
				jmdictEntryReadingInfo.getKana().setKanaType(readingInfoInPolishJapaneseEntry.getKana().getKanaType());
				jmdictEntryReadingInfo.getKana().setRomaji(readingInfoInPolishJapaneseEntry.getKana().getRomaji());
				
			} else {
				
				needManuallyChange = true;
				
				// generujemy kana type i romaji
				generateKanaTypeAndRomaji(jmdictEntryReadingInfo, true);
			}
			
			// i dodajemy
			polishJapaneseEntry.getReadingInfoList().add(jmdictEntryReadingInfo);			
		}		
		
		// aktualizacja sense
		List<Sense> polishJapaneseEntrySenseList = new ArrayList<>(polishJapaneseEntry.getSenseList());
		List<Sense> jmdictEntrySenseList = jmdictEntry.getSenseList();
		
		// czyscimy stary sense
		polishJapaneseEntry.getSenseList().clear();
		
		// 
		int maxSenseLength = jmdictEntrySenseList.size();
		
		if (polishJapaneseEntrySenseList.size() > maxSenseLength) {
			maxSenseLength = polishJapaneseEntrySenseList.size();
		}
			
		for (int senseIdx = 0; senseIdx < maxSenseLength; ++senseIdx) {
			
			// stary sense
			Sense polishJapaneseEntrySense = senseIdx < polishJapaneseEntrySenseList.size() ? polishJapaneseEntrySenseList.get(senseIdx) : null;

			List<Gloss> polishJapaneseEntrySenseGlossEngList = null;
			List<SenseAdditionalInfo> polishJapaneseEntrySenseAdditionalInfoEngList = null;
			
			// bierzmy angielskie tlumaczenia i informacje dodatkowe ze starego sense
			if (polishJapaneseEntrySense != null) {
				
				polishJapaneseEntrySenseGlossEngList = polishJapaneseEntrySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				polishJapaneseEntrySenseAdditionalInfoEngList = polishJapaneseEntrySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
				
			}
			
			// liczymy hash dla znaczenia
			String polishJapaneseEntrySenseHash = getHashForLanguageSourceAdditionalInfoAndGlossListInSenseList(polishJapaneseEntrySense, polishJapaneseEntrySenseGlossEngList, polishJapaneseEntrySenseAdditionalInfoEngList);
													
			// nowy sens
			Sense jmdictEntrySense = senseIdx < jmdictEntrySenseList.size() ? jmdictEntrySenseList.get(senseIdx) : null;
			
			List<Gloss> jmdictEntrySenseGlossEngList = null;
			List<SenseAdditionalInfo> jmdictEntrySenseAdditionalInfoEngList = null;
			
			if (jmdictEntrySense != null) {
				
				// bierzmy angielskie tlumaczenia i informacje dodatkowe z nowego sense
				jmdictEntrySenseGlossEngList = jmdictEntrySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				jmdictEntrySenseAdditionalInfoEngList = jmdictEntrySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
				
			}	
			
			// liczymy hash dla znaczenia
			String jmdictEntrySenseHash = getHashForLanguageSourceAdditionalInfoAndGlossListInSenseList(jmdictEntrySense, jmdictEntrySenseGlossEngList, jmdictEntrySenseAdditionalInfoEngList);
			
			//
			
			if (polishJapaneseEntrySense == null && jmdictEntrySense == null) {
				throw new RuntimeException(); // to nigdy nie powinno zdarzyc sie
			}
			
			// porownujemy hash
			if (polishJapaneseEntrySenseHash.equals(jmdictEntrySenseHash) == true) { // hash ten sam, nie bylo zmiany znaczenia w nowym slowniku
								
				// bierzemy nowy sense + aktualizuje polskie znaczenie
				List<Gloss> polishJapaneseEntrySenseGlossPolList = polishJapaneseEntrySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
				List<SenseAdditionalInfo> polishJapaneseEntrySenseAdditionalInfoPolList = polishJapaneseEntrySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				jmdictEntrySense.getGlossList().addAll(polishJapaneseEntrySenseGlossPolList);
				jmdictEntrySense.getAdditionalInfoList().addAll(polishJapaneseEntrySenseAdditionalInfoPolList);
				
				polishJapaneseEntry.getSenseList().add(jmdictEntrySense);
				
			} else { // jest jakas zmiana
				
				needManuallyChange = true;
				
				// uzupelniamy o puste polskie tlumaczenie (jesli istnieje)				
				if (jmdictEntrySense != null) {					
					createEmptyPolishSense(jmdictEntrySense);					
				}
									
				// uzupelniamy o stare tlumaczenie
				if (polishJapaneseEntrySense != null) {

					// bierzemy stare polskie znaczenia
					List<Gloss> polishJapaneseEntrySenseGlossPolList = polishJapaneseEntrySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
					List<SenseAdditionalInfo> polishJapaneseEntrySenseAdditionalInfoPolList = polishJapaneseEntrySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
					
					// tworzenie struktury pomocniczej
					EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.jmdictEntryAdditionalDataEntryMap.get(jmdictEntry.getEntryId());
					
					if (entryAdditionalDataEntry == null) {
						
						entryAdditionalDataEntry = new EntryAdditionalDataEntry();
						
						entryAdditionalData.jmdictEntryAdditionalDataEntryMap.put(jmdictEntry.getEntryId(), entryAdditionalDataEntry);
					}
					
					if (jmdictEntrySense != null) {
												
						if (entryAdditionalDataEntry.updateDictionarySenseMap == null) {		
							entryAdditionalDataEntry.updateDictionarySenseMap = new TreeMap<>();
						}
						
						entryAdditionalDataEntry.updateDictionarySenseMap.put(System.identityHashCode(jmdictEntrySense), 
								new EntryAdditionalDataEntry$UpdateDictionarySense(polishJapaneseEntrySenseGlossPolList, polishJapaneseEntrySenseAdditionalInfoPolList));
						
					} else {
						
						if (entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary == null) {
							entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary = new ArrayList<>();
						}
						
						// wpisanie dodatkowych znaczen z polskiego slownika
						entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary.add(
								new EntryAdditionalDataEntry$UpdateDictionarySense(polishJapaneseEntrySenseGlossPolList, polishJapaneseEntrySenseAdditionalInfoPolList));						
					}					
				}
				
				if (jmdictEntrySense != null) {
					polishJapaneseEntry.getSenseList().add(jmdictEntrySense);					
				}
			}
		}
		
		return needManuallyChange;
	}
	
	private String getHashForLanguageSourceAdditionalInfoAndGlossListInSenseList(Sense sense, List<Gloss> glossList, List<SenseAdditionalInfo> additionalInfoList) {
		
		StringWriter stringWriter = new StringWriter();
		
		// liczymy hash	
		if (sense != null) {
			for (LanguageSource languageSource : sense.getLanguageSourceList()) {
				
				stringWriter.write(languageSource.getLang());
				stringWriter.write(languageSource.getLsType() != null ? languageSource.getLsType().name() : "");
				stringWriter.write(languageSource.getLsWasei() != null ? languageSource.getLsWasei().name() : "");
				stringWriter.write(languageSource.getValue());			
			}
		}
				
		if (additionalInfoList != null) {
			for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {			
				stringWriter.write(senseAdditionalInfo.getValue());			
			}
		}

		if (glossList != null) {
			for (Gloss gloss : glossList) {
				
				stringWriter.write(gloss.getGType() != null ? gloss.getGType().name() : ""); 
				stringWriter.write(gloss.getValue());
			}
		}
		
		if (sense != null) {
			stringWriter.write(sense.getFieldList().toString());
			stringWriter.write(sense.getMiscList().toString());
		}
								
		return DigestUtils.sha256Hex(stringWriter.toString());
	}

	//
	
	private static class KanjiKanaPair {
		
		private String kanji;
		
		private String kana;
		private ReadingInfoKanaType kanaType;
		
		private String romaji;
		
		private List<Sense> senseList = new ArrayList<>();

		public KanjiKanaPair(String kanji, String kana, ReadingInfoKanaType kanaType, String romaji) {
			this.kanji = kanji;
			this.kana = kana;
			this.kanaType = kanaType;
			this.romaji = romaji;
		}

		public List<Sense> getSenseList() {
			return senseList;
		}

		@Override
		public String toString() {
			return "KanjiKanaPair [kanji=" + kanji + ", kana=" + kana + "]";
		}
	}
		
	public static class EntryAdditionalData {		
		private Map<Integer, EntryAdditionalDataEntry> jmdictEntryAdditionalDataEntryMap = new TreeMap<>();
	}
	
	private static class EntryAdditionalDataEntry {
		
		private List<PolishJapaneseEntry> oldPolishJapaneseEntryList;
		
		private Map<Integer, EntryAdditionalDataEntry$UpdateDictionarySense> updateDictionarySenseMap;
		
		private List<EntryAdditionalDataEntry$UpdateDictionarySense> deleteDictionarySenseListDuringUpdateDictionary;
		
	}
	
	private static class EntryAdditionalDataEntry$UpdateDictionarySense {
		
		private List<Gloss> oldPolishGlossList;
		
		private List<SenseAdditionalInfo> oldPolishSenseAdditionalInfoList;

		public EntryAdditionalDataEntry$UpdateDictionarySense(List<Gloss> oldPolishGlossList, List<SenseAdditionalInfo> oldPolishSenseAdditionalInfoList) {
			this.oldPolishGlossList = oldPolishGlossList;
			this.oldPolishSenseAdditionalInfoList = oldPolishSenseAdditionalInfoList;
		}
	}
}
