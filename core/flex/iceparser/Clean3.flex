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

/* This transducer cleans the output file 		*/
/* 1. Corrects incorrectly detected subject 	*/
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Clean3
%standalone
%line
%extends IceParserTransducer
%unicode

%{
   String str;
	boolean agreement = false;
   
   //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
       
   public void parse(java.io.Writer _out) throws java.io.IOException
   {
       	out = _out;
       	while (!zzAtEOF) 
       	    yylex();
   }

	public void set_doAgreementCheck(boolean option)
	{
		agreement = option;
	}
	public void set_markGrammarError(boolean markGrammarError)
	{
		// If we want grammatical errors to be shown then make sure that the agreement flag is true as well
        if (markGrammarError)
		{
        	agreement = true;
		}
	}


	// "Subject - verbError - Subject" fixer
	private String svsFixer(String input)
	{
		if (agreement)
		{
	//		System.out.println("svsFixer=["+input+"]");
			if (isBadVerb(input))
			{
				input = fixBadWords(input);
			}
		}
		return input;
	}

	// checks if the input contains a bad word (Það, því...etc) with tag of fphen where there is an "er" or "eru" and a [VPb
	// það er (subj)
	private boolean isBadVerb(String input)
	{
//		System.out.println("isBadVerb=["+input+"]");
		return input.contains("fphen")&&input.contains(" er")&&input.contains("[VPb");
	}

	// 1, removes the {*SUBJ     *SUBJ} on the first word
	// 2. checks if there is an error on the verb and removes it if there is
	// 3. adds the rest to the string and returns
	private String fixBadWords(String input)
	{
// 		remove the first   {*SUBJ     *SUBJ}
		int subjStart = input.indexOf("{*SUBJ");
		String output = input.substring(subjStart+7,input.indexOf("*SUBJ",7+subjStart));

//[VPb?Vn eru ^sfg3fn$ VPb?Vn]
//      check if there is an error in the verb, if not, leave it alone, otherwise remove the error.
		if (input.contains("[VPb?"))
		{
			// if there is an error in the verb we remove it and add to output
			int verbPhraseStart = input.indexOf("VPb?");
            int extraLetters = 0;
            int subStart2 = input.indexOf("{*SUBJ",verbPhraseStart);

			// counts the number of letters are until we find whitespace
			for (; (input.charAt(verbPhraseStart+extraLetters) != ' ')&&(verbPhraseStart + extraLetters < subStart2) ; extraLetters++)
		{/*System.out.println("gDB>>+("+input.charAt(verbPhraseStart+extraLetters)+")");*/};

			output += "[VPb "+ input.substring(verbPhraseStart+extraLetters,input.indexOf("VPb",verbPhraseStart+2))+"VPb] ";


// add < sign to subject
			int subEnd = input.lastIndexOf("*SUBJ");
			String subject = "{*SUBJ< "+input.substring(subStart2+7,subEnd)+" *SUBJ<}";

//			System.out.println("gDB>> output=("+output+") rest=("+input.substring(subStart2)+")");
			// then we find where the verphrase ended in the input and add the rest to output and send
			return output + subject;
		}
		else
		{
			// takes the output which is the Það without subj around it
			// then it finds the rest and adds to the back of it and returns
			return output + input.substring(input.indexOf("*SUBJ",subjStart));
		}
//		output += "("+input.substring(phraseStart,input.indexOf(input.indexOf(" VP"),phraseStart))+")";
//		System.out.println("fixBadWords=["+(input.indexOf("{*SUBJ")+5)+","+input.indexOf("*SUBJ",5+input.indexOf("{*SUBJ"))+"]");
//		return output;

	}

	public String ghVPerrorMarker (String input)
	{
//		System.out.println("gDB>> input=("+input+")");

		String end;
		int VPiStart = input.indexOf("[VPi");

//		end = input.substring(VPiStart);

		end = "[VPi?Gh";
		end += input.substring(VPiStart+4,input.length()-1);
		end += "?Gh]";
		//end += input.substring(input.length()-1,input.length());


		String start = input.substring(0,VPiStart);


//		System.out.println("gDB>> start=("+start+")");
//		System.out.println("gDB>> end=("+end+")");

		return start+end;
	}
/*	// "verb - Subject" fixer
	private String vsFixer(String input)
	{
//		System.out.println("vsFixer=["+input+"]");
		return input;
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

VP = "VP"[bi]?{Error}?
VPopen = "["{VP}
VPclose = {VP}"]"

errorVP = "VP"[bi]?{Error}
OpenErrorVP = "["{errorVP}
CloseErrorVP = {VP}"]"

VPError = {OpenErrorVP}~{CloseErrorVP}
// subject error_verb subject
//SubjVerbSubj= {OpenSubj}{CloseSubj}{VPError}{OpenSubj}{CloseSubj}

geta =	"get" ("ur" | "um" | "ið" | "a" | "i" | "ir") | "gat" | "gast" | "gátu" ("m" | "ð") | "gæt" ("i" | "ir" | "um" | "uð" | "u")
hafa =	{hef}|{höf}|{haf}
hef = "hef" ("i" | "ir" | "ur" | "um" | "uð" | "ði" | "ðir" | "ðu" | "ðum" | "ðuð" )?
höf = "höf" ("um" | "ðum" | "ðuð" | "ðu")
haf = "haf" ("ið" | "a" | "ði" | "ðir" | "i" | "ir" )
fekk = {fae} | {fa} | "fékkst" | "fékk" | {feng}
fae = "fær" ("r" | "rð")?
fa = "fá" ("i" | "ir" | "ið" | "um")?
feng = "feng" ("um" | "uð" | "u" | "i" | "ir" | "ju"  | "jum" | "juð")

AdvP = {OpenAdvP}~{CloseAdvP}{WhiteSpace}


SubjVerberrSubj = {OpenSubj}~{CloseSubj}{WhiteSpace}{VPError}{WhiteSpace}{OpenSubj}~{CloseSubj}
//VerbSubj = {OpenVP}~{CloseVP}~{OpenSubj}~{CloseSubj}
// verbs "geta", "hafa" and "fá" cannot stand next to VPi
VPgeta = {VPopen} {WhiteSpace} ({geta}|{hafa}|{fekk}) {WhiteSpace} {VerbTag} {WhiteSpace} {VPclose}
ghVP = {VPgeta} {WhiteSpace} {AdvP}? {VPopen}~{VPclose}

%%

{SubjVerberrSubj}	{ out.write(svsFixer(yytext()));}

{ghVP} { out.write(ghVPerrorMarker(yytext()));}

//{VerbSubj}	{ out.write(vsFixer(yytext()));}
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r");
		out.write("\n"); }
.		{ out.write(yytext());}