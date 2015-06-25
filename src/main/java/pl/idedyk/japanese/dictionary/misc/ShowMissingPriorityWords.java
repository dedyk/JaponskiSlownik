package pl.idedyk.japanese.dictionary.misc;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntryTranslate;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class ShowMissingPriorityWords {
	
	public static void main(String[] args) throws Exception {
		
		// wczytywanie slownika
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		// cache'owanie slownika
		Map<String, List<PolishJapaneseEntry>> cachePolishJapaneseEntryList = 
				pl.idedyk.japanese.dictionary.common.Utils.cachePolishJapaneseEntryList(polishJapaneseEntries);

		// wczytywanie slownika edict
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();

		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		List<Group> groupList = jmeNewDictionary.getGroupList();
		
		// common word writer
		CsvWriter csvWriter = new CsvWriter(new FileWriter("input/common_word.csv"), ',');
		
		int csvId = 1;
		
		for (Group group : groupList) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			List<List<GroupEntry>> groupByTheSameTranslateGroupEntryList = JMENewDictionary.groupByTheSameTranslate(groupEntryList);
						
			for (List<GroupEntry> groupEntryListTheSameTranslate : groupByTheSameTranslateGroupEntryList) {
				
				GroupEntry groupEntry = groupEntryListTheSameTranslate.get(0);
								
				List<String> priority = groupEntry.getPriority();
				
				if (priority.size() == 0) {
					continue;
				}
												
				String groupEntryKanji = groupEntry.getKanji();
				String groupEntryKana = groupEntry.getKana();
									
				List<PolishJapaneseEntry> findPolishJapaneseEntryList = pl.idedyk.japanese.dictionary.common.Utils.findPolishJapaneseEntry(cachePolishJapaneseEntryList, groupEntryKanji, groupEntryKana);
					
				if (findPolishJapaneseEntryList == null || findPolishJapaneseEntryList.size() == 0) {
						
					System.out.println(groupEntry);
					
					csvWriter.write(String.valueOf(csvId));
					csvWriter.write("");
					csvWriter.write(groupEntryKanji != null ? groupEntryKanji : "-");
					csvWriter.write(groupEntryKana);
					csvWriter.write(groupEntry.getWordTypeList().toString());
					
					List<GroupEntryTranslate> translateList = groupEntry.getTranslateList();
					
					List<String> translateStringList = new ArrayList<String>();
					
					for (GroupEntryTranslate groupEntryTranslate : translateList) {
						translateStringList.add(groupEntryTranslate.getTranslate());
					}					
					
					csvWriter.write(translateStringList.toString());

					csvWriter.endRecord();

					csvId++;						
				}				
			}
		}	
		
		csvWriter.close();
	}
}
