package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;

public class SQLInsertGenerator {
	
	public static void main(String[] args) throws IOException {
		
		createSqlInsert("input/word.csv", "out/word.sql");
		
		
	}

	private static void createSqlInsert(String inputFileName, String outputFileName) throws IOException {
		
		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(inputFileName);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			
			
			
		}
		*/
	}
	
	/*
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
	*/
}
