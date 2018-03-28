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

import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class SetEmptyAdditionalKanjiTranslate {

	public static void main(String[] args) throws Exception {
		
		String kradfile = "../JapaneseDictionary_additional/kradfile";
		String kanjidic2 = "../JapaneseDictionary_additional/kanjidic2.xml";
		
		String additionalKanjiFile = "input/additional_kanji.csv";
		String additionalKanjiOuputFile = "input/additional_kanji_output.csv";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(kradfile);		
		Map<String, KanjiDic2EntryForDictionary> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(kanjidic2, kradFileMap);
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = readAdditionalKanjiEntry(additionalKanjiFile);
		
		Map<String, List<KanjiDic2EntryForDictionary>> theSameEngMeaning = detectTheSameEngMeaning(kradFileMap, readKanjiDic2);

		Iterator<String> theSameEngMeaningIterator = theSameEngMeaning.keySet().iterator();
		
		while (theSameEngMeaningIterator.hasNext() == true) {
			
			String key = theSameEngMeaningIterator.next();
			
			if (key.trim().equals("[]") == true) {
				
				List<KanjiDic2EntryForDictionary> kanjiDic2EntryList = theSameEngMeaning.get(key);
				
				for (KanjiDic2EntryForDictionary currentKanjiDic2Entry : kanjiDic2EntryList) {
					
					AdditionalKanjiEntry findAdditionalKanjiEntryResult = findAdditionalKanjiEntry(additionalKanjiEntryList, currentKanjiDic2Entry.getKanji());
					
					if (findAdditionalKanjiEntryResult != null) {						
						String done = findAdditionalKanjiEntryResult.getDone();
						
						if (done.trim().equals("") == true) {
							findAdditionalKanjiEntryResult.setDone("1");
							findAdditionalKanjiEntryResult.setTranslate("???");
						}
					}
				}
			}
		}
		
		writeAdditionalKanjiList(additionalKanjiEntryList, additionalKanjiOuputFile);
	}
	
	private static Map<String, List<KanjiDic2EntryForDictionary>> detectTheSameEngMeaning(Map<String, List<String>> kradFileMap, Map<String, KanjiDic2EntryForDictionary> readKanjiDic2) {
		
		Collection<KanjiDic2EntryForDictionary> readKanjiDic2Values = readKanjiDic2.values();
		
		Map<String, List<KanjiDic2EntryForDictionary>> theSameEngMeaning = new TreeMap<String, List<KanjiDic2EntryForDictionary>>();
		
		for (KanjiDic2EntryForDictionary kanjiDic2Entry : readKanjiDic2Values) {
			
			List<String> engMeaning = kanjiDic2Entry.getEngMeaning();
			
			Collections.sort(engMeaning);
			
			String key = engMeaning.toString();
			
			List<KanjiDic2EntryForDictionary> list = theSameEngMeaning.get(key);
			
			if (list == null) {
				list = new ArrayList<KanjiDic2EntryForDictionary>();
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
