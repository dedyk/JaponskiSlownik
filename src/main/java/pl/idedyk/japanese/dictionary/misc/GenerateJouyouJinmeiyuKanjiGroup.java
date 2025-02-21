package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.KanjiCharacterInfo;
import pl.idedyk.japanese.dictionary2.kanjidic2.xsd.MiscInfo;

public class GenerateJouyouJinmeiyuKanjiGroup {

	public static void main(String[] args) throws Exception {
		
		/*
		// stara implementacja 
		 
		String sourceKanjiName = "input/kanji.csv";
		
		String sourceKanjiDic2FileName = "../JapaneseDictionary_additional/kanjidic2.xml";
		String sourceKradFileName = "../JapaneseDictionary_additional/kradfile";
		
		String destinationFileName = "input/kanji-new.csv";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);		
		Map<String, KanjiDic2EntryForDictionary> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntryForDictionary> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2, false);
		
		for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
			
			List<GroupEnum> kanjiEntryGroupEnumList = kanjiEntry.getGroups();
			
			//
			
			kanjiEntryGroupEnumList.removeAll(Arrays.asList(new GroupEnum[] { 
					GroupEnum.JOUYOU1, GroupEnum.JOUYOU2, GroupEnum.JOUYOU3, GroupEnum.JOUYOU4, GroupEnum.JOUYOU5,
					GroupEnum.JOUYOU6, GroupEnum.JOUYOU7, GroupEnum.JOUYOU8, GroupEnum.JOUYOU9, GroupEnum.JOUYOU10 }));								
			
			String kanji = kanjiEntry.getKanji();
			
			KanjiDic2EntryForDictionary kanjiDic2Entry = readKanjiDic2.get(kanji);
			
			if (kanjiDic2Entry != null) {
				
				Integer jouyouGrade = kanjiDic2Entry.getJouyouGrade();
				
				if (jouyouGrade != null) {
					
					GroupEnum jouyouGroupEnum = null;
					
					switch (jouyouGrade) {
						
						case 1:
							jouyouGroupEnum = GroupEnum.JOUYOU1;
							
							break;
							
						case 2:
							jouyouGroupEnum = GroupEnum.JOUYOU2;
							
							break;
							
						case 3:
							jouyouGroupEnum = GroupEnum.JOUYOU3;
							
							break;
							
						case 4:
							jouyouGroupEnum = GroupEnum.JOUYOU4;
							
							break;
							
						case 5:
							jouyouGroupEnum = GroupEnum.JOUYOU5;
							
							break;
							
						case 6:
							jouyouGroupEnum = GroupEnum.JOUYOU6;
							
							break;
							
						case 7:
							jouyouGroupEnum = GroupEnum.JOUYOU7;
							
							break;
							
						case 8:
							jouyouGroupEnum = GroupEnum.JOUYOU8;
							
							break;
							
						case 9:
							jouyouGroupEnum = GroupEnum.JOUYOU9;
							
							break;
							
						case 10:
							jouyouGroupEnum = GroupEnum.JOUYOU10;
							
							break;
							
						default:
							throw new RuntimeException("Unknown jouyou grade: " + jouyouGrade);	
					}
					
					kanjiEntryGroupEnumList.add(jouyouGroupEnum);
				}
			
			}			
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));

		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries, false, null);
		*/

		/*
		// stara implementacja nr. 2
		
		// pliki wejsciowe i wyjsciowe
		String sourceKanjiName = "input/kanji.csv";
		String destinationFileName = "input/kanji-new.csv";
		
		String joyoFileName = "../JapaneseDictionary_additional/joyo/joyo.csv";
		String sourceKanjiDic2FileName = "../JapaneseDictionary_additional/kanjidic2.xml";
		String sourceKradFileName = "../JapaneseDictionary_additional/kradfile";
		
		// wczytanie danych
		Map<String, GroupEnum> joyoFile = readJoyoFile(joyoFileName);
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);		
		Map<String, KanjiDic2EntryForDictionary> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		// chodzimy po wszystkich grupach
		List<KanjiEntryForDictionary> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2, false);

		for (KanjiEntryForDictionary kanjiEntry : kanjiEntries) {
			
			List<GroupEnum> kanjiEntryGroupEnumList = kanjiEntry.getGroups();
			
			// usuwamy stare grupy (jesli sa jakies)
			kanjiEntryGroupEnumList.removeAll(Arrays.asList(new GroupEnum[] { 
					GroupEnum.JOUYOU1, GroupEnum.JOUYOU2, GroupEnum.JOUYOU3, GroupEnum.JOUYOU4, GroupEnum.JOUYOU5,
					GroupEnum.JOUYOU6 / *, GroupEnum.JOUYOU7, GroupEnum.JOUYOU8, GroupEnum.JOUYOU9, GroupEnum.JOUYOU10 * / }));
			
			// szukamy, czy dany znak nalezy do Joyo
			GroupEnum groupEnum = joyoFile.get(kanjiEntry.getKanji());
			
			if (groupEnum != null) {
				kanjiEntryGroupEnumList.add(groupEnum);
			}
		}
		
		// zapis slownika kanji
		FileOutputStream outputStream = new FileOutputStream(new File(destinationFileName));

		CsvReaderWriter.generateKanjiCsv(outputStream, kanjiEntries, false, null);
		*/
		
		// pomocnik do znakow
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();
		
		// wczytanie starej bazy danych znakow
		List<KanjiEntryForDictionary> oldKanjiPolishDictionaryList = kanji2Helper.getOldKanjiPolishDictionaryList();
		
		// chodzimy po wszystkich grupach
		for (KanjiEntryForDictionary kanjiEntryForDictionary : oldKanjiPolishDictionaryList) {
			
			List<GroupEnum> kanjiEntryGroupEnumList = kanjiEntryForDictionary.getGroups();
			
			// usuwamy stare grupy (jesli sa jakies)
			kanjiEntryGroupEnumList.removeAll(Arrays.asList(new GroupEnum[] { 
					GroupEnum.JOUYOU1, GroupEnum.JOUYOU2, GroupEnum.JOUYOU3, GroupEnum.JOUYOU4, GroupEnum.JOUYOU5,
					GroupEnum.JOUYOU6, GroupEnum.JOUYOUS, GroupEnum.JINMEIYOU, GroupEnum.JINMEIYOU_JOUYOU /*, GroupEnum.JOUYOU7, GroupEnum.JOUYOU8, GroupEnum.JOUYOU9, GroupEnum.JOUYOU10 */ }));
			
			// szukamy znaku w slowniku w nowym formacie
			KanjiCharacterInfo kanjiFromKanjidic2 = kanji2Helper.getKanjiFromKanjidic2(kanjiEntryForDictionary.getKanji());
			
			if (kanjiFromKanjidic2 == null) {
				continue;
			}
			
			// pobieramy misc			
			MiscInfo misc = kanjiFromKanjidic2.getMisc();
			
			if (misc != null) {
				Integer grade = misc.getGrade();
				
				// pobieramy grupe znakow kanji
				GroupEnum groupEnum = getGroupEnum(grade);
				
				if (groupEnum != null) {
					kanjiEntryGroupEnumList.add(groupEnum);
				}
			}
		}
		
		// zapisanie slownika w starej postaci
		FileOutputStream outputStream = new FileOutputStream(new File("input/kanji-wynik.csv"));
		
		CsvReaderWriter.generateKanjiCsv(outputStream, oldKanjiPolishDictionaryList, false, null);
	}
	
	private static GroupEnum getGroupEnum(Integer grade) {
		
		if (grade == null) {
			return null;
		}
		
//		The kanji grade level. 1 through 6 indicates a Kyouiku kanji
//		and the grade in which the kanji is taught in Japanese schools. 
//		8 indicates it is one of the remaining Jouyou Kanji to be learned 
//		in junior high school. 9 indicates it is a Jinmeiyou (for use 
//		in names) kanji which in addition  to the Jouyou kanji are approved 
//		for use in family name registers and other official documents. 10
//		also indicates a Jinmeiyou kanji which is a variant of a
//		Jouyou kanji. [G]

		
		switch (grade) {
		case 1:
			return GroupEnum.JOUYOU1;
		case 2:
			return GroupEnum.JOUYOU2;
		case 3:
			return GroupEnum.JOUYOU3;
		case 4:
			return GroupEnum.JOUYOU4;
		case 5:
			return GroupEnum.JOUYOU5;
		case 6:
			return GroupEnum.JOUYOU6;
		case 8:
			return GroupEnum.JOUYOUS;
		case 9:
			return GroupEnum.JINMEIYOU;
		case 10:
			return GroupEnum.JINMEIYOU_JOUYOU;
		default:
			throw new RuntimeException(); // nieznana wartosc		
		}
	}
	
	/*
	private static Map<String, GroupEnum> readJoyoFile(String joyoFileName) throws IOException {
		
		Map<String, GroupEnum> result = new TreeMap<>();
		
		CsvReader csvReader = new CsvReader(new FileReader(joyoFileName), ',');

		csvReader.readRecord(); // pierwszy rekord to naglowek
		
		while (csvReader.readRecord()) {
			String kanji = csvReader.get(1);
			String grade = csvReader.get(5);
			
			// ustalenie grupy JOYO
			GroupEnum joyoGroup = GroupEnum.valueOf("JOUYOU" + grade);

			result.put(kanji, joyoGroup);
		}
		
		csvReader.close();
		
		return result;
	}
	*/
}
