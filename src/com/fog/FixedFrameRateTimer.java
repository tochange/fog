package com.fog;

import android.os.Handler;
import android.os.SystemClock;

public class FixedFrameRateTimer implements Runnable {
	private Runnable r;
	private final Handler handler;
	private long interval;
	private boolean running;

	private long reportInterval;

	public FixedFrameRateTimer(Runnable r, Handler handler, long interval)
	{
		this.r = r;
		this.handler = handler;
		this.interval = interval;
		reportInterval = interval;
	}
	
	public void start()
	{
		if (running) return;
		running = true;
		handler.postDelayed(this, interval);
	}
	
	public void stop()
	{
		running = false;
	}
	
	public void run()
	{
		if (!running) return;
		
		long start = SystemClock.uptimeMillis();
		r.run();
		long end = SystemClock.uptimeMillis();
		
		long duration = end - start;
		long adjustedInterval = interval - duration;
		if (adjustedInterval < 0)
		{
			reportInterval = duration;
			handler.post(this);
		}
		else
		{
			reportInterval = interval;
			handler.postDelayed(this, adjustedInterval);
		}
	}

	public long getInterval() {
		return reportInterval;
	}

	public void setTargetInterval(int interval) {
		this.interval = interval;
	}
}
