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

/**
 *
 * @author Anton
 */
public class LemmaResult {

    private String lemma;
    private String message;
    private boolean success;
    private String tag;
    private String wordForm;
    
    /** Creates a new instance of LemmaResult */
    public LemmaResult( String lemma, String message, boolean success ) {
        this.setLemma(lemma);
        this.setMessage(message);
        this.setSuccess(success);
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }
    
    public String getWordId(){
        if( tag.startsWith("n") ){
            if( tag.length() == 6 ){
                return lemma+"_"+tag.substring(0,2)+"s";
            }            
            return lemma+"_"+tag.substring(0,2);
        }
        if( tag.startsWith("s")){
            return lemma+"_s";
        }
        if( tag.startsWith("l")){
            return lemma+"_l";
        }
        if( tag.startsWith("f")){
            return lemma+"_f";
        }
        if( tag.startsWith("c")){
            return lemma+"_"+tag;
        }       
        if( tag.startsWith("a")){         
            return lemma+"_"+tag;       
        }
        if( tag.startsWith("t")){
            return lemma+"_t";
        }
        
        return lemma + "_x";
    }
    
    public void setTag( String tag ){
        this.tag = tag;
    }
    
    public String getTag(){
        return tag;
    }
    
    public void setWordForm( String wordForm ){
        this.wordForm = wordForm;
    }

    public String getWordForm(){
        return wordForm;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
}
