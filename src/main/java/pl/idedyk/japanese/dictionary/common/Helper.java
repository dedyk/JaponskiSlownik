package pl.idedyk.japanese.dictionary.common;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.TransitiveIntransitivePair;
import pl.idedyk.japanese.dictionary.tools.EdictReader;

import com.csvreader.CsvWriter;

public class Helper {

	public static List<PolishJapaneseEntry> generateGroups(List<PolishJapaneseEntry> polishJapaneseEntries,
			boolean addOtherGroup) {

		// generate groups

		Map<String, List<String>> polishJapaneseEntriesAndGroups = new HashMap<String, List<String>>();

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			List<String> currentPolishJapaneseEntryGroups = GroupEnum.convertToValues(currentPolishJapaneseEntry.getGroups());

			if (currentPolishJapaneseEntryGroups == null || currentPolishJapaneseEntryGroups.size() == 0) {
				continue;
			}

			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();

			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups
					.get(entryPrefixKanaKanjiKanaKey);

			if (groupsForCurrentPolishJapaneseEntry == null) {
				groupsForCurrentPolishJapaneseEntry = new ArrayList<String>();
			}

			for (String currentEntryOfCurrentPolishJapaneseEntryGroups : currentPolishJapaneseEntryGroups) {

				if (groupsForCurrentPolishJapaneseEntry.contains(currentEntryOfCurrentPolishJapaneseEntryGroups) == false) {
					groupsForCurrentPolishJapaneseEntry.add(currentEntryOfCurrentPolishJapaneseEntryGroups);
				}
			}

			polishJapaneseEntriesAndGroups.put(entryPrefixKanaKanjiKanaKey, groupsForCurrentPolishJapaneseEntry);
		}

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);

			String entryPrefixKanaKanjiKanaKey = currentPolishJapaneseEntry.getEntryPrefixKanaKanjiKanaKey();

			List<String> groupsForCurrentPolishJapaneseEntry = polishJapaneseEntriesAndGroups
					.get(entryPrefixKanaKanjiKanaKey);

			if (groupsForCurrentPolishJapaneseEntry == null) {
				groupsForCurrentPolishJapaneseEntry = new ArrayList<String>();

				if (addOtherGroup == true) {
					groupsForCurrentPolishJapaneseEntry.add("Inne");
				}

			}

			currentPolishJapaneseEntry.setGroups(GroupEnum.convertToListGroupEnum(groupsForCurrentPolishJapaneseEntry));
		}

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			polishJapaneseEntries.get(idx).setId(result.size() + 1);

			result.add(polishJapaneseEntries.get(idx));
		}

		return result;
	}

	public static void generateAdditionalInfoFromEdict(JMENewDictionary jmeNewDictionary,
			TreeMap<String, EDictEntry> jmedictCommon, List<PolishJapaneseEntry> polishJapaneseEntries) throws DictionaryException {

		for (int idx = 0; idx < polishJapaneseEntries.size(); ++idx) {

			PolishJapaneseEntry currentPolishJapaneseEntry = polishJapaneseEntries.get(idx);
			
			String kanji = currentPolishJapaneseEntry.getKanji();
			String kana = currentPolishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
				
				GroupEntry groupEntry = groupEntryList.get(0);
				
				Set<String> groupEntryWordTypeList = groupEntry.getWordTypeList();
				
				if (groupEntryWordTypeList.size() == 0) {
					continue;
				}

				EDictEntry foundEdictCommon = findEdictEntry(jmedictCommon, currentPolishJapaneseEntry);

				AttributeList attributeList = currentPolishJapaneseEntry.getAttributeList();
				
				// common word
				if (foundEdictCommon != null) {

					if (attributeList.contains(AttributeType.COMMON_WORD) == false) {
						attributeList.add(0, AttributeType.COMMON_WORD);
					}
				}
				
				// suru verb
				List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();

				if (groupEntryWordTypeList.contains("vs") == true && attributeList.contains(AttributeType.SURU_VERB) == false) {					
					attributeList.add(AttributeType.SURU_VERB);
				}
				
				// transitivity, intransitivity
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true
						|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true
						|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true) {

					if (attributeList.contains(AttributeType.VERB_TRANSITIVITY) == false
							&& attributeList.contains(AttributeType.VERB_INTRANSITIVITY) == false) {

						if (groupEntryWordTypeList.contains("vt") == true) {
							attributeList.add(AttributeType.VERB_TRANSITIVITY);

						} else if (groupEntryWordTypeList.contains("vi") == true) {
							attributeList.add(AttributeType.VERB_INTRANSITIVITY);
						}
					}
				}
				
				// kanji/kana alone
				if (attributeList.contains(AttributeType.KANJI_ALONE) == false && groupEntryWordTypeList.contains("uK") == true) {
					attributeList.add(AttributeType.KANJI_ALONE);
				}

				if (attributeList.contains(AttributeType.KANA_ALONE) == false && groupEntryWordTypeList.contains("uk") == true) {
					attributeList.add(AttributeType.KANA_ALONE);
				}

				// archaism
				/*
				if (attributeList.contains(AttributeType.ARCHAISM) == false && groupEntryWordTypeList.contains("arch") == true) {
					attributeList.add(AttributeType.ARCHAISM);
				}
				*/

				// obsolete
				if (attributeList.contains(AttributeType.OBSOLETE) == false && (groupEntryWordTypeList.contains("obs") == true || groupEntryWordTypeList.contains("ok") == true)) {
					attributeList.add(AttributeType.OBSOLETE);
				}

				// obsure
				if (attributeList.contains(AttributeType.OBSCURE) == false && groupEntryWordTypeList.contains("obsc") == true) {
					attributeList.add(AttributeType.OBSCURE);
				}

				// suffix
				if (attributeList.contains(AttributeType.SUFFIX) == false && groupEntryWordTypeList.contains("suf") == true) {
					attributeList.add(AttributeType.SUFFIX);
				}

				// noun suffix
				if (attributeList.contains(AttributeType.NOUN_SUFFIX) == false && groupEntryWordTypeList.contains("n-suf") == true) {
					attributeList.add(AttributeType.NOUN_SUFFIX);
				}

				// prefix
				if (attributeList.contains(AttributeType.PREFIX) == false && groupEntryWordTypeList.contains("pref") == true) {
					attributeList.add(AttributeType.PREFIX);
				}

				// noun prefix
				if (attributeList.contains(AttributeType.NOUN_PREFIX) == false && groupEntryWordTypeList.contains("n-pref") == true) {
					attributeList.add(AttributeType.NOUN_PREFIX);
				}
									
				// onamatopoeic or mimetic word
				if (attributeList.contains(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD) == false && groupEntryWordTypeList.contains("on-mim") == true) {
					attributeList.add(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD);
				}
			}
		}
		
		// generowanie alternatyw
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			List<PolishJapaneseEntry> foundPolishJapaneseEntryGroupList = new ArrayList<PolishJapaneseEntry>();
			
			if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
					
					List<GroupEntry> groupEntryList2 = jmeNewDictionary.getGroupEntryList(groupEntryKanji, groupEntryKana);
					
					if (isMultiGroup(groupEntryList2) == false) {
						
						PolishJapaneseEntry findPolishJapaneseEntry = findPolishJapaneseEntry2(polishJapaneseEntries, 
								groupEntryKanji, groupEntryKana);
						
						if (findPolishJapaneseEntry != null) {
							foundPolishJapaneseEntryGroupList.add(findPolishJapaneseEntry);
						}
						
					}
				}
			}
			
			if (foundPolishJapaneseEntryGroupList.size() > 1) {
				
				Set<Integer> foundPolishJapaneseEntryGroupListAllIds = new TreeSet<Integer>();
				
				for (PolishJapaneseEntry currentFoundPolishJapanaeseEntryGroupList : foundPolishJapaneseEntryGroupList) {					
					foundPolishJapaneseEntryGroupListAllIds.add(currentFoundPolishJapanaeseEntryGroupList.getId());					
				}
				
				for (PolishJapaneseEntry currentFoundPolishJapanaeseEntryGroupList : foundPolishJapaneseEntryGroupList) {
					
					if (currentFoundPolishJapanaeseEntryGroupList.getParseAdditionalInfoList().contains(ParseAdditionalInfo.NO_ALTERNATIVE) == true) {
						continue;
					}
					
					if (currentFoundPolishJapanaeseEntryGroupList.getAttributeList().contains(AttributeType.ALTERNATIVE) == false) {
						
						Set<Integer> foundPolishJapaneseEntryGroupListIdsWithoutCurrentId = new TreeSet<Integer>(foundPolishJapaneseEntryGroupListAllIds);
						
						foundPolishJapaneseEntryGroupListIdsWithoutCurrentId.remove(currentFoundPolishJapanaeseEntryGroupList.getId());
						
						for (Integer currentAlternativeId : foundPolishJapaneseEntryGroupListIdsWithoutCurrentId) {
							
							currentFoundPolishJapanaeseEntryGroupList.getAttributeList().
								addAttributeValue(AttributeType.ALTERNATIVE, currentAlternativeId.toString());

						}						
					}
				}				
			}
		}				
	}

	private static boolean isMultiGroup(List<GroupEntry> groupEntryList) {
		
		Set<Integer> uniqueGroupIds = new HashSet<Integer>();
		
		for (GroupEntry groupEntry : groupEntryList) {
			uniqueGroupIds.add(groupEntry.getGroup().getId());
		}
		
		if (uniqueGroupIds.size() == 1) {			
			return false;
			
		} else {
			return true;
		}
	}

	private static EDictEntry findEdictEntry(TreeMap<String, EDictEntry> jmedict,
			PolishJapaneseEntry polishJapaneseEntry) {

		String kanji = polishJapaneseEntry.getKanji();

		if (kanji != null && kanji.equals("-") == true) {
			kanji = null;
		}

		String kana = polishJapaneseEntry.getKana();

		EDictEntry foundEdict = jmedict.get(EdictReader.getMapKey(kanji, kana));

		return foundEdict;
	}

	public static List<PolishJapaneseEntry> generateNames(TreeMap<String, List<JMEDictEntry>> jmedictName) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		Iterator<List<JMEDictEntry>> jmedictNameValuesIterator = jmedictName.values().iterator();

		/*
		- company -
		- product -
		- organization -
		
		+ fem +
		+ given +
		+ masc +		
		+ person +
		+ place +		
		+ station +
		+ surname +
		+ unclass +
		*/

		// mapowanie typow
		Map<String, DictionaryEntryType> nameTypeMapper = new HashMap<String, DictionaryEntryType>();
		
		nameTypeMapper.put("fem", DictionaryEntryType.WORD_FEMALE_NAME);
		nameTypeMapper.put("masc", DictionaryEntryType.WORD_MALE_NAME);
		nameTypeMapper.put("given", DictionaryEntryType.WORD_NAME);
		nameTypeMapper.put("surname", DictionaryEntryType.WORD_SURNAME_NAME);
		nameTypeMapper.put("person", DictionaryEntryType.WORD_PERSON);		
		nameTypeMapper.put("station", DictionaryEntryType.WORD_STATION_NAME);
		nameTypeMapper.put("place", DictionaryEntryType.WORD_PLACE);		
		nameTypeMapper.put("unclass", DictionaryEntryType.WORD_UNCLASS_NAME);
		
		nameTypeMapper.put("company", DictionaryEntryType.WORD_COMPANY_NAME);
		nameTypeMapper.put("product", DictionaryEntryType.WORD_PRODUCT_NAME);
		nameTypeMapper.put("organization", DictionaryEntryType.WORD_ORGANIZATION_NAME);
		
		int counter = 1;

		KanaHelper kanaHelper = new KanaHelper();
		
		while(jmedictNameValuesIterator.hasNext()) {
			
			List<JMEDictEntry> jmedictValueList = jmedictNameValuesIterator.next();

			for (JMEDictEntry jmedictEntry : jmedictValueList) {
				
				if (jmedictEntry.getTrans().size() == 0) {
					continue;
				}
								
				List<DictionaryEntryType> nameDictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
				
				for (String currentTran : jmedictEntry.getTrans()) {
					
					DictionaryEntryType nameDictionaryEntryType = nameTypeMapper.get(currentTran);
					
					if (nameDictionaryEntryType == null) {
						continue;
					}
					
					if (nameDictionaryEntryTypeList.contains(nameDictionaryEntryType) == false) {
						nameDictionaryEntryTypeList.add(nameDictionaryEntryType);
					}					
				}
				
				if (nameDictionaryEntryTypeList.size() == 0) {
					continue;
				}
				
				if (nameDictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PERSON) == true) {
					
					nameDictionaryEntryTypeList.remove(DictionaryEntryType.WORD_FEMALE_NAME);					
				}
				
				for (int idx = 0; idx < jmedictEntry.getKana().size(); ++idx) {
					
					String kanji = null;
					
					if (jmedictEntry.getKanji().size() > idx) {
						kanji = jmedictEntry.getKanji().get(idx);
					}
					
					String kana = jmedictEntry.getKana().get(idx);
					
					List<String> transDetList = jmedictEntry.getTransDet();					
					
					//System.out.println(kanji + " - " + kana + " - " + transDet);
					
					PolishJapaneseEntry newPolishJapaneseEntry = new PolishJapaneseEntry();
					
					newPolishJapaneseEntry.setId(counter);
					counter++;
					
					newPolishJapaneseEntry.setDictionaryEntryTypeList(nameDictionaryEntryTypeList);
					
					newPolishJapaneseEntry.setWordType(WordType.HIRAGANA_KATAKANA);
					
					newPolishJapaneseEntry.setAttributeList(new AttributeList());
					newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());

					newPolishJapaneseEntry.setKanji(kanji != null ? kanji : "-");

					newPolishJapaneseEntry.setKana(kana);

					String romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true));

					newPolishJapaneseEntry.setRomaji(romaji);

					newPolishJapaneseEntry.setTranslates(transDetList);

					newPolishJapaneseEntry.setParseAdditionalInfoList(new ArrayList<ParseAdditionalInfo>());

					//newPolishJapaneseEntry.setUseEntry(true);

					newPolishJapaneseEntry.setExampleSentenceGroupIdsList(new ArrayList<String>());
					
					fixPolishJapaneseEntryName(newPolishJapaneseEntry);
					
					result.add(newPolishJapaneseEntry);
				}
			}
		}
		
		return result;
	}
	
	private static void fixPolishJapaneseEntryName(PolishJapaneseEntry newPolishJapaneseEntry) {
				
		if (newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_FEMALE_NAME ||
				newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_MALE_NAME ||
				newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_PERSON) {
			
			String translate = newPolishJapaneseEntry.getTranslates().get(0);
			
			String romaji = newPolishJapaneseEntry.getRomaji();
						
			newPolishJapaneseEntry.setRomaji(fixRomajiForNames(romaji, translate));			
		}
		
		if (newPolishJapaneseEntry.getDictionaryEntryType() == DictionaryEntryType.WORD_STATION_NAME) {
			
			String translate = newPolishJapaneseEntry.getTranslates().get(0);
			
			translate = translate.replaceAll("Station", "(nazwa stacji)");
			
			List<String> newTranslateList = new ArrayList<String>();
			newTranslateList.add(translate);
			
			newPolishJapaneseEntry.setTranslates(newTranslateList);
		}
	}

	private static String fixRomajiForNames(String romaji, String transDet) {
				
		int transAdd = 0;
		
		StringBuffer result = new StringBuffer();
		
		for (int romajiIdx = 0; romajiIdx < romaji.length(); ++romajiIdx) {
			
			String currentRomajiChar = ("" + romaji.charAt(romajiIdx)).toLowerCase();
			
			String currentTransDetChar = null;
			
			if (romajiIdx + transAdd < transDet.length()) {
				currentTransDetChar = ("" + transDet.charAt(romajiIdx + transAdd)).toLowerCase();
			}
			
			if (currentTransDetChar == null) {
				result = null;
				
				break;
			}
			
			if (currentTransDetChar.equals(" ") == true || currentTransDetChar.equals("-") == true) {
				
				result.append(" ");
				
				transAdd++;
				
				if (romajiIdx + transAdd < transDet.length()) {
					currentTransDetChar = ("" + transDet.charAt(romajiIdx + transAdd)).toLowerCase();
				}				
			}
			
			if (currentTransDetChar == null) {
				result = null;
				
				break;
			}
			
			if (currentRomajiChar.equals(currentTransDetChar) == true) {
				result.append(currentRomajiChar);
				
			} else {
				result = null;
				
				break;
			}
		}
		
		if (result != null) {
			return result.toString();
			
		} else {
			return romaji;
		}		
	}

	public static void generateTransitiveIntransitivePairs(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList,
			List<PolishJapaneseEntry> polishJapaneseEntryList, String transitiveIntransitivePairsOutputFile)
			throws Exception {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {

			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();

			AttributeList attributeList = polishJapaneseEntry.getAttributeList();

			if (attributeList.contains(AttributeType.VERB_TRANSITIVITY) == true) {

				TransitiveIntransitivePair transitiveIntransitivePair = findTransitiveIntransitivePairFromTransitiveVerb(
						transitiveIntransitivePairList, kanji, kana);

				if (transitiveIntransitivePair != null) {

					PolishJapaneseEntry intransitivePolishJapaneseEntry = findPolishJapaneseEntry(
							polishJapaneseEntryList, transitiveIntransitivePair.getIntransitiveKanji(),
							transitiveIntransitivePair.getIntransitiveKana());

					if (intransitivePolishJapaneseEntry != null) {

						attributeList.addAttributeValue(AttributeType.VERB_INTRANSITIVITY_PAIR,
								String.valueOf(intransitivePolishJapaneseEntry.getId()));
					}
				}

			} else if (attributeList.contains(AttributeType.VERB_INTRANSITIVITY) == true) {

				TransitiveIntransitivePair transitiveIntransitivePair = findTransitiveIntransitivePairFromIntransitiveVerb(
						transitiveIntransitivePairList, kanji, kana);

				if (transitiveIntransitivePair != null) {

					PolishJapaneseEntry transitivePolishJapaneseEntry = findPolishJapaneseEntry(
							polishJapaneseEntryList, transitiveIntransitivePair.getTransitiveKanji(),
							transitiveIntransitivePair.getTransitiveKana());

					if (transitivePolishJapaneseEntry != null) {

						attributeList.addAttributeValue(AttributeType.VERB_TRANSITIVITY_PAIR,
								String.valueOf(transitivePolishJapaneseEntry.getId()));
					}
				}
			}
		}

		CsvWriter csvWriter = new CsvWriter(new FileWriter(transitiveIntransitivePairsOutputFile), ',');

		for (TransitiveIntransitivePair currentTransitiveIntransitivePair : transitiveIntransitivePairList) {

			PolishJapaneseEntry transitivePolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntryList,
					currentTransitiveIntransitivePair.getTransitiveKanji(),
					currentTransitiveIntransitivePair.getTransitiveKana());

			if (transitivePolishJapaneseEntry == null) {
				continue;
			}

			PolishJapaneseEntry intransitivePolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntryList,
					currentTransitiveIntransitivePair.getIntransitiveKanji(),
					currentTransitiveIntransitivePair.getIntransitiveKana());

			if (intransitivePolishJapaneseEntry == null) {
				continue;
			}

			csvWriter.write(String.valueOf(transitivePolishJapaneseEntry.getId()));
			csvWriter.write(String.valueOf(intransitivePolishJapaneseEntry.getId()));

			csvWriter.endRecord();
		}

		csvWriter.close();
	}

	private static TransitiveIntransitivePair findTransitiveIntransitivePairFromTransitiveVerb(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList, String transitiveKanjiToFound,
			String transitiveKanaToFound) {

		for (TransitiveIntransitivePair transitiveIntransitivePair : transitiveIntransitivePairList) {

			String transitiveKanji = transitiveIntransitivePair.getTransitiveKanji();
			String transitiveKana = transitiveIntransitivePair.getTransitiveKana();

			if (transitiveKanji.equals("") == true) {
				transitiveKanji = "-";
			}

			if (transitiveKanji.equals(transitiveKanjiToFound) == true
					&& transitiveKana.equals(transitiveKanaToFound) == true) {

				return transitiveIntransitivePair;
			}
		}

		return null;
	}

	private static TransitiveIntransitivePair findTransitiveIntransitivePairFromIntransitiveVerb(
			List<TransitiveIntransitivePair> transitiveIntransitivePairList, String intransitiveKanjiToFound,
			String intransitiveKanaToFound) {

		for (TransitiveIntransitivePair transitiveIntransitivePair : transitiveIntransitivePairList) {

			String intransitiveKanji = transitiveIntransitivePair.getIntransitiveKanji();
			String intransitiveKana = transitiveIntransitivePair.getIntransitiveKana();

			if (intransitiveKanji.equals("") == true) {
				intransitiveKanji = "-";
			}

			if (intransitiveKanji.equals(intransitiveKanjiToFound) == true
					&& intransitiveKana.equals(intransitiveKanaToFound) == true) {

				return transitiveIntransitivePair;
			}
		}

		return null;
	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			String kanji, String kana) {

		if (kanji.equals("") == true) {
			kanji = "-";
		}

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();

			if (polishJapaneseEntryKanji.equals("") == true || polishJapaneseEntryKanji.equals("-") == true) {
				polishJapaneseEntryKanji = "-";
			}

			if (kanji.equals(polishJapaneseEntryKanji) == true) {

				String polishJapaneseEntryKana = polishJapaneseEntry.getKana();

				if (kana.equals(polishJapaneseEntryKana) == true) {
					return polishJapaneseEntry;
				}
			}
		}

		return null;
	}
	
	private static PolishJapaneseEntry findPolishJapaneseEntry2(List<PolishJapaneseEntry> polishJapaneseEntries, 
			String findKanji, String findKana) {
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			if (kanji == null || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}

			if (findKanji == null || findKanji.equals("-") == true) {
				findKanji = "$$$NULL$$$";
			}
			
			if (kanji.equals(findKanji) == true && kana.equals(findKana) == true) {
				return polishJapaneseEntry;
			}
		}
		
		return null;
	}
}
