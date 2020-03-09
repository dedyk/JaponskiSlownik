package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	
	public static JSONObject createDictionaryOutputJSON(JMENewDictionary jmeNewDictionary, List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		JSONObject result = new JSONObject();
		
		// naglowek
		Map<String, String> headerMap = new TreeMap<>();
		
		List<JSONObject> jsonEntryListToAdd = new ArrayList<>();
				
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			JSONObject jsonEntry = new JSONObject();
			
			addJSONEntry(headerMap, jsonEntry, "id", polishJapaneseEntry.getId());
			addJSONEntry(headerMap, jsonEntry, "dictionaryEntryTypeList", Helper.convertListToListString(polishJapaneseEntry.getDictionaryEntryTypeList()));
			addJSONEntry(headerMap, jsonEntry, "attributeList", Helper.convertAttributeListToListString(polishJapaneseEntry.getAttributeList()));
			addJSONEntry(headerMap, jsonEntry, "wordType", polishJapaneseEntry.getWordType().toString());
			addJSONEntry(headerMap, jsonEntry, "groups", Helper.convertListToListString(GroupEnum.convertToValues(polishJapaneseEntry.getGroups())));	
			addJSONEntry(headerMap, jsonEntry, "prefixKana", polishJapaneseEntry.getPrefixKana());
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				addJSONEntry(headerMap, jsonEntry, "kanji", polishJapaneseEntry.getKanji());
			}
			
			addJSONEntry(headerMap, jsonEntry, "kana", polishJapaneseEntry.getKana());
			addJSONEntry(headerMap, jsonEntry, "prefixRomaji", polishJapaneseEntry.getPrefixRomaji());
			addJSONEntry(headerMap, jsonEntry, "romaji", polishJapaneseEntry.getRomaji());
			addJSONEntry(headerMap, jsonEntry, "translates", polishJapaneseEntry.getTranslates());
			addJSONEntry(headerMap, jsonEntry, "info", polishJapaneseEntry.getInfo());
			addJSONEntry(headerMap, jsonEntry, "parseAdditionalInfoList", Helper.convertListToListString(polishJapaneseEntry.getParseAdditionalInfoList()));
			addJSONEntry(headerMap, jsonEntry, "exampleSentenceGroupIdsList", Helper.convertListToListString(polishJapaneseEntry.getExampleSentenceGroupIdsList()));
			
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
					
					addJSONEntry(headerMap, jsonEntry, "englishTranslates", englishTranslateStringList);
				}	
				
				jsonEntryListToAdd.add(jsonEntry);
			}
			
			/*								
			
			if (addJmedictRawData == true) {
				csvWriter.write(Helper.convertListToString(polishJapaneseEntry.getJmedictRawDataList()));
			}
			
			csvWriter.endRecord();
			*/
		}
		
		// dodajemy naglowek
		Iterator<Entry<String, String>> headerMapIterator = headerMap.entrySet().iterator();
		
		JSONObject headerJSONObject = new JSONObject();
		
		while(headerMapIterator.hasNext() == true) {
			
			Entry<String, String> entry = headerMapIterator.next();
			
			headerJSONObject.put(entry.getKey(), entry.getValue());			
		}		
		
		//
		
		JSONArray wordsJSONArray = new JSONArray();
		
		for (JSONObject jsonEntry : jsonEntryListToAdd) {
			wordsJSONArray.put(jsonEntry);
		}		
		
		result.put("header", headerJSONObject);
		result.put("words", wordsJSONArray);
				
		return result;
	}
	
	private static void addHeaderField(Map<String, String> headerMap, String fieldName) {
		
		char generatedFieldName = (char)('a' + headerMap.size());
		
		headerMap.put(fieldName, String.valueOf(generatedFieldName));
	}
	
	private static void addJSONEntry(Map<String, String> headerMap, JSONObject jsonEntry, String key, Object data) {
		
		if (data == null) {
			return;
		}
		
		String fieldName = headerMap.get(key);
		
		if (fieldName == null) {
			addHeaderField(headerMap, key);
			
			fieldName = headerMap.get(key);
		}
		
		if (data instanceof String == true) {
			
			String string = (String)data;
			
			if (StringUtils.isEmpty(string) == true) {
				return;
			}
			
			jsonEntry.put(fieldName, string);			
			
		} else if (data instanceof Integer == true) {
			
			jsonEntry.put(fieldName, (Integer)data);
			
		} else if (data instanceof List == true) { 
			
			List<?> list = (List<?>)data;
			
			if (list.size() == 0) {
				return;
			}
			
			jsonEntry.put(fieldName, list);
			
		} else {
			throw new RuntimeException(data.getClass().toString());
		}		
	}
	
	public static void writeJSONToFile(File outputFile, JSONObject json) {
		
        try (FileWriter file = new FileWriter(outputFile)) {
        	 
            file.write(json.toString(1));
            
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
}
