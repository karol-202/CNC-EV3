package pl.karol202.cncclient.cnc;

public class RecordedPoint extends PreviewPoint
{
	private float time;
	
	RecordedPoint(float x, float y, float z)
	{
		super(x, y, z);
	}
	
	public float getTime()
	{
		return time;
	}
	
	void setTime(float time)
	{
		this.time = time;
	}
}