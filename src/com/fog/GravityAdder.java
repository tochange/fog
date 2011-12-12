package com.fog;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GravityAdder implements SensorEventListener {
	private SensorManager _manager;
	private float ax, ay, az;
	
	public GravityAdder(SensorManager manager)
	{
		_manager = manager;
	}
	
	public void resume()
	{
		_manager.registerListener(
				this,
				_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}
	
	public void pause()
	{
		_manager.unregisterListener(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent se) {
		 if (se.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
			ax = se.values[0];
			ay = se.values[1];
			az = se.values[2];
		 }
	}
	
	public float getX() { return ax; }
	public float getY() { return ay; }
	public float getZ() { return az; }
	
	public void onAccuracyChanged(Sensor se, int accuracy) { }

	public float getUComponent() {
		return -ax;
	}

	public float getVComponent() {
		return ay;
	}
}