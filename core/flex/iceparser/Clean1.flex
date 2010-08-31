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

/* This transducer cleans the output file 			*/
/* (1. Removes NP info from a MWE phrase) phrase_NP now doen't touch MWE */
/* 2. Converts a sequence of adverbial phrases into one		*/
/* 3. Finds the occurence of a nominative adjective phrase inside a dative noun phrase */
/* like, [NPd [APn nátengdara lhenvm AP] sögu nveþ NP]					*/
/* and converts it to [APn nátengdara lhenvm AP] [NPd sögu nveþ NP] 			*/
/* 4. Finds the occurence of a noun phrase which includes two proper nouns, in which 	*/
/* the second one is a qualifier, like [NP Háskóla nkeo-s Íslands nhee-ö NP]		*/
/* Converts it to [NP Háskóla nkeo-s NP] [NP Íslands nhee-ö NP]				*/

package is.iclt.icenlp.core.iceparser;
import java.util.regex.*;
import java.io.*;
import is.iclt.icenlp.core.utils.IceParserUtils;
%%

%public
%class Clean1
%standalone
%line
%extends IceParserTransducer
%unicode



%{
  String encO =  IceParserUtils.encodeOpen;
  String encC =  IceParserUtils.encodeClose;
  
  String rEncO =  IceParserUtils.regexEncodeOpen;
  String rEncC =  IceParserUtils.regexEncodeClose;

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

ProperNounTagNotGen = {encodeOpen}n{Gender}{Number}[noþ]("-"|{ArticleChar}){ProperName}{encodeClose}{WhiteSpace}+
ProperNounTagGen = {encodeOpen}n{Gender}{Number}e("-"|{ArticleChar}){ProperName}{encodeClose}{WhiteSpace}+
ProperNounWithQualifer = {OpenNP}g{WordSpaces}{ProperNounTagNotGen}({WordSpaces}{ProperNounTagGen})+~{CloseNP}

AdvPSeq = (({OpenAdvP}~{AdverbTag}{CloseAdvP}){WhiteSpace}+){2,5}
APs = {OpenAPs}~{CloseAPs}
DatNPWithNomAdjPhrase = {OpenNP}d{WhiteSpace}+{OpenAP}n~{CloseNP}


%%

/*
{MWE}		{ 	String str = yytext();
		  	str = str.replace("[NP","");
		  	str = str.replace("NP]","");
		  	out.write(str);
		}
*/
{AdvPSeq}	{
			String str = yytext();
			str = str.replaceAll("\\[AdvP","");	/* First remove all instances of adverb labels */
			str = str.replaceAll("AdvP]","");
			str = "[AdvP" + str + "AdvP] ";		/* Then add one instance */
		  	out.write(str);
		}
		
{DatNPWithNomAdjPhrase}	{
				String str = yytext();
				str = str.replaceAll("\\[NPd","");
				str = str.replaceAll("AP]","AP] [NPd");
				out.write(str);
			}
		
{ProperNounWithQualifer}	{
					String str = yytext();

					// Search for the first nom/acc/dat proper noun tag 
					//Pattern p = Pattern.compile("\\^"+"n[kvh][ef][noþ](g|-)[msö]"+"\\$");
					Pattern p = Pattern.compile(rEncO+"n[kvh][ef][noþ](g|-)[msö]"+rEncC);

					Matcher m = p.matcher(str);
					if (m.find())
					{
	
						// Return the indexes of the start char matched and the last character matched, plus one.
						int startIdx = m.start();
						int endIdx = m.end();
						String tag = str.substring(startIdx,endIdx);
						tag = tag.substring(encO.length(), tag.length()-encC.length());

				//	System.err.println("Tag: " + tag);
						String caseStr = analyseTag(tag);
						String firstPart = str.substring(0,endIdx);
						String secondPart = str.substring(endIdx);
						// Need to replace [NPg at the beginning for [NPx 
						String replacementStr = "[NP" + caseStr;
						//System.err.println("First part: " + firstPart);
						//System.err.println("Second  part: " + secondPart);
						firstPart = firstPart.replaceFirst("\\[NPg",replacementStr);

						out.write(firstPart + " NP]" + " [NPg " + secondPart);
					}
					//else
					//	System.err.print("Did not match\n");
						
				}	
				
		
"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}
