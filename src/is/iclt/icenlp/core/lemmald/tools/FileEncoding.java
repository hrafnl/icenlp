/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.tools;

import java.io.*;

/**
 *
 * @author Notandi
 */
public class FileEncoding {

    public static final String ENCODING = "UTF-8";

    public static BufferedReader getReader(String filename) throws IOException {
           return new BufferedReader( new InputStreamReader(new FileInputStream(filename),ENCODING));
    }

    public static BufferedReader getReader(InputStream inStream) throws IOException
    {
        return new BufferedReader( new InputStreamReader(inStream, ENCODING));
    }

    public static BufferedWriter getWriter(String filename) throws IOException
    {
        return new BufferedWriter( new OutputStreamWriter(new FileOutputStream(filename),ENCODING));
    }

    public static BufferedWriter getWriter(OutputStream outStream) throws IOException
    {
        return new BufferedWriter(new OutputStreamWriter(outStream, ENCODING));
    }
}

