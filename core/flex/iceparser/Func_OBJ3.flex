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
 
/* This transducer marks objects of complement adjective phrases */
/* Also marks standalone accusative NP, temporal expressions like 	 */
/* [NP eitt foheo vor nheo NP] as having a *TIMEX function 	 */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Func_OBJ3
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  
  String ObjAP1Open=" {*OBJAP< ";
  String ObjAP1Close=" *OBJAP<} ";
  String ObjAP2Open=" {*OBJAP> ";
  String ObjAP2Close=" *OBJAP>} ";

  String Comp1Open=" {*COMP< ";
  String Comp1Close=" *COMP<} ";
  
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
%include verbLexicon.txt

DatObj = {NPDat} | {NPsDat}
AdjCompl = {OpenComp}{WhiteSpace}+{OpenAP}~{CloseAP}{WhiteSpace}+{CloseComp}
VPPastCompl = {OpenComp}{WhiteSpace}+{OpenVPp}~{CloseVPp}{WhiteSpace}+{CloseComp} 

PPPhrase = {OpenPP}~{ClosePP}
//NPAccusative = {NPAcc} | {NPsAcc}

OpenObjNom = "{*OBJNOM"[<>]?{Error}?
CloseObjNom = "*OBJNOM"[<>]?"}"
ObjNom = {OpenObjNom}~{CloseObjNom}

VPPast = {OpenVPp}~{CloseVPp}
VPPastSeq = {VPPast}({WhiteSpace}+{ConjPhraseOrComma}{WhiteSpace}+{VPPast})*
Complement = {APNom} | {APsNom} | {NPNom} | {NPOther} | {NPsNom} | {NPsOther} |{NPForeign} | {NPsForeign} | {VPPastSeq}

/* A temporal expression, e.g. 
	[NP tíu tfhfo skref nhfo NP]
	[NP dag nkeo einn fokeo NP]
	[NP eitt foheo vor nheo NP]
*/

PronounTag = {encodeOpen}f[oa]{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
NounNumeral = {NounTag}{WordSpaces}{NumeralTag}
NumeralNoun = {NumeralTag}{WordSpaces}{NounTag}
PronounNoun = {PronounTag}{WordSpaces}{NounTag}
NounPronoun = {NounTag}{WordSpaces}{PronounTag}
AdjPhrase = {OpenAP}a?~{CloseAP}

TimeAcc = {OpenNP}a({WhiteSpace}+{AdjPhrase})?{WordSpaces}({NounNumeral}|{NumeralNoun}|{NounPronoun}|{PronounNoun}){CloseNP} 

Temporal = {TimeAcc}

/* A dative object preceding or following an adjective complement, [VPb hafa sfg3fn verið ssg VPb] [NP manninum nkeþg NP] {*COMP< [AP hugleikin lhfnsf AP] *COMP<} */

ObjDat = {DatObj}({WhiteSpace}+{FuncQualifier})?
ObjDatCompl = {ObjDat}{WhiteSpace}+{AdjCompl}
ComplObjDat = {AdjCompl}{WhiteSpace}+{ObjDat}


VPDat = {OpenVP}{WhiteSpace}+{VerbDat}~{CloseVP}

SubjVerbObjNom = {FuncSubject}{WhiteSpace}+{VPDat}{WhiteSpace}+{ObjNom}
VerbSubjObjNom = {VPDat}{WhiteSpace}+{FuncSubject}{WhiteSpace}+{ObjNom}

ObjNomCompl = ({SubjVerbObjNom}|{VerbSubjObjNom}){WhiteSpace}+{Complement}

%%
{Function}	{out.write(yytext());}	/* Don't touch the phrases that have already been function marked */
{PPPhrase}	{out.write(yytext());}	/* Don't touch PP phrases */

/* {Temporal}	{out.write(TempOpen+yytext()+TempClose);}	This is catching more wrong than right	*/ 

{ObjDatCompl}	{ 
			/* Find where the Complement function started and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"{*COMP<", true, -1);		
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"{*COMP", true, -1);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(ObjAP2Open+StringSearch.firstString+ObjAP2Close+StringSearch.nextString);
			}
		}
{ComplObjDat}	{ 
			/* Find where the Complement function ended and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"*COMP<}", true, 7);		
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"*COMP}", true, 6);			
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+ObjAP1Open+StringSearch.nextString+ObjAP1Close);
			}
		}

{ObjNomCompl}	{

			/* Find where the second adverb phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"}", false, 1);	
//System.err.println("comp-2");	
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
	}


"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.		{ out.write(yytext());}

