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
package is.iclt.icenlp.core.icetagger;

import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.utils.IceLog;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.IceTag;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * A disambiguator for Icelandic text.
 * <br> Used by the IceTagger class
 * @author Hrafn Loftsson
 */
public class IceDisambiguator {
    private IceLog logger=null;    // Logfile file
    private ArrayList tokens;
    private IceLocalRules LocalRules;
    private IceHeuristics HeuristicsAnalyzer;
    private boolean fullDisambiguation=true;

  public IceDisambiguator(IceLog log, Lexicon verbPrepDict, Lexicon verbObjDict, boolean fullDisam)
  {
      fullDisambiguation = fullDisam;
      logger = log;
      LocalRules = new IceLocalRules(log);
      HeuristicsAnalyzer = new IceHeuristics(log, verbPrepDict, verbObjDict);
  }

  public void setFullDisambiguation(boolean flag)
  {
      fullDisambiguation = flag;
  }

  public void setTokens(ArrayList sentence)
  {
      tokens = sentence;
      HeuristicsAnalyzer.setTokens(sentence);
  }

    /**
     * *  Disallows tags in a given context.
     * @param prevprevToken The token previous to the prevToken
     * @param prevToken     The previous token
     * @param currToken     The current token
     * @param nextToken     The next token
     * @param nextnextToken The token next to the nextToken
     */
    public void removeTags(IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)
    {
        // Get all the tags for the current token
        ArrayList tags = currToken.getTags();
        ListIterator iterator = tags.listIterator();

        // The next two lines are used for processing tags in reverse order
       while (iterator.hasNext()) {
              IceTag tag = (IceTag)iterator.next(); }
        
        //while (iterator.hasNext())
        while (iterator.hasPrevious())
        {
            //IceTag tag = (IceTag)iterator.next();
            IceTag tag = (IceTag)iterator.previous();

            if (tag.isNoun())
                LocalRules.checkNoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isProperNoun())
                LocalRules.checkProperNoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isAdjective())
                LocalRules.checkAdj(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isAdverb())
                LocalRules.checkAdverb(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isPersonalPronoun())
                LocalRules.checkPersonalPronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isIndefinitePronoun())
                LocalRules.checkIndefinitePronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isDemonstrativePronoun())
                LocalRules.checkDemonstrativePronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isReflexivePronoun())
                LocalRules.checkReflexivePronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isInterrogativePronoun())
                LocalRules.checkInterrogativePronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isPossessivePronoun())
                LocalRules.checkPossessivePronoun(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isPreposition())
                LocalRules.checkPreposition(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);
            else if (tag.isInfinitive())
                LocalRules.checkInfinitive(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isVerbInfinitive())
                LocalRules.checkVerbInfinitive(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isVerb() || tag.isVerbPastParticiple())
                LocalRules.checkVerb(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isRelativeConjunction())
                LocalRules.checkRelativeConjunction(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isConjunction())
               LocalRules.checkConjunction(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isNumeral())
               LocalRules.checkNumeral(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isExclamation())
               LocalRules.checkExclamation(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            else if (tag.isArticle())
               LocalRules.checkArticle(tag, prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            if (!tag.isValid())
                iterator.remove();
        }
    }

    /**
     *   Runs through the ambiguous tags and tries to disambiguate them.
     */
    private void disAmbiguateTokens()
    {
       int maxIndex = tokens.size()-1;
       IceTokenTags prevprevToken=null, prevToken=null, currToken,
               nextToken, nextnextToken;

       for (int i=0; i<=maxIndex; i++)
       {
            currToken = (IceTokenTags)tokens.get(i);
            if (i>1)
                prevprevToken = (IceTokenTags)tokens.get(i-2);
            if (i<maxIndex)
                nextToken = (IceTokenTags)tokens.get(i+1);
            else
                nextToken = null;
            if (i<maxIndex-1)
                nextnextToken = (IceTokenTags)tokens.get(i+2);
            else
                nextnextToken = null;

            if (currToken.numTags()  > 1)
               removeTags(prevprevToken, prevToken, currToken, nextToken, nextnextToken);

            prevToken = currToken;
       }
    }

    /**
     * Performs disambiguation based on local rules.
     */
    public void disAmbiguateLocal()
    {
        // Have to make several passes because of phrases like "til mín " where til can be ae_aa and mín can both be
        // personal pronoun and possesive pronoun.  In the first phase possessive pronoun is disallowed and in the second phase
        // adverb is disallowed
        boolean didDisambiguate = true;
        while (didDisambiguate) {
           LocalRules.setDisambiguateFlag(false);
           disAmbiguateTokens();         // Sets didDisambiguate to true if it disambiguates anything
           didDisambiguate = LocalRules.getDisambiguateFlag();
        }
    }

/**
 *   Find a token that splits the sentence.
 *   <br>Returns the index of the token if found, else -1.
 *   @param fromIndex Starting index
 *   @return The index of the token splitting the sentence
*/
    private int findSentenceSplit(int fromIndex)
    {
        IceTokenTags prevToken, currToken, nextToken, nextnextToken=null;
        int index = -1;
        int i = fromIndex+1;    // Ignore first token
        boolean found = false;
        int last = tokens.size()-1;
        while (i<last-2 && !found)
        {
            prevToken = (IceTokenTags)tokens.get(i-1);
            currToken = (IceTokenTags)tokens.get(i);
            nextToken = (IceTokenTags)tokens.get(i+1);
            if (i<last-3)
              nextnextToken = (IceTokenTags)tokens.get(i+2);

            if (currToken.tokenCode == Token.TokenCode.tcSemicolon ||
                    (currToken.tokenCode == Token.TokenCode.tcComma && // make an enumeration stay together
                        !(prevToken.isAdjective() && nextToken.isAdjective()) && !(prevToken.isNoun() && nextToken.isNoun())) ||
                    currToken.tokenCode == Token.TokenCode.tcHyphen
                )
            {
                index = i;
                found = true;
            }
            else
            {
              if (
                      (currToken.isOnlyWordClass(IceTag.WordClass.wcConj) || currToken.isOnlyWordClass(IceTag.WordClass.wcConjRel)) &&
                      (nextToken.isOnlyVerbAny() || nextToken.isVerbBe() || nextToken.isOnlyWordClass(IceTag.WordClass.wcPersPronoun) ||
                      ((nextToken.isOnlyWordClass(IceTag.WordClass.wcNoun) || nextToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun)) &&
                        prevToken!=null && !prevToken.isOnlyWordClass(IceTag.WordClass.wcNoun) &&
                                           !prevToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun) && (nextnextToken == null || !nextnextToken.isPunctuation()))  // upptalning
                      )
                  )
              {
                  index = i;
                  found = true;
              }
            }
            //prevToken = currToken;
            i++;
        }
        return index;
    }

 /**
 *  Checks if an adjective tag should be chosen over a past participle tag.
 */
 /*
 private void adjectiveOrPastParticiple(int from, int to)
 {
    IceTokenTags verb, tok;
    boolean found;

    for (int i=to; i>=from; i--)
    {
       found = false;
       verb = (IceTokenTags)tokens.get(i);
       if (verb.isAdjective() && verb.isVerbPastPart())
       {
          for (int j=i-1; j>=0 && i-j<6 && !found; j--)     // Search in the neighborhood
          {
             tok = (IceTokenTags)tokens.get(j);
             if (tok.isVerbBe())              // Found a verb be
                 found = true;
             else if (tok.isVerbActive() || tok.isVerbSubjunctive())
                 break;
          }
          if (!found)        // Remove past participle tags
             verb.removeWordClass(IceTag.WordClass.wcVerbPastPart);
       }
     }
  }*/

    /**
     * Checks for the existence of an infinitive verb form.
     * @param from  fromIndex
     * @param to    toIndex
     * @return      true if an infinitive verb form was found
     */
   private boolean checkVerbInfinitiveOrActive(int from, int to)
   /*
     Works with sentences like: .. laga kaffi og/eða ELDA mat.  .. mig langaði að setja plötu á fóninn, OPNA bók
     Here elda can be both an infinitive verb form and active but the preceeding verb is the infinitive and
     most likely the current verb is also infinitive
     Additionally, "vildu (special aux) ýmsir grípa (sng)...
  */
   {
     boolean verbFound = false;
     //boolean infSelected = false;
     IceTokenTags currToken;
     int indexFound;

     for (int i=from+1; i<=to; i++)
     {
         verbFound = false;
         //infSelected = false;
         currToken = (IceTokenTags)tokens.get(i);
         if (currToken.isVerbInfinitive() && currToken.isVerbIndicative())
         {
            verbFound = true;
            indexFound = i;

            IceTokenTags prevToken = (IceTokenTags)tokens.get(indexFound-1);
            if (verbFound)
            {
                for (int j=indexFound-2; j>=0 && j>=indexFound-4; j--)  // Just check 5 words back
                {
                    IceTokenTags prevVerb = (IceTokenTags)tokens.get(j);
                    if ((prevVerb.isOnlyWordClass(IceTag.WordClass.wcVerbInf) && prevToken.isConjunction()) || prevVerb.isVerbSpecialAuxiliary())
                    {
                        currToken.removeAllBut(IceTag.WordClass.wcVerbInf);
                        //infSelected = true;
	                    if( logger != null )
                            logger.log("VerbInfinitiveOrActive: Verb Infinitive form deduced " + currToken.lexeme);
                        break;
                    }
                }

            }
         }
     }

     return (verbFound);
   }

/**
 *  Check if a past participle tag is legal.
 *  <br>Not legal if an auxiliary (hafa, gera) is found in the neighborhood.
 *  <br>In that case supine should be the tag.
*/
    private void checkPastParticiple()
      {
         IceTokenTags verb, tok;
         boolean auxFound, beFound;

         int last = tokens.size()-1;

         for (int i=last; i>=0; i--)
         {
           beFound = false;
           auxFound = false;
           verb = (IceTokenTags)tokens.get(i);
           if (!(verb.isSVOMainSubject() || verb.isSVOSubject()) &&
                   verb.isVerbPastPart() && verb.isOtherThanWordClass(IceTag.WordClass.wcVerbPastPart))       // A past participle token?
           {
              for (int j=i-1; j>=0 && i-j<=10 && !auxFound && !beFound; j--)     // Search in the neighborhood
              {
                 tok = (IceTokenTags)tokens.get(j);
                 if (tok.isVerbAuxiliary())              // Found an auxiliary
                     auxFound = true;
                 else if (tok.isVerbBe())
                     beFound = true;
                 else if (tok.isVerbPastPart())
                     beFound = true;
                 if (auxFound || beFound)
                     break;
              }
              if (i<last)   // horft er um öxl (the verb after the past participle)
              {
                  tok = (IceTokenTags)tokens.get(i+1);
                  if (tok.isVerbBe())
                     beFound = true;
              }
              if (auxFound || !beFound)        // Remove past participle tags
              {
                  if (!beFound)
                  {
	                  if( logger != null )
	                    logger.log("Past participle: removed past: " +  verb.lexeme + " " + verb.allTagStrings());
                  }
	              verb.removeWordClass(IceTag.WordClass.wcVerbPastPart);
              }
          }
        }
      }

/**
 * Checks the first token of small sentences.
 * <br>For small sentences there is very probable that the first token is in nominative given that its a noun
 * @param from fromIndex
 * @param to   toIndex
*/
private void checkFirstToken(int from, int to)
  {
     IceTokenTags tok;
     //for (int i=0; i<=to && i<=2; i++)
     for (int i=from; i<=to && i<=from+2; i++)
     {
       tok = (IceTokenTags)tokens.get(i);
       if ((tok.isOnlyWordClass(IceTag.WordClass.wcNoun) || tok.isOnlyWordClass(IceTag.WordClass.wcProperNoun) || tok.isOnlyWordClass(IceTag.WordClass.wcAdj)) &&
               tok.isCase(IceTag.cNominative) &&
               !((IceTag)tok.getFirstTag()).isCase(IceTag.cNominative))
       {
	       if( logger != null )
            logger.log("SVO: First token: " + tok.lexeme + " " + tok.allTagStrings());
           tok.removeAllButCase(IceTag.cNominative);
       }
     }
}

    /**
     * Chooses the probable tag for nominals.
     * @param from fromIndex
     * @param to   toIndex
     */
private void chooseBetweenNominalTags(int from, int to)
{
   IceTokenTags tok, tok2, prevTok;
   boolean nomAndAccDat, accAndDat;
   boolean verbFound;
   boolean found;

   for (int i=to; i>=from; i--)
   {
      verbFound = false; nomAndAccDat = false; accAndDat = false; found = false;

      tok = (IceTokenTags)tokens.get(i);
      if ((tok.isCase(IceTag.cNominative) && (tok.isCase(IceTag.cAccusative) || tok.isCase(IceTag.cDative)) && // only nominative and accusative
                !tok.isCase(IceTag.cGenitive)))
             nomAndAccDat = true;
         else if (!tok.isCase(IceTag.cNominative) && tok.isCase(IceTag.cAccusative) && tok.isCase(IceTag.cDative))    // accusative but not nominative
             accAndDat = true;

      // Choose between nominative and accusative cases
      if (tok.getSVOMark() == IceTokenTags.SVOMark.svoNone &&
              (tok.isOnlyWordClass(IceTag.WordClass.wcNoun) || (tok.isNoun() && tok.isAdjective()) || tok.isOnlyWordClass(IceTag.WordClass.wcIndefPronoun)))
      {
        if (nomAndAccDat || accAndDat)
        {
            for (int j=i-1; j>=from && i-j<=8; j--)
            {
                prevTok = (IceTokenTags)tokens.get(j);

                if ((prevTok.isConjunction() && prevTok.lexeme.equalsIgnoreCase("en"))) // frekar en hestur (nf)
                {
	                if( logger != null )
                        logger.log("ChooseBetweenNounTags: kept nominative case " + tok.lexeme + " " + tok.allTagStrings());
                    tok.removeAllButCase(IceTag.cNominative);
                    break;
                }
                else if (j==i-2 && prevTok.isOnlyWordClass(IceTag.WordClass.wcDemPronoun) && ((IceTag)prevTok.getFirstTag()).isCase(IceTag.cNominative))
                {
                    IceTokenTags genitiveToken = (IceTokenTags)tokens.get(j+1);
                    if (genitiveToken.isOnlyWordClass(IceTag.WordClass.wcNoun) && genitiveToken.isOnlyCase(IceTag.cGenitive))     // Þetta fjárans AUGA
                    {
	                    if( logger != null )
                            logger.log("ChooseBetweenNounTags, : kept nominative case " + tok.lexeme + " " + tok.allTagStrings());
                        tok.removeAllButCase(IceTag.cNominative);
                        break;
                    }
                }
                else if ((prevTok.isSVOMainVerb() || prevTok.isSVOVerb()))
                {
                    verbFound = true;
                    if (nomAndAccDat)
                    {
                        if (!prevTok.isVerbBe())
                        {
	                        if( logger != null )
                                logger.log("ChooseBetweenNounTags: Removed nominative case from " + tok.lexeme + " " + tok.allTagStrings());
                            tok.removeCase(IceTag.cNominative, false);
                            break;
                        }
                        else
                        {
	                        if( logger != null )
                                logger.log("ChooseBetweenNounTags: Removed accusative case from " + tok.lexeme + " " + tok.allTagStrings());
                            //tok.removeCase(is.iclt.icenlp.core.utils.IceTag.cAccusative, false);
                            tok.removeAllButCase(IceTag.cNominative);
                            break;
                        }
                    }
                    else // not nominative case, then assume accusative case
                    {
	                    if( logger != null )
                            logger.log("ChooseBetweenNounTags: kept accusative case " + tok.lexeme + " " + tok.allTagStrings());
                        tok.removeAllButCase(IceTag.cAccusative);
                        break;
                    }
                }
                else if (prevTok.isSVOPrepPhrase())
                    break;
                else if (prevTok.isPunctuation() || prevTok.isConjunction())
                    break;
            }
        }
        // If no preceeding verb was found then use first tag

        if (!verbFound && tok.numTags()>1) {

          if (((IceTag)tok.getFirstTag()).isCase(IceTag.cNominative))
          {
	          if( logger != null )
                logger.log("ChooseBetweenNounTags: Removed all but nominative " + tok.lexeme + " " + tok.allTagStrings());
            tok.removeAllButCase(IceTag.cNominative);
            found = true;
          }
          else {  // If a nominative noun is found in the neighborhood then assume tok is nominative
              //for (int j=i-2; j>=from && i-j<=5; j--)   // STARTING TWO WORDS TO THE LEFT
              int j = i-2;
              if (j>=from)
              {
                  tok2 = (IceTokenTags)tokens.get(j);
                  if (tok2.isNoun() && ((IceTag)tok2.getFirstTag()).isCase(IceTag.cNominative))
                  {
	                  if( logger != null )
                        logger.log("ChooseBetweenNounTags2: Removed all but nominative " + tok.lexeme + " " + tok.allTagStrings());
                      tok.removeAllButCase(IceTag.cNominative);
                      found = true;
                      //break;
                  }
              }
          }
          if (!found)
          {
              if (tok.isOnlyNumber(IceTag.cPlural) && (tok.isOnlyCase(IceTag.cDative)))
              {
	              if( logger != null )
                    logger.log("ChooseBetweenNounTags3: Removed all but first tag " + tok.lexeme + " " + tok.allTagStrings());
                tok.removeAllButFirstTag();
                found = true;
              }
          }
        }
      }
      else if (tok.getSVOMark() == IceTokenTags.SVOMark.svoPrepPhrase &&
              (tok.isOnlyWordClass(IceTag.WordClass.wcNoun) || tok.isOnlyWordClass(IceTag.WordClass.wcIndefPronoun)))
      {
          if (tok.isOnlyNumber(IceTag.cPlural) && (tok.isOnlyCase(IceTag.cDative)))
              {
	              if( logger != null )
                    logger.log("ChooseBetweenNounTags prepPhrase: Removed all but first tag " + tok.lexeme + " " + tok.allTagStrings());
                tok.removeAllButFirstTag();
                found = true;
              }
      }
      // Check subject
      else if ((tok.getSVOMark() == IceTokenTags.SVOMark.svoMainSubject || (tok.getSVOMark() == IceTokenTags.SVOMark.svoSubject)) &&
              tok.isOnlyWordClass(IceTag.WordClass.wcNoun))
      {
        if (i>from && nomAndAccDat)
        {
            tok2 = (IceTokenTags)tokens.get(i-1);
            if (tok2.lexeme.equalsIgnoreCase("og")) // eins og nafn
            {
	            if( logger != null )
                    logger.log("ChooseBetweenNounTags in Subject: kept nominative case " + tok.lexeme + " " + tok.allTagStrings());
                tok.removeAllButCase(IceTag.cNominative);
                break;
            }
        }
      }
   }
}

    /**
     * Chooses between adjective tags.
     * @param from fromIndex
     * @param to   toIndex
     */
private void chooseBetweenAdjectiveTags(int from, int to)
{
   IceTokenTags tok, nextTok=null, prevTok;
   boolean nomAndAccDat;

   for (int i=to; i>=from; i--)
   {
      nomAndAccDat = false;
      //accAndDat = false;

      tok = (IceTokenTags)tokens.get(i);
      if (i<to)
        nextTok = (IceTokenTags)tokens.get(i+1);
      // Choose between nominative and accusative cases
      if (tok.getSVOMark() == IceTokenTags.SVOMark.svoNone && tok.isOnlyWordClass(IceTag.WordClass.wcAdj) && (nextTok==null || !nextTok.isNoun()))
      {
         if (tok.numGenders() > 1)  // Then search for a subject to match against
         {
             for (int j=i-1; j>=from && i-j<=6; j--)
             {
                prevTok = (IceTokenTags)tokens.get(j);
                if (prevTok.isSVOMainSubject() || prevTok.isSVOSubject())
                {
                    tok.setSVOMark(IceTokenTags.SVOMark.svoObject);
                    HeuristicsAnalyzer.checkSubjectObjectAgreement(j, -1, i);
	                if( logger != null )
                        logger.log("ChooseBetweenAdjTags: Subject Object " + prevTok.lexeme + " " + tok.lexeme + " " + tok.allTagStrings());
                    break;
                }
             }
         }
         else
         if ((tok.isCase(IceTag.cNominative) && tok.isCase(IceTag.cAccusative)) && // only nominative and accusative
                !tok.isCase(IceTag.cDative) && !tok.isCase(IceTag.cGenitive))
         {
             nomAndAccDat = true;

            if (nomAndAccDat) //|| accAndDat)
            {
	            if( logger != null )
                    logger.log("ChooseBetweenAdjTags: Removed accusative case from " + tok.lexeme + " " + tok.allTagStrings());
                tok.removeAllButCase(IceTag.cNominative);
            }
        }
      }
   }
}

    /**
     * Chooses between pronoun tags.
     * @param from fromIndex
     * @param to   toIndex
     */
private void chooseBetweenPronounTags(int from, int to)
{
  /*
   * Tries to find a match between the anaphoric words "þeirra", "sér" and their antecedent.
  */
   IceTokenTags tok, prevTok;
   boolean match = false;
   boolean candidateFound = false;

   for (int i=to; i>=from && !candidateFound && !match; i--)
   {
      match = false;

      tok = (IceTokenTags)tokens.get(i);
      if ( (tok.lexeme.equals("þeirra") && tok.isPersonalPronoun() && (tok.numGenders() >= 2)) ||
           (tok.lexeme.equals("sér") && tok.isReflexivePronoun() && (tok.numGenders() >= 2)))
      {
         candidateFound = true;
         for (int j=i-2; j>=from; j--)
         {
            prevTok = (IceTokenTags)tokens.get(j);
            if (prevTok.isNoun())
            {
               match = HeuristicsAnalyzer.match(IceTokenTags.Match.personGenderNumber, tok, prevTok);
               if (match)
               {
	               if( logger != null )
	                logger.log("ChooseBetweenPronoun tags: Matched with " + prevTok.lexeme + " " + prevTok.allTagStrings());
               }
            }
         }
         if (candidateFound && !match) {   // Then there might be a match with a subject in previous sentences
             prevTok = HeuristicsAnalyzer.getLastPluralSubject();
             if (prevTok != null)
             {
                match = HeuristicsAnalyzer.match(IceTokenTags.Match.personGenderNumber, tok, prevTok);
                if (match)
                {
	                if( logger != null )
	                    logger.log("ChooseBetweenPronoun tags: Matched with a previous sentence " + prevTok.lexeme + " " + prevTok.allTagStrings());
                }
             }
         }
      }
   }

}


/**
 *  Checks if a supine tag is legal.
 *  <br>Not legal if an verb be (vera, verða) is found in the neighborhood.
 *  <br>In that case past participle should be the tag.
*/
    private void checkSupine()
      {
         IceTokenTags verb, tok;
         boolean found;

         int last = tokens.size()-1;

         for (int i=last; i>=0; i--)
         {
           found = false;
           verb = (IceTokenTags)tokens.get(i);
           if (verb.isVerbSupine())       // A supine verb?
           {
              if (i<last)
              {
                tok = (IceTokenTags)tokens.get(i+1);
                if (tok.isVerbBe())               // Found an BE verb
                     found = true;
              }

              if (!found)
              {
                for (int j=i-1; j>=0 && i-j<=10 && !found; j--)     // Search in the neighborhood
                {
                   tok = (IceTokenTags)tokens.get(j);
                   if (tok.isVerbAny()) {
                       if (tok.isVerbBe() || tok.isVerbPastPart()) // && !tok.isVerbSupine())              // Found an BE verb, but ok is "verið slegið"
                       {
                         found = true;
                       }
                       // Þá var (verbBe) mér satt að segja (verbInf) brugðið (ssg)
                       if (!tok.isOnlyWordClass(IceTag.WordClass.wcVerbInf) && !((IceTag)tok.getFirstTag()).isVerbPastParticiple())
                         break;
                    }
                }
              }

              if (found)        // Remove supine tags
                  verb.removeVerbForm(IceTokenTags.Condition.condVerbSupine);
          }
        }
      }


    /**
     * Disambiguates using global (heuristic) rules.
     */
   public void disAmbiguateGlobal()
   {
       int lastIndex = 0;
       int fromIndex = 0;

      checkVerbInfinitiveOrActive(0, tokens.size()-1);
      //adjectiveOrPastParticiple(0, tokens.size()-1);
      checkPastParticiple();
      checkSupine();

      boolean conjFound = true;

      while (conjFound)
      {
        int conjIndex = findSentenceSplit(fromIndex);
        if (conjIndex >= 0)
        {
            lastIndex = conjIndex;
            conjFound = true;
        }
        else
        {
            lastIndex = tokens.size()-1;
            conjFound = false;
        }

        HeuristicsAnalyzer.checkSVO(fromIndex, lastIndex);     // analyzer Subject, verb, object

        fromIndex = lastIndex;
    }

     HeuristicsAnalyzer.checkEnumeration();
     HeuristicsAnalyzer.checkReflexives();
     //HeuristicsAnalyzer.checkSubjunctive();


     checkFirstToken(0, tokens.size()-1);
     chooseBetweenNominalTags(0, tokens.size()-1);
     chooseBetweenAdjectiveTags(0, tokens.size()-1);
     chooseBetweenPronounTags(0, tokens.size()-1);

     // Do again because the needed conditions might now fire
     checkPastParticiple();
     checkSupine();

     // If full disambiguation is required then remove all but first tag from nouns and then do nominal agreement
     if (fullDisambiguation)
     {
         for (int i=0; i<=tokens.size()-1; i++)
         {
             IceTokenTags tok = (IceTokenTags)tokens.get(i);
             if (tok.isOnlyWordClass(IceTag.WordClass.wcNoun))
                 tok.removeAllButFirstTag();
         }
     }

     HeuristicsAnalyzer.checkNominalAgreement(0, tokens.size()-1);
     disAmbiguateLocal();               // Once to make sure after the global change
   }
}
