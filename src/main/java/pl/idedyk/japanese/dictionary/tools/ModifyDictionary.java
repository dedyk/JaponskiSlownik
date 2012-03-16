package pl.idedyk.japanese.dictionary.tools;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
				
		//convertPolishJapaneseEntries("input/word.csv", "input/kanji_word.csv", "input/word-temp.csv");
	}
	
	/*
	private static void convertPolishJapaneseEntries(String wordFileName, String kanjiFileName, String destinationFileName) throws Exception {
		
		List<PolishJapaneseEntry> wordPolishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(wordFileName);
		List<PolishJapaneseEntry> kanjiPolishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(kanjiFileName);
		
		for (PolishJapaneseEntry currentWordPolishJapaneseEntry : wordPolishJapaneseEntries) {
			
			List<String> kanaList = currentWordPolishJapaneseEntry.getKanaList();
			
			if (kanaList.size() == 1) {
				
				List<PolishJapaneseEntry> foundKanjiPolishJapaneseEntry = findPolishJapaneseEntry(kanjiPolishJapaneseEntries, kanaList.get(0));
				
				if (foundKanjiPolishJapaneseEntry.size() > 0) {
					System.out.println(foundKanjiPolishJapaneseEntry);
				}
				
				
			}
		}
		
		CsvReaderWriter.generateCsv(destinationFileName, wordPolishJapaneseEntries);
	}
	
	private static List<PolishJapaneseEntry> findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapanaeseEntries, String kanaString) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : polishJapanaeseEntries) {
			
			List<String> kanaList = currentPolishJapaneseEntry.getKanaList();
			
			if (kanaList.size() == 1) {
				if (kanaList.get(0).equals(kanaString) == true) {
					result.add(currentPolishJapaneseEntry);
				}
			}
			
			
		}
		
		return result;
	}
	*/
}
