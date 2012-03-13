package pl.idedyk.japanese.dictionary.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.dto.KanaEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;

public class KanaHelper {
	public static KanaWord convertRomajiIntoHiraganaWord(Map<String, KanaEntry> hiraganaCache, String word) throws JapaneseDictionaryException {
		
		List<KanaEntry> kanaEntries = new ArrayList<KanaEntry>();
		
		String remaingRestChars = null;
		
		String currentRestChars = "";
		
		for (int idx = 0; idx < word.length(); ++idx) {
			String currentChar = String.valueOf(word.charAt(idx));
			
			if (currentChar.equals(" ") == true) {
				continue;
			}
			
			currentRestChars += currentChar.toLowerCase();
			
			if (currentRestChars.length() == 2 && currentRestChars.charAt(0) == currentRestChars.charAt(1) &&
					currentRestChars.charAt(0) != 'n') {
				
				KanaEntry kanaEntry = hiraganaCache.get("ttsu");
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "" + currentRestChars.charAt(1);
				
				continue;				
			}
			
			if (currentRestChars.equals("a") == true ||
					currentRestChars.equals("i") == true ||
					currentRestChars.equals("u") == true ||
					currentRestChars.equals("e") == true ||
					currentRestChars.equals("o") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";
			} else if (currentRestChars.equals("ka") == true ||
					currentRestChars.equals("ki") == true ||
					currentRestChars.equals("ku") == true ||
					currentRestChars.equals("ke") == true ||
					currentRestChars.equals("ko") == true ||
					currentRestChars.equals("kya") == true ||
					currentRestChars.equals("kyu") == true ||
					currentRestChars.equals("kyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("sa") == true ||
					currentRestChars.equals("shi") == true ||
					currentRestChars.equals("sha") == true ||
					currentRestChars.equals("shu") == true ||
					currentRestChars.equals("sho") == true ||
					currentRestChars.equals("su") == true ||
					currentRestChars.equals("se") == true ||
					currentRestChars.equals("so") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ta") == true ||
					currentRestChars.equals("tsu") == true ||
					currentRestChars.equals("te") == true ||
					currentRestChars.equals("to") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("chi") == true ||
					currentRestChars.equals("cha") == true ||
					currentRestChars.equals("chu") == true ||
					currentRestChars.equals("cho") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.startsWith("n") == true || currentRestChars.equals("n'") == true) {
				
				boolean nProcessed = false;
				
				if (currentRestChars.equals("n'") == true) {
					KanaEntry kanaEntry = hiraganaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;					
				}
				
				if (nProcessed == false && (currentRestChars.equals("na") == true ||
						currentRestChars.equals("ni") == true ||
						currentRestChars.equals("nu") == true ||
						currentRestChars.equals("ne") == true ||
						currentRestChars.equals("no") == true ||
						currentRestChars.equals("nya") == true ||
						currentRestChars.equals("nyu") == true ||
						currentRestChars.equals("nyo") == true)) {

					KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;
				} else if (nProcessed == false && currentRestChars.length() > 1) {
					
					if (currentRestChars.startsWith("ny") == false) {
						KanaEntry kanaEntry = hiraganaCache.get("n");
						
						if (kanaEntry == null) {
							throw new JapaneseDictionaryException("Can't find kanaEntry!");
						}
						
						kanaEntries.add(kanaEntry);
						
						currentRestChars = currentRestChars.substring(1);
						
						nProcessed = true;
					}						
				}
				
				if (nProcessed == false && currentRestChars.length() == 1 && idx == word.length() - 1) {
					KanaEntry kanaEntry = hiraganaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;
				}
			} else if (currentRestChars.equals("ha") == true ||
					currentRestChars.equals("hi") == true ||
					currentRestChars.equals("he") == true ||
					currentRestChars.equals("ho") == true ||
					currentRestChars.equals("hya") == true ||
					currentRestChars.equals("hyu") == true ||
					currentRestChars.equals("hyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("fu") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ma") == true ||
					currentRestChars.equals("mi") == true ||
					currentRestChars.equals("mu") == true ||
					currentRestChars.equals("me") == true ||
					currentRestChars.equals("mo") == true ||
					currentRestChars.equals("mya") == true ||
					currentRestChars.equals("myu") == true ||
					currentRestChars.equals("myo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ya") == true ||
					currentRestChars.equals("yu") == true ||
					currentRestChars.equals("yo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ra") == true ||
					currentRestChars.equals("ri") == true ||
					currentRestChars.equals("ru") == true ||
					currentRestChars.equals("re") == true ||
					currentRestChars.equals("ro") == true ||
					currentRestChars.equals("rya") == true ||
					currentRestChars.equals("ryu") == true ||
					currentRestChars.equals("ryo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("wa") == true ||
					currentRestChars.equals("wo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ga") == true ||
					currentRestChars.equals("gi") == true ||
					currentRestChars.equals("gu") == true ||
					currentRestChars.equals("ge") == true ||
					currentRestChars.equals("go") == true ||
					currentRestChars.equals("gya") == true ||
					currentRestChars.equals("gyu") == true ||
					currentRestChars.equals("gyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("za") == true ||
					currentRestChars.equals("zu") == true ||
					currentRestChars.equals("ze") == true ||
					currentRestChars.equals("zo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ji") == true ||
					currentRestChars.equals("ja") == true ||
					currentRestChars.equals("ju") == true ||
					currentRestChars.equals("jo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("da") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("du") == true ||
					currentRestChars.equals("de") == true ||
					currentRestChars.equals("do") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ba") == true ||
					currentRestChars.equals("bi") == true ||
					currentRestChars.equals("bu") == true ||
					currentRestChars.equals("be") == true ||
					currentRestChars.equals("bo") == true ||
					currentRestChars.equals("bya") == true ||
					currentRestChars.equals("byu") == true ||
					currentRestChars.equals("byo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("pa") == true ||
					currentRestChars.equals("pi") == true ||
					currentRestChars.equals("pu") == true ||
					currentRestChars.equals("pe") == true ||
					currentRestChars.equals("po") == true ||
					currentRestChars.equals("pya") == true ||
					currentRestChars.equals("pyu") == true ||
					currentRestChars.equals("pyo") == true) {
				
				KanaEntry kanaEntry = hiraganaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			}
			
			remaingRestChars = currentRestChars;
		}
		
		KanaWord result = new KanaWord();
		
		result.kanaEntries = kanaEntries;
		result.remaingRestChars = remaingRestChars;
		
		return result;	
	}

	public static KanaWord convertRomajiIntoKatakanaWord(Map<String, KanaEntry> kitakanaCache, String word) throws JapaneseDictionaryException {
		
		List<KanaEntry> kanaEntries = new ArrayList<KanaEntry>();
		
		String remaingRestChars = null;
		
		String currentRestChars = "";
		
		for (int idx = 0; idx < word.length(); ++idx) {
			String currentChar = String.valueOf(word.charAt(idx));
			
			if (currentChar.equals(" ") == true) {
				continue;
			}
			
			currentRestChars += currentChar.toLowerCase();
			
			if (idx > 0) {
				char previousChar = word.charAt(idx - 1);
				char currentCharChar = word.charAt(idx);
				
				if (previousChar == currentCharChar && isVowel(previousChar) == true && isVowel(currentCharChar) == true) {
					KanaEntry kanaEntry = kitakanaCache.get("ttsu2");
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);

					currentRestChars = "";
					
					continue;
				}
			}
			
			if (currentRestChars.length() == 2 && currentRestChars.charAt(0) == currentRestChars.charAt(1) &&
					currentRestChars.charAt(0) != 'n') {
				
				KanaEntry kanaEntry = kitakanaCache.get("ttsu");
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "" + currentRestChars.charAt(1);
				
				continue;				
			}
			
			if (currentRestChars.equals("a") == true ||
					currentRestChars.equals("i") == true ||
					currentRestChars.equals("u") == true ||
					currentRestChars.equals("e") == true ||
					currentRestChars.equals("o") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";
			} else if (currentRestChars.equals("ka") == true ||
					currentRestChars.equals("ki") == true ||
					currentRestChars.equals("ku") == true ||
					currentRestChars.equals("ke") == true ||
					currentRestChars.equals("ko") == true ||
					currentRestChars.equals("kya") == true ||
					currentRestChars.equals("kyu") == true ||
					currentRestChars.equals("kyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("sa") == true ||
					currentRestChars.equals("shi") == true ||
					currentRestChars.equals("sha") == true ||
					currentRestChars.equals("shu") == true ||
					currentRestChars.equals("sho") == true ||
					currentRestChars.equals("she") == true ||
					currentRestChars.equals("su") == true ||
					currentRestChars.equals("se") == true ||
					currentRestChars.equals("so") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ta") == true ||
					currentRestChars.equals("tsu") == true ||
					currentRestChars.equals("te") == true ||
					currentRestChars.equals("to") == true ||
					currentRestChars.equals("ti") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("chi") == true ||
					currentRestChars.equals("cha") == true ||
					currentRestChars.equals("chu") == true ||
					currentRestChars.equals("cho") == true ||
					currentRestChars.equals("che") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.startsWith("n") == true || currentRestChars.equals("n'") == true) {
				
				boolean nProcessed = false;
				
				if (currentRestChars.equals("n'") == true) {
					KanaEntry kanaEntry = kitakanaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;					
				}
				
				if (nProcessed == false && (currentRestChars.equals("na") == true ||
						currentRestChars.equals("ni") == true ||
						currentRestChars.equals("nu") == true ||
						currentRestChars.equals("ne") == true ||
						currentRestChars.equals("no") == true ||
						currentRestChars.equals("nya") == true ||
						currentRestChars.equals("nyu") == true ||
						currentRestChars.equals("nyo") == true)) {

					KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;
				} else if (nProcessed == false && currentRestChars.length() > 1) {
					
					if (currentRestChars.startsWith("ny") == false) {
						KanaEntry kanaEntry = kitakanaCache.get("n");
						
						if (kanaEntry == null) {
							throw new JapaneseDictionaryException("Can't find kanaEntry!");
						}
						
						kanaEntries.add(kanaEntry);
						
						currentRestChars = currentRestChars.substring(1);
						
						nProcessed = true;
					}						
				}
				
				if (nProcessed == false && currentRestChars.length() == 1 && idx == word.length() - 1) {
					KanaEntry kanaEntry = kitakanaCache.get("n");
					
					if (kanaEntry == null) {
						throw new JapaneseDictionaryException("Can't find kanaEntry!");
					}
					
					kanaEntries.add(kanaEntry);
					
					currentRestChars = "";
					
					nProcessed = true;
				}
			} else if (currentRestChars.equals("ha") == true ||
					currentRestChars.equals("hi") == true ||
					currentRestChars.equals("he") == true ||
					currentRestChars.equals("ho") == true ||
					currentRestChars.equals("hya") == true ||
					currentRestChars.equals("hyu") == true ||
					currentRestChars.equals("hyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("fu") == true || 
					currentRestChars.equals("fa") == true ||
					currentRestChars.equals("fi") == true ||
					currentRestChars.equals("fe") == true ||
					currentRestChars.equals("fo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ma") == true ||
					currentRestChars.equals("mi") == true ||
					currentRestChars.equals("mu") == true ||
					currentRestChars.equals("me") == true ||
					currentRestChars.equals("mo") == true ||
					currentRestChars.equals("mya") == true ||
					currentRestChars.equals("myu") == true ||
					currentRestChars.equals("myo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ya") == true ||
					currentRestChars.equals("yu") == true ||
					currentRestChars.equals("yo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ra") == true ||
					currentRestChars.equals("ri") == true ||
					currentRestChars.equals("ru") == true ||
					currentRestChars.equals("re") == true ||
					currentRestChars.equals("ro") == true ||
					currentRestChars.equals("rya") == true ||
					currentRestChars.equals("ryu") == true ||
					currentRestChars.equals("ryo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("wa") == true ||
					currentRestChars.equals("wo") == true || 
					currentRestChars.equals("wi") == true ||
					currentRestChars.equals("we") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ga") == true ||
					currentRestChars.equals("gi") == true ||
					currentRestChars.equals("gu") == true ||
					currentRestChars.equals("ge") == true ||
					currentRestChars.equals("go") == true ||
					currentRestChars.equals("gya") == true ||
					currentRestChars.equals("gyu") == true ||
					currentRestChars.equals("gyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("za") == true ||
					currentRestChars.equals("zu") == true ||
					currentRestChars.equals("ze") == true ||
					currentRestChars.equals("zo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ji") == true ||
					currentRestChars.equals("ja") == true ||
					currentRestChars.equals("ju") == true ||
					currentRestChars.equals("jo") == true ||
					currentRestChars.equals("je") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("da") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("du") == true ||
					currentRestChars.equals("de") == true ||
					currentRestChars.equals("do") == true ||
					currentRestChars.equals("di") == true ||
					currentRestChars.equals("dyu") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("ba") == true ||
					currentRestChars.equals("bi") == true ||
					currentRestChars.equals("bu") == true ||
					currentRestChars.equals("be") == true ||
					currentRestChars.equals("bo") == true ||
					currentRestChars.equals("bya") == true ||
					currentRestChars.equals("byu") == true ||
					currentRestChars.equals("byo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			} else if (currentRestChars.equals("pa") == true ||
					currentRestChars.equals("pi") == true ||
					currentRestChars.equals("pu") == true ||
					currentRestChars.equals("pe") == true ||
					currentRestChars.equals("po") == true ||
					currentRestChars.equals("pya") == true ||
					currentRestChars.equals("pyu") == true ||
					currentRestChars.equals("pyo") == true) {
				
				KanaEntry kanaEntry = kitakanaCache.get(currentRestChars);
				
				if (kanaEntry == null) {
					throw new JapaneseDictionaryException("Can't find kanaEntry!");
				}
				
				kanaEntries.add(kanaEntry);
				
				currentRestChars = "";					
			}
			
			remaingRestChars = currentRestChars;
		}
		
		KanaWord result = new KanaWord();
		
		result.kanaEntries = kanaEntries;
		result.remaingRestChars = remaingRestChars;
		
		return result;		
	}
	
	public static class KanaWord {
		public List<KanaEntry> kanaEntries;
		
		public String remaingRestChars;
	}

	private static boolean isVowel(char char_) {
		if (char_ == 'e' || char_ == 'i' || char_ == 'o' || char_ == 'a') {
			return true;
		} else {
			return false;
		}
	}
}
