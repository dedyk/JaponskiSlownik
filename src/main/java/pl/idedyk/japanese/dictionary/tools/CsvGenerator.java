package pl.idedyk.japanese.dictionary.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.dto.RomajiEntry;

public class CsvGenerator {

	public static void generateDictionaryApplicationResult(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		String lastGroupName = null;
		
		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String polishJapaneseEntryGroupName = polishJapaneseEntry.getGroupName().getName();
			
			if (lastGroupName == null || lastGroupName.equals(polishJapaneseEntryGroupName) == false) {
				sb.append("---" + polishJapaneseEntryGroupName + "---\n");
				
				lastGroupName = polishJapaneseEntryGroupName;
			}

			List<RomajiEntry> romajiList = polishJapaneseEntry.getRomajiList();

			for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
				RomajiEntry currentRomajiEntry = romajiList.get(romIdx);
								
				sb.append(polishJapaneseEntry.getWordType().getPrintable() + ":" + currentRomajiEntry.getRomaji());

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

					sb.append(currentPolishTranslate.getWord()).append("|");
					sb.append(polishJapaneseEntry.getInfo());

					sb.append(";");
				}
			}

			sb.append("\n");
		}	

		System.out.println(outputFile);

		PrintWriter pw = new PrintWriter(outputFile);

		pw.write(sb.toString());

		pw.close();
	}
	
	public static void generateCsv(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFile), ',');
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			csvWriter.write(polishJapaneseEntry.getGroupName().toString());
			csvWriter.write(polishJapaneseEntry.getWordType().toString());
			csvWriter.write(polishJapaneseEntry.getJapanese());
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}
	
	//private static String convertArrayToString(String)

	public static void generateKanaEntriesCsv(String outputFile, List<KanaEntry> kanaEntries) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		
		for (KanaEntry kanaEntry : kanaEntries) {
			sb.append(kanaEntry.getKana()).append(";");
			sb.append(kanaEntry.getKanaJapanese()).append(";");
			sb.append(kanaEntry.getImage()).append("\n");
		}
		
		System.out.println(outputFile);

		PrintWriter pw = new PrintWriter(outputFile);

		pw.write(sb.toString());

		pw.close();
	}
}
