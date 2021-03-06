package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class ChangeJMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
				
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input_names/miss2/WORD_PLACE.csv" });
		
		List<PolishJapaneseEntry> newWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			//String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			String info = currentPolishJapaneseEntry.getInfo();
			
			if (info.equals("GOTOWE") == true) {
				newWordPlaceList.add(currentPolishJapaneseEntry);
				
				continue;
			}
			
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
			
			/*
			if (translate.matches(".*\\(cape\\)$") == true) {
				
				translate = translate.replaceAll(" \\(cape\\)$", " (przylądek)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(mountain region\\)$") == true) {
				
				translate = translate.replaceAll(" \\(mountain region\\)$", " (region górski)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(desert\\)$") == true) {
				
				translate = translate.replaceAll(" \\(desert\\)$", " (pustynia)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(channel\\)$") == true) {
				
				translate = translate.replaceAll(" \\(channel\\)$", " (kanał)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Museum$") == true) {
				
				translate = translate.replaceAll(" Museum$", " (muzeum)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Peak$") == true) {
				
				translate = translate.replaceAll(" Peak$", " (szczyt)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(pass\\)$") == true) {
				
				translate = translate.replaceAll(" \\(pass\\)$", " (przełęcz)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(mountain range\\)$") == true) {
				
				translate = translate.replaceAll(" \\(mountain range\\)$", " (pasmo górskie)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(glacier\\)$") == true) {
				
				translate = translate.replaceAll(" \\(glacier\\)$", " (lodowiec)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(peninsula\\)$") == true) {
				
				translate = translate.replaceAll(" \\(peninsula\\)$", " (półwysep)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\(plateau\\)$") == true) {
				
				translate = translate.replaceAll(" \\(plateau\\)$", " (płaskowyż)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(archipelago\\)$") == true) {
				
				translate = translate.replaceAll(" \\(archipelago\\)$", " (archipelag)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(village\\)$") == true) {
				
				translate = translate.replaceAll(" \\(village\\)$", " (wieś)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(state\\)$") == true) {
				
				translate = translate.replaceAll(" \\(state\\)$", " (stan w USA)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(Britain\\)$") == true) {
				
				translate = translate.replaceAll(" \\(Britain\\)$", " (Anglia)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(bay\\)$") == true) {
				
				translate = translate.replaceAll(" \\(bay\\)$", " (zatoka)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".*\\(lowland\\)$") == true) {
				
				translate = translate.replaceAll(" \\(lowland\\)$", " (nizina)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}

			if (translate.matches(".*\\(Dutch province\\)$") == true) {
				
				translate = translate.replaceAll(" \\(Dutch province\\)$", " (duńska prowincja)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".*\\(base\\)$") == true) {
				
				translate = translate.replaceAll(" \\(base\\)$", " (baza)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches("^Mt\\..*") == true) {
				
				translate = translate.replaceAll("Mt\\. ", "góra ");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Beach \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" Beach \\(park\\)$", " (plaża) (park)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Bay$") == true) {
				
				translate = translate.replaceAll(" Bay$", " (zatoka)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* River$") == true) {
				
				translate = translate.replaceAll(" River$", " (rzeka)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Island$") == true) {
				
				translate = translate.replaceAll(" Island$", " (wyspa)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Islands$") == true) {
				
				translate = translate.replaceAll(" Islands$", " (archipelag)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Lake$") == true) {
				
				translate = translate.replaceAll(" Lake$", " (jezioro)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches("^Lake .*$") == true) {
				
				translate = translate.replaceAll("^Lake ", "");
				translate += " (jezioro)";
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Mountains$") == true) {
				
				translate = translate.replaceAll(" Mountains$", " (pasmo górskie)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Factory$") == true) {
				
				translate = translate.replaceAll(" Factory$", " (fabryka)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Park$") == true) {
				
				translate = translate.replaceAll(" Park$", " (park)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* National \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" National \\(park\\)$", " (park narodowy)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* State \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" State \\(park\\)$", " (park stanowy)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Historic \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" Historic \\(park\\)$", " (park historii)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* National Historic \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" National Historic \\(park\\)$", " (park historii narodowej)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Provincial \\(park\\)$") == true) {
				
				translate = translate.replaceAll(" Provincial \\(park\\)$", " (park wojewódźki)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Club golf links$") == true) {
				
				translate = translate.replaceAll(" golf links$", " (pole golfowe)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* Golf Club \\(pole golfowe\\)$") == true) {
				
				translate = translate.replaceAll(" Golf Club \\(pole golfowe\\)$", " (klub golfowy, pole golfowe)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Plateau$") == true) {
				
				translate = translate.replaceAll(" Plateau$", " (płaskowyż)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* byepass$") == true) {
				
				translate = translate.replaceAll(" byepass$", " (obwodnica)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Point$") == true) {
				
				translate = translate.replaceAll(" Point$", " (przylądek)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Reform School$") == true) {
				
				translate = translate.replaceAll(" Reform School$", " (poprawcza szkoła)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Cemetery$") == true) {
				
				translate = translate.replaceAll(" Cemetery$", " (cmentarz)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* Camping Ground$") == true) {
				
				translate = translate.replaceAll(" Camping Ground$", " (pole namiotowe)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* holiday home area$") == true) {
				
				translate = translate.replaceAll(" holiday home area$", " (miejsce letnich wakacyjnych domów)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/			

			/*
			if (translate.matches(".* Channel$") == true) {
				
				translate = translate.replaceAll(" Channel$", " (kanał)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}			
			*/
			
			/*
			if (translate.matches(".* Straits$") == true) {
				
				translate = translate.replaceAll(" Straits$", " (cieśnina)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/			

			/*
			if (translate.matches(".* Reservoir$") == true) {
				
				translate = translate.replaceAll(" Reservoir$", " (jezioro)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/			

			/*
			if (translate.matches(".* spinning mill$") == true) {
				
				translate = translate.replaceAll(" spinning mill$", " (przędzalnia)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/
			
			/*
			if (translate.matches(".* tunnel$") == true) {
				
				translate = translate.replaceAll(" tunnel", " (tunel)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			/*
			if (translate.matches(".* dam$") == true) {
				
				translate = translate.replaceAll(" dam", " (zapora wodna)");
				
				currentPolishJapaneseEntry.setTranslates(Arrays.asList(translate));
			}
			*/

			newWordPlaceList.add(currentPolishJapaneseEntry);
		}		
		
		CsvReaderWriter.generateCsv(new String[] { "input_names/miss2/WORD_PLACE.csv" }, newWordPlaceList, true, false, true, false, null);
	}
}
