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
 /* This transducer only runs if the -a (agreement) parameter is set and marks 1 word
    noun phrases. */
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_NP2
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String NPOpen=" [NP ";
  String NPClose=" NP] ";
  
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


ArticleTag = g{Gender}{Number}{Case}{WhiteSpace}+
PossPronounTag = fe{Gender}{Number}{Case}{WhiteSpace}+
IndefPronounTag = fo{Gender}{Number}{Case}{WhiteSpace}+
InterPronounTag = fs{Gender}{Number}{Case}{WhiteSpace}+
DemonPronounTag = fa{Gender}{Number}{Case}{WhiteSpace}+
ReflexivePronounTag = fb{Gender}{Number}{Case}{WhiteSpace}+

Noun = {WordSpaces}{NounTag}
ProperNoun = {WordSpaces}{ProperNounTag}
PersPronoun = {WordSpaces}{PersPronounTag} 
PossPronoun = {WordSpaces}{PossPronounTag} 
IndefPronoun = {WordSpaces}{IndefPronounTag} 
Hvada = {WhiteSpace}*[Hh]vaða{WhiteSpace}+{InterPronounTag}
Hvad = {WhiteSpace}*[Hh]v(að|((er|or)[a-z]*)){WhiteSpace}+{InterPronounTag}
DemonPronoun = {WordSpaces}{DemonPronounTag} 
ReflexivePronoun = {WordSpaces}{ReflexivePronounTag} 
Numeral = {WordSpaces}{NumeralTag}
Article = {WordSpaces}{ArticleTag}
Title = {WhiteSpace}*(([uU]ng)?[fF]r(ú|öken)|[hH](erra|r\.)|[sS][íé]ra|[dD]r\.){WhiteSpace}+{NounTag}


NounPhrase = {Hvad} | {Noun} | {ProperNoun} | {PersPronoun} | {PossPronoun} | {IndefPronoun} | {Hvada} | {DemonPronoun} | {ReflexivePronoun} | 
		{Numeral} | {Article} | {Title}

NounP = {OpenNP}~{CloseNP}

NP = {NPNom}|{NPAcc}|{NPDat}|{NPGen}|{NounP}

%%


{NP}		{ out.write(yytext());} /* Leave words already within a noun phrase tag */

{MWE}		{ out.write(yytext());} 		/* Don't touch multi-word expression */ 
{NounPhrase}	{ out.write(NPOpen+yytext()+NPClose);}
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}
