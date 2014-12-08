package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.K_Ele;

public class JMEDictNewReader {
	
	private JMEDictEntityMapper entityMapper = new JMEDictEntityMapper();
	
	public List<JMEDictNewNativeEntry> readJMEdict(String fileName) throws DictionaryException {
		return readJMEdict(fileName, "JMdict");
	}

	public List<JMEDictNewNativeEntry> readJMnedict(String fileName) throws DictionaryException {
		return readJMEdict(fileName, "JMnedict");
	}
	
	private List<JMEDictNewNativeEntry> readJMEdict(String fileName, String rootName) throws DictionaryException {

		final List<JMEDictNewNativeEntry> result = new ArrayList<JMEDictNewNativeEntry>();

		System.setProperty("entityExpansionLimit", "1000000");

		SAXReader reader = new SAXReader();
		
		reader.addHandler("/" + rootName + "/entry", new ElementHandler() {

			@Override
			public void onStart(ElementPath path) {

			}

			@Override
			public void onEnd(ElementPath path) {
				
				JMEDictNewNativeEntry jmedictNewNativeEntry = new JMEDictNewNativeEntry();

				Element row = path.getCurrent();
								
				// pobranie wszystkich elementow
				List<?> rowElements = row.elements();
				
				for (Object currentRowElementObject : rowElements) {
					
					Element currentRowElement = (Element)currentRowElementObject;
					
					// pobranie wezla
					String currentRowElementName = currentRowElement.getName();
					
					switch (currentRowElementName) {
					
						case "ent_seq":	{
							
							processEntSeq(jmedictNewNativeEntry, currentRowElement);
											
							break;
							
						}
						
						case "k_ele": {
							
							processKEle(jmedictNewNativeEntry, currentRowElement);
														
							break;
						}
							
						case "r_ele": {
							
							int fixme = 1;
							
							//processREle(jmedictNewNativeEntry, currentRowElement);
							
							break;
						}
						
						case "info": {
							
							int fixme = 1;
							
							
							break;						
						}
						
						case "sense": {
							
							int fixme = 1;
							
							break;
						}
							
						default: {
							int fixme = 1;
							// wyjatek
														
							throw new RuntimeException("Unknown element name: " + currentRowElementName);
							
							//System.err.println("ERROR: " + currentRowElementName);
						}						
					}					
				}				

				result.add(jmedictNewNativeEntry);
				
				row.detach();
			}
		});

		try {
			reader.read(new File(fileName));
			
		} catch (Exception e) {			
			throw new DictionaryException(e.toString());
		}

		return result;
	}
	
	private void processEntSeq(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
		
		Integer ent_seq = Integer.parseInt(element.getText());
		
		jmedictNewNativeEntry.setEnt_seq(ent_seq);		
	}
	
	private void processKEle(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
		
		K_Ele k_ele = new K_Ele();
		
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
				
				case "keb": {
					
					String keb = currentRowElement.getText();
										
					k_ele.setKeb(keb);
					
					break;
				}
				
				case "ke_inf": {
					
					String ke_inf = entityMapper.getEntity(currentRowElement.getText());
					
					k_ele.getKe_inf().add(ke_inf);
					
					break;
				}
				
				case "ke_pri": {
					
					String ke_pri = currentRowElement.getText();
					
					k_ele.getKe_pri().add(ke_pri);
					
					break;
				}				
				
				default: {					
					throw new RuntimeException("Unknown k_ele element name: " + currentRowElementName);					
				}			
			}			
		}
		
		jmedictNewNativeEntry.getK_ele().add(k_ele);
	}
	
	private void processREle(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
		
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
				
				
				
				default: {					
					throw new RuntimeException("Unknown r_ele element name: " + currentRowElementName);					
				}			
			}			
		}
	}
}
