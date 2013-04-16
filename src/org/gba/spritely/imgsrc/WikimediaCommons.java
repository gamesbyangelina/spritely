 package org.gba.spritely.imgsrc;
 
 import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.gba.spritely.Spritely;
import org.gba.spritely.processing.ImageProcessing;
import org.gba.spritely.sitescrapers.WikimediaCommonsScraper;
 
 public class WikimediaCommons
 {
   public static List<BufferedImage> searchWMC(Spritely s)
   {
     List urls = new LinkedList();
     List fresh_urls = WikimediaCommonsScraper.searchWC(s.query, true);
     System.out.println("Searching Wikimedia Commons... " + fresh_urls.size() + " images found...");
     
     if(s.pickRandom){
    	 Collections.shuffle(fresh_urls);
     }
 
     int round = 0;
     while (urls.size() < s.imagesPerSource) {
       if ((round + 1) * s.imagesPerSource > fresh_urls.size()) {
         return urls;
       }
       List set = fresh_urls.subList(round * s.imagesPerSource, (round + 1) * s.imagesPerSource);
 
       LinkedList results = new LinkedList();
       for (BufferedImage image : ImageProcessing.filterForBorderless(set)) {
         results.add(ImageProcessing.reduceBackground(ImageProcessing.shrink(image, 96, 96), true));
       }
 
       urls.addAll(ImageProcessing.filterForSingular(results));
       round++;
     }
 
     if (Spritely.verbose) {
       System.out.println("Wikimedia Commons found " + urls.size() + " images");
     }
     return urls;
   }
 }

