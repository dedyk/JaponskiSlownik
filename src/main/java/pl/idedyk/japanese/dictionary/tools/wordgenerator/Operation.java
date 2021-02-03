package pl.idedyk.japanese.dictionary.tools.wordgenerator;

public enum Operation {
	
	FIND_MISSING_THE_SAME_KANJI("find-missing-the-same-kanji", "Znajduje wszystkie słowa, których brakuje w słowniku, a które pisane są tym samym znakiem kanji"),
	
	FIND_MISSING_THE_SAME_KANA("find-missing-the-same-kana", "Znajduje wszystkie słowa, których brakuje w słowniku, a które pisane są tym samym znakiem kana"),
	
	FIX_DICTIONARY_WORD_TYPE("fix-dictionary-word-type", "Naprawia typy słów wedle nowego jmdict"),
	
	GET_COMMON_PART_LIST("get-common-part-list", "Pobiera listę common'owych słów"),
	
	GENERATE_MISSING_WORD_LIST("generate-missing-word-list", "Generuje brakujące znalezione słowa"),
	
	GENERATE_MISSING_WORD_LIST_NUMBER("generate-missing-word-list-number", "Genruje liczbę brakujących znalezionych słów, aby osiągnąć zamierzoną liczbę nowych słów"),
	
	GENERATE_MISSING_WORD_LIST_FROM_TEXT("generate-missing-word-list-from-text", "Generowanie brakujących słów z tekstu"),
	
	GENERATE_MISSING_WORD_LIST_IN_COMMON_WORDS("generate-missing-word-list-in-common-words", "Generuje słowa, krórych wyszukanie da wynik wśród common'owych słów"),
	
	GENERATE_JMEDICT_GROUP_WORD_LIST("generate-jmedict-group-word-list", "Generuje słowa z grup jmedict"),
	
	GENERATE_JMEDICT_GROUP_WORD_LIST2("generate-jmedict-group-word-list2", "Generuje słowa z grup jmedict 2"),
	
	GENERATE_PREFIX_WORD_LIST("generate-prefix-word-list", "Generuje słowa, które zawierają się w istniejących słowach"),
	
	GENERATE_PREFIX2_WORD_LIST("generate-prefix2-word-list", "Generuje słowa, które początych zaczyna się od istniejących słów"),
	
	SHOW_ALL_MISSING_WORDS("show-all-missing-words", "Generuje wszystkie słowa, które są w słowniku jmedict, a których brakuje"),
	
	SHOW_ALL_MISSING_WORDS_FROM_GROUP_ID("show-all-missing-word-from-group-id", "Generuje wszystkie słowa, które są w słowniku jmedict, a których brakuje. Sprawdzanie odbywa się na podstawie badania groupId"),
	
	SHOW_ALREADY_ADD_WORDS("show-already-add-words", "Pokazuje słowa, które są już dodane do słownika"),
	
	SHOW_ALREADY_ADD_COMMON_WORDS("show-already-add-common-words", "Pokazuje common'owe słowa, które są już dodane do słownika"),
	
	SHOW_EMPTY_COMMON_WORDS("show-empty-common-words", "Pokazuje identyfikatory common'owych słów, które już nie istnieją"),
	
	SHOW_MISSING_PRIORITY_WORDS("show-missing-priority-words", "Generuje brakujące słowa priorytetowe z grup jmedict"),
	
	SHOW_SIMILAR_RELATED_WORDS("show-similar-related-words", "Pokazuje podobne słowa"),
	
	//SHOW_MISSING_WORDS_AND_COMPARE_TO_JMEDICT("show-missing-words-and-compare-to-jmedict", "Pokazuje słowa, których nie ma w jmedict"),
	
	FIND_WORDS_WITH_JMEDICT_CHANGE("find-words-with-jmedict-change", "Pokazuje słowa, które zmieniły swoje tłumaczenie w jmedict"),
	
	FIND_WORDS_NO_EXIST_IN_JMEDICT("find-words-no-exist-in-jmedict", "Pokazuje słowa, które nie istnieją już w słowniku jmedict"),
	
	FIND_WORDS_WITH_JMEDICT_GROUP_CHANGE("find-words-with-jmedict-group-change", "Podaje listę identyfikatorów grup w słowniku, które zmieniły swoją grupę"),
	
	GET_WORDS_BY_ID("get-words-by-id", "Pobiera słowa o podanych id'kach"),
	
	FIND_KANJIS_WITH_KANJIDIC2_CHANGE("find-kanjis-with-kanjidic2-change", "Pokazuje słowa, które zmieniły swoje tłumaczenie w kanjidic2"),
	
	FILTER_WORD_LIST("filter-word-list", "Filtruje listę słów"),
	
	FIND_PARTIAL_TRANSLATE_WORDS("find-partial-translate-words", "Pokazuje słowa niewpełni przetłumaczone"),
	
	HELP("help", "Pokazuje pomoc");
	
	private String operation;
	
	private String description;
	
	Operation(String operation, String description) {
		this.operation = operation;
		this.description = description;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public String getDescription() {
		return description;
	}

	public static Operation findOperation(String operation) {
		
		Operation[] values = values();
		
		for (Operation currentOperation : values) {
			
			if (currentOperation.operation.equals(operation) == true) {
				return currentOperation;				
			}			
		}
		
		return null;
	}	
}
