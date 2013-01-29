package is.iclt.icenlp.core.utils;

import java.io.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: gudmundur
 * Date: 1/15/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordHashMap {
	private static HashMap wordHashMap = new HashMap();

	// load the hashmap with words
	WordHashMap(String filePath) throws FileNotFoundException, IOException
	{
		    InputStream fstream = getClass().getResourceAsStream(filePath);
			readFile(fstream);
	}

	// receives a word to look for in the hashmap
	// looks up the cases for that word
	// returns a string like : "oþ" meaning the word demands next word to be either accusative or dative
	public String wordLookup(String word)
	{
		// get the word we are looking for in the hashmap
		if (wordHashMap.containsKey(word))
		{
			String results = wordHashMap.get(word).toString();
//			System.out.println("gD>>found ("+word+") to have ("+results+")");
			return results;
		}

		return "";
	}

	// reads a file stream, extracts the words and cases, and puts it into the hashmap
	private void readFile(InputStream fstream) throws FileNotFoundException, IOException
	{
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = null;
		int lineNum = 1;

		while ((strLine = br.readLine()) != null)
		{
			if (strLine.length() != 0 && (!strLine.startsWith("#") || !strLine.startsWith("//")))
			{
				String cases = extractCases(strLine);
				String word = strLine.replaceAll("([^=\\s]+)\\s+.*","$1");
			//	System.out.println("gDB>>["+lineNum+"] strLine=("+strLine+") word=("+word+") cases=("+cases+")");

				addWord(word, cases);
			}
			lineNum++;
		}
	}

	private String extractCases (String in)
	{
		StringBuffer out = new StringBuffer();
		String cases = in.replaceAll("[^=\\s]+\\s+(.*)","$1");

		// extract which case the word dictates and convert to our code
		// our code consists of "noþe" where each letter represents a case dictated by the word
		if (cases.contains("NF")||cases.contains("n"))
		{
			out.append("n");
		}
		if (cases.contains("ÞF")||cases.contains("o"))
		{
			out.append("o");
		}
		if (cases.contains("ÞGF")||cases.contains("þ"))
		{
			out.append("þ");
		}
		if (cases.contains("EF")||cases.contains("e"))
		{
			out.append("e");
		}

		return out.toString();
	}

	private void addWord (String word, String cases)
	{
		// check if we already have inserted the word, if we cannot find it we add the word+cases.
		// if we do find it, then we add the value to the current value of that word. (duplications are allowed: ÞF ÞGF ÞF ÞF)
		if (wordHashMap.containsKey(word))
		{
			wordHashMap.put(word,wordHashMap.get(word)+cases);
		}
		else
		{
			wordHashMap.put(word, cases);
		}
	}
}
