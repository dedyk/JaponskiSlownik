package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.GroupWithTatoebaSentenceList;
import pl.idedyk.japanese.dictionary.api.dto.TatoebaSentence;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.TatoebaSentencesParser;

public class Test4 {

	public static void main(String[] args) throws Exception {
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");;

		generateExampleSentence(polishJapaneseEntries, "../JapaneseDictionary_additional/tatoeba", "output/sentences.csv", "output/sentences_groups.csv");

	}
	
	private static void generateExampleSentence(List<PolishJapaneseEntry> dictionary, String tatoebaSentencesDir, 
			String sentencesDestinationFileName, String sentencesGroupsDestinationFileName) throws Exception {
		
		System.out.println("generateExampleSentence");
		
		TatoebaSentencesParser tatoebaSentencesParser = new TatoebaSentencesParser(tatoebaSentencesDir);
		
		tatoebaSentencesParser.parse();
		
		List<TatoebaSentence> uniqueSentences = new ArrayList<TatoebaSentence>();
		List<GroupWithTatoebaSentenceList> uniqueSentencesWithGroupList = new ArrayList<GroupWithTatoebaSentenceList>();
		
		Set<String> uniqueSentenceIds = new TreeSet<String>();
		Set<String> uniqueGroupIds = new TreeSet<String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : dictionary) {
			
			String word = null;
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				word = polishJapaneseEntry.getKanji();
				
			} else {
				word = polishJapaneseEntry.getKanaList().get(0);
			}
			
			List<GroupWithTatoebaSentenceList> exampleSentencesList = tatoebaSentencesParser.getExampleSentences(word);
			
			if (exampleSentencesList == null) {
				continue;
			}
			
			for (GroupWithTatoebaSentenceList currentExampleSentencesGroup : exampleSentencesList) {
				
				if (uniqueGroupIds.contains(currentExampleSentencesGroup.getGroupId()) == false) {					
					uniqueGroupIds.add(currentExampleSentencesGroup.getGroupId());
					
					uniqueSentencesWithGroupList.add(currentExampleSentencesGroup);					
				}				
				
				for (TatoebaSentence currentTatoebaSentenceInGroup : currentExampleSentencesGroup.getTatoebaSentenceList()) {
										
					if (uniqueSentenceIds.contains(currentTatoebaSentenceInGroup.getId()) == false) {
						
						uniqueSentenceIds.add(currentTatoebaSentenceInGroup.getId());
						uniqueSentences.add(currentTatoebaSentenceInGroup);
					}					
				}				
			}
		}
		
		Collections.sort(uniqueSentences, new Comparator<TatoebaSentence>() {

			@Override
			public int compare(TatoebaSentence o1, TatoebaSentence o2) {
				return new Long(o1.getId()).compareTo(new Long(o2.getId()));
			}
		});
		
		Collections.sort(uniqueSentencesWithGroupList, new Comparator<GroupWithTatoebaSentenceList>() {

			@Override
			public int compare(GroupWithTatoebaSentenceList o1, GroupWithTatoebaSentenceList o2) {
				return new Long(o1.getGroupId()).compareTo(new Long(o2.getGroupId()));
			}
		});
		
		CsvReaderWriter.writeTatoebaSentenceList(sentencesDestinationFileName, uniqueSentences);
		CsvReaderWriter.writeTatoebaSentenceGroupsList(sentencesGroupsDestinationFileName, uniqueSentencesWithGroupList);
	}

}
