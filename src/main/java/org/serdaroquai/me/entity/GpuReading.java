package org.serdaroquai.me.entity;

public class GpuReading {

	int index;
	String powerDraw;
	int temp;
	int gpuUtilization;
	int memoryUtilization;
	
	public GpuReading(int index, String powerDraw, int temp, int gpuUtilization, int memoryUtilization) {
		super();
		this.index = index;
		this.powerDraw = powerDraw;
		this.temp = temp;
		this.gpuUtilization = gpuUtilization;
		this.memoryUtilization = memoryUtilization;
	}
	
	public int getIndex() {
		return index;
	}
	public String getPowerDraw() {
		return powerDraw;
	}
	public int getTemp() {
		return temp;
	}
	public int getGpuUtilization() {
		return gpuUtilization;
	}
	public int getMemoryUtilization() {
		return memoryUtilization;
	}

	@Override
	public String toString() {
		return String.format("GPU[%s]: %s W, %s C, %%%s, %%%s", index, powerDraw, temp, gpuUtilization, memoryUtilization);
	}
	
	
	
	
}
