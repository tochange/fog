package com.fog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class FogView extends View implements OnTouchListener
{
	private FludDynamics fluidDynamics;
	private int[] colors;
	private FixedFrameRateTimer timer; 
	
	float max = Float.MIN_VALUE;
	
	public FogView(Context context, FludDynamics fluidDynamics, FixedFrameRateTimer timer)
	{
		super(context);
		this.fluidDynamics = fluidDynamics;
		this.timer = timer;
		
		setOnTouchListener(this);
		
		colors = new int[fluidDynamics.getWidth() * fluidDynamics.getHeight()];
	}
		
	@Override
	protected void onDraw(Canvas canvas)
	{ 
		float[] density = fluidDynamics.getDensity();
		
		for (int i=0; i<density.length; i++) 
			if (density[i] > max) max = density[i];
		
		int index = 0;
		int dindex = fluidDynamics.getStride() + 1;
		for (int y=0; y<fluidDynamics.getHeight(); y++)
		{
			for (int x=0; x<fluidDynamics.getWidth(); x++)
			{
				int c = (int)(255 * density[dindex] / max);
				colors[index++] = (0xff << 24) | (c<< 16) | (c << 8) | c;
				dindex++;
			}
			dindex+=2;
		}
		
		Rect dest = new Rect(0, 0, 0 + getWidth(), 0 + getHeight());
		
		Bitmap bm = Bitmap.createBitmap(colors, fluidDynamics.getWidth(), fluidDynamics.getHeight(), Config.ARGB_8888);
		canvas.drawBitmap(bm, null, dest, null);
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawText("dt: " + timer.getInterval() + "ms", 40, 40, paint);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent me) {
		
		
		int x  = (int)(me.getX() * fluidDynamics.getWidth() / getWidth());
		int y  = (int)(me.getY() * fluidDynamics.getHeight() / getHeight());
		if (x > 0 & x < fluidDynamics.getWidth() && y > 0 && y < fluidDynamics.getHeight())
			fluidDynamics.addDensityAt(x, y);
		return true;
	}
}