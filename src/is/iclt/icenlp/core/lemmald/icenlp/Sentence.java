/*
 * Sentence.java
 *
 * Created on 27. mars 2008, 17:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.icenlp;
import java.util.ArrayList;

public class Sentence extends ArrayList<Word> {

	public Sentence(){
		super();
	}
        
        public Word getWord(int i)
        {
            return this.get(i);
        }

        
        public String getText()
        {
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<this.size();i++){
                sb.append(this.get(i).getWord());
                sb.append(" ");
            }
            return sb.toString().trim();
        }
        
        public boolean isOk(){
            
            String lagstafir="a-z��������";//���ir l�gstafir en a�eins inn� []
            String hastafir="A-Z��������";

            String texti = this.getText();

            if( texti.contains("......")){
                return false;
            }
            
            if( texti.contains("- - - - -")){
                return false;
            }
            
            String check=texti.replaceAll("[0-9.\\s]","");//henda t�lum, punkti og bilum, tab,enter etc.
            if(check.length()==0)//ef inniheldur bara t�lur punkta og whitespaces �misskonar
            {
               return false;
            }
            
            // If a char sequence of uppercase chars and spaces is found and is at least 10 chars, reject sentence
            if( texti.length()>texti.replaceAll("["+hastafir+" ]{10,}","").length()){
              //  System.out.println( "H�st: "+ texti );                
                return false;
            }
            
            for(int i=0;i<this.size();i++){
                if( this.get(i).getWord().length() > 32 ){
                    return false;
                }
            }

            return true;
        }
        
        public String getTagString()
        {
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<this.size();i++){
                sb.append(this.get(i).getTag().charAt(0));
            }
            return sb.toString().trim();
        }


}
