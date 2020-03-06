package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;

public class JSONReaderWriter {
	
	public static JSONArray createDictionaryOutputJSON(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		JSONArray result = new JSONArray();
				
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			JSONObject jsonEntry = new JSONObject();
			
			addJSONEntry(jsonEntry, "id", polishJapaneseEntry.getId());
			addJSONEntry(jsonEntry, "dictionaryEntryTypeList", Helper.convertListToListString(polishJapaneseEntry.getDictionaryEntryTypeList()));
			addJSONEntry(jsonEntry, "attributeList", Helper.convertAttributeListToListString(polishJapaneseEntry.getAttributeList()));
			addJSONEntry(jsonEntry, "wordType", polishJapaneseEntry.getWordType().toString());
			addJSONEntry(jsonEntry, "groups", Helper.convertListToListString(GroupEnum.convertToValues(polishJapaneseEntry.getGroups())));	
			addJSONEntry(jsonEntry, "prefixKana", polishJapaneseEntry.getPrefixKana());
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				addJSONEntry(jsonEntry, "kanji", polishJapaneseEntry.getKanji());
			}
			
			addJSONEntry(jsonEntry, "kana", polishJapaneseEntry.getKana());
			addJSONEntry(jsonEntry, "prefixRomaji", polishJapaneseEntry.getPrefixRomaji());
			addJSONEntry(jsonEntry, "romaji", polishJapaneseEntry.getRomaji());
			addJSONEntry(jsonEntry, "translates", polishJapaneseEntry.getTranslates());
			addJSONEntry(jsonEntry, "info", polishJapaneseEntry.getInfo());
			addJSONEntry(jsonEntry, "parseAdditionalInfoList", Helper.convertListToListString(polishJapaneseEntry.getParseAdditionalInfoList()));
			addJSONEntry(jsonEntry, "exampleSentenceGroupIdsList", Helper.convertListToListString(polishJapaneseEntry.getExampleSentenceGroupIdsList()));
			
			/*								
			
			if (addJmedictRawData == true) {
				csvWriter.write(Helper.convertListToString(polishJapaneseEntry.getJmedictRawDataList()));
			}
			
			csvWriter.endRecord();
			*/
			
			
			result.put(jsonEntry);
		}
				
		return result;
	}
	
	private static void addJSONEntry(JSONObject jsonEntry, String key, Object data) {
		
		if (data == null) {
			return;
		}
		
		if (data instanceof String == true) {
			
			String string = (String)data;
			
			if (StringUtils.isEmpty(string) == true) {
				return;
			}
			
			jsonEntry.put(key, string);			
			
		} else if (data instanceof Integer == true) {
			
			jsonEntry.put(key, (Integer)data);
			
		} else if (data instanceof List == true) { 
			
			List<?> list = (List<?>)data;
			
			if (list.size() == 0) {
				return;
			}
			
			jsonEntry.put(key, list);
			
		} else {
			throw new RuntimeException(data.getClass().toString());
		}		
	}
	
	public static void writeJSONArrayToFile(File outputFile, JSONArray jsonArray) {
		
        try (FileWriter file = new FileWriter(outputFile)) {
        	 
            file.write(jsonArray.toString(1));
            
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
}
