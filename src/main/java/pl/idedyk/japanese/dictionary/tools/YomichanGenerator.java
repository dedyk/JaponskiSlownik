package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class YomichanGenerator {
	
	private static Map<DictionaryEntryType, DefinitionTag> dictionaryEntryTypeToDefinitionTagMap = new TreeMap<DictionaryEntryType, DefinitionTag>() {		
		private static final long serialVersionUID = 1L;

		{
			put(DictionaryEntryType.WORD_NOUN, new DefinitionTag("rz", 0));			
			put(DictionaryEntryType.WORD_TEMPORAL_NOUN, new DefinitionTag("rz-cza", 1));
			
			put(DictionaryEntryType.WORD_NOUN_SUFFIX, new DefinitionTag("rz-suf", 1));
			put(DictionaryEntryType.WORD_NOUN_PREFIX, new DefinitionTag("rz-pre", 1));
			
			put(DictionaryEntryType.WORD_NAME, new DefinitionTag("im", 2));
			put(DictionaryEntryType.WORD_MALE_NAME, new DefinitionTag("me-im", 2));
			put(DictionaryEntryType.WORD_FEMALE_NAME, new DefinitionTag("rz-im", 2));
			
			put(DictionaryEntryType.WORD_SURNAME_NAME, new DefinitionTag("nazw", 2));
						
			put(DictionaryEntryType.WORD_PROPER_NOUN, new DefinitionTag("naz-wl", 2));
			
			put(DictionaryEntryType.WORD_VERB_U, new DefinitionTag("u-cz", 0));
			put(DictionaryEntryType.WORD_VERB_RU, new DefinitionTag("ru-cz", 0));
			put(DictionaryEntryType.WORD_VERB_IRREGULAR, new DefinitionTag("ir-cz", 0));
			put(DictionaryEntryType.WORD_VERB_ZURU, new DefinitionTag("zur-cz", 3));
			put(DictionaryEntryType.WORD_VERB_AUX, new DefinitionTag("cz-pom", 2));
			
			put(DictionaryEntryType.WORD_ADJECTIVE_I, new DefinitionTag("i-prz", 0));
			put(DictionaryEntryType.WORD_AUX_ADJECTIVE_I, new DefinitionTag("i-przy-pom", 2));
			put(DictionaryEntryType.WORD_ADJECTIVE_NA, new DefinitionTag("na-prz", 0));
			put(DictionaryEntryType.WORD_ADJECTIVE_NO, new DefinitionTag("rz-no", 1));
			put(DictionaryEntryType.WORD_ADJECTIVE_F, new DefinitionTag("rz-pr", 1));
			put(DictionaryEntryType.WORD_ADJECTIVE_KU, new DefinitionTag("ku-prz", 2));
			put(DictionaryEntryType.WORD_ADJECTIVE_TARU, new DefinitionTag("tar-prz", 2));
			put(DictionaryEntryType.WORD_ADJECTIVE_NARI, new DefinitionTag("nar-prz", 2));
			put(DictionaryEntryType.WORD_ADJECTIVE_SHIKU, new DefinitionTag("shi-prz", 2));
			
			put(DictionaryEntryType.WORD_EXPRESSION, new DefinitionTag("wyr", 1));
			
			put(DictionaryEntryType.WORD_ADVERB, new DefinitionTag("przy", 0));
			put(DictionaryEntryType.WORD_ADVERB_TO, new DefinitionTag("przy-to", 1));
			put(DictionaryEntryType.WORD_ADVERBIAL_NOUN, new DefinitionTag("rz-prz", 2));
			
			put(DictionaryEntryType.WORD_PRE_NOUN_ADJECTIVAL, new DefinitionTag("pre-rz-prz", 2));
			
			put(DictionaryEntryType.WORD_PRONOUN, new DefinitionTag("zai", 1));
			
			put(DictionaryEntryType.WORD_PARTICULE, new DefinitionTag("par", 2));
			
			put(DictionaryEntryType.WORD_PREFIX, new DefinitionTag("pre", 2));
			put(DictionaryEntryType.WORD_SUFFIX, new DefinitionTag("suf", 2));
			
			put(DictionaryEntryType.WORD_AUX, new DefinitionTag("aux", 2));
			
			put(DictionaryEntryType.WORD_CONJUNCTION, new DefinitionTag("spó", 2));
			
			put(DictionaryEntryType.WORD_INTERJECTION, new DefinitionTag("wyk", 2));
			
			put(DictionaryEntryType.WORD_COUNTER, new DefinitionTag("kla", 2));
			
			put(DictionaryEntryType.WORD_NIDAN_VERB, new DefinitionTag("nid-cz", 3));
			
			put(DictionaryEntryType.WORD_NUMBER, new DefinitionTag("lic", 2));
			
			put(DictionaryEntryType.WORD_COPULA_DA, new DefinitionTag("kop", 3));
			
			put(DictionaryEntryType.WORD_EMPTY, new DefinitionTag("", 999));
			put(DictionaryEntryType.UNKNOWN, new DefinitionTag("", 999));
		}
	};
	
	private static Map<AttributeType, DefinitionTag> attributeTypeToDefinitionTagMap = new TreeMap<AttributeType, DefinitionTag>() {		
		private static final long serialVersionUID = 1L;

		{
			put(AttributeType.SURU_VERB, new DefinitionTag("suru", 10));
			
			put(AttributeType.COMMON_WORD, new DefinitionTag("pow-uz", 11));
			
			put(AttributeType.VERB_TRANSITIVITY, new DefinitionTag("cz-prz", 12));
			put(AttributeType.VERB_INTRANSITIVITY, new DefinitionTag("cz-nprz", 12));

			put(AttributeType.KANJI_ALONE, new DefinitionTag("kanj-sam", 12));
			put(AttributeType.KANA_ALONE, new DefinitionTag("kana-sam", 12));

			put(AttributeType.ATEJI, new DefinitionTag("ate", 13));
			
			put(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD, new DefinitionTag("ono", 14));
			
			put(AttributeType.VERB_KEIGO_HIGH, new DefinitionTag("hon-wyw", 15));
			put(AttributeType.VERB_KEIGO_LOW, new DefinitionTag("hon-unż", 15));
			
			put(AttributeType.OBSCURE, new DefinitionTag("mał-zna", 16));
			
			put(AttributeType.ARCHAISM, new DefinitionTag("arch", 17));
			put(AttributeType.OBSOLETE, new DefinitionTag("przes", 17));
		}
	};
	
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
						
			// generowanie definitionTag
			generateDefinitionTag(polishJapaneseEntry, termBankEntry);			
			
			// generowanie inflectedTags
			{
				List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
				
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ADJECTIVE_I) == true) {
					termBankEntry.addInflectedTags("adj-i");
				}
				
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true) {
					termBankEntry.addInflectedTags("v5");
				}

				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true) {
					termBankEntry.addInflectedTags("v1");
				}
				
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true) {
					
					if (polishJapaneseEntry.getKana().endsWith("する") == true) {
						termBankEntry.addInflectedTags("vs");
						
					} else if (polishJapaneseEntry.getKana().endsWith("くる") == true) {
						termBankEntry.addInflectedTags("vk");
						
					} else {
						throw new RuntimeException();
					}
				}
			}
			
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
	
	private static void generateDefinitionTag(PolishJapaneseEntry polishJapaneseEntry, TermBankEntry termBankEntry) {
		
		List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
		
		for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeList) {	
			
			DefinitionTag tag = dictionaryEntryTypeToDefinitionTagMap.get(dictionaryEntryType);
			
			if (tag != null && tag.getTag().length() > 0) {
				termBankEntry.addDefinitionTag(tag.getTag());
				
			} else if (tag == null) {				
				throw new RuntimeException("Unknown value: " + dictionaryEntryTypeToDefinitionTagMap);
			}
		}
		
		AttributeList attributeList = polishJapaneseEntry.getAttributeList();
		
		if (attributeList != null) {
			
			List<Attribute> attributeListList = attributeList.getAttributeList();
			
			if (attributeListList != null && attributeListList.size() > 0) {
				
				for (Attribute attribute : attributeListList) {
					
					if (attribute.getAttributeType().isShow() == true) {
						
						DefinitionTag tag = attributeTypeToDefinitionTagMap.get(attribute.getAttributeType());
						
						if (tag != null && tag.getTag().length() > 0) {
							termBankEntry.addDefinitionTag(tag.getTag());
							
						} else if (tag == null) {				
							throw new RuntimeException("Unknown value: " + dictionaryEntryTypeToDefinitionTagMap);
						}
					}
				}
			}				
		}
	}

	private static void generateTagBank(String outputDir) {
				
		JSONArray tagBankJSONArray = new JSONArray();
		
		Set<String> alreadyUsedTagsName = new TreeSet<>();
		
		Iterator<Entry<DictionaryEntryType, DefinitionTag>> dictionaryEntryTypeToDefinitionTagMapIterator = dictionaryEntryTypeToDefinitionTagMap.entrySet().iterator();
		
		while (dictionaryEntryTypeToDefinitionTagMapIterator.hasNext() == true) {
			
			Entry<DictionaryEntryType, DefinitionTag> dictionaryEntryTypeToDefinitionTagMapEntry = dictionaryEntryTypeToDefinitionTagMapIterator.next();
			
			//
			
			if (dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getTag().length() == 0) {
				continue;
			}
			
			if (alreadyUsedTagsName.contains(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getTag()) == true) {				
				throw new RuntimeException(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getTag());
			}

			alreadyUsedTagsName.add(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getTag());
			
			//
			
			JSONArray tagBankEntryJSONArray = createTagBankJSONArray(dictionaryEntryTypeToDefinitionTagMapEntry.getValue(), dictionaryEntryTypeToDefinitionTagMapEntry.getKey().getName());
						
			//
			
			tagBankJSONArray.put(tagBankEntryJSONArray);				
		}
		
		//
		
		Iterator<Entry<AttributeType, DefinitionTag>> attributeTypeToDefinitionTagMapIterator = attributeTypeToDefinitionTagMap.entrySet().iterator();
		
		while (attributeTypeToDefinitionTagMapIterator.hasNext() == true) {
			
			Entry<AttributeType, DefinitionTag> attributeTypeToDefinitionTagMapEntry = attributeTypeToDefinitionTagMapIterator.next();
			
			//
			
			if (attributeTypeToDefinitionTagMapEntry.getValue().getTag().length() == 0) {
				continue;
			}
			
			if (alreadyUsedTagsName.contains(attributeTypeToDefinitionTagMapEntry.getValue().getTag()) == true) {				
				throw new RuntimeException(attributeTypeToDefinitionTagMapEntry.getValue().getTag());
			}

			alreadyUsedTagsName.add(attributeTypeToDefinitionTagMapEntry.getValue().getTag());
			
			//
			
			JSONArray tagBankEntryJSONArray = createTagBankJSONArray(attributeTypeToDefinitionTagMapEntry.getValue(), attributeTypeToDefinitionTagMapEntry.getKey().getName());
						
			//
			
			tagBankJSONArray.put(tagBankEntryJSONArray);				
		}		
		
		writeJSONArrayToFile(new File(outputDir, "tag_bank_1.json"), tagBankJSONArray);
	}
	
	private static JSONArray createTagBankJSONArray(DefinitionTag tag, String description) {
		
		JSONArray tagBankEntryJSONArray = new JSONArray();
		
		tagBankEntryJSONArray.put(tag.getTag());
		tagBankEntryJSONArray.put("");
		tagBankEntryJSONArray.put(tag.getSortingOrder());
		tagBankEntryJSONArray.put(description);
		tagBankEntryJSONArray.put(0);
		
		return tagBankEntryJSONArray;
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
		
		public void addDefinitionTag(String tag) {
						
			if (definitionTags == null) {
				definitionTags = new ArrayList<>();
			}
			
			if (definitionTags.contains(tag) == false) {
				definitionTags.add(tag);
			}
		}
		
		public String getDefinitionTagsAsString() {
			
			if (definitionTags == null) {
				definitionTags = new ArrayList<>();
			}

			return getListAsString(definitionTags);
		}
		
		public void addInflectedTags(String tag) {
			
			if (inflectedTags == null) {
				inflectedTags = new ArrayList<>();
			}
			
			inflectedTags.add(tag);			
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
	
	private static class DefinitionTag {
		
		private String tag;
		
		private int sortingOrder;
		
		public DefinitionTag(String tag, int sortingOrder) {
			this.tag = tag;
			this.sortingOrder = sortingOrder;
		}

		public String getTag() {
			return tag;
		}

		public int getSortingOrder() {
			return sortingOrder;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();

		generate(polishJapaneseEntriesList, "/tmp/a");		
	}
}
