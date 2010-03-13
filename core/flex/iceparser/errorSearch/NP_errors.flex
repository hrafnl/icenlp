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

/* The following patterns search for feature disagreements in noun phrases */

package is.iclt.icenlp.core.iceparser;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
%%

%public
%class NP_errors
%standalone
%line

%unicode

%{
public String lastToken(String str)
{
	// Split it into tokens and retrieve the last token
	String[] tokens = str.split("\\s");	
	String last = tokens[tokens.length-1];
	return last;
}

public String getGenderNumberCase(String tag)
{
	String genderNumberCase="";

	String firstLetter = tag.substring(0,1);
	int len = tag.length();
	if (firstLetter.equals("n") || firstLetter.equals("l") || firstLetter.equals("g"))
		genderNumberCase = tag.substring(1,4);
	else if (firstLetter.equals("f") || firstLetter.equals("t")) 
		genderNumberCase = tag.substring(2,5);

	return genderNumberCase;
}

public boolean agreement(String tag1, String tag2)
{
	String gnc1 = getGenderNumberCase(tag1);
	String gnc2 = getGenderNumberCase(tag2);
	//System.out.println(gnc1 + " " + gnc2);
	return gnc1.equals(gnc2);
}

%}

%include ../regularDef.txt
DemIndefPronounTag = f[ao]{Gender}{Number}{Case}{WhiteSpace}+
DemIndefPronoun = {WordSpaces}{DemIndefPronounTag}
PossessivePronounTag = fe{Gender}{Number}{Case}{WhiteSpace}+
PossessivePronoun = {WordSpaces}{PossessivePronounTag}
ArticleTag = g{Gender}{Number}{Case}{WhiteSpace}+
NumTag = tf{Gender}{Number}{Case}{WhiteSpace}+
Noun = {WordSpaces}{NounTag}
Article = {WordSpaces}{ArticleTag}
Numeral={WordSpaces}{NumTag}

CaseMarker = [nadg]
NounPronoun = {OpenNP}{CaseMarker}?{Noun}{PossessivePronoun}{CloseNP}
PronounNoun = {OpenNP}{CaseMarker}?({Numeral}|{DemIndefPronoun}){Noun}{CloseNP}
PronounAdj = {OpenNP}{CaseMarker}?({DemIndefPronoun}|{Article}){OpenAP}~{CloseAP}{WhiteSpace}+{CloseNP}
AdjNoun = {OpenNP}{CaseMarker}?{WhiteSpace}+({Numeral}|{Article}|{DemIndefPronoun})?{OpenAP}~{CloseAP}{Noun}+{CloseNP}
%%
{AdjNoun}	{	
				String str = yytext();	
				//System.out.println("Found: "+ str);	
				// Get the part up to AP]
				int idx = str.lastIndexOf("AP]");
				String thePart = str.substring(0,idx);
				String adjTag = lastToken(thePart);
				// Get the part up to NP]
				idx = str.lastIndexOf("NP]");
				thePart = str.substring(0,idx);
				String nounTag = lastToken(thePart);
				if (!agreement(adjTag,nounTag))
					System.out.println(str);
				else {
					// Find the tag for the Numeral or Article
					Pattern p = Pattern.compile("(tf|g)[kvh][ef][noþe]");
					Matcher m = p.matcher(str);
					if (m.find()) {
						String aTag = str.substring(m.start(),m.end());	
						//System.out.println(aTag+" "+adjTag+" "+nounTag);
						if (!agreement(aTag,nounTag) || !agreement(aTag,adjTag))
					 		System.out.println(str);
					}
				}
		}

{PronounNoun}	{	
				String str = yytext();	
				// Get the part up to NP]
				int idx = str.lastIndexOf("NP]");
				String thePart = str.substring(0,idx);
				String nounTag = lastToken(thePart);

				// Find the tag for the Pronoun/Numeral 
				Pattern p = Pattern.compile("(f[ao]|tf)[kvh][ef][noþe]");
				Matcher m = p.matcher(str);
				if (m.find()) {
					String aTag = str.substring(m.start(),m.end());	
						//System.out.println(aTag);
					if (!agreement(aTag,nounTag))
						System.out.println(str);
				}
		}

{PronounAdj}	{	
				String str = yytext();	
				// Get the part up to AP]
				int idx = str.lastIndexOf("AP]");
				String thePart = str.substring(0,idx);
				String adjTag = lastToken(thePart);

				// Find the tag for the Pronoun/Article 
				Pattern p = Pattern.compile("(f[ao]|g)[kvh][ef][noþe]");
				Matcher m = p.matcher(str);
				if (m.find()) {
					String aTag = str.substring(m.start(),m.end());	
						//System.out.println(aTag);
					if (!agreement(aTag,adjTag))
						System.out.println(str);
				}
		}

{NounPronoun}	{	
				String str = yytext();	
				// Get the part up to NP]
				int idx = str.lastIndexOf("NP]");
				String thePart = str.substring(0,idx);
				String pTag = lastToken(thePart);

				// Find the tag for the noun 
				Pattern p = Pattern.compile("n[kvh][ef][noþe]");
				Matcher m = p.matcher(str);
				if (m.find()) {
					String nounTag = str.substring(m.start(),m.end());	
					if (!agreement(nounTag,pTag))
						System.out.println(str);
				}
			}
. | "\n"		{ ;}
