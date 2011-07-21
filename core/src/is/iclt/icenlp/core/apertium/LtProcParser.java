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
			
			// If we found an unknown word
			if(slashSplit.length == 2 && slashSplit[1].startsWith("*"))
			{
				lexicalUnit.add(new LexicalUnit(sf, sf, true));
			}
			// We found a possible space and/or other non letter characters ()'" etc
			else if(slashSplit.length == 1 && !slashSplit[0].contains("<"))
			{
				// Mark it as a space lexical unit
				LexicalUnit lu = new LexicalUnit(slashSplit[0], slashSplit[0], true, true);
				lexicalUnit.add(lu);
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
					
					// If there is a hash sign # in the symbol
					// Then it is "Start of invariable part of multiword marker."
					// Remove it and add to the lemma
					if(symbols.contains("#"))
					{
						String[] splitSymbols = symbols.split("#");
						
						symbols = splitSymbols[0];
						
						lexicalUnit.add(new LexicalUnit(lemma, symbols, splitSymbols[1]));
					}
					// Symbols do not contain a +, we can process normally
					else if(!symbols.contains("+"))
					{
						lexicalUnit.add(new LexicalUnit(lemma, symbols));
					}
					else
					{
						// If there is a plus sign in the symbol +
						// Then it is a "Joined Lexical unit"
						// We split it from the symbol and add it to a new
						// lexical unit
						String[] splitSymbols = symbols.split("\\+");
						symbols = splitSymbols[0];
						
						// This is the main word
						lexicalUnit.add(new LexicalUnit(lemma, symbols));
						
						int joinedTagStart = splitSymbols[1].indexOf("<");
						
						String joinedLemma = splitSymbols[1].substring(0, joinedTagStart);
						String joinedSymbols = splitSymbols[1].substring(joinedTagStart);
						
						// These kind of lexical units are joined to the previous word
						LexicalUnit joinedLU = new LexicalUnit(joinedLemma, joinedSymbols);
						joinedLU.setLinkedToPreviousWord(true);
						
						// This is the linked word
						lexicalUnit.add(joinedLU);
					}
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
		for(String s: splitResult)
		{
			if(!s.isEmpty())
			{
				result.add(s);
			}
		}
		
		return result;
	}
}
