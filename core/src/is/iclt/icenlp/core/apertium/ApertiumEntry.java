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
	
	// Removes the lu from the list of lexical units
	public void removeLexicalUnit(LexicalUnit lu)
	{
		possibleLexicalUnits.remove(lu);
	}
	
	// Returns true if any lexical unit is a verb
	public boolean isAnyLuAVerb()
	{
		for(LexicalUnit lu: possibleLexicalUnits)
		{
			if(lu.isVerb())
			{
				return true;
			}
		}
		
		return false;
	}
}
