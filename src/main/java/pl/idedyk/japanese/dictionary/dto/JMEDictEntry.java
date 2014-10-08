package pl.idedyk.japanese.dictionary.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JMEDictEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> kanji = new ArrayList<String>();

	private final List<String> kana = new ArrayList<String>();

	private final List<String> pos = new ArrayList<String>();

	private final List<String> misc = new ArrayList<String>();

	private final List<String> trans = new ArrayList<String>();
	
	private final List<String> transDet = new ArrayList<String>();

	public List<String> getKanji() {
		return kanji;
	}

	public List<String> getKana() {
		return kana;
	}

	public List<String> getPos() {
		return pos;
	}

	public List<String> getMisc() {
		return misc;
	}

	public List<String> getTrans() {
		return trans;
	}

	public List<String> getTransDet() {
		return transDet;
	}

	@Override
	public String toString() {
		return kanji + " - " + kana + " - " + pos + " - " + misc + " - " + trans + " - " + transDet;
	}
}
