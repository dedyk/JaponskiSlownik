package pl.idedyk.japanese.dictionary.misc;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.CommonWord;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGeneratorHelper;

public class ShowDuplicateInCommonWord {

	public static void main(String[] args) throws Exception {
		
		final WordGeneratorHelper wordGeneratorHelper = new WordGeneratorHelper(new String[] { "input/word01.csv", "input/word02.csv" }, "input/common_word.csv", 
				"../JapaneseDictionary_additional/JMdict_e");
		
		//
		
		Map<Integer, CommonWord> resultCommonWordMap = new LinkedHashMap<>();
		
		//
		
		Map<String, List<CommonWord>> commonWordExistsMap = wordGeneratorHelper.getCommonWordExistsMap();
		
		Iterator<String> keySetIterator = commonWordExistsMap.keySet().iterator();
		
		while (keySetIterator.hasNext() == true) {
			
			String key = keySetIterator.next();
			
			List<CommonWord> commonWordListForKey = commonWordExistsMap.get(key);
			
			if (commonWordListForKey.size() > 1 && commonWordListForKey.get(0).isDone() == false) {
				
				for (int idx = 0; idx < commonWordListForKey.size(); ++idx) {
					
					CommonWord commonWord = commonWordListForKey.get(idx);
					
					resultCommonWordMap.put(commonWord.getId(), commonWord);
					
					if (idx != 0) {
						commonWord.setDone(true);
					}
				}
			}
		}
		
		CsvReaderWriter.writeCommonWordFile(resultCommonWordMap, "input/common_word-duplicate.csv");
		CsvReaderWriter.writeCommonWordFile(wordGeneratorHelper.getCommonWordMap(), "input/common_word-new.csv");
	}
}
