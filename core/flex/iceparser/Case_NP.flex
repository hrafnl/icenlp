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

/* This transducer mark the case of a noun phrases /*
I.e. [NPx ... xNP], where x=[nadg] (nom,acc,dat,gen) */
/* Dummy heads are also marked, i.e. [NP þeirri faveþ [APd gömlu lveþvf *HeadAd AP] NP] becomes */
/* [NPd þeirri faveþ [APd gömlu lveþvf *HeadAd AP] xNP] */
/* This is done in order to facilitate case agreement in later (PP) phrases */

package is.iclt.icenlp.core.iceparser;
import java.util.regex.Pattern;
import java.io.*;
%%

%public
%class Case_NP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
  
  //static final int labelLength = 3;   // "[NP "
  //boolean isHeadNP = true;
  
  String nomStr = "n";
  String accStr = "a";
  String datStr = "d";
  String genStr = "g";
  
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
  
  private String analyseTag(String tag)
  {
  	String result="";
  	
  	if ( (tag.charAt(0) == 'n') && (tag.length() >= 4))	/* noun */
          	return analyseCase(tag.charAt(3));	
        if ( ((tag.charAt(0) == 'f') || (tag.charAt(0) == 't')) && (tag.length() >= 5)) /* pronoun or numeral */
        	return analyseCase(tag.charAt(4));	
        	
        return result;
  }
  
  
  private String analyse(String txt)
  {	
  	String[] strs;
  	String tag;
  	String result="";
        Pattern p = Pattern.compile("\\s+");
         // Get all the individual lexemes as strings
        strs = p.split(txt);
        int len = strs.length;
        if (len >= 4)
		{
        	tag = strs[len-2];	/* strs[len-1] = "NP]" */
	 	
			if(!tag.contains("]"))
			{
				tag = tag.substring(1, tag.length()-1);     // tag is e.g. "^nken$", will become "nken"
            }

        	if (tag.equals("AP]"))	/* t.d. [NP þeirri faveþ [APd gömlu lveþvf AP] NP] */
        	{
        	   int APstart = txt.indexOf("[AP");
        	   if (APstart != -1)
        	   {
        	   	char casChar = txt.charAt(APstart+3);
        	   	return (new Character(casChar).toString());
        	   }
        	   //tag = strs[len-3];
  		   //isHeadNP = false;    /* the last word of the NP is not a NP head */
  		   //char casChar = tag.charAt(tag.length()-1);
  		   //return (new Character(casChar).toString());
        	}
        	else if (tag.equals("ta") || tag.equals("tp"))		/* t.d. [NP árið nheog 1955 ta NP] */
			{
		    	tag = strs[len-4];
		    	tag = tag.substring(1, tag.length()-1);
		    /* System.err.println("Found the tag: " + tag); */
		    //isHeadNP = true;  
		    	return analyseTag(tag);
        	}

        	else if (tag.length() >= 1) {
        	    //isHeadNP = true;
        	    return analyseTag(tag);
        		
        	}
        }
  	
  	return result;
  }
  
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

/* The space in the pattern below is necessary when this transducer is run separately. */
/* In that case this pattern does not match [NPs .. NPs] */

NP = {OpenNP}" "~{CloseNP}

%%

{MWE}		{ out.write(yytext());}
{NP}		{ 	String caseStr,matchedStr,openLabelStr,closeLabelStr;
			int len, labelLength;
			
			matchedStr = yytext();
			len = matchedStr.length(); // matchedStr starts with "[NP " or e.g. "[NP?Ng " in case of error
			labelLength = matchedStr.indexOf(" ");  // The label ends with a space

			caseStr = analyse(matchedStr);
			if (caseStr.equals(""))
			  out.write(matchedStr);
			else {
				openLabelStr = matchedStr.substring(0,labelLength);
				//closeLabelStr = matchedStr.substring(len-labelLength);
				//if (isHeadNP)
				//	theHead = headStrN;
				//else
				//	theHead = headDummyN;
				
				out.write(openLabelStr + caseStr + matchedStr.substring(labelLength));
				//out.write(openLabelStr + caseStr + matchedStr.substring(labelLength,len-labelLength) + theHead + caseStr + " " + closeLabelStr);
				//out.write(openLabelStr + caseStr + matchedStr.substring(labelLength,len-labelLength) + caseStr + closeLabelStr);
				
			}
		}
"\n"			{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
			out.write("\n"); }
.		{ out.write(yytext());}
