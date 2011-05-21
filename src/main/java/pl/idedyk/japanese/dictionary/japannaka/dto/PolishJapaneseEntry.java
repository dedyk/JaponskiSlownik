package pl.idedyk.japanese.dictionary.japannaka.dto;

import java.util.List;

public class PolishJapaneseEntry {
	
	private String romaji;
	
	private String japanese;
	
	private List<PolishTranslate> polishTranslates;

	public String getRomaji() {
		return romaji;
	}

	public String getJapanese() {
		return japanese;
	}

	public List<PolishTranslate> getPolishTranslates() {
		return polishTranslates;
	}

	public void setRomaji(String romaji) {
		this.romaji = romaji;
	}

	public void setJapanese(String japanese) {
		this.japanese = japanese;
	}

	public void setPolishTranslates(List<PolishTranslate> polishTranslates) {
		this.polishTranslates = polishTranslates;
	}
}
