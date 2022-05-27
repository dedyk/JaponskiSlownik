package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.RelativePriorityEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class YomichanGenerator {
	
	private static enum DefinitionTagCommonDef {

		noun(new DefinitionTag("rz", 0)),
		nounTemporal(new DefinitionTag("rz-cza", 1)),
		nounPrefix(new DefinitionTag("rz-pre", 3)),
		nounSuffix(new DefinitionTag("rz-suf", 3)),
		nounAdverbial(new DefinitionTag("rz-prz", 6)),
		preNounAdverbial(new DefinitionTag("pre-rz-prz", 6)),
		properNoun(new DefinitionTag("naz-wl", 10)),
		
		u_verb(new DefinitionTag("u-cz", 0)),		
		ru_verb(new DefinitionTag("ru-cz", 0)),		
		ir_verb(new DefinitionTag("ir-cz", 0)),
		
		aux_verb(new DefinitionTag("cz-pom", 8)),
		zuru_verb(new DefinitionTag("zur-cz", 8)),						
		nidan_verb(new DefinitionTag("nid-cz", 8)),
		
		i_adjective(new DefinitionTag("i-prz", 0)),	
		i_adjectiveAux(new DefinitionTag("i-przy-pom", 5)),
		na_adjective(new DefinitionTag("na-prz", 0)),
		no_adjective(new DefinitionTag("rz-no", 1)),
		f_adjective(new DefinitionTag("rz-pr", 1)),
		
		ku_adjective(new DefinitionTag("ku-prz", 7)),
		taru_adjective(new DefinitionTag("tar-prz", 7)),
		nari_adjective(new DefinitionTag("nar-prz", 7)),
		shiku_adjective(new DefinitionTag("shi-prz", 7)),
		
		expression(new DefinitionTag("wyr", 1)),
		
		conjunction(new DefinitionTag("spó", 2)),
		
		interjection(new DefinitionTag("wyk", 2)),
		
		particule(new DefinitionTag("par", 2)),
		
		pronoun(new DefinitionTag("zai", 2)),
		
		adverb(new DefinitionTag("przy", 2)),
		adverb_to(new DefinitionTag("przy-to", 2)),
		
		prefix(new DefinitionTag("pre", 3)),
		suffix(new DefinitionTag("suf", 3)),
		
		number(new DefinitionTag("lic", 4)),
		counter(new DefinitionTag("kla", 4)),
		
		aux(new DefinitionTag("aux", 6)),
		
		copula(new DefinitionTag("kop", 9)),
		copula_da(new DefinitionTag("kop_da", 9)),
		
		name(new DefinitionTag("im", 10)),
		
		maleName(new DefinitionTag("me-im", 11)),
		femaleName(new DefinitionTag("rz-im", 11)),
		surnameName(new DefinitionTag("nazw", 12)),

		person(new DefinitionTag("oso", 13)),
		work(new DefinitionTag("dzie", 14)),
		place(new DefinitionTag("miej", 15)),
		stationName(new DefinitionTag("sta", 16)),
		companyName(new DefinitionTag("fir", 17)),
		organizationName(new DefinitionTag("org", 18)),
		productName(new DefinitionTag("prod", 19)),
		unclassName(new DefinitionTag("nies", 20)),
		character(new DefinitionTag("post", 21)),
		creature(new DefinitionTag("stwo", 22)),
		deity(new DefinitionTag("bóst", 23)),
		event(new DefinitionTag("wydarz", 24)),
		fict(new DefinitionTag("fikc", 25)),
		legend(new DefinitionTag("lege", 26)),
		mythology(new DefinitionTag("mito", 27)),
		object(new DefinitionTag("obie", 28)),
		other(new DefinitionTag("inny", 29)),
		religion(new DefinitionTag("reli", 30)),
		service(new DefinitionTag("usłu", 31)),
		group(new DefinitionTag("grup", 32)),	
		
		empty(new DefinitionTag("", 999)),
		unknown(new DefinitionTag("", 999)),
		
		;
		
		private DefinitionTag definitionTag;
		
		DefinitionTagCommonDef(DefinitionTag definitionTag) {
			this.definitionTag = definitionTag;
		}

		public DefinitionTag getDefinitionTag() {
			return definitionTag;
		}
	}
	
	private static Map<DictionaryEntryType, DefinitionTagCommonDef> oldDictionaryEntryTypeToDefinitionTagMap = new TreeMap<DictionaryEntryType, DefinitionTagCommonDef>() {
		
		private static final long serialVersionUID = 1L;

		{			
			put(DictionaryEntryType.WORD_NOUN, DefinitionTagCommonDef.noun);	
			
			put(DictionaryEntryType.WORD_VERB_U, DefinitionTagCommonDef.u_verb);
			put(DictionaryEntryType.WORD_VERB_RU, DefinitionTagCommonDef.ru_verb);
			put(DictionaryEntryType.WORD_VERB_IRREGULAR, DefinitionTagCommonDef.ir_verb);

			put(DictionaryEntryType.WORD_ADJECTIVE_I, DefinitionTagCommonDef.i_adjective);
			put(DictionaryEntryType.WORD_ADJECTIVE_NA, DefinitionTagCommonDef.na_adjective);
			
			put(DictionaryEntryType.WORD_TEMPORAL_NOUN, DefinitionTagCommonDef.nounTemporal);
			put(DictionaryEntryType.WORD_ADJECTIVE_NO, DefinitionTagCommonDef.no_adjective);
			put(DictionaryEntryType.WORD_ADJECTIVE_F, DefinitionTagCommonDef.f_adjective);

			put(DictionaryEntryType.WORD_EXPRESSION, DefinitionTagCommonDef.expression);
			
			put(DictionaryEntryType.WORD_CONJUNCTION, DefinitionTagCommonDef.conjunction);			
			put(DictionaryEntryType.WORD_INTERJECTION, DefinitionTagCommonDef.interjection);
			
			put(DictionaryEntryType.WORD_PARTICULE,DefinitionTagCommonDef.particule);
			
			put(DictionaryEntryType.WORD_PRONOUN, DefinitionTagCommonDef.pronoun);
			
			put(DictionaryEntryType.WORD_ADVERB, DefinitionTagCommonDef.adverb);
			put(DictionaryEntryType.WORD_ADVERB_TO, DefinitionTagCommonDef.adverb_to);

			put(DictionaryEntryType.WORD_NOUN_PREFIX, DefinitionTagCommonDef.nounPrefix);
			put(DictionaryEntryType.WORD_NOUN_SUFFIX, DefinitionTagCommonDef.nounSuffix);			
			
			put(DictionaryEntryType.WORD_PREFIX, DefinitionTagCommonDef.prefix);
			put(DictionaryEntryType.WORD_SUFFIX, DefinitionTagCommonDef.suffix);

			put(DictionaryEntryType.WORD_NUMBER, DefinitionTagCommonDef.number);
			
			put(DictionaryEntryType.WORD_COUNTER, DefinitionTagCommonDef.counter);
			
			put(DictionaryEntryType.WORD_AUX_ADJECTIVE_I, DefinitionTagCommonDef.i_adjectiveAux);
			
			put(DictionaryEntryType.WORD_ADVERBIAL_NOUN, DefinitionTagCommonDef.nounAdverbial);
			put(DictionaryEntryType.WORD_PRE_NOUN_ADJECTIVAL, DefinitionTagCommonDef.preNounAdverbial);
			
			put(DictionaryEntryType.WORD_AUX, DefinitionTagCommonDef.aux);
			
			put(DictionaryEntryType.WORD_ADJECTIVE_KU, DefinitionTagCommonDef.ku_adjective);
			put(DictionaryEntryType.WORD_ADJECTIVE_TARU, DefinitionTagCommonDef.taru_adjective);
			put(DictionaryEntryType.WORD_ADJECTIVE_NARI, DefinitionTagCommonDef.nari_adjective);
			put(DictionaryEntryType.WORD_ADJECTIVE_SHIKU, DefinitionTagCommonDef.shiku_adjective);

			put(DictionaryEntryType.WORD_VERB_AUX, DefinitionTagCommonDef.aux_verb);
			put(DictionaryEntryType.WORD_VERB_ZURU, DefinitionTagCommonDef.zuru_verb);						
			put(DictionaryEntryType.WORD_NIDAN_VERB, DefinitionTagCommonDef.nidan_verb);
			
			put(DictionaryEntryType.WORD_COPULA, DefinitionTagCommonDef.copula);
			put(DictionaryEntryType.WORD_COPULA_DA, DefinitionTagCommonDef.copula_da);
			
			put(DictionaryEntryType.WORD_PROPER_NOUN, DefinitionTagCommonDef.properNoun);
			
			put(DictionaryEntryType.WORD_NAME, DefinitionTagCommonDef.name);
			
			put(DictionaryEntryType.WORD_MALE_NAME, DefinitionTagCommonDef.maleName);
			put(DictionaryEntryType.WORD_FEMALE_NAME, DefinitionTagCommonDef.femaleName);
			put(DictionaryEntryType.WORD_SURNAME_NAME, DefinitionTagCommonDef.surnameName);			
			put(DictionaryEntryType.WORD_PERSON, DefinitionTagCommonDef.person);			
			put(DictionaryEntryType.WORD_WORK, DefinitionTagCommonDef.work);			
			put(DictionaryEntryType.WORD_PLACE, DefinitionTagCommonDef.place);			
			put(DictionaryEntryType.WORD_STATION_NAME, DefinitionTagCommonDef.stationName);			
			put(DictionaryEntryType.WORD_COMPANY_NAME, DefinitionTagCommonDef.companyName);			
			put(DictionaryEntryType.WORD_ORGANIZATION_NAME, DefinitionTagCommonDef.organizationName);			
			put(DictionaryEntryType.WORD_PRODUCT_NAME, DefinitionTagCommonDef.productName);			
			put(DictionaryEntryType.WORD_UNCLASS_NAME, DefinitionTagCommonDef.unclassName);			
			put(DictionaryEntryType.WORD_CHARACTER, DefinitionTagCommonDef.character);			
			put(DictionaryEntryType.WORD_CREATURE, DefinitionTagCommonDef.creature);
			put(DictionaryEntryType.WORD_DEITY, DefinitionTagCommonDef.deity);			
			put(DictionaryEntryType.WORD_EVENT, DefinitionTagCommonDef.event);			
			put(DictionaryEntryType.WORD_FICT, DefinitionTagCommonDef.fict);			
			put(DictionaryEntryType.WORD_LEGEND, DefinitionTagCommonDef.legend);
			put(DictionaryEntryType.WORD_MYTHOLOGY, DefinitionTagCommonDef.mythology);			
			put(DictionaryEntryType.WORD_OBJECT, DefinitionTagCommonDef.object);			
			put(DictionaryEntryType.WORD_OTHER, DefinitionTagCommonDef.other);			
			put(DictionaryEntryType.WORD_RELIGION, DefinitionTagCommonDef.religion);			
			put(DictionaryEntryType.WORD_SERVICE, DefinitionTagCommonDef.service);			
			put(DictionaryEntryType.WORD_GROUP, DefinitionTagCommonDef.group);
			
			put(DictionaryEntryType.WORD_EMPTY, DefinitionTagCommonDef.empty);
			put(DictionaryEntryType.UNKNOWN, DefinitionTagCommonDef.unknown);			
		}
	};
	
	private static Map<AttributeType, DefinitionTag> oldDictionaryAttributeTypeToDefinitionTagMap = new TreeMap<AttributeType, DefinitionTag>() {		
		private static final long serialVersionUID = 1L;

		{
			int fixme = 1; // !!!!!!!!!!!!!1 dokonczyc
			
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
	
	public static void generate(List<PolishJapaneseEntry> polishJapaneseEntriesList, List<PolishJapaneseEntry> namesList, String outputDir) throws Exception {
				
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

		int fixme = 1; // test !!!!!!!!!!!
		indexJSON.put("title", "Mały skromny japoński słownik - test");
		indexJSON.put("format", 3);
				
		indexJSON.put("revision", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		indexJSON.put("sequenced", true);
		indexJSON.put("author", "Fryderyk Mazurek");
		indexJSON.put("url", "https://www.japonski-pomocnik.pl");
		indexJSON.put("description", "Mały skromny japoński słownik");
		
		// zapis
		writeJSONObjectToFile(new File(outputDir, "index.json"), indexJSON);		
	}
	
	private static void generateTermBank(List<List<TermBankEntry>> termBankListList, List<PolishJapaneseEntry> polishJapaneseEntriesList, boolean names) throws Exception {
		
		List<TermBankEntry> currentTermBankList = null;
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			JMdict.Entry jmdictEntry = null;
			
			if (names == false) {				
				
				// sprawdzenie, czy wystepuje slowo w formacie JMdict
				List<Attribute> jmdictEntryIdAttributeList = polishJapaneseEntry.getAttributeList().getAttributeList(AttributeType.JMDICT_ENTRY_ID);
				
				if (jmdictEntryIdAttributeList != null && jmdictEntryIdAttributeList.size() > 0) { // cos jest
					
					// pobieramy entry id
					Integer entryId = Integer.parseInt(jmdictEntryIdAttributeList.get(0).getAttributeValue().get(0));
					
					// pobieramy z bazy danych
					jmdictEntry = dictionaryHelper.getEntryFromPolishDictionary(entryId);				
				}
			}
			
			List<TermBankEntry> termBankEntryList = null;
			
			if (jmdictEntry == null) { // stary sposob generowania
				
				termBankEntryList = new ArrayList<>();
								
				TermBankEntry termBankEntry = new TermBankEntry();
				
				termBankEntryList.add(termBankEntry);
				
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
				
				// !!!!!!!!!!!!!!!!!!
				int fixme = 1;
				termBankEntryList.clear();
				
			} else if (jmdictEntry != null) { // nowy sposob generowania
				
				termBankEntryList = new ArrayList<>();
								
				// laczenie kanji, kana i znaczen w pary
				List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(jmdictEntry);
				
				// wyszukanie konkretnego znaczenia dla naszeo slowka
				KanjiKanaPair kanjiKanaPair = Dictionary2HelperCommon.findKanjiKanaPair(kanjiKanaPairList, polishJapaneseEntry);

				// generowanie znaczenia
				KanjiInfo kanjiInfo = kanjiKanaPair.getKanjiInfo();
				ReadingInfo readingInfo = kanjiKanaPair.getReadingInfo();
				List<Sense> senseList = kanjiKanaPair.getSenseList();
				
				// chodzimy po wszystkich znaczeniach
				for (Sense currentSense : senseList) {
					
					// tworzymy nowy wpis
					TermBankEntry termBankEntry = new TermBankEntry();
					
					termBankEntryList.add(termBankEntry);
					
					if (kanjiInfo != null && kanjiInfo.getKanji() != null) {
						
						termBankEntry.setKanji(kanjiInfo.getKanji());
						termBankEntry.setKana(readingInfo.getKana().getValue());
						
					} else {
						
						termBankEntry.setKanji(readingInfo.getKana().getValue());
						termBankEntry.setKana("");				
					}
					
					// generowanie definitionTag
					generateDefinitionTag(kanjiInfo, readingInfo, currentSense, termBankEntry);
					
					// generowanie inflectedTags
					{						
						List<PartOfSpeechEnum> partOfSpeechList = currentSense.getPartOfSpeechList();
						
						for (PartOfSpeechEnum partOfSpeechEnum : partOfSpeechList) {
							
							if (Arrays.asList(PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI, PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI_YOI_II_CLASS).contains(partOfSpeechEnum) == true) {
								termBankEntry.addInflectedTags("adj-i");
							}

							if (Arrays.asList(PartOfSpeechEnum.ICHIDAN_VERB, PartOfSpeechEnum.ICHIDAN_VERB_KURERU_SPECIAL_CLASS).contains(partOfSpeechEnum) == true) {
								termBankEntry.addInflectedTags("v1");
							}
							
							if (partOfSpeechEnum.name().startsWith("GODAN_VERB") == true && partOfSpeechEnum != PartOfSpeechEnum.GODAN_VERB_URU_OLD_CLASS_VERB_OLD_FORM_OF_ERU) {
								termBankEntry.addInflectedTags("v5");
							}
							
							if (partOfSpeechEnum == PartOfSpeechEnum.KURU_VERB_SPECIAL_CLASS || readingInfo.getKana().getValue().endsWith("くる") == true) {
								termBankEntry.addInflectedTags("vk");
							}
							
							if (Arrays.asList(PartOfSpeechEnum.SURU_VERB_SPECIAL_CLASS, PartOfSpeechEnum.NOUN_OR_PARTICIPLE_WHICH_TAKES_THE_AUX_VERB_SURU, PartOfSpeechEnum.SURU_VERB_INCLUDED).contains(partOfSpeechEnum) == true || 
									readingInfo.getKana().getValue().endsWith("する") == true) {
								
								termBankEntry.addInflectedTags("vs");
							}
						}
					}

					
					// generowanie sequenceNumber
					termBankEntry.setSequenceNumber(jmdictEntry.getEntryId());
					
					// generowanie termTag
					// noop

					
					////////// !!!!!!!!!!!!!!!!!!!!
					
					// informacje dodatkowe do kanji
					if (kanjiInfo != null) {
						int fixme = 1; // !!!!!!!!!!!1
						
						List<String> kanjiAdditionalInfoPolishList = Dictionary2Helper.translateToPolishKanjiAdditionalInfoEnum(kanjiInfo.getKanjiAdditionalInfoList());
						List<RelativePriorityEnum> relativePriorityList = kanjiInfo.getRelativePriorityList();
						
						
					}
				}
				
			}
			
			//
			
			if (currentTermBankList == null) {
				currentTermBankList = new ArrayList<>();
			}
			
			if (termBankEntryList != null) {
				currentTermBankList.addAll(termBankEntryList);
			}
			
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
			
			DefinitionTagCommonDef tagDef = oldDictionaryEntryTypeToDefinitionTagMap.get(dictionaryEntryType);
			
			if (tagDef != null && tagDef.getDefinitionTag().getTag().length() > 0) {
				termBankEntry.addDefinitionTag(tagDef.getDefinitionTag().getTag());
				
			} else if (tagDef == null) {
				throw new RuntimeException("Unknown value: " + dictionaryEntryType);
			}
		}
		
		AttributeList attributeList = polishJapaneseEntry.getAttributeList();
		
		if (attributeList != null) {
			
			List<Attribute> attributeListList = attributeList.getAttributeList();
			
			if (attributeListList != null && attributeListList.size() > 0) {
				
				for (Attribute attribute : attributeListList) {
					
					if (attribute.getAttributeType().isShow() == true) {
						
						DefinitionTag tag = oldDictionaryAttributeTypeToDefinitionTagMap.get(attribute.getAttributeType());
						
						if (tag != null && tag.getTag().length() > 0) {
							termBankEntry.addDefinitionTag(tag.getTag());
							
						} else if (tag == null) {				
							throw new RuntimeException("Unknown value: " + oldDictionaryEntryTypeToDefinitionTagMap);
						}
					}
				}
			}				
		}
	}
	
	private static void generateDefinitionTag(KanjiInfo kanjiInfo, ReadingInfo readingInfo, Sense sense, TermBankEntry termBankEntry) {
		
		int fixme2 = 1; // trzeba to poprawic, uzywajac DefinitionTagCommonDef !!!!!!!!!!!!!!!
		
		// pobieranie czesci mowy
		List<PartOfSpeechEnum> partOfSpeechList = sense.getPartOfSpeechList();
		
		// przetlumaczone czesci mowy
		List<String> polishPartOfSpeechEnumPolishList = Dictionary2Helper.translateToPolishPartOfSpeechEnum(partOfSpeechList);
		
		// dodanie czesci mowy
		int fixme = 1; // !!!!!!!!!!!!!!! sprawdzic, czy to bedzie dobrze
		
		for (String currentPolishPartOfSpeech : polishPartOfSpeechEnumPolishList) {
			termBankEntry.addDefinitionTag(currentPolishPartOfSpeech);
		}
	}

	private static void generateAndSaveTagBank(String outputDir) {
				
		JSONArray tagBankJSONArray = new JSONArray();
		
		Set<String> alreadyUsedTagsName = new TreeSet<>();
		
		int fixme = 1; // !!!!!!!!!!!!
		// chodzenie po DefinitionTagCommonDef,
		// ponizszy kod zakomentowac
		
		// generowanie dla starej postaci slownika
		{
			Iterator<Entry<DictionaryEntryType, DefinitionTagCommonDef>> dictionaryEntryTypeToDefinitionTagMapIterator = oldDictionaryEntryTypeToDefinitionTagMap.entrySet().iterator();

			while (dictionaryEntryTypeToDefinitionTagMapIterator.hasNext() == true) {

				Entry<DictionaryEntryType, DefinitionTagCommonDef> dictionaryEntryTypeToDefinitionTagMapEntry = dictionaryEntryTypeToDefinitionTagMapIterator.next();

				if (dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getDefinitionTag().getTag().length() == 0) {
					continue;
				}

				if (alreadyUsedTagsName.contains(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getDefinitionTag().getTag()) == true) {				
					throw new RuntimeException(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getDefinitionTag().getTag());
				}

				alreadyUsedTagsName.add(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getDefinitionTag().getTag());

				JSONArray tagBankEntryJSONArray = createTagBankJSONArray(dictionaryEntryTypeToDefinitionTagMapEntry.getValue().getDefinitionTag(), dictionaryEntryTypeToDefinitionTagMapEntry.getKey().getName());

				tagBankJSONArray.put(tagBankEntryJSONArray);				
			}

			//

			Iterator<Entry<AttributeType, DefinitionTag>> attributeTypeToDefinitionTagMapIterator = oldDictionaryAttributeTypeToDefinitionTagMap.entrySet().iterator();

			while (attributeTypeToDefinitionTagMapIterator.hasNext() == true) {

				Entry<AttributeType, DefinitionTag> attributeTypeToDefinitionTagMapEntry = attributeTypeToDefinitionTagMapIterator.next();

				if (attributeTypeToDefinitionTagMapEntry.getValue().getTag().length() == 0) {
					continue;
				}

				if (alreadyUsedTagsName.contains(attributeTypeToDefinitionTagMapEntry.getValue().getTag()) == true) {				
					throw new RuntimeException(attributeTypeToDefinitionTagMapEntry.getValue().getTag());
				}

				alreadyUsedTagsName.add(attributeTypeToDefinitionTagMapEntry.getValue().getTag());

				JSONArray tagBankEntryJSONArray = createTagBankJSONArray(attributeTypeToDefinitionTagMapEntry.getValue(), attributeTypeToDefinitionTagMapEntry.getKey().getName());

				tagBankJSONArray.put(tagBankEntryJSONArray);				
			}	
		}
		
		// generowanie dla nowej postaci slownika - to jest zly kod
		/*
		{
			PartOfSpeechEnum[] partOfSpeechValues = PartOfSpeechEnum.values();
			
			for (int idx = 0; idx < partOfSpeechValues.length; ++idx) {
				
				PartOfSpeechEnum partOfSpeechEnum = partOfSpeechValues[idx];
				
				String partOfSpeechEnumInPolish;
				
				// przetlumaczymy czesc mowy
				try {
					partOfSpeechEnumInPolish = Dictionary2Helper.translateToPolishPartOfSpeechEnum(Arrays.asList(partOfSpeechEnum)).get(0);
					
				} catch (Exception e) {
					// jezeli wystapil wyjatek oznacza to, ze dana czesc mowy nie ma polskiego znaczenia
					// ale oznacza tez, ze na razie nie ma zadnego slowka, ktore by mialo ta czesc mowy
					// inaczej nie mozna byloby wygenerowac starej postaci slownika
					// i dlatego ignorujemy ten wyjatek
					continue;					
				}
				
				// dodajemy do listy tagow
				JSONArray tagBankEntryJSONArray = createTagBankJSONArray(partOfSpeechEnumInPolish, 1000 + idx, partOfSpeechEnumInPolish);

				tagBankJSONArray.put(tagBankEntryJSONArray);				
			}
		}
		*/				
		
		writeJSONArrayToFile(new File(outputDir, "tag_bank_1.json"), tagBankJSONArray);
	}

	private static JSONArray createTagBankJSONArray(String tag, int sortingOrder, String description) {
		
		JSONArray tagBankEntryJSONArray = new JSONArray();
		
		tagBankEntryJSONArray.put(tag);
		tagBankEntryJSONArray.put("");
		tagBankEntryJSONArray.put(sortingOrder);
		tagBankEntryJSONArray.put(description);
		tagBankEntryJSONArray.put(0);
		
		return tagBankEntryJSONArray;
	}
	
	private static JSONArray createTagBankJSONArray(DefinitionTag tag, String description) {
		return createTagBankJSONArray(tag.getTag(), tag.getSortingOrder(), description);
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
			
			if (inflectedTags.contains(tag) == false) {
				inflectedTags.add(tag);
			}
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
