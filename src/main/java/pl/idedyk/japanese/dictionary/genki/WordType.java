package pl.idedyk.japanese.dictionary.genki;

public enum WordType {
	HIRAGANA("H"),
	
	KATAKANA("K");
	
	private String printable;

	WordType(String printable) {
		this.printable = printable;
	}

	public String getPrintable() {
		return printable;
	}
}