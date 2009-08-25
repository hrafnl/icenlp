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

import org.w3c.dom.*;

/**
 * 
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class Ice1TagFormat extends TagFormat {

    private static final String SENTENCE_BOUNDARY = System.getProperty("line.separator"); 
    private static final String TOKEN_BOUNDARY = " ";
    private static final String VALUE_BOUNDARY = " ";

    private Ice1TagFormat(){}

    @Override
    public Document decode(String data) {
        final String[] sentences = data.split( SENTENCE_BOUNDARY );
        TaggedText taggedText = TaggedText.newInstance();
        
        for( String sentence : sentences ){
            TaggedSentence taggedSentence = taggedText.createSentence();
            final String[] tokens = sentence.split( TOKEN_BOUNDARY );            
            for( int i=0; i<tokens.length; i+=2 ){
                String word = tokens[i].trim();
                String tag = tokens[i+1].trim(); 
                taggedSentence.createToken(word,tag);
            }            
        }
        return taggedText.getDocument();        
    }

    @Override
    public String encode(Document data) {
        StringBuilder output = new StringBuilder();

        Element docRoot = data.getDocumentElement();
        NodeList docSentences = docRoot.getElementsByTagName("sentence");
        for( int i=0; i<docSentences.getLength(); i++ ){
            Element sentence = (Element) docSentences.item(i);
            NodeList docTokens = sentence.getElementsByTagName("token");
            for( int j=0; j<docTokens.getLength(); j++){
                Element docToken = (Element) docTokens.item(j);
                String word = docToken.getAttribute("word");
                String tag = docToken.getAttribute("tag");
                output.append( word + VALUE_BOUNDARY + tag  );
                if( j < docTokens.getLength()-1 ){
                    output.append(TOKEN_BOUNDARY);
                }
            }

            if( i < docSentences.getLength()-1 ){
                output.append( SENTENCE_BOUNDARY );
            }
        }

        return output.toString();
    }

    public static TagFormat newInstance(){
        return new Ice1TagFormat();
    }

    @Override
    public String sampleData() {
        return "Ég fp1en er sfg1en rauður lkensf kaktus nken"+System.getProperty("line.separator")+"og c ég fp1en ætla sfg1en að cn spila sng þetta faheo lag nheo . .";
    }

}
