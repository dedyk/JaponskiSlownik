package pl.idedyk.japanese.dictionary.tools;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.japannaka.JapannakaHtmlReader;
import pl.idedyk.japanese.dictionary.japannaka.exception.JapannakaException;
import pl.idedyk.japanese.dictionary.japannaka.utils.Utils;

public class KanjiImageWriter {

	public static void main(String[] args) throws Exception {
		
		String imageDir = "images_output";
		
		List<PolishJapaneseEntry> japanesePolishDictionary = 
			JapannakaHtmlReader.readJapannakaHtmlDir("websites/www.japannaka.republika.pl");
		
		Map<String, String> kanjiCache = new HashMap<String, String>();
		
		for (PolishJapaneseEntry polishJapaneseEntry : japanesePolishDictionary) {
			
			//System.out.println("Creating image for: " + polishJapaneseEntry.getJapanese() + " - " + polishJapaneseEntry.getRomajiList());
			
			createKanjiImage(kanjiCache, imageDir, polishJapaneseEntry);
		}
	}
	
	public static void createKanjiImage(Map<String, String> kanjiCache, String imageDir, PolishJapaneseEntry polishJapaneseEntry) 
			throws JapannakaException {
		
		String kanji = polishJapaneseEntry.getJapanese();
		
		if (kanjiCache.containsKey(kanji)) {			
			polishJapaneseEntry.setJapaneseImagePath(kanjiCache.get(kanji));
			
			return;
		}
		
		String fileName = createUniqueFileName(kanji);
				
		polishJapaneseEntry.setJapaneseImagePath(fileName);
		
		String fileFullPath = imageDir + File.separator + fileName;
		File file = new File(fileFullPath);
		
		createImage(file, kanji);				
		
		kanjiCache.put(kanji, fileName);
	}
	
	public static void createNewKanjiImage(Map<String, String> cache, String imageDir, PolishJapaneseEntry polishJapaneseEntry) throws JapannakaException {
		
		String japaneseImagePath = "";
		
		String kanji = polishJapaneseEntry.getJapanese();
		
		for (int idx = 0; idx < kanji.length(); ++idx) {
			
			String currentChar = String.valueOf(kanji.charAt(idx));
			
			String currentCharInCache = cache.get(currentChar);
			
			if (currentCharInCache == null) {
				String fileName = createUniqueFileName(currentChar);
				
				String fileFullPath = imageDir + File.separator + fileName;
				File file = new File(fileFullPath);
				
				createImage(file, currentChar);	
				
				cache.put(currentChar, fileName);
				
				currentCharInCache = fileName;
			}
			
			japaneseImagePath += currentCharInCache;
			
			if (idx != kanji.length() - 1) {
				japaneseImagePath += ",";
			}
		}
		
		polishJapaneseEntry.setJapaneseImagePath(japaneseImagePath);
	}
	
	private static void createImage(File imageFile, String word) throws JapannakaException {
		
		BufferedImage bufferedImage = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);
		
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		
		Font font = new Font("Arial Unicode MS", Font.PLAIN, 30);
		
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		graphics.setFont(font);
		
		FontMetrics fontMetrics = graphics.getFontMetrics();
		
		int stringXPos = 50;
		int stringYPos = 50;
		
		graphics.drawString(word, stringXPos, stringYPos);

		Rectangle2D stringBoundsRectangle = fontMetrics.getStringBounds(word, graphics);
		
		BufferedImage bufferedImageNew = bufferedImage.getSubimage(
				stringXPos + (int)stringBoundsRectangle.getMinX(),
				stringYPos + (int)stringBoundsRectangle.getMinY(), 
				(int)stringBoundsRectangle.getWidth(), 
				(int)stringBoundsRectangle.getHeight());
		
		try {
			ImageIO.write(bufferedImageNew, getImageFileFormat(), imageFile);
		} catch (IOException e) {
			throw new JapannakaException("Can't create image file: " + e.getMessage());
		}
	}
	
	private static String getImageFileFormat() {
		return "png";
	}
	
	private static String createUniqueFileName(String word) {
		String fileName = Utils.replaceChars(new String(Base64.encodeBase64(word.getBytes()))) + "." + getImageFileFormat();
		
		fileName = fileName.replaceAll("/", "_");
		
		return fileName;
	}
}
