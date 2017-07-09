package pl.karol202.cncprinter;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public class Main
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

	private static Reader reader;
	
	private static void init()
	{
		Button.ESCAPE.addKeyListener(escapeListener);
		Button.LEDPattern(1);
		
		Machine.init();
		new Server();
	}
	
	public static boolean setReader(byte[] bytes)
	{
		if(reader != null && reader.isRunning()) return false;
		reader = new Reader(bytes);
		return true;
	}
	
	public static boolean start()
	{
		if(reader == null || reader.isRunning()) return false;
		new Thread(reader).start();
		return true;
	}
	
	public static void main(String[] args)
	{
		init();
	}
}
