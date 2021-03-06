package pl.karol202.cncprinter;

import java.util.HashMap;

class Movement
{
	private Machine machine;
	private HashMap<Word, Float> modals;
	private HashMap<Word, Float> notModals;
	
	Movement(Machine machine, HashMap<Word, Float> modals, HashMap<Word, Float> notModals)
	{
		this.machine = machine;
		this.modals = modals;
		this.notModals = notModals;
	}
	
	void doRapidMovement()
	{
		machine.setXSpeedToMax();
		machine.setYSpeedToMax();
		machine.setZSpeedToMax();
		if(notModals.containsKey(Word.AXIS_X)) machine.goToX(notModals.get(Word.AXIS_X));
		if(notModals.containsKey(Word.AXIS_Y)) machine.goToY(notModals.get(Word.AXIS_Y));
		if(notModals.containsKey(Word.AXIS_Z)) machine.goToZ(notModals.get(Word.AXIS_Z));
		waitForEndOfMove();
	}
	
	void doInterpolatedMovement()
	{
		float speed = modals.get(Word.SPEED);
		
		float x = notModals.containsKey(Word.AXIS_X) ? notModals.get(Word.AXIS_X) : machine.getX();
		float y = notModals.containsKey(Word.AXIS_Y) ? notModals.get(Word.AXIS_Y) : machine.getY();
		float z = notModals.containsKey(Word.AXIS_Z) ? notModals.get(Word.AXIS_Z) : machine.getZ();
		float xDist = Math.abs(x - machine.getX());
		float yDist = Math.abs(y - machine.getY());
		float zDist = Math.abs(z - machine.getZ());
		
		float max = Math.max(xDist, Math.max(yDist, zDist));
		float time = max / speed;
		float xSpeed = xDist / time;
		float ySpeed = yDist / time;
		float zSpeed = zDist / time;
		
		if(xSpeed > Machine.X_MAX_SPEED)
		{
			float divisor = xSpeed / Machine.X_MAX_SPEED;
			xSpeed /= divisor;
			ySpeed /= divisor;
			zSpeed /= divisor;
		}
		if(ySpeed > Machine.Y_MAX_SPEED)
		{
			float divisor = ySpeed / Machine.Y_MAX_SPEED;
			xSpeed /= divisor;
			ySpeed /= divisor;
			zSpeed /= divisor;
		}
		if(zSpeed > Machine.Z_MAX_SPEED)
		{
			float divisor = zSpeed / Machine.Z_MAX_SPEED;
			xSpeed /= divisor;
			ySpeed /= divisor;
			zSpeed /= divisor;
		}
		machine.setXSpeed(xSpeed);
		machine.setYSpeed(ySpeed);
		machine.setZSpeed(zSpeed);
		
		machine.goTo(x, y, z);
		waitForEndOfMove();
		machine.stopAll();
	}
	
	private void waitForEndOfMove()
	{
		while(machine.isMovingX() || machine.isMovingY() || machine.isMovingZ()) Thread.yield();
	}
}
