package pl.idedyk.japanese.dictionary.genki;

public enum DictionaryEntryType {
	
	WORD_GREETINGS("Powitania"),
		
	WORD_NUMBERS("Liczby"),
	
	WORD_HOURS("Godziny"),
	
	WORD_MINUTES("Minuty"),
	
	WORD_YEARS("Lata"),
	
	WORD_VERB_U("u-czasownik"),
	
	WORD_VERB_RU("ru-czasownik"),
	
	WORD_VERB_TE("te-czasownik"),
	
	WORD_VERB_IRREGULAR("czasownik nieregularny"),
	
	WORD_ADJECTIVE_I("i-przymiotnik"),
	
	WORD_ADJECTIVE_NA("na-przymiotnik"),
	
	WORD_KANJI_READING("kanji czytanie"),
	
	UNKNOWN("unknown");
	
	private String name;
	
	DictionaryEntryType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}