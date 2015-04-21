package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class GetCommonPartList {

	public static void main(String[] args) throws Exception {
		
		String fileName = args[0];
		
		// czytanie pliku ze slownikiem
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = 
				pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);

		// czytanie listy common'owych plikow
		Map<Integer, CommonWord> commonWordMap = readCommonWordFile("input/common_word.csv");
		
		// czytanie identyfikatorow common'owych slow
		List<String> commonWordIds = readCommonWordIds(fileName);
		
		// wczytywanie slownika edict
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		JMEDictEntityMapper jmEDictEntityMapper = new JMEDictEntityMapper();
		
		List<PolishJapaneseEntry> newWordList = new ArrayList<PolishJapaneseEntry>();
		
		for (String currentCommonWordId : commonWordIds) {
			
			CommonWord commonWord = commonWordMap.get(Integer.parseInt(currentCommonWordId));
			
			String commonKanji = null;
			String commonKana = null;
			
			List<GroupEntry> groupEntryList = null;
			
			if (commonWord != null) {
				
				commonKanji = commonWord.getKanji();
				commonKana = commonWord.getKana();
				
				groupEntryList = jmeNewDictionary.getGroupEntryList(commonKanji, commonKana);
				
				commonWord.done = true;
				
			} else {
				
				System.out.println("Nie znaleziono slowo o identyfikatorze: " + currentCommonWordId);
			}
						
			if (groupEntryList != null && groupEntryList.size() > 0) {
				
				for (GroupEntry groupEntry : groupEntryList) {

					Set<String> wordTypeList = groupEntry.getWordTypeList();
					
					String kanji = groupEntry.getKanji();
					List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

					String kana = groupEntry.getKana();
					List<String> kanaInfoList = groupEntry.getKanaInfoList();

					String romaji = groupEntry.getRomaji();

					List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();	
					
					List<String> translateList2 = new ArrayList<String>();
					
					for (GroupEntryTranslate groupEntryTranslate : translateList) {
						
						StringBuffer translate = new StringBuffer(groupEntryTranslate.getTranslate());
						
						List<String> miscInfoList = groupEntryTranslate.getMiscInfoList();
						List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
						
						boolean wasMiscOrAdditionalInfo = false;
						
						for (int idx = 0; miscInfoList != null && idx < miscInfoList.size(); ++idx) {
							
							if (wasMiscOrAdditionalInfo == false) {
								translate.append(" (");
								
								wasMiscOrAdditionalInfo = true;
								
							} else {
								translate.append(", ");
							}
							
							translate.append(jmEDictEntityMapper.getDesc(miscInfoList.get(idx)));
						}
						
						for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {
							
							if (wasMiscOrAdditionalInfo == false) {
								translate.append(" (");
								
								wasMiscOrAdditionalInfo = true;
								
							} else {
								translate.append(", ");
							}
						}
						
						if (wasMiscOrAdditionalInfo == true) {
							translate.append(")");
						}
						
						translateList2.add(translate.toString());
					}					
					
					List<String> additionalInfoList = new ArrayList<String>(); //groupEntry.getAdditionalInfoList();
										
					PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
					
					polishJapaneseEntry.setId(Integer.valueOf(currentCommonWordId));
					
					List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
										
					for (String currentEntity : wordTypeList) {
						
						DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
						
						if (dictionaryEntryType != null && dictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
							dictionaryEntryTypeList.add(dictionaryEntryType);
						}
					}
									
					polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);
					
					polishJapaneseEntry.setAttributeList(new AttributeList());
					
					polishJapaneseEntry.setWordType(getWordType(kana));
					
					polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
					
					if (kanji == null || kanji.equals("") == true) {
						kanji = "-";
					}
					
					polishJapaneseEntry.setKanji(kanji);
					polishJapaneseEntry.setKana(kana);
					polishJapaneseEntry.setRomaji(romaji);
									
					polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

					List<String> newTranslateList = new ArrayList<String>();
					
					List<PolishJapaneseEntry> findPolishJapaneseEntry = pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntry(
							cachePolishJapaneseEntryList, kanji, kana);
					
					newTranslateList.add("_");
					newTranslateList.add("-----------");
					
					if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
						newTranslateList.add("JUZ JEST");
						newTranslateList.add("-----------");
					}
					
					newTranslateList.add("-----------");
					newTranslateList.addAll(translateList2);
					
					polishJapaneseEntry.setTranslates(newTranslateList);
					
					StringBuffer additionalInfoSb = new StringBuffer();

					if (kanjiInfoList != null && kanjiInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(kanjiInfoList));
						additionalInfoSb.append("\n");
					}

					if (kanaInfoList != null && kanaInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(kanaInfoList));
						additionalInfoSb.append("\n");
					}
					
					if (additionalInfoList != null && additionalInfoList.size() > 0) {
						additionalInfoSb.append(Utils.convertListToString(additionalInfoList));
						additionalInfoSb.append("\n");
					}
					
					polishJapaneseEntry.setInfo(additionalInfoSb.toString());
									
					newWordList.add(polishJapaneseEntry);
				}				
				
			} else {
				
				PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
				
				polishJapaneseEntry.setId(Integer.valueOf(currentCommonWordId));
				
				List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
				
				dictionaryEntryTypeList.add(DictionaryEntryType.UNKNOWN);
												
				polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);				
				polishJapaneseEntry.setAttributeList(new AttributeList());
				
				polishJapaneseEntry.setWordType(WordType.KATAKANA);
				
				polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
								
				polishJapaneseEntry.setKanji("-");
				polishJapaneseEntry.setKana("-");
				polishJapaneseEntry.setRomaji("-");
								
				polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

				List<String> newTranslateList = new ArrayList<String>();
				
				newTranslateList.add("");
								
				polishJapaneseEntry.setTranslates(newTranslateList);				
				polishJapaneseEntry.setInfo("");
								
				newWordList.add(polishJapaneseEntry);				
			}			
		}
		
		CsvReaderWriter.generateCsv("input/word-common-new.csv", newWordList, true, true, false);
		
		writeCommonWordFile(commonWordMap, "input/common_word-nowy.csv");
	}
	
	private static Map<Integer, CommonWord> readCommonWordFile(String fileName) throws Exception {
		
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
	
	private static void writeCommonWordFile(Map<Integer, CommonWord> commonWordMap, String fileName) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(fileName), ',');
		
		Set<Entry<Integer, CommonWord>> commonWordMapEntrySet = commonWordMap.entrySet();
		
		for (Entry<Integer, CommonWord> currentCommonWordEntry : commonWordMapEntrySet) {
			
			csvWriter.write(String.valueOf(currentCommonWordEntry.getValue().id));
			csvWriter.write(currentCommonWordEntry.getValue().done == true ? "1" : "");
			csvWriter.write(currentCommonWordEntry.getValue().kanji);
			csvWriter.write(currentCommonWordEntry.getValue().kana);
			csvWriter.write(currentCommonWordEntry.getValue().type);
			csvWriter.write(currentCommonWordEntry.getValue().translate);
			
			csvWriter.endRecord();
		}	
		
		csvWriter.close();
	}
	
	private static List<String> readCommonWordIds(String fileName) {

		List<String> result = new ArrayList<String>();
		
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));

			while (true) {

				String line = br.readLine();

				if (line == null) {
					break;
				}
				
				if (line.startsWith("---") == true) {
					continue;
				}
				
				int tabIndex = line.indexOf("\t");
				
				if (tabIndex != -1) {
					line = line.substring(0, tabIndex);
				}
				
				line = line.trim();

				result.add(line);
			}

			br.close();

			return result;

		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
	
	private static WordType getWordType(String kana) {
		
		WordType wordType = null;
				
		for (int idx = 0; idx < kana.length(); ++idx) {
			
			char c = kana.charAt(idx);
			
			boolean currentCIsHiragana = Utils.isHiragana(c);
			boolean currentCIsKatakana = Utils.isKatakana(c);
			
			if (currentCIsHiragana == true) {
				
				if (wordType == null) {
					wordType = WordType.HIRAGANA;
					
				} else if (wordType == WordType.KATAKANA) {
					wordType = WordType.KATAKANA_HIRAGANA;					
				}				
			}

			if (currentCIsKatakana == true) {
				
				if (wordType == null) {
					wordType = WordType.KATAKANA;
					
				} else if (wordType == WordType.HIRAGANA) {
					wordType = WordType.HIRAGANA_KATAKANA;					
				}				
			}			
		}	
		
		return wordType;
	}
	
	private static class CommonWord {
		
		private Integer id;
		
		private boolean done;
		
		private String kanji;
		private String kana;
		
		private String type;
		
		private String translate;
		
		public CommonWord(Integer id, boolean done, String kanji, String kana, String type, String translate) {
			this.id = id;
			this.done = done;
			this.kanji = kanji;
			this.kana = kana;
			this.type = type;
			this.translate = translate;
		}

		@SuppressWarnings("unused")
		public Integer getId() {
			return id;
		}

		@SuppressWarnings("unused")
		public void setId(Integer id) {
			this.id = id;
		}

		@SuppressWarnings("unused")
		public boolean isDone() {
			return done;
		}

		@SuppressWarnings("unused")
		public void setDone(boolean done) {
			this.done = done;
		}

		public String getKanji() {
			return kanji;
		}

		@SuppressWarnings("unused")
		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public String getKana() {
			return kana;
		}

		@SuppressWarnings("unused")
		public void setKana(String kana) {
			this.kana = kana;
		}

		@SuppressWarnings("unused")
		public String getType() {
			return type;
		}

		@SuppressWarnings("unused")
		public void setType(String type) {
			this.type = type;
		}

		@SuppressWarnings("unused")
		public String getTranslate() {
			return translate;
		}

		@SuppressWarnings("unused")
		public void setTranslate(String translate) {
			this.translate = translate;
		}

		@Override
		public String toString() {
			return "CommonWord [id=" + id + ", done=" + done + ", kanji=" + kanji + ", kana=" + kana + ", type=" + type
					+ ", translate=" + translate + "]";
		}
	}
}
