package pl.idedyk.japanese.dictionary.misc;

import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.JMEDictReader;

public class GenerateSingleJMnedict {

	public static void main(String[] args) throws Exception {
		
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict.xml", true);
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmedictName);
		
		CsvReaderWriter.generateCsv("input_names2/names.csv", generatedNames, false, true);
	}
}
