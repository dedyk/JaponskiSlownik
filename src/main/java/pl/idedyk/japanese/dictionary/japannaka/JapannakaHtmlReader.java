package pl.idedyk.japanese.dictionary.japannaka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
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
							
							List<PolishTranslate> polishTranslateList = createPolishTranslateList(polishTranslates);
							
							polishJapaneseEntry.setPolishTranslates(polishTranslateList);
							
							result.add(polishJapaneseEntry);
							
							romaji = null;
							japanese = null;
							polishTranslates = null;
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
	
	private static List<PolishTranslate> createPolishTranslateList(String polishTranslates) {
		List<PolishTranslate> polishTranslateList = new ArrayList<PolishTranslate>();
		
		System.out.println("BEFORE: " + polishTranslates);
		
		List<String> splitedPolishTranslatesWord = new ArrayList<String>();
		
		String[] splitedPolishTranslates = polishTranslates.split("\\),");
		
		int state = 0;
		
		StringBuffer sb = new StringBuffer();
		
		for (String currentSplitedPolishTranslate : splitedPolishTranslates) {
			String currentSplitedPolishTranslate2 = currentSplitedPolishTranslate.trim();
			
			if (currentSplitedPolishTranslate2.indexOf("(") != -1) {
				currentSplitedPolishTranslate2 = currentSplitedPolishTranslate2 + ")";
			}
			
			
			
			System.out.println(currentSplitedPolishTranslate2);
			
			
		}
		
		/*
		
		
		int parseState = 0;
		
		PolishTranslate polishTranslate = null;
		
		while(true) {
			
			boolean hasMoreElements = st.hasMoreElements();
			
			if (hasMoreElements == false && polishTranslate != null) {
				polishTranslateList.add(polishTranslate);
				
				polishTranslate = null;
				
				break;
			} else if (hasMoreElements == false) {
				break;
			}
			
			String nextElement = (String)st.nextElement();
			
			if (nextElement.endsWith(",") == true) {
				nextElement = nextElement.substring(0, nextElement.length() - 1);
			}
			
			if (parseState == 0) {
				polishTranslate = new PolishTranslate();
				
				polishTranslate.setWord(nextElement.trim());
				
				parseState = 1;
			} else if (parseState == 1 && nextElement.startsWith("(") == false) {
				polishTranslateList.add(polishTranslate);
				
				polishTranslate = null;
				
				polishTranslate = new PolishTranslate();
				
				polishTranslate.setWord(nextElement.trim());
				
				parseState = 1;
			} else if (parseState == 1 && nextElement.startsWith("(") == true && nextElement.endsWith(")") == true) {
				List<String> polishTranslateInfos = new ArrayList<String>();
				
				polishTranslateInfos.add(nextElement.substring(1, nextElement.length() - 1));
				
				polishTranslate.setInfo(polishTranslateInfos);
				
				polishTranslateList.add(polishTranslate);
				
				polishTranslate = null;
				
				parseState = 0;
			} else if (parseState == 1 && nextElement.startsWith("(") == true) {
				List<String> polishTranslateInfos = new ArrayList<String>();
				
				polishTranslateInfos.add(nextElement.substring(1));
				
				polishTranslate.setInfo(polishTranslateInfos);
				
				parseState = 2;			
			} else if (parseState == 2 && nextElement.endsWith(")") == true) {
				
				polishTranslate.getInfo().add(nextElement.substring(0, nextElement.length() - 1));
				
				polishTranslateList.add(polishTranslate);
				
				polishTranslate = null;
				
				parseState = 0;
			} else if (parseState == 2) {
				polishTranslate.getInfo().add(nextElement.substring(0, nextElement.length()));
			}
										
		}
		*/
		
		return polishTranslateList;
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
	
	public static void main(String[] args) throws Exception {
		
		// test
/*	
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
		
		System.out.println(japanesePolishDictionary.size()); */
		
		//String polishTranslates = "abc, cbd, efg";
		String polishTranslates = "mierzyć kota (wysokość kota, wzrost kota), xxx (yyy, yyy2, zzz), bbb (ccc), www, uuu, ee (e), jj, f (ff, ff2), jk, po";
		//String polishTranslates = "bbb (ccc)";
		
		List<PolishTranslate> polishTranslateList = createPolishTranslateList(polishTranslates);
		
		for (PolishTranslate polishTranslate2 : polishTranslateList) {
			System.out.println(polishTranslate2.getWord());
			System.out.println(polishTranslate2.getInfo());	
		}
		
		
		/*
		String[] polishTranslatesSplited = polishTranslates.split(",");
		
		for (String currentPolishTranslate : polishTranslatesSplited) {
			
			currentPolishTranslate = trim(currentPolishTranslate);
			
			PolishTranslate polishTranslateEntry = new PolishTranslate();
			
			fillPolishTranslateEntry(polishTranslateEntry, currentPolishTranslate);
			
			System.out.println(polishTranslateEntry.getWord());
			System.out.println(polishTranslateEntry.getInfo());	
		}
		*/
		
		//PolishTranslate pt = new PolishTranslate();
		
		//fillPolishTranslateEntry(pt, "mierzyć (wysokość, wzrost)");
		
		
	}
}
