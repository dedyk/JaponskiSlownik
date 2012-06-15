package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;

public class PolishJapaneseEntry {
	
	private DictionaryEntryType dictionaryEntryType;
	
	private String prefix;
	
	private String kanji;
	
	private String kanjiImagePath;
	
	private List<String> kanaList;
		
	private List<String> polishTranslates;
	
	private String info;

	public DictionaryEntryType getDictionaryEntryType() {
		return dictionaryEntryType;
	}

	public String getKanji() {
		return kanji;
	}

	public String getKanjiImagePath() {
		return kanjiImagePath;
	}

	public List<String> getKanaList() {
		return kanaList;
	}

	public List<String> getPolishTranslates() {
		return polishTranslates;
	}

	public String getInfo() {
		return info;
	}

	public void setDictionaryEntryType(DictionaryEntryType dictionaryEntryType) {
		this.dictionaryEntryType = dictionaryEntryType;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public void setKanjiImagePath(String kanjiImagePath) {
		this.kanjiImagePath = kanjiImagePath;
	}

	public void setKanaList(List<String> kanaList) {
		this.kanaList = kanaList;
	}

	public void setPolishTranslates(List<String> polishTranslates) {
		this.polishTranslates = polishTranslates;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getFullKanji() {
		return kanji.equals("-") == false ? prefix + kanji : kanji;
	}

	@Override
	public String toString() {
		return "PolishJapaneseEntry [dictionaryEntryType="
				+ dictionaryEntryType + ", "
				+ prefix + ", kanji=" + kanji + ", kanjiImagePath="
				+ kanjiImagePath + ", kanaList=" + kanaList + ", "
				+ "polishTranslates=" + polishTranslates
				+ ", info=" + info + "]";
	}	
}
