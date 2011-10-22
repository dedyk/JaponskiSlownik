package pl.idedyk.japanese.dictionary.genki;

public enum DictionaryEntryType {
	
	GREETINGS("greetings.properties");
	
	private String fileName;
	
	DictionaryEntryType(String fileName) {
		this.fileName = fileName;
	}
}