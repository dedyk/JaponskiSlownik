package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

import pl.idedyk.japanese.dictionary.genki.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.genki.WordType;

public class PolishJapaneseEntry {
	
	private DictionaryEntryType dictionaryEntryType;
	
	private WordType wordType;
	
	private String prefix;
	
	private String kanji;
	
	private String kanjiImagePath;
	
	private List<String> kanaList;
	
	private List<String> romajiList;
		
	private List<String> polishTranslates;
	
	private String info;

	public DictionaryEntryType getDictionaryEntryType() {
		return dictionaryEntryType;
	}

	public WordType getWordType() {
		return wordType;
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

	public List<String> getRomajiList() {
		return romajiList;
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

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
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

	public void setRomajiList(List<String> romajiList) {
		this.romajiList = romajiList;
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
				+ dictionaryEntryType + ", wordType=" + wordType + ", prefix="
				+ prefix + ", kanji=" + kanji + ", kanjiImagePath="
				+ kanjiImagePath + ", kanaList=" + kanaList + ", romajiList="
				+ romajiList + ", polishTranslates=" + polishTranslates
				+ ", info=" + info + "]";
	}	
}
