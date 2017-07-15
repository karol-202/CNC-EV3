package pl.karol202.cncclient.cnc;

import java.util.ArrayList;
import java.util.List;

public class MoveRecorder
{
	private MachineState machineState;
	private List<RecordedPoint> recordedPoints;
	
	public MoveRecorder(MachineState machineState)
	{
		this.machineState = machineState;
		this.recordedPoints = new ArrayList<>();
	}
	
	public void clear()
	{
		recordedPoints.clear();
	}
	
	public void recordPoint()
	{
		RecordedPoint point = new RecordedPoint(machineState.getX(), machineState.getY(), machineState.getZ());
		recordedPoints.add(point);
	}
	
	public List<RecordedPoint> getRecordedPoints()
	{
		return new ArrayList<>(recordedPoints);
	}
}