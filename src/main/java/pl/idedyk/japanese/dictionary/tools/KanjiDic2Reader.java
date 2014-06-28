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
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;

public class KanjiDic2Reader {
	
	private static Map<String, String> radicalToCorrectRadical = null;
	private static Map<String, String> radicalCodeToCorrectRadical = null;
	
	public static Map<String, List<String>> readKradFile(String fileName) throws Exception {
		
		createRadicalToCorrectRadicalMapIfNeeded();
		
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
			
			List<String> mappedRadicalsList = new ArrayList<String>();
			
			for (String currentRadical : radicalsList) {
				
				String mappedRadicalException = radicalToCorrectRadical.get(currentRadical);
				
				if (mappedRadicalException == null) {					
					mappedRadicalsList.add(currentRadical);
					
				} else {
					mappedRadicalsList.add(mappedRadicalException);
				}
			}
			
			result.put(kanji, mappedRadicalsList);
		}
		
		reader.close();
		
		return result;
	}
	
	public static Map<String, KanjiDic2Entry> readKanjiDic2(String fileName, Map<String, List<String>> kradFileMap) throws Exception {
		
		createRadicalToCorrectRadicalMapIfNeeded();
		
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
        	
        	List<String> engMeaning = getEngMeaning(rmgroup);
        	
        	int strokeCount = Integer.parseInt(currentCharacterAsElement.selectSingleNode("misc/stroke_count").getText());
        	
        	Element jlptElement = (Element)currentCharacterAsElement.selectSingleNode("misc/jlpt");
        	
        	Integer jlpt = null;
        	
        	if (jlptElement != null) {
        		jlpt = Integer.parseInt(jlptElement.getText());
        	}

        	Element freqElement = (Element)currentCharacterAsElement.selectSingleNode("misc/freq");
        	
        	Integer freq = null;
        	
        	if (freqElement != null) {
        		freq = Integer.parseInt(freqElement.getText());
        	}
        	
        	KanjiDic2Entry kanjiDic2Entry = new KanjiDic2Entry();
        	
        	kanjiDic2Entry.setKanji(kanji);
        	kanjiDic2Entry.setStrokeCount(strokeCount);
        	
        	kanjiDic2Entry.setOnReading(onReading);
        	kanjiDic2Entry.setKunReading(kunReading);
        	
        	kanjiDic2Entry.setEngMeaning(engMeaning);
        	
        	List<String> radicals = kradFileMap.get(kanji);
        	
        	if (radicals == null) {
        		radicals = new ArrayList<String>();
        		
        	} else {
    			List<String> mappedRadicalsList = new ArrayList<String>();
    			
    			for (String currentRadical : radicals) {
    				
    				String mappedRadicalException = radicalToCorrectRadical.get(currentRadical);
    				
    				if (mappedRadicalException == null) {					
    					mappedRadicalsList.add(currentRadical);
    					
    				} else {
    					mappedRadicalsList.add(mappedRadicalException);
    				}
    			}

    			radicals = mappedRadicalsList;
        	}
        	
        	kanjiDic2Entry.setRadicals(radicals);
        	
        	kanjiDic2Entry.setJlpt(jlpt);
        	kanjiDic2Entry.setFreq(freq);
        	
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

	private static List<String> getEngMeaning(Element rmgroup) {
		
		List<String> result = new ArrayList<String>();
		
		List<?> meaningTypeNodes = rmgroup.selectNodes("meaning[not(@m_lang)]");
		
		for (Object currentMeaningTypeAsObject : meaningTypeNodes) {
			Element currentMeaningTypeAsElement = (Element)currentMeaningTypeAsObject;
			
			result.add(currentMeaningTypeAsElement.getText());			
		}
		
		return result;
	}

	public static List<RadicalInfo> readRadkfile(String radkFile) throws IOException {
		
		createRadicalToCorrectRadicalMapIfNeeded();
				
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
				
				if (lineSplited.length > 3) {
					String mappedRadicalCode = lineSplited[3];
					
					String mappedRadical = radicalCodeToCorrectRadical.get(mappedRadicalCode);
					
					if (mappedRadical == null) {
						
						reader.close();
						
						throw new RuntimeException("RadicalExceptionMap null: " + mappedRadicalCode);
					}
					
					radical = mappedRadical;
				}
				
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
	
	private static void createRadicalToCorrectRadicalMapIfNeeded() {
		
		if (radicalToCorrectRadical != null && radicalCodeToCorrectRadical != null) {
			return;
		}
		
		radicalToCorrectRadical = new TreeMap<String, String>();
		radicalCodeToCorrectRadical = new TreeMap<String, String>();
		
		addRadicalToCorrectRadicalMaps("刈","3331","刂");
		addRadicalToCorrectRadicalMaps("滴","3557","啇");
		addRadicalToCorrectRadicalMaps("忙","3D38","忄");
		addRadicalToCorrectRadicalMaps("扎","3F37","扌");
		addRadicalToCorrectRadicalMaps("汁","4653","氵");
		addRadicalToCorrectRadicalMaps("杰","4944","灬");
		addRadicalToCorrectRadicalMaps("犯","4A6D","犭");
		addRadicalToCorrectRadicalMaps("疔","4D46","疒");
		addRadicalToCorrectRadicalMaps("礼","504B","礻");
		addRadicalToCorrectRadicalMaps("禹","5072","禸");
		addRadicalToCorrectRadicalMaps("買","5474","罒");
		addRadicalToCorrectRadicalMaps("初","5C33","衤");
		addRadicalToCorrectRadicalMaps("込","6134","辶");
		addRadicalToCorrectRadicalMaps("化","js01","亻");
		addRadicalToCorrectRadicalMaps("个","js02","个"); // brak znaku w utf8
		addRadicalToCorrectRadicalMaps("艾","js03","艹");
		addRadicalToCorrectRadicalMaps("尚","js04","尚"); // ⺌ - niepoprawne wyswietlanie znaku w utf8
		addRadicalToCorrectRadicalMaps("老","js05","耂");
		addRadicalToCorrectRadicalMaps("并","js07","丷");
		addRadicalToCorrectRadicalMaps("阡","kozatoL","阝_");
		addRadicalToCorrectRadicalMaps("邦","kozatoR","_阝");		
	}
	
	private static void addRadicalToCorrectRadicalMaps(String radical, String radicalCode, String correctRadical) {
		
		radicalToCorrectRadical.put(radical, correctRadical);
		radicalCodeToCorrectRadical.put(radicalCode, correctRadical);
	}
}
