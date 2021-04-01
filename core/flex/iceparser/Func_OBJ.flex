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
package is.iclt.icenlp.core.iceparser;
import java.io.*;
import is.iclt.icenlp.core.utils.ErrorDetector;
%%

%public
%class Func_OBJ
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  String Obj1Open=" {*OBJ< ";
  String Obj1Close=" *OBJ<} ";
  String Obj2Open=" {*OBJ> ";
  String Obj2Close=" *OBJ>} ";
  
  int theIndex=0;
	boolean errorCheck = false;
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
      	out = _out;
      	while (!zzAtEOF)
        {
      	    yylex();
        }
  }

	public void set_markGrammarError(boolean option)
	{
		errorCheck = option;
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

NP = {OpenNP}[adg]~{CloseNP}//  (same as the line below)//CloseNP = "NP"\??{Word}"
//NP = {OpenNP}(a|d|g)~"NP"\??{Word}"]"
//NP = {OpenNP}[adg]~"NP"\??{Word}"]"
//NP = "[NP"\??{Word}?[adg]~"NP"\??{Word}?"]"  // For some reason, we cannot use {OpenNP}[adg]~{CloseNP} because then we are not able to compile this file with JFlex!

NPn = {OpenNP}[n]~{CloseNP}//  (same as the line below)//CloseNP = "NP"\??{Word}"


//NPs = {NPsAcc}|{NPsDat}|{NPsGen}//  (same as the line below)
//NPs = {OpenNPs}{WhiteSpace}+{OpenNP}(a|d|g)~{CloseNPs}
NPs = {StartNPs}(a|d|g)~{CloseNPs}


AdvP = {OpenAdvP}~{CloseAdvP}
//AdvPs = (({AdvP}|{MWE}){WhiteSpace}+)+
AdvPs = (({AdvP}|{MWE_AdvP}){WhiteSpace}+)+
PP = {OpenPP}~{ClosePP}
RelCP = {OpenCP}~sem{WhiteSpace}+{ConjTag}{CloseCP}


Object = ({FuncQualifier}{WhiteSpace}+)?({NP}|{NPs}|{NPForeign}|{NPsForeign}|{AP}|{APs})({WhiteSpace}+{FuncQualifier})?
SubjectRelCP = {FuncSubject}|{RelCP}

SubjVerb = {SubjectRelCP}{WhiteSpace}+{VP}{WhiteSpace}+
VerbSubj = {VP}{WhiteSpace}+{FuncSubject}{WhiteSpace}+

SubjVerbNPn = {SubjVerb}{NPn}
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

{SubjVerbNPn}	{
	theIndex = StringSearch.splitString(yytext(),"VP]", false, 3);
	if(theIndex == -1)
		out.write(yytext());
	else
	{
		if (errorCheck)
		{
			out.write(ErrorDetector.agreementCheckSubjVerbObj(StringSearch.firstString,StringSearch.nextString,Obj1Open,Obj1Close,3));
		}
		else
		{
			out.write(yytext());
		}
	}
}



{SubjVerbObj}	{ 
//	System.err.println("obj-1");
//	System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"VP]", false, 3);		

			if(theIndex == -1)
				out.write(yytext());
			else
			{
//				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
				if (errorCheck)
				{
					out.write(ErrorDetector.agreementCheckSubjVerbObj(StringSearch.firstString,StringSearch.nextString,Obj1Open,Obj1Close,1));
				}
				else
				{
					out.write(StringSearch.firstString + Obj1Open + StringSearch.nextString + Obj1Close);
				}
			}
		}
	
{SubjVerbAdvPObj} { 
//	System.err.println("obj-2");
//	System.err.println(yytext());
			//  An AdvP might be part of the object itself! 
			//  Is there an AdvP after the Verb? 
			theIndex = StringSearch.splitString2(yytext(), "VP]", "AdvP]");
			if (theIndex == -1)
			{
				// Find where the AdvP phrase ended and insert the OBJ label 
				theIndex = StringSearch.splitString(yytext(),"AdvP]", false, 5);		
			}
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 		

{SubjVerbPPObj} { 		
//	System.err.println("obj-3");
//	System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);		
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 		
		
{VerbSubjObj}	{ 
//	System.err.println("obj-4");
//	System.err.println(yytext());
			// Find where the Subj phrase ended and insert the OBJ label 
			//System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);	
			if (theIndex == -1)	
				theIndex = StringSearch.splitString(yytext(),"*SUBJ}", false, 6);
			if (theIndex != -1)
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			else
				out.write(yytext());
		} 
{VerbSubjAdvPObj}	{ 
//	System.err.println("obj-5");
//	System.err.println(yytext());
			// Is there an AdvP after the Subject? 
			theIndex = StringSearch.splitString2(yytext(), "*SUBJ<}", "AdvP]");
			if (theIndex == -1)
			{
				// Find where the Subj phrase ended and insert the OBJ label 
				theIndex = StringSearch.splitString(yytext(),"*SUBJ<}", false, 7);		
			}
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbSubjPPObj}	{ 
//	System.err.println("obj-6");
//	System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);		

			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 

{ObjVerbSubj}	{ 
//	System.err.println("obj-7");
//	System.err.println(yytext());
			/* Find where the Verb phrase started and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"[VP", false, -1);		
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(ErrorDetector.objVerbSubj(StringSearch.firstString,StringSearch.nextString,Obj2Open,Obj2Close));
//				out.write(Obj2Open+StringSearch.firstString+Obj2Close+StringSearch.nextString);
		} 

{VerbObj}	{ 
//	System.err.println("obj-8");
//	System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"VP]", false, 3);		
			if(theIndex == -1)
				out.write(yytext());
			else
			out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbAdvPObj}	{ 
//	System.err.println("obj-9");
//	System.err.println(yytext());
			/* An AdvP might be part of the object itself! */
			/* Is there an AdvP after the Verb? */
			theIndex = StringSearch.splitString2(yytext(), "VP]", "AdvP]");
			if (theIndex == -1)
				theIndex = StringSearch.splitString(yytext(),"AdvP]", false, 5);		
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbPPObj}	{ 
//	System.err.println("obj-10");
//	System.err.println(yytext());
			theIndex = StringSearch.splitString(yytext(),"PP]", false, 3);		
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
		
{VerbInfObj}	{ 
//	System.err.println("obj-11");
//	System.err.println(yytext());
			/* Find where the Verb Infinitive phrase ended and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"VPi]", false, 4);		
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		} 
{VerbInfAdvPObj}	{ 
	//System.err.println("obj-12");
	//System.err.println(yytext());

			String afterVpi = yytext().substring(yytext().indexOf("VPi]")+4, yytext().length()); 
			afterVpi = afterVpi.trim();

			//Is there an AdvP after the Verb?
			if(afterVpi.substring(0, 5).equals("[AdvP"))
			{
				theIndex = StringSearch.splitString(yytext(), " AdvP]", true, 6);
			}
			//Is there a PP after the Verb?
			else if(afterVpi.substring(0, 3).equals("[PP"))
			{
				theIndex = StringSearch.splitString(yytext(), " PP]", true, 4);
			}
			else
			{
				theIndex = StringSearch.splitString(yytext(), "VPi]", false, 4);
			}

			if(theIndex == -1)
			{
				out.write(yytext());
			}
			else
			{
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
			}


/*
			// Is there an AdvP after the Verb? 
			theIndex = StringSearch.splitString2(yytext(), "VPi]", "AdvP]");
			if (theIndex == -1)
			{
				// Is there a PP after the Verb? 
				theIndex = StringSearch.splitString2(yytext(), "VPi]", "PP]");
				if (theIndex == -1)// Find where the Verb Infinitive phrase ended and insert the OBJ label 
					theIndex = StringSearch.splitString(yytext(),"VPi]", false, 4);		
			}
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
*/
		} 
{VerbSupineObj}	{
//	System.err.println("obj-13");
//	System.err.println(yytext());
			/* Find where the Verb Supine phrase ended and insert the OBJ label */
			theIndex = StringSearch.splitString(yytext(),"VPs]", false, 4);
			if(theIndex == -1)
				out.write(yytext());
			else
				out.write(StringSearch.firstString+Obj1Open+StringSearch.nextString+Obj1Close);
		}

"\n"		{/* System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r");
			out.write("\n");*/ }
.		{ out.write(yytext());}

