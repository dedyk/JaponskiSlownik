package pl.idedyk.japanese.dictionary.genki;

public enum DictionaryEntryType {
	
	GREETINGS("Powitania.properties"),
	
	GENKI1_1("Genki1_1.properties"),
	
	GENKI1_2("Genki1_2.properties"),
	
	GENKI1_2_ADDITIONAL("Genki1_2+.properties"),
	
	GENKI1_3("Genki1_3.properties"),
	
	NUMBERS("Liczby.properties"),
	
	HOURS("Godziny.properties"),
	
	MINUTES("Minuty.properties"),
	
	YEARS("Lata.properties"),
	
	ALL("All.properties");
	
	private String fileName;
	
	DictionaryEntryType(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}