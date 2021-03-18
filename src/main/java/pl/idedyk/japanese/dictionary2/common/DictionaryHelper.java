package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

public class DictionaryHelper {
	
	private DictionaryHelper() { }
	
	public static DictionaryHelper init() {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		DictionaryHelper dictionaryHelper = new DictionaryHelper();
		
		//
		
		int fixme = 1; // ok !!!!!!!!!!!!!1
		
		//dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict");
		dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict_e");
		
		return dictionaryHelper;
	}
	
	private KanaHelper kanaHelper = new KanaHelper();
	
	private File jmdictFile;	
	private JMdict jmdict = null;
	
	private Map<Integer, JMdict.Entry> jmdictEntryIdCache;
	
	// analizator lucynkowy
	private SimpleAnalyzer jmdictLuceneAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	
	private Directory jmdictLuceneIndex; 
	private IndexReader jmdictLuceneIndexReader; 
	private IndexSearcher jmdictLuceneIndexReaderSearcher;
	
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
			
			Schema schema = factory.newSchema(DictionaryHelper.class.getResource("/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd"));
			
			Validator validator = schema.newValidator();
						
			validator.validate(new StreamSource(jmdictFile));			

			// wczytywanie pliku JMdict
			System.out.println("Reading JMdict");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);
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
	
	public void saveEntryListAsHumanCsv(String fileName, List<Entry> entryList) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		for (Entry entry : entryList) {
			saveEntryAsHumanCsv(csvWriter, entry);
		}		
		
		csvWriter.close();
	}
	
	private void saveEntryAsHumanCsv(CsvWriter csvWriter, Entry entry) throws Exception {
		
		new EntryPartConverterBegin();
		
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(csvWriter, entry);
		
		// kanji
		new EntryPartConverterKanji().writeToCsv(csvWriter, entry);
		
		// reading
		new EntryPartConverterReading().writeToCsv(csvWriter, entry);
		
		// sense
		new EntryPartConverterSense().writeToCsv(csvWriter, entry);
		
		// rekord koncowy
		new EntryPartConverterEnd().writeToCsv(csvWriter, entry);
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
	
	public void saveJMdictAsXml(JMdict newJMdict, String fileNane) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

		//
				
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		//
		
		jaxbMarshaller.marshal(newJMdict, new File(fileNane));
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

		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
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

		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			csvWriter.write(EntryHumanCsvFieldType.END.name());
			csvWriter.write(String.valueOf(entry.getEntryId()));
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

		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {

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

		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : readingInfoList) {
				
				csvWriter.write(EntryHumanCsvFieldType.READING.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));

				csvWriter.write(readingInfo.getNoKanji() != null ? ReadingInfoNoKanji.NO_KANJI.name() : "-");			
				csvWriter.write(Helper.convertListToString(readingInfo.getKanjiRestrictionList()));
							
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
				
				csvWriter.write(romaji);
				
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
			
			String noKanji = csvReader.get(2);
			
			if (noKanji.equals(ReadingInfoNoKanji.NO_KANJI.name()) == true) {
				readingInfo.setNoKanji(new ReadingInfo.ReNokanji());
			}
			
			readingInfo.getKanjiRestrictionList().addAll(Helper.convertStringToList(csvReader.get(3)));
			
			readingInfo.setKana(new ReadingInfoKana());
			
			int fixme = 1;
			// readingInfo.getKana().setKanaType(ReadingInfoKanaType.fromValue(csvReader.get(4))); // moja modyfikacja
			readingInfo.getKana().setValue(csvReader.get(5));
			
			int fixme2 = 1;
			// readingInfo.getKana().setRomaji(csvReader.get(6)); // moja modyfikacja
			
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

		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense sense : senseList) {
				
				List<Gloss> glossList = sense.getGlossList();
				
				List<Gloss> glossEngList = glossList.stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				List<Gloss> glossPolList = glossList.stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());

				if (glossEngList.size() == 0) {
					continue;
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
				
				writeToCsvLangSense(csvWriter, entry, sense, EntryHumanCsvFieldType.SENSE_ENG, glossEngList);
				writeToCsvLangSense(csvWriter, entry, sense, EntryHumanCsvFieldType.SENSE_POL, glossPolList);	
				
				int fixme = 1;
				// dla jezyka polskiego generowac aktualne tlumaczenie, w celu kontroli zmiany tlumaczenia
			}				
		}
		
		private void writeToCsvLangSense(CsvWriter csvWriter, Entry entry, Sense sense, EntryHumanCsvFieldType entryHumanCsvFieldType, List<Gloss> glossLangList) throws IOException {
			
			if (glossLangList.size() == 0) {
				return;
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
}
