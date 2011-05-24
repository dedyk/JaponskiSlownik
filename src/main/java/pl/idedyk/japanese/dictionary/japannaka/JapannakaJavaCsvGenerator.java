package pl.idedyk.japanese.dictionary.japannaka;

import java.util.List;

import pl.idedyk.japanese.dictionary.japannaka.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.japannaka.dto.PolishTranslate;

public class JapannakaJavaCsvGenerator {
	
	public static void main(String[] args) throws Exception {
		
		// test
	
		List<PolishJapaneseEntry> japanesePolishDictionary = 
			JapannakaHtmlReader.readJapannakaHtmlDir("websites/www.japannaka.republika.pl");
		
		StringBuffer sb = new StringBuffer();
		
		for (PolishJapaneseEntry polishJapaneseEntry : japanesePolishDictionary) {
			
			sb.append(replaceChars(polishJapaneseEntry.getRomaji())).append(";");
			//sb.append(polishJapaneseEntry.getJapanese()).append(";");
			
			List<PolishTranslate> polishTranslates = polishJapaneseEntry.getPolishTranslates();
			
			if (polishTranslates != null) {
				for (PolishTranslate currentPolishTranslate : polishTranslates) {
					
					sb.append(replaceChars(currentPolishTranslate.getWord())).append("|");
					
					if (currentPolishTranslate.getInfo() != null && currentPolishTranslate.getInfo().size() > 0) {
						for (int infoIdx = 0; infoIdx < currentPolishTranslate.getInfo().size(); ++infoIdx) {
							sb.append(replaceChars(currentPolishTranslate.getInfo().get(infoIdx)));
							
							if (infoIdx != currentPolishTranslate.getInfo().size() - 1) {
								sb.append(",");
							}
						}
					}
					
					sb.append(";");
				}
			}
			
			sb.append("\n");
		}
		
		System.out.println(sb.toString());
	}
	
	private static String replaceChars(String text) {
		
		text = text.replaceAll("Ę", "E");
		text = text.replaceAll("ę", "e");
		
		text = text.replaceAll("Ó", "O");
		text = text.replaceAll("ó", "o");
		
		text = text.replaceAll("Ą", "A");
		text = text.replaceAll("ą", "a");
		
		text = text.replaceAll("Ś", "S");
		text = text.replaceAll("ś", "s");
		
		text = text.replaceAll("Ł", "L");
		text = text.replaceAll("ł", "l");
		
		text = text.replaceAll("Ż", "Z");
		text = text.replaceAll("ż", "z");
		
		text = text.replaceAll("Ź", "z");
		text = text.replaceAll("ź", "z");
		
		text = text.replaceAll("Ć", "C");
		text = text.replaceAll("ć", "c");
		
		text = text.replaceAll("Ń", "N");
		text = text.replaceAll("ń", "n");
		
		text = text.replaceAll("ō", "o-");
		text = text.replaceAll("ū", "u-");
		
		return text;
	}
}
