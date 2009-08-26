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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Anton
 */
public class PostFixer {
    
    private static Properties postfixRules;       

    public static void loadPostfixRules( InputStream postfixIs ){
        postfixRules = new Properties();

        try {
            postfixRules.load( postfixIs );
        } catch (IOException ex) {
            System.out.println("Could not load settings for Lemmald!");
            ex.printStackTrace();
        }
    }

    public static void loadPostfixRules( String dataFile ){

            java.io.FileInputStream fis = null;
            try {
                fis = new FileInputStream( dataFile );
            } catch (FileNotFoundException ex) {
                System.out.println("Could not find settings file");
                ex.printStackTrace();
            }

            loadPostfixRules( fis );
    } 
    
    public static void postfix(LemmaResult lemmaResult) {

        String lemma = lemmaResult.getLemma();
        int len = lemma.length();
        String currentEnding;
        for( int i=0; i<len; i++ ){
            currentEnding = lemma.substring(i);
            if( postfixRules.containsKey( currentEnding ) ){
                lemmaResult.setLemma( LemmaldUtils.applyMapping( lemmaResult.getLemma(), currentEnding, postfixRules.get(currentEnding).toString() ));
                return;
            }
        }
    }
    
}
