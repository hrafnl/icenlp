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
package is.iclt.icenlp.core.tritagger;

/**
 * Entries in a frequency lexicon.
 * <br> Used by the FreqLexicoin
 * @author Hrafn Loftsson
 */
public class FreqLexEntry {
    private String tag;
    private int frequency;

    public FreqLexEntry(String tagStr, int freq)
    {
        tag = tagStr;
        frequency = freq;
    }

    public String getTag()
    {
        return tag;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void setFrequency(int freq)
    {
        frequency = freq;
    }
}
