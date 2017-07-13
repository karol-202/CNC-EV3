package pl.karol202.cncclient.cnc;

public class MachineState
{
	private boolean running;
	private boolean paused;
	private float x;
	private float y;
	private float z;
	
	public MachineState()
	{
		this.running = false;
		this.paused = false;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}
	
	public float getX()
	{
		return x;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public void setZ(float z)
	{
		this.z = z;
	}
}