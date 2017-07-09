package pl.karol202.cncprinter;

public enum Word
{
	MOVEMENT_TYPE("G", true),
	SPEED("F", true),
	AXIS_X("X", false),
	AXIS_Y("Y", false),
	AXIS_Z("Z", false);
	
	private String symbol;
	private boolean modal;
	
	Word(String symbol, boolean modal)
	{
		this.symbol = symbol;
		this.modal = modal;
	}
	
	public static Word getBySymbol(String symbol)
	{
		for(Word word : values())
			if(word.symbol.equals(symbol)) return word;
		return null;
	}
	
	public boolean isModal()
	{
		return modal;
	}
}
