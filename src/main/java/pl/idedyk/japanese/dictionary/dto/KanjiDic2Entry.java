package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class KanjiDic2Entry {
	
	private String kanji;
	
	private int strokeCount;
	
	private List<String> onReading;
	
	private List<String> kunReading;
	
	private List<String> radicals;
	
	private Integer jlpt;
	
	private Integer freq;

	public String getKanji() {
		return kanji;
	}

	public int getStrokeCount() {
		return strokeCount;
	}

	public List<String> getOnReading() {
		return onReading;
	}

	public List<String> getKunReading() {
		return kunReading;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public void setStrokeCount(int strokeCount) {
		this.strokeCount = strokeCount;
	}

	public void setOnReading(List<String> onReading) {
		this.onReading = onReading;
	}

	public void setKunReading(List<String> kunReading) {
		this.kunReading = kunReading;
	}

	public List<String> getRadicals() {
		return radicals;
	}

	public void setRadicals(List<String> radicals) {
		this.radicals = radicals;
	}

	public Integer getJlpt() {
		return jlpt;
	}

	public void setJlpt(Integer jlpt) {
		this.jlpt = jlpt;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}
}
