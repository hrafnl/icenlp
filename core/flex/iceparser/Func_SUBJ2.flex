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
 
/* This transducer marks subjects which have not been marked by previous functional transducers */
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Func_SUBJ2
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String Func0Open=" {*SUBJ ";
  String Func0Close=" *SUBJ} ";
  String Func2Open=" {*SUBJ< ";
  String Func2Close=" *SUBJ<} ";
  
  int theIndex=0;
  
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
%include funcDef.txt
%include verbLexicon.txt

Subject = {NomSubject}({WhiteSpace}+{FuncQualifier})?

PP = {OpenPP}~{ClosePP}

%%

{Function}	{out.write(yytext());}	/* Don't touch the phrases that have already been function marked */
{PP}		{out.write(yytext());}	/* Don't touch PP phrases */


{Subject}	{ 
			out.write(Func0Open+yytext()+Func0Close);
		}
		
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}
