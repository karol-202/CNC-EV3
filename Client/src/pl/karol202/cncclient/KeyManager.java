package pl.karol202.cncclient;

import java.util.ArrayList;
import java.util.List;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class KeyManager implements KeyEventDispatcher
{
	private List<KeyListener> listeners;
	
	KeyManager()
	{
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		
		listeners = new ArrayList<>();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		if(e.getID() == KeyEvent.KEY_PRESSED) listeners.forEach(l -> l.keyPressed(e));
		else if(e.getID() == KeyEvent.KEY_RELEASED) listeners.forEach(l -> l.keyReleased(e));
		else if(e.getID() == KeyEvent.KEY_TYPED) listeners.forEach(l -> l.keyTyped(e));
		return false;
	}
	
	void addKeyListener(KeyListener listener)
	{
		listeners.add(listener);
	}
}