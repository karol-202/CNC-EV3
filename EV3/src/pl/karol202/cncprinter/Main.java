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
	private CodeExecutor executor;
	
	private Main()
	{
		Button.ESCAPE.addKeyListener(escapeListener);
		Button.LEDPattern(1);
		
		machine = new Machine();
		manualControl = new ManualControl(machine);
		runServer();
		runSafetyThread();
	}
	
	private void runServer()
	{
		new Thread(new Server(this)).start();
	}
	
	private void runSafetyThread()
	{
		new Thread(new SafetyThread(this, machine)).start();
	}
	
	boolean setGCode(byte[] bytes)
	{
		if(isRunning()) return false;
		executor = new CodeExecutor(machine, bytes);
		machine.setMachineListener(executor);
		return true;
	}
	
	boolean start()
	{
		if(executor == null || executor.isRunning()) return false;
		new Thread(executor).start();
		return true;
	}
	
	boolean setPaused(boolean paused)
	{
		if(!isRunning()) return false;
		executor.setPaused(paused);
		return true;
	}
	
	void stop()
	{
		if(executor != null) executor.stop();
		machine.stopAll();
	}
	
	boolean manualControl(Axis axis, ManualControlAction action, int speed)
	{
		if(isRunning()) return false;
		manualControl.manualControl(axis, action, speed);
		return true;
	}
	
	boolean isRunning()
	{
		return executor != null && executor.isRunning();
	}
	
	boolean isPaused()
	{
		return executor != null && executor.isPaused();
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
