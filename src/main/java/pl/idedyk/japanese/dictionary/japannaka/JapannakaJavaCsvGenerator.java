package pl.idedyk.japanese.dictionary.japannaka;

import java.util.List;

import pl.idedyk.japanese.dictionary.japannaka.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.japannaka.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.japannaka.utils.Utils;

public class JapannakaJavaCsvGenerator {
	
	public static void main(String[] args) throws Exception {
		
		// test
	
		List<PolishJapaneseEntry> japanesePolishDictionary = 
			JapannakaHtmlReader.readJapannakaHtmlDir("websites/www.japannaka.republika.pl");
		
		StringBuffer sb = new StringBuffer();
		
		for (PolishJapaneseEntry polishJapaneseEntry : japanesePolishDictionary) {
			
			sb.append(Utils.replaceChars(polishJapaneseEntry.getRomaji())).append(";");
			//sb.append(polishJapaneseEntry.getJapanese()).append(";");
			
			List<PolishTranslate> polishTranslates = polishJapaneseEntry.getPolishTranslates();
			
			if (polishTranslates != null) {
				for (PolishTranslate currentPolishTranslate : polishTranslates) {
					
					sb.append(Utils.replaceChars(currentPolishTranslate.getWord())).append("|");
					
					if (currentPolishTranslate.getInfo() != null && currentPolishTranslate.getInfo().size() > 0) {
						for (int infoIdx = 0; infoIdx < currentPolishTranslate.getInfo().size(); ++infoIdx) {
							sb.append(Utils.replaceChars(currentPolishTranslate.getInfo().get(infoIdx)));
							
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
}
