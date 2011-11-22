package com.fog;

import android.os.Handler;
import android.view.View;

public class FrameRateRedrawer {
	private final long delay;
	private Handler handler;
	private View target;
	
	public FrameRateRedrawer(float framerate, View target)
	{
		handler = new Handler();
		delay = (long)(1000 / framerate);
		this.target = target;
		
		handler.postDelayed(new Invalidator(), delay);
	}
	
	private class Invalidator implements Runnable
	{
		@Override
		public void run() {
			target.invalidate();
			handler.postDelayed(new Invalidator(), delay);
		}
	}
}
