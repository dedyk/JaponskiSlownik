package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.GroupWithTatoebaSentenceList;
import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.api.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.api.dto.TatoebaSentence;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CsvReaderWriter {
	
	private static final int MAX_DICTIONARY_SIZE = 100000;

	public static void generateDictionaryApplicationResult(String outputFile,
			List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {

		StringBuffer sb = new StringBuffer();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			if (polishJapaneseEntry.getGroups().size() == 1
					&& polishJapaneseEntry.getGroups().get(0).equals("Inne") == true) {
				continue;
			}

			String prefixKana = polishJapaneseEntry.getPrefixKana();

			String prefixRomaji = polishJapaneseEntry.getPrefixRomaji();
			String romaji = polishJapaneseEntry.getRomaji();

			if (prefixKana.equals("を") == true && prefixRomaji.equals("o") == true) {
				prefixRomaji = "wo";
			}

			if (polishJapaneseEntry.getRealRomaji() != null) {
				romaji = polishJapaneseEntry.getRealRomaji();
			}

			
			String currentRomajiEntry = "";

			currentRomajiEntry += romaji;

			sb.append(polishJapaneseEntry.getWordType().getPrintable() + ":" + currentRomajiEntry);
			
			sb.append(";");

			if (prefixRomaji != null && prefixRomaji.equals("") == false) {
				sb.append(prefixRomaji);
			}

			sb.append(";");

			List<String> groups = GroupEnum.convertToValues(polishJapaneseEntry.getGroups());

			for (int groupsIdx = 0; groupsIdx < groups.size(); ++groupsIdx) {

				String currentGroup = groups.get(groupsIdx);

				sb.append(currentGroup);

				if (groupsIdx != groups.size() - 1) {
					sb.append(",");
				}
			}
			sb.append(";");

			sb.append(String.valueOf(true)).append(";");

			if (polishJapaneseEntry.getKanji() != null && polishJapaneseEntry.getKanji().equals("") == false) {
				sb.append(polishJapaneseEntry.getFullKanji()).append(";");
			}

			if (polishJapaneseEntry.getKanjiImagePath() != null
					&& polishJapaneseEntry.getKanjiImagePath().equals("") == false) {
				sb.append(polishJapaneseEntry.getKanjiImagePath()).append(";");
			}

			List<String> polishTranslates = polishJapaneseEntry.getTranslates();

			if (polishTranslates != null) {
				for (int idxPolishTranslates = 0; idxPolishTranslates < polishTranslates.size(); ++idxPolishTranslates) {
					String currentPolishTranslate = polishTranslates.get(idxPolishTranslates);

					sb.append(currentPolishTranslate).append("|");

					if (idxPolishTranslates == polishTranslates.size() - 1) {
						String info = polishJapaneseEntry.getInfo() != null ? polishJapaneseEntry.getInfo().replaceAll(
								"\n", ", ") : "";

						if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_VERB_RU) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "ru-czasownik";
						} else if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_VERB_U) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "u-czasownik";
						} else if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_VERB_IRREGULAR) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "czasownik nieregularny";
						} else if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_VERB_TE) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "forma te";
						} else if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_ADJECTIVE_I) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "i-przymiotnik";
						} else if (polishJapaneseEntry.getDictionaryEntryTypeList().get(0) == DictionaryEntryType.WORD_ADJECTIVE_NA) {
							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "na-przymiotnik";
						}

						AttributeList attributeList = polishJapaneseEntry.getAttributeList();

						if (attributeList.contains(AttributeType.VERB_TRANSITIVITY)) {

							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "czasownik przechodni";
						} else if (attributeList.contains(AttributeType.VERB_INTRANSITIVITY)) {

							if (info.length() > 0) {
								info = info + ", ";
							}

							info += "czasownik nieprzechodni";
						}

						//System.out.println(info);

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

	/*
	public static void generateCsv(OutputStream out, List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addKnownDupplicatedId) throws IOException {

		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');

		writePolishJapaneseEntries(csvWriter, polishJapaneseEntries, addKnownDupplicatedId, true, true, null);

		csvWriter.close();
	}
	*/

	public static void generateCsv(String[] outputFiles, List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addKnownDupplicatedId) throws IOException {
		
		List<List<PolishJapaneseEntry>> polishJapaneseEntriesSplited = null;
		
		if (outputFiles.length == 1) {
			polishJapaneseEntriesSplited = new ArrayList<List<PolishJapaneseEntry>>();
			
			polishJapaneseEntriesSplited.add(polishJapaneseEntries);
			
		} else {
			polishJapaneseEntriesSplited = splitList(polishJapaneseEntries, MAX_DICTIONARY_SIZE);
		}		

		if (outputFiles.length < polishJapaneseEntriesSplited.size()) {
			throw new RuntimeException("Brak wystarczającej liczby plików");
		}
		
		for (int idx = 0; idx < polishJapaneseEntriesSplited.size(); ++idx) {
			
			CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFiles[idx]), ',');

			writePolishJapaneseEntries(csvWriter, polishJapaneseEntriesSplited.get(idx), addKnownDupplicatedId, true, true, null);

			csvWriter.close();
		}		
	}

	public static void generateCsv(String[] outputFiles, List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addKnownDupplicatedId, boolean addId, boolean addExampleSentenceGroupIds) throws IOException {

		List<List<PolishJapaneseEntry>> polishJapaneseEntriesSplited = null;
		
		if (outputFiles.length == 1) {
			polishJapaneseEntriesSplited = new ArrayList<List<PolishJapaneseEntry>>();
			
			polishJapaneseEntriesSplited.add(polishJapaneseEntries);
			
		} else {
			polishJapaneseEntriesSplited = splitList(polishJapaneseEntries, MAX_DICTIONARY_SIZE);
		}		

		if (outputFiles.length < polishJapaneseEntriesSplited.size()) {
			throw new RuntimeException("Brak wystarczającej liczby plików");
		}
		
		for (int idx = 0; idx < polishJapaneseEntriesSplited.size(); ++idx) {
			
			CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFiles[idx]), ',');

			writePolishJapaneseEntries(csvWriter, polishJapaneseEntriesSplited.get(idx), addKnownDupplicatedId, addId, addExampleSentenceGroupIds, null);

			csvWriter.close();
		}
	}

	public static void generateCsv(String[] outputFiles, List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addKnownDupplicatedId, boolean addId, boolean addExampleSentenceGroupIds,
			ICustomAdditionalCsvWriter customAdditionalCsvWriter) throws IOException {

		List<List<PolishJapaneseEntry>> polishJapaneseEntriesSplited = null;
		
		if (outputFiles.length == 1) {
			polishJapaneseEntriesSplited = new ArrayList<List<PolishJapaneseEntry>>();
			
			polishJapaneseEntriesSplited.add(polishJapaneseEntries);
			
		} else {
			polishJapaneseEntriesSplited = splitList(polishJapaneseEntries, MAX_DICTIONARY_SIZE);
		}

		if (outputFiles.length < polishJapaneseEntriesSplited.size()) {
			throw new RuntimeException("Brak wystarczającej liczby plików");
		}
		
		for (int idx = 0; idx < polishJapaneseEntriesSplited.size(); ++idx) {
			
			CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFiles[idx]), ',');

			writePolishJapaneseEntries(csvWriter, polishJapaneseEntriesSplited.get(idx), addKnownDupplicatedId, addId, addExampleSentenceGroupIds, customAdditionalCsvWriter);

			csvWriter.close();
		}
	}
	
	private static <T> List<List<T>> splitList(List<T> list, int size) {
		
		if (size <= 0) {
			throw new RuntimeException("size <= 0");
		}
		
		int start = 0;
		int stop = size;
		
		if (stop >= list.size()) {
			stop = list.size();
		}

		List<List<T>> result = new ArrayList<List<T>>();
				
		while (true) {
			
			List<T> smallResult = list.subList(start, stop);
			
			result.add(smallResult);
			
			start = stop;
			stop += size;
			
			if (start >= list.size()) {
				break;
			}
			
			if (stop >= list.size()) {
				stop = list.size();
			}
		}
		
		return result;
	}
	
	private static void writePolishJapaneseEntries(CsvWriter csvWriter,
			List<PolishJapaneseEntry> polishJapaneseEntries, boolean addKnownDupplicatedId,
			boolean addId, boolean addExampleSentenceGroupIds,
			ICustomAdditionalCsvWriter customAdditionalCsvWriter) throws IOException {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			if (addId == true) {
				csvWriter.write(String.valueOf(polishJapaneseEntry.getId()));
			}
			
			csvWriter.write(convertListToString(polishJapaneseEntry.getDictionaryEntryTypeList()));			
			csvWriter.write(convertAttributeListToString(polishJapaneseEntry.getAttributeList()));
			csvWriter.write(polishJapaneseEntry.getWordType().toString());
			csvWriter.write(convertListToString(GroupEnum.convertToValues(polishJapaneseEntry.getGroups())));
			csvWriter.write(polishJapaneseEntry.getPrefixKana());
			csvWriter.write(polishJapaneseEntry.getKanji());
			csvWriter.write(polishJapaneseEntry.getKana());
			csvWriter.write(polishJapaneseEntry.getPrefixRomaji());
			csvWriter.write(polishJapaneseEntry.getRomaji());
			csvWriter.write(convertListToString(polishJapaneseEntry.getTranslates()));
			csvWriter.write(polishJapaneseEntry.getInfo());
			//csvWriter.write(polishJapaneseEntry.isUseEntry() == false ? "NO" : "");
			csvWriter.write(convertListToString(polishJapaneseEntry.getParseAdditionalInfoList()));

			if (addKnownDupplicatedId == true) {
				
				List<KnownDuplicate> knownDuplicatedId = polishJapaneseEntry.getKnownDuplicatedList();
				
				Collections.sort(knownDuplicatedId);
				
				List<String> knownDuplicateIdStringList = new ArrayList<String>();
				
				for (KnownDuplicate currentKnownDuplicate : knownDuplicatedId) {					
					knownDuplicateIdStringList.add(currentKnownDuplicate.getKnownDuplicateType() + " " + currentKnownDuplicate.getId());					
				}
								
				csvWriter.write(convertListToString(knownDuplicateIdStringList));
			}
			
			if (addExampleSentenceGroupIds == true) {
				csvWriter.write(convertListToString(polishJapaneseEntry.getExampleSentenceGroupIdsList()));
			}
			
			if (customAdditionalCsvWriter != null) {
				customAdditionalCsvWriter.write(csvWriter, polishJapaneseEntry);
			}

			csvWriter.endRecord();
		}
	}
	
	public static interface ICustomAdditionalCsvWriter {		
		public void write(CsvWriter csvWriter, PolishJapaneseEntry polishJapaneseEntry) throws IOException;		
	}
	
	public static void generateWordPowerCsv(OutputStream out, JMENewDictionary jmeNewDictionary, List<PolishJapaneseEntry> polishJapaneseEntries) throws IOException {
		
		TreeMap<Integer, List<PolishJapaneseEntry>> groupByPower = new TreeMap<>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			List<GroupEnum> groups = polishJapaneseEntry.getGroups();
						
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			int power = Integer.MAX_VALUE;
			
			for (GroupEnum groupEnum : groups) {

				if (groupEnum.getPower() < power) {
					power = groupEnum.getPower();
				}				
			}
			
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				List<String> groupEntryPriorityList = groupEntryList.get(0).getPriority();
				
				for (String priority : groupEntryPriorityList) {
					
					int priorityPower = JMENewDictionary.mapPriorityToPower(priority, 100);
					
					if (priorityPower < power) {
						power = priorityPower;
					}
				}
			}
			
			List<PolishJapaneseEntry> polishJapaneseEntryListForPower = groupByPower.get(power);
			
			if (polishJapaneseEntryListForPower == null) {
				
				polishJapaneseEntryListForPower = new ArrayList<>();
				
				groupByPower.put(power, polishJapaneseEntryListForPower);
			}
			
			polishJapaneseEntryListForPower.add(polishJapaneseEntry);			
		}
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');
		
		for (Integer power : groupByPower.keySet()) {
		
			csvWriter.write(String.valueOf(power));
			
			List<PolishJapaneseEntry> polishJapaneseEntryListForPower = groupByPower.get(power);
			
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryListForPower) {
				csvWriter.write(String.valueOf(polishJapaneseEntry.getId()));
			}
			
			csvWriter.endRecord();			
		}
		
		csvWriter.close();
	}


	public static List<PolishJapaneseEntry> parsePolishJapaneseEntriesFromCsv(String[] fileNames) throws IOException,
			JapaneseDictionaryException {

		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (String fileName : fileNames) {

			CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');

			while (csvReader.readRecord()) {

				int id = Integer.parseInt(csvReader.get(0));

				String dictionaryEntryTypeListString = csvReader.get(1);
				String attributesString = csvReader.get(2);
				String wordTypeString = csvReader.get(3);
				String groupString = csvReader.get(4);
				String prefixKanaString = csvReader.get(5);
				String kanjiString = csvReader.get(6);

				if (kanjiString.equals("") == true) {
					throw new JapaneseDictionaryException("Empty kanji!");
				}

				String kanaString = csvReader.get(7);
				String prefixRomajiString = csvReader.get(8);
				String romajiString = csvReader.get(9);
				String polishTranslateListString = csvReader.get(10);
				String infoString = csvReader.get(11);
				String parseAdditionalInfoListString = csvReader.get(12);
				String knownDuplicatedListString = csvReader.get(13);

				PolishJapaneseEntry entry = new PolishJapaneseEntry();

				entry.setId(id);
				entry.setDictionaryEntryTypeList(parseDictionaryEntryTypeStringList(dictionaryEntryTypeListString));
				entry.setAttributeList(parseAttributesStringList(attributesString));
				entry.setWordType(WordType.valueOf(wordTypeString));
				entry.setGroups(GroupEnum.convertToListGroupEnum(parseStringIntoList(groupString)));
				entry.setPrefixKana(prefixKanaString);
				entry.setKanji(kanjiString);
				entry.setKana(kanaString);
				entry.setPrefixRomaji(prefixRomajiString);
				entry.setRomaji(romajiString);
				entry.setTranslates(parseStringIntoList(polishTranslateListString));
				entry.setParseAdditionalInfoList(parseParseAdditionalInfoListString(parseAdditionalInfoListString));

				List<KnownDuplicate> knownDuplicatedList = new ArrayList<PolishJapaneseEntry.KnownDuplicate>();

				List<String> knownDuplicatedListStringAsListString = parseStringIntoList(knownDuplicatedListString);

				for (String currentKnownDuplicateString : knownDuplicatedListStringAsListString) {

					String[] currentKnownDuplicateStringSplited = currentKnownDuplicateString.split(" ");

					knownDuplicatedList.add(new KnownDuplicate(KnownDuplicateType.valueOf(currentKnownDuplicateStringSplited[0]), 
							Integer.parseInt(currentKnownDuplicateStringSplited[1])));				
				}

				entry.setKnownDuplicatedList(knownDuplicatedList);

				entry.setInfo(infoString);

				result.add(entry);
			}

			csvReader.close();
		}

		return result;
	}

	private static List<DictionaryEntryType> parseDictionaryEntryTypeStringList(String dictionaryEntryTypeStringList) {

		List<String> dictionaryEntryTypeList = parseStringIntoList(dictionaryEntryTypeStringList);

		List<DictionaryEntryType> result = new ArrayList<DictionaryEntryType>();

		for (String currentDictionaryEntryTypeString : dictionaryEntryTypeList) {
			result.add(DictionaryEntryType.valueOf(currentDictionaryEntryTypeString));
		}

		return result;
	}

	private static AttributeList parseAttributesStringList(String attributesString) {

		List<String> attributeStringList = parseStringIntoList(attributesString);

		AttributeList result = new AttributeList();

		for (String currentAttributeString : attributeStringList) {

			String[] currentAttributeStringSplited = currentAttributeString.split(" ");

			AttributeType attributeType = AttributeType.valueOf(currentAttributeStringSplited[0]);

			List<String> attributeValueList = null;

			if (currentAttributeStringSplited.length > 1) {

				attributeValueList = new ArrayList<String>();

				for (int currentAttributeStringSplitedIdx = 1; currentAttributeStringSplitedIdx < currentAttributeStringSplited.length; currentAttributeStringSplitedIdx++) {

					attributeValueList.add(currentAttributeStringSplited[currentAttributeStringSplitedIdx]);
				}
			}

			result.addAttributeValue(attributeType, attributeValueList);
		}

		return result;
	}

	private static List<ParseAdditionalInfo> parseParseAdditionalInfoListString(String parseAdditionalInfoListString) {

		List<String> parseAdditionalStringList = parseStringIntoList(parseAdditionalInfoListString);

		List<ParseAdditionalInfo> result = new ArrayList<ParseAdditionalInfo>();

		for (String currentParseAdditionalString : parseAdditionalStringList) {
			result.add(ParseAdditionalInfo.valueOf(currentParseAdditionalString));
		}

		return result;
	}

	private static String convertListToString(List<?> list) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			list = new ArrayList<String>();
		}
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx));

			if (idx != list.size() - 1) {
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	private static String convertAttributeListToString(AttributeList attributeList) {

		StringBuffer sb = new StringBuffer();

		List<Attribute> attributeListList = attributeList.getAttributeList();

		for (int idx = 0; idx < attributeListList.size(); ++idx) {

			Attribute currentAttribute = attributeListList.get(idx);

			sb.append(currentAttribute.getAttributeType().toString());

			List<String> attributeValue = currentAttribute.getAttributeValue();

			if (attributeValue != null && attributeValue.size() > 0) {

				for (String currentSingleAttributeValue : attributeValue) {
					sb.append(" ").append(currentSingleAttributeValue);
				}
			}

			if (idx != attributeListList.size() - 1) {
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

	/*
	private static List<Integer> parseStringIntoIntegerList(String polishTranslateString) {

		List<Integer> result = new ArrayList<Integer>();

		String[] splitedPolishTranslateString = polishTranslateString.split("\n");

		for (String currentPolishTranslateString : splitedPolishTranslateString) {

			if (currentPolishTranslateString.equals("") == true) {
				continue;
			}

			result.add(Integer.parseInt(currentPolishTranslateString));
		}

		return result;
	}
	*/

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

	public static void generateKanaEntriesCsvWithStrokePaths(FileOutputStream outputStream, List<KanaEntry> kanaEntries)
			throws IOException {

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

	public static List<KanjiEntry> parseKanjiEntriesFromCsv(String fileName, Map<String, KanjiDic2Entry> readKanjiDic2, boolean generateJlptGroup)
			throws IOException, JapaneseDictionaryException {

		List<KanjiEntry> result = new ArrayList<KanjiEntry>();

		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');

		while (csvReader.readRecord()) {

			int id = Integer.parseInt(csvReader.get(0));

			String kanjiString = csvReader.get(1);

			if (kanjiString.equals("") == true) {
				throw new JapaneseDictionaryException("Empty kanji!");
			}

			String polishTranslateListString = csvReader.get(2);
			String infoString = csvReader.get(3);

			String groupsString = csvReader.get(4);
			
			String usedString = csvReader.get(5);

			KanjiEntry entry = new KanjiEntry();

			entry.setId(id);
			entry.setKanji(kanjiString);
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setInfo(infoString);

			List<String> groupsList = parseStringIntoList(groupsString);

			entry.setUsed(Boolean.parseBoolean(usedString));

			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(kanjiString);

			entry.setKanjiDic2Entry(kanjiDic2Entry);

			String jlpt = KanjiUtils.getJlpt(kanjiString);

			if (generateJlptGroup == true && jlpt != null) {
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

			entry.setGroups(GroupEnum.convertToListGroupEnum(groupsList));

			result.add(entry);
		}

		csvReader.close();

		return result;
	}

	public static void generateKanjiCsv(OutputStream out, List<KanjiEntry> kanjiEntries, boolean complex) throws IOException {

		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(out), ',');

		if (complex == true) {
			writeComplexKanjiEntries(csvWriter, kanjiEntries);
			
		} else {
			writeSimpleKanjiEntries(csvWriter, kanjiEntries);
		}

		csvWriter.close();
	}

	private static void writeComplexKanjiEntries(CsvWriter csvWriter, List<KanjiEntry> kanjiEntries) throws IOException {

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
			csvWriter.write(String.valueOf(kanjiEntry.isUsed()));
			csvWriter.write(convertListToString(GroupEnum.convertToValues(kanjiEntry.getGroups())));

			csvWriter.endRecord();
		}
	}

	private static void writeSimpleKanjiEntries(CsvWriter csvWriter, List<KanjiEntry> kanjiEntries) throws IOException {

		for (KanjiEntry kanjiEntry : kanjiEntries) {

			csvWriter.write(String.valueOf(kanjiEntry.getId()));
			csvWriter.write(kanjiEntry.getKanji());

			csvWriter.write(convertListToString(kanjiEntry.getPolishTranslates()));
			csvWriter.write(kanjiEntry.getInfo());
			csvWriter.write(convertListToString(GroupEnum.convertToValues(kanjiEntry.getGroups())));
			csvWriter.write(String.valueOf(kanjiEntry.isUsed()));			
			
			csvWriter.endRecord();
		}
	}
	
	public static List<KanjiEntry> parseKanjiEntriesFromCsv(String fileName) throws IOException,
			JapaneseDictionaryException {

		List<KanjiEntry> result = new ArrayList<KanjiEntry>();

		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');

		while (csvReader.readRecord()) {

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

			String usedString = csvReader.get(8);

			KanjiEntry entry = new KanjiEntry();

			entry.setId(id);
			entry.setKanji(kanjiString);
			entry.setPolishTranslates(parseStringIntoList(polishTranslateListString));
			entry.setInfo(infoString);

			entry.setUsed(Boolean.parseBoolean(usedString));

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

	public static List<RadicalInfo> parseRadicalEntriesFromCsv(String fileName) throws IOException,
			JapaneseDictionaryException {

		List<RadicalInfo> result = new ArrayList<RadicalInfo>();

		CsvReader csvReader = new CsvReader(new FileReader(fileName), ',');

		while (csvReader.readRecord()) {

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

	public static void writeTatoebaSentenceList(String sentencesDestinationFileName, List<TatoebaSentence> tatoebaSentenceList) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(sentencesDestinationFileName), ',');
		
		for (TatoebaSentence tatoebaSentence : tatoebaSentenceList) {
			
			csvWriter.write(tatoebaSentence.getId());			
			csvWriter.write(tatoebaSentence.getLang());
			csvWriter.write(tatoebaSentence.getSentence());

			csvWriter.endRecord();			
		}
				
		csvWriter.close();
	}

	public static void writeTatoebaSentenceGroupsList(String sentencesGroupsDestinationFileName, List<GroupWithTatoebaSentenceList> uniqueSentencesWithGroupList) throws IOException {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(sentencesGroupsDestinationFileName), ',');
		
		for (GroupWithTatoebaSentenceList currentGroupWithTatoebaSentenceList : uniqueSentencesWithGroupList) {
			
			List<String> groupSentencesIdList = new ArrayList<String>();
			
			for (TatoebaSentence tatoebaSentence : currentGroupWithTatoebaSentenceList.getTatoebaSentenceList()) {
				groupSentencesIdList.add(tatoebaSentence.getId());
			}			
			
			csvWriter.write(currentGroupWithTatoebaSentenceList.getGroupId());			
			csvWriter.write(convertListToString(groupSentencesIdList));

			csvWriter.endRecord();			
		}
				
		csvWriter.close();		
	}
	
	public static Map<Integer, CommonWord> readCommonWordFile(String fileName) throws Exception {
		
		TreeMap<Integer, CommonWord> result = new TreeMap<Integer, CommonWord>();
		
		CsvReader csvReader = new CsvReader(new FileReader(new File(fileName)));
				
		while (csvReader.readRecord()) {
			
			Integer id = Integer.parseInt(csvReader.get(0));
			
			boolean done = csvReader.get(1).equals("1");
			
			String kanji = csvReader.get(2);
			String kana = csvReader.get(3);
			
			String type = csvReader.get(4);
			
			String translate = csvReader.get(5);
			
			CommonWord commonWord = new CommonWord(id, done, kanji, kana, type, translate);
			
			result.put(id, commonWord);
		}
		
		csvReader.close();
		
		return result;
	}
	
	public static void writeCommonWordFile( Map<Integer, CommonWord> commonWordMap, String fileName) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		Set<Entry<Integer, CommonWord>> commonWordMapEntrySet = commonWordMap.entrySet();
		
		for (Entry<Integer, CommonWord> currentCommonWordEntry : commonWordMapEntrySet) {
			
			csvWriter.write(String.valueOf(currentCommonWordEntry.getValue().getId()));
			csvWriter.write(currentCommonWordEntry.getValue().isDone() == true ? "1" : "");
			csvWriter.write(currentCommonWordEntry.getValue().getKanji() != null ? currentCommonWordEntry.getValue().getKanji() : "-");
			csvWriter.write(currentCommonWordEntry.getValue().getKana());
			csvWriter.write(currentCommonWordEntry.getValue().getType());
			csvWriter.write(currentCommonWordEntry.getValue().getTranslate());
			
			csvWriter.endRecord();
		}	
		
		csvWriter.close();
	}	
}
