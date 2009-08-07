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

 /*This .flex file is a definition of a "rule compiler" which is used to generate */
 /* local rules used by IceTagger.  See readme.txt".  */

package is.iclt.icenlp.flex.icetagger;
%%

%public
%class genIceRules
%standalone

%unicode

%{
  String str;
  String[] strs;
  boolean firstRule=true;
  boolean ifExit=false;
  
  private static final String[] map = {	"PERSPRONOUN", "IceTag.WordClass.wcPersPronoun",
  					"POSSPRONOUN", "IceTag.WordClass.wcPossPronoun",
  					"DEMPRONOUN", "IceTag.WordClass.wcDemPronoun",
  					"REFLPRONOUN", "IceTag.WordClass.wcReflPronoun",
  					"PROPERNOUN", "IceTag.WordClass.wcProperNoun",
  					"ADJ", "IceTag.WordClass.wcAdj",
  					"ADVERB", "IceTag.WordClass.wcAdverb",
  					"NOUN", "IceTag.WordClass.wcNoun",
  					"PREP", "IceTag.WordClass.wcPrep",
  					"VERBINF", "IceTag.WordClass.wcVerbInf",
  					"INF", "IceTag.WordClass.wcInf",
  					"CONJREL", "IceTag.WordClass.wcConjRel",
  					"CONJ", "IceTag.WordClass.wcConj",
  					"ARTICLE", "IceTag.WordClass.wcArticle",
  					"NUMERAL", "IceTag.WordClass.wcNumeral",
  					"VERBPASTPART","IceTag.WordClass.wcVerbPastPart",
  					"VERB","IceTag.WordClass.wcVerb",
  					"NOMINATIVE", "IceTag.cNominative",
  					"ACCUSATIVE", "IceTag.cAccusative",
  					"DATIVE", "IceTag.cDative",
  					"GENITIVE", "IceTag.cGenitive",
  					"COMMA", "Token.TokenCode.tcComma",
  					"MASCULINE", "IceTag.cMasculine",
  					"FEMININE", "IceTag.cFeminine",
  					"NEUTER", "IceTag.cNeuter",
  					"prevTokenFirstTag", "((IceTag)prevToken.getFirstTag())",
  					"nextTokenFirstTag", "((IceTag)nextToken.getFirstTag())"
  					};
  
  private void errorExit(String errorStr)
  {
    	System.err.println(errorStr);
	System.exit(0);
  }
  
  private void printStartClass()
  {
  	System.out.println("package is.iclt.icenlp.flex.icetagger;");
  	System.out.println("import is.iclt.icenlp.core.tokenizer.*;");
  	System.out.println("import is.iclt.icenlp.core.utils.*;");
  	//System.out.println("import java.io.IOException;");
  	System.out.println();
	System.out.println("/**");
	System.out.println(" * Local reductionistic rules for Icelandic text.");
 	System.out.println(" * <br> Used by the IceTagger class.");
 	System.out.println(" * @author Hrafn Loftsson");
	System.out.println(" */");
  	System.out.println();
  	System.out.println("public class IceLocalRules {");
  	System.out.println("// This class is generated automatically from a .flex file");
	System.out.println("\tprivate IceLog logger=null;    // Logfile file");
	System.out.println("\tprivate boolean didDisambiguate=false;");
	System.out.println();
	System.out.println("\tpublic IceLocalRules(IceLog log)");
	System.out.println("\t{");
	System.out.println("\t\tlogger = log;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tpublic void setDisambiguateFlag(boolean flag)");
	System.out.println("\t{");
	System.out.println("\t\tdidDisambiguate=flag;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tpublic boolean getDisambiguateFlag()");
	System.out.println("\t{");
	System.out.println("\t\treturn didDisambiguate;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tprivate void disAllowTag(IceTokenTags currToken, IceTag tag)");
	//System.out.println("\tthrows IOException");
	System.out.println("\t{");
	System.out.println("\t// Only disambiguate if more than one tag left");
	System.out.println("\t\tif (currToken.numTags() > 1)");
	System.out.println("\t\t{");
	System.out.println("\t\t\tString logStr = \"Local disambiguation: \" + currToken.toString();");
	System.out.println("\t\t\ttag.setValid(false);");
	System.out.println("\t\t\tlogStr = logStr + \" Disallowed \" + tag.getTagStr();");
	System.out.println("\t\t\tif (logger != null)");
	System.out.println("\t\t\t\tlogger.log(logStr);");
	System.out.println("\t\t\tdidDisambiguate = true;");
	System.out.println("\t\t}");
	System.out.println("\t}");
	System.out.println();
    }
  
  private void printEndClass()
  {
  	System.out.println("} // end class");
  }
  
  
  private void printFunctionStart(String func)
  {
  	System.out.println("public void check" + func + "(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)");
  	//System.out.println("throws IOException");
  	System.out.println("{");
  	printIfStart(false);
  }
  
  private void printFunctionEnd()
  {
  	printIfEnd();
  	System.out.println("}");
  	System.out.println();
  }
  
  private void printIfStart(boolean withElse)
  {
  	if (!withElse)
  		System.out.print("if(");
  	else
  		System.out.print("else if(");
  }
  
  private void printIfEnd()
  {
  	System.out.println();
    	System.out.println("\t)");
    	System.out.println(")");
    	if (ifExit)
    		System.out.println(")");
    	System.out.println("\tdisAllowTag(currToken, tag);");
    	if (ifExit)
    		System.out.println("}");
    	System.out.println();
  }
  
  private void printIfCondition(String str)
      {
      	System.out.println(str + " && ");
  }
  
  private void printIfExitCondition(String str)
  {
        	System.out.println(str + ")");
        	System.out.println("{");
        	printIfStart(false);
  }
  
  private String replace(String str)
  {
  	String key, value;
  	String changedStr = str;
  	for (int i=0; i<=map.length-2; i=i+2)
  	{
  		key = map[i];
  		value = map[i+1];
  		changedStr = changedStr.replace(key,value);
  	}
  	return changedStr;
  }
  
  private void printCode(String str)
  {
  	String codeStr;
  	codeStr = replace(str);
    	System.out.print(codeStr);
  }
  
%}

WhiteSpace = [ \t\f\n\r]
WordChar = [^\r\n\t\f; ]
String = {WordChar}+
Strings = ({String}{WhiteSpace}*)+
Comment = {WhiteSpace}*"//"~"\n"

StartClass = STARTCLASS
EndClass = ENDCLASS
Else = ELSE

Begin = BEGIN{WhiteSpace}+{String}
End = END{WhiteSpace}+{String}
If = IF{WhiteSpace}+{Strings}";"
IfExit = IFEXIT{WhiteSpace}+{Strings}";"
Rule = RULE{WhiteSpace}+{Strings}";"


%%
{StartClass}	{ printStartClass();}

{EndClass}	{ printEndClass();}

{Begin}		{ 	
			
			str = yytext();
			strs = str.split("\\s");
			if (strs.length < 2)
			   errorExit("Missing function name after BEGIN");
			   
			String func = strs[1];
			printFunctionStart(func);
			firstRule = true;
		}
		
{End}		{	printFunctionEnd();	
			firstRule = false;
		}
		
{Else}		{
			printIfEnd();
			ifExit=false;
			printIfStart(true);
			firstRule = true;
		}
		
{If}	{ 	
			
			str = yytext();
			str = str.replace("IF","");
			str = str.replace(";","");

			ifExit=false;
			printIfCondition(str);
		}
{IfExit}	{ 	
			
			str = yytext();
			str = str.replace("IFEXIT","");
			str = str.replace(";","");

			ifExit=true;
			printIfExitCondition(str);
		}

		
{Rule}		{	str = yytext();
			str = str.replace("RULE","");
			str = str.replace(";","");
			
			if (firstRule) {
				printCode("\t(\n");
				printCode("\t(");
			}
			else {
			   	printCode(" ||\n");
				printCode("\t(");
			}
			printCode(str);
			printCode(")");
			
			firstRule = false;
			
		}

{Comment}	{
			System.out.print(yytext());
		}
		
.		{;}
\n		{;}
