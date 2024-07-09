package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CodePointValueTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.HeaderInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfoValue;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.RadicalInfoValueTypeEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroup;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaning;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaningLangEnum;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupReading;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupReadingTypeEnum;

public class Kanji2Helper {
	
	private int fixme_csv = 1;
	private static final int CSV_COLUMNS = 0; // 11 
	
	private static Kanji2Helper kanji2Helper;
	
	private File kanjidic2File;
	private Kanjidic2 kanjidic2 = null;
	
	private File kanjiPolishDictionaryFile;
	
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
		
		kanji2Helper.kanjiPolishDictionaryFile = new File("input/kanji2.csv");
		
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
	
	public void saveKanjidic2AsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, String fileName, Kanjidic2 kanjidic2, EntryAdditionalData entryAdditionalData) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		// rekord z naglowkiem
		new EntryPartConverterHeaderInfo().writeToCsv(config, csvWriter, kanjidic2);
		
		createEmptyLinesInCsv(csvWriter);
		
		// zapisywanie znakow
		List<CharacterInfo> characterList = kanjidic2.getCharacterList();
		
		for (CharacterInfo characterInfo : characterList) {
			
			// zapisanie znaku
			saveEntryAsHumanCsv(config, csvWriter, characterInfo, entryAdditionalData);
			
			// rozdzielenie, aby zawartosc byla bardziej przjerzysta
			if (characterInfo != characterList.get(characterList.size() - 1)) {
				createEmptyLinesInCsv(csvWriter);
			}			
		}
				
		csvWriter.close();
	}
	
	private void createEmptyLinesInCsv(CsvWriter csvWriter) throws IOException {
		
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
	
	private void saveEntryAsHumanCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo, EntryAdditionalData entryAdditionalData) throws Exception {
		
		int fixme2 = 1;
		
		/*
	    "misc",
	    "dictionaryNumber",
	    "queryCode",
	    "readingMeaning"
	    */
		
		// rekord poczatkowy
		new EntryPartConverterBegin().writeToCsv(config, csvWriter, characterInfo);
		
		// codepoint
		new EntryPartConverterCodepoint().writeToCsv(config, csvWriter, characterInfo);
		
		// radical
		new EntryPartConverterRadical().writeToCsv(config, csvWriter, characterInfo);
			
		/*
		
		
		// kanji
		new EntryPartConverterKanji().writeToCsv(config, csvWriter, entry);
		
		// reading
		new EntryPartConverterReading().writeToCsv(config, csvWriter, entry);
		
		// sense
		new EntryPartConverterSense().writeToCsv(config, csvWriter, entry, entryAdditionalData);
				
		// rekord koncowy
		new EntryPartConverterEnd().writeToCsv(config, csvWriter, entry);
		*/		
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
			
			csvWriter.write(EntryHumanCsvFieldType.HEADER_BEGIN.name()); columnsNo++;
			csvWriter.write(headerInfo.getFileVersion()); columnsNo++;
			csvWriter.write(headerInfo.getDatabaseVersion()); columnsNo++;
			
			LocalDate dateOfCreationAsLocalDate = LocalDate.of(
					headerInfo.getDateOfCreation().getYear(), 
					headerInfo.getDateOfCreation().getMonth(), 
					headerInfo.getDateOfCreation().getDay());
			
			csvWriter.write(dateOfCreationAsLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)); columnsNo++;
						
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
				csvWriter.write(null);
			}
			
			csvWriter.endRecord();
		}

		public void parseCsv(CsvReader csvReader, Kanjidic2 kanjidic2) throws IOException, DatatypeConfigurationException {
			
			EntryHumanCsvFieldType fieldType = EntryHumanCsvFieldType.valueOf(csvReader.get(0));
			
			if (fieldType != EntryHumanCsvFieldType.HEADER_BEGIN) {
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
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
				csvWriter.write(characterInfo.getKanji()); columnsNo++;
				
				csvWriter.write(codePointValueInfo.getType().value());
				csvWriter.write(codePointValueInfo.getValue());
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
					
			codePointValueInfo.setType(CodePointValueTypeEnum.fromValue(csvReader.get(2)));
			codePointValueInfo.setValue(csvReader.get(3));
			
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
				csvWriter.write(characterInfo.getKanji()); columnsNo++;
				
				csvWriter.write(radicalInfoValue.getRadicalValueType().value());
				csvWriter.write(radicalInfoValue.getValue());
				
				// wypelniacz			
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
					
			radicalInfoValue.setRadicalValueType(RadicalInfoValueTypeEnum.fromValue(csvReader.get(2)));
			radicalInfoValue.setValue(csvReader.get(3));
			
			radicalInfo.getRadicalValueList().add(radicalInfoValue);
		}
	}
	
	private int csv_field_fixme = 1;	
	private enum EntryHumanCsvFieldType {
		
		HEADER_BEGIN,
		HEADER_END,
		
		BEGIN,
		
		CODE_POINT,
		RADICAL,
		
		;
		/*
		
		
		KANJI,
		READING,
		
		SENSE_COMMON,
		SENSE_ENG,
		SENSE_POL,
				
		END;
		*/
	}
		
	public static class SaveKanjiDic2AsHumanCsvConfig {
		
		private int fixme = 1;
		
		public boolean shiftCells = false;
		public boolean shiftCellsGenerateIds = false;
		public Integer shiftCellsGenerateIdsId = 1;

		
		/*
		public Set<Integer> polishEntrySet = null;
		
		public boolean addOldPolishTranslates = false;
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
	}
	
	private static class EntryAdditionalDataEntry {
		
		private int fixme = 1;
		
		/*
		private List<PolishJapaneseEntry> oldPolishJapaneseEntryList;
		
		private Map<Integer, EntryAdditionalDataEntry$UpdateDictionarySense> updateDictionarySenseMap;
		
		private List<EntryAdditionalDataEntry$UpdateDictionarySense> deleteDictionarySenseListDuringUpdateDictionary;
		*/
	}

}
