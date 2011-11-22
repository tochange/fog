package com.fog;

public class VectorField2d {
	private float[] xs;
	private float[] ys;
	
	private int width;
	private int height;
	private int stride;
	
	public VectorField2d(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.stride = width;
		xs = new float[width * height];
		ys = new float[width * height];
	}
}
