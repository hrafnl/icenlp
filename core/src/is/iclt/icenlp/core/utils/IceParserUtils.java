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
package is.iclt.icenlp.core.utils;

import java.io.*;

/**
 * 
 * @author Ragnar L. Sigurðsson.
 */
public class IceParserUtils 
{
	public IceParserUtils()
	{
	}
	public static String RemoveFromSymbolToWhitespace(String Symbol, String str)
	{
		if(str.indexOf(Symbol) == -1)
		{
			return str;
		}
		
		String before, middle, after;
		
		before = str.substring(0, str.indexOf(Symbol));
		middle = str.substring(str.indexOf(Symbol), str.length());
		after = middle.substring(middle.indexOf(" "), middle.length());
		
		str = before+after;

		return RemoveFromSymbolToWhitespace(Symbol, str);
	}
	public static String RemoveSpacesAndWords(String str)
	{
		String [] temp = null;
		temp = str.split(" ");
		str = "";
		int wordNr = 0;
		for(int i=0; i < temp.length; i++)
		{
			if(temp[i].length() > 0)
			{	
				wordNr++;
				if(wordNr %2 !=1)
				{
					str += temp[i] + " ";	
				}
			}
		}

		return str;
	}

}


