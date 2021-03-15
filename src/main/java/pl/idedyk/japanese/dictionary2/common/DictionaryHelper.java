package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
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

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.DialectEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.FieldEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSourceLsTypeEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSourceLsWaseiEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.MiscEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfoKanaType;
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
		
		dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict");
		
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
						addTextFieldToDocument(document, JMdictLuceneFields.TRANSLATE, gloss.getValue());
					}
					
					//
					
					List<SenseAdditionalInfo> senseAdditionalInfoList = sense.getAdditionalInfoList();
					
					for (SenseAdditionalInfo senseAdditionalInfo : senseAdditionalInfoList) {
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
		
		new IEntryPartConverterBegin();
		
		// rekord poczatkowy
		new IEntryPartConverterBegin().writeToCsv(csvWriter, entry);
		
		// kanji
		new IEntryPartConverterKanji().writeToCsv(csvWriter, entry);
		
		// reading
		new IEntryPartConverterReading().writeToCsv(csvWriter, entry);
		
		// sense
		new IEntryPartConverterSense().writeToCsv(csvWriter, entry);
		
		// rekord koncowy
		new IEntryPartConverterEnd().writeToCsv(csvWriter, entry);
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
		SENSE,
		
		END;
	}
	
	private static enum ReadingInfoNoKanji {		
		NO_KANJI;		
	}
	
	//
	
	private interface IEntryPartConverter {		
		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException;		
	}
	
	private class IEntryPartConverterBegin implements IEntryPartConverter {

		@Override
		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			csvWriter.write(EntryHumanCsvFieldType.BEGIN.name());		
			csvWriter.write(String.valueOf(entry.getEntryId()));
			csvWriter.endRecord();
		}
	}
	
	private class IEntryPartConverterEnd implements IEntryPartConverter {

		@Override
		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			csvWriter.write(EntryHumanCsvFieldType.END.name());
			csvWriter.write(String.valueOf(entry.getEntryId()));
			csvWriter.endRecord();
			csvWriter.endRecord();
		}
	}
	
	private class IEntryPartConverterKanji implements IEntryPartConverter {

		@Override
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
	}
	
	private class IEntryPartConverterReading implements IEntryPartConverter {

		@Override
		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
			
			for (ReadingInfo readingInfo : readingInfoList) {
				
				csvWriter.write(EntryHumanCsvFieldType.READING.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));

				csvWriter.write(readingInfo.getNoKanji() != null ? ReadingInfoNoKanji.NO_KANJI.name() : "");			
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
	}
	
	private class IEntryPartConverterSense implements IEntryPartConverter {

		@Override
		public void writeToCsv(CsvWriter csvWriter, Entry entry) throws IOException {
			
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense sense : senseList) {
				
				csvWriter.write(EntryHumanCsvFieldType.SENSE.name());		
				csvWriter.write(String.valueOf(entry.getEntryId()));

				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanjiList()));
				csvWriter.write(Helper.convertListToString(sense.getRestrictedToKanaList()));
				
				csvWriter.write(Helper.convertEnumListToString(sense.getPartOfSpeechList()));
				
				csvWriter.write(Helper.convertListToString(sense.getReferenceToAnotherKanjiKanaList()));
				
				csvWriter.write(Helper.convertListToString(sense.getAntonymList()));
				
				csvWriter.write(Helper.convertEnumListToString(sense.getFieldList()));
				csvWriter.write(Helper.convertEnumListToString(sense.getMiscList()));
				
				//
				
				List<SenseAdditionalInfo> additionalInfoList = sense.getAdditionalInfoList();
				
				List<String> senseAdditionalInfoStringList = new ArrayList<>();
				
				for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {
					
					String senseAdditionalInfoLang = senseAdditionalInfo.getLang();
					
					if (senseAdditionalInfoLang == null || senseAdditionalInfoLang.equals("eng") == true) {						
						senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
					}
				}
				
				csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList));
				
				//
				
				List<String> languageSourceListString = new ArrayList<>();
				
				List<LanguageSource> languageSourceList = sense.getLanguageSourceList();
				
				for (LanguageSource languageSource : languageSourceList) {
					
					String languageSourceLsType = languageSource.getLsType() != null ? languageSource.getLsType().value() : "";
					String languageSourceWasei = languageSource.getLsWasei() != null ? languageSource.getLsWasei().value() : "";
					String languageSourceLang = languageSource.getLang() != null ? languageSource.getLang() : "";
					String languageSourceValue = languageSource.getValue() != null ? languageSource.getValue() : "";
					
					StringWriter languageSourceString = new StringWriter();
					
					CsvWriter languageSourceCsvWriter = new CsvWriter(languageSourceString, ',');
					
					languageSourceCsvWriter.write(languageSourceLsType);
					languageSourceCsvWriter.write(languageSourceWasei);
					languageSourceCsvWriter.write(languageSourceLang);
					languageSourceCsvWriter.write(languageSourceValue);
					
					languageSourceCsvWriter.close();
					
					//
					
					languageSourceListString.add(languageSourceString.toString());
				}
				
				csvWriter.write(Helper.convertListToString(languageSourceListString));
				
				csvWriter.write(Helper.convertEnumListToString(sense.getDialectList()));
				
				/*
			    @XmlElement(name = "gloss")
			    protected List<Gloss> glossList;
			    */
				
				csvWriter.endRecord();
			}
		}
	}
}
