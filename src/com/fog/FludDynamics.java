package com.fog;

public class FludDynamics {
	private VectorField2d v;
	private ScalarField2d d;
	
	public FludDynamics()
	{
		v = new VectorField2d(100, 100);
		d = new ScalarField2d(100, 100);
	}

	public void step(float f) {
	}
}
