package com.fog;

public class ScalarField2d {
	private float[] d;
	private int width;
	private int height;
	private int stride;
	
	public ScalarField2d(int width, int height)
	{
		this.width = width;
		this.height = height;
		stride = width;
		d = new float[width * height];
	}
}
