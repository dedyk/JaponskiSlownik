package pl.idedyk.japanese.dictionary2.common;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;

public class DictionaryHelper {
	
	private DictionaryHelper() { }
	
	public static DictionaryHelper init() {
		
		// init
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		//
		
		DictionaryHelper dictionaryHelper = new DictionaryHelper();
		
		//
		
		dictionaryHelper.jmdictFile = new File("../JapaneseDictionary_additional/JMdict");
		
		return dictionaryHelper;
	}
	
	private File jmdictFile;	
	private JMdict jmdict = null;
	
	public JMdict getJMdict() throws Exception {
		
		if (jmdict == null) {

			// walidacja xsd pliku JMdict
			System.out.println("Validating JMdict");
			
			// walidacja xsd
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			Schema schema = factory.newSchema(DictionaryHelper.class.getResource("/pl/idedyk/japanese/dictionary2/jmdict/xsd/JMdict.xsd"));
			
			Validator validator = schema.newValidator();
						
			validator.validate(new StreamSource(jmdictFile));			

			// wczytywanie pliku JMdict
			System.out.println("Reading JMdict");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			jmdict = (JMdict) jaxbUnmarshaller.unmarshal(jmdictFile);
		}
		
		return jmdict;
	}	
}
