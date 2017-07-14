package pl.karol202.cncclient.cnc;

public class PreviewPoint
{
	private float x;
	private float y;
	private float z;
	private boolean interpolated;
	
	PreviewPoint(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.interpolated = false;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public boolean isInterpolated()
	{
		return interpolated;
	}
	
	void setInterpolated(boolean interpolated)
	{
		this.interpolated = interpolated;
	}
}