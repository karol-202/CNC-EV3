package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.gcode.GCode;

import javax.swing.*;

class GCodeListModel extends AbstractListModel<String>
{
	private GCode gcode;
	
	GCodeListModel(GCode gcode)
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
	
	void fireAllRemoved()
	{
		fireIntervalRemoved(this, 0, 0);
	}
	
	void fireAllChanged()
	{
		fireContentsChanged(this, 0, getSize() - 1);
	}
}