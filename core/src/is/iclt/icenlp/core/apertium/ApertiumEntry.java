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
	
	// Returns true for <cm> and <sent>
	public boolean isSeperator()
	{
		return possibleLexicalUnits.get(0).isComma() || possibleLexicalUnits.get(0).isSentence();
	}
	
	// Returns true if there is only one LU and it is unknown
	public boolean isUnknown()
	{
		return possibleLexicalUnits.size() == 1 && possibleLexicalUnits.get(0).isUnknown();
	}

	// Returns true if the trimmed surface form has a space in it
	public boolean isMWE()
	{
		String trimSurfaceForm = surfaceForm.trim();
		
		return trimSurfaceForm.contains(" ") && trimSurfaceForm.length() > 1;
	}

	public boolean isAnyLuPreposition()
	{
		for(LexicalUnit lu: possibleLexicalUnits)
		{
			if(lu.isPreposition())
			{
				return true;
			}
		}
		
		return false;
	}
}
