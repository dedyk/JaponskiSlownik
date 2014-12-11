package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.dto.JMEDictNewNativeEntry;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary;
import pl.idedyk.japanese.dictionary.dto.ParseAdditionalInfo;
import pl.idedyk.japanese.dictionary.dto.JMENewDictionary.GroupEntry;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

public class CompleteJMEDictGroup {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Wczytywanie słownika edict...");
		
		JMEDictNewReader jmedictNewReader = new JMEDictNewReader();
		
		List<JMEDictNewNativeEntry> jmedictNativeList = jmedictNewReader.readJMEdict("../JapaneseDictionary_additional/JMdict_e");
		
		JMENewDictionary jmeNewDictionary = jmedictNewReader.createJMENewDictionary(jmedictNativeList);
		
		System.out.println("Wczytywanie słownika...");
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		System.out.println("Walidowanie słownika...");
		
		boolean validateResult = true;
		
		Set<String> alreadyValidateErrorResultGroupIds = new HashSet<String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKanaList().get(0);
			
			List<GroupEntry> groupEntryList = jmeNewDictionary.getGroupEntryList(kanji, kana);
			
			List<PolishJapaneseEntry> foundPolishJapaneseEntryGroupList = new ArrayList<PolishJapaneseEntry>();
			
			if (groupEntryList != null && isMultiGroup(groupEntryList) == false) {
								
				for (GroupEntry groupEntry : jmeNewDictionary.getTheSameTranslateInTheSameGroupGroupEntryList(kanji, kana)) {
					
					String groupEntryKanji = groupEntry.getKanji();
					String groupEntryKana = groupEntry.getKana();
					
					PolishJapaneseEntry findPolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntries, 
							groupEntryKanji, groupEntryKana);
					
					if (findPolishJapaneseEntry != null) {
						foundPolishJapaneseEntryGroupList.add(findPolishJapaneseEntry);
					}					
				}
			}
			
			// sprawdzanie, czy wszystkie elementy maja to samo tlumaczenie. Powinno tak byc, powinny byc w tej samej grupie
			if (foundPolishJapaneseEntryGroupList.size() > 1) {
				
				String firstTranslate = null;
				String firstInfo = null;
				String firstDictionaryEntryType = null;
				
				boolean localValidationError = false;
				
				for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
					
					String currentFoundTranslate = currentFoundPolishJapaneseEntry.getTranslates().toString();
					String currentFoundInfo = currentFoundPolishJapaneseEntry.getInfo();
					String currentFoundDictionaryEntryType = currentFoundPolishJapaneseEntry.getDictionaryEntryTypeList().toString();
					
					if (firstTranslate == null) {
						
						firstTranslate = currentFoundTranslate;						
						firstInfo = currentFoundInfo;
						firstDictionaryEntryType = currentFoundDictionaryEntryType;
						
					} else { // sprawdzenie
												
						if (	currentFoundTranslate.equals(currentFoundTranslate) == false ||
								currentFoundInfo.equals(firstInfo) == false ||
								currentFoundDictionaryEntryType.equals(firstDictionaryEntryType) == false) {
							
							localValidationError = true;
														
							break;
						}						
					}
				}
				
				if (localValidationError == true) { // jest blad
					
					int edictTranslateInfoGroupDiffCounter = 0;
					
					for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
						
						if (currentFoundPolishJapaneseEntry.getParseAdditionalInfoList().contains(ParseAdditionalInfo.EDICT_TRANSLATE_INFO_GROUP_DIFF) == true) {
							edictTranslateInfoGroupDiffCounter++;
						}						
					}
					
					if (edictTranslateInfoGroupDiffCounter != foundPolishJapaneseEntryGroupList.size()) {
						
						validateResult = false;
						
						StringBuffer errorGroupIds = new StringBuffer();
						
						for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
							errorGroupIds.append(currentFoundPolishJapaneseEntry.getId() + ".");								
						}
						
						if (alreadyValidateErrorResultGroupIds.contains(errorGroupIds.toString()) == false) {
							
							alreadyValidateErrorResultGroupIds.add(errorGroupIds.toString());
							
							System.out.println("Błąd walidacji dla: \n");
							
							for (PolishJapaneseEntry currentFoundPolishJapaneseEntry : foundPolishJapaneseEntryGroupList) {
								
								int errorId = currentFoundPolishJapaneseEntry.getId();
								String errorKanji = currentFoundPolishJapaneseEntry.getKanji();
								String errorKana = currentFoundPolishJapaneseEntry.getKanaList().get(0);
								String dictionaryEntryType = currentFoundPolishJapaneseEntry.getDictionaryEntryTypeList().toString();
								String errorTranslate = currentFoundPolishJapaneseEntry.getTranslates().toString();
								String errorInfo = currentFoundPolishJapaneseEntry.getInfo();
								
								System.out.println("id: " + errorId);
								System.out.println("dictionaryEntryType: " + dictionaryEntryType);
								System.out.println("kanji: " + errorKanji);
								System.out.println("kana: " + errorKana);
								System.out.println("translate: " + errorTranslate);
								System.out.println("info: " + errorInfo);
								
								System.out.println("---\n");
							}
						}
						
					}					
				}
			}
		}	
		
		if (validateResult == false) { // jesli jest blad walidacji
			
			System.out.println("Przerwane ze względu na błędy walidacji...");
			
			return;
		}
				
		System.out.println("Zapisywanie końcowego słownika...");
		
		//CsvReaderWriter.generateCsv("input/word-end.csv", polishJapaneseEntries, true, true, false);		
	}	
	
	private static boolean isMultiGroup(List<GroupEntry> groupEntryList) {
		
		Set<Integer> uniqueGroupIds = new HashSet<Integer>();
		
		for (GroupEntry groupEntry : groupEntryList) {
			uniqueGroupIds.add(groupEntry.getGroup().getId());
		}
		
		if (uniqueGroupIds.size() == 1) {			
			return false;
			
		} else {
			return true;
		}
	}
	
	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries, 
			String findKanji, String findKana) {
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
						
			String kanji = polishJapaneseEntry.getKanji();
			String kana = polishJapaneseEntry.getKanaList().get(0);
			
			if (kanji == null || kanji.equals("-") == true) {
				kanji = "$$$NULL$$$";
			}

			if (findKanji == null || findKanji.equals("-") == true) {
				findKanji = "$$$NULL$$$";
			}
			
			if (kanji.equals(findKanji) == true && kana.equals(findKana) == true) {
				return polishJapaneseEntry;
			}
		}
		
		return null;
	}
}
