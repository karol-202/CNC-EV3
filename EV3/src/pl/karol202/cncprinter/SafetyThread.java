package pl.karol202.cncprinter;

import lejos.hardware.sensor.EV3TouchSensor;

public class SafetyThread implements Runnable
{
	private EV3TouchSensor sensor;
	
	public SafetyThread(EV3TouchSensor sensor)
	{
		this.sensor = sensor;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			float[] sample = new float[sensor.sampleSize()];
			sensor.fetchSample(sample, 0);
			if(sample[0] != 0)
			{
				Machine.stopAll();
				System.exit(0);
			}
		}
	}
}
