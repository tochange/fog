package com.fog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

public class FogActivity extends Activity implements Runnable {
	
    private FludDynamics fluidDynamics;
    private Timer timer;
    private View fogDrawer;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        fogDrawer = new FogDrawer(this); 
        setContentView(fogDrawer);
        
        fluidDynamics = new FludDynamics();
        
        timer = new Timer(this, new Handler(), 40);
    }
    
    private static class FogDrawer extends View
    {
    	public FogDrawer(Context context)
    	{
    		super(context);
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas)
    	{ 
    		
    	}
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	timer.start();
    }
    
    @Override 
    protected void onPause()
    {
    	timer.stop();
    	super.onPause();
    }

	@Override
	public void run() {
		fluidDynamics.step(0.04f);
		fogDrawer.invalidate();
	}
}