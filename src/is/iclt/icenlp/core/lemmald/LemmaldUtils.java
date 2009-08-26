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


import java.util.HashSet;

/**
 *
 * @author Anton
 */
public class LemmaldUtils {
    
    private static HashSet<String> vowels = loadVowels();        
    private static HashSet<String> wordClasses = loadWordClasses();
    private static HashSet<String> punctuation = loadPunctuation();
    
    public static int lastVowel( String s ){                
        int len = s.length();
        for( int i=len-1; i>=0; i-- ){
            if( isVowel( s.charAt(i) ) ){
                return i;
            }
        }        
        return -1;
    }        
    
    public static int firstVowel( String s ){
        int len = s.length();
        for( int i=0; i<len; i++){
            if( isVowel( s.charAt(i) ) ){
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isVowel( char c ){                
        return vowels.contains( Character.toString( c ) );
    }
    
    public static boolean isWordClass( String s){
        return wordClasses.contains( s );
    }
    
    public static boolean isPunctuation( String s ){
        return punctuation.contains( s );
    }
    
    public static String getWordClass( String tag ){
        String wordClass = tag.substring(0,1); 
        if( ! isWordClass(wordClass) ){            
            if( LemmaldUtils.isPunctuation( tag ) ){
                wordClass = "m"; // if punctuation
            }            
            else {
                wordClass = "x";
            }
        }    
        
        return wordClass;
    }
    
    private static HashSet<String> loadVowels(){
        vowels = new HashSet<String>();
        vowels.add("a");
        vowels.add("á");
        vowels.add("e");
        vowels.add("é");
        vowels.add("i");
        vowels.add("í");
        vowels.add("o");
        vowels.add("ó");
        vowels.add("u");
        vowels.add("ú");
        vowels.add("y");
        vowels.add("ý");        
        vowels.add("æ");
        vowels.add("ö");
        vowels.add("E"); // ei
        vowels.add("Y"); // ey
        vowels.add("A"); // au
        
        return vowels;
    }
    
    private static HashSet<String> loadWordClasses() {              
        HashSet<String> wordClasses = new HashSet<String>();
        wordClasses.add("n");
        wordClasses.add("s");
        wordClasses.add("l");
        wordClasses.add("f");
        wordClasses.add("a");
        wordClasses.add("g");
        wordClasses.add("t");
        wordClasses.add("c");
        wordClasses.add("e");
        wordClasses.add("x");
        
        return wordClasses;
    }    
    
    public static String createBestMapping( String wordForm, String lemma ){

        String mapping = wordForm + ">" + lemma;
        // Find longest start match
        int minLen = Math.min( wordForm.length(), lemma.length() );                
        if( wordForm.charAt(0) == lemma.charAt(0) ){
            
            String wordFormStart = wordForm.substring(0,minLen-1);
            String lemmaStart = lemma.substring(0,minLen-1);

                String wordFormEnd;
                String lemmaEnd;
                
                while( !wordFormStart.equals(lemmaStart)){                                        
                    wordFormStart = wordFormStart.substring(0,wordFormStart.length()-1);
                    lemmaStart = lemmaStart.substring(0,lemmaStart.length()-1);                    
                }
                
                wordFormEnd = wordForm.substring(lemmaStart.length());
                lemmaEnd = lemma.substring(lemmaStart.length());                                                               
               
                mapping = wordFormEnd + ">" + lemmaEnd;
        }
                     
        return mapping;
    }
            
    public static String applyMapping( String wordForm, String mappingFrom, String mappingTo ){       
       // return input if ending does not match
        if( !wordForm.endsWith( mappingFrom )){
            return wordForm;
        }
        String wordBase = wordForm.substring(0, Math.max(0, wordForm.length()-mappingFrom.length()));        
        
        // Do u-umlaut
        if( mappingFrom.contains("u") && mappingTo.contains("a") ){
            int lastVowel = LemmaldUtils.lastVowel( wordBase );
            // print("lastV " + wordBase + " - " + lastVowel );
            if( lastVowel > -1 && wordBase.charAt(lastVowel) == 'ö' && ! RuleDatabase.wordFormExists(wordBase+mappingTo) ){
                wordBase = wordBase.substring(0,lastVowel) + "a" + wordBase.substring(lastVowel+1);
            }
        }        
        return  wordBase + mappingTo; 
    } 
    
    public static String applyMapping( String wordForm, String mapping ){
        String[] mappingUnits = mapping.split(">");
        if(mappingUnits.length != 2){
            return wordForm;
        }        
        
        String source = mappingUnits[0];
        String target = mappingUnits[1];
     
        if( !wordForm.endsWith(source)){
            return wordForm;
        }
        
        return wordForm.substring(0, Math.max(0, wordForm.length()-source.length())) + target;                  
    }

    private static HashSet<String> loadPunctuation() {
        HashSet<String> punctuation = new HashSet<String>();
        punctuation.add(".");
        punctuation.add(",");
        punctuation.add(":");
        punctuation.add(";");
        punctuation.add("!");
        punctuation.add("?");
        punctuation.add(".");
        punctuation.add("...");
        punctuation.add("..");
        punctuation.add("-");
        punctuation.add("(");
        punctuation.add(")");
        punctuation.add("{");
        punctuation.add("}");
        punctuation.add("\"");
        punctuation.add("'");
        
        return punctuation;
    }

    public static void print( String s ){
        if( LemmaldSettings.isOn("systemOut")){
            System.out.println( s );
        }
    }


}
