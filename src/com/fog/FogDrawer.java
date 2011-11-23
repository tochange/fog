package com.fog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class FogDrawer extends View implements OnTouchListener
{
	private FludDynamics fluidDynamics;
	private int[] colors;

	public FogDrawer(Context context, FludDynamics fluidDynamics)
	{
		super(context);
		this.fluidDynamics = fluidDynamics;
		setOnTouchListener(this);
		
		colors = new int[fluidDynamics.getWidth() * fluidDynamics.getHeight()];
	}
		
	@Override
	protected void onDraw(Canvas canvas)
	{ 
		float[] density = fluidDynamics.getDensity();
		
		float max = Float.MIN_VALUE;
		for (int i=0; i<density.length; i++) 
			if (density[i] > max) max = density[i];
		
		Log.i("FogDrawer", "max: " + max);
		
		
		
		int index = 0;
		for (int y=0; y<fluidDynamics.getHeight(); y++)
		{
			for (int x=0; x<fluidDynamics.getWidth(); x++)
			{
				int c = (int)(255 * density[index] / max);
				//int c =  (x * 4 + y*4) & 0xff;
				colors[index++] = (0xff << 24) | (c<< 16) | (c << 8) | c;
			}
		}
		
		Rect dest = new Rect(0, 0, 0 + getWidth(), 0 + getHeight());
		
		Bitmap bm = Bitmap.createBitmap(colors, fluidDynamics.getWidth(), fluidDynamics.getHeight(), Config.ARGB_8888);
		canvas.drawBitmap(bm, null, dest, null);
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