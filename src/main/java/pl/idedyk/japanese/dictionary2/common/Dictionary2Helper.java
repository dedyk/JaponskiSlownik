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
import java.util.Comparator;
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
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.PrefixQuery;
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
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.lucene.LuceneAnalyzer;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
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

public class Dictionary2Helper extends Dictionary2HelperCommon {
	
	private static final int CSV_COLUMNS = 11; 
	
	private static Dictionary2Helper dictionary2Helper;
	
	private Dictionary2Helper() { }
	
	public static Dictionary2Helper getOrInit() {
		
		if (dictionary2Helper == null) {
			
			// stary pomocnik		
			WordGeneratorHelper oldWordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" }, "input/common_word.csv", 
					"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");

			dictionary2Helper =  init(oldWordGeneratorHelper);
		}
		
		return dictionary2Helper;				
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
	private Map<String, List<JMdict.Entry>> jmdictEntryKanjiKanaCache;
	
	private Map<String, List<JMdict.Entry>> jmdictEntryKanjiOnlyCache;
	private Map<String, List<JMdict.Entry>> jmdictEntryKanaOnlyCache;
		
	//
	
	private File polishDictionaryFile;		
	private Map<Integer, JMdict.Entry> polishDictionaryEntryListMap;
	
	//
	
	// analizator lucynkowy
	private Analyzer jmdictLuceneAnalyzer = new LuceneAnalyzer(Version.LUCENE_47); // new SimpleAnalyzer(Version.LUCENE_47);
	
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
					
					addTextFieldToDocument(document, JMdictLuceneFields.ROMAJI, romaji);
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
		return findInJMdict(word, 0);
	}

	public List<JMdict.Entry> findInJMdictPrefix(String word) throws Exception {		
		return findInJMdict(word, 1);
	}

	private List<JMdict.Entry> findInJMdict(String word, int queryMode) throws Exception {
		
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
		Query query;
		
		if (queryMode == 0) {
			query = createLuceneDictionaryIndexTermQuery(word);
			
		} else if (queryMode == 1) {
			query = createLuceneDictionaryIndexPrefixQuery(word);
			
		} else {
			throw new RuntimeException();
		}
		
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
	
	public KanjiKanaPair findKanjiKanaPair(PolishJapaneseEntry polishJapaneseEntry) throws Exception {
		
		List<Entry> entryList = findEntryListInJmdict(polishJapaneseEntry, false);
		
		if (entryList.size() == 1) {
						
			// generowanie wszystkich kanji i ich czytan
			List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entryList.get(0));

			return findKanjiKanaPair(kanjiKanaPairListforEntry, polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());			
		}
		
		return null;
	}
	
	public KanjiKanaPair findKanjiKanaPair(List<KanjiKanaPair> kanjiKanaPairListforEntry, final String kanji, final String kana) {
				
		// odnalezienie wlaciwej pary			
		Optional<KanjiKanaPair> KanjiKanaPairOptional = kanjiKanaPairListforEntry.stream().filter(kanjiKanaPair -> {
			
			String kanjiKanaPairKanji = kanjiKanaPair.getKanji();
			
			if (kanjiKanaPairKanji == null) {
				kanjiKanaPairKanji = "-";
			}

			String kanjiKanaPairKana = kanjiKanaPair.getKana();
							
			//
			
			String kanji2 = kanji;
			
			if (kanji2 == null) {
				kanji2 = "-";
			}
			
			//
										
			return kanji2.equals(kanjiKanaPairKanji) == true && kana.equals(kanjiKanaPairKana) == true;
			
		}).findFirst();
		
		if (KanjiKanaPairOptional.isPresent() == true) {
			return KanjiKanaPairOptional.get();
			
		} else {
			return null;
		}
	}
	
	public List<KanjiKanaPair> getAllKanjiKanaPairListWithTheSameTranslate(Entry entry, String kanji, String kana) {
		
		// pobieramy wszystkie mozliwosci
		List<KanjiKanaPair> kanjiKanaPairList = getKanjiKanaPairListStatic(entry);
		
		// grupujemy po tych samyc tlumaczeniach
		List<List<KanjiKanaPair>> kanjiKanaPairListGroupByTheSameTranslateListList = groupByTheSameTranslate(kanjiKanaPairList);
		
		// szukanie, do ktorej list nalezy nasza kanji i kana
		List<KanjiKanaPair> kanjiKanaPairListGroupByTheSameTranslateListForPolishJapanaeseEntry = kanjiKanaPairListGroupByTheSameTranslateListList.stream().filter(
				list -> {
					for (KanjiKanaPair currentKanjiKanaPair : list) {
						
						String currentKanjiKanaPairKanji = currentKanjiKanaPair.getKanji() != null ? currentKanjiKanaPair.getKanji() : "-";
						String currentKanjiKanaPairKana = currentKanjiKanaPair.getKana();
						
						String kanjiKanaPairForPolishJapaneseEntryKanji = kanji != null ? kanji : "-";
						String kanjiKanaPairForPolishJapaneseEntryKana = kana;
						
						if (	currentKanjiKanaPairKanji.equals(kanjiKanaPairForPolishJapaneseEntryKanji) == true &&
								currentKanjiKanaPairKana.equals(kanjiKanaPairForPolishJapaneseEntryKana) == true) {
							return true;
						}
					}
					
					return false;									
				}).findFirst().get();

		// zwracamy liste wszystkich kanji i kana, ktore maja te samo tlumaczenie, co kanji i kana
		return kanjiKanaPairListGroupByTheSameTranslateListForPolishJapanaeseEntry;		
	}
	
	private Query createLuceneDictionaryIndexTermQuery(String word) throws Exception {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = getTokenizedWords(word);

		BooleanQuery wordBooleanQuery = new BooleanQuery();

		wordBooleanQuery.add(createTermQuery(new String[] { word }, JMdictLuceneFields.KANJI), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(new String[] { word }, JMdictLuceneFields.KANA), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.ROMAJI), Occur.SHOULD);

		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.TRANSLATE), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.LANGUAGE_SOURCE), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, JMdictLuceneFields.SENSE_ADDITIONAL_INFO), Occur.SHOULD);

		query.add(wordBooleanQuery, Occur.MUST);

		return query;
	}
	
	private Query createLuceneDictionaryIndexPrefixQuery(String word) throws Exception {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = word.split("\\s+");

		BooleanQuery wordBooleanQuery = new BooleanQuery();
		
		wordBooleanQuery.add(createPrefixQuery(new String[] { word }, JMdictLuceneFields.KANJI), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(new String[] { word }, JMdictLuceneFields.KANA), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(wordSplited, JMdictLuceneFields.ROMAJI), Occur.SHOULD);

		wordBooleanQuery.add(createPrefixQuery(wordSplited, JMdictLuceneFields.TRANSLATE), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(wordSplited, JMdictLuceneFields.LANGUAGE_SOURCE), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(wordSplited, JMdictLuceneFields.SENSE_ADDITIONAL_INFO), Occur.SHOULD);

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
	
	private static Query createPrefixQuery(String[] wordSplited, String fieldName) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (String currentWord : wordSplited) {
			booleanQuery.add(new PrefixQuery(new Term(fieldName, currentWord)), Occur.MUST);
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
	
	public static ReadingInfoKanaType getKanaType(String kana) {
		
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
		
		if (kanaType == null) {
			kanaType = ReadingInfoKanaType.KATAKANA_HIRAGANA;
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
	
	public void sortJMdict(JMdict newJMdict) {
		
		Collections.sort(newJMdict.getEntryList(), new Comparator<JMdict.Entry>() {

			@Override
			public int compare(Entry o1, Entry o2) {
				return o1.getEntryId().compareTo(o2.getEntryId());
			}
		});
	}
	
	public void saveJMdictAsXml(JMdict newJMdict, String fileName) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

		//
				
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		//
		
		jaxbMarshaller.marshal(newJMdict, new File(fileName));
	}
	
	public List<JMdict.Entry> findEntryListInJmdict(PolishJapaneseEntry polishJapaneseEntry, boolean addFoundNotMatchedEntries) throws Exception {
		
		List<JMdict.Entry> result = new ArrayList<>();
		
		if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
			return result;
		}
		
		DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
		
		if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
			return result;
		}
		
		// pobieramy kanji i kana
		String kanji = polishJapaneseEntry.getKanji();
		
		if (kanji != null && kanji.equals("-") == true) {
			kanji = null;
		}
		
		String kana = polishJapaneseEntry.getKana();
		
		if (kana != null && kana.equals("-") == true) {
			kana = null;
		}
		
		// najpierw pobieramy liste na podstawie kanji i kana
		List<Entry> entryListForKanjiAndKana = findEntryListByKanjiAndKana(kanji, kana);

		// pobieramy identyfikator entry
		Integer polishJapaneseEntryEntryId = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
		
		// na znalezionej liscie entry list pobieramy ten, ktory jest wskazany przy slowku
		if (polishJapaneseEntryEntryId != null && entryListForKanjiAndKana != null && entryListForKanjiAndKana.size() > 0) {
			
			// szukamy grupy na podstawie id zawartego w jmedict raw data (rozwiazuje to problem multigroup)
			for (Entry entry : entryListForKanjiAndKana) {
				
				if (entry.getEntryId().intValue() == polishJapaneseEntryEntryId.intValue()) {
					result.add(entry);
				}				
			}
			
		} else { // zwrocenie wszystkich znalezionych elementow
			
			if (entryListForKanjiAndKana != null) {
				result.addAll(entryListForKanjiAndKana);
			}
		}
		
		// jezeli nic nie znalezlismy, ale jakis entry id jest podany przy slowie to znaczy, ze to slowo albo powinno zostac skasowane ze starego slownika albo zmienilo swoj numer entry id
		if (result.size() == 0 && polishJapaneseEntryEntryId != null && addFoundNotMatchedEntries == true) {
			
			if (entryListForKanjiAndKana != null) {
				result.addAll(entryListForKanjiAndKana);
			}
		}
		
		return result;
	}
	
	public List<Entry> findEntryListByKanjiAndKana(String kanji, String kana) throws Exception {
		
		// inicjalizacja cache
		initJmdictEntryKanjiKanaCache();
		
		// wyliczenie klucza
		String key = getKanjiKanaKeyForCache(kanji, kana);
		
		return jmdictEntryKanjiKanaCache.get(key);
	}

	private void initJmdictEntryKanjiKanaCache() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();

		if (jmdictEntryKanjiKanaCache == null) {
			
			System.out.println("Caching JMdict by kanji and kana");
			
			jmdictEntryKanjiKanaCache = new TreeMap<>();
			
			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				
				// generowanie wszystkich kanji i ich czytan
				List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
				
				// chodzenie po wszystkich kanji i kana
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
					
					String kanji = kanjiKanaPair.getKanji();
					String kana = kanjiKanaPair.getKana();
					
					String kanjiKanaKey = getKanjiKanaKeyForCache(kanji, kana);
					
					// sprawdzamy, czy taki klucz juz wystepuje w cache'u
					List<Entry> entryListForKanjiKanaKey = jmdictEntryKanjiKanaCache.get(kanjiKanaKey);
					
					if (entryListForKanjiKanaKey == null) {
						entryListForKanjiKanaKey = new ArrayList<>();
						
						jmdictEntryKanjiKanaCache.put(kanjiKanaKey, entryListForKanjiKanaKey);
					}
					
					if (entryListForKanjiKanaKey.contains(entry) == false) {
						entryListForKanjiKanaKey.add(entry);
					}
				}				
			}			
		}
	}
	
	public List<Entry> findEntryListByKanjiOnly(String kanji) throws Exception {
		
		// inicjalizacja cache
		initJmdictEntryKanjiOnlyCache();
				
		return jmdictEntryKanjiOnlyCache.get(kanji);
	}

	private void initJmdictEntryKanjiOnlyCache() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();

		if (jmdictEntryKanjiOnlyCache == null) {
			
			System.out.println("Caching JMdict by kanji only");
			
			jmdictEntryKanjiOnlyCache = new TreeMap<>();
			
			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				
				// generowanie wszystkich kanji i ich czytan
				List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
				
				// chodzenie po wszystkich kanji i kana
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
					
					String kanji = kanjiKanaPair.getKanji();
					
					if (kanji == null) {
						continue;
					}					
										
					// sprawdzamy, czy taki klucz juz wystepuje w cache'u
					List<Entry> entryListForKanjiOnly = jmdictEntryKanjiOnlyCache.get(kanji);
					
					if (entryListForKanjiOnly == null) {
						entryListForKanjiOnly = new ArrayList<>();
						
						jmdictEntryKanjiOnlyCache.put(kanji, entryListForKanjiOnly);
					}
					
					if (entryListForKanjiOnly.contains(entry) == false) {
						entryListForKanjiOnly.add(entry);
					}
				}				
			}			
		}
	}
	
	public List<Entry> findEntryListByKanaOnly(String kana) throws Exception {
		
		// inicjalizacja cache
		initJmdictEntryKanaOnlyCache();
				
		return jmdictEntryKanaOnlyCache.get(kana);
	}

	private void initJmdictEntryKanaOnlyCache() throws Exception {
		
		// inicjalizacja jmdict
		getJMdict();

		if (jmdictEntryKanaOnlyCache == null) {
			
			System.out.println("Caching JMdict by kana only");
			
			jmdictEntryKanaOnlyCache = new TreeMap<>();
			
			List<Entry> entryList = jmdict.getEntryList();
			
			for (Entry entry : entryList) {
				
				// generowanie wszystkich kanji i ich czytan
				List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
				
				// chodzenie po wszystkich kanji i kana
				for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
					
					String kana = kanjiKanaPair.getKana();
															
					// sprawdzamy, czy taki klucz juz wystepuje w cache'u
					List<Entry> entryListForKanaOnly = jmdictEntryKanaOnlyCache.get(kana);
					
					if (entryListForKanaOnly == null) {
						entryListForKanaOnly = new ArrayList<>();
						
						jmdictEntryKanaOnlyCache.put(kana, entryListForKanaOnly);
					}
					
					if (entryListForKanaOnly.contains(entry) == false) {
						entryListForKanaOnly.add(entry);
					}
				}				
			}			
		}
	}

	private String getKanjiKanaKeyForCache(String kanji, String kana) {
		return kanji + "." + kana;
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
		
		public Set<Integer> polishEntrySet = null;
		
		public boolean addOldPolishTranslates = false;
		public boolean addOldEnglishPolishTranslatesDuringDictionaryUpdate = false;
		public boolean addDeleteSenseDuringDictionaryUpdate = false;
		public boolean addProposalPolishTranslates = false;
		
		public boolean markRomaji = false;
		
		public boolean shiftCells = false;		
		public boolean shiftCellsGenerateIds = false;
		public Integer shiftCellsGenerateIdsId = 1;
		
		public void markAsPolishEntry(Entry polishEntry) {
			
			if (polishEntrySet == null) {
				polishEntrySet = new TreeSet<>();
			}
			
			polishEntrySet.add(polishEntry.getEntryId());
		}		
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
				
				generateKanaTypeAndRomaji(readingInfo, config.markRomaji && (config.polishEntrySet == null || config.polishEntrySet.contains(entry.getEntryId()) == false));
				
				ReadingInfoKanaType kanaType = readingInfo.getKana().getKanaType();				
				csvWriter.write(kanaType != null ? kanaType.name() : "FIXME"); columnsNo++;

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
				if (config.addOldPolishTranslates == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.oldPolishJapaneseEntryList != null &&
					(config.polishEntrySet == null || config.polishEntrySet.contains(entry.getEntryId()) == false)) { // dodawanie tlumaczenia ze starego slownika

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
				
				if (config.addOldEnglishPolishTranslatesDuringDictionaryUpdate == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.updateDictionarySenseMap != null) { // podczas aktualizacji slownika jakis sens zmienil sie
					
					EntryAdditionalDataEntry$UpdateDictionarySense entryAdditionalDataEntry$UpdateDictionarySense = entryAdditionalDataEntry.updateDictionarySenseMap.get(System.identityHashCode(sense));
					
					if (entryAdditionalDataEntry$UpdateDictionarySense != null) { // podczas aktualizacji slownika, jakis sense zmienil sie, wpisanie starego polskiego znaczenia
						
						StringWriter sb = new StringWriter();
						
						// dodajemy stare polskie tlumaczenie
						sb.append("STARE_TŁUMACZENIE\n" + "---\n---\n" + generateGlossWriterCellValue(entryAdditionalDataEntry$UpdateDictionarySense.oldPolishGlossList));
						
						// dodajemy stare angielskie tlumaczenie
						sb.append("---\n---\nSTARE_ANGIELSKIE_TŁUMACZENIE (" +
								(entryAdditionalDataEntry$UpdateDictionarySense.englishGlossListEquals == true ? "IDENTYCZNE" : "RÓŻNICA") + ")\n---\n---\n");
												
						sb.append(generateGlossWriterCellValue(entryAdditionalDataEntry$UpdateDictionarySense.oldEnglishGlossList));							
						
						csvWriter.write(sb.toString()); columnsNo++;
						
						// dodajemy stare polskie informacje dodatkowe
						if (entryAdditionalDataEntry$UpdateDictionarySense.oldPolishSenseAdditionalInfoList.size() > 0 || entryAdditionalDataEntry$UpdateDictionarySense.oldEnglishSenseAdditionalInfoList.size() > 0) {
							
							senseAdditionalInfoStringList = new ArrayList<>();
							
							for (SenseAdditionalInfo senseAdditionalInfo : entryAdditionalDataEntry$UpdateDictionarySense.oldPolishSenseAdditionalInfoList) {
								senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
							}

							senseAdditionalInfoStringList.add("---");
							
							//
							
							// stare angielskie informacje dodatkowe						
							if (entryAdditionalDataEntry$UpdateDictionarySense.englishAdditionalInfoListEquals == true) {
								senseAdditionalInfoStringList.add("IDENTYCZNE");
							} else {
								senseAdditionalInfoStringList.add("RÓŻNICA");
							}
							
							senseAdditionalInfoStringList.add("---\n---\n");
							
							for (SenseAdditionalInfo senseAdditionalInfo : entryAdditionalDataEntry$UpdateDictionarySense.oldEnglishSenseAdditionalInfoList) {
								senseAdditionalInfoStringList.add(senseAdditionalInfo.getValue());
							}
							
							csvWriter.write(Helper.convertListToString(senseAdditionalInfoStringList)); columnsNo++;
						}						
					}
				}	
				
				// aktualizacja o propozycje polskiego znaczenia (jezeli wystepuje)
				if (config.addProposalPolishTranslates == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.proposalNewPolishTranslateMap != null) {
					
					EntryAdditionalDataEntry$ProposalNewPolishTranslate entryAdditionalDataEntry$ProposalNewPolishTranslate = entryAdditionalDataEntry.proposalNewPolishTranslateMap.get(System.identityHashCode(sense));
					
					if (entryAdditionalDataEntry$ProposalNewPolishTranslate != null) {						
						csvWriter.write("PROPOZYCJA\n---\n" +
								(entryAdditionalDataEntry$ProposalNewPolishTranslate.polishGlossListEquals == true ? "IDENTYCZNE" : "RÓŻNICA") + "\n---\n" + generateGlossWriterCellValue(entryAdditionalDataEntry$ProposalNewPolishTranslate.proposalPolishGlossList)); columnsNo++;
						
						csvWriter.write("PROPOZYCJA\n---\n" + (entryAdditionalDataEntry$ProposalNewPolishTranslate.polishAdditionalInfoListEquals == true ? "IDENTYCZNE" : "RÓŻNICA") + "\n---\n" + Helper.convertListToString(entryAdditionalDataEntry$ProposalNewPolishTranslate.proposalPolishSenseAdditionalInfoList.stream()
								.map(m -> m.getValue()).collect(Collectors.toList()))); columnsNo++;
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

			for (Sense currentSense : senseList) {				
				
				// pobieramy wszyskie polskie informacje dodatkowe z tego sensu
				List<SenseAdditionalInfo> senseAdditionalInfoList = currentSense.getAdditionalInfoList().stream().filter(additionalInfo -> (additionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
				
				Set<String> uniqueSenseAdditionalInfoSet = new TreeSet<>();
				List<String> allSenseAdditionalInfoList = new ArrayList<>();
				
				for (SenseAdditionalInfo senseAdditionalInfo : senseAdditionalInfoList) {
					uniqueSenseAdditionalInfoSet.add(senseAdditionalInfo.getValue());
					allSenseAdditionalInfoList.add(senseAdditionalInfo.getValue());
				}
				
				if (uniqueSenseAdditionalInfoSet.size() != allSenseAdditionalInfoList.size()) { // mamy duplikat
					
					Collections.sort(allSenseAdditionalInfoList);
					
					System.out.println("[Error] Sense gloss duplicate for " + entry.getEntryId() + " - " + allSenseAdditionalInfoList);
					
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
				
				String kana = currentReadingInfo.getKana().getValue().replaceAll("・", "").replaceAll("、", "");
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
				
				if (romaji.contains("!") == true || romaji.contains("-") == true || romaji.contains("SPRA") == true) { // !!! SPRAWDŹ !!!
					
					System.out.println("[Error] Romaji validate (5) for " + entry.getEntryId() + " - " + kana + " - " + romaji);
					
					wasError = true;
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
		
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry, false);		
		
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
	
	private List<PolishJapaneseEntry> getPolishJapaneseEntryListFromOldDictionary(Entry entry, List<KanjiKanaPair> kanjiKanaPairListforEntry, boolean throwErrorWhenDifferentGroup) throws Exception {
		
		// wczytanie starego slownika i sche'owanie go		
		Map<String, List<PolishJapaneseEntry>> polishJapaneseEntriesCache = oldWordGeneratorHelper.getPolishJapaneseEntriesCache();
		
		//
				
		// szukamy wszystkich slow ze starego slownika
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = new ArrayList<>();		
				
		for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
			
			// szukamy slowa ze starego slownika
			List<PolishJapaneseEntry> findPolishJapaneseEntryList = Helper.findPolishJapaneseEntry(polishJapaneseEntriesCache, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana());
			
			if (findPolishJapaneseEntryList == null) { // nie znaleziono
				continue;
			}
			
			PolishJapaneseEntry polishJapaneseEntryForKanjiKanaPair = null;
			
			if (findPolishJapaneseEntryList.size() == 1) {
				
				polishJapaneseEntryForKanjiKanaPair = findPolishJapaneseEntryList.get(0);

				// pobieramy identyfikator grupy ze slowa
				Integer polishJapaneseEntryForKanjiKanaPairEntryId = polishJapaneseEntryForKanjiKanaPair.getGroupIdFromJmedictRawDataList();
				
				if (polishJapaneseEntryForKanjiKanaPairEntryId != null && polishJapaneseEntryForKanjiKanaPairEntryId.intValue() != entry.getEntryId().intValue()) { // sprawdzamy grupe
					
					if (throwErrorWhenDifferentGroup == true) {
						throw new Exception(polishJapaneseEntryForKanjiKanaPair.getId() + " - " + kanjiKanaPair.getKanji() + " - " + kanjiKanaPair.getKana() + " - " + polishJapaneseEntryForKanjiKanaPairEntryId + " (obecnie przy pozycji) - " + entry.getEntryId().intValue() + " (wymagany, nowy)"); // jezeli to wydarzylo sie, oznacza to, ze dane slowo zmienilo swoja grupe, mozna to poprawic; ewentualnie jest multi group dla podanego kanji i kana
						
					} else {
						continue;
					}
				}
			}
			
			if (findPolishJapaneseEntryList.size() > 1) { // jezeli mamy kilka takich smaych slow, szukamy tego konkretnego
				
				for (PolishJapaneseEntry currentFindPolishJapaneseEntryList : findPolishJapaneseEntryList) {
					
					if (currentFindPolishJapaneseEntryList.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == true) {
						continue;
					}
					
					if (currentFindPolishJapaneseEntryList.getGroupIdFromJmedictRawDataList() == null) {
						throw new Exception(kanjiKanaPair.getKanji() + " - " + kanjiKanaPair.getKana() + " - no group id from jmedict raw data for: " + currentFindPolishJapaneseEntryList.getId()); // moze jednak nie -> // jezeli to wydarzylo sie, oznacza to, ze dane slowo jest potencjalnym duplikatem i powinien zostac recznie usuniety
					}
					
					if (currentFindPolishJapaneseEntryList.getGroupIdFromJmedictRawDataList().intValue() == entry.getEntryId().intValue()) { // mamy kandydata z naszej grup
						polishJapaneseEntryForKanjiKanaPair = currentFindPolishJapaneseEntryList;
						
						break;
					}
				}				
			}
			
			if (polishJapaneseEntryForKanjiKanaPair == null) { // nie udalo sie znalesc slowa w starym slowniku				
				throw new Exception(kanjiKanaPair.getKanji() + " - " + kanjiKanaPair.getKana() + " - " + entry.getEntryId().intValue() + " - please add manually"); // to chyba nigdy nie powinno zdarzyc sie
			}
			
			allPolishJapaneseEntriesForEntry.add(polishJapaneseEntryForKanjiKanaPair);
		}

		return allPolishJapaneseEntriesForEntry;
	}
		
	public boolean isExistsInOldPolishJapaneseDictionary(Entry entry) throws Exception {
		
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
		
		List<PolishJapaneseEntry> polishJapaneseEntryListFromOldDictionary = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry, true);
		
		if (polishJapaneseEntryListFromOldDictionary != null && polishJapaneseEntryListFromOldDictionary.size() > 0) {
			return true;
			
		} else {
			return false;
			
		}
	}
		
	public List<PolishJapaneseEntry> updatePolishJapaneseEntryInOldDictionary(Entry entry) throws Exception {

		// lista nowych wpisow do dodania do starego slownika
		List<PolishJapaneseEntry> newOldPolishJapaneseEntryList = new ArrayList<>();
		
		// generowanie wszystkich kanji i ich czytan
		List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);
				
		// pobieramy liste 
		List<PolishJapaneseEntry> allPolishJapaneseEntriesForEntry = getPolishJapaneseEntryListFromOldDictionary(entry, kanjiKanaPairListforEntry, true);		

		// chodzenie po wszystkich kombinacjach kanji i kana
		for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairListforEntry) {
			
			// sprawdzenie informacji dodatkowych przy kanji
			if (kanjiKanaPair.getKanjiInfo() != null) {				
				translateToPolishKanjiAdditionalInfoEnum(kanjiKanaPair.getKanjiInfo().getKanjiAdditionalInfoList());
			}
			
			// sprawdzenie informacji dodatkowych przy kana
			translateToPolishReadingAdditionalInfoEnum(kanjiKanaPair.getReadingInfo().getReadingAdditionalInfoList());
			
			// szukamy docelowego slowka
			Optional<PolishJapaneseEntry> polishJapaneseEntryOptional = allPolishJapaneseEntriesForEntry.stream().filter(polishJapaneseEntry -> {
				
				String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
				String polishJapaneseEntryKana = polishJapaneseEntry.getKana();
				
				String searchKanji = kanjiKanaPair.getKanji();
				
				if (searchKanji == null) {
					searchKanji = "-";
				}
				
				String searchKana = kanjiKanaPair.getKana();
				
				return polishJapaneseEntryKanji.equals(searchKanji) == true && polishJapaneseEntryKana.equals(searchKana) == true;				
			}).findFirst();
			
			PolishJapaneseEntry polishJapaneseEntry;
			
			if (polishJapaneseEntryOptional.isPresent() == false) { // tego elementu nie ma w starym slowniku, generowanie elementu
				
				System.out.println("[Warning] Can't find polish japanese entry for " + entry.getEntryId() + " - " + kanjiKanaPair.getKanji() + " - " + kanjiKanaPair.getKana());
				
				// szukamy, czy wystepuje jakis element, ktory zostal zaznaczony do skasowania
				PolishJapaneseEntry polishJapaneseEntryToDelete = getPolishJapaneseEntryToDelete();
				
				if (polishJapaneseEntryToDelete == null) { // nie znaleziono, dodajemy nowy
					
					polishJapaneseEntry = generateNewEmptyOldPolishJapaneseEntry(kanjiKanaPair);
					
					newOldPolishJapaneseEntryList.add(polishJapaneseEntry);
					
				} else { // resetujemy pozycje
					
					// tworzenie nowej pustej pozycji
					PolishJapaneseEntry newPolishJapaneseEntry = generateNewEmptyOldPolishJapaneseEntry(kanjiKanaPair);
					
					// resetowanie wpisudo skasowania na podstawie nowego pustego wpisu 
					polishJapaneseEntryToDelete.setKanji(newPolishJapaneseEntry.getKanji());
					polishJapaneseEntryToDelete.setKana(newPolishJapaneseEntry.getKana());
					polishJapaneseEntryToDelete.setRomaji(newPolishJapaneseEntry.getRomaji());

					polishJapaneseEntryToDelete.setTranslates(newPolishJapaneseEntry.getTranslates());		
					polishJapaneseEntryToDelete.setInfo(newPolishJapaneseEntry.getInfo());	
					
					polishJapaneseEntryToDelete.setWordType(newPolishJapaneseEntry.getWordType());						
					polishJapaneseEntryToDelete.setDictionaryEntryTypeList(newPolishJapaneseEntry.getDictionaryEntryTypeList());						

					polishJapaneseEntryToDelete.setAttributeList(newPolishJapaneseEntry.getAttributeList());						
					polishJapaneseEntryToDelete.setParseAdditionalInfoList(newPolishJapaneseEntry.getParseAdditionalInfoList());				

					polishJapaneseEntryToDelete.setGroups(newPolishJapaneseEntry.getGroups());										
					polishJapaneseEntryToDelete.setKnownDuplicatedList(newPolishJapaneseEntry.getKnownDuplicatedList());					
					
					polishJapaneseEntryToDelete.setJmedictRawDataList(newPolishJapaneseEntry.getJmedictRawDataList());

					//
										
					polishJapaneseEntry = polishJapaneseEntryToDelete;
					
					// musimy jeszcze uniewaznic cache, bo zmienil sie
					oldWordGeneratorHelper.invalidatePolishJapaneseEntriesCache();
				}
				
								
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
				List<PartOfSpeechEnum> partOfSpeechList = currentSense.getPartOfSpeechList();
				
				
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
						currentPolGlossPolishTranslate.addAll(translateToPolishMiscEnumList(filterPolishMiscEnumList(miscEnumListUniqueForCurrentSense)));
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
				
				// czesc mowy - tylko sprawdzenie
				if (partOfSpeechList.size() > 0) {
					translateToPolishPartOfSpeechEnum(partOfSpeechList);
				}
			}
			
			// informacje dodatkowe
						
			// dziedzina
			if (fieldCommonList != null && fieldCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishFieldEnumList(fieldCommonList));						
			}
			
			// rozne informacje
			if (miscCommonList != null && miscCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishMiscEnumList(miscCommonList));
			}
			
			// dialekt
			if (dialectCommonList != null && dialectCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(translateToPolishDialectEnumList(dialectCommonList));
			}
			
			// informacje dodatkowe dla znaczenia
			if (additionalInfoCommonList != null && additionalInfoCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(additionalInfoCommonList);
			}
			
			// jezyk zrodlowy
			if (languageSourceCommonList != null && languageSourceCommonList.size() > 0) {
				newPolishAdditionalInfoList.addAll(languageSourceCommonList);
			}
			
			String newPolishAdditionalInfo = joinStringForOldPolishJapaneseEntry(newPolishAdditionalInfoList, false);

			// aktualizacja wpisu
			polishJapaneseEntry.setWordType(WordType.valueOf(kanjiKanaPair.getKanaType().name()));			
			polishJapaneseEntry.setRomaji(kanjiKanaPair.romaji());
			
			polishJapaneseEntry.setTranslates(newPolishTranslateList);
			polishJapaneseEntry.setInfo(newPolishAdditionalInfo);
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
				polishJapaneseEntry.getParseAdditionalInfoList().add(ParseAdditionalInfo.DICTIONARY2_SOURCE);
			}
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.IGNORE_NO_JMEDICT) == true) {
				polishJapaneseEntry.getParseAdditionalInfoList().remove(ParseAdditionalInfo.IGNORE_NO_JMEDICT);
			}
			
			// generowanie chudego GroupId
			polishJapaneseEntry.setJmedictRawDataList(Arrays.asList("GroupId: " + entry.getEntryId()));			
		}
		
		return newOldPolishJapaneseEntryList;
	}
	
	public PolishJapaneseEntry generateOldPolishJapaneseEntry(Entry entry, KanjiKanaPair kanjiKanaPair, int id, String missingWord) throws Exception {
		
		// generowanie pustego wpisu
		PolishJapaneseEntry polishJapaneseEntry = generateNewEmptyOldPolishJapaneseEntry(kanjiKanaPair);
		
		// i uzupelniamy o kilka danych
		List<String> newTranslatesInOldFormat = generateTranslatesInOldFormat(kanjiKanaPair, missingWord);
		List<String> additionalInfoInOldFormat = generateAdditionalInfoInOldFormat(kanjiKanaPair, polishJapaneseEntry.getWordType());
		
		List<String> rawDataInOldFormat = new ArrayList<>(); 
		fillJmedictRawDataInOldFormat(entry, kanjiKanaPair, rawDataInOldFormat);
		
		polishJapaneseEntry.setId(id);
		polishJapaneseEntry.setRomaji(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kanjiKanaPair.getReadingInfo().getKana().getValue(), 
				kanaHelper.getKanaCache(), true)));
		polishJapaneseEntry.setTranslates(newTranslatesInOldFormat);
		polishJapaneseEntry.setInfo(Helper.convertListToString(additionalInfoInOldFormat));
		polishJapaneseEntry.setJmedictRawDataList(rawDataInOldFormat);

		return polishJapaneseEntry;
	}
	
	private PolishJapaneseEntry generateNewEmptyOldPolishJapaneseEntry(KanjiKanaPair kanjiKanaPair) throws Exception {
						
		// generowanie wpisu
		PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
		
		polishJapaneseEntry.setId(-1);
				
		polishJapaneseEntry.setDictionaryEntryTypeList(getOldDictionaryEntryTypeFromKanjiKanaPair(kanjiKanaPair));
		
		polishJapaneseEntry.setAttributeList(new AttributeList());
				
		polishJapaneseEntry.setWordType(WordType.valueOf(getKanaType(kanjiKanaPair.getReadingInfo().getKana().getValue()).name()));
		
		polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
		
		String kanji = kanjiKanaPair.getKanji();
		String kana = kanjiKanaPair.getKana();
		
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
	
	public List<DictionaryEntryType> getOldDictionaryEntryTypeFromKanjiKanaPair(KanjiKanaPair kanjiKanaPair) throws Exception {
				
		List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		for (Sense sense : kanjiKanaPair.getSenseList()) {
			
			List<PartOfSpeechEnum> partOfSpeechList = sense.getPartOfSpeechList();
			
			for (PartOfSpeechEnum partOfSpeechEnum : partOfSpeechList) {
								
				DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(partOfSpeechEnum);
				
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

		return dictionaryEntryTypeList;		
	}
	
	private PolishJapaneseEntry getPolishJapaneseEntryToDelete() throws Exception {	
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = oldWordGeneratorHelper.getPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
				return polishJapaneseEntry;
			}			
		}
		
		return null;
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
					
					// bierzmy stare angielskie znaczenia
					List<Gloss> englishJapaneseEntrySenseGlossPolList = polishJapaneseEntrySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
					List<SenseAdditionalInfo> englishJapaneseEntrySenseAdditionalInfoPolList = polishJapaneseEntrySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
					
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
								new EntryAdditionalDataEntry$UpdateDictionarySense(
										equalsGlossList(jmdictEntrySenseGlossEngList, englishJapaneseEntrySenseGlossPolList),
										equalsAdditionalInfoList(jmdictEntrySenseAdditionalInfoEngList, englishJapaneseEntrySenseAdditionalInfoPolList),
										englishJapaneseEntrySenseGlossPolList, englishJapaneseEntrySenseAdditionalInfoPolList,
										polishJapaneseEntrySenseGlossPolList, polishJapaneseEntrySenseAdditionalInfoPolList));
						
					} else {
						
						if (entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary == null) {
							entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary = new ArrayList<>();
						}
						
						// wpisanie dodatkowych znaczen z polskiego slownika
						entryAdditionalDataEntry.deleteDictionarySenseListDuringUpdateDictionary.add(
								new EntryAdditionalDataEntry$UpdateDictionarySense(
										equalsGlossList(jmdictEntrySenseGlossEngList, englishJapaneseEntrySenseGlossPolList),
										equalsAdditionalInfoList(jmdictEntrySenseAdditionalInfoEngList, englishJapaneseEntrySenseAdditionalInfoPolList),
										englishJapaneseEntrySenseGlossPolList, englishJapaneseEntrySenseAdditionalInfoPolList,
										polishJapaneseEntrySenseGlossPolList, polishJapaneseEntrySenseAdditionalInfoPolList));						
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
		
		stringWriter.append(joinGlossListAndAdditionalInfoList(glossList, additionalInfoList));
				
		if (sense != null) {
			stringWriter.write(sense.getFieldList().toString());
			stringWriter.write(sense.getMiscList().toString());
		}
								
		return DigestUtils.sha256Hex(stringWriter.toString());
	}
	
	private String joinGlossListAndAdditionalInfoList(List<Gloss> glossList, List<SenseAdditionalInfo> additionalInfoList) {
		
		StringWriter stringWriter = new StringWriter();
		
		stringWriter.append(joinAdditionalInfoList(additionalInfoList));
		stringWriter.append(joinGlossList(glossList));
		
		return stringWriter.toString();
	}
	
	private String joinAdditionalInfoList(List<SenseAdditionalInfo> additionalInfoList) {
		
		StringWriter stringWriter = new StringWriter();
		
		if (additionalInfoList != null) {
			for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoList) {			
				stringWriter.write(senseAdditionalInfo.getValue());			
			}
		}
		
		return stringWriter.toString();
	}
	
	private String joinGlossList(List<Gloss> glossList) {
		
		StringWriter stringWriter = new StringWriter();
		
		if (glossList != null) {
			for (Gloss gloss : glossList) {
				
				stringWriter.write(gloss.getGType() != null ? gloss.getGType().name() : ""); 
				stringWriter.write(gloss.getValue());
			}
		}
		
		return stringWriter.toString();
	}
	
	private boolean equalsGlossList(List<Gloss> glossList1, List<Gloss> glossList2) {		
		return joinGlossList(glossList1).equals(joinGlossList(glossList2));		
	}

	private boolean equalsAdditionalInfoList(List<SenseAdditionalInfo> additionalInfoList1, List<SenseAdditionalInfo> additionalInfoList2) {
		return joinAdditionalInfoList(additionalInfoList1).equals(joinAdditionalInfoList(additionalInfoList2));		
	}

	public List<PolishJapaneseEntry> detectEntriesWhichShouldBeDeletedInOldPolishJapaneseDictionary() throws Exception {
		
		List<PolishJapaneseEntry> entriesWhichShouldBeDeletedInOldPolishJapanaeseList = new ArrayList<>();
		
		//
		
		// wczytanie starego slownika
		List<PolishJapaneseEntry> polishJapaneseEntriesList = oldWordGeneratorHelper.getPolishJapaneseEntriesList();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == false) {
				continue;
			}
			
			// pobranie numeru wpisu
			Integer groupIdFromJmedictRawDataList = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
			
			// pobranie slowa w nowym slowniku
			Entry entry = getEntryFromPolishDictionary(groupIdFromJmedictRawDataList);
			
			if (entry == null) { // nie znaleziono
				entriesWhichShouldBeDeletedInOldPolishJapanaeseList.add(polishJapaneseEntry);
				
				continue;
			}			
			
			// generowanie wszystkich kanji i ich czytan
			List<KanjiKanaPair> kanjiKanaPairListforEntry = getKanjiKanaPairList(entry);

			// sprawdzenie, czy stary wpis w starym slowniku znajduje sie na liscie			
			Optional<KanjiKanaPair> kanjiKanaPairForPolishJapaneseEntry = kanjiKanaPairListforEntry.stream().filter(kanjiKanaPair -> {
				
				String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();
				String polishJapaneseEntryKana = polishJapaneseEntry.getKana();
				
				String searchKanji = kanjiKanaPair.getKanji();
				
				if (searchKanji == null) {
					searchKanji = "-";
				}
				
				String searchKana = kanjiKanaPair.getKana();
				
				return polishJapaneseEntryKanji.equals(searchKanji) == true && polishJapaneseEntryKana.equals(searchKana) == true;				
			}).findFirst();
			
			if (kanjiKanaPairForPolishJapaneseEntry.isPresent() == false) { // nie ma, zostal skasowany
				entriesWhichShouldBeDeletedInOldPolishJapanaeseList.add(polishJapaneseEntry);
				
				continue;
			}
		}
		
		return entriesWhichShouldBeDeletedInOldPolishJapanaeseList;
	}
	
	public List<List<KanjiKanaPair>> groupByTheSameTranslate(List<KanjiKanaPair> kanjiKanaPairList) {
		
		Map<String, List<KanjiKanaPair>> theSameTranslate = new TreeMap<String, List<KanjiKanaPair>>(); 
		
		for (KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
			
			StringBuffer groupEntryTranslate = new StringBuffer();
			
			for (Sense sense : kanjiKanaPair.getSenseList()) {
				
				List<Gloss> glossEngList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
				List<SenseAdditionalInfo> additionalInfoEngList = sense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
				
				for (Gloss gloss : glossEngList) {
					groupEntryTranslate.append(gloss.getValue()).append("\n");
				}
				
				for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoEngList) {
					groupEntryTranslate.append(senseAdditionalInfo.getValue()).append("\n");
				}
			}			
						
			List<KanjiKanaPair> kanjiKanaPairForTheSameTranslateList = theSameTranslate.get(groupEntryTranslate.toString());
			
			if (kanjiKanaPairForTheSameTranslateList == null) {
				kanjiKanaPairForTheSameTranslateList = new ArrayList<KanjiKanaPair>();
				
				theSameTranslate.put(groupEntryTranslate.toString(), kanjiKanaPairForTheSameTranslateList);
			}			
			
			kanjiKanaPairForTheSameTranslateList.add(kanjiKanaPair);
		}
		
		return new ArrayList<List<KanjiKanaPair>>(theSameTranslate.values());
	}
	
	public Entry updateOnlyPolishJapaneseTranslate(Entry entryFromPolishDictionary, Entry entryToCompare, EntryAdditionalData entryAdditionalData) {
		
		// tworzymy klona, aby nie pracowac na zrodlowym obiekcie
		entryFromPolishDictionary = (Entry)SerializationUtils.clone(entryFromPolishDictionary);
		
		if (entryFromPolishDictionary.getEntryId().longValue() != entryToCompare.getEntryId().longValue()) {
			throw new RuntimeException("Different entry id: " + entryFromPolishDictionary.getEntryId() + " vs " + entryToCompare.getEntryId());
		}
		
		// sprawdzenie, czy liczba znaczen jest taka sama, nie moze byc zadnych roznic, a to by znaczylo, ze ktos usunal dany wpis
		if (entryFromPolishDictionary.getSenseList().size() != entryToCompare.getSenseList().size()) {
			throw new RuntimeException("Different sense list for: " + entryFromPolishDictionary.getEntryId());
		}
		
		for (int senseIdx = 0; senseIdx < entryFromPolishDictionary.getSenseList().size(); ++senseIdx) {
			
			// pobieramy znaczenia z obu wpisow
			Sense entryFromPolishDictionarySense = entryFromPolishDictionary.getSenseList().get(senseIdx);
			Sense entryToCompareSense = entryToCompare.getSenseList().get(senseIdx);
			
			// pobranie polskiego znaczenia i informacji dodatkowych z obu wpisow
			List<Gloss> entryFromPolishDictionarySenseGlossPolList = entryFromPolishDictionarySense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
			List<SenseAdditionalInfo> entryFromPolishDictionarySenseAdditionalInfoPolList = entryFromPolishDictionarySense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
			
			List<Gloss> entryToCompareSenseGlossPolList = entryToCompareSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
			List<SenseAdditionalInfo> entryToCompareSenseAdditionalInfoPolList = entryToCompareSense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
			
			// liczymy hash dla obu znaczen (sprawdzenie, czy zostaly wprowadzone jakies zmiany)
			String hashForEntryFromPolishDictionarySense = getHashForLanguageSourceAdditionalInfoAndGlossListInSenseList(entryFromPolishDictionarySense, entryFromPolishDictionarySenseGlossPolList, entryFromPolishDictionarySenseAdditionalInfoPolList);
			String hashForEntryToCompareSense = getHashForLanguageSourceAdditionalInfoAndGlossListInSenseList(entryToCompareSense, entryToCompareSenseGlossPolList, entryToCompareSenseAdditionalInfoPolList);
			
			if (hashForEntryFromPolishDictionarySense.equals(hashForEntryToCompareSense) == false) { // jezeli znaczenie zostalo zmienione to dodaj te propozycje do manualnego sprawdzenia
					
				// sprawdzenie i ewentualne stworzenie obiektu z dodatkowymi danymi
				EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.jmdictEntryAdditionalDataEntryMap.get(entryFromPolishDictionary.getEntryId());
				
				if (entryAdditionalDataEntry == null) {
					entryAdditionalDataEntry = new EntryAdditionalDataEntry();
					
					entryAdditionalData.jmdictEntryAdditionalDataEntryMap.put(entryFromPolishDictionary.getEntryId(), entryAdditionalDataEntry);
				}
				
				if (entryAdditionalDataEntry.proposalNewPolishTranslateMap == null) {		
					entryAdditionalDataEntry.proposalNewPolishTranslateMap = new TreeMap<>();
				}
				
				// zapisujemy obiekt z propozycja nowego tlumaczenia, aby zapis sie pozniej w pliku wynikowym
				entryAdditionalDataEntry.proposalNewPolishTranslateMap.put(System.identityHashCode(entryFromPolishDictionarySense),
						new EntryAdditionalDataEntry$ProposalNewPolishTranslate(
								equalsGlossList(entryFromPolishDictionarySenseGlossPolList, entryToCompareSenseGlossPolList),
								equalsAdditionalInfoList(entryFromPolishDictionarySenseAdditionalInfoPolList, entryToCompareSenseAdditionalInfoPolList),
								entryToCompareSenseGlossPolList, entryToCompareSenseAdditionalInfoPolList));
			}
		}
		
		return entryFromPolishDictionary;
	}
	
	public CommonWord convertKanjiKanaPairToCommonWord(int id, KanjiKanaPair kanjiKanaPair) throws Exception {
		
		List<String> partOfSpeechList = new ArrayList<String>();
		List<String> translateStringList = new ArrayList<String>();
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		for (Sense sense : kanjiKanaPair.getSenseList()) {	
			
			for (PartOfSpeechEnum partOfSpeech : sense.getPartOfSpeechList()) {
				String partOfSpeechAsEntity = dictionaryEntryJMEdictEntityMapper.getPartOfSpeechAsEntity(partOfSpeech);
				
				if (partOfSpeechList.contains(partOfSpeechAsEntity) == false) {
					partOfSpeechList.add(partOfSpeechAsEntity);
				}
			}
			
			List<Gloss> glossEngList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());

			for (Gloss gloss : glossEngList) {
				translateStringList.add(gloss.getValue());
			}		
		}
				
		CommonWord commonWord = new CommonWord(id, false, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana(), partOfSpeechList.toString(), translateStringList.toString());
		
		return commonWord;
	}
	
	public String fillJmedictRawDataInOldFormat(Entry entry, KanjiKanaPair kanjiKanaPair, List<String> result) throws Exception {
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		String groupId = "GroupId: " + entry.getEntryId();
		
		if (result.contains(groupId) == false) {
			result.add(groupId);
		}
		
		for (Sense sense : kanjiKanaPair.getSenseList()) {
			
			List<Gloss> glossEngList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
			List<SenseAdditionalInfo> additionalInfoEngList = sense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
			
			for (Gloss gloss : glossEngList) {
				
				result.add("Translate: " + gloss.getValue());
				
				List<MiscEnum> miscList = sense.getMiscList();
				
				if (miscList != null && miscList.size() > 0) {
					
					for (MiscEnum miscEnum : miscList) {
						result.add("MiscInfo: " + dictionaryEntryJMEdictEntityMapper.getMiscEnumAsEntity(miscEnum));
					}
				}
								
				if (additionalInfoEngList != null && additionalInfoEngList.size() > 0) {
					
					for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoEngList) {
						result.add("AdditionalInfo: " + senseAdditionalInfo.getValue());
					}
				}
				
				List<DialectEnum> dialectList = sense.getDialectList();
				
				if (dialectList != null && dialectList.size() > 0) {
					for (DialectEnum dialectEnum : dialectList) {
						result.add("Dialect: " + dictionaryEntryJMEdictEntityMapper.getDialectEnumAsEntity(dialectEnum));
					}				
				}
			}
		}
				
		return Helper.convertListToString(result);
	} 
	
	public List<String> generateTranslatesInOldFormat(KanjiKanaPair kanjiKanaPair, String missingWord) throws Exception {
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		List<String> translateList = new ArrayList<String>();
		
		if (missingWord != null) {
			translateList.add("_");
			translateList.add("-----------");
		}

		// wczytanie starego slownika i sche'owanie go		
		Map<String, List<PolishJapaneseEntry>> polishJapaneseEntriesCache = oldWordGeneratorHelper.getPolishJapaneseEntriesCache();
		
		List<PolishJapaneseEntry> findPolishJapaneseEntry = Helper.findPolishJapaneseEntry(polishJapaneseEntriesCache, kanjiKanaPair.getKanji(), kanjiKanaPair.getKana());;
				
		if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
			translateList.add("JUZ JEST");
			translateList.add("-----------");
		}
		
		if (missingWord != null) {
			translateList.add(missingWord);
			translateList.add("-----------");
		}
			
		for (Sense sense : kanjiKanaPair.getSenseList()) {
			
			List<Gloss> glossEngList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
			List<SenseAdditionalInfo> additionalInfoEngList = sense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());
			
			for (Gloss gloss : glossEngList) {

				StringBuffer translate = new StringBuffer(gloss.getValue());
				
				if (additionalInfoEngList != null && additionalInfoEngList.size() > 0) {
					
					for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoEngList) {
						translate.append("\n     " + senseAdditionalInfo.getValue());
					}
				}
				
				List<MiscEnum> miscList = sense.getMiscList();
				
				if (miscList != null && miscList.size() > 0) {
					
					for (MiscEnum miscEnum : miscList) {
						translate.append("\n     " + miscEnum.value());
					}
				}
								
				List<DialectEnum> dialectList = sense.getDialectList();
				
				if (dialectList != null && dialectList.size() > 0) {
					for (DialectEnum dialectEnum : dialectList) {
						translate.append("\n     " + dictionaryEntryJMEdictEntityMapper.getDialectEnumAsEntity(dialectEnum));
					}				
				}
				
				translateList.add(translate.toString());
			}
		}
						
		return translateList;
	}
	
	public List<String> generateAdditionalInfoInOldFormat(KanjiKanaPair kanjiKanaPair, WordType wordType) throws Exception {

		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		
		List<String> additionalInfoList = new ArrayList<>();
		
		KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
		
		if (kanjiInfo != null) {
			List<KanjiAdditionalInfoEnum> kanjiAdditionalInfoList = kanjiInfo.getKanjiAdditionalInfoList();
			
			for (KanjiAdditionalInfoEnum kanjiAdditionalInfoEnum : kanjiAdditionalInfoList) {
				additionalInfoList.add(dictionaryEntryJMEdictEntityMapper.getKanjiAdditionalInfoEnumAsEntity(kanjiAdditionalInfoEnum));
			}
		}
		
		List<ReadingAdditionalInfoEnum> readingAdditionalInfoList = kanjiKanaPair.getReadingInfo().getReadingAdditionalInfoList();
		
		for (ReadingAdditionalInfoEnum readingAdditionalInfoEnum : readingAdditionalInfoList) {
			additionalInfoList.add(dictionaryEntryJMEdictEntityMapper.getReadingAdditionalInfoEnumAsEntity(readingAdditionalInfoEnum));
		}
		
		// w stosunku do starej implementacji wystepuje drobna roznica, ale jest to malo istotna zmiana, zmiana ta wynika najprawdopodobniej z lekko innej implementacji wczytywania jmdict
		/*
		if (kanjiKanaPair.getSenseList().size() == 1) {
			for (Sense sense : kanjiKanaPair.getSenseList()) {	
					
				List<SenseAdditionalInfo> additionalInfoEngList = sense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("eng") == true)).collect(Collectors.toList());

				if (additionalInfoEngList != null && additionalInfoEngList.size() > 0) {
					
					for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoEngList) {
						additionalInfoList.add("     " + senseAdditionalInfo.getValue());
					}
				}
				
				List<MiscEnum> miscList = sense.getMiscList();
				
				if (miscList != null && miscList.size() > 0) {
					
					for (MiscEnum miscEnum : miscList) {
						String addText = "     " + miscEnum.value();
						
						if (additionalInfoList.contains(addText) == false) {
							additionalInfoList.add(addText);
						}
					}
				}
			}
		}
		*/
		
		//
		
		List<String> translateList = new ArrayList<>();
		
		for (Sense sense : kanjiKanaPair.getSenseList()) {			
			List<Gloss> glossEngList = sense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("eng") == true)).collect(Collectors.toList());
			
			for (Gloss gloss : glossEngList) {
				translateList.add(gloss.getValue());
			}
		}
		
	    if (	(wordType == WordType.KATAKANA || wordType == WordType.KATAKANA_EXCEPTION) &&
	    		translateList.size() == 1) {
	    	
	    	additionalInfoList.add("ang: " + translateList.get(0));	    	
	    }
		
		return additionalInfoList;
	}
	
	public int mapRelativePriorityToPower(RelativePriorityEnum relativePriorityEnum, int start) {
		
		Map<RelativePriorityEnum, Integer> jmedictPriorityMap = new HashMap<RelativePriorityEnum, Integer>();
				
		jmedictPriorityMap.put(RelativePriorityEnum.GAI_1, 1);
		jmedictPriorityMap.put(RelativePriorityEnum.ICHI_1, 2);
		jmedictPriorityMap.put(RelativePriorityEnum.NEWS_1, 3);
		jmedictPriorityMap.put(RelativePriorityEnum.SPEC_1, 4);
		
		jmedictPriorityMap.put(RelativePriorityEnum.GAI_2, 5);
		jmedictPriorityMap.put(RelativePriorityEnum.ICHI_2, 6);
		jmedictPriorityMap.put(RelativePriorityEnum.NEWS_2, 7);
		jmedictPriorityMap.put(RelativePriorityEnum.SPEC_2, 8);

		jmedictPriorityMap.put(RelativePriorityEnum.NF_01, 9);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_02, 10);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_03, 11);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_04, 12);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_05, 13);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_06, 14);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_07, 15);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_08, 16);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_09, 17);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_10, 18);

		jmedictPriorityMap.put(RelativePriorityEnum.NF_11, 19);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_12, 20);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_13, 21);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_14, 22);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_15, 23);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_16, 24);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_17, 25);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_18, 26);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_19, 27);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_20, 28);
		
		jmedictPriorityMap.put(RelativePriorityEnum.NF_21, 29);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_22, 30);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_23, 31);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_24, 32);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_25, 33);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_26, 34);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_27, 35);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_28, 36);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_29, 37);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_30, 38);

		jmedictPriorityMap.put(RelativePriorityEnum.NF_31, 39);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_32, 40);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_33, 41);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_34, 42);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_35, 43);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_36, 44);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_37, 45);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_38, 46);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_39, 47);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_40, 48);

		jmedictPriorityMap.put(RelativePriorityEnum.NF_41, 49);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_42, 50);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_43, 51);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_44, 52);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_45, 53);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_46, 54);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_47, 55);
		jmedictPriorityMap.put(RelativePriorityEnum.NF_48, 56);
		
		Integer power = jmedictPriorityMap.get(relativePriorityEnum);
		
		if (power == null) {
			throw new RuntimeException("Can't find power for: " + relativePriorityEnum);
		}
		
		return start + power;		
	}
	
	public boolean containsInPartOfSpeechInSenseList(List<Sense> senseList, PartOfSpeechEnum partOfSpeech) {
		
		for (Sense sense : senseList) {
			
			if (sense.getPartOfSpeechList().contains(partOfSpeech) == true) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsInMiscList(List<Sense> senseList, MiscEnum misc) {
		
		for (Sense sense : senseList) {
			
			if (sense.getMiscList().contains(misc) == true) {
				return true;
			}
		}
		
		return false;
	}

	//
			
	public static class EntryAdditionalData {		
		private Map<Integer, EntryAdditionalDataEntry> jmdictEntryAdditionalDataEntryMap = new TreeMap<>();
	}
	
	private static class EntryAdditionalDataEntry {
		
		private List<PolishJapaneseEntry> oldPolishJapaneseEntryList;
		
		private Map<Integer, EntryAdditionalDataEntry$UpdateDictionarySense> updateDictionarySenseMap;
		
		private List<EntryAdditionalDataEntry$UpdateDictionarySense> deleteDictionarySenseListDuringUpdateDictionary;
		
		private Map<Integer, EntryAdditionalDataEntry$ProposalNewPolishTranslate> proposalNewPolishTranslateMap;
		
	}
	
	private static class EntryAdditionalDataEntry$UpdateDictionarySense {
		
		private boolean englishGlossListEquals;
		private boolean englishAdditionalInfoListEquals;

		private List<Gloss> oldEnglishGlossList;		
		private List<SenseAdditionalInfo> oldEnglishSenseAdditionalInfoList;
		
		private List<Gloss> oldPolishGlossList;		
		private List<SenseAdditionalInfo> oldPolishSenseAdditionalInfoList;

		public EntryAdditionalDataEntry$UpdateDictionarySense(boolean englishGlossListEquals,  boolean englishAdditionalInfoListEquals,
				List<Gloss> oldEnglishGlossList, List<SenseAdditionalInfo> oldEnglishSenseAdditionalInfoList,
				List<Gloss> oldPolishGlossList, List<SenseAdditionalInfo> oldPolishSenseAdditionalInfoList) {
			
			this.englishGlossListEquals = englishGlossListEquals;
			this.englishAdditionalInfoListEquals = englishAdditionalInfoListEquals;
			
			this.oldEnglishGlossList = oldEnglishGlossList;
			this.oldEnglishSenseAdditionalInfoList = oldEnglishSenseAdditionalInfoList;
			
			this.oldPolishGlossList = oldPolishGlossList;
			this.oldPolishSenseAdditionalInfoList = oldPolishSenseAdditionalInfoList;
		}
	}
	
	private static class EntryAdditionalDataEntry$ProposalNewPolishTranslate {
		
		private boolean polishGlossListEquals;
		private boolean polishAdditionalInfoListEquals;
		
		private List<Gloss> proposalPolishGlossList;		
		private List<SenseAdditionalInfo> proposalPolishSenseAdditionalInfoList;

		public EntryAdditionalDataEntry$ProposalNewPolishTranslate(
				boolean polishGlossListEquals,  boolean polishAdditionalInfoListEquals,
				List<Gloss> proposalPolishGlossList, List<SenseAdditionalInfo> proposalPolishSenseAdditionalInfoList) {
			
			this.polishGlossListEquals = polishGlossListEquals;
			this.polishAdditionalInfoListEquals = polishAdditionalInfoListEquals;
						
			this.proposalPolishGlossList = proposalPolishGlossList;
			this.proposalPolishSenseAdditionalInfoList = proposalPolishSenseAdditionalInfoList;
		}
	}
}
