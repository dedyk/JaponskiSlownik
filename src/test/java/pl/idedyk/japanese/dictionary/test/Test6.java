package pl.idedyk.japanese.dictionary.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.CharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaning;

public class Test6 {

	public static void main(String[] args) throws Exception {

		// PAMIETAJ: // -Djdk.xml.entityExpansionLimit=0 !!!!
		
		// testJMdict();
		
		// testJMnedict();
		
		testKanjiDict2();
	}
	
	@SuppressWarnings("unused")
	private static void testJMdict() throws Exception {
		
		File jmdictFile = new File("../JapaneseDictionary_additional/JMdict");

		// walidacja xsd

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = factory.newSchema(new File("src/main/resources/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd"));
		
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

		for (JMdict.Entry entry : jmdict.getEntry()) {
			
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
	}
}
