package pl.idedyk.japanese.dictionary.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import pl.idedyk.japanese.dictionary.dto.KanjiDic2Entry;
import pl.idedyk.japanese.dictionary.dto.KanjiEntry;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke.Point;

public class TomoeKanjiImageGenerator {

	public static void main(String[] args) throws Exception {
		
		String tomoeFile = "../JaponskiSlownik_dodatki/tegaki-zinnia-japanese-0.3/handwriting-ja.xml";
		
		String sourceKanjiName = "input/kanji.csv";
		
		String sourceKanjiDic2FileName = "../JaponskiSlownik_dodatki/kanjidic2.xml";
		
		String sourceKradFileName = "../JaponskiSlownik_dodatki/kradfile";
		
		Map<String, List<String>> kradFileMap = KanjiDic2Reader.readKradFile(sourceKradFileName);
		
		Map<String, KanjiDic2Entry> readKanjiDic2 = KanjiDic2Reader.readKanjiDic2(sourceKanjiDic2FileName, kradFileMap);
		
		List<KanjiEntry> kanjiEntries = CsvReaderWriter.parseKanjiEntriesFromCsv(sourceKanjiName, readKanjiDic2);
		
		List<TomoeEntry> tomoeEntries = TomoeReader.readTomoeXmlHandwritingDatabase(tomoeFile);
		
		List<TomoeEntry> neededTomoeEntries = new ArrayList<TomoeEntry>();
		
		Set<String> neededKanjis = new HashSet<String>();
		
		//neededKanjis.add("上");
		//neededKanjis.add("出");
		//neededKanjis.add("社");
		//neededKanjis.add("聞");
		//neededKanjis.add("年");
		//neededKanjis.add("近");
		//neededKanjis.add("歌");
		//neededKanjis.add("神");
		neededKanjis.add("連");
		
		for (int kanjiEntriesIdx = 0; kanjiEntriesIdx < 177; ++kanjiEntriesIdx) {
			
			String currentKanji = kanjiEntries.get(kanjiEntriesIdx).getKanji();
			
			if (neededKanjis.contains(currentKanji) == false) {
				continue;
			}
			
			for (TomoeEntry tomoeEntry : tomoeEntries) {
				if (tomoeEntry.getKanji().equals(currentKanji) == true) {
					neededTomoeEntries.add(tomoeEntry);
				}
			}
			
		}
		
		for (int idx = 0; idx < neededTomoeEntries.size(); ++idx) {
			drawKanji(neededTomoeEntries.get(idx), idx, "output");
		}
	}
	
	private static void drawKanji(TomoeEntry tomoeEntry, int idx, String outputDir) throws Exception {
		
		String kanji = tomoeEntry.getKanji();
		
		BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
		
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		
		List<Stroke> strokeList = tomoeEntry.getStrokeList();
		
		for (int strokeListIdx = 0; strokeListIdx < strokeList.size(); ++strokeListIdx) {
			
			Stroke currentStroke = strokeList.get(strokeListIdx);
			
			List<Point> currentStrokePointList = currentStroke.getPointList();
			
			Stroke.Point lastPoint = currentStrokePointList.get(0);
			
			graphics.drawString(String.valueOf(strokeListIdx + 1), lastPoint.getX() - 30, lastPoint.getY());
			
			for (int currentStrokePointListIdx = 1; currentStrokePointListIdx < currentStrokePointList.size(); ++currentStrokePointListIdx) {
				
				Stroke.Point currentPoint = currentStrokePointList.get(currentStrokePointListIdx);
				
				graphics.drawLine(lastPoint.getX(), lastPoint.getY(), currentPoint.getX(), currentPoint.getY());
				
				lastPoint = currentPoint;
			}
		}
		
		ImageIO.write(bufferedImage, "png", new File(outputDir + File.separator + (idx + 1) + "_" + kanji + "-" + ".png"));
		
	}
}
