package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.RadiacalInfo;
import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;

public class AndroidDictionaryGenerator {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> dictionary = checkAndSavePolishJapaneseEntries("input/word.csv", "output/word.csv");
		
		generateKanjiRadiacal("../JapaneseDictionary_additional/radkfile", "output/radiacal.csv");
		
		generateKanjiEntries(dictionary, "input/kanji.csv", "../JapaneseDictionary_additional/kanjidic2.xml", 
				"../JapaneseDictionary_additional/kradfile",				
				"output/kanji.csv");
	}

	private static List<PolishJapaneseEntry> checkAndSavePolishJapaneseEntries(String sourceFileName, String destinationFileName) throws Exception {
		
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
		
		return result;
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
			List<PolishJapaneseEntry> dictionary, String sourceKanjiName,
			String sourceKanjiDic2FileName,
			String sourceKradFileName,			
			String destinationFileName) throws Exception {
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		generateAdditionalKanjiEntries(dictionary, kanjiEntries, readKanjiDic2);
		
		//OutputStream outputStream = new FileOutputStream(destinationFileName + "-normal.csv");
		OutputStream outputStream = new GZIPOutputStream(new XorOutputStream(new File(destinationFileName), 23));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries);		
	}

	private static void generateAdditionalKanjiEntries(List<PolishJapaneseEntry> dictionary,
			List<KanjiEntry> kanjiEntries, Map<String, KanjiDic2Entry> readKanjiDic2) {
		
		Set<String> alreadySetKanji = new HashSet<String>();
		
		for (KanjiEntry currentKanjiEntry : kanjiEntries) {
			
			alreadySetKanji.add(currentKanjiEntry.getKanji());	
		}
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : dictionary) {
			
			String kanji = currentPolishJapaneseEntry.getKanji();
			
			for (int kanjiCharIdx = 0; kanjiCharIdx < kanji.length(); ++kanjiCharIdx) {
				
				String currentKanjiChar = String.valueOf(kanji.charAt(kanjiCharIdx));
				
				if (alreadySetKanji.contains(currentKanjiChar)) {
					continue;
				}
				
				KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(currentKanjiChar);
				
				if (kanjiDic2Entry != null) {
					alreadySetKanji.add(kanji);
					
					KanjiEntry newKanjiEntry = new KanjiEntry();
					
					newKanjiEntry.setId(kanjiEntries.get(kanjiEntries.size() - 1).getId() + 1);
					newKanjiEntry.setKanji(currentKanjiChar);
					newKanjiEntry.setKanjiDic2Entry(kanjiDic2Entry);
					
					List<String> polishTranslates = new ArrayList<String>();
					
					polishTranslates.add("nieznane znaczenie; możesz pomóc, jeśli znasz znaczenie");
					
					newKanjiEntry.setPolishTranslates(polishTranslates);
					newKanjiEntry.setInfo("");
					
					kanjiEntries.add(newKanjiEntry);
				}
			}
		}
	}
	
	private static void generateKanjiRadiacal(String radfile, String radiacalDestination) throws Exception {
		
		List<RadiacalInfo> radiacalList = KanjiDic2Reader.readRadkfile(radfile);
		
		//OutputStream outputStream = new FileOutputStream(radiacalDestination + "-normal.csv");
		OutputStream outputStream = new GZIPOutputStream(new XorOutputStream(new File(radiacalDestination), 23));

		CsvReaderWriter.generateKanjiRadiacalCsv(outputStream, radiacalList);	
	}
}
