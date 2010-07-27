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
 
/* This transducer marks the case of an adjectival phras */
/* I.e. [APx ... xAP], where x=[nadg] (nom,acc,dat,gen) */

package is.iclt.icenlp.core.iceparser;
import java.util.regex.Pattern;
import java.io.*;
%%

%public
%class Case_AP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  static final int labelLength = 3;
  
  String nomStr = "n";
  String accStr = "a";
  String datStr = "d";
  String genStr = "g";
 
  
  //java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
  java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
    
  public void parse(java.io.Writer _out) throws java.io.IOException
  {
    	out = _out;
    	while (!zzAtEOF) 
    	    yylex();
  }
  
  
  private String analyseCase(char cas)
  {
    	switch (cas) {
    		case 'n' : return nomStr;
    		case 'o' : return accStr;
    		case 'þ' : return datStr;
    		case 'e' : return genStr;
    		default  : return "";
    	}
  }
  
  private String analyse(String txt)
  {	
  	String[] strs;
  	String tag;
  	String result="";
        Pattern p = Pattern.compile("\\s");
         // Get all the individual lexemes as strings
        strs = p.split(txt);
        int len = strs.length;
        if (len >= 2) {
        	tag = strs[len-2];	/* strs[len-1] = "AP]" */
		tag = tag.substring(1, tag.length()-1);
        	if ( (tag.charAt(0) == 'l') && (tag.length() >= 4))
        		return analyseCase(tag.charAt(3));	
        }	
  	return result;
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

/* The space in the pattern below is necessary when this transducer is run separately. */
/* In that case this pattern does not match [APs .. APs] */
AP = {OpenAP}" "~{CloseAP}

%%

{MWE}		{ out.write(yytext());}
{AP}		{ 	String caseStr,matchedStr,openLabelStr,closeLabelStr;
			int len;
			
			matchedStr = yytext();
			len = matchedStr.length();
			caseStr = analyse(matchedStr);
			openLabelStr = matchedStr.substring(0,labelLength);
			//closeLabelStr = matchedStr.substring(len-labelLength);
			out.write(openLabelStr + caseStr + matchedStr.substring(labelLength));
			//out.write(openLabelStr + caseStr + matchedStr.substring(labelLength,len-labelLength) + caseStr + closeLabelStr);
			//out.write(openLabelStr + caseStr + matchedStr.substring(labelLength,len-1) + caseStr + closingBracket);
		}
"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			  out.write("\n"); }
.		{ out.write(yytext());}
