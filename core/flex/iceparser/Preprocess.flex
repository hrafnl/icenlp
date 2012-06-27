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
 
/* This transducer makes sure that there is one additional space after the last character in an input line given that the last character is not a sentence marker.  This is needed because there needs to be a space after each tag */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Preprocess
%standalone
%line    
%extends IceParserTransducer

%unicode

%{
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF) 
      	    yylex();
  }

  private String analyse(String txt)
  {	
	int len, lastIndex;
	String changedStr = txt;

	len = txt.length();
	lastIndex = len-1;
	
	if (lastIndex > 0) {
		Character chr = txt.charAt(lastIndex);
		if (Character.isLetter(chr))	// Then this letter is a part of a real tag and we may need a space after it 
		{
				changedStr =  txt + " ";
		}
		// detects ^tag$ at the end of txt
		// If the string ends with $ and the last whitespace is before the last ^ then this is a tag and we need to add whitespace after the $ to avoid problems
		else if ((chr == '$') && (txt.lastIndexOf(' ') < txt.lastIndexOf('^')))
		{
				changedStr =  txt + " ";
		}
	}

// new, phrase_mwe problem.
/*
	Character chr2 = txt.charAt(0);
	if(Character.isLetter(chr2))
	{	
		changedStr = " "+changedStr;
	}
*/
///
	return changedStr;		
  }
%}
  
%eof{
	try {
	  out.flush();	
	} 
	catch (IOException e) {
            e.printStackTrace();
        }
%eof}

Sentence = .+	
	
%%
{Sentence}	{ String changedStr, matchedStr;
		  int len;
			
		  matchedStr = yytext();
		  changedStr = analyse(matchedStr);
		  out.write(changedStr);
		}
"\n"		{ out.write("\n");}
