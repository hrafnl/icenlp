/*
 * Copyright (C) 2009 Anton Karl Ingason
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
 * Anton Karl Ingason.
 * anton.karl.ingason@gmail.com
 */
package is.iclt.icenlp.facade;

import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.utils.Lexicon;
import java.io.IOException;

/**
 * Provides singleton access to tools from the <a target="_blank" href="http://nlp.ru.is">IceNLP</a>
 * toolbox, IceTagger and IceParser.
 * Use the {@link #getInstance} method to acccess those tools.
 *
 * @author Anton Karl Ingason <anton.karl.ingason@gmail.com>
 */
public class IceNLP {

    private volatile static IceNLP uniqueInstance;
    private TokenizerFacade tokenizer;
    private IceMorphyFacade analyzer;
    private IceTaggerFacade tagger;
    private IceParserFacade parser;
    Lexicon tokLexicon=null;
    private boolean debug=false;

    private IceNLP() {

        Lexicon tokLexicon;
        IceTaggerLexicons iceLexicons;

        try {
            if (debug) {
                iceLexicons = new IceTaggerLexicons("../../dict/icetagger/");
                tokLexicon = new Lexicon("../../dict/tokenizer/lexicon.txt");
            }
            else {
                // Load resources
                IceTaggerResources iceResources = new IceTaggerResources();
                TokenizerResources tokResources = new TokenizerResources();
                iceLexicons = new IceTaggerLexicons(iceResources);
                tokLexicon = new Lexicon(tokResources.isLexicon);
            }
            // Initialize  (one sentence per line)
            tokenizer = new TokenizerFacade(tokLexicon, Segmentizer.sentencePerLine);
            analyzer = new IceMorphyFacade(iceLexicons.morphyLexicons, tokLexicon, Segmentizer.sentencePerLine);
            tagger = new IceTaggerFacade(iceLexicons, tokLexicon, Segmentizer.sentencePerLine);
        } catch ( IOException ex ){
            System.out.println("Could not load IceNLP!");
            ex.printStackTrace();
        }
        // Initialize parser        
        parser = new IceParserFacade();        
    }

    public String tokenize(String input) 
    {
        if( input == null ){
            return null;
        }
        /*if (tokLexicon == null) {
            TokenizerResources tokResources = new TokenizerResources();
            tokLexicon = new Lexicon(tokResources.isLexicon);
        } */
       String output = null;
       try {
           Sentences sents = tokenizer.tokenize(input);
           output = sents.toString();
        } catch (IOException ex) {
            System.out.println("Exception in Tokenizer!");
            ex.printStackTrace();
        }
        return output;
    }

    public String analyze( String input ){
        if( input == null ){
            return null;
        }

        String output = null;
        try {
            output = analyzer.analyze(input).toString();
        } catch (IOException ex) {
            System.out.println("Exception in IceTagger!");
            ex.printStackTrace();
        }
        return output;
    }
    /**
     * Part-of-Speech (PoS) tags a String using IceTagger. IceTagger is a rule
     * based PoS tagger for Icelandic and part of the IceNLP toolbox.
     * @param input The text to be tagged. The input format is one sentence per line
     *    and the encoding should be UTF-8.
     * @return The tagged text. The output format is one sentence per line where
     *    each token is followed by its PoS-tag. Encoding is UTF-8.
     *    Returns {@code null} if input is {@code null}.
     */
    public String tag( String input ){
        if( input == null ){
            return null;
        }

        String output = null;
        try {
            output = tagger.tag(input).toString();
        } catch (IOException ex) {
            System.out.println("Exception in IceTagger!");
            ex.printStackTrace();
        }
        return output;
    }

    /**
     * Parses the input using IceParser. IceParser is a rule based shallow parser
     * (chunker) and part of the IceNLP toolbox. 
     * @param input Part-of-Speech tagged text of the format returned by (@link #tag)
     * method of this class. Encoding UTF-8.
     * @return Parsed text. Includes tags as well as annotation of phrases. Encoding UTF-8.
     * Returns {@code null} if input is {@code null}.
     */
    public String parse( String input ){
        if( input == null ){
            return null;
        }

        String output = null;
        try {
            output = parser.parse(input, true, false);
        } catch (IOException ex) {
            System.out.println("Exception in IceParser!");
            ex.printStackTrace();
        }
        return output;
    }

    /**
     * Tags and parses a String using IceTagger and IceParser. Equivalent
     * to calling {@link #tag} first and passing the output to {@link #parse}.
     * @param input The text to be tagged and parsed. Encoding UTF-8.
     * @return Tagged and parsed text, {@code null} if input is {@code null}.
     */
    public String tagAndParse( String input ){
        if( input == null ){
            return null;
        }
        
        return parse( tag( input ));
    }
    
    /**
     * Provides a unique instance of the IceNLP class. The instance can be
     * used to invoke methods for Part-of-Speech tagging and parsing Icelandic
     * text.
     * @return A unique instance of IceNLP.
     */
    public static IceNLP getInstance(){
        if( uniqueInstance == null ){
            synchronized( IceNLP.class ){
                if( uniqueInstance == null ){
                    uniqueInstance = new IceNLP();
                }
            }
        }
        return uniqueInstance;
    }

}
