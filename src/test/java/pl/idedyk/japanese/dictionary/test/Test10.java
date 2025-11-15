package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict.Entry;

public class Test10 {

	public static void main(String[] args) throws Exception {
		/*
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		List<Entry> entryList = dictionaryHelper.getJMdict().getEntryList();
		
		for (Entry entry : entryList) {
			
            List<Dictionary2HelperCommon.KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(entry, true);
            
            if (kanjiKanaPairList.size() == 0) {
            	System.out.println("Brak par dla: " + entry.getEntryId());
            }
		}
		*/
		
		List<Entry> entryList = loadWord2XmlFiles("/tmp/a/db/word2.xml");
		
		for (Entry dictionaryEntry2 : entryList) {
			List<Dictionary2HelperCommon.KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(dictionaryEntry2, true);

			for (Dictionary2HelperCommon.KanjiKanaPair kanjiKanaPair : kanjiKanaPairList) {
				DictionaryEntry oldDictionaryEntry = Dictionary2HelperCommon.convertKanjiKanaPairToOldDictionaryEntry(kanjiKanaPair);
				
				if (oldDictionaryEntry == null) {
					System.out.println(dictionaryEntry2.getEntryId());
				}
			}
		}
	}
	
	private static List<Entry> loadWord2XmlFiles(String word2XmlFilePath) throws JAXBException {
		
		List<Entry> result = new ArrayList<>();
		
		JAXBContext jaxbContext = JAXBContext.newInstance(JMdict.class);              
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		// pobranie listy plikow word2.xml
		File[] word2XmlFileList = new File(word2XmlFilePath).getParentFile().listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {				
				return pathname.getPath().startsWith(word2XmlFilePath);
			}
		});
		
		Arrays.sort(word2XmlFileList);
						
		for (File currentword2XmlFile : word2XmlFileList) {
			
			JMdict jmdict = (JMdict) jaxbUnmarshaller.unmarshal(currentword2XmlFile);
			
			// pobranie listy wpisow
			result.addAll(jmdict.getEntryList());
		}
		
		return result;
	}
}
