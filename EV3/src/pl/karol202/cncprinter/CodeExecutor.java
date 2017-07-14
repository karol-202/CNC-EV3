package pl.karol202.cncprinter;

import java.util.HashMap;

class CodeExecutor implements Runnable, MachineListener
{
	private Machine machine;
	private boolean running;
	private boolean paused;
	
	private String[] lines;
	private int line; //Use line
	private HashMap<Word, Float> modals;
	private HashMap<Word, Float> notModals;
	
	private Movement movement;
	
	CodeExecutor(Machine machine, byte[] code)
	{
		this.machine = machine;
		this.running = false;
		this.paused = false;
		
		readLines(code);
		this.line = 1;
		this.modals = new HashMap<>();
		this.notModals = new HashMap<>();
		
		this.movement = new Movement(machine, modals, notModals);
	}
	
	private void readLines(byte[] bytes)
	{
		String code = new String(bytes);
		lines = code.split("\\r\\n");
	}
	
	public void run() //Reset
	{
		running = true;
		paused = false;
		machine.floatAll();
		for(String line : lines) runLine(line);
		running = false;
		machine.floatAll();
	}
	
	private void runLine(String line)
	{
		if(!running) return;
		System.out.println(line);
		String[] words = line.split("\\s+");
		for(String word : words) parseWord(word);
		apply();
		while(paused) Thread.yield();
	}
	
	private void parseWord(String word)
	{
		char symbol = word.charAt(0);//Error
		Word wordType = Word.getBySymbol(symbol);
		String valueString = word.substring(1);
		float value = parseFloat(valueString);
		
		if(wordType == null) error("Unknown symbol: " + symbol);
		else if(wordType.isModal()) modals.put(wordType, value);
		else notModals.put(wordType, value);
	}
	
	private float parseFloat(String string)
	{
		try
		{
			return Float.parseFloat(string);
		}
		catch(NumberFormatException e)
		{
			error("Cannot parse number: " + string);
			return -1;
		}
	}
	
	private void apply()
	{
		switch(getMovementType())
		{
		case 0: movement.doRapidMovement(); break;
		case 1: movement.doInterpolatedMovement(); break;
		}
	}
	
	private int getMovementType()
	{
		if(!modals.containsKey(Word.MOVEMENT_TYPE)) return -1;
		float rawMovementType = modals.get(Word.MOVEMENT_TYPE);
		int movementType = (int) rawMovementType;
		if(movementType == rawMovementType) return movementType;
		else
		{
			error("Unknown movement type: " + rawMovementType);
			return -1;
		}
	}
	
	void stop()
	{
		running = false;
	}
	
	@Override
	public void onProblemDetected()
	{
		stop();
	}
	
	private void error(String message)
	{
		System.err.println("Error at line " + line + ": " + message);
		stop();
	}
	
	boolean isRunning()
	{
		return running;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}
}
