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
 
/* This transducer marks adjectival phrases */
/* An adjectival phrase consists of an optional adverb phrase followed by an adjective */
/* The adverb can neither be in the comparative nor in the superlative */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_AP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String APOpen=" [AP ";
  String APClose=" AP] "; 
  
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

Adjective = {WordSpaces}{AdjectiveTag}
AdverbPhrase = {OpenAdvP}~{encodeOpen}"aa"{encodeClose}{WhiteSpace}+{CloseAdvP}
//AdverbPhrase = {OpenAdvP}{WordSpaces}"aa"{WhiteSpace}+{CloseAdvP}
AdjectivePhrase = {AdverbPhrase}?{Adjective} 

%%

{MWE}			{ out.write(yytext());}
{AdjectivePhrase}	{ out.write(APOpen+yytext()+APClose);}
"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.			{ out.write(yytext());}
