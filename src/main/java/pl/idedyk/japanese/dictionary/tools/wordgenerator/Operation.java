package pl.idedyk.japanese.dictionary.tools.wordgenerator;

public enum Operation {
	
	GET_COMMON_PART_LIST("get-common-part-list", "Pobiera listę common'owych słów"),
	
	GENERATE_MISSING_WORD_LIST_IN_COMMON_WORDS("generate-missing-word-list-in-common-words", "Pokazuje słowa, krórych wyszukanie da wynik wśród common'owych słów"),
	
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

/*
GenerateJMEDictGroupWordList
GenerateJMEDictGroupWordList2
GenerateMissingWordList
FindMissingTheSameKanji
ShowAllJMEdictPriorities
ShowAllMissingWords
ShowAlreadyAddCommonWords
ShowMissingCommonWords
ShowMissingPriorityWords

+

prefix, postfix generator
*/
