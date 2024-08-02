package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.common.Helper;

public class KanjiDic2EntryForDictionary extends KanjiDic2Entry {

	private static final long serialVersionUID = 1L;
	
	public String getKanjiDic2RawData() {
		
		List<String> result = new ArrayList<String>();
		
		result.add("Kanji: " + getKanji());
				
		for (String currentEngMeaning : getEngMeaning()) {
			result.add("Meaning: " + currentEngMeaning);
		}
				
		return Helper.convertListToString(result);
	}
}
