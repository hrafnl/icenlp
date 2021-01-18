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
import is.iclt.icenlp.core.utils.ErrorDetector;
%%

%public
%class ErrDetect
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

	// if we find something like "hann getur að lesa"
	// then we put an error on the "að" and the "lesa"
	public String ghVPerrorMarker (String input)
	{

		// if we cannot find [VPi, which is normally something like "að", in "hann getur að lesa"
		// then we insert error markings of ?Ghto VPi
		if (!input.contains("[VPi")) {
			return input;
		}

		int VPiStart = input.indexOf("[VPi");

		StringBuffer out = new StringBuffer();
		out.append(input.substring(0,VPiStart));
		out.append("[VPi?Gh?").append(input.substring(VPiStart+4,input.length()-1)).append("]");
		return out.toString();
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


geta =	"get" ("ur" | "um" | "ið" | "a" | "i" | "ir") | "gat" | "gast" | "gátu" ("m" | "ð") | "gæt" ("i" | "ir" | "um" | "uð" | "u")
hef = "hef" ("i" | "ir" | "ur" | "um" | "uð" | "ði" | "ðir" | "ðu" | "ðum" | "ðuð" )?
hof = "höf" ("um" | "ðum" | "ðuð" | "ðu")
haf = "haf" ("ið" | "a" | "ði" | "ðir" | "i" | "ir" )
hafa =	{hef} | {hof} | {haf}
fekk = {fae} | {fa} | "fékkst" | "fékk" | {feng}
fae = "fær" ("r" | "rð")?
fa = "fá" ("i" | "ir" | "ið" | "um")?
feng = "feng" ("um" | "uð" | "u" | "i" | "ir" | "ju"  | "jum" | "juð")

VP = "VP"[bi]?{Error}?
VPopen = "["{VP}
VPclose = {VP}"]"

AdvP = {OpenAdvP}~{CloseAdvP}{WhiteSpace}
//VerbSubj = {OpenVP}~{CloseVP}~{OpenSubj}~{CloseSubj}
// verbs "geta", "hafa" and "fá" cannot stand next to VPi
VPgeta = {VPopen} {WhiteSpace} ({geta}|{hafa}|{fekk}) {WhiteSpace} {VerbTag} {WhiteSpace} {VPclose}
ghVP = {VPgeta} {WhiteSpace} {AdvP}? {VPopen}~{VPclose}

%%

{ghVP} {out.write(ghVPerrorMarker(yytext()));}

"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r");
		out.write("\n"); }
.		{ out.write(yytext());}