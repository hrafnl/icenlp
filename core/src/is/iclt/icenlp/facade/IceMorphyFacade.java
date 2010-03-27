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

import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;

import java.io.IOException;

/**
 * Provides a simplified interface to IceMorphy.
 * @author Hrafn Loftsson
 */
public class IceMorphyFacade {
    private Tokenizer tokenizer;
    private Segmentizer segmentizer;
    private IceMorphy morphoAnalyzer;
    private boolean strictTokenization=true;

    public IceMorphyFacade(IceMorphyLexicons iceLexicons, Lexicon tokenizerLexicon,  int lineFormat) throws IOException
        {
            segmentizer = new Segmentizer(tokenizerLexicon, lineFormat);
            this.tokenizer = new Tokenizer( Tokenizer.typeIceTokenTags,
                                            strictTokenization,
                                            tokenizerLexicon);
            //this.tokenizer.findMultiWords( false );
            initIceMorphy(iceLexicons);

        }

     private void initIceMorphy(IceMorphyLexicons iceLexicons) {

        morphoAnalyzer = new IceMorphy(
                iceLexicons.dict,
                iceLexicons.baseDict,
                iceLexicons.endingsBase,
                iceLexicons.endings,
                iceLexicons.endingsProper,
                iceLexicons.prefixes,
                iceLexicons.tagFrequency, null );
     }

    public Sentences analyze( String text ) throws IOException
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
                morphoAnalyzer.setTokens(tokenizer.tokens);
                analyzeTokens();

                sent = new Sentence(tokenizer.tokens);
            }
            sents.add(sent);
            //if (insertNewline)
            //    segments.add(newLineSegment);
        }

        return sents;
    }

    private void analyzeUnknown(IceTokenTags tok)
    throws IOException
    {
        if (Character.isUpperCase(tok.lexeme.charAt(0)))    // mark it as proper noun if appropriate
          tok.setUnknownType(IceTokenTags.UnknownType.properNoun);

        morphoAnalyzer.morphoAnalysisToken(tok, null);  // no knowledge of previous tag
    }

    private void analyzeTokens() throws IOException
    {
       String str;
       IceTokenTags tok;

       for( int i = 0; i < tokenizer.tokens.size(); i++ )
	   {
		    tok = (IceTokenTags)tokenizer.tokens.get( i );
            // The lexicon load failes for some punctuation characters => assume all punctuation characters are known
            if (tok.isPunctuation())
            {
                tok.addTag(tok.lexeme);
                tok.setUnknown(false);
            }
            else
            {
                morphoAnalyzer.dictionaryTokenLookup(tok, false); // do a lookup
                if (tok.noTags())
                    morphoAnalyzer.dictionaryTokenLookup(tok, true); // do a lookup

                if (tok.noTags())
                {
                    tok.setUnknown(true);
                    tok.setUnknownType(IceTokenTags.UnknownType.none);
                    tok.setCompound(false);
                    analyzeUnknown(tok);
                }
                else
                    tok.setUnknown(false);
            }
            tok.cleanTags();
       }     
    }
}
