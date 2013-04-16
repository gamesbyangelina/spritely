 package org.gba.spritely.sitescrapers;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.LinkedList;
 import java.util.List;
 
public class OpenClipartScraper{
	public static String rooturl_json = "http://www.openclipart.org/search/json/?";
   public static String rooturl = "http://www.openclipart.org/api/search/?";
 
   public static List<String> findClipartJson(String searchTerm, int firstPage, int lastPage) {
     int maxPages = getMaxPages(searchTerm);
     if (firstPage > maxPages) {
       return new LinkedList<String>();
     }
     LinkedList<String> res = new LinkedList<String>();
     for (int i = Math.max(0, firstPage); i <= Math.min(maxPages, lastPage); i++) {
       try {
         URL url = new URL(rooturl_json + "query=" + searchTerm + "&page=" + i);
         URLConnection connection = url.openConnection();
 
         StringBuilder builder = new StringBuilder();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null)
         {
           builder.append(line);
         }
 
         JsonParser parser = new JsonParser();
         JsonObject obj = parser.parse(builder.toString()).getAsJsonObject();
         JsonArray array = (JsonArray)obj.get("payload");
         for (JsonElement j : array) {
           String img_url = ((JsonObject)((JsonObject)j).get("svg")).get("png_thumb").toString();
           res.add(img_url.substring(1, img_url.length() - 1));
         }
       }
       catch (Exception localException)
       {
       }
     }
 
     return res;
   }
 
   public static List<String> findClipart(String searchTerm, int firstPage, int lastPage) {
     int maxPages = getMaxPages(searchTerm);
     if (firstPage > maxPages) {
       return new LinkedList<String>();
     }
     LinkedList<String> res = new LinkedList<String>();
     for (int i = Math.max(1, firstPage); i <= Math.min(maxPages, lastPage); i++) {
       try {
         URL url = new URL(rooturl + "query=" + searchTerm + "&page=" + i);
         URLConnection connection = url.openConnection();
 
         StringBuilder builder = new StringBuilder();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null)
         {
           builder.append(line);
         }
 
         String[] urls = builder.toString().split("<media:thumbnail url=\"");
         for (int j = 0; j < urls.length; j++) {
           if (urls[j].startsWith("http://")) {
             res.add(urls[j].substring(0, urls[j].indexOf("\"")).replace("90px", "150px"));
           }
         }
       }
       catch (Exception localException)
       {
       }
 
     }
 
     return res;
   }
 
   private static int getMaxPages(String searchTerm) {
     try {
       URL url = new URL(rooturl_json + "query=" + searchTerm);
       URLConnection connection = url.openConnection();
 
       StringBuilder builder = new StringBuilder();
       BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
       String line;
       while ((line = reader.readLine()) != null)
       {
         builder.append(line);
       }
 
       JsonParser parser = new JsonParser();
       JsonObject obj = parser.parse(builder.toString()).getAsJsonObject();
       return ((JsonObject)obj.get("info")).get("pages").getAsInt();
     }
     catch (Exception localException)
     {
     }
 
     return -1;
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.OpenClipartScraper
 * JD-Core Version:    0.6.2
 */
