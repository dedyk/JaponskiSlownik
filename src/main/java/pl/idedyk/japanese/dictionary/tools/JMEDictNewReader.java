package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.K_Ele;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.LSource;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.R_Ele;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.Sense;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry.Trans;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;

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
												
						case "sense": {
							
							processSense(jmedictNewNativeEntry, currentRowElement);
							
							break;
						}
						
						case "trans": {
							
							processTrans(jmedictNewNativeEntry, currentRowElement);
							
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
					
					sense.getPos().add(pos);
					
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
	
	
	protected void processTrans(JMEDictNewNativeEntry jmedictNewNativeEntry, Element element) {
		
		Trans trans = new Trans();
		
		List<?> rowElements = element.elements();
		
		for (Object currentRowElementObject : rowElements) {
			
			Element currentRowElement = (Element)currentRowElementObject;
			
			String currentRowElementName = currentRowElement.getName();
			
			switch (currentRowElementName) {
								
				case "name_type": {
					
					String nameType = entityMapper.getEntity(currentRowElement.getText());
					
					trans.getName_type().add(nameType);
					
					break;
				}
								
				case "xref": {
					
					String xref = currentRowElement.getText();
					
					trans.getXref().add(xref);
					
					break;
				}
				
				case "trans_det": {
					
					String transDet = currentRowElement.getText();
					
					trans.getTrans_det().add(transDet);
					
					break;					
				}
												
				default: {					
					throw new RuntimeException("Unknown trans element name: " + currentRowElementName);					
				}			
			}			
		}
		
		jmedictNewNativeEntry.getTrans().add(trans);
	}
	
	public JMENewDictionary createJMENewDictionary(List<JMEDictNewNativeEntry> jmedictNativeList) {
				
		JMENewDictionary jmeNewDictionary = new JMENewDictionary();
		
		KanaHelper kanaHelper = new KanaHelper();
		
		// dla kazdego elementu listy
		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {
			
			// utworzenie grupy
			Group group = new Group(jmeDictNewNativeEntry.getEnt_seq(), jmeDictNewNativeEntry);
						
			// parsowanie elementu

			// pobranie listy kanji			
			List<K_Ele> k_ele = jmeDictNewNativeEntry.getK_ele();
			
			// pobranie listy kana
			List<R_Ele> r_ele = jmeDictNewNativeEntry.getR_ele();
			
			if (r_ele.size() == 0) {
				throw new RuntimeException("r_ele.size() == 0");
			}
			
			// jesli nie ma kanji
			if (k_ele.size() == 0) {
				
				for (R_Ele currentREle : r_ele) {
					
					boolean noKanji = currentREle.isRe_nokanji();
					
					if (noKanji == false) { // to chyba nie jest potrzebne
						
						String kana = currentREle.getReb();
						List<String> kanaInfoList = currentREle.getRe_inf();

						// utworz wpis do grupy
						GroupEntry groupEntry = new GroupEntry(jmeDictNewNativeEntry, group);
											
						groupEntry.setKana(kana);
						groupEntry.setKanaInfoList(kanaInfoList);
						
						try {
							groupEntry.setRomaji(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true)));
						
						} catch (Exception e) {
							// noop
						}
						
						// tlumaczenia
						generateWordTypeTranslateAdditionalInfoList(groupEntry, jmeDictNewNativeEntry);
						
						group.getGroupEntryList().add(groupEntry);						
					}
				}				
				
			} else {				
				// zlacz kanji z kana
				
				for (K_Ele currentKEle : k_ele) {					
					for (R_Ele currentREle : r_ele) {
						
						// pobierz kanji
						String kanji = currentKEle.getKeb();
						List<String> kanjiInfoList = currentKEle.getKe_inf();
												
						boolean noKanji = currentREle.isRe_nokanji();
						
						// jest pozycja kana nie laczy sie ze znakiem kanji
						if (noKanji == true) {
							continue;
						}
						
						// pobierz kana
						String kana = currentREle.getReb();
						List<String> kanaInfoList = currentREle.getRe_inf();
						List<String> kanaRestrictedList = currentREle.getRe_restr();
						
						boolean isRestricted = true;
						
						// sprawdzanie, czy dany kana laczy sie z kanji
						if (kanaRestrictedList.size() == 0) {							
							isRestricted = false;
							
						} else {							
							if (kanaRestrictedList.contains(kanji) == true) {
								isRestricted = false;
							}							
						}
						
						if (isRestricted == true) {
							continue; // omijamy to zlaczenie
						}
						
						// utworz wpis do grupy
						GroupEntry groupEntry = new GroupEntry(jmeDictNewNativeEntry, group);
						
						groupEntry.setKanji(kanji);
						groupEntry.setKanjiInfoList(kanjiInfoList);
						
						groupEntry.setKana(kana);
						groupEntry.setKanaInfoList(kanaInfoList);
						
						try {
							groupEntry.setRomaji(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true)));
						
						} catch (Exception e) {
							// noop
						}
						
						// tlumaczenia
						generateWordTypeTranslateAdditionalInfoList(groupEntry, jmeDictNewNativeEntry);
						
						group.getGroupEntryList().add(groupEntry);						
					}										
				}
			}
			
			// szukanie kana z no kanji
			for (R_Ele currentREle : r_ele) {
				
				boolean noKanji = currentREle.isRe_nokanji();
				
				if (noKanji == true) {
					
					String kana = currentREle.getReb();
					List<String> kanaInfoList = currentREle.getRe_inf();

					// utworz wpis do grupy
					GroupEntry groupEntry = new GroupEntry(jmeDictNewNativeEntry, group);
										
					groupEntry.setKana(kana);
					groupEntry.setKanaInfoList(kanaInfoList);
					
					try {
						groupEntry.setRomaji(kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true)));
					
					} catch (Exception e) {
						// noop
					}
					
					// tlumaczenia
					generateWordTypeTranslateAdditionalInfoList(groupEntry, jmeDictNewNativeEntry);
					
					group.getGroupEntryList().add(groupEntry);						
				}
			}			
			
			jmeNewDictionary.getGroupList().add(group);
		}
		
		// operacje koncowe
		
		// cache'owanie wynikow		
		for (Group group : jmeNewDictionary.getGroupList()) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				jmeNewDictionary.addGroupEntryToCache(groupEntry);
			}
		}
				
		return jmeNewDictionary;
	}

	private void generateWordTypeTranslateAdditionalInfoList(GroupEntry groupEntry, JMEDictNewNativeEntry jmeDictNewNativeEntry) {
		
		String kanji = groupEntry.getKanji();
		String kana = groupEntry.getKana();
		
		List<Sense> senseList = jmeDictNewNativeEntry.getSense();
		
		Set<String> wordTypeList = new LinkedHashSet<String>();
		
		List<GroupEntryTranslate> translateList = new ArrayList<GroupEntryTranslate>();
				
		for (Sense currentSense : senseList) {
			
			List<String> stagk = currentSense.getStagk();
			List<String> stagr = currentSense.getStagr();
			
			List<String> misc = currentSense.getMisc();
			
			List<String> pos = currentSense.getPos();
			
			List<String> gloss = currentSense.getGloss();
			
			List<String> s_inf = currentSense.getS_inf();

			boolean isKanjiRestricted = true;
			
			if (stagk.size() == 0) {
				isKanjiRestricted = false;
				
			} else {
				if (stagk.contains(kanji) == true) {
					isKanjiRestricted = false;
				}
			}
			
			if (isKanjiRestricted == true) {
				continue;
			}	
			
			boolean isKanaRestricted = true;
			
			if (stagr.size() == 0) {
				isKanaRestricted = false;
				
			} else {
				if (stagr.contains(kana) == true) {
					isKanaRestricted = false;
				}
			}
			
			if (isKanaRestricted == true) {
				continue;
			}
			
			for (String currentPos : pos) {
				wordTypeList.add(currentPos);
			}
			
			
			for (String currentGloss : gloss) {
				
				GroupEntryTranslate groupEntryTranslate = new GroupEntryTranslate();
				
				groupEntryTranslate.setTranslate(currentGloss);
				
				List<String> miscInfoList = new ArrayList<String>();
				List<String> additionalInfoList = new ArrayList<String>();
				
				for (String currentMisc : misc) {				
					miscInfoList.add(currentMisc);
				}
				
				for (String currentSInf : s_inf) {
					additionalInfoList.add(currentSInf);
				}

				groupEntryTranslate.setMiscInfoList(miscInfoList);
				groupEntryTranslate.setAdditionalInfoList(additionalInfoList);
				
				translateList.add(groupEntryTranslate);
			}			
		}
		
		List<Trans> transList = jmeDictNewNativeEntry.getTrans();
		
		for (Trans trans : transList) {
			
			List<String> name_type = trans.getName_type();
			
			List<String> trans_det = trans.getTrans_det();
		
			for (String currentNameType : name_type) {				
				wordTypeList.add(currentNameType);
			}
			
			for (String currentTransDet : trans_det) {
				
				GroupEntryTranslate groupEntryTranslate = new GroupEntryTranslate();
				
				groupEntryTranslate.setTranslate(currentTransDet);
				
				translateList.add(groupEntryTranslate);
			}
		}
		
		groupEntry.setWordTypeList(wordTypeList);
		
		groupEntry.setTranslateList(translateList);
	}
}
