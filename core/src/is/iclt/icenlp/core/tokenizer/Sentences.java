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
 * Encapsulates sentences as a list of Sentence objects.
 * @author Sverrir Sigmundarson
 */
public class Sentences
{
    private ArrayList<Sentence> mySentences;

    public Sentences()
    {
        mySentences = new ArrayList<Sentence>();
	}

    public ArrayList<Sentence> getSentences()
	{
		return mySentences;
	}

    public void add (Sentence sent)
    {
        mySentences.add(sent);
    }

    public String toStringNewline(boolean markUnknown)
	{
		StringBuilder b = new StringBuilder( );

		for( Sentence sent : mySentences )
		{
            String str = sent.toStringNewline(markUnknown);
            b.append( str );
			b.append( "\n" );
        }
		return b.toString();
	}

    public String toString()
	{
		StringBuilder b = new StringBuilder( );

		for( Sentence sent : mySentences )
		{
            String str = sent.toString();
            b.append( str );
			b.append( "\n" );
        }
		return b.toString();
	}
    
    public int size()
    {
    	return mySentences.size();
    }
}
