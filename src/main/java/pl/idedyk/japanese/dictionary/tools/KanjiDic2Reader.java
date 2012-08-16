package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;

public class KanjiDic2Reader {
	
	public static Map<String, List<String>> readKradFile(String fileName) throws Exception {
		
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		while(true) {
			String line = reader.readLine();
			
			if (line == null) {
				break;
			}
			
			if (line.startsWith("#") == true) {
				continue;
			}
			
			String kanji = line.substring(0, 1);
			
			String radicals = line.substring(4);
			
			String[] radicalsSplited = radicals.split(" ");
			
			List<String> radicalsList = Arrays.asList(radicalsSplited);			
			
			result.put(kanji, radicalsList);
		}
		
		reader.close();
		
		return result;
	}
	
	public static Map<String, KanjiDic2Entry> readKanjiDic2(String fileName, Map<String, List<String>> kradFileMap) throws Exception {
		
		Map<String, KanjiDic2Entry> result = new HashMap<String, KanjiDic2Entry>();
		
		SAXReader reader = new SAXReader();
		
        Document document = reader.read(new File(fileName));
        
        List<?> characterList = document.selectNodes("kanjidic2/character");
		
        for (Object currentCharacterAsObject : characterList) {
			
        	Element currentCharacterAsElement = (Element)currentCharacterAsObject;
        	
        	String kanji = currentCharacterAsElement.selectSingleNode("literal").getText();
        	
        	Element readingMeaning = (Element)currentCharacterAsElement.selectSingleNode("reading_meaning");
        	
        	Element rmgroup = (Element)readingMeaning.selectSingleNode("rmgroup");
        	
        	List<String> onReading = getReading(rmgroup, "ja_on");
        	List<String> kunReading = getReading(rmgroup, "ja_kun");
        	
        	int strokeCount = Integer.parseInt(currentCharacterAsElement.selectSingleNode("misc/stroke_count").getText());
        	
        	KanjiDic2Entry kanjiDic2Entry = new KanjiDic2Entry();
        	
        	kanjiDic2Entry.setKanji(kanji);
        	kanjiDic2Entry.setStrokeCount(strokeCount);
        	
        	kanjiDic2Entry.setOnReading(onReading);
        	kanjiDic2Entry.setKunReading(kunReading);
        	
        	List<String> radicals = kradFileMap.get(kanji);
        	
        	if (radicals == null) {
        		radicals = new ArrayList<String>();        		
        	}
        	
        	kanjiDic2Entry.setRadicals(radicals);
        	
        	result.put(kanji, kanjiDic2Entry);
		}
		
		return result;
	}
	
	private static List<String> getReading(Element rmgroup, String type) {
		
		List<String> result = new ArrayList<String>();
		
		List<?> readingTypeNodes = rmgroup.selectNodes("reading[@r_type='" + type + "']");
		
		for (Object currentReadingTypeAsObject : readingTypeNodes) {
			Element currentReadingTypeAsElement = (Element)currentReadingTypeAsObject;
			
			result.add(currentReadingTypeAsElement.getText());			
		}
		
		return result;
	}
	
	public static List<RadicalInfo> readRadkfile(String radkFile) throws IOException {
		
		List<RadicalInfo> result = new ArrayList<RadicalInfo>();
		
		BufferedReader reader = new BufferedReader(new FileReader(radkFile));
		
		int id = 1;
		
		while(true) {
			String line = reader.readLine();
			
			if (line == null) {
				break;
			}
			
			if (line.startsWith("#") == true) {
				continue;
			}
			
			if (line.startsWith("$") == true) {
				
				String[] lineSplited = line.split(" ");
				
				String radical = lineSplited[1];
				String strokeCountString = lineSplited[2];
				
				RadicalInfo newRadicalInfo = new RadicalInfo();
				
				newRadicalInfo.setId(id);
				newRadicalInfo.setRadical(radical);
				newRadicalInfo.setStrokeCount(Integer.parseInt(strokeCountString));
				
				result.add(newRadicalInfo);
				
				id++;
			}
		}
		
		reader.close();
		
		return result;
	}
}
