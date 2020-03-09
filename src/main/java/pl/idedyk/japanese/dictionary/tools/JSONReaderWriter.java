package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class JSONReaderWriter {
	
	public static JSONArray createDictionaryOutputJSON(JMENewDictionary jmeNewDictionary, List<PolishJapaneseEntry> polishJapaneseEntries) {
		
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
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType != DictionaryEntryType.WORD_FEMALE_NAME && dictionaryEntryType != DictionaryEntryType.WORD_MALE_NAME) {
				
				// dodawanie angielskich tlumaczen
				
				List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry);
				
				if (groupEntryList != null && groupEntryList.size() == 1 && JMENewDictionary.isMultiGroup(groupEntryList) == false) { // tylko dla grup pojedynczych
					
					// dodajemy angielskie tlumaczenie
					GroupEntry groupEntry = groupEntryList.get(0);
					
					List<GroupEntryTranslate> englishTranslateList = groupEntry.getTranslateList();
					
					List<String> englishTranslateStringList = new ArrayList<>();
					
					for (GroupEntryTranslate currentEnglishTranslateList : englishTranslateList) {
						
						String englishSingleTranslate = currentEnglishTranslateList.getTranslate();
						
						List<String> englishAdditionalInfoList = currentEnglishTranslateList.getAdditionalInfoList();
						
						//
						
						StringBuffer englishJSONEntryText = new StringBuffer(englishSingleTranslate);
						
						if (englishAdditionalInfoList.size() > 0) {
							
							englishJSONEntryText.append(" (");
							englishJSONEntryText.append(Helper.convertListToString(englishAdditionalInfoList, ", "));
							englishJSONEntryText.append(")");
						}
						
						englishTranslateStringList.add(englishJSONEntryText.toString());
					}
					
					addJSONEntry(jsonEntry, "englishTranslates", englishTranslateStringList);
				}				
			}
			
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
