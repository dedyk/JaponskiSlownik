package pl.idedyk.japanese.dictionary.misc;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.Group;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class ShowAllJMEdictPriorities {

	public static void main(String[] args) throws Exception {
		
		// wczytywanie slownika edict
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		List<Group> groupList = jmeNewDictionary.getGroupList();
		
		TreeMap<String, GroupEntry> allPriority = new TreeMap<String, GroupEntry>();
		
		for (Group group : groupList) {
			
			List<GroupEntry> groupEntryList = group.getGroupEntryList();
			
			for (GroupEntry groupEntry : groupEntryList) {
				
				List<String> priorities = groupEntry.getPriority();
				
				if (priorities.size() == 0) {
					continue;
				}				
				
				for (String priority : priorities) {
					
					if (allPriority.containsKey(priority) == false) {						
						allPriority.put(priority, groupEntry);						
					}
					
				}
			}
		}
		
		for (String priority : allPriority.keySet()) {
			
			System.out.println(priority + " - " + allPriority.get(priority));
			
		}
	}
}
