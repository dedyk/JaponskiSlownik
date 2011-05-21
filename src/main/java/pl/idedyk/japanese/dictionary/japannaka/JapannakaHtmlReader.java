package pl.idedyk.japanese.dictionary.japannaka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import pl.idedyk.japanese.dictionary.japannaka.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.japannaka.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.japannaka.exception.JapannakaException;

public class JapannakaHtmlReader {

	public static List<PolishJapaneseEntry> readJapannakaHtmlDir(String dir) throws JapannakaException {
		
		File dirFile = new File(dir);
		
		File[] htmlDictionaryFileList = dirFile.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				
				return name.endsWith(".html") && name.equals("index.html") == false;
			}
		});
		//File[] htmlDictionaryFileList = new File[] { new File(dir + "/sb.html") };
		
		List<PolishJapaneseEntry> result = 
			new ArrayList<PolishJapaneseEntry>();
		
		InputStream is = null;
		
		try {
			for (File currentHtmlDictionaryFile : htmlDictionaryFileList) {
				
				is = new FileInputStream(currentHtmlDictionaryFile);
				
				Source htmlSource = new Source(is);
				
				List<Element> tableElements = htmlSource.getAllElements("table");
				
				if (tableElements.size() == 1) {
					
					List<Element> tableAllElemets = tableElements.get(0).getAllElements();
					
					int counter = 0;
					
					String romaji = null;
					String japanese = null;
					String polishTranslates = null;
					
					for (Element currentElement : tableAllElemets) {
						if (currentElement.getName().equals("td") == false) {
							continue;
						}
						
						if (counter == 0) {
							romaji = trim(currentElement.getContent().getTextExtractor().toString());
							
							counter++;
						} else if (counter == 1) {
							japanese = trim(currentElement.getContent().getTextExtractor().toString());
							
							counter++;
						} else if (counter == 2) {
							polishTranslates = trim(currentElement.getContent().getTextExtractor().toString());
							
							counter = 0;
							
							if (romaji.toLowerCase().equals("romaji") == true) {
								continue;
							}
							
							PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
							
							polishJapaneseEntry.setRomaji(romaji);
							polishJapaneseEntry.setJapanese(japanese);
							
							List<PolishTranslate> polishTranslateList = new ArrayList<PolishTranslate>();
							
							String[] polishTranslatesSplited = polishTranslates.split(",");
							
							for (String currentPolishTranslate : polishTranslatesSplited) {
								
								currentPolishTranslate = trim(currentPolishTranslate);
								
								PolishTranslate polishTranslateEntry = new PolishTranslate();
								
								fillPolishTranslateEntry(polishTranslateEntry, currentPolishTranslate);
								
								polishTranslateList.add(polishTranslateEntry);
							}
							
							polishJapaneseEntry.setPolishTranslates(polishTranslateList);
							
							result.add(polishJapaneseEntry);
						}
					}
					
					
					
				} else {
					throw new JapannakaException("Bad number of table html element");
				}
			}
		} catch (FileNotFoundException e) {
			throw new JapannakaException("Error during read html japannaka file", e);
		} catch (IOException e) {
			throw new JapannakaException("Error during read html japannaka file", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) { }
			}
		}
		
		return result;
	}
	
	private static String trim(String text) {
		if (text == null) {
			return text;
		}
		
		text = text.trim();
		
		if (text.charAt(0) == (char)160) {
			text = text.substring(1);
		}
		
		return text;
	}
	
	private static void fillPolishTranslateEntry(PolishTranslate polishTranslateEntry, String polishTranslate) {
		
		String regex = "\\((.*?)\\)";
		
		Pattern compiledRegex = Pattern.compile(regex);
		
		Matcher matcher = compiledRegex.matcher(polishTranslate);
		
		List<String> infos = new ArrayList<String>();;
		
		while (matcher.find()) {
			
			infos.add(matcher.group().substring(1, matcher.group().length() - 1));
		}
		
		String word = polishTranslate.replaceAll(regex, "").trim();
		
		polishTranslateEntry.setWord(word);
		polishTranslateEntry.setInfo(infos);
	}

	public static void main(String[] args) throws Exception {
		
		// test
	
		List<PolishJapaneseEntry> japanesePolishDictionary = 
			readJapannakaHtmlDir("websites/www.japannaka.republika.pl");
		
		for (PolishJapaneseEntry polishJapaneseEntry : japanesePolishDictionary) {
			
			System.out.println("Romaji: " + polishJapaneseEntry.getRomaji());
			System.out.println("Japanese: " + polishJapaneseEntry.getJapanese());
			
			List<PolishTranslate> polishTranslates = polishJapaneseEntry.getPolishTranslates();
			
			if (polishTranslates != null) {
				for (PolishTranslate currentPolishTranslate : polishTranslates) {
				
					System.out.println("\tWord = " + currentPolishTranslate.getWord());
					System.out.println("\tInfo = " + currentPolishTranslate.getInfo());
				}
			}
			
			System.out.println("-----------");
		}
		
		System.out.println(japanesePolishDictionary.size());
	}
}
