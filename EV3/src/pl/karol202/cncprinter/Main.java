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
	private CodeExecutor reader;
	
	private Main()
	{
		Button.ESCAPE.addKeyListener(escapeListener);
		Button.LEDPattern(1);
		
		machine = new Machine();
		runServer();
	}
	
	private void runServer()
	{
		new Thread(new Server(this)).start();
	}
	
	boolean setGCode(byte[] bytes)
	{
		if(reader != null && reader.isRunning()) return false;
		reader = new CodeExecutor(machine, bytes);
		return true;
	}
	
	boolean start()
	{
		if(reader == null || reader.isRunning()) return false;
		new Thread(reader).start();
		return true;
	}
	
	public static void main(String[] args)
	{
		new Main();
	}
}
