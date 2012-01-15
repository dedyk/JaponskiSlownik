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
		List<PolishJapaneseEntry> polishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();
		
		generateWords(polishJapaneseEntries);
		
		CsvGenerator.generateCsv("output/japanese_polish_dictionary.properties", polishJapaneseEntries);
		
		polishJapaneseEntries = null;
		
		// Słowniczek kanji
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = new ArrayList<PolishJapaneseEntry>();
		String kanjiOutputDir = "kanji_output";
		
		generateKanjiWords(polishJapaneseKanjiEntries);
		generateKanjiImages(polishJapaneseKanjiEntries, kanjiOutputDir);
		
		CsvGenerator.generateCsv(kanjiOutputDir + "/kanji_dictionary.properties", polishJapaneseKanjiEntries);
		
		System.out.println("Done");
	}

	private static List<PolishJapaneseEntry> generateWords(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "zero", "0", "liczba: z...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "rei", "0", "liczba: r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ichi", "1", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "ni", "2", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "san", "3", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "yon", "4", "liczba: y**...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "shi", "4", "liczba: sh...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, "yo", "4", "liczba: y*...");
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
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Ohayou gozaimasu", "Dzien dobry", "rano (grzecznie)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Konnichiwa", "Dzień dobry", "po południu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Konbanwa", "Dobry wieczór", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Sayounara", "Do widzenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Oyasuminasai", "Dobranoc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Arigatou", "Dziękuję", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Arigatou gozaimasu", "Dziękuję", "grzecznie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Sumimasen", "Przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Iie", "nie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Ittekimasu", "Wychodzę", "Idę i wrócę");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Itterasshai", "Wróć", "Idź i wróć");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Tadaima", "Już jestem", "Po przybyciu do domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Okaerinasai", "Już jesteś", "Będać w domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Itadakimasu", "Smacznego", "Przed jedzeniem");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Gochisousama", "Smacznego", "Po jedzeniu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Hajimemashite", "Przywitanie", "Pierwsze spotkanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Douzo yoroshiku", "Miło Cię spotkać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, "Douzo yoroshiku onegai shimasu", "Miło Cię spotkać", "Kierowane do osoby wyżej postawionej");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Ima", "teraz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "han", "połowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ano", "um", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "eigo", "język angielski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ee", "tak", "mniej oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gakusei", "uczeń", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "go", "... język", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "koukou", "liceum", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gogo", "P.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "gozen", "A.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "sai", "... lat", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "san", "... Pan", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ji", "... godzina", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "jin", "... człowiek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "sensei", "nauczyciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "senmon", "specjalizacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Sou desu ne", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigaku", "uczelnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "denwa", "telefon", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "namae", "imię", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "nan", "co", "***");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "nani", "co", "****");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "Nihon", "Japonia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "nensei", "... student roku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "hai", "tak", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bangou", "numer", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "ryuugakusei", "uczeń międzynarodowy", null);
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
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "koukousei", "licealista", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "shufu", "Pani domu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigakuinsei", "student", "studia magisterskie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "daigakusei", "student", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "bengoshi", "prawnik", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "okaasan", "matka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "otousan", "ojciec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "oneesan", "siostra", "starsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "oniisan", "brat", "starszy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "imouto", "siostra", "młodsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, "otouto", "brat", "młodszy");
				
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
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "toreenaa", "bluza", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "nooto", "zeszyt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "pen", "długopis", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "boushi", "kapelusz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "hon", "książka", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "otearai", "toaleta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kissaten", "kawiarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ginkou", "bank", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "toshokan", "biblioteka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "yuubinkyoku", "poczta", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ikura", "ile", "koszty");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "en", "... yen", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "takai", "drogo", "pieniądze");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "takai", "wysoki", "wzrost");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "irasshaimase", "Witamy", "przy wchodzeniu do sklepu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "o onegai shimasu", "... proszę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "o kudasai", "Proszę dać mi ...", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "jaa", "wtedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "o douzo", "proszę", "przy podawaniu komuś czegoś");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "doumo", "dziękuję", "przy otrzymywaniu czegoś");
		
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
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Mou ichido itte kudasai", "Proszę powtórzyć", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "Chotto matte kudasai", "Proszę poczekać", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "akai", "czerwony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "aoi", "niebieski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "sushi", "sushi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kagu", "meble", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kao", "twarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ai", "miłość", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "kiku", "słuchać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "sake", "sake", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ushi", "krowa", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "keitai denwa", "telefon komórkowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "maakaa", "marker", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "haha", "matka", "h...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "chichi", "ojciec", "ch...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "ha", "ząb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "hi", "słońce", "s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "hi", "ogień", "o...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "mimi", "ucho", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "me", "oko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "momo", "brzoskwinia", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, "yasui", "tani", null);
		
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
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "gakkou", "szkoła", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "asa", "rano", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ashita", "jutro", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "itsu", "kiedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kyou", "dziś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "goro", "około", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "komban", "wieczorem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "shuumatsu", "weekend", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "doyoubi", "sobota", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "nichiyoubi", "niedziela", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "mainichi", "każdego dnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "maiban", "każdej nocy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, new String[] { "ni iku", "e iku" }, "iść", "cel, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, new String[] { "ni kaeru", "e kaeru" }, "wracać", "cel, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o kiku", "słuchać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o nomu", "pić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, new String[] { "o hanasu", "de hanasu" }, "mówić", "język, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o yomu", "czytać", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "okiru", "wstawać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o taberu", "jeść", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "neru", "spać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o miru", "patrzeć", "ru-czasowniki");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, new String[] { "ni kuru", "e kuru" }, "przybyć", "cel, czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o suru", "robić", "czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "o benkyou suru", "studiować", "czasowniki nieregularne");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ii", "dobry", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "hayai", "wczesny", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "amari", "niewiele", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "zenzen", "wcale", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "taitei", "zwykle", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "chotto", "trochę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "tokidoki", "czasami", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "yoku", "często, wiele", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "Sou desu ne", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "demo", "ale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "Dou desu ka", "co ty na to", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "shuu", "tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "matsu", "koniec", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "mai", "każdy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ohashii", "pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "ofuro", "wanna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "mise", "sklep", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "oshousan", "mnich", "buddyjski");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kozousan", "uczniowie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "kanuki", "jenot", null);
		
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
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kouen", "park", "publiczny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "suupaa", "supermarket", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "depaato", "dom handlowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "basutei", "przystanek autobusowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "byouin", "szpital", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hoteru", "hotel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "honya", "księgarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "machi", "miasteczko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "resutoran", "restauracja", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "tsuitachi", "1", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "futsuka", "2", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "mikka", "3", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "yokka", "4", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "itsuka", "5", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "muika", "6", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nanoka", "7", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "youka", "8", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kokonoka", "9", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "touka", "10", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juuichinichi", "11", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juuninichi", "12", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juusannichi", "13", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juuyokka", "14", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juugonichi", "15", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juurokunichi", "16", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juushichinichi", "17", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juuhachinichi", "18", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "juukunichi", "19", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "hatsuka", "20", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuuichinichi", "21", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuuninichi", "22", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuusannichi", "23", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuuyokka", "24", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuugonichi", "25", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuurokunichi", "26", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuushichinichi", "27", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuuhachinichi", "28", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nijuukunichi", "29", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sanjuunichi", "30", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sanjuuichinichi", "31", "dzień miesiąca");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ichigatsu", "styczeń", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "nigatsu", "luty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "sangatsu", "marzec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "shigatsu", "kwiecień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "gogatsu", "maj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "rokugatsu", "czerwiec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "shichigatsu", "lipiec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "hachigatsu", "sierpień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kugatsu", "wrzesień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "juugatsu", "październik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "juuichigatsu", "listopad", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "juunigatsu", "grudzień", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ototoi", "przedwczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kinou", "wczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kyou", "dziś", null);
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

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "getsuyoubi", "poniedziałek", "księżyc");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kayoubi", "wtorek", "ogień");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "suiyoubi", "środa", "woda");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "mokuyoubi", "czwartek", "drzewo");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kinyoubi", "piątek", "pieniądze, złoto, n'y");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "doyoubi", "sobota", "ziemia");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "nichiyoubi", "niedziela", "słońce");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, new String[] { "ni au", "to au" }, new String[] { "spotykać", "widzieć osobę" }, "osoba, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ga aru", "być", "do rzeczy martwych, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "o kau", "kupować", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "o kaku", "pisać", "osoba ni rzecz, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "o toru", "robić", "zdjęcie, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "o matsu", "czekać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ga wakaru", "rozumieć", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "ga iru", "być", "miejsce ni, do rzeczy żywych, ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gurai", "około", "ogólne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "gomen nasai", "przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "dakara", "ponieważ", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "takusan", "dużo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "sukoshi", "mało", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "to", "do łączenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "doushite", "dlaczego", null);
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
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "otonarisan", "sąsiad", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "umi", "morze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kitte", "znaczek pocztowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kippu", "bilet", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "saafin", "surfing", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "shukudai", "praca domowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tabemono", "jedzenie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tanjoubi", "urodziny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tesuto", "test", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tenki", "pogoda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "nomimono", "picie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "hagaki", "pocztówka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "basu", "autobus", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "hikouki", "samolot", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "heya", "pokój", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "boku", "ja", "używane przez mężczyzn");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "yasumi", new String[] { "wakacje", "odpoczynek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "ryokou", "podróż", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "atarashii", "nowy", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "atsui", "gorąco", "pogoda, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "atsui", "gorąco", "przedmioty, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "isogashii", "zajęty", "człowiek / dzień, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "ookii", "duży", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "omoshiroi", "interesujący", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kowai", "straszny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "samui", "zimno", "pogoda, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tanoshii", "zabawny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "chisai", "mały", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "tsumaranai", "nudny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "furui", "stary", "do przedmiotów, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "muzukashii", "trudny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "yasashii", new String[] { "łatwy, miły" }, "problem / człowiek, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "yasui", "tani", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kirai", "nie lubić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kirei", new String [] { "ładne", "czyste" }, "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "genki", "zdrowy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "shizuka", "cichy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "suki", "lubić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "daikirai", "nienawidzić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "daisuki", "kochać", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "nigiyaka", "żywy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "hansamu", "przystojny", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "hima", "nie zajęty", "na-przymiotnik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "oyogu", "pływać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "ni kiku", "pytać", "osobę, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "ni noru", new String[] { "podróżować", "wsiadać" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "o yaru", "wykonywać", "u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "dekakeru", "wychodzić", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "isshoni", "razem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "sorekara", "i wtedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "daijoubu", "w porządku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "totemo", "bardzo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "donna", "jakiego rodzaju", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "mai", "do liczenia", "płaskich przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "made", "do", "miejsce, czasu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kara", "od", "czasu");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "densha", "pociąg", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "byouki", "chory", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "toshi o totta", "stary", "o człowieku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "wakai", "młody", "o człowieku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kitanai", "brudny", null);
		
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "madoguchi", "okienko pocztowe", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kodzutsumi", "paczka", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "koukuubin", "poczta lotnicza", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "hoken", "ubezpieczenie", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "kakitome", "list polecony", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "earoguramu", "poczta lotnicza", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "fuusho", "list", "bardzo oficjalnie");
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "funabin", "poczta lotnicza", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "sokunatsu", "spejcjalna dostawa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "denchi", "bateria", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "okane", "pieniądze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "obaasan", new String[] { "babcia", "starsza kobieta" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ofuro", "kąpiel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kanji", "kanji", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kyoukasho", "podręcznik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "konshuu", "aktualny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "shiminbyouin", "szpital miejski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "tsugi", "następny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "terebi geemu", "gra telewizyjna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "denki", "prąd", "światło");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "densha", "pociąg", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "nimotsu", "bagaż", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "peeji", "strona", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "mado", "okno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "yoru", "noc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "raishuu", "następny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "rainen", "następny rok", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "taihen", "ciężka", "sytuacja");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "asobu", new String[] { "grać", "miło spędzać czas" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "isogu", "spieszyć się", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ofuro ni hairu", "brać kąpiel", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o kaesu", "zwrócić", "u-czasownik, osoba ni rzecz");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o kesu", new String[] { "wyłączyć", "skasować" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "shinu", "umierać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ni suwaru", "siadać", "u-czasownik, siedzenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "tatsu", "wstawać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "tabako o suu", "palić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o tsukau", "używać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o tetsudau", "pomagać", "u-czasownik, osoba/zadanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ni hairu", "wchodzić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o motsu", new String[] { "nosić", "trzymać" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, new String[] { "o yasumu" }, new String[] { "być nieobecnym"}, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, new String[] { "yasumu" }, new String[] { "odpoczywać" }, "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o akeru", "otwierać", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o oshieru", "uczyć", "ru-czasownik, osoba ni rzecz");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o oriru", "zdejmować", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o kariru", "pożyczać", "osoba ni rzecz");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o shimeru", "zamykać", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o tsukeru", new String[] { "włączać" }, "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "denwa o kakeru", "dzwonić", "ru-czasownik, osoba ni");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o wasureru", "zapomnieć", "ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o tsuretekuru", "przyprowadzać", "czasownik nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o mottekuru", "przynosić", "czasownik nieregularny");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "atode", "później", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "osoku", "zrobić coś później", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kara", "ponieważ", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kekkou desu", new String[] { "To jest w porządku", "To nie będzie potrzebne" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "sugu", "w tej chwili", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "hontou desu ka", "naprawdę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "yakkuri", new String[] { "powoli", "wolno", "bez pośpiechu" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "osoi", "późny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "osoku", "późno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "warui", "zły", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kotoba", new String[] { "słowo", "język", "mowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "michi", "droga", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "gomi", "śmieci", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "o suteru", "wyrzucać", "ru-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "obaasan", "babcia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ojiisan", "dziadek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "obasan", "ciocia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "ojisan", "wujek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "atama ga ii", "mądry", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "atama ga warui", "głupi", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "massugu iku", "iść prosto", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "migi ni mogaru", "skręcić w prawo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "hidari ni magaru", "skręcić w lewo", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "hitotsu me no shingou o migi ni magaru", "skręcić w prawo na pierwszych światłach", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "futatsu me no kadou o hidari ni magaru", "skręcić w lewo na drugim skrzyżowaniu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "michi o wataru", "przez ulicę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "michi no hadari kawa", "lewo strona ulicy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "michi no migi kawa", "prawa strona ulicy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "okiru", "okite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "taberu", "tabete", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "neru", "nete", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "miru", "mite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "iru", "ite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "dekakeru", "dekakete", "forma te, ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "au", "atte", "forma te, u, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kau", "katte", "forma te, u, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kiku", "kiite", "forma te, ku, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kaku", "kaite", "forma te, ku, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "iku", "itte", "forma te, ku (nieregularny), u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "oyogu", "ooyoide", "forma te, gu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "hanasu", "hanashite", "forma te, su, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "matsu", "matte", "forma te, tsu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "nomu", "nonde", "forma te, mu, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "yomu", "yonde", "forma te, mu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kaeru", "kaette", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "aru", "atte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "toru", "totte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "wakaru", "wakatte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "noru", "notte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "yaru", "yatte", "forma te, ru, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "kuru", "kite", "forma te, nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "suru", "shite", "forma te, nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "benkyou suru", "benkyou shite", "forma te, nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "hitori", "jedzen człowiek", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "futari", "dwoje ludzi", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "sannin", "troje ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yonin", "czworo ludzi", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "gonin", "pięcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "rokunin", "sześcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shichinin", "siedmioro ludzi", "s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "nananin", "siedmioro ludzi", "n...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "hachinin", "ośmioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kyuunin", "dziewięcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "juunin", "dziesięcioro ludzi", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kami", "włosy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "mimi", "ucho", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "me", "oko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "hana", "nos", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kuchi", "usta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ha", "ząb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yubi", "palce", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kubi", "szyja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "te", "dłoń", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "atama", "głowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "senaka", "plecy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oshiri", "pośladek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kao", "twarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kata", "ramię", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "mune", "klatka piersiowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "onaka", "brzuch", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ashi", "noga", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otousan", "ojciec", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "okaasan", "matka", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oniisan", "starszy brat", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oneesan", "starsza siostra", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otoutosan", "młodszy brat", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "imootosan", "młodsza siostra", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "goshujin", "mąż", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "okusan", "żona", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ojiisan", "dziadek", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "obaasan", "babcia", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "okosan", "dziecko", "spoza swojej rodziny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otousan", "ojciec", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "okaasan", "matka", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oniisan", "starszy brat", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oneesan", "starsza siostra", "własna rodzina, nieformalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "chichi", "ojciec", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "haha", "matka", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ani", "starszy brat", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ane", "starsza siostra", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otouto", "młodszy brat", "własna rodzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "imouto", "młodsza siostra", "własna rodzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shujin", "mąż", "własna rodzina, formalnie, s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otto", "mąż", "własna rodzina, formalnie, o...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kanai", "żona", "własna rodzina, formalnie, k...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "tsuma", "żona", "własna rodzina, formalnie, t...");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "sofu", "dziadek", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "sobo", "babcia", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ojiisan", "dziadek", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "obaasan", "babcia", "własna rodzina, nieformalnie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "uchi no ko", "dziecko", "własna rodzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ginkouin", "pracownik banku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "in", "... jakaś organizacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ni tsutomete imasu", "pracować dla", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shoukyou", "sytuacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kyoudai", "rodzeństwo", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "apaato", "mieszkanie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "uta", "piosenka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "otoko no hito", "mężczyzna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "onna no hito", "kobieta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kaisha", "firma", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kazoku", "rodzina", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kuni", "kraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kuruma", "samochód", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kombini", "kombini", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shukudou", "stołówka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "Tshetsu", "T-shirt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "megane", "okulary", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "atama ga ii", "mądry", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kakko ii", "dobrze wyglądający", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kawaii", "milutki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "se", "wzrost", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "se ga takai", "wysoki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "se ga hikui", "niski", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "nagai", "długi", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "hayai", "szybki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "mijikai", "krótki", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shinsetsu", "miły", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "benri", "wygodny", "na-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "utau", "śpiewać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kaburu", "zakładać", "u-czasownik, na głowę");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shiru", "wiedzieć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shitte imasu", "wiem", "u-czasownik, forma te");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shirimasen", "nie wiedzieć", "u-czasownik, ~masen");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "sumu", "mieszkać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ni sunde imasu", "mieszkać", "u-czasownik, forma te");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "haku", "zakładać", "u-czasownik, poniżej paska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "futoru", "zyskiwać na wadze", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "futotte imasu", "być grubym", "u-czasownik, forma te");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "megane o kakeru", "zakładać okulary", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kiru", "zakładać", "ru-czasownik, powyżej paska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "tsutomeru", "pracować dla", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ni tsutomete imasu", "pracować dla", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yaseru", "tracić na wadze", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yasete imasu", "być chudym", "ru-czasownik, forma te");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "o kekkon suru", "żenić się", "czasownik nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ga", "ale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "nanimo", "nie ... cofolwiek", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "nin", "ludzi", "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "betsuni", "nie ... w określeniu", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "mochiron", "oczywiście", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yokattara", "jeśli chcesz", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "iu", "mówić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "iu", "wołać", "u-czasownik, z imieniem");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "hashioki", "miejsce na pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "oku", "kłaść", "u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "osara", "talerz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "o watasu", "dawać", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "furosato", "miasto rodzinne", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kaeru", "nosić", "coś na głowie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "futotte imasu", "być grubym", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "yasete imasu", "być chudym", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "se ga takai", "wysoki wzrost", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "se ga hikuri", "niski wzrost", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shiroi", "biały", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kuroi", "czarny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "akai", "czerwony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "aoi", "niebieski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "kiiroi", "żółty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "iro", "kolor", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "pinku iro no", "różowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "ocha iro no", "kolor herbaciany", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "midori iro no", "zielony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "sukaato", "spódnica", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "zubon", "spodnie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "shatsu", "koszula", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "seetaa", "sweter", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "toreenaa", "bluza", null);
		
		return result;
	}
	
	private static List<PolishJapaneseEntry> generateKanjiWords(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		// czytania
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一", new String[] { "ichi", "i tsu", "hito" }, new String[] { "jeden" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二", new String[] { "ni", "futa" }, new String[] { "dwa" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三", new String[] { "san", "mi tsu" }, new String[] { "trzy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四", new String[] { "yon", "yo", "yo tsu", "shi" }, new String[] { "cztery" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五", new String[] { "go", "itsu" }, new String[] { "pięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六", new String[] { "roku", "ro tsu", "mu tsu" }, new String[] { "sześć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七", new String[] { "shichi", "nana" }, new String[] { "siedem" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八", new String[] { "hachi",  "ha tsu", "ya tsu" }, new String[] { "osiem" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九", new String[] { "kjuu", "ku", "kokono"}, new String[] { "dziewięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十", new String[] { "juu", "juu tsu", "too" }, new String[] { "dziesięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百", new String[] { "hyaku", "byaku", "pyaku" }, new String[] { "sto" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "千", new String[] { "sen", "zen" }, new String[] { "tysiąc" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "万", new String[] { "man" }, new String[] { "dziesięć tysięcy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "円", new String[] { "en" }, new String[] { "yen", "koło" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時", new String[] { "ji" }, new String[] { "godzina", "czas" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日", new String[] { "ni", "nichi", "bi", "hi", "ni tsu" },  new String[] { "dzień", "słońce" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "本", new String[] { "hon", "moto" },  new String[] { "książka", "prosto" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "人", new String[] { "jin", "hito", "nin" },  new String[] { "człowiek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月", new String[] { "getsu", "gatsu", "tsuki" },  new String[] { "księżyc", "miesiąc" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火", new String[] { "ka", "hi" },  new String[] { "ogień" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水", new String[] { "sui", "mizu" },  new String[] { "woda" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木", new String[] { "moku", "ki" },  new String[] { "drzewo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "金", new String[] { "kin", "kane" },  new String[] { "złoto", "pieniądze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土", new String[] { "do", "tsuchi" },  new String[] { "ziemia" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "曜", new String[] { "you" },  new String[] { "dzień powszechni" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上", new String[] { "ue", "jou" },  new String[] { "na górze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下", new String[] { "shita", "ka" },  new String[] { "na dole" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中", new String[] { "naka", "chuu", "juu" },  new String[] { "środek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "半", new String[] { "han" },  new String[] { "połowa" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "山", new String[] { "yama", "san" },  new String[] { "góra" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "川", new String[] { "kawa", "gawa" },  new String[] { "rzeka" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "元", new String[] { "gen" }, new String[] { "położenie" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "気", new String[] { "ki" }, new String[] { "duch" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "天", new String[] { "ten" }, new String[] { "niebo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "私", new String[] { "watashi", "shi" }, new String[] { "ja", "prywatne" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "今", new String[] { "ima", "kon" }, new String[] { "teraz" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "田", new String[] { "ta", "da" }, new String[] { "pole ryżowe" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "女", new String[] { "onna", "jo" }, new String[] { "kobieta" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "男", new String[] { "otoko", "dan" }, new String[] { "mężczyzna" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "見", new String[] { "mi", "ken" }, new String[] { "widzieć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "行", new String[] { "i", "kou", "gyou" }, new String[] { "iść" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "食", new String[] { "ta", "shoku" }, new String[] { "jeść" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "飲", new String[] { "no", "in" }, new String[] { "pić" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "東", new String[] { "higashi", "tou" }, new String[] { "wschód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "西", new String[] { "nishi", "sei", "sai" }, new String[] { "zachód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "南", new String[] { "minami", "nan" }, new String[] { "południe" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北", new String[] { "kita", "hoku", "ho tsu" }, new String[] { "północ" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "口", new String[] { "guchi", "kuchi", "kou" }, new String[] { "usta" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "出", new String[] { "de", "da", "shu tsu", "shutsu" }, new String[] { "wychodzić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "右", new String[] { "migi", "u", "yuu" }, new String[] { "prawo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "左", new String[] { "hidari", "sa" }, new String[] { "lewo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "分", new String[] { "fun", "pun", "bun" }, new String[] { "minuta", "dzielić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "先", new String[] { "sen", "saki" }, new String[] { "na przód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "生", new String[] { "sei", "u", "shou" }, new String[] { "urodziny" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大", new String[] { "dai", "oo", "tai"}, new String[] { "duży" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "学", new String[] { "gaku", "ga tsu", "mana" }, new String[] { "uczyć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "外", new String[] { "gai", "soto" }, new String[] { "na zewnątrzn" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "国", new String[] { "koku", "goku", "kuni" }, new String[] { "kraj" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "京", new String[] { "kyou" }, new String[] { "stolica" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "子", new String[] { "ko", "shi" }, new String[] { "dziecko" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "小", new String[] { "chii", "shou" }, new String[] { "mały" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "会", new String[] { "a", "kai" }, new String[] { "spotykać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "社", new String[] { "sha", "ja" }, new String[] { "firma" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "父", new String[] { "chichi", "tou", "fu" }, new String[] { "ojciec" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "母", new String[] { "haha", "kaa", "bo" }, new String[] { "matka" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高", new String[] { "taka", "kou" }, new String[] { "wysoki" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "校", new String[] { "kou" }, new String[] { "szkoła" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "毎", new String[] { "mai" }, new String[] { "w każdy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "語", new String[] { "go" }, new String[] { "słowo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "文", new String[] { "bun" }, new String[] { "zdanie" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "帰", new String[] { "kae", "ki" }, new String[] { "wracać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "入", new String[] { "hai", "iri", "i", "nyuu" }, new String[] { "wchodzić" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一時", new String[] { "ichiji" }, new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一年生", new String[] { "ichinensei" }, new String[] { "student pierwszego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一分", new String[] { "ippun" }, new String[] { "jedna minuta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一つ", new String[] { "hitotsu" }, new String[] { "1" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二時", new String[] { "niji" }, new String[] { "2" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二年生", new String[] { "ninensei" }, new String[] { "student drugiego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二つ", new String[] { "futatsu" }, new String[] { "2" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "二つ日間", new String[] { "futsukakan" }, new String[] { "dwa dni" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三時", new String[] { "sanji" }, new String[] {"3" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三年生", new String[] { "sannensei" }, new String[] { "student trzeciego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三つ", new String[] { "mittsu" }, new String[] { "3" }, "liczenie" );

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四時", new String[] { "yoji" }, new String[] { "4" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四年生", new String[] { "yonensei" }, new String[] { "student czwartego roku"} , null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四つ", new String[] { "yottsu" }, new String[] { "4" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "四月", new String[] { "shigatsu" }, new String[] { "kwiecień" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五時", new String[] { "goji" }, new String[] { "5" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "五つ", new String[] { "itsutsu" }, new String[] { "5" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六時", new String[] { "rokuji" }, new String[] { "6" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六分", new String[] { "roppun" }, new String[] { "6" }, "minut");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六つ", new String[] { "muttsu" }, new String[] { "6" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七時", new String[] { "shichiji" }, new String[] { "7" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "七つ", new String[] { "nanatsu" }, new String[] { "7" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八時", new String[] { "hachiji" }, new String[] { "8" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八歳", new String[] { "hassai" }, new String[] { "8" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八つ", new String[] { "yattsu" }, new String[] { "8" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九時", new String[] { "kuji" }, new String[] { "9" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九歳", new String[] { "kjuusai" }, new String[] { "9" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "九つ", new String[] { "kokonotsu" }, new String[] { "9" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十時", new String[] { "juuji" }, new String[] { "10" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十歳", new String[] { "jussai" }, new String[] { "10" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十", new String[] { "too" }, new String[] { "10" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三百", new String[] { "sanbyaku" }, new String[] { "300" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "三千", new String[] { "sanzen" }, new String[] { "3000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "八千", new String[] { "hassen" }, new String[] { "8000" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一万", new String[] { "ichiman" }, new String[] { "10000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "十万", new String[] { "juuman" }, new String[] { "100000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百万", new String[] { "hyakuman" }, new String[] { "1000000" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "百円", new String[] { "hyakuen" },  new String[] { "100" }, null);		
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "一時", new String[] { "ichiji" },  new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "子供の時", new String[] { "kodomo no toki" },  new String[] { "w czasach dziecinstwa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時々", new String[] { "tokidoki" },  new String[] { "czasami" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, "時計", new String[] { "tokei" },  new String[] { "zegarek" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日曜日", new String[] { "nichiyoubi" },  new String[] { "niedziela" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "毎日", new String[] { "mainichi" },  new String[] { "każdego dnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "母の日", new String[] { "haha no hi" },  new String[] { "dzień matki" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日記", new String[] { "nikki" },  new String[] { "pamiętnik" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三日", new String[] { "mikka" },  new String[] { "3 dni" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "本", new String[] { "hon" },  new String[] { "książka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本語", new String[] { "nihongo" },  new String[] { "język japoński" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "山本さん", new String[] { "Yamamoto san" },  new String[] { "Pan Yamamoto" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日本人", new String[] { "nihonjin" },  new String[] { "Japonczyk" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一人で", new String[] { "hitoride" },  new String[] { "samotny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "この人", new String[] { "kono hito" },  new String[] { "ten człowiek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三人", new String[] { "sannin" },  new String[] { "3 osoby" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月曜日", new String[] { "getsuyoubi" },  new String[] { "poniedziałek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一月", new String[] { "ichigatsu" },  new String[] { "styczeń" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "月", new String[] { "tsuki" },  new String[] { "księżyc" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火曜日", new String[] { "kayoubi" },  new String[] { "wtorek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "火", new String[] { "hi" },  new String[] { "ogień" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水曜日", new String[] { "suiyoubi" },  new String[] { "środa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "水", new String[] { "mizu" },  new String[] { "woda" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木曜日", new String[] { "mokuyoubi" },  new String[] { "czwartek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "木", new String[] { "ki" },  new String[] { "drzewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "金曜日", new String[] { "kinyoubi" },  new String[] { "piątek" }, "'");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "お金", new String[] { "okane" },  new String[] { "pieniądze" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土曜日", new String[] { "doyoubi" },  new String[] { "sobota" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "土", new String[] { "tsuchi" },  new String[] { "ziemia" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "日曜日", new String[] { "nichiyoubi" },  new String[] {  "niedziela" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上", new String[] { "ue" },  new String[] { "na górze" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "上手な", new String[] { "jouzuna" },  new String[] { "dobry w" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "屋上", new String[] { "okujou" },  new String[] { "sufit" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下", new String[] { "shita" },  new String[] { "pod" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "地下鉄", new String[] { "chikatetsu" },  new String[] { "metro" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "下手な", new String[] { "hetana" },  new String[] { "słaby" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中", new String[] { "naka" },  new String[] { "środek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中国", new String[] { "chuugoku" },  new String[] { "Chiny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "中学", new String[] { "chuugaku" },  new String[] { "gimnazjum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "一年中", new String[] { "ichinenjuu" },  new String[] { "cały rok" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "三時半", new String[] { "sanji han" },  new String[] { "3:30" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "半分", new String[] { "hanbun" },  new String[] { "połowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "山", new String[] { "yama" }, new String[] { "góra" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "富士山", new String[] { "Fuji san" }, new String[] { "Góra Fuji" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "川", new String[] { "kawa" }, new String[] { "rzeka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "小川さん", new String[] { "Ogawa san" }, new String[] { "Pan Ogawa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "元気な", new String[] { "genkina" }, new String[] { "zdrowy", "energiczny" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "元気", new String[] { "genki" }, new String[] { "zdrowy", "energiczny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "天気", new String[] { "tenki" }, new String[] { "pogoda" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "電気", new String[] { "denki" }, new String[] { "elektryczność" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "天気", new String[] { "tenki" }, new String[] { "pogoda" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "天国", new String[] { "tengoku" }, new String[] { "niebo" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "私", new String[] { "watashi" }, new String[] { "ja" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "市立大学", new String[] { "shiritsudaigaku" }, new String[] { "prywatny uniwersytet" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "今", new String[] { "ima" }, new String[] { "teraz" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "今日", new String[] { "kyou" }, new String[] { "dzisiaj" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "今晩", new String[] { "komban" }, new String[] { "dziś wieczorem" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "田中さん", new String[] { "Tanaka san" }, new String[] { "Pan Tanaka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "山田さん", new String[] { "Yamada san" }, new String[] { "Pan Yamada" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "田んぼ", new String[] { "tambo" }, new String[] { "pole ryżowe" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "女の人", new String[] { "onna no hito" }, new String[] { "kobieta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "女性", new String[] { "josei" }, new String[] { "kobieta" }, "oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "男の人", new String[] { "otoko no hito" }, new String[] { "mężczyzna" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "男性", new String[] { "dansei" }, new String[] { "mężczyzna" }, "oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "見る", new String[] { "miru" }, new String[] { "patrzeć" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "見物", new String[] { "kenbutsu" }, new String[] { "zwiedzanie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "行く", new String[] { "iku" }, new String[] { "iść" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "銀行", new String[] { "ginkou" }, new String[] { "bank" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "一行目", new String[] { "ichigyoume" }, new String[] { "pierwsza linia" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "食べる", new String[] { "taberu" }, new String[] { "jeść" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "食べ物", new String[] { "tabemono" }, new String[] { "jedzenie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "食堂", new String[] { "shokudou" }, new String[] { "stołówka" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "飲む", new String[] { "nomu" }, new String[] { "pić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "飲み物", new String[] { "nomimono" }, new String[] { "picie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, "飲酒運転", new String[] { "inshuunten" }, new String[] { "pijany kierowca" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "東", new String[] { "higashi" }, new String[] { "wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "東口", new String[] { "higashiguchi" }, new String[] { "wschodnie wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "東京", new String[] { "toukyou" }, new String[] { "Tokio" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "西", new String[] { "nishi" }, new String[] { "zachód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "西口", new String[] { "nishiguchi" }, new String[] { "zachodnie wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "関西", new String[] { "kansai" }, new String[] { "Kansai" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "南", new String[] { "minami" }, new String[] { "południe" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "南口", new String[] { "minamiguchi" }, new String[] { "południowe wyjście" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北", new String[] { "kita" }, new String[] { "północ" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北口", new String[] { "kitaguchi" }, new String[] { "północne wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "東北", new String[] { "tohoku" }, new String[] { "Tohoku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北海道", new String[] { "hokkaidou" }, new String[] { "Hokkaido" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北東", new String[] { "hokutou" }, new String[] { "północny wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "南東", new String[] { "nantou" }, new String[] { "południowy wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "南西", new String[] { "nansei" }, new String[] { "południowy zachód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "北西", new String[] { "hokusei" }, new String[] { "północny zachód" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "口", new String[] { "kuchi" }, new String[] { "usta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "人口", new String[] { "jinkou" }, new String[] { "populacja" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "出る", new String[] { "deru" }, new String[] { "wychodzić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "出口", new String[] { "deguchi" }, new String[] { "wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "出す", new String[] { "dasu" }, new String[] { "wziąć coś" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "出席", new String[] { "shusseki" }, new String[] { "nadzór" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "輸出", new String[] { "yushutsu" }, new String[] { "eksport" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "右", new String[] { "migi" }, new String[] { "prawo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "右折", new String[] { "usetsu" }, new String[] { "skręcić w prawo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "左右", new String[] { "sayuu" }, new String[] { "prawo i lewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "左", new String[] { "hidari" }, new String[] { "lewo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "左折", new String[] { "sasetsu" }, new String[] { "skręcić w lewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "五分", new String[] { "gofun" }, new String[] { "pięć minut" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "十分", new String[] { "juppun" }, new String[] { "dziesięć minut" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "自分", new String[] { "jibun" }, new String[] { "siebie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "半分", new String[] { "hanbun" }, new String[] { "połowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "先生", new String[] { "sensei" }, new String[] { "nauczyciel" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "先週", new String[] { "senshuu" }, new String[] { "zeszły tydzień" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "先に", new String[] { "sakini" }, new String[] { "na przód" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "学生", new String[] { "gakusei" }, new String[] { "uczeń" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "生まれる", new String[] { "umareru" }, new String[] { "rodzić się" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "一層に一度", new String[] { "issou ni ichido" }, new String[] { "raz w życiu" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大学生", new String[] { "daigakusei" }, new String[] { "student" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大きい", new String[] { "ookii" }, new String[] { "duży" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大変", new String[] { "taihen" }, new String[] { "trudna sytuacja" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大人", new String[] { "otona" }, new String[] { "dorosły" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "大学", new String[] { "daigaku" }, new String[] { "uniwersytet" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "学校", new String[] { "gakkou" }, new String[] { "szkoła" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "学ぶ", new String[] { "manabu" }, new String[] { "uczyć" }, "kogoś");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "外国", new String[] { "gaikoku" }, new String[] { "zagraniczny kraj" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "外国人", new String[] { "gaikokujin" }, new String[] { "obcokrajowiec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "外", new String[] { "soto" }, new String[] { "na zewnątrzn" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "中国", new String[] { "chuugoku" }, new String[] { "Chiny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, "国", new String[] { "kuni" }, new String[] { "kraj" }, null);		
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "東京", new String[] { "toukyou" }, new String[] { "Tokio" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "京子", new String[] { "kyouko" }, new String[] { "kyoko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "京都", new String[] { "kyouto" }, new String[] { "Kyoto" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "子ども", new String[] { "kodomo" }, new String[] { "dziecko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "京子", new String[] { "kyouko" }, new String[] { "Kyoko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "女の子", new String[] { "onna no ko" }, new String[] { "dziewczynka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "男の子", new String[] { "otoko no ko" }, new String[] { "chłopczyk" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "電子メール", new String[] { "denshi meeru" }, new String[] { "email" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "小さい", new String[] { "chiisai" }, new String[] { "mały" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "小学校", new String[] { "shougakkou" }, new String[] { "szkoła podstawowa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "小学生", new String[] { "shougakusei" }, new String[] { "uczeń szkoły podstawowej" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "会う", new String[] { "au" }, new String[] { "spotykać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "会社", new String[] { "kaisha" }, new String[] { "firma" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "会社員", new String[] { "kaishain" }, new String[] { "pracownik firmy" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "会社", new String[] { "kaisha" }, new String[] { "firma" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "社会", new String[] { "shakai" }, new String[] { "społeczeństwo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "神社", new String[] { "jinja" }, new String[] { "świątynia" }, "shintoistyczna");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "父", new String[] { "chichi" }, new String[] { "ojciec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "お父さん", new String[] { "otousan" }, new String[] { "ojciec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "父母", new String[] { "fubo" }, new String[] { "ojciec i matka" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "母", new String[] { "haha" }, new String[] { "matka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "お母さん", new String[] { "okaasan" }, new String[] { "matka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "母語", new String[] { "bogo" }, new String[] { "język ojczysty" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高い", new String[] { "takai" }, new String[] { "drogi", "wysoki" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高校", new String[] { "koukou" }, new String[] { "liceum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高校生", new String[] { "koukousei" }, new String[] { "licealista" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "学校", new String[] { "gakkou" }, new String[] { "szkoła" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高校", new String[] { "koukou" }, new String[] { "liceum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "高校生", new String[] { "koukousei" }, new String[] { "licealista" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "中学校", new String[] { "chuugakkou" }, new String[] { "gimnazjum" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "毎日", new String[] { "mainichi" }, new String[] { "każdego dnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "毎週", new String[] { "maishuu" }, new String[] { "każdego tygodnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "毎晩", new String[] { "maiban" }, new String[] { "każdej nocy" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "日本語", new String[] { "nihongo" }, new String[] { "język japoński" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "英語", new String[] { "eigo" }, new String[] { "język angielski" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "文学", new String[] { "bungaku" }, new String[] { "literatura" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "作文", new String[] { "sakubun" }, new String[] { "wypracowanie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "文字", new String[] { "moji" }, new String[] { "list" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "帰る", new String[] { "kaeru" }, new String[] { "wracać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "帰国", new String[] { "kikoku" }, new String[] { "wracać do kraju" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "入る", new String[] { "hairu" }, new String[] { "wchodzić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "入口", new String[] { "iriguchi" }, new String[] { "wejście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "入れる", new String[] { "ireru" }, new String[] { "wkładać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, "輸入", new String[] { "yunyuu" }, new String[] { "import" }, null);

		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, "kanji", "czytanie", "tlumaczenie", null);
		
		return result;
	}
	
	private static void generateKanjiImages(List<PolishJapaneseEntry> polishJapaneseEntries, String imageDir) throws JapannakaException {

		Map<String, String> kanjiCache = new HashMap<String, String>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			KanjiImageWriter.createKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
		}
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String romaji, String polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, null, new String[] { romaji }, new String[] { polishTranslateString }, info);
	}

	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String romaji, String[] polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, null, new String[] { romaji }, polishTranslateString, info);
	}

	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String[] romaji, String polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, null, romaji, new String[] { polishTranslateString }, info);
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String[] romaji, String[] polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, null, romaji, polishTranslateString, info);
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, String japanese, String[] romajiArray, String[] polishTranslateList, String info) {
				
		PolishJapaneseEntry entry = new PolishJapaneseEntry();
		
		entry.setGroupName(dictionaryEntryType.getName());
		
		List<PolishTranslate> polishTranslateList2 = new ArrayList<PolishTranslate>();
		
		for (int idx = 0; idx < polishTranslateList.length; ++idx) {
			String currentPolishTranslate = polishTranslateList[idx];
			
			polishTranslateList2.add(createPolishTranslate(currentPolishTranslate, 
					(idx != polishTranslateList.length - 1 ? null : info)));
		}
		
		entry.setJapanese(japanese);
		
		List<String> romajiList = new ArrayList<String>();
		for (String romaji : romajiArray) {
			romajiList.add(romaji);
		}
		
		entry.setRomajiList(romajiList);
		entry.setPolishTranslates(polishTranslateList2);
		
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
