package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class EDictEntry {
	
	private String kanji;
	
	private String kana;
	
	private List<String> pos;

	public String getKanji() {
		return kanji;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public String getKana() {
		return kana;
	}

	public void setKana(String kana) {
		this.kana = kana;
	}

	public List<String> getPos() {
		return pos;
	}

	public void setPos(List<String> pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return kanji + " - " + kana + " - " + pos;
	}	
}
