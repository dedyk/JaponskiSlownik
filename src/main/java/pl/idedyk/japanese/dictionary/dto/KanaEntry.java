package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class KanaEntry {
	private String kanaJapanese;
	
	private String kana;
	
	private String image;
	
	private List<KanjivgEntry> strokePaths;

	public KanaEntry(String kanaJapanese, String kana) {
		this.kanaJapanese = kanaJapanese;
		this.kana = kana;
	}

	public String getKanaJapanese() {
		return kanaJapanese;
	}

	public String getKana() {
		return kana;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<KanjivgEntry> getStrokePaths() {
		return strokePaths;
	}

	public void setStrokePaths(List<KanjivgEntry> strokePaths) {
		this.strokePaths = strokePaths;
	}
}