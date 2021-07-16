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

/* This transducer marks indirect objects of ditransitive verbs 		*/
/* The direct object has been marked by a previous transducer			*/
/* Nominative adjective phrases which have not been assigned a function 	*/
/* are marked as complements as well as past participle verbs 			*/
/* Additionally, it marks objects and complements of past participle verbs 	*/
/* and nominative objects of verbs that demand oblique case subjects 		*/

package is.iclt.icenlp.core.iceparser;
import java.io.*;

import is.iclt.icenlp.core.utils.ErrorDetector;
%%

%public
%class Func_OBJ2
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String errorCode = "?Cg?";

  String Obj1Open=" {*OBJ< ";
  String Obj1Close=" *OBJ<} ";
  String Obj2Open=" {*OBJ> ";
  String Obj2Close=" *OBJ>} ";
  String IObjOpen=" {*IOBJ< ";
  String IObjClose=" *IOBJ<} ";
  
  
  String Comp0Open=" {*COMP ";
  String Comp0Close=" *COMP} ";
  String Comp1Open=" {*COMP< ";
  String Comp1Close=" *COMP<} ";
  
  String ObjNomOpen=" {*OBJNOM< ";
  String ObjNomClose=" *OBJNOM<} ";
  
  int theIndex = 0;
  boolean markGrammarError = false;  // -a parameter
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
	public void parse(java.io.Writer _out) throws java.io.IOException
	{
		out = _out;
		while (!zzAtEOF) {
		yylex();
		}
	}
		      // put the < next to the {*COMP
	private String SubjCompMismatch (String s1, String s2, String open, String close, int order)
	{
		System.out.println("SubjCompMismatch s1="+s1+" s2="+s2);
		if (markGrammarError)
		{
			return ErrorDetector.CompAgreementCheck(s1, s2, open, close, order);
		}
		else if (order == 1)
		{
			return s1+open+s2+close;
		}
		// if order is 2 then make it {comp s1 comp} s2
		else
		{
			return open+s1+close+s2;
		}
	}

	public void set_markGrammarError(boolean error)
	{
		// If we want grammatical errors to be shown then make sure that the agreement flag is true as well
       	markGrammarError = error;
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

//VP = {OpenVP}" "~{CloseVP}
VP = {OpenVP}(" "|s)~({CloseVP}|{CloseVPs})
VPInf = {OpenVPi}~{CloseVPi}
VPPast = {OpenVPp}~{CloseVPp}
PP = {OpenPP}~{ClosePP}
AdvP = {OpenAdvP}~{CloseAdvP}
//NPOblique = {OpenNP}[adg]~{CloseNP}
NPOblique = {NPAcc}|{NPDat}|{NPGen}
NPsOblique = {NPsAcc}|{NPsDat}|{NPsGen}

// Verbs that demand oblique case subjects
VPDat = {OpenVP}{WhiteSpace}+{VerbDat}~{CloseVP}
VPAcc = {OpenVP}{WhiteSpace}+{VerbAcc}~{CloseVP}

Object = ({NPOblique}|{NPsOblique})({WhiteSpace}+{FuncQualifier})?
NPDObj = "{*OBJ<"{WhiteSpace}+{DatObj}{WhiteSpace}+"*OBJ<}"
NPAObj = "{*OBJ<"{WhiteSpace}+{AccObj}{WhiteSpace}+"*OBJ<}"
AccObj = {NPAcc} | {NPsAcc}
DatObj = {NPDat} | {NPsDat}

CloseObj = "*OBJ"[<>]"}"
/* Intervening PP or AdvP is possible: bera okkur [PP á brýn PP] bókmenntasmekk */
VerbDatObjAccObj = ({VP}|{VPInf}){WhiteSpace}+{NPDObj}{WhiteSpace}+(({PP}|{AdvP}){WhiteSpace}+)?{AccObj}
VerbAccObjDatObj = ({VP}|{VPInf}){WhiteSpace}+{NPAObj}{WhiteSpace}+(({PP}|{AdvP}){WhiteSpace}+)?{DatObj}

VPPastCompl = {OpenComp}{WhiteSpace}+{OpenVPp}~{CloseVPp}{WhiteSpace}+{CloseComp} 

NPPhrases = {OpenNP}~{CloseNP} | {OpenNPs}~{CloseNPs}

Complement = {APNom} | {APsNom} | {NPNom} | {NPOther} | {NPsNom} | {NPsOther} |{NPForeign} | {NPsForeign} | {VPPastSeq}

Complement1 = {APsNom}
Complement2 = {APNom} | {VPPast}

Subj = {OpenSubj}~{CloseSubj}
SubjVerbComp = {Subj}{WhiteSpace}+{VP}{WhiteSpace}+{APNom}
SubjVerbAdvPComp = {Subj}{WhiteSpace}+{VP}{WhiteSpace}+{AdvP}{WhiteSpace}+{APNom}

// Complement of a complement: "Hún var orðin húsfrú"

ComplCompl = {VPPastCompl}{WhiteSpace}+({Complement1}|{Complement2}|({NPNom}|{NPsNom})({WhiteSpace}+{FuncQualifier})?)

// An object that follows a past participle complement
ComplObj = {VPPastCompl}{WhiteSpace}+({NPAcc}|{NPDat}|{NPGen}|{NPsAcc}|{NPsDat}|{NPsGen})({WhiteSpace}+{FuncQualifier})?

// An object which follows an infinitive verb phrase inside a PP 
PPVPInfObj = {VPInf}{WhiteSpace}*{ClosePP}{WhiteSpace}+{Object}

// A nominative object which follows a verb which demands an oblique case subject
FuncSubjectOblique = {OpenSubj}{WhiteSpace}+({NPOblique}|{NPsOblique})~{CloseSubj}
SubjVerbObjNom = {FuncSubjectOblique}{WhiteSpace}+{VPDat}{WhiteSpace}+{NomSubject}
//SubjVerbObjNom = {FuncSubjectOblique}{WhiteSpace}+{OpenVP}{WhiteSpace}+{VerbDat}{CloseVP}{WhiteSpace}+{NomSubject}
//SubjVerbObjNom = {FuncSubjectOblique}{WhiteSpace}+"[VP"{WhiteSpace}+{VerbDat}~"VP]"{WhiteSpace}+{NomSubject}
VerbSubjObjNom = {VPDat}{WhiteSpace}+{FuncSubjectOblique}{WhiteSpace}+{NomSubject} // VPDat is a problem
//VerbSubjObjNom = {OpenVP}{WhiteSpace}+{VerbDat}{CloseVP}{WhiteSpace}+{FuncSubjectOblique}{WhiteSpace}+{NomSubject} // VPDat is a problem
//VerbSubjObjNom = "[VP"{WhiteSpace}+{VerbDat}~"VP]"{WhiteSpace}+{FuncSubjectOblique}{WhiteSpace}+{NomSubject} // VPDat is a problem

%%

{VerbDatObjAccObj}
	{
//			System.out.println("VerbDatObjAccObj");
////	System.err.println("obj2-1");
////	System.err.println(yytext());

			String matchedStr = yytext();
			String newStr = matchedStr.replaceAll("\\*OBJ<","*IOBJ<");	// change to indirect object
   			//making sure to check for the tags after the object and before the NPa because the same tags can occur elsewhere
			int objIndex = newStr.indexOf("*IOBJ<}");
			String afterObj = newStr.substring(objIndex, newStr.length());

			int npIndex = afterObj.lastIndexOf("[NPs"); // this is a possible danger, might have to become "[NPs" without " "
			if(npIndex == -1)
            {
				npIndex = afterObj.lastIndexOf("[NPa");
				if (npIndex == -1)
				{
					String nphrase = afterObj.replaceAll(".*(\\[NP\\?(N[gnc]a?)*\\?a).*","$1"); // because of error codes
					npIndex = afterObj.lastIndexOf(nphrase);
				}
//				npIndex = afterObj.lastIndexOf("[NPa");
//				npIndex = afterObj.lastIndexOf("[NPa ");    this has been commented out due to [NPa? existing and messing everything up with that whitespace
            }
            if(npIndex != -1)
            {
				npIndex += objIndex;
            }

			String middlePart = newStr.substring(newStr.indexOf("*IOBJ<}")+7, npIndex);
//	System.err.println("\n\nMiddlePart: \"" + middlePart + "\"\n\n");
			if (middlePart.contains(" PP]")) {	
				theIndex = StringSearch.splitString(newStr," PP]", true, 4);
			}
			else if (middlePart.contains(" AdvP]")) {
				theIndex = StringSearch.splitString(newStr," AdvP]", true, 6);		
			}
			else {
				// Find where the FuncObject phrase ended and insert the OBJ label 
				theIndex = StringSearch.splitString(newStr,"*IOBJ<}", false, 7);		
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			}



/*

			// First mark the object as an indirect object 
			
			String matchedStr = yytext();
			String newStr = matchedStr.replaceAll("\\*OBJ<","*IOBJ<");	// change to indirect object
			
			if (newStr.contains("PP]")) {	
				theIndex = StringSearch.splitString(newStr,"PP]", true, 3);		
			}
			else if (newStr.contains("AdvP]")) {		
				theIndex = StringSearch.splitString(newStr,"AdvP]", true, 5);		
			}
			else {
				// Find where the FuncObject phrase ended and insert the OBJ label 
				theIndex = StringSearch.splitString(newStr,"*IOBJ<}", false, 7);		
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			}
*/
		} 		
{VerbAccObjDatObj}	{ 
//	System.err.println("obj2-2");
//	System.err.println(yytext());
//			System.out.println("VerbAccObjDatObj");

			// The first object is the direct object 
			String matchedStr = yytext();
			
			int vpIndex = matchedStr.indexOf(" VP]") + 4;
			String middlePart = matchedStr.substring(vpIndex, matchedStr.length());

			String first = "";
			String second = "";

			if (middlePart.contains(" PP]")) {	
				int ppIndex = middlePart.indexOf(" PP]") + 4;
				first = matchedStr.substring(0, vpIndex + ppIndex);		
				second = matchedStr.substring(vpIndex + ppIndex, matchedStr.length());
			}
			else if (middlePart.contains("AdvP]")) {
				int advpIndex = middlePart.indexOf("AdvP]") + 5;
				first = matchedStr.substring(0, vpIndex + advpIndex);
				second = matchedStr.substring(vpIndex + advpIndex, matchedStr.length());
			}
			else {
				// Find where the FuncObject phrase ended and insert the IOBJ label
				int objIndex = middlePart.indexOf("*OBJ<}") + 6;
				first = matchedStr.substring(0, vpIndex + objIndex);
				second = matchedStr.substring(vpIndex + objIndex, matchedStr.length());
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(first+IObjOpen+second+IObjClose);
			}

/*
			// The first object is the direct object
			String matchedStr = yytext();

			if (matchedStr.contains("PP]")) {
				theIndex = StringSearch.splitString(matchedStr,"PP]", true, 3);
			}
			else if (matchedStr.contains("AdvP]")) {
				theIndex = StringSearch.splitString(matchedStr,"AdvP]", true, 5);
			}
			else {
				// Find where the FuncObject phrase ended and insert the IOBJ label
				theIndex = StringSearch.splitString(matchedStr,"*OBJ<}", false, 6);
			}
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+IObjOpen+StringSearch.nextString+IObjClose);
			}
*/
		}

{ComplObj}	{
//			System.out.println("ComplObj");
//	System.err.println("obj2-3");
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
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			}
		}

{ComplCompl}	{
//			System.out.println("VerbSubjObjNom");
//	System.err.println("obj2-4");
			/* Find where the Complement function ended and insert the COMP label */
		String compWithError = "*COMP<"+errorCode+"}";
				theIndex = StringSearch.splitString(yytext(),compWithError, true, compWithError.length()); // dangerous to have it this hard coded
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"*COMP<}", true, 7);
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"*COMP}", true, 6);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Comp1Open+StringSearch.nextString+Comp1Close);
			}
		}
{SubjVerbObjNom}  {
//	System.err.println("obj2-5");
			theIndex = StringSearch.splitString(yytext(),"VP]", false, 3);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				// System.out.println("gDB>>SubjVerbObjNom("+StringSearch.firstString+ObjNomOpen+StringSearch.nextString+ObjNomClose+")");
				out.write(StringSearch.firstString+ObjNomOpen+StringSearch.nextString+ObjNomClose);
			}
		  }
{VerbSubjObjNom}  {
//			System.out.println("VerbSubjObjNom");
//	System.err.println("obj2-6");
			theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", true, 7);
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"*SUBJ}", true, 6);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+ObjNomOpen+StringSearch.nextString+ObjNomClose);
			}
		  }

{PPVPInfObj}	{
//			System.out.println("PPVPInfObj");
//	System.err.println("obj2-7");
			/* Find where the PP phrase ended and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			}
		}

{Function}	{out.write(yytext());}	/* Don't touch the phrases that have already been function marked */

{NPPhrases}	{out.write(yytext());}	/* Don't touch NPs phrases */
{Complement1}	{
		out.write(Comp0Open+yytext()+Comp0Close);
		}
{Complement2}	{out.write(yytext());}
{SubjVerbComp} {

//			System.out.println("SubjVerbComp");
			/* Find where the VPb phrase ended and insert the COMP label */
			theIndex = StringSearch.splitString(yytext(),"VPb]", false, 4);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
					out.write(SubjCompMismatch(StringSearch.firstString,StringSearch.nextString,Comp1Open,Comp1Close,1));
			}
		}
{SubjVerbAdvPComp} {
//			System.out.println("SubjVerbAdvPComp");
			/* Find where the AdvP phrase ended and insert the COMP label */
	//		theIndex = StringSearch.splitString(yytext(),"AdvP]", false, 5);
			theIndex = StringSearch.splitString(yytext(),"[AP", false, -3);
			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(SubjCompMismatch(StringSearch.firstString,StringSearch.nextString,Comp1Open,Comp1Close,1));
			}
		}

"\n"		{ /*System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r");
		out.write("\n");*/ }
.		{ out.write(yytext());}
