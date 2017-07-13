package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.ui.ButtonHovering.ButtonListener;
import pl.karol202.cncprinter.Axis;

import javax.swing.*;
import java.awt.*;

class PanelAxis extends JPanel
{
	private ButtonHovering buttonZero;
	private ButtonHovering buttonLeft;
	private ButtonHovering buttonRight;
	private JLabel label;
	
	PanelAxis()
	{
		super(new GridBagLayout());
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
		
		initZeroButton();
		initLeftButton();
		initRightButton();
		initLabel();
	}
	
	private void initZeroButton()
	{
		buttonZero = new ButtonHovering(ImageLoader.loadImage("/res/zero.png"));
		buttonZero.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY));
		add(buttonZero, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void initLeftButton()
	{
		buttonLeft = new ButtonHovering(ImageLoader.loadImage("/res/left.png"));
		add(buttonLeft, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void initRightButton()
	{
		buttonRight = new ButtonHovering(ImageLoader.loadImage("/res/right.png"));
		add(buttonRight, new GridBagConstraints(3, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void initLabel()
	{
		label = new JLabel();
		label.setFont(getFontForLabel());
		add(label, new GridBagConstraints(2, 0, 1, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 8, 0, 8),
				0, 0));
	}
	
	private Font getFontForLabel()
	{
		Font font = label.getFont();
		return new Font(font.getName(), Font.PLAIN, 24);
	}
	
	void setZeroButtonListener(ButtonListener listener)
	{
		buttonZero.setReleasedListener(listener);
	}
	
	void setLeftButtonPressListener(ButtonListener listener)
	{
		buttonLeft.setPressedListener(listener);
	}
	
	void setLeftButtonReleaseListener(ButtonListener listener)
	{
		buttonLeft.setReleasedListener(listener);
	}
	
	void setRightButtonPressListener(ButtonListener listener)
	{
		buttonRight.setPressedListener(listener);
	}
	
	void setRightButtonReleaseListener(ButtonListener listener)
	{
		buttonRight.setReleasedListener(listener);
	}
	
	void updateAxisValue(Axis axis, float value)
	{
		String string = String.format("%s: %.3f", axis.name(), value);
		label.setText(string);
	}
}