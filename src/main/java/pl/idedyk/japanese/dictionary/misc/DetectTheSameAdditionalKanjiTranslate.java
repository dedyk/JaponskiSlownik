package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class DetectTheSameAdditionalKanjiTranslate {

	public static void main(String[] args) throws Exception {
		
		String kradfile = "../JapaneseDictionary_additional/kradfile";
		String kanjidic2 = "../JapaneseDictionary_additional/kanjidic2.xml";
		
		String additionalKanjiFile = "input/additional_kanji.csv";
		String additionalKanjiOuputFile = "input/additional_kanji_output.csv";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(kradfile);		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(kanjidic2, kradFileMap);
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = readAdditionalKanjiEntry(additionalKanjiFile);
		
		Map<String, List<KanjiDic2Entry>> theSameEngMeaning = detectTheSameEngMeaning(kradFileMap, readKanjiDic2);

		Iterator<String> theSameEngMeaningIterator = theSameEngMeaning.keySet().iterator();
		
		while (theSameEngMeaningIterator.hasNext() == true) {
			
			String key = theSameEngMeaningIterator.next();
			
			if (key.trim().equals("[]") == true) {
				continue;
			}
			
			List<KanjiDic2Entry> theSameEngMeaningKanjiDic2EntryList = theSameEngMeaning.get(key);

			if (theSameEngMeaningKanjiDic2EntryList.size() <= 1) {
				continue;
			}
						
			List<AdditionalKanjiEntry> foundAdditionalKanjiEntryList = new ArrayList<AdditionalKanjiEntry>();
			
			for (KanjiDic2Entry currentKanjiDic2Entry : theSameEngMeaningKanjiDic2EntryList) {
				
				AdditionalKanjiEntry additionalKanjiEntry = findAdditionalKanjiEntry(additionalKanjiEntryList, currentKanjiDic2Entry.getKanji());
				
				if (additionalKanjiEntry != null) {
					foundAdditionalKanjiEntryList.add(additionalKanjiEntry);
				}				
			}
			
			if (foundAdditionalKanjiEntryList.size() <= 1) {
				continue;
			}
			
			String theSamePolishTranslate = null;
			String theSamePolishInfo = null;
			
			for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
				
				String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();
				String currentAdditionalKanjiEntryInfo = additionalKanjiEntry.getInfo();
				
				if (currentAdditionalKanjiEntryTranslate.equals("") == false && theSamePolishTranslate == null) {
					
					theSamePolishTranslate = currentAdditionalKanjiEntryTranslate;
					theSamePolishInfo = currentAdditionalKanjiEntryInfo;
					
				} else if (theSamePolishTranslate != null && currentAdditionalKanjiEntryTranslate.equals("") == false && 
						currentAdditionalKanjiEntryTranslate.startsWith("---") == false &&
						currentAdditionalKanjiEntryTranslate.equals(theSamePolishTranslate) == false) {
					
					for (AdditionalKanjiEntry additionalKanjiEntry2 : foundAdditionalKanjiEntryList) {
						System.err.println(additionalKanjiEntry2);
					}
					
					
					System.err.println("Error");
					
					throw new Exception();
				}				
			}
			
			if (theSamePolishTranslate != null) {			

				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					
					String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();
	
					if (currentAdditionalKanjiEntryTranslate.equals("") == true) {
						additionalKanjiEntry.setTranslate(theSamePolishTranslate);
						additionalKanjiEntry.setInfo(theSamePolishInfo);
						additionalKanjiEntry.setDone("0");
					}					
				}
				
			} else {
				
				StringBuffer allKanji = new StringBuffer();
				
				allKanji.append("---\n");
				
				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					allKanji.append(additionalKanjiEntry.getKanji() + "\n");					
				}
				
				for (AdditionalKanjiEntry additionalKanjiEntry : foundAdditionalKanjiEntryList) {
					
					String currentAdditionalKanjiEntryTranslate = additionalKanjiEntry.getTranslate();

					if (currentAdditionalKanjiEntryTranslate.equals("") == false) {
						throw new Exception();
					}
					
					additionalKanjiEntry.setTranslate(allKanji.toString().replaceAll(additionalKanjiEntry.getKanji() + "\n", ""));				
				}				
			}
		}
		
		writeAdditionalKanjiList(additionalKanjiEntryList, additionalKanjiOuputFile);
	}
	
	private static Map<String, List<KanjiDic2Entry>> detectTheSameEngMeaning(Map<String, List<String>> kradFileMap, Map<String, KanjiDic2Entry> readKanjiDic2) {
		
		Collection<KanjiDic2Entry> readKanjiDic2Values = readKanjiDic2.values();
		
		Map<String, List<KanjiDic2Entry>> theSameEngMeaning = new TreeMap<String, List<KanjiDic2Entry>>();
		
		for (KanjiDic2Entry kanjiDic2Entry : readKanjiDic2Values) {
			
			List<String> engMeaning = kanjiDic2Entry.getEngMeaning();
			
			Collections.sort(engMeaning);
			
			String key = engMeaning.toString();
			
			List<KanjiDic2Entry> list = theSameEngMeaning.get(key);
			
			if (list == null) {
				list = new ArrayList<KanjiDic2Entry>();
			}
			
			list.add(kanjiDic2Entry);
			
			theSameEngMeaning.put(key, list);			
		}	
		
		return theSameEngMeaning;
	}
	
	private static List<AdditionalKanjiEntry> readAdditionalKanjiEntry(String additionalKanjiFile) throws Exception {
		
		List<AdditionalKanjiEntry> result = new ArrayList<AdditionalKanjiEntry>();
		
		CsvReader csvReader = new CsvReader(new FileReader(additionalKanjiFile), ',');

		boolean useKanji = true;
		
		while (csvReader.readRecord()) {
			
			AdditionalKanjiEntry additionalKanjiEntry = new AdditionalKanjiEntry();
			
			String id = csvReader.get(0);
			
			additionalKanjiEntry.setId(id);
			additionalKanjiEntry.setDone(csvReader.get(1));
			additionalKanjiEntry.setKanji(csvReader.get(2));
			additionalKanjiEntry.setStrokeCount(csvReader.get(3));
			additionalKanjiEntry.setTranslate(csvReader.get(4));
			additionalKanjiEntry.setInfo(csvReader.get(5));
			
			if (useKanji == true && id.equals("X") == true) {
				useKanji = false;
			}
			
			additionalKanjiEntry.setUseKanji(useKanji);
			
			result.add(additionalKanjiEntry);			
		}
		
		csvReader.close();
		
		return result;		
	}
	
	private static AdditionalKanjiEntry findAdditionalKanjiEntry(List<AdditionalKanjiEntry> additionalKanjiEntryList, String kanji) {
		
		for (AdditionalKanjiEntry currentAdditionalKanjiEntry : additionalKanjiEntryList) {
			
			if (currentAdditionalKanjiEntry.getKanji().equals(kanji) == true) {
				return currentAdditionalKanjiEntry;
			}
		}
		
		return null;		
	}
	
	private static void writeAdditionalKanjiList(List<AdditionalKanjiEntry> additionalKanjiEntryList, String additionalKanjiOutputFile) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(additionalKanjiOutputFile), ',');
		
		for (AdditionalKanjiEntry additionalKanjiEntry : additionalKanjiEntryList) {
			
			csvWriter.write(additionalKanjiEntry.getId());
			csvWriter.write(additionalKanjiEntry.getDone());
			csvWriter.write(additionalKanjiEntry.getKanji());
			csvWriter.write(additionalKanjiEntry.getStrokeCount());
			csvWriter.write(additionalKanjiEntry.getTranslate());
			csvWriter.write(additionalKanjiEntry.getInfo());
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}
	
	private static class AdditionalKanjiEntry {
		
		private String id;
		
		private String done;
		
		private String kanji;
		
		private String strokeCount;
		
		private String translate;
		
		private String info;
		
		private boolean useKanji;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDone() {
			return done;
		}

		public void setDone(String done) {
			this.done = done;
		}

		public String getStrokeCount() {
			return strokeCount;
		}

		public void setStrokeCount(String strokeCount) {
			this.strokeCount = strokeCount;
		}

		public String getTranslate() {
			return translate;
		}

		public void setTranslate(String translate) {
			this.translate = translate;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		public String getKanji() {
			return kanji;
		}

		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public void setUseKanji(boolean useKanji) {
			this.useKanji = useKanji;
		}

		@Override
		public String toString() {
			return "AdditionalKanjiEntry [id=" + id + ", done=" + done + ", kanji=" + kanji + ", strokeCount="
					+ strokeCount + ", translate=" + translate + ", info=" + info + ", useKanji=" + useKanji + "]";
		}
	}
}
