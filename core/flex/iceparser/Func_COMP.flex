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

/* This transducer marks potential complements */
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Func_COMP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String Comp0Open=" {*COMP ";
  String Comp0Close=" *COMP} ";
  String Comp1Open=" {*COMP< ";
  String Comp1Close=" *COMP<} ";
  String Comp2Open=" {*COMP> ";
  String Comp2Close=" *COMP>} ";
  
  int theIndex=0;
  
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

VPBe = {OpenVPb}~{CloseVPb}
VPInf = {OpenVPi}~{CloseVPi}
VPInfVera = {OpenVPi}~verð?a{WhiteSpace}+{VerbInfinitiveTag}{CloseVPi}
VPPast = {OpenVPp}~{CloseVPp}
VPOther = {OpenVP}" "~{CloseVP}
//VPSpecial = {OpenVP}" "~{CloseVP}
NP = {OpenNP}[adg]~{CloseNP}
NPs = {NPsAcc}|{NPsDat}|{NPsGen}
AdvP = {OpenAdvP}~{CloseAdvP}
//AdvPs = (({AdvP}|{MWE}){WhiteSpace}+)+
AdvPs = (({AdvP}|{MWE_AdvP}){WhiteSpace}+)+
PP = {OpenPP}~{ClosePP}
CP = {OpenCP}~{CloseCP}

VPPastSeq = {VPPast}({WhiteSpace}+{ConjPhraseOrComma}{WhiteSpace}+{VPPast})*
Complement = {APNom} | {APsNom} | {NPNom} | {NPsNom} | {VPPastSeq} 

SubjectVerbBe = {FuncSubject}{WhiteSpace}+{VPBe}{WhiteSpace}+

// This covers a case like: "Hann er ekki mjög góður kennari"
SubjVerbAdvPCompl = {SubjectVerbBe}({MWE_AdvP}|{AdvP}){WhiteSpace}+{Complement}
// This covers a case like: "Hann er a.m.k. ekki mjög góður kennari"
SubjVerbMWEAdvPCompl = {SubjectVerbBe}{MWE_AdvP}{WhiteSpace}+{AdvP}{WhiteSpace}+{Complement}
SubjVerbCPCompl = {SubjectVerbBe}{CP}{WhiteSpace}+{Complement}
SubjVerbNPCompl = {SubjectVerbBe}({AdvP}{WhiteSpace}+)?({NPAcc}|{NPDat}){WhiteSpace}+{Complement}  /* Það var mér tilhlökkunarefni */
SubjVerbCompl = {SubjectVerbBe}({FuncQualifier}{WhiteSpace}+)?{Complement}

//SubjVerbVerbPastCompl = {SubjectVerbBe}{VPPast}{WhiteSpace}+{Complement}	/* hún var orðin leið */
//SubjVerbSpecialCompl = {FuncSubject}{WhiteSpace}+{VP}{WhiteSpace}+{Complement}	/* ég heiti Eva */

SubjCompl = {FuncSubject}{WhiteSpace}+{Complement}
SubjPPCompl = {FuncSubject}{WhiteSpace}+{PP}{WhiteSpace}+{Complement}
VerbSubjCompl = {VPBe}{WhiteSpace}{FuncSubject}{WhiteSpace}+({FuncQualifier}{WhiteSpace}+)?{Complement}
VerbCompl = ({VPBe}|{VPInfVera}){WhiteSpace}+{Complement}
ComplVerb = {Complement}{WhiteSpace}+{VPBe}
//VerbAdvPCompl = {VPBe}{WhiteSpace}+{AdvPs}{Complement}
VerbAdvPCompl = {VPBe}{WhiteSpace}+{AdvPs}{Complement}
VerbPPCompl = {VPBe}{WhiteSpace}+{PP}{WhiteSpace}+{Complement}


%%


{SubjVerbAdvPCompl} 	{ 
				/* Find where the Adverb phrase ended and insert the COMP label */
				theIndex = StringSearch.splitString(yytext(),"AdvP]", true, 5);		
				if(theIndex == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				}
			} 
{SubjVerbMWEAdvPCompl}  {
				/* Find where the second adverb phrase ended and insert the COMP label */
				theIndex = StringSearch.splitString(yytext()," AdvP]", true, 6);		
				if(theIndex == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				}
			} 

{SubjVerbNPCompl}	{ 
				/* Find where the NP phrase ended after the verb phrase and insert the COMP label */
				theIndex = StringSearch.splitString2(yytext(),"VPb]","NP]");		
				if(theIndex == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				}
			} 			

{SubjVerbCPCompl}	{ 
				/* Find where the CP phrase ended and insert the COMP label */
				theIndex = StringSearch.splitString(yytext(),"CP]", true, 3);		
				if(theIndex == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				}
			}			
{SubjVerbCompl}	{ 
			/* Find where the Verb phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"VPb]", false, 4);		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		} 
/*
{SubjVerbSpecialCompl}	{ 
			StringSearch.splitString(yytext(),"VP]", false, 3);		
			out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}

{SubjVerbVerbPastCompl}	{ 
			// Find where the Verb phrase ended and insert the COMP label
			StringSearch.splitString(yytext(),"VPb]", false, 4);		
			out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
		} 
*/		
{SubjCompl}	{ 
			/* Find where the func subject phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"*SUBJ}", false, 6);
			if (theIndex == -1) 
			{
				theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", false, 7); 
				if (theIndex == -1) 
					theIndex = StringSearch.splitString(yytext(),"*SUBJ>}", false, 7); 
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);
			}
		} 
{SubjPPCompl}	{ 
			/* Find where the Preposition phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);
			}
		} 		
		
{VerbSubjCompl}	{ 
			/* Find where the Subj function ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		} 
{VerbCompl}	{ 
			/* Find where the Verb phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"VPb]", false, 4);		
			if (theIndex == -1) 
			{
				theIndex = StringSearch.splitString(yytext(),"VPi]", false, 4);
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		} 
{ComplVerb}	{ 
			/* Find where the Verb phrase started and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"[VPb", false, -1);		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(Comp2Open+StringSearch.firstString+Comp2Close+StringSearch.nextString);
			}
		} 
{VerbAdvPCompl}	{ 
			/* Find where the Adverbial phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"AdvP]", false, 5);		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		} 
		
{VerbPPCompl}	{ 
			/* Find where the Preposition phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);		
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
