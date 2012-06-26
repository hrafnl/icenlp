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
package is.iclt.icenlp.core.tritagger;

import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.utils.Idioms;
import is.iclt.icenlp.core.utils.Tag;
import java.util.Vector;
import java.util.ArrayList;

/**
 * A HMM bigram or trigram tagger.
 * <br> A re-implementation of the TnT tagger, described in:
 * <br>Thorsten Brants. 2000. TnT - A Statistical Part-of-Speech Tagger. In Proceedings of the Sixth Conference on Applied Natural Language Processing. Seattle, Washington, USA.
 * <br>Moreover, the tagger is designed to integrate with IceTagger.
 * @author Hrafn Loftsson
 */
public class TriTagger {
    Ngrams myNgrams;                    // The ngrams
    FreqLexicon myLexicon;              // The lexicon
    FreqLexicon myBackupLexicon;        // A backup lexicon
    Idioms myIdioms;                    // Lexicon of idioms
    ArrayList myTokens;                 // Tokens
    Vector myTagsLower;           // A vector used for assigning to unknowns not found in suffix trie
    Vector myTagsUpper;           // A vector used for assigning to unknowns not found in suffix trie
    private StateMap myStateMap;        // tags are keys.  Running counter are values.
    int totalTagsAmbiguous=0, numAmbiguousTokens=0;
    private int totalTags = 0;                // Total number of tags
    int maxSentenceLength=128;
    int maxStatesInSentence=16384;
    private static final double minLogProb = Double.NEGATIVE_INFINITY;
    private static final double cutOffThreshold=1000000.0;
    public static final int bigrams = 2;
    public static final int trigrams = 3;
    private int myNgramType;
    private double [][] sigma;    // sigma[i][j] holds the probability of being in state j at word i
    private int [][] path;        // path[i+1][j] holds the most likely state at word i given that we are in state j at word i+1
    private boolean useIceMorphy=false;
    private IceTokenTags iceToken;
    private IceMorphy morpho;
    private double theta;   // used for smoothing of lexical probabilities of unknown words
    private TokenTags boundaryToken;            // Used for prepending in front of each sentence
    public static final int sentenceStartUpperCase=0;
    public static final int sentenceStartLowerCase=1;
    private static int sentenceStart;
    private boolean caseSensitive=false; // if true, then dont lookup using both upper case and lower case at the start of a sentence

    int numTags;                  // Number of tags in the tag set
    int statesInSentence=0;       // Number of tags in the sentence to be tagged

    public TriTagger(int sentenceStartCode, boolean isCaseSensitive, int ngramType, Ngrams ngrams, FreqLexicon lex, FreqLexicon backup, Idioms id, IceMorphy mAnalyzer)
    {

        myNgrams = ngrams;
        myLexicon = lex;
        myBackupLexicon = backup;
        myIdioms = id;
        sentenceStart = sentenceStartCode;
        caseSensitive = isCaseSensitive;
        numTags = myNgrams.getNumTags();
        myStateMap = new StateMap();
        setArraySizes();
        setNgram(ngramType);
        theta = myNgrams.getTheta();

        boundaryToken = new TokenTags(Ngrams.boundaryTag, Token.TokenCode.tcPeriod);
        boundaryToken.setTag(Ngrams.boundaryTag);

        morpho = mAnalyzer;
        useIceMorphy = morpho != null;
        if (useIceMorphy)
            iceToken = new IceTokenTags();

        getAllTagsForUnknowns();
    }

    public void initStatistics()
    {
        numAmbiguousTokens = 0;
        totalTags = 0;
        totalTagsAmbiguous = 0;
    }

    // Gets the tags for unknowns that were encountered during suffix trie construction
    private void getAllTagsForUnknowns()
    {
        myTagsLower = new Vector();
        myTagsUpper = new Vector();
        Vector tagStrings = myLexicon.getTagsLower();
        for (int i=0; i<tagStrings.size(); i++)
        {
            String tagStr = (String)tagStrings.elementAt(i);
            Tag tag = new Tag(tagStr);
            myTagsLower.add(tag);
        }

        tagStrings = myLexicon.getTagsUpper();
        for (int i=0; i<tagStrings.size(); i++)
        {
            String tagStr = (String)tagStrings.elementAt(i);
            Tag tag = new Tag(tagStr);
            myTagsUpper.add(tag);
        }
    }

    private void setArraySizes()
    {
       sigma = new double [maxSentenceLength][maxStatesInSentence];
       path = new int [maxSentenceLength][maxStatesInSentence];
       //cutOff = new boolean [maxSentenceLength][maxStatesInSentence];
    }

    public void setNgram(int ngramType)
    {
        myNgramType = ngramType;
    }

    public int getTotalTagsAmbiguous()
    {
       return totalTagsAmbiguous;
    }

    public int getNumAmbiguousTokens()
    {
       return numAmbiguousTokens;
    }

    public int getTotalTags()
	{
		return totalTags;
	}

    // Processes tokens that include numeric characters
    private void processCardinal(TokenTags tok)
    {
       String key=null;
       String lexeme = tok.lexeme;
       if (lexeme.matches("^\\d+[\\.\\-,\\/:]+\\d+"))    // Digits separated by dots, dashes, comma, etc.
            key = "@CARDSEPS";
       else if (lexeme.matches("^\\d+\\."))             // Digits followed by punctuation, e.g. "42."
            key = "@CARDPUNCT";
       else if (lexeme.matches("^\\d+$"))               // Only digits
            key = "@CARD";
       else if (lexeme.matches("^\\d+.+"))          // Digits followed by any suffix
            key = "@CARDSUFFIX";
       else if (lexeme.matches("^\\d*\\D+\\d+"))   // Any tokens including a digit
            key = "@CARDSUFFIX";

       if (key != null)
       {
           String tagStr = myLexicon.lookupWord(key, false);
           if (tagStr != null) {
                tok.addAllTags(tagStr);
                tok.setCardinalKey(key);
           }
       }
    }

    private void setTags(TokenTags tok, Vector tags)
    {
        for (int i=0; i<tags.size(); i++)
            tok.addTag((Tag)tags.elementAt(i));
    }

    // Get possible tags from IceMorphy, but only use them if based on morphological/compound analysis
    private void processUnknownIceMorphy(TokenTags tok)
    {
        iceToken.clearTags();
        iceToken.lexeme = tok.lexeme;
        if (Character.isUpperCase(iceToken.lexeme.charAt(0)))    // mark it as proper noun if appropriate
          iceToken.setUnknownType(IceTokenTags.UnknownType.properNoun);

        morpho.morphoAnalysisToken(iceToken, null);  // no knowledge of previous tag
        if (iceToken.isUnknownMorpho())  // If based on morphological analysis
        {
            tok.setAllTags(iceToken.allTagStrings());
            tok.setMorpho(true);
        }
    }

    // Uses suffix info to guess the possible tags for the token
    private void processUnknownSuffix(TokenTags tok)
    {
        String tagStr;
        String lexeme = tok.lexeme;

        boolean isUpperCase = Character.isUpperCase(lexeme.charAt(0));
        int len = tok.lexeme.length();

        int suffixLen = Math.min(myLexicon.suffixLength, len);   // The length of the suffix used
        // Get the possible tags from the suffix lexicon
        boolean found=false;
        for (int i=len-suffixLen; i<len; i++)
        {
            String suffix = lexeme.substring(i);        // suffix from lexeme[i] to end of word

            tagStr = myLexicon.lookupSuffix(suffix, isUpperCase);
            if (tagStr != null) {
                if (!found) {
                    tok.setSuffixLength(suffixLen);     // set the length of the suffix used
                }
                tok.addAllTags(tagStr);
                found = true;
            }
            suffixLen--;
        }
        // If still no tags then use all possible tags
        if (tok.noTags()) {
            if (Character.isUpperCase(tok.lexeme.charAt(0)))
                setTags(tok, myTagsUpper);
            else
            {
                setTags(tok, myTagsLower); // Assign all possible lower case tags found during suffix trie construcion
                //System.out.println("Found no suffix for unknown word: " + tok.lexeme);
            }
        }
    }

    /*
    * Lookups the given token in the dictionary
    * assigns the corresponding tag to the token if found
    */
    private void dictionaryTokenLookup(int tokIndex, TokenTags tok)
    {
         String tagStr, tagStrBackup=null;

         if ((tokIndex == 0) && (sentenceStart == sentenceStartUpperCase))
         {
             if (!caseSensitive) { // Then lookup using both lower and upper case
                tagStr = myLexicon.lookupWord(tok.lexeme, true);       // First ignore case
                if (tagStr == null) // The word then could be a proper noun
                    tagStr = myLexicon.lookupWord(tok.lexeme, false); // Now don't ignore case
             }
             else
                 tagStr = myLexicon.lookupWord(tok.lexeme, false); // Now Don't ignore case
         }
         else
            tagStr = myLexicon.lookupWord(tok.lexeme, false);       // Don't ignore case

         if (myBackupLexicon != null) // Lookup in the backup lexicon?
            tagStrBackup = myBackupLexicon.lookupWord(tok.lexeme, false);
         if (tagStr != null || tagStrBackup != null)
         {
             tok.setUnknown(false);
             if (tagStr != null)
                tok.addAllTags(tagStr);
             if (tagStrBackup != null)
                tok.addAllTags(tagStrBackup);

             int numberOfTags = tok.numTags();
             totalTags += numberOfTags;

             if (numberOfTags > 1) {
                 numAmbiguousTokens++;    // Increase the number of ambiguous tokens
                 totalTagsAmbiguous += numberOfTags;
             }
         }
         // else the word is unknown to the tagger.  Use suffix info or IceMorphy
         else {
             processCardinal(tok);
             if (tok.noTags()) {     // If still no tags
                if (useIceMorphy)
                {
                    processUnknownIceMorphy(tok);
                    if (tok.noTags())
                       processUnknownSuffix(tok);
                }
                else
                    processUnknownSuffix(tok);
             }
             tok.setUnknown(true);
         }
    }

 /*
 *
 * Dictionary lookup.
 * Checks if tag exist in dictionar.
 */
    private void dictionaryLookup()
    {
        ArrayList tokens = myTokens;
        TokenTags currToken;

        for (int i=0; i<tokens.size(); i++)
        {
            currToken = (TokenTags)tokens.get(i);
            if (currToken.noTags())
                dictionaryTokenLookup(i, currToken);    // Don't ignore case
        }
    }

    // Returns the lexical probability for a known word, P(word|tag)
    private double lexicalProbKnown(String word, String tag)
    {
        // P(word/tag) = P(word,tag)/P(tag)
        int wordTagFreq = myLexicon.getFrequencyWordTag(word, tag);
        // Next can happen if the idioms lookup assigns to a word a tag which does not exists in the lexicon for that word
        // or when IceTagger calls TriTagger and the word is known in IceTagger's lexicon but not in the Tritagger model!
        // The latter can happen when for example the word exists in the special lexicon of IceTagger but not in the Tritagger's lexicon
        // derived from a training corpus 
        if (wordTagFreq == 0)
        {
            //System.out.println("Did not find frequency for word " + word + " tag " + tag);
            wordTagFreq = 1;
        }
        int unigramFreq = myNgrams.getFrequency(tag);
        if (unigramFreq == 0)
        {
            return 0.0;
        }
        else
            return (double)wordTagFreq/unigramFreq;
    }

    private double tagSuffixProb(String tag, String suffix, boolean isUpperCase)
    {
        /*
        P(tag|suffix) = P(tag,suffix)/P(suffix)
        Let p=f(tag,suffix)/f(suffix). p is an estimate of P(tag|suffix).
        */
        int suffixTagFreq = myLexicon.getFrequencySuffixTag(suffix, tag, isUpperCase);
        int suffixFreq = myLexicon.getFrequencySuffix(suffix, isUpperCase);
        if (suffixFreq == 0)
        {
            //System.out.println("Did not find frequency for suffix: " + suffix);
            //System.exit(0);
            return 0.0;
        }
        else
            return (double)suffixTagFreq/suffixFreq;
    }

    // Returns the probability of a tag given a suffix
    // P(tag|suffix) with successive abstractions (Brants, 2000)
    // The recursion formula is:
    // P(t|l_n-i+1,...,l_n) = ( ^P(t|l_n-i+1,...,l_n) + theta*P(t|l_n-i,...,l_n) ) / (1+theta)
    // where, ^P(t|l_n-i+1,...,l_n) are maximum likelihood estimates from training corpus

    /*private double probTagSuffixAbstraction(String tag, String suffix, int suffixLen, boolean isUpperCase)
    {
        //if (suffix == null)   // Stop condition
        if (suffixLen <= 0)   // Stop condition
            // return myLexicon.getProbTagSuffixTrie(tag, isUpperCase);
            return myNgrams.getUnigramProb(tag);

        else {
            double prob;
            String newSuffix=null;

            prob = tagSuffixProb(tag, suffix, isUpperCase);

            if (suffix.length() >= myLexicon.suffixStart)
            //if (suffixLen >= myLexicon.suffixStart)
            {
                if (suffix.length() > 1)
                    newSuffix = suffix.substring(1);     // Next suffix is one character shorter, starting from the second letter
                else
                    newSuffix = suffix.substring(0);     // In this case the remaining suffix is the last letter
                suffixLen--;
            }
            else
                suffixLen = -1;

            return (prob + theta*probTagSuffixAbstraction(tag, newSuffix, suffixLen, isUpperCase))/(1.0+theta);
        }
    } */

    // Same as above but implemented as a loop instead of recursion
    //  P(t|l_n-i+1,...,l_n) = ^P(t|l_n-i+1,...,l_n) / (1+theta) + theta*^P(t|l_n-i,...,l_n) / (1+theta)^2 +
    //                         theta^2*^P(t|l_n-i-1,...,l_n) / (1+theta)^3 ... +
    private double probTagSuffixAbstractionLoop(String tag, String suffix, boolean isUpperCase)
    {
        String mySuffix = new String(suffix);
        int suffixLen = mySuffix.length();
        double prob, totalProb=0.0;
        int i=0;

        // Here comes an important revision: If the frequency for a particular suffix is only one then it is highly likely that the
        // probability distribution for the suffix is skewed.  I.e. if we use the abstraction algorithm unchanged then too much probability
        // mass will be assigned to the tag corresponding to the suffix with frequency one.
        // Therefore, in the case of frequency one we ignore the suffix, shorten it by one and continue.
        // This improves tagging accuracy for unknown word.
        int suffixTagFreq = myLexicon.getFrequencySuffixTag(suffix, tag, isUpperCase);
        if (suffixTagFreq <= 1) // Actually shold never be less than one!
        {
            // Shorten the suffix
            if (mySuffix.length() > 1)
                mySuffix = mySuffix.substring(1);     // Next suffix is one character shorter, starting from the second letter
            suffixLen--;
        }

        for (int m=suffixLen; m>=0; m--)
        {
            if (m>0)
                prob = tagSuffixProb(tag, mySuffix, isUpperCase);
            else
                //prob = myLexicon.getProbTagSuffixTrie(tag, isUpperCase);    // P(tag|empty suffix) = P(tag)
                prob = myNgrams.getUnigramProb(tag);

            prob = Math.pow(theta, i)*prob/Math.pow(1.0+theta,i+1);
            totalProb += prob;
            i++;

            if (mySuffix.length() >= myLexicon.suffixStart && m > 0)
               mySuffix = mySuffix.substring(1);     // Next suffix is one character shorter, starting from the second letter
        }
        return totalProb;
    }

    private double lexicalSuffixProb(TokenTags tok, String tag)
    {
        String word = tok.lexeme;
        double prob;
        int len = word.length();
        boolean isUpperCase = Character.isUpperCase(word.charAt(0));

        int suffixLen = tok.getSuffixLength();
        int suffixIndex = len-suffixLen;
        String suffix = word.substring(suffixIndex);
        double probTag = myNgrams.getUnigramProb(tag);
        if (suffixLen == 0)     // This means that no applicable suffix was found for the word, should not happen
            prob = probTag;
        else
        {
           prob = probTagSuffixAbstractionLoop(tag, suffix, isUpperCase);
          // Now we have p(tag|suffix), but we need p(suffix|tag) for Viterbi
          // Using Bayes inversion: p(suffix|tag) = p(tag|suffix)*p(suffix)/p(tag)
          // p(suffix) is a constant when finding the tag that maximises lexProb*contextProb in Viterbi
          // => we can drop p(suffix) from the calculation
          if (prob >= 1.0)
          {
            System.out.println("Major error, probability of suffix: " + suffix + Double.toString(prob));
            System.exit(0);
          }
          prob = prob / probTag;
        }
        return prob;
    }

    // Returns the lexical probability P(word|tag) for known words
    // P(suffix|tag) for unknown words
    private double lexicalProb(TokenTags tok, String tag)
    {
        if (tok.isUnknown())
        {
            if (tok.isCardinal())
                return lexicalProbKnown(tok.getCardinalKey(),tag);
            else if (tok.isMorpho()) // This can be the case if TriTagger called IceMorphy for guessing tags of unknown words
                 return (1.0/tok.numTags());       // Assume all the tags are equally likely
            else
                return lexicalSuffixProb(tok, tag);
        }
        else
           return lexicalProbKnown(tok.lexeme,tag);
    }


    private void init_Arrays()
    {
        // Initialize sigma and path
        // sigma[i][j] holds the probability of being in state j at word i
        for (int i=0; i<myTokens.size(); i++)
            for (int j=0; j<statesInSentence; j++)
            {
               //sigma[i][j] = 0.0;
                sigma[i][j] = minLogProb;
                path[i][j] = -1;
            }
    }

    private void init_bigrams()
    {
        init_Arrays();

        int boundaryId = myStateMap.getStateId(Ngrams.boundaryTag);      // Get the identifier for period
        if (boundaryId < 0)
        {
            System.out.println("Could not find period tag!");
            System.exit(0);
        }
        sigma[0][boundaryId] = 0.0;        // The log probability of first word given the period tag (sentence starter) is 0.0

    }

    private void init_trigrams()
    {
        init_Arrays();

        int boundaryId = myStateMap.getStateId(Ngrams.boundaryTag, Ngrams.boundaryTag);   // Get the identifier for the first state
        if (boundaryId < 0)
        {
            System.out.println("Could not find <period period> state!");
            System.exit(0);
        }
        sigma[1][boundaryId] = 0.0;        // The log probability of first word given the sentence starter is 0.0
    }

    private void pathReadOut_bigrams()
    {
        double maxProb = minLogProb;
        int currTagId=-1;
        TokenTags tok;
        ArrayList tags;
        Tag tag;

        int numTokens = myTokens.size();
        // Path readout
        // First find the state with the highest probability at the end of the sentence
        maxProb = minLogProb;
        tok = (TokenTags)myTokens.get(numTokens-1);
        tags = tok.getTags();
        for (int i=0; i<tags.size(); i++)
        {
           tag = (Tag)tags.get(i);
           String tagStr = tag.getTagStr();
           int k = myStateMap.getStateId(tagStr);
           double prob = sigma[numTokens-1][k];
           if (prob > maxProb)
           {
               maxProb = prob;
               currTagId = k;
           }
         }

         // Read backwords ...
         for (int i=numTokens-1; i>0; i--)
         {
            // Get the chosen tag for token i and remove all other tags
            tok = (TokenTags)myTokens.get(i);
            String tagStr = myStateMap.getState(currTagId);
            tok.removeAllBut(tagStr);
            // Get the next state in the path
            currTagId = path[i][currTagId];
         }
    }

    private void pathReadOut_trigrams()
    {
      double maxProb = minLogProb;
      //int max_PrevStateId=0;
      int max_LastStateId=0;
      TokenTags tok, prevTok;
      ArrayList tags, prevTags;
      Tag tag, prevTag;

      int numTokens = myTokens.size();
      // Path readout
      // First find the state with the highest probability at the end of the sentence
      maxProb = minLogProb;
      tok = (TokenTags)myTokens.get(numTokens-1);
      prevTok = (TokenTags)myTokens.get(myTokens.size()-2);
      tags = tok.getTags();
      prevTags = prevTok.getTags();
      for (int i=0; i<tags.size(); i++)
      {
         tag = (Tag)tags.get(i);
         String lastTagStr = tag.getTagStr();

         for (int j=0; j<prevTags.size(); j++)
         {
             prevTag = (Tag)prevTags.get(j);
             String prevTagStr = prevTag.getTagStr();
             int lastStateId = myStateMap.getStateId(prevTagStr,lastTagStr);

             double prob = sigma[numTokens-1][lastStateId];
             if (prob > maxProb)
             {
                    maxProb = prob;
                    max_LastStateId = lastStateId;
             }
         }
      }

      if (maxProb != minLogProb)
      {
        // Read backwords ...
        for (int i=numTokens-1; i>0; i--)
        {
           // Get the chosen tag for token i and remove all other tags
           tok = (TokenTags)myTokens.get(i);
           String secondTagStr = myStateMap.getSecondTag(max_LastStateId);    // The second tag in each state corresponds to the correct tag
           //String firstTagStr = myStateMap.getFirstTag(max_LastStateId);
           tok.removeAllBut(secondTagStr);
           // Get the next state in the path
           max_LastStateId = path[i][max_LastStateId];
        }
      }
    }

    private void disambiguate_bigrams()
    {
        double maxStateProb = minLogProb;
        double lexProb, contextProb;    // lexical and contextual probabilities
        double atEndProb=0.0;
        int max_TagId = 0;              // Which previous tag (state) gives the maximum probability ?
        int currTagId=-1;

        TokenTags tok, prevTok;
        ArrayList tags, prevTags;
        Tag tag, prevTag;

        // Viterbi
        int lastTokenIdx = myTokens.size()-1;
        init_bigrams();
        // The Viterbi algorithm does not need to consider all possible states (tags)
        // For each state (tag) it is sufficient to consider only those states (tags) that are possible as previous states (tags)

        for (int i=0; i<=lastTokenIdx-1; i++)
        {
            tok = (TokenTags)myTokens.get(i+1);   // is.iclt.icenlp.core.tokenizer.Token i+1 in the sentence
            boolean lastToken = (i+1==lastTokenIdx);     // last token in the sentence?

            tags = tok.getTags();    // The possible tags of token i, according to the lexicon
            for (int j=0; j<tags.size(); j++)
            {
               tag = (Tag)tags.get(j);
               String currTagStr = tag.getTagStr();
               currTagId = myStateMap.getStateId(currTagStr);      // Get the identifier for the tag
               max_TagId = 0;
               maxStateProb = minLogProb;

               lexProb = lexicalProb(tok, currTagStr);
               if (lastToken)
                  atEndProb = myNgrams.contextualProbSmoothing(Ngrams.boundaryTag, currTagStr);

               prevTok = (TokenTags)myTokens.get(i);   // Previous token
               prevTags = prevTok.getTags();    // and its possible tags
               for (int m=0; m<prevTags.size(); m++)    // Compute the maximum probability
               {
                   prevTag = (Tag)prevTags.get(m);
                   String prevTagStr = prevTag.getTagStr();
                   int prevTagId = myStateMap.getStateId(prevTagStr);
                   double sigm = sigma[i][prevTagId];
                   contextProb = myNgrams.contextualProbSmoothing(currTagStr, prevTagStr);

                   double prob = minLogProb;
                   //double prob = sigm * lexprob * contextProb;
                   if (!(lexProb == 0.0 || contextProb == 0.0))
                        prob = sigm + Math.log(lexProb) + Math.log(contextProb);
                   //else
                   //   System.out.println("Lex prob: " + Double.toString(lexProb) + " ContextProb: " + Double.toString(contextProb) + " " + currTagStr + " " + prevTagStr);
                   // Need to add P(t_T+1|t_T)  see Brants(2000)
                   if (lastToken)
                        prob += Math.log(atEndProb);

                   if (prob > maxStateProb)
                   {
                       maxStateProb = prob;
                       max_TagId = prevTagId;
                   }
               }
               sigma[i+1][currTagId] = maxStateProb;     // Record the probability of being in state tagId at word i+1
               path[i+1][currTagId] = max_TagId;    // Most likely state at word i given we are in stage tagId at word i+1
            }
        }
        // Find the optimal path
        pathReadOut_bigrams();
    }


    private void disambiguate_trigrams()
    {
        double maxStateProb = minLogProb;    // Largest sigma found in current state for current word
        double maxSigmaWord=minLogProb;     // Largest sigma found for current word
        double lexProb, contextProb;    // lexical and contextual probabilities
        double atEndProb=0.0;
        int max_PrevStateId=0;
        int currStateId=-1, prevStateId=-1;

        TokenTags tok, prevTok, prevPrevTok;
        ArrayList tags, prevTags, prevPrevTags;
        Tag tag, prevTag, prevPrevTag;

        // Viterbi
        int lastTokenIdx = myTokens.size()-1;
        init_trigrams();
        // The Viterbi algorithm does not need to consider all possible states (tags)
        // It is sufficient to consider only those tags that are possible in the given sentence

        for (int i=1; i<=lastTokenIdx-1; i++)
        {
            tok = (TokenTags)myTokens.get(i+1);   // is.iclt.icenlp.core.tokenizer.Token i+1 in the sentence
            boolean lastToken = (i+1==lastTokenIdx);     // last token in the sentence?
            maxSigmaWord = minLogProb;
            tags = tok.getTags();    // The possible tags of token i, according to the lexicon

            for (int j=0; j<tags.size(); j++)         // Loop through current tags
            {
               tag = (Tag)tags.get(j);
               String currTagStr = tag.getTagStr();
               max_PrevStateId = 0;

               lexProb = lexicalProb(tok, currTagStr);

               prevTok = (TokenTags)myTokens.get(i);   // Previous token
               prevTags = prevTok.getTags();    // and its possible tags
               prevPrevTok = (TokenTags)myTokens.get(i-1);   // Previous previous token
               prevPrevTags = prevPrevTok.getTags();    // and its possible tags

               for (int m=0; m<prevTags.size(); m++)    // Loop through previous tags
               {
                 prevTag = (Tag)prevTags.get(m);
                 String prevTagStr = prevTag.getTagStr();
                 currStateId = myStateMap.getStateId(prevTagStr, currTagStr);
                 maxStateProb = minLogProb;
                 if (lastToken)
                    atEndProb = myNgrams.contextualProbSmoothing(Ngrams.boundaryTag, currTagStr, prevTagStr);
                 for (int n=0; n<prevPrevTags.size(); n++)  // Loop through previous previous tags
                 {                                          // Compute the maximum probability by considering the previous two tags
                   prevPrevTag = (Tag)prevPrevTags.get(n);
                   String prevPrevTagStr = prevPrevTag.getTagStr();
                   prevStateId = myStateMap.getStateId(prevPrevTagStr, prevTagStr);
                   double sigm = sigma[i][prevStateId];
                   contextProb = myNgrams.contextualProbSmoothing(currTagStr, prevPrevTagStr, prevTagStr);

                   double prob = minLogProb;
                   //double prob = sigm * lexprob * contextProb;
                   if (!(lexProb == 0.0 || contextProb == 0.0))
                        prob = sigm + Math.log(lexProb) + Math.log(contextProb);
                     //else
                     //   System.out.println("Lexical prob: " + Double.toString(lexProb) + ", Contextual prob: " + Double.toString(contextProb));

                   // Each state that receives a sigma value smaller than the largest sigma divided by threshold is excluded from further processing
                   if (maxSigmaWord != minLogProb && prob < maxSigmaWord-Math.log(cutOffThreshold))    // The beam search
                        break;

                   // Need to add P(t_T+1|t_T)  see Brants(2000)
                   if (lastToken)
                        prob += Math.log(atEndProb);

                   if (prob > maxStateProb)
                   {
                       maxStateProb = prob;
                       max_PrevStateId = prevStateId;
                   }
                 }
                 sigma[i+1][currStateId] = maxStateProb;           // Record the probability of being in state currStateId at word i+1
                 path[i+1][currStateId] = max_PrevStateId;    // Most likely state at word i given we are in state currStateId at word i+1
                 if (maxStateProb > maxSigmaWord)
                     maxSigmaWord = maxStateProb;
               }
            }
        }
        // Find the optimal path
        pathReadOut_trigrams();
    }

    private void checkArraySizes()
    {
        boolean changeSentenceLength = false;
        boolean changeNumberOfStates = false;

        int sentenceLength = myTokens.size();

        if (sentenceLength > maxSentenceLength)
        {
            maxSentenceLength = sentenceLength;
            changeSentenceLength = true;
        }
        if (statesInSentence > maxStatesInSentence)
        {
            maxStatesInSentence = statesInSentence;
            maxSentenceLength = sentenceLength;         // Try to reduce memory when states increase
            changeNumberOfStates = true;
        }

        if (changeSentenceLength || changeNumberOfStates)
        {
            setArraySizes();
            //System.out.println("Sentence length: " + maxSentenceLength + ", number of states: " + maxStatesInSentence);
            //if (changeNumberOfStates)
            //    System.out.println("Maximum number of states found: " + maxStatesInSentence);
        }

    }

    // IceTagger calls this method with performDictionary=false
    public void tagTokens(ArrayList tokens, boolean performDictionaryLookup)
    {
        myTokens = tokens;

        if (performDictionaryLookup)
        {
            dictionaryLookup();
            if (myIdioms != null)
                myIdioms.findIdioms(myTokens);
        }
        myTokens.add(0, boundaryToken);

        if (myNgramType == trigrams)
        {
            myTokens.add(0, boundaryToken);
            statesInSentence = myStateMap.mapTagsTrigrams(myTokens);
            checkArraySizes();
            disambiguate_trigrams();
            // At last remove the dummy tags at the beginning
            myTokens.remove(0);
            myTokens.remove(0);
        }
        else    // bigrams
        {
            statesInSentence = myStateMap.mapTagsBigrams(myTokens);
            checkArraySizes();
            disambiguate_bigrams();
            // At last remove the dummy tag at the beginning
            myTokens.remove(0);
        }
    }
}
