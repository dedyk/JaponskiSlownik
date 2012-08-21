package pl.idedyk.japanese.dictionary.dto;

import java.util.List;


public class PolishJapaneseEntry {
	
	private int id;
	
	private DictionaryType dictionaryType;
	
	private DictionaryEntryType dictionaryEntryType;
	
	private WordType wordType;
	
	private String prefixKana;
	
	private String kanji;
	
	private String kanjiImagePath;
	
	private List<String> kanaList;
	
	private String prefixRomaji;
	
	private List<String> romajiList;
		
	private List<String> polishTranslates;
	
	private String info;
	
	private boolean useEntry;

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

	public String getPrefixKana() {
		return prefixKana;
	}

	public void setPrefixKana(String prefixKana) {
		this.prefixKana = prefixKana;
	}
	
	public String getFullKanji() {
		return kanji.equals("-") == false ? prefixKana + kanji : kanji;
	}

	@Override
	public String toString() {
		return "PolishJapaneseEntry [id=" + id + ", dictionaryEntryType="
				+ dictionaryEntryType + ", wordType=" + wordType + ", prefixKana="
				+ prefixKana + ", kanji=" + kanji + ", kanjiImagePath="
				+ kanjiImagePath + ", kanaList=" + kanaList + ", romajiList="
				+ romajiList + ", polishTranslates=" + polishTranslates
				+ ", info=" + info + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}

	public void setDictionaryType(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}

	public boolean isUseEntry() {
		return useEntry;
	}

	public void setUseEntry(boolean useEntry) {
		this.useEntry = useEntry;
	}

	public String getPrefixRomaji() {
		return prefixRomaji;
	}

	public void setPrefixRomaji(String prefixRomaji) {
		this.prefixRomaji = prefixRomaji;
	}
}
