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

import is.iclt.icenlp.core.formald.Text;
import is.iclt.icenlp.core.formald.tagsets.Tagset;
import is.iclt.icenlp.core.utils.FileOperations;
import is.iclt.icenlp.core.utils.XmlOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;
// import se.fishtank.css.selectors.dom.DOMNodeSelector;

/**
 * A central class that represents a TaggedText.
 * @author Anton Karl Ingason <anton.karl.ingason@gmail.com>
 */
public class TaggedText extends Text {

    private TagFormat tagFormat = null;
    private Tagset tagset = null;

    private TaggedText(){
        this( createEmptyDoc() );
    }    

    private TaggedText( Document document ){
        this( document, TagFormat.getDefault() );
    }

    private TaggedText( Document document, Tagset tagset ){
        this( document, TagFormat.getDefault(), tagset );
    }

    private TaggedText( Document document, TagFormat tagFormat ){
        this( document, tagFormat, Tagset.getDefault() );
    }

    private TaggedText( Document document, TagFormat tagFormat, Tagset tagset ){
        super( document );
        this.setTagFormat( tagFormat );
        this.setTagset( tagset );
    }
    
    public static TaggedText newInstance(){        
        return new TaggedText();
    }

    public static TaggedText newInstance( Tagset tagset ){
        return new TaggedText( createEmptyDoc(), tagset );
    }

    public static TaggedText newInstance( Document document ){
        return new TaggedText( document );
    }

    public static TaggedText newInstance( Document document, Tagset tagset ){
        return new TaggedText( document, tagset );
    }

    public static TaggedText newInstance( final String data, final TagFormat format ){
        return new TaggedText( format.decode(data), format );
    }

    public static TaggedText newInstanceFromFile( File file, final TagFormat format ){
        String data = FileOperations.fileToString( file.getAbsolutePath() );
        return newInstance( data, format );
    }


    public static Document createEmptyDoc(){
        Document doc = XmlOperations.createDocument();
        Element docRoot = doc.createElement("taggedText");
        doc.appendChild(docRoot);
        return doc;
    }

    public TagFormat getTagFormat(){
        return tagFormat;
    }

    public void setTagFormat( TagFormat tagFormat ){
        this.tagFormat = tagFormat;
    }

    public Tagset getTagset(){
        return tagset;
    }

    public void setTagset( Tagset tagset ){
        this.tagset = tagset;
    }

    public boolean isDefaultTagset(){
        return (this.tagset == null);
    }

    public void addSentence( TaggedSentence sentence ){
        this.getDocument().getDocumentElement().appendChild( sentence.element() );
    }

    public void removeSentence( TaggedSentence sentence ){
        this.getDocument().getDocumentElement().removeChild( sentence.element() );        
    }

    public TaggedSentence createSentence(){
        TaggedSentence sentence = TaggedSentenceImpl.newInstance( this.getDocument().createElement("sentence"), this );
        this.addSentence(sentence);
        return sentence;
    }

    /*
    public List<TaggedSentence> runQuery( String query ){

        ArrayList<TaggedSentence> sentences = new ArrayList<TaggedSentence>();

        DOMNodeSelector selector = null;
        Set<Node> results = null;

        try {
            selector = new DOMNodeSelector( this.getDocument() );
            results = selector.querySelectorAll( query );
        }
        catch( Exception ex ){
            ex.printStackTrace();
        }

        for( Node node : results ){
            sentences.add( TaggedSentenceImpl.newInstance( (Element) node.getParentNode(), this ) );
            //System.out.println( ((Element) node).getAttribute("word") );
        }
        return sentences;
         
        //return null;
    }
    */

    public int getSentenceCount(){
        return this.getDocument().getElementsByTagName("sentence").getLength();
    }

    public TaggedSentence getSentence( int index ){        
        return TaggedSentenceImpl.newInstance( (Element) this.getDocument().getElementsByTagName("sentence").item(index), this );
    }

    public List<TaggedSentence> getSentences(){
        ArrayList<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>();
        NodeList sentences = this.getDocument().getElementsByTagName("sentence");
        for( int i=0; i<sentences.getLength(); i++){
           taggedSentences.add( TaggedSentenceImpl.newInstance( (Element) sentences.item(i), this ) );
        }
        return taggedSentences;
    }

    public List<TaggedToken> getTokens(){
        ArrayList<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();
        List<TaggedSentence> sentences = this.getSentences();
        for( TaggedSentence sentence : sentences ){
            List<TaggedToken> tokens = sentence.getTokens();
            for( TaggedToken token : tokens ){
                taggedTokens.add(token);
            }
        }
        return taggedTokens;
    }

    @Override
    public String toString(){        
        return XmlOperations.docToString( this.getDocument() );
    }

    public String toString( final TagFormat outputFormat ){
        return outputFormat.encode( this.getDocument() );
    }
    
}
class TaggedSentenceImpl implements TaggedSentence {
    
    private Element sentence = null;
    private TaggedText parentText = null;

    private TaggedSentenceImpl( Element sentence, TaggedText parentText ){
        this.sentence = sentence;
        this.parentText = parentText;
    }

    public void addToken( TaggedToken token ){
        sentence.appendChild( token.element() );        
    }
    
    public TaggedToken createToken( String word ){
        return createToken( word, null, null );
    }

    public TaggedToken createToken( String word, String tag ){
        return createToken( word, tag, null );
    }

    public TaggedToken createToken( String word, String tag, String lemma ){
        TaggedToken token = TaggedTokenImpl.newInstance( parentText.getDocument().createElement("token"), this );
        token.setWord(word);
        token.setTag(tag);
        token.setLemma(lemma);
        this.addToken(token);
        return token;
    }

    public List<TaggedToken> getTokens() {
        ArrayList<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();
        NodeList tokens = sentence.getElementsByTagName("token");
        for( int i=0; i<tokens.getLength(); i++){
            taggedTokens.add( TaggedTokenImpl.newInstance( (Element) tokens.item(i), this ) );
        }
        return taggedTokens;
    }

    public static TaggedSentence newInstance( Element sentence, TaggedText parentText ){
        return new TaggedSentenceImpl( sentence, parentText );
    }

    public int getTokenCount() {
        return sentence.getElementsByTagName("token").getLength();
    }

    public TaggedToken getToken(int index) {
        return TaggedTokenImpl.newInstance( (Element) sentence.getElementsByTagName("token").item(index), this );
    }

    public TaggedText getParentText(){
        return parentText;
    }

    public Element element(){
        return sentence;
    }
    
}
class TaggedTokenImpl implements TaggedToken {

    private Element token;
    private TaggedSentence parentSentence;
    
    private TaggedTokenImpl( Element token, TaggedSentence parentSentence ){
        this.token = token;
        this.parentSentence = parentSentence;
    }

    public String getWord() {        
        if( this.hasWord() ){
            return ((Element) token).getAttribute("word");
        }
        else {
            return null;
        }
    }

    public String getTag() {
        if( this.hasTag() ){
            String theTag = token.getAttribute("tag");
            if( ! this.getParentSentence().getParentText().isDefaultTagset() ){
                theTag = this.getParentSentence().getParentText().getTagset().getTag(theTag);
            }
            return theTag;
        }
        else {
            return null;
        }
    }

    public String getLemma() {
        if( this.hasLemma() ){
            return token.getAttribute("lemma");
        }
        else {
            return null;
        }        
    }

    public TaggedSentence getParentSentence(){
        return parentSentence;
    }
    
    private boolean hasWord(){
        return this.element().hasAttribute("word");
    }

    public boolean hasTag(){
        return this.element().hasAttribute("tag");
    }

    public boolean hasLemma(){
        return this.element().hasAttribute("lemma");
    }

    public void setWord(String word) {
        if( word != null){
            this.element().setAttribute("word", word);
        }
    }

    public void setTag(String tag) {
        if( tag != null ){
            String theTag = tag;
            if( ! this.getParentSentence().getParentText().isDefaultTagset() ){
                this.getParentSentence().getParentText().getTagset().getTag(tag);
            }
            this.element().setAttribute("tag", theTag);
        }
    }

    public void setLemma(String lemma) {
        if( lemma != null ){
            this.element().setAttribute("lemma", lemma);
        }
    }

    public static TaggedToken newInstance( Element token, TaggedSentence parentSentence ){
        return new TaggedTokenImpl( token, parentSentence );
    }

    public Element element(){
        return (Element) token;
    }
    
}

