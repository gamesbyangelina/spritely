package org.gba.spritely.processing;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.gba.spritely.color.ColorPalette;

public class ImageProcessing {
	static int pixelSimilarityLimit = 40;

	public static int trans_limit = 100;

	public static BufferedImage reduceBackground(BufferedImage image,
			boolean andCrop) {
		Color[] corners = {
				new Color(image.getRGB(0, 0)),
				new Color(image.getRGB(image.getWidth() - 1, 0)),
				new Color(image.getRGB(0, image.getHeight() - 1)),
				new Color(image.getRGB(image.getWidth() - 1,
						image.getHeight() - 1)) };

		int avr = 0;
		int avb = 0;
		int avg = 0;
		int ava = 0;
		int count = 0;
		for (Color c : corners) {
			if ((c.getRed() != 0) || (c.getBlue() != 0) || (c.getGreen() != 0)) {
				avr += c.getRed();
				avb += c.getBlue();
				avg += c.getGreen();
				ava += c.getAlpha();
				count++;
			}
		}
		if (count == 0) {
			return image;
		}
		for (Color c : corners) {
			if ((c.getRed() != 0) || (c.getBlue() != 0) || (c.getGreen() != 0)) {
				if ((Math.abs(c.getRed() - avr / count) >= pixelSimilarityLimit)
						|| (Math.abs(c.getBlue() - avb / count) >= pixelSimilarityLimit)
						|| (Math.abs(c.getGreen() - avg / count) >= pixelSimilarityLimit)
						|| (Math.abs(c.getAlpha() - ava / count) >= pixelSimilarityLimit)) {
					System.out.println("Reduction failed.");
					return image;
				}
			}
		}
		Color master = new Color(avr / count, avg / count, avb / count, ava
				/ count);

		// Ok, trying to figure out what I'm doing here.
		// Open seems to be a list of corners initially. This is probably the
		// border removal code.
		LinkedList<Pixel> open = new LinkedList<Pixel>();
		LinkedList<Pixel> closed = new LinkedList<Pixel>();
		open.add(new Pixel(0, 0));
		open.add(new Pixel(0, image.getHeight() - 1));
		open.add(new Pixel(image.getWidth() - 1, 0));
		open.add(new Pixel(image.getWidth() - 1, image.getHeight() - 1));
		// Ah I remember this bit. These variables are used to crop the image as
		// tightly as possible
		int bottom = 0;
		int right = 0;
		int left = image.getWidth() - 1;
		int top = image.getHeight() - 1;
		// Which makes this code the code to remove the transparent stuff from
		// outside the image.
		while (open.size() > 0) {
			Pixel p = (Pixel) open.removeFirst();
			closed.add(p);
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++)
					if ((i != 0) || (j != 0)) {
						if ((p.x + i >= 0) && (p.x + i < image.getWidth())
								&& (p.y + j >= 0)
								&& (p.y + j < image.getHeight())) {
							Pixel thisPoint = new Pixel(p.x + i, p.y + j);
							boolean add = true;
							for (Pixel pp : open) {
								if ((thisPoint.x == pp.x)
										&& (thisPoint.y == pp.y)) {
									add = false;
									break;
								}
							}
							if (add) {
								for (Pixel pp : closed) {
									if ((thisPoint.x == pp.x)
											&& (thisPoint.y == pp.y)) {
										add = false;
										break;
									}
								}
							}
							if ((add)
									&& (areSimilar(
											master,
											new Color(image.getRGB(p.x + i, p.y
													+ j), true)))) {
								open.add(thisPoint);
							} else if (add) {
								if (p.x < left)
									left = p.x;
								else if (p.x > right)
									right = p.x;
								if (p.y > bottom)
									bottom = p.y;
								else if (p.y < top)
									top = p.y;
							}
						}
					}
			}
		}

		if (andCrop) {
			image = image.getSubimage(left, top, right - left, bottom - top);
		}

		for (Pixel p : closed) {
			int mx = p.x - left;
			int my = p.y - top;
			if ((mx >= 0) && (mx < right - left) && (my >= 0)
					&& (my < bottom - top)) {
				image.setRGB(mx, my, 0);
			}
		}
		return image;
	}

	public static boolean areSimilar(Color c, Color d) {
		if ((Math.abs(c.getRed() - d.getRed()) < pixelSimilarityLimit)
				&& (Math.abs(c.getBlue() - d.getBlue()) < pixelSimilarityLimit)
				&& (Math.abs(c.getGreen() - d.getGreen()) < pixelSimilarityLimit)
				&& (Math.abs(c.getAlpha() - d.getAlpha()) < pixelSimilarityLimit)) {
			return true;
		}

		return false;
	}

	public static BufferedImage shrink(BufferedImage b, int w, int h) {
		int[][][] array = new int[w][h][4];

		int[][][] count = new int[w][h][1];
		
		BufferedImage res = new BufferedImage(w, h, 2);

		float w_chunk = ((float)b.getWidth() / (float)w) ;
		float h_chunk = ((float)b.getHeight() / (float)h) ;

		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {
				Color c = new Color(b.getRGB(i, j), true);

				int x = (int) Math.floor(i / w_chunk);
				int y = (int) Math.floor(j / h_chunk);
				array[x][y][0] += c.getRed();
				array[x][y][1] += c.getGreen();
				array[x][y][2] += c.getBlue();
				array[x][y][3] += c.getAlpha();
				count[x][y][0] += 1;
			}
		}

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (count[i][j][0] != 0) {
					Color c = new Color(array[i][j][0] / count[i][j][0],
							array[i][j][1] / count[i][j][0], array[i][j][2]
									/ count[i][j][0], array[i][j][3]
									/ count[i][j][0]);

					if (array[i][j][3] / count[i][j][0] < trans_limit)
						res.setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
					else
						res.setRGB(i, j, c.getRGB());
				}
			}
		}
		return res;
	}

	public static List<BufferedImage> filterForBorderless(List<String> urls) {
		return filterForBorderless(urls, true);
	}

	public static List<BufferedImage> filterForBorderless(List<String> urls,
			boolean strict) {
		LinkedList<BufferedImage> res = new LinkedList<BufferedImage>();
		for (String s : urls) {
			if (!s.endsWith("svg")) {
				BufferedImage image = ImageUtil.getFromURL(s);
				if (image != null) {
					Color[] corners = {
							new Color(image.getRGB(0, 0), true),
							new Color(image.getRGB(image.getWidth() - 1, 0),
									true),
							new Color(image.getRGB(0, image.getHeight() - 1),
									true),
							new Color(image.getRGB(image.getWidth() - 1,
									image.getHeight() - 1), true) };

					int avr = 0;
					int avb = 0;
					int avg = 0;
					int ava = 0;
					for (Color c : corners) {
						avr += c.getRed();
						avb += c.getBlue();
						avg += c.getGreen();
						ava += c.getAlpha();
					}

					List<Color> colors = new LinkedList<Color>();
					if (strict) {
						for (int i = 0; i < image.getWidth(); i++) {
							colors.add(new Color(image.getRGB(i, 0), true));
						}
						for (int i = 0; i < image.getHeight(); i++)
							colors.add(new Color(image.getRGB(0, i), true));
					} else {
						colors.add(new Color(image.getRGB(0, 0), true));
						colors.add(new Color(image.getRGB(image.getWidth() - 1,
								0), true));
						colors.add(new Color(image.getRGB(0,
								image.getHeight() - 1), true));
						colors.add(new Color(image.getRGB(image.getWidth() - 1,
								image.getHeight() - 1), true));
					}

					boolean add = true;
					for (Color c : colors) {
						if ((Math.abs(c.getRed() - avr / 4) >= pixelSimilarityLimit)
								|| (Math.abs(c.getBlue() - avb / 4) >= pixelSimilarityLimit)
								|| (Math.abs(c.getGreen() - avg / 4) >= pixelSimilarityLimit)
								|| (Math.abs(c.getAlpha() - ava / 4) >= pixelSimilarityLimit)) {
							if (add) {
								add = false;
							}
						}
					}
					if (add)
						res.add(image);
				}
			}
		}
		return res;
	}

	public static BufferedImage isBorderless(String s, boolean strict) {
		if (!s.endsWith("svg")) {
			BufferedImage image = ImageUtil.getFromURL(s);
			if (image != null) {
				Color[] corners = {
						new Color(image.getRGB(0, 0), true),
						new Color(image.getRGB(image.getWidth() - 1, 0), true),
						new Color(image.getRGB(0, image.getHeight() - 1), true),
						new Color(image.getRGB(image.getWidth() - 1,
								image.getHeight() - 1), true) };

				int avr = 0;
				int avb = 0;
				int avg = 0;
				int ava = 0;
				for (Color c : corners) {
					avr += c.getRed();
					avb += c.getBlue();
					avg += c.getGreen();
					ava += c.getAlpha();
				}

				List<Color> colors = new LinkedList<Color>();
				if (strict) {
					for (int i = 0; i < image.getWidth(); i++) {
						colors.add(new Color(image.getRGB(i, 0), true));
					}
					for (int i = 0; i < image.getHeight(); i++)
						colors.add(new Color(image.getRGB(0, i), true));
				} else {
					colors.add(new Color(image.getRGB(0, 0), true));
					colors.add(new Color(image.getRGB(image.getWidth() - 1, 0),
							true));
					colors.add(new Color(
							image.getRGB(0, image.getHeight() - 1), true));
					colors.add(new Color(image.getRGB(image.getWidth() - 1,
							image.getHeight() - 1), true));
				}

				boolean add = true;
				for (Color c : colors) {
					if ((Math.abs(c.getRed() - avr / 4) >= pixelSimilarityLimit)
							|| (Math.abs(c.getBlue() - avb / 4) >= pixelSimilarityLimit)
							|| (Math.abs(c.getGreen() - avg / 4) >= pixelSimilarityLimit)
							|| (Math.abs(c.getAlpha() - ava / 4) >= pixelSimilarityLimit)) {
						if (add) {
							add = false;
						}
					}
				}
				if(add)
					return image;
				else 
					return null;
			}
		}
		return null;
	}

	public static List<BufferedImage> filterForSingular(List<BufferedImage> imgs) {
		LinkedList<BufferedImage> res = new LinkedList<BufferedImage>();
		LinkedList<Point> locations = new LinkedList<Point>();
		for (BufferedImage img : imgs) {
			locations = new LinkedList<Point>();
			boolean add = true;
			for (int i = 0; i < img.getWidth(); i++) {
				for (int j = 0; j < img.getHeight(); j++) {
					if ((locations.size() == 0)
							&& (new Color(img.getRGB(i, j), true).getAlpha() > trans_limit)) {
						locations.add(new Point(i, j));
						locations = fill(locations, img);
					} else if ((!locations.contains(new Point(i, j)))
							&& (new Color(img.getRGB(i, j), true).getAlpha() > trans_limit)) {
						i = img.getWidth();
						j = img.getHeight();
						add = false;
					}
				}
			}
			if (add)
				res.add(img);
		}

		return res;
	}

	public static BufferedImage isSingular(BufferedImage img) {

		LinkedList<Point> locations = new LinkedList<Point>();
		locations = new LinkedList<Point>();
		boolean add = true;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if ((locations.size() == 0)
						&& (new Color(img.getRGB(i, j), true).getAlpha() > trans_limit)) {
					locations.add(new Point(i, j));
					locations = fill(locations, img);
				} else if ((!locations.contains(new Point(i, j)))
						&& (new Color(img.getRGB(i, j), true).getAlpha() > trans_limit)) {
					i = img.getWidth();
					j = img.getHeight();
					add = false;
				}
			}
		}
		if (add) {
			return img;
		}

		return null;
	}

	public static LinkedList<Point> fill(LinkedList<Point> openset,
			BufferedImage img) {
		LinkedList<Point> closedset = new LinkedList<Point>();
		while (openset.size() > 0) {

			Point p = (Point) openset.removeFirst();
			closedset.add(p);
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					Point q = new Point(p.x + i, p.y + j);
					if ((q.x >= 0)
							&& (q.x < img.getWidth())
							&& (q.y >= 0)
							&& (q.y < img.getHeight())
							&& (!closedset.contains(q))
							&& (!openset.contains(q))
							&& (new Color(img.getRGB(q.x, q.y), true)
									.getAlpha() > trans_limit))
						openset.add(q);
				}
			}
		}

		return closedset;
	}

	public static void recolorImage(BufferedImage img, ColorPalette cp, int type) {
		Color c = null;
		Color d = null;
		for (int i = 0; i < img.getWidth(); i++)
			for (int j = 0; j < img.getHeight(); j++) {
				c = new Color(img.getRGB(i, j), true);
				if (c.getAlpha() > 100) {
					switch (type) {
					case 0:
						d = getClosestColorRGB(c, cp);
						break;
					case 1:
						d = getClosestColorHSV(c, cp);
						break;
					case 2:
						d = getClosestColorColton(c, cp);
					}
					img.setRGB(i, j, d.getRGB());
				}
			}
	}

	private static Color getClosestColorColton(Color c, ColorPalette p) {
		float delta = 765.0F;
		Color closest = c;
		float[] cvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null);
		float[] rvals = new float[3];
		Color pc = null;
		float[] vals = new float[3];
		for (int i = 0; i < p.colors.size(); i++) {
			pc = (Color) p.colors.get(i);
			vals = Color.RGBtoHSB(pc.getRed(), pc.getGreen(), pc.getBlue(),
					vals);

			float diff = Math.abs(cvals[0] - vals[0]);
			diff += Math.abs(cvals[1] - vals[1]);
			diff += Math.abs(cvals[2] - vals[2]);
			if (diff < delta) {
				delta = diff;
				closest = pc;
				rvals = Color.RGBtoHSB(pc.getRed(), pc.getGreen(),
						pc.getBlue(), null);
			}
		}
		Color res = new Color(Color.HSBtoRGB(rvals[0], rvals[1], cvals[2]));
		return res;
	}

	private static Color getClosestColorHSV(Color c, ColorPalette p) {
		float delta = 765.0F;
		Color closest = c;
		float[] cvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null);
		Color pc = null;
		float[] vals = new float[3];
		for (int i = 0; i < p.colors.size(); i++) {
			pc = (Color) p.colors.get(i);
			vals = Color.RGBtoHSB(pc.getRed(), pc.getGreen(), pc.getBlue(),
					vals);

			float diff = Math.abs(cvals[0] - vals[0]);
			diff += Math.abs(cvals[1] - vals[1]);
			diff += Math.abs(cvals[2] - vals[2]);
			if (diff < delta) {
				delta = diff;
				closest = pc;
			}
		}
		return closest;
	}

	private static Color getClosestColorRGB(Color c, ColorPalette p) {
		int delta = 765;
		Color closest = c;
		for (int i = 0; i < p.colors.size(); i++) {
			Color pc = (Color) p.colors.get(i);
			int diff = Math.abs(c.getRed() - pc.getRed());
			diff += Math.abs(c.getGreen() - pc.getGreen());
			diff += Math.abs(c.getBlue() - pc.getBlue());
			if (diff < delta) {
				delta = diff;
				closest = pc;
			}
		}
		return closest;
	}

}

