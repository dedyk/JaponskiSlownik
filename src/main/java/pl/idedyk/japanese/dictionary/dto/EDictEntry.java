package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

public class EDictEntry {

	private String kanji;

	private String kana;

	private String name;

	private List<String> pos;

	private String rawLine;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRawLine() {
		return rawLine;
	}

	public void setRawLine(String rawLine) {
		this.rawLine = rawLine;
	}

	@Override
	public String toString() {
		return kanji + " - " + kana + " - " + name + " - " + pos + " - " + rawLine;
	}
}
