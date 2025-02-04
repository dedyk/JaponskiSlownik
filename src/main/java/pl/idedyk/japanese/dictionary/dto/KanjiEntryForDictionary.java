package pl.idedyk.japanese.dictionary.dto;

import pl.idedyk.japanese.dictionary.api.dto.KanjiEntry;

public class KanjiEntryForDictionary extends KanjiEntry {

	private static final long serialVersionUID = 1L;
	
	private String kanjiDic2RawData;
	
	public String getKanjiDic2RawData() {
		return kanjiDic2RawData;
	}

	public void setKanjiDic2RawDataList(String kanjiDic2RawData) {
		this.kanjiDic2RawData = kanjiDic2RawData;
	}
}
