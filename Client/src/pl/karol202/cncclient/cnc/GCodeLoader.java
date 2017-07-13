package pl.karol202.cncclient.cnc;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GCodeLoader
{
	private GCode gcode;
	private String lastPath;
	
	public GCodeLoader(GCode gcode)
	{
		this.gcode = gcode;
	}
	
	public void newFile()
	{
		gcode.clear();
	}
	
	public void openFile(Component parentForDialog)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Otwórz plik");
		fileChooser.setApproveButtonText("Otwórz");
		int result = fileChooser.showOpenDialog(parentForDialog);
		if(result == JFileChooser.APPROVE_OPTION) openFile(fileChooser.getSelectedFile().getAbsolutePath(), parentForDialog);
	}
	
	private void openFile(String path, Component parentForDialog)
	{
		try
		{
			gcode.loadFromFile(path);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(parentForDialog, "Nie można otworzyć pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void saveFile(Component parentForDialog)
	{
		if(lastPath != null) saveFileAs(lastPath, parentForDialog);
		else saveFileAs(parentForDialog);
	}
	
	public void saveFileAs(Component parentForDialog)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Zapisz plik");
		fileChooser.setApproveButtonText("Zapisz");
		int result = fileChooser.showSaveDialog(parentForDialog);
		if(result == JFileChooser.APPROVE_OPTION) saveFileAs(fileChooser.getSelectedFile().getAbsolutePath(), parentForDialog);
	}
	
	private void saveFileAs(String path, Component parentForDialog)
	{
		try
		{
			gcode.saveToFile(path);
			lastPath = path;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(parentForDialog, "Nie można zapisać pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
		}
	}
}