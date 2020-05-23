package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;


public class DetectMultiGroup {
	
	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e", "input/kanji.csv", "../JapaneseDictionary_additional/kradfile", "../JapaneseDictionary_additional/kanjidic2.xml");
		
		List<PolishJapaneseEntry> polishJapaneseEntriesList = wordGeneratorHelper.getPolishJapaneseEntriesList();
		
		JMENewDictionary jmeNewDictionary = wordGeneratorHelper.getJMENewDictionary();
		
		List<Integer> idList = new ArrayList<>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntriesList) {
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(polishJapaneseEntry.getKanji(), polishJapaneseEntry.getKana());
			
			if (groupEntryList == null) {
				continue;
			}
			
			boolean multiGroup = JMENewDictionary.isMultiGroup(groupEntryList);
			
			if (multiGroup == true) {
				
				idList.add(polishJapaneseEntry.getId());
				
				System.out.println(polishJapaneseEntry.getId());
			}			
		}
		
		System.out.print("./word-generator.sh find-words-with-jmedict-change -s 888888 --ignore-dictionary-filled-raw-data -wid ");
		
		for (Integer id : idList) {
			System.out.print(id + ",");			
		}
		
		System.out.println();
	}
}
