package org.gba.spritely.imgsrc;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.gba.spritely.Spritely;
import org.gba.spritely.processing.ImageProcessing;
import org.gba.spritely.sitescrapers.OpenClipartScraper;

public class OpenClipart {
	public static List<BufferedImage> searchOC(Spritely s) {
		List<BufferedImage> urls = new LinkedList<BufferedImage>();
		int roundSize = 1;
		List<String> fresh_urls = OpenClipartScraper.findClipart(s.query, 0, roundSize);

		if(s.pickRandom){
			Collections.shuffle(fresh_urls);
		}
		
		int round = 0;
		while (urls.size() < s.imagesPerSource) {
			if(Spritely.verbose)
				System.out.println("Searching OpenClipart... " + fresh_urls.size() + " images found...");
			LinkedList<BufferedImage> results = new LinkedList<BufferedImage>();
			
			for (String url : fresh_urls) {
				if (urls.size() > s.imagesPerSource)
					break;
				BufferedImage img = ImageProcessing.isBorderless(url, false);
				if (img == null)
					continue;
				img = ImageProcessing.reduceBackground(
						ImageProcessing.shrink(img, 96, 96), true);
				img = ImageProcessing.isSingular(img);
				if (img == null)
					continue;
				urls.add(img);
			}

			round++;
			if (urls.size() < s.imagesPerSource) {
				fresh_urls = OpenClipartScraper.findClipart(s.query, round * roundSize,
						(round + 1) * roundSize);
				if (fresh_urls.size() == 0) {
					return urls;
				}
			}
		}
		if (Spritely.verbose) {
			System.out.println("OpenClipart found " + urls.size() + " images");
		}

		while (urls.size() > s.imagesPerSource)
			urls.remove(urls.size() - 1);

		return urls;
	}
}
