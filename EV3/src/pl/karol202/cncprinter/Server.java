package pl.karol202.cncprinter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import lejos.hardware.Sound;

public class Server implements Runnable
{
	private final String PASSWORD = "ev3cncprinter";
	private ServerSocket server;
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	public Server()
	{
		new Thread(this).start();
	}
	
	public void run()
	{
		try
		{
			this.server = new ServerSocket(666);
			while(true)
			{
				System.out.println("Waiting for connection...");
				Sound.beep();
				this.socket = this.server.accept();
				this.is = this.socket.getInputStream();
				this.os = this.socket.getOutputStream();
				if (connected()) listen();
				System.out.println("End of connection");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private boolean connected() throws IOException
	{
		System.out.println("Connecting...");
		this.os.write(2);
		byte[] read = new byte[128];
		this.is.read(read, 0, 128);
		if (read[0] != 1) {
			throw new RuntimeException("Connecting: Invalid password message from client.");
		}
		byte len = read[1];
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			chars[i] = ((char)read[(i + 2)]);
		}
		String pass = String.copyValueOf(chars, 0, len);
		if (pass.equals(PASSWORD))
		{
			this.os.write(1);
			System.out.println("Succesfully connected");
			return true;
		}
		else
		{
			this.os.write(3);
			this.socket.close();
			System.out.println("Invalid password");
			return false;
		}
	}
	
	private void listen() throws IOException
	{
		while(true)
		{
			System.out.println("Listening...");
			int read = this.is.read();
			if (read == -1) return;
			else if(read == 3)
			{
				System.out.println("CLIENT: DISCONNECT");
				return;
			}
			else if (read == 4)
			{
				System.out.println("CLIENT: GCODE");
				int len = readInt();
				byte[] bytes = new byte[len];
				this.is.read(bytes, 0, len);
				boolean done = Main.setReader(bytes);
				this.os.write(done ? 1 : 9);
			}
			else if (read == 5)
			{
				System.out.println("CLIENT: START");
				boolean done = Main.start();
				this.os.write(done ? 1 : 9);
			}
		}
	}
	
	private int readInt() throws IOException
	{
		byte[] bytes = new byte[4];
		this.is.read(bytes, 0, 4);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		return bb.getInt();
	}
}
