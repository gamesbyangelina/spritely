package org.gba.spritely.sitescrapers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Excluder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.ExceptionUtils;
import org.xml.sax.InputSource;

public class OpenClipartScraper{
	public static String rooturl_json = "https://openclipart.org/search/json/?";
	public static String rooturl = "https://openclipart.org/api/search/?";


	private static JsonObject getJsonResultPage(String searchTerm, int i)
			throws MalformedURLException, URISyntaxException, IOException,
			ClientProtocolException {
		URL url = new URL(rooturl_json + "query=" + searchTerm + "&page=" + i);

		InputStream response = ScrapeUtils.readURLLax(url);

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(new InputStreamReader(response)).getAsJsonObject();
		return obj;
	}

	public static List<String> findClipart(String searchTerm, int firstPage, int lastPage) {



		List<String> res = new ArrayList<String>();
		for (int i = Math.max(1, firstPage); i <= lastPage; i++) {
			try {
				JsonObject obj = getJsonResultPage(searchTerm, i);

				JsonArray array = (JsonArray)obj.get("payload");
				for (JsonElement j : array) {
					String img_url = ((JsonObject)((JsonObject)j).get("svg")).get("png_thumb").toString();
					res.add(img_url.substring(1, img_url.length() - 1));
				}


				int numPages = ((JsonObject)obj.get("info")).get("pages").getAsInt();	   
				int currentPage = ((JsonObject)obj.get("info")).get("current_page").getAsInt();
				if(currentPage >= numPages)
				{
					break;
				}
			}
			catch (Exception localException)
			{
				System.out.println("Error getting page from open clipart");
				localException.printStackTrace();
				break;
			}

		}

		return res;
	}

}

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.OpenClipartScraper
 * JD-Core Version:    0.6.2
 */
