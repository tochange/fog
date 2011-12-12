package com.fog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class FogActivity extends Activity implements Runnable, OnSharedPreferenceChangeListener {
	
    private FludDynamics fluidDynamics;
    private FixedFrameRateTimer timer;
    private FogView fogDrawer; // should be view.
	private SharedPreferences prefs;
    
	private GravityAdder adder;
	private MicAdder adder2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        adder = new GravityAdder((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        adder2 = new MicAdder();
        
        fluidDynamics = new FludDynamics(32, 32);
        
        timer = new FixedFrameRateTimer(this, new Handler(), 40);
        
        fogDrawer = new FogView(this, fluidDynamics, timer, adder);
        fogDrawer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(fogDrawer);
    }
    
	@Override
	protected void onStart()
	{
		super.onStart();
		prefs.registerOnSharedPreferenceChangeListener(this);
		updateSimulation(prefs);
	}
	
    @Override
    protected void onResume()
    {
    	super.onResume();
    	adder.resume();
    	timer.start();
    }

	@Override
	public void run() {
		//fluidDynamics.addUniformFlow(adder.getUComponent() / 20.f, adder.getVComponent() / 20.0f);
		int amp = adder2.getAmplitude();
		fluidDynamics.addFlowAt(5,5, 0.0f, amp / 10.0f);
		
		fluidDynamics.step(millisToSeconds(timer.getInterval()));
		fogDrawer.setAmp(amp);
		fogDrawer.invalidate();
		
		fluidDynamics.clearStartingConditions();
	}
	
    @Override 
    protected void onPause()
    {
    	timer.stop();
    	adder.pause();
    	super.onPause();
    }

    @Override
	protected void onStop()
    {
    	prefs.unregisterOnSharedPreferenceChangeListener(this);
    	super.onStop();
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		updateSimulation(preferences);
	}

	private void updateSimulation(SharedPreferences preferences) {
		float visc = Float.parseFloat(preferences.getString("viscosity", "0"));
		float diff = Float.parseFloat(preferences.getString("diff_rate", "0.1"));
		fluidDynamics.setViscosity(visc);
		fluidDynamics.setDiffusionRate(diff);
		
		int target_fps = Integer.parseInt(prefs.getString("target_fps", "20"));
		timer.setTargetInterval(1000 / target_fps);
	}
}