package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CutAlreadySetAdditionalKanji {

	public static void main(String[] args) throws Exception {
		
		String additionalKanjiFile = "input/additional_kanji.csv";
		String cutAdditionalKanjiOuputFile = "input/cut_additional_kanji_output.csv";
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = readAdditionalKanjiEntry(additionalKanjiFile);
		
		List<AdditionalKanjiEntry> filteredAdditionalKanjiEntryList = new ArrayList<AdditionalKanjiEntry>();
		
		for (AdditionalKanjiEntry currentAdditionalKanjiEntry : additionalKanjiEntryList) {
			
			String done = currentAdditionalKanjiEntry.getDone();
			String translate = currentAdditionalKanjiEntry.getTranslate();
			
			if (done != null && (done.equals("1") == true || done.equals("2") == true)) {
				
				if (translate.startsWith("---") == true || translate.trim().equals("") == true) {
					throw new Exception(currentAdditionalKanjiEntry.toString());
				}				
			}
			
			if (translate.startsWith("---") == false && translate.trim().equals("") == false && done != null && done.equals("") == true) {
				throw new Exception(currentAdditionalKanjiEntry.toString());				
			}
			
			if (currentAdditionalKanjiEntry.isUseKanji() == false && done != null && (done.equals("1") == true)) {
				filteredAdditionalKanjiEntryList.add(currentAdditionalKanjiEntry);
			}
		}		
		
		writeCutAdditionalKanjiList(filteredAdditionalKanjiEntryList, cutAdditionalKanjiOuputFile);
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
	
	private static void writeCutAdditionalKanjiList(List<AdditionalKanjiEntry> additionalKanjiEntryList, String cutAdditionalKanjiOutputFile) throws Exception {
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(cutAdditionalKanjiOutputFile), ',');
		
		for (AdditionalKanjiEntry additionalKanjiEntry : additionalKanjiEntryList) {
			
			csvWriter.write(additionalKanjiEntry.getKanji());
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

		public boolean isUseKanji() {
			return useKanji;
		}

		@Override
		public String toString() {
			return "AdditionalKanjiEntry [id=" + id + ", done=" + done + ", kanji=" + kanji + ", strokeCount="
					+ strokeCount + ", translate=" + translate + ", info=" + info + ", useKanji=" + useKanji + "]";
		}
	}
}
