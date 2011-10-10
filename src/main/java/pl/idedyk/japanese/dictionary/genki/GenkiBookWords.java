package pl.idedyk.japanese.dictionary.genki;

import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.tools.CsvGenerator;

public class GenkiBookWords {

	private static List<PolishJapaneseEntry> polishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();
	
	public static void main(String[] args) {
		generateWords();
		
		String csvResult = CsvGenerator.generateCsv(polishJapaneseEntries);
		
		System.out.println(csvResult);

	}
	
	private static List<PolishJapaneseEntry> generateWords() {
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		// Lekcja 1
		
		addPolishJapaneseEntry("Ohayoo", "Dzien dobry", "rano");
		addPolishJapaneseEntry("Ohayoo gozaimasu", "Dzien dobry", "rano (grzecznie)");
		addPolishJapaneseEntry("Konnichiwa", "Dzień dobry", "po południu");
		addPolishJapaneseEntry("Konbanwa", "Dobry wieczór", null);
		addPolishJapaneseEntry("Sayoonara", "Do widzenia", null);
		addPolishJapaneseEntry("Oyasuminasai", "Dobranoc", null);
		addPolishJapaneseEntry("Arigatoo", "Dziękuję", null);
		addPolishJapaneseEntry("Arigatoo gozaimasu", "Dziękuję", "grzecznie");
		addPolishJapaneseEntry("Sumimasen", "Przepraszam", null);
		addPolishJapaneseEntry("Iie", "nie", null);
		addPolishJapaneseEntry("Ittekimasu", "Wychodzę", "Idę i wrócę");
		addPolishJapaneseEntry("Itterasshai", "Wróć", "Idź i wróć");
		addPolishJapaneseEntry("Tadaima", "Już jestem", "Po przybyciu do domu");
		addPolishJapaneseEntry("Okaerinasai", "Już jesteś", "Będać w domu");
		addPolishJapaneseEntry("Itadakimasu", "Smacznego", "Przed jedzeniem");
		addPolishJapaneseEntry("Gochisoosama", "Smacznego", "Po jedzeniu");
		addPolishJapaneseEntry("Hajimemashite", "Przywitanie", "Pierwsze spotkanie");
		addPolishJapaneseEntry("Doozo yoroshiku", "Miło Cię spotkać", null);
		addPolishJapaneseEntry("Doozo yoroshiku onegai shimasu", "Miło Cię spotkać", "Kierowane do osoby wyżej postawionej");
		
		addPolishJapaneseEntry("Ima", "teraz", null);
		addPolishJapaneseEntry("han", "połowa", null);
		addPolishJapaneseEntry("ano", "um", null);
		addPolishJapaneseEntry("eigo", "język angielski", null);
		addPolishJapaneseEntry("ee", "tak", "e...");
		addPolishJapaneseEntry("gakusei", "uczeń", null);
		addPolishJapaneseEntry("go", "język", null);
		addPolishJapaneseEntry("kookoo", "liceum", null);
		addPolishJapaneseEntry("gogo", "P.M.", null);
		addPolishJapaneseEntry("gozen", "A.M.", null);
		addPolishJapaneseEntry("sai", "lat", null);
		addPolishJapaneseEntry("san", "Pan/Pani", null);
		addPolishJapaneseEntry("ji", "godzina", null);
		addPolishJapaneseEntry("jin", "człowiek", null);
		addPolishJapaneseEntry("sensei", "nauczyciel", null);
		addPolishJapaneseEntry("senmon", "specjalizacja", null);
		addPolishJapaneseEntry("Soo desu", "To prawda", null);
		addPolishJapaneseEntry("daigaku", "uczelnia", null);
		addPolishJapaneseEntry("denwa", "telefon", null);
		addPolishJapaneseEntry("tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry("namae", "imię", null);
		addPolishJapaneseEntry("nan/nani", "co", null);
		addPolishJapaneseEntry("Nihon", "Japonia", null);
		addPolishJapaneseEntry("nensei", "student roku", null);
		addPolishJapaneseEntry("hai", "tak", null);
		addPolishJapaneseEntry("bangoo", "numer", null);
		addPolishJapaneseEntry("ryuugakusei", "student międzynarodowy", null);
		addPolishJapaneseEntry("watashi", "ja", null);
		addPolishJapaneseEntry("Amerika", "Ameryka", null);
		addPolishJapaneseEntry("Igirisu", "Wielka Brytania", null);
		addPolishJapaneseEntry("Oosutoraria", "Australia", null);
		addPolishJapaneseEntry("Kankoku", "Korea", null);
		addPolishJapaneseEntry("Sueeden", "Szwecja", null);
		addPolishJapaneseEntry("Chuugoku", "Chiny", null);
		
		addPolishJapaneseEntry("kagaku", "nauka", null);
		addPolishJapaneseEntry("ajiakenkyuu", "orientarystyka", null);
		addPolishJapaneseEntry("keizai", "ekonomia", null);
		addPolishJapaneseEntry("kokusaikankei", "stosunki międzynarodowe", null);
		addPolishJapaneseEntry("konpyuutaa", "komputer", null);
		addPolishJapaneseEntry("jinruigaku", "antropotologia", null);
		addPolishJapaneseEntry("seiji", "polityka", null);
		addPolishJapaneseEntry("bijinesu", "biznes", null);
		addPolishJapaneseEntry("bungaku", "literatura", null);
		addPolishJapaneseEntry("rekishi", "historia", null);
		
		addPolishJapaneseEntry("shigoto", "praca", null);
		addPolishJapaneseEntry("isha", "lekarz", null);
		addPolishJapaneseEntry("kaishain", "pracownik biurowy", null);
		addPolishJapaneseEntry("kookoosei", "licealista", null);
		addPolishJapaneseEntry("shufu", "Pani domu", null);
		addPolishJapaneseEntry("daigakuinsei", "absolwent", "studia");
		addPolishJapaneseEntry("daigakusei", "student", null);
		addPolishJapaneseEntry("bengoshi", "prawnik", null);
		
		addPolishJapaneseEntry("okaasan", "matka", null);
		addPolishJapaneseEntry("otoosan", "ojciec", null);
		addPolishJapaneseEntry("oneesan", "siostra", "starsza");
		addPolishJapaneseEntry("oniisan", "brat", "starszy");
		addPolishJapaneseEntry("imooto", "siostra", "młodsza");
		addPolishJapaneseEntry("otooto", "brat", "młodszy");
		
		addPolishJapaneseEntry("zero", "0", "liczba: z...");
		addPolishJapaneseEntry("rei", "0", "liczba: r...");
		addPolishJapaneseEntry("ichi", "1", null);
		addPolishJapaneseEntry("ni", "2", null);
		addPolishJapaneseEntry("san", "3", null);
		addPolishJapaneseEntry("yon", "4", "liczba: y...");
		addPolishJapaneseEntry("shi", "4", "liczba: sh...");
		addPolishJapaneseEntry("yo", "4", "liczba: yo...");
		addPolishJapaneseEntry("go", "5", null);
		addPolishJapaneseEntry("roku", "6", null);
		addPolishJapaneseEntry("nana", "7", "liczba: n...");
		addPolishJapaneseEntry("shichi", "7", "liczba: s...");
		addPolishJapaneseEntry("hachi", "8", null);
		addPolishJapaneseEntry("kyuu", "9", "liczba: ky...");
		addPolishJapaneseEntry("ku", "9", "liczba: ku...");
		addPolishJapaneseEntry("juu", "10", null);
		addPolishJapaneseEntry("hyaku", "100", null);
		addPolishJapaneseEntry("ichiji", "1", "godzina");
		addPolishJapaneseEntry("niji", "2", "godzina");
		addPolishJapaneseEntry("sanji", "3", "godzina");
		addPolishJapaneseEntry("yoji", "4", "godzina");
		addPolishJapaneseEntry("goji", "5", "godzina");
		addPolishJapaneseEntry("rokuji", "6", "godzina");
		addPolishJapaneseEntry("shichiji", "7", "godzina");
		addPolishJapaneseEntry("hachiji", "8", "godzina");
		addPolishJapaneseEntry("kuji", "9", "godzina");
		addPolishJapaneseEntry("juuji", "10", "godzina");
		addPolishJapaneseEntry("juuichiji", "11", "godzina");
		addPolishJapaneseEntry("juuniji", "12", "godzina");
		addPolishJapaneseEntry("ichiji han", "1:30", "godzina");
		
		addPolishJapaneseEntry("ippun", "1", "minuta");
		addPolishJapaneseEntry("nifun", "2", "minuta");
		addPolishJapaneseEntry("sanpun", "3", "minuta");
		addPolishJapaneseEntry("yonpun", "4", "minuta");
		addPolishJapaneseEntry("gofun", "5", "minuta");
		addPolishJapaneseEntry("roppun", "6", "minuta");
		addPolishJapaneseEntry("nanafun", "7", "minuta");
		addPolishJapaneseEntry("happun", "8", "minuta: hap...");
		addPolishJapaneseEntry("hachifun", "8", "minuta: hach...");
		addPolishJapaneseEntry("kyuufun", "9", "minuta");
		addPolishJapaneseEntry("juppun", "10", "minuta");

		addPolishJapaneseEntry("issai", "1", "lata");
		addPolishJapaneseEntry("nisai", "2", "lata");
		addPolishJapaneseEntry("sansai", "3", "lata");
		addPolishJapaneseEntry("yonsai", "4", "lata");
		addPolishJapaneseEntry("gosai", "5", "lata");
		addPolishJapaneseEntry("rokusai", "6", "lata");
		addPolishJapaneseEntry("nanasai", "7", "lata");
		addPolishJapaneseEntry("hassai", "8", "lata");
		addPolishJapaneseEntry("kyuusai", "9", "lata");
		addPolishJapaneseEntry("jussai", "10", "lata");
		addPolishJapaneseEntry("juissai", "11", "lata");
		addPolishJapaneseEntry("hatachi", "20", "lata");

		return result;
	}
	
	private static void addPolishJapaneseEntry(String romaji, String polishTranslateString, String info) {
		
		PolishJapaneseEntry entry = new PolishJapaneseEntry();
		
		List<PolishTranslate> polishTranslateList = new ArrayList<PolishTranslate>();
		
		polishTranslateList.add(createPolishTranslate(polishTranslateString, info));
				
		entry.setRomaji(romaji);
		entry.setPolishTranslates(polishTranslateList);
		
		polishJapaneseEntries.add(entry);
	}
	
	private static PolishTranslate createPolishTranslate(String word, String info) {
		PolishTranslate polishTranslate = new PolishTranslate();
		
		polishTranslate.setWord(word);
		
		if (info != null) {
			List<String> infoList = new ArrayList<String>();
		
			infoList.add(info);
			
			polishTranslate.setInfo(infoList);
		}
		
		return polishTranslate;
	}
}
