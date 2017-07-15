package pl.karol202.cncclient;

import pl.karol202.cncclient.client.ClientManager;
import pl.karol202.cncclient.cnc.*;
import pl.karol202.cncclient.ui.FrameMain;

import javax.swing.*;

public class Main
{
	private FrameMain frameMain;
	
	private ClientManager client;
	private GCode gcode;
	private GCodeLoader gcodeLoader;
	private ManualControl manualControl;
	private MachineState machineState;
	private MoveRecorder moveRecorder;
	
	private KeyManager keyManager;
	
	private Main()
	{
		client = new ClientManager();
		gcode = new GCode();
		gcodeLoader = new GCodeLoader(gcode);
		manualControl = new ManualControl(client);
		machineState = new MachineState();
		moveRecorder = new MoveRecorder(machineState);
		
		keyManager = new KeyManager();
		keyManager.addKeyListener(manualControl);
		
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
			frameMain = new FrameMain(client, gcode, gcodeLoader, manualControl, machineState, moveRecorder);
			client.setConnectionListener(frameMain);
		});
	}
	
	public static void main(String[] args)
	{
		new Main();
	}
}