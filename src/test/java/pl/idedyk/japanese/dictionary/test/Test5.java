package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class Test5 {

	public static void main(String[] args) throws Exception {
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		//List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {
			
			//if (jmeDictNewNativeEntry.getEnt_seq().intValue() == 1008390) {
				
				
			System.out.println(jmeDictNewNativeEntry);
			//}
			
			
		}
	}
}
