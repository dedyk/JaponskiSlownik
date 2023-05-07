package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary.common.Helper;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2NameHelper;

public class GenerateSingleJMnedict {

	public static void main(String[] args) throws Exception {
		
		//TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml");
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		Dictionary2NameHelper dictionary2NameHelper = Dictionary2NameHelper.getOrInit();
				
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(dictionary2Helper, dictionary2NameHelper);
		
		CsvReaderWriter.generateCsv(new String[] { "input_names2/names.csv" }, generatedNames, true, false, true, false, null);
	}
}
