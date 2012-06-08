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
package is.iclt.icenlp.core.tokenizer;

import is.iclt.icenlp.core.utils.IceTag;

import java.util.Iterator;
import java.util.ArrayList;

/**
  * Tokens used by IceTagger
  * @author Hrafn Loftsson
 */
public class IceTokenTags extends TokenTags
{
    public enum UnknownType {morpho, ending, guessed, properNoun, none}
    public enum SVOMark {svoMainSubject, svoMainVerb, svoObject, svoVerb, svoPrepPhrase, svoSubject, svoNone}
    public enum Match {personGenderNumber,  genderNumberCase, numberCase, aCase, number, gender}
    public enum Condition{condVerbBe, condVerbAny, condVerbInf, condVerbAux, condVerbSpecialAux, condVerbSpecialInf,
                        condVerbCaseMark, condVerbMiddle, condVerbImperative, condVerbIndicative, condVerbSubjunctive,
                        condVerbActive,condVerbSupine, condVerbPresentPart, condPronoun, condArticle,
                        condAdverbComp, condAdverbSuper, condAdjPositive, condAdjComp, condAdjSuper, condAdjWeak,
                        condAdjStrong, condAdjIndeclineable, condHasGender, condCase, condWordClass, condOrdinal, condProperNoun}


    private UnknownType unknownType;    // type of unknown
    private boolean compound;   // Assigned by compound analysis?
    private SVOMark svoMark;        // Subject, verb, object mark
    
    private boolean unknownExternal = false;	// Was the word marked unknown by an external source
    private String invMWMark = null;
    
    public IceTokenTags()
    {
        super();
        initialize();
    }

    public IceTokenTags(String str, TokenCode tc)
    {
        super(str, tc);
        initialize();
    }


    private void initialize()
    {
        compound = false;
        unknownType = UnknownType.none;
        svoMark = SVOMark.svoNone;
    }

    public void addTag(String t)
    {
        if (!tagExists(t))
            tags.add(new IceTag(t));
    }

    public void addTagFront(String t)
    {
        if (!tagExists(t))
            tags.add(0, new IceTag(t));
    }

    public IceTokenTags makeCopy()
    {
        // First make a copy of the lexeme, tokencode and unknown type
        IceTokenTags newTok = new IceTokenTags(lexeme, tokenCode);
        newTok.setUnknown(isUnknown());
        newTok.setUnknownType(unknownType);
        newTok.setSVOMark(svoMark);
        newTok.setCompound(compound);
        // Then copy the tags
        ArrayList tags = getTags();
        for (int j=0; j<=tags.size()-1; j++) {
            IceTag tag = (IceTag)tags.get(j);
            newTok.addTag(tag.getTagStr());
        }
        return newTok;
    }

    public String getSVOMarkString()
   {
       return svoMark.toString();
   }

   public SVOMark getSVOMark()
   {
       return svoMark;
   }

   public void setSVOMark(SVOMark mark)
   {
       svoMark = mark;
   }

    public boolean isSVOObject()
    {
        return (svoMark == SVOMark.svoObject);
    }

    public boolean isSVOMainSubject()
    {
        return (svoMark == SVOMark.svoMainSubject);
    }

    public boolean isSVOSubject()
    {
        return (svoMark == SVOMark.svoSubject);
    }

    public boolean isSVOMainVerb()
    {
        return (svoMark == SVOMark.svoMainVerb);
    }

    public boolean isSVOVerb()
    {
        return (svoMark == SVOMark.svoVerb);
    }

    public boolean isSVOPrepPhrase()
    {
        return (svoMark == SVOMark.svoPrepPhrase);
    }

    public boolean isSVONone()
    {
        return (svoMark == SVOMark.svoNone);
    }

    public boolean isUnknownMorpho()
    {
        return (unknownType==UnknownType.morpho);
    }

    public boolean isUnknownEnding()
    {
        return (unknownType==UnknownType.ending);
    }

    public boolean isUnknownGuessed()
    {
        return (unknownType==UnknownType.guessed);
    }

    public boolean isUnknownProperNoun()
    {
        return (unknownType==UnknownType.properNoun);
    }

    public boolean isUnknownNone()
    {
        return (unknownType==UnknownType.none);
    }

    public boolean isCompound()
    {
        return (compound);
    }
    
    public boolean isUnknownExternal()
    {
    	return (unknownExternal);
    }
    
    public void setUnknownExternal(boolean unknown)
    {
    	unknownExternal = unknown;
    }
    
    public void setInvMWMark(String invMWMark)
    {
    	this.invMWMark = invMWMark;
    }
    
    public String getInvMWMark()
    {
    	return invMWMark;
    }

    public void setUnknownType(UnknownType type)
    {
        unknownType = type;
    }

    public void setCompound(boolean flag)
    {
        compound = flag;
    }

    public int numNominals()
    {
        // Tags might have been marked as invalid
        int num=0;
        for (int i=0; i<tags.size(); i++)
        {
            IceTag tag = (IceTag)tags.get(i);
            if ((tag.isNominal() || tag.isVerbPastParticiple()) && tag.isValid())
                num++;
        }
        return num;
    }

    // Returns the number of different cases
    public int numCases()
    {
        int nf = 0, thf=0, thgf=0, ef=0;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           char cas = tag.getCaseLetter();

           switch (cas)
           {
             case IceTag.cNominative: if (nf == 0) nf++; break;
             case IceTag.cAccusative: if (thf == 0) thf++; break;
             case IceTag.cDative: if (thgf == 0) thgf++; break;
             case IceTag.cGenitive: if (ef == 0) ef++; break;
           }
        }
        return (nf+thf+thgf+ef);
    }

    // Returns the number of different genders
    public int numGenders()
    {
        int kk = 0, kvk=0, hk=0;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           char cas = tag.getPersonGenderLetter();

           switch (cas)
           {
             case IceTag.cMasculine: if (kk == 0) kk++; break;
             case IceTag.cFeminine: if (kvk == 0) kvk++; break;
             case IceTag.cNeuter: if (hk == 0) hk++; break;
           }
        }
        return (kk+kvk+hk);
    }

    public String getFirstWordClass()
    {
        IceTag tag = (IceTag)getFirstTag();
        return (tag.getWordClass());
    }

    // Adds article marker to each tag
    public void addArticle()
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.addArticle();
       }
    }

    // set for each tag
    public void setProperNameType(char ch)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setProperNameType(ch);
       }
    }

    // set plural marker for each tag
    public void setPlural()
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setNumber(IceTag.cPlural);
       }
    }

    // set case marker for each tag
    public void setCase(char ch)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setCase(ch);
       }
    }

    // set gender marker for each tag
    public void setGender(char gen)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setPersonGender(gen);
       }
    }

    // set declension each tag
    public void setDeclension(char decl)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setDeclension(decl);
       }
    }

    // set degree each tag
    public void setDegree(char degree)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.setDegree(degree);
       }
    }

   public void removeAllButNounAdj()
   {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (!(tag.isNoun() || tag.isAdjective()))
            iterator.remove();
       }
    }

   public void removeAllButVerbs(boolean allowNoTags)
   {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && (count > 1 || allowNoTags)) 
       {
          IceTag tag = (IceTag)iterator.next();
          if (!(tag.isVerbAny()))
          {
            iterator.remove();
            count--;
          }
       }
    }

    public void removeAllBut(IceTag.WordClass wClass)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          if (!(tag.isWordClass(wClass)))
            iterator.remove();
       }
    }

    public void removeAllButCase(char ch)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && count > 1) {
          IceTag tag = (IceTag)iterator.next();
          if (!tag.isCase(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }

    public void removeAllButGender(char ch, boolean allowNoTags)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && (count > 1 || allowNoTags)) {
          IceTag tag = (IceTag)iterator.next();
          if (!tag.isGender(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }

    public void removeAllButNumber(char ch)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && count > 1) {
          IceTag tag = (IceTag)iterator.next();
          if (!tag.isNumber(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }

    public void removeAllButProperNounType(char pnType)
    {
        int count = numTags();
        Iterator iterator = tags.iterator();
        while (iterator.hasNext() && count > 1) {
           IceTag tag = (IceTag)iterator.next();
           if (!tag.isProperNounType(pnType))
           {
               iterator.remove();
               count--;
           }
        }
    }

    public void removeArticle()
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          IceTag tag = (IceTag)iterator.next();
          tag.removeArticle();
       }
    }

    public void removeOnCondition(Condition condi, IceTag.WordClass wc, char ch)
    {
       boolean remove;

       Iterator iterator = tags.iterator();
       int count = numTags();

       while (iterator.hasNext()) {
          remove = false;
          IceTag tag = (IceTag)iterator.next();
          switch (condi)
          {
              case condVerbAny:         remove = tag.isVerbAny();
                                        break;
              case condVerbIndicative:  remove = tag.isVerbIndicativeForm();
                                        break;
              case condVerbActive:      remove = tag.isVerbActiveForm();
                                        break;
              case condVerbSubjunctive: remove = tag.isVerbSubjunctiveForm();
                                        break;
              case condVerbSupine:      remove = tag.isVerbSupine();
                                        break;
              case condPronoun:         remove = tag.isPronoun();
                                        break;
              case condProperNoun:      remove = tag.isProperNounType(ch);
                                        break;
              case condWordClass:       remove = tag.isWordClass(wc);
                                        break;
          }

          if (remove && count > 1)
          {
            iterator.remove();
            count--;
          }
       }
    }

    public void removeProperNounType(char pType)
    {
        removeOnCondition(Condition.condProperNoun, IceTag.WordClass.wcUndef, pType);
    }

    public void removeVerbForm(Condition form)
    {
        removeOnCondition(form,IceTag.WordClass.wcUndef, IceTag.cUndef);
    }



    public void removeVerbs()
    {
        removeOnCondition(Condition.condVerbAny, IceTag.WordClass.wcUndef, IceTag.cUndef);
    }

    public void removePronouns()
    {
        removeOnCondition(Condition.condPronoun, IceTag.WordClass.wcUndef, IceTag.cUndef);
    }

    public void removeWordClass(IceTag.WordClass wcCode)
    {
        removeOnCondition(Condition.condWordClass, wcCode, IceTag.cUndef);
    }

    public void removeCase(char ch, boolean allowNoTags)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && (count > 1 || allowNoTags)) {
          IceTag tag = (IceTag)iterator.next();
          if (tag.isCase(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }

    public void removeGender(char ch, boolean allowNoTags)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && (count > 1 || allowNoTags)) {
          IceTag tag = (IceTag)iterator.next();
          if (tag.isGender(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }

    public void removeNumber(char ch, boolean allowNoTags)
    {
       int count = numTags();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext() && (count > 1 || allowNoTags)) {
          IceTag tag = (IceTag)iterator.next();
          if (tag.isNumber(ch))
          {
               iterator.remove();
               count--;
          }
       }
    }



  public void addInfinitiveVerbForm()
  {
     if (lexeme.endsWith("ast"))
        addTag(IceTag.tagVerbInfMiddle);      // Middle voice
     else
       addTag(IceTag.tagVerbInfActive);       // Active voice
  }
/*
*  Check if any tag is word class wcCode
*/
    public boolean isWordClass(IceTag.WordClass wcCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isWordClass(wcCode))
               return true;
        }
        return false;
    }

  private boolean isAnyMatch(Match type, ArrayList otherTags)
  {
      boolean result = false;
      for (int i=0; i<=tags.size()-1; i++)
      {
        IceTag tag = (IceTag)tags.get(i);

        switch (type)
        {
          case personGenderNumber: if (tag.personGenderNumberMatch(otherTags))
                                           return true;
                                        break;
          case genderNumberCase:   if (tag.genderNumberCaseMatch(otherTags))
                                           return true;
                                        break;
          case aCase:               if (tag.caseMatch(otherTags))
                                           return true;
                                        break;
          case numberCase:         if (tag.numberCaseMatch(otherTags))
                                           return true;
                                        break;
          case number:             if (tag.numberMatch(otherTags))
                                           return true;
                                        break;
          case gender:             if (tag.personGenderMatch(otherTags))
                                             return true;
                                          break;

        }
      }
      return result;
  }

/*
*  Check if any tag has the given gender/person
*/
    public boolean isGenderPerson(char cCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isGender(cCode))
               return true;
        }
        return false;
    }
/*
*  Check if any tag has the given case
*/
    public boolean isCase(char cCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isCase(cCode))
               return true;
        }
        return false;
    }

    // Aukafall
    public boolean isObliqueCase()
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isCase(IceTag.cAccusative) || tag.isCase(IceTag.cDative) || tag.isCase(IceTag.cGenitive))
               return true;
        }
        return false;
    }


    public boolean isNumber(char cCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isNumber(cCode))
               return true;
        }
        return false;
    }

 /*
*  Check if all tags have the given case
*/
    public boolean isOnlyCase(char cCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (!tag.isCase(cCode))
               return false;
        }
        return true;
    }

    public boolean isOnlyGender(char gCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (!tag.isGender(gCode))
               return false;
        }
        return true;
    }

    public boolean isOnlyNumber(char nCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (!tag.isNumber(nCode))
               return false;
        }
        return true;
    }

   // Returns true if the token is only of one gender
    public boolean isOnlyOneGender()
    {
        int kk = 0, kvk=0, hk=0;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           char gender = tag.getPersonGenderLetter();

           switch (gender)
           {
             case IceTag.cMasculine: if (kk == 0) kk++; break;
             case IceTag.cFeminine: if (kvk == 0) kvk++; break;
             case IceTag.cNeuter: if (hk == 0) hk++; break;
           }
        }
        return (kk+kvk+hk==1);
    }
  // Returns true if the token is only of one gender and number
    public boolean isOnlyOneGenderNumber()
    {
        int kk = 0, kvk=0, hk=0;
        int et = 0, ft = 0;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           char gender = tag.getPersonGenderLetter();
           char number = tag.getNumberLetter();

           switch (gender)
           {
             case IceTag.cMasculine: if (kk == 0) kk++; break;
             case IceTag.cFeminine: if (kvk == 0) kvk++; break;
             case IceTag.cNeuter: if (hk == 0) hk++; break;
           }
           switch (number)
           {
             case IceTag.cSingular: if (et == 0) et++; break;
             case IceTag.cPlural: if (ft == 0) ft++; break;
           }
        }
        return (kk+kvk+hk==1 && et+ft==1);
    }

  public boolean isDeclension(char dCode)
  {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isDeclension(dCode))
               return true;
        }
        return false;
  }

/*
* Returns true if any tag matches a tag in the given token
*/
 public boolean numberCaseMatch(IceTokenTags tok)
 {
     return isAnyMatch(Match.numberCase, tok.getTags());
 }
/*
* Returns true if any tag matches a tag in the given token
*/
 public boolean caseMatch(IceTokenTags tok)
 {
     return isAnyMatch(Match.aCase, tok.getTags());
 }
/*
* Returns true if any tag matches a tag in the given token
*/
 public boolean numberMatch(IceTokenTags tok)
 {
     return isAnyMatch(Match.number, tok.getTags());
 }
/*
* Returns true if any tag matches a tag in the given token
*/
 public boolean genderMatch(IceTokenTags tok)
 {
     return isAnyMatch(Match.gender, tok.getTags());
 }
/*
* Returns true if any tag matches a tag in the given token
*/
 public boolean genderNumberCaseMatch(IceTokenTags tok)
 {
     return isAnyMatch(Match.genderNumberCase, tok.getTags());
 }

/*
* Returns true if any tag matches a tag in the given token
*/
public boolean personNumberMatch(IceTokenTags tok)
{
     return isAnyMatch(Match.personGenderNumber, tok.getTags());
}

 public boolean isCondition(Condition condi)
    {
        boolean result = false;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           switch (condi)
           {
               case condVerbBe:     result = tag.isVerbBe();
                                    break;
               case condVerbAny:    result = tag.isVerbAny();
                                    break;
               case condVerbInf:    result = tag.isVerbInfinitive();
                                    break;
               case condVerbAux:    result = tag.isVerbAuxiliary();
                                    break;
               case condVerbSpecialAux:    result = tag.isVerbSpecialAuxiliary();
                                    break;
               case condVerbSpecialInf:    result = tag.isVerbSpecialInf();
                                    break;
               case condVerbCaseMark: result = tag.isVerbCaseMarking();
                                      break;
               case condVerbMiddle: result = tag.isVerbMiddleForm();
                                    break;
               case condVerbImperative: result = tag.isVerbImperative();
                                        break;
               case condVerbIndicative: result = tag.isVerbIndicativeForm();
                                        break;
               case condVerbSubjunctive: result = tag.isVerbSubjunctiveForm();
                                        break;
               case condVerbActive: result = tag.isVerbActiveForm();
                                        break;
               case condVerbSupine: result = tag.isVerbSupine();
                                        break;
               case condVerbPresentPart: result = tag.isVerbPresentPart();
                                        break;
               case condPronoun:    result = tag.isPronoun();
                                    break;
               case condArticle:    result = tag.hasArticle();
                                    break;
               case condHasGender:    result = tag.hasGender();
                                    break;
               case condAdverbComp:     result = tag.isAdverbComparative();
                                        break;
               case condAdverbSuper:     result = tag.isAdverbSuper();
                                        break;
               case condAdjPositive:    result = tag.isAdjectivePositive();
                                        break;
               case condAdjComp:        result = tag.isAdjectiveComparative();
                                        break;
               case condAdjSuper:       result = tag.isAdjectiveSuper();
                                        break;
               case condAdjWeak:       result = tag.isAdjectiveWeak();
                                        break;
               case condAdjStrong:       result = tag.isAdjectiveStrong();
                                        break;
               case condAdjIndeclineable:       result = tag.isAdjectiveIndeclineable();
                                                break;
               case condOrdinal:       result = tag.isNumeralOrdinal();
                                                break;
               //case condCompleteTagProfile:     result = tag.isCompleteTagProfile();
               //                                 break;

           }
           if (result)
               return true;
        }
        return result;
    }
 /*
*  Returns true if at least one of the tags are of some other class than the given word class
*/
    public boolean isOtherThanWordClass(IceTag.WordClass wcCode)
    {
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (!tag.isWordClass(wcCode))
               return true;
        }
        return false;
    }
/*
*  Returns true if all the tags are of the given word class
*/
    public boolean isOnlyWordClass(IceTag.WordClass wcCode)
    {
        boolean only = false;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isWordClass(wcCode))
               only = true;
           else
               return false;
        }
        return only;
    }

    public boolean isOnlyVerbAny()
    {
        boolean only = false;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isWordClass(IceTag.WordClass.wcVerb) || tag.isWordClass(IceTag.WordClass.wcVerbInf) || tag.isWordClass(IceTag.WordClass.wcVerbPastPart))
               only = true;
           else
               return false;
        }
        return only;
    }

    public boolean isOnlyAdverbSuper()
    {
        boolean only = false;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isAdverbSuper())
               only = true;
           else
               return false;
        }
        return only;
    }

    public boolean isOnlyAdjectiveSuper()
    {
        boolean only = false;

        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
           IceTag tag = (IceTag)iterator.next();
           if (tag.isAdjectiveSuper())
               only = true;
           else
               return false;
        }
        return only;
    }

    public boolean hasArticle()
    {
       return isCondition(Condition.condArticle);
    }

    public boolean hasGender()
    {
       return isCondition(Condition.condHasGender);
    }

/*  Check if any tag is pronoun
*/
    public boolean isPronoun()
    {
        return isCondition(Condition.condPronoun);
    }

    public boolean isAdverbComparative()
    {
        return isCondition(Condition.condAdverbComp);
    }

    public boolean isAdverbSuper()
    {
        return isCondition(Condition.condAdverbSuper);
    }

    public boolean isAdjectivePositive()
    {
        return isCondition(Condition.condAdjPositive);
    }

    public boolean isAdjectiveComparative()
    {
        return isCondition(Condition.condAdjComp);
    }

    public boolean isAdjectiveSuper()
    {
        return isCondition(Condition.condAdjSuper);
    }

    public boolean isAdjectiveWeak()
    {
        return isCondition(Condition.condAdjWeak);
    }

    public boolean isAdjectiveStrong()
    {
        return isCondition(Condition.condAdjStrong);
    }
    public boolean isAdjectiveIndeclineable()
    {
        return isCondition(Condition.condAdjIndeclineable);
    }

    public boolean isVerbAny()
    {
        return isCondition(Condition.condVerbAny);
    }

    public boolean isVerbBe()
    {
        return isCondition(Condition.condVerbBe);
    }

    public boolean isVerbInf()
    {
        return isCondition(Condition.condVerbInf);
    }

    public boolean isVerbAuxiliary()
    {
        return isCondition(Condition.condVerbAux);
    }

    public boolean isVerbSpecialAuxiliary()
    {
        return isCondition(Condition.condVerbSpecialAux);
    }
    public boolean isVerbSpecialInf()
    {
        return isCondition(Condition.condVerbSpecialInf);
    }

    public boolean isVerbSubjunctive()
    {
        return isCondition(Condition.condVerbSubjunctive);
    }

    public boolean isVerbIndicative()
    {
        return isCondition(Condition.condVerbIndicative);
    }

    public boolean isVerbActive()
    {
        return isCondition(Condition.condVerbActive);
    }

    public boolean isVerbCaseMarking()
    {
        return isCondition(Condition.condVerbCaseMark);
    }

    public boolean isVerbMiddleForm()
    {
        return isCondition(Condition.condVerbMiddle);
    }

    public boolean isVerbImperative()
    {
        return isCondition(Condition.condVerbImperative);
    }

    public boolean isVerbSupine()
    {
        return isCondition(Condition.condVerbSupine);
    }

    public boolean isVerbPresentPart()
    {
        return isCondition(Condition.condVerbPresentPart);
    }

    /*public boolean isCompleteTagProfile()
    {
        return isCondition(Condition.condCompleteTagProfile);
    }*/

    public boolean isNominal()
    {
        return (isNoun() || isProperNoun() || isAdjective() || isNumeral() || isPronoun() || isArticle());
    }


    public boolean isNoun()
    {
        return isWordClass(IceTag.WordClass.wcNoun);
    }

    public boolean isProperNoun()
    {
        return isWordClass(IceTag.WordClass.wcProperNoun);
    }
    public boolean isAdjective()
    {
        return isWordClass(IceTag.WordClass.wcAdj);
    }
    public boolean isAdverb()
    {
        return isWordClass(IceTag.WordClass.wcAdverb);
    }
    public boolean isNumeral()
    {
        return isWordClass(IceTag.WordClass.wcNumeral);
    }

    public boolean isNumeralOrdinal()
    {
        return isCondition(Condition.condOrdinal);
    }

    public boolean isArticle()
    {
        return isWordClass(IceTag.WordClass.wcArticle);
    }
    public boolean isVerb()
    {
        return isWordClass(IceTag.WordClass.wcVerb);
    }
    public boolean isVerbPastPart()
    {
        return isWordClass(IceTag.WordClass.wcVerbPastPart);
    }

    public boolean isVerbInfinitive()
    {
        return isWordClass(IceTag.WordClass.wcVerbInf);
    }
    public boolean isExclamation()
    {
        return isWordClass(IceTag.WordClass.wcExcl);
    }
    public boolean isPreposition()
    {
        return isWordClass(IceTag.WordClass.wcPrep);
    }
    public boolean isConjunction()
    {
        return isWordClass(IceTag.WordClass.wcConj);
    }
    public boolean isRelativeConjunction()
    {
        return isWordClass(IceTag.WordClass.wcConjRel);
    }
    public boolean isInfinitive()
    {
        return isWordClass(IceTag.WordClass.wcInf);
    }
    public boolean isPersonalPronoun()
    {
        return isWordClass(IceTag.WordClass.wcPersPronoun);
    }
    public boolean isPossessivePronoun()
    {
        return isWordClass(IceTag.WordClass.wcPossPronoun);
    }
    public boolean isDemonstrativePronoun()
    {
        return isWordClass(IceTag.WordClass.wcDemPronoun);
    }
    public boolean isIndefinitePronoun()
    {
        return isWordClass(IceTag.WordClass.wcIndefPronoun);
    }
    public boolean isInterrogativePronoun()
    {
        return isWordClass(IceTag.WordClass.wcIntPronoun);
    }
    public boolean isReflexivePronoun()
    {
        return isWordClass(IceTag.WordClass.wcReflPronoun);
    }
    public boolean isRelativePronoun()
    {
        return isWordClass(IceTag.WordClass.wcRelativePronoun);
    }

    public void cleanTags()
    {
        cleanVerbTags();
        changeReflexivePronounTags();
    }

    public void cleanProperNounTags()
    // Removes named entity distinction from proper nouns
    {
        ArrayList tags = getTags();
		for( int j = 0; j < tags.size(); j++ )
		{
            IceTag tag = (IceTag)tags.get( j );
            if (tag.isProperNoun())
            {
            	tag.setOtherName();
            }
        }
    }

    private void cleanVerbTags()
    {
        ArrayList tags = getTags();
		for( int j = 0; j < tags.size(); j++ )
		{
		    boolean caseMark = false;
			IceTag tag = (IceTag)tags.get( j );
			if( tag.isVerbMarking() )
			{
				if( tag.isVerbSupine() || tag.isVerbInfinitive() )
					tag.setTagStr( tag.getTagStr().substring( 0, 3 ) );    // Delete the <h|v> marking
				else
				{
					// Verb that demand oblique cases for subjects (like langa, hraka) do for some reason only exist in 3rd person
					if( tag.isVerbCaseMarking() )
						caseMark = true;
					tag.setTagStr( tag.getTagStr().substring( 0, 6 ) );    // Delete the marking
					if( caseMark )
						tag.setPersonGender( IceTag.cThirdPerson );
				}
			}
		}
    }

    private void changeReflexivePronounTags()
	{
		if( isReflexivePronoun() &&
			    ( lexeme.equalsIgnoreCase( "sig" ) || lexeme.equalsIgnoreCase( "sér" ) ||
			      lexeme.equalsIgnoreCase( "sín" ) ) )
		{
				ArrayList tags = getTags();
				for( int j = 0; j < tags.size(); j++ )
				{
					IceTag tag = (IceTag)tags.get( j );
					if( tag.getTagStr().substring( 0, 2 ).equals( "fb" ) )
						tag.setTagStr( "fp" + tag.getTagStr().substring( 2, 5 ) );
				}

		}
	}

    public String allWordClasses()
    {
        String wClass = "";
        int i=0;
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
            IceTag element = (IceTag)iterator.next();
            //if (element.isValid())
            //{
                if (i==0)
                    wClass = element.getWordClass();
                else
                    wClass = wClass + "_" + element.getWordClass();
                i++;
            //}
        }
        return wClass;
    }

    public String toString()
    {
        String output = null;

        String tagStr = allTagStrings();

        output = lexeme + " " + tagStr;
        return output;
    }

    public String toStringSpecial()
    {
        String output = null;

        String tagStr = allTagStrings();

        output = lexeme + " <" + tokenCode.toString() + ">" +
            " <" + tagStr + ">"; // + " <" + wClass + ">";
        if (isUnknown())
            output = output + " <UNKNOWN>";
        if (svoMark != SVOMark.svoNone)
            output = output + " <" + getSVOMarkString() +  ">";

        return output;
    }

}
