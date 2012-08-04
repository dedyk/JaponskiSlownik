package pl.idedyk.japanese.dictionary.tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.genki.DictionaryType;
import pl.idedyk.japanese.dictionary.genki.WordType;

public class CsvReaderWriter {

	public static void generateDictionaryApplicationResult(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries, boolean addKanji) throws IOException {
		
		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			List<String> romajiList = polishJapaneseEntry.getRomajiList();

			for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
				String currentRomajiEntry = romajiList.get(romIdx);
								
				sb.append(polishJapaneseEntry.getWordType().getPrintable() + ":" + currentRomajiEntry);

				if (romIdx != romajiList.size() - 1) {
					sb.append(",");
				}					
			}
			sb.append(";");
			
			if (addKanji == true && polishJapaneseEntry.getKanji() != null && polishJapaneseEntry.getKanji().equals("") == false) {
				sb.append(polishJapaneseEntry.getFullKanji()).append(";");
			}

			if (polishJapaneseEntry.getKanjiImagePath() != null && polishJapaneseEntry.getKanjiImagePath().equals("") == false) {
				sb.append(polishJapaneseEntry.getKanjiImagePath()).append(";");
			}

			List<String> polishTranslates = polishJapaneseEntry.getPolishTranslates();

			if (polishTranslates != null) {
				for (int idxPolishTranslates = 0; idxPolishTranslates < polishTranslates.size(); ++idxPolishTranslates) {
					String currentPolishTranslate = polishTranslates.get(idxPolishTranslates);
					
					sb.append(currentPolishTranslate).append("|");
					
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
						} else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_VERB_IRREGULAR) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "czasownik nieregularny";
						}  else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_VERB_TE) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "forma te";
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
						} else if (polishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
							if (info.length() > 0) {
								info = info + ", ";
							}
							
							info += "czytanie";
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
	
	public static void generateCsv(OutputStream out, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');
		
		writePolishJapaneseEntries(csvWriter, polishJapaneseEntries);
		
		csvWriter.close();		
	}
	
	public static void generateCsv(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFile), ',');
		
		writePolishJapaneseEntries(csvWriter, polishJapaneseEntries);
		
		csvWriter.close();
	}
	
	private static void writePolishJapaneseEntries(CsvWriter csvWriter, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			csvWriter.write(String.valueOf(polishJapaneseEntry.getId()));
			csvWriter.write(polishJapaneseEntry.getDictionaryType().getName());
			csvWriter.write(polishJapaneseEntry.getDictionaryEntryType().toString());
			csvWriter.write(polishJapaneseEntry.getWordType().toString());						
			csvWriter.write(polishJapaneseEntry.getPrefix());
			csvWriter.write(polishJapaneseEntry.getKanji());
			csvWriter.write(polishJapaneseEntry.getKanjiImagePath());
			csvWriter.write(convertListToString(polishJapaneseEntry.getKanaList()));
			csvWriter.write(convertListToString(polishJapaneseEntry.getRomajiList()));
			csvWriter.write(convertListToString(polishJapaneseEntry.getPolishTranslates()));
			csvWriter.write(polishJapaneseEntry.getInfo());
			csvWriter.write(polishJapaneseEntry.isUseEntry() == false ? "NO" : "");
			
			csvWriter.endRecord();
		}

	}
	
	public static List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv(String fileName, String filterName) throws IOException, JapaneseDictionaryException {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
		
		while(csvReader.readRecord()) {
			
			int id = Integer.parseInt(csvReader.get(0));
			
			String dictionaryType = csvReader.get(1);
			
			if (filterName != null && dictionaryType.equals(filterName) == false) {
				continue;
			}
			
			String dictionaryEntryTypeString = csvReader.get(2);
			String wordTypeString = csvReader.get(3);
			String prefixString = csvReader.get(4);
			String kanjiString = csvReader.get(5);
			
			if (kanjiString.equals("") == true) {
				throw new JapaneseDictionaryException("Empty kanji!");
			}
			
			String kanjiImagePathString = csvReader.get(6);
			String kanaListString = csvReader.get(7);
			String romajiListString = csvReader.get(8);
			String polishTranslateListString = csvReader.get(9);
			String infoString = csvReader.get(10);
			String useEntryString = csvReader.get(11);
			
			boolean useEntry = true;
			
			if (useEntryString != null) {
				if (useEntryString.equals("NO")) {
					useEntry = false;
				} else if (useEntryString.equals("") == false) {
					throw new JapaneseDictionaryException("Bad use entry string: " + useEntryString);
				}
			}
			
			DictionaryEntryType dictionaryEntryType = DictionaryEntryType.valueOf(dictionaryEntryTypeString);
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			PolishJapaneseEntry entry = new PolishJapaneseEntry();
			
			entry.setId(id);
			entry.setDictionaryType(DictionaryType.valueOf(dictionaryType));
			entry.setDictionaryEntryType(dictionaryEntryType);
			entry.setWordType(WordType.valueOf(wordTypeString));
			entry.setPrefix(prefixString);
			entry.setKanji(kanjiString);
			entry.setKanjiImagePath(kanjiImagePathString);
			entry.setKanaList(parseStringIntoList(kanaListString));
			entry.setRomajiList(parseStringIntoList(romajiListString));
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setUseEntry(useEntry);
			
			entry.setInfo(infoString);
			
			result.add(entry);
		}
		
		csvReader.close();
		
		return result;
	}
	
	private static String convertListToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx));
			
			if (idx != list.size() - 1) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	private static List<String> parseStringIntoList(String polishTranslateString) {
		
		List<String> result = new ArrayList<String>();
		
		String[] splitedPolishTranslateString = polishTranslateString.split("\n");
		
		for (String currentPolishTranslateString : splitedPolishTranslateString) {			
			result.add(currentPolishTranslateString);
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
