package pl.karol202.cncclient.cnc;

import pl.karol202.cncclient.client.ClientManager;
import pl.karol202.cncprinter.Axis;
import pl.karol202.cncprinter.ManualControlAction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class ManualControl implements KeyListener
{
	private ClientManager client;
	private Map<Axis, Boolean> axesMoving;
	private int speed;
	
	public ManualControl(ClientManager client)
	{
		this.client = client;
		this.axesMoving = new HashMap<>();
		this.axesMoving.put(Axis.X, false);
		this.axesMoving.put(Axis.Y, false);
		this.axesMoving.put(Axis.Z, false);
		this.speed = 10;
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(!client.isAuthenticated()) return;
		if(e.getKeyCode() == KeyEvent.VK_LEFT) control(Axis.X, ManualControlAction.MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) control(Axis.X, ManualControlAction.MOVE_RIGHT);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) control(Axis.Y, ManualControlAction.MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_UP) control(Axis.Y, ManualControlAction.MOVE_RIGHT);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) control(Axis.Z, ManualControlAction.MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) control(Axis.Z, ManualControlAction.MOVE_RIGHT);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if(!client.isAuthenticated()) return;
		if(e.getKeyCode() == KeyEvent.VK_LEFT) control(Axis.X, ManualControlAction.STOP);
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) control(Axis.X, ManualControlAction.STOP);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) control(Axis.Y, ManualControlAction.STOP);
		else if(e.getKeyCode() == KeyEvent.VK_UP) control(Axis.Y, ManualControlAction.STOP);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) control(Axis.Z, ManualControlAction.STOP);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) control(Axis.Z, ManualControlAction.STOP);
	}
	
	private void control(Axis axis, ManualControlAction action)
	{
		if(action != ManualControlAction.STOP && axesMoving.get(axis)) return;
		client.manualControl(axis, action, speed);
		axesMoving.put(axis, action != ManualControlAction.STOP);
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
}