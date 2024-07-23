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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfoReference;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.DictionaryNumberInfoReferenceTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.HeaderInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
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
	
	private File kanjidic2File;
	private Kanjidic2 kanjidic2 = null;
	
	private File oldKanjiPolishDictionaryFile;
	
	@SuppressWarnings("deprecation")
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
			List<CharacterInfo> characterInfoList = kanjidic2.getCharacterList();
			
			for (CharacterInfo characterInfo : characterInfoList) {
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
	
	public void saveKanjidic2AsXml(Kanjidic2 kanjidic2, File file) throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Kanjidic2.class);              

		//
				
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		//
		
		jaxbMarshaller.marshal(kanjidic2, file);
	}

	
	@SuppressWarnings("deprecation")
	public List<KanjiEntryForDictionary> getOldKanjiPolishDictionaryList() throws IOException, JapaneseDictionaryException {
		
		if (oldKanjiPolishDictionaryList == null) {
			System.out.println("Reading old kanji polish dictionary file");
			
			oldKanjiPolishDictionaryList = CsvReaderWriter.parseKanjiEntriesFromCsv(oldKanjiPolishDictionaryFile.getPath(), null, true);	
		}
		
		return oldKanjiPolishDictionaryList;
	}
	
	@SuppressWarnings("deprecation")
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
	
	@SuppressWarnings("deprecation")
	public KanjiEntryForDictionary getOldKanjiEntryForDictionary(String kanji) throws IOException, JapaneseDictionaryException {
		Map<String, KanjiEntryForDictionary> oldKanjiPolishDictionaryMap = getOldKanjiPolishDictionaryMap();
		
		return oldKanjiPolishDictionaryMap.get(kanji);
	}
	
	public void saveKanjidic2AsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, String fileName, Kanjidic2 kanjidic2, EntryAdditionalData entryAdditionalData) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		// rekord z naglowkiem
		new EntryPartConverterHeaderInfo().writeToCsv(config, csvWriter, kanjidic2);
		
		createEmptyLinesInCsv(config, csvWriter);
		
		// zapisywanie znakow
		List<CharacterInfo> characterList = kanjidic2.getCharacterList();
		
		for (CharacterInfo characterInfo : characterList) {
			
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
	
	private void saveEntryAsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo, EntryAdditionalData entryAdditionalData) throws Exception {
				
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(config, csvWriter, characterInfo);
		
		// codepoint
		new EntryPartConverterCodepoint().writeToCsv(config, csvWriter, characterInfo);
		
		// radical
		new EntryPartConverterRadical().writeToCsv(config, csvWriter, characterInfo);
		
		// misc
		new EntryPartConverterMisc().writeToCsv(config, csvWriter, characterInfo);
		
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
		CharacterInfo characterInfo = null;
				
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
				
				characterInfo = new CharacterInfo();
				
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}

		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.BEGIN) {
				throw new RuntimeException(fieldType.name());
			}
			
			characterInfo.setKanji(csvReader.get(1));
		}
	}
	
	private class EntryPartConverterCodepoint {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

	private class EntryPartConverterDictionaryNumber {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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
				
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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

		@SuppressWarnings("deprecation")
		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo, EntryAdditionalData entryAdditionalData) throws IOException {
			
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
					
					csvWriter.write("STARE_ZNACZENIE\n" + "---\n---\n" + Helper.convertListToString(oldKanjiEntryForDictionary.getPolishTranslates())); columnsNo++;

					if (oldKanjiEntryForDictionary.getInfo().equals("") == false) {
						csvWriter.write("STARE_INFO\n" + "---\n---\n" + oldKanjiEntryForDictionary.getInfo()); columnsNo++;
					}
				}
			}
			
			//
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();			
			
			int fixme2 = 1;
			
			/*
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
					for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
						csvWriter.write(null);
					}

					csvWriter.endRecord();
				}				
			}
			*/
		}
		
		int fixme3 = 1;
		
		/*
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
			}			
			
			//////
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS + (config.shiftCells == true ? 1 : 0); ++columnsNo) {
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
		*/
		
		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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
			

			
			
			int fixme4 = 1;
			
			/*
			if (fieldType == EntryHumanCsvFieldType.f) {
				
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
			*/	
		}
	}
	
	
	///////////////////////
	
	
	private class EntryPartConverterEnd {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
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

		public void parseCsv(CsvReader csvReader, CharacterInfo characterInfo) throws IOException {
			
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
		
		private int fixme = 1;
		
		public boolean shiftCells = false;
		public boolean shiftCellsGenerateIds = false;
		public Integer shiftCellsGenerateIdsId = 1;

		public boolean addOldPolishTranslates = false;
		
		/*
		public Set<Integer> polishEntrySet = null;
				
		public boolean addOldEnglishPolishTranslatesDuringDictionaryUpdate = false;
		public boolean addDeleteSenseDuringDictionaryUpdate = false;
		
		public boolean markRomaji = false;
				
		public void markAsPolishEntry(Entry polishEntry) {
			
			if (polishEntrySet == null) {
				polishEntrySet = new TreeSet<>();
			}
			
			polishEntrySet.add(polishEntry.getEntryId());
		}
		*/		
	}
	
	public static class EntryAdditionalData {		
		
		int fixme = 1;
		
		private Map<String, EntryAdditionalDataEntry> kanjidic2AdditionalDataEntryMap = new TreeMap<>();
		
		public void setOldKanjiEntryForDictionary(String kanji, @SuppressWarnings("deprecation") KanjiEntryForDictionary oldKanjiEntryForDictionary) {
			
			EntryAdditionalDataEntry entryAdditionalDataEntry = kanjidic2AdditionalDataEntryMap.get(kanji);
			
			if (entryAdditionalDataEntry == null) {
				entryAdditionalDataEntry = new EntryAdditionalDataEntry();
				
				kanjidic2AdditionalDataEntryMap.put(kanji, entryAdditionalDataEntry);
			}
			
			entryAdditionalDataEntry.oldKanjiEntryForDictionary = oldKanjiEntryForDictionary;			
		}
	}
	
	private static class EntryAdditionalDataEntry {
				
		@SuppressWarnings("deprecation")
		private KanjiEntryForDictionary oldKanjiEntryForDictionary;
	}
}
