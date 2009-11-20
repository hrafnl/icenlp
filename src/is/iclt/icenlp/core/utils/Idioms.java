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
package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.tokenizer.TokenTags;
import is.iclt.icenlp.core.tokenizer.Token.MWECode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A class for storing and processing idioms.
 * @author Hrafn Loftsson
 */
public class Idioms extends Lexicon
{
	private TokenTags dummyToken;

	public Idioms( String filename ) throws IOException
	{
        super(filename);
		dummyToken = new TokenTags();
	}

    public Idioms( InputStream in ) throws IOException, NullPointerException {
       super(in);
       dummyToken = new TokenTags(); 
    }

	/**
	 * Finds idioms in the supplied vector. Replaces the tags of the idioms tokens with the one found in the idioms lexicon.
	 * @param tokens An array of tokens
	 */
	public void findIdioms( ArrayList tokens )
	{
		TokenTags first, second, third;
		TokenTags tok;
		String multiWord2=null, multiWord3=null, tags;

        int count = tokens.size();

		boolean secondFound = false;
		boolean thirdFound = false;

		for( int i = 0; i < count; i++ )
		{
			tags = null;
			first = (TokenTags)tokens.get( i );
			multiWord2 = first.lexeme;
			if( i < count - 1 )
			{
				second = (TokenTags)tokens.get( i + 1 );
				secondFound = true;
				multiWord2 = multiWord2 + "_" + second.lexeme;
			}
			else
				secondFound = false;
			if( i < count - 2 )
			{
				third = (TokenTags)tokens.get( i + 2 );
				thirdFound = true;
				multiWord3 = multiWord2 + "_" + third.lexeme;
			}
			else
				thirdFound = false;

			if( thirdFound )
			{
				tags = lookup( multiWord3, true );
			}
			if( tags == null && secondFound )
			{
				tags = lookup( multiWord2, true );
			}

			if( tags != null )
			{
				dummyToken.lexeme = tags;
				String tagStrings[] = dummyToken.splitLexeme( "_" );
				for( int k = 0; k < tagStrings.length; k++ )
				{
					if( k + i < tokens.size() )
					{
						tok = (TokenTags)tokens.get( k + i );
						if(k == 0)
						{
							//System.out.println("First: " + tok.lexeme);
							tok.mweCode = MWECode.begins;
						}
						if(k+1 == tagStrings.length)
						{
							//System.out.println("last: " + tok.lexeme);
							tok.mweCode = MWECode.ends;
						}
						tok.setTag( tagStrings[k] );
					}
				}
			}
		}
	}

}
