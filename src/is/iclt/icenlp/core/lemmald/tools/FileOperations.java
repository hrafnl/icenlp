/*
 * FileOperations.java
 *
 * Created on 27. mars 2008, 17:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.tools;


import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.util.Scanner;

/**
 * General purpose methods for reading and writing files.
 * @author Anton
 */
public class FileOperations {
    
    /**
     * Writes a String into a file.
     * @param content The contents to be written to the file.
     * @param path The path the file should be written to.
     */
    static public void writeContents(String content, String path) {
        try {
            File outFile = new File(path);                        
            // FileWriter out = new FileWriter(outFile);            
            BufferedWriter out = FileEncoding.getWriter(path);
            out.write(content);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
                
    }
    
    /**
     * Returns the contents of a file at a given path as a String.
     * @param path The path of the file to read.
     * @return The contents of the file which was read.
     */
    
    static public String getContents(String path) {
        
        File aFile = new File(path);
        //...checks on aFile are elided
        StringBuffer contents = new StringBuffer();
        
        //declared here only to make visible to finally clause
        BufferedReader input = null;
        
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            // input = new BufferedReader( new FileReader(aFile) );
            input = FileEncoding.getReader(path);
            
            String line = null; //not declared within while loop
              /*
               * readLine is a bit quirky :
               * it returns the content of a line MINUS the newline.
               * it returns null only for the END of the stream.
               * it returns an empty String if two newlines appear in a row.
               */
    
            while (( line = input.readLine()) != null){
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                if (input!= null) {
                    //flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return contents.toString();
    }

    public static String gz2String( InputStream fis )
    {        
        StringBuffer output = new StringBuffer();        
     
    // Create a buffered gzip input stream to the archive file.
    GZIPInputStream gzip_in_stream;
    try {
      // FileInputStream in = fis;
      BufferedInputStream source = new BufferedInputStream (fis);
      gzip_in_stream = new GZIPInputStream(source);
      
      BufferedReader reader = FileEncoding.getReader(gzip_in_stream);
      
      String gutti;
      while( (gutti = reader.readLine()) != null ){
        output.append(gutti + System.getProperty("line.separator"));
      }

    }
    catch (IOException e) {

    }
 
        return output.toString();
    }
    
    public static String gz2StringOld(String filePath) 
    {
        StringBuffer output = new StringBuffer();
        
        
        try {
        
        // Fileinput -> unzippa� -> og b�i� til buffered input stream 
        GZIPInputStream stream =  new GZIPInputStream( new BufferedInputStream( new FileInputStream(filePath) ));
        
        // GZIPInputStream stream =  new GZIPInputStream( FileEncoding.getReader(new FileInputStream(filePath)).);
        Scanner scanner = new Scanner( stream );
        
        while( scanner.hasNextLine() ){
            output.append( scanner.nextLine() + "\n" );
        }
        
        } catch (Exception e ){
           System.out.println("Exception in gz2String:");
           e.printStackTrace();                   
        }
                
        return output.toString();
    }
    
    public static ArrayList<String> getFilePaths( String folderPath ){
        
        File rootFolder = new File( folderPath );
        
        File[] rootFiles = rootFolder.listFiles();
        ArrayList<String> filePaths=new ArrayList<String>();
        
        for( int i=0; i<rootFiles.length;i++ ) {
            String lFolderPath=rootFiles[i].getAbsolutePath();
            //l = local
            if(rootFiles[i].isDirectory()) {
                filePaths.addAll(getFilePaths(lFolderPath));
            } else if(rootFiles[i].isFile()) {
                filePaths.add(lFolderPath);
            }
        }
        
        return filePaths;
    }
    
    
    
}
