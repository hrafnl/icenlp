package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.MappingLexicon;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class takes the apertium entries and converts them to the IceNLP tokens
 */
public class IceNLPTokenConverter
{
	private ArrayList<ApertiumEntry> entries;
	private MappingLexicon mapping;
	private Lexicon baseDict;
	private Lexicon otbDict;
	
	public IceNLPTokenConverter(ArrayList<ApertiumEntry> entries, MappingLexicon mapping) throws IOException
	{
		this.entries = entries;
		this.mapping = mapping;
		
		IceTaggerResources resource = new IceTaggerResources();
		this.baseDict = new Lexicon(resource.isDictionaryBase);
		this.otbDict = new Lexicon(resource.isDictionary);
	}
	
	/**
	 * Converts the apertium entries to a IceNLP token
	 * @return An Array of IceTokenTags
	 */
	public ArrayList<IceTokenTags> convert()
	{
		ArrayList<IceTokenTags> list = new ArrayList<IceTokenTags>();
		int counter = 0; // Used to find proper nouns.
		
		for(ApertiumEntry ae: entries)
		{
			// Add the lexeme
			IceTokenTags ice = new IceTokenTags();
			ice.lexeme = ae.getSurfaceForm();
			
			if(ae.isAnyLuPreposition())
			{
				handlePreposition(ice, ae);
			}
			else if(ae.isMWE())
			{
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					// Fix the lu if possible
					lexicalUnitFixes(lu);
					
					String invTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
					
					// TODO Might be an issue if we need that symbol set
					if(invTag != null)
					{
						ice.addTagWithLemma(invTag, lu.getLemma());
					}
				}
			}
			// If we have a verb in any of the lu's
			else if(ae.isAnyLuAVerb())
			{
				handleVerb(ice, ae);
			}
			else if(ae.getPossibleLexicalUnits().get(0).isUnknown() && !ae.getPossibleLexicalUnits().get(0).isSpace())
			{
				// We might have an unknown word
				handleUnknown(ice, counter);
			}
			// lt-proc treats ()' as spaces, but IceNLP does not
			else if(ae.getPossibleLexicalUnits().get(0).isSpace())
			{
				ice.lexeme = ice.lexeme.trim();
				
				if(ice.lexeme.isEmpty())
				{
					continue;
				}
				
				// "Space" characters have themselves as tags
				ice.addTag(ice.lexeme);
			}
			else
			{
				// Normal processing
				handleNormal(ice, ae);
			}
			
			list.add(ice);
			counter++;
		}
		
		return list;
	}
	
	private void handleNormal(IceTokenTags ice, ApertiumEntry ae)
	{
		// If it is a MWE, it is stored with _ instead of spaces in the dicts
		String lookupStr = ice.lexeme.replace(" ", "_");
		
		String tags = baseDict.lookup(lookupStr, false);
		
		// If the tag is not in the base dict, we will use the otb dict.
		if(tags == null)
		{
			tags = otbDict.lookup(lookupStr, false);
		}
		
		// Here we only work on words that have a tag set
		if(tags != null)
		{	
			// Now we should have the tag, if not, the word is not in any dictionary.
			// Split it and loop through each element
			String[] tagSplit = tags.split("_");
			
			// For each tag
			for(String tag: tagSplit)
			{
				// Look at each apertium entries tag to see if it matches with the tag
				// Insert that tag first into tag list
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					// Fix the lu if possible
					lexicalUnitFixes(lu);
					
					String invTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
					
					// We found the correct lu
					if(invTag != null && tag.equals(invTag))
					{
						ice.addTagWithLemma(tag, lu.getLemma());
						
						// Remove the lu since we found it.
						ae.removeLexicalUnit(lu);
						
						break;
					}
				}
			}
			
			// Here we have a possibility that there are still possible lexical units
			// Then we add them in the order they are now
			for(LexicalUnit lu: ae.getPossibleLexicalUnits())
			{
				// Fix the lu if possible
				lexicalUnitFixes(lu);
				
				String invTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
				
				// TODO Might be an issue if we need that symbol set
				if(invTag != null)
				{
					ice.addTagWithLemma(invTag, lu.getLemma());
				}
			}
		}
		else
		{
			// If we still have no tags, then the word is in none of our dictionaries and
			// we need to blindly convert it (which might fail)
			for(LexicalUnit lu: ae.getPossibleLexicalUnits())
			{
				// Fix the lu if possible
				lexicalUnitFixes(lu);
				
				// TODO Check for null
				String tag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
				
				ice.addTagWithLemma(tag, lu.getLemma());
			}
		}
	}
	
	// Performs fixes to the lexical unit if needed
	// So it confirms to the icenlp tags
	private void lexicalUnitFixes(LexicalUnit lu)
	{
		// Changes <det><qnt> to <prn><qnt>, also removes <sta> if there is
		if(!lu.isVerb() && lu.getSymbols().contains("<det><qnt>"))
		{
			String symbols = lu.getSymbols();
			symbols = symbols.replace("<det><qnt>", "<prn><qnt>");
			
			// If we find a strong inflection, we remove it in this case
			if(symbols.endsWith("<sta>"))
			{
				symbols = symbols.replaceAll("<sta>", "");
			}
			
			lu.setSymbols(symbols);
		}
		
		// #TODO Temporary solution, until apertium is updated
		// If we have <sta> in a <pp> verb, we remove <sta>
		// <pp> verbs do not have a strong inflection.
		if(lu.isVerb() && lu.getSymbols().contains("<pp>") && lu.getSymbols().contains("<sta>"))
		{
			String newSymbols = lu.getSymbols();
			newSymbols = newSymbols.replace("<sta>", "");
			lu.setSymbols(newSymbols);
		}
	}

	// Handles conversion of verbs
	private void handleVerb(IceTokenTags ice, ApertiumEntry ae)
	{
		String baseTag = baseDict.lookup(ice.lexeme, true);
		
		// We find that verb in the base dictionary
		if(baseTag != null)
		{
			// Then we use those results and ignore the results from lt-proc
			String baseTagSplit[] = baseTag.split("_");
			
			// For each base tag
			for(String base: baseTagSplit)
			{
				String cleanbase = base;
				
				if(cleanbase.contains("<"))
				{
					// Get the base tag without the extra info <t> for example
					cleanbase = cleanbase.split("<")[0];
				}
				
				// We check each lu tag to see if that lu has the same tag (minus the extra info)
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					// Fix the lu if possible
					lexicalUnitFixes(lu);
					
					String invertedTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
					
					// We have the found the lexical unit that has the base tag
					if(invertedTag != null && invertedTag.equals(cleanbase))
					{
						// Add it to the tag list
						ice.addTagWithLemma(base, lu.getLemma());
						
						// Remove it from the possible list
						ae.removeLexicalUnit(lu);
						
						// Stop looping and try out the next base
						break;
					}
				}
			}
			
			// We have now added all the tags found in the base dict
			// It is possible there are still possible lexical units, but currently we don't care
			// We trust the base dict.
		}
		else
		{
			// If we still have no tags, then the word is in none of our dictionaries and
			// we need to blindly convert it (which might fail)
			for(LexicalUnit lu: ae.getPossibleLexicalUnits())
			{
				// Fix the lu if possible
				lexicalUnitFixes(lu);
				
				// TODO Check for null
				String tag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
				
				ice.addTagWithLemma(tag, lu.getLemma());
			}
		}
	}
	
	private void handlePreposition(IceTokenTags ice, ApertiumEntry ae)
	{
		String baseTag;
		
		// MWE prepositions only use the last word for the lookup
		if(ae.isMWE())
		{
			String[] mweSplit = ice.lexeme.split(" ");
			int length = mweSplit.length;
			
			baseTag = baseDict.lookup(mweSplit[length-1], true);
		}
		else
		{
			// Normal lookup
			baseTag = baseDict.lookup(ice.lexeme, true);
		}
		
		// We find that verb in the base dictionary
		if(baseTag != null)
		{
			// Then we use those results and ignore the results from lt-proc
			String baseTagSplit[] = baseTag.split("_");
			
			for(String tag: baseTagSplit)
			{
				ice.addTagWithLemma(tag, ice.lexeme);
			}
		}
		else
		{
			// TODO remove
			System.out.println("ERROR:"+ice.lexeme);
			System.exit(0);
		}
	}
	
	// Handles unknown words
	private void handleUnknown(IceTokenTags ice, int counter)
	{
		ice.setUnknown(true);
		
		// Check if it is a possible proper noun
		// First character is upper case in a word that is not the first one
		if(counter != 0 && Character.isUpperCase(ice.lexeme.charAt(0)))
		{
			ice.setUnknownType(IceTokenTags.UnknownType.properNoun);
		}
	}
}
