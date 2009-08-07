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
package is.iclt.icenlp.core.utils;

import java.util.ArrayList;
import java.io.*;

/**
 * A list of words.
 * @author Hrafn Loftsson
 */
public class WordList {
    private ArrayList words;

    public WordList(String filename) throws IOException
    {
        BufferedReader in = FileEncoding.getReader(filename);
        load(in);
        // Close the file.
        in.close();
    }

    public WordList( InputStream in ) throws IOException, NullPointerException
    {

        if( in == null )
            throw new NullPointerException( "InputStream was not initialized correctly (null)" );
        BufferedReader br = FileEncoding.getReader(in);

        load(br);
    }

    private void load(BufferedReader br) throws IOException {
        String s;
         words = new ArrayList();
        // Read a line at a time until the end of the file is reached.
        if (br == null)
            throw new NullPointerException( "BufferedReader in load() is null" );
        while( (s = br.readLine()) != null )
        {
            words.add(s);
        }
    }

    public ArrayList getWords()
    {
        return words;
    }
}
