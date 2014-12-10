package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class JMENewDictionary {

	private List<Group> groupList = new ArrayList<Group>();
	
	private TreeMap<String, List<GroupEntry>> groupEntryCache = new TreeMap<String, List<GroupEntry>>();
	
	public List<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}
	
	public TreeMap<String, List<GroupEntry>> getGroupEntryCache() {
		return groupEntryCache;
	}

	public void setGroupEntryCache(TreeMap<String, List<GroupEntry>> groupEntryCache) {
		this.groupEntryCache = groupEntryCache;
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
	
	public List<GroupEntry> getGroupEntryList(String kanji, String kana) {
		
		String groupEntryKey = getKey(kanji, kana);
		
		return groupEntryCache.get(groupEntryKey);
	}
	
	private String getKey(String kanji, String kana) {
		
		if (kanji == null) {
			kanji = "$$$NULL$$$";
		}
		
		return kanji + "." + kana;		
	}

	public static class Group {
		
		private JMEDictNewNativeEntry nativeEntry;
		
		private List<GroupEntry> groupEntryList = new ArrayList<JMENewDictionary.GroupEntry>();
		
		public Group(JMEDictNewNativeEntry nativeEntry) {
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
		
		private List<String> translateList;
		
		private List<String> additionalInfoList;

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

		public List<String> getTranslateList() {
			return translateList;
		}

		public void setTranslateList(List<String> translateList) {
			this.translateList = translateList;
		}

		public List<String> getAdditionalInfoList() {
			return additionalInfoList;
		}

		public void setAdditionalInfoList(List<String> additionalInfoList) {
			this.additionalInfoList = additionalInfoList;
		}

		@Override
		public String toString() {
			return "GroupEntry [wordTypeList=" + wordTypeList + ", kanji=" + kanji + ", kanjiInfoList=" + kanjiInfoList
					+ ", kana=" + kana + ", kanaInfoList=" + kanaInfoList + ", romaji=" + romaji + ", translateList="
					+ translateList + ", additionalInfoList=" + additionalInfoList + "]";
		}
	}
}
