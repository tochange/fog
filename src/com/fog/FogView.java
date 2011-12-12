package com.fog;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
	private GravityAdder adder; // temporary. just for testing accelerometer.
	private int amp;
	
	public FogView(Context context, FludDynamics fluidDynamics, FixedFrameRateTimer timer, GravityAdder adder)
	{
		super(context);
		this.fluidDynamics = fluidDynamics;
		this.timer = timer;
		
		setOnTouchListener(this);
		
		colors = new int[fluidDynamics.getWidth() * fluidDynamics.getHeight()];
		
		this.adder = adder;
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
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawText("fps=" + timer.getInterval() + "ms", 0, 20, paint);
		canvas.drawText("v= " + formatter.format(fluidDynamics.getDiffusionRate()), 0, 40, paint);
		canvas.drawText("d=" + formatter.format(fluidDynamics.getViscosity()), 0, 60, paint);
		
		canvas.drawText("x=" + formatter.format(adder.getX()), 0, 100, paint);
		canvas.drawText("y=" + formatter.format(adder.getY()), 0, 120, paint);
		canvas.drawText("z=" + formatter.format(adder.getZ()), 0, 140, paint);
		canvas.drawText("amp=" + formatter.format(amp), 0, 160, paint);
	}
	
	public void setAmp(int amp)
	{
		this.amp = amp;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent me) {
		for (int i=0; i<me.getPointerCount(); i++)
		{
			int x  = (int)(me.getX(i) * fluidDynamics.getWidth() / getWidth());
			int y  = (int)(me.getY(i) * fluidDynamics.getHeight() / getHeight());
			
			fluidDynamics.addDensityAt(x, y);
			fluidDynamics.addDensityAt(x+1, y);
			fluidDynamics.addDensityAt(x, y+1);
			fluidDynamics.addDensityAt(x-1, y);
			fluidDynamics.addDensityAt(x, y-1);
		}
		
		return true;
	}
}