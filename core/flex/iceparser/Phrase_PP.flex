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
 
/* This transducer marks prepositional phrases 				*/
/* A prepositional phrase can include a sequence of noun phrases, all of which have the same case */

/* A prep phrase can contain a multiword expression phrase, e.g. 			*/
/* [PP [MWE_PP fyrir framan MWE_PP] [NP [AP stóran AP] stein NP] PP]	*/

/* A prep phrase can contain an infinitive verb phrase, e.g. 		*/
/* [PP til [VP að fara *InfV VP] PP]					*/

package is.iclt.icenlp.core.iceparser;
import java.io.*;
import is.iclt.icenlp.core.utils.ErrorDetector;
%%

%public
%class Phrase_PP
%standalone
%line
%extends IceParserTransducer
%unicode

%{

  boolean markError = false;

  String PPOpen=" [PP ";
  String PPClose=" PP] ";
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF) 
      	    yylex();
  }

  public void set_markGrammarError(boolean option)
  {
        markError = option;
  }


  private String errorCheck(String originalStr)
  {
    if (markError)
      return ErrorDetector.PPErrorCheck(originalStr, PPOpen, PPClose);
    else
      return PPOpen + originalStr + PPClose;
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

Vegna = {WhiteSpace}*vegna{WhiteSpace}+

NounPhrase = {OpenNP}" "~{CloseNP} | {OpenNP}n~{CloseNP} | {OpenNP}a~{CloseNP} | {OpenNP}d~{CloseNP} | {OpenNP}g~{CloseNP} //avoid "NP?"
//NounPhrase = {OpenNP}~{CloseNP} this allows for too many states, which will run jFlex out of memory
NPs = {OpenNPs}~{CloseNPs}
AdverbPrepPhrase = {MWE_PP}

NPGenSpec = {OpenNP}g~{encodeOpen}f(p|s)hee{encodeClose}{WhiteSpace}+{CloseNP}

AdjectivePhrase = {OpenAP} ~{CloseAP}
AdjectivePhrases = {OpenAPs}~{CloseAPs}
 
AdverbPhrase = {OpenAdvP}~{CloseAdvP} | {MWE_AdvP}
VerbPhraseInf = {OpenVPi}~{CloseVPi}

NumSeq = {NPNum}({WhiteSpace}*{ConjPhraseOrComma}{WhiteSpace}+{NPNum})*
GenQualifier = {WhiteSpace}*({NPGen} | {NPsGen})

OneNP = ({NounPhrase}|{AdjectivePhrase}|{AdjectivePhrases}){GenQualifier}?
SeqNP = {NPs}{GenQualifier}?
//Sem = {ConjPhraseSem}{WhiteSpace}+{OneNP}

// MWE_AdvP can appear like in [PP á aþ [MWE_AdvP um ao það faheo bil nheo MWE_AdvP] [NP tíu tfvfþ sekúndum nvfþ NP] PP]

PrepPhraseRest1 = {AdverbPhrase}{WhiteSpace}+({OneNP}|{SeqNP}) | {OneNP} | {SeqNP} | {NumSeq} 
PrepPhraseRest2 = {VerbPhraseInf}
PrepPhraseRest3 = {GenQualifier}{WhiteSpace}+({OneNP}|{SeqNP})


PrepPhraseAccDat = {PrepositionAccDat}({PrepPhraseRest1}|{PrepPhraseRest2}|{PrepPhraseRest3})? 
PrepPhraseGen = {PrepositionGen}({PrepPhraseRest1}|{PrepPhraseRest2})?
PrepPhraseMWE = {AdverbPrepPhrase}{WhiteSpace}+({OneNP}|{SeqNP})
PrepPhraseF = {PrepositionF}({PrepPhraseRest1}|{PrepPhraseRest2}|{PrepPhraseRest3})? 

PrepPhraseSpecial = {NPGenSpec}{Vegna}{PrepTagGen} 

%%

{MWE_AdvP}|{MWE_CP}|{MWE_AP}	{ out.write(yytext());}
{PrepPhraseSpecial}		{ out.write(errorCheck(yytext()));/*out.write(PPOpen+yytext()+PPClose);*/}
{PrepPhraseAccDat}		{ out.write(errorCheck(yytext()));/*out.write(PPOpen+yytext()+PPClose);*/}
{PrepPhraseGen}			{ out.write(errorCheck(yytext()));/*out.write(PPOpen+yytext()+PPClose);*/}
{PrepPhraseMWE}			{ out.write(errorCheck(yytext()));/*out.write(PPOpen+yytext()+PPClose);*/}
{PrepPhraseF}		{ out.write(errorCheck(yytext()));/*out.write(PPOpen+yytext()+PPClose);*/}
"\n"				{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
				out.write("\n"); }
.		{ out.write(yytext());}
