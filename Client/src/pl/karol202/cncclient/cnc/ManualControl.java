package pl.karol202.cncclient.cnc;

import pl.karol202.cncclient.client.ClientManager;
import pl.karol202.cncprinter.Axis;
import pl.karol202.cncprinter.ManualControlAction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import static pl.karol202.cncprinter.ManualControlAction.MOVE_LEFT;
import static pl.karol202.cncprinter.ManualControlAction.MOVE_RIGHT;
import static pl.karol202.cncprinter.ManualControlAction.STOP;

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
		if(e.getKeyCode() == KeyEvent.VK_LEFT) control(Axis.X, MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) control(Axis.X, MOVE_RIGHT);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) control(Axis.Y, MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_UP) control(Axis.Y, MOVE_RIGHT);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) control(Axis.Z, MOVE_LEFT);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) control(Axis.Z, MOVE_RIGHT);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_LEFT) control(Axis.X, STOP);
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) control(Axis.X, STOP);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) control(Axis.Y, STOP);
		else if(e.getKeyCode() == KeyEvent.VK_UP) control(Axis.Y, STOP);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) control(Axis.Z, STOP);
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) control(Axis.Z, STOP);
	}
	
	public void control(Axis axis, ManualControlAction action)
	{
		if(!client.isAuthenticated()) return;
		if(isMoveAction(action) && axesMoving.get(axis)) return;
		client.manualControl(axis, action, speed);
		axesMoving.put(axis, isMoveAction(action));
	}
	
	private boolean isMoveAction(ManualControlAction action)
	{
		return action == MOVE_LEFT || action == MOVE_RIGHT;
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
}