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

/* The following patterns search for disagreement associated with verb phrases */

package is.iclt.icenlp.core.iceparser;

import java.io.*;
%%

%public
%class VP_errors
%standalone
%line

%unicode

%{
public String getTag(String str, int idx)
{
	// Split it into tokens and retrieve the token no. idx
	String[] tokens = str.split("\\s+");	
	if (tokens.length > idx) 
		return tokens[idx];
	
	else
		return null;
}

public String getSubjectTag(String str)
{
	// Remove subject, NP, AP, and AdvP markings
	// {*SUBJ> [NP ég fp1en NP] *SUBJ>}
	String stripped = str.replaceFirst("\\[NP","");
	stripped = stripped.replaceFirst("NP]","");
	stripped = stripped.replaceFirst("\\[AP","");
	stripped = stripped.replaceFirst("AP]","");
	stripped = stripped.replaceFirst("\\[AdvP.*AdvP]","");

	//System.out.println("Stripped: " + stripped);
	return getTag(stripped,2);
}

public String getCase(String tag)
{
	String theCase=null;
	int len = tag.length();
	if (len < 4) return theCase;

	String firstLetter = tag.substring(0,1);

	if (firstLetter.equals("n") || firstLetter.equals("l") || firstLetter.equals("g"))
		theCase = tag.substring(3,4);
	else if ((firstLetter.equals("f") || firstLetter.equals("t")) && len >=5) 
		theCase = tag.substring(4,5);

	return theCase;
}

public String getPersonNumber(String tag)
{
	String personNumber=null;

	int len = tag.length();
	if (len < 4) return personNumber;

	String firstLetter = tag.substring(0,1);

	if (firstLetter.equals("n") || firstLetter.equals("l") || firstLetter.equals("g"))
		personNumber = tag.substring(1,3);
	else if (firstLetter.equals("f") || firstLetter.equals("t")) 
		personNumber = tag.substring(2,4);
	else if (firstLetter.equals("s") && len == 6)
		personNumber = 	tag.substring(3,5);
	
	if (personNumber != null) {	
		//System.out.println(personNumber);	
		char firstChar = personNumber.charAt(0);
		String rest = personNumber.substring(1);
 		// karlkyn, kvenkyn, hvorugkyn => 3 persóna
		if ( firstChar == 'k' || firstChar == 'v' || firstChar == 'h' || firstChar == 'x')
			personNumber = "3"+rest;
	}

	return personNumber;
}

public boolean agreement(String tag1, String tag2)
{
	String pn1 = getPersonNumber(tag1);
	String pn2 = getPersonNumber(tag2);
	String case1 = getCase(tag1);
	// Only consider nominative case of subjects
	if ((pn1 == null) || (pn2 == null) || (case1 == null) || !case1.equals("n"))
		return true;
	else
	{
		//System.out.println(pn1 + " " + pn2);
		return pn1.equals(pn2);
	}
}

%}

%include ../regularDef.txt

OpenSubj = "{*SUBJ>"
CloseSubj = "*SUBJ>}"
//OpenSubj2 = "{*SUBJ<"
//CloseSubj2 = "*SUBJ<}"
FuncSubject = {OpenSubj}~{CloseSubj}
//FuncSubject2 = {OpenSubj2}~{CloseSubj2}

InfinitiveError=að{WhiteSpace}+{InfinitiveTag}{WhiteSpace}+\[VP~VP[bisp]?\]
InfinitiveError2=að{WhiteSpace}+c{WhiteSpace}+{OpenVPi}~{CloseVPi}

SubjectVP = {FuncSubject}{WhiteSpace}+{OpenVP}" "~{CloseVP}
//VPSubject = {OpenVP}" "~{CloseVP}{WhiteSpace}+{FuncSubject2}

%%
{InfinitiveError}	{ System.out.println(yytext()); } 
{InfinitiveError2}	{ System.out.println(yytext()); }
{SubjectVP}		{ String str = yytext();	
			  // Get the part up to *SUBJ>
			  int idx = str.indexOf("[VP");
			  String theSubject = str.substring(0,idx-1);
			  String theVP = str.substring(idx);
			  // returns the tag for the subject, {*SUBJ> [NP ég fp1en NP] *SUBJ>}
			  String tagSubject = getSubjectTag(theSubject);
			  // returns the tag for the verb, [VP stökk sfg1eþ VP]
			  String tagVerb = getTag(theVP, 2);
			  System.out.println("gDB>str>"+theSubject + " : " + tagSubject + " -- " + theVP + " : " + tagVerb);
			  if (tagSubject != null && tagVerb != null && !agreement(tagSubject,tagVerb))
				System.out.println("gDB>str>"+str);
} 

//{VPSubject}		{ String str = yytext();	
//			  int idx = str.indexOf("{*SUBJ<");
//			  String theVP = str.substring(0,idx-1);
//			  String theSubject = str.substring(idx);
//			  String tagSubject = getSubjectTag(theSubject);
//			  String tagVerb = getTag(theVP, 2);
//			  if (tagSubject != null && tagVerb != null && !agreement(tagSubject,tagVerb))
//				System.out.println(str);
//} 
. | "\n"		{ ;}
