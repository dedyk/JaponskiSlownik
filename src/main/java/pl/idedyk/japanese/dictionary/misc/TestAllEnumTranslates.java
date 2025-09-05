package pl.idedyk.japanese.dictionary.misc;

import java.util.List;

import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.JMdict;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.KanjiInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.ReadingInfo;
import pl.idedyk.japanese.dictionary2.jmdict.xsd.Sense;

public class TestAllEnumTranslates {

	public static void main(String[] args) throws Exception {
		Dictionary2Helper dictionaryHelper = Dictionary2Helper.getOrInit();

		JMdict jmdict = dictionaryHelper.getJMdict();
		List<JMdict.Entry> entryList = jmdict.getEntryList();

		for (JMdict.Entry entry : entryList) {
			System.out.println("Entry id: " + entry.getEntryId());
			
			for (KanjiInfo kanjiInfo : entry.getKanjiInfoList()) {
				Dictionary2Helper.translateToPolishKanjiAdditionalInfoEnum(kanjiInfo.getKanjiAdditionalInfoList());	
			}
			
			for (ReadingInfo readingInfo : entry.getReadingInfoList()) {
				Dictionary2Helper.translateToPolishReadingAdditionalInfoEnum(readingInfo.getReadingAdditionalInfoList());
			}
			
			for (Sense sense : entry.getSenseList()) {				
				Dictionary2Helper.translateToPolishDialectEnumList(sense.getDialectList());
				Dictionary2Helper.translateToPolishFieldEnumList(sense.getFieldList());
				Dictionary2Helper.translateToPolishMiscEnumList(sense.getMiscList());
				Dictionary2Helper.translateToPolishPartOfSpeechEnum(sense.getPartOfSpeechList());
								
				sense.getGlossList().stream().filter(f -> f.getGType() != null).map(m -> m.getGType()).forEach(c -> Dictionary2Helper.translateToPolishGlossType(c));
				
				sense.getGlossList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCode(c));
				sense.getGlossList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCodeWithoutValue(c));
				
				sense.getAdditionalInfoList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCode(c));
				sense.getAdditionalInfoList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCodeWithoutValue(c));
				
				sense.getLanguageSourceList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCode(c));
				sense.getLanguageSourceList().stream().filter(f -> f.getLang() != null).map(m -> m.getLang()).forEach(c -> Dictionary2Helper.translateToPolishLanguageCodeWithoutValue(c));
				sense.getLanguageSourceList().stream().filter(f -> f.getLsWasei() != null).map(m -> m.getLsWasei()).forEach(c -> Dictionary2Helper.translateToPolishLanguageSourceLsWaseiEnum(c));
			}			
		}

	}
}
