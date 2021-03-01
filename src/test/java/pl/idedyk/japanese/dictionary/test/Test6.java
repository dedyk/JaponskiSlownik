package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;

public class Test6 {

	public static void main(String[] args) throws Exception {

		// PAMIETAJ: // -Djdk.xml.entityExpansionLimit=0 !!!!
		
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

		//

		JMdict jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);

		for (JMdict.Entry entry : jmdict.getEntry()) {
			
			if (entry.getEntryId().intValue() == 2848204) {
			
				List<KanjiInfo> kanjiInfoList = entry.getKanjiInfoList();
				
				for (KanjiInfo kanjiInfo : kanjiInfoList) {
					System.out.println("Kanji: " + kanjiInfo.getKanji() + " - " + kanjiInfo.getKanjiAdditionalInfoList() + " - " + kanjiInfo.getRelativePriorityList());
				}
				
				
				
				List<ReadingInfo> readingInfoList = entry.getReadingInfoList();

				/*
				for (ReadingInfo readingInfo : readingInfoList) {				
					//System.out.println(readingInfo.getKana());
				}
				*/		
				
				System.out.println(kanjiInfoList.size() + " - " + readingInfoList.size());
			}			
		}
		
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
		
	}
}
