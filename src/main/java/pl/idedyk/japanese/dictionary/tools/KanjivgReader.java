package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.dto.KanjivgEntry;

public class KanjivgReader {
	
	private static final String svgNamespace = "http://www.w3.org/2000/svg";
	private static final String kvgNamespace = "http://kanjivg.tagaini.net";

	public static KanjivgEntry readKanjivgFile(File file) throws Exception {

		String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

		SAXReader reader = new SAXReader();		
		reader.setFeature(feature, false);

		Document document = reader.read(file);

		XPath kvgElementXPath = createXPath(document, "/svg:svg/svg:g/*[@kvg:element]");
		
		Element gElement = (Element)kvgElementXPath.selectSingleNode(document);
		
		if (gElement == null) {
			return null;
		}
		
		String kanji = gElement.attributeValue(createQNameKvg("element"));
		
		if (kanji == null) {
			throw new RuntimeException("kanji == null");
		}
		
		XPath pathXPath = createXPath(document, "/svg:svg/svg:g/*[@kvg:element]//svg:path");
		
		List<?> pathNodes = pathXPath.selectNodes(document);
		
		if (pathNodes.size() == 0) {
			throw new RuntimeException("pathNodes.size() == 0");
		}
		
		List<String> strokePaths = new ArrayList<String>();
		
		for (Object currentPathNodeAsObject : pathNodes) {
			Element currentPathNodeAsElement = (Element)currentPathNodeAsObject;
			
			String dValue = currentPathNodeAsElement.attributeValue("d");
			
			if (dValue == null) {
				throw new RuntimeException("dValue == null");
			}
			
			strokePaths.add(dValue);
		}
		
		KanjivgEntry kanjivgEntry = new KanjivgEntry();

		kanjivgEntry.setKanji(kanji);
		kanjivgEntry.setStrokePaths(strokePaths);
		
		return kanjivgEntry;
	}

	private static XPath createXPath(Document document, String xpath) {

		XPath result = document.createXPath(xpath);

		Map<String, String> namespaceUris = new HashMap<String, String>();  
		namespaceUris.put("svg", svgNamespace);
		namespaceUris.put("kvg", kvgNamespace);

		result.setNamespaceURIs( namespaceUris);
		
		return result;
	}
	
	private static QName createQNameKvg(String name) {
		
		Namespace namespace = new Namespace("kvg", kvgNamespace);
		
		QName qname = new QName(name, namespace);
				
		return qname;
	}
}
