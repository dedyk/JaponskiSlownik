package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class Test5 {

	public static void main(String[] args) throws Exception {

		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");

		List<JMEDictNewNativeEntry> jmedictNativeTestList = new ArrayList<JMEDictNewNativeEntry>();

		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {

			if (jmeDictNewNativeEntry.getEnt_seq().intValue() == 1001130) {

				jmedictNativeTestList.add(jmeDictNewNativeEntry);

				System.out.println(jmeDictNewNativeEntry);
			}
		}
		
		System.out.println();
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeTestList);
		
		for (Group group : jmeNewDictionary.getGroupList()) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				Set<String> wordTypeList = groupEntry.getWordTypeList();
				
				String kanji = groupEntry.getKanji();
				List<String> kanjiInfoList = groupEntry.getKanjiInfoList();
				
				String kana = groupEntry.getKana();
				List<String> kanaInfoList = groupEntry.getKanaInfoList();
				
				String romaji = groupEntry.getRomaji();
				
				List<String> translateList = groupEntry.getTranslateList();				
				List<String> additionalInfoList = groupEntry.getAdditionalInfoList();
				
				System.out.println("WordTypeList: " + wordTypeList);
				
				System.out.println("Kanji: " + kanji);
				System.out.println("KanjiInfoList: " + kanjiInfoList);
				
				System.out.println("Kana: " + kana);
				System.out.println("KanaInfoList: " + kanaInfoList);
				
				System.out.println("Romaji: " + romaji);
				
				System.out.println("TranslateList: " + translateList);
				System.out.println("AdditionalInfoList: " + additionalInfoList);
				
				System.out.println("---\n");
			}
		}
	}
}
