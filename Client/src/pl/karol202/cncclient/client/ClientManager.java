package pl.karol202.cncclient.client;

import pl.karol202.cncprinter.Axis;
import pl.karol202.cncprinter.ManualControlAction;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientManager
{
	private Client client;
	private Executor executorService;
	
	public ClientManager()
	{
		client = new Client();
		executorService = Executors.newFixedThreadPool(2);
	}
	
	public void connect(String ip)
	{
		executorService.execute(() -> client.tryToConnect(ip));
	}
	
	public void disconnect()
	{
		executorService.execute(() -> client.tryToDisconnect());
	}
	
	public void sendGCode(byte[] code)
	{
		executorService.execute(() -> client.tryToSendGCode(code));
	}
	
	public void start()
	{
		executorService.execute(() -> client.tryToStart());
	}
	
	public void manualControl(Axis axis, ManualControlAction action, int speed)
	{
		executorService.execute(() -> client.tryToManualControl(axis, action, speed));
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