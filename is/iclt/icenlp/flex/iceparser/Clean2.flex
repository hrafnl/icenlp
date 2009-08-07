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

/* This transducer cleans the output file 		*/
/* 1. Suppress two or more whitespaces into one 	*/
/* 2. Removes case information from phrase labels 	*/
package is.iclt.icenlp.flex.iceparser;
import java.io.*;
%%

%public
%class Clean2
%standalone
%line

%unicode

%{
   String str;
   
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

StartNP = {OpenNP}{PhraseCase}
StartAP = {OpenAP}{PhraseCase}

%%

^" "{1,3} 	{;}
{WhiteSpace}+	{ out.write(" ");}
{StartNP}	{ out.write("[NP");}
{StartAP}	{ out.write("[AP");}
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}

