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

/* This transducer marks potential objects */
package is.iclt.icenlp.flex.iceparser;
import java.io.*;
%%

%public
%class Func_OBJ
%standalone
%line

%unicode

%{
  String Obj1Open=" {*OBJ< ";
  String Obj1Close=" *OBJ<} ";
  String Obj2Open=" {*OBJ> ";
  String Obj2Close=" *OBJ>} ";  
  
  int theIndex=0;
  
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
%include funcDef.txt
%include verbLexicon.txt

VPInf = {OpenVPi}~{CloseVPi}
VPSupine = {OpenVPs}~{CloseVPs}
VP = {OpenVP}" "~{CloseVP}
AP = {OpenAP}[adg]~{CloseAP}
APs = {APsAcc}|{APsDat}|{APsGen}
NP = {OpenNP}[adg]~{CloseNP}
NPs = {NPsAcc}|{NPsDat}|{NPsGen}
AdvP = {OpenAdvP}~{CloseAdvP}
//AdvPs = (({AdvP}|{MWE}){WhiteSpace}+)+
AdvPs = (({AdvP}|{MWE_AdvP}){WhiteSpace}+)+
PP = {OpenPP}~{ClosePP}
RelCP = {OpenCP}~sem{WhiteSpace}+{ConjTag}{CloseCP}


Object = ({FuncQualifier}{WhiteSpace}+)?({NP}|{NPs}|{AP}|{APs})({WhiteSpace}+{FuncQualifier})?
SubjectRelCP = {FuncSubject}|{RelCP}

SubjVerb = {SubjectRelCP}{WhiteSpace}+{VP}{WhiteSpace}+
VerbSubj = {VP}{WhiteSpace}+{FuncSubject}{WhiteSpace}+

SubjVerbObj = {SubjVerb}{Object}
SubjVerbAdvPObj = {SubjVerb}{AdvPs}{Object}
SubjVerbPPObj = {SubjVerb}{AdvPs}?{PP}{WhiteSpace}+{Object}
VerbSubjObj = {VerbSubj}{Object}
VerbSubjAdvPObj = {VerbSubj}{AdvPs}{Object}
VerbSubjPPObj = {VerbSubj}{AdvPs}?({PP}{WhiteSpace}+){Object}
ObjVerbSubj = {Object}{WhiteSpace}+{VP}{WhiteSpace}+({AdvP}{WhiteSpace}+)?{FuncSubject}
VerbObj = {VP}{WhiteSpace}+{Object}
VerbAdvPObj = {VP}{WhiteSpace}+{AdvPs}{Object}
VerbPPObj = {VP}{WhiteSpace}+{AdvPs}?({PP}{WhiteSpace}+){Object}
VerbInfObj = {VPInf}{WhiteSpace}+{Object}
VerbInfAdvPObj = {VPInf}{WhiteSpace}+({AdvPs}|{PP}{WhiteSpace}+){Object}
VerbSupineObj = {VPSupine}{WhiteSpace}+{Object}

%%

{SubjVerbObj}	{ 
			StringSearch.splitString(yytext(),"VP]", false, 3);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 		
	
{SubjVerbAdvPObj} { 
			/* An AdvP might be part of the object itself! */
			/* Is there an AdvP after the Verb? */
			theIndex = StringSearch.splitString2(yytext(), "VP]", "AdvP]");
			if (theIndex == -1)
			{
				/* Find where the AdvP phrase ended and insert the OBJ label */
				StringSearch.splitString(yytext(),"AdvP]", false, 5);		
			}
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 		
		
{SubjVerbPPObj} { 			
			StringSearch.splitString(yytext(),"PP]", false, 3);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 		
		
{VerbSubjObj}	{ 
			/* Find where the Subj phrase ended and insert the OBJ label */
			StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbSubjAdvPObj}	{ 
			/* Is there an AdvP after the Subject? */
			theIndex = StringSearch.splitString2(yytext(), "*SUBJ<}", "AdvP]");
			if (theIndex == -1)
				/* Find where the Subj phrase ended and insert the OBJ label */
				StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbSubjPPObj}	{ 
			StringSearch.splitString(yytext(),"PP]", false, 3);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 

{ObjVerbSubj}	{ 
			/* Find where the Verb phrase started and insert the OBJ label */
			StringSearch.splitString(yytext(),"[VP", false, -1);		
			out.write(Obj2Open+StringSearch.firstString+Obj2Close+StringSearch.nextString);
		} 

{VerbObj}	{ 
			StringSearch.splitString(yytext(),"VP]", false, 3);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbAdvPObj}	{ 
			/* An AdvP might be part of the object itself! */
			/* Is there an AdvP after the Verb? */
			theIndex = StringSearch.splitString2(yytext(), "VP]", "AdvP]");
			if (theIndex == -1)
				StringSearch.splitString(yytext(),"AdvP]", false, 5);		
			
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbPPObj}	{ 
			StringSearch.splitString(yytext(),"PP]", false, 3);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
		
{VerbInfObj}	{ 
			/* Find where the Verb Infinitive phrase ended and insert the OBJ label */
			StringSearch.splitString(yytext(),"VPi]", false, 4);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbInfAdvPObj}	{ 
			/* Is there an AdvP after the Verb? */
			theIndex = StringSearch.splitString2(yytext(), "VPi]", "AdvP]");
			if (theIndex == -1)
			{
				/* Is there a PP after the Verb? */
				theIndex = StringSearch.splitString2(yytext(), "VPi]", "PP]");
				if (theIndex == -1)
					/* Find where the Verb Infinitive phrase ended and insert the OBJ label */
					StringSearch.splitString(yytext(),"VPi]", false, 4);		
			}
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbSupineObj}	{ 
			/* Find where the Verb Supine phrase ended and insert the OBJ label */
			StringSearch.splitString(yytext(),"VPs]", false, 4);		
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
		

"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.		{ out.write(yytext());}
