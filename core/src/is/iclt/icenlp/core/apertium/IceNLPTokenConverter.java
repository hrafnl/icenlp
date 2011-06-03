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
	
	public IceNLPTokenConverter(ArrayList<ApertiumEntry> entries, MappingLexicon mapping) throws IOException
	{
		this.entries = entries;
		this.mapping = mapping;
		
		IceTaggerResources resource = new IceTaggerResources();
		this.baseDict = new Lexicon(resource.isDictionaryBase);
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
			
			// If we have a verb in any of the lu's
			if(ae.isAnyLuAVerb())
			{
				handleVerb(ice, ae);
			}
			else if(ae.getPossibleLexicalUnits().get(0).isUnknown())
			{
				// We might have an unknown word
				handleUnknown(ice, counter);
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
	
	// Handles normal converting
	private void handleNormal(IceTokenTags ice, ApertiumEntry ae)
	{
		// Add all the tags
		for(LexicalUnit lu: ae.getPossibleLexicalUnits())
		{
			String tag;
			
			// Check if it is a preposition or if it is a verb
			// Then we only use the results from the base dict
			// If the word is not in the baseDict, we skip this action
			if(lu.isPreposition() && baseDict.lookup(ice.lexeme, true) != null)
			{
				tag = baseDict.lookup(ice.lexeme, true);
				
				// Most likely this tag has many results split by _
				if(tag.contains("_"))
				{
					String[] split = tag.split("_");
					
					// Add each result as a separate tag
					for(String s: split)
					{
						ice.addTagWithLemma(s, lu.getLemma());
					}
				}
				else
				{
					// No underscore, that means it's only 1 tag
					ice.addTagWithLemma(tag, lu.getLemma());
				}
			}
			else
			{
				// If we find <det><qnt> we convert it to <prn><qnt>
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
				}
				
				tag = mapping.getInvertedTagMap(lu.getSymbols(), lu.getLemma());
				
				ice.addTagWithLemma(tag, lu.getLemma());
			}
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
					// #TODO Temporary solution, until apertium is updated
					// If we have <sta> in a <pp> verb, we remove <sta>
					// <pp> verbs do not have a strong inflection.
					if(lu.getSymbols().contains("<pp>") && lu.getSymbols().contains("<sta>"))
					{
						String newSymbols = lu.getSymbols();
						newSymbols = newSymbols.replace("<sta>", "");
						lu.setSymbols(newSymbols);
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
