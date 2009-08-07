/*
 * Copyright (C) 2009 Sverrir Sigmundarson
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

import java.util.ArrayList;

/**
 * Encapsulates a sentence as a list of tokens.
 * @author Sverrir Sigmundarson
 */
public class Sentence {  // The parameterized type should be of class Token or any subclass
    private ArrayList<Token> myTokens;

    public Sentence()
    {
        myTokens = new ArrayList<Token>();
	}

    public Sentence(ArrayList<Token> tokens)
	{
        myTokens = new ArrayList<Token>();
        for( Token tok : tokens )
            myTokens.add(tok);
    }

    public ArrayList<Token> getTokens()
	{
		return myTokens;
	}

    public void add (Token tok)
    {
        myTokens.add(tok);
    }

    public String toString()
	{
		StringBuilder b = new StringBuilder( );

		for( Token token : myTokens )
		{
            String str = token.toString();
            b.append( str );
			b.append( " " );
        }

		return b.toString();
	}

    // New line between each segment
    public String toStringNewline(boolean markUnknown)
	{
		StringBuilder b = new StringBuilder( );

		for( Token token : myTokens )
		{
            String str = token.toString();
            b.append( str );
            if (markUnknown && token.isUnknown())
               b.append(" *");  // Means unknown word
            b.append("\n");
		}

		return b.toString();
	}
}
