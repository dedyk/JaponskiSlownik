package pl.idedyk.japanese.dictionary.tools;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;

public class KanjiImageWriter {
	
	public static void createKanjiImage(Map<String, String> kanjiCache, String imageDir, PolishJapaneseEntry polishJapaneseEntry) 
			throws JapaneseDictionaryException {
		
		String kanji = polishJapaneseEntry.getKanji();
		
		if (kanjiCache.containsKey(kanji)) {			
			polishJapaneseEntry.setKanjiImagePath(kanjiCache.get(kanji));
			
			return;
		}
		
		String fileName = createUniqueFileName(kanjiCache, kanji);
				
		polishJapaneseEntry.setKanjiImagePath(fileName);
		
		String fileFullPath = imageDir + File.separator + fileName;
		File file = new File(fileFullPath);
		
		createImage(file, kanji);				
		
		kanjiCache.put(kanji, fileName);
	}
	
	public static void createNewKanjiImage(Map<String, String> cache, String imageDir, PolishJapaneseEntry polishJapaneseEntry) throws JapaneseDictionaryException {
		
		String japaneseImagePath = "";
		
		String word = polishJapaneseEntry.getKanji().equals("-") == false ? 
				polishJapaneseEntry.getPrefix() + polishJapaneseEntry.getKanji() : polishJapaneseEntry.getKanji();
		
		for (int idx = 0; idx < word.length(); ++idx) {
			
			String currentChar = String.valueOf(word.charAt(idx));
			
			String currentCharInCache = createNewKanjiImage(cache, imageDir, currentChar);
			
			japaneseImagePath += currentCharInCache;
			
			if (idx != word.length() - 1) {
				japaneseImagePath += ",";
			}
		}
		
		polishJapaneseEntry.setKanjiImagePath(japaneseImagePath);
	}
	
	public static String createNewKanjiImage(Map<String, String> cache, String imageDir, String word) throws JapaneseDictionaryException {
		
		String currentWordFileNameInCache = cache.get(word);
		
		if (currentWordFileNameInCache == null) {
			String fileName = createUniqueFileName(cache, word);
			
			String fileFullPath = imageDir + File.separator + fileName;
			File file = new File(fileFullPath);
			
			createImage(file, word);	
			
			cache.put(word, fileName);
			
			currentWordFileNameInCache = fileName;
		}

		return currentWordFileNameInCache;
	}
	
	private static void createImage(File imageFile, String word) throws JapaneseDictionaryException {
		
		BufferedImage bufferedImage = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);
		
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		
		Font font = new Font("Monispace", Font.PLAIN, 20);
		
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
			throw new JapaneseDictionaryException("Can't create image file: " + e.getMessage());
		}
	}
	
	private static String getImageFileFormat() {
		return "png";
	}
	
	private static String createUniqueFileName(Map<String, String> cache, String word) {
		
		String postFix = "";
		
		String fileName = null;
		
		while(true) {
			fileName = new String(Base64.encodeBase64(word.getBytes())) + postFix + "." + getImageFileFormat();
			
			String cacheKey = "File:" + fileName.toLowerCase();
			
			if (cache.get(cacheKey) == null) {
				cache.put(cacheKey, fileName);
				
				break;
			} else {
				postFix += "_";
			}
		}
		
		fileName = fileName.replaceAll("/", "_");
		
		return fileName;
	}
}
