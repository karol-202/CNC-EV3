package pl.karol202.cncclient.client;

import pl.karol202.cncclient.cnc.MachineState;
import pl.karol202.cncprinter.Axis;
import pl.karol202.cncprinter.ManualControlAction;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientManager
{
	private Client client;
	private Executor executor;
	
	public ClientManager()
	{
		client = new Client();
		executor = Executors.newFixedThreadPool(3);
	}
	
	public void connect(String ip)
	{
		executor.execute(() -> client.tryToConnect(ip));
	}
	
	public void disconnect()
	{
		executor.execute(() -> client.tryToDisconnect());
	}
	
	public void sendGCode(byte[] code)
	{
		executor.execute(() -> client.tryToSendGCode(code));
	}
	
	public void start()
	{
		executor.execute(() -> client.tryToStart());
	}
	
	public void manualControl(Axis axis, ManualControlAction action, int speed)
	{
		executor.execute(() -> client.tryToManualControl(axis, action, speed));
	}
	
	public void runStateCheckLoop(MachineState state)
	{
		executor.execute(() -> client.tryToRunStateCheckLoop(state));
	}
	
	public boolean isConnected()
	{
		return client.isConnected();
	}
	
	public boolean isAuthenticated()
	{
		return client.isAuthenticated();
	}
	
	public void setConnectionListener(ConnectionListener listener)
	{
		client.setConnectionListener(listener);
	}
}