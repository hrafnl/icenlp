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

import is.iclt.icenlp.core.formald.tags.TagFormat;
import is.iclt.icenlp.core.formald.tags.TaggedText;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.SrxSegmentizer;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.utils.Lexicon;
import java.io.IOException;

/**
 * Provides simple singleton access to tools from the <a target="_blank" href="http://nlp.ru.is">IceNLP</a>
 * toolbox. Tools include IceTagger, IceParser (shallow parser) and Lemmald (lemmatizer).
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
    
    public String srxSegmentize( String text ){    
    	if( text == null ){
    		return null;    		
    	}
    	
    	return SrxSegmentizer.getInstance().sentencePerLine(text);
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
     * This operation assigns a fully disambiguated unique tag, for unknown
     * as well as known word forms.
     * @param input The text to be tagged. The input format is plain text.
     *    and the encoding should be UTF-8. Sentences are segmentized using
     *    {@link srxSegmentize} before tagging.
     * @return The tagged text as a {@link TaggedText} object that can be
     *    queried for sentences, tokens and their corresponding tags.
     *    Returns {@code null} if input is {@code null}.
     */
    public TaggedText tagText( String text ){
        if( text == null ){
            return null;
        }
        
        String lines = srxSegmentize(text);
        return tagLines( lines );       
    }
    
    
    
    /**
     * Part-of-Speech (PoS) tags a String using IceTagger. IceTagger is a rule
     * based PoS tagger for Icelandic and part of the IceNLP toolbox.
     * This operation assigns a fully disambiguated unique tag, for unknown
     * as well as known word forms.
     * @param lines The text to be tagged. The input format is <b>one sentence per line</b>
     *    and the encoding should be UTF-8.
     * @return The tagged text as a {@link TaggedText} object that can be
     *    queried for sentences, tokens and their corresponding tags.
     *    Returns {@code null} if input is {@code null}.
     */
    public TaggedText tagLines( String lines ){
        if( lines == null ){
            return null;
        }

        String output = null;
        try {
            output = tagger.tag( lines ).toString();
        } catch (IOException ex) {
            System.out.println("Exception in IceTagger!");
            ex.printStackTrace();
        }
        
        return TaggedText.newInstance( output, TagFormat.ICE1 );
    }
    
    /**
     * Part-of-Speech tags a String using the method {@link #tagLines}
     * and also assigns a lemma (base form) to each token using the
     * Lemmald lemmatizer. 
     * @param text
     * @return The tagged text as a {@link TaggedText} object that can be
     *    queried for sentences, tokens and their corresponding tags and lemmata.
     *    Returns {@code null} if input is {@code null}.
     */
    public TaggedText tagAndLemmatizeText( String text ){
    	String lines = srxSegmentize( text );
    	TaggedText output = tagAndLemmatizeLines( lines );    	
    	return output;
    }     
    
    /**
     * Part-of-Speech tags a String using the method {@link #tagLines}
     * and also assigns a lemma (base form) to each token using the
     * Lemmald lemmatizer. 
     * @param lines
     * @return The tagged text as a {@link TaggedText} object that can be
     *    queried for sentences, tokens and their corresponding tags and lemmata.
     *    Returns {@code null} if input is {@code null}.
     */
    public TaggedText tagAndLemmatizeLines( String lines ){
    	TaggedText output = tagLines( lines );
    	Lemmald.getInstance().lemmatizeTagged( output );
    	return output;
    }        

    /**
     * Parses the input using IceParser. IceParser is a rule based shallow parser
     * (chunker) and part of the IceNLP toolbox. 
     * @param input Part-of-Speech tagged text of the format ICE2.
     * method of this class. Encoding UTF-8.
     * @return Parsed text. Includes tags as well as annotation of phrases. Encoding UTF-8.
     * Returns {@code null} if input is {@code null}.
     */
    public String parseLines( String input ){
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
     * Tags and parses a String using IceTagger and IceParser.
     * @param text The text to be tagged and parsed. Encoding UTF-8. 
     *  Plain text. Sentences are segmentized using {@link #srxSegmentize(String)}
     * @return Tagged and parsed text, {@code null} if input is {@code null}.
     */
    public String tagAndParseText( String text ){
        if( text == null ){
            return null;
        }
        
        String lines = srxSegmentize(text);
        return parseLines( tagLines( lines ).toString(TagFormat.ICE2) );
    }   
    

    /**
     * Tags and parses a String using IceTagger and IceParser.
     * @param input The text to be tagged and parsed. Encoding UTF-8. 
     *  One sentence per line.
     * @return Tagged and parsed text, {@code null} if input is {@code null}.
     */
    public String tagAndParseLines( String input ){
        if( input == null ){
            return null;
        }
        
        return parseLines( tagLines( input ).toString(TagFormat.ICE2) );
    }
    
    /**
     * Constructs a new instance of IceNLP
     * @return A newly created instance of IceNLP.
     */
    public static IceNLP newInstance(){
    	return new IceNLP();	    	
    }
        
    
    /**
     * Provides a unique instance of the IceNLP class. The instance can be
     * used to invoke methods for Part-of-Speech tagging and parsing Icelandic
     * text. Use <code>IceNLP.terminate()</code> to terminate the instance
     * and release resources.
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
    
    /**
     * Terminates the unique instance provided by getInstance() by
     * setting it to null and releasing resources.
     */
    public static void terminate(){
    	Lemmald.terminate();
    	SrxSegmentizer.terminate();
    	uniqueInstance = null;
    }

}
