package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.client.ConnectionListener;
import pl.karol202.cncclient.gcode.GCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static javax.swing.JList.VERTICAL;

public class FrameMain extends JFrame implements ConnectionListener
{
	private GCode gcode;
	
	private JPanel panelGCode;
	private JScrollPane scrollPaneGCode;
	private JList<String> listGCode;
	private GCodeListModel listModelGCode;
	private JTextField fieldGCode;
	private JButton buttonGCodeAdd;
	
	public FrameMain(GCode gcode)
	{
		super("CNC - Client");
		this.gcode = gcode;
		
		setFrameParams();
		initGCodePanel();
	}
	
	private void setFrameParams()
	{
		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setVisible(true);
	}
	
	private void initGCodePanel()
	{
		panelGCode = new JPanel(new GridBagLayout());
		initGCodeScrollPane();
		initGCodeField();
		initGCodeAddButton();
		add(panelGCode, BorderLayout.WEST);
	}
	
	private void initGCodeScrollPane()
	{
		initGCodeList();
		scrollPaneGCode = new JScrollPane(listGCode);
		panelGCode.add(scrollPaneGCode, new GridBagConstraints(0, 0, 2, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void initGCodeList()
	{
		listModelGCode = new GCodeListModel(gcode);
		
		listGCode = new JList<>(listModelGCode);
		listGCode.setLayoutOrientation(VERTICAL);
		listGCode.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listGCode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_DELETE) removeGCodeLine();
			}
		});
	}
	
	private void initGCodeField()
	{
		fieldGCode = new JTextField();
		panelGCode.add(fieldGCode, new GridBagConstraints(0, 1, 1, 1, 1, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void initGCodeAddButton()
	{
		buttonGCodeAdd = new JButton("+");
		buttonGCodeAdd.setFocusable(false);
		buttonGCodeAdd.addActionListener(e -> addGCodeLine());
		panelGCode.add(buttonGCodeAdd, new GridBagConstraints(1, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
	}
	
	private void addGCodeLine()
	{
		int selection = listGCode.getSelectedIndex();
		int position = selection + 1;
		String line = fieldGCode.getText();
		
		gcode.addLine(position, line);
		fieldGCode.setText("");
		listModelGCode.fireLineAdded(position);
		listGCode.setSelectedIndex(position);
	}
	
	private void removeGCodeLine()
	{
		int selection = listGCode.getSelectedIndex();
		if(selection == -1) return;
		
		gcode.removeLine(selection);
		listModelGCode.fireLineRemoved(selection);
	}
	
	@Override
	public void onConnected()
	{
		
	}
	
	@Override
	public void onUnknownHost()
	{
		
	}
	
	@Override
	public void onCannotConnect()
	{
		
	}
	
	@Override
	public void onAuthenticationFailed()
	{
		
	}
	
	@Override
	public void onDisconnected()
	{
		
	}
	
	@Override
	public void onConnectionProblem()
	{
		
	}
	
	@Override
	public void onSent()
	{
		
	}
	
	@Override
	public void onSendingDenied()
	{
		
	}
	
	@Override
	public void onStarted()
	{
		
	}
	
	@Override
	public void onStartingDenied()
	{
		
	}
}