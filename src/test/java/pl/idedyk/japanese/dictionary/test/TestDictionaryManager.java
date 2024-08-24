package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

import pl.idedyk.japanese.dictionary.api.dictionary.DictionaryManagerAbstract;
import pl.idedyk.japanese.dictionary.api.dictionary.IDatabaseConnector;
import pl.idedyk.japanese.dictionary.api.dictionary.Utils;
import pl.idedyk.japanese.dictionary.api.dictionary.dto.WordPowerList;
import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.api.dto.RadicalInfo;
import pl.idedyk.japanese.dictionary.api.dto.TransitiveIntransitivePairWithDictionaryEntry;
import pl.idedyk.japanese.dictionary.api.exception.DictionaryException;
import pl.idedyk.japanese.dictionary.api.keigo.KeigoHelper;
import pl.idedyk.japanese.dictionary.api.tools.KanaHelper;
import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.lucene.LuceneDatabase;
import pl.idedyk.japanese.dictionary2.common.Dictionary2Helper;

public class TestDictionaryManager {
	
	private static final String dbMainDir = "/tmp/a/20240804";

	public static void main(String[] args) throws Exception {
		
		Dictionary2Helper dictionary2Helper = Dictionary2Helper.getOrInit();
		
		List<PolishJapaneseEntry> oldPolishJapaneseEntriesList = dictionary2Helper.getOldPolishJapaneseEntriesList();
		
		LuceneDatabase luceneDatabase = new LuceneDatabase(dbMainDir + "/db-lucene");
		
		luceneDatabase.open();
		
		//
		
		DictionaryManagerTest dictionaryManager = new DictionaryManagerTest(luceneDatabase);
		
		dictionaryManager.getStrokePathsForWord("逬る");
		
		/*
		for (PolishJapaneseEntry polishJapaneseEntry : oldPolishJapaneseEntriesList) {
			
			if (polishJapaneseEntry.isKanjiExists() == true) {
				dictionaryManager.getStrokePathsForWord(polishJapaneseEntry.getKanji());
			}
		}
		*/
		
		
		luceneDatabase.close();


	}
	
	private static class DictionaryManagerTest extends DictionaryManagerAbstract {
		
		private KanaHelper kanaHelper = null;

		public DictionaryManagerTest(IDatabaseConnector databaseConnector) {
			this.databaseConnector = databaseConnector;
		}
		
		@Override
		public KanaHelper getKanaHelper() {
			
			if (kanaHelper == null) {
				try {
					InputStream kanaFileInputStream = new FileInputStream(new File(dbMainDir, "kana.csv"));

					readKanaFile(kanaFileInputStream);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			return kanaHelper;
		}

		@Override
		public KeigoHelper getKeigoHelper() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<TransitiveIntransitivePairWithDictionaryEntry> getTransitiveIntransitivePairsList() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void waitForDatabaseReady() {
			return;
		}

		@Override
		public List<RadicalInfo> getRadicalList() {
			throw new UnsupportedOperationException();
		}

		@Override
		public WordPowerList getWordPowerList() throws DictionaryException {
			throw new UnsupportedOperationException();
		}
		
		private void readKanaFile(InputStream kanaFileInputStream) throws IOException {

			CsvReader csvReader = new CsvReader(new InputStreamReader(kanaFileInputStream), ',');

			Map<String, List<KanjivgEntry>> kanaAndStrokePaths = new HashMap<String, List<KanjivgEntry>>();

			while (csvReader.readRecord()) {

				// int id = Integer.parseInt(csvReader.get(0));

				String kana = csvReader.get(1);
				String strokePath1String = csvReader.get(2);
				String strokePath2String = csvReader.get(3);

				List<KanjivgEntry> strokePaths = new ArrayList<KanjivgEntry>();

				strokePaths.add(new KanjivgEntry(Utils.parseStringIntoList(strokePath1String /* , false */)));

				if (strokePath2String == null || strokePath2String.equals("") == false) {
					strokePaths.add(new KanjivgEntry(Utils.parseStringIntoList(strokePath2String /* , false */)));
				}

				kanaAndStrokePaths.put(kana, strokePaths);
			}

			kanaHelper = new KanaHelper(kanaAndStrokePaths);

			csvReader.close();
		}
	}
}
