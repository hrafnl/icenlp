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

import is.iclt.icenlp.core.utils.ZipGzipper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author Anton Karl Ingason
 */
public class RuleDatabase {
    
    // Rules
    private static Hashtable<String,HashSet<LemmaRule>> rules;

    public static void loadRules( String dataFile ){
            java.io.FileInputStream fis = null;
            try {
                fis = new FileInputStream( dataFile );
            } catch (FileNotFoundException ex) {
                System.out.println("Could not find settings file");
                ex.printStackTrace();
            }

            loadRules( fis );
    }

    public static void loadRules( InputStream ris ){
        LemmaldUtils.print("Loading rules ...");

        long start = System.currentTimeMillis();
        rules = new Hashtable<String,HashSet<LemmaRule>>();


        // Load and tokenize file
        String ruleDatabase = ZipGzipper.gz2String( ris );
        //String ruleDatabase = FileOperations.getContents("rules.txt");
        StringTokenizer ruleTokenizer = new StringTokenizer( ruleDatabase, System.getProperty("line.separator") );

        // Prepare variables
        String ruleLine;
        LemmaRule ruleToInsert;
        HashSet<LemmaRule> mappingSet;
        int ruleCounter = 0;
        String ruleId;
        String currentMapping;
        int successCount;
        StringTokenizer ruleSplitter;
        String delim = ":";

        // Go through every line in rule database file
        while( ruleTokenizer.hasMoreTokens() ){
            ruleLine = ruleTokenizer.nextToken();
            ruleSplitter = new StringTokenizer( ruleLine, delim );

            ruleId = ruleSplitter.nextToken();
            currentMapping = ruleSplitter.nextToken();

            successCount = Integer.parseInt( ruleSplitter.nextToken() );
            ruleToInsert = new LemmaRule( currentMapping, successCount );

            if( !rules.containsKey( ruleId ) ){
                mappingSet = new HashSet<LemmaRule>();
                mappingSet.add( ruleToInsert );
                rules.put( ruleId, mappingSet );
            }
            else {
                mappingSet = rules.get( ruleId );
                mappingSet.add( ruleToInsert );
            }

            ruleCounter++;
            if( ruleCounter % 100000 == 0 ){
                LemmaldUtils.print( ruleCounter + " rules loaded" );
            }
        }

        long time = System.currentTimeMillis() - start;
        LemmaldUtils.print( ruleCounter + " rules loaded in " + time + " ms");
    }
                  
    public static boolean ruleIdExists( String ruleId ){
        return rules.containsKey( ruleId );
    }
    
    public static HashSet<LemmaRule> getLemmaRules( String ruleId ){
        return rules.get( ruleId );
    }
    
    public static boolean wordExists( String wordForm, String tag ){
        if( wordForm.length() == 0 ){
            return false;
        }        
        return rules.containsKey("e="+wordForm.charAt(wordForm.length()-1)+",w="+wordForm+",p="+tag);        
    }
    
    public static boolean wordFormExists( String wordForm ){
        if( wordForm.length() == 0 ){
            return false;
        }               
        return rules.containsKey("e="+wordForm.charAt(wordForm.length()-1)+",w="+wordForm );           
    } 
    
    // experimental method, needs a lot more work
    public static boolean wordFormExistsLoose(String wordForm) {
        if( wordForm.length() < 3 ){
            return false;
        }
        
        if( wordForm.endsWith("s") && wordFormExists( wordForm.substring(0,wordForm.length()-1) ) ){
            return true;
        }

        if( wordForm.endsWith("ar") && wordFormExists( wordForm.substring(0,wordForm.length()-2) ) ){
            return true;
        }

        if( wordForm.endsWith("a") && wordFormExists( wordForm.substring(0,wordForm.length()-1) ) ){
            return true;
        }              
        
        if( wordExists( wordForm + "a", "sng")){
            return true;
        }
    
        return false;
    }    
    
}
