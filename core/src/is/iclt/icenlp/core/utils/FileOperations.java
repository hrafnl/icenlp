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


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Various static methods for dealing with files.
 * @author Anton Karl Ingason
 */
public class FileOperations {
	
    /**
     * Reads a file to a String. Complains to system out and prints stack trace
     * if an IOException occurs.
     * @param filename Name of the file to be read.
     * @return The content of the file as a String.
     */

    public static String fileToString( String filename ){
        String contents = null;
        try {
            //contents = FileUtils.readFileToString(new File(filename));
            contents = FileUtils.readFileToString(new File(filename), FileEncoding.ENCODING);
        } catch (IOException ex) {
            System.out.println("Could not read file '"+filename+"'!");
            ex.printStackTrace();
        }
        return contents;
    }
      
	/**
	 * Writes a String to a file. Complains to system out and prints stack trace 
	 * if an IOException occurs.
	 * @param filename Name of the file to be written.
	 * @param data The String data that will be written to the file.
	 */
    public static void stringToFile( String filename, String data ){
        try {
            //FileUtils.writeStringToFile(new File(filename), data);
            FileUtils.writeStringToFile(new File(filename), data, FileEncoding.ENCODING);
        } catch (IOException ex) {
            System.out.println("Could not write to file '"+filename+"'!");
            ex.printStackTrace();
        }
    }
   

	public static ArrayList<String> getFilePaths(String folderPath) {

		File rootFolder = new File(folderPath);

		File[] rootFiles = rootFolder.listFiles();
		ArrayList<String> filePaths = new ArrayList<String>();

		for (int i = 0; i < rootFiles.length; i++) {
			String lFolderPath = rootFiles[i].getAbsolutePath();
			// l = local
			if (rootFiles[i].isDirectory()) {
				filePaths.addAll(getFilePaths(lFolderPath));
			} else if (rootFiles[i].isFile()) {
				filePaths.add(lFolderPath);
			}
		}

		return filePaths;
	}
	

}
