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

/* The following patterns search for case disagreement between a preposition and the following noun phrase */

package is.iclt.icenlp.core.iceparser;

import java.io.*;
%%

%public
%class PP_errors
%standalone
%line

%unicode

%{
%}

%include ../regularDef.txt

PrepTagAcc = ao[em]?{WhiteSpace}+
PrepTagDat = aþ[em]?{WhiteSpace}+
PrepTagGen = ae{WhiteSpace}+
PrepAcc = {WordSpaces}{PrepTagAcc}
PrepDat = {WordSpaces}{PrepTagDat}
PrepGen = {WordSpaces}{PrepTagGen}
PrepMWEDat = {WhiteSpace}+{OpenMWE_PP}{Adverb}{PrepDat}{CloseMWE_PP}{WhiteSpace}+
PrepMWEAcc = {WhiteSpace}+{OpenMWE_PP}{Adverb}{PrepAcc}{CloseMWE_PP}{WhiteSpace}+
NPStart = ({OpenNPs}{WhiteSpace}+)?{OpenNP}

PrepAccError = {OpenPP}({PrepAcc}|{PrepMWEAcc})({NPStart}[nde]~{CloseNP}|{OpenAP}[nde]~{CloseAP})
PrepDatError = {OpenPP}({PrepDat}|{PrepMWEDat})({NPStart}[nae]~{CloseNP}|{OpenAP}[nae]~{CloseAP})
PrepGenError = {OpenPP}{PrepGen}{NPStart}[adn]~{CloseNP}
//ConjDatError = {OpenSCP}{WhiteSpace}+að{WhiteSpace}+{ConjTag}{CloseSCP}{WhiteSpace}+({NPStart}d~{CloseNP})
%%
{PrepAccError}		{ System.out.println(yytext()); } 
{PrepDatError}		{ System.out.println(yytext()); } 
{PrepGenError}		{ System.out.println(yytext()); } 
. | "\n"		{ ;}
