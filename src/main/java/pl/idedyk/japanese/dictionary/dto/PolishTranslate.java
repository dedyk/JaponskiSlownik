package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class PolishTranslate {
	
	private String word;
	
	private List<String> infos;

	public String getWord() {
		return word;
	}

	public List<String> getInfo() {
		return infos;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setInfo(List<String> infos) {
		this.infos = infos;
	}
}
