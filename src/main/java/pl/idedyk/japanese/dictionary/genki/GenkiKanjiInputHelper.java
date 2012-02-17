package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenkiKanjiInputHelper {

	public static void main(String[] args) throws IOException {
		
		List<KanjiEntry> kanjiList = new ArrayList<KanjiEntry>();
		
		while(true) {
			
			System.out.print("Kanji: ");
			
			String kanji = stdinReadline();
			
			if (kanji == null || kanji.equals("") == true) {
				break;
			}
			
			List<String> romajiList = new ArrayList<String>();
			
			while(true) {
				System.out.print("Romaji: ");
				
				String romaji = stdinReadline();
				
				if (romaji == null || romaji.equals("") == true) {
					break;
				}
				
				romajiList.add(romaji);
			}
			
			List<String> polishTranslateList = new ArrayList<String>();
			
			while(true) {
				System.out.print("Polish translate: ");
			
				String polishTranslateString = stdinReadline();
			
				if (polishTranslateString == null || polishTranslateString.equals("") == true) {
					break;
				}
				
				polishTranslateList.add(polishTranslateString);
			}
			
			System.out.print("Info: ");
			
			String info = stdinReadline();
			
			if (info.equals("") == true) {
				info = null;
			}
			
			KanjiEntry kanjiEntry = new KanjiEntry();
			
			kanjiEntry.setKanji(kanji);
			kanjiEntry.setRomajiList(romajiList);
			kanjiEntry.setPolishTranslateString(polishTranslateList);
			kanjiEntry.setInfo(info);
			
			kanjiList.add(kanjiEntry);
			
			System.out.println("---");
		}
		
		for (KanjiEntry kanjiEntry : kanjiList) {
			
			System.out.print("addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, \"" + 
					kanjiEntry.getKanji() + "\", new String[] { ");
			
			for (int romIdx = 0; romIdx < kanjiEntry.getRomajiList().size(); ++romIdx) {
				String romaji = kanjiEntry.getRomajiList().get(romIdx);
				
				System.out.print("\"" + romaji + "\"");
				
				if (romIdx != kanjiEntry.getRomajiList().size() - 1) {
					System.out.print(", ");
				}
			}
			
			System.out.print(" }, new String[] { ");
			
			for (int polIdx = 0; polIdx < kanjiEntry.getPolishTranslateString().size(); ++polIdx) {
				String polishTranslate = kanjiEntry.getPolishTranslateString().get(polIdx);
				
				System.out.print("\"" + polishTranslate + "\"");
				
				if (polIdx != kanjiEntry.getPolishTranslateString().size() - 1) {
					System.out.print(", ");
				}
			}
			
			System.out.println(" }, " + 
					(kanjiEntry.getInfo() == null ? "null" : "\"" + kanjiEntry.getInfo() + "\"") + ");");
		}
		
	}
	
	private static String stdinReadline() throws IOException {
		java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		
		return stdin.readLine();
	}

	
	private static class KanjiEntry {
		
		private String kanji;
		
		private List<String> romajiList;
		
		private List<String> polishTranslateList;
		
		private String info;

		public String getKanji() {
			return kanji;
		}

		public List<String> getRomajiList() {
			return romajiList;
		}

		public List<String> getPolishTranslateString() {
			return polishTranslateList;
		}

		public String getInfo() {
			return info;
		}

		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public void setRomajiList(List<String> romajiList) {
			this.romajiList = romajiList;
		}

		public void setPolishTranslateString(List<String> polishTranslateList) {
			this.polishTranslateList = polishTranslateList;
		}

		public void setInfo(String info) {
			this.info = info;
		}
	}
}
