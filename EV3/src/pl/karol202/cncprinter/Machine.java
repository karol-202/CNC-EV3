package pl.karol202.cncprinter;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class Machine
{
	public static final int AXIS_NO_CHANGES = Integer.MIN_VALUE;
	
	public static final float X_SCALE = 0.8181F;
	public static final float Y_SCALE = 1.0F;
	public static final float Z_SCALE = 180.0F;
	
	public static final float Y_MIN_LIMIT = 0.0F / Y_SCALE;
	public static final float Z_MIN_LIMIT = 0.0F / Z_SCALE;
	public static final float Y_MAX_LIMIT = 340.0F / Y_SCALE;
	public static final float Z_MAX_LIMIT = 180.0F / Z_SCALE;
	
	public static final float X_MAX_SPEED = 1050.0F / X_SCALE;
	public static final float Y_MAX_SPEED = 1050.0F / Y_SCALE;
	public static final float Z_MAX_SPEED = 1560.0F / Z_SCALE;
	
	private static BaseRegulatedMotor motorA;
  	private static BaseRegulatedMotor motorB;
  	private static BaseRegulatedMotor motorC;
  	private static EV3TouchSensor touch;
  	private static Thread safetyThread;
  
  	public static void init()
  	{
  		motorA = new EV3MediumRegulatedMotor(MotorPort.A);
  		motorB = new EV3LargeRegulatedMotor(MotorPort.B);
  		motorC = new EV3LargeRegulatedMotor(MotorPort.C);
  		resetX();
  		resetY();
  		resetZ();
    	touch = new EV3TouchSensor(SensorPort.S1);
    
    	safetyThread = new Thread(new SafetyThread(touch));
    	safetyThread.start();
  	}
  
 	public static int getX()
 	{
 		return (int)(motorC.getTachoCount() / X_SCALE);
 	}
  
 	public static int getY()
 	{
 		return (int)(motorA.getTachoCount() / Y_SCALE);
 	}
  
 	public static int getZ()
 	{
 		return (int)(motorB.getTachoCount() / Z_SCALE);
 	}
  
 	public static void goToX(int pos)
 	{
 		motorC.rotateTo((int)(pos * X_SCALE), true);
 	}
  
 	public static void goToY(int pos)
 	{
 		if (pos < Y_MIN_LIMIT)
 			warning("Y value under the limit: " + pos);
 		else if (pos > Y_MAX_LIMIT)
 			warning("Y value over the limit: " + pos);
 		motorA.rotateTo((int)(pos * Y_SCALE), true);
 	}
  
 	public static void goToZ(int pos)
 	{
 		if (pos < Z_MIN_LIMIT)
 			warning("Z value under the limit: " + pos);
 		else if (pos > Z_MAX_LIMIT)
 			warning("Z value over the limit: " + pos);
 		motorB.rotateTo((int)(pos * Z_SCALE), true);
 	}
  
 	public static void goTo(int x, int y, int z)
 	{
 		boolean rotX = x != AXIS_NO_CHANGES;
 		boolean rotY = y != AXIS_NO_CHANGES;
 		boolean rotZ = z != AXIS_NO_CHANGES;
 		
 		if (y < Y_MIN_LIMIT && rotY)
 			warning("Y value under the limit: " + y);
 		else if (y > Y_MAX_LIMIT && rotY)
 			warning("Y value over the limit: " + y);
 		
 		if (z < Z_MIN_LIMIT && rotZ)
 			warning("Z value under the limit: " + z);
 		else if (z > Z_MAX_LIMIT && rotZ)
 			warning("Z value over the limit: " + z);
 			
 		motorC.synchronizeWith(new RegulatedMotor[] { motorA, motorB });
 		motorC.startSynchronization();
 		if(rotX) motorC.rotateTo((int)(x * X_SCALE), true);
 		if(rotY) motorA.rotateTo((int)(y * Y_SCALE), true);
 		if(rotZ) motorB.rotateTo((int)(z * Z_SCALE), true);
 		motorC.endSynchronization();
 	}
  
 	public static void resetX()
 	{
 		motorC.resetTachoCount();
 	}
 	
 	public static void resetY()
 	{
 		motorA.resetTachoCount();
 	}
 	
 	public static void resetZ()
 	{
 		motorB.resetTachoCount();
 	}
 	
 	public static void floatAll()
 	{
 		motorC.flt();
 		motorA.flt();
 		motorB.flt();
 	}
 	
 	public static void stopAll()
 	{
 		motorC.close();
 		motorA.close();
 		motorB.close();
 		touch.close();
 	}
 	
 	public static void setSpeedX(float speed)
 	{
 		if (speed > X_MAX_SPEED)
 			warning("X speed over the limit");
 		motorC.setSpeed(speed * X_SCALE);
 	}
  
 	public static void setSpeedY(float speed)
 	{
 		if (speed > Y_MAX_SPEED)
 			warning("Y speed over the limit");
 		motorA.setSpeed(speed * Y_SCALE);
 	}
  
 	public static void setSpeedZ(float speed)
 	{
 		if (speed > Z_MAX_SPEED)
 			warning("Z speed over the limit");
 		motorB.setSpeed(speed * Z_SCALE);
 	}
  
 	public static boolean isMovingX()
 	{
 		return motorC.isMoving();
 	}
  
 	public static boolean isMovingY()
 	{
 		return motorA.isMoving();
 	}
  
 	public static boolean isMovingZ()
 	{
 		return motorB.isMoving();
 	}
 	
 	private static void warning(String message)
 	{
 		throw new RuntimeException("Warning: " + message);
 	}
}