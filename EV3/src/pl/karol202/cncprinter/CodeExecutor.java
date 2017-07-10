package pl.karol202.cncprinter;

import java.util.HashMap;
import java.util.stream.Stream;

class CodeExecutor implements Runnable
{
	private Machine machine;
	private boolean running;
	private String[] lines;
	private int line;
	private HashMap<Word, Float> modals;
	private HashMap<Word, Float> notModals;
	
	private Movement movement;
	
	CodeExecutor(Machine machine, byte[] code)
	{
		this.machine = machine;
		
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
	
	public void run()
	{
		running = true;
		Stream.of(lines).forEach(this::runLine);
		running = false;
		machine.floatAll();
	}
	
	private void runLine(String line)
	{
		System.out.println(line);
		String[] words = line.split("\\s+");
		Stream.of(words).forEach(this::parseWord);
		apply();
	}
	
	private void parseWord(String word)
	{
		char symbol = word.charAt(0);
		Word wordType = Word.getBySymbol(symbol);
		String valueString = word.substring(1);
		float value = Float.parseFloat(valueString);
		
		if(wordType == null) error("Unknown symbol: " + symbol);
		if(wordType.isModal()) modals.put(wordType, value);
		else notModals.put(wordType, value);
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
		float rawMovementType = modals.get(Word.MOVEMENT_TYPE);
		int movementType = (int) rawMovementType;
		if(movementType == rawMovementType) return movementType;
		else error("Unknown movement type: " + rawMovementType);
		return -1;
	}
	
	private void error(String message)
	{
		throw new RuntimeException("Error at line " + line + ": " + message);
	}
	
	boolean isRunning()
	{
		return running;
	}
}
