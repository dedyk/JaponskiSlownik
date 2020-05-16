package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class YomichanGenerator {
	
	public static void generate(List<PolishJapaneseEntry> polishJapaneseEntriesList, String outputDir) {
		
		int fixme = 1;
		// kanji
		// slownik nazw
		
		// generowanie indeksu
		generateIndex(outputDir);
		
		// generowanie slow (term_bank)
		generateTermBank(outputDir, polishJapaneseEntriesList);
		
		// generowanie tagow (tag_bank)
		generateTagBank(outputDir);
		
	}

	private static void generateIndex(String outputDir) {
				
		JSONObject indexJSON = new JSONObject();

		indexJSON.put("title", "Mały skromny japoński słownik");
		indexJSON.put("format", 3);
				
		indexJSON.put("revision", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		indexJSON.put("sequenced", true);
		indexJSON.put("author", "Fryderyk Mazurek");
		indexJSON.put("url", "https://www.japonski-pomocnik.pl");
		indexJSON.put("description", "Mały skromny japoński słownik");
		
		// zapis
		writeJSONObjectToFile(new File(outputDir, "index.json"), indexJSON);		
	}
	
	private static void generateTermBank(String outputDir, List<PolishJapaneseEntry> polishJapaneseEntriesList) {
				
		List<List<TermBankEntry>> termBankListList = new ArrayList<>();
		
		List<TermBankEntry> currentTermBankList = null;
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			TermBankEntry termBankEntry = new TermBankEntry();
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				
				termBankEntry.setKanji(polishJapaneseEntry.getKanji());
				termBankEntry.setKana(polishJapaneseEntry.getKana());
				
			} else {
				
				termBankEntry.setKanji(polishJapaneseEntry.getKana());
				termBankEntry.setKana("");				
			}
						
			
			int fixme = 1;
			// skróty !!!!!!!!!!!!			
			/*
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeList) {				
				termBankEntry.addDdefinitionTag(dictionaryEntryType.getName());				
			}
			
			AttributeList attributeList = polishJapaneseEntry.getAttributeList();
			
			if (attributeList != null) {
				
				List<Attribute> attributeListList = attributeList.getAttributeList();
				
				if (attributeListList != null && attributeListList.size() > 0) {
					
					for (Attribute attribute : attributeListList) {
						
						if (attribute.getAttributeType().isShow() == true) {
							termBankEntry.addDdefinitionTag(attribute.getAttributeType().getName());
						}
					}
				}				
			}
			*/
			
			// generowanie inflectedTags
			int fixme2 = 1;
			/*
			v1: ichidan verb; -> RU_VERB
			v5: godan verb; -> U_VERB
			vs: suru verb; -> SURU
			vk: kuru verb; -> KURU
			adj-i: i-adjective -> i-przymiotniki
			*/
			
			// generowanie popularity
			int fixme3 = 1;
			
			// generowanie sequenceNumber
			int fixme4 = 1;
			termBankEntry.setSequenceNumber(polishJapaneseEntry.getId());
			
			// generowanie termTag
			int fixme5 = 5;
			
			//
			
			List<String> translates = polishJapaneseEntry.getTranslates();
			
			for (String currentTranslate : translates) {				
				termBankEntry.addTranslate(currentTranslate);				
			}
			
			if (polishJapaneseEntry.getInfo() != null && polishJapaneseEntry.getInfo().length() > 0) {
				termBankEntry.addTranslate("Informacja dodatkowa: " + polishJapaneseEntry.getInfo());
			}
			
			
			//
			
			if (currentTermBankList == null) {
				currentTermBankList = new ArrayList<>();
			}
			
			currentTermBankList.add(termBankEntry);
			
			if (currentTermBankList.size() >= 10000) {
				termBankListList.add(currentTermBankList);
				
				currentTermBankList = null;
			}			
		}
		
		if (currentTermBankList != null && currentTermBankList.size() > 0) {
			
			termBankListList.add(currentTermBankList);
			
			currentTermBankList = null;
		}		

		//
		
		int bankNo = 1;
		
		for (List<TermBankEntry> currentTermBankListtoSave : termBankListList) {
			
			JSONArray termBankListJSONArray = new JSONArray();
			
			for (TermBankEntry termBankEntry : currentTermBankListtoSave) {
				
				JSONArray termBankEntryJSONArray = new JSONArray();
				
				//
				
				termBankEntryJSONArray.put(termBankEntry.getKanji());
				termBankEntryJSONArray.put(termBankEntry.getKana());
				termBankEntryJSONArray.put(termBankEntry.getDefinitionTagsAsString());
				termBankEntryJSONArray.put(termBankEntry.getInflectedTagsAsString());
				termBankEntryJSONArray.put(termBankEntry.getPopularity());
				termBankEntryJSONArray.put(termBankEntry.getTranslates());
				termBankEntryJSONArray.put(termBankEntry.getSequenceNumber());
				termBankEntryJSONArray.put(termBankEntry.getTermTagsAsString());
				
				//
				
				termBankListJSONArray.put(termBankEntryJSONArray);
			}			
			
			// zapisanie termBan
			writeJSONArrayToFile(new File(outputDir, "term_bank_" + bankNo + ".json"), termBankListJSONArray);
			
			bankNo++;
		}
	}
	
	private static void generateTagBank(String outputDir) {
		
		int fixme = 1;
		
		JSONArray tagBankJSONArray = new JSONArray();
		
		writeJSONArrayToFile(new File(outputDir, "tag_bank_1.json"), tagBankJSONArray);
	}
	
	private static void writeJSONArrayToFile(File outputFile, JSONArray jsonArray) {
		
        try (FileWriter file = new FileWriter(outputFile)) {
        	 
            file.write(jsonArray.toString(1));
            
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}

	private static void writeJSONObjectToFile(File outputFile, JSONObject jsonObject) {
		
        try (FileWriter file = new FileWriter(outputFile)) {
        	 
            file.write(jsonObject.toString(1));
            
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
	
	private static class TermBankEntry {
		
		private String kanji;
		
		private String kana;
		
		private List<String> definitionTags;
		
		private List<String> inflectedTags;
		
		private int popularity;
		
		private List<String> translates;
		
		private int sequenceNumber;
		
		private List<String> termTags;
		
		//

		public String getKanji() {
			return kanji;
		}

		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public String getKana() {
			return kana;
		}

		public void setKana(String kana) {
			this.kana = kana;
		}		
		
		public void addDdefinitionTag(String tag) {
						
			if (definitionTags == null) {
				definitionTags = new ArrayList<>();
			}
			
			definitionTags.add(tag);			
		}
		
		public String getDefinitionTagsAsString() {
			
			if (definitionTags == null) {
				definitionTags = new ArrayList<>();
			}

			return getListAsString(definitionTags);
		}

		public String getInflectedTagsAsString() {
			
			if (inflectedTags == null) {
				inflectedTags = new ArrayList<>();
			}

			return getListAsString(inflectedTags);
		}
		
		public int getPopularity() {
			return popularity;
		}

		public void setPopularity(int popularity) {
			this.popularity = popularity;
		}
		
		public void addTranslate(String translate) {
			
			if (translates == null) {
				translates = new ArrayList<>();
			}
			
			translates.add(translate);			
		}

		public List<String> getTranslates() {
			return translates;
		}

		public int getSequenceNumber() {
			return sequenceNumber;
		}

		public void setSequenceNumber(int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
		}
		
		public String getTermTagsAsString() {
			
			if (termTags == null) {
				termTags = new ArrayList<>();
			}

			return getListAsString(termTags);
		}

		private String getListAsString(List<String> list) {
			
			StringBuffer result = new StringBuffer();
			
			for (String currentListValue : list) {
				
				if (result.length() > 0) {
					result.append(" ");
				}
				
				result.append(currentListValue);
			}			
			
			return result.toString();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();

		generate(polishJapaneseEntriesList, "/tmp/a");		
	}
}
