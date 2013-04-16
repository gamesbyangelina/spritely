 package org.gba.spritely.sitescrapers;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.URL;
 import java.net.URLConnection;
 import java.net.URLEncoder;
 import java.nio.channels.Channels;
 import java.nio.channels.ReadableByteChannel;
 import java.util.LinkedList;
 import java.util.List;
 
 public class GoogleImageScraper
 {
   public int max_recall = 10;
 
   public static String color_arg = null;
 
   public static void writePicturesToRelativeDirectory(List<String> urls, String targetFolder) throws IOException {
     writePicturesToRelativeDirectory(urls, targetFolder, "news");
   }
 
   public static LinkedList<String> writePicturesToRelativeDirectory(List<String> urls, String targetFolder, String prefix) throws IOException {
     File f = new File(".");
     LinkedList<String> results = new LinkedList<String>();
			  FileOutputStream fos = null;
     for (String url : urls) {
       try {
         URL fileTarget = new URL(url);
         ReadableByteChannel rbc = Channels.newChannel(fileTarget.openStream());
				  fos = new FileOutputStream(f.getCanonicalPath() + targetFolder + "/" + prefix + "_img_" + urls.indexOf(url) + ".png");
         fos.getChannel().transferFrom(rbc, 0L, 16777216L);
         results.add(f.getCanonicalPath() + targetFolder + "/" + prefix + "_img_" + urls.indexOf(url) + ".png");
       }
       catch (IOException e)
       {
         e.printStackTrace();
         System.err.println("Couldn't reach " + url);
       }
				finally{
					if(fos != null)
						fos.close();
				}
     }
     return results;
   }
 
   public static List<String> getFaces(String args) throws Exception {
     return getPictures(args, new String[] { "imgtype=face" });
   }
 
   public static List<String> getPhotos(String args) throws Exception {
     return getPictures(args, new String[] { "imgtype=photo" });
   }
 
   public static List<String> getPictures(String args, String[] url_args) throws Exception {
     LinkedList<String> results = new LinkedList<String>();
 
     String prefs = "";
     for (String s : url_args) {
       prefs = prefs + "&" + s;
     }
 
     if (color_arg != null) {
       prefs = prefs + "&imgcolor=" + color_arg;
     }
 
     prefs += "&safe=active";
     
     String ip = "109.144.197.160";
 
     URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?safe=off&v=1.0" + 
       prefs + "&userip=" + ip + "&rsz=8&q=" + URLEncoder.encode(args, "UTF-8"));
     URLConnection connection = url.openConnection();
     connection.addRequestProperty("Referer", "http://www.gamesbyangelina.org");
 
     StringBuilder builder = new StringBuilder();
     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     String line;
     while ((line = reader.readLine()) != null)
     {
       builder.append(line);
     }
 
     JsonParser parser = new JsonParser();
     JsonObject obj = parser.parse(builder.toString()).getAsJsonObject();
     if (obj.get("responseData") == null)
       return new LinkedList<String>();
     JsonArray array = (JsonArray)((JsonObject)obj.get("responseData")).get("results");
     for (JsonElement j : array) {
       String img_url = ((JsonObject)j).get("unescapedUrl").toString();
       results.add(img_url.substring(1, img_url.length() - 1));
     }
 
     if (color_arg != null) {
       color_arg = null;
     }
 
     return results;
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.GoogleImageScraper
 * JD-Core Version:    0.6.2
 */
