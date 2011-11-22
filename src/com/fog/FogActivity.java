package com.fog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class FogActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new FogDrawer(this));
    }
    
    private static class FogDrawer extends View
    {
    	public FogDrawer(Context context)
    	{
    		super(context);
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas)
    	{    	}
    }
}