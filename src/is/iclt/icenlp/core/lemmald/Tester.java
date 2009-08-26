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


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author Anton
 */
public class Tester {
    
    Lemmald lemmatizer = null;
    Hashtable<String,SuccessCounter> successCounters = null; 
    
    /** Creates a new instance of Tester */
    public Tester( Lemmald lemmatizer ) {
        this.lemmatizer = lemmatizer;
        successCounters = new Hashtable<String,SuccessCounter>();
    }
    
    public void runLemmatizerTest( String testingFile ){

         // pick up file to test                  
         ArrayList<LemmaWord> testari = Trainer.loadTrainingData( testingFile, false );
                
         Iterator<LemmaWord> gutti = testari.iterator();
         int total=0;
         int correct=0;
         LemmaResult lemmaResult;      
         boolean success;
         
         int counter = 0;
         while( gutti.hasNext() /*&& total < 10000*/ ){   
             LemmaWord theWord = gutti.next();             
             counter++;
             String wordForm = theWord.getWordForm().toLowerCase();          
             lemmaResult = lemmatizer.lemmatize( wordForm, theWord.getTag() );
                    
             if( lemmaResult.getLemma().equals(theWord.getLemma())){
                correct++;    
                success = true;
             }
             else {
                success = false;
                if( lemmaResult.getMessage() == null ){
                    lemmaResult.setMessage("");
                }
                System.out.println( counter+": "+ theWord.getWordForm() + " : " + theWord.getTag() + " : " + lemmaResult.getLemma() + " "+ lemmaResult.getMessage() );
             }
             
             ArrayList<String> types = createTypes(theWord.getTag());
             for( int i=0; i<types.size() ;i++){
                this.countSuccess( types.get(i), success);
             }

             total++;
         }  
         
         if( total%1000 == 0){
              System.out.println("t:"+total);
         }
         System.out.println( correct + "/"+ total);    
         
         Iterator<String> typeIterator = successCounters.keySet().iterator();
         while( typeIterator.hasNext()){
            String type = typeIterator.next();
            SuccessCounter sc = successCounters.get(type);
            
            System.out.println( type + "\t" + sc.getCorrect() + "\t" + sc.getTotal() );
         }
          System.out.println( correct + "/"+ total + " (villur:"+(total-correct)+ "): " + ((double)correct/(double)total));                
    }
    
    private void countSuccess( String type, boolean success ){
        if( successCounters.containsKey(type) ){
            successCounters.get(type).increment( success );
        }
        else {
            SuccessCounter counter = new SuccessCounter();
            counter.increment( success );
            successCounters.put(type,counter);
        }
    }
    
    private ArrayList<String> createTypes( String tag ){
        ArrayList<String> ret = new ArrayList<String>();
        
        String preFix = LemmaldUtils.getWordClass( tag );
        ret.add( preFix );        
        
        if( preFix.equals("n") || preFix.equals("s") || preFix.equals("l") || preFix.equals("a")){            
            for( int i=1; i<tag.length();i++){
                ret.add( tag.substring(0,1)+i+tag.charAt(i));
            }
        }
        
        return ret;
    }
    
}
