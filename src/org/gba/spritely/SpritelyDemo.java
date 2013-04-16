package org.gba.spritely;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpritelyDemo {
	
	public static void main(String[] args){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		Spritely s = new Spritely();
		s.setQuery("scientist");
		s.setRecolor("iranian");
		s.setSize(32);
		s.setImagesPerSource(2);
		s.pickRandom = false;
		s.pickRandomPalette = true;
		s.setSearchGoogleImages(true);
		s.setSearchOpenClipart(true);
		s.setSearchWikimediaCommons(false);
		s.setOutputPath(System.getProperty("user.home"));
		s.write(s.search());
		
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();
		System.out.println(dateFormat.format(date));
		
	}

}
