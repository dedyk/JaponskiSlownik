package pl.idedyk.japanese.dictionary.misc;

public class SplitJMnedict {

	public static void main(String[] args) throws Exception {
		
		/*
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmedictName);
		
		TreeMap<DictionaryEntryType, List<PolishJapaneseEntry>> groupedPolishJapaneseEntryList = new 
				TreeMap<DictionaryEntryType, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : generatedNames) {			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			List<PolishJapaneseEntry> dictionaryEntryTypeList = groupedPolishJapaneseEntryList.get(dictionaryEntryType);
			
			if (dictionaryEntryTypeList == null) {
				dictionaryEntryTypeList = new ArrayList<PolishJapaneseEntry>();
				
				groupedPolishJapaneseEntryList.put(dictionaryEntryType, dictionaryEntryTypeList);
			}
			
			dictionaryEntryTypeList.add(polishJapaneseEntry);			
		}
		
		Iterator<DictionaryEntryType> dictionaryEntryTypeiterator = groupedPolishJapaneseEntryList.keySet().iterator();
		
		while (dictionaryEntryTypeiterator.hasNext()) {
			
			DictionaryEntryType dictionaryEntryType = dictionaryEntryTypeiterator.next();
			
			List<PolishJapaneseEntry> dictionaryEntryTypeList = groupedPolishJapaneseEntryList.get(dictionaryEntryType);
			
			CsvReaderWriter.generateCsv(new String[] { "input_names3/" + dictionaryEntryType + ".csv" }, dictionaryEntryTypeList, true, false, true, false, null);		
		}	
		*/	
	}
}
