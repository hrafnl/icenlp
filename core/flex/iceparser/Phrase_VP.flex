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
/* Infinitive verb phrases and verb BE phrases are specially marked for use in later transducers */
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

VerbPastPartTag = \^sþ{Voice}{Gender}{Number}{Case}\$
VerbPresentPartTag = \^slg\$
VerbSupineTag = \^ss{Voice}\$

AdverbPhrase = {OpenAdvP}~{CloseAdvP}
VerbBeAndTag =	{WhiteSpace}*{VerbBe}{WhiteSpace}+{VerbFiniteTag}
Infinitive = 	{WordSpaces}{InfinitiveTag}
VerbFinite = 	{WordSpaces}{VerbFiniteTag}
VerbPastPart =  {WordSpaces}{VerbPastPartTag}
VerbPresentPart =  {WordSpaces}{VerbPresentPartTag}
VerbSupine = 	{WordSpaces}{VerbSupineTag}
VerbOther = 	{WordSpaces}{VerbTag}
VerbInfinitive = {WordSpaces}{VerbInfinitiveTag}
InfinitivePhrase = {Infinitive}?{VerbInfinitive}{VerbSupine}*

FinitePhrase = {VerbFinite} (({WhiteSpace}*{AdverbPhrase})*{VerbSupine}+)?
VerbPhrase =  {FinitePhrase} 
VerbPhraseInf = {InfinitivePhrase}
VerbPhraseSupine = {VerbSupine}+
VerbPhrasePastPart = {VerbPastPart}
VerbPhrasePresentPart = {VerbPresentPart}

%%

{MWE}			{ out.write(yytext());}
{VerbBeAndTag}		{ out.write(VPBOpen+yytext()+VPBClose);}
{VerbPhrase}		{ 
			  /* If the last word in the verb phrase is "verið" then mark as a BE phrase */
			 	String lastWord = getLastWord(yytext());
			 	if (lastWord.equals("verið") || lastWord.equals("orðið"))
					out.write(VPBOpen+yytext()+VPBClose);
				else
					out.write(VPOpen+yytext()+VPClose);
			}
{VerbPhraseInf}		{ /* If the last word in the verb phrase is "vera" then mark as a BE phrase */
			  	String lastWord = getLastWord(yytext());
			  	if (lastWord.equals("vera"))
			  		out.write(VPBOpen+yytext()+VPBClose);
			  	else
					out.write(VPIOpen+yytext()+VPIClose);}
{VerbPhraseSupine}	{ 
			  /* If the last word in the verb phrase is "verið" then mark as a BE phrase */
				String lastWord = getLastWord(yytext());
				if (lastWord.equals("verið") || lastWord.equals("orðið"))
					out.write(VPBOpen+yytext()+VPBClose);
				else
					out.write(VPSOpen+yytext()+VPSClose);
			}
{VerbPhrasePastPart}	{ 
			   out.write(VPPOpen+yytext()+VPPClose);
			}
{VerbPhrasePresentPart}	{ 
			   out.write(VPGOpen+yytext()+VPGClose);
			}

"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.			{ out.write(yytext());}
