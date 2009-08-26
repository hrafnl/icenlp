/*
 * IceNLP.java
 *
 * Created on 27. mars 2008, 17:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.icenlp;

/*
 * IceNLP.java
 *
 * Created on 23. j�n� 2007, 12:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.facade.IceParserFacade;
import is.iclt.icenlp.facade.IceTaggerFacade;


import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Wrapper class to connect to the IceNLP tools by Hrafn Loftsson.
 * @author Anton
 */
public class IceNLP {
    
    private static IceTaggerFacade tagger;
    private static IceParserFacade parser;
    private static boolean triTaggerCreated;
    private static String modelPath;
    
    /**
     * Creates a new instance of IceNLP with a tagger and a parser.
     * @throws IOException Throws exception if input files are not found.
     * @param dataFolder The folder where the data files for IceNLP are loacated.
     */
    public IceNLP() {
              
        // Load resources
        IceTaggerResources iceResources = new IceTaggerResources();
        TokenizerResources tokResources = new TokenizerResources();
        Lexicon tokLexicon = null;
        IceTaggerLexicons iceLexicons = null;

        try {
            iceLexicons = new IceTaggerLexicons(iceResources);
            tokLexicon = new Lexicon(tokResources.isLexicon);

            // Initialize tagger (one sentence per line)
            tagger = new IceTaggerFacade(iceLexicons, tokLexicon, Segmentizer.sentencePerLine);
        } catch ( IOException ex ){
            System.out.println("Could not load IceNLP!");
            ex.printStackTrace();
        }
         
        // parser = new IceParserFacade();
        
    }
    
    /**
     * Tags Icelandic text using IceParser and splits sentences.
     * @param text The text to be tagged.
     * @return Tagged text with the following output:
     * 
     * <PRE>
     * B�r�ur nken-m
     * leikur sfg3en
     * s�r fpke�
     * . .
     * 
     * Anna nvfe
     * reiknar sfg3en
     * flatarm�l nhen
     * undir a�
     * ferli nke�
     * . .
     * </PRE>
     * 
     * Note: Empty line between sentences. A third column may appear with a (*) indicating that the word is unknown by the tagger.
     */
    public ArrayList<Sentence> tagText( String text ) {
        
        ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();

        ArrayList<is.iclt.icenlp.core.tokenizer.Sentence> sentences;
                 
    	try {
           
            sentences = tagger.tag(text.trim()).getSentences();
      
        } catch ( Exception e ){   
          // System.out.println("Villa � tagger. " );
    	  // System.out.println(e);    	            
    	  return sentenceList;
    	}
        
          for( int i=0; i<sentences.size(); i++ ){
             Sentence currentSentence = new Sentence();                                  
             String taggedSentence = sentences.get(i).toStringNewline(true).trim();             
             StringTokenizer wordTokenizer = new StringTokenizer( taggedSentence, "\n" );
             while( wordTokenizer.hasMoreTokens() ){
                String[] wordLine = wordTokenizer.nextToken().split(" ");
                
                String word = wordLine[0];
                String tag = wordLine[1];
                boolean unknown = false;                
                
                if( wordLine.length == 3 ){
                    unknown = true;
                }
                
                Word wordObject = new Word(word,tag,unknown);
                currentSentence.add( wordObject );
             }
             
             sentenceList.add( currentSentence );
          }
                        	  
          

    	
        return sentenceList;
    }
    
    public String tagAndParseText( String text ){
    
        try {
        	is.iclt.icenlp.core.tokenizer.Sentences sentences = tagger.tag(text);
            String s = sentences.toString();                       
            
            String parsedText = parser.parse( s );            
            StringTokenizer butari = new StringTokenizer( parsedText );
            
            while( butari.hasMoreTokens() ){
                String butur = butari.nextToken();
            }
            
            return parsedText;
            
        } catch ( IOException e ){
            System.out.println(e);
            return null;
        }
               
    }
    
    
}
