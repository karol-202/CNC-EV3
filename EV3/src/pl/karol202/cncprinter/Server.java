package pl.karol202.cncprinter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Server implements Runnable
{
	private static final int PORT = 666;
	private static final char[] PASSWORD = "ev3cncprinter".toCharArray();
	
	public static final int MESSAGE_OK = 1;
	public static final int MESSAGE_PASSWORD = 2;
	public static final int MESSAGE_DISCONNECT = 3;
	public static final int MESSAGE_GCODE = 4;
	public static final int MESSAGE_START = 5;
	public static final int MESSAGE_STOP = 6;
	public static final int MESSAGE_PAUSE = 7;
	public static final int MESSAGE_RESUME = 8;
	public static final int MESSAGE_DENIED = 9;
	public static final int MESSAGE_STATE = 10;
	public static final int MESSAGE_MANUAL = 11;
	
	private Main main;
	private ServerSocket server;
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	Server(Main main)
	{
		this.main = main;
	}
	
	public void run()
	{
		try
		{
			startServer();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void startServer() throws IOException
	{
		server = new ServerSocket(PORT);
		runServerLoop();
	}
	
	@SuppressWarnings("InfiniteLoopStatement")
	private void runServerLoop() throws IOException
	{
		while(true)
		{
			System.out.println("Waiting for connection...");
			//Sound.beep();
			socket = server.accept();
			is = socket.getInputStream();
			os = socket.getOutputStream();
			if(checkClient()) listen();
			System.out.println("End of connection");
		}
	}
	
	private boolean checkClient() throws IOException
	{
		System.out.println("Connecting...");
		sendPasswordRequest();
		char[] password = getPassword();
		if(Arrays.equals(password, PASSWORD))
		{
			sendConnectedSuccessfullyMessage();
			System.out.println("Successfully connected");
			return true;
		}
		else
		{
			disconnect();
			System.out.println("Invalid password");
			return false;
		}
	}
	
	private void sendPasswordRequest() throws IOException
	{
		os.write(MESSAGE_PASSWORD);
	}
	
	private char[] getPassword() throws IOException
	{
		byte[] bytes = new byte[128];
		int passwordLength = is.read(bytes, 0, 128) - 1;
		if(bytes[0] != MESSAGE_OK)
		{
			System.err.println("Connecting: Invalid password message from client.");
			return null;
		}
		char[] password = new char[passwordLength];
		for (int i = 0; i < passwordLength; i++) password[i] = (char) bytes[i + 1];
		return password;
	}
	
	private void sendConnectedSuccessfullyMessage() throws IOException
	{
		os.write(MESSAGE_OK);
	}
	
	private void disconnect() throws IOException
	{
		os.write(MESSAGE_DISCONNECT);
		socket.close();
	}
	
	private void listen() throws IOException
	{
		while(true)
		{
			int message = is.read();
			if(message == -1) return;
			switch(message)
			{
			case MESSAGE_DISCONNECT:
				System.out.println("CLIENT: DISCONNECT");
				return;
			case MESSAGE_GCODE:
				System.out.println("CLIENT: GCODE");
				tryToSetGCode();
				break;
			case MESSAGE_START:
				System.out.println("CLIENT: START");
				tryToStartProgram();
				break;
			case MESSAGE_STOP:
				System.out.println("CLIENT: STOP");
				tryToStopProgram();
				break;
			case MESSAGE_PAUSE:
				System.out.println("CLIENT: PAUSE");
				tryToPauseProgram();
				break;
			case MESSAGE_RESUME:
				System.out.println("CLIENT: RESUME");
				tryToResumeProgram();
				break;
			case MESSAGE_STATE:
				sendMachineState();
				break;
			case MESSAGE_MANUAL:
				System.out.println("CLIENT: MANUAL");
				tryToManualControl();
				break;
			default: System.err.println("Unknown message: " + message);
			}
		}
	}
	
	private void tryToSetGCode() throws IOException
	{
		int length = readInt();
		byte[] bytes = new byte[length];
		is.read(bytes, 0, length);
		boolean done = main.setGCode(bytes);
		os.write(done ? MESSAGE_OK : MESSAGE_DENIED);
	}
	
	private void tryToStartProgram() throws IOException
	{
		boolean done = main.start();
		os.write(done ? MESSAGE_OK : MESSAGE_DENIED);
	}
	
	private void tryToStopProgram() throws IOException
	{
		main.stop();
		os.write(MESSAGE_OK);
	}
	
	private void tryToPauseProgram() throws IOException
	{
		boolean done = main.setPaused(true);
		os.write(done ? MESSAGE_OK : MESSAGE_DENIED);
	}
	
	private void tryToResumeProgram() throws IOException
	{
		boolean done = main.setPaused(false);
		os.write(done ? MESSAGE_OK : MESSAGE_DENIED);
	}
	
	private void sendMachineState() throws IOException
	{
		os.write(MESSAGE_STATE);
		os.write(main.isRunning() ? 1 : 0);
		os.write(main.isPaused() ? 1 : 0);
		os.write(floatToBytes(main.getX()));
		os.write(floatToBytes(main.getY()));
		os.write(floatToBytes(main.getZ()));
	}
	
	private void tryToManualControl() throws IOException
	{
		Axis axis = Axis.values()[is.read()];
		ManualControlAction action = ManualControlAction.values()[is.read()];
		int speed = readInt();
		main.manualControl(axis, action, speed);
	}
	
	private int readInt() throws IOException
	{
		byte[] bytes = new byte[4];
		is.read(bytes, 0, 4);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		return bb.getInt();
	}
	
	private byte[] floatToBytes(float value)
	{
		return ByteBuffer.allocate(4).putFloat(value).array();
	}
}
