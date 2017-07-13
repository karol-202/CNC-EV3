package pl.karol202.cncprinter;

import lejos.hardware.sensor.EV3TouchSensor;

class SafetyThread implements Runnable
{
	private Main main;
	private EV3TouchSensor sensor;
	
	SafetyThread(Main main, Machine machine)
	{
		this.main = main;
		this.sensor = machine.getTouchSensor();
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			if(getSample() == 0) continue;
			main.stop();
		}
	}
	
	private float getSample()
	{
		float[] sample = new float[sensor.sampleSize()];
		sensor.fetchSample(sample, 0);
		return sample[0];
	}
}
