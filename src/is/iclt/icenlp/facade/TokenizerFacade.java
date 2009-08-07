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
package is.iclt.icenlp.facade;

import is.iclt.icenlp.core.tokenizer.Tokenizer;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.utils.Lexicon;

import java.io.IOException;

/**
 * Provides a simplified interface to Tokenizer.
 * @author Hrafn Loftsson
 */
public class TokenizerFacade {
    private Tokenizer tokenizer;
    private Segmentizer segmentizer;
    private boolean strictTokenization = true;

    public TokenizerFacade(Lexicon tokenizerLexicon, int lineFormat) throws IOException
    {
        segmentizer = new Segmentizer(tokenizerLexicon, lineFormat);
        tokenizer = new Tokenizer( Tokenizer.typeToken,
                                        strictTokenization,
                                        tokenizerLexicon);
        tokenizer.findMultiWords( false );
    }

public Sentences tokenize( String text ) throws IOException
    {
        Sentence sent=null;
        segmentizer.segmentize( text );

        Sentences sents = new Sentences();

        while( segmentizer.hasMoreSentences() )
        {
            String sentenceStr = segmentizer.getNextSentence();

            if( !sentenceStr.equals( "" ) )
            {
                tokenizer.tokenize(sentenceStr);
                if( tokenizer.tokens.size() <= 0 )
                    continue;

                tokenizer.splitAbbreviations();
                sent = new Sentence(tokenizer.tokens);
            }
            sents.add(sent);
            //if (insertNewline)
            //    segments.add(newLineSegment);
        }

        return sents;
    }

}
