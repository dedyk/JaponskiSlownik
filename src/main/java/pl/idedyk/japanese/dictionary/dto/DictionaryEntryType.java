package pl.idedyk.japanese.dictionary.dto;

public enum DictionaryEntryType {

	WORD_NUMBER("Liczba"),

	WORD_TIME("Godzina"),

	WORD_AGE("Wiek"),

	WORD_NOUN("rzeczownik"),
	
	WORD_PRE_NOUN_ADJECTIVAL("???"),
	
	WORD_TEMPORAL_NOUN("rzeczownik czasowy"),

	WORD_VERB_U("u-czasownik"),

	WORD_VERB_RU("ru-czasownik"),

	WORD_VERB_TE("te-czasownik"),

	WORD_VERB_IRREGULAR("czasownik nieregularny"),

	WORD_VERB_ZURU("czasownik zuru"),
	
	WORD_VERB_AUX("czasownik pomocniczy"),
	
	WORD_ADJECTIVE_F("rzeczownik, bądź czasownik pełniący rolę przymiotnika"),

	WORD_ADJECTIVE_I("i-przymiotnik"),

	WORD_AUX_ADJECTIVE_I("pomocniczy i-przymiotnik"),

	WORD_ADJECTIVE_NA("na-przymiotnik"),

	WORD_ADJECTIVE_TARU("taru-przymiotnik"),

	WORD_EXPRESSION("Wyrażenia"),

	WORD_THAT_POINT("Wskazywanie punktu"),

	WORD_ASK("Słówka pytające"),

	WORD_ADVERB("Przysłówek"),

	WORD_ADVERB_TO("Przysłówek z partykułą to"),

	WORD_ADVERBIAL_NOUN("Rzeczownik przysłówkowy"),

	WORD_PRE_NOUN_ADVERBIAL("Pre rzeczownik przysłówkowy"),

	WORD_DAY_NUMBER("Numer dnia"),

	WORD_DAY_WEEK("Dzień tygodnia"),

	WORD_MONTH("Miesiąc"),

	WORD_LOCATION("Lokalizacja"),

	WORD_PEOPLE_NUMBER("Liczenie ludzi"),

	WORD_COUNT_DAY_NUMBER("Liczenie dni"),

	WORD_COUNTERS("Klasyfikatory"),

	WORD_PRONOUN("Zaimki"),

	WORD_NAME("Imię"),

	WORD_MALE_NAME("Imię męskie"),

	WORD_FEMALE_NAME("Imię żeńskie"),

	WORD_SURNAME_NAME("Nazwisko"),

	WORD_CONJUNCTION("Spójnik"),

	WORD_PARTICULE("Partykuła"),

	WORD_INTERJECTION("Wykrzyknik"),

	WORD_AUX("Pomocniczy"),
	
	WORD_ATEJI("Ateji (fonetyczne czytanie)"),

	WORD_EMPTY("Pusty"),

	UNKNOWN("unknown");

	private String name;

	DictionaryEntryType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}