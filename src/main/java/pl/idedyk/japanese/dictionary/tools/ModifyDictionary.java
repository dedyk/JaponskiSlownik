package pl.idedyk.japanese.dictionary.tools;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class ModifyDictionary {

	public static void main(String[] args) throws Exception {
		
		checkPolishJapaneseEntries(new String[] { "input/word01.csv", "input/word02.csv" }, new String[] { "input/word01-temp.csv", "input/word02-temp.csv" });
	}
	
	private static void checkPolishJapaneseEntries(String[] sourceFileNames, String[] destinationFileNames) throws Exception {
		
		//List<KanaEntry> allKanaEntries = KanaHelper.getAllHiraganaKanaEntries();
		//allKanaEntries.addAll(KanaHelper.getAllKatakanaKanaEntries());
		
		/*
		Set<String> kanaSet = new TreeSet<String>();
		
		for (KanaEntry currentKana : allKanaEntries) {
			kanaSet.add(currentKana.getKanaJapanese());
		}
		*/
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(sourceFileNames);
		
		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {
			
			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);
			
			/*
			List<String> romajiList = currentPolishJapaneseEntry.getRomajiList();
			
			for (int romajiListIdx = 0; romajiListIdx < romajiList.size(); ++romajiListIdx) {
				String currentRomaji = romajiList.get(romajiListIdx);
				String prefix = currentPolishJapaneseEntry.getPrefix();
				String kanji = currentPolishJapaneseEntry.getKanji();
				
				if (currentRomaji.startsWith("e ") == true || 
						currentRomaji.startsWith("o ") == true ||
						currentRomaji.startsWith("de ") == true ||
						currentRomaji.startsWith("ni ") == true ||
						currentRomaji.startsWith("wo ") == true ||
						currentRomaji.startsWith("to ") == true) {
					
					if (prefix != null && prefix.equals("") == true) {
						System.out.println((idx + 1) + " - " + kanji);
					}
				}
			}
			*/
			
			/*
			
			String firstKanjiChar = "" + kanji.charAt(0);
			
			if (kanaSet.contains(firstKanjiChar) == true) {
				System.out.println((idx + 1) + " - " + kanji);
			}
			*/
			
			String prefixKana = currentPolishJapaneseEntry.getPrefixKana();
			String prefixRomaji = null;
			
			if (prefixKana == null || prefixKana.equals("") == true) {
				continue;
			}
			
			if (prefixKana.equals("を") == true) {
				prefixRomaji = "wo";
			} else if (prefixKana.equals("お") == true) {
				prefixRomaji = "o";
			} else if (prefixKana.equals("に") == true) {
				prefixRomaji = "ni";
			} else if (prefixKana.equals("へ") == true) {
				prefixRomaji = "he";
			} else if (prefixKana.equals("で") == true) {
				prefixRomaji = "de";
			} else if (prefixKana.equals("と") == true) {
				prefixRomaji = "to";
			} else if (prefixKana.equals("が") == true) {
				prefixRomaji = "ga";
			} else {
				throw new RuntimeException(prefixKana);
			}
			
			currentPolishJapaneseEntry.setPrefixRomaji(prefixRomaji);
			
			String romaji = currentPolishJapaneseEntry.getRomaji();

			romaji = romaji.substring(prefixRomaji.length()).trim();
						
			currentPolishJapaneseEntry.setRomaji(romaji);
		}
		
		CsvReaderWriter.generateCsv(destinationFileNames, polishJapaneseEntries, true);
	}
}
