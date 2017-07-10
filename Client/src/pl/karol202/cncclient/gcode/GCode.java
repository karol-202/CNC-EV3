package pl.karol202.cncclient.gcode;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GCode
{
	private List<String> lines;
	
	public GCode()
	{
		lines = new ArrayList<>();
	}
	
	public void clear()
	{
		lines.clear();
	}
	
	public void addLine(int position, String line)
	{
		lines.add(position, line);
	}
	
	public void changeLine(int position, String line)
	{
		lines.set(position, line);
	}
	
	public void removeLine(int position)
	{
		lines.remove(position);
	}
	
	public String getLine(int position)
	{
		return lines.get(position);
	}
	
	public int getLinesAmount()
	{
		return lines.size();
	}
	
	public byte[] toByteArray()
	{
		List<Byte> bytes = new ArrayList<>();
		for(String line : lines)
		{
			byte[] lineBytes = line.getBytes();
			for(byte b : lineBytes) bytes.add(b);
			bytes.add((byte) 13);
			bytes.add((byte) 10);
		}
		byte[] byteArray = new byte[bytes.size()];
		for(int i = 0; i < bytes.size(); i++) byteArray[i] = bytes.get(i);
		return byteArray;
	}
	
	
	public void loadFromFile(String pathString) throws IOException
	{
		lines.clear();
		
		Path path = Paths.get(pathString);
		Files.lines(path).forEach(lines::add);
	}
	
	public void saveToFile(String path) throws IOException
	{
		FileWriter writer = new FileWriter(path);
		for(String line : lines) writer.write(line + "\r\n");
		writer.close();
	}
}