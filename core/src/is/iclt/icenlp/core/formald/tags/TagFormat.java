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

import is.iclt.icenlp.core.formald.Format;

/**
 * An abstract class that represents a <b>file format for tagged text</b> (non-parsed) where each
 * token (usually a word) can be assigned a Part-Of-Speech tag and a lemma
 * (base form). No phrase structure can be included but a text is made up of
 * sentences which are made up of tagged tokens.
 * <br /><br />
 * Instances of built-in formats are provided as static constants.
 * Custom formats can be handled by subclassing the current class.
 * 
 * @see TaggedText
 * @see TaggedSentence
 * @see TaggedToken
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public abstract class TagFormat extends Format {
	
    public static final TagFormat XML = XMLTagFormat.newInstance();
    public static final TagFormat ICE1 = Ice1TagFormat.newInstance();
    public static final TagFormat ICE2 = Ice2TagFormat.newInstance();

    /**
     * Get the default format for tagged text in IceNLP.
     * @return The default format (ICE2).
     * @see Ice2TagFormat
     */
    public static TagFormat getDefault(){
        return ICE2;
    }
 
}
