package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.K_Ele;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.LSource;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.R_Ele;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.Sense;

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
														
							processREle(jmedictNewNativeEntry, currentRowElement);
							
							break;
						}
						
						case "info": {
							
							processInfo(jmedictNewNativeEntry, currentRowElement);							
							
							break;						
						}
						
						case "sense": {
							
							processSense(jmedictNewNativeEntry, currentRowElement);
							
							break;
						}
							
						default: {														
							throw new RuntimeException("Unknown element name: " + currentRowElementName);
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
		
		R_Ele r_ele = new R_Ele();
		
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
				
				case "reb": {
					
					String reb = currentRowElement.getText();
										
					r_ele.setReb(reb);
					
					break;
				}
				
				case "re_nokanji": {
					
					r_ele.setRe_nokanji(true);
					
					break;
				}
				
				case "re_restr": {
					
					String re_restr = currentRowElement.getText();
					
					r_ele.getRe_restr().add(re_restr);
					
					break;
				}
				
				case "re_inf": {
					
					String re_inf = entityMapper.getEntity(currentRowElement.getText());

					r_ele.getRe_inf().add(re_inf);
					
					break;
				}
				
				case "re_pri": {
					
					String re_pri = currentRowElement.getText();
					
					r_ele.getRe_pri().add(re_pri);
					
					break;
				}
				
				default: {					
					throw new RuntimeException("Unknown r_ele element name: " + currentRowElementName);					
				}			
			}			
		}
		
		jmedictNewNativeEntry.getR_ele().add(r_ele);
	}

	private void processInfo(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
				
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
				
				case "audit": {
					
					// noop
					
					break;
				}
								
				default: {					
					throw new RuntimeException("Unknown info element name: " + currentRowElementName);					
				}			
			}			
		}
	}

	private void processSense(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
		
		Sense sense = new Sense();
		
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
				
				case "stagk": {
					
					String stagk = currentRowElement.getText();
					
					sense.getStagk().add(stagk);
					
					break;					
				}

				case "stagr": {
					
					String stagr = currentRowElement.getText();
					
					sense.getStagr().add(stagr);
					
					break;					
				}
				
				case "pos": {
					
					String pos = entityMapper.getEntity(currentRowElement.getText());
					
					sense.setPos(pos);
					
					break;
				}
								
				case "xref": {
					
					String xref = currentRowElement.getText();
					
					sense.getXref().add(xref);
					
					break;
				}
				
				case "ant": {
					
					String ant = currentRowElement.getText();
					
					sense.getAnt().add(ant);
					
					break;					
				}
				
				case "field": {
					
					String field = entityMapper.getEntity(currentRowElement.getText());
					
					sense.getField().add(field);
					
					break;
				}
				
				case "misc": {
					
					String misc = entityMapper.getEntity(currentRowElement.getText());
					
					sense.getMisc().add(misc);
					
					break;
				}

				case "s_inf": {
					
					String s_inf = currentRowElement.getText();
					
					sense.getS_inf().add(s_inf);
					
					break;
				}
				
				case "lsource": {
					
					processLSource(sense, currentRowElement);
					
					break;
				}
				
				case "dial": {
					
					String dial = entityMapper.getEntity(currentRowElement.getText());
					
					sense.getDial().add(dial);
					
					break;
				}
				
				case "gloss": {
					
					if (currentRowElement.attribute("lang").getText().equals("eng") == true) {

						String gloss = currentRowElement.getText();
						
						sense.getGloss().add(gloss);
					}					
					
					break;
				}
								
				default: {					
					throw new RuntimeException("Unknown sense element name: " + currentRowElementName);					
				}			
			}			
		}
		
		jmedictNewNativeEntry.getSense().add(sense);
	}

	private void processLSource(Sense sense, Element element) {
		
		LSource lSource = new LSource();
		
		String value = element.getText();
		
		lSource.setValue(value);
		
		List<?> attributes = element.attributes();
		
		for (Object currentAttributeObject : attributes) {
			
			Attribute currentAttribute = (Attribute)currentAttributeObject;
			
			String currentAttributeName = currentAttribute.getName();
			
			switch (currentAttributeName) {
				
				case "lang" : {
					
					String lang = currentAttribute.getText();
					
					lSource.setLang(lang);
					
					break;
				}
				
				case "ls_wasei": {
					
					String wasei = currentAttribute.getText();
					
					lSource.setWasei(wasei);
					
					break;					
				}

				case "ls_type": {
					
					String type = currentAttribute.getText();
					
					lSource.setType(type);
					
					break;					
				}
				
				default: {
					throw new RuntimeException("Unknown lsource attribute name: " + currentAttributeName);
				}
			}			
		}
				
		sense.getLsource().add(lSource);
	}
}
