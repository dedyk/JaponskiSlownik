package pl.idedyk.japanese.dictionary.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class DictionarySizeStat {

	public static void main(String[] args) throws Exception {
		
		final String repositoryPath = "sciezka_do_tymczasowego_repozytorium";
		
		// git checkout `git rev-list -n 1 --before="2012-03-10 00:00" master`
		
		//
		
		final Calendar currentCheckDate = Calendar.getInstance();
		
		currentCheckDate.set(2012, 2, 10, 0, 0, 0);
		
		//
		
		final Calendar currentDate = Calendar.getInstance();
		
		//
		
		List<DateStat> dateStatList = new ArrayList<DateStat>();
		
		//
				
		while (true) {
			
			String gitDateFormat = formatDateToGit(currentCheckDate);
			
			//
			
			checkoutGit(repositoryPath, gitDateFormat);
			
			//
			
			int countWords = countWords(repositoryPath);
			
			dateStatList.add(new DateStat(currentCheckDate.getTime(), countWords));
			
			System.out.println(gitDateFormat + ": " + countWords);			
			
			//
						
			currentCheckDate.add(Calendar.DAY_OF_MONTH, 1);
			
			if (currentCheckDate.getTime().getTime() >= currentDate.getTime().getTime()) {
				break;
			}
		}
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(new File(repositoryPath, "dictionary_size_stat.csv")), ',');
		
		for (DateStat dateStat : dateStatList) {
			
			csvWriter.write(formatDateToStat(dateStat.date));
			csvWriter.write(String.valueOf(dateStat.stat));
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}

	private static String formatDateToGit(Calendar calendar) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		return sdf.format(calendar.getTime());
	}

	private static String formatDateToStat(Date date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return sdf.format(date);
	}
	
	private static void checkoutGit(String repositoryPath, String gitDateFormat) throws IOException, InterruptedException {
				
		StringBuffer scriptSb = new StringBuffer();
		
		scriptSb.append("#!/bin/bash\n\n");
		
		scriptSb.append("cd " + repositoryPath + "\n\n");
		scriptSb.append("git checkout `git rev-list -n 1 --before=\"" + gitDateFormat + "\" master`\n");
		
		//
		
		File scriptFile = new File(repositoryPath, "dictionary_size_stat.sh");
		
		IOUtils.write(scriptSb.toString().getBytes(), new FileOutputStream(scriptFile));
		
		//
		
		ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptFile.getAbsolutePath());
		
		processBuilder.redirectErrorStream(true);
		
        Process p = processBuilder.start();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        while (true) {
        	
            String line = bufferedReader.readLine();
            
            if (line == null) { 
            	break;
            }
            
            //System.out.println(line);
        }
	}
	
	private static int countWords(String repositoryPath) throws IOException {
		
		final String[] wordCsvs = new String[] { "word.csv", "word01.csv", "word02.csv" };
		//final String[] wordCsvs = new String[] { "common_word.csv" };
		
		//
		
		int counter = 0;
		
		for (String currentWordCsv : wordCsvs) {
			
			File currentWordFile = new File(repositoryPath + "/input", currentWordCsv);
			
			if (currentWordFile.exists() == false) {
				continue;
			}
			
			CsvReader csvReader = new CsvReader(new FileReader(currentWordFile));
			
			while (csvReader.readRecord()) {
				
				/* Dla common_word
				if (csvReader.get(1).equals("1") == true) {
					continue;
				}
				*/
				
				counter++;
			}
			
			csvReader.close();
		}
				
		return counter;
	}
	
	private static class DateStat {
		
		private Date date;
		
		private int stat;

		public DateStat(Date date, int stat) {
			super();
			this.date = date;
			this.stat = stat;
		}		
	}
}
