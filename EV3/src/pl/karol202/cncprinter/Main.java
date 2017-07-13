package pl.karol202.cncprinter;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

class Main
{
	private static KeyListener escapeListener = new KeyListener()
	{
		@Override
		public void keyPressed(Key k)
		{
			System.exit(0);
		}

		@Override
		public void keyReleased(Key k) {}
	};

	private Machine machine;
	private ManualControl manualControl;
	private CodeExecutor reader;
	
	private Main()
	{
		Button.ESCAPE.addKeyListener(escapeListener);
		Button.LEDPattern(1);
		
		machine = new Machine();
		manualControl = new ManualControl(machine);
		runServer();
	}
	
	private void runServer()
	{
		new Thread(new Server(this)).start();
	}
	
	boolean setGCode(byte[] bytes)
	{
		if(isRunning()) return false;
		reader = new CodeExecutor(machine, bytes);
		machine.setMachineListener(reader);
		return true;
	}
	
	boolean start()
	{
		if(reader == null || reader.isRunning()) return false;
		new Thread(reader).start();
		return true;
	}
	
	boolean manualControl(Axis axis, ManualControlAction action, int speed)
	{
		if(isRunning()) return false;
		manualControl.manualControl(axis, action, speed);
		return true;
	}
	
	boolean isRunning()
	{
		return reader != null && reader.isRunning();
	}
	
	boolean isPaused()
	{
		return false;
	}
	
	float getX()
	{
		return machine.getX();
	}
	
	float getY()
	{
		return machine.getY();
	}
	
	float getZ()
	{
		return machine.getZ();
	}
	
	public static void main(String[] args)
	{
		new Main();
	}
}
