package pl.idedyk.japanese.dictionary.dto;

import pl.idedyk.japanese.dictionary.genki.WordType;

public class RomajiEntry {
	private String romaji;
	
	private WordType wordType;

	public String getRomaji() {
		return romaji;
	}

	public WordType getWordType() {
		return wordType;
	}

	public void setRomaji(String romaji) {
		this.romaji = romaji;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}
}
