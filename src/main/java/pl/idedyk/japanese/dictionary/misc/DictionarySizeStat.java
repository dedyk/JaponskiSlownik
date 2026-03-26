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
		
		// INFO: trzeba zaczynac z repozytorium tylko z katalogiem .git
		
		final String dictionaryRepositoryPath = "sciezka_do_tymczasowego_repozytorium_ze_slownikiem";
		final String dictionaryAdditionalRepositoryPath = "sciezka_do_tymczasowego_repozytorium_z_dodatkami";
		
		// git checkout `git rev-list -n 1 --before="2012-03-10 00:00" master`
		
		//
		
		final Calendar currentCheckDate = Calendar.getInstance();
		
		currentCheckDate.set(2012, 2, 10, 23, 59, 59);
		
		//
		
		final Calendar currentDate = Calendar.getInstance();
		
		resetCalendar(currentDate, 23, 59, 59, 999);
		
		//
		
		List<DateStat> dateStatList = new ArrayList<DateStat>();
		
		//
				
		while (true) {
			
			String gitDateFormat = formatDateToGit(currentCheckDate);
			
			//
			
			checkoutGit(dictionaryRepositoryPath, gitDateFormat);
			checkoutGit(dictionaryAdditionalRepositoryPath, gitDateFormat);
			
			//
			
			DateStat dateStat = countWords(dictionaryRepositoryPath, dictionaryAdditionalRepositoryPath, currentCheckDate.getTime());
			
			dateStatList.add(dateStat);
			
			System.out.println(gitDateFormat + ": " + dateStat.wordCounter + " - " + dateStat.commonWordCounter + 
					" - " + dateStat.word2Counter + " - " + dateStat.wordCounterInDictionary2Source + " - " + dateStat.alljmdictCounter);
			
			//
						
			currentCheckDate.add(Calendar.DAY_OF_MONTH, 1);
			
			if (currentCheckDate.getTime().getTime() > currentDate.getTime().getTime()) {
				break;
			}
		}
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter(new File(dictionaryRepositoryPath, "dictionary_size_stat.csv")), ',');
		
		for (DateStat dateStat : dateStatList) {
			
			csvWriter.write(formatDateToStat(dateStat.date));
			csvWriter.write(String.valueOf(dateStat.wordCounter));
			csvWriter.write(String.valueOf(dateStat.commonWordCounter));
			csvWriter.write(String.valueOf(dateStat.word2Counter));
			csvWriter.write(String.valueOf(dateStat.wordCounterInDictionary2Source));
			csvWriter.write(String.valueOf(dateStat.alljmdictCounter));
			
			csvWriter.endRecord();
		}
		
		csvWriter.close();
	}
	
	private static void resetCalendar(Calendar calendar, int hour, int minute, int second, int millisecond) {
		
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
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
	
	private static DateStat countWords(String dictionaryRepositoryPath, String dictionaryAdditionalRepositoryPath, Date date) throws Exception {
		
		// word.csv
		final String[] wordCsvs = new String[] { "word.csv", "word01.csv", "word02.csv", "word03.csv", "word04.csv" };
		
		//
		
		int wordCounter = 0;
		int wordCounterInDictionary2Source = 0;
		
		for (String currentWordCsv : wordCsvs) {
			
			File currentWordFile = new File(dictionaryRepositoryPath + "/input", currentWordCsv);
			
			if (currentWordFile.exists() == false) {
				continue;
			}
			
			CsvReader csvReader = new CsvReader(new FileReader(currentWordFile));
			
			while (csvReader.readRecord()) {				
				wordCounter++;
				
				if (csvReader.get(12).contains("DICTIONARY2_SOURCE") == true) {
					wordCounterInDictionary2Source++;
				}
			}
			
			csvReader.close();
		}
		
		//
		
		// common_word.csv		
		final String[] commonWordCsvs = new String[] { "common_word.csv" };
		
		int commonWordCounter = 0;
		
		for (String currentWordCsv : commonWordCsvs) {
			
			File currentWordFile = new File(dictionaryRepositoryPath + "/input", currentWordCsv);
			
			if (currentWordFile.exists() == false) {
				continue;
			}
			
			CsvReader csvReader = new CsvReader(new FileReader(currentWordFile));
			
			while (csvReader.readRecord()) {
				
				if (csvReader.get(1).equals("1") == true) {
					continue;
				}
				
				commonWordCounter++;
			}
			
			csvReader.close();
		}
		
		// word2.csv
		int word2Counter = 0;
		
		File word2CsvFile = new File(dictionaryRepositoryPath + "/input/word2.csv");
		
		if (word2CsvFile.exists() == true) {
			
			CsvReader csvReader = new CsvReader(new FileReader(word2CsvFile));
			
			while (csvReader.readRecord()) {
								
				if (csvReader.get(0).equals("BEGIN") == false) {
					continue;
				}
				
				word2Counter++;
			}
			
			csvReader.close();
		}
		
		// JMdict_e
		int alljmdictCounter = 0;
		
		File alljmdictFile = new File(dictionaryAdditionalRepositoryPath + "/JMdict_e");
		
		if (alljmdictFile.exists() == true) {
			
			BufferedReader reader = new BufferedReader(new FileReader(alljmdictFile));
						
			while (true) {
				String line = reader.readLine(); // INFO: nie trzeba parsowac xml-a, gdyz kazdy tag jest w osobnej linii				
				
				if (line == null) {
					break;
				}
				
				if (line.contains("ent_seq") == false) {
					continue;
				}
				
				alljmdictCounter++;
			}
			
			reader.close();
		}
		
		//
				
		return new DateStat(date, wordCounter, wordCounterInDictionary2Source, commonWordCounter, word2Counter, alljmdictCounter);
	}
	
	private static class DateStat {
		
		private Date date;
		
		private int wordCounter;
		private int wordCounterInDictionary2Source;
		private int commonWordCounter;
		private int word2Counter;
		private int alljmdictCounter;

		public DateStat(Date date, int wordCounter, int wordCounterInDictionary2Source, int commonWordCounter, int word2Counter, int alljmdictCounter) {
			super();
			this.date = date;
			this.wordCounter = wordCounter;
			this.wordCounterInDictionary2Source = wordCounterInDictionary2Source;
			this.commonWordCounter = commonWordCounter;
			this.word2Counter = word2Counter;
			this.alljmdictCounter = alljmdictCounter;
		}		
	}
}
