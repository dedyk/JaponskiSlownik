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
		addPolishJapaneseEntry("ee", "tak", "mniej oficjalnie");
		addPolishJapaneseEntry("gakusei", "uczeń", null);
		addPolishJapaneseEntry("... go", "... język", null);
		addPolishJapaneseEntry("kookoo", "liceum", null);
		addPolishJapaneseEntry("gogo", "P.M.", null);
		addPolishJapaneseEntry("gozen", "A.M.", null);
		addPolishJapaneseEntry("... sai", "... lat", null);
		addPolishJapaneseEntry("... san", "... Pan/Pani", null);
		addPolishJapaneseEntry("... ji", "... godzina", null);
		addPolishJapaneseEntry("... jin", "... człowiek", null);
		addPolishJapaneseEntry("sensei", "nauczyciel", null);
		addPolishJapaneseEntry("senmon", "specjalizacja", null);
		addPolishJapaneseEntry("Soo desu", "To prawda", null);
		addPolishJapaneseEntry("daigaku", "uczelnia", null);
		addPolishJapaneseEntry("denwa", "telefon", null);
		addPolishJapaneseEntry("tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry("namae", "imię", null);
		addPolishJapaneseEntry("nan/nani", "co", null);
		addPolishJapaneseEntry("Nihon", "Japonia", null);
		addPolishJapaneseEntry("... nensei", "... student roku", null);
		addPolishJapaneseEntry("hai", "tak", "oficjalnie");
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
		addPolishJapaneseEntry("daigakuinsei", "student", "studia magisterskie");
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
		
		addPolishJapaneseEntry("kore", "ten", "blisko mówcy");
		addPolishJapaneseEntry("sore", "ten", "blisko osoby docelowej");
		addPolishJapaneseEntry("are", "tam", "daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry("dore", "który", null);
		addPolishJapaneseEntry("kono", "ten ...", "z rzeczownikiem");
		addPolishJapaneseEntry("sono", "ten ...", "z rzeczownikiem");
		addPolishJapaneseEntry("ano", "tam ...", "z rzeczownikiem");
		addPolishJapaneseEntry("dono", "który ...", "z rzeczownikiem");
		addPolishJapaneseEntry("asoko", "tam", null);
		addPolishJapaneseEntry("doko", "gdzie", null);
		addPolishJapaneseEntry("dare", "kto", null);

		addPolishJapaneseEntry("oishii", "smaczne", null);
		addPolishJapaneseEntry("sakana", "ryba", null);
		addPolishJapaneseEntry("tonkatsu", "wieprzowy kotlet", null);
		addPolishJapaneseEntry("niku", "mięso", null);
		addPolishJapaneseEntry("menyuu", "menu", null);
		addPolishJapaneseEntry("yasai", "warzywa", null);
		
		addPolishJapaneseEntry("enpitsu", "ołówek", null);
		addPolishJapaneseEntry("kasa", "parasolka", null);
		addPolishJapaneseEntry("kaban", "torba", null);
		addPolishJapaneseEntry("kutsu", "buty", null);
		addPolishJapaneseEntry("saifu", "potfel", null);
		addPolishJapaneseEntry("jiinzu", "jeansy", null);
		addPolishJapaneseEntry("jisho", "słownik", null);
		addPolishJapaneseEntry("jitensha", "rower", null);
		addPolishJapaneseEntry("shinbun", "gazeta", null);
		addPolishJapaneseEntry("teepe", "kaseta", null);
		addPolishJapaneseEntry("tokee", "zegarek", null);
		addPolishJapaneseEntry("toreenaa", "koszula", null);
		addPolishJapaneseEntry("nooto", "zeszyt", null);
		addPolishJapaneseEntry("pen", "długopis", null);
		addPolishJapaneseEntry("booshi", "kapelusz", null);
		addPolishJapaneseEntry("hon", "książka", null);

		addPolishJapaneseEntry("otearai", "toaleta", null);
		addPolishJapaneseEntry("kissaten", "kawiarnia", null);
		addPolishJapaneseEntry("ginkoo", "bank", null);
		addPolishJapaneseEntry("toshokan", "biblioteka", null);
		addPolishJapaneseEntry("yuubinkyoku", "poczta", null);

		addPolishJapaneseEntry("ikura", "ile", "koszty");
		addPolishJapaneseEntry("... en", "... yen", null);
		addPolishJapaneseEntry("takai", "drogo", "pieniądze");
		addPolishJapaneseEntry("takai", "wysoki", "wzrost");
		
		addPolishJapaneseEntry("irasshaimase", "Witamy", "przy wchodzeniu do sklepu");
		addPolishJapaneseEntry("... o onegashimasu", "... proszę", null);
		addPolishJapaneseEntry("... o kudasai", "Proszę dać mi ...", null);
		addPolishJapaneseEntry("jaa", "wtedy", null);
		addPolishJapaneseEntry("... o doozo", "proszę", "przy podawaniu komuś czegoś");
		addPolishJapaneseEntry("doomo", "dziękuję", "przy otrzymywaniu czegoś");
		
		addPolishJapaneseEntry("desu", "jest", "oficjalnie");
		addPolishJapaneseEntry("dewa arimasen", "nie jest", "oficjalnie");
		addPolishJapaneseEntry("ja arimasen", "nie jest", "mniej oficjalnie");

		addPolishJapaneseEntry("sanbyaku", "300", null);
		addPolishJapaneseEntry("roppyaku", "600", null);
		addPolishJapaneseEntry("happyaku", "800", null);
		addPolishJapaneseEntry("sen", "1000", null);
		addPolishJapaneseEntry("sanzen", "3000", null);
		addPolishJapaneseEntry("hassen", "8000", null);
		addPolishJapaneseEntry("ichiman", "10000", null);
		
		addPolishJapaneseEntry("Wakarimashita", "zrozumiałem", null);
		addPolishJapaneseEntry("Wakarimasen", "nie rozumiem", null);
		addPolishJapaneseEntry("Yukkuri itte kudasai", "Proszę mówić wolno", null);
		addPolishJapaneseEntry("Moo ichido itte kudasai", "Proszę powtórzyć", null);
		addPolishJapaneseEntry("Chotto matte kudasai", "Proszę poczekać", null);
		
		addPolishJapaneseEntry("akai", "czerwony", null);
		addPolishJapaneseEntry("aoi", "niebieski", null);
		addPolishJapaneseEntry("sushi", "sushi", null);
		addPolishJapaneseEntry("kagu", "meble", null);
		addPolishJapaneseEntry("kao", "twarz", null);
		addPolishJapaneseEntry("ai", "miłość", null);
		addPolishJapaneseEntry("kiku", "słuchać", null);
		addPolishJapaneseEntry("sake", "sake", null);
		addPolishJapaneseEntry("ushi", "krowa", null);
		
		addPolishJapaneseEntry("keutai denwa", "telefon komórkowy", null);
		addPolishJapaneseEntry("maakaa", "marker", null);
		addPolishJapaneseEntry("haha", "matka", "h...");
		addPolishJapaneseEntry("chichi", "ojciec", "ch...");
		addPolishJapaneseEntry("ha", "ząb", null);
		addPolishJapaneseEntry("hi", "słońce", "s...");
		addPolishJapaneseEntry("hi", "ogień", "o...");
		addPolishJapaneseEntry("mimo", "ucho", null);
		addPolishJapaneseEntry("me", "oko", null);
		addPolishJapaneseEntry("momo", "brzoskwinia", null);
		
		addPolishJapaneseEntry("yasui", "tani", null);

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
