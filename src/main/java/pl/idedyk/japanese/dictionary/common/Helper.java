package pl.idedyk.japanese.dictionary.common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlEnumValue;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dto.Attribute;
import pl.idedyk.japanese.dictionary.api.dto.AttributeList;
import pl.idedyk.japanese.dictionary.api.dto.AttributeType;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntry;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryGroup;
import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.api.dto.WordType;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.dto.EDictEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry.KnownDuplicateType;
import pl.idedyk.japanese.dictionary.lucene.LuceneAnalyzer;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.TransitiveIntransitivePair;
import pl.idedyk.japanese.dictionary.tools.DictionaryEntryJMEdictEntityMapper;
import pl.idedyk.japanese.dictionary.tools.EdictReader;
import pl.idedyk.japanese.dictionary.tools.JMEDictEntityMapper;

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
						
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(currentPolishJapaneseEntry);
			
			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
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
				
				// priorytet słowa
				{
					int power = Integer.MAX_VALUE;
					
					List<GroupEnum> groups = currentPolishJapaneseEntry.getGroups();
					
					for (GroupEnum groupEnum : groups) {

						if (groupEnum.getPower() < power) {
							power = groupEnum.getPower();
						}				
					}
					
					List<String> groupEntryPriorityList = groupEntryList.get(0).getPriority();
					
					for (String priority : groupEntryPriorityList) {
						
						int priorityPower = JMENewDictionary.mapPriorityToPower(priority, 100);
						
						if (priorityPower < power) {
							power = priorityPower;
						}
					}

					if (currentPolishJapaneseEntry.getTranslates().size() == 1 && currentPolishJapaneseEntry.getTranslates().get(0).equals("???") == true) {				
						power = 999;
					}
					
					//
					
					if (attributeList.contains(AttributeType.PRIORITY) == false) {						
						attributeList.addAttributeValue(AttributeType.PRIORITY, String.valueOf(power));
					}
				}
				
				// suru verb
				List<DictionaryEntryType> dictionaryEntryTypeList = currentPolishJapaneseEntry.getDictionaryEntryTypeList();

				if (groupEntry.containsAttribute("vs") == true && attributeList.contains(AttributeType.SURU_VERB) == false) {					
					attributeList.add(AttributeType.SURU_VERB);
				}
				
				// transitivity, intransitivity
				if (dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_U) == true
						|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_RU) == true
						|| dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_VERB_IRREGULAR) == true) {

					if (attributeList.contains(AttributeType.VERB_TRANSITIVITY) == false
							&& attributeList.contains(AttributeType.VERB_INTRANSITIVITY) == false) {

						if (groupEntry.containsAttribute("vt") == true) {
							attributeList.add(AttributeType.VERB_TRANSITIVITY);

						} else if (groupEntry.containsAttribute("vi") == true) {
							attributeList.add(AttributeType.VERB_INTRANSITIVITY);
						}
					}
				}
				
				// kanji/kana alone
				if (attributeList.contains(AttributeType.KANJI_ALONE) == false && groupEntry.containsAttribute("uK") == true) {
					attributeList.add(AttributeType.KANJI_ALONE);
				}

				if (attributeList.contains(AttributeType.KANA_ALONE) == false && groupEntry.containsAttribute("uk") == true) {
					attributeList.add(AttributeType.KANA_ALONE);
				}

				// archaiczny
				/*
				if (attributeList.contains(AttributeType.ARCHAIC) == false && groupEntryWordTypeList.contains("arch") == true) {
					attributeList.add(AttributeType.ARCHAIC);
				}
				*/

				// obsolete
				if (attributeList.contains(AttributeType.OBSOLETE) == false && (groupEntry.containsAttribute("obs") == true || groupEntry.containsAttribute("ok") == true)) {
					attributeList.add(AttributeType.OBSOLETE);
				}

				// obsure
				if (attributeList.contains(AttributeType.OBSCURE) == false && groupEntry.containsAttribute("obsc") == true) {
					attributeList.add(AttributeType.OBSCURE);
				}
				
				/*
				// suffix
				if (attributeList.contains(AttributeType.SUFFIX) == false && groupEntry.containsAttribute("suf") == true) {
					attributeList.add(AttributeType.SUFFIX);
				}

				// noun suffix
				if (attributeList.contains(AttributeType.NOUN_SUFFIX) == false && groupEntry.containsAttribute("n-suf") == true) {
					attributeList.add(AttributeType.NOUN_SUFFIX);
				}

				// prefix
				if (attributeList.contains(AttributeType.PREFIX) == false && groupEntry.containsAttribute("pref") == true) {
					attributeList.add(AttributeType.PREFIX);
				}

				// noun prefix
				if (attributeList.contains(AttributeType.NOUN_PREFIX) == false && groupEntry.containsAttribute("n-pref") == true) {
					attributeList.add(AttributeType.NOUN_PREFIX);
				}
				*/
									
				// onamatopoeic or mimetic word
				if (attributeList.contains(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD) == false && groupEntry.containsAttribute("on-mim") == true) {
					attributeList.add(AttributeType.ONAMATOPOEIC_OR_MIMETIC_WORD);
				}
			}
			
			if (currentPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.DICTIONARY2_SOURCE) == true) {
				
				Integer groupId = currentPolishJapaneseEntry.getGroupIdFromJmedictRawDataList();
				
				if (groupId != null) {
					currentPolishJapaneseEntry.getAttributeList().addAttributeValue(AttributeType.JMDICT_ENTRY_ID, groupId.toString());
				}
			}
		}
		
		// generowanie alternatyw		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryListKanjiKana = cachePolishJapaneseEntryList(polishJapaneseEntries);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
					
			//			
							
			List<PolishJapaneseEntry> foundPolishJapaneseEntryGroupList = new ArrayList<>();
			
			//
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry);
			
			if (groupEntryList != null && groupEntryList.size() > 0 && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				List<GroupEntry> fullGroupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList();
				
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(fullGroupEntryList, kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
										
					PolishJapaneseEntry findPolishJapaneseEntry = findPolishJapaneseEntryWithEdictDuplicate(
							polishJapaneseEntry, cachePolishJapaneseEntryListKanjiKana, groupEntryKanji, groupEntryKana);							
					
					if (findPolishJapaneseEntry == null) {
						findPolishJapaneseEntry = findPolishJapaneseEntryWithEdictDuplicate(
								polishJapaneseEntry, cachePolishJapaneseEntryListKanjiKana, groupEntryKanji, groupEntryKana);
					}
					
					if (findPolishJapaneseEntry != null) {
						foundPolishJapaneseEntryGroupList.add(findPolishJapaneseEntry);
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
		
		// generowanie 'zobacz rowniez'
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryListKanjiOnly = cachePolishJapaneseEntryList(polishJapaneseEntries, new ICachePolishJapaneseEntryGetValue() {
			
			@Override
			public String getKanji(PolishJapaneseEntry polishJapanaeseEntry) {
				return polishJapanaeseEntry.getKanji();
			}
			
			@Override
			public String getKana(PolishJapaneseEntry polishJapanaeseEntry) {
				return null;
			}
		});
		
		//
		
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryListKanaOnly = cachePolishJapaneseEntryList(polishJapaneseEntries, new ICachePolishJapaneseEntryGetValue() {
			
			@Override
			public String getKanji(PolishJapaneseEntry polishJapanaeseEntry) {
				return null;
			}
			
			@Override
			public String getKana(PolishJapaneseEntry polishJapanaeseEntry) {
				return polishJapanaeseEntry.getKana();
			}
		});
		
		//
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKana();
			
			//
			
			List<PolishJapaneseEntry> relatedSimilarListForPolishJapaneseEntry = new ArrayList<>();
			List<PolishJapaneseEntry> antonymListForPolishJapaneseEntry = new ArrayList<>();
			
			//
						
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry);

			if (groupEntryList != null && JMENewDictionary.isMultiGroup(groupEntryList) == false) {
				
				List<GroupEntry> fullGroupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList();
				
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(fullGroupEntryList, kanji, kana)) {
					
					List<String> similarRelatedList = groupEntry.getSimilarRelatedList();
					List<String> antonymList = groupEntry.getAntonymList();
					
					List<String> similarRelatedListAndAntonymList = new ArrayList<>();
					
					similarRelatedListAndAntonymList.addAll(similarRelatedList);
					similarRelatedListAndAntonymList.addAll(antonymList);
					
					// chodzimy po wszystkich powiazanych slowach
					for (String currentSimilarReleatedOrAntonym : similarRelatedListAndAntonymList) {
												
						String[] currentSimilarReleatedOrAntonymSplited = currentSimilarReleatedOrAntonym.split("・");
						
						String kanjiToFound = null;
						String kanaToFound = null;
						
						//
						
						if (currentSimilarReleatedOrAntonymSplited.length == 1 && Utils.isAllKanjiChars(currentSimilarReleatedOrAntonymSplited[0]) == true) {
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
														
						} else if (currentSimilarReleatedOrAntonymSplited.length == 1 && Utils.isAllKanaChars(currentSimilarReleatedOrAntonymSplited[0]) == true) {
							kanaToFound = currentSimilarReleatedOrAntonymSplited[0];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 2 && Utils.isAllKanjiChars(currentSimilarReleatedOrAntonymSplited[0]) == true && 
								StringUtils.isNumeric(currentSimilarReleatedOrAntonymSplited[1]) == true) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 2 && Utils.isAllKanaChars(currentSimilarReleatedOrAntonymSplited[0]) == true && 
								StringUtils.isNumeric(currentSimilarReleatedOrAntonymSplited[1]) == true) {
						
							kanaToFound = currentSimilarReleatedOrAntonymSplited[0];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 2 && Utils.isAllKanjiChars(currentSimilarReleatedOrAntonymSplited[0]) == true &&
								Utils.isAllKanaChars(currentSimilarReleatedOrAntonymSplited[1]) == true) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
							kanaToFound = currentSimilarReleatedOrAntonymSplited[1];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 3) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
							kanaToFound = currentSimilarReleatedOrAntonymSplited[1];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 1 && Utils.isAllKanaChars(currentSimilarReleatedOrAntonymSplited[0]) == false &&
								StringUtils.isNumeric(currentSimilarReleatedOrAntonymSplited[0]) == false) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
							
						} else if (currentSimilarReleatedOrAntonymSplited.length == 2 && Utils.isAllKanaChars(currentSimilarReleatedOrAntonymSplited[1]) == true) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
							kanaToFound = currentSimilarReleatedOrAntonymSplited[1];
														
						} else if (currentSimilarReleatedOrAntonymSplited.length == 2 &&	StringUtils.isNumeric(currentSimilarReleatedOrAntonymSplited[1]) == true) {
						
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0];
														
						} else if (	currentSimilarReleatedOrAntonymSplited.length == 2 && currentSimilarReleatedOrAntonymSplited[0].contains("、") == true &&
									currentSimilarReleatedOrAntonymSplited[1].contains("、") == true) {
							
							kanjiToFound = currentSimilarReleatedOrAntonymSplited[0].substring(0, currentSimilarReleatedOrAntonymSplited[0].indexOf("、"));
							kanaToFound = currentSimilarReleatedOrAntonymSplited[1].substring(0, currentSimilarReleatedOrAntonymSplited[1].indexOf("、"));							
							
						} else {
							kanjiToFound = currentSimilarReleatedOrAntonym;
							
							//throw new DictionaryException("Unknown similar related word: " + currentSimilarReleated);
						}
						
						//
						
						//tutaj();
						
						List<PolishJapaneseEntry> findPolishJapaneseEntry = null;
						
						if (kanjiToFound != null && kanaToFound == null) { // tylko kanji							
							findPolishJapaneseEntry = findPolishJapaneseEntry(cachePolishJapaneseEntryListKanjiOnly, kanjiToFound, kanaToFound);
							
						} else if (kanjiToFound == null && kanaToFound != null) { // tylko kana							
							findPolishJapaneseEntry = findPolishJapaneseEntry(cachePolishJapaneseEntryListKanaOnly, kanjiToFound, kanaToFound);
														
						} else if (kanjiToFound != null && kanaToFound != null) { // kanji i kana
							findPolishJapaneseEntry = findPolishJapaneseEntry(cachePolishJapaneseEntryListKanjiKana, kanjiToFound, kanaToFound);
							
						}	
						
						//
						
						if (findPolishJapaneseEntry != null) {

							for (PolishJapaneseEntry foundPolishJapaneseEntryRelatedSimilar : findPolishJapaneseEntry) {
								
								if (similarRelatedList.contains(currentSimilarReleatedOrAntonym) == true && relatedSimilarListForPolishJapaneseEntry.contains(foundPolishJapaneseEntryRelatedSimilar) == false) {
									relatedSimilarListForPolishJapaneseEntry.add(foundPolishJapaneseEntryRelatedSimilar);
								}
								
								if (antonymList.contains(currentSimilarReleatedOrAntonym) == true && antonymListForPolishJapaneseEntry.contains(foundPolishJapaneseEntryRelatedSimilar) == false) {
									antonymListForPolishJapaneseEntry.add(foundPolishJapaneseEntryRelatedSimilar);
								}

							}
						}
					}
				}
			}
			
			if (relatedSimilarListForPolishJapaneseEntry.size() > 0) {
				
				//System.out.println(polishJapaneseEntry.getKanji() + " , " + polishJapaneseEntry.getKana() + " , " + polishJapaneseEntry.getTranslates());
				
				for (PolishJapaneseEntry currentRelatedSimilar : relatedSimilarListForPolishJapaneseEntry) {
					//System.out.println("\t" + currentRelatedSimilar.getKanji() + " , " + currentRelatedSimilar.getKana() + " , " + currentRelatedSimilar.getTranslates());
					
					polishJapaneseEntry.getAttributeList().addAttributeValue(AttributeType.RELATED, "" + currentRelatedSimilar.getId());
				}
			}
			
			if (antonymListForPolishJapaneseEntry.size() > 0) {
								
				for (PolishJapaneseEntry currentRelatedSimilar : antonymListForPolishJapaneseEntry) {					
					polishJapaneseEntry.getAttributeList().addAttributeValue(AttributeType.ANTONYM, "" + currentRelatedSimilar.getId());
				}
			}			
		}		
	}

	private static EDictEntry findEdictEntry(TreeMap<String, EDictEntry> jmedict,
			PolishJapaneseEntry polishJapaneseEntry) {

		String kanji = polishJapaneseEntry.getKanji();

		if (kanji != null && (kanji.equals("") == true || kanji.equals("-") == true)) {
			kanji = null;
		}

		String kana = polishJapaneseEntry.getKana();

		EDictEntry foundEdict = jmedict.get(EdictReader.getMapKey(kanji, kana));

		return foundEdict;
	}

	public static List<PolishJapaneseEntry> generateNames(JMENewDictionary jmeNewDictionary) {

		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		// mapowanie typow
		Map<String, DictionaryEntryType> nameTypeMapper = new HashMap<String, DictionaryEntryType>();
		
		nameTypeMapper.put("unclass", DictionaryEntryType.WORD_UNCLASS_NAME);
		nameTypeMapper.put("place", DictionaryEntryType.WORD_PLACE);
		nameTypeMapper.put("given", DictionaryEntryType.WORD_NAME);
		nameTypeMapper.put("company", DictionaryEntryType.WORD_COMPANY_NAME);
		nameTypeMapper.put("work", DictionaryEntryType.WORD_WORK);
		nameTypeMapper.put("masc", DictionaryEntryType.WORD_MALE_NAME);
		nameTypeMapper.put("person", DictionaryEntryType.WORD_PERSON);
		nameTypeMapper.put("surname", DictionaryEntryType.WORD_SURNAME_NAME);
		nameTypeMapper.put("product", DictionaryEntryType.WORD_PRODUCT_NAME);
		nameTypeMapper.put("organization", DictionaryEntryType.WORD_ORGANIZATION_NAME);
		nameTypeMapper.put("station", DictionaryEntryType.WORD_STATION_NAME);
		nameTypeMapper.put("char", DictionaryEntryType.WORD_CHARACTER);
		nameTypeMapper.put("creat", DictionaryEntryType.WORD_CREATURE);
		nameTypeMapper.put("dei", DictionaryEntryType.WORD_DEITY);
		nameTypeMapper.put("ev", DictionaryEntryType.WORD_EVENT);
		nameTypeMapper.put("fem", DictionaryEntryType.WORD_FEMALE_NAME);
		nameTypeMapper.put("fict", DictionaryEntryType.WORD_FICT);
		nameTypeMapper.put("leg", DictionaryEntryType.WORD_LEGEND);
		nameTypeMapper.put("myth", DictionaryEntryType.WORD_MYTHOLOGY);
		nameTypeMapper.put("obj", DictionaryEntryType.WORD_OBJECT);
		nameTypeMapper.put("oth", DictionaryEntryType.WORD_OTHER);
		nameTypeMapper.put("relig", DictionaryEntryType.WORD_RELIGION);
		nameTypeMapper.put("serv", DictionaryEntryType.WORD_SERVICE);
		nameTypeMapper.put("ship", DictionaryEntryType.WORD_SHIP_NAME);
		nameTypeMapper.put("group", DictionaryEntryType.WORD_GROUP);
		nameTypeMapper.put("doc", DictionaryEntryType.WORD_DOCUMENT);
		
		// informacje wygenerowane automatycznie !!!!!!!!!!!!!!!!!!11
		// WORD_COMPANY_NAME
		// WORD_PLACE
		// albo wszystkie !!!!!!!!!!!!!!!!!!!!!!!
		
		int counter = 1;

		KanaHelper kanaHelper = new KanaHelper();
		
		for (Group group : jmeNewDictionary.getGroupList()) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				// zamiana typow na nasze
				List<DictionaryEntryType> nameDictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
				
				Set<String> wordTypeList = groupEntry.getWordTypeList();
				
				for (String wordType : wordTypeList) {
					
					DictionaryEntryType nameDictionaryEntryType = nameTypeMapper.get(wordType);
					
					if (nameDictionaryEntryType == null) {
						throw new RuntimeException("Unknown name type: " + wordType);
					}
					
					if (nameDictionaryEntryTypeList.contains(nameDictionaryEntryType) == false) {
						nameDictionaryEntryTypeList.add(nameDictionaryEntryType);
					}					
				}

				if (nameDictionaryEntryTypeList.size() == 0) {
					nameDictionaryEntryTypeList.add(DictionaryEntryType.WORD_EMPTY); 
				}
				
				String kanji = groupEntry.getKanji();
				
				if (kanji == null || kanji.equals("") == true) {
					kanji = "-";
				}
								
				String kana = groupEntry.getKana();
								
				String romaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(kana, kanaHelper.getKanaCache(), true));

				List<GroupEntryTranslate> groupEntryTranslateList = groupEntry.getTranslateList();
				
				List<String> newPolishJapaneseEntryTranslates = new ArrayList<String>();
				
				for (GroupEntryTranslate groupEntryTranslate : groupEntryTranslateList) {
					newPolishJapaneseEntryTranslates.add(groupEntryTranslate.getTranslate());
				}
				
				//
									
				PolishJapaneseEntry newPolishJapaneseEntry = new PolishJapaneseEntry();
				
				newPolishJapaneseEntry.setId(counter);
				counter++;
				
				newPolishJapaneseEntry.setDictionaryEntryTypeList(nameDictionaryEntryTypeList);
				
				newPolishJapaneseEntry.setWordType(WordType.HIRAGANA_KATAKANA);
				
				newPolishJapaneseEntry.setAttributeList(new AttributeList());
				newPolishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
				newPolishJapaneseEntry.setKanji(kanji != null ? kanji : "-");
				newPolishJapaneseEntry.setKana(kana);
				newPolishJapaneseEntry.setRomaji(romaji);
				newPolishJapaneseEntry.setTranslates(newPolishJapaneseEntryTranslates);
				newPolishJapaneseEntry.setParseAdditionalInfoList(new ArrayList<ParseAdditionalInfo>());
				newPolishJapaneseEntry.setExampleSentenceGroupIdsList(new ArrayList<String>());
				
				fixPolishJapaneseEntryName(newPolishJapaneseEntry);
									
				result.add(newPolishJapaneseEntry);
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
			
			/*
			String translate = newPolishJapaneseEntry.getTranslates().get(0);
			
			translate = translate.replaceAll("Station", "(nazwa stacji)");
			
			List<String> newTranslateList = new ArrayList<String>();
			newTranslateList.add(translate);
			
			newPolishJapaneseEntry.setTranslates(newTranslateList);
			*/
			
			String romaji = newPolishJapaneseEntry.getRomaji();
			
			if (romaji.endsWith("eki") == true) {
				romaji = romaji.substring(0, romaji.length() - 3) + " eki";
			}
			
			newPolishJapaneseEntry.setRomaji(romaji);
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

		if (kanji == null || kanji.equals("") == true) {
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
	
	public static class CreatePolishJapaneseEntryResult {
		
		public PolishJapaneseEntry polishJapaneseEntry;
		
		public boolean alreadyAddedPolishJapaneseEntry;
	}
	
	public static CreatePolishJapaneseEntryResult createPolishJapaneseEntry(GroupEntry groupEntry) throws DictionaryException {		
		return createPolishJapaneseEntry(null, groupEntry, 0, null);
	}	
	
	public static CreatePolishJapaneseEntryResult createPolishJapaneseEntry(GroupEntry groupEntry, int id) throws DictionaryException {
		return createPolishJapaneseEntry(null, groupEntry, id, null);
	}
	
	public static CreatePolishJapaneseEntryResult createPolishJapaneseEntry(Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList, GroupEntry groupEntry, int id, String missingWord) throws DictionaryException {
		
		DictionaryEntryJMEdictEntityMapper dictionaryEntryJMEdictEntityMapper = new DictionaryEntryJMEdictEntityMapper();
		JMEDictEntityMapper jmEDictEntityMapper = new JMEDictEntityMapper();
		
		Set<String> wordTypeList = groupEntry.getWordTypeList();
		
		String kanji = groupEntry.getKanji();
		List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

		String kana = groupEntry.getKana();
		List<String> kanaInfoList = groupEntry.getKanaInfoList();

		String romaji = groupEntry.getRomaji();

		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();			
		
		List<String> translateList2 = new ArrayList<String>();
		
		List<String> jmedictRawDataList = new ArrayList<String>();
		
		for (GroupEntryTranslate groupEntryTranslate : translateList) {
			
			StringBuffer translate = new StringBuffer(groupEntryTranslate.getTranslate());
			
			List<String> miscInfoList = groupEntryTranslate.getMiscInfoList();
			List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
			List<String> dialectList = groupEntryTranslate.getDialectList();
			
			for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {				
				translate.append("\n     " + additionalInfoList.get(0));
			}
						
			for (int idx = 0; miscInfoList != null && idx < miscInfoList.size(); ++idx) {				
				translate.append("\n     " + jmEDictEntityMapper.getDesc(miscInfoList.get(idx)));
			}			

			for (int idx = 0; dialectList != null && idx < dialectList.size(); ++idx) {				
				translate.append("\n     " + dialectList.get(idx));
			}			
			
			translateList2.add(translate.toString());
			
			//
			
			groupEntryTranslate.fillJmedictRawData(jmedictRawDataList);
		}					
		
		List<String> additionalInfoList = new ArrayList<String>(); //groupEntry.getAdditionalInfoList();
		
		PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
		
		polishJapaneseEntry.setId(id);
		
		List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
							
		for (String currentEntity : wordTypeList) {
			
			DictionaryEntryType dictionaryEntryType = dictionaryEntryJMEdictEntityMapper.getDictionaryEntryType(currentEntity);
			
			if (dictionaryEntryType != null && dictionaryEntryTypeList.contains(dictionaryEntryType) == false) {
				dictionaryEntryTypeList.add(dictionaryEntryType);
			}
		}
						
		polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);
		
		polishJapaneseEntry.setAttributeList(new AttributeList());
		
		WordType wordType = getWordType(kana);
		
		polishJapaneseEntry.setWordType(wordType);
		
		polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
		
		if (kanji == null || kanji.equals("") == true) {
			kanji = "-";
		}
		
		polishJapaneseEntry.setKanji(kanji);
		polishJapaneseEntry.setKana(kana);
		polishJapaneseEntry.setRomaji(romaji);
						
		polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

		List<String> newTranslateList = new ArrayList<String>();
		
		List<PolishJapaneseEntry> findPolishJapaneseEntry = null;
		
		if (cachePolishJapaneseEntryList != null) {
			
			findPolishJapaneseEntry = findPolishJapaneseEntry(cachePolishJapaneseEntryList, kanji, kana);			
		}
		
		newTranslateList.add("_");
		newTranslateList.add("-----------");
		
		boolean alreadyAddedPolishJapaneseEntry = false;
		
		if (findPolishJapaneseEntry != null && findPolishJapaneseEntry.size() > 0) {
			newTranslateList.add("JUZ JEST");
			newTranslateList.add("-----------");
			
			alreadyAddedPolishJapaneseEntry = true;
		}
		
		if (missingWord != null) {
			newTranslateList.add(missingWord);
		}
				
		newTranslateList.add("-----------");
		newTranslateList.addAll(translateList2);
				
		polishJapaneseEntry.setTranslates(newTranslateList);
		
		StringBuffer additionalInfoSb = new StringBuffer();

	    if (kanjiInfoList != null && kanjiInfoList.size() > 0) {
		    additionalInfoSb.append(Utils.convertListToString(kanjiInfoList));
		    additionalInfoSb.append("\n");
	    }

	    if (kanaInfoList != null && kanaInfoList.size() > 0) {
		    additionalInfoSb.append(Utils.convertListToString(kanaInfoList));
		    additionalInfoSb.append("\n");
	    }
	    
	    if (additionalInfoList != null && additionalInfoList.size() > 0) {
		    additionalInfoSb.append(Utils.convertListToString(additionalInfoList));
		    additionalInfoSb.append("\n");
	    }
	    
	    if (	(wordType == WordType.KATAKANA || wordType == WordType.KATAKANA_EXCEPTION) &&
	    		translateList2.size() == 1) {
	    	
	    	additionalInfoSb.append("ang: " + translateList2.get(0));	    	
	    }	    
		
		polishJapaneseEntry.setInfo(additionalInfoSb.toString());		
		
		polishJapaneseEntry.setJmedictRawDataList(jmedictRawDataList);
		
		CreatePolishJapaneseEntryResult result = new CreatePolishJapaneseEntryResult();
		
		result.polishJapaneseEntry = polishJapaneseEntry;
		result.alreadyAddedPolishJapaneseEntry = alreadyAddedPolishJapaneseEntry;
		
		return result;
	}
	
	public static PolishJapaneseEntry createEmptyPolishJapaneseEntry(String missingWord, int counter) {
		
		PolishJapaneseEntry polishJapaneseEntry = new PolishJapaneseEntry();
		
		polishJapaneseEntry.setId(counter);
		
		List<DictionaryEntryType> dictionaryEntryTypeList = new ArrayList<DictionaryEntryType>();
		
		dictionaryEntryTypeList.add(DictionaryEntryType.UNKNOWN);
										
		polishJapaneseEntry.setDictionaryEntryTypeList(dictionaryEntryTypeList);				
		polishJapaneseEntry.setAttributeList(new AttributeList());
		
		polishJapaneseEntry.setWordType(WordType.KATAKANA);
		
		polishJapaneseEntry.setGroups(new ArrayList<GroupEnum>());
						
		polishJapaneseEntry.setKanji("-");
		polishJapaneseEntry.setKana("-");
		polishJapaneseEntry.setRomaji("-");
						
		polishJapaneseEntry.setKnownDuplicatedList(new ArrayList<KnownDuplicate>());

		List<String> newTranslateList = new ArrayList<String>();
		
		if (missingWord != null) {
			newTranslateList.add(missingWord);
			
		} else {
			newTranslateList.add("");			
		}		
						
		polishJapaneseEntry.setTranslates(newTranslateList);				
		polishJapaneseEntry.setInfo("");
		
		return polishJapaneseEntry;
	}
	
	private static WordType getWordType(String kana) {
		
		WordType wordType = null;
				
		for (int idx = 0; idx < kana.length(); ++idx) {
			
			char c = kana.charAt(idx);
			
			boolean currentCIsHiragana = Utils.isHiragana(c);
			boolean currentCIsKatakana = Utils.isKatakana(c);
			
			if (currentCIsHiragana == true) {
				
				if (wordType == null) {
					wordType = WordType.HIRAGANA;
					
				} else if (wordType == WordType.KATAKANA) {
					wordType = WordType.KATAKANA_HIRAGANA;					
				}				
			}

			if (currentCIsKatakana == true) {
				
				if (wordType == null) {
					wordType = WordType.KATAKANA;
					
				} else if (wordType == WordType.HIRAGANA) {
					wordType = WordType.HIRAGANA_KATAKANA;					
				}				
			}			
		}	
		
		return wordType;
	}
	
	public static Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		ICachePolishJapaneseEntryGetValue icachePolishJapaneseEntryGetValue = new ICachePolishJapaneseEntryGetValue() {
			
			@Override
			public String getKanji(PolishJapaneseEntry polishJapanaeseEntry) {
				return polishJapanaeseEntry.getKanji();
			}
			
			@Override
			public String getKana(PolishJapaneseEntry polishJapanaeseEntry) {
				return polishJapanaeseEntry.getKana();
			}
		};
		
		return cachePolishJapaneseEntryList(polishJapaneseEntries, icachePolishJapaneseEntryGetValue);
	}
	
	private static Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList(List<PolishJapaneseEntry> polishJapaneseEntries, ICachePolishJapaneseEntryGetValue icachePolishJapaneseEntryGetValue) {
		
		Map<String, List<PolishJapaneseEntry>> result = new TreeMap<String, List<PolishJapaneseEntry>>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = icachePolishJapaneseEntryGetValue.getKanji(polishJapaneseEntry); // polishJapaneseEntry.getKanji();
			String kana = icachePolishJapaneseEntryGetValue.getKana(polishJapaneseEntry); // polishJapaneseEntry.getKana();
			
			if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}
			
			if (kana == null) {
				kana = "$$$NULL$$$";
			}
			
			String key = kanji + "." + kana;
			
			List<PolishJapaneseEntry> keyPolishJapaneseEntryList = result.get(key);
			
			if (keyPolishJapaneseEntryList == null) {				
				keyPolishJapaneseEntryList = new ArrayList<PolishJapaneseEntry>();
				
				result.put(key, keyPolishJapaneseEntryList);
			}
			
			keyPolishJapaneseEntryList.add(polishJapaneseEntry);			
		}
		
		return result;
	}
	
	public static interface ICachePolishJapaneseEntryGetValue {
		
		public String getKanji(PolishJapaneseEntry polishJapanaeseEntry);
		public String getKana(PolishJapaneseEntry polishJapanaeseEntry);
	}
	
	public static List<PolishJapaneseEntry> findPolishJapaneseEntry(
			Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryMap, String findKanji, String findKana) {
		
		if (findKanji == null || findKanji.equals("") == true || findKanji.equals("-") == true) {
			findKanji = "$$$NULL$$$";
		}
		
		if (findKana == null) {
			findKana = "$$$NULL$$$";
		}

		String foundKey = findKanji + "." + findKana;
		
		List<PolishJapaneseEntry> polishJapaneseEntries = cachePolishJapaneseEntryMap.get(foundKey);
		
		if (polishJapaneseEntries == null) {
			return null;
		}
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}		
			
			result.add(polishJapaneseEntry);
		}
		
		return result;
	}	
	
	public static PolishJapaneseEntry findPolishJapaneseEntryWithEdictDuplicate(PolishJapaneseEntry parentPolishJapaneseEntry,
			Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryMap, String findKanji, String findKana) {
		
		// PolishJapaneseEntry result = null;
		
		if (findKanji == null || findKanji.equals("") == true || findKanji.equals("-") == true) {
			findKanji = "$$$NULL$$$";
		}
		
		if (findKana == null) {
			findKana = "$$$NULL$$$";
		}

		String foundKey = findKanji + "." + findKana;
		
		List<PolishJapaneseEntry> polishJapaneseEntries = cachePolishJapaneseEntryMap.get(foundKey);
		
		if (polishJapaneseEntries == null) {
			return null;
		}
				
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			DictionaryEntryType dictionaryEntryType = polishJapaneseEntry.getDictionaryEntryType();
			
			if (dictionaryEntryType == DictionaryEntryType.WORD_FEMALE_NAME || dictionaryEntryType == DictionaryEntryType.WORD_MALE_NAME) {
				continue;
			}
			
			if (parentPolishJapaneseEntry.getGroupIdFromJmedictRawDataList() == null || polishJapaneseEntry.getGroupIdFromJmedictRawDataList() == null) { // dzialamy po staremu
				
				if (parentPolishJapaneseEntry.isKnownDuplicate(KnownDuplicateType.EDICT_DUPLICATE, polishJapaneseEntry.getId()) == false) {				
					return polishJapaneseEntry;
					
					/*
					if (result != null) {
						//throw new RuntimeException("Please add EDICT_DUPLICATE to id: " + parentPolishJapaneseEntry.getId() + " or " + result.getId() + " or " + polishJapaneseEntry.getId() + " (check translates)");
						
						//System.err.println("Please add EDICT_DUPLICATE to id: " + parentPolishJapaneseEntry.getId() + " or " + result.getId() + " or " + polishJapaneseEntry.getId() + " (check translates)");
						
						// System.out.println(parentPolishJapaneseEntry.getId());
						// System.out.println(result.getId());
						// System.out.println(polishJapaneseEntry.getId());
						
						return result;
					}
					
					result = polishJapaneseEntry;
					*/
				}
				
			} else {
				
				if (polishJapaneseEntry.getGroupIdFromJmedictRawDataList().intValue() == parentPolishJapaneseEntry.getGroupIdFromJmedictRawDataList().intValue()) {
					return polishJapaneseEntry;
				}
			}
		}
		
		return null;
	}
		
	public static CommonWord convertGroupEntryToCommonWord(int id, GroupEntry groupEntry) {
				
		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
		
		List<String> translateStringList = new ArrayList<String>();
		
		for (GroupEntryTranslate groupEntryTranslate : translateList) {
			translateStringList.add(groupEntryTranslate.getTranslate());
		}
		
		CommonWord commonWord = new CommonWord(id, false, groupEntry.getKanji(), groupEntry.getKana(), groupEntry.getWordTypeList().toString(), translateStringList.toString());
		
		return commonWord;
	}
	
	public static CommonWord convertEDictEntryToCommonWord(int id, EDictEntry edictEntry) {
		
		CommonWord commonWord = new CommonWord(id, false, edictEntry.getKanji(), edictEntry.getKana(), edictEntry.getPos().toString(), edictEntry.getRawLine());
		
		return commonWord;		
	}
	
	public static Directory createLuceneDictionaryIndex(JMENewDictionary jmeNewDictionary) throws IOException {
		
		JMEDictEntityMapper jmEDictEntityMapper = new JMEDictEntityMapper();
		
		Directory index = new RAMDirectory();

		// tworzenie analizatora lucene
		LuceneAnalyzer analyzer = new LuceneAnalyzer(Version.LUCENE_47);

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE);

		IndexWriter indexWriter = new IndexWriter(index, indexWriterConfig);

		for (Group group : jmeNewDictionary.getGroupList()) {

			List<GroupEntry> groupEntryList = group.getGroupEntryList();

			for (GroupEntry groupEntry : groupEntryList) {

				Document document = new Document();

				Integer groupId = groupEntry.getGroup().getId();
				
				Set<String> wordTypeList = groupEntry.getWordTypeList();

				String kanji = groupEntry.getKanji();
				List<String> kanjiInfoList = groupEntry.getKanjiInfoList();

				String kana = groupEntry.getKana();
				List<String> kanaInfoList = groupEntry.getKanaInfoList();

				String romaji = groupEntry.getRomaji();

				List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
				
				List<String> translateList2 = new ArrayList<String>();
				
				for (GroupEntryTranslate groupEntryTranslate : translateList) {
					
					StringBuffer translate = new StringBuffer(groupEntryTranslate.getTranslate());
					
					List<String> miscInfoList = groupEntryTranslate.getMiscInfoList();
					List<String> additionalInfoList = groupEntryTranslate.getAdditionalInfoList();
					List<String> dialectList = groupEntryTranslate.getDialectList();
					
					boolean wasMiscOrAdditionalInfo = false;
					
					for (int idx = 0; miscInfoList != null && idx < miscInfoList.size(); ++idx) {
						
						if (wasMiscOrAdditionalInfo == false) {
							translate.append(" (");
							
							wasMiscOrAdditionalInfo = true;
							
						} else {
							translate.append(", ");
						}
						
						translate.append(jmEDictEntityMapper.getDesc(miscInfoList.get(idx)));
					}
					
					for (int idx = 0; additionalInfoList != null && idx < additionalInfoList.size(); ++idx) {
						
						if (wasMiscOrAdditionalInfo == false) {
							translate.append(" (");
							
							wasMiscOrAdditionalInfo = true;
							
						} else {
							translate.append(", ");
						}
					}
					
					for (int idx = 0; dialectList != null && idx < dialectList.size(); ++idx) {
						
						if (wasMiscOrAdditionalInfo == false) {
							translate.append(" (dialect: ");
							
							wasMiscOrAdditionalInfo = true;
							
						} else {
							translate.append(", dialect: ");
						}
						
						translate.append(dialectList.get(idx));
					}

					
					if (wasMiscOrAdditionalInfo == true) {
						translate.append(")");
					}
					
					translateList2.add(translate.toString());
				}
				
				addFieldToDocument(document, "groupId", groupId);
				
				addStringFieldToDocument(document, "wordTypeList", wordTypeList);

				addStringFieldToDocument(document, "kanji", kanji);
				addTextFieldToDocument(document, "kanjiInfoList", kanjiInfoList);

				addStringFieldToDocument(document, "kana", kana);
				addTextFieldToDocument(document, "kanaInfoList", kanaInfoList);

				addTextFieldToDocument(document, "romaji", romaji);
				
				addTextFieldToDocument(document, "translateList", translateList2);
				//addFieldToDocument(document, "additionalInfoList", additionalInfoList);

				indexWriter.addDocument(document);
			}			
		}

		indexWriter.close();
		
		
		return index;
	}
	
	private static void addTextFieldToDocument(Document document, String fieldName, String value) {

		if (value != null) {
			document.add(new TextField(fieldName, value, Field.Store.YES));
		}
	}

	private static void addStringFieldToDocument(Document document, String fieldName, String value) {

		if (value != null) {
			document.add(new StringField(fieldName, value, Field.Store.YES));
		}
	}
	
	private static void addTextFieldToDocument(Document document, String fieldName, Collection<String> collection) {

		if (collection == null) {
			return;
		}

		for (String string : collection) {
			document.add(new TextField(fieldName, string, Field.Store.YES));

		}		
	}

	private static void addStringFieldToDocument(Document document, String fieldName, Collection<String> collection) {

		if (collection == null) {
			return;
		}

		for (String string : collection) {
			document.add(new StringField(fieldName, string, Field.Store.YES));

		}		
	}
	
	private static void addFieldToDocument(Document document, String fieldName, Integer value) {

		if (value != null) {
			document.add(new IntField(fieldName, value, Field.Store.YES));
		}
	}
		
	public static Query createLuceneDictionaryIndexTermQuery(String word) {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = word.split("\\s+");

		BooleanQuery wordBooleanQuery = new BooleanQuery();

		wordBooleanQuery.add(createTermQuery(wordSplited, "kanji"), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, "kana"), Occur.SHOULD);				

		wordBooleanQuery.add(createTermQuery(wordSplited, "romaji"), Occur.SHOULD);

		wordBooleanQuery.add(createTermQuery(wordSplited, "translateList"), Occur.SHOULD);
		wordBooleanQuery.add(createTermQuery(wordSplited, "additionalInfoList"), Occur.SHOULD);

		query.add(wordBooleanQuery, Occur.MUST);

		return query;
	}
	
	public static Query createLuceneDictionaryIndexPrefixQuery(String word) {

		BooleanQuery query = new BooleanQuery();

		String[] wordSplited = word.split("\\s+");

		BooleanQuery wordBooleanQuery = new BooleanQuery();

		wordBooleanQuery.add(createPrefixQuery(wordSplited, "kanji"), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(wordSplited, "kana"), Occur.SHOULD);				

		wordBooleanQuery.add(createPrefixQuery(wordSplited, "romaji"), Occur.SHOULD);

		wordBooleanQuery.add(createPrefixQuery(wordSplited, "translateList"), Occur.SHOULD);
		wordBooleanQuery.add(createPrefixQuery(wordSplited, "additionalInfoList"), Occur.SHOULD);

		query.add(wordBooleanQuery, Occur.MUST);

		return query;
	}
	
	private static Query createTermQuery(String[] wordSplited, String fieldName) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (String currentWord : wordSplited) {
			booleanQuery.add(new TermQuery(new Term(fieldName, currentWord)), Occur.MUST);
		}

		return booleanQuery;
	}
	
	private static Query createPrefixQuery(String[] wordSplited, String fieldName) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (String currentWord : wordSplited) {
			booleanQuery.add(new PrefixQuery(new Term(fieldName, currentWord)), Occur.MUST);
		}

		return booleanQuery;
	}
	
	public static GroupEntry createGroupEntry(Document document) {

		Group fakeGroup = new Group(Integer.parseInt(document.get("groupId")), null);
		
		GroupEntry groupEntry = new GroupEntry(null, fakeGroup);		
		
		groupEntry.setWordTypeList(new LinkedHashSet<String>(Arrays.asList(document.getValues("wordTypeList"))));

		groupEntry.setKanji(document.get("kanji"));
		groupEntry.setKanjiInfoList(Arrays.asList(document.getValues("kanjiInfoList")));

		groupEntry.setKana(document.get("kana"));
		groupEntry.setKanaInfoList(Arrays.asList(document.getValues("kanaInfoList")));

		groupEntry.setRomaji(document.get("romaji"));
		
		List<GroupEntryTranslate> translateList = new ArrayList<GroupEntryTranslate>();
		
		for (String currentTranslate : Arrays.asList(document.getValues("translateList"))) {
			
			GroupEntryTranslate translate = new GroupEntryTranslate(groupEntry);
			
			translate.setTranslate(currentTranslate);
			
			translateList.add(translate);
		}
				
		groupEntry.setTranslateList(translateList);
		
		//groupEntry.setAdditionalInfoList(Arrays.asList(document.getValues("additionalInfoList")));

		return groupEntry;
	}
	
	public static List<DictionaryEntryGroup> generateDictionaryEntryGroup(List<PolishJapaneseEntry> polishJapaneseEntryList) {
		
		List<DictionaryEntryGroup> result = new ArrayList<DictionaryEntryGroup>();
		
		// utworz mape z id'kami
		TreeMap<Integer, PolishJapaneseEntry> polishJapaneseEntryIdMap = new TreeMap<Integer, PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntryList) {
			polishJapaneseEntryIdMap.put(polishJapaneseEntry.getId(), polishJapaneseEntry);
		}
		
		int dictionaryEntryGroupCounter = 1;
		
		while (true) {
			
			// pobieramy pierwszy wpis
			Entry<Integer, PolishJapaneseEntry> firstEntry = polishJapaneseEntryIdMap.firstEntry();
			
			if (firstEntry == null) {
				break; // wychodzimy
			}
			
			// mamy slowo
			PolishJapaneseEntry polishJapaneseEntry = firstEntry.getValue();
			
			polishJapaneseEntryIdMap.remove(polishJapaneseEntry.getId());
			
			// pobranie wszystkich innych alternatyw
			List<Attribute> alternativeAttributeList = polishJapaneseEntry.getAttributeList().getAttributeList(AttributeType.ALTERNATIVE);

			List<DictionaryEntry> dictionaryEntryList = new ArrayList<DictionaryEntry>();
			
			dictionaryEntryList.add(polishJapaneseEntry);
			
			if (alternativeAttributeList.size() >  0) { // // to slowo ma alternatywy, dodajemy je
				
				for (Attribute alternativeAttribute : alternativeAttributeList) {
					
					// id alternatywy
					Integer alternativeId = Integer.parseInt(alternativeAttribute.getAttributeValue().get(0));
					
					// pobranie alternatywy
					PolishJapaneseEntry alternativePolishJapaneseEntry = polishJapaneseEntryIdMap.get(alternativeId);
					
					if (alternativePolishJapaneseEntry == null) {
						continue;
					}
					
					// usuniecie alternatywy z mapy
					polishJapaneseEntryIdMap.remove(alternativeId);
					
					dictionaryEntryList.add(alternativePolishJapaneseEntry);					
				}
			}

			// tworzymy grupe
			DictionaryEntryGroup dictionaryEntryGroup = new DictionaryEntryGroup();
			
			dictionaryEntryGroup.setId(dictionaryEntryGroupCounter);
			dictionaryEntryGroup.setDictionaryEntryList(dictionaryEntryList);

			// dodajemy do wyniku
			result.add(dictionaryEntryGroup);
			
			// zwiekszamy licznik
			dictionaryEntryGroupCounter++;
		}
		
		return result;
	}
	
	public static String convertListToString(List<?> list) {
		return convertListToString(list, "\n");
	}
	
	public static String convertListToString(List<?> list, String separator) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			list = new ArrayList<String>();
		}
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx));

			if (idx != list.size() - 1) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}
	
	public static String convertEnumListToString(List<? extends Enum<?>> list) {
		return convertEnumListToString(list, "\n");
	}
	
	public static String convertEnumListToString(List<? extends Enum<?>> list, String separator) {
		
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			list = new ArrayList<Enum<?>>();
		}
		
		for (int idx = 0; idx < list.size(); ++idx) {
			
			Enum<?> enum_ = list.get(idx);
			
			String enumXmlEnumValue;
			
			try {
				enumXmlEnumValue = enum_.getDeclaringClass().getField(enum_.name()).getAnnotation(XmlEnumValue.class).value();
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
						
			sb.append(enumXmlEnumValue);

			if (idx != list.size() - 1) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}
	
	public static List<String> convertListToListString(List<?> list) {

		if (list == null) {
			return null;
		}
		
		List<String> result = new ArrayList<>();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			result.add(list.get(idx).toString());
		}

		return result;
	}
	
	public static List<String> convertAttributeListToListString(AttributeList attributeList) {

		List<String> result = new ArrayList<String>();

		List<Attribute> attributeListList = attributeList.getAttributeList();

		for (int idx = 0; idx < attributeListList.size(); ++idx) {
			
			StringBuffer sb = new StringBuffer();
			
			Attribute currentAttribute = attributeListList.get(idx);

			sb.append(currentAttribute.getAttributeType().toString());

			List<String> attributeValue = currentAttribute.getAttributeValue();

			if (attributeValue != null && attributeValue.size() > 0) {

				for (String currentSingleAttributeValue : attributeValue) {
					sb.append(" ").append(currentSingleAttributeValue);
				}
			}
			
			result.add(sb.toString());
		}

		return result;
	}
		
	public static String convertListListToString(List<List<String>> listList, String listSeparator, String elementSeparator) {

		StringBuffer result = new StringBuffer();
		
		for (int idx = 0; idx < listList.size(); ++idx) {
			
			List<String> currentList = listList.get(idx);
			
			result.append(convertListToString(currentList, elementSeparator));
			
			if (idx != listList.size() - 1) {
				result.append("\n").append(listSeparator).append("\n");
			}
		}
		
		return result.toString();
	}
	
	public static String convertAttributeListToString(AttributeList attributeList) {
	
		StringBuffer sb = new StringBuffer();
				
		List<String> resultList = convertAttributeListToListString(attributeList);
		
		for (int idx = 0; idx < resultList.size(); ++idx) {
			
			sb.append(resultList.get(idx));
			
			if (idx != resultList.size() - 1) {
				sb.append("\n");
			}
		}
	
		return sb.toString();
	}

	public static List<String> convertStringToList(String stringList) {		
		return convertStringToList(stringList, "\n");
	}
	
	public static List<String> convertStringToList(String stringList, String separator) {
		
		if (stringList.equals("") == true) {
			return new ArrayList<>();
		}
		
		return new ArrayList<String>(Arrays.asList(stringList.split(separator)));
	}
}
