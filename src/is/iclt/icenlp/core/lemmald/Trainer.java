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

import is.iclt.icenlp.core.lemmald.tools.FileOperations;
import is.iclt.icenlp.core.lemmald.tools.ZipGzipper;

import java.io.File;
import java.util.*;

/**
 *
 * @author Anton
 */
public class Trainer {
    
    public void trainLemmatizer( String trainingDataFile, String ruleOutputFile ){
          
        Hashtable<String,HashSet> allRules = new Hashtable<String,HashSet>();        
        Hashtable<String,Integer> ruleSuccess = new Hashtable<String,Integer>();
        Hashtable<String,Integer> ruleFailure = new Hashtable<String,Integer>();
        
        // Load training data        
        ArrayList<LemmaWord> trainingData = this.loadTrainingData( trainingDataFile );
                
        // Begin training
        System.out.println("Begin training");
        Iterator<LemmaWord> trainingIterator = trainingData.iterator();
        
        // Prepare variables
        LemmaWord currentWord;
        Hashtable<String,String> currentRuleSet;
        String currentRule;
        String currentMapping;
        HashSet currentMappingSet;
        int mappingCounter = 0;
        HashSet<String> mappingSet;
        Iterator<String> ruleIterator;
        
        // Go through every token in training data
        while( trainingIterator.hasNext() ){
            currentWord = trainingIterator.next();
            
            // Create every rule needed for this token
            currentRuleSet = createRules( currentWord );                         
            ruleIterator = currentRuleSet.keySet().iterator();            
            while( ruleIterator.hasNext() ){
                currentRule = ruleIterator.next();                                
                currentMapping = currentRuleSet.get( currentRule );
                       
                // Insert this rule in allRules
                if( !allRules.containsKey(currentRule) ){
                    mappingSet = new HashSet<String>();
                    mappingSet.add( currentMapping );
                    allRules.put( currentRule, mappingSet );
                    mappingCounter++;
                }
                else {
                    currentMappingSet = allRules.get( currentRule );
                    if( !currentMappingSet.contains( currentMapping )){
                        currentMappingSet.add( currentMapping );
                        mappingCounter++;
                    }
                }
            }           
        }
        
        System.out.println("Rule count: " + allRules.size() );
        System.out.println("Mapping count: " + mappingCounter );
        
        // Begin testing rules
        
        // Prepare variables
        int counter = 0;
        Iterator<LemmaWord> testingIterator = trainingData.iterator();
        ArrayList<String> currentWordRules;
        Iterator<String> currentRulesIterator;
        
        // Go through every token in training data
        while( testingIterator.hasNext() ){
            counter++;
            if( counter %100000 == 0 ){
                System.out.println("words done: " + counter );
            }
            
            currentWord = testingIterator.next();
            
            currentWordRules = Lemmald.createRuleIds( currentWord.getWordForm(), currentWord.getTag() );
            currentRulesIterator = currentWordRules.iterator();
            while( currentRulesIterator.hasNext() ){
                currentRule = currentRulesIterator.next();                
                currentMappingSet = allRules.get( currentRule );
                
                // go through all mappings for this rule/pattern matches
                Iterator<String> mappingSetIterator = currentMappingSet.iterator();
                while( mappingSetIterator.hasNext() ){
                    currentMapping = mappingSetIterator.next();
                    String currentRuleWithMapping = currentRule + ":" + currentMapping;                                       
                    String[] mappingUnits = currentMapping.split(">");
                    // if mappingFrom has the correct ending
                    if( currentWord.getWordForm().endsWith( mappingUnits[0] ) ){
                        
                            String lemmaSuggestion = LemmaldUtils.applyMapping( currentWord.getWordForm(), currentMapping );
                            
                            // check if lemmatization was correct
                            if( currentWord.getLemma().equals(lemmaSuggestion) ){
                                // it was
                                if( !ruleSuccess.containsKey( currentRuleWithMapping ) ){
                                    // create success counter
                                    ruleSuccess.put( currentRuleWithMapping, new Integer(1) );
                                }
                                else {
                                    // or increment if it exists
                                    Integer successCount = ruleSuccess.get( currentRuleWithMapping );
                                    int myCount = successCount.intValue() + 1;
                                    ruleSuccess.put( currentRuleWithMapping, myCount );
                                }
                            }
                            else {
                                // did not lemmatize correctly
                                if( !ruleFailure.containsKey( currentRuleWithMapping ) ){
                                    // create failure counter
                                    ruleFailure.put( currentRuleWithMapping, new Integer(1) );
                                }
                                else {
                                    // or increment if it exists
                                    Integer failureCount = ruleFailure.get( currentRuleWithMapping );
                                    int myCount = failureCount.intValue() + 1;
                                    ruleFailure.put( currentRuleWithMapping, myCount );
                                }               
                            }
                    }
                    // end of: if mappingFrom has the correct ending
                }                
            }
        }
                
        Hashtable<String,String> goodRules = new Hashtable<String,String>();        
        Hashtable<String,Double> rulesToWrite = new Hashtable<String,Double>();        
        Hashtable<String,String> finalRules = new Hashtable<String,String>();
        
        Iterator<String> successRuleIterator = ruleSuccess.keySet().iterator();
        while( successRuleIterator.hasNext() ){
            String successRule = successRuleIterator.next();                        
            int successRate = ruleSuccess.get(successRule).intValue(); 
           
            String[] successRuleParts = successRule.split(">");                        
            String ruleId = successRuleParts[0];
            
            if( ! rulesToWrite.containsKey(ruleId)){                                                
                rulesToWrite.put( ruleId, new Double(successRate) );
                finalRules.put( successRuleParts[0], successRuleParts[1] + ":" + successRate );                
            }
            else {
                double oldRate = rulesToWrite.get(ruleId).doubleValue();
                if( oldRate < successRate ){
                 rulesToWrite.put( ruleId, new Double(successRate) );
                 finalRules.put( successRuleParts[0], successRuleParts[1] + ":" + successRate );        
                }
            }
        }
        
        Iterator<String> ruleOutput = finalRules.keySet().iterator();
        StringBuilder fileOutput = new StringBuilder();
        while( ruleOutput.hasNext() ){
            String myRule = ruleOutput.next();
            fileOutput.append( myRule + ">" + finalRules.get(myRule) + System.getProperty("line.separator") );            
        }
                
        String tempFile = System.getProperty("user.dir") + "/" + "rules.txt";
                 
        FileOperations.writeContents( fileOutput.toString(), tempFile );         
        File theRules = new File( tempFile );        
        ZipGzipper.gzipFile( theRules, ruleOutputFile );
        File toDelete = new File( tempFile );      
        toDelete.delete();
    }  
    
    public static ArrayList<LemmaWord> loadTrainingData( String filename ){
        return loadTrainingData( filename, true );
    }
    
    public static ArrayList<LemmaWord> loadTrainingData( String filename, boolean omitColon ){
    
        System.out.println("Loading data");        
        ArrayList<LemmaWord> lemmaList = new ArrayList<LemmaWord>();
                
        String otbtxt = FileOperations.getContents( filename );        
        StringTokenizer otbLines = new StringTokenizer( otbtxt, System.getProperty("line.separator") );
        
        // Prepare variables
        String line;
        String[] lineTokens;
        LemmaWord toInsert;
        while( otbLines.hasMoreTokens() ){
            line = otbLines.nextToken();
            if( !(line.contains(":")&&omitColon)){
                lineTokens = line.split(" ");
                if( lineTokens.length == 3 ){
                    toInsert = new LemmaWord( lineTokens[0], lineTokens[1], lineTokens[2] );
                    lemmaList.add( toInsert );                          
                }
            }
        }        
        
        return lemmaList;
    }
    
    public static Hashtable<String,String> createRules( LemmaWord word ){
        return createRules( word.getWordForm(), word.getLemma(), word.getTag(), false );
    }
    
    public static Hashtable<String,String> createRules( LemmaWord word, boolean fullMapping ){
        return createRules( word.getWordForm(), word.getLemma(), word.getTag(), fullMapping );
    }
    
    public static Hashtable<String,String> createRules( String wordForm, String lemma, String tag, boolean fullMapping ){
        
        Hashtable<String,String> rules = new Hashtable<String,String>();
        ArrayList<String> ruleIds = Lemmald.createRuleIds( wordForm, tag );
        String mapping;
        
        if( fullMapping ){
            mapping = wordForm +">"+lemma;
        }
        else {
             mapping = LemmaldUtils.createBestMapping( wordForm, lemma );
        }                          
        for( int i=0; i<ruleIds.size(); i++){
            rules.put(ruleIds.get(i), mapping);                
        }
               
        return rules;
    }   
    
}
