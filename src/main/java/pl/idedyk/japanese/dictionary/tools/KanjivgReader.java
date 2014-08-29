package pl.idedyk.japanese.dictionary.tools;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;

public class KanjivgReader {
	
	private static final String svgNamespace = "http://www.w3.org/2000/svg";
	private static final String kvgNamespace = "http://kanjivg.tagaini.net";

	public static KanjivgEntry readKanjivgFile(File file) throws Exception {

		if (file.isFile() == false) {
			return null;
		}
		
		String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

		SAXReader reader = new SAXReader();		
		reader.setFeature(feature, false);

		Document document = reader.read(file);
		
		XPath pathXPath = createXPath(document, "/svg:svg/svg:g/*[@kvg:element]/@kvg:element");
		
		Node kanjiNameNode = pathXPath.selectSingleNode(document);
		
		String kanji = null;
		
		if (kanjiNameNode != null) {			
			kanji = kanjiNameNode.getText();			
		}		
		
		pathXPath = createXPath(document, "/svg:svg/svg:g/*[@kvg:element]//svg:path");
		
		List<?> pathNodes = pathXPath.selectNodes(document);
		
		if (pathNodes.size() == 0) {
			
			pathXPath = createXPath(document, "/svg:svg/svg:g/*/svg:path");
			
			pathNodes = pathXPath.selectNodes(document);
			
			if (pathNodes.size() == 0) {
				throw new RuntimeException("pathNodes.size() == 0");
			}
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
	
	/*
	private static QName createQNameKvg(String name) {
		
		Namespace namespace = new Namespace("kvg", kvgNamespace);
		
		QName qname = new QName(name, namespace);
				
		return qname;
	}
	*/
	
	public static String getKanjivgId(String kanji) {
		Charset unicodeCharset = Charset.forName("UNICODE");
		
		ByteBuffer unicodeByteBuffer = unicodeCharset.encode(kanji);
		
		byte[] unicodeByteBufferArray = unicodeByteBuffer.array();
		
		if (unicodeByteBufferArray.length != 5) {
			throw new RuntimeException();
		}
		
		int[] unicodeIntBufferArray = new int[unicodeByteBufferArray.length];
		
		for (int idx = 0; idx < unicodeByteBufferArray.length; ++idx) {
			
			unicodeIntBufferArray[idx] = (int)unicodeByteBufferArray[idx];
			
			if (unicodeIntBufferArray[idx] < 0) {
				unicodeIntBufferArray[idx] = 256 + unicodeByteBufferArray[idx];
			}
		}
		
		StringBuffer result = new StringBuffer("0");
		
		String u1 = Integer.toHexString(unicodeIntBufferArray[2]);
		
		if (u1.length() == 1) {
			u1 = "0" + u1;
		}
		result.append(u1);
		
		String u2 = Integer.toHexString(unicodeIntBufferArray[3]); 

		if (u2.length() == 1) {
			u2 = "0" + u2;
		}
		result.append(u2);
		
		return result.toString();
	}
}
