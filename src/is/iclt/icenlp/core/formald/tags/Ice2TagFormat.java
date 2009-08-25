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

package is.iclt.icenlp.core.formald.tags;

import java.util.List;
import org.w3c.dom.*;

/**
 * 
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class Ice2TagFormat extends TagFormat {

    private static final String SENTENCE_BOUNDARY = System.getProperty("line.separator") + System.getProperty("line.separator");
    private static final String TOKEN_BOUNDARY = System.getProperty("line.separator");
    private static final String VALUE_BOUNDARY = " ";

    private Ice2TagFormat(){}

    @Override
    public Document decode(String data) {

        final String[] sentences = data.split( SENTENCE_BOUNDARY );
        TaggedText taggedText = TaggedText.newInstance();
        for( String sentence : sentences ){
            TaggedSentence taggedSentence = taggedText.createSentence();
            final String[] tokens = sentence.split( TOKEN_BOUNDARY );
            for( String token : tokens ) {
                String word = null;
                String tag = null;
                String lemma = null;

                String[] tokenValues = token.split( VALUE_BOUNDARY );

                if( tokenValues.length == 2 ){
                    word = tokenValues[0].trim();
                    tag = tokenValues[1].trim();
                }
                else if (tokenValues.length == 3) {
                    word = tokenValues[0].trim();
                    lemma = tokenValues[1].trim();
                    tag = tokenValues[2].trim();
                }

                taggedSentence.createToken(word,tag,lemma);
            }

        }
        return taggedText.getDocument();

    }

    @Override
    public String encode(Document data) {
        StringBuilder output = new StringBuilder();
        TaggedText taggedText = TaggedText.newInstance(data);
        List<TaggedSentence> sentences = taggedText.getSentences();

        for( TaggedSentence sentence : sentences ){
            List<TaggedToken> tokens = sentence.getTokens();
            for( TaggedToken token : tokens ){
                output.append( token.getWord() );
                if( token.hasLemma() ){
                    output.append(VALUE_BOUNDARY +token.getLemma());
                }
                if( token.hasTag() ){
                    output.append(VALUE_BOUNDARY +token.getTag() );
                }
                output.append(TOKEN_BOUNDARY);
            }
            output.append( System.getProperty("line.separator")  );
        }


        return output.toString();
    }
    
    public static TagFormat newInstance(){
        return new Ice2TagFormat();
    }

    @Override
    public String sampleData() {
        return "Ég fp1en" + System.getProperty("line.separator") +
                "er sfg1en" + System.getProperty("line.separator") +
                "rauður lkensf" + System.getProperty("line.separator") +
                "kaktus nken" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "og c" + System.getProperty("line.separator") +
                "ég fp1en" + System.getProperty("line.separator") +
                "ætla sfg1en" + System.getProperty("line.separator") +
                "að cn" + System.getProperty("line.separator") +
                "spila sng" + System.getProperty("line.separator") +
                "þetta faheo" + System.getProperty("line.separator") +
                "lag nheo" + System.getProperty("line.separator") +
                ". .";
    }
}
