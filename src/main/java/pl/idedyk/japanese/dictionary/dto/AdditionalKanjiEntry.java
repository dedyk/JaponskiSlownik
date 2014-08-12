package pl.idedyk.japanese.dictionary.dto;

public class AdditionalKanjiEntry {
	
	private String id;
	
	private String done;
	
	private String kanji;
	
	private String strokeCount;
	
	private String translate;
	
	private String info;
	
	private boolean useKanji;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDone() {
		return done;
	}

	public void setDone(String done) {
		this.done = done;
	}

	public String getStrokeCount() {
		return strokeCount;
	}

	public void setStrokeCount(String strokeCount) {
		this.strokeCount = strokeCount;
	}

	public String getTranslate() {
		return translate;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getKanji() {
		return kanji;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public void setUseKanji(boolean useKanji) {
		this.useKanji = useKanji;
	}

	public boolean isUseKanji() {
		return useKanji;
	}

	@Override
	public String toString() {
		return "AdditionalKanjiEntry [id=" + id + ", done=" + done + ", kanji=" + kanji + ", strokeCount="
				+ strokeCount + ", translate=" + translate + ", info=" + info + ", useKanji=" + useKanji + "]";
	}
}
