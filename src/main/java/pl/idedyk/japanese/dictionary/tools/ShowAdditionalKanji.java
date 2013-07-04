package pl.idedyk.japanese.dictionary.tools;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;

public class ShowAdditionalKanji {

	public static void main(String[] args) throws Exception {
		
		String sourceKradFileName = "../JaponskiSlownik_dodatki/kradfile";
		String sourceKanjiDic2FileName = "../JaponskiSlownik_dodatki/kanjidic2.xml";
		String sourceKanjiName = "input/kanji.csv";
		
		System.out.println("generateKanjiEntries");
		
		System.out.println("generateKanjiEntries: readKradFile");
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		System.out.println("generateKanjiEntries: readKanjiDic2");
		final Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		System.out.println("generateKanjiEntries: parseKanjiEntriesFromCsv");
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		Set<String> kanjiEntriesSet = new HashSet<String>();
		
		for (KanjiEntry kanjiEntry : kanjiEntries) {
			kanjiEntriesSet.add(kanjiEntry.getKanji());
		}
		
		Iterator<String> kradFileMapKeyIterator = kradFileMap.keySet().iterator();
		
		TreeSet<KanjiDic2Entry> result = new TreeSet<KanjiDic2Entry>(new Comparator<KanjiDic2Entry>() {

			@Override
			public int compare(KanjiDic2Entry o1, KanjiDic2Entry o2) {
								
				return o1.getFreq().compareTo(o2.getFreq());
			}
		});
		
		while(kradFileMapKeyIterator.hasNext()) {
			
			String kradFileMapKeyIteratorCurrentKanji = kradFileMapKeyIterator.next();
			
			KanjiDic2Entry kanjiDic2Entry = readKanjiDic2.get(kradFileMapKeyIteratorCurrentKanji);
			
			Integer freq = kanjiDic2Entry.getFreq();
			
			if (freq == null) {
				continue;
			}
			
			if (kanjiEntriesSet.contains(kradFileMapKeyIteratorCurrentKanji) == true) {
				continue;
			}
			
			result.add(kanjiDic2Entry);
		}
		
		Iterator<KanjiDic2Entry> resultIterator = result.iterator();
		
		while (resultIterator.hasNext()) {
			
			KanjiDic2Entry currentKanjiDic2Entry = resultIterator.next();
			
			System.out.println(currentKanjiDic2Entry.getKanji() + " - " + currentKanjiDic2Entry.getFreq());
		}
		
		System.out.println("\nSize: " + result.size());

	}
}
