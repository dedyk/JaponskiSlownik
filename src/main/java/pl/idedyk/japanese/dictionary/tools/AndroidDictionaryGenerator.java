package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		checkPolishJapaneseEntries("input/word.csv", "output/word.csv");
	}
	
	private static void checkPolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileName, null);
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			if (polishJapaneseEntries.get(idx).getDictionaryEntryType() == DictionaryEntryType.WORD_KANJI_READING) {
				continue;
			}
			
			if (polishJapaneseEntries.get(idx).isUseEntry() == true) {
				result.add(polishJapaneseEntries.get(idx));
			}
	
		}
		
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new XorOutputStream(new File(destinationFileName), 23));
		
		CsvReaderWriter.generateCsv(gzipOutputStream, result);
	}
	
	private static class XorOutputStream extends OutputStream {

		private FileOutputStream fos;
		
		private int xor;
		
		public XorOutputStream(File file, int xor) throws FileNotFoundException {
			fos = new FileOutputStream(file);
			
			this.xor = xor;
		}

		public void write(int b) throws IOException {
			fos.write(b ^ xor);
		}
	}
}
