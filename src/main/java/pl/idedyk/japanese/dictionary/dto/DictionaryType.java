package pl.idedyk.japanese.dictionary.dto;

public enum DictionaryType {
	WORD("WORD"),
	
	KANJI("KANJI");
	
	private String name;
	
	DictionaryType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
