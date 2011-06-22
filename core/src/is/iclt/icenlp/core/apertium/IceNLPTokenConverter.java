package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.MappingLexicon;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
	
	// Returns the tags from the dictionaries
	private String dictLookup(String lexeme, boolean useOtbDict)
	{
		String tags = baseDict.lookup(lexeme, true);
		
		// If the tag is not in the base dict, we will use the otb dict.
		if(useOtbDict && tags == null)
		{
			tags = otbDict.lookup(lexeme, true);
		}
		
		return tags;
	}
	
	/**
	 * Converts the apertium entries to a IceNLP token
	 * @return An Array of IceTokenTags
	 */
	public ArrayList<IceTokenTags> convert()
	{
		ArrayList<IceTokenTags> list = new ArrayList<IceTokenTags>();
		int counter = 0; // Used to find proper nouns.
		
		// Goes through all the lexical units in each apertium entry and cleans it up
		// This is done here so it's not needed to be done within the handlers
		cleanLexicalUnits(entries);
		
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
				// Multi word expressions are handled in a standard way.
				standardConvert(ice, ae);
			}
			else if(ae.isAnyLuAVerb())
			{
				// If we have a verb in any of the lu's
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
				
				// If it is empty, then it was 1 or more spaces.
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
	
	// Cleans up the lexical units.
	private void cleanLexicalUnits(ArrayList<ApertiumEntry> entries)
	{
		for(ApertiumEntry ae: entries)
		{
			for(LexicalUnit lu: ae.getPossibleLexicalUnits())
			{
				if(lu.isDet())
				{
					// If it is a <det><ind> then we mark it to be ignored
					if(lu.getSymbols().contains("<det><ind>"))
					{
						lu.setIgnore(true);
						
						break;
					}
					
					// Changes <det><qnt> to <prn><qnt>, also removes <sta> if there is
					if(lu.getSymbols().contains("<det><qnt>"))
					{
						String symbols = lu.getSymbols();
						symbols = symbols.replace("<det><qnt>", "<prn><qnt>");
						
						// If we find a strong inflection, we remove it in this case
						if(symbols.endsWith("<sta>"))
						{
							symbols = symbols.replaceAll("<sta>", "");
						}
						
						lu.setSymbols(symbols);
						
						break;
					}
				}
				
				// #TODO Temporary solution, until apertium is updated
				// If we have <sta> in a <pp> verb, we remove <sta>
				// If we have <vei> in a <pp> verb, we remove <vei>
				// <pp> verbs do not have a strong inflection.
				if(lu.isVerb() && lu.getSymbols().contains("<pp>"))
				{
					if(lu.getSymbols().contains("<sta>"))
					{
						String newSymbols = lu.getSymbols();
						newSymbols = newSymbols.replace("<sta>", "");
						lu.setSymbols(newSymbols);
					}
					else if(lu.getSymbols().contains("<vei>"))
					{
						String newSymbols = lu.getSymbols();
						newSymbols = newSymbols.replace("<vei>", "");
						lu.setSymbols(newSymbols);
					}
					
					break;
				}
			}
		}
	}

	private void handleNormal(IceTokenTags ice, ApertiumEntry ae)
	{
		// Get the tags from the dict, using otb as well
		String tags = dictLookup(ice.lexeme, true);
		
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
					if(lu.isIgnore())
					{
						continue;
					}
					
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
		}

		// If we still have no tags, or no tags found, and the word is in none of our dictionaries
		// we need to blindly convert it (which might fail)
		standardConvert(ice, ae);
	}

	// Handles conversion of verbs
	private void handleVerb(IceTokenTags ice, ApertiumEntry ae)
	{
		// Get the tags from the dict, not using otb
		String baseTag = dictLookup(ice.lexeme, false);
		
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
					if(lu.isIgnore())
					{
						continue;
					}
					
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
			standardConvert(ice, ae);
		}
	}
	
	private void handlePreposition(IceTokenTags ice, ApertiumEntry ae)
	{
		String baseTag;
		
		// MWE prepositions only use the last word for the lookup
		if(ae.isMWE())
		{
			String[] mweSplit = ice.lexeme.split(" ");
			
			baseTag = dictLookup(mweSplit[mweSplit.length-1], false);
		}
		else
		{
			// Normal lookup
			baseTag = dictLookup(ice.lexeme, false);
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
			// We have not found the preposition and mark it as unknown
			// for the tagger to guess
			handleUnknown(ice, 0);
		}
	}
	
	// Handles unknown words
	private void handleUnknown(IceTokenTags ice, int counter)
	{
		// This means IceNLP needs to guess it's tags
		ice.setUnknown(true);
		
		// This means we remember that it was an unknown from this external source
		// Since programs later in the pipeline need to know that.
		ice.setUnknownExternal(true);
		
		// Check if it is a possible proper noun
		// First character is upper case in a word that is not the first one
		if(counter != 0 && Character.isUpperCase(ice.lexeme.charAt(0)))
		{
			ice.setUnknownType(IceTokenTags.UnknownType.properNoun);
		}
	}
	
	// Standard Apertium to IceNLP conversion with no grammar logic
	// "Blind" conversion
	// Converts the lexical units of the apertium entry to ice token tags
	private void standardConvert(IceTokenTags ice, ApertiumEntry ae)
	{
		// If we still have no tags, then the word is in none of our dictionaries and
		// we need to blindly convert it (which might fail)
		for(LexicalUnit lu: ae.getPossibleLexicalUnits())
		{
			if(lu.isIgnore())
			{
				continue;
			}
			
			String tag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
			
			if(tag != null)
			{
				ice.addTagWithLemma(tag, lu.getLemma());
			}
		}
	}
}
