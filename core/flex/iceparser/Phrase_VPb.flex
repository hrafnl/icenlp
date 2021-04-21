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
 
/* This transducer marks verb BE phrases */
/* Verb BE phrases are parsed before other verbs for better sentence function parsing */
package is.iclt.icenlp.core.iceparser;
import java.util.regex.Pattern;
import java.io.*;
%%

%public
%class Phrase_VPb
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String VPBOpen=" [VPb ";
  String VPBClose=" VPb] "; 
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF) 
      	    yylex();
  }
  
  
  private String getLastWord(String txt)
  {
  	String[] strs;
	String tag;
	String result="";
	Pattern p = Pattern.compile("\\s+");
	// Get all the individual lexemes as strings
	strs = p.split(txt);
        int len = strs.length;
        if (len >= 2)
        {
            tag = strs[len-1];
            /* if (tag.equal("ssg")) 		Is this necessary? */
            result = strs[len-2];
        }
        return result;
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
%include verbLexicon.txt

BeVerb = {VerbBe}|vera
BeWordSpaces = {WhiteSpace}*{BeVerb}{WhiteSpace}+

VerbPastPartTag = {encodeOpen}sþ{Voice}{Gender}{Number}{Case}{encodeClose}
VerbPastPartTagNeut = {encodeOpen}sþ{Voice}h{Number}{Case}{encodeClose}
VerbPresentPartTag = {encodeOpen}slg{encodeClose}
VerbSupineTag = {encodeOpen}ss{Voice}{encodeClose}

AdverbPhrase = {OpenAdvP}~{CloseAdvP}
Infinitive = 	{WordSpaces}{InfinitiveTag}

VerbPastPartNeut =  {WordSpaces}{VerbPastPartTagNeut}
VerbSupine = 	{WordSpaces}{VerbSupineTag}

BeFinite = 	{BeWordSpaces}{VerbFiniteTag}
BePastPart =  {BeWordSpaces}{VerbPastPartTag}
BePresentPart =  {BeWordSpaces}{VerbPresentPartTag}
BeSupine = 	{BeWordSpaces}{VerbSupineTag}
BeInfinitive = {BeWordSpaces}{VerbInfinitiveTag}

BeInfinitivePhrase = {Infinitive}?{BeInfinitive}
BeFinitePhrase = {BeFinite}
BePhrase =  {BeFinitePhrase} 
BePhraseInf = {BeInfinitivePhrase}
BePhraseSupine = {BeSupine}+
BePhrasePastPart = {BePastPart}
BePhrasePresentPart = {BePresentPart}

%%

{MWE}			{ out.write(yytext());}
{BePhrase}		{ out.write(VPBOpen+yytext()+VPBClose);}
{BePhraseInf}		{ out.write(VPBOpen+yytext()+VPBClose);}
{BePhraseSupine}	{ out.write(VPBOpen+yytext()+VPBClose);}
{BePhrasePastPart}	{ out.write(VPBOpen+yytext()+VPBClose);}
{BePhrasePresentPart}	{ out.write(VPBOpen+yytext()+VPBClose);}

"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.			{ out.write(yytext());}




