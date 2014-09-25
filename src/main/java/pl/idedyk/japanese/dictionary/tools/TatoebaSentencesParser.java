package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.api.dto.TatoebaSentence;

import com.csvreader.CsvReader;

public class TatoebaSentencesParser {
	
	private String tatoebaSentencesDir;
	
	private Map<String, TatoebaSentence> tatoebaSentenceMap;
	private Map<String, List<TatoebaSentence>> linksMap;
	private Map<String, List<TatoebaSentence>> keyWordsAndSentenceMap;
	
	public TatoebaSentencesParser(String tatoebaSentencesDir) {
		this.tatoebaSentencesDir = tatoebaSentencesDir;
	}
	
	public void parse() throws IOException {
		
		// parsowanie pliku sentences.csv
		tatoebaSentenceMap = new TreeMap<String, TatoebaSentence>();
		
		File sentencesFile = new File(tatoebaSentencesDir, "sentences.csv");
		
		CsvReader csvReader = new CsvReader(new FileReader(sentencesFile), '\t');
		
		while (csvReader.readRecord()) {
			
			String id = csvReader.get(0);
			String lang = csvReader.get(1);
			String sentence = csvReader.get(2);
			
			if (lang.equals("pol") == false && lang.equals("jpn") == false) {
				continue;
			}
			
			TatoebaSentence tatoebaSentence = new TatoebaSentence();
			
			tatoebaSentence.setId(id);
			tatoebaSentence.setLang(lang);
			tatoebaSentence.setSentence(sentence);
			
			tatoebaSentenceMap.put(id, tatoebaSentence);			
		}
		
		csvReader.close();
		
		// parsowanie pliku links.csv
		linksMap = new TreeMap<String, List<TatoebaSentence>>();
		
		File linksFile = new File(tatoebaSentencesDir, "links.csv");
		
		csvReader = new CsvReader(new FileReader(linksFile), '\t');
		
		while (csvReader.readRecord()) {
			
			String groupId = csvReader.get(0);
			String sentenceId = csvReader.get(1);
			
			TatoebaSentence tatoebaSentence = tatoebaSentenceMap.get(sentenceId);
			
			if (tatoebaSentence == null) {
				continue;
			}
			
			List<TatoebaSentence> groupList = linksMap.get(groupId);
			
			if (groupList == null) {
				groupList = new ArrayList<TatoebaSentence>();
				
				linksMap.put(groupId, groupList);
			}
			
			tatoebaSentence.setGroupId(groupId);
			
			groupList.add(tatoebaSentence);
		}
		
		csvReader.close();
		
		// filtrowanie grup w poszukiwaniu grup z polskim i japonskim jezykiem
		Iterator<String> groupIterator = linksMap.keySet().iterator();
		
		Map<String, List<TatoebaSentence>> filteredLinksMap = new TreeMap<String, List<TatoebaSentence>>();
		Map<String, TatoebaSentence> filteredTatoebaSentenceMap = new TreeMap<String, TatoebaSentence>();
		
		while (groupIterator.hasNext() == true) {			
			String groupId = groupIterator.next();
			
			List<TatoebaSentence> tatoebaSentenceList = linksMap.get(groupId);
			
			boolean containtPolishSentece = false;
			boolean containtJapaneseSentece = false;
			
			for (TatoebaSentence tatoebaSentence : tatoebaSentenceList) {

				if (tatoebaSentence.getLang().equals("pol") == true) {
					containtPolishSentece = true;
				}
				
				if (tatoebaSentence.getLang().equals("jpn") == true) {
					containtJapaneseSentece = true;
				}
				
				if (containtPolishSentece == true && containtJapaneseSentece == true) {
					break;
				}				
			}
			
			if (containtPolishSentece == true && containtJapaneseSentece == true) {
				filteredLinksMap.put(groupId, tatoebaSentenceList);
				
				for (TatoebaSentence tatoebaSentence : tatoebaSentenceList) {
					filteredTatoebaSentenceMap.put(tatoebaSentence.getId(), tatoebaSentence);
				}
			}
		}		
		
		linksMap = filteredLinksMap;
		tatoebaSentenceMap = filteredTatoebaSentenceMap;
		
		// parsowanie pliku jpn_indices.csv
		File jpnIndicesFile = new File(tatoebaSentencesDir, "jpn_indices.csv");
		
		csvReader = new CsvReader(new FileReader(jpnIndicesFile), '\t');
		
		while (csvReader.readRecord()) {
			
			String sentenceId = csvReader.get(0);			
			String japaneseIndices = csvReader.get(2);
			
			TatoebaSentence tatoebaSentence = tatoebaSentenceMap.get(sentenceId);
			
			if (tatoebaSentence == null) {
				continue;
			}
						
			tatoebaSentence.setSentenceToken(tokenWord(japaneseIndices));
		}
		
		csvReader.close();
		
		// cache'owanie po slowach
		keyWordsAndSentenceMap = new TreeMap<String, List<TatoebaSentence>>();
		
		Collection<TatoebaSentence> tatoebaSentenceMapValues = tatoebaSentenceMap.values();
		
		for (TatoebaSentence tatoebaSentence : tatoebaSentenceMapValues) {
			
			
			
		}		
	}
	
	public List<String> tokenWord(String word) {
		
		if (word == null) {
			return null;
		}
		
		List<String> result = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(word, " \t\n\r\f.,:;()|[]\"'?!-–{}");
		
		while (st.hasMoreTokens()) {
			result.add(st.nextToken());
		}
		
		return result;
	}

	
	public static void main(String[] args) throws Exception {
		
		TatoebaSentencesParser tatoebaSentencesParser = new TatoebaSentencesParser("../JapaneseDictionary_additional/tatoeba");
		
		tatoebaSentencesParser.parse();
		
		
		
	}	
}
