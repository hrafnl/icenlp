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


import java.io.Serializable;

/**
 *
 * @author Anton
 */
public class LemmaRule {
    
    private String mappingFrom;
    private String mappingTo;
    private int successRate;
    
    /** Creates a new instance of LemmaRule */
    public LemmaRule( String mapping, int successRate ) {
        this.successRate = successRate;
        this.setMapping( mapping );
    }
    
    public String applyTo( String wordForm ){
        if( !wordForm.endsWith( mappingFrom )){
            return wordForm;
        }
        String wordBase = wordForm.substring(0, Math.max(0, wordForm.length()-mappingFrom.length()));        

        if( LemmaldSettings.isOn("umlautSubstitution")){
            if( isUmlautCandidate( wordBase ) ){
                wordBase = performUmlautSubstitution( wordBase );
            } 
        }
               
        return  wordBase + mappingTo; 
    }    
    
    private String performUmlautSubstitution( String wordBase ){
        int lastVowel = LemmaldUtils.lastVowel( wordBase );
        return wordBase.substring(0,lastVowel) + "a" + wordBase.substring(lastVowel+1);    
    }
    
    private boolean isUmlautCandidate( String wordBase ){
        int mappingFromFirstVowel = LemmaldUtils.firstVowel( mappingFrom );
        int mappingToFirstVowel = LemmaldUtils.firstVowel( mappingTo );
        
        // must have vowels in from and to
        if( mappingFromFirstVowel == -1 || mappingToFirstVowel == -1 ){
            return false;
        }
        
        // the vowels must be 'u' and 'a'
        if( ! (mappingFrom.charAt(mappingFromFirstVowel) == 'u' && mappingTo.charAt(mappingToFirstVowel) == 'a') ){
            return false;
        }
        
        // wordForm with no umlaut substitution must not exist
        if( RuleDatabase.wordFormExists(wordBase+mappingTo) ){
            return false;
        }
                
        // there must be a vowel in the wordBase
        int lastVowel = LemmaldUtils.lastVowel( wordBase );
        if( lastVowel == -1 ){
            return false;
        }
        
        // if the last vowel is '�' then wordBase is an umlaut candidate
        if( wordBase.charAt(lastVowel) == '�' ){                
            return true; 
        }
        
        return false;
    }
   
    public String getMappingFrom(){
        return this.mappingFrom;        
    }
    
    public String getMappingTo(){
        return this.mappingTo;
    }

    public String getMapping() {
        return mappingFrom + ">" + mappingTo;
    }

    public void setMapping(String mapping) {        
        String[] mappingParts = mapping.split(">");
        this.mappingFrom = mappingParts[0];
        this.mappingTo = mappingParts[1];       
    }

    public int getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(int successRate) {
        this.successRate = successRate;
    }
    
}
