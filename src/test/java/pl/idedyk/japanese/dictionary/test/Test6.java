package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper.EntryAdditionalData;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test6 {

	public static void main(String[] args) throws Exception {

		// PAMIETAJ: // -Djdk.xml.entityExpansionLimit=0 !!!!
		
		testJMdict();
		
		// testJMnedict();
		
		// testKanjiDict2();
	}
	
	@SuppressWarnings("unused")
	private static void testJMdict() throws Exception {
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		
		//
		
		JMdict englishJMDict = dictionary2Helper.getJMdict();		
		
		englishJMDict.getEntryList().removeIf(r -> {
			try {
				return dictionary2Helper.getEntryFromPolishDictionary(r.getEntryId()) == null;
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
		});

		
		dictionary2Helper.saveJMdictAsXml(englishJMDict, "/tmp/a/englishJMDict.xml");
		
		Dictionary2Helper.SaveEntryListAsHumanCsvConfig saveEntryListAsHumanCsvConfig = new Dictionary2Helper.SaveEntryListAsHumanCsvConfig();
		dictionary2Helper.saveEntryListAsHumanCsv(saveEntryListAsHumanCsvConfig, "/tmp/a/englishJMDict.csv", englishJMDict, new EntryAdditionalData());
		
		//
		
		JMdict newEnglishJMDictFromCsv = dictionary2Helper.readEntryListFromHumanCsv("/tmp/a/englishJMDict.csv");
				
		for (Entry entry : newEnglishJMDictFromCsv.getEntryList()) {
			
			entry.getReadingInfoList().forEach(f -> {
				f.getKana().setKanaType(null);
				f.getKana().setRomaji(null);
			});
			
			entry.getSenseList().forEach(c -> {
				c.getGlossList().removeIf(r -> "pol".equals(r.getLang()) == true);
				
				c.getAdditionalInfoList().removeIf(r -> "pol".equals(r.getLang()) == true);
				/*
				c.getAdditionalInfoList().forEach(f -> {
					f.setLang(null);
				});
				*/
			});
		}
				
		dictionary2Helper.saveJMdictAsXml(newEnglishJMDictFromCsv, "/tmp/a/englishJMDict2.xml");
		
		//
		
		List<Entry> allPolishDictionaryEntryList = dictionary2Helper.getAllPolishDictionaryEntryList();
		
		allPolishDictionaryEntryList.sort(new Comparator<Entry>() {

			@Override
			public int compare(Entry e1, Entry e2) {
				return e1.getEntryId().compareTo(e2.getEntryId());
			}
		});
		
		for (Entry entry : allPolishDictionaryEntryList) {
			
			entry.getReadingInfoList().forEach(f -> {
				f.getKana().setKanaType(null);
				f.getKana().setRomaji(null);
			});
			
			entry.getSenseList().forEach(c -> {
				c.getGlossList().removeIf(r -> "pol".equals(r.getLang()) == true);
				
				c.getAdditionalInfoList().removeIf(r -> "pol".equals(r.getLang()) == true);
				/*
				c.getAdditionalInfoList().forEach(f -> {
					f.setLang(null);
				});
				*/
			});
		}
		
		JMdict polishJmdict = dictionary2Helper.getPolishJMdict();
		
		polishJmdict.getEntryList().clear();		
		polishJmdict.getEntryList().addAll(allPolishDictionaryEntryList);
		
		dictionary2Helper.saveJMdictAsXml(polishJmdict, "/tmp/a/polishJMDict2.xml");
		
		/*
		File jmdictFile = new File("../JapaneseDictionary_additional/JMdict_e_NG");

		// walidacja xsd
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = factory.newSchema(Test6.class.getResource(("/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd")));
		
		Validator validator = schema.newValidator();
		
		System.out.println("Validate");		
		validator.validate(new StreamSource(jmdictFile));
		System.out.println("Load");

		// wczytywanie		
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		//

		JMdict jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);
		
		//
		
		
		System.out.println("Save");
		
		jaxbMarshaller.marshal(jmdict, new File("/tmp/a/JMdict_e_saved"));
		
		//
		
		*/

		/*
		for (JMdict.Entry entry : jmdict.getEntryList()) {
			
			if (entry.getEntryId().intValue() == 1000260) {
			
				List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
				
				for (KanjiInfo kanjiInfo : kanjiInfoList) {
					System.out.println("Kanji: " + kanjiInfo.getKanji() + " - " + kanjiInfo.getKanjiAdditionalInfoList() + " - " + kanjiInfo.getRelativePriorityList());
				}
				
				List<ReadingInfo> readingInfoList = entry.getReadingInfoList();

				for (ReadingInfo readingInfo : readingInfoList) {				
					System.out.println("Reading: " + readingInfo.getKana() + " - " + readingInfo.getNoKanji() + " - " + readingInfo.getKanjiRestrictionList() +
							" - " + readingInfo.getReadingAdditionalInfoList() + " - " + readingInfo.getRelativePriorityList());
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
			    QName qName = new QName("local", "SingleEntry");
			    
			    JAXBElement<JMdict.Entry> entryRoot = new JAXBElement<JMdict.Entry>(qName, JMdict.Entry.class, entry);
				
				jaxbMarshaller.marshal(entryRoot, baos);
				
				//
				
				System.out.println(baos.toString());
			}				
			
			//System.out.println("--------");
		}
		*/
		
		/*
		//
		
		System.out.println("Load 2");
				
		//
		
		FileInputStream jmdictFileInputStream = new FileInputStream(jmdictFile);
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		
		XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(jmdictFileInputStream);
				
		while(xmlEventReader.peek() != null) {
			
			if (xmlEventReader.peek().isStartElement() && xmlEventReader.peek().asStartElement().getName().getLocalPart().equals("entry")) {
				
				JMdict.Entry entry = jaxbUnmarshaller.unmarshal(xmlEventReader, JMdict.Entry.class).getValue();
				
				entry.toString();
				
				// System.out.println(entry.getEntryId());
			
			
			} else {
				xmlEventReader.next();
			}
		}
		*/				
	}
	
	@SuppressWarnings("unused")
	private static void testJMnedict() throws Exception {
		
		File jmnedict = new File("../JapaneseDictionary_additional/JMnedict.xml");

		// walidacja xsd

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = factory.newSchema(new File("src/main/resources/pl/idedyk/japanese/dictionary2/jmnedict/xsd/JMnedict.xsd"));
		
		Validator validator = schema.newValidator();
		
		System.out.println("Validate");
		
		validator.validate(new StreamSource(jmnedict));		
	}
	
	private static void testKanjiDict2() throws Exception {
		
		/*
		File kanjidic2File = new File("../JapaneseDictionary_additional/kanjidic2.xml");
		
		// walidacja xsd

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = factory.newSchema(new File("src/main/resources/pl/idedyk/japanese/dictionary2/kanjidic2/xsd/kanjidic2.xsd"));
		
		Validator validator = schema.newValidator();
		
		System.out.println("Validate");
		
		validator.validate(new StreamSource(kanjidic2File));
		
		//
		
		// wczytywanie
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Kanjidic2.class);              

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		
		Kanjidic2 kanjidic2 = (Kanjidic2) jaxbUnmarshaller.unmarshal(kanjidic2File);
		*/
		
		// Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		/*
		Kanjidic2 kanjidic2 = kanji2Helper.getKanjidic2();
		
		SaveKanjiDic2AsHumanCsvConfig config = new SaveKanjiDic2AsHumanCsvConfig();
		
		config.shiftCells = false;
		config.shiftCellsGenerateIds = true;
		config.addOldPolishTranslates = false;
		
		EntryAdditionalData entryAdditionalData = new EntryAdditionalData();
		
		for (CharacterInfo characterInfo : kanjidic2.getCharacterList()) {
			
			// pobieramy stare polskie tlumaczenie
			KanjiEntryForDictionary oldKanjiEntryForDictionary = kanji2Helper.getOldKanjiEntryForDictionary(characterInfo.getKanji());
						
			entryAdditionalData.setOldKanjiEntryForDictionary(characterInfo.getKanji(), oldKanjiEntryForDictionary);
		}
		*/
		
		// kanji2Helper.saveKanjidic2AsHumanCsv(config, "/tmp/a/kanji2-test.csv", kanjidic2, entryAdditionalData);
		
		// Kanjidic2 kanjidic2 = kanji2Helper.readKanjidic2FromHumanCsv(new File("/tmp/a/kanji2-test.csv"));
		
		// kanji2Helper.saveKanjidic2AsXml(kanjidic2, new File("/tmp/a/kanjidic2-csv-to-xml.xml"));
		
		/*
		List<CharacterInfo> characterList = kanjidic2.getCharacterList();
		
		for (CharacterInfo characterInfo : characterList) {
			
			System.out.println(characterInfo.getKanji());
			
			ReadingMeaningInfo readingMeaning = characterInfo.getReadingMeaning();
			
			if (readingMeaning != null) {
				
				List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningList = readingMeaning.getReadingMeaningGroup().getMeaningList();
				
				for (ReadingMeaningInfoReadingMeaningGroupMeaning meaning : meaningList) {				
					System.out.println("\t" + meaning.getLang() + " - " + meaning.getValue());				
				}				
			}			
		}
		*/		
	}
}
