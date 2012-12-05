package pl.idedyk.japanese.dictionary.dto;

public enum WordType {
	HIRAGANA("H"),
	
	KATAKANA("K"),
	
	HIRAGANA_KATAKANA("H"),
	
	KATAKANA_HIRAGANA("K");
	
	private String printable;

	WordType(String printable) {
		this.printable = printable;
	}

	public String getPrintable() {
		return printable;
	}
}