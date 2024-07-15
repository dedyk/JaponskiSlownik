package pl.idedyk.japanese.dictionary2.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
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
						
						/*
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
						*/
						
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
	    "readingMeaning"
	    */
		
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
		
		
		//tutaj();
		
		// rekord koncowy
		new EntryPartConverterEnd().writeToCsv(config, csvWriter, characterInfo);		
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
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
			
			miscInfo.setGrade(csvReader.get(0).equals("-") == false ? Integer.valueOf(csvReader.get(1)) : null);
			
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
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
				for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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
	
	/*
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
		
	*/
	
	private class EntryPartConverterEnd {

		public void writeToCsv(SaveKanjiDic2AsHumanCsvConfig config, CsvWriter csvWriter, CharacterInfo characterInfo) throws IOException {
			
			int columnsNo = 0;
			
			if (config.shiftCells == true) {
				csvWriter.write(""); columnsNo++;
			}
			
			csvWriter.write(EntryHumanCsvFieldType.END.name()); columnsNo++;
			csvWriter.write(characterInfo.getKanji()); columnsNo++;
			
			// wypelniacz			
			for (; columnsNo < CSV_COLUMNS; ++columnsNo) {
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

	
	private int csv_field_fixme = 1;	
	private enum EntryHumanCsvFieldType {
		
		HEADER_BEGIN,
		HEADER_END,
		
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
