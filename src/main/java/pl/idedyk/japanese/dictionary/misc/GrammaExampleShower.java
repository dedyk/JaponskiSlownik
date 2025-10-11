package pl.idedyk.japanese.dictionary.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import pl.idedyk.japanese.dictionary.api.dto.DictionaryEntryType;
import pl.idedyk.japanese.dictionary.api.example.ExampleManager;
import pl.idedyk.japanese.dictionary.api.example.dto.ExampleGroupTypeElements;
import pl.idedyk.japanese.dictionary.api.example.dto.ExampleRequest;
import pl.idedyk.japanese.dictionary.api.example.dto.ExampleResult;
import pl.idedyk.japanese.dictionary.api.gramma.GrammaConjugaterManager;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateGroupTypeElements;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateRequest;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateResult;
import pl.idedyk.japanese.dictionary.api.gramma.dto.GrammaFormConjugateResultType;
import pl.idedyk.japanese.dictionary.api.keigo.KeigoHelper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

public class GrammaExampleShower {

	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.out.println("Brak identyfikatora słowa");
			return;
		}
		
		// pobranie identyfikatora slowa
		Integer polishJapaneseId;
		
		try {
			polishJapaneseId = Integer.parseInt(args[0]);
			
		} catch (NumberFormatException e) {
			System.out.println("Niepoprawny identyfikator słowa: " + args[0]);
			return;
		}
		
		// pobranie typu slowa
		DictionaryEntryType forceDictionaryEntryType = null;
		
		if (args.length > 1) {
			forceDictionaryEntryType = DictionaryEntryType.valueOf(args[1]);
		}
		
		// pomocnik
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();

		// lista slow w starym formacie
		List<PolishJapaneseEntry> polishJapaneseEntriesList = dictionary2Helper.getOldPolishJapaneseEntriesList();

		// odnalezienie slowa o podanym identyfikatorze
		Optional<PolishJapaneseEntry> polishJapaneseEntryOptional = polishJapaneseEntriesList.stream().filter(p -> p.getId() == polishJapaneseId).findFirst();
		
		if (polishJapaneseEntryOptional.isPresent() == false) {
			System.out.println("Nie znaleziono słowa o podanym identyfikatorze: " + polishJapaneseId);
			return;
		}
		
		// mala poprawka kanji
		String kanji = polishJapaneseEntryOptional.get().getKanji();
		
		if (kanji.equals("-") == true) {
			polishJapaneseEntryOptional.get().setKanji(null);
		}
		
		// pomocnicy do wyliczania
		KeigoHelper keigoHelper = new KeigoHelper();
		
		Map<GrammaFormConjugateResultType, GrammaFormConjugateResult> grammaFormCache = new HashMap<GrammaFormConjugateResultType, GrammaFormConjugateResult>();

		// wyliczenie form gramatycznych
		List<GrammaFormConjugateGroupTypeElements> grammaConjufateResult = GrammaConjugaterManager.getGrammaConjufateResult(keigoHelper, new GrammaFormConjugateRequest(polishJapaneseEntryOptional.get()), grammaFormCache, forceDictionaryEntryType, true);
		
		// wypisanie na ekranie
		System.out.println("+++ Formy gramatyczne +++\n");
		
		for (GrammaFormConjugateGroupTypeElements currentGrammaForm : grammaConjufateResult) {
			
			// tytul formy
			System.out.println(currentGrammaForm.getGrammaFormConjugateGroupType().getName() + " (" + currentGrammaForm.getGrammaFormConjugateGroupType().name() + "), pokazać: " + currentGrammaForm.getGrammaFormConjugateGroupType().isShow());
			
			// info
			if (StringUtils.isBlank(currentGrammaForm.getGrammaFormConjugateGroupType().getInfo()) == false) {
				System.out.println(currentGrammaForm.getGrammaFormConjugateGroupType().getInfo());
			}
			System.out.println();
			
			// dalszy podzial
			List<GrammaFormConjugateResult> grammaFormConjugateResults = currentGrammaForm.getGrammaFormConjugateResults();
			
			for (GrammaFormConjugateResult currentGrammaFormSingleResult : grammaFormConjugateResults) {
				
				// wypisanie pojedynczego wpisu
				showGrammaFormSingleResult(currentGrammaFormSingleResult, true, 1);				
			}
			
			System.out.println("-------------\n");
		}
		
		// wyliczenie przykladow
		List<ExampleGroupTypeElements> examples = ExampleManager.getExamples(keigoHelper, new ExampleRequest(polishJapaneseEntryOptional.get()), grammaFormCache, forceDictionaryEntryType, true);
		
		// wypisanie na ekranie
		System.out.println("+++ Przykłady +++\n");
		
		for (ExampleGroupTypeElements currentExample : examples) {
			
			// tytul przykladu
			System.out.println(currentExample.getExampleGroupType().getName() + " (" + currentExample.getExampleGroupType().name() + ")");
			
			// info
			if (StringUtils.isBlank(currentExample.getExampleGroupType().getInfo()) == false) {
				System.out.println(currentExample.getExampleGroupType().getInfo());
			}
			System.out.println();
				
			// dalszy podzial
			List<ExampleResult> exampleResults = currentExample.getExampleResults();
			
			for (ExampleResult currentExampleSingleResult : exampleResults) {
				
				// wypisanie pojedynczego wpisu
				showExampleSingleResult(currentExampleSingleResult, 1);
			}
			
			System.out.println("-------------\n");
		}
	}

	private static void showGrammaFormSingleResult(GrammaFormConjugateResult currentGrammaFormSingleResult, boolean showTitle, int level) {
		
		// wygenerowanie odstepu z poziomem
		String levelPrefix = "";
		String levelPrefix2 = "";
		
		for (int i = 0; i < level; ++i) {
			levelPrefix += "\t";
		}
		levelPrefix2 = levelPrefix + "\t";
		
		// tytul
		if (showTitle == true) {
			System.out.println(levelPrefix + currentGrammaFormSingleResult.getResultType().getName() + " (" + currentGrammaFormSingleResult.getResultType().name() + "), pokazać: " + currentGrammaFormSingleResult.getResultType().isShow());
			
			// info do tytulu
			if (StringUtils.isBlank(currentGrammaFormSingleResult.getResultType().getInfo()) == false) {
				System.out.println(levelPrefix + currentGrammaFormSingleResult.getResultType().getInfo());
			}
		}
		
		// kanji
		System.out.println(levelPrefix2 + (StringUtils.isBlank(currentGrammaFormSingleResult.getPrefixKana()) == false ? (" " + currentGrammaFormSingleResult.getPrefixKana()) : "") + currentGrammaFormSingleResult.getKanji());
		
		// kana
		for (String currentKana : currentGrammaFormSingleResult.getKanaList()) {
			System.out.println(levelPrefix2 + (StringUtils.isBlank(currentGrammaFormSingleResult.getPrefixKana()) == false ? (" " + currentGrammaFormSingleResult.getPrefixKana()) : "") + currentKana);
		}
		
		// romaji
		for (String currentRomaji : currentGrammaFormSingleResult.getRomajiList()) {
			System.out.println(levelPrefix2 + (StringUtils.isBlank(currentGrammaFormSingleResult.getPrefixRomaji()) == false ? (" " + currentGrammaFormSingleResult.getPrefixRomaji()) : "") + currentRomaji);
		}
		
		// info do elementu
		if (StringUtils.isBlank(currentGrammaFormSingleResult.getInfo()) == false) {
			System.out.println(levelPrefix2 + currentGrammaFormSingleResult.getInfo());
		}
		
		System.out.println();
		
		// alternatywa
		if (currentGrammaFormSingleResult.getAlternative() != null) {
			showGrammaFormSingleResult(currentGrammaFormSingleResult.getAlternative(), false, level);
		}		
	}

	private static void showExampleSingleResult(ExampleResult currentExampleSingleResult, int level) {
		
		// wygenerowanie odstepu z poziomem
		String levelPrefix = "";
		String levelPrefix2 = "";
		
		for (int i = 0; i < level; ++i) {
			levelPrefix += "\t";
		}
		levelPrefix2 = levelPrefix + "\t";
		
		// info
		if (StringUtils.isBlank(currentExampleSingleResult.getInfo()) == false) {
			System.out.println(levelPrefix2 + currentExampleSingleResult.getInfo());
		}
		
		// kanji
		System.out.println(levelPrefix2 + (StringUtils.isBlank(currentExampleSingleResult.getPrefixKana()) == false ? (" " + currentExampleSingleResult.getPrefixKana()) : "") + currentExampleSingleResult.getKanji());
		
		// kana
		for (String currentKana : currentExampleSingleResult.getKanaList()) {
			System.out.println(levelPrefix2 + (StringUtils.isBlank(currentExampleSingleResult.getPrefixKana()) == false ? (" " + currentExampleSingleResult.getPrefixKana()) : "") + currentKana);
		}
		
		// romaji
		for (String currentRomaji : currentExampleSingleResult.getRomajiList()) {
			System.out.println(levelPrefix2 + (StringUtils.isBlank(currentExampleSingleResult.getPrefixRomaji()) == false ? (" " + currentExampleSingleResult.getPrefixRomaji()) : "") + currentRomaji);
		}
		
		System.out.println();
		
		// alternatywa
		if (currentExampleSingleResult.getAlternative() != null) {
			showExampleSingleResult(currentExampleSingleResult.getAlternative(), level);
		}		
	}
}
