package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenkiWordInputHelper {

	public static void main(String[] args) throws IOException {
		
		List<WordEntry> wordList = new ArrayList<WordEntry>();
		
		while(true) {
						
			System.out.print("Romaji: ");
				
			String romaji = stdinReadline();
				
			if (romaji == null || romaji.equals("") == true) {
				break;
			}
			
			System.out.print("Polish translate: ");
			
			String polishTranslateString = stdinReadline();
			
			if (polishTranslateString == null || polishTranslateString.equals("") == true) {
				break;
			}
						
			System.out.print("Info: ");
			
			String info = stdinReadline();
			
			if (info.equals("") == true) {
				info = null;
			}
			
			WordEntry wordEntry = new WordEntry();
			
			wordEntry.setRomaji(romaji);
			wordEntry.setPolishTranslateString(polishTranslateString);
			wordEntry.setInfo(info);
			
			wordList.add(wordEntry);
			
			System.out.println("---");
		}
		
		for (WordEntry wordEntry : wordList) {
			
			System.out.println("addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, \"" + 
					wordEntry.getRomaji() + "\", \"" + 
					wordEntry.getPolishTranslateString() + "\", " + 
					(wordEntry.getInfo() == null ? "null" : "\"" + wordEntry.getInfo() + "\"") + ");");

		}		
	}
	
	private static String stdinReadline() throws IOException {
		java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		
		return stdin.readLine();
	}

	
	private static class WordEntry {
				
		private String romaji;
		
		private String polishTranslate;
		
		private String info;

		public String getRomaji() {
			return romaji;
		}

		public String getPolishTranslateString() {
			return polishTranslate;
		}

		public String getInfo() {
			return info;
		}

		public void setRomaji(String romaji) {
			this.romaji = romaji;
		}

		public void setPolishTranslateString(String polishTranslate) {
			this.polishTranslate = polishTranslate;
		}

		public void setInfo(String info) {
			this.info = info;
		}
	}
}
