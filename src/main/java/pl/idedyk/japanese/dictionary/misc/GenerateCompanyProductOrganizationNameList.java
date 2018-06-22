package pl.idedyk.japanese.dictionary.misc;

public class GenerateCompanyProductOrganizationNameList {
	
	public static void main(String[] args) throws Exception {
		
		/*
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		List<PolishJapaneseEntry> namesList = Helper.generateNames(jmedictName);
				
		List<PolishJapaneseEntry> newCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		
		List<PolishJapaneseEntry> companyAddedCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> productAddedCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> organizationAddedCompanyProductOrganizationName = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_COMPANY_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRODUCT_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ORGANIZATION_NAME) == true) {
				
				if (dictionaryEntryTypeList.size() == 1) {
					newCompanyProductOrganizationName.add(polishJapaneseEntry);
					
				} else {
					
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_COMPANY_NAME) == true) {

						companyAddedCompanyProductOrganizationName.add(polishJapaneseEntry);
					}
					
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRODUCT_NAME) == true) {

						productAddedCompanyProductOrganizationName.add(polishJapaneseEntry);
					}
					
					if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ORGANIZATION_NAME) == true) {
						
						organizationAddedCompanyProductOrganizationName.add(polishJapaneseEntry);
					}
				}				
			}			
		}
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/newCompanyProductOrganizationName.csv" }, newCompanyProductOrganizationName, true, false, true, false, null);
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/alreadyAddedCompanyName.csv" }, companyAddedCompanyProductOrganizationName, true, false, true, false, null);
		CsvReaderWriter.generateCsv(new String[] { "input_names2/alreadyAddedProductName.csv" }, productAddedCompanyProductOrganizationName, true, false, true, false, null);
		CsvReaderWriter.generateCsv(new String[] { "input_names2/alreadyAddedOrganizationName.csv" }, organizationAddedCompanyProductOrganizationName, true, false, true, false, null);
		*/
	}
}
