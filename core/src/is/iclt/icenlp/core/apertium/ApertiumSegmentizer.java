/**
 * Segmentizes the output from Lt-Proc
 */
package is.iclt.icenlp.core.apertium;

import is.iclt.icenlp.core.utils.FileEncoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ApertiumSegmentizer
{
	private String toSegment;
	private ArrayList<String> segments;
	private int current;
	private BufferedReader input;
	
	static private final String SPLIT_ON = "(?<=%1$s)"; // Look behind regex
	
	public ApertiumSegmentizer(InputStream stream) throws IOException
	{
		input = FileEncoding.getReader(stream);
		current = 0;
		segments = new ArrayList<String>();
		
		segmentize();
	}
	
	private void segmentize() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		while(input.ready())
		{
			sb.append(input.readLine());
		}
		
		String segment = sb.toString();
		
		String[] segmentSplit = segment.split(String.format(SPLIT_ON, "<sent>\\$"));
		
		for(String s: segmentSplit)
		{
			segments.add(s.trim());
		}
	}
	
	public boolean hasMoreSentences()
	{
		return current != segments.size();
	}
	
	public String getNextSentence()
	{
		return segments.get(current++);
	}
}
