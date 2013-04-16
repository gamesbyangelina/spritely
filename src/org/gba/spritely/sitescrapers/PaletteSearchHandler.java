 package org.gba.spritely.sitescrapers;
 
 import java.util.LinkedList;
 import org.xml.sax.Attributes;
 import org.xml.sax.SAXException;
 import org.xml.sax.helpers.DefaultHandler;
 
 public class PaletteSearchHandler extends DefaultHandler
 {
   private LinkedList<String> urls;
 
   public PaletteSearchHandler(LinkedList<String> urls, String string)
   {
     this.urls = urls;
   }
 
   public boolean isTagWithAttribute(String ttag, String attr, String tval, String tag, Attributes attributes) {
     return (tag.equalsIgnoreCase(ttag)) && (attributes.getIndex(attr) > -1) && (attributes.getValue(attributes.getIndex(attr)).equalsIgnoreCase(tval));
   }
 
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
     if ((qName.equalsIgnoreCase("a")) && (attributes.getValue("class") != null) && (attributes.getValue("class").equalsIgnoreCase("palette")) && 
       (attributes.getValue("href").startsWith("/palette/")) && 
       (!this.urls.contains("http://www.colourlovers.com/" + attributes.getValue("href"))))
       this.urls.add("http://www.colourlovers.com/" + attributes.getValue("href"));
   }
 
   public void endElement(String uri, String localName, String qName)
     throws SAXException
   {
   }
 
   public void endDocument()
   {
   }
 
   public void characters(char[] ch, int start, int length)
     throws SAXException
   {
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.PaletteSearchHandler
 * JD-Core Version:    0.6.2
 */
