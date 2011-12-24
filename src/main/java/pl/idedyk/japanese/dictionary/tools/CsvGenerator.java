package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.japannaka.utils.Utils;

public class CsvGenerator {
	
	public static void generateCsv(String outputDir, Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries) throws IOException {
		
		DictionaryEntryType[] dictionaryTypes = new DictionaryEntryType[polishJapaneseEntries.size()];
		
		polishJapaneseEntries.keySet().toArray(dictionaryTypes);
		
		for (DictionaryEntryType dictionaryEntryType : dictionaryTypes) {
			
			StringBuffer sb = new StringBuffer();
			
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries.get(dictionaryEntryType)) {
				
				List<String> romajiList = polishJapaneseEntry.getRomajiList();
				
				for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
					sb.append(Utils.replaceChars(romajiList.get(romIdx)));
					
					if (romIdx != romajiList.size() - 1) {
						sb.append(",");
					}					
				}
				sb.append(";");				
				
				if (polishJapaneseEntry.getJapanese() != null) {
					sb.append(polishJapaneseEntry.getJapanese()).append(";");
				}
				
				if (polishJapaneseEntry.getJapaneseImagePath() != null) {
					sb.append(polishJapaneseEntry.getJapaneseImagePath()).append(";");
				}
				
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
			
			System.out.println(outputDir + File.separatorChar + dictionaryEntryType.getFileName());
			
			PrintWriter pw = new PrintWriter(outputDir + File.separatorChar + dictionaryEntryType.getFileName());
			
			pw.write(sb.toString());
			
			pw.close();
		}
	}
}
