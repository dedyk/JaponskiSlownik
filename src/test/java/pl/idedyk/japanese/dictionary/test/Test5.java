package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class Test5 {

	public static void main(String[] args) throws Exception {
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e-TEST");
		
		for (JMEDictNewNativeEntry jmeDictNewNativeEntry : jmedictNativeList) {
			
			System.out.println(jmeDictNewNativeEntry);
		}
	}
}
