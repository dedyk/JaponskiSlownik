package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.tools.CsvGenerator;

public class GenkiBookWords {

	private static Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries = new HashMap<DictionaryEntryType, List<PolishJapaneseEntry>>();
	
	public static void main(String[] args) throws IOException {
		
		generateWords();
		
		CsvGenerator.generateCsv("output", polishJapaneseEntries);
		
		System.out.println("Done");
	}
	
	private static List<PolishJapaneseEntry> generateWords() {
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "zero", "0", "liczba: z...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "rei", "0", "liczba: r...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "ichi", "1", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "ni", "2", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "san", "3", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "yon", "4", "liczba: y...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "shi", "4", "liczba: sh...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "yo", "4", "liczba: yo...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "go", "5", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "roku", "6", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "nana", "7", "liczba: n...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "shichi", "7", "liczba: s...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "hachi", "8", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "kyuu", "9", "liczba: ky...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "ku", "9", "liczba: ku...");
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "juu", "10", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "hyaku", "100", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "ichiji", "1", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "niji", "2", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "sanji", "3", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "yoji", "4", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "goji", "5", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "rokuji", "6", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "shichiji", "7", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "hachiji", "8", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "kuji", "9", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "juuji", "10", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "juuichiji", "11", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "juuniji", "12", "godzina");
		addPolishJapaneseEntry(DictionaryEntryType.HOURS, "ichiji han", "1:30", "godzina");
		
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "ippun", "1", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "nifun", "2", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "sanpun", "3", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "yonpun", "4", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "gofun", "5", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "roppun", "6", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "nanafun", "7", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "happun", "8", "minuta: hap...");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "hachifun", "8", "minuta: hach...");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "kyuufun", "9", "minuta");
		addPolishJapaneseEntry(DictionaryEntryType.MINUTES, "juppun", "10", "minuta");

		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "issai", "1", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "nisai", "2", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "sansai", "3", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "yonsai", "4", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "gosai", "5", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "rokusai", "6", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "nanasai", "7", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "hassai", "8", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "kyuusai", "9", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "jussai", "10", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "juissai", "11", "lata");
		addPolishJapaneseEntry(DictionaryEntryType.YEARS, "hatachi", "20", "lata");
		
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Ohayoo gozaimasu", "Dzien dobry", "rano (grzecznie)");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Konnichiwa", "Dzień dobry", "po południu");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Konbanwa", "Dobry wieczór", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Sayoonara", "Do widzenia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Oyasuminasai", "Dobranoc", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Arigatoo", "Dziękuję", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Arigatoo gozaimasu", "Dziękuję", "grzecznie");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Sumimasen", "Przepraszam", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Iie", "nie", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Ittekimasu", "Wychodzę", "Idę i wrócę");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Itterasshai", "Wróć", "Idź i wróć");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Tadaima", "Już jestem", "Po przybyciu do domu");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Okaerinasai", "Już jesteś", "Będać w domu");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Itadakimasu", "Smacznego", "Przed jedzeniem");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Gochisoosama", "Smacznego", "Po jedzeniu");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Hajimemashite", "Przywitanie", "Pierwsze spotkanie");
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Doozo yoroshiku", "Miło Cię spotkać", null);
		addPolishJapaneseEntry(DictionaryEntryType.GREETINGS, "Doozo yoroshiku onegai shimasu", "Miło Cię spotkać", "Kierowane do osoby wyżej postawionej");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Ima", "teraz", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "han", "połowa", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "ano", "um", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "eigo", "język angielski", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "ee", "tak", "mniej oficjalnie");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "gakusei", "uczeń", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... go", "... język", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "kookoo", "liceum", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "gogo", "P.M.", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "gozen", "A.M.", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... sai", "... lat", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... san", "... Pan/Pani", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... ji", "... godzina", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... jin", "... człowiek", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "sensei", "nauczyciel", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "senmon", "specjalizacja", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Soo desu", "To prawda", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "daigaku", "uczelnia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "denwa", "telefon", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "namae", "imię", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "nan/nani", "co", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Nihon", "Japonia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "... nensei", "... student roku", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "hai", "tak", "oficjalnie");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "bangoo", "numer", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "ryuugakusei", "student międzynarodowy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "watashi", "ja", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Amerika", "Ameryka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Igirisu", "Wielka Brytania", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Oosutoraria", "Australia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Kankoku", "Korea", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Sueeden", "Szwecja", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "Chuugoku", "Chiny", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "kagaku", "nauka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "ajiakenkyuu", "orientarystyka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "keizai", "ekonomia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "kokusaikankei", "stosunki międzynarodowe", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "konpyuutaa", "komputer", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "jinruigaku", "antropotologia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "seiji", "polityka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "bijinesu", "biznes", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "bungaku", "literatura", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "rekishi", "historia", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "shigoto", "praca", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "isha", "lekarz", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "kaishain", "pracownik biurowy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "kookoosei", "licealista", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "shufu", "Pani domu", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "daigakuinsei", "student", "studia magisterskie");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "daigakusei", "student", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "bengoshi", "prawnik", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "okaasan", "matka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "otoosan", "ojciec", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "oneesan", "siostra", "starsza");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "oniisan", "brat", "starszy");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "imooto", "siostra", "młodsza");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_1, "otooto", "brat", "młodszy");
				
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kore", "ten", "blisko mówcy");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "sore", "ten", "blisko osoby docelowej");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "are", "tam", "daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "dore", "który", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kono", "ten", "blisko mówcy (z rzeczownikiem)");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "sono", "ten", "blisko osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "ano", "tam", "daleko od mówcy i osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "dono", "który", "z rzeczownikiem");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "koko", "tam", "miejsce, blisko mówcy");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "soko", "tam", "miejsce, blisko osoby docelowej");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "asoko", "tam", "miejsce, daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "doko", "gdzie", "miejsce");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "dare", "kto", null);

		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "oishii", "smaczne", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "sakana", "ryba", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "tonkatsu", "kotlet wieprzowy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "niku", "mięso", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "menyuu", "menu", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "yasai", "warzywa", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "enpitsu", "ołówek", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kasa", "parasolka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kaban", "torba", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kutsu", "buty", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "saifu", "potfel", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "jiinzu", "jeansy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "jisho", "słownik", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "jitensha", "rower", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "shinbun", "gazeta", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "teepu", "kaseta", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "tokei", "zegarek", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "toreenaa", "podkoszulek", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "nooto", "zeszyt", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "pen", "długopis", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "booshi", "kapelusz", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "hon", "książka", null);

		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "otearai", "toaleta", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "kissaten", "kawiarnia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "ginkoo", "bank", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "toshokan", "biblioteka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "yuubinkyoku", "poczta", null);

		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "ikura", "ile", "koszty");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "... en", "... yen", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "takai", "drogo", "pieniądze");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "takai", "wysoki", "wzrost");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "irasshaimase", "Witamy", "przy wchodzeniu do sklepu");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "... o onegai shimasu", "... proszę", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "... o kudasai", "Proszę dać mi ...", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "jaa", "wtedy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "... o doozo", "proszę", "przy podawaniu komuś czegoś");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "doomo", "dziękuję", "przy otrzymywaniu czegoś");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "desu", "jest", "oficjalnie");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "dewa arimasen", "nie jest", "oficjalnie");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "ja arimasen", "nie jest", "mniej oficjalnie");

		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "sanbyaku", "300", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "roppyaku", "600", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "happyaku", "800", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "sen", "1000", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "sanzen", "3000", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "hassen", "8000", null);
		addPolishJapaneseEntry(DictionaryEntryType.NUMBERS, "ichiman", "10000", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "Wakarimashita", "zrozumiałem", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "Wakarimasen", "nie rozumiem", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "Yukkuri itte kudasai", "Proszę mówić wolno", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "Moo ichido itte kudasai", "Proszę powtórzyć", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2, "Chotto matte kudasai", "Proszę poczekać", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "akai", "czerwony", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "aoi", "niebieski", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "sushi", "sushi", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "kagu", "meble", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "kao", "twarz", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "ai", "miłość", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "kiku", "słuchać", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "sake", "sake", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "ushi", "krowa", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "keitai denwa", "telefon komórkowy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "maakaa", "marker", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "haha", "matka", "h...");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "chichi", "ojciec", "ch...");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "ha", "ząb", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "hi", "słońce", "s...");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "hi", "ogień", "o...");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "mimi", "ucho", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "me", "oko", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "momo", "brzoskwinia", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_2_ADDITIONAL, "yasui", "tani", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "eiga", "film", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "ongaku", "muzyka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "zasshi", "magazyn", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "supootsu", "sport", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "deeto", "randka", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "tenisu", "tenis", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "terebi", "telewizja", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "bideo", "kaseta video", null);

		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "asagohan", "śniadanie", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "osake", "alkohol", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "ocha", "zielona herbata", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "koohii", "kawa", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "bangohan", "kolacja", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "hanbaagaa", "hamburger", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "hirugohan", "obiad", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "mizu", "woda", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "ie", "dom", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "uchi", "dom", "moje miejsce");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "LL", "laboratorium językowe", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "gakkoo", "szkoła", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "asa", "rano", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "ashita", "jutro", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "itsu", "kiedy", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "kyoo", "dziś", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "goro", "około", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "komban", "wieczorem", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "shuumatsu", "weekend", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "doyoobi", "sobota", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "nichiyoobi", "niedziela", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "mainichi", "każdego dnia", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "maiban", "każdej nocy", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "iku", "iść", "u-czasownik");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "kaeru", "wracać", "u-czasownik");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "kiku", "słuchać", "u-czasownik");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "nomu", "pić", "u-czasownik");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "hanasu", "mówić", "u-czasownik");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "yomu", "czytać", "u-czasownik");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "okiru", "wstawać", "ru-czasowniki");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "taberu", "jeść", "ru-czasowniki");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "neru", "spać", "ru-czasowniki");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "miru", "patrzeć", "ru-czasowniki");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "kuru", "przybyć", "czasowniki nieregularne");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "suru", "robić", "czasowniki nieregularne");
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "benkyoo suru", "studiować", "czasowniki nieregularne");
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "ii", "dobry", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "hayai", "wczesny", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "amari", "niewiele", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "zenzen", "wcale", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "taitei", "zwykle", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "chotto", "trochę", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "tokidoki", "czasami", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "yoku", "często, wiele", null);
		
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "Soo desu ne", "To prawda", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "demo", "ale", null);
		addPolishJapaneseEntry(DictionaryEntryType.GENKI1_3, "Doo desu ka", "co ty na to", null);
		
		return result;
	}
	
	private static void addPolishJapaneseEntry(DictionaryEntryType dictionaryEntryType, String romaji, String polishTranslateString, String info) {
		
		// temporary
		dictionaryEntryType = DictionaryEntryType.ALL;
		
		PolishJapaneseEntry entry = new PolishJapaneseEntry();
		
		List<PolishTranslate> polishTranslateList = new ArrayList<PolishTranslate>();
		
		polishTranslateList.add(createPolishTranslate(polishTranslateString, info));
				
		entry.setRomaji(romaji);
		entry.setPolishTranslates(polishTranslateList);
		
		List<PolishJapaneseEntry> dictionaryEntryWordList = polishJapaneseEntries.get(dictionaryEntryType);
		
		if (dictionaryEntryWordList == null) {
			dictionaryEntryWordList = new ArrayList<PolishJapaneseEntry>();
		}
		
		polishJapaneseEntries.put(dictionaryEntryType, dictionaryEntryWordList);
		
		dictionaryEntryWordList.add(entry);
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
