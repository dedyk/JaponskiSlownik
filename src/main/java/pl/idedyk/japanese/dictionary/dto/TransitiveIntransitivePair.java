package pl.idedyk.japanese.dictionary.dto;

public class TransitiveIntransitivePair {
	
	private String transitiveKanji;
	private String transitiveKana;
	
	private String intransitiveKanji;
	private String intransitiveKana;
	
	public String getTransitiveKanji() {
		return transitiveKanji;
	}
	
	public void setTransitiveKanji(String transitiveKanji) {
		this.transitiveKanji = transitiveKanji;
	}
	
	public String getTransitiveKana() {
		return transitiveKana;
	}
	
	public void setTransitiveKana(String transitiveKana) {
		this.transitiveKana = transitiveKana;
	}
	
	public String getIntransitiveKanji() {
		return intransitiveKanji;
	}
	
	public void setIntransitiveKanji(String intransitiveKanji) {
		this.intransitiveKanji = intransitiveKanji;
	}
	
	public String getIntransitiveKana() {
		return intransitiveKana;
	}
	
	public void setIntransitiveKana(String intransitiveKana) {
		this.intransitiveKana = intransitiveKana;
	}	
}