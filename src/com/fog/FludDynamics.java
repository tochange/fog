package com.fog;

public class FludDynamics {
	private VectorField2d velocity;
	private VectorField2d velocity_prev;
	
	private ScalarField2d density;
	private ScalarField2d density_prev;
	
	public FludDynamics()
	{
		velocity = new VectorField2d(100 + 2, 100 + 2);
		velocity_prev = new VectorField2d(100 + 2, 100 + 2);
		
		density = new ScalarField2d(100 + 2, 100 + 2);
		density_prev = new ScalarField2d(100 + 2, 100 + 2);
	}

	public void step(float dt) {
	}

	public ScalarField2d getDensity() {
		return density;
	}
}
