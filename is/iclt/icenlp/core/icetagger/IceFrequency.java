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
package is.iclt.icenlp.core.icetagger;
import is.iclt.icenlp.core.utils.Lexicon;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads and stores frequency information for tags.
 * @author Hrafn Loftsson
 */
public class IceFrequency extends Lexicon {

    public IceFrequency(String freqFile)
    throws IOException
    {
        super(freqFile);
    }

    public IceFrequency( InputStream in ) throws IOException, NullPointerException
    {
        super(in);
    }

    public int getFrequency(String key)
    {
        String value = lookup(key, false);
        if (value != null)
            return (Integer.parseInt(value));
        else
            return 0;
    }

    public String maxFrequency(String[] keys)
    {
        int maxFreq = 0;
        String maxKey = null;
        for (int i=0; i<keys.length; i++)       // find the key with the highest frequency
        {
           String key = keys[i];
           if (key.matches(".+<.>"))    // t.d. sng<v>
           {
               key = key.replaceFirst("<.>","");
           }
           int freq = getFrequency(key);   // get the frequency
           if (freq > maxFreq)
           {
               maxFreq = freq;
               maxKey = key;
            }
        }
        return maxKey;
    }
}
