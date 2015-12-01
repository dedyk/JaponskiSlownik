package pl.idedyk.japanese.dictionary.dto;

public class CommonWord {
	
	private Integer id;
	
	private boolean done;
	
	private String kanji;
	private String kana;
	
	private String type;
	
	private String translate;
	
	public CommonWord(Integer id, boolean done, String kanji, String kana, String type, String translate) {
		this.id = id;
		this.done = done;
		this.kanji = kanji;
		this.kana = kana;
		this.type = type;
		this.translate = translate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public String getKanji() {
		return kanji;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public String getKana() {
		return kana;
	}

	public void setKana(String kana) {
		this.kana = kana;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTranslate() {
		return translate;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}

	@Override
	public String toString() {
		return "CommonWord [id=" + id + ", done=" + done + ", kanji=" + kanji + ", kana=" + kana + ", type=" + type
				+ ", translate=" + translate + "]";
	}
}
