package is.iclt.icenlp.core.apertium;

import java.util.ArrayList;

// Splits lt-proc output into sentences.
public class LtProcParser
{
	private ArrayList<String> splitInput;
	
	public LtProcParser(String input)
	{
		splitInput = split(input);
	}
	
	/**
	 * Parses the input and returns an array of each entry
	 */
	public ArrayList<ApertiumEntry> parse()
	{
		ArrayList<ApertiumEntry> entries = new ArrayList<ApertiumEntry>();
		
		// For each word, create an apertium entry out of it and put it in the array
		for(String word: splitInput)
		{
			// Split the word by the slashes
			String[] slashSplit = word.split("/");
			
			// Surface form is the first word
			String sf = slashSplit[0];
			
			// Create the array of possible lexical units
			ArrayList<LexicalUnit> lexicalUnit = new ArrayList<LexicalUnit>();
			
			// If there is nothing to split by (then we most likely have characters like .:() etc)
			if(slashSplit.length == 1)
			{
				lexicalUnit.add(new LexicalUnit(sf, sf));
			}
			// If we found an unknown word
			else if(slashSplit.length == 2 && slashSplit[1].startsWith("*"))
			{
				lexicalUnit.add(new LexicalUnit(sf, sf, true));
			}
			else
			{
				// The rest of the array are lex units
				for(int i = 1; i < slashSplit.length; i++)
				{	
					// Find where the tags begin
					int tagStart = slashSplit[i].indexOf("<");
					
					String lemma = slashSplit[i].substring(0, tagStart);
					String symbols = slashSplit[i].substring(tagStart);
					
					lexicalUnit.add(new LexicalUnit(lemma, symbols));
				}
			}
			
			entries.add(new ApertiumEntry(sf, lexicalUnit));
		}
		
		return entries;
	}
	
	/**
	 * Splits the input string by (^|^ $|^$|$) and cleans it up
	 */
	private ArrayList<String> split(String input)
	{
		// Split the input
		String[] splitResult = input.split("\\^|\\^ \\$|\\^\\$|\\$");
		
		ArrayList<String> result = new ArrayList<String>();
		
		// Remove the empty results
		// TODO: Should we keep the spaces?
		for(String s: splitResult)
		{
			if(!s.isEmpty() && !s.equals(" "))
			{
				result.add(s);
			}
		}
		
		return result;
	}
}






