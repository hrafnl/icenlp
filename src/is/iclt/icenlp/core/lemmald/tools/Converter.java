/*
 * Converter.java
 *
 * Created on 29. mars 2008, 19:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.tools;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 *
 * @author Notandi
 */
public class Converter {
    
    /** Creates a new instance of Converter */
    public Converter() {
    }
      
    public static void otbToText( String otbFile, String textFile ){
        Hashtable<String,Integer> lines = new Hashtable<String,Integer>();
        
        String input = FileOperations.getContents( otbFile );
        StringTokenizer tok = new StringTokenizer( input, System.getProperty("line.separator") );
        String currentLine;
        StringBuilder outGutti = new StringBuilder();
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken().trim();
            String[] lineTokens = currentLine.split(" ");
            if( lineTokens.length == 3 ){
                outGutti.append( lineTokens[0] + " ");
            }
        }
        
        FileOperations.writeContents( outGutti.toString(), textFile );
   }
        
    public static void otbToFreq( String otbFile, String freqOutFile, String lemmaOutFile ){
        Hashtable<String,Integer> lines = new Hashtable<String,Integer>();
        
        String input = FileOperations.getContents( otbFile ).replace(" ","\t");
        StringTokenizer tok = new StringTokenizer( input, System.getProperty("line.separator") );
        String currentLine;
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken();
            if( lines.containsKey(currentLine) ){               
                lines.put( currentLine, new Integer(lines.get(currentLine).intValue()+1) );
            }
            else {
                lines.put( currentLine, new Integer(1) );
            }
        }        
        
        StringBuilder sb = new StringBuilder();
        StringBuilder lemmas = new StringBuilder();
        
        Iterator<String> lineIt = lines.keySet().iterator();         
        while( lineIt.hasNext() ){
            currentLine = lineIt.next();
            sb.append( lines.get(currentLine).intValue() + "\t" + currentLine + System.getProperty("line.separator") );
            lemmas.append( currentLine + System.getProperty("line.separator") );
        }
        
        FileOperations.writeContents( sb.toString(), freqOutFile );
        FileOperations.writeContents( lemmas.toString(), lemmaOutFile );

    }
    
    public static void evaluateCST(){
    
        String correctData = FileOperations.getContents("../cstlemma/lemmunarskrar/TNTL01P.txt"); //.replace( System.getProperty("line.separator")+System.getProperty("line.separator"), System.getProperty("line.separator")  );
        //String cstData = FileOperations.getContents("../cstlemma/out10kp.txt");
        String cstData = FileOperations.getContents("../cstlemma/lemmunarskrar/output1.txt");
        
        StringTokenizer tok = new StringTokenizer( correctData, System.getProperty("line.separator") );
        ArrayList<String> correct = new ArrayList<String>();
        String currentLine;
        String[] lineTokens;        
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken();
            lineTokens = currentLine.trim().split(" ");
            if( lineTokens.length == 3 ){
                correct.add( lineTokens[1] );
            }
        }        
        
        tok = new StringTokenizer( cstData, System.getProperty("line.separator") );
        ArrayList<String> cst = new ArrayList<String>();
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken();
            lineTokens = currentLine.trim().split("\t");
            if( lineTokens.length == 3 ){
                cst.add( lineTokens[1] );
            }
        }
        int total=0;
        int good=0;
        System.out.println("correct:" + correct.size() + " cst:"+cst.size());
        int len = cst.size();
        for( int i=0; i<len;i++){
            if( correct.get(i).toLowerCase().equals(cst.get(i).toLowerCase())){
               good++;
            }
            else {
                System.out.println( (i+1)+":"+correct.get(i).toLowerCase() + " - " + cst.get(i).toLowerCase() );               
            }
            total++;
        }
        System.out.println("Hlutfall: " + good +"/" + total + " (" + ((double)good/(double)total)  );
    }
    
    public static void evaluateIceLemma( String lemmaFile, String correctFile ){
        String correctData = FileOperations.getContents( correctFile ); //.replace( System.getProperty("line.separator")+System.getProperty("line.separator"), System.getProperty("line.separator")  );
        String iceData = FileOperations.getContents( lemmaFile );
        
        StringTokenizer tok = new StringTokenizer( correctData, System.getProperty("line.separator") );
        ArrayList<String> correct = new ArrayList<String>();
        String currentLine;
        String[] lineTokens;        
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken();
            lineTokens = currentLine.trim().split(" ");
            if( lineTokens.length == 3 ){
                correct.add( lineTokens[1] );
            }
        }        
        
        tok = new StringTokenizer( iceData, System.getProperty("line.separator") );
        ArrayList<String> ice = new ArrayList<String>();
        while( tok.hasMoreTokens() ){
            currentLine = tok.nextToken();
            lineTokens = currentLine.trim().split(" ");
            if( lineTokens.length == 3 ){
                ice.add( lineTokens[2] );
            }
        }
        int total=0;
        int good=0;
        System.out.println("correct:" + correct.size() + " ice:"+ice.size());
        int len = ice.size();
        for( int i=0; i<len;i++){
            if( correct.get(i).toLowerCase().equals(ice.get(i).toLowerCase())){
               good++;
            }
            else {
                System.out.println( (i+1)+":"+correct.get(i).toLowerCase() + " - " + ice.get(i).toLowerCase() );               
            }
            total++;
        }
        System.out.println("ICE Hlutfall: " + good +"/" + total + " (" + ((double)good/(double)total)  );
    }    
    
}
