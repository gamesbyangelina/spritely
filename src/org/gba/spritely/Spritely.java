package org.gba.spritely;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.gba.spritely.color.ColorPalette;
import org.gba.spritely.color.PaletteFinder;
import org.gba.spritely.imgsrc.GoogleImages;
import org.gba.spritely.imgsrc.OpenClipart;
import org.gba.spritely.imgsrc.WikimediaCommons;
import org.gba.spritely.processing.ImageProcessing;
import org.gba.spritely.processing.ImageUtil;

public class Spritely {
	private static int setSizePerSource;
	public static boolean verbose = true;
	public static boolean logTime = false; long time, newtime;

	private String recolour = "";
	private String outputPath = "";
	public int imagesPerSource = 5;
	private boolean searchGoogleImages = false;
	public String[] googleSearchPrefixes = { "cartoon" };
	public String[] googleSearchSuffixes = { "silhouette", "side view" };
	private boolean searchOpenClipart = false;
	private boolean searchWikimediaCommons = false;
	public String query;
	private int size = 64;
	public String filename = "";
	public boolean pickRandom;
	public boolean pickRandomPalette;

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("c", true,
				"query for color palettes related to <arg>");
		options.addOption("d", true,
				"output images will be <arg> pixels wide/high");
		options.addOption("n", true,
				"<arg> images extracted per image source (Google, Wikimedia, etc.)");
		options.addOption("o", true, "images saved to filepath <arg>");
		options.addOption("rs", false, "choose images randomly from an initial set");
		options.addOption("rc", false, "choose color palette randomly from set");
		options.addOption("sgi", false, "search google images");
		options.addOption("swm", false, "search wikimedia commons");
		options.addOption("soc", false, "search openclipart");

		if (args.length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("spritely", options);
			System.exit(0);
		}

		CommandLineParser p = new PosixParser();
		CommandLine cmd = null;
		
		Spritely s = new Spritely();
		
		try {
			cmd = p.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

		String recolourQuery = "";
		if (cmd.hasOption("c")) {
			recolourQuery = cmd.getOptionValue("c");
			if(cmd.hasOption("rc"))
				s.pickRandomPalette = true;
		}

		String outputPath = System.getProperty("user.dir");
		if (cmd.hasOption("o")) {
			outputPath = cmd.getOptionValue("o");
		}

		int imagesPerSource = 2;
		if (cmd.hasOption("n")) {
			imagesPerSource = Integer.parseInt(cmd.getOptionValue("n"));
		}

		int size = 8;
		if (cmd.hasOption("d")) {
			size = Integer.parseInt(cmd.getOptionValue("d"));
		}

		if (cmd.hasOption("rs")) {
			s.pickRandom = true;
			setSizePerSource = Integer.parseInt(cmd.getOptionValue("s"));
		}

		s.setImagesPerSource(imagesPerSource);
		s.setOutputPath(outputPath);
		s.setRecolor(recolourQuery);
		s.setSize(size);
		s.setQuery(cmd.getArgs()[(cmd.getArgs().length - 1)]);

		if (cmd.hasOption("sgi")) {
			s.searchGoogleImages = true;
		}
		if (cmd.hasOption("swm")) {
			s.searchWikimediaCommons = true;
		}
		if (cmd.hasOption("soc")) {
			s.searchOpenClipart = true;
		}
		
		if ((!s.searchGoogleImages) && (!s.searchOpenClipart)
				&& (!s.searchWikimediaCommons)) {
			s.searchGoogleImages = true;
			s.searchWikimediaCommons = true;
			s.searchOpenClipart = true;
		}

		s.write(s.search());
	}

	public void write(List<BufferedImage> images) {
		if(logTime)
			time = System.currentTimeMillis();
		
		ColorPalette cp = null;
		if (!recolour.equalsIgnoreCase("")) {
			List palettes = PaletteFinder.searchForPalettes(recolour);
			if(pickRandomPalette){
				Collections.shuffle(palettes);
			}
			if(logTime){
				long newtime = System.currentTimeMillis();
				System.out.println("Searching for palettes took " + (newtime - time));
				time = newtime;
			}
			cp = (ColorPalette) palettes.get(0);
		}

		for (int i = 0; i < Math.min(images.size(), 20); i++) {
			BufferedImage image = (BufferedImage) images.get(i);

			image = ImageProcessing.shrink(image, size, size);
			
			if(logTime){
				long newtime = System.currentTimeMillis();
				System.out.println("Resizing took " + (newtime - time));
				time = newtime;
			}

			if (!recolour.equalsIgnoreCase("")) {
				ImageProcessing.recolorImage(image, cp, 2);
			}
			
			if(logTime){
				newtime = System.currentTimeMillis();
				System.out.println("Recolour took " + (newtime - time));
				time = newtime;
			}
			
			if (filename.equalsIgnoreCase("")) {
				ImageUtil.writeToFile(image, outputPath + File.separator
						+ query.replace(" ","") + i + ".png");
			} else {
				if(filename.startsWith("."))
					filename = filename.substring(1);
				
				ImageUtil.writeToFile(image, outputPath + File.separator
						+ filename.replace(" ", "")+".png");
			}
			
			if(logTime){
				newtime = System.currentTimeMillis();
				System.out.println("Write took " + (newtime - time));
				time = newtime;
			}
		}

		if(logTime){
			long newtime = System.currentTimeMillis();
			System.out.println("Total recolour/write took " + (newtime - time));
			time = newtime;
		}

//		if (verbose)
			System.out.println("Wrote " + images.size() + " images to "
					+ outputPath + File.separator);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<BufferedImage> search() {
		if(logTime){
			time = System.currentTimeMillis();
			System.out.println("Beginning search. Time: " + time);
		}
		
		List<BufferedImage> images = new LinkedList<BufferedImage>();
		if (this.searchGoogleImages)
			images.addAll(GoogleImages.searchGoogle(this));
		
		if(logTime){
			newtime = System.currentTimeMillis();
			System.out.println("Google took " + (newtime - time));
			time = newtime;
		}
		
		if (this.searchWikimediaCommons)
			images.addAll(WikimediaCommons.searchWMC(this));
		
		if(logTime){
			newtime = System.currentTimeMillis();
			System.out.println("Wikimedia took " + (newtime - time));
			time = newtime;
		}
		
		if (this.searchOpenClipart) {
			images.addAll(OpenClipart.searchOC(this));
		}
		
		if(logTime){
			newtime = System.currentTimeMillis();
			System.out.println("OpenClipart took " + (newtime - time));
		}

		return images;
	}

	public void setRecolor(String recolourQuery) {
		this.recolour = recolourQuery;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public void setImagesPerSource(int imagesPerSource) {
		this.imagesPerSource = imagesPerSource;
	}

	public void setSearchGoogleImages(boolean searchGoogleImages) {
		this.searchGoogleImages = searchGoogleImages;
	}

	public void setSearchOpenClipart(boolean searchOpenClipart) {
		this.searchOpenClipart = searchOpenClipart;
	}

	public void setSearchWikimediaCommons(boolean searchWikimediaCommons) {
		this.searchWikimediaCommons = searchWikimediaCommons;
	}

	public void setOutputFilename(String string) {
		filename = string;
	}
}