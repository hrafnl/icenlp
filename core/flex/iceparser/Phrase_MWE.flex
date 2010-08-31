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
 
/* This transducer groups multiword expressions  */

/*
	KNOWN PROBLEM

If words like "aðeins og" appear in a text, phrase_mwe will recognize it as "eins og"
because no whitespace is required before the word. 
*/
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_MWE
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String OpenAdv=" [MWE_AdvP ";
  String CloseAdv=" MWE_AdvP] ";
  String OpenA=" [MWE_AP ";
  String CloseA=" MWE_AP] ";
  String OpenC=" [MWE_CP ";
  String CloseC=" MWE_CP] ";
  
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


PronounTag = {encodeOpen}f[abpos]({Gender}|{Person}){Number}{Case}{encodeClose}//þessi var fyrir


Ada = {WhiteSpace}*[aA]ð{WhiteSpace}+{AdverbTag}
Adc = {WhiteSpace}*[aA]ð{WhiteSpace}+{ConjTag}
Adi = {WhiteSpace}*[aA]ð{WhiteSpace}+{InfinitiveTag}
Adp = {WhiteSpace}*[aA]ð{WhiteSpace}+{PrepTag}
Af = {WhiteSpace}*[aA]f{WhiteSpace}+{PrepTag}
Afa = {WhiteSpace}*[aA]f{WhiteSpace}+{AdverbTag}
Aa = {WhiteSpace}*á{WhiteSpace}+{AdverbTag}
Ap = {WhiteSpace}*á{WhiteSpace}+{PrepTag}
Adeins = {WhiteSpace}*aðeins{WhiteSpace}+{AdverbTag}
Adur = {WhiteSpace}*áður{WhiteSpace}+{AdverbTag}
Aftur = {WhiteSpace}*[aA]ftur{WhiteSpace}+{AdverbTag} 
Afram = {WhiteSpace}*áfram{WhiteSpace}+{AdverbTag} 
Alls = {WhiteSpace}*[aA]lls{WhiteSpace}+{PronounTag}
Allt = {WhiteSpace}*[aA]llt{WhiteSpace}+{PronounTag} 
Alveg = {WhiteSpace}*[aA]lveg{WhiteSpace}+{AdverbTag} 
Annad = {WhiteSpace}*annað{WhiteSpace}+{PronounTag} 
Annars = {WhiteSpace}*[aA]nnars{WhiteSpace}+{PronounTag}
Aukp = {WhiteSpace}*auk{WhiteSpace}+{PrepTag} 
Auki = {WhiteSpace}*auki{WhiteSpace}+{AdverbTag} 
An = {WhiteSpace}*[áÁ]n{WhiteSpace}+{PrepTag}
Bak = {WhiteSpace}*bak{WhiteSpace}+{NounTag}
Badum = {WhiteSpace}*[bB]áðum?{WhiteSpace}+{PronounTag} 
Beggja = {WhiteSpace}*[bB]eggja{WhiteSpace}+{PronounTag} 
Betur = {WhiteSpace}*betur{WhiteSpace}+{AdverbTag} 
Bil = {WhiteSpace}*bil{WhiteSpace}+{NounTag}
Blatt = {WhiteSpace}*[bB]látt{WhiteSpace}+{AdverbTag} 
Boginn = {WhiteSpace}*bóginn{WhiteSpace}+{NounTag}
Daemis = {WhiteSpace}*d(æmis|\.){WhiteSpace}+{NounTag}
Ed = {WhiteSpace}*eð{WhiteSpace}+{ConjTag}
Eda = {WhiteSpace}*[eE]ða{WhiteSpace}+{ConjTag}
Ef = {WhiteSpace}*[eE]f{WhiteSpace}+{ConjTag}
Eftir = {WhiteSpace}*eftir{WhiteSpace}+{PrepTag}
Einhvern = {WhiteSpace}*[eE]inhvern{WhiteSpace}+{PronounTag} 
Einhvers = {WhiteSpace}*[eE]inhvers{WhiteSpace}+{PronounTag} 
Eins = {WhiteSpace}*[eE]ins{WhiteSpace}+{AdverbTag}
Einsp = {WhiteSpace}*[eE]ins{WhiteSpace}+{PronounTag}
Ekki = {WhiteSpace}*[eE]kki{WhiteSpace}+{AdverbTag}
En = {WhiteSpace}*en{WhiteSpace}+{ConjTag}
Enn = {WhiteSpace}*enn{WhiteSpace}+{AdverbTag}
Enda = {WhiteSpace}*[eE]nda{WhiteSpace}+{AdverbTag}
Engan = {WhiteSpace}*[eE]ngan{WhiteSpace}+{PronounTag}
Engu = {WhiteSpace}*[eE]ngu{WhiteSpace}+{PronounTag}
Einu = {WhiteSpace}*einu{WhiteSpace}+{AdjectiveTag}
Einun = {WhiteSpace}*einu{WhiteSpace}+{PronounTag}
Er = {WhiteSpace}*er{WhiteSpace}+{VerbFiniteTag}
Fer = {WhiteSpace}*fer{WhiteSpace}+{VerbFiniteTag}
Ferns = {WhiteSpace}*[fF]erns{WhiteSpace}+{AdjectiveTag}
Fraa = {WhiteSpace}*frá{WhiteSpace}+{AdverbTag}
Fram = {WhiteSpace}*[fF]ram{WhiteSpace}+{AdverbTag}
Framvegis = {WhiteSpace}*framvegis{WhiteSpace}+{AdverbTag}
Fremst = {WhiteSpace}*fremst{WhiteSpace}+{AdverbTag}
Fyrr = {WhiteSpace}*[fF]yrr{WhiteSpace}+{AdverbTag}
Fyrst = {WhiteSpace}*[fF]yrst{WhiteSpace}+{AdverbTag}
Hattar = {WhiteSpace}*háttar{WhiteSpace}+{NounTag}
Heldur = {WhiteSpace}*[hH]eldur{WhiteSpace}+{AdverbTag}
Her = {WhiteSpace}*[hH]ér{WhiteSpace}+{AdverbTag}
Herna = {WhiteSpace}*[hH]érna{WhiteSpace}+{AdverbTag}
Hinn = {WhiteSpace}*[hH]inn{WhiteSpace}+{PronounTag} 
Hins = {WhiteSpace}*[hH]ins{WhiteSpace}+{PronounTag} 
Hinum = {WhiteSpace}*[hH]inum?{WhiteSpace}+{PronounTag} 
Hvad = {WhiteSpace}*[hH]vað{WhiteSpace}+{PronounTag} 
Hvar = {WhiteSpace}*[hH]var{WhiteSpace}+{AdverbTag} 
Hvort = {WhiteSpace}*hvort{WhiteSpace}+({ConjTag}|{PronounTag})
Hverju = {WhiteSpace}*hverju{WhiteSpace}+{PronounTag} 
Hvers = {WhiteSpace}*[hH]vers{WhiteSpace}+{PronounTag} 
Hvorki = {WhiteSpace}*[hH]vorki{WhiteSpace}+{ConjTag} 
Haegra = {WhiteSpace}*[hH]ægra{WhiteSpace}+{AdjectiveTag}
Haerra = {WhiteSpace}*[hH]ærra{WhiteSpace}+{AdverbTag}
I = {WhiteSpace}*í{WhiteSpace}+{PrepTag}
Jafnt = {WhiteSpace}*[jJ]afnt{WhiteSpace}+{AdverbTag}
Jafnvel = {WhiteSpace}*[jJ]afnvel{WhiteSpace}+{AdverbTag}
Konar = {WhiteSpace}*konar{WhiteSpace}+{NounTag}
Kosti = {WhiteSpace}*kosti{WhiteSpace}+{NounTag}
Kyns = {WhiteSpace}*kyns{WhiteSpace}+{NounTag}
Lagi = {WhiteSpace}*lagi{WhiteSpace}+{NounTag}
Leid = {WhiteSpace}*leið{WhiteSpace}+{NounTag}
Leyti = {WhiteSpace}*leyti{WhiteSpace}+{NounTag}
Likt = {WhiteSpace}*[lL]íkt{WhiteSpace}+{AdverbTag}
Margs = {WhiteSpace}*[mM]args{WhiteSpace}+{AdjectiveTag}
Medal = {WhiteSpace}*meðal{WhiteSpace}+{AdverbTag}
Medan = {WhiteSpace}*meðan{WhiteSpace}+{ConjTag}
Megin = {WhiteSpace}*megin{WhiteSpace}+{AdverbTag}
Meira = {WhiteSpace}*[mM]eira{WhiteSpace}+{AdjectiveTag}
Meiraa = {WhiteSpace}*[mM]eira{WhiteSpace}+{AdverbTag}
Mikid = {WhiteSpace}*mikið{WhiteSpace}+{AdverbTag}
Minna = {WhiteSpace}*minna{WhiteSpace}+{AdverbTag}
Minnsta = {WhiteSpace}*minnsta{WhiteSpace}+{AdjectiveTag}
Min = {WhiteSpace}*[mM]ín{WhiteSpace}+{PronounTag} 
Moti = {WhiteSpace}*móti{WhiteSpace}+{NounTag}
Mynda = {WhiteSpace}*mynda{WhiteSpace}+{VerbInfinitiveTag}
Ne = {WhiteSpace}*[nN]é{WhiteSpace}+{ConjTag} 
Neins = {WhiteSpace}*[nN]eins{WhiteSpace}+{PronounTag} 
Nokkru = {WhiteSpace}*[nN]okkru{WhiteSpace}+{PronounTag} 
Nokkurn = {WhiteSpace}*[nN]okkurn{WhiteSpace}+{PronounTag} 
Nokkurs = {WhiteSpace}*[nN]okkurs{WhiteSpace}+{PronounTag} 
Nu = {WhiteSpace}*nú{WhiteSpace}+{AdverbTag}
Ny = {WhiteSpace}*ný{WhiteSpace}+{AdverbTag}
Nyju = {WhiteSpace}*nýju{WhiteSpace}+{AdjectiveTag}
Rettu = {WhiteSpace}*[rR]éttu{WhiteSpace}+{AdjectiveTag}
Sagt = {WhiteSpace}*sagt{WhiteSpace}+{VerbTag}
Sama = {WhiteSpace}*[sS]ama{WhiteSpace}+{PronounTag}
Saman = {WhiteSpace}*saman{WhiteSpace}+{AdverbTag}
Sams = {WhiteSpace}*[sS]ams{WhiteSpace}+{PronounTag}
Samt = {WhiteSpace}*[sS]amt{WhiteSpace}+{AdverbTag}
Segja = {WhiteSpace}*segja{WhiteSpace}+{VerbInfinitiveTag} 
Sem = {WhiteSpace}*[sS]em{WhiteSpace}+{ConjTag}
Sema = {WhiteSpace}*sem{WhiteSpace}+{AdverbTag}
Sidur = {WhiteSpace}*síður{WhiteSpace}+{AdverbTag}
Sin = {WhiteSpace}*[sS]ín{WhiteSpace}+{PronounTag} 
Sinni = {WhiteSpace}*sinni{WhiteSpace}+{NounTag}
Sist = {WhiteSpace}*síst{WhiteSpace}+{AdverbTag}
Sjalfsogdu = {WhiteSpace}*sjálfsögðu{WhiteSpace}+{AdjectiveTag}
Smam = {WhiteSpace}*[sS]mám{WhiteSpace}+{AdverbTag}
Stad = {WhiteSpace}*stað{WhiteSpace}+{NounTag}
Stadar = {WhiteSpace}*staðar{WhiteSpace}+{NounTag}
Stundum = {WhiteSpace}*stundum{WhiteSpace}+{AdverbTag}
Svo = {WhiteSpace}*svo{WhiteSpace}+{AdverbTag}
Og = {WhiteSpace}*[oO]g{WhiteSpace}+{ConjTag}
Tila = {WhiteSpace}*[tT]il{WhiteSpace}+{AdverbTag}
Tilp = {WhiteSpace}*[tT](il|\.){WhiteSpace}+{PrepTag}
Tvenns = {WhiteSpace}*[tT]venns{WhiteSpace}+{AdjectiveTag}
Um = {WhiteSpace}*[uU]m{WhiteSpace}+{PrepTag}
Ur = {WhiteSpace}*[úÚ]r{WhiteSpace}+{PrepTag}
Vegar = {WhiteSpace}*vegar{WhiteSpace}+{NounTag}
Veginn = {WhiteSpace}*veginn{WhiteSpace}+{NounTag}
Vegna = {WhiteSpace}*vegna{WhiteSpace}+{PrepTag}
Vid = {WhiteSpace}*[vV]ið{WhiteSpace}+{AdverbTag}
Vill = {WhiteSpace}*vill{WhiteSpace}+{VerbFiniteTag} 
Vinstra = {WhiteSpace}*[vV]instra{WhiteSpace}+{AdjectiveTag}
Visu = {WhiteSpace}*vísu{WhiteSpace}+{AdjectiveTag}
Ymiss = {WhiteSpace}*[ýÝ]miss{WhiteSpace}+{PronounTag}
Thad = {WhiteSpace}*það{WhiteSpace}+{PronounTag}
Thar = {WhiteSpace}*[þÞ]ar{WhiteSpace}+{AdverbTag}
/* 
Thangad = {WhiteSpace}*[þÞ]angað{WhiteSpace}+{AdverbTag}
Thannig = {WhiteSpace}*[þÞ]annig{WhiteSpace}+{AdverbTag}
*/
Theim = {WhiteSpace}*[þÞ]eim{WhiteSpace}+{PronounTag} 
Thess = {WhiteSpace}*þess{WhiteSpace}+{PronounTag}
Thett = {WhiteSpace}*þétt{WhiteSpace}+{AdverbTag}
Thin = {WhiteSpace}*[þÞ]ín{WhiteSpace}+{PronounTag} 
Tho = {WhiteSpace}*[þÞ]ó{WhiteSpace}+{AdverbTag}
Thott = {WhiteSpace}*[þÞ]ótt{WhiteSpace}+{ConjTag}
Threnns = {WhiteSpace}*[þÞ]renns{WhiteSpace}+{AdjectiveTag}
Thvi = {WhiteSpace}*því{WhiteSpace}+{PronounTag}
Thvia = {WhiteSpace}*því{WhiteSpace}+{AdverbTag}
Odru = {WhiteSpace}*[öÖ]ðru{WhiteSpace}+{PronounTag}
Ofugu = {WhiteSpace}*[öÖ]fugu{WhiteSpace}+{AdjectiveTag}
Ollu = {WhiteSpace}*[öÖ]llu{WhiteSpace}+{PronounTag}
	
		
MWEAdv =	{Adp}({Nyju}|{Sjalfsogdu}|{Visu}|{Minnsta}{Kosti}|({Nokkru}|{Odru}){Leyti}) 		|
		({Alls}|{Annars}|{Einhvers}){Stadar} 		|
		{Afa}{Og}{Tila}					|
		{Aftur}{Ap}({Bak}|{Moti})			|
		{Aftur}{Og}{Aftur} 				|
		{Allt}{I}({Einu}|{Lagi}) 			| 
		{Allt}{Adp}{Thvi} 				| 
		({Annars}|{Hins}){Vegar}			|
		{Aukp}{Thess}					|
		{Aa}({Ny}|{Stundum}) 				|
		{Ap}{Hinn}{Boginn}				|
		{Adur}{Fyrr}					|
		{An}{Thess}{Adc}				|
		{Blatt}{Afram}					|
		({Badum}|{Beggja}|{Herna}|{Hinum}|{Haegra}|{Min}		|
		{Rettu}|{Sin}|{Thin}|{Vinstra}|{Theim}|{Odru}|{Ofugu}){Megin}	|
		({Einhvern}|{Engan}|{Nokkurn}){Veginn}		|
		{Eda}{Ollu}{Heldur}				|
		{Ef}{Tila}{Vill} 				| 
		{Ekki}{Sist}					|
		{Engu}{Ada}{Sidur} 				|
		{Enn}?{Einun}{Sinni} 				|
		{Fram}{Og}{Aftur}				|
		{Fyrst}{Og}{Fremst} 				|
		{Her}{Og}({Thar}|{Hvar}|{Nu}) 			| 
		{Hvar}{Sem}{Er} 				| 
		{Hvort}({Ed}|{Sem}){Er}? 			|
		{Hvers}{Vegna}					|
		{Haerra}{Og}{Haerra}				|
		{Jafnt}{Og}{Thett} 				|
		{Meira}{Adi}?{Segja}				|
		{Nokkru}{Sinni}					|
		{Og}{Svo}{Framvegis}				|
		{Sama}{Hvort}					|
		{Samt}{Sem}{Adur} 				|
		{Sem}({Sagt}|{Betur}{Fer})			|
		{Sidur}{En}{Svo} 				| 
		{Smam}{Saman}					|
		{Svo}{Og}					|
		{Tilp}({Daemis}|{Adi}{Mynda})			|
		{Tila}{Og}{Fraa} 				|
		{Um}({Leid}|{Thad}{Bil})			|
		{Vegna}{Thess}					|
		{Vid}{Og}{Vid}					|
		{Thess}({Vegna}|{I}{Stad})			|
		{Thar}?{Ada}{Auki}				|
		{Thar}{Aa}{Medal}				|
		{Odru}{Hverju}					|
		{Abbreviation}

MWEA =		({Alls}|{Annars}|{Einhvers}|{Einsp}|{Ferns}|{Hvers}|{Margs}	|
		{Neins}|{Nokkurs}|{Sams}|{Tvenns}|{Threnns}	|
		{Ymiss}|{Thess}){Konar}				|
		{Hvers}{Kyns}	|
		{Thess}{Hattar}


MWEC =		({Af}|{Ur})?{Thvi}{Adc} 	| 
		{Aa}{Medan}			|
		{Eftir}{Adc} 			|
		{Adur}{En}			|
		{Alveg}?({Eins}|{Likt}){Og}	|
		({Enda}|{Jafnvel}){Thott} 	|
		{Hvorki}{Meiraa}{Ne}{Minna}{En}	|
		{Svo}{Adc}			|
		{Svo}{Mikid}?{Sem}		|
		{Tilp}({Adi}|{Adc}) 		|	
		{Tilp}{Thess}({Adi}|{Adc})? 	|	
		{An}{Thess}({Adi}|{Adc})? 	|	
//		({Tilp}|{An}){Thess}?({Adi}|{Adc}) 	|	
		{Um}{Leid}{Og}			|
		{Vegna}{Thess}{Adc}		|
		{Thar}({Tila}|{Sem})		|
		{Tho}{Adc}			|
		{Thvia}{Adeins}?{Adc}
		

%%
{MWEC}		{ out.write(OpenC+yytext()+CloseC);}
{MWEAdv}	{ out.write(OpenAdv+yytext()+CloseAdv);}
{MWEA}		{ out.write(OpenA+yytext()+CloseA);}
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		  out.write("\n"); }
.		{ out.write(yytext());}
