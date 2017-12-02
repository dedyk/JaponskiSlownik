package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.common.Helper;

public class JMENewDictionary {

	private List<Group> groupList = new ArrayList<Group>();
	
	private TreeMap<String, List<GroupEntry>> groupEntryCache = new TreeMap<String, List<GroupEntry>>();
	
	private TreeMap<String, List<GroupEntry>> groupEntryOnlyKanjiCache = new TreeMap<String, List<GroupEntry>>();
	
	private TreeMap<String, List<GroupEntry>> groupEntryOnlyKanaCache = new TreeMap<String, List<GroupEntry>>();
	
	private Map<Integer, Group> groupByIdCache = new TreeMap<Integer, Group>();
	
	public List<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Group> groupList) {		
		this.groupList = groupList;		
	}
	
	public Group getGroupById(Integer id) {
		return groupByIdCache.get(id);
	}
	
	public void addGroupToCache(Group group) {				
		groupByIdCache.put(group.getId(), group);
	}
		
	public void addGroupEntryToCache(GroupEntry groupEntry) {
		
		String groupEntryKey = getKey(groupEntry.getKanji(), groupEntry.getKana());
		
		List<GroupEntry> groupEntryListInCache = groupEntryCache.get(groupEntryKey);
		
		if (groupEntryListInCache == null) {
			groupEntryListInCache = new ArrayList<JMENewDictionary.GroupEntry>();
			
			groupEntryCache.put(groupEntryKey, groupEntryListInCache);
			
		}
		
		groupEntryListInCache.add(groupEntry);		
	}
	
	public void addGroupEntryToOnlyKanjiCache(GroupEntry groupEntry) {
		
		String kanji = groupEntry.getKanji();
		
		if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
			return;
		}
		
		String groupEntryKey = getKey(groupEntry.getKanji(), null);
		
		List<GroupEntry> groupEntryListInOnlyKanjiCache = groupEntryOnlyKanjiCache.get(groupEntryKey);
		
		if (groupEntryListInOnlyKanjiCache == null) {
			groupEntryListInOnlyKanjiCache = new ArrayList<JMENewDictionary.GroupEntry>();
			
			groupEntryOnlyKanjiCache.put(groupEntryKey, groupEntryListInOnlyKanjiCache);
			
		}
		
		groupEntryListInOnlyKanjiCache.add(groupEntry);		
	}	
	
	public void addGroupEntryToOnlyKanaCache(GroupEntry groupEntry) {
				
		String groupEntryKey = getKey(null, groupEntry.getKana());
		
		List<GroupEntry> groupEntryListInOnlyKanaCache = groupEntryOnlyKanaCache.get(groupEntryKey);
		
		if (groupEntryListInOnlyKanaCache == null) {
			groupEntryListInOnlyKanaCache = new ArrayList<JMENewDictionary.GroupEntry>();
			
			groupEntryOnlyKanaCache.put(groupEntryKey, groupEntryListInOnlyKanaCache);			
		}
		
		groupEntryListInOnlyKanaCache.add(groupEntry);		
	}	
	
	public List<Group> getGroupList(String kanji, String kana) {
		
		List<Group> result = new ArrayList<Group>();
		
		Set<Integer> uniqueGroupIds = new HashSet<Integer>();
		
		String groupEntryKey = getKey(kanji, kana);
		
		List<GroupEntry> groupEntryList = groupEntryCache.get(groupEntryKey);
		
		if (groupEntryList == null) {
			return result;
		}
		
		for (GroupEntry groupEntry : groupEntryList) {
			
			Group group = groupEntry.getGroup();
			
			if (uniqueGroupIds.contains(group.getId()) == false) {
				
				uniqueGroupIds.add(group.getId());
				
				result.add(group);
			}			
		}
		
		return result;
	}
	
	public List<GroupEntry> getGroupEntryList(String kanji, String kana) {
		
		String groupEntryKey = getKey(kanji, kana);
		
		return groupEntryCache.get(groupEntryKey);
	}

	public List<GroupEntry> getGroupEntryListInOnlyKanji(String kanji) {
		
		String groupEntryKey = getKey(kanji, null);
		
		return groupEntryOnlyKanjiCache.get(groupEntryKey);
	}

	public List<GroupEntry> getGroupEntryListInOnlyKana(String kana) {
		
		String groupEntryKey = getKey(null, kana);
		
		return groupEntryOnlyKanaCache.get(groupEntryKey);
	}
	
	public List<GroupEntry> getTheSameTranslateInTheSameGroupGroupEntryList(String kanji, String kana) throws DictionaryException {
		
		String groupEntryKey = getKey(kanji, kana);
		
		List<GroupEntry> groupEntryList = groupEntryCache.get(groupEntryKey);
		
		if (groupEntryList == null) {
			return groupEntryList;
		}
		
		if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
			kanji = "$$$NULL$$$";
		}
		
		boolean multiGroup = isMultiGroup(groupEntryList);
		
		if (multiGroup == true) {
			throw new DictionaryException("Multi group: " + kanji + " - " + kana);
		}
		
		groupEntryList = groupEntryList.get(0).getGroup().getGroupEntryList();
		
		Map<String, List<GroupEntry>> theSameTranslate = new TreeMap<String, List<GroupEntry>>(); 
		
		for (GroupEntry groupEntry : groupEntryList) {
			
			String groupEntryTranslate = groupEntry.getTranslateList().toString();
			
			List<GroupEntry> groupEntryForTheSameTranslateList = theSameTranslate.get(groupEntryTranslate);
			
			if (groupEntryForTheSameTranslateList == null) {
				groupEntryForTheSameTranslateList = new ArrayList<JMENewDictionary.GroupEntry>();
				
				theSameTranslate.put(groupEntryTranslate, groupEntryForTheSameTranslateList);
			}			
			
			groupEntryForTheSameTranslateList.add(groupEntry);
		}
		
		for (List<GroupEntry> groupEntryListWithTheSameTranslate : theSameTranslate.values()) {
			
			for (GroupEntry groupEntry : groupEntryListWithTheSameTranslate) {
				
				String groupEntryKanji = groupEntry.getKanji();
				
				if (groupEntryKanji == null || groupEntryKanji.equals("") == true || groupEntryKanji.equals("-") == true) {
					groupEntryKanji = "$$$NULL$$$";
				}			
				
				String groupEntryKana = groupEntry.getKana();
				
				if (kanji.equals(groupEntryKanji) == true && kana.equals(groupEntryKana) == true) {
					return groupEntryListWithTheSameTranslate;
				}				
			}			
		}
		
		return null;
	}
	
	public static boolean isMultiGroup(List<GroupEntry> groupEntryList) {
		
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
	
	private String getKey(String kanji, String kana) {
		
		if (kanji == null || kanji.equals("") == true || kanji.equals("-") == true) {
			kanji = "$$$NULL$$$";
		}
		
		if (kana != null) {
			return kanji + "." + kana;
			
		} else {
			return kanji;
		}
	}
	
	public static List<List<GroupEntry>> groupByTheSameTranslate(List<GroupEntry> groupEntryList) {
		
		Map<String, List<GroupEntry>> theSameTranslate = new TreeMap<String, List<GroupEntry>>(); 
		
		for (GroupEntry groupEntry : groupEntryList) {
			
			String groupEntryTranslate = groupEntry.getTranslateList().toString();
			
			List<GroupEntry> groupEntryForTheSameTranslateList = theSameTranslate.get(groupEntryTranslate);
			
			if (groupEntryForTheSameTranslateList == null) {
				groupEntryForTheSameTranslateList = new ArrayList<JMENewDictionary.GroupEntry>();
				
				theSameTranslate.put(groupEntryTranslate, groupEntryForTheSameTranslateList);
			}			
			
			groupEntryForTheSameTranslateList.add(groupEntry);
		}
		
		return new ArrayList<List<GroupEntry>>(theSameTranslate.values());
	}
	
	public static int mapPriorityToPower(String priority, int start) {
		
		Map<String, Integer> jmedictPriorityMap = new HashMap<String, Integer>();
				
		jmedictPriorityMap.put("gai1", 1);
		jmedictPriorityMap.put("ichi1", 2);
		jmedictPriorityMap.put("news1", 3);
		jmedictPriorityMap.put("spec1", 4);
		
		jmedictPriorityMap.put("gai2", 5);
		jmedictPriorityMap.put("ichi2", 6);
		jmedictPriorityMap.put("news2", 7);
		jmedictPriorityMap.put("spec2", 8);

		jmedictPriorityMap.put("nf01", 9);
		jmedictPriorityMap.put("nf02", 10);
		jmedictPriorityMap.put("nf03", 11);
		jmedictPriorityMap.put("nf04", 12);
		jmedictPriorityMap.put("nf05", 13);
		jmedictPriorityMap.put("nf06", 14);
		jmedictPriorityMap.put("nf07", 15);
		jmedictPriorityMap.put("nf08", 16);
		jmedictPriorityMap.put("nf09", 17);
		jmedictPriorityMap.put("nf10", 18);

		jmedictPriorityMap.put("nf11", 19);
		jmedictPriorityMap.put("nf12", 20);
		jmedictPriorityMap.put("nf13", 21);
		jmedictPriorityMap.put("nf14", 22);
		jmedictPriorityMap.put("nf15", 23);
		jmedictPriorityMap.put("nf16", 24);
		jmedictPriorityMap.put("nf17", 25);
		jmedictPriorityMap.put("nf18", 26);
		jmedictPriorityMap.put("nf19", 27);
		jmedictPriorityMap.put("nf20", 28);
		
		jmedictPriorityMap.put("nf21", 29);
		jmedictPriorityMap.put("nf22", 30);
		jmedictPriorityMap.put("nf23", 31);
		jmedictPriorityMap.put("nf24", 32);
		jmedictPriorityMap.put("nf25", 33);
		jmedictPriorityMap.put("nf26", 34);
		jmedictPriorityMap.put("nf27", 35);
		jmedictPriorityMap.put("nf28", 36);
		jmedictPriorityMap.put("nf29", 37);
		jmedictPriorityMap.put("nf30", 38);

		jmedictPriorityMap.put("nf31", 39);
		jmedictPriorityMap.put("nf32", 40);
		jmedictPriorityMap.put("nf33", 41);
		jmedictPriorityMap.put("nf34", 42);
		jmedictPriorityMap.put("nf35", 43);
		jmedictPriorityMap.put("nf36", 44);
		jmedictPriorityMap.put("nf37", 45);
		jmedictPriorityMap.put("nf38", 46);
		jmedictPriorityMap.put("nf39", 47);
		jmedictPriorityMap.put("nf40", 48);

		jmedictPriorityMap.put("nf41", 49);
		jmedictPriorityMap.put("nf42", 50);
		jmedictPriorityMap.put("nf43", 51);
		jmedictPriorityMap.put("nf44", 52);
		jmedictPriorityMap.put("nf45", 53);
		jmedictPriorityMap.put("nf46", 54);
		jmedictPriorityMap.put("nf47", 55);
		jmedictPriorityMap.put("nf48", 56);
		
		Integer power = jmedictPriorityMap.get(priority);
		
		if (power == null) {
			throw new RuntimeException("Can't find power for: " + priority);
		}
		
		return start + power;		
	}

	public static class Group {
		
		private Integer id;
		
		private JMEDictNewNativeEntry nativeEntry;
		
		private List<GroupEntry> groupEntryList = new ArrayList<JMENewDictionary.GroupEntry>();
		
		public Group(Integer id, JMEDictNewNativeEntry nativeEntry) {
			this.id = id;
			this.nativeEntry = nativeEntry;
		}

		public JMEDictNewNativeEntry getNativeEntry() {
			return nativeEntry;
		}

		public void setNativeEntry(JMEDictNewNativeEntry nativeEntry) {
			this.nativeEntry = nativeEntry;
		}

		public List<GroupEntry> getGroupEntryList() {
			return groupEntryList;
		}

		public void setGroupEntryList(List<GroupEntry> groupEntryList) {
			this.groupEntryList = groupEntryList;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}
	
	public static class GroupEntry {
		
		private JMEDictNewNativeEntry nativeEntry;
		
		private Group group;
		
		private Set<String> wordTypeList;
		
		private String kanji;		
		private List<String> kanjiInfoList;
		
		private String kana;
		private List<String> kanaInfoList;
		
		private String romaji;
		
		private List<GroupEntryTranslate> translateList;
		
		private List<String> similarRelatedList;
		
		private List<String> priority;
		
		public GroupEntry(JMEDictNewNativeEntry nativeEntry, Group group) {
			this.nativeEntry = nativeEntry;
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}

		public void setGroup(Group group) {
			this.group = group;
		}
		
		public JMEDictNewNativeEntry getNativeEntry() {
			return nativeEntry;
		}

		public void setNativeEntry(JMEDictNewNativeEntry nativeEntry) {
			this.nativeEntry = nativeEntry;
		}

		public String getKanji() {
			return kanji;
		}

		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public List<String> getKanjiInfoList() {
			return kanjiInfoList;
		}

		public void setKanjiInfoList(List<String> kanjiInfoList) {
			this.kanjiInfoList = kanjiInfoList;
		}

		public String getKana() {
			return kana;
		}

		public void setKana(String kana) {
			this.kana = kana;
		}

		public List<String> getKanaInfoList() {
			return kanaInfoList;
		}

		public void setKanaInfoList(List<String> kanaInfoList) {
			this.kanaInfoList = kanaInfoList;
		}

		public String getRomaji() {
			return romaji;
		}

		public void setRomaji(String romaji) {
			this.romaji = romaji;
		}

		public Set<String> getWordTypeList() {
			return wordTypeList;
		}

		public void setWordTypeList(Set<String> wordTypeList) {
			this.wordTypeList = wordTypeList;
		}

		public List<GroupEntryTranslate> getTranslateList() {
			return translateList;
		}

		public void setTranslateList(List<GroupEntryTranslate> translateList) {
			this.translateList = translateList;
		}
		
		public List<String> getPriority() {
			return priority;
		}

		public void setPriority(List<String> priority) {
			this.priority = priority;
		}

		public List<String> getSimilarRelatedList() {
			return similarRelatedList;
		}

		public void setSimilarRelatedList(List<String> similarRelatedList) {
			this.similarRelatedList = similarRelatedList;
		}

		public boolean containsAttribute(String attribute) {
			
			if (wordTypeList != null && wordTypeList.contains(attribute) == true) {
				return true;
			}
			
			if (kanjiInfoList != null && kanjiInfoList.contains(attribute) == true) {
				return true;
			}
			
			if (kanaInfoList != null && kanaInfoList.contains(attribute) == true) {
				return true;
			}
			
			if (translateList != null) {
				
				for (GroupEntryTranslate groupEntryTranslate : translateList) {
					
					if (groupEntryTranslate.getMiscInfoList() != null && groupEntryTranslate.getMiscInfoList().contains(attribute) == true) {
						return true;
					}
					
					if (groupEntryTranslate.getAdditionalInfoList() != null && groupEntryTranslate.getAdditionalInfoList().contains(attribute) == true) {
						return true;
					}
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			return "GroupEntry [wordTypeList=" + wordTypeList + ", kanji=" + kanji + ", kanjiInfoList=" + kanjiInfoList
					+ ", kana=" + kana + ", kanaInfoList=" + kanaInfoList + ", romaji=" + romaji + ", translateList="
					+ translateList + ", priority=" + priority + "]";
		}
	}
	
	public static class GroupEntryTranslate {
		
		private String translate;
		
		private List<String> miscInfoList;
		
		private List<String> additionalInfoList;

		public String getTranslate() {
			return translate;
		}

		public void setTranslate(String translate) {
			this.translate = translate;
		}

		public List<String> getMiscInfoList() {
			return miscInfoList;
		}

		public void setMiscInfoList(List<String> miscInfoList) {
			this.miscInfoList = miscInfoList;
		}

		public List<String> getAdditionalInfoList() {
			return additionalInfoList;
		}

		public void setAdditionalInfoList(List<String> additionalInfoList) {
			this.additionalInfoList = additionalInfoList;
		}

		@Override
		public String toString() {
			return "GroupEntryTranslate [translate=" + translate + ", miscInfoList=" + miscInfoList
					+ ", additionalInfoList=" + additionalInfoList + "]";
		}
		
		public String toJmedictRawData() {
			
			List<String> result = new ArrayList<String>();
			
			result.add("Translate: " + translate);
			
			if (miscInfoList != null && miscInfoList.size() > 0) {
				
				for (String currentMiscInfo : miscInfoList) {
					result.add("MiscInfo: " + currentMiscInfo);
				}
			}
			
			if (additionalInfoList != null && additionalInfoList.size() > 0) {
				
				for (String currentAdditionalInfo : additionalInfoList) {
					result.add("AdditionalInfo: " + currentAdditionalInfo);
				}
			}
			
			return Helper.convertListToString(result);
		}
	}
}
