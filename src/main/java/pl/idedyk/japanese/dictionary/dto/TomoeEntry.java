package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class TomoeEntry {
	
	private String kanji;
	
	private List<Stroke> strokeList = new ArrayList<Stroke>();
	
	public TomoeEntry(String kanji) {
		this.kanji = kanji;
	}

	public String getKanji() {
		return kanji;
	}

	public List<Stroke> getStrokeList() {
		return strokeList;
	}

	public static class Stroke {
	
		private List<Point> pointList = new ArrayList<Point>();
		
		public List<Point> getPointList() {
			return pointList;
		}

		public static class Point {
			
			private int x;
			
			private int y;

			public Point(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}
		}
	}
}
