package com.fog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.view.View;

class FogDrawer extends View
{
	private FludDynamics fluidDynamics;

	public FogDrawer(Context context, FludDynamics fluidDynamics)
	{
		super(context);
		this.fluidDynamics = fluidDynamics;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{ 
		float[] density = fluidDynamics.getDensity();
		
		int[] colors = new int[fluidDynamics.getWidth() * fluidDynamics.getHeight()];
		
		int index = 0;
		for (int y=0; y<fluidDynamics.getHeight(); y++)
		{
			for (int x=0; x<fluidDynamics.getWidth(); x++)
			{
				int c = (int)density[index] & 0xff;
				colors[index++] = c << 16 + c << 8 + c;
			}
		}
		
		//Bitmap bm = Bitmap.createBitmap(colors, fluidDynamics.getWidth(), fluidDynamics.getHeight(), Config.ARGB_8888);
	}
}