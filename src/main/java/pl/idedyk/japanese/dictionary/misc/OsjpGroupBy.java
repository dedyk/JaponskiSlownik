package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class OsjpGroupBy {

	public static void main(String[] args) throws Exception {
	
		CsvReader csvReader = new CsvReader(new FileReader("input/osjp.csv"), ',');
		
		TreeMap<String, List<String>> groupByResult = new TreeMap<String, List<String>>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				int o1HashCode = Arrays.hashCode(o1.getBytes());
				int o2HashCode = Arrays.hashCode(o2.getBytes());
				
				return Integer.compare(o1HashCode, o2HashCode);
			}
		});
		
		while(csvReader.readRecord()) {
			
			String kanji = csvReader.get(0);
			String translate = csvReader.get(1);
			
			System.out.println(kanji + " - " + translate);
			
			List<String> translateForKanji = groupByResult.get(kanji);
			
			if (translateForKanji == null) {
				translateForKanji = new ArrayList<String>();
			}
			
			translateForKanji.add(translate);
			
			groupByResult.put(kanji, translateForKanji);			
		}
		
		csvReader.close();
		
		Iterator<String> groupByResultIterator = groupByResult.keySet().iterator();		
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter("input/osjp-2.csv"), ',');
		
		while (groupByResultIterator.hasNext()) {
			
			String currentKey = groupByResultIterator.next();
			
			csvWriter.write(currentKey);
			csvWriter.write(convertListToString(groupByResult.get(currentKey)));
			
			csvWriter.endRecord();
		}		
		
		csvWriter.close();
	}
	
	private static String convertListToString(List<?> list) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx));
			
			if (idx != list.size() - 1) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
}
