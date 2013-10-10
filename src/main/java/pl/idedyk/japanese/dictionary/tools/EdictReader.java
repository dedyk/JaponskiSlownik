package pl.idedyk.japanese.dictionary.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pl.idedyk.japanese.dictionary.dto.EDictEntry;

public class EdictReader {

	public static TreeMap<String, EDictEntry> readEdict(String fileName) throws Exception {

		final TreeMap<String, EDictEntry> treeMap = new TreeMap<String, EDictEntry>();

		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));

			boolean firstLine = true;

			while (true) {
				String line = bufferedReader.readLine();

				if (line == null) {
					break;
				}

				if (firstLine == true) {
					firstLine = false;

					continue;
				}

				EDictEntry edictEntry = parseEdict(line);

				addEdictEntry(treeMap, edictEntry);
			}

		} finally {

			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}

		return treeMap;
	}

	private static EDictEntry parseEdict(String text) {

		String kanji = null;
		String kana = null;
		String name = null;
		List<String> pos = new ArrayList<String>();

		String[] splited = text.split(" ");

		if (splited[1].startsWith("[") == true) {
			kanji = splited[0];

			kana = splited[1].substring(1, splited[1].length() - 1);

			if (splited[2].startsWith("/(") == true && splited[2].endsWith(")") == true) { // dla edict

				String posString = splited[2].substring(2, splited[2].length() - 1);

				String[] posStringSplited = posString.split(",");

				for (String currentPos : posStringSplited) {
					pos.add(currentPos);
				}

			} else if (splited[2].startsWith("/") == true && splited[3].startsWith("(") == true
					&& splited[3].endsWith(")/") == true) { // dla enamdic

				name = splited[2].substring(1);

				String posString = splited[3].substring(1, splited[3].length() - 2);

				String[] posStringSplited = posString.split(",");

				for (String currentPos : posStringSplited) {
					pos.add(currentPos);
				}
			}

		} else {

			kana = splited[0];

			if (splited[1].startsWith("/(") == true && splited[1].endsWith(")") == true) { // dla edict

				String posString = splited[1].substring(2, splited[1].length() - 1);

				String[] posStringSplited = posString.split(",");

				for (String currentPos : posStringSplited) {
					pos.add(currentPos);
				}

			} else if (splited[1].startsWith("/") == true && splited[2].startsWith("(") == true
					&& splited[2].endsWith(")/") == true) { // dla enamdic

				name = splited[1].substring(1);

				String posString = splited[2].substring(1, splited[2].length() - 2);

				String[] posStringSplited = posString.split(",");

				for (String currentPos : posStringSplited) {
					pos.add(currentPos);
				}
			}
		}

		EDictEntry edictEntry = new EDictEntry();

		edictEntry.setKanji(kanji);
		edictEntry.setKana(kana);
		edictEntry.setName(name);
		edictEntry.setPos(pos);
		edictEntry.setRawLine(text);

		return edictEntry;
	}

	private static void addEdictEntry(TreeMap<String, EDictEntry> treeMap, EDictEntry edictEntry) {

		String kanji = edictEntry.getKanji();
		String kana = edictEntry.getKana();

		String mapKey = getMapKey(kanji, kana);

		treeMap.put(mapKey, edictEntry);
	}

	public static String getMapKey(String kanji, String kana) {
		return kanji + ".-." + kana;
	}
}
