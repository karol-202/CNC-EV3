package pl.karol202.cncclient.cnc;

import pl.karol202.cncprinter.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodePreviewer
{
	private GCode gcode;
	private int line;
	private HashMap<Word, Float> modals;
	private HashMap<Word, Float> notModals;
	private List<PreviewPoint> previewPoints;
	private String error;
	
	public CodePreviewer(GCode gcode)
	{
		this.gcode = gcode;
		reset();
	}
	
	private void reset()
	{
		this.line = 0;
		this.modals = new HashMap<>();
		this.notModals = new HashMap<>();
		this.previewPoints = new ArrayList<>();
		this.error = null;
	}
	
	public List<PreviewPoint> getAllPoints()
	{
		reset();
		gcode.getAllLines().forEach(this::parseLine);
		return previewPoints;
	}
	
	public List<PreviewPoint> getPointsOfLinesBeforeOrAtSelection()
	{
		reset();
		gcode.getLinesBeforeOrAtSelectionStream().forEach(this::parseLine);
		return previewPoints;
	}
	
	private void parseLine(String line)
	{
		String[] words = line.split("\\s+");
		for(String word : words) parseWord(word);
		apply();
	}
	
	private void parseWord(String word)
	{
		line++;
		
		char symbol = word.charAt(0);
		Word wordType = Word.getBySymbol(symbol);
		String valueString = word.substring(1);
		float value = parseFloat(valueString);
		
		if(wordType == null) error("Nieznany symbol " + symbol);
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
			error("Nie można odczytać liczby: " + string);
			return -1;
		}
	}
	
	private void apply()
	{
		PreviewPoint point = new PreviewPoint(getCurrentX(), getCurrentY(), getCurrentZ());
		switch(getMovementType())
		{
		case 0: point.setInterpolated(false); break;
		case 1: point.setInterpolated(true); break;
		default: point = null;
		}
		if(point != null) previewPoints.add(point);
	}
	
	private int getMovementType()
	{
		if(!modals.containsKey(Word.MOVEMENT_TYPE)) return -1;
		float rawMovementType = modals.get(Word.MOVEMENT_TYPE);
		int movementType = (int) rawMovementType;
		if(movementType == rawMovementType) return movementType;
		else
		{
			error("Nieznany typ ruchu: " + rawMovementType);
			return -1;
		}
	}
	
	private float getCurrentX()
	{
		return getFromNotModals(Word.AXIS_X);
	}
	
	private float getCurrentY()
	{
		return getFromNotModals(Word.AXIS_Y);
	}
	
	private float getCurrentZ()
	{
		return getFromNotModals(Word.AXIS_Z);
	}
	
	private float getFromNotModals(Word word)
	{
		Float f = notModals.get(word);
		return f != null ? f : 0;
	}
	
	private void error(String message)
	{
		error = "Błąd w linii " + line + ": " + message;
	}
	
	public String getError()
	{
		return error;
	}
}
