package pl.idedyk.japanese.dictionary.dto;

import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;

public class PolishJapaneseEntry extends DictionaryEntry {

	private static final long serialVersionUID = 1L;

	private int id;
		
	private String kanjiImagePath;
	
	private String realPrefixRomaji;
	
	private List<String> realRomajiList;
	
	private List<ParseAdditionalInfo> parseAdditionalInfoList;

	private Set<Integer> knownDuplicatedId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getRealPrefixRomaji() {
		return realPrefixRomaji;
	}

	public void setRealPrefixRomaji(String realPrefixRomaji) {
		this.realPrefixRomaji = realPrefixRomaji;
	}

	public String getKanjiImagePath() {
		return kanjiImagePath;
	}

	public void setKanjiImagePath(String kanjiImagePath) {
		this.kanjiImagePath = kanjiImagePath;
	}
	
	public List<String> getRealRomajiList() {
		return realRomajiList;
	}

	public void setRealRomajiList(List<String> realRomajiList) {
		this.realRomajiList = realRomajiList;
	}
	
	public List<ParseAdditionalInfo> getParseAdditionalInfoList() {
		return parseAdditionalInfoList;
	}

	public void setParseAdditionalInfoList(List<ParseAdditionalInfo> parseAdditionalInfoList) {
		this.parseAdditionalInfoList = parseAdditionalInfoList;
	}
	
	public Set<Integer> getKnownDuplicatedId() {
		return knownDuplicatedId;
	}

	public void setKnownDuplicatedId(Set<Integer> knownDuplicatedId) {
		this.knownDuplicatedId = knownDuplicatedId;
	}
	
	public String getEntryPrefixKanaKanjiKanaKey() {
		return getPrefixKana() + "." + getKanji() + "." + getKanaList().toString();
	}
}
