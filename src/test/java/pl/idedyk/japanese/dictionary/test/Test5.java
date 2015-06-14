package pl.idedyk.japanese.dictionary.test;

import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class Test5 {

	public static void main(String[] args) throws Exception {

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");

		/*
		List<JMEDictNewNativeEntry> jmedictNativeTestList = new ArrayList<JMEDictNewNativeEntry>();

		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {

			if (jmeDictNewNativeEntry.getEnt_seq().intValue() == 5000037) {

				jmedictNativeTestList.add(jmeDictNewNativeEntry);

				System.out.println(jmeDictNewNativeEntry);
			}
		}
		
		System.out.println();
		*/
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		/*
		List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList("水瓜", "すいか");
		
		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
		
		//List<GroupEntry> groupEntryList = jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList("月立ち", "つきたち");
		
		List<Group> groupList = jmeNewDictionary.getGroupList("正身", "そうじみ");
		
		for (Group group : groupList) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {							
				printGroupEntry(groupEntry);
			}
		}
				
		/*
		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
		
		
		/*
		for (Group group : jmeNewDictionary.getGroupList()) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				printGroupEntry(groupEntry);
			}
		}
		*/
		
		/*
		List<GroupEntry> groupEntryList = jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList("ＪＲ三山木駅", "ジェイアールみやまきえき");

		for (GroupEntry groupEntry : groupEntryList) {							
			printGroupEntry(groupEntry);
		}
		*/
	}
	
	private static void printGroupEntry(GroupEntry groupEntry) {

		Set<String> wordTypeList = groupEntry.getWordTypeList();
		
		String kanji = groupEntry.getKanji();
		List<String> kanjiInfoList = groupEntry.getKanjiInfoList();
		
		String kana = groupEntry.getKana();
		List<String> kanaInfoList = groupEntry.getKanaInfoList();
		
		String romaji = groupEntry.getRomaji();
		
		List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();		
		
		System.out.println("WordTypeList: " + wordTypeList);
		
		System.out.println("Kanji: " + kanji);
		System.out.println("KanjiInfoList: " + kanjiInfoList);
		
		System.out.println("Kana: " + kana);
		System.out.println("KanaInfoList: " + kanaInfoList);
		
		System.out.println("Romaji: " + romaji);
		
		System.out.println("TranslateList: " + translateList);
		
		System.out.println("---\n");		
	}
}
