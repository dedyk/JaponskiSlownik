package pl.idedyk.japanese.dictionary.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import pl.idedyk.japanese.dictionary.dto.TomoeEntry;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke;
import pl.idedyk.japanese.dictionary.dto.TomoeEntry.Stroke.Point;

public class TomoeKanjiImageGenerator {

	public static void main(String[] args) throws Exception {
		
		String tomoeFile = "../JaponskiSlownik_dodatki/tegaki-zinnia-japanese-0.3/handwriting-ja.xml";
		
		List<TomoeEntry> tomoeEntries = TomoeReader.readTomoeXmlHandwritingDatabase(tomoeFile);
		
		String neededKanji = "間";
		
		List<TomoeEntry> neededTomoeEntries = new ArrayList<TomoeEntry>();
		
		for (TomoeEntry tomoeEntry : tomoeEntries) {
			if (tomoeEntry.getKanji().equals(neededKanji) == true) {
				neededTomoeEntries.add(tomoeEntry);
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
		
		ImageIO.write(bufferedImage, "png", new File(outputDir + File.separator + kanji + "-" + idx + ".png"));
		
	}
}
