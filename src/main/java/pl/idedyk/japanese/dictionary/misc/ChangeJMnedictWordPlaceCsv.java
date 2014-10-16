package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ChangeJMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
				
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/WORD_PLACE.csv");
		
		List<PolishJapaneseEntry> newWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			/*
			if (translate.matches("^([A-Z]|[a-z]|'|-)*\\ University$") == true) {
				
				translate = translate.replaceAll(" University$", " (uniwersytet)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\ Thermal \\(elektrownia\\)$") == true) {
				
				translate = translate.replaceAll(" Thermal \\(elektrownia\\)$", " (elektrownia cieplna)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\ Nuclear \\(elektrownia\\)$") == true) {
				
				translate = translate.replaceAll(" Nuclear \\(elektrownia\\)$", " (elektrownia nuklearna)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\ underground \\(elektrownia\\)$") == true) {
				
				translate = translate.replaceAll(" underground \\(elektrownia\\)$", " (podziemna elektrownia wodna)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\ water purification plant$") == true) {
				
				translate = translate.replaceAll(" water purification plant$", " (oczyszczalnia wody)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			/*
			if (translate.matches(".*\\ Astronomical Observatory$") == true) {
				
				translate = translate.replaceAll(" Astronomical Observatory$", " (obserwatorium astronomiczne)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			/*
			if (translate.matches(".*\\(China\\)$") == true) {
				
				translate = translate.replaceAll(" \\(China\\)$", " (Chiny)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\ Prefecture$") == true) {
				
				translate = translate.replaceAll("\\ Prefecture$", " (prefektura)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			if (translate.matches(".*\\ Tumulus$") == true) {
				
				translate = translate.replaceAll("\\ Tumulus$", " (tumulus, kurhan)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			

			newWordPlaceList.add(currentPolishJapaneseEntry);
		}		
		
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE.csv", newWordPlaceList, false);
	}
}
