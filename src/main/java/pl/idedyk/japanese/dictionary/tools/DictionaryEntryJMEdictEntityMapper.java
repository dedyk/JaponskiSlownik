package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.DialectEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.MiscEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.PartOfSpeechEnum;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingAdditionalInfoEnum;
import pl.idedyk.japanese.dictionary2.jmnedict.xsd.TranslationalInfoNameType;

public class DictionaryEntryJMEdictEntityMapper {

	private Map<DictionaryEntryType, List<String>> dictionaryEntryToEntityMapper;
	
	private Map<String, DictionaryEntryType> entityToDictionaryEntryMapper;
	
	private Map<PartOfSpeechEnum, String> partOfSpeechEnumToEntityMapper;
	private Map<String, PartOfSpeechEnum> entityToPartOfSpeechEnumMapper;
	
	private Map<MiscEnum, String> miscEnumToEntityMapper;
	private Map<DialectEnum, String> dialectEnumToEntityMapper;
	private Map<KanjiAdditionalInfoEnum, String> kanjiAdditionalInfoEnumToEntityMapper;
	private Map<ReadingAdditionalInfoEnum, String> readingAdditionalInfoEnumToEntityMapper;
	
	private Map<TranslationalInfoNameType, DictionaryEntryType> translationalInfoNameTypeToDictionaryEntryMapper;
	
	public DictionaryEntryJMEdictEntityMapper() {
		
		dictionaryEntryToEntityMapper = new TreeMap<DictionaryEntryType, List<String>>();
		entityToDictionaryEntryMapper = new TreeMap<String, DictionaryEntryType>();
		
		partOfSpeechEnumToEntityMapper = new TreeMap<>();
		entityToPartOfSpeechEnumMapper = new TreeMap<>();
		
		miscEnumToEntityMapper = new TreeMap<>();
		dialectEnumToEntityMapper = new TreeMap<>();
		kanjiAdditionalInfoEnumToEntityMapper = new TreeMap<>();
		readingAdditionalInfoEnumToEntityMapper = new TreeMap<>();
		
		translationalInfoNameTypeToDictionaryEntryMapper = new TreeMap<>();
		
		fillMaps();
	}

	private void fillMaps() {

		addMap(DictionaryEntryType.WORD_NUMBER, "num");
	
		addMap(DictionaryEntryType.WORD_NOUN, "n");
		addMap(DictionaryEntryType.WORD_NOUN_PREFIX, "n-pref");
		addMap(DictionaryEntryType.WORD_NOUN_SUFFIX, "n-suf");
		addMap(DictionaryEntryType.WORD_PROPER_NOUN, "n-pr");
		addMap(DictionaryEntryType.WORD_NOUN, "vs");
		addMap(DictionaryEntryType.WORD_TEMPORAL_NOUN, "n-t");
		
		addMap(DictionaryEntryType.WORD_ADVERB, "adv");
		addMap(DictionaryEntryType.WORD_ADVERBIAL_NOUN, "n-adv");
		addMap(DictionaryEntryType.WORD_ADVERB_TO, "adv-to");
		
		addMap(DictionaryEntryType.WORD_ADJECTIVE_I, "adj-i");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_I, "adj-ix");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_NA, "adj-na");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_NO, "adj-no");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_F, "adj-f");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_TARU, "adj-t");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_NARI, "adj-nari");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_KU, "adj-ku");
		addMap(DictionaryEntryType.WORD_AUX_ADJECTIVE_I, "aux-adj");
		addMap(DictionaryEntryType.WORD_ADJECTIVE_SHIKU, "adj-shiku");
		
		addMap(DictionaryEntryType.WORD_COPULA, "cop");
		addMap(DictionaryEntryType.WORD_COPULA_DA, "cop-da");
				
		addMap(DictionaryEntryType.WORD_PRE_NOUN_ADJECTIVAL, "adj-pn");
		
		addMap(DictionaryEntryType.WORD_VERB_RU, "v1");
		addMap(DictionaryEntryType.WORD_VERB_RU, "v1-s");
		addMap(DictionaryEntryType.WORD_VERB_RU, "v2r-k");
		
		addMap(DictionaryEntryType.WORD_VERB_U, "v4h");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5k-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5r");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5m");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4r");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4t");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5u");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5r-i");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5t");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5g");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5b");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5n");
		
		addMap(DictionaryEntryType.WORD_VERB_U, "v5u-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2a-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "vs-c");
		addMap(DictionaryEntryType.WORD_VERB_U, "vn");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5aru");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2w-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2r-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2t-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4m");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4g");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2n-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2y-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2m-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2b-k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4b");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2y-k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2t-k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2g-k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2g-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2d-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2k-k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2s-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v2z-s");
		
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vk");
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vs-i");
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vs-s");
		
		addMap(DictionaryEntryType.WORD_VERB_ZURU, "vz");
		
		addMap(DictionaryEntryType.WORD_NIDAN_VERB, "v2h-k");
		addMap(DictionaryEntryType.WORD_NIDAN_VERB, "v2h-s");
		addMap(DictionaryEntryType.WORD_NIDAN_VERB, "v2k-s");
		
		addMap(DictionaryEntryType.WORD_AUX, "aux");
		addMap(DictionaryEntryType.WORD_VERB_AUX, "aux-v");
		
		addMap(DictionaryEntryType.WORD_INTERJECTION, "int");
		
		addMap(DictionaryEntryType.WORD_CONJUNCTION, "conj");
		
		addMap(DictionaryEntryType.WORD_COUNTER, "ctr");
		
		addMap(DictionaryEntryType.WORD_EXPRESSION, "exp");
		
		addMap(DictionaryEntryType.WORD_PARTICULE, "prt");
		
		addMap(DictionaryEntryType.WORD_PRONOUN, "pn");
		
		addMap(DictionaryEntryType.WORD_MALE_NAME, null);
		addMap(DictionaryEntryType.WORD_FEMALE_NAME, null);
		
		addMap(DictionaryEntryType.WORD_PREFIX, "pref");
		addMap(DictionaryEntryType.WORD_SUFFIX, "suf");
		
		addMap(DictionaryEntryType.WORD_EMPTY, null);
		
		addMap(DictionaryEntryType.UNKNOWN, "unc");
		addMap(DictionaryEntryType.UNKNOWN, "v-unspec");
		
		addNullMap("vt");
		addNullMap("vi");
		addNullMap("on-mim");
		addNullMap("hon");
		addNullMap("uk");
		addNullMap("col");
		addNullMap("arch");
		addNullMap("abbr");
		addNullMap("sl");
		addNullMap("male");
		addNullMap("fam");
		addNullMap("fem");
		addNullMap("cop-da");
		addNullMap("obsc");
		addNullMap("yoji");
		addNullMap("derog");
		addNullMap("pol");
		addNullMap("sens");
		addNullMap("vulg");
		addNullMap("hum");
		addNullMap("obs");
		addNullMap("chn");
		addNullMap("id");
		addNullMap("m-sl");
		addNullMap("v4h");
		addNullMap("joc");
		addNullMap("vr");
		addNullMap("poet");
		
		//
		
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_OR_VERB_ACTING_PRENOMINALLY, "adj-f");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI, "adj-i");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI_YOI_II_CLASS, "adj-ix");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.KARI_ADJECTIVE_ARCHAIC, "adj-kari");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.KU_ADJECTIVE_ARCHAIC, "adj-ku");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADJECTIVAL_NOUNS_OR_QUASI_ADJECTIVES_KEIYODOSHI, "adj-na");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ARCHAIC_FORMAL_FORM_OF_NA_ADJECTIVE, "adj-nari");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUNS_WHICH_MAY_TAKE_THE_GENITIVE_CASE_PARTICLE_NO, "adj-no");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.PRE_NOUN_ADJECTIVAL_RENTAISHI, "adj-pn");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.SHIKU_ADJECTIVE_ARCHAIC, "adj-shiku");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.TARU_ADJECTIVE, "adj-t");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADVERB_FUKUSHI, "adv");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADVERB_TAKING_THE_TO_PARTICLE, "adv-to");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.AUXILIARY, "aux");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.AUXILIARY_ADJECTIVE, "aux-adj");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.AUXILIARY_VERB, "aux-v");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.CONJUNCTION, "conj");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.COPULA, "cop");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.COUNTER, "ctr");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.EXPRESSIONS_PHRASES_CLAUSES_ETC, "exp");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.INTERJECTION_KANDOUSHI, "int");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_COMMON_FUTSUUMEISHI, "n");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ADVERBIAL_NOUN_FUKUSHITEKIMEISHI, "n-adv");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.PROPER_NOUN, "n-pr");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_USED_AS_A_PREFIX, "n-pref");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_USED_AS_A_SUFFIX, "n-suf");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_TEMPORAL_JISOUMEISHI, "n-t");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NUMERIC, "num");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.PRONOUN, "pn");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.PREFIX, "pref");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.PARTICLE, "prt");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.SUFFIX, "suf");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.UNCLASSIFIED, "unc");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.VERB_UNSPECIFIED, "v-unspec");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ICHIDAN_VERB, "v1");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ICHIDAN_VERB_KURERU_SPECIAL_CLASS, "v1-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_WITH_U_ENDING_ARCHAIC, "v2a-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_BU_ENDING_ARCHAIC, "v2b-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_BU_ENDING_ARCHAIC, "v2b-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_DZU_ENDING_ARCHAIC, "v2d-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_DZU_ENDING_ARCHAIC, "v2d-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_GU_ENDING_ARCHAIC, "v2g-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_GU_ENDING_ARCHAIC, "v2g-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_HU_FU_ENDING_ARCHAIC, "v2h-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_HU_FU_ENDING_ARCHAIC, "v2h-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_KU_ENDING_ARCHAIC, "v2k-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_KU_ENDING_ARCHAIC, "v2k-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_MU_ENDING_ARCHAIC, "v2m-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_MU_ENDING_ARCHAIC, "v2m-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_NU_ENDING_ARCHAIC, "v2n-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_RU_ENDING_ARCHAIC, "v2r-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_RU_ENDING_ARCHAIC, "v2r-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_SU_ENDING_ARCHAIC, "v2s-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_TSU_ENDING_ARCHAIC, "v2t-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_TSU_ENDING_ARCHAIC, "v2t-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_U_ENDING_AND_WE_CONJUGATION_ARCHAIC, "v2w-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_UPPER_CLASS_WITH_YU_ENDING_ARCHAIC, "v2y-k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_YU_ENDING_ARCHAIC, "v2y-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NIDAN_VERB_LOWER_CLASS_WITH_ZU_ENDING_ARCHAIC, "v2z-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_BU_ENDING_ARCHAIC, "v4b");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_GU_ENDING_ARCHAIC, "v4g");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_HU_FU_ENDING_ARCHAIC, "v4h");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_KU_ENDING_ARCHAIC, "v4k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_MU_ENDING_ARCHAIC, "v4m");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_NU_ENDING_ARCHAIC, "v4n");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_RU_ENDING_ARCHAIC, "v4r");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_SU_ENDING_ARCHAIC, "v4s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.YODAN_VERB_WITH_TSU_ENDING_ARCHAIC, "v4t");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_ARU_SPECIAL_CLASS, "v5aru");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_BU_ENDING, "v5b");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_GU_ENDING, "v5g");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_KU_ENDING, "v5k");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_IKU_YUKU_SPECIAL_CLASS, "v5k-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_MU_ENDING, "v5m");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_NU_ENDING, "v5n");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_RU_ENDING, "v5r");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_RU_ENDING_IRREGULAR_VERB, "v5r-i");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_SU_ENDING, "v5s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_TSU_ENDING, "v5t");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_U_ENDING, "v5u");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_WITH_U_ENDING_SPECIAL_CLASS, "v5u-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.GODAN_VERB_URU_OLD_CLASS_VERB_OLD_FORM_OF_ERU, "v5uru");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.INTRANSITIVE_VERB, "vi");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.KURU_VERB_SPECIAL_CLASS, "vk");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.IRREGULAR_NU_VERB, "vn");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.IRREGULAR_RU_VERB_PLAIN_FORM_ENDS_WITH_RI, "vr");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.NOUN_OR_PARTICIPLE_WHICH_TAKES_THE_AUX_VERB_SURU, "vs");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.SU_VERB_PRECURSOR_TO_THE_MODERN_SURU, "vs-c");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.SURU_VERB_INCLUDED, "vs-i");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.SURU_VERB_SPECIAL_CLASS, "vs-s");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.TRANSITIVE_VERB, "vt");
		partOfSpeechEnumToEntityMapper.put(PartOfSpeechEnum.ICHIDAN_VERB_ZURU_VERB_ALTERNATIVE_FORM_OF_JIRU_VERBS, "vz");
		
		// odwrocenie partOfSpeechEnumToEntityMapper
		for (Map.Entry<PartOfSpeechEnum, String> partOfSpeechEnumToEntityMapperKeySet : partOfSpeechEnumToEntityMapper.entrySet()) {
			entityToPartOfSpeechEnumMapper.put(partOfSpeechEnumToEntityMapperKeySet.getValue(), partOfSpeechEnumToEntityMapperKeySet.getKey());
		}		
		
		//
		
		miscEnumToEntityMapper.put(MiscEnum.ABBREVIATION, "abbr");
		miscEnumToEntityMapper.put(MiscEnum.ARCHAIC, "arch");
		miscEnumToEntityMapper.put(MiscEnum.CHARACTER, "char");
		miscEnumToEntityMapper.put(MiscEnum.CHILDREN_S_LANGUAGE, "chn");
		miscEnumToEntityMapper.put(MiscEnum.COLLOQUIAL, "col");
		miscEnumToEntityMapper.put(MiscEnum.COMPANY_NAME, "company");
		miscEnumToEntityMapper.put(MiscEnum.CREATURE, "creat");
		miscEnumToEntityMapper.put(MiscEnum.DATED_TERM, "dated");
		miscEnumToEntityMapper.put(MiscEnum.DEITY, "dei");
		miscEnumToEntityMapper.put(MiscEnum.DEROGATORY, "derog");
		miscEnumToEntityMapper.put(MiscEnum.DOCUMENT, "doc");
		miscEnumToEntityMapper.put(MiscEnum.EUPHEMISTIC, "euph");
		miscEnumToEntityMapper.put(MiscEnum.EVENT, "ev");
		miscEnumToEntityMapper.put(MiscEnum.FAMILIAR_LANGUAGE, "fam");
		miscEnumToEntityMapper.put(MiscEnum.FEMALE_TERM_OR_LANGUAGE, "fem");
		miscEnumToEntityMapper.put(MiscEnum.FICTION, "fict");
		miscEnumToEntityMapper.put(MiscEnum.FORMAL_OR_LITERARY_TERM, "form");
		miscEnumToEntityMapper.put(MiscEnum.GIVEN_NAME_OR_FORENAME_GENDER_NOT_SPECIFIED, "given");
		miscEnumToEntityMapper.put(MiscEnum.GROUP, "group");
		miscEnumToEntityMapper.put(MiscEnum.HISTORICAL_TERM, "hist");
		miscEnumToEntityMapper.put(MiscEnum.HONORIFIC_OR_RESPECTFUL_SONKEIGO_LANGUAGE, "hon");
		miscEnumToEntityMapper.put(MiscEnum.HUMBLE_KENJOUGO_LANGUAGE, "hum");
		miscEnumToEntityMapper.put(MiscEnum.IDIOMATIC_EXPRESSION, "id");
		miscEnumToEntityMapper.put(MiscEnum.JOCULAR_HUMOROUS_TERM, "joc");
		miscEnumToEntityMapper.put(MiscEnum.LEGEND, "leg");
		miscEnumToEntityMapper.put(MiscEnum.MANGA_SLANG, "m-sl");
		miscEnumToEntityMapper.put(MiscEnum.MALE_TERM_OR_LANGUAGE, "male");
		miscEnumToEntityMapper.put(MiscEnum.MYTHOLOGY, "myth");
		miscEnumToEntityMapper.put(MiscEnum.INTERNET_SLANG, "net-sl");
		miscEnumToEntityMapper.put(MiscEnum.OBJECT, "obj");
		miscEnumToEntityMapper.put(MiscEnum.OBSOLETE_TERM, "obs");
		miscEnumToEntityMapper.put(MiscEnum.ONOMATOPOEIC_OR_MIMETIC_WORD, "on-mim");
		miscEnumToEntityMapper.put(MiscEnum.ORGANIZATION_NAME, "organization");
		miscEnumToEntityMapper.put(MiscEnum.OTHER, "oth");
		miscEnumToEntityMapper.put(MiscEnum.FULL_NAME_OF_A_PARTICULAR_PERSON, "person");
		miscEnumToEntityMapper.put(MiscEnum.PLACE_NAME, "place");
		miscEnumToEntityMapper.put(MiscEnum.POETICAL_TERM, "poet");
		miscEnumToEntityMapper.put(MiscEnum.POLITE_TEINEIGO_LANGUAGE, "pol");
		miscEnumToEntityMapper.put(MiscEnum.PRODUCT_NAME, "product");
		miscEnumToEntityMapper.put(MiscEnum.PROVERB, "proverb");
		miscEnumToEntityMapper.put(MiscEnum.QUOTATION, "quote");
		miscEnumToEntityMapper.put(MiscEnum.RARE_TERM, "rare");
		miscEnumToEntityMapper.put(MiscEnum.RELIGION, "relig");
		miscEnumToEntityMapper.put(MiscEnum.SENSITIVE, "sens");
		miscEnumToEntityMapper.put(MiscEnum.SERVICE, "serv");
		miscEnumToEntityMapper.put(MiscEnum.SHIP_NAME, "ship");
		miscEnumToEntityMapper.put(MiscEnum.SLANG, "sl");
		miscEnumToEntityMapper.put(MiscEnum.RAILWAY_STATION, "station");
		miscEnumToEntityMapper.put(MiscEnum.FAMILY_OR_SURNAME, "surname");
		miscEnumToEntityMapper.put(MiscEnum.WORD_USUALLY_WRITTEN_USING_KANA_ALONE, "uk");
		miscEnumToEntityMapper.put(MiscEnum.UNCLASSIFIED_NAME, "unclass");
		miscEnumToEntityMapper.put(MiscEnum.VULGAR_EXPRESSION_OR_WORD, "vulg");
		miscEnumToEntityMapper.put(MiscEnum.WORK_OF_ART_LITERATURE_MUSIC_ETC_NAME, "work");
		miscEnumToEntityMapper.put(MiscEnum.RUDE_OR_X_RATED_TERM_NOT_DISPLAYED_IN_EDUCATIONAL_SOFTWARE, "X");
		miscEnumToEntityMapper.put(MiscEnum.YOJIJUKUGO, "yoji");

		//
		
		dialectEnumToEntityMapper.put(DialectEnum.BRAZILIAN, "bra");
		dialectEnumToEntityMapper.put(DialectEnum.HOKKAIDO_BEN, "hob");
		dialectEnumToEntityMapper.put(DialectEnum.KANSAI_BEN, "ksb");
		dialectEnumToEntityMapper.put(DialectEnum.KANTOU_BEN, "ktb");
		dialectEnumToEntityMapper.put(DialectEnum.KYOTO_BEN, "kyb");
		dialectEnumToEntityMapper.put(DialectEnum.KYUUSHUU_BEN, "kyu");
		dialectEnumToEntityMapper.put(DialectEnum.NAGANO_BEN, "nab");
		dialectEnumToEntityMapper.put(DialectEnum.OSAKA_BEN, "osb");
		dialectEnumToEntityMapper.put(DialectEnum.RYUUKYUU_BEN, "rkb");
		dialectEnumToEntityMapper.put(DialectEnum.TOUHOKU_BEN, "thb");
		dialectEnumToEntityMapper.put(DialectEnum.TOSA_BEN, "tsb");
		dialectEnumToEntityMapper.put(DialectEnum.TSUGARU_BEN, "tsug");
		
		//
		
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.ATEJI_PHONETIC_READING, "ateji");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.WORD_CONTAINING_IRREGULAR_KANA_USAGE, "ik");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.WORD_CONTAINING_IRREGULAR_KANJI_USAGE, "iK");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.IRREGULAR_OKURIGANA_USAGE, "io");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.WORD_CONTAINING_OUT_DATED_KANJI_OR_KANJI_USAGE, "oK");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.RARELY_USED_KANJI_FORM, "rK");
		kanjiAdditionalInfoEnumToEntityMapper.put(KanjiAdditionalInfoEnum.SEARCH_ONLY_KANJI_FORM, "sK");
		
		//
		
		readingAdditionalInfoEnumToEntityMapper.put(ReadingAdditionalInfoEnum.GIKUN_MEANING_AS_READING_OR_JUKUJIKUN_SPECIAL_KANJI_READING, "gikun");
		readingAdditionalInfoEnumToEntityMapper.put(ReadingAdditionalInfoEnum.WORD_CONTAINING_IRREGULAR_KANA_USAGE, "ik");
		readingAdditionalInfoEnumToEntityMapper.put(ReadingAdditionalInfoEnum.OUT_DATED_OR_OBSOLETE_KANA_USAGE, "ok");
		readingAdditionalInfoEnumToEntityMapper.put(ReadingAdditionalInfoEnum.SEARCH_ONLY_KANA_FORM, "sk");		
		readingAdditionalInfoEnumToEntityMapper.put(ReadingAdditionalInfoEnum.RARELY_USED_KANA_FORM, "rk");
		
		//
		
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.UNCLASSIFIED_NAME, DictionaryEntryType.WORD_UNCLASS_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.PLACE_NAME, DictionaryEntryType.WORD_PLACE);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.GIVEN_NAME_OR_FORENAME_GENDER_NOT_SPECIFIED, DictionaryEntryType.WORD_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.COMPANY_NAME, DictionaryEntryType.WORD_COMPANY_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.WORK_OF_ART_LITERATURE_MUSIC_ETC_NAME, DictionaryEntryType.WORD_WORK);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.MALE_GIVEN_NAME_OR_FORENAME, DictionaryEntryType.WORD_MALE_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.FULL_NAME_OF_A_PARTICULAR_PERSON, DictionaryEntryType.WORD_PERSON);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.FAMILY_OR_SURNAME, DictionaryEntryType.WORD_SURNAME_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.PRODUCT_NAME, DictionaryEntryType.WORD_PRODUCT_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.ORGANIZATION_NAME, DictionaryEntryType.WORD_ORGANIZATION_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.RAILWAY_STATION, DictionaryEntryType.WORD_STATION_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.CHARACTER, DictionaryEntryType.WORD_CHARACTER);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.CREATURE, DictionaryEntryType.WORD_CREATURE);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.DEITY, DictionaryEntryType.WORD_DEITY);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.EVENT, DictionaryEntryType.WORD_EVENT);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.FEMALE_GIVEN_NAME_OR_FORENAME, DictionaryEntryType.WORD_FEMALE_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.FICTION, DictionaryEntryType.WORD_FICT);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.LEGEND, DictionaryEntryType.WORD_LEGEND);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.MYTHOLOGY, DictionaryEntryType.WORD_MYTHOLOGY);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.OBJECT, DictionaryEntryType.WORD_OBJECT);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.OTHER, DictionaryEntryType.WORD_OTHER);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.RELIGION, DictionaryEntryType.WORD_RELIGION);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.SERVICE, DictionaryEntryType.WORD_SERVICE);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.SHIP_NAME, DictionaryEntryType.WORD_SHIP_NAME);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.GROUP, DictionaryEntryType.WORD_GROUP);
		translationalInfoNameTypeToDictionaryEntryMapper.put(TranslationalInfoNameType.DOCUMENT, DictionaryEntryType.WORD_DOCUMENT);
	}

	private void addMap(DictionaryEntryType dictionaryEntryType, String entity) {
		
		List<String> entityList = dictionaryEntryToEntityMapper.get(dictionaryEntryType);
		
		if (entityList == null) {
			entityList = new ArrayList<String>();
			
			dictionaryEntryToEntityMapper.put(dictionaryEntryType, entityList);
		}
		
		if (entity != null) {
			entityList.add(entity);
		}
		
		//
		
		if (entity != null) {
			entityToDictionaryEntryMapper.put(entity, dictionaryEntryType);
		}
	}
	
	private void addNullMap(String entity) {		
		entityToDictionaryEntryMapper.put(entity, null);		
	}
	
	public List<String> getEntity(DictionaryEntryType dictionaryEntryType) throws DictionaryException {
				
		List<String> entityList = dictionaryEntryToEntityMapper.get(dictionaryEntryType);
		
		if (entityList == null) {
			throw new DictionaryException("Empty entity for: " + dictionaryEntryType);
		}
		
		return entityList;
	}
	
	public DictionaryEntryType getDictionaryEntryType(String entity) throws DictionaryException {
		
		if (entityToDictionaryEntryMapper.containsKey(entity) == false) {
			throw new DictionaryException("Empty DictionaryEntryType for: " + entity);
		}
		
		DictionaryEntryType dictionaryEntryType = entityToDictionaryEntryMapper.get(entity);
		
		if (dictionaryEntryType == DictionaryEntryType.WORD_EMPTY) {
			return null;
		}
		
		return dictionaryEntryType;
	}
	
	public String getPartOfSpeechAsEntity(PartOfSpeechEnum partOfSpeech) throws DictionaryException {
		
		String partOfSpeechEnumAsEntity = partOfSpeechEnumToEntityMapper.get(partOfSpeech);
		
		if (partOfSpeechEnumAsEntity == null) {
			throw new RuntimeException("partOfSpeechEnumAsEntity: " + partOfSpeechEnumAsEntity);
		}
		
		return partOfSpeechEnumAsEntity;
	}

	public String getMiscEnumAsEntity(MiscEnum miscEnum) throws DictionaryException {
		
		String miscEnumAsEntity = miscEnumToEntityMapper.get(miscEnum);
		
		if (miscEnumAsEntity == null) {
			throw new RuntimeException("miscEnumAsEntity: " + miscEnumAsEntity);
		}
		
		return miscEnumAsEntity;
	}

	public String getDialectEnumAsEntity(DialectEnum dialectEnum) throws DictionaryException {
		
		String dialectEnumAsEntity = dialectEnumToEntityMapper.get(dialectEnum);
		
		if (dialectEnumAsEntity == null) {
			throw new RuntimeException("dialectEnumAsEntity: " + dialectEnumAsEntity);
		}
		
		return dialectEnumAsEntity;
	}

	public String getKanjiAdditionalInfoEnumAsEntity(KanjiAdditionalInfoEnum kanjiAdditionalInfoEnum) throws DictionaryException {
		
		String kanjiAdditionalInfoEnumAsEntity = kanjiAdditionalInfoEnumToEntityMapper.get(kanjiAdditionalInfoEnum);
		
		if (kanjiAdditionalInfoEnumAsEntity == null) {
			throw new RuntimeException("kanjiAdditionalInfoEnumAsEntity: " + kanjiAdditionalInfoEnumAsEntity);
		}
		
		return kanjiAdditionalInfoEnumAsEntity;
	}

	public String getReadingAdditionalInfoEnumAsEntity(ReadingAdditionalInfoEnum readingAdditionalInfoEnum) throws DictionaryException {
		
		String readingAdditionalInfoEnumAsEntity = readingAdditionalInfoEnumToEntityMapper.get(readingAdditionalInfoEnum);
		
		if (readingAdditionalInfoEnumAsEntity == null) {
			throw new RuntimeException("readingAdditionalInfoEnumAsEntity: " + readingAdditionalInfoEnumAsEntity);
		}
		
		return readingAdditionalInfoEnumAsEntity;
	}

	public DictionaryEntryType getDictionaryEntryType(PartOfSpeechEnum partOfSpeech) throws DictionaryException {
				
		DictionaryEntryType dictionaryEntryType = getDictionaryEntryType(getPartOfSpeechAsEntity(partOfSpeech));

		return dictionaryEntryType;
	}
	
	public List<PartOfSpeechEnum> getPartOfSpeechEnumFromDictionaryEntryType(PolishJapaneseEntry polishJapaneseEntry) {
		
		// pobieramy wszystkie stare typy
		List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
		
		// lista wynikowa
		List<PartOfSpeechEnum> result = new ArrayList<>();
		
		for (DictionaryEntryType dictionaryEntryType : dictionaryEntryTypeList) {
						
			// sprawdzenie, czy to nie jeden z dziwnych 'specjalnych' typow
			if (dictionaryEntryType == DictionaryEntryType.WORD_SURNAME_NAME || dictionaryEntryType == DictionaryEntryType.WORD_NAME || 
					dictionaryEntryType == DictionaryEntryType.WORD_PERSON || dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME
					|| dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				
				result.add(PartOfSpeechEnum.PROPER_NOUN);
			
			} else if (dictionaryEntryType == DictionaryEntryType.WORD_EMPTY || dictionaryEntryType == DictionaryEntryType.UNKNOWN) {
				result.add(PartOfSpeechEnum.UNCLASSIFIED);
					
			} else {
				// mapowanie na nowe typy zapisane w entity
				List<String> newDictionaryEntryTypeMapperAsNewEntity = dictionaryEntryToEntityMapper.get(dictionaryEntryType);
				
				// tutaj bedziemy mieli tylko propozycje wpisow, trzeba jeszcze to uszczegolowic
				List<PartOfSpeechEnum> proposalPartOfSpeechEnumList = 
						newDictionaryEntryTypeMapperAsNewEntity.stream().map(m -> {
							PartOfSpeechEnum partOfSpeechEnum = entityToPartOfSpeechEnumMapper.get(m);
							
							if (partOfSpeechEnum == null) {
								throw new RuntimeException("Can't find part of speech enum: " + partOfSpeechEnum); // to nigdy nie powinno zdarzyc sie
							}
							
							return partOfSpeechEnum;
						}).collect(Collectors.toList());
				
				if (proposalPartOfSpeechEnumList.size() == 0) {
					throw new RuntimeException("Can't find any proposal of part of speech for: " + polishJapaneseEntry);
					
				} else if (proposalPartOfSpeechEnumList.size() == 1) { // sprawa jest latwa
					result.add(proposalPartOfSpeechEnumList.get(0));
										
				} else { // trzeba uszczegolowic typ
					
					if (dictionaryEntryType == DictionaryEntryType.WORD_NOUN) {
						result.add(PartOfSpeechEnum.NOUN_COMMON_FUTSUUMEISHI);
						
						continue;
						
					} else if (dictionaryEntryType == DictionaryEntryType.WORD_VERB_U) {
						
						// trzeba sprawdzic, ktory jest to typ (dziwne typy sa pomijane, ale ich nie powinno byc
						String romaji = polishJapaneseEntry.getRomaji();
						
						PartOfSpeechEnum partOfSpeechEnum;
						
						boolean endsWithAru = romaji.endsWith("aru") == true;
						boolean endsWithIkuOrYuku = romaji.endsWith("iku") == true || romaji.endsWith("yuku") == true;
						boolean endsWithBu = romaji.endsWith("bu");
						boolean endsWithGu = romaji.endsWith("gu");
						boolean endsWithKu = romaji.endsWith("ku");
						boolean endsWithMu = romaji.endsWith("mu");
						boolean endsWithNu = romaji.endsWith("nu");
						boolean endsWithRu = romaji.endsWith("ru");
						boolean endsWithSu = romaji.endsWith("su");
						boolean endsWithTsu = romaji.endsWith("tsu");
						boolean endsWithU = romaji.endsWith("u");
						
						if (endsWithAru == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_ARU_SPECIAL_CLASS;
							
						} else if (endsWithIkuOrYuku == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_IKU_YUKU_SPECIAL_CLASS;
							
						} else if (endsWithBu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_BU_ENDING;

						} else if (endsWithGu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_GU_ENDING;						
							
						} else if (endsWithKu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_KU_ENDING;						

						} else if (endsWithMu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_MU_ENDING;

						} else if (endsWithNu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_NU_ENDING;
							
						} else if (endsWithRu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_RU_ENDING;

						} else if (endsWithTsu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_TSU_ENDING;

						} else if (endsWithSu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_SU_ENDING;

						} else if (endsWithU == true) {
							partOfSpeechEnum = PartOfSpeechEnum.GODAN_VERB_WITH_U_ENDING;

						} else {
							throw new RuntimeException("Can't detect u verb part of speech: " + polishJapaneseEntry);
						}					
												
						result.add(partOfSpeechEnum);						
						
						continue;
						
					} else if (dictionaryEntryType == DictionaryEntryType.WORD_VERB_RU) {
						
						// trzeba sprawdzic, ktory jest to typ (dziwne typy sa pomijane, ale ich nie powinno byc
						String romaji = polishJapaneseEntry.getRomaji();
						
						PartOfSpeechEnum partOfSpeechEnum;
						
						boolean endsWithRu = romaji.endsWith("ru");
						boolean endsWithKureru = romaji.endsWith("kureru");
						
						if (endsWithKureru == true) {
							partOfSpeechEnum = PartOfSpeechEnum.ICHIDAN_VERB_KURERU_SPECIAL_CLASS;
							
						} else if (endsWithRu == true) {
							partOfSpeechEnum = PartOfSpeechEnum.ICHIDAN_VERB;
							
						} else {
							throw new RuntimeException("Can't detect u verb part of speech: " + polishJapaneseEntry);
						}					
												
						result.add(partOfSpeechEnum);						
						
						continue;
						
					} else if (dictionaryEntryType == DictionaryEntryType.WORD_VERB_IRREGULAR) {
						
						// trzeba sprawdzic, czy to suru, czy kuru						
						String romaji = polishJapaneseEntry.getRomaji();
						
						PartOfSpeechEnum irregularVerbPartOfSpeech;
						
						if (romaji.endsWith("kuru") == true) {
							irregularVerbPartOfSpeech = PartOfSpeechEnum.KURU_VERB_SPECIAL_CLASS;
							
						} else if (romaji.endsWith("suru") == true) {
							irregularVerbPartOfSpeech = PartOfSpeechEnum.SURU_VERB_INCLUDED;
							
						} else {
							throw new RuntimeException("Can't detect irregular verb part of speech: " + polishJapaneseEntry);
						}
						
						result.add(irregularVerbPartOfSpeech);						
						
						continue;
						
					} else if (dictionaryEntryType == DictionaryEntryType.WORD_ADJECTIVE_I) {
						
						// trzeba sprawdzic, ktory jest to typ
						String romaji = polishJapaneseEntry.getRomaji();
						
						PartOfSpeechEnum partOfSpeechEnum;
						
						boolean endsWithIIorYoi = romaji.endsWith("ii") || romaji.endsWith("yoi");
						boolean endsWithI = romaji.endsWith("i");
						
						if (endsWithIIorYoi == true) {
							partOfSpeechEnum = PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI_YOI_II_CLASS;
							
						} else if (endsWithI == true) {
							partOfSpeechEnum = PartOfSpeechEnum.ADJECTIVE_KEIYOUSHI;
							
						} else {
							throw new RuntimeException("Can't detect u verb part of speech: " + polishJapaneseEntry);
						}					
												
						result.add(partOfSpeechEnum);						
						
						continue;						
					}
					
					// to nigdy nie powinno zdarzyc sie
					throw new RuntimeException("Can't detect part of speech: " + polishJapaneseEntry);
				}								
			}
		}		
		
		return result;
	}
	

	public DictionaryEntryType getDictionaryEntryType(TranslationalInfoNameType translationalInfoName) {

		DictionaryEntryType dictionaryEntryType = translationalInfoNameTypeToDictionaryEntryMapper.get(translationalInfoName);
		
		if (dictionaryEntryType == null) {
			throw new RuntimeException("getDictionaryEntryType: " + translationalInfoName);
		}
		
		return dictionaryEntryType;		
	}
}
