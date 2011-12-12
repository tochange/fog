package com.fog;

public interface VelocityTarget {
	void addUniformFlow(float uComponent, float vComponent);
	void addFlowAt(int x, int y, float u, float v);
}
