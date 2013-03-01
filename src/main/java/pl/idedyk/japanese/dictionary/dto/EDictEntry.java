package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class EDictEntry {
	
	private List<String> kanji = new ArrayList<String>();
	
	private List<String> kana = new ArrayList<String>();
	
	private List<String> pos = new ArrayList<String>();

	public List<String> getKanji() {
		return kanji;
	}

	public List<String> getKana() {
		return kana;
	}

	public List<String> getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return kanji + " - " + kana + " - " + pos;
	}	
}
