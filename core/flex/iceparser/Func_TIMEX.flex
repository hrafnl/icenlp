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
%extends IceParserTransducer
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
NPsPhrase = {OpenNPs}~{CloseNPs}

/* A temporal expression, e.g. 
	[NP 1982 ta NP] 
	[NP árið nheog 1982 ta NP]
	[NP [AP 30. lkeovf AP] apríl nkeo NP]
	[NP [AP 30. lkeovf AP] apríl nkeo 1939 ta NP]
	[NP nokkrum fokfþ dögum nkfþ NP] [AdvP síðar/seinna aam AdvP]
*/

NumeralTag = {encodeOpen}t[ao]{encodeClose}{WhiteSpace}+
Numeral = {WordSpaces}{NumeralTag}
PronounTag = {encodeOpen}f~{encodeClose}{WhiteSpace}+
Pronoun = {WordSpaces}{PronounTag}
NounNumeral = {WordSpaces}{NounTag}{Numeral}
AdjPhrase = {OpenAP}a?~{CloseAP}{WhiteSpace}+
AdvPWords =  {OpenAdvP}{WhiteSpace}+(s(íðar|einna)|fyrr){WhiteSpace}+{AdverbTag}{CloseAdvP}{WhiteSpace}+
Klukkan = [kK]lukkan{WhiteSpace}+{NounTag}
AdjOrNumOrPro = ({AdjPhrase}|{Numeral}|{Pronoun})

Day =  ([mM]ánu		|
		[þÞ]riðju	|
		[mM]iðviku	|
		[fF]immtu	|
		[fF]östu	|
		[lL]augar	|
		[sS]unnu	|
		[fF]yrr[ai]	|	
		[gG]ær		|
		[mM]orgun)dag(inn)?

Month =	jan(\.|úar)		|
		feb(\.|rúar)	|
		mar(\.|s)		|
		apr(\.|íl)		|
		maí				|
		jún(\.|í)		|
		júl(\.|í)		|
		ágú(\.|st)		|
		sep(\.|tember)	|
		okt(\.|óber)	|
		nóv(\.|ember)	|
		des(\.|ember)

Season =	[vV]etur(inn)?	|
			[vV]or(ið)?		|
			[sS]umar(ið)?	|
			[hH]aust(ið)?

Duration = 	[sS]ekúndu(na|r(nar)?)?			|
			[mM]ínútu(na|r(nar)?)?			|
			[kK]lukkutíma(nn|na)?			|
			[kK]lukkustund(ina|ir(nar)?)?	|
			[dD]ag(inn|a(na)?)?				|
			[sS]ólarhring(inn|a(na)?)?		|
			[vV]iku(na|r(nar)?)?			|
			[mM]ánuð(inn|i(na)?)?			|
			[áÁ]r(ið|in)?					|
			[áÁ]ratug(inn|i(na)?)?			|
			[tT]íma(nn|na)?					|
			[sS]tund(ina|ir(nar)?)?

TimeAndTag = ({Day}|{Month}|{Season}|{Duration}){WhiteSpace}+{NounTag}

TimeAcc = {OpenNP}a{NounNumeral}{CloseNP} 
TimeClock = {OpenNP}n{WhiteSpace}+{Klukkan}{Numeral}{CloseNP}
TimeSpan = {OpenNP}[nadg]?{WhiteSpace}+({TimeAndTag}{WhiteSpace}+)?{Numeral}{WhiteSpace}+{Symbol}{WhiteSpace}+{Numeral}{WhiteSpace}+{CloseNP}
TemporalPhrase = {OpenNP}a{WhiteSpace}+{AdjOrNumOrPro}?{TimeAndTag}{AdjOrNumOrPro}?{CloseNP}({WhiteSpace}+{NPGen})?
TemporalNPs = {OpenNPs}{WhiteSpace}+{TimeSpan}({WhiteSpace}+{ConjPhraseAndOrComma}{WhiteSpace}+{TimeSpan})+{WhiteSpace}+{CloseNPs}
Temporal = {TimeAcc} | {TemporalPhrase} | {TimeClock} | {TemporalNPs}

TimeDat = {OpenNP}d~{CloseNP}
TemporalDat = {TimeDat}{WhiteSpace}+{AdvPWords}


%%
{Function}	{out.write(yytext());}	/* Don't touch the phrases that have already been function marked */
{PPPhrase}	{out.write(yytext());}	/* Don't touch PP phrases */


{Temporal}	{out.write(TempOpen+yytext()+TempClose);}	

{TemporalDat}	{	
			// Find where the NPd ends and insert the temporal label to the AdvP after it

		
			theIndex = StringSearch.splitString(yytext()," NP]", false, 4);
			if(theIndex == -1)
			{	
				out.write(yytext());
			}
			else
			{
				out.write(TempOpen+StringSearch.firstString+TempClose+StringSearch.nextString);
			}
			
/*
			// Find where the AdvP started and insert the Temporal label 
			theIndex = StringSearch.splitString(yytext(),"[AdvP", true, -1);
			if(theIndex == -1)
			{	
				out.write(yytext());
			}
			else
			{
				out.write(TempOpen+StringSearch.firstString+TempClose+StringSearch.nextString);
			}
*/
		}

"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.		{ out.write(yytext());}

