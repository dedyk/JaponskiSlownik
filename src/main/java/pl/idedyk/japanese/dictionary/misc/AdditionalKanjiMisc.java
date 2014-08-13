package pl.idedyk.japanese.dictionary.misc;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.AdditionalKanjiEntry;
import pl.idedyk.japanese.dictionary.tools.AdditionalKanjiReaderWriter;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class AdditionalKanjiMisc {

	public static void main(String[] args) throws Exception{
		
		String kradfile = "../JapaneseDictionary_additional/kradfile";
		String kanjidic2 = "../JapaneseDictionary_additional/kanjidic2.xml";
		
		String additionalKanjiFile = "input/additional_kanji.csv";

		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(kradfile);		
		final Map<String, KanjiDic2Entry> kanjiDic2Map = KanjiDic2Reader.readKanjiDic2(kanjidic2, kradFileMap);
		
		List<AdditionalKanjiEntry> additionalKanjiEntryList = AdditionalKanjiReaderWriter.readAdditionalKanjiEntry(additionalKanjiFile);
		
		Collections.sort(additionalKanjiEntryList, new Comparator<AdditionalKanjiEntry>() {

			@Override
			public int compare(AdditionalKanjiEntry o1, AdditionalKanjiEntry o2) {
				
				if (o1.isUseKanji() == false) {
					return -1;
				}

				if (o2.isUseKanji() == false) {
					return 1;
				}
				
				KanjiDic2Entry o1KanjiDic2Entry = kanjiDic2Map.get(o1.getKanji());
				
				if (o1KanjiDic2Entry == null) {
					System.err.println("o1KanjiDic2Entry: " + o1.getKanji());
				}
				
				KanjiDic2Entry o2KanjiDic2Entry = kanjiDic2Map.get(o2.getKanji());

				if (o2KanjiDic2Entry == null) {
					System.err.println("o2KanjiDic2Entry: " + o2.getKanji());
				}
				
				int result = new Integer(o2KanjiDic2Entry.getEngMeaning().size()).compareTo(o1KanjiDic2Entry.getEngMeaning().size());
				
				if (result != 0) {
					return result;
				}
				
				result = new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
				
				return result;
			}
		});
		
		for (int idx = 0; idx < additionalKanjiEntryList.size(); ++idx) {
			
			AdditionalKanjiEntry additionalKanjiEntry = additionalKanjiEntryList.get(idx);
			
			if (additionalKanjiEntry.getDone().equals("1") == true) {
				continue;
			}
			
			System.out.println(additionalKanjiEntry.getId());
		}
	}	
}
