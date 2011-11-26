package com.fog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class FogActivity extends Activity implements Runnable {
	
    private FludDynamics fluidDynamics;
    private FixedFrameRateTimer timer;
    private View fogDrawer;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        fluidDynamics = new FludDynamics(32, 32);
        
        timer = new FixedFrameRateTimer(this, new Handler(), 40);
        
        fogDrawer = new FogView(this, fluidDynamics, timer);
        fogDrawer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(fogDrawer);
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	Log.i("fog", "resuming");
    	timer.start();
    }
    
    @Override 
    protected void onPause()
    {
    	timer.stop();
    	Log.i("fog", "pausing");
    	super.onPause();
    }

	@Override
	public void run() {
		// and some flow...
		fluidDynamics.addSomeRandomFlow();
		
		fluidDynamics.step(millisToSeconds(timer.getInterval()));
		fogDrawer.invalidate();
		
		fluidDynamics.clearStartingConditions();
	}
	
	private float millisToSeconds(long millis)
	{
		return millis / 1000f;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.openPreferences:
	        Intent intent = new Intent(this, SimulationPreferences.class);
	        startActivity(intent);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}