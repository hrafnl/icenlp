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
 * Anton Karl Ingason, University of Iceland.
 * anton.karl.ingason@gmail.com
 */

package is.iclt.icenlp.core.lemmald;

import is.iclt.icenlp.core.formald.tags.TaggedSentence;
import is.iclt.icenlp.core.formald.tags.TaggedText;
import is.iclt.icenlp.core.formald.tags.TaggedToken;
import is.iclt.icenlp.facade.IceNLP;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class Lemmald {

    private volatile static Lemmald uniqueInstance;

    /**
     * Construct Lemmald using the provided rule database and postfix rules file.
     * @param ruleDatabase The rule database to be used. If null is passed, default database is loaded.
     * @param postfixRules The postfix rules file to be used. If null is passed, default database is loaded.
     */
    public Lemmald( String ruleDatabase, String postfixRules ){
        // Rule Database
        if( ruleDatabase != null ){
            RuleDatabase.loadRules( ruleDatabase );
        }
        else {
            RuleDatabase.loadRules( getClass().getResourceAsStream("/dict/lemmald/rule_database_utf8.dat") );
        }

        // Postfix Rules
        if( postfixRules != null ){
            PostFixer.loadPostfixRules( postfixRules );
        }
        else {
            PostFixer.loadPostfixRules( getClass().getResourceAsStream("/dict/lemmald/postfixRules.txt") );
        }
    }

    /**
     * Construct Lemmald with default settings
     */
    public Lemmald(){
        this( null, null );
    }

    public TaggedText lemmatizeText( String plainText ){
        // TODO review this!
        // IceNLP iceNLP = IceNLP.getInstance();
        // Document document = Ice1TagFormat.newInstance().decode( iceNLP.tagText(plainText) );
        // TaggedText taggedText = TaggedText.newInstance(document);
        
        TaggedText taggedText = IceNLP.getInstance().tagText(plainText);
        lemmatizeTagged( taggedText );
        return taggedText;
    }

    public void lemmatizeTagged( TaggedText taggedText ){
        List<TaggedSentence> sentences = taggedText.getSentences();
        String word, tag, lemma;

        for( TaggedSentence sentence : sentences ){
            List<TaggedToken> tokens = sentence.getTokens();
            for( TaggedToken token : tokens ){
                word = token.getWord();
                tag = token.getTag();
                lemma = lemmatize( word.toLowerCase(), tag ).getLemma();
                token.setLemma( lemma );
            }
        }        
    }

    public LemmaResult lemmatize(String wordForm, String tag ) {
        // Initialize result object
        LemmaResult lemmaResult = new LemmaResult( wordForm, null, false );

        // Do compound analysis
        if( LemmaldSettings.isOn("compoundAnalysis")){
            lemmaResult=this.compoundLemmatize( wordForm, tag );
        }

        if( lemmaResult.isSuccess() ){
            return lemmaResult;
        }
        else {
            // Do longest match lemmatization
            if( LemmaldSettings.isOn("longestMatch")){
                lemmaResult = this.longestMatchLemmatize( wordForm, tag );
            }
        }

        // Do main lemmatization
        if( lemmaResult.isSuccess() ){
            return lemmaResult;
        }
        else {
            lemmaResult=this.lemmatizeEnding( wordForm, tag );
        }

        // Run PostFixer
        if( LemmaldSettings.isOn("postFixer")){
            PostFixer.postfix( lemmaResult );
        }


        // TODO: Do this properly
        if( lemmaResult.getLemma().endsWith("$^1$") ){
            lemmaResult.setLemma( lemmaResult.getLemma().replace("$^1$", "")); // sökkva$^1$
        }
        else if( lemmaResult.getLemma().endsWith( "$^2$") ){
            lemmaResult.setLemma( lemmaResult.getLemma().replace("$^2$", ""));
        }
        else if( lemmaResult.getLemma().contains( "/") ){
        	int index = lemmaResult.getLemma().indexOf('/');        	
            lemmaResult.setLemma( lemmaResult.getLemma().substring(0,index) ); // éta/eta
        }        

        return lemmaResult;
    }

    protected static ArrayList<String> createRuleIds( String wordForm, String tag ){
        String prefix = "e="+wordForm.charAt( wordForm.length()-1 );
                
        ArrayList<String> ruleIds = new ArrayList<String>();
        String wordClass = LemmaldUtils.getWordClass( tag );
       
        ruleIds.add( prefix + ",w="+wordForm );        
 
        if( tag.length() > 1 ){
            
            ruleIds.add( prefix + ",p="+tag);  
            
            // Tag cutoff length for nouns, verbs etc.
            int cutlen = tag.length()-1;
            
            if( wordClass.equals("n")){
                cutlen=1;
            }
            if( wordClass.equals("s")){
                cutlen=1;
            }
            if( wordClass.equals("l")){
                cutlen=2;
            } 
                                                
            for( int i=cutlen; i>0;i--){                                   
               ruleIds.add(  prefix + ",w="+wordForm+",p="+wordClass+i+tag.charAt(i) );
            }                        
        }                        
        ruleIds.add( prefix + ",w="+wordForm+",p="+tag);
        
        return ruleIds;
    }
     
    private LemmaResult applyRuleId( String ruleId, String wordForm ){
                HashSet<LemmaRule> mappings = RuleDatabase.getLemmaRules( ruleId );               
                LemmaRule currentLemmaRule;
                LemmaResult lemma = new LemmaResult( wordForm, null, false );
                Iterator<LemmaRule> mappingIterator = mappings.iterator();                
                // String currentMappingEnding;                        
                String message;
                int currentSuccessValue=0;
                int currentLen=0;
                
                while( mappingIterator.hasNext() ){
                    
                    currentLemmaRule =  mappingIterator.next();                                                                                                          

                        if( wordForm.endsWith( currentLemmaRule.getMappingFrom() ) && currentSuccessValue < currentLemmaRule.getSuccessRate() ){
                            currentSuccessValue = currentLemmaRule.getSuccessRate();
                            currentLen = currentLemmaRule.getMappingFrom().length();
                            lemma.setLemma( currentLemmaRule.applyTo( wordForm ) );                                       
                            message = "[END("+ruleId+":"+currentLemmaRule.getMapping()+")]";
                            lemma.setMessage(message);
                            lemma.setSuccess(true);
                        }
                }
                return lemma;
    }

    private LemmaResult compoundLemmatize(String wordForm, String tag) {
        LemmaResult lemma = new LemmaResult( wordForm, "", false );
        String beginning;
        String ending;
        int len = wordForm.length();
        
        if( ! RuleDatabase.wordExists( wordForm, tag ) ){
           for( int i=1; i<len-2; i++ ){
               beginning = wordForm.substring(0,i);
               ending = wordForm.substring(i);
                              
               if( RuleDatabase.wordExists( ending, tag ) && RuleDatabase.wordFormExists(beginning) ){
                    lemma = lemmatizeEnding( ending, tag );
                    lemma.setLemma( beginning + lemma.getLemma() );
                    lemma.setMessage("[COMP:ST] ("+beginning+"-"+ending+">"+lemma.getLemma()+")" );
                    lemma.setSuccess( true );
                    return lemma;         
               }
                              
               if( RuleDatabase.wordExists( ending, tag ) && RuleDatabase.wordFormExistsLoose(beginning) ){
                    lemma = lemmatizeEnding( ending, tag );
                    lemma.setLemma( beginning + lemma.getLemma() );
                    lemma.setMessage("[COMP:LO]");
                    lemma.setSuccess( true );
                    return lemma;         
               }                                                
             
           }           
           
        }           
                     
        return lemma;
    }
    
    private LemmaResult longestMatchLemmatize( String wordForm, String tag ){
        int len = wordForm.length();
        String ending;
        String beginning;
        LemmaResult lemma = new LemmaResult( wordForm, null, false );
        if( len > 6 ){
            int i=1;        
            while( i<len-5 ){
                ending = wordForm.substring(i);
                beginning = wordForm.substring(0,i);  
                
                if( RuleDatabase.wordExists( ending, tag ) && LemmaldUtils.firstVowel(beginning) > -1 ){
                   lemma = lemmatizeEnding( ending, tag );
                   lemma.setLemma( beginning + lemma.getLemma() );
                   lemma.setMessage(" [COMP:LM]");
                   lemma.setSuccess( true );
                   return lemma;
                }
                i++;
            }        
        }
        
        return lemma;
    }
            
    private LemmaResult lemmatizeEnding( String wordForm, String tag ){
        
        LemmaResult lemma = new LemmaResult( wordForm, null, false );       
        ArrayList<String> ruleIds = createRuleIds( wordForm, tag );

        int ruleLevel = ruleIds.size();
        String theRule;
        
        for( int i=ruleIds.size()-1; i>=0; i-- ){
            
            theRule = ruleIds.get(i);               
            
            if( RuleDatabase.ruleIdExists( theRule ) ){
                lemma = applyRuleId( theRule, wordForm );
            }
            if( lemma.isSuccess() ){
                return lemma;
            }
            ruleLevel--;
        }       
        return lemma; 
    }

    public static Lemmald newInstance(){
        return new Lemmald();
    }
    
    public static Lemmald getInstance(){
        if( uniqueInstance == null ){
            synchronized( Lemmald.class ){
                if( uniqueInstance == null ){
                    uniqueInstance = new Lemmald();
                }
            }
        }
        return uniqueInstance;
    }

    public static void terminate(){
        uniqueInstance = null;
    }
}