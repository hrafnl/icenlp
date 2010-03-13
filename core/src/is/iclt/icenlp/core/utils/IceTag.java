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
package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.utils.Tag;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * Encapsulates a PoS tag in the Icelandic tagset.
 * @author Hrafn Loftsson
 */
public class IceTag extends Tag {
   public enum WordClass
    {wcNoun, wcProperNoun, wcAdj, wcPronoun, wcPersPronoun, wcReflPronoun, wcPossPronoun, wcIndefPronoun,
     wcDemPronoun, wcIntPronoun, wcRelativePronoun, wcArticle, wcNumeral, wcVerb, wcVerbInf,
     wcVerbPastPart, wcConj, wcConjRel, wcAdverb, wcPrep, wcInf, wcExcl, wcPunct, wcForeign, wcUndef

   }
   private WordClass wordClass;

   // Constants
   public static final char cNominative = 'n';
   public static final char cAccusative = 'o';
   public static final char cDative = 'þ';
   public static final char cGenitive = 'e';

   public static final char cWildCard = '*';
   public static final char cNoMatch = '@';
   public static final char cArticle = 'g';
   public static final char cArticlePreceding = '-';

   public static final char cMasculine = 'k';
   public static final char cFeminine = 'v';
   public static final char cNeuter = 'h';
   public static final char cGenderUnspec = 'x';       // Unspecified gender

   public static final char cFirstPerson = '1';
   public static final char cSecondPerson = '2';
   public static final char cThirdPerson = '3';

   public static final char cSingular = 'e';
   public static final char cPlural = 'f';
   public static final char cNumberUnspec = 'x';       // Unspecified number

   public static final char cNoDeclension = 'o';
   public static final char cWeak = 'v';
   public static final char cStrong = 's';
   public static final char cIndeclineable = 'o';

   public static final char cPositive = 'f';
   public static final char cComparative = 'm';
   public static final char cSuperlative = 'e';

   public static final char cPresent = 'n';
   public static final char cPast = 'þ';

   public static final char cPersonName = 'm';
   public static final char cPlaceName='ö';
   public static final char cOtherName='s';

   public static final char cActive = 'g';
   public static final char cMiddle = 'm';

   public static final char cInfinitive = 'g';
   public static final char cImperative = 'b';
   public static final char cIndicative = 'f';
   public static final char cSubjunctive = 'v';
   public static final char cSupine = 's';
   public static final char cPresParticiple = 'l';
   public static final char cPastParticiple = 'þ';

   public static final char cPersPronoun = 'p';
   public static final char cDemPronoun = 'a';
   public static final char cReflPronoun = 'b';
   public static final char cPossPronoun = 'e';
   public static final char cIndefPronoun = 'o';
   public static final char cInterPronoun = 's';
   public static final char cRelPronoun = 't';

   public static final char cConjunction = 'c';
   public static final char cConjunctionRel = 't';

   public static final char cAdverb = 'a';
   public static final char cExclamation = 'u';
   public static final char cAbbreviation = 's';
    
   public static final char cUndef=' ';

    // Some tags to use
    public static final String tagMostFrequentNoun = "nken";
    public static final String tagMostFrequentProperNoun = "nken-m";
    public static final String tagProperNoun = "nken-m";
    public static final String tagProperNounPlace = "n***-ö";
    public static final String tagProperNounOther = "n***-s";
    public static final String tagCardinalsSingular = "tfken_tfkeo_tfkeþ_tfkee_tfven_tfveo_tfveþ_tfvee_tfhen_tfheo_tfheþ_tfhee";
    public static final String tagCardinalsPlural = "tfkfn_tfkfo_tfkfþ_tfkfe_tfvfn_tfvfo_tfvfþ_tfvfe_tfhfn_tfhfo_tfhfþ_tfhfe";
    public static final String tagAdjectivesSingular = "lkenvf_lkeovf_lkeþvf_lkeevf_lvenvf_lveovf_lveþvf_lveevf_lhenvf_lheovf_lheþvf_lheevf";
    public static final String tagAdjectivesPlural = "lkfnvf_lkfovf_lkfþvf_lkfevf_lvfnvf_lvfovf_lvfþvf_lvfevf_lhfnvf_lhfovf_lhfþvf_lhfevf";
    public static final String tagAdjectivesIndeclineable = "lkenof_lvenof_lhenof_lkeoof_lveoof_lheoof_lkeþof_lveþof_lheþof_lkeeof_lveeof_lheeof_lkfnof_lvfnof_lhfnof_lkfoof_lvfoof_lhfoof_lkfþof_lvfþof_lhfþof_lkfeof_lvfeof_lhfeof";
    public static final String tagAdjectiveComparative = "lkenvm_lkeovm_lkeþvm_lkeevm_lvenvm_lveovm_lveþvm_lveevm_lkfnvm_lkfovm_lkfþvm_lkfevm_lvfnvm_lvfovm_lvfþvm_lvfevm_lhfnvm_lhfovm_lhfþvm_lhfevm";
    public static final String tagAdjectiveNeuterPluralStrongNominative = "lhfnsf";
    public static final String tagAdjectiveNeuterPluralStrongAccusative = "lhfosf";
    public static final String tagAdjectiveFeminineSingularStrongNominative = "lvensf";
    public static final String tagOrdinal = "ta";
    public static final String tagOrdinal2 = "to";
    public static final String tagPercentage = "tp";
    public static final String tagAdverb = "aa";
    public static final String tagVerb = "sfg**n";
    public static final String tagVerbFirstSingular = "sfg1en";
    public static final String tagVerbFirstSingularSubjunctive = "svg1en";
    public static final String tagVerbFirstSingularPast = "sfg1eþ";
    public static final String tagVerbSecondSingular = "sfg2en";
    public static final String tagVerbThirdSingular = "sfg3en";
    public static final String tagVerbThirdSingularSubjunctive = "svg3en";
    public static final String tagVerbThirdPlural = "sfg3fn";
    public static final String tagVerbThirdPluralSubjunctive = "svg3fn";
    public static final String tagVerbThirdPluralMiddle = "sfm3fn";
    public static final String tagVerbThirdSingularPast = "sfg3eþ";
    public static final String tagVerbSubjunctive = "svg**n";
    public static final String tagVerbImperative = "sbg**n";
    public static final String tagVerbMiddle = "sfm**n";
    public static final String tagVerbInfActive = "sng";
    public static final String tagVerbInfMiddle = "snm";
    public static final String tagVerbPastPart = "sþghen";
    public static final String tagVerbPresentPart = "slg";
    public static final String tagVerbSupine = "ssg";
    public static final String tagVerbSupineMiddle = "ssm";
    public static final String tagNoun = "n***";
    public static final String tagAdj =  "l***sf";
    public static final String tagNounMasculineWeak =  "nkeo_nkeþ_nkee_nkfo_nkfe";
    public static final String tagNounFeminineFirstThree =  "nven_nveo_nveþ";
    public static final String tagNounNeuter =  "nhen_nheo_nheþ_nhfn_nhfo";
    public static final String tagNounNeuterSingular =  "nhen_nheo";
    public static final String tagNounNeuterPlural =  "nhfn_nhfo";
    public static final String tagNounNeuterSingularArticle =  "nheng_nheog";
    public static final String tagNounNeuterPluralArticle =  "nhfng_nhfog";
    public static final String tagNounMasculinePluralNominative =  "nkfn";
    public static final String tagNounMasculinePluralNominativeArticle =  "nkfng";
    public static final String tagForeign =  "e";

    private static final String AnnotationSeparator = "; ";

   public IceTag()
   {
       super();
       wordClass = WordClass.wcUndef;

   }

   public IceTag(String str)
   {
       super(str);
   }

   public WordClass getWordClassCode()
   {
       return wordClass;
   }

   public String getWordClass()
   {
       return wordClass.toString();
   }


   public void addArticle()
   {
       if (!hasArticle())
       {
          if (isNoun())
            tagStr.append(cArticle);
          else if (isProperNoun())
            tagStr.setCharAt(4, cArticle);
       }
   }

   public void removeArticle()
   {
       if (hasArticle())
       {
          if (isNoun())
            tagStr.deleteCharAt(4);
          else if (isProperNoun())
            tagStr.setCharAt(4, cArticlePreceding);
       }
   }

   // Returns true if the tag has a gender feature
   public boolean hasGender()
   {
        char gen = getPersonGenderLetter();
        return (gen == cMasculine || gen == cFeminine || gen == cNeuter);
   }

   public void setTense(char ch)
   {
      if (isVerb() || isVerbPastParticiple())
         tagStr.setCharAt(5, ch);
   }

   public void setVoice(char ch)
   {
      if (isVerb())
         tagStr.setCharAt(2, ch);
   }

   public void setProperNameType(char ch)
   {
     if (isProperNoun())
     {
        tagStr.setCharAt(5, ch);
     }
     else
     {
         StringBuffer str;
         // Build a string buffer and append it to the tag
         if (!hasArticle())
         {
            str = new StringBuffer("-x");
            str.setCharAt(1,ch);
         }
         else
         {
            str = new StringBuffer("x");
            str.setCharAt(0,ch);
         }

         tagStr.append(str);
         interpretTag();            // The tag has now changed to a Proper Noun
     }

   }

   public void setPersonName()
   {
       if (isProperNoun())
         tagStr.setCharAt(5, cPersonName);
   }

   public void setDeclension(char ch)
   {
      if (isAdjective())
      {
         if (!(ch == cStrong && isAdjectiveComparative()))  // comparative has only weak declension 
            tagStr.setCharAt(4, ch);
      }
   }

   public void setDegree(char ch)
   {
      if (isAdjective())
         tagStr.setCharAt(5, ch);
      if (isAdverb())
         tagStr.setCharAt(2, ch);
   }

   public void setPersonGender(char ch)
    {
       if (isNoun() || isProperNoun() || isAdjective() || isArticle())
       {
         if (tagStr.length() >= 2)
            tagStr.setCharAt(1, ch);
       }
       else if (isPronoun() || isNumeral())
       {
         if (tagStr.length() >= 3)
            tagStr.setCharAt(2, ch);
       }
       else if (isVerb() && (ch == cFirstPerson || ch == cSecondPerson || ch == cThirdPerson))
       {
         if (tagStr.length() >= 4)
            tagStr.setCharAt(3, ch);
       }
       else if (isVerbPastParticiple())
       {
         if (tagStr.length() >= 4)
            tagStr.setCharAt(3, ch);
       }
    }

    public void setNumber(char ch)
    {
       if (isNoun() || isProperNoun() || isAdjective() || isArticle())
       {
           if (tagStr.length() >= 3)
             tagStr.setCharAt(2, ch);
       }
       else if (isPronoun() || isNumeral())
       {
         if (tagStr.length() >= 4)
            tagStr.setCharAt(3, ch);
       }
       else if (isVerb() || isVerbPastParticiple())
       {
         if (tagStr.length() >= 5)
            tagStr.setCharAt(4, ch);
       }
    }

    public void setCase(char ch)
    {
       if (isNoun() || isProperNoun() || isAdjective() || isArticle())
       {
         if (tagStr.length() >= 4)
           tagStr.setCharAt(3, ch);
       }
       else if (isPronoun() || isNumeral())
       {
         if (tagStr.length() >= 5)
            tagStr.setCharAt(4, ch);
       }
       else if (isPreposition())
       {
         if (tagStr.length() >= 2)
            tagStr.setCharAt(1, ch);
       }
       else if (isVerbPastParticiple())
       {
           if (tagStr.length() >= 6)
            tagStr.setCharAt(5,ch);
       }
    }

   public void setGenderNumberCase(char gen, char num, char cas)
   {
       setPersonGender(gen);
       setNumber(num);
       setCase(cas);
   }

   public boolean isWordClass(WordClass wcCode)
   {
       return (wordClass == wcCode);
   }

    public boolean isPronoun()
    {
      return (wordClass == WordClass.wcDemPronoun ||
                wordClass == WordClass.wcPersPronoun ||
                wordClass == WordClass.wcReflPronoun ||
                wordClass == WordClass.wcRelativePronoun ||
                wordClass == WordClass.wcPossPronoun ||
                wordClass == WordClass.wcIndefPronoun ||
                wordClass == WordClass.wcIntPronoun
                );
    }

  public boolean isNominal()
  {
        return (isNoun() || isProperNoun() || isAdjective() || isNumeral() || isPronoun() || isArticle());
  }

  public boolean isNoun()
   {
       return (wordClass == WordClass.wcNoun);
   }

   public boolean isProperNoun()
   {
       return (wordClass == WordClass.wcProperNoun);
   }

   public boolean isAdjective()
   {
       return (wordClass == WordClass.wcAdj);
   }

   public boolean isArticle()
   {
       return (wordClass == WordClass.wcArticle);
   }

   public boolean isAdverb()
   {
       return (wordClass == WordClass.wcAdverb);
   }

   public boolean isForeign()
   {
       return (wordClass == WordClass.wcForeign);
   }

   // Comparative
   public boolean isAdverbComparative()
   {
       return (tagStr.length() == 3 && tagStr.substring(0,3).equals("aam"));
   }

   // Superlative
   public boolean isAdverbSuper()
   {
       return (tagStr.length() == 3 && tagStr.substring(0,3).equals("aae"));
   }

   public boolean isAdjectivePositive()
   {
       return (isAdjective() && (tagStr.charAt(5) == cPositive));
   }

   public boolean isAdjectiveComparative()
   {
       return (isAdjective() && (tagStr.charAt(5) == cComparative));
   }

   public boolean isAdjectiveSuper()
   {
       return (isAdjective() && (tagStr.charAt(5) == cSuperlative));
   }

   public boolean isAdjectiveWeak()
   {
       return (isAdjective() && (tagStr.charAt(4) == cWeak));
   }
   public boolean isAdjectiveStrong()
   {
       return (isAdjective() && (tagStr.charAt(4) == cStrong));
   }
   public boolean isAdjectiveIndeclineable()
   {
       return (isAdjective() && (tagStr.charAt(4) == cIndeclineable));
   }

    public boolean isExclamation()
   {
       return (wordClass == WordClass.wcExcl);
   }
    public boolean isRelativeConjunction()
   {
       return (wordClass == WordClass.wcConjRel);
   }

    public boolean isConjunction()
   {
       return (wordClass == WordClass.wcConj);
   }

    public boolean isInfinitive()
    {
        return (wordClass == WordClass.wcInf);
    }

    public boolean isPreposition()
    {
        return  (wordClass == WordClass.wcPrep);
    }

     public boolean isPersonalPronoun()
    {
        return (wordClass == WordClass.wcPersPronoun);
    }

    public boolean isIndefinitePronoun()
    {
        return (wordClass == WordClass.wcIndefPronoun);
    }

    public boolean isReflexivePronoun()
    {
       return (wordClass == WordClass.wcReflPronoun);
    }

    public boolean isRelativePronoun()
    {
       return (wordClass == WordClass.wcRelativePronoun);
    }

    public boolean isDemonstrativePronoun()
    {
       return (wordClass == WordClass.wcDemPronoun);
    }

    public boolean isInterrogativePronoun()
    {
       return (wordClass == WordClass.wcIntPronoun);
    }

    public boolean isPossessivePronoun()
    {
        return (wordClass == WordClass.wcPossPronoun);
    }

   public boolean isNumeral()
   {
       return (wordClass == WordClass.wcNumeral);
   }

   public boolean isNumeralOrdinal()
   {
       return (tagStr.toString().equals(tagOrdinal) || tagStr.toString().equals(tagOrdinal2) || tagStr.toString().equals(tagPercentage));
   }

   public boolean isNumeralNominal()
   {
       return !(tagStr.toString().equals(tagOrdinal) || tagStr.toString().equals(tagOrdinal2) || tagStr.toString().equals(tagPercentage));
   }

    public boolean isVerbInfinitive()
   {
       return (wordClass == WordClass.wcVerbInf);
   }


   public boolean isVerbPastParticiple()
   {
       return (wordClass == WordClass.wcVerbPastPart);
   }

   public boolean isVerb()
   {
      return (wordClass == WordClass.wcVerb);
   }

   public boolean isVerbAny()
   {
       return (wordClass == WordClass.wcVerb || wordClass == WordClass.wcVerbInf || wordClass == WordClass.wcVerbPastPart);
   }

   public boolean isVerbAuxiliary()
   {
       return (tagStr.toString().matches(".+<h>.*"));
   }

   public boolean isVerbSpecialAuxiliary()
   {
       return (tagStr.toString().matches(".+<s.?>.*"));
   }

   public boolean isVerbSpecialInf()
   {
       return (tagStr.toString().matches(".+<.?i>.*"));
   }

   public boolean isVerbMarking()
   {
       return (tagStr.toString().matches(".+<.+>.*"));
   }

   public boolean isVerbCaseMarking()
   {
       return (tagStr.toString().matches(".+<[oþe].?>.*"));
   }

 // Returns true if the tag denotes some form of the verb "að vera"
   public boolean isVerbBe()
   {
       return (tagStr.toString().matches(".+<v>.*"));
   }

   public boolean isVerbMiddleForm()
   {
       return (tagStr.length() > 2 &&
               (tagStr.substring(0,3).equals("snm") || tagStr.substring(0,3).equals("sfm") || tagStr.substring(0,3).equals("svm")));
   }

   public boolean isVerbImperative()
   {
       return (tagStr.length() > 1 &&
               (tagStr.substring(0,2).equals("sb")));
   }

   public boolean isVerbIndicativeForm()
   {
       return (tagStr.length() > 1 &&
               tagStr.substring(0,2).equals("sf"));
   }
   public boolean isVerbSubjunctiveForm()
   {
       return (tagStr.length() > 1 &&
               tagStr.substring(0,2).equals("sv"));
   }

   public boolean isVerbActiveForm()
   {
       return (tagStr.length() > 2 &&
               (tagStr.substring(0,3).equals("sfg") || tagStr.substring(0,3).equals("svg")));
   }


   public boolean isVerbSupine()               // Sagnbót
   {
       return (tagStr.length() > 2 &&
               (tagStr.substring(0,3).equals("ssg") || tagStr.substring(0,3).equals("ssm")));
   }

   public boolean isVerbPresentPart()
   {
       return (tagStr.length() > 2 &&
               (tagStr.substring(0,2).equals("sl")));
   }

   public boolean hasArticle()
   {
       return ((isNoun() || isProperNoun()) && tagStr.length() > 4 && tagStr.charAt(4) == cArticle);
   }


   public boolean isDeclension(char ch)
   {
      return (ch == getDeclension());
   }

   public boolean isCase(char ch)
   {
      return (ch == getCaseLetter());
   }

   public boolean isGender(char ch)
   {
      return (ch == getPersonGenderLetter());
   }

   public boolean isNumber(char ch)
   {
      return (ch == getNumberLetter());
   }

   public boolean isProperNounType(char ch)
   {
      return (ch == getProperNounType());
   }

   public char getProperNounType()
   {
      if (isProperNoun())
         return tagStr.charAt(tagStr.length()-1);
      else
         return cNoMatch;
   }

   public char getDeclension()
   {
      if (isAdjective())
         return tagStr.charAt(4);
      else
         return cNoMatch;
   }

   public char getCaseLetter()
   {
       StringBuffer tag = tagStr;
       char caseLetter=cNoMatch;
       char first = tag.charAt(0);
       switch  (first)
       {
          case 'a' : if (tag.charAt(1) == cAdverb)       // adverb
                        caseLetter = cNoMatch;           // wildCard, allow adverbs to match all cases
                     else
                        caseLetter = tag.charAt(1);     // Prepositions
                     break;
          case 'f' : caseLetter = tag.charAt(4);     // Pronouns
                     break;
          case 't' : if (tag.length() >= 5)          // Numerals
                        caseLetter = tag.charAt(4);
                     //else
                     //   caseLetter = cWildCard;
                     break;
          case 'n' :
          case 'l' :
          case 'g' : if (tag.length() >= 4)          // Nouns, Adjectives, Article
                        caseLetter = tag.charAt(3);
                     //else
                     //       caseLetter = cWildCard;
                     break;
          // For verb tags marked with case which means that the
          // subject can not be in nominative case, like "léttir" sfg3en<o>
          case 's' : if (isVerbCaseMarking())
                        caseLetter = tag.charAt(tag.length()-2);
                     else
                        if (isVerbPastParticiple())
                            caseLetter = tag.charAt(5);
                     else
                        caseLetter = cNoMatch;
                     break;
          default:   caseLetter = cNoMatch;                  // Should not match a case
                     break;
       }
       return caseLetter;
   }

   public char getNumberLetter()
   {
       StringBuffer tag = tagStr;
       char nLetter=cNoMatch;
       char first = tag.charAt(0);
       switch  (first)
       {
          case 'f' : nLetter = tag.charAt(3);     // Pronouns
                     break;
          case 't' : if (tag.length() >= 4)          // Numerals
                        nLetter = tag.charAt(3);
                     //else
                     //   nLetter = cWildCard;
                     break;
          case 'n' :
          case 'l' :
          case 'g' : if (tag.length() >= 3)          // Nouns, Adjectives, Article
                        nLetter = tag.charAt(2);
                     //else
                     //   nLetter = cWildCard;
                     break;
          case 's' : if (tag.length() >= 5)          // Verbs
                        nLetter = tag.charAt(4);
                     //else
                     //   nLetter = cWildCard;
                     break;
          default:   nLetter = cNoMatch;                  // Should not match a case
                     break;
       }
       return nLetter;
   }

    public char getPersonGenderLetter()
   {
       StringBuffer tag = tagStr;
       char pLetter=cNoMatch;
       char first = tag.charAt(0);
       switch  (first)
       {
          case 'f' : pLetter = tag.charAt(2);     // Pronouns
                     break;
          case 't' : if (tag.length() >= 3)          // Numerals
                        pLetter = tag.charAt(2);

                     break;
          case 'n' :
          case 'l' :
          case 'g' : if (tag.length() >= 2)          // Nouns, Adjectives, Article
                        pLetter = tag.charAt(1);
                     break;
          case 's' : if (tag.length() >= 4)          // Verbs
                        pLetter = tag.charAt(3);

                     break;
          default:   pLetter = cNoMatch;                  // Should not match a case
                     break;
       }
       return pLetter;
   }

  public boolean numberMatch(IceTag tagCmp)
 {
    char letter1, letter2;

    letter1 = getNumberLetter();
    letter2 = tagCmp.getNumberLetter();

    return (letter1 != cNoMatch && (letter1 == letter2 || letter1 == cWildCard || letter2 == cWildCard));

 }

   public boolean caseMatch(IceTag tagCmp)
   {
       if (tagStr == null || (tagCmp.tagStr == null))
            return true;
       else
       {
        char caseLetter1, caseLetter2;

       caseLetter1 = getCaseLetter();
       caseLetter2 = tagCmp.getCaseLetter();

       return (caseLetter1 != cNoMatch && (caseLetter1 == caseLetter2 || caseLetter1 == cWildCard || caseLetter2 == cWildCard));
       }
   }

   public boolean personGenderMatch(IceTag tagCmp)
   {
       if (tagStr == null || (tagCmp.tagStr == null))
            return true;
       else
       {
        char letter1, letter2;

        letter1 = getPersonGenderLetter();
        letter2 = tagCmp.getPersonGenderLetter();

        return (letter1 != cNoMatch &&
                (letter1 == letter2 || letter1 == cWildCard || letter2 == cWildCard ||
                (letter1 == cThirdPerson &&
                (letter2 == cMasculine || letter2 == cFeminine ||
                 letter2 == cNeuter || letter2 == cGenderUnspec)) ||
                 ((letter1 == cMasculine || letter1 == cFeminine ||
                 letter1 == cNeuter || letter1 == cGenderUnspec) && letter2==cThirdPerson )
                ));
       }
   }

   public boolean numberMatch(ArrayList tags)
   {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (numberMatch(tag))
              return true;
       }
       return false;
   }

   public boolean caseMatch(ArrayList tags)
   {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (caseMatch(tag))
              return true;
       }
       return false;
   }

  public boolean personGenderMatch(ArrayList tags)
   {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (personGenderMatch(tag))
              return true;
       }
       return false;
   }


  public boolean personGenderNumberMatch(ArrayList tags)
  {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (personGenderNumberMatch(tag))
              return true;
       }
       return false;
  }

  public boolean personGenderNumberMatch(IceTag tagCmp)
  {
       if (tagStr == null || (tagCmp.tagStr == null))  // don't compare two verbs
            return true;
       else
       {
            boolean match=false;
            match = personGenderMatch(tagCmp);
            if (match)
            {
                match = numberMatch(tagCmp);
            }
            return match;
       }
  }

  public boolean numberCaseMatch(IceTag tagCmp)
  {
       if (tagStr == null || (tagCmp.tagStr == null))
            return true;
       else
       {
            boolean match=false;
            match = numberMatch(tagCmp);
            if (match)
               match = caseMatch(tagCmp);
            return match;
       }
  }

  public boolean genderNumberCaseMatch(IceTag tagCmp)
  {
       if (tagStr == null || (tagCmp.tagStr == null))
            return true;
       else
       {
            boolean match=false;
            match = personGenderMatch(tagCmp);
            if (match)
            {
                match = numberMatch(tagCmp);
                if (match)
                    match = caseMatch(tagCmp);
            }
            return match;
       }
  }

  public boolean numberCaseMatch(ArrayList tags)
  {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (numberCaseMatch(tag))
              return true;
       }
       return false;
   }

   public boolean genderNumberCaseMatch(ArrayList tags)
   {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (genderNumberCaseMatch(tag))
              return true;
       }
       return false;
   }

   /*
 * Sets     the word class according to the tag
 */
  protected void interpretTag()
  {
    if (tagStr != null)
    //if (tagStr.length() > 0)
    {
        switch (tagStr.charAt(0)) {
            case 'a' :  if (tagStr.length() == 1)
                            wordClass = WordClass.wcAdverb;
                        else if (tagStr.charAt(1) == cAdverb || tagStr.charAt(1) == cAbbreviation)
                            wordClass = WordClass.wcAdverb;
                        else if (tagStr.charAt(1) == 'u')
                            wordClass = WordClass.wcExcl;
                        else
                            wordClass = WordClass.wcPrep;
                        break;
            case 'e' :  wordClass = WordClass.wcForeign;
                        break;
            case 'n' :  if (tagStr.length() == 6 &&
                             // m=person name, ö=place name, s=other proper name
                            (tagStr.charAt(5) == cOtherName || tagStr.charAt(5) == cPersonName || tagStr.charAt(5) == cPlaceName))
                            wordClass = WordClass.wcProperNoun;
                        else
                            wordClass = WordClass.wcNoun;
                        break;
            case 'l' :  wordClass = WordClass.wcAdj;
                        break;
            case 'f' :  if (tagStr.length() == 1)
                            wordClass = WordClass.wcPronoun;
                        else if (tagStr.substring(1,2).equals("p"))
                            wordClass = WordClass.wcPersPronoun;
                        else if (tagStr.substring(1,2).equals("b"))
                            wordClass = WordClass.wcReflPronoun;
                        else if (tagStr.substring(1,2).equals("e"))
                            wordClass = WordClass.wcPossPronoun;
                        else if (tagStr.substring(1,2).equals("s"))
                            wordClass = WordClass.wcIntPronoun;
                        else if (tagStr.substring(1,2).equals("o"))
                            wordClass = WordClass.wcIndefPronoun;
                        else if (tagStr.substring(1,2).equals("a"))
                            wordClass = WordClass.wcDemPronoun;
                        else if (tagStr.substring(1,2).equals("t"))
                            wordClass = WordClass.wcRelativePronoun;
                        break;
            case 't' :  wordClass = WordClass.wcNumeral;
                        break;
            case 'g' :  wordClass = WordClass.wcArticle;
                        break;
            case 's' :
                        if (tagStr.length() == 1)
                            wordClass = WordClass.wcVerb;
                        else if (tagStr.charAt(1) == 'n')
                            wordClass = WordClass.wcVerbInf;
                        else if ((tagStr.charAt(1) == 'þ')) //|| (tagStr.charAt(1) == 's'))
                            wordClass = WordClass.wcVerbPastPart;
                        else
                            wordClass = WordClass.wcVerb;
                        break;
            case 'c' :
                        if (tagStr.length() == 1)
                            wordClass = WordClass.wcConj;
                        else if (tagStr.charAt(1) == 'n')
                            wordClass = WordClass.wcInf;
                        else if (tagStr.charAt(1) == 't')
                             wordClass = WordClass.wcConjRel;
                        else
                            wordClass = WordClass.wcConj;
                        break;
            default:    wordClass = WordClass.wcPunct;
                        break;
        }
    }
    else
        wordClass = WordClass.wcUndef;
  }

  private String annotationGenderNumberCase(int firstIndex, boolean english)
  {
      if (tagStr.length() >= firstIndex+1)
        return
           annotationGender(tagStr.charAt(firstIndex), english) + AnnotationSeparator +
           annotationNumber(tagStr.charAt(firstIndex+1), english) + AnnotationSeparator +
           annotationCase(tagStr.charAt(firstIndex+2), english);
      else
        return "";
  }

  private String annotationGender(char c, boolean english)
  {
      switch (c) {
          case cMasculine:
                if (english) return "Masculine";
                else return "Karlkyn";
          case cFeminine:
              if (english) return "Feminine";
                else return "Kvenkyn";
          case cNeuter:
              if (english) return "Neuter";
                else return "Hvorugkyn";
          case cFirstPerson:
              if (english) return "1. person";
                else return "1. persóna";
          case cSecondPerson:
              if (english) return "2. person";
                else return "2. persóna";
          case cThirdPerson:
              if (english) return "3. person";
                else return "3. persóna";
          case cGenderUnspec:
              if (english) return "Unspecified gender";
                else return "Kynlaust";
          default:
              if (english) return "Unknown gender";
                else return "Óþekkt kyn";
      }
  }

  private String annotationNumber(char c, boolean english)
  {
      switch (c) {
          case cSingular:
                if (english) return "Singular";
                else return "Eintala";
          case cPlural:
              if (english) return "Plural";
                else return "Fleirtala";
          default:
              if (english) return "Unknown number";
                else return "Óþekkt tala";
      }
  }

  private String annotationCase(char c, boolean english)
  {
      switch (c) {
          case cNominative:
                if (english) return "Nominative";
                else return "Nefnifall";
          case cAccusative:
              if (english) return "Accusative";
                else return "Þolfall";
          case cDative:
              if (english) return "Dative";
                else return "Þágufall";
          case cGenitive:
              if (english) return "Genitive";
                else return "Eignarfall";
          default:
              if (english) return "Unknown case";
                else return "Óþekkt fall";
      }
  }

  private String annotationSuffixedArticle(boolean english)
  {
      if (tagStr.length() >= 5 && (tagStr.charAt(4) == cArticle))
      {
          if (english) return (AnnotationSeparator + "Suffixed article");
          else return (AnnotationSeparator + "Viðskeyttur greinir");
      }
      else
        return "";
  }

  private String annotationPronoun(boolean english)
  {
      StringBuffer txt = new StringBuffer();
      switch (tagStr.charAt(1)) {
        case cPersPronoun:  if (english) txt.append("Pronoun Personal"); else txt.append("Persónufornafn");
                            break;
        case cReflPronoun:  if (english) txt.append("Pronoun Reflexive"); else txt.append("Afturbeygt fornafn");
                            break;
        case cPossPronoun:  if (english) txt.append("Pronoun Possessive"); else txt.append("Eignarfornafn");
                            break;
        case cInterPronoun: if (english) txt.append("Pronoun Interrogative"); else txt.append("Spurnarfornafn");
                            break;
        case cIndefPronoun: if (english) txt.append("Pronoun Indefinite"); else txt.append("Óákveðið fornafn");
                            break;
        case cDemPronoun:   if (english) txt.append("Pronoun Demonstrative"); else txt.append("Ábendingarfornafn");
                            break;
        case cRelPronoun:   if (english) txt.append("Pronoun Relative"); else txt.append("Tilvísanafornafn");
                            break;
        default:            if (english) txt.append("Pronoun Unknown"); else txt.append("Óþekkt fornafn");
                            break;
     }
     txt.append(AnnotationSeparator);
     txt.append(annotationGenderNumberCase(2, english));

     return txt.toString();
  }

  private String annotationDegree(char c, boolean english)
  {
    switch (c) {
            case cPositive:     if (english) return "Positive"; else return "Frumstig";
            case cComparative:  if (english) return "Comparative"; else return "Miðstig";
            case cSuperlative:  if (english) return "Superlative"; else return "Efsta stig";
            default:            if (english) return "Unknown degree"; else return "Óþekkt stig";
         }
  }

  private String annotationAdjective(boolean english)
  {
      StringBuffer txt = new StringBuffer();
      if (english) txt.append("Adjective"); else txt.append("Lýsingarorð");
      txt.append(AnnotationSeparator);
      txt.append(annotationGenderNumberCase(1, english));

      if (tagStr.length() == 6)
      {
         txt.append(AnnotationSeparator);
         switch (tagStr.charAt(4)) {
            case cStrong:   if (english) txt.append("Strong declension"); else txt.append("Sterk beyging");
                            break;
            case cWeak:     if (english) txt.append("Weak declension"); else txt.append("Veik beyging");
                            break;
            case cIndeclineable: if (english) txt.append("No declension"); else txt.append("Óbeygt");
                            break;
            default:        if (english) txt.append("Unknown declension"); else txt.append("Óþekkt beyging");
                            break;
         }
         txt.append(AnnotationSeparator);
         txt.append(annotationDegree(tagStr.charAt(5), english));
      }
      return txt.toString();
  }

  private String annotationVoice(char c, boolean english)
  {
      switch (c) {
          case cActive:
                if (english) return "Active voice";
                else return "Germynd";
          case cMiddle:
              if (english) return "Middle voice";
                else return "Miðmynd";
          default:
              if (english) return "Unknown voice";
                else return "Óþekkt mynd";
      }
  }

  private String annotationTense(char c, boolean english)
  {
      switch (c) {
          case cPresent:
                if (english) return "Present";
                else return "Nútíð";
          case cPast:
              if (english) return "Past";
                else return "Þátíð";
          default:
              if (english) return "Unknown tense";
                else return "Óþekkt tíð";
      }
  }

  private String annotationVerb(boolean english)
  {
     StringBuffer txt = new StringBuffer();
     if (english) txt.append("Verb" + AnnotationSeparator);
     else
        txt.append("Sögn" + AnnotationSeparator);
     switch (tagStr.charAt(1)) {
        case cIndicative:   if (english) txt.append("Indicative"); else txt.append("Framsöguháttur");
                            break;
        case cSubjunctive:  if (english) txt.append("Indicative"); else txt.append("Viðtengingarháttur");
                            break;
        case cInfinitive:   if (english) txt.append("Infinitive"); else txt.append("Nafnháttur");
                            break;
        case cSupine:       if (english) txt.append("Supine"); else txt.append("Sagnbót");
                            break;
        case cImperative:   if (english) txt.append("Imperative"); else txt.append("Boðháttur");
                            break;
        case cPastParticiple:   if (english) txt.append("Past participle"); else txt.append("Lýsingarháttur þátíðar");
                                break;
        case cPresParticiple:   if (english) txt.append("Present participle"); else txt.append("Lýsingarháttur nútíðar");
                                break;
        default:            if (english) txt.append("Unknown mood"); else txt.append("Óþekktur háttur");
                                break;
     }
     txt.append(AnnotationSeparator);
     txt.append(annotationVoice(tagStr.charAt(2), english));

     if (tagStr.length() == 6) {
        if (tagStr.charAt(1) == cPastParticiple)
        {
            txt.append(AnnotationSeparator);
            txt.append(annotationGenderNumberCase(3, english));
        }
        else {
            txt.append(AnnotationSeparator);
            txt.append(annotationGender(tagStr.charAt(3), english));
            txt.append(AnnotationSeparator);
            txt.append(annotationNumber(tagStr.charAt(4), english));
            txt.append(AnnotationSeparator);
            txt.append(annotationTense(tagStr.charAt(5), english));
        }
     }
     return txt.toString();
  }

  private String annotationNumeral(boolean english)
  {
      StringBuffer txt = new StringBuffer();
      if (english) txt.append("Numeral "); else txt.append("Töluorð ");
      if (tagStr.length() > 2) {
         txt.append(AnnotationSeparator);
         txt.append(annotationGenderNumberCase(2, english));
      }
      return txt.toString();
  }

  private String annotationArticle(boolean english)
  {
      StringBuffer txt = new StringBuffer();
      if (english) txt.append("Article "); else txt.append("Greinir ");
      txt.append(AnnotationSeparator);
      txt.append(annotationGenderNumberCase(1, english));

      return txt.toString();
  }

  private String annotationNoun(boolean english)
  {
      StringBuffer txt = new StringBuffer();
      if (tagStr.length() == 6 &&
          // m=person name, ö=place name, s=other proper name
         (tagStr.charAt(5) == cOtherName || tagStr.charAt(5) == cPersonName || tagStr.charAt(5) == cPlaceName))
        {
            if (english) txt.append("Proper noun"); else txt.append("Sérnafn");
            txt.append(AnnotationSeparator);
            txt.append(annotationGenderNumberCase(1, english));
            txt.append(annotationSuffixedArticle(english));
            txt.append(AnnotationSeparator);
            switch (tagStr.charAt(5)) {
                case cPersonName:   if (english) txt.append("Person name"); else txt.append("Nafn á persónu");
                                    break;
                case cPlaceName:    if (english) txt.append("Place name"); else txt.append("Staðarnafn");
                                    break;
            }
         }
      else {
          if (english) txt.append("Noun"); else txt.append("Nafnorð");
          txt.append(AnnotationSeparator);
          txt.append(annotationGenderNumberCase(1, english));
          txt.append(annotationSuffixedArticle(english));
      }
      return txt.toString();
  }

  private String annotationConjunction(boolean english)
  {
    if (tagStr.length() == 1)
    {
       if (english) return "Conjunction"; else return "Samtenging";
    }
    else
      switch (tagStr.charAt(1)) {
          case cInfinitive: if (english) return "Infinitive marker"; else return "Nafnháttarmerki";
          case cConjunctionRel: if (english) return "Conjunction Relative"; else return "Tilvísunartenging";
          default: if (english) return "Conjunction"; else return "Samtenging";
      }
  }

  private String annotationAdverb(boolean english)
  {
     StringBuffer txt = new StringBuffer();
     if (tagStr.charAt(1) == cAdverb)
     {
          if (english) txt.append("Adverb"); else txt.append("Atviksorð");
          if (tagStr.length() == 3) {
              txt.append(AnnotationSeparator);
              txt.append(annotationDegree(tagStr.charAt(2), english));
          }
     }
     else if (tagStr.charAt(1) == cAbbreviation) {
         if (english) txt.append("Abbreviation"); else txt.append("Skammstöfun");
     }
     else if (tagStr.charAt(1) == cExclamation)
     {
         if (english) txt.append("Interjection"); else txt.append("Upphrópun");
     }
     else {
         if (english) txt.append("Preposition"); else txt.append("Forsetning");
         txt.append(AnnotationSeparator);
         txt.append(annotationCase(tagStr.charAt(1), english));
     }
     return txt.toString();
  }

  public String annotation(boolean english)
  {
    if (tagStr != null)
    {
        switch (tagStr.charAt(0)) {
            case 'a' :  return (annotationAdverb(english));
            case 'e' :  if (english) return "Foreign word"; else return "Erlent orð";
            case 'n' :  return (annotationNoun(english));
            case 'l' :  return (annotationAdjective(english));
            case 'f' :  return (annotationPronoun(english));
            case 't' :  return (annotationNumeral(english));
            case 'g' :  return (annotationArticle(english));
            case 's' :  return (annotationVerb(english));
            case 'c' :  return (annotationConjunction(english));
            default:    if (english) return "Punctuation"; else return "Greinamerki";
        }
    }
    else
        return "";
  }

}