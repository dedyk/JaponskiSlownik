package pl.idedyk.japanese.dictionary.dto;

import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;

public class PolishJapaneseEntry extends DictionaryEntry implements Comparable<PolishJapaneseEntry>, Cloneable {

	private static final long serialVersionUID = 1L;
		
	private String kanjiImagePath;
	
	private String realPrefixRomaji;
	
	private String realRomaji;
	
	private List<ParseAdditionalInfo> parseAdditionalInfoList;

	private Set<Integer> knownDuplicatedId;
		
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
	
	public String getRealRomaji() {
		return realRomaji;
	}

	public void setRealRomaji(String realRomaji) {
		this.realRomaji = realRomaji;
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
		return getPrefixKana() + "." + getKanji() + "." + getKana().toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		PolishJapaneseEntry other = (PolishJapaneseEntry) obj;

		if (getId() != other.getId()) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(PolishJapaneseEntry entry) {

		if (getId() < entry.getId()) {
			return -1;
		} else if (getId() > entry.getId()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public  Object clone() throws CloneNotSupportedException {
		return super.clone();
	}	
}
