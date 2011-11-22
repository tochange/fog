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
		ScalarField2d density = fluidDynamics.getDensity();
		
		int[] colors = new int[density.getWidth() * density.getHeight()];
		
		Config config = Config.ARGB_8888;
		int index = 0;
		for (int y=0; y<density.getHeight(); y++)
		{
			for (int x=0; x<density.getWidth(); x++)
			{
				int c = (int)density.get(x, y) & 0xff;
				colors[index++] = c << 16 + c << 8 + c;
			}
		}
				
		Bitmap bm = Bitmap.createBitmap(colors, density.getWidth(), density.getHeight(), config);
	}
}