/*
 * Copyright (C) 2009 Hrafn Loftsson
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
 * Hrafn Loftsson, School of Computer Science, Reykjavik University.
 * hrafn@ru.is
 */
package is.iclt.icenlp.flex.iceparser;
/**
 * A static helper class for doing string searches
 */
public class StringSearch {

	/* firstString and nextString are set by the functions defined below						*/
	/* firstString contains the first part of the whole string with regard to the search string */
	/* nextString contains the second part of the whole string with regard to the search string */

	public static String firstString, nextString;


	/* Searches wholeStr for the first/last occurence of searchStr and splits the string	*/
	/* into two strings, firstString and nextString											*/
	/* The argument step is used to step some characters back/forward in the wholeStr when	*/
	/* splitting wholeStr																	*/

	public static int splitString(String wholeStr, String searchStr, boolean first, int step)
	{
	  /* If first=true then interested in the first occurence of searchStr */
	  /* else interested in the last occurence of searchStr */

	  	int idx, endIdx;
	  	if (first)
	  	  idx = wholeStr.indexOf(searchStr);		/* First index of searchStr */
	  	else
	  	  idx = wholeStr.lastIndexOf(searchStr);	/* Last index of searchStr */

	  	if (idx != -1)
	  	{
	  		endIdx = idx + step;
			nextString = wholeStr.substring(endIdx);
			firstString = wholeStr.substring(0,endIdx);
		}
		return idx;
	}

	/* Searches wholeStr for an occurence of searchStr2 in case where searchStr2 follows searchStr1 */
	/* Splits wholeStr into two strings, firstString and nextString									*/

	public static int splitString2(String wholeStr, String searchStr1, String searchStr2)
	{
	      /* Find the index of the character following searchStr2 in the case where */
	      /* searchStr2 follows searchStr1 */

	      	int idx1, idx2, endIdx;
	      	int step = searchStr2.length();

	      	idx1 = wholeStr.indexOf(searchStr1);		/* First index of searchStr */
	      	idx2 = wholeStr.indexOf(searchStr2, idx1);

	      	if (idx2 != -1) {
	      		endIdx = idx2 + step;
	    		nextString = wholeStr.substring(endIdx);
	    		firstString = wholeStr.substring(0,endIdx);
			}
			return idx2;
  	}


}