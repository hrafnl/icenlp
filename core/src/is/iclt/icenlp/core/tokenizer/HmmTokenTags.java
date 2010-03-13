/*
 * Copyright (C) 2009 Hrafn Loftsson
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
 * Hrafn Loftsson, School of Computer Science, Reykjavik University.
 * hrafn@ru.is
 */
package is.iclt.icenlp.core.tokenizer;
/**
 * Tokens used by a HMM tagger
 * @author Hrafn Loftsson
 */
public class HmmTokenTags extends TokenTags
{
    protected String cardinalKey;     // A string used as a key for lexicon lookup for this unknown cardinals
    protected int suffixLength;       // The length of the suffix used for lookup for this unknown token
    //protected double probSuffix;      // The probability of the suffix of length suffixLength
    protected boolean morpho;         // True if tags for this token came from a morphological analyzer

    public HmmTokenTags()
    {
        super();
        cardinalKey = null;
        suffixLength = 0;
        //probSuffix = 0.0;
        morpho = false;
    }

    public HmmTokenTags(String str, TokenCode tc)
    {
        super(str, tc);
        cardinalKey = null;
        suffixLength = 0;
        //probSuffix = 0.0;
        morpho = false;
    }


    public boolean isCardinal()
    {
        return (cardinalKey != null);
    }

    public String getCardinalKey()
    {
        return cardinalKey;
    }

    public int getSuffixLength()
    {
        return suffixLength;
    }

    /*public double getProbSuffix()
    {
        return probSuffix;
    } */

    public boolean isMorpho()
    {
        return morpho;
    }

    public void setCardinalKey(String key)
    {
        cardinalKey = key;
    }

    public void setSuffixLength(int len)
    {
        suffixLength = len;
    }

    /*public void setProbSuffix(double prob)
    {
        probSuffix = prob;
    } */

    public void setMorpho(boolean flag)
    {
        morpho = flag;
    }



}
