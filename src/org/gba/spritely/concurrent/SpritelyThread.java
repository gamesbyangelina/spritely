package org.gba.spritely.concurrent;

import org.gba.spritely.Spritely;
import org.gba.spritely.SpritelyUI;

public class SpritelyThread extends Thread{
	
	public boolean purgeGoogle = false;
	public int size = -1;
	private Spritely s;
	
	public SpritelyThread(Spritely s){
		this.s = s;
	}
	
	@Override
	public void run() {
		s.write(s.search());
		SpritelyUI.notifySearchComplete();
	}


}
