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

import java.io.*;

/**
 * A class for reading and writing files in UTF-8
 * @author Hrafn Loftsson
 */
public class FileEncoding {

    public static final String ENCODING = "UTF-8";

    public static BufferedReader getReader(String filename) throws IOException {
           return new BufferedReader(
                  new InputStreamReader(new FileInputStream(filename), ENCODING));
    }

    public static BufferedReader getReader(InputStream inStream) throws IOException
    {
        return new BufferedReader(
                new InputStreamReader(inStream, ENCODING));
    }

    public static BufferedWriter getWriter(String filename) throws IOException
    {
        return new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), ENCODING));
    }

    public static BufferedWriter getWriter(OutputStream outStream) throws IOException
    {
        return new BufferedWriter(new OutputStreamWriter(outStream, ENCODING));
    }
}


