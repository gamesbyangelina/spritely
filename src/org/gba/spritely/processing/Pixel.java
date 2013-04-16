package org.gba.spritely.processing;

public class Pixel implements Comparable {
	int x;
	int y;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int compareTo(Object arg0) {
		Pixel p = (Pixel) arg0;
		if ((p.x == this.x) && (p.y == this.y))
			return 0;
		return -1;
	}
}
