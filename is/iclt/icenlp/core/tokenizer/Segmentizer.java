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
package is.iclt.icenlp.core.tokenizer;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.FileEncoding;

import java.io.*;

/**
 * Segmentizes text into individual sentences.
 * @author Hrafn Loftsson
 */
public class Segmentizer
{
	private BufferedReader input = null;      // Input file
	private BufferedWriter output = null;      // Output file used if tags are removed from input
	private Lexicon lex = null;			// a lexicon consisting of known abbreviations
	private String currLine = null;           // Current line in input
	public static final int tokenPerLine = 1;
	public static final int sentencePerLine = 2;
	public static final int otherDifferentFormat = 3;
    private int lineFormat;

	private int lastIndex = 0;
	private int currIndex = 0;

    public static String interpretLineFormat(int format)
    {
        switch (format) {
            case tokenPerLine:      return "one token per line";
            case sentencePerLine:   return "one sentence per line";
            default:                return "unspecified format";
        }
    }

	public Segmentizer( String inputFileName, int lineForm, String lexiconFile )
			throws IOException
	{
		lex = new Lexicon();
		lex.load( lexiconFile);
		lineFormat = lineForm;

	    openFile( inputFileName );
	}

    public Segmentizer( String inputFileName, int lineForm, InputStream lexiconFile )
			throws IOException
	{
		lex = new Lexicon(lexiconFile);
		lineFormat = lineForm;

		openFile( inputFileName );

	}

    public Segmentizer( String inputFileName, int lineForm, Lexicon lexicon )
			throws IOException
	{
		lex = lexicon;
		lineFormat = lineForm;

		openFile( inputFileName );

	}

    public Segmentizer( BufferedReader reader, int lineForm, String lexiconFile )
			throws IOException
	{
		lex = new Lexicon();
		lex.load( lexiconFile);
		lineFormat = lineForm;

        input = reader;
        currLine = input.readLine();

	}


    public Segmentizer( BufferedReader reader, int lineForm, InputStream lexiconFile )
			throws IOException
	{
		lex = new Lexicon(lexiconFile);
		lineFormat = lineForm;

        input = reader;
        currLine = input.readLine();

	}

    public Segmentizer( BufferedReader reader, int lineForm, Lexicon lexicon )
			throws IOException
	{
		lex = lexicon;
		lineFormat = lineForm;

        input = reader;
        currLine = input.readLine();

	}

    public Segmentizer( String str, String lexiconFile ) throws IOException
	{
		lex = new Lexicon();
		lex.load( lexiconFile);
		lineFormat = otherDifferentFormat;
		segmentize( str );
	}

	public Segmentizer( InputStream in ) throws IOException, NullPointerException
	{
		if( in == null )
			throw new NullPointerException( "InputStream was not initialized correctly (null)" );
		
		lex = new Lexicon( in );
		lineFormat = otherDifferentFormat;
	}

	public Segmentizer( Lexicon lexicon ) throws NullPointerException
	{
		if( lexicon == null )
			throw new NullPointerException( "Lexicon is null");

		lex = lexicon;
		lineFormat = otherDifferentFormat;
	}

    public Segmentizer( Lexicon lexicon, int lineform ) throws NullPointerException
	{
		if( lexicon == null )
			throw new NullPointerException( "Lexicon is null");

		lex = lexicon;
		lineFormat = lineform;
	}


    public void segmentize( String str ) throws IOException
	{
		input = new BufferedReader( new StringReader( str ) );
		// Read one line
		currLine = input.readLine();
	}

	private void openFile( String fileName )
			throws IOException
	{
        input = FileEncoding.getReader(fileName);
        // Read one line
		currLine = input.readLine();
	}


	public boolean hasMoreSentences()
	{
		return (currLine != null);
	}

	private boolean isAbbreviation( int endIndex )
	{
		String abbrev = null;
		int idx = endIndex;
		boolean startOfAbbrevFound = false;

		while( !startOfAbbrevFound )
		{
			// Search for the start of the abbreviation
			char ch = currLine.charAt( idx );
			if( ch == ' ' ||
			    ch == '(' ||
			    ch == '[' || idx == 0 )
				startOfAbbrevFound = true;
			else
				idx--;
		}
		abbrev = currLine.substring( idx + 1, endIndex + 1 );
		return (lex.lookup( abbrev, true ) != null);
	}

	private boolean isPeriodEOS()         // Checks if the period is really marking end of sentence
	{
		if( isAbbreviation( currIndex - 1 ) )  // The period could have been a part of an abbreviation
			return false;
		else
		{
			if( currIndex + 1 >= currLine.length() )
				return true;
			else
			{
				// For example "..."
				char charCurrent = currLine.charAt( currIndex );
				if( charCurrent == '.' )
					return false;

				else                 // What about substring like "72. mínútu"
				{
					if( currIndex > 1 )
					{
						char charBeforePeriod = currLine.charAt( currIndex - 2 );
						char charAfterPeriodAndSpace = currLine.charAt( currIndex + 1 );
						if( Character.isDigit( charBeforePeriod ) && (Character.isLowerCase( charAfterPeriodAndSpace )) )
							return false;
						else
							return true;
					}
					else return true;
				}
			}
		}
	}

	private boolean isFullStop( char ch, boolean isLastChar )
	{
		char nextChar;
		boolean endOfSentence = false;

		if( isLastChar )  // If last character of the line or
			endOfSentence = true;
        else
		{
			nextChar = currLine.charAt( currIndex );
			if( nextChar == ' ' )    // If a space after the full stops
				endOfSentence = true;
            // fífunni.1 eða salati.»
            else if ((ch == '.' || ch == '!' || ch == '?' || ch == ':' || ch == '»') && Character.isDigit(nextChar))
                endOfSentence = true;


            // This else added 18.07.2007 because of Mogga corpus
            /*else if ( (ch == '"' || ch == '«' || ch == '»'))
            {
                if (currIndex > 0) {
                    char prevCh =  currLine.charAt(currIndex-1);
                    if (prevCh == '.' || prevCh == '?' || prevCh == '!')  
                        endOfSentence = true;
                }
            } */

            if( ch == '.' && !isPeriodEOS() )        // Is the period really marking EOS
				endOfSentence = false;
            // Check for salati.»
			else if( (ch == '.' || ch == '!' || ch == '?' || ch == ':') && (nextChar == '"' || nextChar == '«' || ch == '»') ) // || ch == '»'))
				endOfSentence = false;
				// Check for like: "Elsku mamma," jörmuðu kiðlingarnir ....
			else if( (ch == '"' || ch == '»') && currIndex > 1) {
                char prevCh = currLine.charAt( currIndex - 2 );
                if (prevCh != '.' && prevCh != '!' && prevCh != '?' && prevCh != ':')
				    endOfSentence = false;
            }
		}

		return endOfSentence;
	}

	public String getNextSentence()
			throws IOException
	{
		String sentence;
		switch( lineFormat )
		{
			case tokenPerLine:
				sentence = nextSentenceBrill();
				break;
			case sentencePerLine:
				sentence = nextSentenceLine();
				break;
			case otherDifferentFormat:
				sentence = nextSentence();
				break;
			default:
				sentence = nextSentenceLine();
				break;
		}
		return sentence;
	}

	// Assumes the sentences occupies one line
	private String nextSentenceLine()
			throws IOException
	{
		String sentence = null;
		if( currLine != null )
		{
			sentence = currLine;
			currLine = input.readLine();      // Read next line
            // Remove trailing spaces
            if (currLine != null)
                currLine = currLine.replaceAll("\\s+$", "");
    	}
		else // End of file
			input.close();

		return sentence;
	}

	// Assumes the sentence can occupie more than one line and thus does sentence segmentation
	private String nextSentence()
			throws IOException
	{
		String sentence = "";
		boolean endOfSentence = false;
		char ch;

		while( !endOfSentence )
		{
			boolean saveSentence = false;
			boolean isLastChar = false;      // Last character of the line?
			if( currLine != null )            // Not end of file
			{
				if( currLine.length() != 0 )   // Not an empty line
				{
					ch = currLine.charAt( currIndex );
					currIndex++;

					if( currIndex == currLine.length() )
					{
						isLastChar = true;
						//if( ch != ' ' && ch != '\t' && sentence != "" )
						//	sentence = sentence + " ";   // Add a space between sentences that comprise more that one line
						saveSentence = true;
					}

					if( ch == '.' || ch == '!' || ch == '?' || ch == ':'
					    || ch == '"' || ch == '«' || ch == '»')    // Full stops
					{
						if( isFullStop( ch, isLastChar ) )
						{
							saveSentence = true;
							endOfSentence = true;
						}
					}

					if( saveSentence ) {
						sentence = sentence + currLine.substring( lastIndex, currIndex );     // Save the current sentence
                         if( ch != ' ' && ch != '\t' && !endOfSentence)
							sentence = sentence + " ";   // Add a space between sentences th
                    }
				}
				else                         // An empty line
					endOfSentence = true;

				if( currIndex == currLine.length() || (currLine.length() == 0) )     // Then end of line
				{
					currLine = input.readLine();      // Read next line
					currIndex = 0;
					lastIndex = 0;
				}
			}
			else // End of file
			{
				endOfSentence = true;
				input.close();
			}
		}

		lastIndex = currIndex;
		return sentence;
	}

// The sentence occupies more than one line but only one token per line.

	// An empty line is between sentences.  Brill format.
	private String nextSentenceBrill()
			throws IOException
	{
		String sentence = "";
		boolean endOfSentence = false;
		boolean firstTok = true;

		while( !endOfSentence )
		{
			if( currLine != null )            // Not end of file
			{
				if( currLine.length() != 0 )   // Not an empty line
				{
					if( !firstTok )
						sentence += " ";
					sentence = sentence + currLine.substring( 0, currLine.length() );  // Save the current sentence
					currLine = input.readLine();      // Read next line
					firstTok = false;
				}
				else                         // An empty line
				{
					endOfSentence = true;
					currLine = input.readLine();      // Read next line
					firstTok = true;
				}

			}
			else // End of file
			{
				endOfSentence = true;
				input.close();
			}
		}

		return sentence;
	}

}

