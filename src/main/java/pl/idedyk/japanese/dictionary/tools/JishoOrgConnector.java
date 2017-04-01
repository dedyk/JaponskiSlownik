package pl.idedyk.japanese.dictionary.tools;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JishoOrgConnector {

	private static final String URL = "http://jisho.org/api/v1/search/words?keyword=";
	
	private String getJsonString(String word) {

		HttpURLConnection httpURLConnection = null;

		try {

			java.net.URL url = new URL(URL + URLEncoder.encode(word, "UTF-8"));
			
			httpURLConnection = (HttpURLConnection)url.openConnection();
			
			int responseCode = httpURLConnection.getResponseCode();
			
			if (responseCode != 200) {				
				throw new RuntimeException("Incorrect response code: " + responseCode);				
			}
			
			String jsonString = IOUtils.toString(httpURLConnection.getInputStream());
			
			return jsonString;

		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			httpURLConnection.disconnect();

		}
	}
	
	private JSONArray getData(String jsonString) {
				
		JSONObject responseJSONRoot = new JSONObject(jsonString);
		
		JSONObject root$meta = responseJSONRoot.getJSONObject("meta");
		
		Integer root$meta$status = root$meta.getInt("status");
		
		if (root$meta$status != 200) {
			throw new RuntimeException("Incorrect root meta status: " + root$meta$status);				
		}
				
		JSONArray root$data = responseJSONRoot.getJSONArray("data");
		
		return root$data;
	}
	
	public boolean isWordExists(String word) {
		
		String jsonString = getJsonString(word);
		
		JSONArray root$data = getData(jsonString);
		
		if (root$data.length() == 0) {
			return false;
			
		} else {
			return true;
		}
	}
	
	public List<JapaneseWord> getJapaneseWords(String word) {
		
		List<JapaneseWord> result = new ArrayList<>();
		
		//
		
		String jsonString = getJsonString(word);
		
		JSONArray root$data = getData(jsonString);
		
		for (int idx = 0; idx < root$data.length(); ++idx) {
			
			JSONObject root$data$result = root$data.getJSONObject(idx);
			
			JSONArray root$data$result$japanese = root$data$result.getJSONArray("japanese");
			
			//
			
			for (int japaneseIdx = 0; japaneseIdx < root$data$result$japanese.length(); ++japaneseIdx) {
				
				JSONObject currentJapaneseObject = root$data$result$japanese.getJSONObject(japaneseIdx);
				
				//
				
				String kanji = currentJapaneseObject.optString( "word", null);
				String kana = currentJapaneseObject.optString("reading", null);
				
				result.add(new JapaneseWord(kanji, kana));
			}			
		}
		
		return result;
	}
	
	public static class JapaneseWord {
		
		public String kanji;
		
		public String kana;

		public JapaneseWord(String kanji, String kana) {
			this.kanji = kanji;
			this.kana = kana;
		}
	}
}
