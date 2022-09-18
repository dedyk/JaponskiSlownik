package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.JMnedict;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.TranslationalInfo;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.TranslationalInfoTransDet;

public class Dictionary2NameHelper {
	
	private static Dictionary2NameHelper dictionary2NameHelper;

	private Dictionary2NameHelper() { }
	
	public static Dictionary2NameHelper getOrInit() {
		
		if (dictionary2NameHelper == null) {
			dictionary2NameHelper =  init();
		}
		
		return dictionary2NameHelper;				
	}
	
	public static Dictionary2NameHelper init() {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		Dictionary2NameHelper dictionary2NameHelper = new Dictionary2NameHelper();
		
		dictionary2NameHelper.jmnedictFile = new File("../JapaneseDictionary_additional/JMnedict.xml");
				
		return dictionary2NameHelper;
	}
	
	//
	
	private File jmnedictFile;	
	private JMnedict jmnedict = null;
	
	private Map<String, List<JMnedict.Entry>> jmnedictEntryKanjiKanaCache;

	public JMnedict getJMnedict() throws Exception {
		
		if (jmnedict == null) {

			// walidacja xsd pliku JMdict
			System.out.println("Validating JMNedict");
			
			// walidacja xsd
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			Schema schema = factory.newSchema(Dictionary2Helper.class.getResource("/pl/idedyk/japanese/dictionary2/jmnedict/xsd/JMnedict.xsd"));
			
			Validator validator = schema.newValidator();
						
			validator.validate(new StreamSource(jmnedictFile));			

			// wczytywanie pliku JMdict
			System.out.println("Reading JMNedict");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(JMnedict.class);              

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			jmnedict = (JMnedict) jaxbUnmarshaller.unmarshal(jmnedictFile);		
			
			//
			
			// uzupelnienie o jezyk domyslny
			List<JMnedict.Entry> entryList = jmnedict.getEntryList();
			
			for (JMnedict.Entry entry : entryList) {
								
				for (TranslationalInfo translationalInfo : entry.getTranslationInfo()) {
					
					for (TranslationalInfoTransDet translationalInfoTransDet : translationalInfo.getTransDet()) {
						
						if (translationalInfoTransDet.getLang() == null) {
							translationalInfoTransDet.setLang("eng");
						}
					}					
				}
			}
		}
		
		return jmnedict;
	}

	public List<JMnedict.Entry> findEntryListInJmndict(PolishJapaneseEntry polishJapaneseEntry, boolean addFoundNotMatchedEntries) throws Exception {
		
		List<JMnedict.Entry> result = new ArrayList<>();
		
		if (polishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.TO_DELETE) == true) {
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
		List<JMnedict.Entry> entryListForKanjiAndKana = findEntryListByKanjiAndKana(kanji, kana);

		// pobieramy identyfikator entry
		Integer polishJapaneseEntryEntryId = polishJapaneseEntry.getGroupIdFromJmedictRawDataList();
		
		// na znalezionej liscie entry list pobieramy ten, ktory jest wskazany przy slowku
		if (polishJapaneseEntryEntryId != null && entryListForKanjiAndKana != null && entryListForKanjiAndKana.size() > 0) {
			
			// szukamy grupy na podstawie id zawartego w jmedict raw data (rozwiazuje to problem multigroup)
			for (JMnedict.Entry entry : entryListForKanjiAndKana) {
				
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
	
	public List<JMnedict.Entry> findEntryListByKanjiAndKana(String kanji, String kana) throws Exception {
		
		// inicjalizacja cache
		initJmnedictEntryKanjiKanaCache();
		
		// wyliczenie klucza
		String key = getKanjiKanaKeyForCache(kanji, kana);
		
		return jmnedictEntryKanjiKanaCache.get(key);
	}

	private void initJmnedictEntryKanjiKanaCache() throws Exception {
		
		// inicjalizacja jmdict
		getJMnedict();

		if (jmnedictEntryKanjiKanaCache == null) {
			
			System.out.println("Caching JMnedict by kanji and kana");
			
			jmnedictEntryKanjiKanaCache = new TreeMap<>();
			
			List<JMnedict.Entry> entryList = jmnedict.getEntryList();
			
			for (JMnedict.Entry entry : entryList) {
				
				// generowanie wszystkich kanji i ich czytan
				List<NameKanjiKanaPair> kanjiKanaPairListforEntry = getNameKanjiKanaPairList(entry);
				
				// chodzenie po wszystkich kanji i kana
				for (NameKanjiKanaPair nameKanjiKanaPair : kanjiKanaPairListforEntry) {
					
					String kanji = nameKanjiKanaPair.getKanji();
					String kana = nameKanjiKanaPair.getKana();
					
					String kanjiKanaKey = getKanjiKanaKeyForCache(kanji, kana);
					
					// sprawdzamy, czy taki klucz juz wystepuje w cache'u
					List<JMnedict.Entry> entryListForKanjiKanaKey = jmnedictEntryKanjiKanaCache.get(kanjiKanaKey);
					
					if (entryListForKanjiKanaKey == null) {
						entryListForKanjiKanaKey = new ArrayList<>();
						
						jmnedictEntryKanjiKanaCache.put(kanjiKanaKey, entryListForKanjiKanaKey);
					}
					
					if (entryListForKanjiKanaKey.contains(entry) == false) {
						entryListForKanjiKanaKey.add(entry);
					}
				}				
			}			
		}
	}	
	
	public List<NameKanjiKanaPair> getNameKanjiKanaPairList(JMnedict.Entry entry) {
		
		List<NameKanjiKanaPair> result = new ArrayList<>();
		
		//
		
		List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
		List<ReadingInfo> readingInfoList = entry.getReadingInfoList();
		
		// jesli nie ma kanji
		if (kanjiInfoList.size() > 0) {
			
			// zlaczenie kanji z kana
			
			for (KanjiInfo kanjiInfo : kanjiInfoList) {
				for (ReadingInfo readingInfo : readingInfoList) {
					
					// pobierz kanji
					String kanji = kanjiInfo.getKanji();
															
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
					result.add(new NameKanjiKanaPair(kanjiInfo, readingInfo));					
				}				
			}
		}
				
		// dopasowanie listy translationalInfo do danego kanji i kana
		List<TranslationalInfo> translationalInfoList = entry.getTranslationInfo();
				
		for (NameKanjiKanaPair nameKanjiKanaPair : result) {
						
			// chodzimy po wszystkich translationalInfo i sprawdzamy, czy mozemy je dodac do naszej pary kanji i kana
			for (TranslationalInfo translationalInfo : translationalInfoList) {
								
				// dodajemy ten sens do danej pary				
				nameKanjiKanaPair.getTranslationalInfoList().add(translationalInfo);
			}
		}
		
		return result;
	}
	
	private String getKanjiKanaKeyForCache(String kanji, String kana) {
		return kanji + "." + kana;
	}
	
	public NameKanjiKanaPair findNameKanjiKanaPair(PolishJapaneseEntry polishJapaneseEntry) throws Exception {
		
		List<JMnedict.Entry> entryList = findEntryListInJmndict(polishJapaneseEntry, false);
		
		if (entryList.size() == 1) {
						
			// generowanie wszystkich kanji i ich czytan
			List<NameKanjiKanaPair> nameKanjiKanaPairListforEntry = getNameKanjiKanaPairList(entryList.get(0));

			return findNameKanjiKanaPair(nameKanjiKanaPairListforEntry, polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());			
		}
		
		return null;
	}
	
	public NameKanjiKanaPair findNameKanjiKanaPair(List<NameKanjiKanaPair> nameKanjiKanaPairListforEntry, final String kanji, final String kana) {
				
		// odnalezienie wlaciwej pary			
		Optional<NameKanjiKanaPair> KanjiKanaPairOptional = nameKanjiKanaPairListforEntry.stream().filter(kanjiKanaPair -> {
			
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

	//
	
	public static class NameKanjiKanaPair {
		
		private KanjiInfo kanjiInfo;		
		private ReadingInfo readingInfo;
				
		private List<TranslationalInfo> translationalInfoList = new ArrayList<>();

		public NameKanjiKanaPair(KanjiInfo kanjiInfo, ReadingInfo readingInfo) {
			this.kanjiInfo = kanjiInfo;
			this.readingInfo = readingInfo;
		}

		public KanjiInfo getKanjiInfo() {
			return kanjiInfo;
		}

		public ReadingInfo getReadingInfo() {
			return readingInfo;
		}

		public List<TranslationalInfo> getTranslationalInfoList() {
			return translationalInfoList;
		}
		
		public String getKanji() {
			
			if (kanjiInfo == null) {
				return null;
			}
			
			return kanjiInfo.getKanji();			
		}
		
		public String getKana() {
			
			if (readingInfo == null) {
				return null;
			}

			return readingInfo.getKana();
		}
				
		@Override
		public String toString() {
			return "KanjiKanaPair [kanji=" + kanjiInfo + ", kana=" + readingInfo + "]";
		}
	}

}
