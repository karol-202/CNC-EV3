package pl.karol202.cncprinter;

enum Word
{
	MOVEMENT_TYPE('G', true),
	SPEED('F', true),
	AXIS_X('X', false),
	AXIS_Y('Y', false),
	AXIS_Z('Z', false);
	
	private char symbol;
	private boolean modal;
	
	Word(char symbol, boolean modal)
	{
		this.symbol = symbol;
		this.modal = modal;
	}
	
	public static Word getBySymbol(char symbol)
	{
		for(Word word : values())
			if(word.symbol == symbol) return word;
		return null;
	}
	
	public boolean isModal()
	{
		return modal;
	}
}
