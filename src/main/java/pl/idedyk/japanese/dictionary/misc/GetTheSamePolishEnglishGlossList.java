package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.csvreader.CsvWriter;

import pl.idedyk.japanese.dictionary.common.Helper;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class GetTheSamePolishEnglishGlossList {
	
	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();		
		JMdict jmdict = dictionary2Helper.getPolishJMdict();
		
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(new FileOutputStream(new File("/tmp/a/a.csv"))), ',');
		
		for (JMdict.Entry entry : jmdict.getEntryList()) {
						
			List<Sense> senseList = entry.getSenseList();
			
			for (Sense sense : senseList) {
				
				final List<String> polGlossList = new ArrayList<>();
				final List<String> engGlossList = new ArrayList<>();
				
				sense.getGlossList().stream().forEach(c -> {
					if (c.getLang() == null || c.getLang().equals("eng") == true) {
						engGlossList.add(c.getValue());
					}

					if (c.getLang().equals("pol") == true) {
						polGlossList.add(c.getValue());
					}
				});
				
				if (polGlossList.equals(engGlossList) == true) {
					csvWriter.write("" + entry.getEntryId());
					csvWriter.write(Helper.convertListToString(engGlossList));
					csvWriter.write(Helper.convertListToString(polGlossList));
					csvWriter.write(Helper.convertListToString(sense.getAdditionalInfoList().stream().filter(senseAdditionalInfo -> (senseAdditionalInfo.getLang().equals("pol") == true)).map(m -> m.getValue()).collect(Collectors.toList())));
					
					csvWriter.endRecord();
				}
			}			
		}	
		
		csvWriter.close();
	}
}
