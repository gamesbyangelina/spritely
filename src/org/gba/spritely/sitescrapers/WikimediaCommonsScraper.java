 package org.gba.spritely.sitescrapers;
 
 import java.net.URL;
 import java.util.LinkedList;
 import java.util.List;
 import javax.xml.parsers.SAXParser;
 import javax.xml.parsers.SAXParserFactory;
 import org.ccil.cowan.tagsoup.Parser;
 import org.xml.sax.Attributes;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.helpers.DefaultHandler;
 
 public class WikimediaCommonsScraper extends DefaultHandler
 {
   private LinkedList<String> urls;
 
   public static List<String> searchWC(String query, boolean removethumbs)
   {
     LinkedList urls = new LinkedList();
 
     WikimediaCommonsScraper scraper = new WikimediaCommonsScraper(urls);
     try {
       URL u = new URL("http://commons.wikimedia.org/wiki/" + query);
 
       Parser p = new Parser();
 
       SAXParserFactory factory = SAXParserFactory.newInstance();
       SAXParser saxParser = factory.newSAXParser();
 
       p.setContentHandler(scraper);
       p.parse(new InputSource(u.openStream()));
     }
     catch (Exception e) {
//       e.printStackTrace();
       return new LinkedList();
     }
 
     List res = new LinkedList();
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
 
   public WikimediaCommonsScraper(LinkedList<String> urls)
   {
     this.urls = urls;
   }
 
   public boolean isTagWithAttribute(String ttag, String attr, String tval, String tag, Attributes attributes) {
     return (tag.equalsIgnoreCase(ttag)) && (attributes.getIndex(attr) > -1) && (attributes.getValue(attributes.getIndex(attr)).equalsIgnoreCase(tval));
   }
 
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
     if ((qName.equalsIgnoreCase("img")) && (attributes.getValue("alt") != null) && (attributes.getValue("alt").equalsIgnoreCase("")))
       this.urls.add(attributes.getValue("src"));
   }
 
   public void characters(char[] ch, int st, int len)
     throws SAXException
   {
   }
 
   public void endElement(String uri, String localName, String qName)
     throws SAXException
   {
   }
 
   public void endDocument()
   {
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.WikimediaCommonsScraper
 * JD-Core Version:    0.6.2
 */
