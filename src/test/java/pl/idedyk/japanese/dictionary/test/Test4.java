package pl.idedyk.japanese.dictionary.test;

import java.util.List;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

public class Test4 {

	public static void main(String[] args) throws Exception {
		
		//List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(new String[] { "input/word01.csv", "input/word02.csv", "input/word03.csv", "input/word04.csv" });

		//generateExampleSentence(polishJapaneseEntries, "../JapaneseDictionary_additional/tatoeba", "output/sentences.csv", "output/sentences_groups.csv");

		// TreeSet<String> uniqueTrans = new TreeSet<String>();
		
		/*
		Iterator<List<JMEDictEntry>> jmedictNameValuesIterator = jmedictName.values().iterator();
		
		while(jmedictNameValuesIterator.hasNext()) {
			
			List<JMEDictEntry> jmedictValueList = jmedictNameValuesIterator.next();
			
			for (JMEDictEntry jmedictEntry : jmedictValueList) {				
				List<String> trans = jmedictEntry.getTrans();
				
				/ *
				for (String currentTrans : trans) {
					uniqueTrans.add(currentTrans);
				}
				* /
				
				if (trans.contains("unclass") == true) {
					System.out.println(jmedictEntry);
				}
			}
		}
		*/
		
		/*
		for (String currentUniqueTran : uniqueTrans) {
			System.out.println(currentUniqueTran);
		}
		*/		
		
		/*
		company -
		fem +
		given +
		masc +
		organization -
		person +
		place * ?
		product -
		station +
		surname +
		unclass * ?
		*/
		
		/*
		TreeMap<String, List<JMEDictEntry>> jmedictName = JMEDictReader.readJMnedict("../JapaneseDictionary_additional/JMnedict-TEST.xml");
		
		List<PolishJapaneseEntry> generatedNames = Helper.generateNames(jmedictName);
		
		CsvReaderWriter.generateCsv("input_names3/test.csv", generatedNames, false);
		*/
		
		/*
		List<PolishJapaneseEntry> namesList = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input_names2/names.csv");
		
		List<PolishJapaneseEntry> smallNamesList = new ArrayList<PolishJapaneseEntry>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : namesList) {
			
			List<DictionaryEntryType> dictionaryEntryTypeList = polishJapaneseEntry.getDictionaryEntryTypeList();
			
			if (	dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_COMPANY_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_PRODUCT_NAME) == true ||
					dictionaryEntryTypeList.contains(DictionaryEntryType.WORD_ORGANIZATION_NAME) == true) {
				
				if (dictionaryEntryTypeList.size() > 1) {					
					smallNamesList.add(polishJapaneseEntry);					
				}				
			}			
		}
		
		CsvReaderWriter.generateCsv("input_names2/names2.csv", smallNamesList, false);
		*/
		
		// test tokenizera
		//LuceneAnalyzer analyzerTest = new LuceneAnalyzer(Version.LUCENE_47);
		//JapaneseAnalyzer analyzerTest = new JapaneseAnalyzer(Version.LUCENE_47);
		
		//String text = "1";
		//String text = "1 ala";
		//String text = "Ala ma kota i psa";
		//String text = "Ala ma kota i psa2 i abc";
		//String text = "dziesięć lat";
		//String text = "お早う御座います";
		
		// KanaHelper kanaHelper = new KanaHelper();
				
		// Map<String, KanaEntry> kanaCache = kanaHelper.getKanaCache();
		
		//String text = "北海道医療大学駅"; // hokkaidou iryou daigaku eki
		
		// String text = "上越国際スキー場前駅"; 
		
		String text = "小さくて奥ゆかしい日本語ヘルパー";
		
		//String text = "北朝鮮兵が中国で殺人韓国紙";
		
		//String text = "北朝鮮難民救援基金";
				
		Tokenizer tokenizer = new Tokenizer();
		
		List<Token> tokenList = tokenizer.tokenize(text);
		
        for (Token token : tokenList) {
        	
        	System.out.println("S: " + token.getSurface());
        	System.out.println("B: " + token.getBaseForm());
        	System.out.println("CF: " + token.getConjugationForm());
        	System.out.println("CT: " + token.getConjugationType());
        	System.out.println("PSL1: " + token.getPartOfSpeechLevel1());
        	System.out.println("PSL2: " + token.getPartOfSpeechLevel2());
        	System.out.println("PSL3: " + token.getPartOfSpeechLevel3());
        	System.out.println("PSL4: " + token.getPartOfSpeechLevel4());
        	System.out.println("PO: " + token.getPosition());
        	System.out.println("PR: " + token.getPronunciation());
        	System.out.println("R: " + token.getReading());        	        	
        	
        	System.out.println("-----\n");
        	//System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
        	
        	/*
        	KanaWord kanaWord = kanaHelper.convertKanaStringIntoKanaWord(reading, kanaCache, false);
        	
        	String romaji = kanaHelper.createRomajiString(kanaWord);
        	
            System.out.println(surfaceForm + "\t" + reading + "\t" + romaji);
            */
        }
		
		/*
		TokenStream tokenStream = analyzerTest.tokenStream("a", text);
		
		tokenStream.reset();
		
		while(true) {
			
			boolean incrementTokenResult = tokenStream.incrementToken();
			
			if (incrementTokenResult == false) {
				break;
			}
			
			System.out.println("Token: " + tokenStream.getAttribute(CharTermAttribute.class).toString());	
			
			ReadingAttribute readingAttribute = tokenStream.getAttribute(ReadingAttribute.class);
			
			System.out.println("R: " + readingAttribute.getPronunciation() + " - " + readingAttribute.getReading());
			
		}
		
		analyzerTest.close();
		*/
		
		
	}
}
