package is.iclt.icenlp.core.apertium;

import java.util.ArrayList;

// Pojo for an Apertium Entry
public class ApertiumEntry
{
	private String surfaceForm;
	private ArrayList<LexicalUnit> possibleLexicalUnits;
	
	public ApertiumEntry(String surfaceForm, ArrayList<LexicalUnit> possibleLexicalUnits)
	{
		this.surfaceForm = surfaceForm;
		this.possibleLexicalUnits = possibleLexicalUnits;
	}

	public String getSurfaceForm()
	{
		return surfaceForm;
	}

	public ArrayList<LexicalUnit> getPossibleLexicalUnits()
	{
		return possibleLexicalUnits;
	}
}
