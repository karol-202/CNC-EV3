package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.client.ClientManager;
import pl.karol202.cncclient.client.ConnectionListener;
import pl.karol202.cncclient.cnc.GCode;
import pl.karol202.cncclient.cnc.GCodeLoader;
import pl.karol202.cncclient.cnc.MachineState;
import pl.karol202.cncclient.cnc.ManualControl;
import pl.karol202.cncprinter.Axis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;

import static javax.swing.JList.VERTICAL;
import static pl.karol202.cncprinter.ManualControlAction.*;

public class FrameMain extends JFrame implements ConnectionListener
{
	private ClientManager client;
	private GCode gcode;
	private GCodeLoader gcodeLoader;
	private ManualControl manualControl;
	private MachineState machineState;
	
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem itemNewFile;
	private JMenuItem itemOpenFile;
	private JMenuItem itemSaveFile;
	private JMenuItem itemSaveFileAs;
	
	private JPanel panelGCode;
	private JScrollPane scrollPaneGCode;
	private JList<String> listGCode;
	private GCodeListModel listModelGCode;
	private JTextField fieldGCode;
	private JButton buttonGCodeAdd;
	
	private JPanel panelControl;
	private JLabel labelConnection;
	private JButton buttonConnection;
	private JLabel labelGCodeStatus;
	private JButton buttonGCodeUpdate;
	private JLabel labelWorkStatus;
	private JButton buttonStart;
	private JButton buttonPause;
	private JButton buttonStop;
	
	private JPanel panelAxes;
	private PanelAxis panelX;
	private PanelAxis panelY;
	private PanelAxis panelZ;
	private JLabel labelSpeed;
	private JSpinner spinnerSpeed;
	
	public FrameMain(ClientManager client, GCode gcode, GCodeLoader gcodeLoader, ManualControl manualControl, MachineState machineState)
	{
		super("CNC - Client");
		this.client = client;
		this.gcode = gcode;
		this.gcodeLoader = gcodeLoader;
		this.manualControl = manualControl;
		this.machineState = machineState;
		
		setFrameParams();
		initMenuBar();
		initGCodePanel();
		initControlPanel();
		initAxesPanel();
		
		updateControlPanel();
		updateAxesPanel();
	}
	
	private void setFrameParams()
	{
		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setVisible(true);
	}
	
	private void initMenuBar()
	{
		menuBar = new JMenuBar();
		initFileMenu();
		setJMenuBar(menuBar);
	}
	
	private void initFileMenu()
	{
		menuFile = new JMenu("Plik");
		initNewFileItem();
		initOpenFileItem();
		initSaveFileItem();
		initSaveFileAsItem();
		menuBar.add(menuFile);
	}
	
	private void initNewFileItem()
	{
		itemNewFile = new JMenuItem("Nowy");
		itemNewFile.addActionListener(e -> newFile());
		menuFile.add(itemNewFile);
	}
	
	private void initOpenFileItem()
	{
		itemOpenFile = new JMenuItem("Otwórz");
		itemOpenFile.addActionListener(e -> openFile());
		menuFile.add(itemOpenFile);
	}
	
	private void initSaveFileItem()
	{
		itemSaveFile = new JMenuItem("Zapisz");
		itemSaveFile.addActionListener(e -> saveFile());
		menuFile.add(itemSaveFile);
	}
	
	private void initSaveFileAsItem()
	{
		itemSaveFileAs = new JMenuItem("Zapisz jako");
		itemSaveFileAs.addActionListener(e -> saveFileAs());
		menuFile.add(itemSaveFileAs);
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
		fieldGCode = new JTextField(20);
		fieldGCode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() != KeyEvent.VK_ENTER) return;
				addGCodeLine();
			}
		});
		panelGCode.add(fieldGCode, new GridBagConstraints(0, 1, 1, 1, 1, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 5, 1),
				0, 0));
	}
	
	private void initGCodeAddButton()
	{
		buttonGCodeAdd = new JButton("+");
		buttonGCodeAdd.setFocusable(false);
		buttonGCodeAdd.addActionListener(e -> addGCodeLine());
		panelGCode.add(buttonGCodeAdd, new GridBagConstraints(1, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 4, 0),
				5, 0));
	}
	
	private void initControlPanel()
	{
		panelControl = new JPanel(new GridBagLayout());
		panelControl.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
		initConnectionLabel();
		initConnectionButton();
		initGCodeStatusLabel();
		initGCodeUpdateButton();
		initWorkStatusLabel();
		initStartButton();
		initPauseButton();
		initStopButton();
		add(panelControl, BorderLayout.SOUTH);
	}
	
	private void initConnectionLabel()
	{
		labelConnection = new JLabel();
		panelControl.add(labelConnection, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0),
				0, 0));
	}
	
	private void initConnectionButton()
	{
		buttonConnection = new JButton();
		buttonConnection.setFocusable(false);
		panelControl.add(buttonConnection, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0),
				36, 8));
	}
	
	private void initGCodeStatusLabel()
	{
		labelGCodeStatus = new JLabel();
		panelControl.add(labelGCodeStatus, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0),
				0, 0));
	}
	
	private void initGCodeUpdateButton()
	{
		buttonGCodeUpdate = new JButton("Aktualizuj kod");
		buttonGCodeUpdate.setFocusable(false);
		buttonGCodeUpdate.addActionListener(e -> updateGCode());
		panelControl.add(buttonGCodeUpdate, new GridBagConstraints(1, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0),
				36, 8));
	}
	
	private void initWorkStatusLabel()
	{
		labelWorkStatus = new JLabel();
		panelControl.add(labelWorkStatus, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0),
				0, 0));
	}
	
	private void initStartButton()
	{
		buttonStart = new JButton("Start");
		buttonStart.setFocusable(false);
		buttonStart.addActionListener(e -> start());
		panelControl.add(buttonStart, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0),
				36, 8));
	}
	
	private void initPauseButton()
	{
		buttonPause = new JButton();
		buttonPause.setFocusable(false);
		buttonPause.addActionListener(e -> pause());
		panelControl.add(buttonPause, new GridBagConstraints(3, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0),
				36, 8));
	}
	
	private void initStopButton()
	{
		buttonStop = new JButton("STOP");
		buttonStop.setFocusable(false);
		buttonStop.addActionListener(e -> stop());
		panelControl.add(buttonStop, new GridBagConstraints(4, 0, 1, 2, 1, 0,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(5, 0, 5, 5),
				36, 8));
	}
	
	private void initAxesPanel()
	{
		panelAxes = new JPanel(new GridBagLayout());
		initXPanel();
		initYPanel();
		initZPanel();
		initSpeedLabel();
		initSpeedSpinner();
		add(panelAxes, BorderLayout.EAST);
	}
	
	private void initXPanel()
	{
		panelX = new PanelAxis();
		panelX.setZeroButtonListener(() -> manualControl.control(Axis.X, ZERO));
		panelX.setLeftButtonPressListener(() -> manualControl.control(Axis.X, MOVE_LEFT));
		panelX.setLeftButtonReleaseListener(() -> manualControl.control(Axis.X, STOP));
		panelX.setRightButtonPressListener(() -> manualControl.control(Axis.X, MOVE_RIGHT));
		panelX.setRightButtonReleaseListener(() -> manualControl.control(Axis.X, STOP));
		panelAxes.add(panelX, new GridBagConstraints(0, 0, 2, 1, 0, 1,
				GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0),
				0, 0));
	}
	
	private void initYPanel()
	{
		panelY = new PanelAxis();
		panelY.setZeroButtonListener(() -> manualControl.control(Axis.Y, ZERO));
		panelY.setLeftButtonPressListener(() -> manualControl.control(Axis.Y, MOVE_LEFT));
		panelY.setLeftButtonReleaseListener(() -> manualControl.control(Axis.Y, STOP));
		panelY.setRightButtonPressListener(() -> manualControl.control(Axis.Y, MOVE_RIGHT));
		panelY.setRightButtonReleaseListener(() -> manualControl.control(Axis.Y, STOP));
		panelAxes.add(panelY, new GridBagConstraints(0, 1, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0),
				0, 0));
	}
	
	private void initZPanel()
	{
		panelZ = new PanelAxis();
		panelZ.setZeroButtonListener(() -> manualControl.control(Axis.Z, ZERO));
		panelZ.setLeftButtonPressListener(() -> manualControl.control(Axis.Z, MOVE_LEFT));
		panelZ.setLeftButtonReleaseListener(() -> manualControl.control(Axis.Z, STOP));
		panelZ.setRightButtonPressListener(() -> manualControl.control(Axis.Z, MOVE_RIGHT));
		panelZ.setRightButtonReleaseListener(() -> manualControl.control(Axis.Z, STOP));
		panelAxes.add(panelZ, new GridBagConstraints(0, 2, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0),
				0, 0));
	}
	
	private void initSpeedLabel()
	{
		labelSpeed = new JLabel("Prędkość: ");
		panelAxes.add(labelSpeed, new GridBagConstraints(0, 3, 1, 1, 1, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(12, 0, 0, 2),
				0, 0));
	}
	
	private void initSpeedSpinner()
	{
		spinnerSpeed = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
		spinnerSpeed.addChangeListener(e -> manualControl.setSpeed((int) spinnerSpeed.getValue()));
		spinnerSpeed.setPreferredSize(new Dimension(70, spinnerSpeed.getPreferredSize().height));
		panelAxes.add(spinnerSpeed, new GridBagConstraints(1, 3, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(8, 0, 0, 0),
				0, 0));
	}
	
	private void updateControlPanel()
	{
		updateConnectionLabel();
		updateConnectionButton();
		updateGCodeStatusLabel();
		updateGCodeUpdateButton();
		updateWorkStatusLabel();
		updateStartButton();
		updatePauseButton();
		updateStopButton();
	}
	
	private void updateConnectionLabel()
	{
		if(client.isAuthenticated()) labelConnection.setText("Połączono");
		else if(client.isConnected()) labelConnection.setText("Uwierzytelnianie");
		else labelConnection.setText("Nie połączono");
	}
	
	private void updateConnectionButton()
	{
		buttonConnection.setText(client.isConnected() ? "Rozłącz" : "Połącz");
		buttonConnection.setEnabled(!client.isConnected() || client.isAuthenticated());
		removeButtonListeners(buttonConnection);
		buttonConnection.addActionListener(client.isConnected() ? e -> disconnect() : e -> connect());
	}
	
	private void updateGCodeStatusLabel()
	{
		labelGCodeStatus.setVisible(client.isAuthenticated());
		labelGCodeStatus.setText(gcode.isUpToDate() ? "Kod aktualny" : "Kod nieaktualny");
		labelGCodeStatus.setForeground(gcode.isUpToDate() ? Color.BLACK : Color.RED);
	}
	
	private void updateGCodeUpdateButton()
	{
		buttonGCodeUpdate.setEnabled(client.isAuthenticated());
	}
	
	private void updateWorkStatusLabel()
	{
		labelWorkStatus.setVisible(client.isAuthenticated());
		if(machineState.isRunning() && !machineState.isPaused()) labelWorkStatus.setText("Wykonywanie");
		else if(machineState.isRunning() && machineState.isPaused()) labelWorkStatus.setText("Pauza");
		else labelWorkStatus.setText("Bezczynność");
	}
	
	private void updateStartButton()
	{
		buttonStart.setEnabled(client.isAuthenticated() && !machineState.isRunning());
	}
	
	private void updatePauseButton()
	{
		buttonPause.setText(machineState.isPaused() ? "Wznów" : "Wstrzymaj");
		buttonPause.setEnabled(client.isAuthenticated() && machineState.isRunning());
	}
	
	private void updateStopButton()
	{
		buttonStop.setEnabled(client.isAuthenticated());
	}
	
	private void removeButtonListeners(AbstractButton button)
	{
		ActionListener[] listeners = button.getActionListeners();
		Stream.of(listeners).forEach(button::removeActionListener);
	}
	
	private void updateAxesPanel()
	{
		panelX.updateAxisValue(Axis.X, machineState.getX());
		panelY.updateAxisValue(Axis.Y, machineState.getY());
		panelZ.updateAxisValue(Axis.Z, machineState.getZ());
	}
	
	private void newFile()
	{
		gcodeLoader.newFile();
		listModelGCode.fireAllRemoved();
		listGCode.clearSelection();
	}
	
	private void openFile()
	{
		gcodeLoader.openFile(this);
		listModelGCode.fireAllChanged();
		listGCode.clearSelection();
		
		updateControlPanel();
	}
	
	private void saveFile()
	{
		gcodeLoader.saveFile(this);
	}
	
	private void saveFileAs()
	{
		gcodeLoader.saveFileAs(this);
	}
	
	private void addGCodeLine()
	{
		int selection = listGCode.getSelectedIndex();
		int position = selection + 1;
		
		String line = fieldGCode.getText();
		if(line.isEmpty()) return;
		
		gcode.addLine(position, line);
		fieldGCode.setText("");
		listModelGCode.fireLineAdded(position);
		listGCode.setSelectedIndex(position);
		
		updateControlPanel();
	}
	
	private void removeGCodeLine()
	{
		int selection = listGCode.getSelectedIndex();
		if(selection == -1) return;
		
		gcode.removeLine(selection);
		listModelGCode.fireLineRemoved(selection);
		
		updateControlPanel();
	}
	
	private void connect()
	{
		if(client.isConnected()) return;
		String ip = JOptionPane.showInputDialog(this, "Podaj numer IP plotera:", "Połączenie",
				JOptionPane.PLAIN_MESSAGE);
		if(ip == null || ip.isEmpty()) return;
		client.connect(ip);
		client.runStateCheckLoop(machineState);
	}
	
	private void disconnect()
	{
		if(!client.isConnected()) return;
		client.disconnect();
	}
	
	private void updateGCode()
	{
		if(!client.isAuthenticated()) return;
		byte[] code = gcode.toByteArray();
		client.sendGCode(code);
	}
	
	private void start()
	{
		if(!client.isAuthenticated()) return;
		client.start();
	}
	
	private void pause()
	{
		if(!client.isAuthenticated()) return;
		if(!machineState.isPaused()) client.pause();
		else client.resume();
	}
	
	private void stop()
	{
		if(!client.isAuthenticated()) return;
		client.stop();
	}
	
	@Override
	public void onConnected()
	{
		updateControlPanel();
	}
	
	@Override
	public void onAuthenticated()
	{
		gcode.setUpToDate(false);
		updateControlPanel();
	}
	
	@Override
	public void onUnknownHost()
	{
		JOptionPane.showMessageDialog(this, "Nie można połączyć.\nNieznany host", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onCannotConnect()
	{
		JOptionPane.showMessageDialog(this, "Nie można połączyć.", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onAuthenticationFailed()
	{
		JOptionPane.showMessageDialog(this, "Nie można połączyć.\nUwierzytelnianie zakończone niepowodzeniem.", "Błąd", JOptionPane.ERROR_MESSAGE);
		updateControlPanel();
	}
	
	@Override
	public void onDisconnected()
	{
		updateControlPanel();
	}
	
	@Override
	public void onConnectionProblem()
	{
		JOptionPane.showMessageDialog(this, "Problem z połączeniem.", "Błąd", JOptionPane.ERROR_MESSAGE);
		updateControlPanel();
	}
	
	@Override
	public void onSent()
	{
		gcode.setUpToDate(true);
		updateControlPanel();
	}
	
	@Override
	public void onSendingDenied()
	{
		JOptionPane.showMessageDialog(this, "Urządzenie odmówiło przyjęcia kodu.", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onStarted()
	{
		updateControlPanel();
	}
	
	@Override
	public void onStartingDenied()
	{
		JOptionPane.showMessageDialog(this, "Urządzenie odmówiło rozpoczęcia programu", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onPaused()
	{
		updateControlPanel();
	}
	
	@Override
	public void onPausingDenied()
	{
		JOptionPane.showMessageDialog(this, "Urządzenie odmówiło wstrzymania programu", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onResumed()
	{
		updateControlPanel();
	}
	
	@Override
	public void onResumingDenied()
	{
		JOptionPane.showMessageDialog(this, "Urządzenie odmówiło wznowienia programu", "Błąd", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onStopped()
	{
		updateControlPanel();
	}
	
	@Override
	public void onMachineStateUpdated()
	{
		updateControlPanel();
		updateAxesPanel();
	}
}