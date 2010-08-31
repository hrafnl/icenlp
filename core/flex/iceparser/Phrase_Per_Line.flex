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
 
/* This transducer prints each phrase of a sentence on a separate line */
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_Per_Line
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  StringBuffer str = new StringBuffer();
  int count = 0;
  int funcCount = 0;
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF) 
      	    yylex();
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

%include regularDef.txt

WordChar = [^\r\n\t\f\[\]\{\} ]

Word = {WordChar}+
Label = FRWs?|AdvP|APs?|NP[s\?]?|VP[bgips]?|PP|S?CP|InjP|MWE_(AdvP|AP|CP|PP)
Func = (("*"SUBJ|"*"I?OBJ(AP|NOM)?|"*"COMP)(<|>)?)|"*"QUAL | "*"TIMEX\??

//nýtt
Symbol = \[{WhiteSpace}*{encodeOpen}\[{encodeClose}  | \]{WhiteSpace}*{encodeOpen}\]{encodeClose} | \{{WhiteSpace}*{encodeOpen}\{{encodeClose} | \}{WhiteSpace}*{encodeOpen}\}{encodeClose}

%state PHRASE
%state FUNC

%%

<YYINITIAL> 
{
	//nýtt

	{Symbol}
	{
		out.write(yytext()); out.write("\n");
	}
	// //
	
	"["{Label}" " 	
	{ 
		/* System.err.println("InitLabel open " + yytext()); */
		count++; str.append(yytext()); 
		yybegin(PHRASE);
	}
	"{"{Func}" " 	
	{ 
		/* System.err.println("InitFunc open " + yytext()); */
		funcCount++; 
		str.append(yytext()); 
		yybegin(FUNC);
	}
	{Word}" "{Word}	
	{ 
		out.write(yytext()); out.write("\n");
	}
	"\n"		
	{ 
		//System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); 
	}
	.	{ ;}
}

<PHRASE> 
{
	" "{Label}"]"	
	{ 
		count--; 
		/* System.err.println("PhraseLabel close " + yytext()); */
		str.append(yytext());
		if (count == 0) 
		{ 
		  	/*System.out.println("Match");*/
			out.write(str.toString());
			out.write("\n");
			str.setLength(0);
			yybegin(YYINITIAL);
		}
	}
	"["{Label}" " 	
	{ 
		/* System.err.println("PhraseLabel open " + yytext()); */
		count++; 
		str.append(yytext());
	}
	.		{ str.append(yytext());}
}

<FUNC> 
{
	" "{Func}"}"	
	{
		/* System.err.println("Func close " + yytext()); */
		funcCount--; 
		str.append(yytext());
		if (funcCount == 0) 
		{ 
			out.write(str.toString());
			out.write("\n");
			str.setLength(0);
			yybegin(YYINITIAL);
		}
	}
	"{"{Func}" " 	
	{ 
		/* System.err.println("Func open2 " + yytext()); */
		funcCount++; str.append(yytext());
	}	  	
	.	{ str.append(yytext());}
}
