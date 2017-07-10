package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.gcode.GCode;

import javax.swing.*;

public class GCodeListModel extends AbstractListModel<String>
{
	private GCode gcode;
	
	public GCodeListModel(GCode gcode)
	{
		this.gcode = gcode;
	}
	
	@Override
	public int getSize()
	{
		return gcode.getLinesAmount();
	}
	
	@Override
	public String getElementAt(int index)
	{
		return gcode.getLine(index);
	}
	
	void fireLineAdded(int position)
	{
		fireIntervalAdded(this, position, position);
	}
	
	void fireLineRemoved(int position)
	{
		fireIntervalRemoved(this, position, position);
	}
}