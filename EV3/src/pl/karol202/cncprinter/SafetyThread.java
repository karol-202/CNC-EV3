package pl.karol202.cncprinter;

import lejos.hardware.sensor.EV3TouchSensor;

class SafetyThread implements Runnable
{
	private Machine machine;
	private EV3TouchSensor sensor;
	
	SafetyThread(Machine machine)
	{
		this.machine = machine;
		this.sensor = machine.getTouchSensor();
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			if(getSample() == 0) continue;
			machine.stopAll();
			System.exit(0);
		}
	}
	
	private float getSample()
	{
		float[] sample = new float[sensor.sampleSize()];
		sensor.fetchSample(sample, 0);
		return sample[0];
	}
}
