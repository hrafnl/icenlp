package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.utils.IceTag;
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
	
	private boolean linkedToPreviousWord = false;
	private String preSpace = null;
	
	public IceNLPTokenConverter(ArrayList<ApertiumEntry> entries, MappingLexicon mapping, IceTaggerLexicons iceLex) throws IOException
	{
		this.entries = entries;
		this.mapping = mapping;
		
		this.baseDict = iceLex.morphyLexicons.baseDict;
		this.otbDict = iceLex.morphyLexicons.dict;
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
		int counter = 0; // Used to find proper nouns and other checks
		
		// Goes through all the lexical units in each apertium entry and cleans it up
		// This is done here so it's not needed to be done within the handlers
		cleanLexicalUnits(entries);
		
		for(ApertiumEntry ae: entries)
		{
			ArrayList<LexicalUnit> possibleLu = ae.getPossibleLexicalUnits();
			
			// Add the lexeme
			IceTokenTags ice = new IceTokenTags();
			ice.lexeme = ae.getSurfaceForm();
			
			// If we have any inv mw mark
			// Then we add it to the IceTokenTag
			if(ae.isFirstInvMWMark())
			{
				ice.setInvMWMark(possibleLu.get(0).getInvMWMarker());
			}
			
			// We first check for unknown words, then we check for spaces, then we can process normally.
			if(possibleLu.get(0).isUnknown() && !possibleLu.get(0).isSpace())
			{
				// We might have an unknown word
				handleUnknown(ice, counter);
			}
			// lt-proc treats ()' as spaces, but IceNLP does not
			else if(possibleLu.get(0).isSpace())
			{
				// "Space" characters are not tags, we add them to the next tag.
				preSpace = ice.lexeme;
				continue;
			}
			else if(ae.isMWE())
			{
				// Multi word expressions are handled in a standard way.
				standardConvert(ice, ae);
			}
			else if(ae.isAnyLuPreposition())
			{
				handlePreposition(ice, ae);
			}
			else if(ae.isAnyLuAVerb())
			{
				// If we have a verb in any of the lu's
				handleVerb(ice, ae);
			}
			else if(ae.isSeperator())
			{
				// Seperators like ,.; etc
				handleSeperators(ice, ae);
			}
			else
			{
				// Normal processing
				handleNormal(ice, ae);
			}
			
			if(preSpace != null)
			{
				ice.preSpace = preSpace;
				preSpace = null;
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
			ArrayList<LexicalUnit> possibleLu = ae.getPossibleLexicalUnits();
			
			for(LexicalUnit lu: possibleLu)
			{
				if(lu.isProperNoun())
				{
					// If it is a <np><org>, we change the org to a <np><al>
					// We do not do this with <mf><sp>, there is a special rule for that in another place.
					if(lu.getSymbols().contains("<org>") && !lu.getSymbols().contains("<mf><sp>"))
					{
						String symbols = lu.getSymbols();
						
						lu.setSymbols(symbols.replace("<org>", "<al>"));
						
						continue;
					}
					
					// If it is a <np><cog>, we change the cog to a <al>
					if(lu.getSymbols().contains("<cog>"))
					{
						String symbols = lu.getSymbols();
						
						lu.setSymbols(symbols.replace("<cog>", "<al>"));
						
						continue;
					}
					
					continue;
				}
				
				if(lu.isDet())
				{					
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
						
						continue;
					}
					
					continue;
				}
				
				// #TODO Temporary solution, until apertium is updated
				// If we have <sta> in a <pp> verb, we remove <sta>
				// If we have <vei> in a <pp> verb, we remove <vei>
				// <pp> verbs do not have strong or weak inflections.
				if(lu.isVerb() && lu.getSymbols().contains("<pp>"))
				{
					if(lu.getSymbols().contains("<sta>"))
					{
						String newSymbols = lu.getSymbols();
						
						lu.setSymbols(newSymbols.replace("<sta>", ""));
					}
					else if(lu.getSymbols().contains("<vei>"))
					{
						String newSymbols = lu.getSymbols();
						
						lu.setSymbols(newSymbols.replace("<vei>", ""));
					}
					
					continue;
				}
			}
		}
	}
	
	// Processes the apertium entries based on the tag and adds them in the correct order to the IceTokenTag
	private void processMapping(String tag, String cleanTag, IceTokenTags ice, ApertiumEntry ae)
	{
		ArrayList<LexicalUnit> possibleLu = ae.getPossibleLexicalUnits();
		
		// Look at each apertium entries tag to see if it matches with the tag
		// Insert that tag first into tag list
		for(LexicalUnit lu: possibleLu)
		{
			if(lu.isIgnore())
			{
				continue;
			}
			
			String invTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
			
			// If it is a multi tag set, which does not happen often
			// but needs to be handled differently
			if(invTag != null && invTag.contains("_"))
			{
				// Then we split it
				String[] multiTag = invTag.split("_");
				
				boolean tagFound = false;
				
				// Then we go through
				for(String mTag: multiTag)
				{
					// If we find the tag within the multi tag
					// Then we add them all
					if(mTag.equals(cleanTag))
					{
						tagFound = true;
						
						// We found a tag that matches, then we add them all in order
						for(String addTag: multiTag)
						{
							ice.addTagWithLemma(addTag, lu.getLemma());
						}
						
						if(lu.isLinkedToPreviousWord())
						{
							ice.linkedToPreviousWord = true;
						}
						
						// Remove the lu since we found it.
						ae.removeLexicalUnit(lu);
						
						break;
					}
				}
				
				if(tagFound)
				{
					break;
				}
			}
			else if(invTag != null && invTag.equals(cleanTag))
			{
				// We found the correct lu
				ice.addTagWithLemma(tag, lu.getLemma());
				
				if(lu.isLinkedToPreviousWord())
				{
					ice.linkedToPreviousWord = true;
				}
				
				// Remove the lu since we found it.
				ae.removeLexicalUnit(lu);
				
				break;
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
				processMapping(tag, tag, ice, ae);
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
				
				processMapping(base, cleanbase, ice, ae);
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
				// We have a preposition, they use the lexeme as a lemma
				if(tag.equals("aþm") || tag.equals("aþe") || tag.equals("ao") || tag.equals("aþ") || tag.equals("ae"))
				{
					ice.addTagWithLemma(tag, ice.lexeme);					
				}
				else
				{
					// Then we need to search for the lemma within apertium entry list.
					//
					// There might be tags from the base dict that are lost here because they
					// are not defined in the is.dix
					for(LexicalUnit lu: ae.getPossibleLexicalUnits())
					{
						String invTag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
						
						if(tag.equals(invTag))
						{
							ice.addTagWithLemma(tag, lu.getLemma());
						}
					}
				}
			}
		}
		else
		{
			// We have not found the preposition and mark it as unknown
			// for the tagger to guess
			handleUnknown(ice, 0);
		}
	}
	
	// Seperators have themselves as tags and are linked to the previous word
	private void handleSeperators(IceTokenTags ice, ApertiumEntry ae)
	{
		ice.linkedToPreviousWord = true;
		ice.addTagWithLemma(ice.lexeme, ice.lexeme);
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
		ArrayList<LexicalUnit> possibleLu = ae.getPossibleLexicalUnits();
		
		for(LexicalUnit lu: possibleLu)
		{
			if(lu.isIgnore())
			{
				continue;
			}
			
			String tag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
			
			if(tag != null)
			{
				ice.addTagWithLemma(tag, lu.getLemma());
				
				if(lu.isLinkedToPreviousWord())
				{
					ice.linkedToPreviousWord = true;
				}
			}
		}
	}
	
	// Cleans back changes that the IceTagger makes that we do not want, but only applies when using apertium
    public void changeReflexivePronounTags(ArrayList<IceTokenTags> tokens)
	{
    	for(IceTokenTags itt: tokens)
    	{
			if( itt.isPersonalPronoun() &&
				    ( itt.lexeme.equalsIgnoreCase( "sig" ) || itt.lexeme.equalsIgnoreCase( "sér" ) ||
				      itt.lexeme.equalsIgnoreCase( "sín" ) ) )
			{
				ArrayList tags = itt.getTags();
				for( int j = 0; j < tags.size(); j++ )
				{
					IceTag tag = (IceTag)tags.get( j );
					if( tag.getTagStr().substring( 0, 2 ).equals( "fp" ) )
						tag.setTagStr( "fb" + tag.getTagStr().substring( 2, 5 ) );
				}
			}
    	}
	}
}
