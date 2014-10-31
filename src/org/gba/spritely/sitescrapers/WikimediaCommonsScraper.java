 package org.gba.spritely.sitescrapers;
 
 import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
 public class WikimediaCommonsScraper extends DefaultHandler
 {
 
   public static List<String> searchWC(String query, boolean removethumbs)
   {
     
     WikimediaCommonsScraper scraper = new WikimediaCommonsScraper();
     try {
       URL u = new URL("http://commons.wikimedia.org/wiki/" + query);
 
       Parser p = new Parser();
 
//       SAXParserFactory factory = SAXParserFactory.newInstance();
//       SAXParser saxParser = factory.newSAXParser();
 
       p.setContentHandler(scraper);
       
       HttpClientBuilder clientBuilder = HttpClientBuilder.create();
       clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
       HttpClient client = clientBuilder.build();
       
       HttpGet getRequest = new HttpGet(u.toURI());
       HttpResponse response = client.execute(getRequest);
              
       p.parse(new InputSource(response.getEntity().getContent()));
     }
     catch (Exception e) {
//       e.printStackTrace();
       return Collections.EMPTY_LIST;
     }
 
     List<String> res = new ArrayList<String>();
     String url = "";
     for (int i = 0; i < scraper.urls.size(); i++) {
       url = "http:" + (String)scraper.urls.get(i);
       if ((removethumbs) && (url.contains("/thumb/"))) {
         url = url.replaceAll("/thumb", "");
         url = url.substring(0, url.lastIndexOf("/"));
       }
       if (!url.endsWith(".svg"))
       {
         res.add(url);
       }
     }
     return res;
   }
 
   private List<String> urls;   
   boolean insideImageStructure;
   int imageStructureNestingLevel;
   
   public WikimediaCommonsScraper()
   {
     this.urls =  new ArrayList<String>();     
;
     insideImageStructure = false;
     imageStructureNestingLevel = 0;
   }
 
   public boolean isTagWithAttribute(String ttag, String attr, String tval, String tag, Attributes attributes) {
     return (tag.equalsIgnoreCase(ttag)) && (attributes.getIndex(attr) > -1) && (attributes.getValue(attributes.getIndex(attr)).equalsIgnoreCase(tval));
   }
 
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	 if(insideImageStructure)
	 {
         imageStructureNestingLevel++;
	     if ((qName.equalsIgnoreCase("img")))
	         this.urls.add(attributes.getValue("src"));		 
	 }
	 else
	 {
		 if(attributes.getIndex("class") > -1 && attributes.getValue(attributes.getIndex("class")).equalsIgnoreCase("gallerybox"))
		 {
			 insideImageStructure = true;
			 imageStructureNestingLevel = 1;
		 }
	 }
   }
 
   public void characters(char[] ch, int st, int len)
     throws SAXException
   {
   }
 
   public void endElement(String uri, String localName, String qName)
     throws SAXException
   {
	   if(insideImageStructure)
	   {
		   imageStructureNestingLevel--;
	   }
	   if(imageStructureNestingLevel <= 0)
	   {
		   insideImageStructure = false;
	   }
   }
 
   public void endDocument()
   {
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.WikimediaCommonsScraper
 * JD-Core Version:    0.6.2
 */
