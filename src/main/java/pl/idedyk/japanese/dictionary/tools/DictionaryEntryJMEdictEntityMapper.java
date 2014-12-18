package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;

public class DictionaryEntryJMEdictEntityMapper {

	private Map<DictionaryEntryType, List<String>> dictionaryEntryToEntityMapper;
	
	private Map<String, DictionaryEntryType> entityToDictionaryEntryMapper;
	
	public DictionaryEntryJMEdictEntityMapper() {
		
		dictionaryEntryToEntityMapper = new TreeMap<DictionaryEntryType, List<String>>();
		entityToDictionaryEntryMapper = new TreeMap<String, DictionaryEntryType>();

		fillMaps();
	}

	private void fillMaps() {

		addMap(DictionaryEntryType.WORD_NUMBER, "num");
	
		addMap(DictionaryEntryType.WORD_NOUN, "n");
		addMap(DictionaryEntryType.WORD_NOUN, "n-pref");
		addMap(DictionaryEntryType.WORD_NOUN, "n-suf");
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
		addMap(DictionaryEntryType.WORD_AUX_ADJECTIVE_I, "aux-adj");
				
		addMap(DictionaryEntryType.WORD_PRE_NOUN_ADJECTIVAL, "adj-pn");
		
		addMap(DictionaryEntryType.WORD_VERB_RU, "v1");
		addMap(DictionaryEntryType.WORD_VERB_RU, "v1-s");
		
		addMap(DictionaryEntryType.WORD_VERB_U, "v5k");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5k-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5r");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5m");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5s");
		addMap(DictionaryEntryType.WORD_VERB_U, "v4r");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5u");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5r-i");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5t");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5g");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5b");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5n");
		
		addMap(DictionaryEntryType.WORD_VERB_U, "v5u-s");
		addMap(DictionaryEntryType.WORD_VERB_U, "vs-c");
		addMap(DictionaryEntryType.WORD_VERB_U, "vn");
		addMap(DictionaryEntryType.WORD_VERB_U, "v5aru");
		
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vk");
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vs-i");
		addMap(DictionaryEntryType.WORD_VERB_IRREGULAR, "vs-s");
		
		addMap(DictionaryEntryType.WORD_VERB_ZURU, "vz");
		
		addMap(DictionaryEntryType.WORD_NIDAN_VERB, "v2h-k");
		addMap(DictionaryEntryType.WORD_NIDAN_VERB, "v2h-s");
		
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
		
		addMap(DictionaryEntryType.WORD_EMPTY, "pref");
		addMap(DictionaryEntryType.WORD_EMPTY, "suf");
		
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
}
