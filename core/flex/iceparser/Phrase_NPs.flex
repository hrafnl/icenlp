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
 
/* This transducer groups together a sequence of noun phrases */
/* The phrases must agree in case */

package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Phrase_NPs
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String NPOpen=" [NPs ";
  String NPClose=" NPs] ";
  
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

ProperNouns = ({WordSpaces}{ProperNounTag})+
//AdverbPhrase = {OpenAdvP}~{CloseAdvP}{WhiteSpace}+

NPProperNom = {OpenNP}n{ProperNouns}{CloseNP}
NPProperAcc = {OpenNP}a{ProperNouns}{CloseNP}
NPProperDat = {OpenNP}d{ProperNouns}{CloseNP}
NPProperGen = {OpenNP}g{ProperNouns}{CloseNP}
GenQualifier = {WhiteSpace}*{NPGen}

NPShortening = {OpenNP}{Shortening}{CloseNP}

NPNomGenQual = {NPNom}{GenQualifier}*
NPAccGenQual = {NPAcc}{GenQualifier}*
NPDatGenQual = {NPDat}{GenQualifier}*
NPForeignGenQual = {NPForeign}{GenQualifier}*

CommaNPNom = {Comma}{WhiteSpace}+{NPNom}{WhiteSpace}+
CommaNPAcc = {Comma}{WhiteSpace}+({NPAcc}|{APAcc}){WhiteSpace}+
CommaNPDat = {Comma}{WhiteSpace}+({NPDat}|{APDat}){WhiteSpace}+
CommaNPGen = {Comma}{WhiteSpace}+{NPGen}{WhiteSpace}+
CommaNPShortening = {Comma}{WhiteSpace}+{NPShortening}{WhiteSpace}+
CommaNPForeign = {Comma}{WhiteSpace}+{NPForeign}{WhiteSpace}+

NPConjNom = {CommaNPNom}*{ConjPhrase}{WhiteSpace}+{NPNom}
NPConjAcc = {CommaNPAcc}*{ConjPhrase}{WhiteSpace}+({NPAcc}|{APAcc})
// [NP litlum bæ NP] en [AP fornum AP]
NPConjDat = {CommaNPDat}*{ConjPhrase}{WhiteSpace}+({NPDat}|{APDat})
NPConjGen = {CommaNPGen}*{ConjPhrase}{WhiteSpace}+{NPGen}
NPConjForeign = 
    {CommaNPForeign}*{ConjPhrase}{WhiteSpace}+{NPForeign}   |
    ({CommaNPNom}|{CommaNPAcc}|{CommaNPDat}|{CommaNPGen})*{ConjPhrase}{WhiteSpace}+{NPForeign}  |
    {CommaNPForeign}{ConjPhrase}{WhiteSpace}+({NPNom}|{NPAcc}|{APAcc}|{NPDat}|{APDat}|{NPGen})

NPSeq = 
    {NPProperNom}{WhiteSpace}+({NPNom}|{NPForeign})	|
	{NPProperAcc}{WhiteSpace}+({NPAcc}|{NPForeign})	|
	{NPProperDat}{WhiteSpace}+({NPDat}|{NPForeign})	|
	{NPProperGen}{WhiteSpace}+({NPGen}|{NPForeign})	|
    {NPForeign}{WhiteSpace}+({NPNom}|{NPAcc}|{NPDat}|{NPGen}|{NPForeign})    |

	({NPNomGenQual}|{NPForeignGenQual}){WhiteSpace}+({NPProperNom}|{NPConjNom}) 	| 
	({NPAccGenQual}|{NPForeignGenQual}){WhiteSpace}+({NPProperAcc}|{NPConjAcc}) 	| 
	({NPDatGenQual}|{NPForeignGenQual}){WhiteSpace}+({NPProperDat}|{NPConjDat}) 	| 
	({NPGen}|{NPForeign}){WhiteSpace}+({NPProperGen}|{NPConjGen})         |
    ({NPNomGenQual}|{NPAccGenQual}|{NPDatGenQual}|{NPForeignGenQual}|{NPGen}){WhiteSpace}+({NPForeign}|{NPConjForeign})  |

    {NPShortening}{WhiteSpace}+{CommaNPShortening}  |
	{NPShortening}{WhiteSpace}+({NPProperNom}|{NPConjNom}) 	| 
	{NPShortening}{WhiteSpace}+({NPProperAcc}|{NPConjAcc}) 	| 
	{NPShortening}{WhiteSpace}+({NPProperDat}|{NPConjDat}) 	| 
	{NPShortening}{WhiteSpace}+({NPProperGen}|{NPConjGen})  |
    {NPShortening}{WhiteSpace}+({NPForeign}|{NPConjForeign})
%%

{NPSeq}	{ out.write(NPOpen+yytext()+NPClose);}
"\n"	{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
	out.write("\n"); }
.	{ out.write(yytext());}
