package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		checkAndSavePolishJapaneseEntries("input/word.csv", "output/word.csv");
		
		generateKanjiEntries("input/kanji.csv", "../JapaneseDictionary_additional/kanjidic2.xml", 
				"../JapaneseDictionary_additional/kradfile",				
				"output/kanji.csv");
	}

	private static void checkAndSavePolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
		
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
	
	private static void generateKanjiEntries(
			String sourceKanjiName,
			String sourceKanjiDic2FileName,
			String sourceKradFileName,			
			String destinationFileName) throws Exception {
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		//OutputStream outputStream = new FileOutputStream(destinationFileName + "-normal");
		OutputStream outputStream = new GZIPOutputStream(new XorOutputStream(new File(destinationFileName), 23));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries);
		
		// test
		//List<KanjiEntry> fullKanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(destinationFileName);
		
		//OutputStream outputStream2 = new FileOutputStream(destinationFileName + "-test");
		
		//CsvReaderWriter.generateKanjiCsv(outputStream2, fullKanjiEntries);
		
	}
}
