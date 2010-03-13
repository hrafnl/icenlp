// "Java Tech"
//  Code provided with book for educational purposes only.
//  No warranty or guarantee implied.
//  This code freely available. No copyright claimed.
//  2003
//  (minor changes by Anton Karl Ingason 2009, same conditions as above)
//

package is.iclt.icenlp.core.utils;


import java.io.*;
import java.util.Scanner;
import java.util.zip.*;

/**
 * A class for zipping and unzipping
 */
public class ZipGzipper {

  public static final int BUF_SIZE = 8192;

  public static final int STATUS_OK          = 0;
  public static final int STATUS_OUT_FAIL    = 1; // No output stream.
  public static final int STATUS_ZIP_FAIL    = 2; // No zipped file
  public static final int STATUS_GZIP_FAIL   = 3; // No gzipped file
  public static final int STATUS_IN_FAIL     = 4; // No input stream.
  public static final int STATUS_UNZIP_FAIL  = 5; // No decompressed zip file
  public static final int STATUS_GUNZIP_FAIL = 6; // No decompressed gzip file

  private static String fMessages [] = {
    "Operation succeeded",
    "Failed to create output stream",
    "Failed to create zipped file",
    "Failed to create gzipped file",
    "Failed to open input stream",
    "Failed to decompress zip file",
    "Failed to decompress gzip file"
  };

  /** Return a brief message for each status number. **/
  public static String getStatusMessage (int msg_number) {
    return fMessages [msg_number];
  }

  /**
    * Zip the input file and send the zip archive to the output directory.
    * This method only packs one file into the archive.
   **/
  public static int zipFile (File file_input, File dir_output) {

    // Create the output zip archive from the name of the input file
    // and the output directory. Add the ".zip" file type at the end.
    File zip_output = new File (dir_output, file_input.getName () + ".zip");

    // Create a buffered zip output stream to the archive.
    ZipOutputStream zip_out_stream;
    try {
      FileOutputStream out = new FileOutputStream (zip_output);
      zip_out_stream = new ZipOutputStream (new BufferedOutputStream (out));
    }
    catch (IOException e) {
      return STATUS_OUT_FAIL;
    }

    // Now compress the file using the ZIP output stream.
    // Need to use a buffer to read the file.
    byte[] input_buffer = new byte[BUF_SIZE];
    int len = 0;
    try {
      // Use the file name for the ZipEntry name.
      ZipEntry zip_entry = new ZipEntry (file_input.getName ());
      zip_out_stream.putNextEntry (zip_entry);

      // Create a buffered input stream from the file stream.
      FileInputStream in = new FileInputStream (file_input);
      BufferedInputStream source = new BufferedInputStream (in, BUF_SIZE);

      // Now read in the date from the file and write via the zip stream
      // which will compress the data before writing it to the archive 
      // output.
      while ((len = source.read (input_buffer, 0, BUF_SIZE)) != -1)
        zip_out_stream.write (input_buffer, 0, len);
      in.close ();
    }
    catch (IOException e) {
      return STATUS_ZIP_FAIL;
    }

    // Close up the output file
    try {
      zip_out_stream.close ();
    }
    catch (IOException e) {}

    return STATUS_OK;

  } // zipFile


  /**
    *  Gzip the input file to an archive with the same name except for
    *  ".gz" appended to it. The archive will be in the chosen output 
    *  directory.
   **/
  public static int gzipFile (File file_input, String file_output) {

    // Create the output gzip archive from the name of the input file
    // and the output directory. Add the ".gz" file type at the end.
    File gzip_output = new File (file_output);

    // Create a buffered gzip output stream to the archive.
    GZIPOutputStream gzip_out_stream;
    try {
      FileOutputStream out = new FileOutputStream (gzip_output);
      gzip_out_stream = 
        new GZIPOutputStream (new BufferedOutputStream (out));
    }
    catch (IOException e) {
      return STATUS_OUT_FAIL;
    }

    // Now compress the file using the GZIP output stream.
    // Need to use a buffer to read the file.
    byte[] input_buffer = new byte[BUF_SIZE];
    int len = 0;
    try {

      // Create a buffered stream from the input file.
      FileInputStream in = new FileInputStream (file_input);
      BufferedInputStream source = new BufferedInputStream (in, BUF_SIZE);

      // Read from the input stream and write to the gzip output stream
      // which will compress the output before writing to the output file.
      while ((len = source.read (input_buffer, 0, BUF_SIZE)) != -1)
        gzip_out_stream.write (input_buffer, 0, len);
      in.close ();
    }
    catch (IOException e) {
      return STATUS_GZIP_FAIL;
    }

    // Close up the output file
    try {
      gzip_out_stream.close ();
    }
    catch (IOException e) {}

    return STATUS_OK;
  } // gzipFile


  /**
    *  Unzip the files from a zip archive into the given output directory.
    *  It is assumed the archive file ends in ".zip".
   **/
  public static int unzipFile (File file_input, File dir_output) {

    // Create a buffered zip stream to the archive file input.
    ZipInputStream zip_in_stream;
    try {
      FileInputStream in = new FileInputStream (file_input);
      BufferedInputStream source = new BufferedInputStream (in);
      zip_in_stream = new ZipInputStream (source);
    }
    catch (IOException e) {
      return STATUS_IN_FAIL;
    }

    // Need a buffer for reading from the input file.
    byte[] input_buffer = new byte[BUF_SIZE];
    int len = 0;

    // Loop through the entries in the ZIP archive and read
    // each compressed file.
    do {
      try {
        // Need to read the ZipEntry for each file in the archive
        ZipEntry zip_entry = zip_in_stream.getNextEntry ();
        if (zip_entry == null) break;

        // Use the ZipEntry name as that of the compressed file.
        File output_file = new File (dir_output, zip_entry.getName ());

        // Create a buffered output stream.
        FileOutputStream out = new FileOutputStream (output_file);
        BufferedOutputStream destination = 
          new BufferedOutputStream (out, BUF_SIZE);

        // Reading from the zip input stream will decompress the data
        // which is then written to the output file.
        while ((len = zip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1)
          destination.write (input_buffer, 0, len);
        destination.flush (); // Insure all the data is output
        out.close ();
      }
      catch (IOException e) {
        return STATUS_GUNZIP_FAIL;
      }
    } while (true);// Continue reading files from the archive

    try {
      zip_in_stream.close ();
    }
    catch (IOException e) {}


    return STATUS_OK;

  } // unzipFile


  /**
    * Gunzip the input archive. Send the output to the directory specified 
    * by dir_output. Assumes that the input file name ends with ".gz"
   **/
  public static int gunzipFile (File file_input, File dir_output) {

    // Create a buffered gzip input stream to the archive file.
    GZIPInputStream gzip_in_stream;
    try {
      FileInputStream in = new FileInputStream(file_input);
      BufferedInputStream source = new BufferedInputStream (in);
      gzip_in_stream = new GZIPInputStream(source);
    }
    catch (IOException e) {
      return STATUS_IN_FAIL;
    }

    // Use the name of the archive for the output file name but
    // with ".gz" stripped off.
    String file_input_name = file_input.getName ();
    String file_output_name =  
      file_input_name.substring (0, file_input_name.length () - 3);

    // Create the decompressed output file.
    File output_file = new File (dir_output, file_output_name);

    // Decompress the gzipped file by reading it via
    // the GZIP input stream. Will need a buffer.
    byte[] input_buffer = new byte[BUF_SIZE];
    int len = 0;
    try {
      // Create a buffered output stream to the file.
      FileOutputStream out = new FileOutputStream(output_file);
      BufferedOutputStream destination = 
        new BufferedOutputStream (out, BUF_SIZE);

      // Now read from the gzip stream, which will decompress the data,
      // and write to the output stream.
      while ((len = gzip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1)
        destination.write (input_buffer, 0, len);
      destination.flush (); // Insure that all data is written to the output.
      out.close ();
    }
    catch (IOException e) {
      return STATUS_GUNZIP_FAIL;
    }

    try {
      gzip_in_stream.close ();
    }
    catch (IOException e) {}

    return STATUS_OK;

  } // gunzipFile
  
  
	public static String gz2String(InputStream fis) {
		StringBuffer output = new StringBuffer();

		GZIPInputStream gzip_in_stream;
		try {

			BufferedInputStream source = new BufferedInputStream(fis);
			gzip_in_stream = new GZIPInputStream(source);

			BufferedReader reader = FileEncoding.getReader(gzip_in_stream);

			String gutti;
			while ((gutti = reader.readLine()) != null) {
				output.append(gutti + System.getProperty("line.separator"));
			}

		} catch (IOException ex) {
			System.out.println("IO Exception while unzipping data!");
			ex.printStackTrace();
		}

		return output.toString();
	}

	public static String gz2String(String filePath) {
		StringBuffer output = new StringBuffer();

		try {

			GZIPInputStream stream = new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(filePath)));
			Scanner scanner = new Scanner(stream);
			while (scanner.hasNextLine()) {
				output.append(scanner.nextLine() + "\n");
			}

		} catch (Exception e) {
			System.out.println("Exception in gz2String:");
			e.printStackTrace();
		}

		return output.toString();
	}
	

} // ZipGzipper
