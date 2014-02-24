package pl.idedyk.japanese.dictionary.misc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.tools.CsvReaderWriter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class OsjpAllreadyAdded {

	public static void main(String[] args) throws Exception {

		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter
				.parsePolishJapaneseEntriesFromCsv("input/word.csv");

		CsvReader csvReader = new CsvReader(new FileReader("input/osjp.csv"), ',');
		CsvWriter csvWriter = new CsvWriter(new FileWriter("input/osjp.csv-wynik"), ',');

		while (csvReader.readRecord()) {

			String id = csvReader.get(0);
			String processed = csvReader.get(1);
			String kanji = csvReader.get(2);
			String translateString = csvReader.get(3);

			boolean setProcessed = false;

			csvWriter.write(id);

			if (processed.equals("1") == false) {

				PolishJapaneseEntry foundPolishJapaneseEntry = findPolishJapaneseEntry(polishJapaneseEntries, kanji);

				if (foundPolishJapaneseEntry != null) {

					List<String> osjpTranslateList = parseStringIntoList(translateString);

					List<String> queue = new ArrayList<String>(osjpTranslateList);

					int before = queue.size();

					for (String currentPolishTranslate : foundPolishJapaneseEntry.getTranslates()) {
						queue.remove(currentPolishTranslate);
					}

					if (queue.size() == 0) {
						setProcessed = true;

						System.out.println(id + " - " + before);
					}
				}
			}

			if (setProcessed == true) {
				processed = "1";
			}

			csvWriter.write(processed);
			csvWriter.write(kanji);
			csvWriter.write(translateString);

			csvWriter.endRecord();
		}

		csvReader.close();
		csvWriter.close();
	}

	private static PolishJapaneseEntry findPolishJapaneseEntry(List<PolishJapaneseEntry> polishJapaneseEntries,
			String kanji) {

		for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {

			String polishJapaneseEntryKanji = polishJapaneseEntry.getKanji();

			if (kanji.equals(polishJapaneseEntryKanji) == true) {

				return polishJapaneseEntry;
			}
		}

		return null;
	}

	private static List<String> parseStringIntoList(String polishTranslateString) {

		List<String> result = new ArrayList<String>();

		String[] splitedPolishTranslateString = polishTranslateString.split("\n");

		for (String currentPolishTranslateString : splitedPolishTranslateString) {

			if (currentPolishTranslateString.equals("") == true) {
				continue;
			}

			result.add(currentPolishTranslateString);
		}

		return result;
	}
}
