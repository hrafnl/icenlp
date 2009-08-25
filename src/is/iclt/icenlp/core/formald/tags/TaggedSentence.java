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
import org.w3c.dom.Element;

/**
 *
 * @author Anton Karl Ingason <anton.karl.ingason@gmail.com>
 */
public interface TaggedSentence {
    public List<TaggedToken> getTokens();
    public int getTokenCount();
    public TaggedToken getToken( int index );
    public TaggedText getParentText();

    public void addToken( TaggedToken token );

    public TaggedToken createToken( String word );
    public TaggedToken createToken( String word, String tag );
    public TaggedToken createToken( String word, String tag, String lemma );
    public Element element();
}
