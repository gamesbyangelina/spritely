package org.gba.spritely.imgsrc;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gba.spritely.Spritely;
import org.gba.spritely.processing.ImageProcessing;
import org.gba.spritely.sitescrapers.GoogleImageScraper;

public class GoogleImages {
	public static List<BufferedImage> searchGoogle(Spritely s) {
		List<BufferedImage> google_urls = new LinkedList<BufferedImage>();

		int start = 0;
		while (google_urls.size() < s.imagesPerSource) {
			List<String> fresh_urls = new LinkedList<String>();

			if (Spritely.verbose)
				System.out.println("Searching Google... " + google_urls.size()
						+ " images so far...");

			List<List<String>> searches = new LinkedList<List<String>>();
			try {
				for (String prefix : s.googleSearchPrefixes) {
					searches.add(GoogleImageScraper.getPictures(prefix + " "
							+ s.query, new String[] { "start=" + start }));
				}
				for (String suffix : s.googleSearchSuffixes) {
					searches.add(GoogleImageScraper.getPictures(s.query + " "
							+ suffix, new String[] { "start=" + start }));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new LinkedList<BufferedImage>();
			}
			
			/*
			 * Because we have multiple Google queries, interleave them to ensure variety.
			 */
			interleave(searches, fresh_urls);

			if (s.pickRandom) {
				Collections.shuffle(fresh_urls);
			}

			LinkedList results = new LinkedList();
			for (String str : fresh_urls) {
				BufferedImage img = ImageProcessing.isBorderless(str, true);
				if (img == null)
					continue;
				img = ImageProcessing.reduceBackground(
						ImageProcessing.shrink(img, 96, 96), true);
				img = ImageProcessing.isSingular(img);
				if (img == null)
					continue;
				google_urls.add(img);
				if (google_urls.size() >= s.imagesPerSource) {
					if (Spritely.verbose) {
						System.out.println("Google found " + google_urls.size()
								+ " images");
					}
					return google_urls;
				}
			}

			start += 8;
		}

		if (Spritely.verbose) {
			System.out
					.println("Google found " + google_urls.size() + " images");
		}
		return google_urls;
	}

	private static void interleave(List<List<String>> in, List<String> out) {

		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		for (List<String> i : in) {
			iterators.add(i.iterator());
		}
		boolean stop = false;
		
		while(!stop){
			stop = true;
			for(Iterator<String> i : iterators){
				if(i.hasNext()){
					stop = false;
					out.add(i.next());
				}
			}
		}

	}
}
