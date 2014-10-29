package pl.idedyk.japanese.dictionary.misc;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

public class SplitJMnedictWordPlaceCsv {

	public static void main(String[] args) throws Exception {
		
		final String[] matches = {
			/*
			"^([A-Z]|[a-z]|'|-)*$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(świątynia\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(zapora wodna\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(pole golfowe\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(miasto\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(fabryka\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(park\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(szpital\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(elektrownia\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(strefa przemysłowa\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(strefa wulkaniczna\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(więzienie\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(ruiny historyczne\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(park lekkoatletyczny\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(droga ekspresowa\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(żeński uniwersytet\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(onsen\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(uniwersytet\\)$",
			"^([A-Z]|[a-z]|'|-)*\\ \\(muzeum sztuki\\)$",
			".*\\(lotnisko\\).*",
			".*\\(kotlina\\).*",
			".*\\(pomnik pamięci\\).*",
			".*\\(kulturalny pomnik pamięci\\).*",
			".*\\(kolejowy pomnik pamięci\\).*",
			".*\\(elektrownia cieplna\\).*",
			".*\\(elektrownia nuklearna\\).*",
			".*\\(podziemna elektrownia wodna\\).*",
			".*\\(elektrownia hydroelektryczna\\).*",
			".*\\(oczyszczalnia wody\\).*",
			".*\\(zamek\\).*",
			".*\\(Chiny\\).*",
			".*prefekt.*",
			".*\\(tumulus, kurhan\\).*",
			".*\\(budynek\\).*",
			".*\\(grób\\).*",
			".*\\(szkoła medyczna\\).*",
			".*\\(klub golfowy, pole golfowe\\).*",
			".*\\ Gakuen \\(uniwersytet\\).*",
			".*\\ Gakuin \\(uniwersytet\\).*",
			".*\\(żeńskie studia licencjackie\\).*",
			".*\\(studia licencjackie\\).*",
			".*stadion baseball'a.*",
			".*\\(obserwatorium astronomiczne\\).*",
			".*\\(schronisko, w górach\\).*",
			".*\\(malownicza górska droga\\).*",
			".*\\(rzeka\\).*",
			".*\\(wyspa\\).*",
			".*\\(jezioro\\).*",
			".*\\(góra\\).*",
			".*\\(archipelag\\).*",
			".*\\(przylądek\\).*",
			".*\\(region górski\\).*",
			".*\\(pustynia\\).*",
			".*\\(kanał\\).*",
			".*\\(muzeum\\).*",
			".*\\(pole golfowe\\).*",
			".*\\(szczyt\\).*",
			".*\\(przełęcz\\).*",
			".*\\(pasmo górskie\\).*",
			".*\\(lodowiec\\).*",
			".*\\(półwysep\\).*",
			".*\\(płaskowyż\\).*",
			".*\\(archipelag\\).*",
			".*\\(wieś\\).*",
			".*\\(stan w USA\\).*",
			".*\\(Anglia\\).*",
			".*\\(zatoka\\).*",
			".*\\(nizina\\).*",
			".*\\(duńska prowincja\\).*",
			".*\\(baza\\).*",
			"^góra .*",
			".* \\(plaża\\) \\(park\\)$",
			".*\\(pasmo górskie\\).*",
			".*\\(park historii\\).*",
			".*\\(park historii narodowej\\).*",
			".*\\(park wojewódźki\\).*",
			".*\\(park narodowy\\).*",
			".*\\(park stanowy\\).*",
			".* Kanal$",
			".*\\(płaskowyż\\).*",
			".*\\(obwodnica\\).*",
			".*\\(przylądek\\).*",
			".*\\(poprawcza szkoła\\).*",
			".*\\(cmentarz\\).*",
			".*\\(pole namiotowe\\).*",
			".*\\(miejsce letnich wakacyjnych domów\\).*",
			".*\\(cieśnina\\).*",
			".*\\(przędzalnia\\).*",	
			*/		
			"^([A-Z]|[a-z]|'|-)*\\ \\(tunel\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Anglia\\)$",	
			"^([A-Z]|[a-z]|'|-| )* \\(Afganistan\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Nowa Zelandia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Finlandia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Republika Południowej Afryki\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Indie\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Holandia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Izrael\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kanada\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Szwajcaria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Argentyna\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Demokratyczna Republika Konga\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Malezja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Indie\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Arabia Saudyjska\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Grecja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Hiszpania\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Brazylia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Tunezja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Peru\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Meksyk\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Iran\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Rumunia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Algeria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Wietnam\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Mjanmie\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Wenezuela\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Egipt\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kuba\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Dania\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Portugalia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kolumbia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Norwegia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Szwecja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Sri Lanka\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Papua-Nowa Gwinea\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Indonezja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Pakistan\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Czechy, Słowacja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Tanzania\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Turcja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kenia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Irak\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Zimbabwe\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Belgia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Filipiny\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Urugwaj\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Hawaje\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Tajlandia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Chile\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Ekwador\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Nigeria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Sierra Leone\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Uganda\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Węgry\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kamerun\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Syria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Mongolia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Sudan\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Gwinea\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Mozambik\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Etiopia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Paragwaj\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kostaryka\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Ghana\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Chorwacja\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Boliwia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kongo\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Salwador\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Bangladesz\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Nepal\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Austria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Togo\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Bułgaria\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Honduras\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Barbados\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Maroko\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Laos\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Samoa\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Angola\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Afryka\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Unia Arabska\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Rumunia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Serbia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Jordan\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Irlandia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Albania\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Gwatemala\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Kambodża\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Panama\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Libia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Bośnia i Hercegowina\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Madagaskar\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Macedonia\\)$",
			"^([A-Z]|[a-z]|'|-| )* \\(Cypr\\)$",
		};
		
		List<PolishJapaneseEntry> wordPlaceList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names/miss2/WORD_PLACE.csv");
		
		List<PolishJapaneseEntry> readyWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		List<PolishJapaneseEntry> waitingWordPlaceList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry currentPolishJapaneseEntry : wordPlaceList) {
			
			String translate = currentPolishJapaneseEntry.getTranslates().get(0);
			
			//System.out.println(translate);
			
			boolean ok = false;
			
			for (String currentMatch : matches) {
				
				if (translate.matches(currentMatch) == true) {
					ok = true;
					
					break;
				}				
			}
			
			if (ok == false) {
				String info = currentPolishJapaneseEntry.getInfo();
				
				if (info.equals("GOTOWE") == true) {
					ok = true;
				}
			}			
			
			if (ok == true) {
				readyWordPlaceList.add(currentPolishJapaneseEntry);
			} else {
				waitingWordPlaceList.add(currentPolishJapaneseEntry);
			}
		}		
		
		CsvReaderWriter.generateCsv("input_names/miss2/WORD_PLACE-gotowe.csv", readyWordPlaceList, false);
		CsvReaderWriter.generateCsv("input_names/miss2/WORD_PLACE-oczekujace.csv", waitingWordPlaceList, false);
	}
}
