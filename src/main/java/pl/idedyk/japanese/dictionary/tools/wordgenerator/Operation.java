package pl.idedyk.japanese.dictionary.tools.wordgenerator;

public enum Operation {
	
	FIND_MISSING_THE_SAME_KANJI("find-missing-the-same-kanji", "Znajduje wszystkie słowa, których brakuje w słowniku, a które pisane są tym samym znakiem kanji"),
	
	GET_COMMON_PART_LIST("get-common-part-list", "Pobiera listę common'owych słów"),
	
	GENERATE_MISSING_WORD_LIST("generate-missing-word-list", "Generuje grabujące znalezione słowa"),
	
	GENERATE_MISSING_WORD_LIST_IN_COMMON_WORDS("generate-missing-word-list-in-common-words", "Generuje słowa, krórych wyszukanie da wynik wśród common'owych słów"),
	
	GENERATE_JMEDICT_GROUP_WORD_LIST("generate-jmedict-group-word-list", "Generuje słowa z grup jmedict"),
	
	GENERATE_JMEDICT_GROUP_WORD_LIST2("generate-jmedict-group-word-list2", "Generuje słowa z grup jmedict 2"),
	
	SHOW_ALL_MISSING_WORDS("show-all-missing-words", "Generuje wszystkie słowa, które są w słowniku jmedict, a których brakuje"),
	
	SHOW_ALREADY_ADD_COMMON_WORDS("show-already-add-common-words", "Pokazuje common'owe słowa, które są już dodane do słownika"),
	
	SHOW_MISSING_PRIORITY_WORDS("show-missing-priority-words", "Generuje brakujące słowa priorytetowe z grup jmedict"),
	
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
