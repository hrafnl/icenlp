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
 
/* This transducer marks noun phrases */
/* A noun phrase can include a sequence of adjectival phrases */

/* A number of cases are covered, e.g: 				*/
/* allt (indef. pronoun) 					*/
/* ég	(pers. pronoun)						*/
/* maður (noun)							*/
/* Jón	(proper noun)						*/
/* Jón Jónsson (proper noun, proper noun)			*/
/* frú Sigrún (title(noun), proper noun)			*/
/* minn (poss. pronoun)						*/
/* maðurinn minn (noun, poss. pronoun)				*/
/* sinn sess (poss. pronoun, noun				*/
/* sinn rétta tíma (poss. pronoun, adjective, noun		*/
/* mennirnir tveir (noun, numeral)				*/
/* hver	(inter. pronoun)					*/
/* sjálfan sig (refl. pronoun, pers. pronoun) 			*/
/* honum sjálfum (pers. pronoun, refl. pronoun) 		*/
/* þetta allt (dem. pronoun, indef. pronoun 			*/
/* landið allt (noun, indef. pronoun				*/
/* allir aðrir (indef. pronoun, indef. pronoun			*/
/* land þetta allt (noun, dem. pronoun, indef. pronoun 		*/
/* þeirri gömlu (dem. pronoun, adj phrase)			*/
/* hinn stóri (article, adj phrase) 				*/
/* hinir tveir stóru menn (article, numeral, adj phrase, noun)	*/
/* þrír stórir strákar 	  (numeral, adj phrase, noun)		*/
/* hvaða stóru strákar (inter. pronoun, adj. phrase, noun	*/
/* stórir strákar 	  (adj phrase, noun)			*/
/* allir þessir þrír stóru strákar (indef. prounoun, dem. pronoun, numeral, adj phrase, noun) */
/* hlaðin einhverju	  (adj phrase, indef. prounoun)	á að leyfa þetta, hlaðin stýrir falli	*/
/* einhverju hátíðlegu	  (indef. pronoun, adj phrase		*/
/* gamla, litla og mjóa manninum (adj phrases, noun)		*/
/* fyrstu átta vikunum (adj phrase, numeral, noun)		*/



package is.iclt.icenlp.core.iceparser;
import java.io.*;
import java.util.*;
import is.iclt.icenlp.core.utils.IceParserUtils;
%%

%public
%class Phrase_NP
%standalone
%line
%extends IceParserTransducer
%unicode

%{
	String NPOpen=" [NP ";
	String NPClose=" NP] ";
	String ErrNPOpen=" [NP? ";
	String ErrNPClose=" NP?] ";
	
	static String encO =  IceParserUtils.encodeOpen;
	static String encC =  IceParserUtils.encodeClose;

	boolean agreement = false;  // -a parameter attribute	;
	boolean markGrammarError = false;
	
	//java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
	java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
      	
	public void set_doAgreementCheck(boolean option)
	{
		agreement = option;
	}
	public void set_markGrammarError(boolean option)
	{
		markGrammarError = option;
		// If we want grammatical errors to be shown then make sure that the agreement flag is true as well
                if (markGrammarError) 
                        agreement = true;

	}
	public void parse(java.io.Writer _out) throws java.io.IOException
	{
	    	out = _out;
	    	while (!zzAtEOF) 
	    	    yylex();
	}

	private String FinalCheck(String originalStr)
	{
	//System.out.println(originalStr);

		if(!agreement && !markGrammarError)//do nothing
		{
			return NPOpen + originalStr + NPClose;
		}
		String tokenlessStr = RemoveTokens(originalStr);

	//System.out.println("tokenelss: " + tokenlessStr);
		
		boolean allTheSame = CheckGenNumCase(tokenlessStr);

		if(allTheSame)
		{
			return NPOpen + originalStr + NPClose;
		}
		else
		{
			if(markGrammarError)
				return ErrNPOpen + originalStr + ErrNPClose;

			return originalStr;
		}
	}
	public static String RemoveTokens(String str)
	{
		str = IceParserUtils.RemoveFromSymbolToWhitespace("[", str);
		str = new StringBuffer(str).reverse().toString();
		str = IceParserUtils.RemoveFromSymbolToWhitespace("]", str);
		str = new StringBuffer(str).reverse().toString();
		str = IceParserUtils.RemoveSpacesAndWords(str);
			
		return str;
	}
	public static int GetModifier(String letter)
	{
		if(letter.equals("n") || letter.equals("l") || letter.equals("g") )
			return 0;
		if(letter.equals("f") || letter.equals("t"))
			return 1;

		return -1;
	}
	public static Boolean CheckGenNumCase(String str)
	{
		boolean allTheSame = true;
		String [] tags = null;
		tags = str.split(" ");

		for(int i=0; i<tags.length; i++)
		{
			for(int x=i+1; x<tags.length; x++)
			{
				int mod1, mod2;
				String tagI = tags[i].substring(encO.length(), tags[i].length()-encC.length());
				String tagX = tags[x].substring(encO.length(), tags[x].length()-encC.length());
				
				mod1 = GetModifier(tagI.substring(0,1));
				mod2 = GetModifier(tagX.substring(0,1));

			//System.out.println("Mod1 : "+mod1+"\nMod2 : "+mod2);

				if(mod1 == -1 || mod2 == -1) continue;

				//ef t þá verður 2 sæti að vera f
			
				if(tagI.length() < 4+mod1 || tagX.length() < 4+mod2)
				{
					continue;
				}


				String gen1,num1,case1, gen2,num2,case2;

				gen1 = tagI.substring(1+mod1, 2+mod1);
				num1 = tagI.substring(2+mod1, 3+mod1);
				case1 = tagI.substring(3+mod1, 4+mod1);
			
				gen2 = tagX.substring(1+mod2, 2+mod2);
				num2 = tagX.substring(2+mod2, 3+mod2);
				case2 = tagX.substring(3+mod2, 4+mod2);
				
			//System.out.println("gnc1 : "+gen1+" - "+num1+" - "+case1);				
			//System.out.println("gnc2 : "+gen2+" - "+num2+" - "+case2);

				if( !gen1.equals(gen2) || !num1.equals(num2) || !case1.equals(case2))
				{	
					allTheSame = false;
				}
			}
		}
		return allTheSame;
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

ArticleTag = {encodeOpen}g{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
PossPronounTag = {encodeOpen}fe{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
IndefPronounTag = {encodeOpen}fo{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
InterPronounTag = {encodeOpen}fs{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
DemonPronounTag = {encodeOpen}fa{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+
ReflexivePronounTag = {encodeOpen}fb{Gender}{Number}{Case}{encodeClose}{WhiteSpace}+

Noun = {WordSpaces}{NounTag}
ProperNoun = {WordSpaces}{ProperNounTag}
PersPronoun = {WordSpaces}{PersPronounTag} 
PossPronoun = {WordSpaces}{PossPronounTag} 
IndefPronoun = {WordSpaces}{IndefPronounTag} 
Hvada = {WhiteSpace}*[Hh]vaða{WhiteSpace}+{InterPronounTag}
Hvad = {WhiteSpace}*[Hh]v(að|((er|or)[a-z]*)){WhiteSpace}+{InterPronounTag}
DemonPronoun = {WordSpaces}{DemonPronounTag} 
ReflexivePronoun = {WordSpaces}{ReflexivePronounTag} 
Numeral = {WordSpaces}{NumeralTag}
Article = {WordSpaces}{ArticleTag}
Title = {WhiteSpace}*(([uU]ng)?[fF]r(ú|öken)|[hH](erra|r\.)|[sS][íé]ra|[dD]r\.){WhiteSpace}+{NounTag}

AdjectivePhrase = {OpenAP}~{CloseAP}
AdjectivePhrases = {WhiteSpace}*({OpenAPs}~{CloseAPs}| {AdjectivePhrase} |{MWE_AP})

NounProperPoss = {ReflexivePronoun}?({Noun}|{ProperNoun})({PossPronoun}|{Numeral})?
ReflNP = {ReflexivePronoun}({Noun}|{PersPronoun})?
HvadaNP = {Hvada}{Numeral}?{AdjectivePhrases}?{NounProperPoss}
NumNP = {Numeral}({AdjectivePhrases}?{NounProperPoss})?
ArticleNP = {Article}{Numeral}?{AdjectivePhrases}?{Noun}?
PersNP = {PersPronoun}{ReflexivePronoun}?
PossNP = {PossPronoun}({AdjectivePhrases}?{Noun})?
DemonNP = {DemonPronoun}({Numeral}?{AdjectivePhrases}?{NounProperPoss}? | {IndefPronoun})
IndefNP = {IndefPronoun}+(({Article}|{DemonPronoun})?{Numeral}?{AdjectivePhrases}?{NounProperPoss}? | {PossNP})
AdjAP = {AdjectivePhrases}{Numeral}?{NounProperPoss}
ProperNounNP = {Title}?{ProperNoun}+({ReflexivePronoun}|{PossPronoun})?
NounNP = {Noun}({ReflexivePronoun}|{Numeral}|{DemonPronoun}?{IndefPronoun}|{PossPronoun})?


NounPhrase = {Hvad} | {HvadaNP} | {ReflNP} | {ArticleNP} | {DemonNP} | {IndefNP} | {PersNP} | {PossNP} | {NumNP} | {AdjAP} | {NounNP} | {ProperNounNP}


%%

{MWE}			{ out.write(yytext());} 		// Don't touch multi-word expression 

{NounPhrase}
{
	String str = yytext();		
	out.write(FinalCheck(str));
}



"\n"		{ //System.err.print("Reading line: " + Integer.toString(yyline+1) + "\r"); 
		out.write("\n"); }
.		{ out.write(yytext());}
