package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

import pl.idedyk.japanese.dictionary.genki.WordType;

public class PolishJapaneseEntry {
	
	private int id;
	
	private String groupName;
	
	private WordType wordType;
	
	private List<RomajiEntry> romajiList;
	
	private String japanese;
	
	private String japaneseImagePath;
	
	private List<PolishTranslate> polishTranslates;

	public List<RomajiEntry> getRomajiList() {
		return romajiList;
	}

	public String getJapanese() {
		return japanese;
	}

	public List<PolishTranslate> getPolishTranslates() {
		return polishTranslates;
	}

	public void setRomajiList(List<RomajiEntry> romajiList) {
		this.romajiList = romajiList;
	}

	public void setJapanese(String japanese) {
		this.japanese = japanese;
	}

	public void setPolishTranslates(List<PolishTranslate> polishTranslates) {
		this.polishTranslates = polishTranslates;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJapaneseImagePath() {
		return japaneseImagePath;
	}

	public void setJapaneseImagePath(String japaneseImagePath) {
		this.japaneseImagePath = japaneseImagePath;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public WordType getWordType() {
		return wordType;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}
}
