package pl.idedyk.japanese.dictionary.genki;

public enum DictionaryEntryType {
	
	WORD_GREETINGS("Powitania.properties"),
	
	WORD_GENKI1_1("Genki1_1.properties"),
	
	WORD_GENKI1_2("Genki1_2.properties"),
	
	WORD_GENKI1_2_ADDITIONAL("Genki1_2+.properties"),
	
	WORD_GENKI1_3("Genki1_3.properties"),
	
	WORD_GENKI1_3_ADDITIONAL("Genki1_3+.properties"),
	
	WORD_GENKI1_4("Genki1_4.properties"),
	
	WORD_GENKI1_5("Genki1_5.properties"),
	
	WORD_NUMBERS("Liczby.properties"),
	
	WORD_HOURS("Godziny.properties"),
	
	WORD_MINUTES("Minuty.properties"),
	
	WORD_YEARS("Lata.properties"),
	
	ALL("All.properties");
	
	private String fileName;
	
	DictionaryEntryType(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}