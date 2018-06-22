package pl.idedyk.japanese.dictionary.misc;

public class GenerateMultiTranslateNameList {
	
	public static void main(String[] args) throws Exception {
		
		/*
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		List<PolishJapaneseEntry> namesList = Helper.generateNames(jmedictName);
				
		List<PolishJapaneseEntry> multiName = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			if (polishJapaneseEntry.getTranslates().size() > 1) {
				multiName.add(polishJapaneseEntry);
			}
		}
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/multiTranslateName.csv" }, multiName, true, false, true, false, null);
		*/
	}
}
