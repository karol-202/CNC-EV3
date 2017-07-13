package pl.karol202.cncprinter;

import static pl.karol202.cncprinter.ManualControlAction.*;

class ManualControl
{
	private Machine machine;
	
	ManualControl(Machine machine)
	{
		this.machine = machine;
	}
	
	void manualControl(Axis axis, ManualControlAction action, int speed)
	{
		if(axis == Axis.X) controlX(action, speed);
		else if(axis == Axis.Y) controlY(action, speed);
		else if(axis == Axis.Z) controlZ(action, speed);
	}
	
	private void controlX(ManualControlAction action, int speed)
	{
		machine.setXSpeedClamp(speed);
		if(isMoveAction(action)) machine.goToX(action == MOVE_LEFT ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
		else if(action == STOP) machine.floatX();
		else if(action == ZERO) machine.zeroX();
	}
	
	private void controlY(ManualControlAction action, int speed)
	{
		machine.setYSpeedClamp(speed);
		if(isMoveAction(action)) machine.goToY(action == MOVE_LEFT ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
		else if(action == STOP) machine.floatY();
		else if(action == ZERO) machine.zeroY();
	}
	
	private void controlZ(ManualControlAction action, int speed)
	{
		machine.setZSpeedClamp(speed);
		if(isMoveAction(action)) machine.goToZ(action == MOVE_LEFT ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
		else if(action == STOP) machine.floatZ();
		else if(action == ZERO) machine.zeroZ();
	}
	
	private boolean isMoveAction(ManualControlAction action)
	{
		return action == MOVE_LEFT || action == MOVE_RIGHT;
	}
}