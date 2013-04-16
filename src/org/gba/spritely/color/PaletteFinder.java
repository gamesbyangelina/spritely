package org.gba.spritely.color;

import java.net.URL;
import java.util.LinkedList;
import org.ccil.cowan.tagsoup.Parser;
import org.gba.spritely.sitescrapers.PalettePageHandler;
import org.gba.spritely.sitescrapers.PaletteSearchHandler;
import org.xml.sax.InputSource;

public class PaletteFinder {
	public static LinkedList<ColorPalette> searchForPalettes(String term) {
		String query = term;
		LinkedList<ColorPalette> palettes = new LinkedList<ColorPalette>();
		try {
			URL u = new URL(
					"http://www.colourlovers.com/ajax/search-palettes/_page_1?sortCol=views&sortBy=desc&query="
							+ query);

			Parser p = new Parser();

			LinkedList<String> urls = new LinkedList<String>();

			PaletteSearchHandler handler = new PaletteSearchHandler(
					urls,
					"http://www.colourlovers.com/ajax/search-palettes/_page_1?sortCol=votes&sortBy=desc&query="
							+ query);
			p.setContentHandler(handler);
			p.parse(new InputSource(u.openStream()));

			for (String s : urls) {
				PalettePageHandler pandler = new PalettePageHandler(palettes, s);
				p.setContentHandler(pandler);
				p.parse(new InputSource(new URL(s).openStream()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return palettes;
	}
}
