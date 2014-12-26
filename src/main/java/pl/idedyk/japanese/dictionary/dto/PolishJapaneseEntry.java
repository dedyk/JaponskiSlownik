package pl.idedyk.japanese.dictionary.dto;

import java.util.List;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;

public class PolishJapaneseEntry extends DictionaryEntry implements Comparable<PolishJapaneseEntry>, Cloneable {

	private static final long serialVersionUID = 1L;
		
	private String kanjiImagePath;
	
	private String realPrefixRomaji;
	
	private String realRomaji;
	
	private List<ParseAdditionalInfo> parseAdditionalInfoList;

	private List<KnownDuplicate> knownDuplicatedList;
		
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
	
	public List<KnownDuplicate> getKnownDuplicatedList() {
		return knownDuplicatedList;
	}

	public void setKnownDuplicatedList(List<KnownDuplicate> knownDuplicatedList) {
		this.knownDuplicatedList = knownDuplicatedList;
	}
	
	public boolean isKnownDuplicate(KnownDuplicateType knownDuplicateType, int id) {
		
		for (KnownDuplicate knownDuplicate : knownDuplicatedList) {
			
			if (knownDuplicate.getKnownDuplicateType() != knownDuplicateType) {
				continue;
			}
			
			if (knownDuplicate.getId() == id) {
				return true;
			}
		}
		
		return false;
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
	
	public static class KnownDuplicate {
		
		private KnownDuplicateType knownDuplicateType;
		
		private int id;

		public KnownDuplicate(KnownDuplicateType knownDuplicateType, int id) {
			this.knownDuplicateType = knownDuplicateType;
			this.id = id;
		}

		public KnownDuplicateType getKnownDuplicateType() {
			return knownDuplicateType;
		}

		public int getId() {
			return id;
		}

		@Override
		public int hashCode() {
			
			final int prime = 31;
			
			int result = 1;
			
			result = prime * result + ((knownDuplicateType == null) ? 0 : knownDuplicateType.hashCode());
			result = prime * result + id;
			
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			KnownDuplicate other = (KnownDuplicate) obj;
			
			if (knownDuplicateType != other.knownDuplicateType)
				return false;
			
			if (id != other.id)
				return false;
			
			return true;
		}
	}
	
	public static enum KnownDuplicateType {		
		DUPLICATE;		
	}
}
