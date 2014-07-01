package pl.idedyk.japanese.dictionary.misc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.api.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class GenerateCharHexCode {

	public static void main(String[] args) throws Exception {
		
		KanaHelper kanaHelper = new KanaHelper();
		
		// pobranie wszystkich znakow kana
		List<KanaEntry> hiraganaKanaEntries = kanaHelper.getAllHiraganaKanaEntries();
		List<KanaEntry> katakanaKanaEntries = kanaHelper.getAllKatakanaKanaEntries();
		List<KanaEntry> additionalKanaEntries = kanaHelper.getAllAdditionalKanaEntries();
		
		List<KanaEntry> kanaEntryList = new ArrayList<KanaEntry>();

		kanaEntryList.addAll(hiraganaKanaEntries);
		kanaEntryList.addAll(katakanaKanaEntries);
		kanaEntryList.addAll(additionalKanaEntries);
		
		TreeSet<String> uniqueCharacters = new TreeSet<String>();
		
		for (KanaEntry currentKanaEntry : kanaEntryList) {
			
			String kanaJapanese = currentKanaEntry.getKanaJapanese();
			
			for (int kanaJapaneseIdx = 0; kanaJapaneseIdx < kanaJapanese.length(); ++kanaJapaneseIdx) {				
				uniqueCharacters.add(new String("" + kanaJapanese.charAt(kanaJapaneseIdx)));
			}
		}
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile("../JapaneseDictionary_additional/kradfile");
		
		Collection<List<String>> kradFileMapValues = kradFileMap.values();
		
		for (List<String> currentkradFileMapValuesList : kradFileMapValues) {
			
			for (String currentCurrentkradFileMapValues : currentkradFileMapValuesList) {
				
				currentCurrentkradFileMapValues = currentCurrentkradFileMapValues.replaceAll("_", "");
				
				uniqueCharacters.add(currentCurrentkradFileMapValues);
				
				for (int currentCurrentkradFileMapValuesIdx = 0; currentCurrentkradFileMapValuesIdx < currentCurrentkradFileMapValues.length(); ++currentCurrentkradFileMapValuesIdx) {				
					uniqueCharacters.add("" + currentCurrentkradFileMapValues.charAt(currentCurrentkradFileMapValuesIdx));
				}				
			}
		}
		
		StringBuffer result = new StringBuffer();
		
		for (String character : uniqueCharacters) {
			
			String characterAsUTF32 = getUTF32ByteHex(character);
			
			System.out.println(character + " - " + characterAsUTF32);
			
			result.append("U+" + characterAsUTF32).append(" \\\n");
		}
		
		System.out.println("-----");
		
		System.out.println(result.toString());
	}

	private static String getUTF32ByteHex(String text) throws UnsupportedEncodingException {
		
		byte[] textBytes = text.getBytes("UTF-32");
		
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < textBytes.length; ++idx) {			
			sb.append(String.format("%1$02X", (textBytes[idx] & 0xFF)));			
		}
		
		Integer sbAsInteger = Integer.parseInt(sb.toString(), 16);
		
		return Integer.toHexString(sbAsInteger).toUpperCase();
	}
}
