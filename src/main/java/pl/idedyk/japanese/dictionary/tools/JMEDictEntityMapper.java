package pl.idedyk.japanese.dictionary.tools;

import java.util.HashMap;

@Deprecated
public class JMEDictEntityMapper {

	private final HashMap<String, String> entityToDescMap;
	private final HashMap<String, String> descToEntityMap;

	public JMEDictEntityMapper() {
		entityToDescMap = new HashMap<String, String>();
		descToEntityMap = new HashMap<String, String>();

		fillMaps();
	}

	private void fillMaps() {

		addMap("bra", "Brazilian");
		addMap("hob", "Hokkaido-ben");
		addMap("ksb", "Kansai-ben");
		addMap("ktb", "Kantou-ben");
		addMap("kyb", "Kyoto-ben");
		addMap("kyu", "Kyuushuu-ben");
		addMap("nab", "Nagano-ben");
		addMap("osb", "Osaka-ben");
		addMap("rkb", "Ryuukyuu-ben");
		addMap("thb", "Touhoku-ben");
		addMap("tsb", "Tosa-ben");
		addMap("tsug", "Tsugaru-ben");
		addMap("agric", "agriculture");
		addMap("anat", "anatomy");
		addMap("archeol", "archeology");
		addMap("archit", "architecture");
		addMap("art", "art, aesthetics");
		addMap("astron", "astronomy");
		addMap("audvid", "audiovisual");
		addMap("aviat", "aviation");
		addMap("baseb", "baseball");
		addMap("biochem", "biochemistry");
		addMap("biol", "biology");
		addMap("bot", "botany");
		addMap("Buddh", "Buddhism");
		addMap("bus", "business");
		addMap("cards", "card games");
		addMap("chem", "chemistry");
		addMap("Christn", "Christianity");
		addMap("cloth", "clothing");
		addMap("comp", "computing");
		addMap("cryst", "crystallography");
		addMap("dent", "dentistry");
		addMap("ecol", "ecology");
		addMap("econ", "economics");
		addMap("elec", "electricity, elec. eng.");
		addMap("electr", "electronics");
		addMap("embryo", "embryology");
		addMap("engr", "engineering");
		addMap("ent", "entomology");
		addMap("film", "film");
		addMap("finc", "finance");
		addMap("fish", "fishing");
		addMap("food", "food, cooking");
		addMap("gardn", "gardening, horticulture");
		addMap("genet", "genetics");
		addMap("geogr", "geography");
		addMap("geol", "geology");
		addMap("geom", "geometry");
		addMap("go", "go (game)");
		addMap("golf", "golf");
		addMap("gramm", "grammar");
		addMap("grmyth", "Greek mythology");
		addMap("hanaf", "hanafuda");
		addMap("horse", "horse racing");
		addMap("kabuki", "kabuki");
		addMap("law", "law");
		addMap("ling", "linguistics");
		addMap("logic", "logic");
		addMap("MA", "martial arts");
		addMap("mahj", "mahjong");
		addMap("manga", "manga");
		addMap("math", "mathematics");
		addMap("mech", "mechanical engineering");
		addMap("med", "medicine");
		addMap("met", "meteorology");
		addMap("mil", "military");
		addMap("mining", "mining");
		addMap("music", "music");		
		addMap("ornith", "ornithology");		
		addMap("paleo", "paleontology");
		addMap("pathol", "pathology");
		addMap("pharm", "pharmacology");
		addMap("phil", "philosophy");
		addMap("photo", "photography");
		addMap("physics", "physics");
		addMap("physiol", "physiology");
		addMap("politics", "politics");
		addMap("print", "printing");
		addMap("psy", "psychiatry");
		addMap("psyanal", "psychoanalysis");
		addMap("psych", "psychology");
		addMap("rail", "railway");
		addMap("rommyth", "Roman mythology");
		addMap("Shinto", "Shinto");
		addMap("shogi", "shogi");
		addMap("ski", "skiing");
		addMap("sports", "sports");
		addMap("stat", "statistics");
		addMap("stockm", "stock market");
		addMap("sumo", "sumo");
		addMap("telec", "telecommunications");
		addMap("tradem", "trademark");
		addMap("tv", "television");
		addMap("vidg", "video games");
		addMap("zool", "zoology");
		addMap("ateji", "ateji (phonetic) reading");
		addMap("ik", "word containing irregular kana usage");
		addMap("iK", "word containing irregular kanji usage");
		addMap("io", "irregular okurigana usage");
		addMap("oK", "word containing out-dated kanji or kanji usage");
		addMap("rK", "rarely used kanji form");
		addMap("abbr", "abbreviation");
		addMap("arch", "archaic");
		addMap("char", "character");
		addMap("chn", "children's language");
		addMap("col", "colloquial");
		addMap("company", "company name");
		addMap("creat", "creature");		
		addMap("dated", "dated term");
		addMap("dei", "deity");
		addMap("derog", "derogatory");
		addMap("doc", "document");
		addMap("euph", "euphemistic");
		addMap("ev", "event");
		addMap("fam", "familiar language");
		addMap("fem", "female term or language");
		addMap("fict", "fiction");
		addMap("form", "formal or literary term");
		addMap("given", "given name or forename, gender not specified");
		addMap("group", "group");
		addMap("hist", "historical term");
		addMap("hon", "honorific or respectful (sonkeigo) language");
		addMap("hum", "humble (kenjougo) language");
		addMap("id", "idiomatic expression");
		addMap("joc", "jocular, humorous term");
		addMap("leg", "legend");
		addMap("m-sl", "manga slang");
		addMap("male", "male term or language");
		addMap("myth", "mythology");
		addMap("net-sl", "Internet slang");
		addMap("obj", "object");
		addMap("obs", "obsolete term");
		addMap("on-mim", "onomatopoeic or mimetic word");
		addMap("organization", "organization name");
		addMap("oth", "other");
		addMap("person", "full name of a particular person");
		addMap("place", "place name");
		addMap("poet", "poetical term");
		addMap("pol", "polite (teineigo) language");
		addMap("product", "product name");
		addMap("proverb", "proverb");
		addMap("quote", "quotation");
		addMap("rare", "rare term");
		addMap("relig", "religion");
		addMap("sens", "sensitive");
		addMap("serv", "service");
		addMap("ship", "ship name");
		addMap("sl", "slang");
		addMap("station", "railway station");
		addMap("surname", "family or surname");
		addMap("uk", "word usually written using kana alone");
		addMap("unclass", "unclassified name");
		addMap("vulg", "vulgar expression or word");
		addMap("work", "work of art, literature, music, etc. name");
		addMap("X", "rude or X-rated term (not displayed in educational software)");
		addMap("yoji", "yojijukugo");
		addMap("adj-f", "noun or verb acting prenominally");
		addMap("adj-i", "adjective (keiyoushi)");
		addMap("adj-ix", "adjective (keiyoushi) - yoi/ii class");
		addMap("adj-kari", "'kari' adjective (archaic)");
		addMap("adj-ku", "'ku' adjective (archaic)");
		addMap("adj-na", "adjectival nouns or quasi-adjectives (keiyodoshi)");
		addMap("adj-nari", "archaic/formal form of na-adjective");
		addMap("adj-no", "nouns which may take the genitive case particle 'no'");
		addMap("adj-pn", "pre-noun adjectival (rentaishi)");
		addMap("adj-shiku", "'shiku' adjective (archaic)");
		addMap("adj-t", "'taru' adjective");
		addMap("adv", "adverb (fukushi)");
		addMap("adv-to", "adverb taking the 'to' particle");
		addMap("aux", "auxiliary");
		addMap("aux-adj", "auxiliary adjective");
		addMap("aux-v", "auxiliary verb");
		addMap("conj", "conjunction");
		addMap("cop", "copula");
		addMap("ctr", "counter");
		addMap("exp", "expressions (phrases, clauses, etc.)");
		addMap("int", "interjection (kandoushi)");
		addMap("n", "noun (common) (futsuumeishi)");
		addMap("n-adv", "adverbial noun (fukushitekimeishi)");
		addMap("n-pr", "proper noun");
		addMap("n-pref", "noun, used as a prefix");
		addMap("n-suf", "noun, used as a suffix");
		addMap("n-t", "noun (temporal) (jisoumeishi)");
		addMap("num", "numeric");
		addMap("pn", "pronoun");
		addMap("pref", "prefix");
		addMap("prt", "particle");
		addMap("suf", "suffix");
		addMap("unc", "unclassified");
		addMap("v-unspec", "verb unspecified");
		addMap("v1", "Ichidan verb");
		addMap("v1-s", "Ichidan verb - kureru special class");
		addMap("v2a-s", "Nidan verb with 'u' ending (archaic)");
		addMap("v2b-k", "Nidan verb (upper class) with 'bu' ending (archaic)");
		addMap("v2b-s", "Nidan verb (lower class) with 'bu' ending (archaic)");
		addMap("v2d-k", "Nidan verb (upper class) with 'dzu' ending (archaic)");
		addMap("v2d-s", "Nidan verb (lower class) with 'dzu' ending (archaic)");
		addMap("v2g-k", "Nidan verb (upper class) with 'gu' ending (archaic)");
		addMap("v2g-s", "Nidan verb (lower class) with 'gu' ending (archaic)");
		addMap("v2h-k", "Nidan verb (upper class) with 'hu/fu' ending (archaic)");
		addMap("v2h-s", "Nidan verb (lower class) with 'hu/fu' ending (archaic)");
		addMap("v2k-k", "Nidan verb (upper class) with 'ku' ending (archaic)");
		addMap("v2k-s", "Nidan verb (lower class) with 'ku' ending (archaic)");
		addMap("v2m-k", "Nidan verb (upper class) with 'mu' ending (archaic)");
		addMap("v2m-s", "Nidan verb (lower class) with 'mu' ending (archaic)");
		addMap("v2n-s", "Nidan verb (lower class) with 'nu' ending (archaic)");
		addMap("v2r-k", "Nidan verb (upper class) with 'ru' ending (archaic)");
		addMap("v2r-s", "Nidan verb (lower class) with 'ru' ending (archaic)");
		addMap("v2s-s", "Nidan verb (lower class) with 'su' ending (archaic)");
		addMap("v2t-k", "Nidan verb (upper class) with 'tsu' ending (archaic)");
		addMap("v2t-s", "Nidan verb (lower class) with 'tsu' ending (archaic)");
		addMap("v2w-s", "Nidan verb (lower class) with 'u' ending and 'we' conjugation (archaic)");
		addMap("v2y-k", "Nidan verb (upper class) with 'yu' ending (archaic)");
		addMap("v2y-s", "Nidan verb (lower class) with 'yu' ending (archaic)");
		addMap("v2z-s", "Nidan verb (lower class) with 'zu' ending (archaic)");
		addMap("v4b", "Yodan verb with 'bu' ending (archaic)");
		addMap("v4g", "Yodan verb with 'gu' ending (archaic)");
		addMap("v4h", "Yodan verb with 'hu/fu' ending (archaic)");
		addMap("v4k", "Yodan verb with 'ku' ending (archaic)");
		addMap("v4m", "Yodan verb with 'mu' ending (archaic)");
		addMap("v4n", "Yodan verb with 'nu' ending (archaic)");
		addMap("v4r", "Yodan verb with 'ru' ending (archaic)");
		addMap("v4s", "Yodan verb with 'su' ending (archaic)");
		addMap("v4t", "Yodan verb with 'tsu' ending (archaic)");
		addMap("v5aru", "Godan verb - -aru special class");
		addMap("v5b", "Godan verb with 'bu' ending");
		addMap("v5g", "Godan verb with 'gu' ending");
		addMap("v5k", "Godan verb with 'ku' ending");
		addMap("v5k-s", "Godan verb - Iku/Yuku special class");
		addMap("v5m", "Godan verb with 'mu' ending");
		addMap("v5n", "Godan verb with 'nu' ending");
		addMap("v5r", "Godan verb with 'ru' ending");
		addMap("v5r-i", "Godan verb with 'ru' ending (irregular verb)");
		addMap("v5s", "Godan verb with 'su' ending");
		addMap("v5t", "Godan verb with 'tsu' ending");
		addMap("v5u", "Godan verb with 'u' ending");
		addMap("v5u-s", "Godan verb with 'u' ending (special class)");
		addMap("v5uru", "Godan verb - Uru old class verb (old form of Eru)");
		addMap("vi", "intransitive verb");
		addMap("vk", "Kuru verb - special class");
		addMap("vn", "irregular nu verb");
		addMap("vr", "irregular ru verb, plain form ends with -ri");
		addMap("vs", "noun or participle which takes the aux. verb suru");
		addMap("vs-c", "su verb - precursor to the modern suru");
		addMap("vs-i", "suru verb - included");
		addMap("vs-s", "suru verb - special class");
		addMap("vt", "transitive verb");
		addMap("vz", "Ichidan verb - zuru verb (alternative form of -jiru verbs)");
		addMap("gikun", "gikun (meaning as reading) or jukujikun (special kanji reading)");
		addMap("ik", "word containing irregular kana usage");
		addMap("ok", "out-dated or obsolete kana usage");
		addMap("uK", "word usually written using kanji alone");
		addMap("rk", "rarely used kana form");
		
		addMap("sK", "search-only kanji form");
		addMap("sk", "search-only kana form");
		
		addMap("fem", "female given name or forename");
		addMap("masc", "male given name or forename");
	}

	private void addMap(String entity, String desc) {

		entityToDescMap.put(entity, desc);

		descToEntityMap.put(desc, entity);
	}

	public String getEntity(String desc) {

		String result = descToEntityMap.get(desc);
		
		if (result == null) {
			throw new RuntimeException(desc);
		}
		
		return result;
	}
	
	public String getDesc(String entity) {
		
		String result = entityToDescMap.get(entity);
		
		if (result == null) {
			throw new RuntimeException(entity);
		}
		
		return result;

	}
}
