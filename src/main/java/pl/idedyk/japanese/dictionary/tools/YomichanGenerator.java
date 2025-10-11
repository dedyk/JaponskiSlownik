package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon;
import pl.idedyk.japanese.dictionary2.api.helper.Dictionary2HelperCommon.KanjiKanaPair;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2NameHelper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Gloss;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.LanguageSource;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.RelativePriorityEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.SenseAdditionalInfo;

public class YomichanGenerator {
	
	private static enum DefinitionTagCommonDef {

		noun(new DefinitionTag("rz", 0, "rzeczownik")),
		nounTemporal(new DefinitionTag("rz-cza", 1, "rzeczownik czasowy")),
		nounPrefix(new DefinitionTag("rz-pre", 3, "rzeczownikowy prefiks")),
		nounSuffix(new DefinitionTag("rz-suf", 3, "rzeczownikowy sufiks")),
		nounAdverbial(new DefinitionTag("rz-prz", 6, "rzeczownik przysłówkowy")),
		preNounAdverbial(new DefinitionTag("pre-rz-prz", 6, "pre-rzeczownik przymiotnikowy")),
		properNoun(new DefinitionTag("naz-wl", 10, "nazwa własna")),
		nounSuru(new DefinitionTag("rz-suru", 10, "rzeczownik łączący się z czasownikiem suru")),
		
		u_verb(new DefinitionTag("u-cz", 0, "u-czasownik")),		
		ru_verb(new DefinitionTag("ru-cz", 0, "ru-czasownik")),		
		ir_verb(new DefinitionTag("ir-cz", 0, "czasownik nieregularny")),

		ichidan_verb(new DefinitionTag("ich-cz", 0, "czasownik ichidan")),
		nidan_verb(new DefinitionTag("nid-cz", 0, "czasownik nidan")),
		yodan_verb(new DefinitionTag("yod-cz", 0, "czasownik yodan")),
		godan_verb(new DefinitionTag("yod-cz", 0, "czasownik godan")),
		
		aux_verb(new DefinitionTag("cz-pom", 8, "czasownik pomocniczy")),
		suru_verb(new DefinitionTag("suru-cz", 8, "czasownik suru")),
		zuru_verb(new DefinitionTag("zuru-cz", 8, "czasownik zuru")),
		kuru_verb(new DefinitionTag("kuru-cz", 8, "czasownik kuru")),
		unclassified_verb(new DefinitionTag("niesk-cz", 17, "czasownik nieskalsyfikowany")),
		
		su_verb_precursor_suru(new DefinitionTag("su-cz-prek-suru", 8, "czasownik su, prekursor suru")),
		
		verbTransitivity(new DefinitionTag("cz-prz", 12, "czasownik przechodni")),
		verbIntransitivity(new DefinitionTag("cz-nprz", 12, "czasownik nieprzechodni")),
		
		i_adjective(new DefinitionTag("i-prz", 0, "i-przymiotnik")),	
		i_adjectiveAux(new DefinitionTag("i-przy-pom", 5, "i-przymiotnik pomocniczy")),
		na_adjective(new DefinitionTag("na-prz", 0, "na-przymiotnik")),
		no_adjective(new DefinitionTag("rz-no", 1, "no-przymiotnik")),
		f_adjective(new DefinitionTag("rz-pr", 1, "f-przymiotnik")),
		
		ku_adjective(new DefinitionTag("ku-prz", 7, "ku-przymiotnik")),
		taru_adjective(new DefinitionTag("taru-prz", 7, "taru-przymiotnik")),
		nari_adjective(new DefinitionTag("nari-prz", 7, "nari-przymiotnik")),
		shiku_adjective(new DefinitionTag("shiku-prz", 7, "shiku-przymiotnik")),
		kari_adjective(new DefinitionTag("kari-prz", 7, "kiri-przymiotnik")),
		na_adjective_archaic_formal(new DefinitionTag("na-prz-arch-form", 7, "na-przymiotnik, archaiczna/formalna forma")),
		
		expression(new DefinitionTag("wyr", 1, "wyrażenie")),
		
		conjunction(new DefinitionTag("spó", 2, "spójnik")),
		
		interjection(new DefinitionTag("wyk", 2, "wykrzyknik")),
		
		particule(new DefinitionTag("par", 2, "partykuła")),
		
		pronoun(new DefinitionTag("zai", 2, "zaimek")),
		
		adverb(new DefinitionTag("przy", 2, "przysłówek")),
		adverb_to(new DefinitionTag("przy-to", 2, "to-przysłówek")),
		
		prefix(new DefinitionTag("pre", 3, "prefiks")),
		suffix(new DefinitionTag("suf", 3, "sufiks")),
		
		number(new DefinitionTag("lic", 4, "liczba")),
		counter(new DefinitionTag("kla", 4, "klasyfikator")),
		
		aux(new DefinitionTag("aux", 6, "słowo pomocnicze")),
		
		copula(new DefinitionTag("kop", 9, "kopula")),
		copula_da(new DefinitionTag("kop_da", 9, "da-kopula")),
		
		name(new DefinitionTag("naz", 10, "nazwa")),
		
		commonWord(new DefinitionTag("pow-uz", 11, "słowo powszechne")),
		
		maleName(new DefinitionTag("me-im", 11, "imię męskie")),
		femaleName(new DefinitionTag("rz-im", 11, "imię żeńskie")),
		surnameName(new DefinitionTag("nazw", 12, "nazwisko")),

		person(new DefinitionTag("oso", 13, "osoba")),
		work(new DefinitionTag("dzie", 14, "praca")),
		place(new DefinitionTag("miej", 15, "miejsce")),
		stationName(new DefinitionTag("sta", 16, "nazwa stacji")),
		companyName(new DefinitionTag("fir", 17, "nazwa firmy")),
		organizationName(new DefinitionTag("org", 18, "nazwa organizacji")),
		productName(new DefinitionTag("prod", 19, "nazwa produktu")),
		unclassName(new DefinitionTag("nies", 20, "nazwa niesklasyfikowana")),
		character(new DefinitionTag("post", 21, "postać")),
		creature(new DefinitionTag("ist", 22, "istota")),
		deity(new DefinitionTag("bóst", 23, "bóstwo")),
		event(new DefinitionTag("wydarz", 24, "wydarzenie")),
		fict(new DefinitionTag("fikc", 25, "fikcja")),
		legend(new DefinitionTag("lege", 26, "legenda")),
		mythology(new DefinitionTag("mito", 27, "mitologia")),
		object(new DefinitionTag("obie", 28, "obiekt")),
		other(new DefinitionTag("inny", 29, "inny")),
		religion(new DefinitionTag("reli", 30, "religia")),
		service(new DefinitionTag("usłu", 31, "usługa")),
		group(new DefinitionTag("grup", 32, "grupa")),
		document(new DefinitionTag("grup", 33, "dokument")),
		shipName(new DefinitionTag("stat", 34, "nazwa statku")),
		
		// kanjiAlone(new DefinitionTag("kanj-sam", 12, "kanji występujący zwykle samodzielnie")),
		kanaAlone(new DefinitionTag("kana-sam", 12, "kana występująca zwykle samodzielnie")),
		
		kanjiIrregularUsage(new DefinitionTag("kanj-niere-uzy", 13, "nieregularne użycie kanji")),
		kanaIrregularUsage(new DefinitionTag("kana-niere-uzy", 13, "nieregularne użycie kana")),
		kanjiOkuriganaUsage(new DefinitionTag("kanj-okuri-uzy", 13, "użycie okurigana kanji")),
		kanjiOutDatedUsage(new DefinitionTag("kanj-przes-uzy", 13, "przestarzałe użycie kanji")),
		kanaOutDatedUsage(new DefinitionTag("kana-przes-uzy", 13, "przestarzałe użycie kana")),
		kanaSearchOnly(new DefinitionTag("kana-tylk-szuk", 13, "kana używane tylko do szukania")),
		kanjiRarelyUsage(new DefinitionTag("kanj-rzad-uzy", 13, "kanji rzadko używane")),
		kanjiSearchOnly(new DefinitionTag("kanj-tylk-szuk", 13, "kanji używane tylko do szukania")),
		ateji(new DefinitionTag("ate", 13, "ateji")),
		kanaGikunMeaningJikujikunSpecialKanjiReading(new DefinitionTag("kana-gik-jiku", 13, "gikun (znaczenie jako czytanie) lub jukujikun (specjalne czytanie kanji)")),
		
		onamatopoeicMimeticWord(new DefinitionTag("ono", 14, "onomatopeja")),
		
		verbKeigoHigh(new DefinitionTag("hon-wyw", 15, "czasownik wywyższający")),
		verbKeigoLow(new DefinitionTag("hon-unż", 15, "czasownik uniżający")),
		
		obscure(new DefinitionTag("mał-zna", 16, "mało znane słowo")),
		archaic(new DefinitionTag("arch", 17, "archaiczny")),
		obsolete(new DefinitionTag("przes", 17, "przestarzałe słowo")),
		
		unclassified(new DefinitionTag("niesk", 17, "słowo nieskalsyfikowane")),
		
		empty(new DefinitionTag("", 999, "pusty")),
		unknown(new DefinitionTag("", 999, "nieznany")),
		
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
			put(DictionaryEntryType.WORD_DOCUMENT, DefinitionTagCommonDef.document);
			put(DictionaryEntryType.WORD_SHIP_NAME, DefinitionTagCommonDef.shipName);
			
			put(DictionaryEntryType.WORD_EMPTY, DefinitionTagCommonDef.empty);
			put(DictionaryEntryType.UNKNOWN, DefinitionTagCommonDef.unknown);			
		}
	};
	
	private static Map<AttributeType, DefinitionTagCommonDef> oldDictionaryAttributeTypeToDefinitionTagMap = new TreeMap<AttributeType, DefinitionTagCommonDef>() {		
		private static final long serialVersionUID = 1L;

		{			
			put(AttributeType.SURU_VERB, DefinitionTagCommonDef.suru_verb);
			
			put(AttributeType.COMMON_WORD, DefinitionTagCommonDef.commonWord);
			
			put(AttributeType.VERB_TRANSITIVITY, DefinitionTagCommonDef.verbTransitivity);
			put(AttributeType.VERB_INTRANSITIVITY, DefinitionTagCommonDef.verbIntransitivity);

			// put(AttributeType.KANJI_ALONE, DefinitionTagCommonDef.kanjiAlone);
			put(AttributeType.KANA_ALONE, DefinitionTagCommonDef.kanaAlone);

			put(AttributeType.ATEJI, DefinitionTagCommonDef.ateji);
			
			put(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD, DefinitionTagCommonDef.onamatopoeicMimeticWord);
			
			put(AttributeType.VERB_KEIGO_HIGH, DefinitionTagCommonDef.verbKeigoHigh);
			put(AttributeType.VERB_KEIGO_LOW, DefinitionTagCommonDef.verbKeigoLow);
			
			put(AttributeType.OBSCURE, DefinitionTagCommonDef.obscure);
			
			put(AttributeType.ARCHAIC, DefinitionTagCommonDef.archaic);
			put(AttributeType.OBSOLETE, DefinitionTagCommonDef.obsolete);
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
	
	private static void generateTermBank(List<List<TermBankEntry>> termBankListList, List<PolishJapaneseEntry> polishJapaneseEntriesList, boolean names) throws Exception {
		
		List<TermBankEntry> currentTermBankList = null;
		
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			JMdict.Entry jmdictEntry = null;
			
			if (names == false) {		
				
				// pobieramy entry id
				Integer entryId = polishJapaneseEntry.getJmdictEntryId();
				
				if (entryId != null) {
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
					boolean isArchaic = false;
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
								
								if (attribute.getAttributeType() == AttributeType.ARCHAIC) {
									isArchaic = true;
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
					
					} else if (isArchaic == true) {
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
					termBankEntry.addTranslate("[Informacja dodatkowa]: " + polishJapaneseEntry.getInfo());
				}
				
				termBankEntry.addTranslate("[Romaji]: " + polishJapaneseEntry.getRomaji());
								
			} else if (jmdictEntry != null) { // nowy sposob generowania
				
				termBankEntryList = new ArrayList<>();
								
				// laczenie kanji, kana i znaczen w pary
				List<KanjiKanaPair> kanjiKanaPairList = Dictionary2HelperCommon.getKanjiKanaPairListStatic(jmdictEntry, false);
				
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

							if (Arrays.asList(PartOfSpeechEnum.ICHIDAN_VERB, PartOfSpeechEnum.ICHIDAN_VERB_KURERU_SPECIAL_CLASS, PartOfSpeechEnum.ICHIDAN_VERB_ZURU_VERB_ALTERNATIVE_FORM_OF_JIRU_VERBS).contains(partOfSpeechEnum) == true) {
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
					
					// generowanie popularity
					generatePopularity(kanjiInfo, readingInfo, termBankEntry);
					
					// generowanie znaczen i innych dodatkowych informacji
					
					// pobranie polskiego tlumaczenia i informacji dodatkowych
					List<Gloss> glossPolList = currentSense.getGlossList().stream().filter(gloss -> (gloss.getLang().equals("pol") == true)).collect(Collectors.toList());
					List<SenseAdditionalInfo> additionalInfoPolList = currentSense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).collect(Collectors.toList());
					
					for (Gloss currentGlossPol : glossPolList) {						
						termBankEntry.addTranslate(currentGlossPol.getValue() + 
								(currentGlossPol.getGType() != null ? Dictionary2HelperCommon.translateToPolishGlossType(currentGlossPol.getGType()) : ""));						
					}
					
					for (SenseAdditionalInfo senseAdditionalInfo : additionalInfoPolList) {
						termBankEntry.addTranslate("[Informacja dodatkowa]: " + senseAdditionalInfo.getValue());
					}

					
					////////// !!!!!!!!!!!!!!!!!!!!
					
					// informacje dodatkowe do kanji
					if (kanjiInfo != null) {						
						List<String> kanjiAdditionalInfoPolishList = Dictionary2Helper.translateToPolishKanjiAdditionalInfoEnum(kanjiInfo.getKanjiAdditionalInfoList());
						
						for (String currentKanjiAdditionalInfoPolish : kanjiAdditionalInfoPolishList) {
							termBankEntry.addTranslate("[Kanji info]: " + currentKanjiAdditionalInfoPolish);
						}
					}
					
					// informacje z czytania
					{
						termBankEntry.addTranslate("[Romaji]: " + readingInfo.getKana().getRomaji());
												
						List<String> readingAdditionalInfoPolishList = Dictionary2Helper.translateToPolishReadingAdditionalInfoEnum(readingInfo.getReadingAdditionalInfoList());
						
						for (String currentReadingAdditionalInfoPolish : readingAdditionalInfoPolishList) {
							termBankEntry.addTranslate("[Czytanie info]: " + currentReadingAdditionalInfoPolish);
						}
					}
					
					// zrodlo z ktorego pochodzi to slowo
					{
						List<LanguageSource> senseLanguageSourceList = currentSense.getLanguageSourceList();
						
						for (LanguageSource languageSource : senseLanguageSourceList) {
							
							String languageCodeInPolish = Dictionary2HelperCommon.translateToPolishLanguageCode(languageSource.getLang());
							String languageValue = languageSource.getValue();
							String languageLsWasei = Dictionary2HelperCommon.translateToPolishLanguageSourceLsWaseiEnum(languageSource.getLsWasei());
							
							if (languageValue != null && languageValue.trim().equals("") == false) {
								termBankEntry.addTranslate("[Pochodzenie słowa]: " + languageCodeInPolish + ": " + languageValue);
								
							} else {
								termBankEntry.addTranslate("[Pochodzenie słowa]: " +Dictionary2HelperCommon.translateToPolishLanguageCodeWithoutValue(languageSource.getLang()));
							}
							
							if (languageLsWasei != null) {
								termBankEntry.addTranslate("[Pochodzenie słowa]: " +languageLsWasei);
							}
						}
					}
					
					// dialekt
					{
						
						List<String> dialectPolishInfo = Dictionary2Helper.translateToPolishDialectEnumList(currentSense.getDialectList());
						
						for (String currentDialectPolish : dialectPolishInfo) {
							termBankEntry.addTranslate("[Dialekt]: " + currentDialectPolish);
						}
					}
					
					// dziedzina
					{
						List<String> fieldListPolishList = Dictionary2Helper.translateToPolishFieldEnumList(currentSense.getFieldList());
						
						for (String currentFieldEnumPolish : fieldListPolishList) {
							termBankEntry.addTranslate("[Dziedzina]: " + currentFieldEnumPolish);
						}
					}
					
					// misc
					{
						List<String> miscPolishList = Dictionary2Helper.translateToPolishMiscEnumList(currentSense.getMiscList());
						
						for (String currentMiscEnumPolish : miscPolishList) {
							termBankEntry.addTranslate("[Różności]: " + currentMiscEnumPolish);
						}
					}
					
					// odnosnik do innego slowa
					for (String currentReferenceToAnotherKanjiKana : currentSense.getReferenceToAnotherKanjiKanaList()) {
						termBankEntry.addTranslate("[Powiązane słowo]: " + currentReferenceToAnotherKanjiKana);
					}
					
					// przeciwienstwo
					for (String currentAntonym : currentSense.getAntonymList()) {
						termBankEntry.addTranslate("[Przeciwieństwo]: " + currentAntonym);
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
						
						DefinitionTagCommonDef tag = oldDictionaryAttributeTypeToDefinitionTagMap.get(attribute.getAttributeType());
						
						if (tag != null && tag.getDefinitionTag().getTag().length() > 0) {
							termBankEntry.addDefinitionTag(tag.getDefinitionTag().getTag());
							
						} else if (tag == null) {				
							throw new RuntimeException("Unknown value: " + oldDictionaryEntryTypeToDefinitionTagMap);
						}
					}
				}
			}				
		}
	}
	
	private static void generateDefinitionTag(KanjiInfo kanjiInfo, ReadingInfo readingInfo, Sense sense, TermBankEntry termBankEntry) {
		
		// pobieranie informacji z kanji info
		if (kanjiInfo != null) {
			List<KanjiAdditionalInfoEnum> kanjiAdditionalInfoList = kanjiInfo.getKanjiAdditionalInfoList();
			
			for (KanjiAdditionalInfoEnum kanjiAdditionalInfoEnum : kanjiAdditionalInfoList) {
				
				switch (kanjiAdditionalInfoEnum) {
				
				case ATEJI_PHONETIC_READING:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.ateji.getDefinitionTag().getTag());
					
					break;
				
				case WORD_CONTAINING_IRREGULAR_KANJI_USAGE:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiIrregularUsage.getDefinitionTag().getTag());
					
					break;
					
				case WORD_CONTAINING_IRREGULAR_KANA_USAGE:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanaIrregularUsage.getDefinitionTag().getTag());
					
					break;
					
				case IRREGULAR_OKURIGANA_USAGE:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiOkuriganaUsage.getDefinitionTag().getTag());
	
					break;
					
				case WORD_CONTAINING_OUT_DATED_KANJI_OR_KANJI_USAGE:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiOutDatedUsage.getDefinitionTag().getTag());
					
					break;
					
				case RARELY_USED_KANJI_FORM:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiRarelyUsage.getDefinitionTag().getTag());
					
					break;
					
				case SEARCH_ONLY_KANJI_FORM:
					termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiSearchOnly.getDefinitionTag().getTag());
					
					break;
					
				default:
					throw new RuntimeException("Unknown kanji additional info enum: " + kanjiAdditionalInfoEnum);
				
				}
			}
		}
		
		// pobranie informacji z czytania
		List<ReadingAdditionalInfoEnum> readingAdditionalInfoList = readingInfo.getReadingAdditionalInfoList();
		
		for (ReadingAdditionalInfoEnum readingAdditionalInfoEnum : readingAdditionalInfoList) {
			
			switch (readingAdditionalInfoEnum) {
			
			case GIKUN_MEANING_AS_READING_OR_JUKUJIKUN_SPECIAL_KANJI_READING:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanaGikunMeaningJikujikunSpecialKanjiReading.getDefinitionTag().getTag());
				
				break;
			
			case WORD_CONTAINING_IRREGULAR_KANA_USAGE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanaIrregularUsage.getDefinitionTag().getTag());
				
				break;

			case OUT_DATED_OR_OBSOLETE_KANA_USAGE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanaOutDatedUsage.getDefinitionTag().getTag());
				
				break;
				
			case SEARCH_ONLY_KANA_FORM:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanaSearchOnly.getDefinitionTag().getTag());
				
				break;
				
			/*	
			case WORD_USUALLY_WRITTEN_USING_KANJI_ALONE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kanjiAlone.getDefinitionTag().getTag());
				
				break;
			*/
				
			default:
				throw new RuntimeException("Unknown reading additional info enum: " + readingAdditionalInfoEnum);
			}			
		}
		
		// pobieranie czesci mowy
		List<PartOfSpeechEnum> partOfSpeechList = sense.getPartOfSpeechList();
				
		for (PartOfSpeechEnum partOfSpeechEnum : partOfSpeechList) {
			
			switch (partOfSpeechEnum) {
			
			case NOUN_COMMON_FUTSUUMEISHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.noun.getDefinitionTag().getTag());
			
				break;
				
			case NOUN_TEMPORAL_JISOUMEISHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nounTemporal.getDefinitionTag().getTag());
				
				break;	
				
			case NOUN_USED_AS_A_PREFIX:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nounPrefix.getDefinitionTag().getTag());
				
				break;	

			case NOUN_USED_AS_A_SUFFIX:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nounSuffix.getDefinitionTag().getTag());
				
				break;	
				
			case ADVERBIAL_NOUN_FUKUSHITEKIMEISHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nounAdverbial.getDefinitionTag().getTag());
				
				break;					
				
			case PRE_NOUN_ADJECTIVAL_RENTAISHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.preNounAdverbial.getDefinitionTag().getTag());
				
				break;	
				
			case PROPER_NOUN:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.properNoun.getDefinitionTag().getTag());
				
				break;	
				
			case NOUNS_WHICH_MAY_TAKE_THE_GENITIVE_CASE_PARTICLE_NO:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.no_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case NOUN_OR_PARTICIPLE_WHICH_TAKES_THE_AUX_VERB_SURU:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nounSuru.getDefinitionTag().getTag());
				
				break;					
				
			case ADJECTIVE_KEIYOUSHI:
			case ADJECTIVE_KEIYOUSHI_YOI_II_CLASS:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.i_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case ADJECTIVAL_NOUNS_OR_QUASI_ADJECTIVES_KEIYODOSHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.na_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case NOUN_OR_VERB_ACTING_PRENOMINALLY:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.f_adjective.getDefinitionTag().getTag());
				
				break;					
				
			case AUXILIARY_ADJECTIVE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.i_adjectiveAux.getDefinitionTag().getTag());
				
				break;

			case AUXILIARY:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.aux.getDefinitionTag().getTag());
				
				break;
				
			case AUXILIARY_VERB:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.aux_verb.getDefinitionTag().getTag());
				
				break;
				
			case TARU_ADJECTIVE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.taru_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case KU_ADJECTIVE_ARCHAIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.ku_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case SHIKU_ADJECTIVE_ARCHAIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.shiku_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case KARI_ADJECTIVE_ARCHAIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kari_adjective.getDefinitionTag().getTag());
				
				break;	
				
			case GODAN_VERB_WITH_U_ENDING:
			case GODAN_VERB_WITH_U_ENDING_SPECIAL_CLASS:
			case GODAN_VERB_WITH_KU_ENDING:
			case GODAN_VERB_WITH_TSU_ENDING:
			case GODAN_VERB_WITH_SU_ENDING:
			case GODAN_VERB_WITH_RU_ENDING:
			case GODAN_VERB_WITH_RU_ENDING_IRREGULAR_VERB:
			case GODAN_VERB_WITH_BU_ENDING:
			case GODAN_VERB_WITH_GU_ENDING:
			case GODAN_VERB_WITH_MU_ENDING:
			case GODAN_VERB_WITH_NU_ENDING:
			case GODAN_VERB_ARU_SPECIAL_CLASS:
			case GODAN_VERB_IKU_YUKU_SPECIAL_CLASS:
			case GODAN_VERB_URU_OLD_CLASS_VERB_OLD_FORM_OF_ERU:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.godan_verb.getDefinitionTag().getTag());
				
				break;	
				
			case NIDAN_VERB_LOWER_CLASS_WITH_BU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_DZU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_GU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_HU_FU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_KU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_MU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_NU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_RU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_SU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_TSU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_U_ENDING_AND_WE_CONJUGATION_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_YU_ENDING_ARCHAIC:
			case NIDAN_VERB_LOWER_CLASS_WITH_ZU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_BU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_DZU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_GU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_HU_FU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_KU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_MU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_RU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_TSU_ENDING_ARCHAIC:
			case NIDAN_VERB_UPPER_CLASS_WITH_YU_ENDING_ARCHAIC:
			case NIDAN_VERB_WITH_U_ENDING_ARCHAIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.nidan_verb.getDefinitionTag().getTag());
				
				break;	
				
			case YODAN_VERB_WITH_BU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_GU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_HU_FU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_KU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_MU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_NU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_RU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_SU_ENDING_ARCHAIC:
			case YODAN_VERB_WITH_TSU_ENDING_ARCHAIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.yodan_verb.getDefinitionTag().getTag());
				
				break;					

			case ICHIDAN_VERB:
			case ICHIDAN_VERB_KURERU_SPECIAL_CLASS:
			case ICHIDAN_VERB_ZURU_VERB_ALTERNATIVE_FORM_OF_JIRU_VERBS:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.ichidan_verb.getDefinitionTag().getTag());
				
				break;
				
			case SURU_VERB_INCLUDED:
			case SURU_VERB_SPECIAL_CLASS:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.suru_verb.getDefinitionTag().getTag());
				
				break;
				
			case SU_VERB_PRECURSOR_TO_THE_MODERN_SURU:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.su_verb_precursor_suru.getDefinitionTag().getTag());
				
				break;
				
			case KURU_VERB_SPECIAL_CLASS:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.kuru_verb.getDefinitionTag().getTag());
				
				break;
				
			case VERB_UNSPECIFIED:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.unclassified_verb.getDefinitionTag().getTag());
				
				break;
				
			case IRREGULAR_NU_VERB:
			case IRREGULAR_RU_VERB_PLAIN_FORM_ENDS_WITH_RI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.ir_verb.getDefinitionTag().getTag());
				
				break;				
				
			case TRANSITIVE_VERB:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.verbTransitivity.getDefinitionTag().getTag());
				
				break;
				
			case INTRANSITIVE_VERB:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.verbIntransitivity.getDefinitionTag().getTag());
				
				break;
				
			case ADVERB_FUKUSHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.adverb.getDefinitionTag().getTag());
				
				break;
				
			case ADVERB_TAKING_THE_TO_PARTICLE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.adverb_to.getDefinitionTag().getTag());
				
				break;				
				
			case COUNTER:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.counter.getDefinitionTag().getTag());
				
				break;
				
			case NUMERIC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.number.getDefinitionTag().getTag());
				
				break;
				
			case PRONOUN:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.pronoun.getDefinitionTag().getTag());
				
				break;
				
			case CONJUNCTION:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.conjunction.getDefinitionTag().getTag());
				
				break;
								
			case PARTICLE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.particule.getDefinitionTag().getTag());
				
				break;
			
			case COPULA:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.copula.getDefinitionTag().getTag());
				
				break;
				
			case PREFIX:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.prefix.getDefinitionTag().getTag());
				
				break;

			case SUFFIX:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.suffix.getDefinitionTag().getTag());
				
				break;
								
			case EXPRESSIONS_PHRASES_CLAUSES_ETC:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.expression.getDefinitionTag().getTag());
				
				break;
				
			case INTERJECTION_KANDOUSHI:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.interjection.getDefinitionTag().getTag());
				
				break;
				
			case ARCHAIC_FORMAL_FORM_OF_NA_ADJECTIVE:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.na_adjective_archaic_formal.getDefinitionTag().getTag());
				
				break;	
				
			case UNCLASSIFIED:
				termBankEntry.addDefinitionTag(DefinitionTagCommonDef.unclassified.getDefinitionTag().getTag());
				
				break;
			
			default:				
				throw new RuntimeException("Unknown sense part of speech enum: " + partOfSpeechEnum);
			}			
		}		
	}
	
	private static void generatePopularity(KanjiInfo kanjiInfo, ReadingInfo readingInfo, TermBankEntry termBankEntry) {
		
		List<RelativePriorityEnum> allRelativePriorityEnumList = new ArrayList<>();
		
		if (kanjiInfo != null) {
			allRelativePriorityEnumList.addAll(kanjiInfo.getRelativePriorityList());
		}
		
		allRelativePriorityEnumList.addAll(readingInfo.getRelativePriorityList());
		
		//
		
		Integer popularity = null;
		
		for (RelativePriorityEnum relativePriorityEnum : allRelativePriorityEnumList) {
		    
			Integer newPopularity = null;
			
		    if (Arrays.asList(RelativePriorityEnum.NEWS_1, RelativePriorityEnum.ICHI_1, RelativePriorityEnum.SPEC_1, RelativePriorityEnum.SPEC_2, RelativePriorityEnum.GAI_1).contains(relativePriorityEnum) == true) {
		    	newPopularity = 0;
		    	
		    } else if (Arrays.asList(RelativePriorityEnum.NEWS_2, RelativePriorityEnum.ICHI_2, RelativePriorityEnum.GAI_2).contains(relativePriorityEnum) == true) {
		    	newPopularity = -1;
		    	
		    } else if (relativePriorityEnum.value().startsWith("nf") == true) {
		    	newPopularity = -2;
		    }

			if (newPopularity != null && (popularity == null || newPopularity.intValue() > popularity.intValue())) {
				popularity = newPopularity;
			}			
		}
		
		if (popularity == null) {
			popularity = -3;
		}
		
		termBankEntry.setPopularity(popularity);
	}

	private static void generateAndSaveTagBank(String outputDir) {
						
		JSONArray tagBankJSONArray = new JSONArray();
				
		// generowanie tag'ow z DefinitionTagCommonDef		
		for (DefinitionTagCommonDef definitionTagCommonDef : DefinitionTagCommonDef.values()) {
			
			if (definitionTagCommonDef.getDefinitionTag().getTag().length() == 0) {
				continue;
			}

			JSONArray tagBankEntryJSONArray = createTagBankJSONArray(definitionTagCommonDef.getDefinitionTag());

			tagBankJSONArray.put(tagBankEntryJSONArray);				
		}
		
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
	
	private static JSONArray createTagBankJSONArray(DefinitionTag tag) {
		return createTagBankJSONArray(tag.getTag(), tag.getSortingOrder(), tag.getDescription());
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
		
		private String description;
		
		/*
		public DefinitionTag(String tag, int sortingOrder) {
			this.tag = tag;
			this.sortingOrder = sortingOrder;
		}
		*/
		
		public DefinitionTag(String tag, int sortingOrder, String description) {
			this.tag = tag;
			this.sortingOrder = sortingOrder;
			this.description = description;
		}


		public String getTag() {
			return tag;
		}

		public int getSortingOrder() {
			return sortingOrder;
		}

		public String getDescription() {
			return description;
		}
	}
	
	public static void main(String[] args) throws Exception {

		// read edict common
		TreeMap<String, EDictEntry> jmedictCommon = EdictReader.readEdict("../JapaneseDictionary_additional/edict_sub-utf8");
				
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
		
		//
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = dictionary2Helper.getOldPolishJapaneseEntriesList();		

		Helper.generateAdditionalInfoFromEdict(dictionary2Helper, jmedictCommon, polishJapaneseEntriesList);

		//
		
		List<PolishJapaneseEntry> namesList = Helper.generateNames(dictionary2Helper, dictionary2NameHelper);
		
		generate(polishJapaneseEntriesList, namesList, "/tmp/a");		
	}
}
