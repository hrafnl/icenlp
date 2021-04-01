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
 
/* This transducer marks potential subjects 					*/
/* A potential subject is:							*/
/* a nominative NP which precedes/follows a finite verb phrase 			*/
/* a nominative NP which precedes a relative conjunction			*/
/* a dative NP which precedes/follows a verb which demands a dative subject 	*/
/* an accusative NP which precedes/follows a verb which demands an accusative subject */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
import is.iclt.icenlp.core.utils.IceParserUtils;
import is.iclt.icenlp.core.utils.ErrorDetector;
%%

%public
%class Func_SUBJ
%standalone
%line
%extends IceParserTransducer
%unicode

%{
	String Func0Open=" {*SUBJ ";
	String Func0Close=" *SUBJ} ";
	String Func1Open=" {*SUBJ> ";
	String Func1Close=" *SUBJ>} ";
	String Func2Open=" {*SUBJ< ";
	String Func2Close=" *SUBJ<} ";

	static String encO =  IceParserUtils.encodeOpen;
	static String encC =  IceParserUtils.encodeClose;

	boolean agreement = false;  // -a parameter
    boolean markGrammarError = false; // -e parameter
	int theIndex=0;

	//java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
	java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));

	public void set_doAgreementCheck(boolean option)
	{
		agreement = option;
	}  
	public void set_markGrammarError(boolean option)
	{
		markGrammarError = option;
		// If we want grammatical errors to be shown then make sure that the agreement flag is true as well
                if (markGrammarError)
                        agreement = true;
	}

	public void parse(java.io.Writer _out) throws java.io.IOException
	{
	  	out = _out;
	  	while (!zzAtEOF) 
	  	    yylex();
	}

/*	public String AgreementCheck(String s1, String s2, String s3, String s4, int order)
	{
		return ErrorDetector.AgreementCheck(s1,s2,s3,s4,order,agreement,markGrammarError);
	}
 */

	  
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
VP = {OpenVP}" "~{CloseVP}
//VP = {OpenVP}(" "|s)~({CloseVP}|{CloseVPs})
VPPast = {OpenVPp}~{CloseVPp}
VPorVPBe = {VP}|{VPBe} 
VPDat = {OpenVP}{WhiteSpace}+{VerbDat}~{CloseVP}
VPAcc = {OpenVP}{WhiteSpace}+{VerbAcc}~{CloseVP} 
RelCP = {OpenSCP}{WhiteSpace}+sem{WhiteSpace}+{ConjTag}~{CloseSCP}
AdvP = {OpenAdvP}~{CloseAdvP}
PP = {OpenPP}~{ClosePP}

PPorQual = ({PP}{WhiteSpace}+)+ | {FuncQualifier}{WhiteSpace}+ (({PP}{WhiteSpace}+)+)?
/* Qual = {FuncQualifier}{WhiteSpace}+ */

SubjectVerb = 	({FuncQualifier}{WhiteSpace}+)?({NomSubject}|{NPNum}){WhiteSpace}+{PPorQual}?{VPorVPBe} 	| 
		{DatSubject}{WhiteSpace}+{PPorQual}?{VPDat} 	| 
		{AccSubject}{WhiteSpace}+{PPorQual}?{VPAcc}
		
/* SubjectAPVerb = {APNom}{WhiteSpace}+{VPorVPBe} */
		
SubjectVerbMissing = {NomSubject}{WhiteSpace}+({PP}{WhiteSpace}+)?({OpenAP}~{CloseAP} | {OpenAPs}~{CloseAPs} | {VPPast}) 
		
/* The subject can follow the verb */
VerbSubject = 	
		// {VPorVPBe}{WhiteSpace}+({FuncQualifier}{WhiteSpace}+)?{NomSubject}({WhiteSpace}+{FuncQualifier})? | /* kom hann þá */
		{VPorVPBe}{WhiteSpace}+{PPorQual}?{NomSubject}({WhiteSpace}+{FuncQualifier})? | /* kom hann þá */
		{VPDat}{WhiteSpace}+{DatSubject} | 	/* þótti mér hann erfiður */
		{VPAcc}{WhiteSpace}+{AccSubject}  	/* langaði mig að .. */
		
VerbAdvPSubject = {VPorVPBe}{WhiteSpace}+{AdvP}{WhiteSpace}({FuncQualifier}{WhiteSpace}+)?{NomSubject}({WhiteSpace}+{FuncQualifier})?  /* hafði þá samingagerðin .. */
		
//PPVerbSubject = ({AdvP}{WhiteSpace}+({PP}{WhiteSpace}+)? | {PP}{WhiteSpace}+) {VPBe}{WhiteSpace}+{NomSubject}	/* kannski voru allir; innst í honum var maður, í bílnum var ... */
SubjectRel = {NomSubject}{WhiteSpace}+({FuncQualifier}{WhiteSpace}+)?{RelCP}


%%


{SubjectVerb}
{
//System.err.println("subj-1");
	String str = yytext();
	if (str.contains("[PP"))	/* We don't want the preposition phrase to be included */
		theIndex = StringSearch.splitString(str, "[PP", true, -1);

	else
		theIndex = StringSearch.splitString(str,"[VP", true, -1);		
	if(theIndex == -1)
	{
		out.write(yytext());
	}
	else
	{
	//	out.write(AgreementCheck(Func1Open,StringSearch.firstString,Func1Close,StringSearch.nextString,1));
		if (agreement)
		{
			out.write(ErrorDetector.agreementSubjectVerbCheckNumberAndPerson(StringSearch.firstString,StringSearch.nextString,Func1Open,Func1Close,0));
		}
		else
		{
			out.write(Func1Open + StringSearch.firstString + Func1Close + StringSearch.nextString);
		}
// missing agreementCheckGenderPerson   fpkeo = 3 pers
	}
}


//{SubjectAPVerb}	{ 
//			/* Find where the AP ended and insert the SUBJ label */
//			StringSearch.splitString(yytext(),"AP]", true, 3);
//			out.write(Func1Open+StringSearch.firstString+Func1Close+StringSearch.nextString);
//		} 



{SubjectVerbMissing}	
{
//System.err.println("subj-2");
	String str = yytext();
	if (str.contains("[PP"))
		theIndex = StringSearch.splitString(str, "[PP", true, -1);
	else 
	{
		// Find where the NPs/NP ended and insert the SUBJ label 
		theIndex = StringSearch.splitString(str,"NPs]", true, 4);
		if (theIndex == -1)
			theIndex = StringSearch.splitString(str,"NP]", true, 3);		
	}
	if(theIndex == -1)
	{
		out.write(yytext());
	}
	else
	{
//		out.write(AgreementCheck(Func0Open,StringSearch.firstString,Func0Close,StringSearch.nextString,1));
		out.write(Func0Open+StringSearch.firstString+Func0Close+StringSearch.nextString);
	}
} 
		
{VerbSubject}	
{
//System.err.println("subj-3");
	String str = yytext();
	if (str.contains(" PP]"))
	{
			StringSearch.splitString(str, " PP]", true, 4);
	}
	else 
	{
		// Find where the VP ended and insert the SUBJ label 
		if (str.contains(" VPb]"))
			theIndex = StringSearch.splitString(str," VPb]", true, 5);	
		else if (str.contains(" VPs]"))
			theIndex = StringSearch.splitString(str," VPs]", true, 5);
		else
			theIndex = StringSearch.splitString(str," VP]", true, 4);		
	}
	if(theIndex == -1)
	{
		out.write(yytext());
	}
	else
	{
		if (agreement)
		{
			out.write(ErrorDetector.agreementSubjectVerbCheckNumberAndPerson(StringSearch.firstString,StringSearch.nextString,Func2Open,Func2Close,1));
		}
		else
		{
//			out.write(StringSearch.firstString + Func1Open + StringSearch.nextString + Func1Close);
			out.write(StringSearch.firstString + Func2Open + StringSearch.nextString + Func2Close);
		}
	}
}
		
{VerbAdvPSubject}	
{
//System.err.println("subj-4");
	String str = yytext();
	String searchFor=null;
	if (str.contains("VPb]"))
		searchFor = "VPb]";
	else if (str.contains("VPs]"))
		searchFor = "VPs]";
	else
		searchFor = "VP]";
	theIndex = StringSearch.splitString2(yytext(),searchFor,"AdvP]");
	if(theIndex == -1)
	{
		out.write(yytext());
	}
	else
	{
//		out.write(AgreementCheck(StringSearch.firstString,Func2Open,StringSearch.nextString,Func2Close,2));
		out.write(StringSearch.firstString+Func2Open+StringSearch.nextString+Func2Close);
	}
}
			
//{PPVerbSubject}	{ 
//			String str = yytext();
//			if (str.contains("{*QUAL")) {	// Make sure the qualifier is a part of the subject 
//				StringSearch.splitString(yytext(),"{*QUAL", true, -1);		
//			}
//			else {
//				// Find where the NPs/NP started and insert the SUBJ label 
//				theIndex = StringSearch.splitString(yytext(),"[NPs", false, -1);
//				if (theIndex == -1)
//					StringSearch.splitString(yytext(),"[NP", false, -1);		
//			}
//			out.write(StringSearch.firstString+Func2Open+StringSearch.nextString+Func2Close);
//			
//		}

{SubjectRel}	
{
//System.err.println("subj-5");
	// Find where the relative phrase started and the NP ended and insert the SUBJ label 
	theIndex = StringSearch.splitString(yytext(),"[SCP", true, -1);		
	if(theIndex == -1)
	{
		out.write(yytext());
	}
	else
	{
//		out.write(AgreementCheck(Func1Open,StringSearch.firstString,Func1Close,StringSearch.nextString,1));
		out.write(Func1Open+StringSearch.firstString+Func1Close+StringSearch.nextString);
	}
}
"\n"
{
	//System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
	out.write("\n"); 
}
.
{ 
	out.write(yytext());
}





