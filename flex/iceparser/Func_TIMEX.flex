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
 
/* This transducer marks temporal expressions like 	  */
/* [NP árið nheog 1982 ta NP] as having a *TIMEX function */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Func_TIMEX
%standalone
%line

%unicode

%{ 
  String TempOpen = " {*TIMEX ";
  String TempClose=" *TIMEX} ";  
  
  int theIndex = 0;
  
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


PPPhrase = {OpenPP}~{ClosePP}

/* A temporal expression, e.g. 
	[NP 1982 ta NP], [NP árið nheog 1982 ta NP]
	[NP [AP 30. lkeovf AP] apríl nkeo NP]
	[NP [AP 30. lkeovf AP] apríl nkeo 1939 ta NP]
	[NP nokkrum fokfþ dögum nkfþ NP] [AdvP síðar/seinna aam AdvP]
*/

NumberTag = t[ao]{WhiteSpace}+
NounNumeral = {WordSpaces}({NounTag}{WordSpaces}){NumberTag}
AdjPhrase = {OpenAP}a?~{CloseAP}
AdvPWords =  {OpenAdvP}{WhiteSpace}+s(íðar|einna){WhiteSpace}+{AdverbTag}{CloseAdvP}

Month = {WhiteSpace}+(jan(\.|úar)|feb(\.|rúar)|mar(\.|s)|apr(\.|íl)|maí|jún(\.|í)|
		      júl(\.|í)|ágú(\.|st)|sep(\.|tember)|okt(\.|óber)|nóv(\.|ember)|des(\.|ember)){WhiteSpace}+{NounTag}

TimeAcc = {OpenNP}a{NounNumeral}{CloseNP} 
TimeMonth = {OpenNP}a({WhiteSpace}+{AdjPhrase}){Month}({WordSpaces}{NumberTag})?{CloseNP} 
OneNumber = {OpenNP}{WordSpaces}{NumberTag}{CloseNP} 
TimeDat = {OpenNP}d~{CloseNP} 

Temporal = {TimeAcc} | {TimeMonth} | {OneNumber}
TemporalDat = {TimeDat}{WhiteSpace}+{AdvPWords}


%%
{Function}	{out.write(yytext());}	/* Don't touch the phrases that have already been function marked */
{PPPhrase}	{out.write(yytext());}	/* Don't touch PP phrases */


{Temporal}	{out.write(TempOpen+yytext()+TempClose);}	

{TemporalDat}	{	/* Find where the AdvP started and insert the Temporal label */
			StringSearch.splitString(yytext(),"[AdvP", true, -1);
			out.write(TempOpen+StringSearch.firstString+TempClose+StringSearch.nextString);
		}

"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.		{ out.write(yytext());}

