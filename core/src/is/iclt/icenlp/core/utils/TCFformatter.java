package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.tokenizer.Segmentizer;

import javax.swing.text.Segment;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: gudmundur
 * Date: 10/4/12
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class TCFformatter {

	// Takes in a Segmentizer object
	// checks every line for <text> and </text>
	// if we find it in the same line we return the text that is between the <text> and </text>
	// returns empty "" string if it is not found.
	public static String getTCFtokens(Segmentizer segmentizer) throws IOException
	{

		String temp;
		System.out.println("segmentizer.hasMoreSentences()="+segmentizer.hasMoreSentences());
		while (segmentizer.hasMoreSentences())
		{
			temp = segmentizer.getNextSentence();

			System.out.println("."+temp);
			if (temp.contains("<text>")&&temp.contains("</text>"))
			{

				System.out.println("temp="+temp);
				return temp.replaceAll(".*<text>(.*)</text>.*","$1");
			}
		}

		System.out.println("getTCFtokens quit!");
		return "";
	}

	// Takes in a Segmentizer object
	// Compiles a string buffer from the segmentizer for easier analyses
	// Checks if it finds opening and closing of D-Spin and TextCorpus
	// returns True : if it finds D-Spin and TextCorpus opening and closing
	// returns False : if it does not find D-Spin and TextCorpus opening and closing
	public static boolean isTCF(Segmentizer segmentizer) throws IOException
	{

		StringBuilder text = new StringBuilder();
		while (segmentizer.hasMoreSentences())
		{
			text.append(segmentizer.getNextSentence());
		}
		String temp = text.toString();

		return isTCF(temp);
	}


	public static boolean isTCF(String text)
	{
		return ((text.contains("<D-Spin"))&&(text.contains("<TextCorpus"))&&(text.contains("<text>"))&&(text.contains("</text>"))&&(text.contains("</TextCorpus>"))&&(text.contains("</D-Spin>")));
	}

	// input: TCF
	// output: text part of the TCF
	public static String getText()
	{
		return "";
	}

	// input: TCF
	// output: string array of tokens from the TCF
	public static String[] getTokens()
	{
		String out[] = {};
		return out;
	}

	// input: TCF
	// output: string array of tags from the TCF
	public static String[] getTags()
	{
		String out[] = {};
		return out;
	}


	public static String TCFtoText(String tcf)
	{
		String[] tcfArray = tcf.split("\n");
		int n = tcfArray.length / 2;
		int tokenCounter = 0, tagCounter = 0, constituentCounter = 0;
		String[] token = new String[n];
		String[] tag = new String[n];
		String[] constituent = new String[n];


		// extract token, tag and constituent from the TCF and put in the next empty slot in the appropriate array
		for (String line: tcfArray)
		{
			if (line.matches("\\s*<token ID=\"t\\d+\">\\S+</token>"))
			{
				token[tokenCounter] = line.replaceAll("\\s*<token ID=\"t\\d+\">(\\S+)</token>","$1");
				tokenCounter++;
			}
			else if (line.matches("\\s*<tag tokenIDs=\"t\\d+\">\\S+</tag>"))
			{
				tag[tagCounter] = line.replaceAll("\\s*<tag tokenIDs=\"t\\d+\">(\\S+)</tag>","$1");
				tagCounter++;
			}
			else if (line.matches("\\s*<constituent ID=\"c\\d+\" cat=\"[\\[\\{]\\S+\">"))
			{
				constituent[constituentCounter] = line.replaceAll("\\s*<constituent ID=\"c\\d+\" cat=\"[\\[\\{](\\S+)\">","open-$1");
				constituentCounter++;
			}
			else if (line.matches("\\s*<constituent ID=\"c\\d+\" cat=\"\\S+\" tokenIDs=\"t\\d+\"/>"))
			{
				constituent[constituentCounter] = line.replaceAll("\\s*<constituent ID=\"c\\d+\" cat=\"(\\S+)\" tokenIDs=\"t(\\d+)\"/>","$2-$1");
				constituentCounter++;
			}
			else if (line.matches("\\s*</constituent>"))
			{
				constituent[constituentCounter] = "close";
				constituentCounter++;
			}
		}

		return compileText(token, tag, constituent);
	}

	// Input: token, tag and constituent string arrays, were each has been extracted from the TCF and put in the next empty array slot
	private static String compileText(String[] token, String[] tag, String[] constituent)
	{
		StringBuilder out = new StringBuilder();
		for (String s: constituent)
		{
			if (s==null)
			{
				break;
			}
			else if (s.matches("\\d+-\\S+"))
			{
				// 0. get the phrase type
				String phraseType = s.replaceAll("\\d+-(\\S+)","$1");
				// 1. extract number from the string
				String tokenNumberString = s.replaceAll("(\\d+)-\\S+","$1");
				// 2. convert the string to an integer
				int tokenNumberInt = Integer.parseInt(tokenNumberString);
				// 3. reduce the number by one as 0 is the first array but not 1
				tokenNumberInt--;

				// 4. add it to the output : "[NP Hún fpven ] "
				out.append("[").append(phraseType).append(" ").append(token[tokenNumberInt]).append(" ").append(tag[tokenNumberInt]).append("] ");
			}
			else if (s.contains("open"))
			{
				// extract the function [NP and add to the output
				out.append(s.replaceAll("open-(\\S+)","[$1")).append(" ");
			}
			else if (s.contains("close"))
			{
				// we are closing what has been open
				out.append("] ");
			}
		}

		//	remove the last "] " plus the whitespace before that
		int length = out.length() - 3;
		if (length <= 0)
		{
			length = 0;
		}
		out.setLength(length);

		return out.toString();
	}
}
