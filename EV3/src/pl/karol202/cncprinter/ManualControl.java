package pl.karol202.cncprinter;

import static pl.karol202.cncprinter.ManualControlAction.MOVE_LEFT;
import static pl.karol202.cncprinter.ManualControlAction.STOP;

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
		if(action != STOP) machine.goToX(action == MOVE_LEFT ? Integer.MIN_VALUE : Integer.MAX_VALUE);
		else machine.floatX();
	}
	
	private void controlY(ManualControlAction action, int speed)
	{
		machine.setYSpeedClamp(action != STOP ? speed : 0);
		if(action != STOP) machine.goToY(action == MOVE_LEFT ? Integer.MIN_VALUE : Integer.MAX_VALUE);
		else machine.floatY();
	}
	
	private void controlZ(ManualControlAction action, int speed)
	{
		machine.setZSpeedClamp(action != STOP ? speed : 0);
		if(action != STOP) machine.goToZ(action == MOVE_LEFT ? Integer.MIN_VALUE : Integer.MAX_VALUE);
		else machine.floatZ();
	}
}