package pl.karol202.cncprinter;

import java.util.HashMap;

public class MovementLinear
{
	public static void movementRapid(HashMap<Word, Float> modals, HashMap<Word, Float> nModals)
	{
		Machine.setSpeedX(Machine.X_MAX_SPEED);
		Machine.setSpeedY(Machine.Y_MAX_SPEED);
		Machine.setSpeedZ(Machine.Z_MAX_SPEED);
		if(nModals.containsKey(Word.AXIS_X))
			Machine.goToX(nModals.get(Word.AXIS_X).intValue());
		if(nModals.containsKey(Word.AXIS_Y))
			Machine.goToY(nModals.get(Word.AXIS_Y).intValue());
		if(nModals.containsKey(Word.AXIS_Z))
			Machine.goToZ(nModals.get(Word.AXIS_Z).intValue());
		waitForEnd();
	}
	
	public static void movementInterpolation(HashMap<Word, Float> modals, HashMap<Word, Float> nModals)
	{
		float speed = modals.get(Word.SPEED);
		int x = Machine.AXIS_NO_CHANGES;
		int y = Machine.AXIS_NO_CHANGES;
		int z = Machine.AXIS_NO_CHANGES;
		int xDist = 0;
		int yDist = 0;
		int zDist = 0;
		float xSpeed;
		float ySpeed;
		float zSpeed;
		
		if(nModals.containsKey(Word.AXIS_X))
		{
			x = nModals.get(Word.AXIS_X).intValue();
			xDist = Math.abs(x - Machine.getX());
		}
		if(nModals.containsKey(Word.AXIS_Y))
		{
			y = nModals.get(Word.AXIS_Y).intValue();
			yDist = Math.abs(y - Machine.getY());
		}
		if(nModals.containsKey(Word.AXIS_Z))
		{
			z = nModals.get(Word.AXIS_Z).intValue();
			zDist = Math.abs(z - Machine.getZ());
		}
		
		float max = Math.max(xDist, Math.max(yDist, zDist));
		float time = max / speed;
		xSpeed = xDist / time;
		ySpeed = yDist / time;
		zSpeed = zDist / time;
		
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
		Machine.setSpeedX(xSpeed);
		Machine.setSpeedY(ySpeed);
		Machine.setSpeedZ(zSpeed);
		
		Machine.goTo(x, y, z);
		waitForEnd();
	}
	
	private static void waitForEnd()
	{
		while(Machine.isMovingX() ||
			  Machine.isMovingY() ||
			  Machine.isMovingZ())
		{
			Thread.yield();
		}
	}
}
