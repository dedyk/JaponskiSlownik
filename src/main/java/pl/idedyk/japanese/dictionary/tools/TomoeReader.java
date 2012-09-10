package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.dto.TomoeEntry;

public class TomoeReader {
	
	public static List<TomoeEntry> readTomoeXmlHandwritingDatabase(String fileName) throws Exception {
		
		List<TomoeEntry> result = new ArrayList<TomoeEntry>();
		
		SAXReader reader = new SAXReader();
		
        Document document = reader.read(new File(fileName));

        List<?> characterList = document.selectNodes("/dictionary/character");
		
        for (Object currentCharacterAsObject : characterList) {
        	
        	Element currentCharacterAsElement = (Element)currentCharacterAsObject;
        	
        	String kanji = currentCharacterAsElement.selectSingleNode("utf8").getText();
        	
        	TomoeEntry tomoeEntry = new TomoeEntry(kanji);
        	
        	List<?> strokeList = currentCharacterAsElement.selectNodes("strokes/stroke");
        	
        	for (Object currentStrokeAsObject : strokeList) {
        		
        		Element currentStrokeAsElement = (Element)currentStrokeAsObject;
        		
        		TomoeEntry.Stroke tomoeEntryStroke = new TomoeEntry.Stroke();
        		
        		List<?> pointList = currentStrokeAsElement.selectNodes("point");
        		
        		for (Object currentPointAsObject : pointList) {
        			
        			Element currentPointAsElement = (Element)currentPointAsObject;
        			
        			tomoeEntryStroke.getPointList().add(
        					new TomoeEntry.Stroke.Point(Integer.parseInt(currentPointAsElement.attributeValue("x")),
        							Integer.parseInt(currentPointAsElement.attributeValue("y"))));
				}
        		
        		tomoeEntry.getStrokeList().add(tomoeEntryStroke);
			}
        	
        	result.add(tomoeEntry);
		}
        
        return result;
	}
}
