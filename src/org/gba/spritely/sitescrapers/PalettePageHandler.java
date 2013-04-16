 package org.gba.spritely.sitescrapers;
 
 import java.awt.Color;
 import java.util.LinkedList;
 import java.util.List;
 import org.gba.spritely.color.ColorPalette;
 import org.xml.sax.Attributes;
 import org.xml.sax.SAXException;
 import org.xml.sax.helpers.DefaultHandler;
 
 public class PalettePageHandler extends DefaultHandler
 {
   private String url;
   private boolean startListening = false;
   public LinkedList<ColorPalette> ps;
   public ColorPalette p;
 
   public PalettePageHandler(LinkedList<ColorPalette> palettes, String string)
   {
     this.ps = palettes;
     this.p = new ColorPalette();
     this.url = string;
   }
 
   public boolean isTagWithAttribute(String ttag, String attr, String tval, String tag, Attributes attributes) {
     return (tag.equalsIgnoreCase(ttag)) && (attributes.getIndex(attr) > -1) && (attributes.getValue(attributes.getIndex(attr)).equalsIgnoreCase(tval));
   }
 
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
     if (qName.equalsIgnoreCase("noscript")) {
       this.startListening = true;
     }
 
     if (!this.startListening) {
       return;
     }
     if ((qName.equalsIgnoreCase("a")) && (attributes.getValue("href").equalsIgnoreCase("#")) && (attributes.getValue("onclick").equalsIgnoreCase("return false;"))) {
       String line = attributes.getValue("style");
       line = line.substring(line.indexOf('#') + 1, line.indexOf('#') + 7);
 
       this.p.colors.add(new Color(
         Integer.parseInt(line.substring(0, 2), 16), 
         Integer.parseInt(line.substring(2, 4), 16), 
         Integer.parseInt(line.substring(4, 6), 16)));
     }
   }
 
   public void endElement(String uri, String localName, String qName)
     throws SAXException
   {
     if (qName.equalsIgnoreCase("noscript"))
       this.startListening = false;
   }
 
   public void endDocument()
   {
     this.ps.add(this.p);
   }
 
   public void characters(char[] ch, int start, int length)
     throws SAXException
   {
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.sitescrapers.PalettePageHandler
 * JD-Core Version:    0.6.2
 */
