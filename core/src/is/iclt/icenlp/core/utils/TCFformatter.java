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

				System.out.println("boobies! and "+temp);
				return temp.replaceAll(".*<text>(.*)</text>.*","$1");
			}
		}

		System.out.println("freud!");
		return "";
	}

	// Takes in a Segmentizer object
	// Compiles a string buffer from the segmentizer for easier analyses
	// Checks if it finds opening and closing of D-Spin and TextCorpus
	// returns True : if it finds D-Spin and TextCorpus opening and closing
	// returns False : if it does not find D-Spin and TextCorpus opening and closing
	public static boolean isTCF(Segmentizer segmentizer) throws IOException
	{

		StringBuffer text = new StringBuffer();
		while (segmentizer.hasMoreSentences())
		{
			text.append(segmentizer.getNextSentence());
		}
		String temp = text.toString();

		if ((temp.contains("<D-Spin"))&&(temp.contains("<TextCorpus"))&&(temp.contains("</TextCorpus>"))&&(temp.contains("</D-Spin>")))
		{
			System.out.println("nice");
			return true;
		}
		else
		{
			System.out.println("eww");
			return false;
		}
	}
}
