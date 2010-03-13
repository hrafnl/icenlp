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

import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.tritagger.TriTagger;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.tokenizer.*;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hrafn
 * Date: 19.11.2009
 * Time: 20:26:20
 * To change this template use File | Settings | File Templates.
 */
public class TriTaggerFacade {
    private TriTagger tagger;
    private static int ngram=3;
    private Tokenizer tokenizer;
    private Segmentizer segmentizer;
    private int sentenceStart = TriTagger.sentenceStartUpperCase;
    // Tokenizer variables
    private boolean strictTokenization = true;

    public TriTaggerFacade() throws IOException
    {
        this(new TriTaggerLexicons(new TriTaggerResources(), true), new Lexicon(new TokenizerResources().isLexicon));

    }

    public TriTaggerFacade(TriTaggerLexicons triLexicons, Lexicon tokenizerLexicon) throws IOException
    {
        segmentizer = new Segmentizer(tokenizerLexicon);
        this.tokenizer = new Tokenizer( Tokenizer.typeIceTokenTags,
                                        strictTokenization,
                                        tokenizerLexicon);
        this.tokenizer.findMultiWords( false );
        initTriTagger(triLexicons);

    }

    private void initTriTagger(TriTaggerLexicons triLexicons) {
       /* BackupLexicon, Phrases, Morpho are all null */
       tagger = new TriTagger(sentenceStart, false, ngram, triLexicons.ngrams, triLexicons.freqLexicon, null, null, null);
}

    public Sentences tag( String text ) throws IOException
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
                tagger.tagTokens( tokenizer.tokens, true );

                sent = new Sentence(tokenizer.tokens);
                sents.add(sent);
            }
            //if (insertNewline)
            //    segments.add(newLineSegment);
        }

        return sents;
    }

}
