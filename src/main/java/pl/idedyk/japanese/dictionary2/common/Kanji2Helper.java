package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfoReference;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfoReferenceTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.HeaderInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Misc2Info;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.MiscInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.MiscInfoVariant;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.MiscInfoVariantTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.QueryCodeInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.QueryCodeInfoCode;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.QueryCodeInfoCodeSkipMisClassEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.QueryCodeInfoCodeType;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfoValue;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfoValueTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroup;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupAdditionalInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaning;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupReading;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum;

public class Kanji2Helper {
	
	private static final int CSV_COLUMNS = 7; 
	
	private static Kanji2Helper kanji2Helper;
	
	// source kanji dict
	private File kanjidic2File;
	private Kanjidic2 kanjidic2 = null;
	private Map<String, KanjiCharacterInfo> kanjidic2Cache;
	
	// new kanji polish dictionary file
	private File polishDictionaryKanjidic2File;
	private Kanjidic2 polishDictionaryKanjidic2;
	private Map<String, KanjiCharacterInfo> polishDictionaryKanjidic2Cache;
	
	// old kanji polish dictionary file
	private File oldKanjiPolishDictionaryFile;
	
	private List<KanjiEntryForDictionary> oldKanjiPolishDictionaryList;
	private Map<String, KanjiEntryForDictionary> oldKanjiPolishDictionaryMap;
	
	private Kanji2Helper() { }
	
	public static Kanji2Helper getOrInit() {
		
		if (kanji2Helper == null) {
			kanji2Helper =  init();
		}
		
		return kanji2Helper;				
	}
	
	private static Kanji2Helper init() {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		Kanji2Helper kanji2Helper = new Kanji2Helper();
		
		kanji2Helper.polishDictionaryKanjidic2File = new File("input/kanji2.csv");
		
		//
				
		kanji2Helper.kanjidic2File = new File("../JapaneseDictionary_additional/kanjidic2.xml");
		
		kanji2Helper.oldKanjiPolishDictionaryFile = new File("input/kanji.csv");
		
		//
		
		return kanji2Helper;
	}
	
	public Kanjidic2 getKanjidic2() throws Exception {
		
		if (kanjidic2 == null) {
			// walidacja xsd pliku JMdict
			System.out.println("Validating Kanjidic2");
			
			// walidacja xsd
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			Schema schema = factory.newSchema(Kanjidic2.class.getResource("/pl/idedyk/japanese/dictionary2/kanjidic2/xsd/kanjidic2.xsd"));
			Validator validator = schema.newValidator();
						
			validator.validate(new StreamSource(kanjidic2File));			

			// wczytywanie pliku kanjidic2
			System.out.println("Reading Kanjidic2");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Kanjidic2.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			kanjidic2 = (Kanjidic2) jaxbUnmarshaller.unmarshal(kanjidic2File);
			
			// uzupelnienie i usuniecie zbednych rzeczy
			List<KanjiCharacterInfo> characterInfoList = kanjidic2.getCharacterList();
			
			for (KanjiCharacterInfo characterInfo : characterInfoList) {
				ReadingMeaningInfo readingMeaning = characterInfo.getReadingMeaning();
				
				if (readingMeaning != null) {
					
					ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaning.getReadingMeaningGroup();
					
					if (readingMeaningGroup != null) {
						
						List<ReadingMeaningInfoReadingMeaningGroupReading> readingList = readingMeaningGroup.getReadingList();
						Iterator<ReadingMeaningInfoReadingMeaningGroupReading> readingListIterator = readingList.iterator();
						
						while (readingListIterator.hasNext() == true) {
							ReadingMeaningInfoReadingMeaningGroupReading readingMeaningInfoReadingMeaningGroupReading = readingListIterator.next();
							
							ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum readingMeaningInfoReadingMeaningGroupReadingType = readingMeaningInfoReadingMeaningGroupReading.getType();
							
							// usuwanie czytan z jezykow innych niz japonskie
							if (Arrays.asList(ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_ON, ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_KUN).contains(readingMeaningInfoReadingMeaningGroupReadingType) == false) {
								readingListIterator.remove();
								
								continue;
							}					
						}
						
						List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningList = readingMeaningGroup.getMeaningList();
						Iterator<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningListIterator = meaningList.iterator();
						
						while (meaningListIterator.hasNext() == true) {
							ReadingMeaningInfoReadingMeaningGroupMeaning readingMeaningInfoReadingMeaningGroupMeaning = meaningListIterator.next();
							
							ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum readingMeaningInfoReadingMeaningGroupMeaningLang = readingMeaningInfoReadingMeaningGroupMeaning.getLang();
							
							if (readingMeaningInfoReadingMeaningGroupMeaningLang == null) { // ustawienie, ze jest to znaczenie w jezyku angielskim
								readingMeaningInfoReadingMeaningGroupMeaningLang = ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN;
								
								readingMeaningInfoReadingMeaningGroupMeaning.setLang(readingMeaningInfoReadingMeaningGroupMeaningLang);
								
							} else if (readingMeaningInfoReadingMeaningGroupMeaningLang == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL) { // polskie znaczenie
								// noop
								
							} else { // usuwamy inne znaczenia
								meaningListIterator.remove();
								
								continue;
							}					
						}	
					}	
				}				
			}
		}
		
		return kanjidic2;
	}
	
	private void createKanjidic2Cache() throws Exception {
		
		Kanjidic2 kanjidic2 = getKanjidic2();
		
		if (kanjidic2Cache == null) {
			
			kanjidic2Cache = new TreeMap<String, KanjiCharacterInfo>();
			
			for (KanjiCharacterInfo characterInfo : kanjidic2.getCharacterList()) {
				kanjidic2Cache.put(characterInfo.getKanji(), characterInfo);
			}			
		}
	}
	
	public KanjiCharacterInfo getKanjiFromKanjidic2(String kanji) throws Exception {
		
		createKanjidic2Cache();
		
		return kanjidic2Cache.get(kanji);
	}
	
	public void saveKanjidic2AsXml(Kanjidic2 kanjidic2, File file) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Kanjidic2.class);              

		//
				
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		//
		
		jaxbMarshaller.marshal(kanjidic2, file);
	}
	
	public Kanjidic2 getPolishDictionaryKanjidic2() throws Exception {
		
		readPolishDictionaryKanjidic2();
		
		return polishDictionaryKanjidic2;
	}
	
	private Kanjidic2 readPolishDictionaryKanjidic2() throws Exception {
		
		if (polishDictionaryKanjidic2 == null) {
			System.out.println("Reading polish dictionary Kanjidic2");
			
			polishDictionaryKanjidic2 = readKanjidic2FromHumanCsv(polishDictionaryKanjidic2File);
		}		
		
		return polishDictionaryKanjidic2;
	}
	
	private void createPolishDictionaryKanjidic2Cache() throws Exception {
		
		Kanjidic2 polishDictionaryKanjidic2 = readPolishDictionaryKanjidic2();
		
		if (polishDictionaryKanjidic2Cache == null) {
			
			polishDictionaryKanjidic2Cache = new TreeMap<String, KanjiCharacterInfo>();
			
			for (KanjiCharacterInfo characterInfo : polishDictionaryKanjidic2.getCharacterList()) {
				polishDictionaryKanjidic2Cache.put(characterInfo.getKanji(), characterInfo);
				
			}
		}
	}
	
	public KanjiCharacterInfo getKanjiFromPolishDictionaryKanjidic2(String kanji) throws Exception {
		
		createPolishDictionaryKanjidic2Cache();
		
		return polishDictionaryKanjidic2Cache.get(kanji);		
	}
	
	public void addKanjiToPolishDictionary(KanjiCharacterInfo characterInfo) throws Exception {
		
		readPolishDictionaryKanjidic2();
		
		if (polishDictionaryKanjidic2Cache.get(characterInfo.getKanji()) != null) {
			throw new Exception("Can't add already added kanji: " + characterInfo.getKanji());
		}
				
		polishDictionaryKanjidic2.getCharacterList().add(characterInfo);
		polishDictionaryKanjidic2Cache.put(characterInfo.getKanji(), characterInfo);		
	}
	
	public void deleteKanjiFromPolishDictionary(String kanji) throws Exception {
		
		readPolishDictionaryKanjidic2();
		createPolishDictionaryKanjidic2Cache();
		
		KanjiCharacterInfo characterInfoInPolishDictionaryKanjidic2 = polishDictionaryKanjidic2Cache.get(kanji);
		
		if (characterInfoInPolishDictionaryKanjidic2 == null) {
			throw new Exception("Can't delete kanji: " + kanji);
		}
				
		polishDictionaryKanjidic2.getCharacterList().remove(characterInfoInPolishDictionaryKanjidic2);
		polishDictionaryKanjidic2Cache.remove(kanji);		
	}
	
	public void updateKanjiInPolishDictionary(KanjiCharacterInfo characterInfo) throws Exception {
		
		readPolishDictionaryKanjidic2();
		
		if (polishDictionaryKanjidic2Cache.get(characterInfo.getKanji()) == null) {
			throw new Exception("Can't update kanji: " + characterInfo.getKanji());
		}
		
		for (int idx = 0; idx < polishDictionaryKanjidic2.getCharacterList().size(); ++idx) {
			
			KanjiCharacterInfo oldCharacterInfo = polishDictionaryKanjidic2.getCharacterList().get(idx);
			
			if (oldCharacterInfo.getKanji().equals(characterInfo.getKanji()) == true) {				
				polishDictionaryKanjidic2.getCharacterList().set(idx, characterInfo);		
			}			
		}
			
		polishDictionaryKanjidic2Cache.put(characterInfo.getKanji(), characterInfo);			
	}
	
	public void validateAllKanjisInPolishDictionaryList() throws Exception {
		
		readPolishDictionaryKanjidic2();
		
		//
		
		boolean wasError = false;
		
		// sprawdzenie, czy gdzies nie ma duplikatu znaczen
		for (KanjiCharacterInfo characterInfo : polishDictionaryKanjidic2.getCharacterList()) {
			
			ReadingMeaningInfo readingMeaning = characterInfo.getReadingMeaning();
			
			if (readingMeaning != null) {
				ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaning.getReadingMeaningGroup();
				
				if (readingMeaningGroup != null) {
					List<String> readingPolMeaningList = readingMeaningGroup.getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
					map(meaning -> meaning.getValue()).collect(Collectors.toList());
					
					if (readingPolMeaningList.size() > 0) {
						
						Set<String> uniqueReadingPolMeaningSet = new TreeSet<>(readingPolMeaningList);
						
						if (uniqueReadingPolMeaningSet.size() != readingPolMeaningList.size()) { // mamy duplikat
														
							System.out.println("[Error] Kanji reading pol meaning duplicate for " + characterInfo.getKanji() + " - " + readingPolMeaningList);
							
							wasError = true;
						}
					}
				}
			}
		}	
		
		if (wasError == true) { // byl jakis blad			
			throw new Exception("Error");			
		}
	}

	public List<KanjiEntryForDictionary> getOldKanjiPolishDictionaryList() throws IOException, JapaneseDictionaryException {
		
		if (oldKanjiPolishDictionaryList == null) {
			System.out.println("Reading old kanji polish dictionary file");
			
			oldKanjiPolishDictionaryList = CsvReaderWriter.parseKanjiEntriesFromCsv(oldKanjiPolishDictionaryFile.getPath(), null, false);
		}
		
		return oldKanjiPolishDictionaryList;
	}
	
	public Map<String, KanjiEntryForDictionary> getOldKanjiPolishDictionaryMap() throws IOException, JapaneseDictionaryException {
		
		if (oldKanjiPolishDictionaryMap == null) {
			System.out.println("Creating old kanji polish dictionary map");
			
			List<KanjiEntryForDictionary> oldKanjiPolishDictionaryList = getOldKanjiPolishDictionaryList();
			
			oldKanjiPolishDictionaryMap = new TreeMap<>();
			
			for (KanjiEntryForDictionary kanjiEntryForDictionary : oldKanjiPolishDictionaryList) {
				oldKanjiPolishDictionaryMap.put(kanjiEntryForDictionary.getKanji(), kanjiEntryForDictionary);
			}			
		}
		
		return oldKanjiPolishDictionaryMap;
	}
	
	public KanjiEntryForDictionary getOldKanjiEntryForDictionary(String kanji) throws IOException, JapaneseDictionaryException {
		Map<String, KanjiEntryForDictionary> oldKanjiPolishDictionaryMap = getOldKanjiPolishDictionaryMap();
		
		return oldKanjiPolishDictionaryMap.get(kanji);
	}
	
	public KanjiCharacterInfo addDatasFromOldKanjiEntryForDictionary(KanjiCharacterInfo characterInfo, KanjiEntryForDictionary oldKanjiEntryForDictionary) {
		
		// tworzymy kopie, aby nie modyfikowac glownego elementu
		characterInfo = (KanjiCharacterInfo)SerializationUtils.clone(characterInfo);
		
		characterInfo.setId(oldKanjiEntryForDictionary.getId());
		
		Misc2Info misc2 = characterInfo.getMisc2();
		
		if (misc2 == null) {
			misc2 = new Misc2Info();
			
			characterInfo.setMisc2(misc2);
		}
		
		misc2.getGroups().clear();
		
		List<GroupEnum> groupsInOldKanjiEntryForDictionary = oldKanjiEntryForDictionary.getGroups();
		
		for (GroupEnum groupEnum : groupsInOldKanjiEntryForDictionary) {
			
			// nie dodajemy tych grup, gdyz ta informacja znajduje sie juz w MISC
			if (Arrays.asList(GroupEnum.JOUYOU1, GroupEnum.JOUYOU2, GroupEnum.JOUYOU3, GroupEnum.JOUYOU4, GroupEnum.JOUYOU5, GroupEnum.JOUYOU6, 
					GroupEnum.JOUYOUS, GroupEnum.JINMEIYOU, GroupEnum.JINMEIYOU_JOUYOU).contains(groupEnum) == true) {			
				continue;
			}
			
			// nie dodajemy jeszcze tych grup
			if (Arrays.asList(GroupEnum.JLPT_1, GroupEnum.JLPT_2, GroupEnum.JLPT_3, GroupEnum.JLPT_4, GroupEnum.JLPT_5).contains(groupEnum) == true) {			
				continue;
			}			
			
			misc2.getGroups().add(groupEnum);
		}
		
		misc2.setUsed(oldKanjiEntryForDictionary.isUsed());		
		
		return characterInfo;
	}
	
	public void saveKanjidic2AsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, String fileName, Kanjidic2 kanjidic2, EntryAdditionalData entryAdditionalData) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		// rekord z naglowkiem
		new EntryPartConverterHeaderInfo().writeToCsv(config, csvWriter, kanjidic2);
		
		createEmptyLinesInCsv(config, csvWriter);
		
		// zapisywanie znakow
		List<KanjiCharacterInfo> characterList = kanjidic2.getCharacterList();
		
		for (KanjiCharacterInfo characterInfo : characterList) {
			
			// zapisanie znaku
			saveEntryAsHumanCsv(config, csvWriter, characterInfo, entryAdditionalData);
			
			// rozdzielenie, aby zawartosc byla bardziej przjerzysta
			if (characterInfo != characterList.get(characterList.size() - 1)) {
				createEmptyLinesInCsv(config, csvWriter);
			}			
		}
				
		csvWriter.close();
	}
	
	private void createEmptyLinesInCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter) throws IOException {
		
		boolean useTextQualifier = csvWriter.getUseTextQualifier();
		
		int columnsNo = 0;
				
		// wypelniacz 2
		csvWriter.setUseTextQualifier(false); // takie obejscie dziwnego zachowania
		
		for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
			csvWriter.write("");
		}
		
		csvWriter.endRecord();
		
		//
		
		columnsNo = 0;
		
		// wypelniacz 3			
		for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
			csvWriter.write(null);
		}
		
		csvWriter.endRecord();
		
		//
		
		csvWriter.setUseTextQualifier(useTextQualifier);
	}
	
	private void saveEntryAsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo, EntryAdditionalData entryAdditionalData) throws Exception {
				
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(config, csvWriter, characterInfo);
		
		// codepoint
		new EntryPartConverterCodepoint().writeToCsv(config, csvWriter, characterInfo);
		
		// radical
		new EntryPartConverterRadical().writeToCsv(config, csvWriter, characterInfo);
		
		// misc
		new EntryPartConverterMisc().writeToCsv(config, csvWriter, characterInfo);

		// misc 2
		new EntryPartConverterMisc2().writeToCsv(config, csvWriter, characterInfo);
		
		// dictionary number
		new EntryPartConverterDictionaryNumber().writeToCsv(config, csvWriter, characterInfo);
		
		// query code
		new EntryPartConverterQueryCode().writeToCsv(config, csvWriter, characterInfo);
		
		// reading meaning - nanori
		new EntryPartConverterReadingMeaningNanori().writeToCsv(config, csvWriter, characterInfo);
		
		// reading meaning - reading
		new EntryPartConverterReadingMeaningReading().writeToCsv(config, csvWriter, characterInfo);
		
		// reading meaning - meaning
		new EntryPartConverterReadingMeaningMeaning(EntryHumanCsvFieldType.READING_MEANING_MEANING_ENG).writeToCsv(config, csvWriter, characterInfo, entryAdditionalData);
		new EntryPartConverterReadingMeaningMeaning(EntryHumanCsvFieldType.READING_MEANING_MEANING_POL).writeToCsv(config, csvWriter, characterInfo, entryAdditionalData);
				
		// rekord koncowy
		new EntryPartConverterEnd().writeToCsv(config, csvWriter, characterInfo);		
	}
	
	public Kanjidic2 readKanjidic2FromHumanCsv(File file) throws Exception {
				
		EntryPartConverterHeaderInfo entryPartConverterHeaderInfo = new EntryPartConverterHeaderInfo();
		EntryPartConverterBegin entryPartConverterBegin = new EntryPartConverterBegin();
		EntryPartConverterCodepoint entryPartConverterCodepoint = new EntryPartConverterCodepoint();
		EntryPartConverterRadical entryPartConverterRadical = new EntryPartConverterRadical();
		EntryPartConverterMisc entryPartConverterMisc = new EntryPartConverterMisc();
		EntryPartConverterMisc2 entryPartConverterMisc2 = new EntryPartConverterMisc2();
		EntryPartConverterDictionaryNumber entryPartConverterDictionaryNumber = new EntryPartConverterDictionaryNumber();
		EntryPartConverterQueryCode entryPartConverterQueryCode = new EntryPartConverterQueryCode();
		EntryPartConverterReadingMeaningNanori entryPartConverterReadingMeaningNanori = new EntryPartConverterReadingMeaningNanori();
		EntryPartConverterReadingMeaningReading entryPartConverterReadingMeaningReading = new EntryPartConverterReadingMeaningReading();
		EntryPartConverterReadingMeaningMeaning entryPartConverterReadingMeaningEngMeaning = new EntryPartConverterReadingMeaningMeaning(EntryHumanCsvFieldType.READING_MEANING_MEANING_ENG);
		EntryPartConverterReadingMeaningMeaning entryPartConverterReadingMeaningPolMeaning = new EntryPartConverterReadingMeaningMeaning(EntryHumanCsvFieldType.READING_MEANING_MEANING_POL);
		EntryPartConverterEnd entryPartConverterEnd = new EntryPartConverterEnd();		

		//
		
		CsvReader csvReader = new CsvReader(new FileReader(file), ',');
				
		//
		
		Kanjidic2 kanjidic2 = null;
		KanjiCharacterInfo characterInfo = null;
				
		while (csvReader.readRecord()) {
			
			String fieldTypeString = csvReader.get(0);
			
			if (fieldTypeString.equals("") == true) {
				continue;
			}

			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
						
			if (fieldType == EntryHumanCsvFieldType.HEADER) { // rekord z naglowkiem
				
				kanjidic2 = new Kanjidic2();
				
				entryPartConverterHeaderInfo.parseCsv(csvReader, kanjidic2);
			
			} else if (fieldType == EntryHumanCsvFieldType.BEGIN) { // nowy rekord
				
				characterInfo = new KanjiCharacterInfo();
				
				entryPartConverterBegin.parseCsv(csvReader, characterInfo);
				
			} else if (fieldType == EntryHumanCsvFieldType.END) { // zakonczenie rekordu
				
				entryPartConverterEnd.parseCsv(csvReader, characterInfo);
				
				kanjidic2.getCharacterList().add(characterInfo);
				
			} else if (fieldType == EntryHumanCsvFieldType.CODE_POINT) { // code point
				
				entryPartConverterCodepoint.parseCsv(csvReader, characterInfo);				
				
			} else if (fieldType == EntryHumanCsvFieldType.RADICAL) { // radical
				
				entryPartConverterRadical.parseCsv(csvReader, characterInfo);
				
			} else if (fieldType == EntryHumanCsvFieldType.MISC) { // misc 
				
				entryPartConverterMisc.parseCsv(csvReader, characterInfo);

			} else if (fieldType == EntryHumanCsvFieldType.MISC2) { // misc2 
				
				entryPartConverterMisc2.parseCsv(csvReader, characterInfo);
				
			} else if (fieldType == EntryHumanCsvFieldType.DICTIONARY_NUMBER) { // dictionary number 
				
				entryPartConverterDictionaryNumber.parseCsv(csvReader, characterInfo);
			
			} else if (fieldType == EntryHumanCsvFieldType.QUERY_CODE) { // query code 
				
				entryPartConverterQueryCode.parseCsv(csvReader, characterInfo);
			
			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_NANONI) { // meaning - nanoni 
				
				entryPartConverterReadingMeaningNanori.parseCsv(csvReader, characterInfo);
			
			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_READING) { // meaning - reading 
				
				entryPartConverterReadingMeaningReading.parseCsv(csvReader, characterInfo);
			
			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_ENG) { // meaning - eng 
				
				entryPartConverterReadingMeaningEngMeaning.parseCsv(csvReader, characterInfo);

			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_POL) { // meaning - pol 
				
				entryPartConverterReadingMeaningPolMeaning.parseCsv(csvReader, characterInfo);
				
			} else {				
				throw new RuntimeException(fieldType.name());
			}			
		}
		
		csvReader.close();
		
		return kanjidic2;
	}
	
	private class EntryPartConverterHeaderInfo{

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, Kanjidic2 kanjidic2) throws IOException {
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				
				if (config.shiftCellsGenerateIds == false) {
					csvWriter.write(""); columnsNo++;
					
				} else {
					csvWriter.write(String.valueOf(config.shiftCellsGenerateIdsId)); columnsNo++;
					
					config.shiftCellsGenerateIdsId++;
				}
			}
			
			HeaderInfo headerInfo = kanjidic2.getHeader();
			
			csvWriter.write(EntryHumanCsvFieldType.HEADER.name()); columnsNo++;
			csvWriter.write(headerInfo.getFileVersion()); columnsNo++;
			csvWriter.write(headerInfo.getDatabaseVersion()); columnsNo++;
			
			LocalDate dateOfCreationAsLocalDate = LocalDate.of(
					headerInfo.getDateOfCreation().getYear(), 
					headerInfo.getDateOfCreation().getMonth(), 
					headerInfo.getDateOfCreation().getDay());
			
			csvWriter.write(dateOfCreationAsLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)); columnsNo++;
						
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}

		public void parseCsv(CsvReader csvReader, Kanjidic2 kanjidic2) throws IOException, DatatypeConfigurationException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.HEADER) {
				throw new RuntimeException(fieldType.name());
			}
			
			HeaderInfo headerInfo = new HeaderInfo();
			
			headerInfo.setFileVersion(csvReader.get(1));
			headerInfo.setDatabaseVersion(csvReader.get(2));
			
			LocalDate dateOfCreationAsLocalDate = LocalDate.parse(csvReader.get(3), DateTimeFormatter.ISO_LOCAL_DATE);			
			XMLGregorianCalendar dateOfCreation = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateOfCreationAsLocalDate.toString());
				
			headerInfo.setDateOfCreation(dateOfCreation);
			
			kanjidic2.setHeader(headerInfo);
		}
	}
	
	private class EntryPartConverterBegin {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
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
			csvWriter.write(characterInfo.getKanji()); columnsNo++;
			csvWriter.write(characterInfo.getId().toString()); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}

		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.BEGIN) {
				throw new RuntimeException(fieldType.name());
			}
			
			characterInfo.setKanji(csvReader.get(1));
			characterInfo.setId(Integer.parseInt(csvReader.get(2)));
		}
	}
	
	private class EntryPartConverterCodepoint {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			CodePointInfo codePoint = characterInfo.getCodePoint();
			
			if (codePoint == null) {
				return;
			}
			
			List<CodePointValueInfo> codePointValueList = codePoint.getCodePointValueList();
			
			for (CodePointValueInfo codePointValueInfo : codePointValueList) {
			
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				csvWriter.write(EntryHumanCsvFieldType.CODE_POINT.name()); columnsNo++;
				
				csvWriter.write(codePointValueInfo.getType().value()); columnsNo++;
				csvWriter.write(codePointValueInfo.getValue()); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.CODE_POINT) {
				throw new RuntimeException(fieldType.name());
			}
			
			CodePointInfo codePoint = characterInfo.getCodePoint();
			
			if (codePoint == null) {
				codePoint = new CodePointInfo();
				
				characterInfo.setCodePoint(codePoint);
			}
						
			CodePointValueInfo codePointValueInfo = new CodePointValueInfo();
					
			codePointValueInfo.setType(CodePointValueTypeEnum.fromValue(csvReader.get(1)));
			codePointValueInfo.setValue(csvReader.get(2));
			
			codePoint.getCodePointValueList().add(codePointValueInfo);
		}
	}

	private class EntryPartConverterRadical {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			RadicalInfo radicalInfo = characterInfo.getRadical();
			
			if (radicalInfo == null) {
				return;
			}
			
			List<RadicalInfoValue> radicalValueList = radicalInfo.getRadicalValueList();
			
			for (RadicalInfoValue radicalInfoValue : radicalValueList) {
			
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				csvWriter.write(EntryHumanCsvFieldType.RADICAL.name()); columnsNo++;
				
				csvWriter.write(radicalInfoValue.getRadicalValueType().value()); columnsNo++;
				csvWriter.write(radicalInfoValue.getValue()); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.RADICAL) {
				throw new RuntimeException(fieldType.name());
			}
			
			RadicalInfo radicalInfo = characterInfo.getRadical();
			
			if (radicalInfo == null) {
				radicalInfo = new RadicalInfo();
				
				characterInfo.setRadical(radicalInfo);
			}
						
			RadicalInfoValue radicalInfoValue = new RadicalInfoValue();
					
			radicalInfoValue.setRadicalValueType(RadicalInfoValueTypeEnum.fromValue(csvReader.get(1)));
			radicalInfoValue.setValue(csvReader.get(2));
			
			radicalInfo.getRadicalValueList().add(radicalInfoValue);
		}
	}
	
	private class EntryPartConverterMisc {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			MiscInfo miscInfo = characterInfo.getMisc();
			
			if (miscInfo == null) {
				return;
			}
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(EntryHumanCsvFieldType.MISC.name()); columnsNo++;			
			csvWriter.write(miscInfo.getGrade() != null ? miscInfo.getGrade().toString() : "-"); columnsNo++;
			csvWriter.write(Helper.convertListToString(miscInfo.getStrokeCountList())); columnsNo++;
			
			//
			
			List<MiscInfoVariant> variantList = miscInfo.getVariantList();
			
			StringWriter variantListCsvWriterString = new StringWriter();
			
			CsvWriter variantListCsvWriter = new CsvWriter(variantListCsvWriterString, '|');
			
			for (MiscInfoVariant miscInfoVariant : variantList) {
				
				String miscInfoVariantVarType = miscInfoVariant.getVarType() != null ? miscInfoVariant.getVarType().value() : "-";
				String miscInfoVariantValue = miscInfoVariant.getValue() != null ? miscInfoVariant.getValue() : "-";
																			
				variantListCsvWriter.write(miscInfoVariantVarType);
				variantListCsvWriter.write(miscInfoVariantValue);
				
				variantListCsvWriter.endRecord();
			}
			
			variantListCsvWriter.close();
			
			csvWriter.write(variantListCsvWriterString.toString()); columnsNo++;
			csvWriter.write(miscInfo.getFrequency() != null ? miscInfo.getFrequency().toString() : "-"); columnsNo++;
			csvWriter.write(Helper.convertListToString(miscInfo.getRadicalNameList())); columnsNo++;
			csvWriter.write(miscInfo.getJlpt() != null ? miscInfo.getJlpt().toString() : "-"); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.MISC) {
				throw new RuntimeException(fieldType.name());
			}
			
			MiscInfo miscInfo = characterInfo.getMisc();
			
			if (miscInfo == null) {
				miscInfo = new MiscInfo();
				
				characterInfo.setMisc(miscInfo);
			}
			
			//
			
			miscInfo.setGrade(csvReader.get(1).equals("-") == false ? Integer.valueOf(csvReader.get(1)) : null);
			
			List<String> strokeCountList = Helper.convertStringToList(csvReader.get(2));
			
			for (String strokeCount : strokeCountList) {
				miscInfo.getStrokeCountList().add(Integer.valueOf(strokeCount));
			}
			
			//
			
			String variantListCsvWriterString = csvReader.get(3);
			
			CsvReader variantListCsvReader = new CsvReader(new StringReader(variantListCsvWriterString), '|');
			
			while (variantListCsvReader.readRecord()) {
				
				MiscInfoVariantTypeEnum miscInfoVariantVarType = variantListCsvReader.get(0).equals("-") == false ? MiscInfoVariantTypeEnum.fromValue(variantListCsvReader.get(0)) : null;
				String miscInfoVariantValue = variantListCsvReader.get(1).equals("-") == false ? variantListCsvReader.get(1) : null;

				//
				
				MiscInfoVariant miscInfoVariant = new MiscInfoVariant();
				
				miscInfoVariant.setVarType(miscInfoVariantVarType);
				miscInfoVariant.setValue(miscInfoVariantValue);
				
				//
				
				miscInfo.getVariantList().add(miscInfoVariant);
			}
			
			variantListCsvReader.close();

			//
			
			miscInfo.setFrequency(csvReader.get(4).equals("-") == false ? Integer.valueOf(csvReader.get(4)) : null);
			miscInfo.getRadicalNameList().addAll(Helper.convertStringToList(csvReader.get(5)));
			miscInfo.setJlpt(csvReader.get(6).equals("-") == false ? Integer.valueOf(csvReader.get(6)) : null);			
		}
	}

	private class EntryPartConverterMisc2 {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			Misc2Info misc2Info = characterInfo.getMisc2();
			
			if (misc2Info == null) {
				return;
			}
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(EntryHumanCsvFieldType.MISC2.name()); columnsNo++;			
			csvWriter.write(Helper.convertListToString(misc2Info.getGroups().stream().map(m -> m.getValue()).collect(Collectors.toList()))); columnsNo++;
			csvWriter.write("" + misc2Info.isUsed()); columnsNo++;
						
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.MISC2) {
				throw new RuntimeException(fieldType.name());
			}
			
			Misc2Info misc2Info = characterInfo.getMisc2();
			
			if (misc2Info == null) {
				misc2Info = new Misc2Info();
				
				characterInfo.setMisc2(misc2Info);
			}
			
			//
			
			List<String> misc2GroupsEnumStringList = Helper.convertStringToList(csvReader.get(1));
			
			for (String currentMisc2GroupValue : misc2GroupsEnumStringList) {
				misc2Info.getGroups().add(GroupEnum.getGroupEnum(currentMisc2GroupValue));
			}
			
			//
			
			misc2Info.setUsed(Boolean.valueOf(csvReader.get(2)));
		}
	}
	
	private class EntryPartConverterDictionaryNumber {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			DictionaryNumberInfo dictionaryNumber = characterInfo.getDictionaryNumber();
			
			if (dictionaryNumber == null) {
				return;
			}
			
			List<DictionaryNumberInfoReference> dictionaryReferenceList = dictionaryNumber.getDictionaryReferenceList();
			
			for (DictionaryNumberInfoReference dictionaryNumberInfoReference : dictionaryReferenceList) {
								
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				csvWriter.write(EntryHumanCsvFieldType.DICTIONARY_NUMBER.name()); columnsNo++;
				
				csvWriter.write(dictionaryNumberInfoReference.getDictionaryType().value()); columnsNo++;
				csvWriter.write(dictionaryNumberInfoReference.getMonoVolume() != null ? dictionaryNumberInfoReference.getMonoVolume() : "-"); columnsNo++;
				csvWriter.write(dictionaryNumberInfoReference.getMonoPage() != null ? dictionaryNumberInfoReference.getMonoPage() : "-"); columnsNo++;
				csvWriter.write(dictionaryNumberInfoReference.getValue()); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}			
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.DICTIONARY_NUMBER) {
				throw new RuntimeException(fieldType.name());
			}
			
			DictionaryNumberInfo dictionaryNumber = characterInfo.getDictionaryNumber();

			if (dictionaryNumber == null) {
				dictionaryNumber = new DictionaryNumberInfo();
				
				characterInfo.setDictionaryNumber(dictionaryNumber);
			}
			
			DictionaryNumberInfoReference dictionaryNumberInfoReference = new DictionaryNumberInfoReference();
						
			dictionaryNumberInfoReference.setDictionaryType(DictionaryNumberInfoReferenceTypeEnum.fromValue(csvReader.get(1)));
			dictionaryNumberInfoReference.setMonoVolume(csvReader.get(2).equals("-") == false ? csvReader.get(2) : null); 
			dictionaryNumberInfoReference.setMonoPage(csvReader.get(3).equals("-") == false ? csvReader.get(3) : null);
			dictionaryNumberInfoReference.setValue(csvReader.get(4));		
			
			dictionaryNumber.getDictionaryReferenceList().add(dictionaryNumberInfoReference);
		}
	}

	private class EntryPartConverterQueryCode {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			QueryCodeInfo queryCodeInfo = characterInfo.getQueryCode();
			
			if (queryCodeInfo == null) {
				return;
			}
			
			List<QueryCodeInfoCode> queryCodeList = queryCodeInfo.getQueryCodeList();
			
			for (QueryCodeInfoCode queryCodeInfoCode : queryCodeList) {
								
				int columnsNo = 0;
				
				if (config.shiftCells == true) {
					csvWriter.write(""); columnsNo++;
				}
				
				csvWriter.write(EntryHumanCsvFieldType.QUERY_CODE.name()); columnsNo++;
				
				csvWriter.write(queryCodeInfoCode.getType().value()); columnsNo++;
				csvWriter.write(queryCodeInfoCode.getSkipMisclass() != null ? queryCodeInfoCode.getSkipMisclass().value() : "-"); columnsNo++;
				csvWriter.write(queryCodeInfoCode.getValue()); columnsNo++;
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
					csvWriter.write(null);
				}
				
				csvWriter.endRecord();
			}			
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.QUERY_CODE) {
				throw new RuntimeException(fieldType.name());
			}
			
			QueryCodeInfo queryCodeInfo = characterInfo.getQueryCode();

			if (queryCodeInfo == null) {
				queryCodeInfo = new QueryCodeInfo();
				
				characterInfo.setQueryCode(queryCodeInfo);
			}
			
			QueryCodeInfoCode queryCodeInfoCode = new QueryCodeInfoCode();
						
			queryCodeInfoCode.setType(QueryCodeInfoCodeType.fromValue(csvReader.get(1)));
			queryCodeInfoCode.setSkipMisclass(csvReader.get(2).equals("-") == false ? QueryCodeInfoCodeSkipMisClassEnum.fromValue(csvReader.get(2)) : null); 
			queryCodeInfoCode.setValue(csvReader.get(3));	
			
			queryCodeInfo.getQueryCodeList().add(queryCodeInfoCode);
		}
	}
	
	private class EntryPartConverterReadingMeaningNanori {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();
			
			if (readingMeaningInfo == null) {
				return;
			}
			
			int columnsNo = 0;
						
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
						
			csvWriter.write(EntryHumanCsvFieldType.READING_MEANING_NANONI.name()); columnsNo++;
			csvWriter.write(Helper.convertListToString(readingMeaningInfo.getNanoriList())); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();			
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.READING_MEANING_NANONI) {
				throw new RuntimeException(fieldType.name());
			}
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();

			if (readingMeaningInfo == null) {
				readingMeaningInfo = new ReadingMeaningInfo();
				
				characterInfo.setReadingMeaning(readingMeaningInfo);
			}
			
			readingMeaningInfo.getNanoriList().addAll(Helper.convertStringToList(csvReader.get(1)));
		}
	}

	private class EntryPartConverterReadingMeaningReading {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();
			
			if (readingMeaningInfo == null) {
				return;
			}
			
			ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaningInfo.getReadingMeaningGroup();
			
			if (readingMeaningGroup == null) {
				return;
			}
			
			// podzielenie japonskiego czytania
			List<ReadingMeaningInfoReadingMeaningGroupReading> readingList = readingMeaningGroup.getReadingList();
			
			List<String> jaOnReadingList = new ArrayList<>();
			List<String> jaKunReadingList = new ArrayList<>();
			
			for (ReadingMeaningInfoReadingMeaningGroupReading readingMeaningInfoReadingMeaningGroupReading : readingList) {
				
				ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum type = readingMeaningInfoReadingMeaningGroupReading.getType();
				
				if (type == ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_ON) {
					jaOnReadingList.add(readingMeaningInfoReadingMeaningGroupReading.getValue());
					
				} else if (type == ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_KUN) {
					jaKunReadingList.add(readingMeaningInfoReadingMeaningGroupReading.getValue());
					
				} else {
					throw new RuntimeException(); // to nigdy nie powinno zdarzyc sie, gdyz jest filtrowane podczas wczytywania
				}				
			}
			
			int columnsNo = 0;
						
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
						
			csvWriter.write(EntryHumanCsvFieldType.READING_MEANING_READING.name()); columnsNo++;			
			csvWriter.write(Helper.convertListToString(jaOnReadingList)); columnsNo++;
			csvWriter.write(Helper.convertListToString(jaKunReadingList)); columnsNo++;			
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();			
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.READING_MEANING_READING) {
				throw new RuntimeException(fieldType.name());
			}
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();

			if (readingMeaningInfo == null) {
				readingMeaningInfo = new ReadingMeaningInfo();
				
				characterInfo.setReadingMeaning(readingMeaningInfo);
			}
			
			ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaningInfo.getReadingMeaningGroup();
			
			if (readingMeaningGroup == null) {
				readingMeaningGroup = new ReadingMeaningInfoReadingMeaningGroup();
				
				readingMeaningInfo.setReadingMeaningGroup(readingMeaningGroup);
			}
			
			String jaOnReadingListString = csvReader.get(1);

			List<ReadingMeaningInfoReadingMeaningGroupReading> readingList = new ArrayList<>();
			
			for (String jaOnReading : Helper.convertStringToList(jaOnReadingListString)) {

				ReadingMeaningInfoReadingMeaningGroupReading readingMeaningInfoReadingMeaningGroupReading = new ReadingMeaningInfoReadingMeaningGroupReading();
				
				readingMeaningInfoReadingMeaningGroupReading.setType(ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_ON);
				readingMeaningInfoReadingMeaningGroupReading.setValue(jaOnReading);
				
				readingList.add(readingMeaningInfoReadingMeaningGroupReading);
			}
			
			//
			
			String jaKunReadingList = csvReader.get(2);

			for (String jaKunReading : Helper.convertStringToList(jaKunReadingList)) {

				ReadingMeaningInfoReadingMeaningGroupReading readingMeaningInfoReadingMeaningGroupReading = new ReadingMeaningInfoReadingMeaningGroupReading();
				
				readingMeaningInfoReadingMeaningGroupReading.setType(ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum.JA_KUN);
				readingMeaningInfoReadingMeaningGroupReading.setValue(jaKunReading);
				
				readingList.add(readingMeaningInfoReadingMeaningGroupReading);
			}
			
			//
			
			readingMeaningGroup.getReadingList().addAll(readingList);
		}
	}
		
	private class EntryPartConverterReadingMeaningMeaning {
		
		private EntryHumanCsvFieldType fieldType;
		
		private EntryPartConverterReadingMeaningMeaning(EntryHumanCsvFieldType fieldType) {
			this.fieldType = fieldType;
		}

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo, EntryAdditionalData entryAdditionalData) throws IOException {
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();
			
			if (readingMeaningInfo == null) {
				return;
			}
			
			ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaningInfo.getReadingMeaningGroup();
			
			if (readingMeaningGroup == null) {
				return;
			}

			List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningList = readingMeaningGroup.getMeaningList();
			List<ReadingMeaningInfoReadingMeaningGroupAdditionalInfo> additionalInfoList = readingMeaningGroup.getAdditionalInfoList();
			
			List<String> meaningLangList;
			String additionalInfoLang;
			
			if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_ENG) {
				meaningLangList = meaningList.stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						map(meaning -> meaning.getValue()).collect(Collectors.toList());
				
				additionalInfoLang = additionalInfoList.stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						map(additionalInfo -> additionalInfo.getValue()).findFirst().orElse(null);
				
			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_POL) {
				meaningLangList = meaningList.stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						map(meaning -> meaning.getValue()).collect(Collectors.toList());
				
				additionalInfoLang = additionalInfoList.stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						map(additionalInfo -> additionalInfo.getValue()).findFirst().orElse(null);
				
			} else {
				throw new RuntimeException();
			}
			
			if (meaningLangList.size() == 0 && config.addOldPolishTranslates == false) {
				return;
			}
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
		
			csvWriter.write(fieldType.name());  columnsNo++;
			csvWriter.write(Helper.convertListToString(meaningLangList)); columnsNo++;
			csvWriter.write(additionalInfoLang != null ? additionalInfoLang : ""); columnsNo++;
			
			// uzupelnienie o dodatkowe dane
			if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_POL) {
				
				// sprawdzamy, czy cos zostalo przygotowane
				EntryAdditionalDataEntry entryAdditionalDataEntry = entryAdditionalData.kanjidic2AdditionalDataEntryMap.get(characterInfo.getKanji());

				if (config.addOldPolishTranslates == true && entryAdditionalDataEntry != null && entryAdditionalDataEntry.oldKanjiEntryForDictionary != null) {
					
					KanjiEntryForDictionary oldKanjiEntryForDictionary = entryAdditionalDataEntry.oldKanjiEntryForDictionary;
					
					if (oldKanjiEntryForDictionary.getPolishTranslates().size() != 1 || oldKanjiEntryForDictionary.getPolishTranslates().get(0).equals("-") == false) {
						csvWriter.write("STARE_ZNACZENIE\n" + "---\n---\n" + Helper.convertListToString(oldKanjiEntryForDictionary.getPolishTranslates())); columnsNo++;

						if (oldKanjiEntryForDictionary.getInfo().equals("") == false) {
							csvWriter.write("STARE_INFO\n" + "---\n---\n" + oldKanjiEntryForDictionary.getInfo()); columnsNo++;
						}						
					}					
				}
			}
			
			//
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}
				
		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != this.fieldType) {
				throw new RuntimeException(fieldType.name());
			}
			
			ReadingMeaningInfo readingMeaningInfo = characterInfo.getReadingMeaning();

			if (readingMeaningInfo == null) {
				readingMeaningInfo = new ReadingMeaningInfo();
				
				characterInfo.setReadingMeaning(readingMeaningInfo);
			}
			
			ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaningInfo.getReadingMeaningGroup();
			
			if (readingMeaningGroup == null) {
				readingMeaningGroup = new ReadingMeaningInfoReadingMeaningGroup();
				
				readingMeaningInfo.setReadingMeaningGroup(readingMeaningGroup);
			}
			
			//
			
			List<String> meaningLangList = Helper.convertStringToList(csvReader.get(1));
			String additionalInfo = csvReader.get(2).equals("") == false ? csvReader.get(2) : null; 
			
			ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum lang;
			
			if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_ENG) {
				lang = ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN;					
				
			} else if (fieldType == EntryHumanCsvFieldType.READING_MEANING_MEANING_POL) {
				lang = ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL;
				
			} else {
				throw new RuntimeException();
			}

			
			for (String meaning : meaningLangList) {				
				ReadingMeaningInfoReadingMeaningGroupMeaning readingMeaningInfoReadingMeaningGroupMeaning = new ReadingMeaningInfoReadingMeaningGroupMeaning();
				
				readingMeaningInfoReadingMeaningGroupMeaning.setLang(lang);
				readingMeaningInfoReadingMeaningGroupMeaning.setValue(meaning);
				
				readingMeaningGroup.getMeaningList().add(readingMeaningInfoReadingMeaningGroupMeaning);
			}
			
			if (additionalInfo != null) {				
				ReadingMeaningInfoReadingMeaningGroupAdditionalInfo readingMeaningInfoReadingMeaningGroupAdditionalInfo = new ReadingMeaningInfoReadingMeaningGroupAdditionalInfo();
				
				readingMeaningInfoReadingMeaningGroupAdditionalInfo.setLang(lang);
				readingMeaningInfoReadingMeaningGroupAdditionalInfo.setValue(additionalInfo);
				
				readingMeaningGroup.getAdditionalInfoList().add(readingMeaningInfoReadingMeaningGroupAdditionalInfo);
			}			
		}
	}
		
	public Kanjidic2 createEmptyKanjidic2() throws DatatypeConfigurationException {
		
		Kanjidic2 kanjidic2 = new Kanjidic2();
				
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		
		gregorianCalendar.setTime(new Date());
		
		HeaderInfo headerInfo = new HeaderInfo();
		
		headerInfo.setFileVersion("4");
		headerInfo.setDatabaseVersion("000");
		headerInfo.setDateOfCreation(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
		
		kanjidic2.setHeader(headerInfo);
		
		return kanjidic2;
	}
	
	public void updateHeaderKanjidic2(Kanjidic2 sourceKanjidic2, Kanjidic2 destinationKanjidic2) {
		
		HeaderInfo sourceKanjidic2HeaderInfo = (HeaderInfo)SerializationUtils.clone(sourceKanjidic2.getHeader());
		
		destinationKanjidic2.setHeader(sourceKanjidic2HeaderInfo);
	}
	
	public boolean updatePolishKanjiCharacterInfo(KanjiCharacterInfo sourceKanjiCharacterInfo, KanjiCharacterInfo destinationKanjiCharacterInfo, EntryAdditionalData entryAdditionalData) {
		
		boolean isManuallyNeeded = false;
		
		// aktualizacja docelowego kanji
		destinationKanjiCharacterInfo.setCodePoint((CodePointInfo)SerializationUtils.clone(sourceKanjiCharacterInfo.getCodePoint()));
		destinationKanjiCharacterInfo.setRadical((RadicalInfo)SerializationUtils.clone(sourceKanjiCharacterInfo.getRadical()));
		destinationKanjiCharacterInfo.setMisc((MiscInfo)SerializationUtils.clone(sourceKanjiCharacterInfo.getMisc()));
		destinationKanjiCharacterInfo.setDictionaryNumber((DictionaryNumberInfo)SerializationUtils.clone(sourceKanjiCharacterInfo.getDictionaryNumber()));
		destinationKanjiCharacterInfo.setQueryCode((QueryCodeInfo)SerializationUtils.clone(sourceKanjiCharacterInfo.getQueryCode()));

		//
		
		ReadingMeaningInfo sourceKanjiCharacterInfoReadingMeaning = sourceKanjiCharacterInfo.getReadingMeaning();
		ReadingMeaningInfo destinationKanjiCharacterInfoReadingMeaning = destinationKanjiCharacterInfo.getReadingMeaning();
		
		if (sourceKanjiCharacterInfoReadingMeaning == null && destinationKanjiCharacterInfoReadingMeaning == null) {
			// noop
			
		} else if (sourceKanjiCharacterInfoReadingMeaning == null && destinationKanjiCharacterInfoReadingMeaning != null) {
			destinationKanjiCharacterInfo.setReadingMeaning(null);
			
		} else if (sourceKanjiCharacterInfoReadingMeaning != null && destinationKanjiCharacterInfoReadingMeaning == null) {
			destinationKanjiCharacterInfo.setReadingMeaning((ReadingMeaningInfo)SerializationUtils.clone(sourceKanjiCharacterInfoReadingMeaning));
			
			isManuallyNeeded = true;
			
		} else {
			ReadingMeaningInfo newReadingMeaningInfo = new ReadingMeaningInfo();
			
			newReadingMeaningInfo.getNanoriList().addAll(sourceKanjiCharacterInfoReadingMeaning.getNanoriList());
			
			ReadingMeaningInfoReadingMeaningGroup sourceKanjiCharacterInfoReadingMeaningReadingMeaningGroup = sourceKanjiCharacterInfoReadingMeaning.getReadingMeaningGroup();
			
			if (sourceKanjiCharacterInfoReadingMeaningReadingMeaningGroup != null) {
				ReadingMeaningInfoReadingMeaningGroup newReadingMeaningInfoReadingMeaningGroup = new ReadingMeaningInfoReadingMeaningGroup();
				
				newReadingMeaningInfo.setReadingMeaningGroup(newReadingMeaningInfoReadingMeaningGroup);
				
				//
				
				newReadingMeaningInfoReadingMeaningGroup.getReadingList().addAll(sourceKanjiCharacterInfoReadingMeaningReadingMeaningGroup.getReadingList());
				
				// pobranie angielskich znaczen ze zrodlowego slownika
				List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningEngLangListFromSource = sourceKanjiCharacterInfoReadingMeaningReadingMeaningGroup.getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						collect(Collectors.toList());
				
				List<ReadingMeaningInfoReadingMeaningGroupAdditionalInfo> additionalInfoEngLangFromSource = sourceKanjiCharacterInfoReadingMeaningReadingMeaningGroup.getAdditionalInfoList().stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						collect(Collectors.toList());

				// pobranie angielskich znaczen z polskiego slownika
				List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningEngLangListFromDestination = destinationKanjiCharacterInfoReadingMeaning.getReadingMeaningGroup().getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						collect(Collectors.toList());
				
				List<ReadingMeaningInfoReadingMeaningGroupAdditionalInfo> additionalInfoEngLangFromDestination = destinationKanjiCharacterInfoReadingMeaning.getReadingMeaningGroup().getAdditionalInfoList().stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						collect(Collectors.toList());

				// pobranie polskiego znaczenie z polskiego slownika
				List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningPolLangListFromDestination = destinationKanjiCharacterInfoReadingMeaning.getReadingMeaningGroup().getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						collect(Collectors.toList());
				
				List<ReadingMeaningInfoReadingMeaningGroupAdditionalInfo> additionalInfoPolLangFromDestination = destinationKanjiCharacterInfoReadingMeaning.getReadingMeaningGroup().getAdditionalInfoList().stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						collect(Collectors.toList());
				
				// porownanie obu angielskich znaczen
				boolean compareSense = getHashFromMeaningsAndAdditionalInfos(meaningEngLangListFromSource, additionalInfoEngLangFromSource).equals(
						getHashFromMeaningsAndAdditionalInfos(meaningEngLangListFromDestination, additionalInfoEngLangFromDestination));
				
				if (compareSense == true) { // znaczenie nie zmienilo sie
					
					// dodanie to, co bylo wczesniej
					newReadingMeaningInfoReadingMeaningGroup.getMeaningList().addAll(meaningEngLangListFromSource);
					newReadingMeaningInfoReadingMeaningGroup.getAdditionalInfoList().addAll(additionalInfoEngLangFromSource);

					newReadingMeaningInfoReadingMeaningGroup.getMeaningList().addAll(meaningPolLangListFromDestination);
					newReadingMeaningInfoReadingMeaningGroup.getAdditionalInfoList().addAll(additionalInfoPolLangFromDestination);
					
				} else {
					
					// dodanie nowe angielskie znaczenie
					newReadingMeaningInfoReadingMeaningGroup.getMeaningList().addAll(meaningEngLangListFromSource);
					newReadingMeaningInfoReadingMeaningGroup.getAdditionalInfoList().addAll(additionalInfoEngLangFromSource);

					// dodanie starego polskiego znaczenia, aby wstawilo sie w docelowym pliku csv
					{
						ReadingMeaningInfoReadingMeaningGroupMeaning readingMeaningInfoReadingMeaningGroupMeaning = new ReadingMeaningInfoReadingMeaningGroupMeaning();
						
						readingMeaningInfoReadingMeaningGroupMeaning.setLang(ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL);
						readingMeaningInfoReadingMeaningGroupMeaning.setValue("STARE_ZNACZENIE\n" + "---\n---\n");
						
						newReadingMeaningInfoReadingMeaningGroup.getMeaningList().add(readingMeaningInfoReadingMeaningGroupMeaning);
						newReadingMeaningInfoReadingMeaningGroup.getMeaningList().addAll(meaningPolLangListFromDestination);						
					}
					
					{
						ReadingMeaningInfoReadingMeaningGroupAdditionalInfo readingMeaningInfoReadingMeaningGroupAdditionalInfo = new ReadingMeaningInfoReadingMeaningGroupAdditionalInfo();
						
						readingMeaningInfoReadingMeaningGroupAdditionalInfo.setLang(ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL);
						readingMeaningInfoReadingMeaningGroupAdditionalInfo.setValue("STARE_INFO\n" + "---\n---\n");
						
						newReadingMeaningInfoReadingMeaningGroup.getAdditionalInfoList().add(readingMeaningInfoReadingMeaningGroupAdditionalInfo);
						newReadingMeaningInfoReadingMeaningGroup.getAdditionalInfoList().addAll(additionalInfoPolLangFromDestination);											
					}					
					
					isManuallyNeeded = true;
				}				
			}
			
			destinationKanjiCharacterInfo.setReadingMeaning(newReadingMeaningInfo);
		}

		return isManuallyNeeded;
	}
	
	private String getHashFromMeaningsAndAdditionalInfos(List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningLangList, List<ReadingMeaningInfoReadingMeaningGroupAdditionalInfo> additionalInfoLangList) {
		
		StringWriter stringWriter = new StringWriter();
		
		for (ReadingMeaningInfoReadingMeaningGroupMeaning readingMeaningInfoReadingMeaningGroupMeaning : meaningLangList) {
			stringWriter.write(readingMeaningInfoReadingMeaningGroupMeaning.getLang().name());
			stringWriter.write(readingMeaningInfoReadingMeaningGroupMeaning.getValue());
		}
		
		for (ReadingMeaningInfoReadingMeaningGroupAdditionalInfo readingMeaningInfoReadingMeaningGroupAdditionalInfo : additionalInfoLangList) {
			stringWriter.write(readingMeaningInfoReadingMeaningGroupAdditionalInfo.getLang().name());
			stringWriter.write(readingMeaningInfoReadingMeaningGroupAdditionalInfo.getValue());
		}		
		
		return DigestUtils.sha256Hex(stringWriter.toString());
	}
	
	public void updateOldKanjiEntryForDictionaryFromCharacterInfo(KanjiEntryForDictionary kanjiEntryForDictionary, KanjiCharacterInfo characterInfo) {
		
		ReadingMeaningInfo readingMeaning = characterInfo.getReadingMeaning();
		
		if (readingMeaning != null) {
			ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaning.getReadingMeaningGroup();
			
			if (readingMeaningGroup != null) {
				List<String> meaningEngLangList = readingMeaningGroup.getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
						map(meaning -> meaning.getValue()).collect(Collectors.toList());
				
				// String additionalEngInfoLang = readingMeaningGroup.getAdditionalInfoList().stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.EN).
				//		map(additionalInfo -> additionalInfo.getValue()).findFirst().orElse(null);

				List<String> meaningPolLangList = readingMeaningGroup.getMeaningList().stream().filter(meaning -> meaning.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						map(meaning -> meaning.getValue()).collect(Collectors.toList());
				
				String additionalPolInfoLang = readingMeaningGroup.getAdditionalInfoList().stream().filter(additionalInfo -> additionalInfo.getLang() == ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum.PL).
						map(additionalInfo -> additionalInfo.getValue()).findFirst().orElse("");
				
				if (meaningEngLangList.size() != 0 && meaningPolLangList.size() == 0) { // tlumaczenie zostalo skasowane?
					throw new RuntimeException("No translates for: " + characterInfo.getKanji());
				}
				
				//
				
				if (meaningEngLangList.size() > 0) {
					kanjiEntryForDictionary.setPolishTranslates(meaningPolLangList);
					
				} else {
					kanjiEntryForDictionary.setPolishTranslates(Arrays.asList("-"));
				}			
				
				kanjiEntryForDictionary.setInfo(additionalPolInfoLang);
				
				// wygenerowanie kanji dic2 raw data
				{
					List<String> kanjidic2RawData = new ArrayList<String>();
					
					kanjidic2RawData.add("Kanji: " + characterInfo.getKanji());
							
					for (String currentEngMeaning : meaningEngLangList) {
						kanjidic2RawData.add("Meaning: " + currentEngMeaning);
					}
											
					kanjiEntryForDictionary.setKanjiDic2RawDataList(Helper.convertListToString(kanjidic2RawData));
				}
				
			}
		}
	}
	
	private class EntryPartConverterEnd {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, KanjiCharacterInfo characterInfo) throws IOException {
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(EntryHumanCsvFieldType.END.name()); columnsNo++;
			csvWriter.write(characterInfo.getKanji()); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();			
		}

		public void parseCsv(CsvReader csvReader, KanjiCharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.END) {
				throw new RuntimeException(fieldType.name());
			}

			// noop
		}
	}

	
	private enum EntryHumanCsvFieldType {
		HEADER,
		
		BEGIN,
		
		CODE_POINT,
		RADICAL,
		MISC,
		MISC2,
		DICTIONARY_NUMBER,
		QUERY_CODE,
		
		READING_MEANING_NANONI,
		READING_MEANING_READING,
		READING_MEANING_MEANING_ENG,
		READING_MEANING_MEANING_POL,
		
		END;
		
		;
	}
		
	public static class SaveKanjiDic2AsHumanCsvConfig {
				
		public boolean shiftCells = false;
		public boolean shiftCellsGenerateIds = false;
		public Integer shiftCellsGenerateIdsId = 1;

		public boolean addOldPolishTranslates = false;		
	}
	
	public static class EntryAdditionalData {
		
		private Map<String, EntryAdditionalDataEntry> kanjidic2AdditionalDataEntryMap = new TreeMap<>();
		
		public void setOldKanjiEntryForDictionary(String kanji, KanjiEntryForDictionary oldKanjiEntryForDictionary) {
			
			EntryAdditionalDataEntry entryAdditionalDataEntry = kanjidic2AdditionalDataEntryMap.get(kanji);
			
			if (entryAdditionalDataEntry == null) {
				entryAdditionalDataEntry = new EntryAdditionalDataEntry();
				
				kanjidic2AdditionalDataEntryMap.put(kanji, entryAdditionalDataEntry);
			}
			
			entryAdditionalDataEntry.oldKanjiEntryForDictionary = oldKanjiEntryForDictionary;			
		}
		
	}
	
	private static class EntryAdditionalDataEntry {
		private KanjiEntryForDictionary oldKanjiEntryForDictionary;
	}
}
