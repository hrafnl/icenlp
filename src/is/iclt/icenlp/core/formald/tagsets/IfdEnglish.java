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

package is.iclt.icenlp.core.formald.tagsets;

import java.util.HashMap;

/**
 * In progress, a class that represents an English version of the IFD tagset.
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class IfdEnglish {

    HashMap<String,String> tagmap = new HashMap<String,String>();
    HashMap<String,String> postmap = new HashMap<String,String>();

    public String getTag( String standardTag ){
        String theTag = this.getMapping( standardTag );


        // lh.þt.
        if( theTag.charAt(0) == 'v' && theTag.charAt(2) == 'd' ){            
            if( theTag.charAt(6) == 'p' ){
                theTag = theTag.substring(0,6) + "n";
            }
        }

        // indefinite common nouns
        if( theTag.charAt(0) == 'n' && theTag.length() == 5){
            theTag+="i";
        }

        // common nouns (as opposed to proper)
        if( theTag.charAt(0) == 'n' && theTag.length() == 6){
            theTag+="c";
        }


        if( postmap.containsKey(theTag)){
            theTag = postmap.get(theTag);
        }


        return theTag.toUpperCase();
    }


    public String getMapping( String standardTag ){
        if( standardTag == null ){
            return null;
        }
        if( standardTag.length() == 0 ){
            return null;
        }

        if( ! tagmap.containsKey( standardTag.charAt(0)+"1"+standardTag.charAt(0) ) ){
            return standardTag;
        }

        String theTag = "";
        theTag += tagmap.get( standardTag.charAt(0)+"1"+standardTag.charAt(0) );

        if( standardTag.length() > 1 ){
            theTag += "-";
        }

        for( int i=1; i<standardTag.length(); i++ ){
          String stdChar = ""+standardTag.charAt(0)+(i+1)+ standardTag.charAt(i);
          theTag += tagmap.get( stdChar );
        }

        return theTag;
    }

    public void loadPostmap(){
        postmap.put("adv-n", "adv");
        postmap.put("adv-a", "p-a");
        postmap.put("adv-d", "p-d");
        postmap.put("adv-g", "p-g");
    }

    public IfdEnglish(){
        this.loadTagmaps();
        this.loadPostmap();
    }

    public void loadTagmaps() {
        //
        tagmap.put(".1.", ";");
        tagmap.put("!1!", ";");
        tagmap.put("?1?", ";");

        // noun
        tagmap.put("n1n", "n");

        // noun gender
        tagmap.put("n2k", "m");
        tagmap.put("n2v", "f");
        tagmap.put("n2h", "n");
        tagmap.put("n2x", "x");

        // noun number
        tagmap.put("n3e", "s");
        tagmap.put("n3f", "p");

        // noun case
        tagmap.put("n4n", "n");
        tagmap.put("n4o", "a");
        tagmap.put("n4þ", "d");
        tagmap.put("n4e", "g");

        // noun suffixed article
        tagmap.put("n5g", "d");
        tagmap.put("n5-", "i");

        // noun proper name class
        tagmap.put("n6m", "p");
        tagmap.put("n6ö", "l");
        tagmap.put("n6s", "o");

        // adjective
        tagmap.put("l1l", "adj");

        // adjective gender
        tagmap.put("l2k", "m");
        tagmap.put("l2v", "f");
        tagmap.put("l2h", "n");


        // adjective number
        tagmap.put("l3e", "s");
        tagmap.put("l3f", "p");

        // adjective case
        tagmap.put("l4n", "n");
        tagmap.put("l4o", "a");
        tagmap.put("l4þ", "d");
        tagmap.put("l4e", "g");

        // adjective declension
        tagmap.put("l5s", "s");
        tagmap.put("l5v", "w");
        tagmap.put("l5o", "x");
        

        // adjective degree
        tagmap.put("l6f", "p");
        tagmap.put("l6m", "c");
        tagmap.put("l6e", "s");
        
        // pronoun
        tagmap.put("f1f", "pro");

        // pronoun subclass
        tagmap.put("f2a", "d"); // demonstrative p
        tagmap.put("f2b", "b"); // óákveðið ábendingarfn.
        tagmap.put("f2e", "q"); // possessive p
        tagmap.put("f2o", "x"); // "óákveðið"
        tagmap.put("f2p", "p"); // personal p
        tagmap.put("f2s", "w"); // interrogative p
        tagmap.put("f2t", "r"); // relative

        // pronoun gender / person
        tagmap.put("f3k", "m");
        tagmap.put("f3v", "f");
        tagmap.put("f3h", "n");
        tagmap.put("f31", "1");
        tagmap.put("f32", "2");

        // pronoun number
        tagmap.put("f4e", "s");
        tagmap.put("f4f", "p");

        // pronoun case
        tagmap.put("f5n", "n");
        tagmap.put("f5o", "a");
        tagmap.put("f5þ", "d");
        tagmap.put("f5e", "g");

        // article (determiner?)
        tagmap.put("g1g", "d");
        
        // article gender
        tagmap.put("g2k", "m");
        tagmap.put("g2v", "f");
        tagmap.put("g2h", "n");

        // article number
        tagmap.put("g3e", "s");
        tagmap.put("g3f", "p");

        // article case
        tagmap.put("g4n", "n");
        tagmap.put("g4o", "a");
        tagmap.put("g4þ", "d");
        tagmap.put("g4e", "g");

        // numeral
        tagmap.put("t1t", "num");

        // numeral subtype
        tagmap.put("t2f", "p"); // frumtala, prime number
        tagmap.put("t2p", "f"); // prósenta, fraction/percentage
        tagmap.put("t2a", "o"); // (kemur ekki fyrir í sturlunguskrá?) annað, other kind of number
        tagmap.put("t2o", "o"); // annað, other kind of number

        // numeral gender
        tagmap.put("t3k", "m");
        tagmap.put("t3v", "f");
        tagmap.put("t3h", "n");

        // numeral number
        tagmap.put("t4e", "s");
        tagmap.put("t4f", "p");

        // numeral case
        tagmap.put("t5n", "n");
        tagmap.put("t5o", "a");
        tagmap.put("t5þ", "d");
        tagmap.put("t5e", "g");

        // verb
        tagmap.put("s1s", "v");

        // verb mood
        tagmap.put("s2n", "t"); // infinitive
        tagmap.put("s2b", "m");  // boðháttur, imperative
        tagmap.put("s2f", "i"); // indicative
        tagmap.put("s2v", "s"); // subjunctive
        tagmap.put("s2s", "u"); // sagnbót, supine
        tagmap.put("s2l", "p"); //  lýsingarháttur nútíðar, present participle
        tagmap.put("s2þ", "d"); //  lýsingarháttur þátíðar, past participle

        // verb voice
        tagmap.put("s3g", "a"); // active
        tagmap.put("s3m", "m"); // middle

        // verb person
        tagmap.put("s41", "1");
        tagmap.put("s42", "2");
        tagmap.put("s43", "3");

        tagmap.put("s4-", "_");

        // verb number
        tagmap.put("s5e", "s"); // singular
        tagmap.put("s5f", "p"); // plural

        tagmap.put("s5-", "_");
        
        // verb tense
        tagmap.put("s6n", "p"); // present tense
        tagmap.put("s6þ", "d"); // past tense


        // verb, past part, gender
        tagmap.put("s4k", "m");
        tagmap.put("s4v", "f");
        tagmap.put("s4h", "n");

        // verb, past part., case
        // tagmap.put("s6n", "p");
        tagmap.put("s6o", "a");

        // adverb
        tagmap.put("a1a", "adv");

        // adverb case/class
        tagmap.put("a2u", "i"); // upphrópun, interjection
        tagmap.put("a2a", "n");
        tagmap.put("a2o", "a");
        tagmap.put("a2þ", "d");
        tagmap.put("a2e", "g");

        // adverb degree
        tagmap.put("a3m", "c");
        tagmap.put("a3e", "s");

        // conjunction
        tagmap.put("c1c", "c");

        tagmap.put("c2n", "i"); // infinitival marker
        tagmap.put("c2t", "r"); // tilvísunartenging, relative conjunction

        // foreign word
        tagmap.put("e1e", "foreign");

        // ógreint orð
        tagmap.put("x1x", "x");

    }

}
