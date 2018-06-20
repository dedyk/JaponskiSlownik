package pl.idedyk.japanese.dictionary.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.GroupEnum;
import pl.idedyk.japanese.dictionary.dto.KanjiDic2EntryForDictionary;
import pl.idedyk.japanese.dictionary.dto.KanjiEntryForDictionary;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;
import pl.idedyk.japanese.dictionary.tools.KanjiDic2Reader;

public class GenerateJouyouKanjiGroup {

	public static void main(String[] args) throws Exception {
		
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
	}
}
