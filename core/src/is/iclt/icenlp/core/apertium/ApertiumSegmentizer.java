/**
 * Segmentizes the output from Lt-Proc
 */
package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.utils.FileEncoding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ApertiumSegmentizer
{
	private BufferedReader input;
	private String currentSentence;
	
	static private final String SPLIT_ON = "(?<=%1$s)"; // Look behind regex
	
	public ApertiumSegmentizer(InputStream stream) throws IOException
	{
		input = FileEncoding.getReader(stream);
		
		currentSentence = readOneSentance();
	}
	
	public ApertiumSegmentizer(String inputFile) throws IOException
	{
		input = FileEncoding.getReader(inputFile);
		
		currentSentence = readOneSentance();
	}

	/**
	 * This reads from the input stream until it hits a <sent>
	 * 
	 * @return String
	 * @throws IOException 
	 */
	private String readOneSentance() throws IOException
	{
		int readInt = 0;
		
		String match = "<sent>$";
		StringBuilder sb = new StringBuilder();
		
		// Read one character
		while((readInt = input.read()) != -1)
		{
			char readChar = (char)readInt;
			
			// Add it to the string builder
			sb.append(readChar);
			
			// Check if the end sentence match is found
			if(readChar == '$' && sb.toString().contains(match))
			{
				break;
			}
		}
		
		return sb.toString();
	}
	
	public boolean hasMoreSentences()
	{
		return currentSentence != null && currentSentence.length() > 0;
	}
	
	public String getSentance()
	{	
		return currentSentence;
	}
	
	public void processNextSentence() throws IOException
	{
		currentSentence = readOneSentance();
	}
}
