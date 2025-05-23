package pl.idedyk.japanese.kanji2.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.Kanjidic2;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroup;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.ReadingMeaningInfoReadingMeaningGroupMeaning;

public class GetKanjiWithEmptySense {
	
	public static void main(String[] args) throws Exception {
		
		// wczytywanie pomocnika slownikowego
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		Kanjidic2 englishKanjidic2 = kanji2Helper.getKanjidic2();
		
		// sprawdzenie, ktory znak ma puste znaczenie
		for (KanjiCharacterInfo kanjiCharacterInfo : englishKanjidic2.getCharacterList()) {
			
			boolean isEmptySense = true;
			
			ReadingMeaningInfo readingMeaning = kanjiCharacterInfo.getReadingMeaning();
			
			if (readingMeaning != null) {
				ReadingMeaningInfoReadingMeaningGroup readingMeaningGroup = readingMeaning.getReadingMeaningGroup();
				
				if (readingMeaningGroup != null) {
					List<ReadingMeaningInfoReadingMeaningGroupMeaning> meaningList = readingMeaningGroup.getMeaningList();
					
					isEmptySense = meaningList.size() == 0;					
				}				
			}
			
			if (isEmptySense == true) {
				System.out.println("Puste znaczenia: " + kanjiCharacterInfo.getKanji());
			}
		}
		
	}
}
