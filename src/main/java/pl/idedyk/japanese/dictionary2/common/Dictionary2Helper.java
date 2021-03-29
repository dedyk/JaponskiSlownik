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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;
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
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
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
	
	private Dictionary2Helper() { }
	
	public static Dictionary2Helper init() {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		Dictionary2Helper dictionaryHelper = new Dictionary2Helper();
		
		//
				
		dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict_e");
		dictionaryHelper.jmdictEntryAdditionalDataMap = new TreeMap<>();
		
		//
		
		dictionaryHelper.polishDictionaryFiles = new File[] {
				new File("input/word2-test01.csv"),
				new File("input/word2-test02.csv"),
				new File("input/word2-test03.csv"),
				new File("input/word2-test04.csv")
		};
		
		// stary pomocnik		
		dictionaryHelper.oldWordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");

		
		return dictionaryHelper;
	}
	
	private KanaHelper kanaHelper = new KanaHelper();
	
	//
	
	private File jmdictFile;	
	private JMdict jmdict = null;
	
	private Map<Integer, JMdict.Entry> jmdictEntryIdCache;
	
	private Map<Integer, EntryAdditionalData> jmdictEntryAdditionalDataMap;
	
	//
	
	private File[] polishDictionaryFiles;	
	private List<JMdict.Entry> polishDictionaryEntryList;
	
	private Map<Integer, JMdict.Entry> polishDictionaryEntryListCache;
	
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
	
	public void saveEntryListAsHumanCsv(SaveEntryListAsHumanCsvConfig config, String fileName, List<Entry> entryList) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		for (Entry entry : entryList) {
			saveEntryAsHumanCsv(config, csvWriter, entry);
		}		
		
		csvWriter.close();
	}
	
	private void saveEntryAsHumanCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws Exception {
		
		new EntryPartConverterBegin();
		
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(config, csvWriter, entry);
		
		// kanji
		new EntryPartConverterKanji().writeToCsv(config, csvWriter, entry);
		
		// reading
		new EntryPartConverterReading().writeToCsv(config, csvWriter, entry);
		
		// sense
		new EntryPartConverterSense().writeToCsv(config, csvWriter, entry);
				
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
			
			if (config.shiftCells == true) {
				
				if (config.shiftCellsGenerateIds == false) {
					csvWriter.write("");
					
				} else {
					csvWriter.write(String.valueOf(config.shiftCellsGenerateIdsId));
					
					config.shiftCellsGenerateIdsId++;
				}
			}
			
			csvWriter.write(EntryHumanCsvFieldType.BEGIN.name());		
			csvWriter.write(String.valueOf(entry.getEntryId()));
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
			
			if (config.shiftCells == true) {
				csvWriter.write("");
			}
			
			csvWriter.write(EntryHumanCsvFieldType.END.name());
			csvWriter.write(String.valueOf(entry.getEntryId()));
			csvWriter.endRecord();			
			csvWriter.endRecord();
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
				
				if (config.shiftCells == true) {
					csvWriter.write("");
				}

				csvWriter.write(EntryHumanCsvFieldType.KANJI.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));

				csvWriter.write(kanjiInfo.getKanji());
				csvWriter.write(Helper.convertEnumListToString(kanjiInfo.getKanjiAdditionalInfoList()));
				csvWriter.write(Helper.convertEnumListToString(kanjiInfo.getRelativePriorityList()));
				
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
				
				if (config.shiftCells == true) {
					csvWriter.write("");
				}
				
				csvWriter.write(EntryHumanCsvFieldType.READING.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));
				
				//
				
				ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();
				
				if (kanaType == null) {
					kanaType = getKanaType(readingInfo.getKana().getValue());
				}
				
				csvWriter.write(kanaType.name());

				csvWriter.write(readingInfo.getKana().getValue());
				
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
				
				if (config.markRomaji == true) {
					romaji = romaji + " --- !!! SPRAWDŹ !!!";
				}
				
				csvWriter.write(romaji);
				
				csvWriter.write(readingInfo.getNoKanji() != null ? ReadingInfoNoKanji.NO_KANJI.name() : "-");			
				csvWriter.write(Helper.convertListToString(readingInfo.getKanjiRestrictionList()));
								
				csvWriter.write(Helper.convertEnumListToString(readingInfo.getReadingAdditionalInfoList()));
				csvWriter.write(Helper.convertEnumListToString(readingInfo.getRelativePriorityList()));
				
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

		public void writeToCsv(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense sense : senseList) {
				
				List<Gloss> glossList = sense.getGlossList();
				
				List<Gloss> glossEngList = glossList.stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				List<Gloss> glossPolList = glossList.stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());

				if (glossEngList.size() == 0) {
					continue;
				}
				
				if (config.shiftCells == true) {
					csvWriter.write("");
				}
				
				// czesc wspolna dla wszystkich jezykow
				csvWriter.write(EntryHumanCsvFieldType.SENSE_COMMON.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));

				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanjiList()));
				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanaList()));
				
				csvWriter.write(Helper.convertEnumListToString(sense.getPartOfSpeechList()));
				
				csvWriter.write(Helper.convertListToString(sense.getReferenceToAnotherKanjiKanaList()));
				
				csvWriter.write(Helper.convertListToString(sense.getAntonymList()));
				
				csvWriter.write(Helper.convertEnumListToString(sense.getFieldList()));
				csvWriter.write(Helper.convertEnumListToString(sense.getMiscList()));
				
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
				
				csvWriter.write(languageSourceCsvWriterString.toString());
				
				csvWriter.write(Helper.convertEnumListToString(sense.getDialectList()));
				
				csvWriter.endRecord();
				
				// czesc specyficzna dla jezyka angielskiego i polskiego (tlumaczenia)
				
				writeToCsvLangSense(config, csvWriter, entry, sense, EntryHumanCsvFieldType.SENSE_ENG, glossEngList);
				writeToCsvLangSense(config, csvWriter, entry, sense, EntryHumanCsvFieldType.SENSE_POL, glossPolList);	
				
				int fixme = 1;
				// dla jezyka polskiego generowac aktualne tlumaczenie, w celu kontroli zmiany tlumaczenia
			}				
		}
		
		private void writeToCsvLangSense(SaveEntryListAsHumanCsvConfig config, CsvWriter csvWriter, Entry entry, Sense sense, EntryHumanCsvFieldType entryHumanCsvFieldType, List<Gloss> glossLangList) throws IOException {
			
			if (glossLangList.size() == 0) {
				return;
			}
			
			if (config.shiftCells == true) {
				csvWriter.write("");
			}
			
			csvWriter.write(entryHumanCsvFieldType.name());		
			csvWriter.write(String.valueOf(entry.getEntryId()));

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

			csvWriter.write(glossListCsvWriterString.toString());

			//

			List<SenseAdditionalInfo> additionalInfoList = sense.getAdditionalInfoList();

			List<String> senseAdditionalInfoStringList = new ArrayList<>();

			for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {

				String senseAdditionalInfoLang = senseAdditionalInfo.getLang();

				if (senseAdditionalInfoLang.equals(entryHumanCsvFieldType == EntryHumanCsvFieldType.SENSE_ENG ? "eng" : "pol") == true) {						
					senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
				}
			}

			csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList));
			
			//
			
			BEFORE_IF:
			if (entryHumanCsvFieldType == EntryHumanCsvFieldType.SENSE_POL && config.addOldPolishTranslates == true) { // dodawanie tlumaczenia ze starego slownika
				
				// sprawdzamy, czy cos zostalo przygotowane
				EntryAdditionalData entryAdditionalData = jmdictEntryAdditionalDataMap.get(entry.getEntryId());

				if (entryAdditionalData == null || entryAdditionalData.oldPolishJapaneseEntryList == null) {
					break BEFORE_IF;
				}

				// grupujemy po unikalnym tlumaczeniu
				Map<String, String> uniqueOldPolishJapaneseTranslates = new TreeMap<>();
				
				for (PolishJapaneseEntry polishJapaneseEntry : entryAdditionalData.oldPolishJapaneseEntryList) {
					
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
									
					csvWriter.write("STARE_TŁUMACZENIE\n" + "---\n---\n" + currentPolishJapaneseTranslateAndInfo.getKey());
					
					if (currentPolishJapaneseTranslateAndInfo.getValue().equals("") == false) {
						csvWriter.write("STARE_INFO\n" + "---\n---\n" + currentPolishJapaneseTranslateAndInfo.getValue());
					}
				}
			}
			
			csvWriter.endRecord();
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
		
	public void createEmptyPolishSense(Entry entry) {
		
		List<Sense> senseList = entry.getSenseList();
		
		for (Sense sense : senseList) {
						
			List<Gloss> glossList = sense.getGlossList();
			
			List<Gloss> glossEngList = glossList.stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());

			if (glossEngList.size() == 0) {
				continue;
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
				continue;
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
	}
	
	private void readPolishDictionary() throws Exception {
		
		// wczytywanie slownika
		if (polishDictionaryEntryList == null) {
			
			polishDictionaryEntryList = new ArrayList<>();
			
			//
			
			for (File currentPolishDictionaryFile : polishDictionaryFiles) {
				
				if (currentPolishDictionaryFile.exists() == true) {
					
					System.out.println("Reading polish dictionary file: " + currentPolishDictionaryFile);
					
					List<Entry> currentPolishDictionaryFileEntryList = readEntryListFromHumanCsv(currentPolishDictionaryFile.getAbsolutePath());
					
					polishDictionaryEntryList.addAll(currentPolishDictionaryFileEntryList);					
				}
			}
		}		
	}
	
	private void cachePolishDictionary() throws Exception {
		
		readPolishDictionary();
		
		// cachoe'owanie encji z polskiego slownika
		if (polishDictionaryEntryListCache == null) {
			
			System.out.println("Caching polish dictionary");
			
			polishDictionaryEntryListCache = new TreeMap<>();
						
			for (Entry entry : polishDictionaryEntryList) {
				polishDictionaryEntryListCache.put(entry.getEntryId(), entry);
			}
		}		
	}

	public Entry getEntryFromPolishDictionary(Integer entryId) throws Exception {
		
		cachePolishDictionary();
		
		return polishDictionaryEntryListCache.get(entryId);
	}
	
	public List<JMdict.Entry> getAllPolishDictionaryEntryList() throws Exception {
		
		// wczytywanie slownika
		readPolishDictionary();
		
		return polishDictionaryEntryList;
	}

	public void fillDataFromOldPolishJapaneseDictionary(Entry entry) throws Exception {
		
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
		
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry);		
		
		if (allPolishJapaneseEntriesForEntry.size() > 0) { // jezeli dany wpis juz jest w starym slowniku, mozemy przetworzyc te dane
			
			// aktualizacja romaji
			List<ReadingInfo> entryReadingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : entryReadingInfoList) {
				
				PolishJapaneseEntry polishJapaneseEntryForReadingKana = allPolishJapaneseEntriesForEntry.stream().filter((p) -> p.getKana().equals(readingInfo.getKana().getValue())).findFirst().get();
				
				readingInfo.getKana().setKanaType(ReadingInfoKanaType.fromValue(polishJapaneseEntryForReadingKana.getWordType().name()));
				readingInfo.getKana().setRomaji(polishJapaneseEntryForReadingKana.getRomaji());
			}
						
			// istniejace tlumaczenie (przygotowanie danych)
			EntryAdditionalData entryAdditionalData = jmdictEntryAdditionalDataMap.get(entry.getEntryId());
			
			if (entryAdditionalData == null) {
				entryAdditionalData = new EntryAdditionalData();
				
				jmdictEntryAdditionalDataMap.put(entry.getEntryId(), entryAdditionalData);
			}
			
			entryAdditionalData.oldPolishJapaneseEntryList = allPolishJapaneseEntriesForEntry;
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
			
			if (polishJapaneseEntryForKanjiKanaPair == null) { // nie udalo sie znalexc slowa w starym slowniku				
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
					
					//
					
					result.add(new KanjiKanaPair(null, kana, romaji));
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
					result.add(new KanjiKanaPair(kanji, kana, romaji));					
				}				
			}
		}
		
		// szukanie kana z no kanji
		for (ReadingInfo readingInfo : readingInfoList) {
			
			ReadingInfo.ReNokanji noKanji = readingInfo.getNoKanji();
			
			if (noKanji != null) {
								
				String kana = readingInfo.getKana().getValue();
				String romaji = readingInfo.getKana().getRomaji();
				
				//
				
				result.add(new KanjiKanaPair(null, kana, romaji));
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
	
	public void updatePolishJapaneseEntryInOldDictionary(Entry entry) throws Exception {

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
			
			if (polishJapaneseEntryOptional.isPresent() == false) { // to nigdy nie powinno zdarzyc sie
				throw new RuntimeException(kanjiKanaPair.kanji + " - " + kanjiKanaPair.kana);
			}
			
			PolishJapaneseEntry polishJapaneseEntry = polishJapaneseEntryOptional.get();
			
			//
			
			// pobieranie wszystkich znaczen
			List<Sense> kanjiKanaPairSenseList = kanjiKanaPair.getSenseList();
			
			int fixme = 1;
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
			polishJapaneseEntry.setRomaji(kanjiKanaPair.romaji);
			
			polishJapaneseEntry.setTranslates(newPolishTranslateList);
			polishJapaneseEntry.setInfo(newPolishAdditionalInfo);
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
				polishJapaneseEntry.getParseAdditionalInfoList().add(ParseAdditionalInfo.DICTIONARY2_SOURCE);
			}
			
			// generowanie chudego GroupId
			polishJapaneseEntry.setJmedictRawDataList(Arrays.asList("GroupId: " + entry.getEntryId()));			
		}
	}
	
	private List<String> translateToPolishFieldEnumList(Collection<FieldEnum> fieldEnumList) {
		
		List<String> result = new ArrayList<>();

		for (FieldEnum fieldEnum : fieldEnumList) {
			
			switch (fieldEnum) {
			
			case COMPUTING:
				result.add("informatyka"); break;
			
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
			
			result.add(languageCodeInPolish + ": " + languageValue);			
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
	
	private static class KanjiKanaPair {
		
		private String kanji;
		
		private String kana;
		private String romaji;
		
		private List<Sense> senseList = new ArrayList<>();

		public KanjiKanaPair(String kanji, String kana, String romaji) {
			this.kanji = kanji;
			this.kana = kana;
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
	
	private static class EntryAdditionalData {
		
		private List<PolishJapaneseEntry> oldPolishJapaneseEntryList;
				
	}
}
