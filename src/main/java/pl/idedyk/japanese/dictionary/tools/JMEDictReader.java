package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.dto.JMEDictEntry;

public class JMEDictReader {

	public static TreeMap<String, List<JMEDictEntry>> readJMEdict(String fileName) throws Exception {
		return readJMEdict(fileName, "JMdict", false);
	}

	public static TreeMap<String, List<JMEDictEntry>> readJMEdict(String fileName, boolean kanjiKeyNull)
			throws Exception {
		return readJMEdict(fileName, "JMdict", kanjiKeyNull);
	}

	public static TreeMap<String, List<JMEDictEntry>> readJMnedict(String fileName) throws Exception {
		return readJMEdict(fileName, "JMnedict", false);
	}
	
	/*
	public static TreeMap<String, List<JMEDictEntry>> readJMnedict(String fileName, boolean kanjiKeyNull) throws Exception {
		return readJMEdict(fileName, "JMnedict", kanjiKeyNull);
	}
	*/

	private static TreeMap<String, List<JMEDictEntry>> readJMEdict(String fileName, String rootName,
			final boolean kanjiKeyNull) throws Exception {

		final TreeMap<String, List<JMEDictEntry>> treeMap = new TreeMap<String, List<JMEDictEntry>>();

		System.setProperty("entityExpansionLimit", "1000000");

		final JMEDictEntityMapper entityMapper = new JMEDictEntityMapper();

		SAXReader reader = new SAXReader();

		reader.addHandler("/" + rootName + "/entry", new ElementHandler() {

			@Override
			public void onStart(ElementPath path) {

			}

			@Override
			public void onEnd(ElementPath path) {

				JMEDictEntry jmeDictEntry = new JMEDictEntry();

				Element row = path.getCurrent();

				boolean addNoKanji = false;

				// String entSeq = row.selectSingleNode("ent_seq").getText();

				// kanji
				List<?> kEleList = row.selectNodes("k_ele");

				for (Object kEleListObject : kEleList) {

					Element kEle = (Element) kEleListObject;

					List<?> kEleKebList = kEle.selectNodes("keb");

					for (Object object : kEleKebList) {

						Element element = (Element) object;

						jmeDictEntry.getKanji().add(element.getText());
					}
				}

				// kana
				List<?> rEleList = row.selectNodes("r_ele");

				for (Object rEleListObject : rEleList) {

					Element rEle = (Element) rEleListObject;

					List<?> rEleRebList = rEle.selectNodes("reb");

					for (Object object : rEleRebList) {

						Element element = (Element) object;
						
						jmeDictEntry.getKana().add(element.getText());
					}

					Element reNokanjiElement = (Element) rEle.selectSingleNode("re_nokanji");

					if (reNokanjiElement != null) {
						addNoKanji = true;
					}
				}

				// pos
				List<?> senseList = row.selectNodes("sense");

				boolean wasPos = false;

				for (Object senseListObject : senseList) {

					if (wasPos == true) {
						break;
					}

					Element sense = (Element) senseListObject;

					List<?> sensePosList = sense.selectNodes("pos");

					for (Object object : sensePosList) {

						wasPos = true;

						Element element = (Element) object;

						jmeDictEntry.getPos().add(entityMapper.getEntity(element.getText()));
					}

					List<?> miscList = sense.selectNodes("misc");

					for (Object object : miscList) {

						Element misc = (Element) object;

						String miscText = misc.getText();

						jmeDictEntry.getMisc().add(entityMapper.getEntity(miscText));

						if (miscText.equals("word usually written using kana alone") == true) {
							addNoKanji = true;
						}
					}
				}

				// trans
				List<?> transList = row.selectNodes("trans");

				for (Object transListObject : transList) {

					Element trans = (Element) transListObject;

					List<?> nameType = trans.selectNodes("name_type");

					if (nameType != null) {
						
						for (Object currentNameTypeObject : nameType) {
							
							Element currentNameTypeObjectAsElement = (Element)currentNameTypeObject;
							
							jmeDictEntry.getTrans().add(entityMapper.getEntity(currentNameTypeObjectAsElement.getText()));
						}
					}
					
					Node transDet = trans.selectSingleNode("trans_det");
					
					if (transDet != null) {
						jmeDictEntry.getTransDet().add(transDet.getText());
					}
				}

				addEdictEntry(treeMap, jmeDictEntry, addNoKanji || kanjiKeyNull == true);

				row.detach();
			}
		});

		reader.read(new File(fileName));

		return treeMap;
	}

	private static void addEdictEntry(TreeMap<String, List<JMEDictEntry>> treeMap, JMEDictEntry jmedictEntry,
			boolean addNoKanji) {

		List<String> kanji = jmedictEntry.getKanji();
		List<String> kana = jmedictEntry.getKana();

		if (addNoKanji == true) {

			String kanjiKey = null;

			for (String currentKana : kana) {
				String mapKey = getMapKey(kanjiKey, currentKana);

				putEdictEntry(treeMap, mapKey, jmedictEntry);
			}
		}

		if (kanji.size() == 0) {
			String kanjiKey = null;

			for (String currentKana : kana) {
				String mapKey = getMapKey(kanjiKey, currentKana);

				putEdictEntry(treeMap, mapKey, jmedictEntry);
			}

		} else {

			for (String currentKanji : kanji) {
				for (String currentKana : kana) {

					String mapKey = getMapKey(currentKanji, currentKana);

					putEdictEntry(treeMap, mapKey, jmedictEntry);
				}
			}
		}
	}

	private static void putEdictEntry(TreeMap<String, List<JMEDictEntry>> treeMap, String mapKey,
			JMEDictEntry jmedictEntry) {

		List<JMEDictEntry> list = treeMap.get(mapKey);

		if (list == null) {
			list = new ArrayList<JMEDictEntry>();
		}
		
		if (list.contains(jmedictEntry) == false) {
			list.add(jmedictEntry);
		}

		treeMap.put(mapKey, list);
	}

	public static String getMapKey(String kanji, String kana) {

		if (kanji == null) {
			kanji = "$$$NULL$$$";
		}

		return kanji + ".-." + kana;
	}
}
