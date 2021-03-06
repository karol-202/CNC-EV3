package pl.karol202.cncclient.client;

import pl.karol202.cncclient.cnc.MachineState;
import pl.karol202.cncprinter.Axis;
import pl.karol202.cncprinter.ManualControlAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static pl.karol202.cncprinter.Server.*;

class Client
{
	private static final int TIMEOUT = 1000;
	private static final int STATE_CHECK_TIME = 100;
	private static final int PORT = 666;
	private static final byte[] PASSWORD = "ev3cncprinter".getBytes();
	
	private ConnectionListener listener;
	private MachineState machineState;
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	private boolean waitingForAuthentication;
	private boolean authenticating;
	private boolean sending;
	private boolean starting;
	private boolean stopping;
	private boolean pausing;
	private boolean resuming;
	
	void tryToConnect(String ip)
	{
		try
		{
			connect(ip);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
			if(listener != null) listener.onUnknownHost();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			if(listener != null && socket == null) listener.onCannotConnect();
		}
	}
	
	private void connect(String ip) throws IOException
	{
		System.out.println("Connecting...");
		SocketAddress address = new InetSocketAddress(ip, PORT);
		socket = new Socket();
		socket.connect(address, TIMEOUT);
		is = socket.getInputStream();
		os = socket.getOutputStream();
		
		if(listener != null) listener.onConnected();
		System.out.println("Connected successfully");
		
		waitingForAuthentication = true;
		authenticating = false;
		sending = false;
		starting = false;
		pausing = false;
		resuming = false;
		stopping = false;
		
		listen();
	}
	
	void tryToDisconnect()
	{
		try
		{
			disconnect();
			System.out.println("Disconnected successfully");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			tryToClose();
		}
	}
	
	private void disconnect() throws IOException
	{
		os.write(MESSAGE_DISCONNECT);
		tryToClose();
		if(listener != null) listener.onDisconnected();
	}
	
	private void tryToClose()
	{
		try
		{
			close();
			System.out.println("Socket closed");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void close() throws IOException
	{
		socket.close();
	}
	
	private void listen() throws IOException
	{
		while(!socket.isClosed())
		{
			int message = is.read();
			if(message == -1) return;
			switch(message)
			{
			case MESSAGE_OK: onOKMessage(); break;
			case MESSAGE_PASSWORD: onPasswordRequested(); break;
			case MESSAGE_DISCONNECT: onDisconnectMessage(); break;
			case MESSAGE_DENIED: onDeniedMessage(); break;
			case MESSAGE_STATE: onMachineStateMessage(); break;
			default: System.err.println("Unknown message: " + message);
			}
		}
	}
	
	private void onOKMessage()
	{
		System.out.println("SERVER: OK");
		if(authenticating) onAuthenticated();
		else if(sending) onSent();
		else if(starting) onStarted();
		else if(stopping) onStopped();
		else if(pausing) onPaused();
		else if(resuming) onResumed();
	}
	
	private void onAuthenticated()
	{
		System.out.println("Authenticated correctly");
		authenticating = false;
		if(listener != null) listener.onAuthenticated();
	}
	
	private void onSent()
	{
		System.out.println("GCode sent");
		if(listener != null) listener.onSent();
		sending = false;
	}
	
	private void onStarted()
	{
		System.out.println("Started");
		if(listener != null) listener.onStarted();
		starting = false;
	}
	
	private void onStopped()
	{
		System.out.println("Stopped");
		if(listener != null) listener.onStopped();
		stopping = false;
	}
	
	private void onPaused()
	{
		System.out.println("Paused");
		if(listener != null) listener.onPaused();
		pausing = false;
	}
	
	private void onResumed()
	{
		System.out.println("Resumed");
		if(listener != null) listener.onResumed();
		resuming = false;
	}
	
	private void onPasswordRequested() throws IOException
	{
		System.out.println("SERVER: PASSWORD REQUEST");
		if(!waitingForAuthentication) System.err.println("Already authenticated!");
		sendPassword();
		waitingForAuthentication = false;
		authenticating = true;
	}
	
	private void sendPassword() throws IOException
	{
		os.write(MESSAGE_OK);
		os.write(PASSWORD);
	}
	
	private void onDisconnectMessage()
	{
		System.out.println("SERVER: DISCONNECT");
		if(authenticating) onAuthenticationFailed();
		if(listener != null) listener.onDisconnected();
	}
	
	private void onAuthenticationFailed()
	{
		System.err.println("Authentication failed!");
		if(listener != null) listener.onAuthenticationFailed();
	}
	
	private void onDeniedMessage()
	{
		System.out.println("SERVER: DENIED");
		if(sending) onSendingDenied();
		else if(starting) onStartingDenied();
		else if(pausing) onPausingDenied();
		else if(resuming) onResumingDenied();
	}
	
	private void onSendingDenied()
	{
		System.err.println("Cannot send");
		if(listener != null) listener.onSendingDenied();
		sending = false;
	}
	
	private void onStartingDenied()
	{
		System.err.println("Cannot start");
		if(listener != null) listener.onStartingDenied();
		starting = false;
	}
	
	private void onPausingDenied()
	{
		System.err.println("Cannot pause");
		if(listener != null) listener.onPausingDenied();
		pausing = false;
	}
	
	private void onResumingDenied()
	{
		System.err.println("Cannot resume");
		if(listener != null) listener.onResumingDenied();
	}
	
	private void onMachineStateMessage() throws IOException
	{
		machineState.setRunning(is.read() != 0);
		machineState.setPaused(is.read() != 0);
		machineState.setX(readFloat());
		machineState.setY(readFloat());
		machineState.setZ(readFloat());
		if(listener != null) listener.onMachineStateUpdated();
	}
	
	void tryToSendGCode(byte[] code)
	{
		try
		{
			sendGCode(code);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void sendGCode(byte[] code) throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Sending GCode");
		os.write(MESSAGE_GCODE);
		os.write(intToBytes(code.length));
		os.write(code);
		sending = true;
	}
	
	void tryToStart()
	{
		try
		{
			start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void start() throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Starting");
		os.write(MESSAGE_START);
		starting = true;
	}
	
	void tryToStop()
	{
		try
		{
			stop();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void stop() throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Stopping");
		os.write(MESSAGE_STOP);
		stopping = true;
	}
	
	void tryToPause()
	{
		try
		{
			pause();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void pause() throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Pausing");
		os.write(MESSAGE_PAUSE);
		pausing = true;
	}
	
	void tryToResume()
	{
		try
		{
			resume();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void resume() throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Resuming");
		os.write(MESSAGE_RESUME);
		resuming = true;
	}
	
	void tryToManualControl(Axis axis, ManualControlAction action, int speed)
	{
		try
		{
			manualControl(axis, action, speed);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			tryToDisconnect();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void manualControl(Axis axis, ManualControlAction action, int speed) throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Manual control: " + axis.name() + " - " + action.name());
		os.write(MESSAGE_MANUAL);
		os.write(axis.ordinal());
		os.write(action.ordinal());
		os.write(intToBytes(speed));
	}
	
	void tryToRunStateCheckLoop(MachineState machineState)
	{
		try
		{
			runStateCheckLoop(machineState);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			if(listener != null) listener.onConnectionProblem();
		}
	}
	
	private void runStateCheckLoop(MachineState machineState) throws IOException, InterruptedException
	{
		this.machineState = machineState;
		Thread.sleep(1000);
		while(!socket.isClosed())
		{
			checkState();
			Thread.sleep(STATE_CHECK_TIME);
		}
		this.machineState = null;
	}
	
	private void checkState() throws IOException
	{
		if(!checkReady()) return;
		os.write(MESSAGE_STATE);
	}
	
	private boolean checkReady()
	{
		if(isAuthenticated()) return true;
		if(listener != null) listener.onConnectionProblem();
		return false;
	}
	
	boolean isConnected()
	{
		return socket != null && socket.isConnected() && !socket.isClosed();
	}
	
	boolean isAuthenticated()
	{
		return isConnected() && !waitingForAuthentication && !authenticating;
	}
	
	void setConnectionListener(ConnectionListener listener)
	{
		this.listener = listener;
	}
	
	private byte[] intToBytes(int value)
	{
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	
	private float readFloat() throws IOException
	{
		byte[] bytes = new byte[4];
		is.read(bytes, 0, 4);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		return bb.getFloat();
	}
}