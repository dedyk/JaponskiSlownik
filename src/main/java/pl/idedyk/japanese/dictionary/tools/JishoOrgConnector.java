package pl.idedyk.japanese.dictionary.tools;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JishoOrgConnector {

	private static final String URL = "http://jisho.org/api/v1/search/words?keyword=";
	
	public boolean isWordExists(String word) {
		
		HttpURLConnection httpURLConnection = null;
		
		try {
			java.net.URL url = new URL(URL + URLEncoder.encode(word, "UTF-8"));
			
			httpURLConnection = (HttpURLConnection)url.openConnection();
			
			int responseCode = httpURLConnection.getResponseCode();
			
			if (responseCode != 200) {				
				throw new RuntimeException("Incorrect response code: " + responseCode);				
			}
			
			String jsonString = IOUtils.toString(httpURLConnection.getInputStream());
						
			JSONObject responseJSONRoot = new JSONObject(jsonString);
			
			JSONObject root$meta = responseJSONRoot.getJSONObject("meta");
			
			Integer root$meta$status = root$meta.getInt("status");
			
			if (root$meta$status != 200) {
				throw new RuntimeException("Incorrect root meta status: " + responseCode);				
			}
					
			JSONArray root$data = responseJSONRoot.getJSONArray("data");
			
			if (root$data.length() == 0) {
				return false;
				
			} else {
				return true;
			}
						
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		} finally {
			httpURLConnection.disconnect();
			
		}
	}
}
