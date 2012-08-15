package pl.idedyk.japanese.dictionary.dto;

public class RadiacalInfo {
	
	private int id;
	
	private String radiacal;
	
	private int strokeCount;

	public int getId() {
		return id;
	}

	public String getRadiacal() {
		return radiacal;
	}

	public int getStrokeCount() {
		return strokeCount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRadiacal(String radiacal) {
		this.radiacal = radiacal;
	}

	public void setStrokeCount(int strokeCount) {
		this.strokeCount = strokeCount;
	}	
}
