package pl.karol202.cncprinter;

import java.util.ArrayList;
import java.util.HashMap;

public class Reader implements Runnable
{
	private boolean running;
	private byte[] bytes;
	private int line;
	private HashMap<Word, Float> modals;
	private HashMap<Word, Float> nModals;
	private ArrayList<Character> current;
	private boolean readingNumber;
	private boolean readingWord;
	private Word currentWord;
	private boolean newLine;
	
	public Reader(String code)
	{
		this(code.getBytes());
	}
	
	public Reader(byte[] code)
	{
		this.bytes = code;
		this.line = 1;
		this.modals = new HashMap<Word, Float>();
		this.nModals = new HashMap<Word, Float>();
		this.current = new ArrayList<Character>();
	}
	
	public void run()
	{
		running = true;
		for (int i = 0; i < bytes.length; i++)
		{
			byte b = bytes[i];
			
			this.newLine = false;
			char ch = (char)b;
			if (((b < 48) || (b > 57)) && (b != 43) && (b != 45) && (b != 46) && (this.readingNumber))
				number();
			if (((b < 65) || (b > 90)) && (this.readingWord))
				word();
			
			if (((b >= 48) && (b <= 57)) || (b == 43) || (b == 45) || (b == 46))
			{
				this.readingNumber = true;
				this.current.add(ch);
			}
			else if ((b >= 65) && (b <= 90))
			{
				this.readingWord = true;
				this.current.add(ch);
			}
			else if (b == 10) endOfLine();
			else if ((b != 32) && (b != 13)) error("Unexpected token");
		}
		if (!this.newLine)
			endOfLine();
		running = false;
		Machine.floatAll();
	}
	
	private void endOfLine()
	{
		if (this.readingNumber)
			number();
		
		this.newLine = true;
		float move = this.modals.get(Word.MOVEMENT_TYPE);
		if (move == 0)
			MovementLinear.movementRapid(this.modals, this.nModals);
		else if (move == 1)
			MovementLinear.movementInterpolation(this.modals, this.nModals);
		this.nModals.clear();
		this.line += 1;
	}
	
	private void number()
	{
		if (this.currentWord == null) error("Unexpected number");
		
		String str = "";
		for(Character ch : current)
			str += ch;
		float number = Float.parseFloat(str);
		
		if (this.currentWord.isModal())
			this.modals.put(this.currentWord, number);
		else
			this.nModals.put(this.currentWord, number);
		
		this.currentWord = null;
		
		this.current.clear();
		this.readingNumber = false;
	}
	
	private void word()
	{
		String str = "";
		for(Character ch : current)
			str += ch;
		Word word = Word.getBySymbol(str);
		if (word == null) error("Invalid word: " + str);
		this.currentWord = word;
		
		this.current.clear();
		this.readingWord = false;
	}
	
	private void error(String message)
	{
		throw new RuntimeException("Error at line " + this.line + ": " + message);
	}
	
	public boolean isRunning()
	{
		return running;
	}
}
