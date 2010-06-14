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
 
/* This transducer groups specific (adverb,prep) pairs  */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_MWEP2
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String Open=" [MWE_PP ";
  String Close=" MWE_PP] ";
  
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

AdverbPart = {WhiteSpace}+{AdverbTag}
PrepPart = {WhiteSpace}+{PrepTag}
NounPart = {WhiteSpace}+{NounTag}
MainPrepPart = (að|af|á|eftir|frá|fyrir|hjá|í|með|til|um|úr|við|yfir|undir){PrepPart}	

Pair = 	[aA]ft(an|ur){AdverbPart}{MainPrepPart}				|
	[aA]ust(an|ur){AdverbPart}{MainPrepPart}			|
	[áÁ]{AdverbPart}(eftir|meðal|milli|móti|undan){PrepPart} 	|
	[bB]ak{NounPart}við{PrepPart} 					|
	[fF]ram(an|mi)?{AdverbPart}{MainPrepPart}			|
	[gG]egn{AdverbPart}um{PrepPart}					|
	[hH]ér{AdverbPart}(á|fyrir|hjá|í|við|undir){PrepPart}		|
	[hH]andan{AdverbPart}(að|af|frá|fyrir|í|um|við|yfir){PrepPart}	|
	[nN]eðan{AdverbPart}{MainPrepPart}				|
	[nN]ið(ur|ri){AdverbPart}{MainPrepPart}				|
	[nN]orð(an|ur){AdverbPart}{MainPrepPart}			|
	[iI]nn(i|an)?{AdverbPart}{MainPrepPart}				|
	[íÍ]{AdverbPart}(gegnum|kringum){PrepPart}			|
	[oO]fan{AdverbPart}{MainPrepPart}				|
	[rR]étt{AdverbPart}(í|á|hjá|við){PrepPart}			|
	[sS](unnan|uður){AdverbPart}{MainPrepPart}			|
	[uU]pp{AdverbPart}{MainPrepPart}				|
	[uU]ppi{AdverbPart}(á|í){PrepPart}				|
	[uU]tan{AdverbPart}{MainPrepPart}				|
	[úÚ]t{AdverbPart}{MainPrepPart}					|
	[úÚ]ti{AdverbPart}(á|í|við){PrepPart}				|
	[vV]esta(an|ur){AdverbPart}{MainPrepPart}			|
	[yY]fir{AdverbPart}{MainPrepPart}				|
	[þÞ]rátt{AdverbPart}fyrir{PrepPart}
	
NotPair = [áÁ]fram{AdverbPart}{MainPrepPart} |
	  [þÞ]á{AdverbPart}{MainPrepPart}			
//AdverbPrep = {WhiteSpace}+{Pair}

%%
{MWE_PP}	{ out.write(yytext());} 
{NotPair}	{ out.write(yytext());} 
{Pair}	{ out.write(Open+yytext()+Close);}
//{AdverbPrep}	{ out.write(Open+yytext()+Close);}
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}
