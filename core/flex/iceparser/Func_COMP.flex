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
import is.iclt.icenlp.core.utils.ErrorDetector;
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
  String SubjOpen=" {*SUBJ> ";
  String SubjClose=" *SUBJ>} ";
  
  int theIndex=0;
  boolean agreement = false;  // -a parameter
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF) 
      	    yylex();
  }



    // order 1 = s1 {comp s2 comp}
	// order 2 = {comp s1 comp} s2
	public String AgreementCheck(String s1, String s2, String open, String close, int order)
	{
		if (agreement)
		{
			return ErrorDetector.CompAgreementCheck(s1, s2, open, close, order);
		}
		else
		{
			if (order == 1)
			{
				return s1+open+s2+close;
            }
			else
			{
				return  open+s1+close+s2;
			}
		}
	}

	public void set_doAgreementCheck(boolean option)
	{
		agreement = option;
	}
	public void set_markGrammarError(boolean markGrammarError)
	{
		// If we want grammatical errors to be shown then make sure that the agreement flag is true as well
        agreement = markGrammarError;
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
//Complement = {APNom} | {APsNom} | {NPNom} | {VPPastSeq}

SubjectVerbBe = {FuncSubject}{WhiteSpace}+{VPBe}{WhiteSpace}+

// This covers a case like: "Hann er ekki mjög góður kennari"
SubjVerbAdvPCompl = {SubjectVerbBe}({MWE_AdvP}|{AdvP}){WhiteSpace}+{Complement}
// This covers a case like: "Hann er a.m.k. ekki mjög góður kennari"
SubjVerbMWEAdvPCompl = {SubjectVerbBe}{MWE_AdvP}{WhiteSpace}+{AdvP}{WhiteSpace}+{Complement}
SubjVerbCPCompl = {SubjectVerbBe}{CP}{WhiteSpace}+{Complement}
SubjVerbNPCompl = {SubjectVerbBe}({AdvP}{WhiteSpace}+)?({NPAcc}|{NPDat}){WhiteSpace}+{Complement}  /* Það var mér tilhlökkunarefni */


SubjVerbCompl = {SubjectVerbBe}({FuncQualifier}{WhiteSpace}+)?{Complement}
//leyfa comp a undan lika, þarf að testa.
//SubjVerbCompl = {SubjectVerbBe}(({FuncQualifier}{WhiteSpace}+)?{Complement}|{Complement}{WhiteSpace}+{FuncQualifier}?)


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

// GÖL
// [NPa unginn ^nkeog$  NP] [VPb  er ^sfg3en$  VPb] [AdvP some ^tag$ AdvP] [APn  gul ^lvensf$ AP]
NounVerbComp = {NP}{WhiteSpace}+{VPBe}{WhiteSpace}+({AdvP}{WhiteSpace}+)?{Complement}


%%


{SubjVerbAdvPCompl} 	{ 
				//Find where the Verbe Be phrase ends and search for the possible complement tags from there.
	//System.err.println("comp-1");
	//System.err.println(yytext());



				theIndex = yytext().indexOf("VPb]")+4;

				String afterVPb = yytext().substring(theIndex, yytext().length());
				String firstPart = "";
				String secondPart = "";
				
				if(afterVPb.contains("[NP"))
				{
					firstPart = yytext().substring(0, theIndex + afterVPb.indexOf("[NP"));
					secondPart = yytext().substring(theIndex + afterVPb.indexOf("[NP"), yytext().length());
				}
				else if(afterVPb.contains("[VP"))
				{
					firstPart = yytext().substring(0, theIndex + afterVPb.indexOf("[VP"));
					secondPart = yytext().substring(theIndex + afterVPb.indexOf("[VP"), yytext().length());
				}
				else if(afterVPb.contains("[AP"))
				{
					firstPart = yytext().substring(0, theIndex + afterVPb.indexOf("[AP"));
					secondPart = yytext().substring(theIndex + afterVPb.indexOf("[AP"), yytext().length());
				}

				
				if(yytext().indexOf("VPb]") == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(AgreementCheck(firstPart,secondPart,Comp1Open,Comp1Close,1));
//					out.write(firstPart + Comp1Open + secondPart + Comp1Close);
				}


				// old.
/*
				// Find where the Adverb phrase ended and insert the COMP label 
				theIndex = StringSearch.splitString(yytext(),"AdvP]", true, 5);	
	
				if(theIndex == -1)
				{
					out.write(yytext());
				}
				else
				{
					out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				}
*/
			} 
{SubjVerbMWEAdvPCompl}  {

				/* Find where the second adverb phrase ended and insert the COMP label */
				theIndex = StringSearch.splitString(yytext()," AdvP]", true, 6);	
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

{SubjVerbNPCompl}	{

				/* Find where the NP phrase ended after the verb phrase and insert the COMP label */
				theIndex = StringSearch.splitString2(yytext(),"VPb]","NP]");	
	//System.err.println("comp-3");	
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
	//System.err.println("comp-4");	
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
	//System.err.println("comp-5");
	//System.err.println(yytext());	
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(AgreementCheck(StringSearch.firstString,StringSearch.nextString,Comp1Open,Comp1Close,1));
//				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
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
	//System.err.println("comp-6");
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
//	System.out.println("gDB>> Func_COMP(SubjCompl)="+StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);

				out.write(AgreementCheck(StringSearch.firstString,StringSearch.nextString,Comp0Open,Comp0Close,1));
//				out.write(StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);
			}
		} 
{SubjPPCompl}	{
			/* Find where the Preposition phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);	
	//System.err.println("comp-7");	
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
//	System.out.println("gDB>> Func_COMP(SubjPPCompl)="+StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);
				out.write(StringSearch.firstString+Comp0Open+StringSearch.nextString+Comp0Close);
			}
		} 		
		
{VerbSubjCompl}	{
			/* Find where the Subj function ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);
	//System.err.println("comp-8");		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
	//			out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
				out.write(AgreementCheck(StringSearch.firstString,StringSearch.nextString,Comp1Open,Comp1Close,1));
			}
		} 
{VerbCompl}	{
			/* Find where the Verb phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"VPb]", false, 4);	
	//System.err.println("comp-9");	
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
	//System.err.println("comp-10");	
	//System.err.println(yytext());		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(Comp2Open+StringSearch.firstString+Comp2Close+StringSearch.nextString);
	//System.err.println("First : \n" + StringSearch.firstString);
	//System.err.println("Second : \n" + StringSearch.nextString);
			}
		} 
{VerbAdvPCompl}	{
			/* Find where the Adverbial phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"AdvP]", true, 5);/////////////////var upprunalega false/*/-/-/-/-/-/-/-/-///
	//System.err.println("comp-11");
	//System.err.println(yytext());		
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
	//System.err.println("First : \n" + StringSearch.firstString);
	//System.err.println("Second : \n" + StringSearch.nextString);
			}
		} 
		
{VerbPPCompl}	{
			/* Find where the Preposition phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);	
	//System.err.println("comp-12");	
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		}

// GÖL
// this grabs and inserts
{NounVerbComp} {
			/* Find where the Preposition phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"NP]", false, 3);

			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(SubjOpen+StringSearch.firstString+SubjClose+StringSearch.nextString);
			}
		}

"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n");
 		}
.		{ out.write(yytext());
		}











