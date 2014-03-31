package pl.idedyk.japanese.dictionary.test;

//import org.apache.lucene.analysis.core.SimpleAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.TextField;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.RAMDirectory;
//import org.apache.lucene.util.Version;

public class LuceneTest {

	public static void main(String[] args) throws Exception {
		
		/*
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");
		
		SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
		
		Directory index = new RAMDirectory();
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
			addPolishJapaneseEntry(w, polishJapaneseEntry);
		}

		w.close();
		
		// 2. query
		//Query q = new QueryParser(Version.LUCENE_47, "<default field>", analyzer).parse("赤*");
		Query q = new MultiFieldQueryParser(Version.LUCENE_47, new String [] { "kanji", "kana", "translate", "info" }, analyzer).parse("イ*");

		// 3. search
		int hitsPerPage = 100;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
						
			System.out.println((i + 1) + ". " + d.get("kanji") + "\t" + Arrays.toString(d.getValues("kana")) + "\t" + Arrays.toString(d.getValues("translate")) + "\t" + d.get("info"));
		}

		reader.close();
		*/
	}
	
	/*
	private static void addPolishJapaneseEntry(IndexWriter w, PolishJapaneseEntry polishJapaneseEntry) throws Exception {
		
		/*
		Document doc = new Document();
		
		String kanji = polishJapaneseEntry.getKanji();
		
		doc.add(new TextField("kanji", kanji, Field.Store.YES));

		List<String> kanaList = polishJapaneseEntry.getKanaList();
		
		for (String currentKana : kanaList) {
			doc.add(new TextField("kana", currentKana, Field.Store.YES));
		}
		
		List<String> translates = polishJapaneseEntry.getTranslates();
		
		for (String currentTranslate : translates) {
			doc.add(new TextField("translate", currentTranslate, Field.Store.YES));
		}
		
		String info = polishJapaneseEntry.getInfo();
		
		doc.add(new TextField("info", info, Field.Store.YES));

		// use a string field for isbn because we don't want it tokenized
		//doc.add(new StringField("isbn", isbn, Field.Store.YES));
		
		w.addDocument(doc);	
	}
	*/
}
