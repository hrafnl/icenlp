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

import org.w3c.dom.Element;

/**
 * An interface that represents a tagged token. Implementations 
 * should provide getters and setters for word form, Part-of-Speech tag
 * and lemma (base form), as well as a reference to the parent sentence
 * of which the current token is part of.
 * <br /><br />
 * An instance will usually be created by a TaggedSentence during the
 * construction of a TaggedText object.
 * 
 * @see TaggedSentence
 * @see TaggedText
 * @author Anton Karl Ingason <anton.karl.ingason@gmail.com>
 */
public interface TaggedToken {
	
	/**
	 * The word form. This can also be punctuation or whatever other
	 * kind of a token that may appear in the text.
	 * @return The word form
	 */
    public String getWord();
    
    /**
     * The Part-of-Speech tag of the token
     * @return PoS-tag as String
     */
    public String getTag();
    
    /**
     * The lemma (base form) of the token. In IceNLP the lemma is
     * usually provided by the Lemmald lemmatizer. 
     * @return Lemma as String.
     */
    public String getLemma();
    
    /**
     * A reference to the parent sentence.
     * @return The sentence in which the word occurs.
     */
    public TaggedSentence getParentSentence();
    
    /**
     * A flag that says if a lemma has been set for the token. 
     * @return true if a lemma has been set, false otherwise.
     */
    public boolean hasLemma();
    
    /**
     * A flag that says if a tag has been set for the token.     
     * @return true if a tag has been set, false otherwise.
     */
    public boolean hasTag();

    public void setWord( String word );
    public void setTag( String tag );
    public void setLemma( String lemma );

    public Element element();
}
