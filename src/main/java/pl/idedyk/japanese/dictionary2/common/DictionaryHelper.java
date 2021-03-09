package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
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

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
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
	
	private File jmdictFile;	
	private JMdict jmdict = null;
	
	private Map<Integer, JMdict.Entry> jmdictEntryIdCache;
	
	private Directory jmdictLuceneIndex; 
	
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
	
	public List<JMdict.Entry> findInJMdict(String word) throws Exception {
		
		// tworzenie indeksu (jesli trzeba)
		createJMdictLuceneIndex();
		
		
		return null;
	}
	
	private void createJMdictLuceneIndex() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();
		
		// inicjalizacja cache wpisow JMdict
		initJMdictEntryIdCache();
		
		//
		
		if (jmdictLuceneIndex == null) {
			
			System.out.println("Initializing JMdict lucene index");
			
			// analizator lucynkowy
			SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
			
			jmdictLuceneIndex = new RAMDirectory();
			
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
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
					addStringFieldToDocument(document, JMdictLuceneFields.KANA, readingInfo.getKana());
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
			
			//
			
			indexWriter.close();
		}
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
	
	private static class JMdictLuceneFields {
		
		private static final String ENTRY_ID = "entryId";
		
		private static final String KANJI = "kanji";
		
		private static final String KANA = "kana";
		
		private static final String TRANSLATE = "translate";
		
		private static final String SENSE_ADDITIONAL_INFO = "senseAdditionalInfo";
		
		private static final String LANGUAGE_SOURCE = "languageSource";
		
	}
}
