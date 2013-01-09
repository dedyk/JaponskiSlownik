package pl.idedyk.japanese.dictionary.tools;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.DictionaryType;
import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.dto.WordType;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;

public class CsvReaderWriter {

	public static void generateDictionaryApplicationResult(String outputFile, List<PolishJapaneseEntry> polishJapaneseEntries, DictionaryType dictionaryType, boolean addKanji) throws IOException {
		
		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			if (polishJapaneseEntry.getDictionaryType() != dictionaryType) {
				continue;
			}

			String prefixKana = polishJapaneseEntry.getPrefixKana();
			
			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();
			List<String> romajiList = polishJapaneseEntry.getRomajiList();
			
			if (prefixKana.equals("を") == true && prefixRomaji.equals("o") == true) {
				prefixRomaji = "wo";
			}
			
			if (polishJapaneseEntry.getRealRomajiList() != null) {
				romajiList = polishJapaneseEntry.getRealRomajiList();
			}

			for (int romIdx = 0; romIdx < romajiList.size(); ++romIdx) {
				
				String currentRomajiEntry = "";
				
				if (prefixRomaji != null && prefixRomaji.equals("") == false) {
					currentRomajiEntry = prefixRomaji + " ";	
				}
				
				currentRomajiEntry += romajiList.get(romIdx);
								
				sb.append(polishJapaneseEntry.getWordType().getPrintable() + ":" + currentRomajiEntry);

				if (romIdx != romajiList.size() - 1) {
					sb.append(",");
				}					
			}
			sb.append(";");
			
			List<String> groups = polishJapaneseEntry.getGroups();
			
			for (int groupsIdx = 0; groupsIdx < groups.size(); ++groupsIdx) {
				
				String currentGroup = groups.get(groupsIdx);
								
				sb.append(currentGroup);

				if (groupsIdx != groups.size() - 1) {
					sb.append(",");
				}					
			}
			sb.append(";");
			
			boolean useEntry = polishJapaneseEntry.isUseEntry();
			
			sb.append(String.valueOf(useEntry)).append(";");
			
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
						String info = polishJapaneseEntry.getInfo() != null ? polishJapaneseEntry.getInfo().replaceAll("\n", ", ") : ""; 
						
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
			csvWriter.write(convertListToString(polishJapaneseEntry.getGroups()));			
			csvWriter.write(polishJapaneseEntry.getPrefixKana());
			csvWriter.write(polishJapaneseEntry.getKanji());
			csvWriter.write(convertListToString(polishJapaneseEntry.getKanaList()));
			csvWriter.write(polishJapaneseEntry.getPrefixRomaji());
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
			String groupString = csvReader.get(4);
			String prefixKanaString = csvReader.get(5);
			String kanjiString = csvReader.get(6);
			
			if (kanjiString.equals("") == true) {
				throw new JapaneseDictionaryException("Empty kanji!");
			}
			
			String kanaListString = csvReader.get(7);
			String prefixRomajiString = csvReader.get(8);
			String romajiListString = csvReader.get(9);
			String polishTranslateListString = csvReader.get(10);
			String infoString = csvReader.get(11);
			String useEntryString = csvReader.get(12);
			String knownDuplicatedListString = csvReader.get(13);
			
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
			entry.setGroups(parseStringIntoList(groupString));
			entry.setPrefixKana(prefixKanaString);
			entry.setKanji(kanjiString);
			entry.setKanaList(parseStringIntoList(kanaListString));
			entry.setPrefixRomaji(prefixRomajiString);
			entry.setRomajiList(parseStringIntoList(romajiListString));
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setUseEntry(useEntry);
			
			Set<String> knownDuplicatedHashMap = new HashSet<String>();
			
			knownDuplicatedHashMap.addAll(parseStringIntoList(knownDuplicatedListString));
			entry.setKnownDuplicatedId(knownDuplicatedHashMap);
						
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
			
			if (currentPolishTranslateString.equals("") == true) {
				continue;
			}
			
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
	
	public static void generateKanaEntriesCsvWithStrokePaths(FileOutputStream outputStream, List<KanaEntry> kanaEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(outputStream), ',');
		
		int counter = 1;
		
		for (KanaEntry kanaEntry : kanaEntries) {
			csvWriter.write(String.valueOf(counter));
			csvWriter.write(kanaEntry.getKanaJapanese());
			
			List<KanjivgEntry> strokePaths = kanaEntry.getStrokePaths();
			
			for (KanjivgEntry currentKanjivgEntry : strokePaths) {
				csvWriter.write(convertListToString(currentKanjivgEntry.getStrokePaths()));
			}
			
			csvWriter.endRecord();
			
			counter++;
		}
		
		csvWriter.close();
	}
	
	public static List<KanjiEntry> parseKanjiEntriesFromCsv(String fileName, Map<String, KanjiDic2Entry> readKanjiDic2) throws IOException, JapaneseDictionaryException {
		
		List<KanjiEntry> result = new ArrayList<KanjiEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
		
		while(csvReader.readRecord()) {
			
			int id = Integer.parseInt(csvReader.get(0));
					
			String kanjiString = csvReader.get(1);
			
			if (kanjiString.equals("") == true) {
				throw new JapaneseDictionaryException("Empty kanji!");
			}
			
			String polishTranslateListString = csvReader.get(2);
			String infoString = csvReader.get(3);
			
			String groupsString = csvReader.get(4);
			
			KanjiEntry entry = new KanjiEntry();
			
			entry.setId(id);
			entry.setKanji(kanjiString);
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setInfo(infoString);
			
			List<String> groupsList = parseStringIntoList(groupsString);
			
			entry.setGenerated(false);
			
			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(kanjiString);
			
			entry.setKanjiDic2Entry(kanjiDic2Entry);
			
			String jlpt = KanjiUtils.getJlpt(kanjiString);
			
			if (jlpt != null) {
				groupsList.add(jlpt);
			}
			
			/*
			if (kanjiDic2Entry != null) {
				Integer jlpt = kanjiDic2Entry.getJlpt();
				
				if (jlpt != null) {
					groupsList.add("JLPT " + jlpt);
				}
			}
			*/
			
			entry.setGroups(groupsList);
			
			result.add(entry);
		}
		
		csvReader.close();
		
		return result;
	}
	
	public static void generateKanjiCsv(OutputStream out, List<KanjiEntry> kanjiEntries) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');
		
		writeKanjiEntries(csvWriter, kanjiEntries);
		
		csvWriter.close();		
	}
	
	private static void writeKanjiEntries(CsvWriter csvWriter, List<KanjiEntry> kanjiEntries) throws IOException {
		
		for (KanjiEntry kanjiEntry : kanjiEntries) {
			
			csvWriter.write(String.valueOf(kanjiEntry.getId()));
			csvWriter.write(kanjiEntry.getKanji());
			
			KanjiDic2Entry kanjiDic2Entry = kanjiEntry.getKanjiDic2Entry();
			
			if (kanjiDic2Entry != null) {
				csvWriter.write(String.valueOf(kanjiDic2Entry.getStrokeCount()));
				csvWriter.write(convertListToString(kanjiDic2Entry.getRadicals()));
				csvWriter.write(convertListToString(kanjiDic2Entry.getOnReading()));
				csvWriter.write(convertListToString(kanjiDic2Entry.getKunReading()));				
			} else {
				csvWriter.write(String.valueOf(""));
				csvWriter.write(convertListToString(new ArrayList<String>()));
				csvWriter.write(convertListToString(new ArrayList<String>()));
				csvWriter.write(convertListToString(new ArrayList<String>()));								
			}
			
			KanjivgEntry kanjivgEntry = kanjiEntry.getKanjivgEntry();
			
			if (kanjivgEntry != null) {
				csvWriter.write(convertListToString(kanjivgEntry.getStrokePaths()));
			} else {
				csvWriter.write(convertListToString(new ArrayList<String>()));
			}
			
			csvWriter.write(convertListToString(kanjiEntry.getPolishTranslates()));
			csvWriter.write(kanjiEntry.getInfo());
			csvWriter.write(String.valueOf(kanjiEntry.isGenerated()));
			csvWriter.write(convertListToString(kanjiEntry.getGroups()));
			
			csvWriter.endRecord();
		}
	}

	public static List<KanjiEntry> parseKanjiEntriesFromCsv(String fileName) throws IOException, JapaneseDictionaryException {
		
		List<KanjiEntry> result = new ArrayList<KanjiEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
		
		while(csvReader.readRecord()) {
			
			int id = Integer.parseInt(csvReader.get(0));
					
			String kanjiString = csvReader.get(1);
			
			if (kanjiString.equals("") == true) {
				throw new JapaneseDictionaryException("Empty kanji!");
			}
			
			String strokeCountString = csvReader.get(2);
			
			KanjiDic2Entry kanjiDic2Entry = null;
			
			if (strokeCountString.equals("") == false) {
				
				kanjiDic2Entry = new KanjiDic2Entry();
				
				int strokeCount = Integer.parseInt(strokeCountString);
			
				String radicalsString = csvReader.get(3);
				List<String> radicals = parseStringIntoList(radicalsString);
			
				String onReadingString = csvReader.get(4);
				List<String> onReading = parseStringIntoList(onReadingString);
			
				String kunReadingString = csvReader.get(5);
				List<String> kunReading = parseStringIntoList(kunReadingString);
				
				kanjiDic2Entry.setKanji(kanjiString);
				kanjiDic2Entry.setStrokeCount(strokeCount);
				kanjiDic2Entry.setRadicals(radicals);
				kanjiDic2Entry.setKunReading(kunReading);
				kanjiDic2Entry.setOnReading(onReading);			
			}
			
			String polishTranslateListString = csvReader.get(6);
			String infoString = csvReader.get(7);
			
			String generatedString = csvReader.get(8);
			
			KanjiEntry entry = new KanjiEntry();
			
			entry.setId(id);
			entry.setKanji(kanjiString);
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setInfo(infoString);
			
			entry.setGenerated(Boolean.parseBoolean(generatedString));
						
			entry.setKanjiDic2Entry(kanjiDic2Entry);
			
			result.add(entry);
		}
		
		csvReader.close();
		
		return result;
	}

	public static void generateKanjiRadicalCsv(OutputStream out, List<RadicalInfo> radicalList) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');
		
		writeKanjiRadicalEntries(csvWriter, radicalList);
		
		csvWriter.close();		
	}

	private static void writeKanjiRadicalEntries(CsvWriter csvWriter, List<RadicalInfo> radicalList) throws IOException {
		
		for (RadicalInfo currentRadicalInfo : radicalList) {
			
			csvWriter.write(String.valueOf(currentRadicalInfo.getId()));
			csvWriter.write(currentRadicalInfo.getRadical());
			csvWriter.write(String.valueOf(currentRadicalInfo.getStrokeCount()));
			
			csvWriter.endRecord();
		}		
	}
	
	public static List<RadicalInfo> parseRadicalEntriesFromCsv(String fileName) throws IOException, JapaneseDictionaryException {
		
		List<RadicalInfo> result = new ArrayList<RadicalInfo>();
		
		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');
		
		while(csvReader.readRecord()) {
			
			int id = Integer.parseInt(csvReader.get(0));
					
			String radical = csvReader.get(1);
			
			if (radical.equals("") == true) {
				throw new JapaneseDictionaryException("Empty radical!");
			}
			
			String strokeCountString = csvReader.get(2);
			
			int strokeCount = Integer.parseInt(strokeCountString);
			
			RadicalInfo entry = new RadicalInfo();
			
			entry.setId(id);
			entry.setRadical(radical);
			entry.setStrokeCount(strokeCount);
			
			result.add(entry);
		}
		
		csvReader.close();
		
		return result;
	}
}

