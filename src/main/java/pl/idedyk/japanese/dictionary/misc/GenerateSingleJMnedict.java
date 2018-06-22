package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictNewReader;

public class GenerateSingleJMnedict {

	public static void main(String[] args) throws Exception {
		
		//TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNameNativeList = jmedictNewReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNameNativeList);
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmeNewDictionary);
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/names.csv" }, generatedNames, true, false, true, false, null);
	}
}
