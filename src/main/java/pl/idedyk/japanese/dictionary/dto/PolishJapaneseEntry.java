package pl.idedyk.japanese.dictionary.dto;

import java.util.List;
import java.util.Set;

public class PolishJapaneseEntry implements Comparable<PolishJapaneseEntry> {
	
	private int id;
	
	private DictionaryEntryType dictionaryEntryType;
	
	private List<AttributeType> attributeTypeList;
	
	private WordType wordType;
	
	private String prefixKana;
	
	private List<String> groups;
	
	private String kanji;
	
	private String kanjiImagePath;
	
	private List<String> kanaList;
	
	private String prefixRomaji;
	
	private String realPrefixRomaji;
	
	private List<String> romajiList;
	
	private List<String> realRomajiList;
		
	private List<String> polishTranslates;
	
	private String info;
	
	private boolean useEntry;
	
	private Set<Integer> knownDuplicatedId;

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

	public Set<Integer> getKnownDuplicatedId() {
		return knownDuplicatedId;
	}

	public void setKnownDuplicatedId(Set<Integer> knownDuplicatedId) {
		this.knownDuplicatedId = knownDuplicatedId;
	}

	@Override
	public String toString() {
		return "PolishJapaneseEntry [id=" + id + ", dictionaryEntryType="
				+ dictionaryEntryType + ", attributeTypeList="
				+ attributeTypeList + ", wordType=" + wordType
				+ ", prefixKana=" + prefixKana + ", groups=" + groups
				+ ", kanji=" + kanji + ", kanaList=" + kanaList
				+ ", prefixRomaji=" + prefixRomaji + ", romajiList="
				+ romajiList + ", polishTranslates=" + polishTranslates
				+ ", info=" + info + ", useEntry=" + useEntry
				+ ", knownDuplicatedId=" + knownDuplicatedId + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getRealPrefixRomaji() {
		return realPrefixRomaji;
	}

	public void setRealPrefixRomaji(String realPrefixRomaji) {
		this.realPrefixRomaji = realPrefixRomaji;
	}

	public List<String> getRealRomajiList() {
		return realRomajiList;
	}

	public void setRealRomajiList(List<String> realRomajiList) {
		this.realRomajiList = realRomajiList;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<AttributeType> getAttributeTypeList() {
		return attributeTypeList;
	}

	public void setAttributeTypeList(List<AttributeType> attributeTypeList) {
		this.attributeTypeList = attributeTypeList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		
		PolishJapaneseEntry other = (PolishJapaneseEntry) obj;
		
		if (id != other.id)
			return false;
		
		return true;
	}

	@Override
	public int compareTo(PolishJapaneseEntry entry) {
		
		if (id < entry.id) { 
			return -1;
		} else if (id > entry.id) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public String getEntryPrefixKanaKanjiKanaKey() {
		return prefixKana + "." + kanji + "." + kanaList.toString();
	}
}
