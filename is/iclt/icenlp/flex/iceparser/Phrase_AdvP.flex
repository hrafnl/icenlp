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
 
/* This transducer marks adverb phrases, conjunction phrases and interjection phrases */
package is.iclt.icenlp.flex.iceparser;
import java.io.*;
%%

%public
%class Phrase_AdvP
%standalone
%line

%unicode

%{
  String APOpen=" [AdvP ";
  String APClose=" AdvP] ";
  String CPOpen=" [CP ";
  String CPClose=" CP] ";
  String SCPOpen=" [SCP ";
  String SCPClose=" SCP] ";
  String InjOpen=" [InjP ";
  String InjClose=" InjP] ";
  
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

InterjectionTag = au{WhiteSpace}+
Interjection = {WordSpaces}{InterjectionTag}

AdverbPhrase = {Adverb}
InterjectionPhrase = {Interjection} 

MainConjWords = [oO]g|[eE](n(da)?|ða|llegar)|[hH]eldur|[nN]é
MainConjTag = c{WhiteSpace}+
MainConj = {WhiteSpace}*{MainConjWords}{WhiteSpace}+{MainConjTag}
MainConjPhrase = {MainConj} 
SubConjPhrase = {Conj}

%%
{MWE}			{ out.write(yytext());}
{MainConjPhrase}	{ out.write(CPOpen+yytext()+CPClose);}
{SubConjPhrase}		{ out.write(SCPOpen+yytext()+SCPClose);}
{AdverbPhrase}		{ out.write(APOpen+yytext()+APClose);}
{InterjectionPhrase}	{ out.write(InjOpen+yytext()+InjClose);}
"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.			{ out.write(yytext());}
