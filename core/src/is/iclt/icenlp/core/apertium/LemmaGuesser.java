package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.utils.MappingLexicon;

import java.util.ArrayList;

/**
 * This is class is used to try to guess the lemma of the lexeme inserted
 * based on the apertium entries provided.
 * 
 * It finds the apertium tags for the IceNLP tag string and uses the first tag only
 * 
 * It then starts by finding the word that matches the lexeme
 * 
 * If all the lemmas as the same within that apertium entry, then it returns
 * the first lemma.
 * 
 * If they are not all the same then it tries to find the lexical unit that has the same
 * first tag as the one provided.
 */
public class LemmaGuesser
{
	private String lexeme;
	private ArrayList<ApertiumEntry> entries;
	private String firstTag;

	public LemmaGuesser(String lexeme, ArrayList<ApertiumEntry> entries, String tags, MappingLexicon mappingLexicon)
	{
		this.lexeme = lexeme;
		this.entries = entries;
		
		this.firstTag = getFirstTag(mappingLexicon.lookupTagmap(tags, true));
	}
	
	// Returns the data from the first character to the 2nd <
	// Which should be the first tag
	private String getFirstTag(String tags)
	{
		// We have received something that is not set or is not a tag
		// This might happen with space characters
		if(tags == null || !tags.startsWith("<"))
		{
			return null;
		}
		
		return tags.substring(0, tags.indexOf('<', 1));
	}
	
	/**
	 * Returns a best guess on the lemma based on the information provided
	 * by the constructor
	 */
	public String guess()
	{
		// Some data was passed to us wrong
		if(lexeme == null || entries == null || firstTag == null)
		{
			return null;
		}
		
		// For every entry
		for(ApertiumEntry ae: entries)
		{
			// Is it the same as the lexeme
			// Then we have found the word we are trying to find the lemma for
			if(ae.getSurfaceForm().equals(lexeme))
			{
				// First we check if all the lemmas are the same
				// If they are, then we use any one of them
				String sameCheck = ae.getPossibleLexicalUnits().get(0).getLemma();
				boolean isSame = true;
				
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					// Not the same
					if(!lu.getLemma().equals(sameCheck))
					{
						isSame = false;
						break;
					}
				}
				
				// They are all the same, return the 1st one.
				if(isSame)
				{
					return sameCheck;
				}
				
				// Else we have to do some logic to figure out what lemma to use
				
				// For every lexical unit
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					// If that starts with the first tag
					// Then we want to use that lemma
					if(lu.getSymbols().startsWith(firstTag))
					{
						return lu.getLemma();
					}
				}
			}
		}
		
		// Else we return null
		return null;
	}
}
