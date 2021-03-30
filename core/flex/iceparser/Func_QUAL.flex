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
 
/* This transducer marks genitive qualifers (noun phrases) */
package is.iclt.icenlp.core.iceparser;
import java.io.*;
%%

%public
%class Func_QUAL
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String OpenQual="{*QUAL ";
  String CloseQual=" *QUAL}";
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF){
      	    yylex();  }
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
MWEPP_Gen = {WhiteSpace}+{OpenMWE_PP}~({PrepTagGen}|{PrepTagF}){CloseMWE_PP}{WhiteSpace}+

/* Don't mark genetive NPs that appear after genitive prepositions
and don't mark genitive NPS that appear after VPs, e.g. "hann hóf máls", "njóta þess" */

GenPP = {OpenPP}(({PrepositionGen}|{PrepositionF})|{MWEPP_Gen})
VP = {OpenVP}~{CloseVP} | {OpenVPs}~{CloseVPs} | {OpenVPi}~{CloseVPi}

PPSkip = ({GenPP}|{VP}{WhiteSpace}+)({NPGen}|{NPsGen})
NPGenSeq = {OpenNPs}{WhiteSpace}+{OpenNP}g~{CloseNPs}
NPQual = {NPGen}({WhiteSpace}+{NPGen})* | {NPGenSeq}
//Fiskinn = "[NPa"({WhiteSpace}+{Word}{WhiteSpace}+{NounTag}{WhiteSpace}+)*~"NP]"

%%
{PPSkip}	{out.write(yytext()); }
{NPQual} { out.write(OpenQual+yytext()+CloseQual);}
//{Fiskinn} { out.write(OpenQual+yytext()+CloseQual);System.out.println("gDB>> FuncQUAL[Fiskinn]=("+yytext()+")");}
"\n"
{
		//System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r");
		out.write("\n");
}
.
{
		out.write(yytext());
}
