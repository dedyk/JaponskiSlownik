package pl.idedyk.japanese.dictionary.japannaka.utils;

public class Utils {
	public static String replaceChars(String text) {
		
		/*
		text = text.replaceAll("Ę", "E");
		text = text.replaceAll("ę", "e");
		
		text = text.replaceAll("Ó", "O");
		text = text.replaceAll("ó", "o");
		
		text = text.replaceAll("Ą", "A");
		text = text.replaceAll("ą", "a");
		
		text = text.replaceAll("Ś", "S");
		text = text.replaceAll("ś", "s");
		
		text = text.replaceAll("Ł", "L");
		text = text.replaceAll("ł", "l");
		
		text = text.replaceAll("Ż", "Z");
		text = text.replaceAll("ż", "z");
		
		text = text.replaceAll("Ź", "z");
		text = text.replaceAll("ź", "z");
		
		text = text.replaceAll("Ć", "C");
		text = text.replaceAll("ć", "c");
		
		text = text.replaceAll("Ń", "N");
		text = text.replaceAll("ń", "n");
		*/
		
		text = text.replaceAll("â", "a-");
		text = text.replaceAll("ā", "a-");
		text = text.replaceAll("ō", "o-");
		text = text.replaceAll("ū", "u-");
		
		return text;
	}
}
