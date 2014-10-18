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
			
			/*
			if (translate.matches(".*\\ Tumulus$") == true) {
				
				translate = translate.replaceAll("\\ Tumulus$", " (tumulus, kurhan)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(grave\\)$") == true) {
				
				translate = translate.replaceAll(" \\(grave\\)$", " (grób)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Medical School$") == true) {
				
				translate = translate.replaceAll(" Medical School$", " (szkoła medyczna)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Gakuen University$") == true) {
				
				translate = translate.replaceAll(" Gakuen University$", " Gakuen (uniwersytet)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Gakuin University$") == true) {
				
				translate = translate.replaceAll(" Gakuin University$", " Gakuin (uniwersytet)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Women's Junior College$") == true) {
				
				translate = translate.replaceAll(" Women's Junior College$", " (żeńskie studia licencjackie)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Junior College$") == true) {
				
				translate = translate.replaceAll(" Junior College$", " (studia licencjackie)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Baseball Stadium$") == true) {
				
				translate = translate.replaceAll(" Baseball Stadium$", " (stadion baseball'a)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*Museum.*") == true) {
				
				translate = translate.replaceAll(" Museum ", " (muzeum)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Hut$") == true) {
				
				translate = translate.replaceAll(" Hut$", " (schronisko, w górach)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Skyline.*") == true) {
				
				translate = translate.replaceAll(" Skyline", " (malownicza górska droga)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(river\\)$") == true) {
				
				translate = translate.replaceAll(" \\(river\\)$", " (rzeka)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\(island\\)$") == true) {
				
				translate = translate.replaceAll(" \\(island\\)$", " (wyspa)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\(lake\\)$") == true) {
				
				translate = translate.replaceAll(" \\(lake\\)$", " (jezioro)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\(mountain\\)$") == true) {
				
				translate = translate.replaceAll(" \\(mountain\\)$", " (góra)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(islands\\)$") == true) {
				
				translate = translate.replaceAll(" \\(islands\\)$", " (archipelag)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			if (translate.matches(".*\\(cape\\)$") == true) {
				
				translate = translate.replaceAll(" \\(cape\\)$", " (przylądek)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}

			
			newWordPlaceList.add(currentPolishJapaneseEntry);
		}		
		
		CsvReaderWriter.generateCsv("input_names/WORD_PLACE.csv", newWordPlaceList, false);
	}
}
