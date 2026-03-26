package pl.idedyk.japanese.dictionary.test;

import pl.idedyk.japanese.dictionary2.common.Kanji2Helper;

public class Test11 {

	public static void main(String[] args) throws Exception {
		
		// pobieramy pomocnika Kanji2
		Kanji2Helper kanji2Helper = Kanji2Helper.getOrInit();

		// pobieramy poziom Kanji Kentei dla znaku
		System.out.println("Kanji Kentei: " + kanji2Helper.getKenteiLevel("猫"));
	}
}
