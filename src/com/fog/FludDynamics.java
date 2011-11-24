package com.fog;

import java.util.Arrays;


public class FludDynamics {
	
	private int width;
	private int height;
	private int stride;
	
	private float[] u, v;
	private float[] u_prev, v_prev;
	
	private float[] density;
	private float[] density_prev;
	
	public FludDynamics(int width, int height)
	{
		this.width = width;
		this.height = height;
		stride = width + 2;
		int n = (width+2) * (height+2);
		u = new float[n];
		v = new float[n];
		u_prev = new float[n];
		v_prev = new float[n];
		
		density = new float[n];
		density_prev = new float[n];
	}
	
	public void addDensityAt(int x, int y)
	{
		density_prev[IX(x, y)] = 10;
	}

	public void step(float dt) {
		//get_from_UI( dens_prev, u_prev, v_prev );
		float visc = 0.3f;
		vel_step (dt, u, v, u_prev, v_prev, visc);
		dens_step (density, density_prev, u, v, dt);
		//draw_dens ( N, dens );
	}
	
	private void dens_step(float[] x, float[] x0, float[] u, float[] v, float dt)
	{
		float diff = 0.1f;
		float[] tmp;
		add_source(x, x0, dt);
		
		tmp = x; x = x0; x0 = tmp;
		diffuse(dt, 0, x, x0, diff);
		
		tmp = x; x = x0; x0 = tmp;
		advect(dt, 0, x, x0, u, v);
	}
	
	private void vel_step(float dt, float[] u, float[] v, float[] u0, float[] v0, float visc)
	{
		float[] tmp;
		add_source(u, u0, dt );
		add_source(v, v0, dt );
		
		//SWAP ( u0, u );
		tmp = u0; u0 = u; u = tmp;
		diffuse(dt, 1, u, u0, visc);
		
		//SWAP ( v0, v );
		tmp = v0; v0 = v; v = tmp;
		diffuse (dt, 2, v, v0, visc);
		
		project(u, v, u0, v0);
		
		//SWAP ( u0, u ); SWAP ( v0, v );
		tmp = u0; u0 = u; u = tmp;
		tmp = v0; v0 = v; v = tmp;
		
		advect(dt, 1, u, u0, u0, v0);
		advect(dt, 2, v, v0, u0, v0);
		
		project(u, v, u0, v0);
	}
	
	private void project(float[] u, float[] v, float[] p, float[] div)
	{
		int i, j, k;
		float h = 1.0f / width; // TODO: or / width??
		int index = stride + 1; // start at 1,1
		for ( i=1 ; i<=height ; i++ ) {
			for ( j=1 ; j<=width ; j++ ) {
//				div[IX(i,j)] = -0.5f * h *(u[IX(i+1,j)]-u[IX(i-1,j)]+v[IX(i,j+1)]-v[IX(i,j-1)]);
//				p[IX(i,j)] = 0;
				div[index] = -0.5f * h *(u[index+1]-u[index-1]+v[index+stride]-v[index-stride]);
				p[index] = 0;
				index++;
			}
			index += stride - width;
		}
		set_bnd(0, div ); set_bnd (0, p);
		
		for ( k=0 ; k<20 ; k++ ) {
			index = stride + 1; // start at 1,1
			for ( i=1 ; i<=height ; i++ ) {
				for ( j=1 ; j<=width ; j++ ) {
					//p[IX(i,j)] = (div[IX(i,j)]+p[IX(i-1,j)]+p[IX(i+1,j)]+ p[IX(i,j-1)]+p[IX(i,j+1)])/4;
					p[index] = (div[index]+p[index-1]+p[index+1]+ p[index-stride]+p[index+stride])/4;
					index++;
				}
			}
			index += stride - width;
			set_bnd(0, p);
		}
		
		index = stride + 1; // start at 1,1
		for ( i=1 ; i<=height ; i++ ) {
			for ( j=1 ; j<=width ; j++ ) {
//				u[IX(i,j)] -= 0.5*(p[IX(i+1,j)]-p[IX(i-1,j)])/h;
//				v[IX(i,j)] -= 0.5*(p[IX(i,j+1)]-p[IX(i,j-1)])/h;
				u[index] -= 0.5*(p[index+1]-p[index-1])/h;
				v[index] -= 0.5*(p[index+stride]-p[index-stride])/h;
				index++;
			}
			index += stride - width;
			set_bnd(1, u);
			set_bnd(2, v);
		}
	}
	
	private void diffuse(float dt, int b, float[] x, float[] x0, float diff)
	{
		float a = dt * diff * width * height;
		for (int k=0 ; k<20 ; k++ ) {
			int index = stride + 1; // start at 1,1
			for (int i=1 ; i<=width ; i++ ) {
				for (int j=1 ; j<=height; j++ ) {
					x[index] = (x0[index] + a*(x[index-1]+x[index+1]+x[index-stride]+x[index+stride]))/(1+4*a);
					index++;
				}
				index += stride - width;
			}
			set_bnd(b, x);
		}
	}
	
	private void advect(float dt, int b, float[] d, float[] d0, float[] u, float[] v)
	{
		int i, j, i0, j0, i1, j1; float x, y, s0, t0, s1, t1, dt0;
		dt0 = dt * width; // or * height??
		for ( i=1 ; i<=width ; i++ ) {
			for ( j=1 ; j<=height ; j++ ) {
				x = i-dt0*u[IX(i,j)];
				y = j-dt0*v[IX(i,j)];
				
				if (x<0.5) x=0.5f;
				if (x>width+0.5) x = width + 0.5f;
				i0=(int)x;
				i1=i0+1;
				
				if (y<0.5) y=0.5f;
				if (y>height+0.5) y = height + 0.5f;
				j0=(int)y;
				j1=j0+1;
				
				s1 = x-i0;
				s0 = 1-s1;
				t1 = y-j0;
				t0 = 1-t1;
				
				d[IX(i,j)] = s0 * (t0 * d0[IX(i0, j0)] + t1 * d0[IX(i0,j1)])+s1*(t0*d0[IX(i1,j0)]+t1*d0[IX(i1,j1)]);
			}
		}
		set_bnd(b, d);
	}
	
	private int IX(int x, int y)
	{
		return x + y*stride;
	}
	
	void set_bnd (int b, float[] x)
	{
		int N = width; // TODO: or height?
		for (int i=1 ; i<=N ; i++ ) {
			x[IX(0 ,i)] = b==1 ? -x[IX(1,i)] : x[IX(1,i)];
			x[IX(N+1,i)] = b==1 ? -x[IX(N,i)] : x[IX(N,i)];
			x[IX(i,0 )] = b==2 ? -x[IX(i,1)] : x[IX(i,1)];
			x[IX(i,N+1)] = b==2 ? -x[IX(i,N)] : x[IX(i,N)];
		}
		x[IX(0 ,0 )] = 0.5f*(x[IX(1,0 )]+x[IX(0 ,1)]);
		x[IX(0	,N+1)]	=	0.5f*(x[IX(1,N+1)]+x[IX(0	,N )]);
		x[IX(N+1,0 )] = 0.5f*(x[IX(N,0 )]+x[IX(N+1,1)]);
		x[IX(N+1,N+1)] = 0.5f*(x[IX(N,N+1)]+x[IX(N+1,N)]);
	} 

	private void add_source(float[] x, float[] s, float dt)
	{
		int size=width*height;
		for (int i=0; i<size; i++) x[i] += dt*s[i];
	}	
	
	public float[] getDensity() {
		return density;
	}

	public int getWidth() { return width; }
	public int getHeight() { return height; }

	public void clearStartingConditions() {
		Arrays.fill(density_prev, 0f);
		Arrays.fill(u_prev, 0f);
		Arrays.fill(v_prev, 0f);
	}

	public void addSomeRandomFlow() {
		u_prev[0] = 50;
		v_prev[0] = 50;
		u_prev[1] = 50;
		v_prev[1] = 50;
		
	}

	public int getStride() {
		return stride;
	}
}
