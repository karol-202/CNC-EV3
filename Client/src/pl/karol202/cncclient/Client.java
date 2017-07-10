package pl.karol202.cncclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static pl.karol202.cncprinter.Server.*;

class Client
{
	private static final int PORT = 666;
	private static final byte[] PASSWORD = "ev3cncprinter".getBytes();
	
	private String ip;
	private ConnectionListener listener;
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	private boolean waitingForAuthentication;
	private boolean authenticating;
	private boolean sending;
	private boolean starting;
	
	Client(String ip, ConnectionListener listener)
	{
		this.ip = ip;
		this.listener = listener;
	}
	
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
			if(listener != null) listener.onCannotConnect();
		}
	}
	
	private void connect(String ip) throws IOException
	{
		System.out.println("Connecting...");
		socket = new Socket(ip, PORT);
		is = socket.getInputStream();
		os = socket.getOutputStream();
		System.out.println("Connected successfully");
		
		waitingForAuthentication = true;
		authenticating = false;
		sending = false;
		starting = false;
		
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
		tryToDisconnect();
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
			switch(message)
			{
			case MESSAGE_OK: onOKMessage(); break;
			case MESSAGE_PASSWORD: onPasswordRequested(); break;
			case MESSAGE_DISCONNECT: onDisconnectMessage(); break;
			case MESSAGE_DENIED: onDeniedMessage(); break;
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
	}
	
	private void onAuthenticated()
	{
		System.out.println("Authenticated correctly");
		if(listener != null) listener.onConnected();
		authenticating = false;
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
	
	void sendGCode(byte[] code) throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Sending GCode");
		os.write(MESSAGE_GCODE);
		os.write(intToBytes(code.length));
		os.write(code);
		sending = true;
	}
	
	void start() throws IOException
	{
		if(!checkReady()) return;
		System.out.println("Starting");
		os.write(MESSAGE_START);
		starting = true;
	}
	
	private boolean isReady()
	{
		return socket.isConnected() && !waitingForAuthentication && !authenticating;
	}
	
	private boolean checkReady()
	{
		if(isReady()) return true;
		if(listener != null) listener.onConnectionProblem();
		return false;
	}
	
	private byte[] intToBytes(int value)
	{
		return ByteBuffer.allocate(4).putInt(value).array();
	}
}