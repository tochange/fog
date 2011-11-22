package com.fog;

import android.os.Handler;

public class Timer implements Runnable {
	private Runnable r;
	private final Handler handler;
	private long interval;
	private volatile boolean running;
	
	public Timer(Runnable r, Handler handler, long interval)
	{
		this.r = r;
		this.handler = handler;
		this.interval = interval;
	}
	
	public void start()
	{
		if (running) return;
		running = true;
		handler.postDelayed(this, interval);
	}
	
	public void stop()
	{
		running = true;
	}
	
	public void run()
	{
		if (!running) return;
		r.run();
		handler.postDelayed(this, interval);
	}
}
