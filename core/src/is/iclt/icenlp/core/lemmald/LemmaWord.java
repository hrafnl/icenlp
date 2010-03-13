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
public class LemmaWord {
    
    private String wordForm;
    private String lemma;
    private String tag;
    
    /** Creates a new instance of LemmaWord */
    public LemmaWord( String wordForm, String lemma, String tag ) {
        this.setWordForm(wordForm);
        this.setLemma(lemma);
        this.setTag(tag);
    }

    public String getWordForm() {
        return wordForm;
    }

    public void setWordForm(String wordForm) {
        this.wordForm = wordForm.toLowerCase();
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma.toLowerCase();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }    
    
    public boolean equals( Object o ){
        LemmaWord other = (LemmaWord) o;
        if( this.getWordForm().equals(other.getWordForm()) && 
                this.getLemma().equals(other.getLemma()) && 
                this.getTag().equals(other.getTag())){
                return true;
        }
        return false;
    }
    
    public String toString(){
        return "("+this.getWordForm()+","+this.getLemma()+","+this.getTag()+")";
    }
}
