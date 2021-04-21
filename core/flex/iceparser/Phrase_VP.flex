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
 
/* This transducer marks verb phrases 	*/
/* Verb BE phrases are parsed in Phrase_VPb.flex and are ignored in this transducer */
package is.iclt.icenlp.core.iceparser;
import java.util.regex.Pattern;
import java.io.*;
%%

%public
%class Phrase_VP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String VPOpen=" [VP ";
  String VPClose=" VP] "; 
  String VPIOpen=" [VPi ";
  String VPIClose=" VPi] "; 
  String VPBOpen=" [VPb ";
  String VPBClose=" VPb] "; 
  String VPSOpen=" [VPs ";
  String VPSClose=" VPs] "; 
  String VPPOpen=" [VPp ";
  String VPPClose=" VPp] ";
  String VPGOpen=" [VPg ";
  String VPGClose=" VPg] ";
  
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

VerbPhraseBe = {OpenVPb}~{CloseVPb}

VerbPastPartTag = {encodeOpen}sþ{Voice}{Gender}{Number}{Case}{encodeClose}
VerbPastPartTagNeut = {encodeOpen}sþ{Voice}h{Number}{Case}{encodeClose}
VerbPresentPartTag = {encodeOpen}slg{encodeClose}
VerbSupineTag = {encodeOpen}ss{Voice}{encodeClose}

AdverbPhrase = {OpenAdvP}~{CloseAdvP}
Infinitive = 	{WordSpaces}{InfinitiveTag}

VerbFinite = 	{WordSpaces}{VerbFiniteTag}
VerbPastPart =  {WordSpaces}{VerbPastPartTag}
VerbPastPartNeut =  {WordSpaces}{VerbPastPartTagNeut}
VerbPresentPart =  {WordSpaces}{VerbPresentPartTag}
VerbSupine = 	{WordSpaces}{VerbSupineTag}
VerbOther = 	{WordSpaces}{VerbTag}
VerbInfinitive = {WordSpaces}{VerbInfinitiveTag}

InfinitivePhrase = {Infinitive}?{VerbInfinitive}({VerbSupine}|{VerbPastPartNeut})*
FinitePhrase = {VerbFinite} (({WhiteSpace}*{AdverbPhrase})*({VerbSupine}|{VerbPastPartNeut})+)?
VerbPhrase =  {FinitePhrase} 
VerbPhraseInf = {InfinitivePhrase}
VerbPhraseSupine = {VerbSupine}+
VerbPhrasePastPart = {VerbPastPart}
VerbPhrasePresentPart = {VerbPresentPart}

%%

{MWE}			{ out.write(yytext());}
{VerbPhraseBe}	{ out.write(yytext());}

{VerbPhrase}		{ out.write(VPOpen+yytext()+VPClose);}
{VerbPhraseInf}		{ out.write(VPIOpen+yytext()+VPIClose);}
{VerbPhraseSupine}	{ out.write(VPSOpen+yytext()+VPSClose);}
{VerbPhrasePastPart}	{ out.write(VPPOpen+yytext()+VPPClose);}
{VerbPhrasePresentPart}	{ out.write(VPGOpen+yytext()+VPGClose);}

"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.			{ out.write(yytext());}
