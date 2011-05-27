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
			
			// We might have an unknown word
			if(ae.getPossibleLexicalUnits().get(0).isUnknown())
			{
				ice.setUnknown(true);
				
				// Check if it is a possible proper noun
				// First character is upper case in a word that is not the first one
				if(counter != 0 && Character.isUpperCase(ae.getSurfaceForm().charAt(0)))
				{
					ice.setUnknownType(IceTokenTags.UnknownType.properNoun);
				}
			}
			else
			{
				// Add all the tags
				for(LexicalUnit lu: ae.getPossibleLexicalUnits())
				{
					String tag;
					
					// Check if it is a preposition or if it is a verb
					// Then we only use the results from the base dict
					// If the word is not in the baseDict, we skip this action
					if((lu.isPreposition() || lu.isVerb()) && baseDict.lookup(ice.lexeme, true) != null)
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
			
			list.add(ice);
			counter++;
		}
		
		return list;
	}
}
