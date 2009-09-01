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

package is.iclt.icenlp.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to recursively get a list of all files in a directory. 
 * An optional suffix filter can be provided.
 * <br /><br />
 * Usage: <br />
 * <code>List&lt;File&gt; myFiles =  FileLister.create( new File("/path/to/dir/") );</code><br />
 * <code>List&lt;File&gt; myFiles =  FileLister.create( new File("/path/to/dir/",".txt") );</code><br />
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class FileLister {

    private ArrayList<File> files;

    private FileLister( File dir, String suffix ){
        files = new ArrayList<File>();
        visitAllFiles( dir, suffix );
    }

    // Process only files under dir
    private void visitAllFiles(File dir, String suffix ) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                visitAllFiles(new File(dir, children[i]), suffix);
            }
        } else {
            if( suffix == null || dir.getName().endsWith("."+suffix) ){
                files.add(dir);
            }
        }
    }

    /**
     * Returns the files of the directory passed to <code>create</code>.
     * @return List of Files of the selected directory
     */
    public List<File> getFiles(){
        return files;
    }

    /**
     * Constructs a FileLister for the provided directory.
     * @param dir The directory for which to list files.
     * @return A <code>FileLister</code> object that can return the list of files.
     */
    public static List<File> create( File dir ){
        return new FileLister( dir, null ).getFiles();
    }
    
    /**
     * Constructs a FileLister for the provided directory.
     * Only files that match the supplied suffix are returned.
     * @param dir The directory for which to list files.
     * @param suffix The suffix used to filter files, e.g. <code>".txt"</code>
     * @return A <code>FileLister</code> object that can return the list of files.
     */
    public static List<File> create( File dir, String suffix ){
        return new FileLister( dir, suffix ).getFiles();
    }

}