package pl.idedyk.japanese.dictionary.test;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class Test6 {

	public static void main(String[] args) throws Exception {

		// PAMIETAJ: // -Djdk.xml.entityExpansionLimit=0 !!!!
		
		File jmdictFile = new File("../JapaneseDictionary_additional/JMdict");

		// walidacja xsd

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = factory.newSchema(new File("src/main/resources/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd"));
		
		Validator validator = schema.newValidator();
		
		validator.validate(new StreamSource(jmdictFile));


		// wczytywanie

		/*
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		JMdict jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);

		for (JMdict.Entry entry : jmdict.getEntry()) {
			
			System.out.println(entry.getEntSeq());
			
		}
		*/
	}
}
