package is.iclt.icenlp.flex.icetagger;
import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.utils.*;

/**
 * Local reductionistic rules for Icelandic text.
 * <br> Used by the IceTagger class.
 * @author Hrafn Loftsson
 */

public class IceLocalRules {
// This class is generated automatically from a .flex file
	private IceLog logger=null;    // Logfile file
	private boolean didDisambiguate=false;

	public IceLocalRules(IceLog log)
	{
		logger = log;
	}

	public void setDisambiguateFlag(boolean flag)
	{
		didDisambiguate=flag;
	}

	public boolean getDisambiguateFlag()
	{
		return didDisambiguate;
	}

	private void disAllowTag(IceTokenTags currToken, IceTag tag)
	{
	// Only disambiguate if more than one tag left
		if (currToken.numTags() > 1)
		{
			String logStr = "Local disambiguation: " + currToken.toString();
			tag.setValid(false);
			logStr = logStr + " Disallowed " + tag.getTagStr();
			if (logger != null)
				logger.log(logStr);
			didDisambiguate = true;
		}
	}

public void checkProperNoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 

// hús Láru
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.isCase(IceTag.cGenitive) && !tag.isCase(IceTag.cGenitive) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && !tag.caseMatch(prevToken.getTags()) ) 
// Hildar Guðmundsdóttur
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && prevToken.isOnlyCase(IceTag.cGenitive) && currToken.lexeme.endsWith("dóttur") && currToken.isCase(IceTag.cGenitive) && !tag.isCase(IceTag.cGenitive) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && prevToken.isOnlyGender(IceTag.cFeminine) && !tag.isGender(IceTag.cFeminine) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && prevToken.isOnlyGender(IceTag.cMasculine) && !tag.isGender(IceTag.cMasculine) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkNoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 

// hann sýnir but stakk hún hendi is ok, það sýnir aldrei
	(
	( (prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) || (prevToken.isPersonalPronoun() && prevToken.isDemonstrativePronoun()) || prevToken.lexeme.equalsIgnoreCase("við")) 
	&& currToken.isVerb() && prevToken.personNumberMatch(currToken)
	&& (prevprevToken == null || (!prevprevToken.isVerb() && !prevprevToken.isVerbInfinitive())) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && !tag.caseMatch(prevToken.getTags()) )
// að (cn/aa) beina (sgn/n*) mér
 ||
	( prevToken.isInfinitive() && currToken.isVerbInfinitive() )
// einhvern sem sæti
 ||
	( prevToken.lexeme.equalsIgnoreCase("sem") && currToken.isVerbAny() ) 
// choose the verb
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && currToken.isVerb() ) 
// Disallow two nouns in a row unless the latter is in the genitive case
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && !prevToken.isCase(IceTag.cGenitive) && !tag.isCase(IceTag.cGenitive) 
	&& (prevprevToken == null || !prevprevToken.isProperNoun()) )
// select the possessive pronoun, "pabbi sinn"
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.isPossessivePronoun() ) ||
	( currToken.lexeme.equalsIgnoreCase("á") && prevToken.isAdverb() )
// skal (ekki) hita
 ||
	( currToken.isVerbInfinitive() && (prevToken.isVerbSpecialAuxiliary() || (prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && prevprevToken!=null && prevprevToken.isVerbSpecialAuxiliary())) )
// hafði talið, velja sagnbót
 ||
	( currToken.isVerbSupine() && prevToken.isVerbAuxiliary() )
// select the adjective, but ok is "fyrir framan X", and ok is "bara verð markaðarins"
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && currToken.isAdjective() 
	&& (prevprevToken == null || !prevprevToken.isPreposition()) 
	&& (nextToken == null || !(nextToken.isNoun() && nextToken.isCase(IceTag.cGenitive))))
// sú líkn
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) && prevToken.isOnlyCase(IceTag.cNominative) && !tag.isCase(IceTag.cNominative) ) ||
	( prevToken.isVerbBe() && !tag.isCase(IceTag.cNominative) )
// select the adjective, "hann er lasinn",
 ||
	( prevToken.isVerbBe() && currToken.isAdjective() && prevprevToken != null && prevprevToken.isPersonalPronoun() )
	)
)
	disAllowTag(currToken, tag);

else if( prevprevToken != null  && 

// Case agreement with preposition two words to the left, but only remove if a case match exists
	(
	(  prevprevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && !prevToken.isReflexivePronoun() 
	&& !tag.isCase(IceTag.cGenitive) && currToken.caseMatch(prevprevToken) && !tag.caseMatch(prevprevToken.getTags()) )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 

// Disallow two nouns in a row unless the latter is in genitive case
// Also legal is: að minnsta kosti (þgf) herbergi (þf): Not covered!
	(
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && !currToken.isOnlyWordClass(IceTag.WordClass.wcNoun) 
	&& prevToken != null && !prevToken.isPreposition() 
	&& !nextToken.isCase(IceTag.cGenitive) && !tag.isCase(IceTag.cGenitive) ) 
// choose the adjective
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && !nextToken.isCase(IceTag.cGenitive) 
	&& currToken.isAdjective() && currToken.genderNumberCaseMatch(nextToken) )
// árið (þf) 1904
 ||
	( currToken.lexeme.equalsIgnoreCase("árið") && tag.isCase(IceTag.cNominative) && nextToken.isNumeralOrdinal() ) ||
	( currToken.lexeme.equalsIgnoreCase("á") && (nextToken.isNoun() || nextToken.isAdjective()) )
// Helps with unknown words that are possibly both a noun and a proper noun
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && tag.isNoun() && currToken.isProperNoun() )
// choose the verb: gat ég
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) 
	&& (nextToken.isCase(IceTag.cNominative) || nextToken.isCase(IceTag.cAccusative)) 
	&& currToken.isVerb() )
// o.fl.
 ||
	( currToken.lexeme.equals("o.") && nextToken.lexeme.equals("fl.") )
	)
)
	disAllowTag(currToken, tag);

}

public void checkAdj(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 

// hann sýnir.  Same condition as for nouns
	(
	( (prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) || (prevToken.isPersonalPronoun() && prevToken.isDemonstrativePronoun()))
	&& currToken.isVerb() && !currToken.isVerbPastPart() 
	&& (prevprevToken == null || (!prevprevToken.isVerb() && !prevprevToken.isVerbInfinitive())) )
// að (cn/aa) beina (sgn/l*) mér 
 ||
	( prevToken.isInfinitive() && currToken.isVerbInfinitive() )
// niður í tær (select the noun)
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && !tag.caseMatch(prevToken.getTags()) )
// miklu meira, bjartur depill (unknown)
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdj) 
	&& (currToken.isAdverb() || (currToken.isNoun() && currToken.genderNumberCaseMatch(prevToken))) )
// þessi dýr 
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) && currToken.isNoun() 
	&& (nextToken == null || !nextToken.isNoun())  )
// choose the verb    tunglið óð í skýjum
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.isVerb() 
	&& nextToken != null && nextToken.isOnlyWordClass(IceTag.WordClass.wcPrep) )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 
	(
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) && tag.isAdjectivePositive() )
// select the noun
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) && currToken.isNoun() )
// select the adverb
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) && currToken.isAdverb() )
// if the next is a noun and the current is an adverb than remove the adjective if not a match with the noun
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.isAdverb() 
	&& !nextToken.isOnlyCase(IceTag.cGenitive) && !currToken.genderNumberCaseMatch(nextToken) )
// fyrst hún borðaði, fyrst þeir fóru
 ||
	( currToken.lexeme.equalsIgnoreCase("fyrst") && (nextToken.isNoun() || nextToken.isProperNoun() || nextToken.isPersonalPronoun()) 
	&& nextnextToken != null && nextnextToken.isVerbActive() ) 
// choose the supine, "bætt um betur"
 ||
	( currToken.isVerbSupine() && nextToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) )
// choose the adverb
 ||
	( currToken.lexeme.endsWith("lega") && currToken.isAdverb() && !nextToken.isNoun() )
// eins og
 ||
	( currToken.lexeme.equalsIgnoreCase("eins") && nextToken.lexeme.equalsIgnoreCase("og") )
// margra landa
 ||
	( tag.isAdjectiveComparative() && currToken.isAdjectivePositive() && nextToken.isNoun() && !tag.genderNumberCaseMatch(nextToken.getTags()))
	)
)
	disAllowTag(currToken, tag);

}

public void checkAdverb(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcInf) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPossPronoun) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcArticle) )
 // "Jón heldur að" but ok is "ekki Jón heldur"
 ||
	( currToken.lexeme.equals("heldur") && (prevprevToken == null || !prevprevToken.lexeme.equalsIgnoreCase("ekki")) 
	&& (prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun))
        && prevToken.personNumberMatch(currToken) )
// þeim stundum
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) && currToken.lexeme.equals("stundum") ) 
// Tárin sem
 ||
	( (prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) && currToken.lexeme.equalsIgnoreCase("sem") )
// Choose the adjective : "nægilega sterkt skip"
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && currToken.isAdjective() 
	&& nextToken != null && nextToken.isNoun() && currToken.genderNumberCaseMatch(nextToken) )
// Choose the adjective: "atriði eru eins."
 ||
	( currToken.isAdjective() && prevToken.isVerbBe() && nextToken != null && nextToken.isPunctuation() )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null && !nextToken.lexeme.equalsIgnoreCase("á")  && 

// vissi að hún ...
	(
	( currToken.lexeme.equalsIgnoreCase("að") && (nextToken.isVerbInfinitive() || nextToken.isNoun() || nextToken.isPersonalPronoun()) )
// við erum
 ||
	( currToken.lexeme.equalsIgnoreCase("við") && nextToken.isVerbBe() ) 
// stjórna því sem..
 ||
	( currToken.lexeme.equalsIgnoreCase("því") && nextToken.isRelativeConjunction() ) 
// fyrst þeir fóru
 ||
	( currToken.lexeme.equalsIgnoreCase("fyrst") 
	&& (nextToken.isNoun() || nextToken.isProperNoun() || nextToken.isPersonalPronoun()) 
	&& nextnextToken != null && nextnextToken.isVerbActive() ) ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcConjRel) ) ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcReflPronoun) )
// choose the adjective, if a match exists
 ||
	( (currToken.isAdjective() || currToken.isIndefinitePronoun()) 
	&& (nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) && currToken.genderNumberCaseMatch(nextToken) )
// hægt er, hægt að sinna
 ||
	( currToken.isAdjective() && (currToken.lexeme.equalsIgnoreCase("hægt") || currToken.lexeme.equalsIgnoreCase("rétt")) 
	&& (nextToken.isVerbBe() || nextToken.isOnlyWordClass(IceTag.WordClass.wcInf)) )
// Choose the noun
 ||
	( currToken.isNoun() && prevToken != null && prevToken.isNumeral() )	
// Remove if the current is also preposition - "Um leið ...", but ok is "fyrir utan (aa_ae) gluggann"
 ||
	( currToken.isPreposition() && (prevToken == null || !prevToken.isPreposition())  
	&& !nextToken.lexeme.equals("við") && nextToken.isNominal() && currToken.caseMatch(nextToken) )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null && nextToken != null  && 

// mynd SEM túlkaði
	(
	( prevToken.isNoun() && nextToken.isOnlyVerbAny() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkPersonalPronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 

// þá auðveldustu
// select the demonstrative pronoun
	(
	(  nextToken.isOnlyAdjectiveSuper() ) ||
	( currToken.lexeme.equalsIgnoreCase("þá") 
	&& (	(nextToken.isVerb() && !nextToken.isVerbCaseMarking()) ||
                nextToken.isConjunction() || nextToken.isAdverb() || 
                nextToken.isNominal()
           ) )
// stjórna því sem..
 ||
	( currToken.lexeme.equalsIgnoreCase("því") 
	&& (nextToken.isVerbPastPart() || nextToken.isRelativeConjunction()) ) 
// select the demonstrative , "þeir sem", "þeir, sem"
 ||
	( (currToken.lexeme.equals("þeir") || currToken.lexeme.equals("þær") || currToken.lexeme.equals("þau") ||
      currToken.lexeme.equalsIgnoreCase("það") || currToken.lexeme.equalsIgnoreCase("þess")) &&
      (nextToken.lexeme.equalsIgnoreCase("sem") ||
      (nextToken.lexeme.equals(",") && nextnextToken != null && nextnextToken.lexeme.equalsIgnoreCase("sem"))) )
// við kirkjugarðinn
// "Við Helgi fórum" is ok  , "hann pabbi" is ok, "til mín augunum" is ok, "komum við síðdegis", is ok
 ||
	( currToken.lexeme.equalsIgnoreCase("við") 
	&& ((nextToken.isNominal() && currToken.caseMatch(nextToken) && !nextToken.isCase(IceTag.cNominative) 
	&& !nextToken.isVerbAny() 
	&&  (prevToken==null || !(prevToken.isVerb() && currToken.personNumberMatch(prevToken))))   // "vorum við" is ok
          || 
        (nextToken.isPunctuation() && prevToken != null && !currToken.personNumberMatch(prevToken)) // á bak við?
           ) )
// í því skyni
// Important: Only assume demonstrative pronoun if there is a match with the following noun
 ||
	(  nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) 
	&& currToken.isDemonstrativePronoun() && currToken.genderNumberCaseMatch(nextToken) )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 

// snúa sér við, bætti hún við
// sneri blaðinu við, hafði rangt við, blasti tóm flaskan við
	(
	( currToken.lexeme.equalsIgnoreCase("við") 
    && (
           (prevprevToken!=null && (prevprevToken.isVerb() || prevprevToken.isVerbInfinitive() || prevprevToken.isAdjective()) 
		&&  (prevToken.isNoun() || prevToken.isPersonalPronoun() || prevToken.isReflexivePronoun())
           ) ||
    	prevToken.isOnlyWordClass(IceTag.WordClass.wcVerbInf) || // snúa við
    	(prevToken.isOnlyVerbAny() && (
    	prevToken.isVerbSupine() ||                    // haldið við
        prevToken.isVerbPastPart() ||                    // var bætt við
        prevToken.isVerbMiddleForm() ||                   // ræðst við
        (prevprevToken != null && prevprevToken.lexeme.equalsIgnoreCase("við")) )) // við gengum við
    ) )
// Choose the adverb
 ||
	( currToken.lexeme.equalsIgnoreCase("því") && currToken.isAdverb() 
	&& (prevToken.isVerbBe() || prevToken.isVerbMiddleForm()) ) // er því, gefst því
// væri þá and réði sig þá
 ||
	( currToken.lexeme.equalsIgnoreCase("þá") 
	&& (prevToken.isVerbBe() || prevToken.isOnlyWordClass(IceTag.WordClass.wcReflPronoun) || prevToken.isPersonalPronoun()) )  // finnst þér þá
// Er mamma þín við?
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPossPronoun) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkIndefinitePronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( (nextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) || (nextToken.isPersonalPronoun() && nextToken.isDemonstrativePronoun())) 
	&& !nextToken.isCase(IceTag.cGenitive) ) ||
	( currToken.lexeme.equalsIgnoreCase("hvort") && !nextToken.isPreposition() 
	&& !nextToken.lexeme.equalsIgnoreCase("annað") && !nextToken.isCase(IceTag.cGenitive) ) // í lagi er: hvort tveggja, hvort annað
// "bæði (c) Jón og Gunna", but "bæði svínin" is ok
 ||
	( currToken.lexeme.equalsIgnoreCase("bæði") && !currToken.genderNumberCaseMatch(nextToken) )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( prevToken.isPreposition() && !tag.caseMatch(prevToken.getTags()) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkDemonstrativePronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 

// sá hinn sami
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) ) ||
	( prevToken.isPreposition() && !tag.caseMatch(prevToken.getTags()) ) ||
	( currToken.lexeme.equalsIgnoreCase("þá") 
	&& (prevToken.isVerbBe() || prevToken.isOnlyWordClass(IceTag.WordClass.wcReflPronoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun)))
// pabbi sá auglýsingu
 ||
	( currToken.lexeme.equals("sá") && prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && nextToken != null && !nextToken.isPunctuation() )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 

// hlýtur þá að vera
	(
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcInf) ) ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) )
// hinn (article) þreytti maður
 ||
	( currToken.isArticle() && nextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) ) ||
	( nextToken.isVerbBe() && currToken.isPersonalPronoun() ) ||
	( currToken.lexeme.equalsIgnoreCase("þá") 
	&& ( (nextToken.isVerb() && !nextToken.isRelativeConjunction()) ||    // þá sem ...
             ((nextToken.isConjunction() || nextToken.isAdverb()) && prevToken == null) || // Þá þegar
             ((nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) &&
                        prevToken != null && prevToken.isOnlyVerbAny())) )   // gekk þá maðurinn
 ||
	( currToken.lexeme.equalsIgnoreCase("því") && nextToken.isVerbPastPart() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkReflexivePronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 

// "í sjálfum mér" is ok
	(
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) 
	&& !(nextToken.lexeme.equalsIgnoreCase("mér") || nextToken.lexeme.equalsIgnoreCase("þér")) )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( currToken.lexeme.equalsIgnoreCase("sér") 
	&& (prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) ||
                        prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkInterrogativePronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( nextToken.isIndefinitePronoun() )
// í lagi er: hvort er
 ||
	( currToken.lexeme.equalsIgnoreCase("hvort") && !nextToken.isVerb() ) ||
	( currToken.lexeme.equalsIgnoreCase("hvers") && nextToken.isNoun() )   // hvers kyns: hér er hvers óáfn.
// hver einasti
 ||
	( currToken.lexeme.equalsIgnoreCase("hver") && (nextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) || nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun)) )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) )
// "í hverju orði", "á hverju götuhorni" but "í hvaða formi" is ok
 ||
	( !currToken.lexeme.equals("hvaða") && nextToken != null && prevToken.isPreposition() && nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkPossessivePronoun(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 

// í fyrsta sinn
	(
	( prevToken.isAdjectiveSuper() ) ||
	( prevToken.isDemonstrativePronoun() || prevToken.isIndefinitePronoun() || prevToken.isInfinitive() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkPreposition(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( currToken.lexeme.equalsIgnoreCase("vegna") )
{
if(	(
	( (nextToken != null && !(nextToken.isNominal() && nextToken.isCase(IceTag.cGenitive)) 
	&& (prevToken == null ||  !(prevToken.isNominal() && prevToken.isCase(IceTag.cGenitive)))) ||
           (nextToken == null && prevToken != null && !(prevToken.isNominal() && prevToken.isCase(IceTag.cGenitive)) )
	)
)
)
	disAllowTag(currToken, tag);
}

else if( currToken.lexeme.equalsIgnoreCase("að")  && 
	(
	( prevToken != null 
	&& (prevToken.lexeme.equals("til") || prevToken.lexeme.equals("því") || prevToken.lexeme.equals("um")) )
// til þess að
 ||
	( prevToken != null && prevprevToken != null && prevprevToken.lexeme.equalsIgnoreCase("til") && prevToken.lexeme.equals("þess") ) ||
	( nextToken != null 
	&& (nextToken.isVerbInfinitive() || nextToken.isOnlyWordClass(IceTag.WordClass.wcAdverb)) )
	)
)
	disAllowTag(currToken, tag);

else if(
// á móti mér
	(
	( currToken.lexeme.equals("á") && 
 (
	(nextToken != null && nextToken.lexeme.equals("móti") && 
	 nextnextToken != null && nextnextToken.isNominal() && nextToken.caseMatch(nextnextToken)) ||        
// Hver á ...
	(prevToken != null && prevToken.isInterrogativePronoun() && 
	(prevToken.lexeme.equalsIgnoreCase("hver") || prevToken.lexeme.equalsIgnoreCase("hvað"))) ||
// Ég á
	(prevprevToken == null && prevToken != null && prevToken.isPersonalPronoun()) ||
// Ég/Maður á (ekki) að
	(prevToken != null && (prevToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || prevToken.isProperNoun()) &&
	prevToken.personNumberMatch(currToken) && prevToken.isCase(IceTag.cNominative) &&
	nextToken != null && (nextToken.isOnlyWordClass(IceTag.WordClass.wcInf) || nextToken.lexeme.equals("ekki") || nextToken.lexeme.equals("von")))
 ))
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( currToken.lexeme.equalsIgnoreCase("við") &&  prevToken.isVerbAny() && currToken.personNumberMatch(prevToken) )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 
// innan undir er=sfg3en<v>_sfg1en<v>_ct_c
	(
	( nextToken.lexeme.equalsIgnoreCase("er") ) ||
	( nextToken.isOnlyVerbAny() ) ||
	( nextToken.isRelativeConjunction() ) ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcInf) && currToken.lexeme.equals("eftir") )
// "með að", "eftir að"
 ||
	( (currToken.lexeme.equals("með") || currToken.lexeme.equals("eftir")) && nextToken.lexeme.equals("að") && tag.isCase(IceTag.cDative))
// "blá í framan af kulda",  langt í burtu
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && nextnextToken != null 
	&& (nextnextToken.isPreposition() || nextnextToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) ||
            nextnextToken.isConjunction() || nextnextToken.isPunctuation() ||
            // að svo fjölmenn ...
            ( (nextnextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) ||
               nextnextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) ||  // hefur í senn lífsviðurværi
               nextnextToken.isOnlyWordClass(IceTag.WordClass.wcNumeral) ||
               nextnextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) ||
               nextnextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun))  // fyrir aftan hana
              && !tag.caseMatch(nextnextToken.getTags()))) ) ||
	( ((nextToken.isAdjective() && !(nextToken.isAdverb() && nextnextToken != null && nextnextToken.isAdjective()) && !nextToken.isOnlyCase(IceTag.cGenitive)) ||  // með helsti kynlegu ...
          nextToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) ||
          nextToken.isArticle()  ||
          (nextToken.isOnlyWordClass(IceTag.WordClass.wcNumeral) && !nextToken.isNumeralOrdinal() &&
                    !nextToken.isOnlyCase(IceTag.cGenitive))) 
         && !tag.caseMatch(nextToken.getTags())  )
// Ok is "viss um , að" or "að hugsa um."
 ||
	( nextToken.isPunctuation() && !currToken.lexeme.equals("um")
	&& (nextnextToken==null || !nextnextToken.lexeme.equalsIgnoreCase("að")) 
	&& !(nextToken.lexeme.equals("«") && nextnextToken != null && tag.caseMatch(nextnextToken.getTags())) )
// "hugsa um (ao) að (c)" er í lagi, "eftir að" er í lagi
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcConj) && !currToken.lexeme.equals("um") 
	&& !(currToken.lexeme.equals("eftir") && tag.isCase(IceTag.cAccusative) && nextToken.lexeme.equals("að")) )
// "Eigum við ekki"
 ||
	( currToken.lexeme.equalsIgnoreCase("við") && nextToken.lexeme.equalsIgnoreCase("ekki") )
// "ok is til að hjálpa" , ok is "fyrir utan(aa_ae) gluggann"
 ||
	( nextToken.isPreposition() && !((IceTag)nextToken.getFirstTag()).isAdverb() && !nextToken.isInfinitive() && !nextToken.isNoun() ) 
// um 1903, 
 ||
	( currToken.lexeme.equals("um") && nextToken.isNumeral() )
// flest sex til sjö
 ||
	( prevToken != null && currToken.lexeme.equals("til") && prevToken.isOnlyWordClass(IceTag.WordClass.wcNumeral) && nextToken.isOnlyWordClass(IceTag.WordClass.wcNumeral) )                 
// Check pronouns/nouns/proper nouns:
 ||
	( (nextToken.isPronoun() || nextToken.isNoun() || nextToken.isProperNoun())
     && !tag.caseMatch(nextToken.getTags()) 
     && !((currToken.isAdverb() &&
           (nextToken.isProperNoun() || (nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && (prevToken==null || prevToken.isNoun() || prevToken.isVerb())))
          ) ||
          // "á hans aldri" / "eftir andartaks þögn", "í orðsins fyllstu merkingu", "fyrir neðan hana" is ok
          // also með tveggja tíma viðdvöl , NOTE: proper nouns might have missing tags
             (nextToken.isCase(IceTag.cGenitive) && nextnextToken != null &&
             (((nextnextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextnextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) ||
                nextnextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) 
             )
            && tag.caseMatch(nextnextToken.getTags())) || nextnextToken.isCase(IceTag.cGenitive))) 
         ) )
	)
)
	disAllowTag(currToken, tag);

}

public void checkInfinitive(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( !nextToken.isVerbInf() || !nextToken.isVerbAny() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkVerbInfinitive(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken != null  && 
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcReflPronoun) && prevToken.isCase(IceTag.cNominative) ) ||
	( prevToken.isVerbInfinitive() && !prevToken.isVerbSpecialAuxiliary() ) // láta smyrja is ok
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) ) ||
	( prevToken.isRelativeConjunction() && (nextToken==null || !nextToken.isVerbSpecialAuxiliary()) )   // konurnar tvær sem FÁ, but ok is "sem rekja má.."
 ||
	( prevToken.isVerbAuxiliary() )   // hefði smitast
// hin syrgða
 ||
	( prevToken.isArticle() )
// þessi kæfa
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) )
// choose the adjective : í sinn RÉTTA tíma
 ||
	( prevToken.isNominal() && prevprevToken != null && prevprevToken.isPreposition() 
	&& currToken.isAdjective() && nextToken != null && nextToken.isNoun() )
// margra landa
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdj) && prevToken.isOnlyCase(IceTag.cGenitive) && 
	currToken.isNoun() && currToken.isCase(IceTag.cGenitive) && currToken.genderNumberCaseMatch(prevToken))
	)
)
	disAllowTag(currToken, tag);

else if( prevprevToken != null  && 

// til fjarlægra landa
	(
	( prevprevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && prevToken.isOnlyWordClass(IceTag.WordClass.wcAdj) )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null && 

// kvíða sínum
	(
	( currToken.isNoun() & nextToken.isOnlyWordClass(IceTag.WordClass.wcPossPronoun))
	)
)
	disAllowTag(currToken, tag);

else if( prevToken == null && 
	(
	( currToken.isNoun() && nextToken != null && nextToken.isVerb() ) // A noun is more likely as the first word

	)
)
	disAllowTag(currToken, tag);

}

public void checkVerb(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( prevToken == null  && 
	(
	( currToken.lexeme.equalsIgnoreCase("er") && nextToken != null && nextToken.isVerbInfinitive() ) // Er hausta tekur
 ||
	( currToken.isNoun() && nextToken != null && nextToken.isVerb() ) // A noun is more likely as the first word

	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcPrep) && !prevToken.lexeme.equalsIgnoreCase("vegna")) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcArticle) ) ||
	( prevToken.isInfinitive() && currToken.isVerbInfinitive() )    // Infinitive verb are handled above
// á RÉTTI dagsins but make sure that "við höfðum ekkert".. works
 ||
	( prevToken.isPreposition() && !prevToken.isPersonalPronoun() && currToken.isNoun() )
 // "var orðið",
 ||
	( (prevToken.isVerbBe() && !prevToken.isVerbSupine() && !prevToken.isVerbPastPart()) && (tag.isVerbSupine() || tag.isVerbIndicativeForm() || tag.isVerbSubjunctiveForm()) )
// tók lokið af
 ||
	( tag.isVerbPastParticiple() 
	&& !prevToken.lexeme.equalsIgnoreCase("sem") && (prevToken.isVerb() || prevToken.isVerbInfinitive()) 
	&& !prevToken.isVerbBe() && (nextToken == null || !nextToken.isVerbBe()) ) ||
	( prevToken.isVerbSpecialAuxiliary() )
// skal (ekki) hita
 ||
	( currToken.isVerbInfinitive() 
	&& (prevToken.isVerbSpecialAuxiliary() || (prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && prevprevToken != null && prevprevToken.isVerbSpecialAuxiliary())) )
// hefði (mikið) MÓTAST, select the supine
 ||
	( (tag.isVerbIndicativeForm() || tag.isVerbSubjunctiveForm()) && currToken.isVerbSupine() 
	&& ( (prevToken != null && prevToken.isVerbAuxiliary()) || (prevprevToken != null && prevprevToken.isVerbAuxiliary())) )
// hin eina sanna
 ||
	( prevToken.isAdjective() && prevprevToken != null && (prevprevToken.isOnlyWordClass(IceTag.WordClass.wcArticle)) )
// select the noun "þróttmiklum söng"
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdj) && currToken.isNoun() && currToken.genderNumberCaseMatch(prevToken) )
// ört vaxandi umferð
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb) && currToken.isAdjective() && nextToken != null 
	&& nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.genderNumberCaseMatch(nextToken) )
// maðurinn sem
 ||
	( currToken.isRelativeConjunction() && currToken.lexeme.equalsIgnoreCase("sem") 
	&& (prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) )
// subjunctive  , "ef hún gætti sín ekki"
 ||
	( prevprevToken != null && (prevprevToken.lexeme.equalsIgnoreCase("ef") || prevprevToken.lexeme.equalsIgnoreCase("þótt")) 
	&& currToken.isVerbSubjunctive() && !tag.isVerbSubjunctiveForm() )
// mín gæfa , select the noun
 ||
	( prevToken.isPossessivePronoun() && currToken.isNoun() )
// á hennar pökkum, í þetta skipti
 ||
	( (prevToken.isPersonalPronoun() || prevToken.isPossessivePronoun() || prevToken.isDemonstrativePronoun()) 
	&& prevprevToken != null && prevprevToken.isPreposition() && currToken.isNoun() )
// ýmsir SKÆÐIR keppinautar, select the adjective
 ||
	( prevToken.isIndefinitePronoun() && currToken.isAdjective() 
	&& nextToken != null && nextToken.isNoun() && !tag.personGenderNumberMatch(prevToken.getTags()) ) ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) && currToken.isNoun() ) ||
	( currToken.lexeme.equalsIgnoreCase("sem") && !prevToken.isPersonalPronoun() )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 
	(
	( currToken.lexeme.equalsIgnoreCase("mun") && nextToken.isAdjectiveComparative() ) // mun fleiri
 ||
	( currToken.lexeme.equalsIgnoreCase("eigi") && nextToken.isOnlyVerbAny()  ) // eigi skal höggva
// sá sem, sá skal, karlinn sá, sá maður
 ||
	( currToken.lexeme.equalsIgnoreCase("sá") && (nextToken.isRelativeConjunction() || nextToken.isOnlyVerbAny() ||  
    nextToken.isEOS() || (nextToken.isNoun() && nextToken.isOnlyCase(IceTag.cNominative))) ) 
 // er gerðist
 ||
	( currToken.lexeme.equalsIgnoreCase("er") 
	&& nextToken.isOnlyVerbAny() && !nextToken.isVerbPastPart() 
	&& (nextToken.isVerbMiddleForm() || nextToken.isVerbIndicative() || nextToken.isVerbSubjunctive())  )
// "... á meðan "...
 ||
	( currToken.lexeme.equalsIgnoreCase("á") 
	&& (((nextToken.isConjunction() && !nextToken.isInfinitive()) || nextToken.isPunctuation()) ||
           // á þessum
           (nextToken.isNominal() && nextToken.isOnlyCase(IceTag.cDative)) ||
           (nextToken.lexeme.equals("milli") && (prevToken == null || (!prevToken.isProperNoun() && !prevToken.isPersonalPronoun()))) ||
           // "hingað á flugvöllinn", but ok is "sem á .."
           (prevToken != null && !prevToken.isCase(IceTag.cNominative) && !prevToken.isRelativeConjunction())
           ) )
// heldur luralegur
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) && currToken.isAdverb() && currToken.lexeme.equalsIgnoreCase("heldur") )
// gengi hlutabréfa
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && nextToken.isOnlyCase(IceTag.cGenitive) && currToken.isNoun() )
// select the adjective; "sagði skjálfandi röddu"
 ||
	( nextToken.isNoun() && tag.isVerbPresentPart() && currToken.isAdjective() )
// select the adjective; "nýbökuðum (unknown) stjórnarformanni"
 ||
	( nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && currToken.isAdjective() 
	&& currToken.genderNumberCaseMatch(nextToken) && nextToken.isOnlyCase(IceTag.cDative) )
 // engin LEIÐ var, select the noun, ok is "hverjir GETA verið ..."
 ||
	( nextToken.isVerbBe() && !nextToken.isVerbSupine() 
	&& prevToken != null && prevToken.isIndefinitePronoun() && currToken.isNoun() )
 // nefna má..
 ||
	( currToken.isVerbInfinitive() && nextToken.isVerbSpecialAuxiliary() ) ||
	( currToken.isNoun() & nextToken.isOnlyWordClass(IceTag.WordClass.wcPossPronoun))
	)
)
	disAllowTag(currToken, tag);

}

public void checkRelativeConjunction(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( currToken != null  && 
	(
	( prevToken == null ) ||
	( prevToken.lexeme.equalsIgnoreCase("þar") )   // þar sem
 ||
	( prevToken.isVerbMiddleForm() ) // virðist sem
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcAdverb)  ) // enn sem ..
 ||
	( (currToken.lexeme.equals("sem") 
	&& prevToken.isAdjectiveComparative() && prevprevToken != null && prevprevToken.lexeme.equals("því")) ) // því betri sem
// ok is "gæti verið sem var", "að finna sem hann"
 ||
	( prevToken.isOnlyVerbAny() 
	&& (nextToken == null || (!nextToken.isVerbIndicative() && !nextToken.isVerbSubjunctive())) ) ||
	( currToken.lexeme.equalsIgnoreCase("er") 
	&& ((prevToken != null && prevToken.isOnlyWordClass(IceTag.WordClass.wcConjRel)) ||
            (nextToken != null && nextToken.isOnlyWordClass(IceTag.WordClass.wcInf))) ) // fólk er að ...
// sem betur fer, sem slíkur
 ||
	( nextToken != null 
	&& (nextToken.isOnlyWordClass(IceTag.WordClass.wcReflPronoun) || nextToken.lexeme.equalsIgnoreCase("betur")) )
// enda sem grenjuskjóða.
 ||
	( nextToken != null && (nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextToken.isReflexivePronoun()) 
	&& nextnextToken != null && nextnextToken.isEOS() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkConjunction(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( currToken.lexeme.equalsIgnoreCase("sem")  && 

// svo sem
	(
	(  (prevToken != null && (prevToken.isNoun() || prevToken.lexeme.equalsIgnoreCase("svo"))) ||
	(nextToken != null && (nextToken.isVerbBe() || nextToken.isAdverbSuper() || nextToken.isAdjectiveSuper())) ) // sem fyrst, sem best

	)
)
	disAllowTag(currToken, tag);

else if( currToken.lexeme.equalsIgnoreCase("utan")  && 
	(
	( nextToken != null && nextToken.isPreposition() ) // utan í
 ||
	( prevToken != null && prevToken.lexeme.equalsIgnoreCase("fyrir") ) // fyrir utan

//ELSE
//IF currToken.lexeme.equalsIgnoreCase("nema") ;
//RULE prevToken != null && prevToken.isOnlyWordClass(PREP) ; // kominn með nema

	)
)
	disAllowTag(currToken, tag);

else if( currToken.lexeme.equalsIgnoreCase("að")  && 

// First there are some special cases where "að" is correctly a conjunction
// ég sagði (mér) að...  , það var nefnt að..
	(
	(  (prevToken == null || 
	( !prevToken.isVerbSpecialInf() &&  
	  prevToken.tokenCode != Token.TokenCode.tcComma &&  // , að
      	  !prevToken.lexeme.equals("svo") &&  // svo að ..
      // held að Kalli..., beindust að is ok
          (!prevToken.isVerb() || prevToken.isVerbMiddleForm() 
      		|| nextToken == null || (!nextToken.isNoun() && !nextToken.isProperNoun()) || !nextToken.isCase(IceTag.cNominative))
	)) &&      
      // Here reduction rules start
      (
	 nextToken != null && (
         // "Hver heldur þú að finni" is ok,  "að enda"
         ((nextToken.isOnlyWordClass(IceTag.WordClass.wcVerb) && !nextToken.isVerbSubjunctive()) ||
           nextToken.isVerbInfinitive() ||
           (nextToken.isAdverb() && nextnextToken != null && nextnextToken.isPunctuation()) ) || // að aftan .
           (nextToken.isVerbInfinitive() && nextToken.isOnlyVerbAny()) ||   // að lesa
           (nextToken.isNominal()  && !nextToken.lexeme.equals("á") 
           	&& !nextToken.lexeme.equals("við") && currToken.caseMatch(nextToken)) || // could be a prep "aþ"
           (nextToken.isCase(IceTag.cGenitive) && nextnextToken != null &&
                ((nextnextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextnextToken.isOnlyWordClass(IceTag.WordClass.wcAdj) ||
                  nextnextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun))
             && currToken.caseMatch(nextnextToken)))
	 )
      ) )
	)
)
	disAllowTag(currToken, tag);

else if( nextToken != null  && 

// var þegar dáinn
	(
	( currToken.lexeme.equalsIgnoreCase("þegar") && (nextToken.isVerbPastPart()) && prevToken!=null && prevToken.isVerbBe() )  
// heldur en (heldur er hér aam)
 ||
	( currToken.lexeme.equalsIgnoreCase("heldur") && (nextToken.lexeme.equalsIgnoreCase("en") || nextToken.lexeme.equalsIgnoreCase("áfram")) ) ||
	( nextToken.isPunctuation() )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 
	(
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcConjRel) ) // sem og , sem er
 ||
	( prevToken.isOnlyWordClass(IceTag.WordClass.wcInf) )
// var heldur ekki, og heldur fast
 ||
	( (prevToken.isVerbBe() || prevToken.isConjunction()) && currToken.lexeme.equalsIgnoreCase("heldur") )
	)
)
	disAllowTag(currToken, tag);

}

public void checkNumeral(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( !nextToken.isNoun() && !nextToken.isAdjective() && !nextToken.isPunctuation() 
	&& !nextToken.isConjunction()        // tvö og þrú saman
        && !nextToken.isPreposition()        // sátu þrjú í ...
        && !nextToken.isAdverb() && !nextToken.isVerb() ) ||
	( (currToken.lexeme.equalsIgnoreCase("einn") || currToken.lexeme.equalsIgnoreCase("ein")) 
	&& currToken.isAdjective() && !nextToken.isNominal() )
	)
)
	disAllowTag(currToken, tag);

else if( prevToken != null  && 

// "hin eina sanna"
	(
	( prevToken.isArticle() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkExclamation(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( nextToken.isAdverb() || nextToken.isAdjective() )
	)
)
	disAllowTag(currToken, tag);

}

public void checkArticle(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
{
if( nextToken != null  && 
	(
	( !nextToken.isAdjective() && !nextToken.isReflexivePronoun() )  // hið sama

	)
)
	disAllowTag(currToken, tag);

}

} // end class
