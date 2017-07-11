package pl.karol202.cncclient;

import pl.karol202.cncclient.client.ClientManager;
import pl.karol202.cncclient.gcode.GCode;
import pl.karol202.cncclient.ui.FrameMain;
import pl.karol202.cncclient.ui.GCodeLoader;

import javax.swing.*;

public class Main
{
	private FrameMain frameMain;
	
	private ClientManager client;
	private GCode gcode;
	private GCodeLoader gcodeLoader;
	
	private Main()
	{
		client = new ClientManager();
		gcode = new GCode();
		gcodeLoader = new GCodeLoader(gcode);
		
		setLookAndFeel();
		runMainFrame();
	}
	
	private void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void runMainFrame()
	{
		SwingUtilities.invokeLater(() -> {
			frameMain = new FrameMain(client, gcode, gcodeLoader);
			client.setConnectionListener(frameMain);
		});
	}
	
	public static void main(String[] args)
	{
		new Main();
	}
}