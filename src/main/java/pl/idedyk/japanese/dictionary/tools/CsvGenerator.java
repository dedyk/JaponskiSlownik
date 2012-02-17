package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.dto.RomajiEntry;
import pl.idedyk.japanese.dictionary.japannaka.utils.Utils;

public class CsvGenerator {

	public static void generateCsv(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		String lastGroupName = null;
		
		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String polishJapaneseEntryGroupName = polishJapaneseEntry.getGroupName();
			
			if (lastGroupName == null || lastGroupName.equals(polishJapaneseEntryGroupName) == false) {
				sb.append("---" + polishJapaneseEntryGroupName + "---\n");
				
				lastGroupName = polishJapaneseEntryGroupName;
			}

			List<RomajiEntry> romajiList = polishJapaneseEntry.getRomajiList();

			for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
				RomajiEntry currentRomajiEntry = romajiList.get(romIdx);
								
				sb.append(currentRomajiEntry.getWordType().getPrintable() + ":" + Utils.replaceChars(currentRomajiEntry.getRomaji()));

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

		System.out.println(outputFile);

		PrintWriter pw = new PrintWriter(outputFile);

		pw.write(sb.toString());

		pw.close();
	}

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
