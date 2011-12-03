package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.japannaka.exception.JapannakaException;
import pl.idedyk.japanese.dictionary.tools.CsvGenerator;
import pl.idedyk.japanese.dictionary.tools.KanjiImageWriter;

public class GenkiBookWords {

	public static void main(String[] args) throws IOException, JapannakaException {
		
		// Słowniczek
		Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries = new HashMap<DictionaryEntryType, List<PolishJapaneseEntry>>();
		
		generateWords(polishJapaneseEntries);
		
		CsvGenerator.generateCsv("output", polishJapaneseEntries);
		
		polishJapaneseEntries = null;
		
		// Słowniczek kanji
		Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseKanjiEntries = new HashMap<DictionaryEntryType, List<PolishJapaneseEntry>>();
		String kanjiOutputDir = "kanji_output";
		
		generateKanjiWords(polishJapaneseKanjiEntries);
		generateKanjiImages(polishJapaneseKanjiEntries, kanjiOutputDir);
		
		CsvGenerator.generateCsv(kanjiOutputDir, polishJapaneseKanjiEntries);
		
		System.out.println("Done");
	}

	private static List<PolishJapaneseEntry> generateWords(Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "zero", "0", "liczba: z...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "rei", "0", "liczba: r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ichi", "1", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ni", "2", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "san", "3", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "yon", "4", "liczba: y...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "shi", "4", "liczba: sh...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "yo", "4", "liczba: yo...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "go", "5", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "roku", "6", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "nana", "7", "liczba: n...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "shichi", "7", "liczba: s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "hachi", "8", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "kyuu", "9", "liczba: ky...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ku", "9", "liczba: ku...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "juu", "10", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "hyaku", "100", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "ichiji", "1", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "niji", "2", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "sanji", "3", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "yoji", "4", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "goji", "5", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "rokuji", "6", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "shichiji", "7", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "hachiji", "8", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "kuji", "9", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "juuji", "10", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "juuichiji", "11", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "juuniji", "12", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, "ichiji han", "1:30", "godzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "ippun", "1", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "nifun", "2", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "sanpun", "3", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "yonpun", "4", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "gofun", "5", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "roppun", "6", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "nanafun", "7", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "happun", "8", "minuta: hap...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "hachifun", "8", "minuta: hach...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "kyuufun", "9", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, "juppun", "10", "minuta");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "issai", "1", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "nisai", "2", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "sansai", "3", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "yonsai", "4", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "gosai", "5", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "rokusai", "6", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "nanasai", "7", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "hassai", "8", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "kyuusai", "9", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "jussai", "10", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "juissai", "11", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, "hatachi", "20", "lata");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Ohayoo gozaimasu", "Dzien dobry", "rano (grzecznie)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Konnichiwa", "Dzień dobry", "po południu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Konbanwa", "Dobry wieczór", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Sayoonara", "Do widzenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Oyasuminasai", "Dobranoc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Arigatoo", "Dziękuję", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Arigatoo gozaimasu", "Dziękuję", "grzecznie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Sumimasen", "Przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Iie", "nie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Ittekimasu", "Wychodzę", "Idę i wrócę");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Itterasshai", "Wróć", "Idź i wróć");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Tadaima", "Już jestem", "Po przybyciu do domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Okaerinasai", "Już jesteś", "Będać w domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Itadakimasu", "Smacznego", "Przed jedzeniem");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Gochisoosama", "Smacznego", "Po jedzeniu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Hajimemashite", "Przywitanie", "Pierwsze spotkanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Doozo yoroshiku", "Miło Cię spotkać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Doozo yoroshiku onegai shimasu", "Miło Cię spotkać", "Kierowane do osoby wyżej postawionej");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Ima", "teraz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "han", "połowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ano", "um", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "eigo", "język angielski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ee", "tak", "mniej oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gakusei", "uczeń", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... go", "... język", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "kookoo", "liceum", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gogo", "P.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gozen", "A.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... sai", "... lat", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... san", "... Pan/Pani", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... ji", "... godzina", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... jin", "... człowiek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "sensei", "nauczyciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "senmon", "specjalizacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Soo desu", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigaku", "uczelnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "denwa", "telefon", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "namae", "imię", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "nan/nani", "co", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Nihon", "Japonia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "... nensei", "... student roku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "hai", "tak", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bangoo", "numer", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ryuugakusei", "student międzynarodowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "watashi", "ja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Amerika", "Ameryka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Igirisu", "Wielka Brytania", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Oosutoraria", "Australia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Kankoku", "Korea", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Sueeden", "Szwecja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Chuugoku", "Chiny", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "kagaku", "nauka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ajiakenkyuu", "orientarystyka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "keizai", "ekonomia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "kokusaikankei", "stosunki międzynarodowe", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "konpyuutaa", "komputer", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "jinruigaku", "antropotologia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "seiji", "polityka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bijinesu", "biznes", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bungaku", "literatura", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "rekishi", "historia", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "shigoto", "praca", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "isha", "lekarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "kaishain", "pracownik biurowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "kookoosei", "licealista", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "shufu", "Pani domu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigakuinsei", "student", "studia magisterskie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigakusei", "student", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bengoshi", "prawnik", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "okaasan", "matka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "otoosan", "ojciec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "oneesan", "siostra", "starsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "oniisan", "brat", "starszy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "imooto", "siostra", "młodsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "otooto", "brat", "młodszy");
				
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kore", "ten", "blisko mówcy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "sore", "ten", "blisko osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "are", "tam", "daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "dore", "który", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kono", "ten", "blisko mówcy (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "sono", "ten", "blisko osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ano", "tam", "daleko od mówcy i osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "dono", "który", "z rzeczownikiem");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "koko", "tam", "miejsce, blisko mówcy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "soko", "tam", "miejsce, blisko osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "asoko", "tam", "miejsce, daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "doko", "gdzie", "miejsce");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "dare", "kto", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "oishii", "smaczne", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "sakana", "ryba", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "tonkatsu", "kotlet wieprzowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "niku", "mięso", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "menyuu", "menu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "yasai", "warzywa", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "enpitsu", "ołówek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kasa", "parasolka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kaban", "torba", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kutsu", "buty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "saifu", "potfel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "jiinzu", "jeansy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "jisho", "słownik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "jitensha", "rower", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "shimbun", "gazeta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "teepu", "kaseta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "tokei", "zegarek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "toreenaa", "podkoszulek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "nooto", "zeszyt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "pen", "długopis", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "booshi", "kapelusz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "hon", "książka", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "otearai", "toaleta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kissaten", "kawiarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ginkoo", "bank", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "toshokan", "biblioteka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "yuubinkyoku", "poczta", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ikura", "ile", "koszty");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "... en", "... yen", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "takai", "drogo", "pieniądze");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "takai", "wysoki", "wzrost");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "irasshaimase", "Witamy", "przy wchodzeniu do sklepu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "... o onegai shimasu", "... proszę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "... o kudasai", "Proszę dać mi ...", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "jaa", "wtedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "... o doozo", "proszę", "przy podawaniu komuś czegoś");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "doomo", "dziękuję", "przy otrzymywaniu czegoś");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "desu", "jest", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "dewa arimasen", "nie jest", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ja arimasen", "nie jest", "mniej oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "sanbyaku", "300", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "roppyaku", "600", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "happyaku", "800", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "sen", "1000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "sanzen", "3000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "hassen", "8000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ichiman", "10000", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Wakarimashita", "zrozumiałem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Wakarimasen", "nie rozumiem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Yukkuri itte kudasai", "Proszę mówić wolno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Moo ichido itte kudasai", "Proszę powtórzyć", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Chotto matte kudasai", "Proszę poczekać", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "akai", "czerwony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "aoi", "niebieski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "sushi", "sushi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "kagu", "meble", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "kao", "twarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "ai", "miłość", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "kiku", "słuchać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "sake", "sake", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "ushi", "krowa", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "keitai denwa", "telefon komórkowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "maakaa", "marker", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "haha", "matka", "h...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "chichi", "ojciec", "ch...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "ha", "ząb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "hi", "słońce", "s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "hi", "ogień", "o...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "mimi", "ucho", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "me", "oko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "momo", "brzoskwinia", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2_ADDITIONAL, "yasui", "tani", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "eiga", "film", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ongaku", "muzyka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "zasshi", "magazyn", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "supootsu", "sport", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "deeto", "randka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "tenisu", "tenis", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "terebi", "telewizja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "bideo", "kaseta video", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "asagohan", "śniadanie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "osake", "alkohol", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ocha", "zielona herbata", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "koohii", "kawa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "bangohan", "kolacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "hanbaagaa", "hamburger", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "hirugohan", "obiad", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "mizu", "woda", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ie", "dom", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "uchi", "dom", "moje miejsce");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "LL", "laboratorium językowe", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "gakkoo", "szkoła", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "asa", "rano", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ashita", "jutro", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "itsu", "kiedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kyoo", "dziś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "goro", "około", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "komban", "wieczorem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "shuumatsu", "weekend", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "doyoobi", "sobota", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "nichiyoobi", "niedziela", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "mainichi", "każdego dnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "maiban", "każdej nocy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "iku", "iść", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kaeru", "wracać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kiku", "słuchać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "nomu", "pić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "hanasu", "mówić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "yomu", "czytać", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "okiru", "wstawać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "taberu", "jeść", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "neru", "spać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "miru", "patrzeć", "ru-czasowniki");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kuru", "przybyć", "czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "suru", "robić", "czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "benkyoo suru", "studiować", "czasowniki nieregularne");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ii", "dobry", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "hayai", "wczesny", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "amari", "niewiele", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "zenzen", "wcale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "taitei", "zwykle", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "chotto", "trochę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "tokidoki", "czasami", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "yoku", "często, wiele", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "Soo desu ne", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "demo", "ale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "Doo desu ka", "co ty na to", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "shuu", "tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "matsu", "koniec", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "mai", "każdy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "ohashii", "pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "ofuro", "wanna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "mise", "sklep", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "oshoosan", "mnich", "buddyjski");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "kozoosan", "uczniowie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3_ADDITIONAL, "kanuki", "jenot", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "arubaito", "praca w niepełnym wymiarze godzin", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kaimono", "zakupy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kurasu", "klasa", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "anata", "ty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "inu", "pies", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "omiyage", "piamiątka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kodomo", "dziecko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gohan", "jedzenie", "j...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gohan", "ryż gotowany", "r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "shashin", "rysunek", "r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "shashin", "zdjęcie", "zd...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "tsukue", "stół", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "tegami", "list", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "neko", "kot", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "pan", "chleb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hito", "osoba", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "otera", "świątynia", "buddyjska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "jinja", "świątynia", "shintoistyczna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kooen", "park", "publiczny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "suupaa", "supermarket", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "depaato", "dom handlowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "basutei", "przystanek autobusowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "byooin", "szpital", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hoteru", "hotel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "honya", "księgarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "machi", "miasteczko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "resutoran", "restauracja", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ototoi", "przedwczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kinoo", "wczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kyoo", "dziś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ashita", "jutro", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "asatte", "pojutrze", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sensenshuu", "tydzień przed zeszłym tygodniem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "senshuu", "zeszły tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "konshuu", "obecny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "raishuu", "przyszły tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "saraishuu", "tydzień po przyszłym tygodniu", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ototoshi", "rok przed poprzedni rokiem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kyonen", "poprzedni rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kotoshi", "aktualny rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "rainen", "przyszły rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sarainen", "rok po przyszłym roku", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sakki", "przed chwilą", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "jikan", "godzina", "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ichijikan", "jedna godzina", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "toki", "kiedy", "wskazanie jakiegoś momentu w czasie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "getsuyoobi", "poniedziałek", "księżyc");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kayoobi", "wtorek", "ogień");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "suiyoobi", "środa", "woda");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "mokuyoobi", "czwartek", "drzewo");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kinyoobi", "piątek", "pieniądze, złoto, n'y");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "doyoobi", "sobota", "ziemia");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nichiyoobi", "niedziela", "słońce");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "au", "spotykać / widzieć osobę", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "aru", "być", "do rzeczy martwych, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kau", "kupować", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kaku", "pisać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "toru", "robić", "zdjęcie, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "matsu", "czekać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "wakaru", "rozumieć", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "iru", "być", "do rzeczy żywych, ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gurai", "około", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gomen nasai", "przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "dakara", "ponieważ", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "takusan", "dużo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sukoshi", "mało", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "to", "do łączenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "dooshite", "dlaczego", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hitoride", "sam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "moshimoshi", "hallo", "używane przy odbieraniu telefonu");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "migi", "na prawo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hidari", "na lewo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "mae", "przed", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ushiro", "za", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "naka", "w środku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ue", "na", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "shita", "pod", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "soba", "w pobliżu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "tonari", "obok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "aida", "pomiędzy", null);

		return result;
	}
	
	private static List<PolishJapaneseEntry> generateKanjiWords(Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一", new String[] { "ichi", "i tsu", "hito" }, new String[] { "1" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一時", new String[] { "ichiji" }, new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一年生", new String[] { "ichinensei" }, new String[] { "student pierwszego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一分", new String[] { "ippun" }, new String[] { "jedna minuta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一つ", new String[] { "hitotsu" }, new String[] { "1" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二", new String[] { "ni", "futa" }, new String[] { "2" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二時", new String[] { "niji" }, new String[] { "2" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二年生", new String[] { "ninensei" }, new String[] { "student drugiego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二つ", new String[] { "futatsu" }, new String[] { "2" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二つ日間", new String[] { "futsukakan" }, new String[] { "dwa dni" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三", new String[] { "san", "mi tsu" }, new String[] { "3" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三時", new String[] { "sanji" }, new String[] {"3" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三年生", new String[] { "sannensei" }, new String[] { "student trzeciego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三つ", new String[] { "mittsu" }, new String[] { "3" }, "liczenie" );
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四", new String[] { "yon", "yo", "yo tsu", "shi" }, new String[] { "4" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四時", new String[] { "yoji" }, new String[] { "4" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四年生", new String[] { "yonensei" }, new String[] { "student czwartego roku"} , null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四つ", new String[] { "yottsu" }, new String[] { "4" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四月", new String[] { "shigatsu" }, new String[] { "kwiecień" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五", new String[] { "go", "itsu" }, new String[] { "5" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五時", new String[] { "goji" }, new String[] { "5" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五つ", new String[] { "itsutsu" }, new String[] { "5" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六", new String[] { "roku", "roku tsu", "mu tsu" }, new String[] { "6" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六時", new String[] { "rokuji" }, new String[] { "6" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六分", new String[] { "roppun" }, new String[] { "6" }, "minut");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六つ", new String[] { "muttsu" }, new String[] { "6" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七", new String[] { "shichi", "nana" }, new String[] { "7" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七時", new String[] { "shichiji" }, new String[] { "7" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七つ", new String[] { "nanatsu" }, new String[] { "7" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八", new String[] { "hachi",  "ha tsu", "ya tsu" }, new String[] { "8" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八時", new String[] { "hachiji" }, new String[] { "8" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八歳", new String[] { "hassai" }, new String[] { "8" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八つ", new String[] { "yattsu" }, new String[] { "8" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九", new String[] { "kjuu", "ku", "kokono"}, new String[] { "9" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九時", new String[] { "kuji" }, new String[] { "9" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九歳", new String[] { "kjuusai" }, new String[] { "9" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九つ", new String[] { "kokonotsu" }, new String[] { "9" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十", new String[] { "juu", "ju tsu", "too" }, new String[] { "10" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十時", new String[] { "juuji" }, new String[] { "10" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十歳", new String[] { "jussai" }, new String[] { "10" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十", new String[] { "too" }, new String[] { "10" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百", new String[] { "hyaku", "byaku", "pyaku" }, new String[] { "100" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三百", new String[] { "sanbyaku" }, new String[] { "300" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "千", new String[] { "sen", "zen" }, new String[] { "1000" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三千", new String[] { "sanzen" }, new String[] { "3000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八千", new String[] { "hassen" }, new String[] { "8000" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "万", new String[] { "man" }, new String[] { "10000" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一万", new String[] { "ichiman" }, new String[] { "10000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十万", new String[] { "juuman" }, new String[] { "100000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百万", new String[] { "hyakuman" }, new String[] { "1000000" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "円", new String[] { "en" }, new String[] { "yen", "koło" }, "y...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百円", new String[] { "hyakuen" },  new String[] { "100" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時", new String[] { "ji" }, new String[] { "godzina", "czas" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一時", new String[] { "ichiji" },  new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "子供の時", new String[] { "kodomo no toki" },  new String[] { "w czasach dziecinstwa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時々", new String[] { "tokidoki" },  new String[] { "czasami" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時計", new String[] { "tokei" },  new String[] { "zegarek" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日", new String[] { "ni", "nichi", "bi", "hi", "ni tsu" },  new String[] { "dzień", "słońce" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日曜日", new String[] { "nichiyoobi" },  new String[] { "niedziela" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "毎日", new String[] { "mainichi" },  new String[] { "każdego dnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "母の日", new String[] { "haha no hi" },  new String[] { "dzień matki" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日記", new String[] { "nikki" },  new String[] { "pamiętnik" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三日", new String[] { "mikka" },  new String[] { "3 dni" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "本", new String[] { "hon", "moto" },  new String[] { "książka", "prosto" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "本", new String[] { "hon" },  new String[] { "książka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本語", new String[] { "nihongo" },  new String[] { "język japoński" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "山本さん", new String[] { "Yamamoto san" },  new String[] { "Pan Yamamoto" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "人", new String[] { "jin", "hito", "nin" },  new String[] { "człowiek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本人", new String[] { "nihonjin" },  new String[] { "Japonczyk" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一人で", new String[] { "hitoride" },  new String[] { "samotny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "この人", new String[] { "kono hito" },  new String[] { "ten człowiek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三人", new String[] { "sannin" },  new String[] { "3 osoby" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月", new String[] { "getsu", "gatsu", "tsuki" },  new String[] { "księżyc", "miesiąc" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月曜日", new String[] { "getsuyoobi" },  new String[] { "poniedziałek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一月", new String[] { "ichigatsu" },  new String[] { "styczeń" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月", new String[] { "tsuki" },  new String[] { "księżyc" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火", new String[] { "ka", "hi" },  new String[] { "ogień" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火曜日", new String[] { "kayoobi" },  new String[] { "wtorek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火", new String[] { "hi" },  new String[] { "ogień" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水", new String[] { "sui", "mizu" },  new String[] { "woda" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水曜日", new String[] { "suiyoobi" },  new String[] { "środa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水", new String[] { "mizu" },  new String[] { "woda" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木", new String[] { "moku", "ki" },  new String[] { "drzewo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木曜日", new String[] { "mokuyoobi" },  new String[] { "czwartek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木", new String[] { "ki" },  new String[] { "drzewo" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "金", new String[] { "kin", "kane" },  new String[] { "złoto", "pieniądze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "金曜日", new String[] { "kinyoobi" },  new String[] { "piątek" }, "'");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "お金", new String[] { "okane" },  new String[] { "pieniądze" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土", new String[] { "do", "tsuchi" },  new String[] { "ziemia" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土曜日", new String[] { "doyoobi" },  new String[] { "sobota" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土", new String[] { "tsuchi" },  new String[] { "ziemia" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "曜", new String[] { "yoo" },  new String[] { "dzień powszechni" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日曜日", new String[] { "nichiyoobi" },  new String[] {  "niedziela" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上", new String[] { "ue", "joo" },  new String[] { "na górze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上", new String[] { "ue" },  new String[] { "na górze" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上手な", new String[] { "joozuna" },  new String[] { "dobry" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "屋上", new String[] { "okujoo" },  new String[] { "sufit" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下", new String[] { "shita", "ka" },  new String[] { "na dole" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下", new String[] { "shita" },  new String[] { "pod" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "地下鉄", new String[] { "chikatetsu" },  new String[] { "metro" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下手な", new String[] { "hetana" },  new String[] { "słaby" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中", new String[] { "naka", "chuu", "juu" },  new String[] { "środek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中", new String[] { "naka" },  new String[] { "środek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中国", new String[] { "chuugoku" },  new String[] { "Chiny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中学", new String[] { "chuugaku" },  new String[] { "gimnazjum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一年中", new String[] { "ichinenjuu" },  new String[] { "cały rok" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "半", new String[] { "han" },  new String[] { "połowa" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三時半", new String[] { "sanji han" },  new String[] { "3:30" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "半分", new String[] { "hanbun" },  new String[] { "połowa" }, null);
		
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kanji", "czytanie", "tlumaczenie", null);
		
		return result;
	}
	
	private static void generateKanjiImages(Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries, String imageDir) throws JapannakaException {
		
		Map<String, String> kanjiCache = new HashMap<String, String>();
		
		DictionaryEntryType[] dictionaryTypes = new DictionaryEntryType[polishJapaneseEntries.size()];
		
		polishJapaneseEntries.keySet().toArray(dictionaryTypes);
		
		for (DictionaryEntryType dictionaryEntryType : dictionaryTypes) {
			
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries.get(dictionaryEntryType)) {

				KanjiImageWriter.createKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
				
				
			}
			
		}
		
	}
	
	private static void addPolishJapaneseEntry(Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String romaji, String polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, null, new String[] { romaji }, new String[] { polishTranslateString }, info);
	}
	
	private static void addPolishJapaneseEntry(Map<DictionaryEntryType, List<PolishJapaneseEntry>> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String japanese, String[] romajiArray, String[] polishTranslateList, String info) {
		
		// temporary
		dictionaryEntryType = DictionaryEntryType.ALL;
		
		PolishJapaneseEntry entry = new PolishJapaneseEntry();
		
		List<PolishTranslate> polishTranslateList2 = new ArrayList<PolishTranslate>();
		
		for (String currentPolishTranslate : polishTranslateList) {
			polishTranslateList2.add(createPolishTranslate(currentPolishTranslate, info));
		}
		
		entry.setJapanese(japanese);
		
		List<String> romajiList = new ArrayList<String>();
		for (String romaji : romajiArray) {
			romajiList.add(romaji);
		}
		
		entry.setRomajiList(romajiList);
		entry.setPolishTranslates(polishTranslateList2);
		
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
