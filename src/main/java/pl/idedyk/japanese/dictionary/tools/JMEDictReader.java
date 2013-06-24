package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;

public class JMEDictReader {
	
	public static TreeMap<String, JMEDictEntry> readJMEdict(String fileName) throws Exception {
		
		final TreeMap<String, JMEDictEntry> treeMap = new TreeMap<String, JMEDictEntry>();
		
		System.setProperty("entityExpansionLimit", "1000000");
		
		final EntityMapper entityMapper = new EntityMapper();

		SAXReader reader = new SAXReader();

		reader.addHandler("/JMdict/entry", new ElementHandler() {
			
			public void onStart(ElementPath path) {

			}
			
			public void onEnd(ElementPath path) {
				
				JMEDictEntry jmeDictEntry = new JMEDictEntry();
				
				Element row = path.getCurrent();
				
				// String entSeq = row.selectSingleNode("ent_seq").getText();
				
				// kanji
				List<?> kEleList = row.selectNodes("k_ele");
				
				for (Object kEleListObject : kEleList) {
					
					Element kEle = (Element)kEleListObject;
					
					List<?> kEleKebList = kEle.selectNodes("keb");
					
					for (Object object : kEleKebList) {
						
						Element element = (Element)object;
						
						jmeDictEntry.getKanji().add(element.getText());
					}					
				}
				
				// kana
				List<?> rEleList = row.selectNodes("r_ele");
				
				for (Object rEleListObject : rEleList) {
					
					Element rEle = (Element)rEleListObject;
					
					List<?> rEleRebList = rEle.selectNodes("reb");
					
					for (Object object : rEleRebList) {
						
						Element element = (Element)object;
						
						jmeDictEntry.getKana().add(element.getText());
					}
					
					Element reNokanjiElement = (Element)rEle.selectSingleNode("re_nokanji");
					
					if (reNokanjiElement != null) {
						jmeDictEntry.getKanji().clear();						
					}
				}
				
				// pos
				List<?> senseList = row.selectNodes("sense");
				
				boolean wasPos = false;
				
				for (Object senseListObject : senseList) {
				
					if (wasPos == true) {
						break;
					}
					
					Element sense = (Element)senseListObject;
					
					List<?> sensePosList = sense.selectNodes("pos");
					
					for (Object object : sensePosList) {
						
						wasPos = true;
						
						Element element = (Element)object;
						
						jmeDictEntry.getPos().add(entityMapper.getEntity(element.getText()));
					}
				}
				
				addEdictEntry(treeMap, jmeDictEntry);
				
				row.detach();
			}
		});

		reader.read(new File(fileName));
		
		return treeMap;
	}
	
	private static void addEdictEntry(TreeMap<String, JMEDictEntry> treeMap, JMEDictEntry edictEntry) {
		
		List<String> kanji = edictEntry.getKanji();
		List<String> kana = edictEntry.getKana();
		
		if (kanji.size() == 0) {			
			String kanjiKey = null;
			
			for (String currentKana : kana) {				
				String mapKey = getMapKey(kanjiKey, currentKana);
				
				treeMap.put(mapKey, edictEntry);				
			}
			
		} else {
			
			for (String currentKanji : kanji) {
				for (String currentKana : kana) {
					
					String mapKey = getMapKey(currentKanji, currentKana);
					
					treeMap.put(mapKey, edictEntry);					
				}				
			}			
		}
	}
	
	public static String getMapKey(String kanji, String kana) {
		
		if (kanji == null) {
			kanji = "$$$NULL$$$";
		}
		
		return kanji + ".-." + kana;		
	}
	
	private static class EntityMapper {
		
		private HashMap<String, String> entityToDescMap;
		private HashMap<String, String> descToEntityMap;
		
		public EntityMapper() {
			entityToDescMap = new HashMap<String, String>();
			descToEntityMap = new HashMap<String, String>();
			
			fillMaps();
		}

		private void fillMaps() {
			
			addMap("MA", "martial arts term");
			addMap("X", "rude or X-rated term (not displayed in educational software)");
			addMap("abbr", "abbreviation");
			addMap("adj-i", "adjective (keiyoushi)");
			addMap("adj-na", "adjectival nouns or quasi-adjectives (keiyodoshi)");
			addMap("adj-no", "nouns which may take the genitive case particle `no'");
			addMap("adj-pn", "pre-noun adjectival (rentaishi)");
			addMap("adj-t", "`taru' adjective");
			addMap("adj-f", "noun or verb acting prenominally");
			addMap("adv", "adverb (fukushi)");
			addMap("adv-to", "adverb taking the `to' particle");
			addMap("arch", "archaism");
			addMap("ateji", "ateji (phonetic) reading");
			addMap("aux", "auxiliary");
			addMap("aux-v", "auxiliary verb");
			addMap("aux-adj", "auxiliary adjective");
			addMap("Buddh", "Buddhist term");
			addMap("chem", "chemistry term");
			addMap("chn", "children's language");
			addMap("col", "colloquialism");
			addMap("comp", "computer terminology");
			addMap("conj", "conjunction");
			addMap("ctr", "counter");
			addMap("derog", "derogatory");
			addMap("eK", "exclusively kanji");
			addMap("ek", "exclusively kana");
			addMap("exp", "Expressions (phrases, clauses, etc.)");
			addMap("fam", "familiar language");
			addMap("fem", "female term or language");
			addMap("food", "food term");
			addMap("geom", "geometry term");
			addMap("gikun", "gikun (meaning as reading) or jukujikun (special kanji reading)");
			addMap("hon", "honorific or respectful (sonkeigo) language");
			addMap("hum", "humble (kenjougo) language");
			addMap("iK", "word containing irregular kanji usage");
			addMap("id", "idiomatic expression");
			addMap("ik", "word containing irregular kana usage");
			addMap("int", "interjection (kandoushi)");
			addMap("io", "irregular okurigana usage");
			addMap("iv", "irregular verb");
			addMap("ling", "linguistics terminology");
			addMap("m-sl", "manga slang");
			addMap("male", "male term or language");
			addMap("male-sl", "male slang");
			addMap("math", "mathematics");
			addMap("mil", "military");
			addMap("n", "noun (common) (futsuumeishi)");
			addMap("n-adv", "adverbial noun (fukushitekimeishi)");
			addMap("n-suf", "noun, used as a suffix");
			addMap("n-pref", "noun, used as a prefix");
			addMap("n-t", "noun (temporal) (jisoumeishi)");
			addMap("num", "numeric");
			addMap("oK", "word containing out-dated kanji");
			addMap("obs", "obsolete term");
			addMap("obsc", "obscure term");
			addMap("ok", "out-dated or obsolete kana usage");
			addMap("oik", "old or irregular kana form");
			addMap("on-mim", "onomatopoeic or mimetic word");
			addMap("pn", "pronoun");
			addMap("poet", "poetical term");
			addMap("pol", "polite (teineigo) language");
			addMap("pref", "prefix");
			addMap("proverb", "proverb");
			addMap("prt", "particle");
			addMap("physics", "physics terminology");
			addMap("rare", "rare");
			addMap("sens", "sensitive");
			addMap("sl", "slang");
			addMap("suf", "suffix");
			addMap("uK", "word usually written using kanji alone");
			addMap("uk", "word usually written using kana alone");
			addMap("v1", "Ichidan verb");
			addMap("v2a-s", "Nidan verb with 'u' ending (archaic)");
			addMap("v4h", "Yodan verb with `hu/fu' ending (archaic)");
			addMap("v4r", "Yodan verb with `ru' ending (archaic)");
			addMap("v5aru", "Godan verb - -aru special class");
			addMap("v5b", "Godan verb with `bu' ending");
			addMap("v5g", "Godan verb with `gu' ending");
			addMap("v5k", "Godan verb with `ku' ending");
			addMap("v5k-s", "Godan verb - Iku/Yuku special class");
			addMap("v5m", "Godan verb with `mu' ending");
			addMap("v5n", "Godan verb with `nu' ending");
			addMap("v5r", "Godan verb with `ru' ending");
			addMap("v5r-i", "Godan verb with `ru' ending (irregular verb)");
			addMap("v5s", "Godan verb with `su' ending");
			addMap("v5t", "Godan verb with `tsu' ending");
			addMap("v5u", "Godan verb with `u' ending");
			addMap("v5u-s", "Godan verb with `u' ending (special class)");
			addMap("v5uru", "Godan verb - Uru old class verb (old form of Eru)");
			addMap("vz", "Ichidan verb - zuru verb (alternative form of -jiru verbs)");
			addMap("vi", "intransitive verb");
			addMap("vk", "Kuru verb - special class");
			addMap("vn", "irregular nu verb");
			addMap("vr", "irregular ru verb, plain form ends with -ri");
			addMap("vs", "noun or participle which takes the aux. verb suru");
			addMap("vs-c", "su verb - precursor to the modern suru");
			addMap("vs-s", "suru verb - special class");
			addMap("vs-i", "suru verb - irregular");
			addMap("kyb", "Kyoto-ben");
			addMap("osb", "Osaka-ben");
			addMap("ksb", "Kansai-ben");
			addMap("ktb", "Kantou-ben");
			addMap("tsb", "Tosa-ben");
			addMap("thb", "Touhoku-ben");
			addMap("tsug", "Tsugaru-ben");
			addMap("kyu", "Kyuushuu-ben");
			addMap("rkb", "Ryuukyuu-ben");
			addMap("nab", "Nagano-ben");
			addMap("hob", "Hokkaido-ben");
			addMap("vt", "transitive verb");
			addMap("vulg", "vulgar expression or word");
			addMap("adj-kari", "`kari' adjective (archaic)");
			addMap("adj-ku", "`ku' adjective (archaic)");
			addMap("adj-shiku", "`shiku' adjective (archaic)");
			addMap("adj-nari", "archaic/formal form of na-adjective");
			addMap("n-pr", "proper noun");
			addMap("v-unspec", "verb unspecified");
			addMap("v4k", "Yodan verb with `ku' ending (archaic)");
			addMap("v4g", "Yodan verb with `gu' ending (archaic)");
			addMap("v4s", "Yodan verb with `su' ending (archaic)");
			addMap("v4t", "Yodan verb with `tsu' ending (archaic)");
			addMap("v4n", "Yodan verb with `nu' ending (archaic)");
			addMap("v4b", "Yodan verb with `bu' ending (archaic)");
			addMap("v4m", "Yodan verb with `mu' ending (archaic)");
			addMap("v2k-k", "Nidan verb (upper class) with `ku' ending (archaic)");
			addMap("v2g-k", "Nidan verb (upper class) with `gu' ending (archaic)");
			addMap("v2t-k", "Nidan verb (upper class) with `tsu' ending (archaic)");
			addMap("v2d-k", "Nidan verb (upper class) with `dzu' ending (archaic)");
			addMap("v2h-k", "Nidan verb (upper class) with `hu/fu' ending (archaic)");
			addMap("v2b-k", "Nidan verb (upper class) with `bu' ending (archaic)");
			addMap("v2m-k", "Nidan verb (upper class) with `mu' ending (archaic)");
			addMap("v2y-k", "Nidan verb (upper class) with `yu' ending (archaic)");
			addMap("v2r-k", "Nidan verb (upper class) with `ru' ending (archaic)");
			addMap("v2k-s", "Nidan verb (lower class) with `ku' ending (archaic)");
			addMap("v2g-s", "Nidan verb (lower class) with `gu' ending (archaic)");
			addMap("v2s-s", "Nidan verb (lower class) with `su' ending (archaic)");
			addMap("v2z-s", "Nidan verb (lower class) with `zu' ending (archaic)");
			addMap("v2t-s", "Nidan verb (lower class) with `tsu' ending (archaic)");
			addMap("v2d-s", "Nidan verb (lower class) with `dzu' ending (archaic)");
			addMap("v2n-s", "Nidan verb (lower class) with `nu' ending (archaic)");
			addMap("v2h-s", "Nidan verb (lower class) with `hu/fu' ending (archaic)");
			addMap("v2b-s", "Nidan verb (lower class) with `bu' ending (archaic)");
			addMap("v2m-s", "Nidan verb (lower class) with `mu' ending (archaic)");
			addMap("v2y-s", "Nidan verb (lower class) with `yu' ending (archaic)");
			addMap("v2r-s", "Nidan verb (lower class) with `ru' ending (archaic)");
			addMap("v2w-s", "Nidan verb (lower class) with `u' ending and `we' conjugation (archaic)");
			addMap("archit", "architecture term");
			addMap("astron", "astronomy, etc. term");
			addMap("baseb", "baseball term");
			addMap("biol", "biology term");
			addMap("bot", "botany term");
			addMap("bus", "business term");
			addMap("econ", "economics term");
			addMap("engr", "engineering term");
			addMap("finc", "finance term");
			addMap("geol", "geology, etc. term");
			addMap("law", "law, etc. term");
			addMap("med", "medicine, etc. term");
			addMap("music", "music term");
			addMap("Shinto", "Shinto term");
			addMap("sports", "sports term");
			addMap("sumo", "sumo term");
			addMap("zool", "zoology term");
			addMap("joc", "jocular, humorous term");
			addMap("anat", "anatomical term");			
		}
		
		private void addMap(String entity, String desc) {
			
			entityToDescMap.put(entity, desc);
			
			descToEntityMap.put(desc, entity);
		}
		
		public String getEntity(String desc) {
			
			return descToEntityMap.get(desc);			
		}
	}
}
