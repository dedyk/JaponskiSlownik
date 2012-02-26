package pl.idedyk.japanese.dictionary.genki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.dto.PolishTranslate;
import pl.idedyk.japanese.dictionary.dto.RomajiEntry;
import pl.idedyk.japanese.dictionary.japannaka.exception.JapannakaException;
import pl.idedyk.japanese.dictionary.tools.CsvGenerator;
import pl.idedyk.japanese.dictionary.tools.KanjiImageWriter;

public class GenkiBookWords {

	public static void main(String[] args) throws IOException, JapannakaException {

		String kanjiOutputDir = "kanji_output";
		Map<String, String> charsCache = new HashMap<String, String>();
		
		// hiragana
		List<KanaEntry> hiraganaEntries = new ArrayList<KanaEntry>();
		generateHiraganaImages(hiraganaEntries, charsCache, kanjiOutputDir);
		
		// katakana
		List<KanaEntry> katakanaEntries = new ArrayList<KanaEntry>();
		generateKatakanaImages(katakanaEntries, charsCache, kanjiOutputDir);
		
		// Słowniczek
		List<PolishJapaneseEntry> polishJapaneseEntries = new ArrayList<PolishJapaneseEntry>();
		
		// dictionary
		generateWords(polishJapaneseEntries);
		validatePolishJapaneseEntries(polishJapaneseEntries, hiraganaEntries, katakanaEntries);	
		
		// kanji dictionary
		List<PolishJapaneseEntry> polishJapaneseKanjiEntries = new ArrayList<PolishJapaneseEntry>();
		generateKanjiWords(polishJapaneseKanjiEntries);
		generateKanjiImages(polishJapaneseKanjiEntries, charsCache, kanjiOutputDir);
		validatePolishJapaneseEntries(polishJapaneseKanjiEntries, hiraganaEntries, katakanaEntries);
		
		CsvGenerator.generateCsv("output/japanese_polish_dictionary.properties", polishJapaneseEntries);
		CsvGenerator.generateKanaEntriesCsv(kanjiOutputDir + "/hiragana.properties", hiraganaEntries);
		CsvGenerator.generateKanaEntriesCsv(kanjiOutputDir + "/katakana.properties", katakanaEntries);
		CsvGenerator.generateCsv(kanjiOutputDir + "/kanji_dictionary.properties", polishJapaneseKanjiEntries);
		
		System.out.println("Done");
	}

	private static void generateHiraganaImages(List<KanaEntry> hiraganaEntries, Map<String, String> kanjiCache, String kanjiOutputDir) throws JapannakaException {
		
		hiraganaEntries.add(new KanaEntry("あ", "a"));
		hiraganaEntries.add(new KanaEntry("い", "i"));
		hiraganaEntries.add(new KanaEntry("う", "u"));
		hiraganaEntries.add(new KanaEntry("え", "e"));
		hiraganaEntries.add(new KanaEntry("お", "o"));
		
		hiraganaEntries.add(new KanaEntry("か", "ka"));
		hiraganaEntries.add(new KanaEntry("き", "ki"));
		hiraganaEntries.add(new KanaEntry("く", "ku"));
		hiraganaEntries.add(new KanaEntry("け", "ke"));
		hiraganaEntries.add(new KanaEntry("こ", "ko"));
		
		hiraganaEntries.add(new KanaEntry("さ", "sa"));
		hiraganaEntries.add(new KanaEntry("し", "shi"));
		hiraganaEntries.add(new KanaEntry("す", "su"));
		hiraganaEntries.add(new KanaEntry("せ", "se"));
		hiraganaEntries.add(new KanaEntry("そ", "so"));
		
		hiraganaEntries.add(new KanaEntry("た", "ta"));
		hiraganaEntries.add(new KanaEntry("ち", "chi"));
		hiraganaEntries.add(new KanaEntry("つ", "tsu"));
		hiraganaEntries.add(new KanaEntry("て", "te"));
		hiraganaEntries.add(new KanaEntry("と", "to"));
		
		hiraganaEntries.add(new KanaEntry("な", "na"));
		hiraganaEntries.add(new KanaEntry("に", "ni"));
		hiraganaEntries.add(new KanaEntry("ぬ", "nu"));
		hiraganaEntries.add(new KanaEntry("ね", "ne"));
		hiraganaEntries.add(new KanaEntry("の", "no"));
		
		hiraganaEntries.add(new KanaEntry("は", "ha"));
		hiraganaEntries.add(new KanaEntry("ひ", "hi"));
		hiraganaEntries.add(new KanaEntry("ふ", "fu"));
		hiraganaEntries.add(new KanaEntry("へ", "he"));
		hiraganaEntries.add(new KanaEntry("ほ", "ho"));
		
		hiraganaEntries.add(new KanaEntry("ま", "ma"));
		hiraganaEntries.add(new KanaEntry("み", "mi"));
		hiraganaEntries.add(new KanaEntry("む", "mu"));
		hiraganaEntries.add(new KanaEntry("め", "me"));
		hiraganaEntries.add(new KanaEntry("も", "mo"));
		
		hiraganaEntries.add(new KanaEntry("や", "ya"));
		hiraganaEntries.add(new KanaEntry("ゆ", "yu"));
		hiraganaEntries.add(new KanaEntry("よ", "yo"));
		
		hiraganaEntries.add(new KanaEntry("ら", "ra"));
		hiraganaEntries.add(new KanaEntry("り", "ri"));
		hiraganaEntries.add(new KanaEntry("る", "ru"));
		hiraganaEntries.add(new KanaEntry("れ", "re"));
		hiraganaEntries.add(new KanaEntry("ろ", "ro"));
		
		hiraganaEntries.add(new KanaEntry("わ", "wa"));
		hiraganaEntries.add(new KanaEntry("を", "wo"));
		
		hiraganaEntries.add(new KanaEntry("ん", "n"));
		
		hiraganaEntries.add(new KanaEntry("が", "ga"));
		hiraganaEntries.add(new KanaEntry("ぎ", "gi"));
		hiraganaEntries.add(new KanaEntry("ぐ", "gu"));
		hiraganaEntries.add(new KanaEntry("げ", "ge"));
		hiraganaEntries.add(new KanaEntry("ご", "go"));
		
		hiraganaEntries.add(new KanaEntry("ざ", "za"));
		hiraganaEntries.add(new KanaEntry("じ", "ji"));
		hiraganaEntries.add(new KanaEntry("ず", "zu"));
		hiraganaEntries.add(new KanaEntry("ぜ", "ze"));
		hiraganaEntries.add(new KanaEntry("ぞ", "zo"));
		
		hiraganaEntries.add(new KanaEntry("だ", "da"));
		hiraganaEntries.add(new KanaEntry("ぢ", "di"));
		hiraganaEntries.add(new KanaEntry("づ", "du"));
		hiraganaEntries.add(new KanaEntry("で", "de"));
		hiraganaEntries.add(new KanaEntry("ど", "do"));
		
		hiraganaEntries.add(new KanaEntry("ば", "ba"));
		hiraganaEntries.add(new KanaEntry("び", "bi"));
		hiraganaEntries.add(new KanaEntry("ぶ", "bu"));
		hiraganaEntries.add(new KanaEntry("べ", "be"));
		hiraganaEntries.add(new KanaEntry("ぼ", "bo"));
		
		hiraganaEntries.add(new KanaEntry("ぱ", "pa"));
		hiraganaEntries.add(new KanaEntry("ぴ", "pi"));
		hiraganaEntries.add(new KanaEntry("ぷ", "pu"));
		hiraganaEntries.add(new KanaEntry("ぺ", "pe"));
		hiraganaEntries.add(new KanaEntry("ぽ", "po"));
		
		hiraganaEntries.add(new KanaEntry("きゃ", "kya"));
		hiraganaEntries.add(new KanaEntry("きゅ", "kyu"));
		hiraganaEntries.add(new KanaEntry("きょ", "kyo"));
		
		hiraganaEntries.add(new KanaEntry("しゃ", "sha"));
		hiraganaEntries.add(new KanaEntry("しゅ", "shu"));		
		hiraganaEntries.add(new KanaEntry("しょ", "sho"));
		
		hiraganaEntries.add(new KanaEntry("ちゃ", "cha"));
		hiraganaEntries.add(new KanaEntry("ちゅ", "chu"));
		hiraganaEntries.add(new KanaEntry("ちょ", "cho"));
		
		hiraganaEntries.add(new KanaEntry("にゃ", "nya"));
		hiraganaEntries.add(new KanaEntry("にゅ", "nyu"));
		hiraganaEntries.add(new KanaEntry("にょ", "nyo"));
		
		hiraganaEntries.add(new KanaEntry("ひゃ", "hya"));
		hiraganaEntries.add(new KanaEntry("ひゅ", "hyu"));
		hiraganaEntries.add(new KanaEntry("ひょ", "hyo"));
		
		hiraganaEntries.add(new KanaEntry("みゃ", "mya"));
		hiraganaEntries.add(new KanaEntry("みゅ", "myu"));
		hiraganaEntries.add(new KanaEntry("みょ", "myo"));
		
		hiraganaEntries.add(new KanaEntry("りゃ", "rya"));
		hiraganaEntries.add(new KanaEntry("りゅ", "ryu"));
		hiraganaEntries.add(new KanaEntry("りょ", "ryo"));
		
		hiraganaEntries.add(new KanaEntry("ぎゃ", "gya"));
		hiraganaEntries.add(new KanaEntry("ぎゅ", "gyu"));
		hiraganaEntries.add(new KanaEntry("ぎょ", "gyo"));
		
		hiraganaEntries.add(new KanaEntry("じゃ", "ja"));		
		hiraganaEntries.add(new KanaEntry("じゅ", "ju"));
		hiraganaEntries.add(new KanaEntry("じょ", "jo"));
		
		hiraganaEntries.add(new KanaEntry("びゃ", "bya"));
		hiraganaEntries.add(new KanaEntry("びゅ", "byu"));
		hiraganaEntries.add(new KanaEntry("びょ", "byo"));
		
		hiraganaEntries.add(new KanaEntry("ぴゃ", "pya"));
		hiraganaEntries.add(new KanaEntry("ぴゅ", "pyu"));
		hiraganaEntries.add(new KanaEntry("ぴょ", "pyo"));	
		
		hiraganaEntries.add(new KanaEntry("っ", "ttsu"));
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			String image = KanjiImageWriter.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());
			
			kanaEntry.setImage(image);
		}
	}

	private static void generateKatakanaImages(List<KanaEntry> katakanaEntries, Map<String, String> kanjiCache, String kanjiOutputDir) throws JapannakaException {
		
		katakanaEntries.add(new KanaEntry("ア", "a"));
		katakanaEntries.add(new KanaEntry("イ", "i"));
		katakanaEntries.add(new KanaEntry("ウ", "u"));
		katakanaEntries.add(new KanaEntry("エ", "e"));
		katakanaEntries.add(new KanaEntry("オ", "o"));
		
		katakanaEntries.add(new KanaEntry("カ", "ka"));
		katakanaEntries.add(new KanaEntry("キ", "ki"));
		katakanaEntries.add(new KanaEntry("ク", "ku"));
		katakanaEntries.add(new KanaEntry("ケ", "ke"));
		katakanaEntries.add(new KanaEntry("コ", "ko"));
		
		katakanaEntries.add(new KanaEntry("サ", "sa"));
		katakanaEntries.add(new KanaEntry("シ", "shi"));
		katakanaEntries.add(new KanaEntry("ス", "su"));
		katakanaEntries.add(new KanaEntry("せ", "se"));
		katakanaEntries.add(new KanaEntry("ソ", "so"));
		
		katakanaEntries.add(new KanaEntry("タ", "ta"));
		katakanaEntries.add(new KanaEntry("千", "chi"));
		katakanaEntries.add(new KanaEntry("ツ", "tsu"));
		katakanaEntries.add(new KanaEntry("テ", "te"));
		katakanaEntries.add(new KanaEntry("ト", "to"));
		
		katakanaEntries.add(new KanaEntry("ナ", "na"));
		katakanaEntries.add(new KanaEntry("二", "ni"));
		katakanaEntries.add(new KanaEntry("ヌ", "nu"));
		katakanaEntries.add(new KanaEntry("ネ", "ne"));
		katakanaEntries.add(new KanaEntry("ノ", "no"));
		
		katakanaEntries.add(new KanaEntry("ハ", "ha"));
		katakanaEntries.add(new KanaEntry("匕", "hi"));
		katakanaEntries.add(new KanaEntry("フ", "fu"));
		katakanaEntries.add(new KanaEntry("ヘ", "he"));
		katakanaEntries.add(new KanaEntry("ホ", "ho"));
		
		katakanaEntries.add(new KanaEntry("マ", "ma"));
		katakanaEntries.add(new KanaEntry("ミ", "mi"));
		katakanaEntries.add(new KanaEntry("厶", "mu"));
		katakanaEntries.add(new KanaEntry("メ", "me"));
		katakanaEntries.add(new KanaEntry("モ", "mo"));
		
		katakanaEntries.add(new KanaEntry("ヤ", "ya"));
		katakanaEntries.add(new KanaEntry("ユ", "yu"));
		katakanaEntries.add(new KanaEntry("ヨ", "yo"));
		
		katakanaEntries.add(new KanaEntry("ラ", "ra"));
		katakanaEntries.add(new KanaEntry("リ", "ri"));
		katakanaEntries.add(new KanaEntry("ル", "ru"));
		katakanaEntries.add(new KanaEntry("レ", "re"));
		katakanaEntries.add(new KanaEntry("ロ", "ro"));
		
		katakanaEntries.add(new KanaEntry("ワ", "wa"));
		katakanaEntries.add(new KanaEntry("ヲ", "wo"));
		
		katakanaEntries.add(new KanaEntry("ン", "n"));
		
		katakanaEntries.add(new KanaEntry("ガ", "ga"));
		katakanaEntries.add(new KanaEntry("ギ", "gi"));
		katakanaEntries.add(new KanaEntry("グ", "gu"));
		katakanaEntries.add(new KanaEntry("ゲ", "ge"));
		katakanaEntries.add(new KanaEntry("ゴ", "go"));
		
		katakanaEntries.add(new KanaEntry("ザ", "za"));
		katakanaEntries.add(new KanaEntry("ジ", "ji"));
		katakanaEntries.add(new KanaEntry("ズ", "zu"));
		katakanaEntries.add(new KanaEntry("ゼ", "ze"));
		katakanaEntries.add(new KanaEntry("ゾ", "zo"));
		
		katakanaEntries.add(new KanaEntry("ダ", "da"));
		katakanaEntries.add(new KanaEntry("ヂ", "di"));
		katakanaEntries.add(new KanaEntry("づ", "du"));
		katakanaEntries.add(new KanaEntry("デ", "de"));
		katakanaEntries.add(new KanaEntry("ド", "do"));
		
		katakanaEntries.add(new KanaEntry("バ", "ba"));
		katakanaEntries.add(new KanaEntry("ビ", "bi"));
		katakanaEntries.add(new KanaEntry("ブ", "bu"));
		katakanaEntries.add(new KanaEntry("ベ", "be"));
		katakanaEntries.add(new KanaEntry("ボ", "bo"));
		
		katakanaEntries.add(new KanaEntry("パ", "pa"));
		katakanaEntries.add(new KanaEntry("ピ", "pi"));
		katakanaEntries.add(new KanaEntry("プ", "pu"));
		katakanaEntries.add(new KanaEntry("ペ", "pe"));
		katakanaEntries.add(new KanaEntry("ポ", "po"));
		
		katakanaEntries.add(new KanaEntry("キャ", "kya"));
		katakanaEntries.add(new KanaEntry("キュ", "kyu"));
		katakanaEntries.add(new KanaEntry("キョ", "kyo"));
		
		katakanaEntries.add(new KanaEntry("シャ", "sha"));
		katakanaEntries.add(new KanaEntry("シュ", "shu"));		
		katakanaEntries.add(new KanaEntry("ショ", "sho"));
		
		katakanaEntries.add(new KanaEntry("チャ", "cha"));
		katakanaEntries.add(new KanaEntry("チュ", "chu"));
		katakanaEntries.add(new KanaEntry("チョ", "cho"));
		
		katakanaEntries.add(new KanaEntry("ニャ", "nya"));
		katakanaEntries.add(new KanaEntry("ニュ", "nyu"));
		katakanaEntries.add(new KanaEntry("ニョ", "nyo"));
		
		katakanaEntries.add(new KanaEntry("ヒャ", "hya"));
		katakanaEntries.add(new KanaEntry("ヒュ", "hyu"));
		katakanaEntries.add(new KanaEntry("ヒョ", "hyo"));
		
		katakanaEntries.add(new KanaEntry("ミャ", "mya"));
		katakanaEntries.add(new KanaEntry("ミュ", "myu"));
		katakanaEntries.add(new KanaEntry("ミョ", "myo"));
		
		katakanaEntries.add(new KanaEntry("リャ", "rya"));
		katakanaEntries.add(new KanaEntry("リュ", "ryu"));
		katakanaEntries.add(new KanaEntry("リョ", "ryo"));
		
		katakanaEntries.add(new KanaEntry("ギャ", "gya"));
		katakanaEntries.add(new KanaEntry("ギュ", "gyu"));
		katakanaEntries.add(new KanaEntry("ギョ", "gyo"));
		
		katakanaEntries.add(new KanaEntry("ジャ", "ja"));		
		katakanaEntries.add(new KanaEntry("ジュ", "ju"));
		katakanaEntries.add(new KanaEntry("ジョ", "jo"));
		
		katakanaEntries.add(new KanaEntry("ビャ", "bya"));
		katakanaEntries.add(new KanaEntry("ビュ", "byu"));
		katakanaEntries.add(new KanaEntry("ビョ", "byo"));
		
		katakanaEntries.add(new KanaEntry("ピャ", "pya"));
		katakanaEntries.add(new KanaEntry("ピュ", "pyu"));
		katakanaEntries.add(new KanaEntry("ピョ", "pyo"));	
		
		katakanaEntries.add(new KanaEntry("ウィ", "wi"));
		katakanaEntries.add(new KanaEntry("ウェ", "we"));
		
		katakanaEntries.add(new KanaEntry("シェ", "she"));
		katakanaEntries.add(new KanaEntry("ジェ", "je"));
		katakanaEntries.add(new KanaEntry("チェ", "che"));
		
		katakanaEntries.add(new KanaEntry("ファ", "fa"));
		katakanaEntries.add(new KanaEntry("フィ", "fi"));
		katakanaEntries.add(new KanaEntry("フェ", "fe"));
		katakanaEntries.add(new KanaEntry("フォ", "fo"));
		
		katakanaEntries.add(new KanaEntry("ティ", "ti"));
		katakanaEntries.add(new KanaEntry("ディ", "di"));
		katakanaEntries.add(new KanaEntry("ヂュ", "dyu"));
		
		katakanaEntries.add(new KanaEntry("ッ", "ttsu"));
		katakanaEntries.add(new KanaEntry("ー", "ttsu2"));
		
		for (KanaEntry kanaEntry : katakanaEntries) {
			String image = KanjiImageWriter.createNewKanjiImage(kanjiCache, kanjiOutputDir, kanaEntry.getKanaJapanese());
			
			kanaEntry.setImage(image);
		}
	}

	private static List<PolishJapaneseEntry> generateWords(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "zero", "0", "liczba: z...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "rei", "0", "liczba: r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "ichi", "1", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "ni", "2", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "san", "3", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "yon", "4", "liczba: y**...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "shi", "4", "liczba: sh...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "yo", "4", "liczba: y*...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "go", "5", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "roku", "6", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "nana", "7", "liczba: n...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "shichi", "7", "liczba: s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "hachi", "8", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "kyuu", "9", "liczba: ky...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "ku", "9", "liczba: ku...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "juu", "10", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "hyaku", "100", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "ichiji", "1", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "niji", "2", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "sanji", "3", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "yoji", "4", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "goji", "5", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "rokuji", "6", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "shichiji", "7", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "hachiji", "8", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "kuji", "9", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "juuji", "10", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "juuichiji", "11", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "juuniji", "12", "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_HOURS, WordType.HIRAGANA, "ichiji han", "1:30", "godzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "ippun", "1", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "nifun", "2", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "sanpun", "3", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "yonpun", "4", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "gofun", "5", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "roppun", "6", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "nanafun", "7", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "happun", "8", "minuta: hap...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "hachifun", "8", "minuta: hach...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "kyuufun", "9", "minuta");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_MINUTES, WordType.HIRAGANA, "juppun", "10", "minuta");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "issai", "1", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "nisai", "2", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "sansai", "3", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "yonsai", "4", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "gosai", "5", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "rokusai", "6", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "nanasai", "7", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "hassai", "8", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "kyuusai", "9", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "jussai", "10", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "juissai", "11", "lata");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_YEARS, WordType.HIRAGANA, "hatachi", "20", "lata");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Ohayou gozaimasu", "Dzien dobry", "rano (grzecznie)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Konnichiwa", "Dzień dobry", "po południu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Konbanwa", "Dobry wieczór", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Sayounara", "Do widzenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Oyasuminasai", "Dobranoc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Arigatou", "Dziękuję", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Arigatou gozaimasu", "Dziękuję", "grzecznie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Sumimasen", "Przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Iie", "nie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Ittekimasu", "Wychodzę", "Idę i wrócę");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Itterasshai", "Wróć", "Idź i wróć");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Tadaima", "Już jestem", "Po przybyciu do domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Okaerinasai", "Już jesteś", "Będać w domu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Itadakimasu", "Smacznego", "Przed jedzeniem");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Gochisousama", "Smacznego", "Po jedzeniu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Hajimemashite", "Przywitanie", "Pierwsze spotkanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Douzo yoroshiku", "Miło Cię spotkać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GREETINGS, WordType.HIRAGANA, "Douzo yoroshiku onegai shimasu", "Miło Cię spotkać", "Kierowane do osoby wyżej postawionej");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "Ima", "teraz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "han", "połowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "ano", "um", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "eigo", "język angielski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "ee", "tak", "mniej oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "gakusei", "uczeń", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "go", "... język", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "koukou", "liceum", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "gogo", "P.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "gozen", "A.M.", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "sai", "... lat", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "san", "... Pan", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "ji", "... godzina", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "jin", "... człowiek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "sensei", "nauczyciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "senmon", "specjalizacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "Sou desu ne", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "daigaku", "uczelnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "denwa", "telefon", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "tomodachi", "przyjaciel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "namae", "imię", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "nan", "co", "***");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "nani", "co", "****");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "Nihon", "Japonia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "nensei", "... student roku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "hai", "tak", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "bangou", "numer", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "ryuugakusei", "uczeń międzynarodowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "watashi", "ja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "Amerika", "Ameryka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "Igirisu", "Wielka Brytania", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "Oosutoraria", "Australia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "Kankoku", "Korea", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "Sueeden", "Szwecja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "Chuugoku", "Chiny", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "kagaku", "nauka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "ajiakenkyuu", "orientarystyka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "keizai", "ekonomia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "kokusaikankei", "stosunki międzynarodowe", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "konpyuutaa", "komputer", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "jinruigaku", "antropotologia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "seiji", "polityka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.KATAKANA, "bijinesu", "biznes", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "bungaku", "literatura", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "rekishi", "historia", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "shigoto", "praca", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "isha", "lekarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "kaishain", "pracownik biurowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "koukousei", "licealista", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "shufu", "Pani domu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "daigakuinsei", "student", "studia magisterskie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "daigakusei", "student", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "bengoshi", "prawnik", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "okaasan", "matka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "otousan", "ojciec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "oneesan", "siostra", "starsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "oniisan", "brat", "starszy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "imouto", "siostra", "młodsza");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_1, WordType.HIRAGANA, "otouto", "brat", "młodszy");
				
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kore", "ten", "blisko mówcy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "sore", "ten", "blisko osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "are", "tam", "daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "dore", "który", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kono", "ten", "blisko mówcy (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "sono", "ten", "blisko osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ano", "tam", "daleko od mówcy i osoby docelowej (z rzeczownikiem)");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "dono", "który", "z rzeczownikiem");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "koko", "tam", "miejsce, blisko mówcy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "soko", "tam", "miejsce, blisko osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "asoko", "tam", "miejsce, daleko od mówcy i osoby docelowej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "doko", "gdzie", "miejsce");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "dare", "kto", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "oishii", "smaczne", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "sakana", "ryba", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "tonkatsu", "kotlet wieprzowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "niku", "mięso", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "menyuu", "menu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "yasai", "warzywa", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "enpitsu", "ołówek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kasa", "parasolka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kaban", "torba", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kutsu", "buty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "saifu", "potfel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "jiinzu", "jeansy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "jisho", "słownik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "jitensha", "rower", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "shinbun", "gazeta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "teepu", "kaseta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "tokei", "zegarek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "toreenaa", "bluza", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "nooto", "zeszyt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "pen", "długopis", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "boushi", "kapelusz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "hon", "książka", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "otearai", "toaleta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kissaten", "kawiarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ginkou", "bank", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "toshokan", "biblioteka", "w budynku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "yuubinkyoku", "poczta", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ikura", "ile", "koszty");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "en", "... yen", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "takai", "drogo", "pieniądze");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "takai", "wysoki", "wzrost");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "irasshaimase", "Witamy", "przy wchodzeniu do sklepu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "wo onegai shimasu", "... proszę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "wo kudasai", "Proszę dać mi ...", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "jaa", "wtedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "wo douzo", "proszę", "przy podawaniu komuś czegoś");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "doumo", "dziękuję", "przy otrzymywaniu czegoś");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "desu", "jest", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "dewa arimasen", "nie jest", "oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ja arimasen", "nie jest", "mniej oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "sanbyaku", "300", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "roppyaku", "600", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "happyaku", "800", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "sen", "1000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "sanzen", "3000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "hassen", "8000", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_NUMBERS, WordType.HIRAGANA, "ichiman", "10000", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kokuban", "tablica", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "bideo", "video", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kaaten", "zasłonka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "doa", "dzwi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "keshigomu", "gumka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "isu", "krzesło", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "Wakarimashita", "zrozumiałem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "Wakarimasen", "nie rozumiem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "Yukkuri itte kudasai", "Proszę mówić wolno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "Mou ichido itte kudasai", "Proszę powtórzyć", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "Chotto matte kudasai", "Proszę poczekać", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "akai", "czerwony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "aoi", "niebieski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "sushi", "sushi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kagu", "meble", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kao", "twarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ai", "miłość", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "kiku", "słuchać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "sake", "sake", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ushi", "krowa", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "keitai denwa", "telefon komórkowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.KATAKANA, "maakaa", "marker", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "haha", "matka", "h...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "chichi", "ojciec", "ch...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "ha", "ząb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "hi", "słońce", "s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "hi", "ogień", "o...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "mimi", "ucho", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "me", "oko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "momo", "brzoskwinia", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_2, WordType.HIRAGANA, "yasui", "tani", null);
				
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "eiga", "film", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ongaku", "muzyka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "zasshi", "magazyn", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "supootsu", "sport", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "deeto", "randka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "tenisu", "tenis", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "terebi", "telewizja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "bideo", "kaseta video", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "asagohan", "śniadanie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "osake", "alkohol", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ocha", "zielona herbata", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "koohii", "kawa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "bangohan", "kolacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.KATAKANA, "hanbaagaa", "hamburger", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "hirugohan", "obiad", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "mizu", "woda", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ie", "dom", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "uchi", "dom", "moje miejsce");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "erueru", "laboratorium językowe", "ll");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "gakkou", "szkoła", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "asa", "rano", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ashita", "jutro", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "itsu", "kiedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "kyou", "dziś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "goro", "około", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "konban", "wieczorem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "shuumatsu", "weekend", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "doyoubi", "sobota", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "nichiyoubi", "niedziela", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "mainichi", "każdego dnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "maiban", "każdej nocy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, new String[] { "ni iku", "e iku" }, "iść", "cel, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, new String[] { "ni kaeru", "e kaeru" }, "wracać", "cel, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo kiku", "słuchać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo nomu", "pić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, new String[] { "wo hanasu", "de hanasu" }, "mówić", "język, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo yomu", "czytać", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "okiru", "wstawać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo taberu", "jeść", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "neru", "spać", "ru-czasowniki");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo miru", "patrzeć", "ru-czasowniki");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, new String[] { "ni kuru", "e kuru" }, "przybyć", "cel, czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo suru", "robić", "czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "wo benkyou suru", "studiować", "czasowniki nieregularne");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ii", "dobry", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "hayai", "wczesny", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "amari", "niewiele", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "zenzen", "wcale", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "taitei", "zwykle", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "chotto", "trochę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "tokidoki", "czasami", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "yoku", new String[] { "często", "wiele" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "Sou desu ne", "To prawda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "demo", "ale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "Dou desu ka", "co ty na to", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "shuu", "tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "matsu", "koniec", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "mai", "każdy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ohashii", "pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "ofuro", "wanna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "mise", "sklep", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "oshousan", "mnich", "buddyjski");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "kozousan", "uczniowie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "kanuki", "jenot", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "arubaito", "praca w niepełnym wymiarze godzin", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kaimono", "zakupy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "kurasu", "klasa", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "anata", "ty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "inu", "pies", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "omiyage", "piamiątka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kodomo", "dziecko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "gohan", "jedzenie", "j...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "gohan", "ryż gotowany", "r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "shashin", "rysunek", "r...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "shashin", "zdjęcie", "zd...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "tsukue", "stół", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "tegami", "list", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "neko", "kot", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "pan", "chleb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "hito", "osoba", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "otera", "świątynia", "buddyjska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "jinja", "świątynia", "shintoistyczna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kouen", "park", "publiczny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "suupaa", "supermarket", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "depaato", "dom handlowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "basutei", "przystanek autobusowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "byouin", "szpital", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "hoteru", "hotel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "honya", "księgarnia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "machi", "miasteczko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.KATAKANA, "resutoran", "restauracja", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "tsuitachi", "1", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "futsuka", "2", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "mikka", "3", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "yokka", "4", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "itsuka", "5", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "muika", "6", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nanoka", "7", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "youka", "8", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kokonoka", "9", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "touka", "10", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juuichinichi", "11", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juuninichi", "12", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juusannichi", "13", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juuyokka", "14", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juugonichi", "15", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juurokunichi", "16", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juushichinichi", "17", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juuhachinichi", "18", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "juukunichi", "19", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "hatsuka", "20", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuuichinichi", "21", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuuninichi", "22", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuusannichi", "23", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuuyokka", "24", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuugonichi", "25", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuurokunichi", "26", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuushichinichi", "27", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuuhachinichi", "28", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nijuukunichi", "29", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sanjuunichi", "30", "dzień miesiąca");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sanjuuichinichi", "31", "dzień miesiąca");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ichigatsu", "styczeń", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "nigatsu", "luty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "sangatsu", "marzec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "shigatsu", "kwiecień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "gogatsu", "maj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "rokugatsu", "czerwiec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "shichigatsu", "lipiec", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "hachigatsu", "sierpień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kugatsu", "wrzesień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "juugatsu", "październik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "juuichigatsu", "listopad", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "juunigatsu", "grudzień", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ototoi", "przedwczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kinou", "wczoraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kyou", "dziś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ashita", "jutro", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "asatte", "pojutrze", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sensenshuu", "tydzień przed zeszłym tygodniem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "senshuu", "zeszły tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "konshuu", "obecny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "raishuu", "przyszły tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "saraishuu", "tydzień po przyszłym tygodniu", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ototoshi", "rok przed poprzedni rokiem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kyonen", "poprzedni rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kotoshi", "aktualny rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "rainen", "przyszły rok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sarainen", "rok po przyszłym roku", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sakki", "przed chwilą", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "jikan", "godzina", "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ichijikan", "jedna godzina", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "toki", "kiedy", "wskazanie jakiegoś momentu w czasie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "getsuyoubi", "poniedziałek", "księżyc");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kayoubi", "wtorek", "ogień");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "suiyoubi", "środa", "woda");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "mokuyoubi", "czwartek", "drzewo");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kinyoubi", "piątek", "pieniądze, złoto, n'y");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "doyoubi", "sobota", "ziemia");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "nichiyoubi", "niedziela", "słońce");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, new String[] { "ni au", "to au" }, new String[] { "spotykać", "widzieć osobę" }, "osoba, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ga aru", "być", "do rzeczy martwych, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "wo kau", "kupować", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "wo kaku", "pisać", "osoba ni rzecz, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "wo toru", "robić", "zdjęcie, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "wo matsu", "czekać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ga wakaru", "rozumieć", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ga iru", "być", "miejsce ni, do rzeczy żywych, ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "gurai", "około", "ogólne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "gomen nasai", "przepraszam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "dakara", "ponieważ", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "takusan", "dużo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "sukoshi", "mało", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "to", "do łączenia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "doushite", "dlaczego", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "hitoride", "sam", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "moshimoshi", "hallo", "używane przy odbieraniu telefonu");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "migi", "na prawo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "hidari", "na lewo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "mae", "przed", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ushiro", "za", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "naka", "w środku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "ue", "na", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "shita", "pod", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "soba", "w pobliżu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "tonari", "obok", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "aida", "pomiędzy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "otonarisan", "sąsiad", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "umi", "morze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kitte", "znaczek pocztowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kippu", "bilet", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.KATAKANA, "saafin", "surfing", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "shukudai", "praca domowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "tabemono", "jedzenie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "tanjoubi", "urodziny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.KATAKANA, "tesuto", "test", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "tenki", "pogoda", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "nomimono", "picie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "hagaki", "pocztówka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.KATAKANA, "basu", "autobus", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "hikouki", "samolot", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "heya", "pokój", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "boku", "ja", "używane przez mężczyzn");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "yasumi", new String[] { "wakacje", "odpoczynek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "ryokou", "podróż", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "atarashii", "nowy", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "atsui", "gorąco", "pogoda, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "atsui", "gorąco", "przedmioty, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "isogashii", "zajęty", "człowiek / dzień, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "ookii", "duży", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "omoshiroi", "interesujący", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kowai", "straszny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "samui", "zimno", "pogoda, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "tanoshii", "zabawny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "chiisai", "mały", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "tsumaranai", "nudny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "furui", "stary", "do przedmiotów, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "muzukashii", "trudny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "yasashii", new String[] { "łatwy", "miły" }, "problem / człowiek, i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "yasui", "tani", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kirai", "nie lubić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kirei", new String [] { "ładne", "czyste" }, "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "genki", "zdrowy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "shizuka", "cichy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "suki", "lubić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "daikirai", "nienawidzić", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "daisuki", "kochać", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "nigiyaka", "żywy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "hansamu", "przystojny", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "hima", "nie zajęty", "na-przymiotnik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "oyogu", "pływać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "ni kiku", "pytać", "osobę, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "ni noru", new String[] { "podróżować", "wsiadać" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "wo yaru", "wykonywać", "u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "dekakeru", "wychodzić", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "isshoni", "razem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "sorekara", "i wtedy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "daijoubu", "w porządku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "totemo", "bardzo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "donna", "jakiego rodzaju", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "mai", "do liczenia", "płaskich przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "made", "do", "miejsce, czasu");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kara", "od", "czasu");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "densha", "pociąg", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "byouki", "chory", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "toshi wo totta", "stary", "wo człowieku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "wakai", "młody", "wo człowieku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kitanai", "brudny", null);
		
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "madoguchi", "okienko pocztowe", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kodutsumi", "paczka", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "koukuubin", "poczta lotnicza", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "hoken", "ubezpieczenie", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "kakitome", "list polecony", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "earoguramu", "poczta lotnicza", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "fuusho", "list", "bardzo oficjalnie");
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "funabin", "poczta lotnicza", null);
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "sokunatsu", "spejcjalna dostawa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "denchi", "bateria", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "okane", "pieniądze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "obaasan", new String[] { "babcia", "starsza kobieta" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ofuro", "kąpiel", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kanji", "kanji", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kyoukasho", "podręcznik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "konshuu", "aktualny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "shiminbyouin", "szpital miejski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "tsugi", "następny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "terebi geemu", "gra telewizyjna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "denki", "prąd", "światło");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "densha", "pociąg", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "nimotsu", "bagaż", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.KATAKANA, "peeji", "strona", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "mado", "okno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "yoru", "noc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "raishuu", "następny tydzień", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "rainen", "następny rok", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "taihen", "ciężka", "sytuacja");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "asobu", new String[] { "grać", "miło spędzać czas" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "isogu", "spieszyć się", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ofuro ni hairu", "brać kąpiel", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo kaesu", "zwrócić", "u-czasownik, osoba ni rzecz");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo kesu", new String[] { "wyłączyć", "skasować" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "shinu", "umierać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ni suwaru", "siadać", "u-czasownik, siedzenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "tatsu", "wstawać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "tabako wo suu", "palić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo tsukau", "używać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo tetsudau", "pomagać", "u-czasownik, osoba/zadanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ni hairu", "wchodzić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo motsu", new String[] { "nosić", "trzymać" }, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, new String[] { "wo yasumu" }, new String[] { "być nieobecnym"}, "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, new String[] { "yasumu" }, new String[] { "odpoczywać" }, "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo akeru", "otwierać", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo oshieru", "uczyć", "ru-czasownik, osoba ni rzecz");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo oriru", "zdejmować", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo kariru", "pożyczać", "osoba ni rzecz");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo shimeru", "zamykać", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo tsukeru", new String[] { "włączać" }, "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "denwa wo kakeru", "dzwonić", "ru-czasownik, osoba ni");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo wasureru", "zapomnieć", "ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo tsurete kuru", "przyprowadzać", "czasownik nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo motte kuru", "przynosić", "czasownik nieregularny");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "atode", "później", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "osoku", "zrobić coś później", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kara", "ponieważ", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kekkou desu", new String[] { "To jest w porządku", "To nie będzie potrzebne" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "sugu", "w tej chwili", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "hontou", "naprawdę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "yukkuri", new String[] { "powoli", "wolno", "bez pośpiechu" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "osoi", "późny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "osoku", "późno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "warui", "zły", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kotoba", new String[] { "słowo", "język", "mowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "michi", "droga", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "gomi", "śmieci", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wo suteru", "wyrzucać", "ru-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "obaasan", "babcia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ojiisan", "dziadek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "obasan", "ciocia", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "ojisan", "wujek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "atama ga ii", "mądry", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "atama ga warui", "głupi", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "massugu iku", "iść prosto", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "migi ni magaru", "skręcić w prawo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "hidari ni magaru", "skręcić w lewo", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "hitotsu me no shingou wo migi ni magaru", "skręcić w prawo na pierwszych światłach", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "futatsu me no kadou wo hidari ni magaru", "skręcić w lewo na drugim skrzyżowaniu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "michi wo wataru", "przez ulicę", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "michi no hidari kawa", "lewo strona ulicy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "michi no migi kawa", "prawa strona ulicy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "okiru", "okite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "taberu", "tabete", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "neru", "nete", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "miru", "mite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "iru", "ite", "forma te, ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "dekakeru", "dekakete", "forma te, ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "au", "atte", "forma te, u, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kau", "katte", "forma te, u, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kiku", "kiite", "forma te, ku, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kaku", "kaite", "forma te, ku, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "iku", "itte", "forma te, ku (nieregularny), u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "oyogu", "oyoide", "forma te, gu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "hanasu", "hanashite", "forma te, su, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "matsu", "matte", "forma te, tsu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "nomu", "nonde", "forma te, mu, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "yomu", "yonde", "forma te, mu, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kaeru", "kaette", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "aru", "atte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "toru", "totte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "wakaru", "wakatte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "noru", "notte", "forma te, ru, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "yaru", "yatte", "forma te, ru, u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "kuru", "kite", "forma te, nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "suru", "shite", "forma te, nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "benkyou suru", "benkyou shite", "forma te, nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "hitori", "jedzen człowiek", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "futari", "dwoje ludzi", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "sannin", "troje ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "yonin", "czworo ludzi", "*");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "gonin", "pięcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "rokunin", "sześcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shichinin", "siedmioro ludzi", "s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "nananin", "siedmioro ludzi", "n...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "hachinin", "ośmioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kyuunin", "dziewięcioro ludzi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "juunin", "dziesięcioro ludzi", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kami", "włosy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "mimi", "ucho", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "me", "oko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "hana", "nos", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kuchi", "usta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ha", "ząb", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "yubi", "palce", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kubi", "szyja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "te", "dłoń", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "atama", "głowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "senaka", "plecy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oshiri", "pośladek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kao", "twarz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kata", "ramię", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "mune", "klatka piersiowa", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "onaka", "brzuch", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ashi", "noga", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otousan", "ojciec", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "okaasan", "matka", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oniisan", "starszy brat", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oneesan", "starsza siostra", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otoutosan", "młodszy brat", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "imootosan", "młodsza siostra", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "goshujin", "mąż", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "okusan", "żona", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ojiisan", "dziadek", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "obaasan", "babcia", "spoza swojej rodziny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "okosan", "dziecko", "spoza swojej rodziny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otousan", "ojciec", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "okaasan", "matka", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oniisan", "starszy brat", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oneesan", "starsza siostra", "własna rodzina, nieformalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "chichi", "ojciec", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "haha", "matka", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ani", "starszy brat", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ane", "starsza siostra", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otouto", "młodszy brat", "własna rodzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "imouto", "młodsza siostra", "własna rodzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shujin", "mąż", "własna rodzina, formalnie, s...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otto", "mąż", "własna rodzina, formalnie, o...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kanai", "żona", "własna rodzina, formalnie, k...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "tsuma", "żona", "własna rodzina, formalnie, t...");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "sofu", "dziadek", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "sobo", "babcia", "własna rodzina, formalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ojiisan", "dziadek", "własna rodzina, nieformalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "obaasan", "babcia", "własna rodzina, nieformalnie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "uchi no ko", "dziecko", "własna rodzina");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ginkouin", "pracownik banku", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "in", "... jakaś organizacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shoukyou", "sytuacja", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kyoudai", "rodzeństwo", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.KATAKANA, "apaato", "mieszkanie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "uta", "piosenka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "otoko no hito", "mężczyzna", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "onna no hito", "kobieta", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kaisha", "firma", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kazoku", "rodzina", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kuni", "kraj", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kuruma", "samochód", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "konbini", "kombini", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shukudou", "stołówka", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.KATAKANA, "Tishetsu", "T-shirt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "megane", "okulary", null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "atama ga ii", "mądry", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kakko ii", "dobrze wyglądający", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kawaii", "milutki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "se", "wzrost", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "se ga takai", "wysoki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "se ga hikui", "niski", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "nagai", "długi", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "hayai", "szybki", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "mijikai", "krótki", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shinsetsu", "miły", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "benri", "przydatny", "na-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "utau", "śpiewać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kaburu", "zakładać", "u-czasownik, na głowę");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shiru", "wiedzieć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shitte imasu", "wiem", "u-czasownik, forma te");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shirimasen", "nie wiedzieć", "u-czasownik, ~masen");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ni sumu", "mieszkać", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ni sunde imasu", "mieszkać", "u-czasownik, forma te");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "haku", "zakładać", "u-czasownik, poniżej paska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "futoru", "zyskiwać na wadze", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "futotte imasu", "być grubym", "u-czasownik, forma te");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "megane wo kakeru", "zakładać okulary", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kiru", "zakładać", "ru-czasownik, powyżej paska");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ni tsutomeru", "pracować dla", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ni tsutomete imasu", "pracować dla", "forma te");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "yaseru", "tracić na wadze", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "yasete imasu", "być chudym", "forma te");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "wo kekkon suru", "żenić się", "czasownik nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ga", "ale", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "nanimo", "nic", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "nin", "ludzi", "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "betsuni", "nieszczególnie", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "mochiron", "oczywiście", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "yokattara", "jeśli chcesz", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "iu", "mówić", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "iu", "wołać", "u-czasownik, z imieniem");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "hashioki", "miejsce na pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "oku", "kłaść", "u-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "osara", "talerz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "wo watasu", "dawać", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "furosato", "miasto rodzinne", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shiroi", "biały", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kuroi", "czarny", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "akai", "czerwony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "aoi", "niebieski", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "kiiroi", "żółty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "iro", "kolor", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "pinku iro no", "różowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "ocha iro no", "kolor herbaciany", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "midori", "zielony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.KATAKANA, "sukaato", "spódnica", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "zubon", "spodnie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "shatsu", "koszula", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.KATAKANA, "seetaa", "sweter", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.KATAKANA, "toreenaa", "bluza", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "asatte", "po jutrze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ame", "deszcz", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kaishain", "pracownik biurowy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "kamera", "kamera", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "karaoke", "karaoke", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kuuki", "powietrze", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kesa", "dzisiejszy poranek", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kongetsu", "aktualny miesiąc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "shigoto", "praca", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "daigakusei", "student", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "disuko", "disco", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "tenkiyohou", "prognoza pogody", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "tokoro", "miejsce", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "tomato", "pomidor", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "natsu", "lato", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "nani ka", "coś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "paatii", "party", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "baabekyuu", "grill", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "hashi", "pałeczki", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "fuya", "zima", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.KATAKANA, "hoomesutei", new String[] { "pozostać w domu", "żyć ze swoją lokalną rodziną" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "maishuu", "w każdym tygodniu", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "raigetsu", "następny miesiąć", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ga jouzu", "dobry w", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ga heta", "słaby w", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "yuumei", "sławny", "na-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ame ga furu", "pada deszcz", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "arau", "myć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "iu", "mówić", "u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ga iru", "potrzebować", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ni osokunaru", "być spóźniony", "na, u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "omou", "myśleć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kiru", "ciąć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "tsukuru", "tworzyć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "motte iku", "brać", "coś, u-czasownik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "wo jirojiromiru", "spojrzeć", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "hajimeru", "zaczynać", "ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "wo unten suru", "prowadzić", "czasowniki nieregularne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "sentaku suru", "robić pranie", "czasownik nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "souji suru", "czyścić", "czasownik nieregularny");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "ryouri suru", "gotować", "czasownik nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "uun", "nie", "u...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "un", "tak", "u...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kanpai", "na zdrowie", "toast");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "zannen desu ne", "trudno", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "mada", "jeszcze nie", "+ forma negatywna");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "minna", "wszyscy", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "nanika", "coś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "dareka", "ktoś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "doreka", "któryś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "dokoka", "gdzieś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "nanimo", "nic", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "daremo", "nikt", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "doremo", "żaden", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "dokomo", "nigdzie", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "nitsuite", "o czymś", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "tsukarete iru", "być zmęczony", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "mazu", "pierwszy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "kotaeru", "odpowiadać", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "tsugini", "drugi", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "saigoni", "ostatni", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "iiko", "dobre dziecko", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "iro", "kolor", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "obentou", "pudełko na jedzenie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "onsen", "gorące źródło", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kabuki", "Kabuki", "tradycyjny Japoński teatr");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "gitaa", "gitara", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kusuri", "lekarstwo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kusuri wo nomu", "brać lekarstwo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "konseeto", "koncert", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kondo", "następnym razem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "sakubun", "wypracowanie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "shiken", "egzamin", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "shinkansen", "Shinkansen", "szybka kolej");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "sukii", "narty", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "sengetsu", "poprzedni miesiąc", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "tango", "słownictwo", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "piano", "pianino", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "byouki", "choroba", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "aoi", "niebeski", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "akai", "czerwony", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kuroi", "czarny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "sabishii", "samotny", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "shiroi", "biały", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "wakai", "młody", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "ijiwaru", "złośliwy", "na-przymiotnik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "odoru", "tańczyć", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "ga owaru", "kończyć", "u-czasownik, coś");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "ninki ga aru", "być popularny", "u-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "ga hajimaru", "rozpoczynać", "u-czasownik, coś");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "hiku", "grać", "u-czasownik, instrument klawiszowy");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "tataku", "grać", "u-czasownik, na bębnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "fuku", "grać", "u-czasownik, instrument dmuchany");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "wo morau", "dostać", "u-czasownik, coś, osoba ni rzecz");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "oboeru", "zapamiętać", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "ni deru", "pojawić się", "ru-czasownik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "wo deru", "wychodzić", "ru-czasownik");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "undou suru", "ćwiczenia", "czasownik nieregularny, fizyczne");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "sanpo suru", "spacerować", "czasownik nieregularny");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kara", "od", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "zehi", "koniecznie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "tokorode", "przy okazji", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "minna", "wszyscy", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "mou", "już", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "mada", "jeszcze", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "hitotsu", "jeden", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "futatsu", "dwa", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "mittsu", "trzy", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "yottsu", "cztery", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "itsutsu", "pięć", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "muttsu", "sześć", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "nanatsu", "siedem", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "yattsu", "osiem", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kokonotsu", "dziewięć", "liczby, liczenie małych przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "too", "dziesięć", "liczby, liczenie małych przedmiotów");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "soshite", "następnie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "sorekara", "no i", null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "toshokan", "biblioteka", "w budynku");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "toshoshitsu", "biblioteka", "w sali");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kiiroi", "żółty", "i-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "chairoi", "brązowy", "i-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "midori", "zielony", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "haiiro", "szary", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "pinku", "różowy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "gin'iro", "srebny", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "murasaki", "fioletowy", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "mizuiro", "jasno niebieski", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "kin'iro", "złoty", "na-przymiotnik");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "nikki", "pamiętnik", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "gozenchuu", "przed południem", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.KATAKANA, "hosutofamirii", "rodzina goszczaca", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "gochisou", "dobre jedzenie", null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "iroiro", "różne", "na-przymiotnik");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "hanashi wo suru", "rozmawiać", null);		
		
		return result;
	}
	
	private static List<PolishJapaneseEntry> generateKanjiWords(List<PolishJapaneseEntry> polishJapaneseEntries) {
		
		List<PolishJapaneseEntry> result = new ArrayList<PolishJapaneseEntry>();
		
		// czytania
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一", new String[] { "ichi", "i tsu", "hito" }, new String[] { "jeden" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "二", new String[] { "ni", "futa" }, new String[] { "dwa" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三", new String[] { "san", "mi tsu" }, new String[] { "trzy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "四", new String[] { "yon", "yo", "yo tsu", "shi" }, new String[] { "cztery" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "五", new String[] { "go", "itsu" }, new String[] { "pięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六", new String[] { "roku", "ro tsu", "mu tsu" }, new String[] { "sześć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "七", new String[] { "shichi", "nana" }, new String[] { "siedem" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八", new String[] { "hachi",  "ha tsu", "ya tsu" }, new String[] { "osiem" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "九", new String[] { "kyuu", "ku", "kokono"}, new String[] { "dziewięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "十", new String[] { "juu", "juu tsu", "too" }, new String[] { "dziesięć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "百", new String[] { "hyaku", "byaku", "pyaku" }, new String[] { "sto" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "千", new String[] { "sen", "zen" }, new String[] { "tysiąc" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "万", new String[] { "man" }, new String[] { "dziesięć tysięcy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "円", new String[] { "en" }, new String[] { "yen", "koło" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "時", new String[] { "ji" }, new String[] { "godzina", "czas" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日", new String[] { "ni", "nichi", "bi", "hi", "ni tsu" },  new String[] { "dzień", "słońce" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "本", new String[] { "hon", "moto" },  new String[] { "książka", "prosto" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "人", new String[] { "jin", "hito", "nin" },  new String[] { "człowiek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "月", new String[] { "getsu", "gatsu", "tsuki" },  new String[] { "księżyc", "miesiąc" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "火", new String[] { "ka", "hi" },  new String[] { "ogień" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "水", new String[] { "sui", "mizu" },  new String[] { "woda" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "木", new String[] { "moku", "ki" },  new String[] { "drzewo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "金", new String[] { "kin", "kane" },  new String[] { "złoto", "pieniądze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "土", new String[] { "do", "tsuchi" },  new String[] { "ziemia" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "曜", new String[] { "you" },  new String[] { "dzień powszechni" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "上", new String[] { "ue", "jou" },  new String[] { "na górze" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "下", new String[] { "shita", "ka" },  new String[] { "na dole" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "中", new String[] { "naka", "chuu", "juu" },  new String[] { "środek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "半", new String[] { "han" },  new String[] { "połowa" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "山", new String[] { "yama", "san" },  new String[] { "góra" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "川", new String[] { "kawa", "gawa" },  new String[] { "rzeka" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "元", new String[] { "gen" }, new String[] { "położenie" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "気", new String[] { "ki" }, new String[] { "duch" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "天", new String[] { "ten" }, new String[] { "niebo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "私", new String[] { "watashi", "shi" }, new String[] { "ja", "prywatne" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "今", new String[] { "ima", "kon" }, new String[] { "teraz" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "田", new String[] { "ta", "da" }, new String[] { "pole ryżowe" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "女", new String[] { "onna", "jo" }, new String[] { "kobieta" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "男", new String[] { "otoko", "dan" }, new String[] { "mężczyzna" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "見", new String[] { "mi", "ken" }, new String[] { "widzieć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "行", new String[] { "i", "kou", "gyou" }, new String[] { "iść" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "食", new String[] { "ta", "shoku" }, new String[] { "jeść" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "飲", new String[] { "no", "in" }, new String[] { "pić" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "東", new String[] { "higashi", "tou" }, new String[] { "wschód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "西", new String[] { "nishi", "sei", "sai" }, new String[] { "zachód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "南", new String[] { "minami", "nan" }, new String[] { "południe" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北", new String[] { "kita", "hoku", "ho tsu" }, new String[] { "północ" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "口", new String[] { "guchi", "kuchi", "kou" }, new String[] { "usta" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "出", new String[] { "de", "da", "shu tsu", "shutsu" }, new String[] { "wychodzić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "右", new String[] { "migi", "u", "yuu" }, new String[] { "prawo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "左", new String[] { "hidari", "sa" }, new String[] { "lewo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "分", new String[] { "fun", "pun", "bun" }, new String[] { "minuta", "dzielić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "先", new String[] { "sen", "saki" }, new String[] { "na przód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "生", new String[] { "sei", "u", "shou" }, new String[] { "urodziny" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大", new String[] { "dai", "oo", "tai"}, new String[] { "duży" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "学", new String[] { "gaku", "ga tsu", "mana" }, new String[] { "uczyć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "外", new String[] { "gai", "soto" }, new String[] { "na zewnątrzn" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "国", new String[] { "koku", "goku", "kuni" }, new String[] { "kraj" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "京", new String[] { "kyou" }, new String[] { "stolica" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "子", new String[] { "ko", "shi" }, new String[] { "dziecko" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "小", new String[] { "chii", "shou" }, new String[] { "mały" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "会", new String[] { "a", "kai" }, new String[] { "spotykać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "社", new String[] { "sha", "ja" }, new String[] { "firma" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "父", new String[] { "chichi", "tou", "fu" }, new String[] { "ojciec" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "母", new String[] { "haha", "kaa", "bo" }, new String[] { "matka" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高", new String[] { "taka", "kou" }, new String[] { "wysoki" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "校", new String[] { "kou" }, new String[] { "szkoła" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "毎", new String[] { "mai" }, new String[] { "w każdy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "語", new String[] { "go" }, new String[] { "słowo" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "文", new String[] { "bun" }, new String[] { "zdanie" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "帰", new String[] { "kae", "ki" }, new String[] { "wracać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "入", new String[] { "hai", "iri", "i", "nyuu" }, new String[] { "wchodzić" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "員", new String[] { "in" }, new String[] { "członek" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "新", new String[] { "atara", "shin" }, new String[] { "nowy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "聞", new String[] { "ki", "bun" }, new String[] { "słuczać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "作", new String[] { "tsuku", "saku" }, new String[] { "tworzyć", "robić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "仕", new String[] { "shi" }, new String[] { "służyć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "事", new String[] { "goto", "koto", "ji" }, new String[] { "rzecz" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "電", new String[] { "den" }, new String[] { "elektryczność" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "車", new String[] { "kuruma", "sha" }, new String[] { "samochód" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "休", new String[] { "yasu", "kyuu" }, new String[] { "odpoczywać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "言", new String[] { "i", "gen" }, new String[] { "mówić" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "読", new String[] { "yo", "doku" }, new String[] { "czytać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "思", new String[] { "omo", "shi" }, new String[] { "myśleć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "次", new String[] { "tsugi", "ji" }, new String[] { "następny" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "何", new String[] { "nani", "nan" }, new String[] { "co" }, "czytanie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午", new String[] { "go" }, new String[] { "południe" }, "czytanie, czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "後", new String[] { "go", "ato", "ushi" }, new String[] { "za" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "前", new String[] { "mae", "zen" }, new String[] { "przed" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "名", new String[] { "na", "mei" }, new String[] { "imię" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "白", new String[] { "shiro", "haku" }, new String[] { "biały" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "雨", new String[] { "ame", "u" }, new String[] { "deszcz" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "書", new String[] { "ka", "sho" }, new String[] { "pisać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "友", new String[] { "tomo", "yuu" }, new String[] { "przyjaciel" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "間", new String[] { "kan", "aida" }, new String[] { "pomiędzy" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "家", new String[] { "ie", "ka" }, new String[] { "dom" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "話", new String[] { "hana", "hanashi", "wa" }, new String[] { "rozmawiać" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "少", new String[] { "suko", "suku", "shou" }, new String[] { "trochę" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "古", new String[] { "furu", "ko" }, new String[] { "stary" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "知", new String[] { "shi", "chi" }, new String[] { "wiedzieć" }, "czytanie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "来", new String[] { "ku", "ki", "ko", "rai" }, new String[] { "wiedzieć" }, "czytanie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一時", new String[] { "ichiji" }, new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一年生", new String[] { "ichinensei" }, new String[] { "student pierwszego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一分", new String[] { "ippun" }, new String[] { "jedna minuta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一つ", new String[] { "hitotsu" }, new String[] { "1" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "二時", new String[] { "niji" }, new String[] { "2" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "二年生", new String[] { "ninensei" }, new String[] { "student drugiego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "二つ", new String[] { "futatsu" }, new String[] { "2" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "二つ日間", new String[] { "futsukakan" }, new String[] { "dwa dni" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三時", new String[] { "sanji" }, new String[] {"3" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三年生", new String[] { "sannensei" }, new String[] { "student trzeciego roku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三つ", new String[] { "mittsu" }, new String[] { "3" }, "liczenie" );

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "四時", new String[] { "yoji" }, new String[] { "4" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "四年生", new String[] { "yonensei" }, new String[] { "student czwartego roku"} , null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "四つ", new String[] { "yottsu" }, new String[] { "4" }, "liczenie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "四月", new String[] { "shigatsu" }, new String[] { "kwiecień" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "五時", new String[] { "goji" }, new String[] { "5" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "五つ", new String[] { "itsutsu" }, new String[] { "5" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六時", new String[] { "rokuji" }, new String[] { "6" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六分", new String[] { "roppun" }, new String[] { "6" }, "minut");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六つ", new String[] { "muttsu" }, new String[] { "6" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "七時", new String[] { "shichiji" }, new String[] { "7" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "七つ", new String[] { "nanatsu" }, new String[] { "7" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八時", new String[] { "hachiji" }, new String[] { "8" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八歳", new String[] { "hassai" }, new String[] { "8" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八つ", new String[] { "yattsu" }, new String[] { "8" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "九時", new String[] { "kuji" }, new String[] { "9" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "九歳", new String[] { "kyuusai" }, new String[] { "9" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "九つ", new String[] { "kokonotsu" }, new String[] { "9" }, "liczenie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "十時", new String[] { "juuji" }, new String[] { "10" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "十歳", new String[] { "jussai" }, new String[] { "10" }, "wiek");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "十", new String[] { "too" }, new String[] { "10" }, "liczenie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三百", new String[] { "sanbyaku" }, new String[] { "300" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "六百", new String[] { "roppyaku" }, new String[] { "600" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八百", new String[] { "happyaku" }, new String[] { "800" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "三千", new String[] { "sanzen" }, new String[] { "3000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "八千", new String[] { "hassen" }, new String[] { "8000" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一万", new String[] { "ichiman" }, new String[] { "10000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "十万", new String[] { "juuman" }, new String[] { "100000" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "百万", new String[] { "hyakuman" }, new String[] { "1000000" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "百円", new String[] { "hyakuen" },  new String[] { "100" }, null);		
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "一時", new String[] { "ichiji" },  new String[] { "1" }, "godzina");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "子供の時", new String[] { "kodomo no toki" },  new String[] { "w czasach dziecinstwa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "時々", new String[] { "tokidoki" },  new String[] { "czasami" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_3, WordType.HIRAGANA, "時計", new String[] { "tokei" },  new String[] { "zegarek" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日曜日", new String[] { "nichiyoubi" },  new String[] { "niedziela" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "毎日", new String[] { "mainichi" },  new String[] { "każdego dnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "母の日", new String[] { "haha no hi" },  new String[] { "dzień matki" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日記", new String[] { "nikki" },  new String[] { "pamiętnik" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "三日", new String[] { "mikka" },  new String[] { "3 dni" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "本", new String[] { "hon" },  new String[] { "książka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日本", new String[] { "nihon" },  new String[] { "Japonia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日本語", new String[] { "nihongo" },  new String[] { "język japoński" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "山本さん", new String[] { "Yamamoto san" },  new String[] { "Pan Yamamoto" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日本人", new String[] { "nihonjin" },  new String[] { "Japonczyk" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "一人で", new String[] { "hitoride" },  new String[] { "samotny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "この人", new String[] { "kono hito" },  new String[] { "ten człowiek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "三人", new String[] { "sannin" },  new String[] { "3 osoby" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "月曜日", new String[] { "getsuyoubi" },  new String[] { "poniedziałek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "一月", new String[] { "ichigatsu" },  new String[] { "styczeń" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "月", new String[] { "tsuki" },  new String[] { "księżyc" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "火曜日", new String[] { "kayoubi" },  new String[] { "wtorek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "火", new String[] { "hi" },  new String[] { "ogień" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "水曜日", new String[] { "suiyoubi" },  new String[] { "środa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "水", new String[] { "mizu" },  new String[] { "woda" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "木曜日", new String[] { "mokuyoubi" },  new String[] { "czwartek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "木", new String[] { "ki" },  new String[] { "drzewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "金曜日", new String[] { "kinyoubi" },  new String[] { "piątek" }, "'");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "お金", new String[] { "okane" },  new String[] { "pieniądze" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "土曜日", new String[] { "doyoubi" },  new String[] { "sobota" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "土", new String[] { "tsuchi" },  new String[] { "ziemia" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "日曜日", new String[] { "nichiyoubi" },  new String[] {  "niedziela" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "上", new String[] { "ue" },  new String[] { "na górze" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "上手な", new String[] { "jouzuna" },  new String[] { "dobry w" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "屋上", new String[] { "okujou" },  new String[] { "sufit" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "下", new String[] { "shita" },  new String[] { "pod" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "地下鉄", new String[] { "chikatetsu" },  new String[] { "metro" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "下手な", new String[] { "hetana" },  new String[] { "słaby" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "中", new String[] { "naka" },  new String[] { "środek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "中国", new String[] { "chuugoku" },  new String[] { "Chiny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "中学", new String[] { "chuugaku" },  new String[] { "gimnazjum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "一年中", new String[] { "ichinenjuu" },  new String[] { "cały rok" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "三時半", new String[] { "sanji han" },  new String[] { "3:30" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "半分", new String[] { "hanbun" },  new String[] { "połowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "山", new String[] { "yama" }, new String[] { "góra" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "富士山", new String[] { "Fuji san" }, new String[] { "Góra Fuji" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "川", new String[] { "kawa" }, new String[] { "rzeka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "小川さん", new String[] { "Ogawa san" }, new String[] { "Pan Ogawa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "元気な", new String[] { "genkina" }, new String[] { "zdrowy", "energiczny" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "元気", new String[] { "genki" }, new String[] { "zdrowy", "energiczny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "天気", new String[] { "tenki" }, new String[] { "pogoda" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "電気", new String[] { "denki" }, new String[] { "elektryczność" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "天気", new String[] { "tenki" }, new String[] { "pogoda" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "天国", new String[] { "tengoku" }, new String[] { "niebo" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "私", new String[] { "watashi" }, new String[] { "ja" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "市立大学", new String[] { "shiritsudaigaku" }, new String[] { "prywatny uniwersytet" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "今", new String[] { "ima" }, new String[] { "teraz" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "今日", new String[] { "kyou" }, new String[] { "dzisiaj" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "今晩", new String[] { "konban" }, new String[] { "dziś wieczorem" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "田中さん", new String[] { "Tanaka san" }, new String[] { "Pan Tanaka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "山田さん", new String[] { "Yamada san" }, new String[] { "Pan Yamada" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "田んぼ", new String[] { "tanbo" }, new String[] { "pole ryżowe" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "女の人", new String[] { "onna no hito" }, new String[] { "kobieta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "女性", new String[] { "josei" }, new String[] { "kobieta" }, "oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "男の人", new String[] { "otoko no hito" }, new String[] { "mężczyzna" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "男性", new String[] { "dansei" }, new String[] { "mężczyzna" }, "oficjalnie");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "見る", new String[] { "miru" }, new String[] { "patrzeć" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "見物", new String[] { "kenbutsu" }, new String[] { "zwiedzanie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "行く", new String[] { "iku" }, new String[] { "iść" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "銀行", new String[] { "ginkou" }, new String[] { "bank" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "一行目", new String[] { "ichigyoume" }, new String[] { "pierwsza linia" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "食べる", new String[] { "taberu" }, new String[] { "jeść" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "食べ物", new String[] { "tabemono" }, new String[] { "jedzenie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "食堂", new String[] { "shokudou" }, new String[] { "stołówka" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "飲む", new String[] { "nomu" }, new String[] { "pić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "飲み物", new String[] { "nomimono" }, new String[] { "picie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_5, WordType.HIRAGANA, "飲酒運転", new String[] { "inshuunten" }, new String[] { "pijany kierowca" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "東", new String[] { "higashi" }, new String[] { "wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "東口", new String[] { "higashiguchi" }, new String[] { "wschodnie wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "東京", new String[] { "toukyou" }, new String[] { "Tokio" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "西", new String[] { "nishi" }, new String[] { "zachód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "西口", new String[] { "nishiguchi" }, new String[] { "zachodnie wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "関西", new String[] { "kansai" }, new String[] { "Kansai" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "南", new String[] { "minami" }, new String[] { "południe" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "南口", new String[] { "minamiguchi" }, new String[] { "południowe wyjście" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北", new String[] { "kita" }, new String[] { "północ" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北口", new String[] { "kitaguchi" }, new String[] { "północne wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "東北", new String[] { "tohoku" }, new String[] { "Tohoku" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北海道", new String[] { "hokkaidou" }, new String[] { "Hokkaido" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北東", new String[] { "hokutou" }, new String[] { "północny wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "南東", new String[] { "nantou" }, new String[] { "południowy wschód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "南西", new String[] { "nansei" }, new String[] { "południowy zachód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "北西", new String[] { "hokusei" }, new String[] { "północny zachód" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "口", new String[] { "kuchi" }, new String[] { "usta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "人口", new String[] { "jinkou" }, new String[] { "populacja" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "出る", new String[] { "deru" }, new String[] { "wychodzić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "出口", new String[] { "deguchi" }, new String[] { "wyjście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "出す", new String[] { "dasu" }, new String[] { "wziąć coś" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "出席", new String[] { "shusseki" }, new String[] { "nadzór" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "輸出", new String[] { "yushutsu" }, new String[] { "eksport" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "右", new String[] { "migi" }, new String[] { "prawo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "右折", new String[] { "usetsu" }, new String[] { "skręcić w prawo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "左右", new String[] { "sayuu" }, new String[] { "prawo i lewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "左", new String[] { "hidari" }, new String[] { "lewo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "左折", new String[] { "sasetsu" }, new String[] { "skręcić w lewo" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "五分", new String[] { "gofun" }, new String[] { "pięć minut" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "十分", new String[] { "juppun" }, new String[] { "dziesięć minut" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "自分", new String[] { "jibun" }, new String[] { "siebie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "半分", new String[] { "hanbun" }, new String[] { "połowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "先生", new String[] { "sensei" }, new String[] { "nauczyciel" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "先週", new String[] { "senshuu" }, new String[] { "zeszły tydzień" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "先に", new String[] { "sakini" }, new String[] { "na przód" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "学生", new String[] { "gakusei" }, new String[] { "uczeń" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "生まれる", new String[] { "umareru" }, new String[] { "rodzić się" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "一層に一度", new String[] { "issou ni ichido" }, new String[] { "raz w życiu" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大学生", new String[] { "daigakusei" }, new String[] { "student" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大きい", new String[] { "ookii" }, new String[] { "duży" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大変", new String[] { "taihen" }, new String[] { "trudna sytuacja" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大人", new String[] { "otona" }, new String[] { "dorosły" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "大学", new String[] { "daigaku" }, new String[] { "uniwersytet" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "学校", new String[] { "gakkou" }, new String[] { "szkoła" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "学ぶ", new String[] { "manabu" }, new String[] { "uczyć" }, "kogoś");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "外国", new String[] { "gaikoku" }, new String[] { "zagraniczny kraj" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "外国人", new String[] { "gaikokujin" }, new String[] { "obcokrajowiec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "外", new String[] { "soto" }, new String[] { "na zewnątrzn" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "中国", new String[] { "chuugoku" }, new String[] { "Chiny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_6, WordType.HIRAGANA, "国", new String[] { "kuni" }, new String[] { "kraj" }, null);		
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "東京", new String[] { "toukyou" }, new String[] { "Tokio" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "京子", new String[] { "kyouko" }, new String[] { "kyoko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "京都", new String[] { "kyouto" }, new String[] { "Kyoto" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "子ども", new String[] { "kodomo" }, new String[] { "dziecko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "京子", new String[] { "kyouko" }, new String[] { "Kyoko" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "女の子", new String[] { "onna no ko" }, new String[] { "dziewczynka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "男の子", new String[] { "otoko no ko" }, new String[] { "chłopczyk" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "電子メール", new String[] { "denshi meeru" }, new String[] { "email" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "小さい", new String[] { "chiisai" }, new String[] { "mały" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "小学校", new String[] { "shougakkou" }, new String[] { "szkoła podstawowa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "小学生", new String[] { "shougakusei" }, new String[] { "uczeń szkoły podstawowej" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "会う", new String[] { "au" }, new String[] { "spotykać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "会社", new String[] { "kaisha" }, new String[] { "firma" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "会社員", new String[] { "kaishain" }, new String[] { "pracownik firmy" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "会社", new String[] { "kaisha" }, new String[] { "firma" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "社会", new String[] { "shakai" }, new String[] { "społeczeństwo" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "神社", new String[] { "jinja" }, new String[] { "świątynia" }, "shintoistyczna");

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "父", new String[] { "chichi" }, new String[] { "ojciec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "お父さん", new String[] { "otousan" }, new String[] { "ojciec" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "父母", new String[] { "fubo" }, new String[] { "ojciec i matka" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "母", new String[] { "haha" }, new String[] { "matka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "お母さん", new String[] { "okaasan" }, new String[] { "matka" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "母語", new String[] { "bogo" }, new String[] { "język ojczysty" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高い", new String[] { "takai" }, new String[] { "drogi", "wysoki" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高校", new String[] { "koukou" }, new String[] { "liceum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高校生", new String[] { "koukousei" }, new String[] { "licealista" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "学校", new String[] { "gakkou" }, new String[] { "szkoła" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高校", new String[] { "koukou" }, new String[] { "liceum" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "高校生", new String[] { "koukousei" }, new String[] { "licealista" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "中学校", new String[] { "chuugakkou" }, new String[] { "gimnazjum" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "毎日", new String[] { "mainichi" }, new String[] { "każdego dnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "毎週", new String[] { "maishuu" }, new String[] { "każdego tygodnia" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "毎晩", new String[] { "maiban" }, new String[] { "każdej nocy" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "日本語", new String[] { "nihongo" }, new String[] { "język japoński" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "英語", new String[] { "eigo" }, new String[] { "język angielski" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "文学", new String[] { "bungaku" }, new String[] { "literatura" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "作文", new String[] { "sakubun" }, new String[] { "wypracowanie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "文字", new String[] { "moji" }, new String[] { "list" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "帰る", new String[] { "kaeru" }, new String[] { "wracać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "帰国", new String[] { "kikoku" }, new String[] { "wracać do kraju" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "入る", new String[] { "hairu" }, new String[] { "wchodzić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "入口", new String[] { "iriguchi" }, new String[] { "wejście" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "入れる", new String[] { "ireru" }, new String[] { "wkładać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_7, WordType.HIRAGANA, "輸入", new String[] { "yunyuu" }, new String[] { "import" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "会社員", new String[] { "kaishain" }, new String[] { "pracownik biurowy" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "店員", new String[] { "ten'in" }, new String[] { "sprzedawca" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "新しい", new String[] { "atarashii" }, new String[] { "nowy" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "新聞", new String[] { "shinbun" }, new String[] { "gazeta" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "新幹線", new String[] { "shinkansen" }, new String[] { "shinkansen" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "聞く", new String[] { "kiku" }, new String[] { "słuchać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "新聞", new String[] { "shinbun" }, new String[] { "gazeta" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "作る", new String[] { "tsukuru" }, new String[] { "tworzyć" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "作文", new String[] { "sakubun" }, new String[] { "wypracowanie" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "作品", new String[] { "sakuhin" }, new String[] { "utwór artystyczny" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "仕事", new String[] { "shigoto" }, new String[] { "praca" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "仕事", new String[] { "shigoto" }, new String[] { "praca" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "仕事", new String[] { "shigoto" }, new String[] { "praca" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "事", new String[] { "koto" }, new String[] { "rzecz" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "火事", new String[] { "kaji" }, new String[] { "ogień" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "食事", new String[] { "shokuji" }, new String[] { "posiłek" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "電車", new String[] { "densha" }, new String[] { "pociąg" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "電気", new String[] { "denki" }, new String[] { "elektryczność" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "電話", new String[] { "denwa" }, new String[] { "telefon" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "車", new String[] { "kuruma" }, new String[] { "samochód" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "電車", new String[] { "densha" }, new String[] { "pociąg" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "自転車", new String[] { "jitensha" }, new String[] { "rower" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "休む", new String[] { "yasumu" }, new String[] { "być nieobecny", "odpoczywać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "休み", new String[] { "yasumi" }, new String[] { "wakacje", "nieobecny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "休日", new String[] { "kyuujitsu" }, new String[] { "wakacje" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "言う", new String[] { "iu" }, new String[] { "mówić" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "言語学", new String[] { "gengogaku" }, new String[] { "lingwistyka" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "読む", new String[] { "yomu" }, new String[] { "czytać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "読書", new String[] { "dokusho" }, new String[] { "czytać książki" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "思う", new String[] { "omou" }, new String[] { "myśleć" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "不思議な", new String[] { "fushigina" }, new String[] { "tajemniczy" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "次", new String[] { "tsugi" }, new String[] { "następny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "次女", new String[] { "jijo" }, new String[] { "następna córka" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "何", new String[] { "nani" }, new String[] { "co" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "何時", new String[] { "nanji" }, new String[] { "która godzina" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_8, WordType.HIRAGANA, "何人", new String[] { "nan nin" }, new String[] { "ile ludzi" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午前", new String[] { "gozen" }, new String[] { "am" }, "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午後", new String[] { "gogo" }, new String[] { "pm" }, "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午前中", new String[] { "gozenchuu" }, new String[] { "przed południem" }, null);

		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午後", new String[] { "gogo" }, new String[] { "pm" }, "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "の後", new String[] { "no ato" }, new String[] { "po" }, "...");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "後で", new String[] { "atode" }, new String[] { "później" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "後ろ", new String[] { "ushiro" }, new String[] { "za" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "最後に", new String[] { "saigoni" }, new String[] { "ostatni" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "前", new String[] { "mae" }, new String[] { "przed" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "午前", new String[] { "gozen" }, new String[] { "am" }, "czas");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "名前", new String[] { "namae" }, new String[] { "imię" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "名前", new String[] { "namae" }, new String[] { "imię" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "有名な", new String[] { "yuumeina" }, new String[] { "sławny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "名刺", new String[] { "meishi" }, new String[] { "wizytówka" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "白い", new String[] { "shiroi" }, new String[] { "biały" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "白紙", new String[] { "hakushi" }, new String[] { "biała kartka" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "雨", new String[] { "ame" }, new String[] { "deszcz" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "雨期", new String[] { "uki" }, new String[] { "deszczowe dni" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "書く", new String[] { "kaku" }, new String[] { "pisać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "辞書", new String[] { "jisho" }, new String[] { "słownik" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "友だち", new String[] { "tomodachi" }, new String[] { "przyjaciel" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "親友", new String[] { "shin'yuu" }, new String[] { "najlepszy przyjaciel" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "友人", new String[] { "yuujin" }, new String[] { "przyjaciel" }, "bardziej oficjalnie");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "時間", new String[] { "jikan" }, new String[] { "godzina" }, "okres");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "二時間", new String[] { "nijikan" }, new String[] { "dwie godziny" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "間", new String[] { "aida" }, new String[] { "pomiędzy" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "人間", new String[] { "ningen" }, new String[] { "człowiek" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "一週間", new String[] { "ishuukan" }, new String[] { "jeden tydzień" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "家", new String[] { "ie" }, new String[] { "dom" }, "bardziej oficjalnie");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "家族", new String[] { "kazoku" }, new String[] { "rodzina" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "家", new String[] { "uchi" }, new String[] { "dom" }, "moje miejsce");
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "話す", new String[] { "hanasu" }, new String[] { "rozmawiać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "話", new String[] { "hanashi" }, new String[] { "rozmowa" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "電話", new String[] { "denwa" }, new String[] { "telefon" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "会話", new String[] { "kaiwa" }, new String[] { "rozmowa" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "少し", new String[] { "sukoshi" }, new String[] { "mało" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "少ない", new String[] { "sukunai" }, new String[] { "trochę" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "少々", new String[] { "shoushou" }, new String[] { "trochę" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "古い", new String[] { "furui" }, new String[] { "stary" }, "do przedmiotów");
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "中古", new String[] { "chuuko" }, new String[] { "używany" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "知る", new String[] { "shiru" }, new String[] { "wiedzieć" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "知人", new String[] { "chijin" }, new String[] { "znajomy" }, null);
		
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "来る", new String[] { "kuru" }, new String[] { "wracać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "来ます", new String[] { "kimasu" }, new String[] { "wracać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "来ない", new String[] { "konai" }, new String[] { "nie wracać" }, null);
		addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_9, WordType.HIRAGANA, "来週", new String[] { "raishuu" }, new String[] { "następny tydzień" }, null);
		
		
		//addPolishJapaneseEntry(polishJapaneseEntries, DictionaryEntryType.WORD_GENKI1_4, WordType.HIRAGANA, "kanji", "czytanie", "tlumaczenie", null);
		
		return result;
	}
	
	private static void generateKanjiImages(List<PolishJapaneseEntry> polishJapaneseEntries, Map<String, String> kanjiCache, String imageDir) throws JapannakaException {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			KanjiImageWriter.createNewKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
		}
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, WordType wordType, String romaji, String polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, wordType, null, new String[] { romaji }, new String[] { polishTranslateString }, info);
	}

	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, WordType wordType, String romaji, String[] polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, wordType, null, new String[] { romaji }, polishTranslateString, info);
	}

	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, WordType wordType, String[] romaji, String polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, wordType, null, romaji, new String[] { polishTranslateString }, info);
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, WordType wordType, String[] romaji, String[] polishTranslateString, String info) {
		
		addPolishJapaneseEntry(polishJapaneseEntries, dictionaryEntryType, wordType, null, romaji, polishTranslateString, info);
	}
	
	private static void addPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			DictionaryEntryType dictionaryEntryType, WordType wordType, String japanese, String[] romajiArray, String[] polishTranslateList, String info) {
				
		PolishJapaneseEntry entry = new PolishJapaneseEntry();
		
		entry.setGroupName(dictionaryEntryType.getName());
		
		List<PolishTranslate> polishTranslateList2 = new ArrayList<PolishTranslate>();
		
		for (int idx = 0; idx < polishTranslateList.length; ++idx) {
			String currentPolishTranslate = polishTranslateList[idx];
			
			polishTranslateList2.add(createPolishTranslate(currentPolishTranslate, 
					(idx != polishTranslateList.length - 1 ? null : info)));
		}
		
		entry.setJapanese(japanese);
		
		List<RomajiEntry> romajiList = new ArrayList<RomajiEntry>();
		for (String romaji : romajiArray) {
			RomajiEntry romajiEntry = new RomajiEntry();
			
			romajiEntry.setRomaji(romaji);
			romajiEntry.setWordType(wordType);
			
			romajiList.add(romajiEntry);
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
	
	private static void validatePolishJapaneseEntries(List<PolishJapaneseEntry> polishJapaneseKanjiEntries, List<KanaEntry> hiraganaEntries,
			List<KanaEntry> kitakanaEntries) throws JapannakaException {
		
		Map<String, KanaEntry> hiraganaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : hiraganaEntries) {
			hiraganaCache.put(kanaEntry.getKana(), kanaEntry);
		}

		Map<String, KanaEntry> kitakanaCache = new HashMap<String, KanaEntry>();
		
		for (KanaEntry kanaEntry : kitakanaEntries) {
			kitakanaCache.put(kanaEntry.getKana(), kanaEntry);
		}
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseKanjiEntries) {
			List<RomajiEntry> romajiList = polishJapaneseEntry.getRomajiList();
			
			for (RomajiEntry currentRomaji : romajiList) {
				if (currentRomaji.getWordType() == WordType.HIRAGANA) { 
					validateJapaneseHiraganaWord(hiraganaCache, currentRomaji.getRomaji());
				} else if (currentRomaji.getWordType() == WordType.KATAKANA) { 
					validateJapaneseKitakanaWord(kitakanaCache, currentRomaji.getRomaji());
				} else {
					throw new RuntimeException("Bard word type");
				}
			}
		}
	}
	
	private static void validateJapaneseHiraganaWord(Map<String, KanaEntry> hiraganaCache, String word) throws JapannakaException {
		String remaingRestChars = null;
		
		String currentRestChars = "";
		
		for (int idx = 0; idx < word.length(); ++idx) {
			String currentChar = String.valueOf(word.charAt(idx));
			
			if (currentChar.equals(" ") == true) {
				continue;
			}
			
			currentRestChars += currentChar.toLowerCase();
			
			if (currentRestChars.length() == 2 && currentRestChars.charAt(0) == currentRestChars.charAt(1) &&
					currentRestChars.charAt(0) != 'n') {
				
				KanaEntry kanaEntry = hiraganaCache.get("ttsu");
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "" + currentRestChars.charAt(1);
				
				continue;				
			}
			
			if (currentRestChars.equals("a") == true ||
					currentRestChars.equals("i") == true ||
					currentRestChars.equals("u") == true ||
					currentRestChars.equals("e") == true ||
					currentRestChars.equals("o") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";
			} else if (currentRestChars.equals("ka") == true ||
					currentRestChars.equals("ki") == true ||
					currentRestChars.equals("ku") == true ||
					currentRestChars.equals("ke") == true ||
					currentRestChars.equals("ko") == true ||
					currentRestChars.equals("kya") == true ||
					currentRestChars.equals("kyu") == true ||
					currentRestChars.equals("kyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("sa") == true ||
					currentRestChars.equals("shi") == true ||
					currentRestChars.equals("sha") == true ||
					currentRestChars.equals("shu") == true ||
					currentRestChars.equals("sho") == true ||
					currentRestChars.equals("su") == true ||
					currentRestChars.equals("se") == true ||
					currentRestChars.equals("so") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ta") == true ||
					currentRestChars.equals("tsu") == true ||
					currentRestChars.equals("te") == true ||
					currentRestChars.equals("to") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("chi") == true ||
					currentRestChars.equals("cha") == true ||
					currentRestChars.equals("chu") == true ||
					currentRestChars.equals("cho") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.startsWith("n") == true || currentRestChars.equals("n'") == true) {
				
				boolean nProcessed = false;
				
				if (currentRestChars.equals("n'") == true) {
					KanaEntry kanaEntry = hiraganaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;					
				}
				
				if (nProcessed == false && (currentRestChars.equals("na") == true ||
						currentRestChars.equals("ni") == true ||
						currentRestChars.equals("nu") == true ||
						currentRestChars.equals("ne") == true ||
						currentRestChars.equals("no") == true ||
						currentRestChars.equals("nya") == true ||
						currentRestChars.equals("nyu") == true ||
						currentRestChars.equals("nyo") == true)) {

					KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;
				} else if (nProcessed == false && currentRestChars.length() > 1) {
					
					if (currentRestChars.startsWith("ny") == false) {
						KanaEntry kanaEntry = hiraganaCache.get("n");
						
						if (kanaEntry == null) {
							throw new JapannakaException("Can't find kanaEntry!");
						}
						
						currentRestChars = currentRestChars.substring(1);
						
						nProcessed = true;
					}						
				}
				
				if (nProcessed == false && currentRestChars.length() == 1 && idx == word.length() - 1) {
					KanaEntry kanaEntry = hiraganaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;
				}
			} else if (currentRestChars.equals("ha") == true ||
					currentRestChars.equals("hi") == true ||
					currentRestChars.equals("he") == true ||
					currentRestChars.equals("ho") == true ||
					currentRestChars.equals("hya") == true ||
					currentRestChars.equals("hyu") == true ||
					currentRestChars.equals("hyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("fu") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ma") == true ||
					currentRestChars.equals("mi") == true ||
					currentRestChars.equals("mu") == true ||
					currentRestChars.equals("me") == true ||
					currentRestChars.equals("mo") == true ||
					currentRestChars.equals("mya") == true ||
					currentRestChars.equals("myu") == true ||
					currentRestChars.equals("myo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ya") == true ||
					currentRestChars.equals("yu") == true ||
					currentRestChars.equals("yo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ra") == true ||
					currentRestChars.equals("ri") == true ||
					currentRestChars.equals("ru") == true ||
					currentRestChars.equals("re") == true ||
					currentRestChars.equals("ro") == true ||
					currentRestChars.equals("rya") == true ||
					currentRestChars.equals("ryu") == true ||
					currentRestChars.equals("ryo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("wa") == true ||
					currentRestChars.equals("wo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ga") == true ||
					currentRestChars.equals("gi") == true ||
					currentRestChars.equals("gu") == true ||
					currentRestChars.equals("ge") == true ||
					currentRestChars.equals("go") == true ||
					currentRestChars.equals("gya") == true ||
					currentRestChars.equals("gyu") == true ||
					currentRestChars.equals("gyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("za") == true ||
					currentRestChars.equals("zu") == true ||
					currentRestChars.equals("ze") == true ||
					currentRestChars.equals("zo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ji") == true ||
					currentRestChars.equals("ja") == true ||
					currentRestChars.equals("ju") == true ||
					currentRestChars.equals("jo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("da") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("du") == true ||
					currentRestChars.equals("de") == true ||
					currentRestChars.equals("do") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ba") == true ||
					currentRestChars.equals("bi") == true ||
					currentRestChars.equals("bu") == true ||
					currentRestChars.equals("be") == true ||
					currentRestChars.equals("bo") == true ||
					currentRestChars.equals("bya") == true ||
					currentRestChars.equals("byu") == true ||
					currentRestChars.equals("byo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("pa") == true ||
					currentRestChars.equals("pi") == true ||
					currentRestChars.equals("pu") == true ||
					currentRestChars.equals("pe") == true ||
					currentRestChars.equals("po") == true ||
					currentRestChars.equals("pya") == true ||
					currentRestChars.equals("pyu") == true ||
					currentRestChars.equals("pyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			}
			
			remaingRestChars = currentRestChars;
		}
		
		if (remaingRestChars.equals("") == false) {
			throw new JapannakaException("Validate error for word: " + word + ", remaing: " + remaingRestChars);
		}		
	}

	private static void validateJapaneseKitakanaWord(Map<String, KanaEntry> kitakanaCache, String word) throws JapannakaException {
		String remaingRestChars = null;
		
		String currentRestChars = "";
		
		for (int idx = 0; idx < word.length(); ++idx) {
			String currentChar = String.valueOf(word.charAt(idx));
			
			if (currentChar.equals(" ") == true) {
				continue;
			}
			
			currentRestChars += currentChar.toLowerCase();
			
			if (idx > 0) {
				char previousChar = word.charAt(idx - 1);
				char currentCharChar = word.charAt(idx);
				
				if (previousChar == currentCharChar && isVowel(previousChar) == true && isVowel(currentCharChar) == true) {
					KanaEntry kanaEntry = kitakanaCache.get("ttsu2");
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}

					currentRestChars = "";
					
					continue;
				}
			}
			
			if (currentRestChars.length() == 2 && currentRestChars.charAt(0) == currentRestChars.charAt(1) &&
					currentRestChars.charAt(0) != 'n') {
				
				KanaEntry kanaEntry = kitakanaCache.get("ttsu");
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "" + currentRestChars.charAt(1);
				
				continue;				
			}
			
			if (currentRestChars.equals("a") == true ||
					currentRestChars.equals("i") == true ||
					currentRestChars.equals("u") == true ||
					currentRestChars.equals("e") == true ||
					currentRestChars.equals("o") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";
			} else if (currentRestChars.equals("ka") == true ||
					currentRestChars.equals("ki") == true ||
					currentRestChars.equals("ku") == true ||
					currentRestChars.equals("ke") == true ||
					currentRestChars.equals("ko") == true ||
					currentRestChars.equals("kya") == true ||
					currentRestChars.equals("kyu") == true ||
					currentRestChars.equals("kyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("sa") == true ||
					currentRestChars.equals("shi") == true ||
					currentRestChars.equals("sha") == true ||
					currentRestChars.equals("shu") == true ||
					currentRestChars.equals("sho") == true ||
					currentRestChars.equals("she") == true ||
					currentRestChars.equals("su") == true ||
					currentRestChars.equals("se") == true ||
					currentRestChars.equals("so") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ta") == true ||
					currentRestChars.equals("tsu") == true ||
					currentRestChars.equals("te") == true ||
					currentRestChars.equals("to") == true ||
					currentRestChars.equals("ti") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("chi") == true ||
					currentRestChars.equals("cha") == true ||
					currentRestChars.equals("chu") == true ||
					currentRestChars.equals("cho") == true ||
					currentRestChars.equals("che") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.startsWith("n") == true || currentRestChars.equals("n'") == true) {
				
				boolean nProcessed = false;
				
				if (currentRestChars.equals("n'") == true) {
					KanaEntry kanaEntry = kitakanaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;					
				}
				
				if (nProcessed == false && (currentRestChars.equals("na") == true ||
						currentRestChars.equals("ni") == true ||
						currentRestChars.equals("nu") == true ||
						currentRestChars.equals("ne") == true ||
						currentRestChars.equals("no") == true ||
						currentRestChars.equals("nya") == true ||
						currentRestChars.equals("nyu") == true ||
						currentRestChars.equals("nyo") == true)) {

					KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;
				} else if (nProcessed == false && currentRestChars.length() > 1) {
					
					if (currentRestChars.startsWith("ny") == false) {
						KanaEntry kanaEntry = kitakanaCache.get("n");
						
						if (kanaEntry == null) {
							throw new JapannakaException("Can't find kanaEntry!");
						}
						
						currentRestChars = currentRestChars.substring(1);
						
						nProcessed = true;
					}						
				}
				
				if (nProcessed == false && currentRestChars.length() == 1 && idx == word.length() - 1) {
					KanaEntry kanaEntry = kitakanaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapannakaException("Can't find kanaEntry!");
					}
					
					currentRestChars = "";
					
					nProcessed = true;
				}
			} else if (currentRestChars.equals("ha") == true ||
					currentRestChars.equals("hi") == true ||
					currentRestChars.equals("he") == true ||
					currentRestChars.equals("ho") == true ||
					currentRestChars.equals("hya") == true ||
					currentRestChars.equals("hyu") == true ||
					currentRestChars.equals("hyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("fu") == true || 
					currentRestChars.equals("fa") == true ||
					currentRestChars.equals("fi") == true ||
					currentRestChars.equals("fe") == true ||
					currentRestChars.equals("fo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ma") == true ||
					currentRestChars.equals("mi") == true ||
					currentRestChars.equals("mu") == true ||
					currentRestChars.equals("me") == true ||
					currentRestChars.equals("mo") == true ||
					currentRestChars.equals("mya") == true ||
					currentRestChars.equals("myu") == true ||
					currentRestChars.equals("myo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ya") == true ||
					currentRestChars.equals("yu") == true ||
					currentRestChars.equals("yo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ra") == true ||
					currentRestChars.equals("ri") == true ||
					currentRestChars.equals("ru") == true ||
					currentRestChars.equals("re") == true ||
					currentRestChars.equals("ro") == true ||
					currentRestChars.equals("rya") == true ||
					currentRestChars.equals("ryu") == true ||
					currentRestChars.equals("ryo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("wa") == true ||
					currentRestChars.equals("wo") == true || 
					currentRestChars.equals("wi") == true ||
					currentRestChars.equals("we") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ga") == true ||
					currentRestChars.equals("gi") == true ||
					currentRestChars.equals("gu") == true ||
					currentRestChars.equals("ge") == true ||
					currentRestChars.equals("go") == true ||
					currentRestChars.equals("gya") == true ||
					currentRestChars.equals("gyu") == true ||
					currentRestChars.equals("gyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("za") == true ||
					currentRestChars.equals("zu") == true ||
					currentRestChars.equals("ze") == true ||
					currentRestChars.equals("zo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ji") == true ||
					currentRestChars.equals("ja") == true ||
					currentRestChars.equals("ju") == true ||
					currentRestChars.equals("jo") == true ||
					currentRestChars.equals("je") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("da") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("du") == true ||
					currentRestChars.equals("de") == true ||
					currentRestChars.equals("do") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("dyu") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ba") == true ||
					currentRestChars.equals("bi") == true ||
					currentRestChars.equals("bu") == true ||
					currentRestChars.equals("be") == true ||
					currentRestChars.equals("bo") == true ||
					currentRestChars.equals("bya") == true ||
					currentRestChars.equals("byu") == true ||
					currentRestChars.equals("byo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("pa") == true ||
					currentRestChars.equals("pi") == true ||
					currentRestChars.equals("pu") == true ||
					currentRestChars.equals("pe") == true ||
					currentRestChars.equals("po") == true ||
					currentRestChars.equals("pya") == true ||
					currentRestChars.equals("pyu") == true ||
					currentRestChars.equals("pyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapannakaException("Can't find kanaEntry!");
				}
				
				currentRestChars = "";					
			}
			
			remaingRestChars = currentRestChars;
		}
		
		if (remaingRestChars.equals("") == false) {
			throw new JapannakaException("Validate error for word: " + word + ", remaing: " + remaingRestChars);
		}		
	}
	
	private static boolean isVowel(char char_) {
		if (char_ == 'e' || char_ == 'i' || char_ == 'o' || char_ == 'a') {
			return true;
		} else {
			return false;
		}
	}
}
