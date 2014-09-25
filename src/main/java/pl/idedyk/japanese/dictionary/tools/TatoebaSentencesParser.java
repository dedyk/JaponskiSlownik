package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

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
				
				Collections.sort(tatoebaSentenceList, new Comparator<TatoebaSentence>() {

					@Override
					public int compare(TatoebaSentence o1, TatoebaSentence o2) {
						
						int compareTo = o2.getLang().compareTo(o1.getLang());
						
						if (compareTo != 0) {
							return compareTo;
						}
						
						return o1.getSentence().compareTo(o2.getSentence());
					}
				});
				
				filteredLinksMap.put(groupId, tatoebaSentenceList);
			}
		}		
		
		linksMap = filteredLinksMap;
		
		Map<String, TatoebaSentence> filteredTatoebaSentenceMap = new TreeMap<String, TatoebaSentence>();
		
		for (List<TatoebaSentence> currentTatoebeSentece : linksMap.values()) {
			
			for (TatoebaSentence tatoebaSentence : currentTatoebeSentece) {
				filteredTatoebaSentenceMap.put(tatoebaSentence.getId(), tatoebaSentence);
			}	
		}
		
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
			
			List<String> sentenceToken = tatoebaSentence.getSentenceToken();
			
			if (sentenceToken == null || sentenceToken.size() == 0) {
				continue;
			}
			
			for (String currentToken : sentenceToken) {
				
				List<TatoebaSentence> tatoebaSentenceListForToken = keyWordsAndSentenceMap.get(currentToken);
				
				if (tatoebaSentenceListForToken == null) {
					tatoebaSentenceListForToken = new ArrayList<TatoebaSentence>();
					
					keyWordsAndSentenceMap.put(currentToken, tatoebaSentenceListForToken);
				}
				
				if (tatoebaSentenceListForToken.contains(tatoebaSentence) == false) {					
					tatoebaSentenceListForToken.add(tatoebaSentence);
				}				
			}			
		}
	}
	
	private List<String> tokenWord(String word) {
		
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
	
	public List<List<TatoebaSentence>> getSentenceExamples(String word) {
		
		List<TatoebaSentence> tatoebeSentence = keyWordsAndSentenceMap.get(word);
		
		if (tatoebeSentence == null || tatoebeSentence.size() == 0) {
			return null;
		}
		
		List<List<TatoebaSentence>> result = new ArrayList<List<TatoebaSentence>>();
		
		Set<String> uniqueGroupIds = new TreeSet<String>();
		
		for (TatoebaSentence tatoebaSentence : tatoebeSentence) {
			
			String groupId = tatoebaSentence.getGroupId();
			
			if (uniqueGroupIds.contains(groupId) == false && linksMap.containsKey(groupId) == true) {
				
				uniqueGroupIds.add(groupId);
				
				List<TatoebaSentence> tatoebeSentenceForGroup = linksMap.get(groupId);
								
				result.add(tatoebeSentenceForGroup);
			}			
		}
		
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		
		TatoebaSentencesParser tatoebaSentencesParser = new TatoebaSentencesParser("../JapaneseDictionary_additional/tatoeba");
		
		tatoebaSentencesParser.parse();
		
		List<List<TatoebaSentence>> sentenceExamples = tatoebaSentencesParser.getSentenceExamples("食べる");
		
		for (List<TatoebaSentence> currentSentenceGroup : sentenceExamples) {
			
			String groupId = currentSentenceGroup.get(0).getGroupId();
			
			System.out.println("Group id: " + groupId);
			
			for (TatoebaSentence tatoebaSentence : currentSentenceGroup) {
				System.out.println(tatoebaSentence.getSentence());
			}
			
			System.out.println("-------");
		}
	}	
}
