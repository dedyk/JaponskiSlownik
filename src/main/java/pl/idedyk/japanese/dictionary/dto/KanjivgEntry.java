package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class KanjivgEntry {

	private String kanji;
	
	private List<String> strokePaths;

	public String getKanji() {
		return kanji;
	}

	public List<String> getStrokePaths() {
		return strokePaths;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public void setStrokePaths(List<String> strokePaths) {
		this.strokePaths = strokePaths;
	}
}
