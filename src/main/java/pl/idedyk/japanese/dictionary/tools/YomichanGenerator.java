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
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class YomichanGenerator {
	
	private static Map<DictionaryEntryType, DefinitionTag> dictionaryEntryTypeToDefinitionTagMap = new TreeMap<DictionaryEntryType, DefinitionTag>() {
		
		private static final long serialVersionUID = 1L;

		{
			put(DictionaryEntryType.WORD_NOUN, new DefinitionTag("rz", 0));	
			
			put(DictionaryEntryType.WORD_VERB_U, new DefinitionTag("u-cz", 0));
			put(DictionaryEntryType.WORD_VERB_RU, new DefinitionTag("ru-cz", 0));
			put(DictionaryEntryType.WORD_VERB_IRREGULAR, new DefinitionTag("ir-cz", 0));

			put(DictionaryEntryType.WORD_ADJECTIVE_I, new DefinitionTag("i-prz", 0));
			put(DictionaryEntryType.WORD_ADJECTIVE_NA, new DefinitionTag("na-prz", 0));
			
			put(DictionaryEntryType.WORD_TEMPORAL_NOUN, new DefinitionTag("rz-cza", 1));
			put(DictionaryEntryType.WORD_ADJECTIVE_NO, new DefinitionTag("rz-no", 1));
			put(DictionaryEntryType.WORD_ADJECTIVE_F, new DefinitionTag("rz-pr", 1));

			put(DictionaryEntryType.WORD_EXPRESSION, new DefinitionTag("wyr", 1));
			
			put(DictionaryEntryType.WORD_CONJUNCTION, new DefinitionTag("spó", 2));			
			put(DictionaryEntryType.WORD_INTERJECTION, new DefinitionTag("wyk", 2));
			
			put(DictionaryEntryType.WORD_PARTICULE, new DefinitionTag("par", 2));
			
			put(DictionaryEntryType.WORD_PRONOUN, new DefinitionTag("zai", 2));
			
			put(DictionaryEntryType.WORD_ADVERB, new DefinitionTag("przy", 2));
			put(DictionaryEntryType.WORD_ADVERB_TO, new DefinitionTag("przy-to", 2));

			put(DictionaryEntryType.WORD_NOUN_SUFFIX, new DefinitionTag("rz-suf", 3));
			put(DictionaryEntryType.WORD_NOUN_PREFIX, new DefinitionTag("rz-pre", 3));
			
			put(DictionaryEntryType.WORD_PREFIX, new DefinitionTag("pre", 3));
			put(DictionaryEntryType.WORD_SUFFIX, new DefinitionTag("suf", 3));

			put(DictionaryEntryType.WORD_NUMBER, new DefinitionTag("lic", 4));
			
			put(DictionaryEntryType.WORD_COUNTER, new DefinitionTag("kla", 4));
			
			put(DictionaryEntryType.WORD_AUX_ADJECTIVE_I, new DefinitionTag("i-przy-pom", 5));
			
			put(DictionaryEntryType.WORD_ADVERBIAL_NOUN, new DefinitionTag("rz-prz", 6));
			put(DictionaryEntryType.WORD_PRE_NOUN_ADJECTIVAL, new DefinitionTag("pre-rz-prz", 6));
			
			put(DictionaryEntryType.WORD_AUX, new DefinitionTag("aux", 6));
			
			put(DictionaryEntryType.WORD_ADJECTIVE_KU, new DefinitionTag("ku-prz", 7));
			put(DictionaryEntryType.WORD_ADJECTIVE_TARU, new DefinitionTag("tar-prz", 7));
			put(DictionaryEntryType.WORD_ADJECTIVE_NARI, new DefinitionTag("nar-prz", 7));
			put(DictionaryEntryType.WORD_ADJECTIVE_SHIKU, new DefinitionTag("shi-prz", 7));

			put(DictionaryEntryType.WORD_VERB_ZURU, new DefinitionTag("zur-cz", 8));
			put(DictionaryEntryType.WORD_VERB_AUX, new DefinitionTag("cz-pom", 8));
			put(DictionaryEntryType.WORD_NIDAN_VERB, new DefinitionTag("nid-cz", 8));
			
			put(DictionaryEntryType.WORD_COPULA_DA, new DefinitionTag("kop", 9));
			
			put(DictionaryEntryType.WORD_PROPER_NOUN, new DefinitionTag("naz-wl", 10));
			
			put(DictionaryEntryType.WORD_NAME, new DefinitionTag("im", 10));
			
			put(DictionaryEntryType.WORD_MALE_NAME, new DefinitionTag("me-im", 11));
			put(DictionaryEntryType.WORD_FEMALE_NAME, new DefinitionTag("rz-im", 11));
			
			put(DictionaryEntryType.WORD_SURNAME_NAME, new DefinitionTag("nazw", 12));
			
			put(DictionaryEntryType.WORD_PERSON, new DefinitionTag("oso", 13));
			
			put(DictionaryEntryType.WORD_WORK, new DefinitionTag("dzie", 14));
			
			put(DictionaryEntryType.WORD_PLACE, new DefinitionTag("miej", 15));
			
			put(DictionaryEntryType.WORD_STATION_NAME, new DefinitionTag("sta", 16));
			
			put(DictionaryEntryType.WORD_COMPANY_NAME, new DefinitionTag("fir", 17));
			
			put(DictionaryEntryType.WORD_ORGANIZATION_NAME, new DefinitionTag("org", 18));
			
			put(DictionaryEntryType.WORD_PRODUCT_NAME, new DefinitionTag("prod", 19));
			
			put(DictionaryEntryType.WORD_UNCLASS_NAME, new DefinitionTag("nies", 20));
						
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
	
	public static void generate(List<PolishJapaneseEntry> polishJapaneseEntriesList, List<PolishJapaneseEntry> namesList, String outputDir) {
				
		// generowanie indeksu
		generateAndSaveIndex(outputDir);
		
		// generowanie slow (term_bank)
		List<List<TermBankEntry>> termBankListList = new ArrayList<>();
		
		generateTermBank(termBankListList, polishJapaneseEntriesList, false);
		generateTermBank(termBankListList, namesList, true);
		
		saveTermBank(outputDir, termBankListList);
		
		// generowanie tagow (tag_bank)
		generateAndSaveTagBank(outputDir);
		
	}

	private static void generateAndSaveIndex(String outputDir) {
				
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
	
	private static void generateTermBank(List<List<TermBankEntry>> termBankListList, List<PolishJapaneseEntry> polishJapaneseEntriesList, boolean names) {
		
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
			{
				boolean isCommon = false;
				boolean isArchaism = false;
				boolean isObscure = false;
				boolean isObsolete = false;
				
				//
				
				AttributeList attributeList = polishJapaneseEntry.getAttributeList();
				
				if (attributeList != null) {
					
					List<Attribute> attributeListList = attributeList.getAttributeList();
					
					if (attributeListList != null && attributeListList.size() > 0) {
						
						for (Attribute attribute : attributeListList) {
														
							if (attribute.getAttributeType() == AttributeType.COMMON_WORD) {
								isCommon = true;
							}
							
							if (attribute.getAttributeType() == AttributeType.ARCHAISM) {
								isArchaism = true;
							}
							
							if (attribute.getAttributeType() == AttributeType.OBSCURE) {
								isObscure = true;
							}
							
							if (attribute.getAttributeType() == AttributeType.OBSOLETE) {
								isObsolete = true;
							}
						}
					}
				}
				
				if (isCommon == true) {
					termBankEntry.setPopularity(0);
					
				} else if (isObscure == true) {
					termBankEntry.setPopularity(-2);
				
				} else if (isObsolete == true) {
					termBankEntry.setPopularity(-3);
				
				} else if (isArchaism == true) {
					termBankEntry.setPopularity(-4);
					
				} else {
					
					if (names == false) {
						termBankEntry.setPopularity(-1);
						
					} else {
						termBankEntry.setPopularity(-5);						
					}
				}					
			}
			
			// generowanie sequenceNumber
			termBankEntry.setSequenceNumber(names == false ? polishJapaneseEntry.getId() : 1000000 + polishJapaneseEntry.getId());
			
			// generowanie termTag
			// noop
			
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
	}
	
	private static void saveTermBank(String outputDir, List<List<TermBankEntry>> termBankListList) {
		
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
				throw new RuntimeException("Unknown value: " + dictionaryEntryType);
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

	private static void generateAndSaveTagBank(String outputDir) {
				
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

		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		//
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();		

		Helper.generateAdditionalInfoFromEdict(wordGeneratorHelper.getJMENewDictionary(), jmedictCommon, polishJapaneseEntriesList);

		//
		
		List<PolishJapaneseEntry> namesList;
		
		{
			JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
			List<JMEDictNewNativeEntry> jmedictNameNativeList = jmedictNewReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
			
			JMENewDictionary jmeNewNameDictionary = jmedictNewReader.createJMENewDictionary(jmedictNameNativeList);
			
			namesList = Helper.generateNames(jmeNewNameDictionary);
		}
		
		generate(polishJapaneseEntriesList, namesList, "/tmp/a");		
	}
}
