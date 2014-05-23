package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;


public class Test2 {

	public static void main(String[] args) throws Exception {

		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		List<ResultItem> resultItemList = new ArrayList<ResultItem>();

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			resultItemList.add(new ResultItem(createDictionaryEntry(polishJapaneseEntry)));
		}

		FindWordRequest findWordRequest = new FindWordRequest();

		findWordRequest.word = "sdfdsfsdfsdfdsfsdf";

		// ResultItemComparator resultItemComparator = new ResultItemComparator(findWordRequest);

		//Collections.sort(resultItemList, resultItemComparator);
		*/
		
		/*
		if (lhsKanji != null && lhsKanji.endsWith(findWord) == true && rhsKanji != null && rhsKanji.equals(findWord) == false) {
			return -1;
		} else if (lhsKanji != null && lhsKanji.endsWith(findWord) == false && rhsKanji != null && rhsKanji.equals(findWord) == true) {
			return 1;
		}
		 */
		
		/*
		for (ResultItem resultItem1 : resultItemList) {

			for (ResultItem resultItem2 : resultItemList) {

				String lhsKanji = resultItem1.getKanji();
				String rhsKanji = resultItem2.getKanji();
				
				String findWord = rhsKanji;

				int firstCompare = 0;
				int secondCompare = 0;

				if (lhsKanji != null && lhsKanji.endsWith(findWord) == true && rhsKanji != null && rhsKanji.equals(findWord) == false) {
					firstCompare = -1;
				} 

				if (lhsKanji != null && lhsKanji.endsWith(findWord) == false && rhsKanji != null && rhsKanji.equals(findWord) == true) {
					secondCompare = -1;
				}

				if (firstCompare == -1 && secondCompare == -1) {
					throw new RuntimeException(resultItem1.getKanji() + " - " + resultItem2.getKanji());
				}
			}
		}
		*/

		/*
		for (ResultItem resultItem1 : resultItemList) {

			for (ResultItem resultItem2 : resultItemList) {

				int firstCompare = resultItemComparator.compare(resultItem1, resultItem2);
				int secondCompare = resultItemComparator.compare(resultItem2, resultItem1);

				if (firstCompare < 0) {

					if (secondCompare <= 0) {
						throw new RuntimeException(resultItem1.getKanji() + " - " + resultItem2.getKanji());
					}

				} else if (firstCompare == 0) {

					if (secondCompare != 0) {
						throw new RuntimeException(resultItem1.getKanji() + " - " + resultItem2.getKanji());
					}

				} else if (firstCompare > 0) {

					if (secondCompare >= 0) {
						throw new RuntimeException(resultItem1.getKanji() + " - " + resultItem2.getKanji());
					}					
				}
			}
		}
		 */
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile("../JapaneseDictionary_additional/kradfile");
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2("../JapaneseDictionary_additional/kanjidic2.xml", kradFileMap);

		Collection<KanjiDic2Entry> readKanjiDic2Values = readKanjiDic2.values();
		
		Map<String, List<KanjiDic2Entry>> theSameEngMeaning = new TreeMap<String, List<KanjiDic2Entry>>();
		
		for (KanjiDic2Entry kanjiDic2Entry : readKanjiDic2Values) {
			
			List<String> engMeaning = kanjiDic2Entry.getEngMeaning();
			
			Collections.sort(engMeaning);
			
			String key = engMeaning.toString();
			
			List<KanjiDic2Entry> list = theSameEngMeaning.get(key);
			
			if (list == null) {
				list = new ArrayList<KanjiDic2Entry>();
			}
			
			list.add(kanjiDic2Entry);
			
			theSameEngMeaning.put(key, list);			
		}
		
		Iterator<String> theSameEngMeaningIterator = theSameEngMeaning.keySet().iterator();
		
		while (theSameEngMeaningIterator.hasNext() == true) {
			
			String key = theSameEngMeaningIterator.next();
			
			List<KanjiDic2Entry> list = theSameEngMeaning.get(key);
			
			if (list.size() > 1) {
				
				StringBuffer listKanji = new StringBuffer();
				
				for (KanjiDic2Entry kanjiDic2Entry : list) {
					listKanji.append(kanjiDic2Entry.getKanji() + " ");
				}
				
				System.out.println(key + " - " + listKanji.toString());
			}
			
		}
	}

	/*
	private static DictionaryEntry createDictionaryEntry(PolishJapaneseEntry polishJapaneseEntry) {

		DictionaryEntry dictionaryEntry = new DictionaryEntry();

		dictionaryEntry.setId(polishJapaneseEntry.getId());
		dictionaryEntry.setDictionaryEntryTypeList(polishJapaneseEntry.getDictionaryEntryTypeList());
		dictionaryEntry.setAttributeList(polishJapaneseEntry.getAttributeList());
		dictionaryEntry.setGroups(polishJapaneseEntry.getGroups());
		dictionaryEntry.setPrefixKana(polishJapaneseEntry.getPrefixKana());
		dictionaryEntry.setKanji(polishJapaneseEntry.getKanji());
		dictionaryEntry.setKanaList(polishJapaneseEntry.getKanaList());
		dictionaryEntry.setPrefixRomaji(polishJapaneseEntry.getPrefixRomaji());
		dictionaryEntry.setRomajiList(polishJapaneseEntry.getRomajiList());
		dictionaryEntry.setTranslates(polishJapaneseEntry.getTranslates());

		return dictionaryEntry;
	}	
	*/
	
	/*
	private static class ResultItemComparator implements Comparator<ResultItem> {

		private FindWordRequest findWordRequest;

		private KanaHelper kanaHelper;

		private Map<String, KanaEntry> kanaCache;

		public ResultItemComparator(FindWordRequest findWordRequest) {
			this.findWordRequest = findWordRequest;

			this.kanaHelper = new KanaHelper();
			kanaCache = kanaHelper.getKanaCache();
		}

		@Override
		public int compare(ResultItem lhs, ResultItem rhs) {			

			String findWord = findWordRequest.word;

			String lhsKanji = lhs.getKanji();
			String rhsKanji = rhs.getKanji();

			if (lhsKanji != null && lhsKanji.endsWith(findWord) == true && rhsKanji != null && rhsKanji.equals(findWord) == false) {
				return -1;
			} else if (lhsKanji != null && lhsKanji.endsWith(findWord) == false && rhsKanji != null && rhsKanji.equals(findWord) == true) {
				return 1;
			}

			List<String> lhsKanaList = lhs.getKanaList();
			List<String> rhsKanaList = rhs.getKanaList();

			if (lhsKanaList.contains(findWord) == true && rhsKanaList.contains(findWord) == false) {
				return -1;
			} else if (lhsKanaList.contains(findWord) == false && rhsKanaList.contains(findWord) == true) {
				return 1;
			}

			List<String> lhsRomajiList = lhs.getRomajiList();

			boolean isInLhsRomajiList = false;

			for (String currentLhsRomajiList : lhsRomajiList) {
				if (currentLhsRomajiList.equalsIgnoreCase(findWord) == true) {
					isInLhsRomajiList = true;
				}
			}

			List<String> rhsRomajiList = rhs.getRomajiList();

			boolean isInRhsRomajiList = false;

			for (String currentRhsRomajiList : rhsRomajiList) {
				if (currentRhsRomajiList.equalsIgnoreCase(findWord) == true) {
					isInRhsRomajiList = true;
				}
			}

			if (isInLhsRomajiList == true && isInRhsRomajiList == false) {
				return -1;
			} else if (isInLhsRomajiList == false && isInRhsRomajiList == true) {
				return 1;
			}

			List<String> lhsTranslates = lhs.getTranslates();

			boolean islhsTranslates = false;

			for (String currentLhsTranslates : lhsTranslates) {
				if (Utils.removePolishChars(currentLhsTranslates).equalsIgnoreCase(findWord) == true) {
					islhsTranslates = true;
				}
			}

			List<String> rhsTranslates = rhs.getTranslates();

			boolean isRhsTranslates = false;

			for (String currentRhsTranslates : rhsTranslates) {
				if (Utils.removePolishChars(currentRhsTranslates).equalsIgnoreCase(findWord) == true) {
					isRhsTranslates = true;
				}
			}

			if (islhsTranslates == true && isRhsTranslates == false) {
				return -1;
			} else if (islhsTranslates == false && isRhsTranslates == true) {
				return 1;
			}

			int maxKanaListArraySize = lhsKanaList.size();

			if (maxKanaListArraySize < rhsKanaList.size()) {
				maxKanaListArraySize = rhsKanaList.size();
			}

			for (int idx = 0; idx < maxKanaListArraySize; ++idx) {
				int compareResult = compare(lhsKanaList, rhsKanaList, idx);

				if (compareResult != 0) {
					return compareResult;
				}
			}

			return 0;
		}

		private int compare(List<String> lhsKanaList, List<String> rhsKanaList, int idx) {

			String lhsString = getString(lhsKanaList, idx);

			String rhsString = getString(rhsKanaList, idx);

			if (lhsString == null && rhsString == null) {
				return 0;
			} else if (lhsString != null && rhsString == null) {
				return -1;
			} else if (lhsString == null && rhsString != null) {
				return 1;
			} else {
				String lhsRomaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(
						lhsString, kanaCache, true));
				String rhsRomaji = kanaHelper.createRomajiString(kanaHelper.convertKanaStringIntoKanaWord(
						rhsString, kanaCache, true));

				return lhsRomaji.compareToIgnoreCase(rhsRomaji);
			}
		}

		private String getString(List<String> kanaList, int kanaListIdx) {
			if (kanaListIdx < kanaList.size()) {
				return kanaList.get(kanaListIdx);
			} else {
				return null;
			}
		}
	}
	*/
}
