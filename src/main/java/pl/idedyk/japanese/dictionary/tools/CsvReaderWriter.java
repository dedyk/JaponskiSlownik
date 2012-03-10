package pl.idedyk.japanese.dictionary.tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.dto.RomajiEntry;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.genki.WordType;

public class CsvReaderWriter {

	public static void generateDictionaryApplicationResult(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			List<RomajiEntry> romajiList = polishJapaneseEntry.getRomajiList();

			for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
				RomajiEntry currentRomajiEntry = romajiList.get(romIdx);
								
				sb.append(polishJapaneseEntry.getWordType().getPrintable() + ":" + currentRomajiEntry.getRomaji());

				if (romIdx != romajiList.size() - 1) {
					sb.append(",");
				}					
			}
			sb.append(";");				

			if (polishJapaneseEntry.getJapanese() != null && polishJapaneseEntry.getJapanese().equals("") == false) {
				sb.append(polishJapaneseEntry.getJapanese()).append(";");
			}

			if (polishJapaneseEntry.getJapaneseImagePath() != null) {
				sb.append(polishJapaneseEntry.getJapaneseImagePath()).append(";");
			}

			List<PolishTranslate> polishTranslates = polishJapaneseEntry.getPolishTranslates();

			if (polishTranslates != null) {
				for (int idxPolishTranslates = 0; idxPolishTranslates < polishTranslates.size(); ++idxPolishTranslates) {
					PolishTranslate currentPolishTranslate = polishTranslates.get(idxPolishTranslates);
					
					sb.append(currentPolishTranslate.getWord()).append("|");
					
					if (idxPolishTranslates == polishTranslates.size() - 1) {
						String info = polishJapaneseEntry.getInfo() != null ? polishJapaneseEntry.getInfo() : ""; 
						
						if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_VERB_RU) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "ru-czasownik";
						} else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_VERB_U) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "u-czasownik";
						} else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_ADJECTIVE_I) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "i-przymiotnik";
						} else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_ADJECTIVE_NA) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "na-przymiotnik";
						}
						
						sb.append(info);
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
	
	public static void generateCsv(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFile), ',');
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			csvWriter.write(polishJapaneseEntry.getDictionaryEntryType().toString());
			csvWriter.write(polishJapaneseEntry.getWordType().toString());
			csvWriter.write(polishJapaneseEntry.getJapanese());
			csvWriter.write(convertRomajiEntryListToString(polishJapaneseEntry.getRomajiList()));
			csvWriter.write(convertPolishTranslateListToString(polishJapaneseEntry.getPolishTranslates()));
			csvWriter.write(polishJapaneseEntry.getInfo());
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}
	
	public static List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv(String fileName) throws IOException {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
		
		while(csvReader.readRecord()) {
			
			String groupNameString = csvReader.get(0);
			String wordTypeString = csvReader.get(1);
			String japaneseString = csvReader.get(2);
			String romajiListString = csvReader.get(3);
			String polishTranslateListString = csvReader.get(4);
			String infoString = csvReader.get(5);
			
			PolishJapaneseEntry entry = new PolishJapaneseEntry();
			
			entry.setDictionaryEntryType(DictionaryEntryType.valueOf(groupNameString));
			entry.setWordType(WordType.valueOf(wordTypeString));
			entry.setJapanese(japaneseString);
			entry.setRomajiList(parsePolishRomajiString(romajiListString));
			entry.setPolishTranslates(parsePolishTranslateString(polishTranslateListString));
			
			entry.setInfo(infoString);
			
			result.add(entry);
		}
		
		csvReader.close();
		
		return result;
	}
	
	private static String convertPolishTranslateListToString(List<PolishTranslate> list) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx).getWord());
			
			if (idx != list.size() - 1) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	private static List<PolishTranslate> parsePolishTranslateString(String polishTranslateString) {
		
		List<PolishTranslate> result = new ArrayList<PolishTranslate>();
		
		String[] splitedPolishTranslateString = polishTranslateString.split("\n");
		
		for (String currentpolishTranslateString : splitedPolishTranslateString) {
			PolishTranslate polishTranslate = new PolishTranslate();
			
			polishTranslate.setWord(currentpolishTranslateString);
			
			result.add(polishTranslate);
		}
		
		return result;		
	}
	
	private static String convertRomajiEntryListToString(List<RomajiEntry> list) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx).getRomaji());
			
			if (idx != list.size() - 1) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	private static List<RomajiEntry> parsePolishRomajiString(String romajiString) {
		
		List<RomajiEntry> result = new ArrayList<RomajiEntry>();
		
		String[] splitedRomajiListString = romajiString.split("\n");
		
		for (String currentRomajiString : splitedRomajiListString) {
			RomajiEntry romajiEntry = new RomajiEntry();
			
			romajiEntry.setRomaji(currentRomajiString);
			
			result.add(romajiEntry);
		}
		
		return result;		
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
