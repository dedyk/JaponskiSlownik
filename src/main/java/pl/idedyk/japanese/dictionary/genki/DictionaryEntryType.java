package pl.idedyk.japanese.dictionary.genki;

public enum DictionaryEntryType {
	
	WORD_GREETINGS("Powitania"),
	
	WORD_GENKI1_1("Genki 1_1"),
	
	WORD_GENKI1_2("Genki 1_2"),
	
	WORD_GENKI1_3("Genki 1_3"),
	
	WORD_GENKI1_4("Genki 1_4"),
	
	WORD_GENKI1_5("Genki 1_5"),
	
	WORD_GENKI1_6("Genki 1_6"),
	
	WORD_GENKI1_7("Genki 1_7"),
	
	WORD_GENKI1_8("Genki 1_8"),
	
	WORD_GENKI1_9("Genki 1_9"),
	
	WORD_GENKI1_10("Genki 1_10"),
	
	WORD_GENKI1_11("Genki 1_11"),
	
	WORD_NUMBERS("Liczby"),
	
	WORD_HOURS("Godziny"),
	
	WORD_MINUTES("Minuty"),
	
	WORD_YEARS("Lata"),
	
	WORD_VERB_U("u-czasownik"),
	
	WORD_VERB_RU("ru-czasownik"),
	
	WORD_ADJECTIVE_I("i-przymiotnik"),
	
	WORD_ADJECTIVE_NA("na-przymiotnik"),
	
	UNKNOWN("unknown");
	
	private String name;
	
	DictionaryEntryType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}