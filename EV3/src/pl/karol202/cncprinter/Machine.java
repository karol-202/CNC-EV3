package pl.karol202.cncprinter;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

class Machine
{
	private static final float X_SCALE = 7.947F;
	private static final float Y_SCALE = 7.963F;
	private static final float Z_SCALE = 180.0F;
	
	private static final float Y_MIN_LIMIT = 0.0F / Y_SCALE;
	private static final float Y_MAX_LIMIT = 340.0F / Y_SCALE;
	private static final float Z_MIN_LIMIT = 0.0F / Z_SCALE;
	private static final float Z_MAX_LIMIT = 180.0F / Z_SCALE;
	
	static final float X_MAX_SPEED = 1050.0F / X_SCALE;
	static final float Y_MAX_SPEED = 1050.0F / Y_SCALE;
	static final float Z_MAX_SPEED = 1560.0F / Z_SCALE;
	
	private BaseRegulatedMotor motorX;
	private BaseRegulatedMotor motorY;
  	private BaseRegulatedMotor motorZ;
  	private EV3TouchSensor touchSensor;
  	
  	private MachineListener listener;
  
  	Machine()
    {
	    this.motorX = new EV3LargeRegulatedMotor(MotorPort.C);
  		this.motorY = new EV3MediumRegulatedMotor(MotorPort.A);
  		this.motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
	    this.touchSensor = new EV3TouchSensor(SensorPort.S1);
    	
		runSafetyThread();
	    resetAll();
  	}
  	
  	private void runSafetyThread()
    {
	    Thread safetyThread = new Thread(new SafetyThread(this));
	    safetyThread.start();
    }
  
 	int getX()
 	{
 		return (int) (motorX.getTachoCount() / X_SCALE);
 	}
  
 	int getY()
 	{
 		return (int) (motorY.getTachoCount() / Y_SCALE);
 	}
  
 	int getZ()
 	{
 		return (int) (motorZ.getTachoCount() / Z_SCALE);
 	}
  
 	void goToX(int pos)
 	{
 		motorX.rotateTo((int) (pos * X_SCALE), true);
 	}
  
 	void goToY(int pos)
 	{
 		pos *= Y_SCALE;
 		if(checkYLimit(pos)) return;
 		motorY.rotateTo(pos, true);
 	}
 	
 	private boolean checkYLimit(int pos)
    {
	    if(pos < Y_MIN_LIMIT) error("Y value under the limit: " + pos);
	    else if(pos > Y_MAX_LIMIT) error("Y value over the limit: " + pos);
	    else return false;
	    return true;
    }
  
 	void goToZ(int pos)
 	{
 		pos *= Z_SCALE;
 		if(checkZLimit(pos)) return;
 		motorZ.rotateTo(pos, true);
 	}
 	
 	private boolean checkZLimit(int pos)
    {
	    if(pos < Z_MIN_LIMIT) error("Z value under the limit: " + pos);
	    else if(pos > Z_MAX_LIMIT) error("Z value over the limit: " + pos);
	    else return false;
	    return true;
    }
  
 	void goTo(int x, int y, int z)
 	{
 		boolean rotX = x != getX();
 		boolean rotY = y != getY();
 		boolean rotZ = z != getZ();
 		
 		if((rotY && checkYLimit(y)) || (rotZ && checkZLimit(z))) return;
 			
 		motorX.synchronizeWith(new RegulatedMotor[] { motorY, motorZ });
 		motorX.startSynchronization();
 		if(rotX) motorX.rotateTo((int) (x * X_SCALE), true);
 		if(rotY) motorY.rotateTo((int) (y * Y_SCALE), true);
 		if(rotZ) motorZ.rotateTo((int) (z * Z_SCALE), true);
 		motorX.endSynchronization();
 	}
  
 	private void resetAll()
 	{
 		motorX.resetTachoCount();
 		motorY.resetTachoCount();
 		motorZ.resetTachoCount();
 	}
 	
 	void floatAll()
 	{
 		motorX.flt();
 		motorY.flt();
 		motorZ.flt();
 	}
 	
 	void stopAll()
    {
    	motorX.stop();
    	motorY.stop();
	    motorZ.stop();
    }
 	
 	void closeAll()
 	{
 		motorX.close();
 		motorY.close();
 		motorZ.close();
 		touchSensor.close();
 	}
 	
 	void setXSpeed(float speed)
 	{
 		if(checkXSpeedLimit(speed)) return;
 		motorX.setSpeed(speed * X_SCALE);
 	}
 	
 	private boolean checkXSpeedLimit(float speed)
    {
	    if(speed > X_MAX_SPEED) error("X speed over the limit");
	    else if(speed < 0) error("X speed under the limit");
	    else return false;
	    return true;
    }
  
 	void setYSpeed(float speed)
 	{
 		if(checkYSpeedLimit(speed)) return;
 		motorY.setSpeed(speed * Y_SCALE);
 	}
 	
 	private boolean checkYSpeedLimit(float speed)
    {
	    if(speed > Y_MAX_SPEED) error("Y speed over the limit");
	    else if(speed < 0) error("Y speed under the limit");
	    else return false;
	    return true;
    }
  
 	void setZSpeed(float speed)
 	{
 		if(checkZSpeedLimit(speed)) return;;
 		motorZ.setSpeed(speed * Z_SCALE);
 	}
 	
 	private boolean checkZSpeedLimit(float speed)
    {
	    if(speed > Z_MAX_SPEED) error("Z speed over the limit");
	    else if(speed < 0) error("Z speed under the limit");
	    else return false;
	    return true;
    }
	
 	void setXSpeedToMax()
    {
    	setXSpeed(X_MAX_SPEED);
    }
    
    void setYSpeedToMax()
    {
    	setYSpeed(Y_MAX_SPEED);
    }
    
    void setZSpeedToMax()
    {
    	setZSpeed(Z_MAX_SPEED);
    }
  
 	boolean isMovingX()
 	{
 		return motorX.isMoving();
 	}
  
 	boolean isMovingY()
 	{
 		return motorY.isMoving();
 	}
  
 	boolean isMovingZ()
 	{
 		return motorZ.isMoving();
 	}
	
	private void error(String message)
	{
		System.err.println("Machine error: " + message);
		stopAll();
		if(listener != null) listener.onProblemDetected();
	}
	
	EV3TouchSensor getTouchSensor()
	{
		return touchSensor;
	}
	
	void setMachineListener(MachineListener listener)
	{
		this.listener = listener;
	}
}