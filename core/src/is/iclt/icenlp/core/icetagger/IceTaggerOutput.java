/*
 * Copyright (C) 2009 Hrafn Loftsson
 *
 * This file is part of the IceNLP toolkit.
 * IceNLP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IceNLP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with IceNLP. If not,  see <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 * Hrafn Loftsson, School of Computer Science, Reykjavik University.
 * hrafn@ru.is
 */
package is.iclt.icenlp.core.icetagger;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.lemmald.LemmaResult;

import java.io.IOException;

/**
 * Generates the output for IceTagger.
 * @author Hrafn Loftsson
 */
public class IceTaggerOutput {

	protected int outputFormat = Segmentizer.tokenPerLine;	// Default is one word/tag per line
	protected boolean fullOutput = false;
	protected boolean fullDisambiguation = true;
	//protected boolean lemmatize = false;
	protected String separator;	  // Separator between a word and its tag
	protected Lexicon myTagMap=null;		// Maps IFD tags to some other tag set
	protected Lemmald myLemmald=null;
	private final String unknownStr = "<UNKNOWN>";
	private final String guessedStr = "<GUESSED>";
	private final String morphoStr = "<MORPHO>";
	private final String endingStr = "<ENDING>";
	private final String compoundStr = "<COMPOUND>";
	protected final String tagNotFoundInMap = "<NOT MAPPED>";

	public IceTaggerOutput()
	{
	   // Do not use this one!
	}

	public IceTaggerOutput(int outFormat, String wordTagSeparator, boolean useFullOutput, boolean useFullDisambiguation, String tagMapFile, boolean showLemma) throws IOException
	{
		outputFormat = outFormat;
		separator = wordTagSeparator;
		fullOutput = useFullOutput;
		fullDisambiguation = useFullDisambiguation;
		//lemmatize = showLemma;
		// A tap map file if needed
		if (tagMapFile != null)
			myTagMap = new Lexicon(tagMapFile);
		// A lemmatizer if needed
		if (showLemma) {
		   //myLemmald = new Lemmald(lemmatizerFile);
		   myLemmald = Lemmald.getInstance();
		}
	}

	protected String getMappedTag(String tag)
	{
		String mappedTag = myTagMap.lookup(tag, false);
		if (mappedTag == null)
			mappedTag =  tagNotFoundInMap + ":" + tag;
		return mappedTag;

	}



	public String buildOutput( IceTokenTags tok, int index, int numTokens )
	{
		String str, tag, mappedTag;

		if( outputFormat == Segmentizer.tokenPerLine )
		{
			if( fullDisambiguation ) {
				tag = tok.getFirstTagStr();

				if (myTagMap != null) {
					mappedTag = getMappedTag(tag);
					str = tok.lexeme + " " + mappedTag;
				}
				else
					str = tok.lexeme + " " + tag;

				// Add the lemma?
				if (myLemmald != null) {
					LemmaResult lemmaResult = myLemmald.lemmatize(tok.lexeme,tag);
					String lemma = lemmaResult.getLemma();
				   //lemma = myLemmald.getLemma(tok.lexeme, tag);
				   str = str + " " + lemma;
				}
			}
			else
				str = tok.lexeme + " " + tok.allTagStrings();

			if( fullOutput && ( tok.getSVOMark() != IceTokenTags.SVOMark.svoNone ) ) {
				str = str + " <" + tok.getSVOMarkString() + "> ";
			}
		}
		else
		{
			str = tok.lexeme + separator + tok.getFirstTagStr();
			if( index < numTokens - 1 )
				str = str + " ";
		}

		if( tok.isUnknown() )
		{
			if( outputFormat == Segmentizer.tokenPerLine )
			{
				str = str + " " + unknownStr;
				if( fullOutput )
				{
					if( tok.isUnknownMorpho() )
						str = str + " " + morphoStr;
					// Nota StringBuilder
					else if( tok.isUnknownEnding() )
						str = str + " " + endingStr;
					else if( tok.isUnknownGuessed() )
						str = str + " " + guessedStr;
					/*else
						System.out.println( "MISSING UNKNOWN TYPE: " + tok.lexeme + " " + tok.allTagStrings() );*/

					if( tok.isCompound() )
						str = str + " " + compoundStr;
				}
			}
		}
		return str;
	}


	public String buildOutputBaseTagging( IceTokenTags tok)
	{
		String tag, str, mappedTag;

		tag = tok.getFirstTagStr();
		if (myTagMap != null) {
				   mappedTag = getMappedTag(tag);
				   str = tok.lexeme + " " + mappedTag;
		}
		else
			str = tok.lexeme + " " + tag;

		if (myLemmald != null) {
			 LemmaResult lemmaResult = myLemmald.lemmatize(tok.lexeme,tag);
			 String lemma = lemmaResult.getLemma();
			 //lemma = myLemmald.getLemma(tok.lexeme, tag);
			 str = str + " " + lemma;
		}

		if( tok.isUnknown() && outputFormat == Segmentizer.tokenPerLine )
				str = str + " " + unknownStr;
		return str;
	}
	
	public String lemmatizeWord (String wordform, String tags)
	{
		//myLemmald.getLemma(tok.lexeme, tag);
		//myLemmald.getLemma("borðar", "sfg3en");
		return "";
	}
}
