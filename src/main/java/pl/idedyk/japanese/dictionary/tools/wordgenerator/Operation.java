package pl.idedyk.japanese.dictionary.tools.wordgenerator;

public enum Operation {
	
	GET_COMMON_PART_LIST("get-common-part-list");
	
	private String operation;
	
	Operation(String operation) {
		this.operation = operation;
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
HELP

GenerateJMEDictGroupWordList
GenerateJMEDictGroupWordList2
GenerateMissingWordList
GenerateMissingWordListInCommonWords
GetCommonPartList
FindMissingTheSameKanji
ShowAllJMEdictPriorities
ShowAllMissingWords
ShowAlreadyAddCommonWords
ShowMissingCommonWords
ShowMissingPriorityWords

+

prefix, postfix generator
*/
