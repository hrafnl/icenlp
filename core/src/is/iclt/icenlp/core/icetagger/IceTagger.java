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
import is.iclt.icenlp.core.tritagger.*;
import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.utils.*;
import java.util.ArrayList;

/**
 * A PoS-tagger for tagging Icelandic text.
 * <p>IceTagger is described in the following papers:
 * <ul>
 * <li>Hrafn Loftsson, Ida Kramarczyk, Sigrún Helgadóttir and Eiríkur Rögnvaldsson. 2009. Improving the PoS tagging accuracy of Icelandic text. In Proceedings of the 17th Nordic Conference of Computational Linguistics (NODALIDA-2009). Odense, Denmark.</li>
 * <li>Hrafn Loftsson. 2008. Tagging Icelandic text: A linguistic rule-based approach. Nordic Journal of Linguistics, 31(1), 47-72.</li>
 * <li>Hrafn Loftsson. 2006. Tagging a morphologically complex language using heuristics. In T. Salakoski, F. Ginter, S. Pyysalo and T. Pahikkala (eds.), Advances in Natural Language Processing, 5th International Conference on NLP, FinTAL 2006, Proceedings. Turku, Finland.</li>
 * @author Hrafn Loftsson
 */

public class IceTagger
{
    public enum HmmModelType {start, end, startend, none} // start and/or end with a HMM model or no model at all

    private TriTagger triTagger;        // a hmm tagger
	private Lexicon baseDict;			// a base dictionary
	private Lexicon bigDictionary;       // a big dictionary
    private Lexicon verbAdverbDictionary;   // dictionary for verb-adverb (phrasal verbs)
	private Idioms phrases;                 // dictionary for idioms
	private IceDisambiguator ambiguator;    // a POS disambiguator
	private IceMorphy morpho;               // a morphological analyzer
	private IceLog logger = null;           // Log object
	private boolean baseTagging = false;    // Only produce initial assignment based on frequency?
	private boolean fullDisambiguation = true; // Produce fully disambiguated tags?
    private boolean endWithHmmModel = false;
    private boolean startWithHmmModel = false;
    private int numAmbiguousTokens = 0;             // Number of ambiguous tokens
	private int totalTags = 0;                // Total number of tags
	private int totalTagsAmbiguous = 0;       // Total number of tags for ambiguous words
	private ArrayList myTokens;
	public static final int sentenceStartUpperCase = 0;
	public static final int sentenceStartLowerCase = 1;
	private int sentenceStart;
    private HmmModelType modelType = HmmModelType.none; // no HMM model as a default
    // HL: Added 26.11.2010  - If true, then numbers are always tagged with "ta"
    private boolean sameTagForAllNumbers = true;
    // HL: Added 26.11.2010 -  If true then don't ignore the last letter of Proper nouns
    private boolean namedEntityRecognition = false;

    public IceTagger( int sentenceStart, IceLog log, IceMorphy morphoAnalyzer, Lexicon baseDict,
                      Lexicon mainDict,
                      Idioms idioms, Lexicon verbPrepDict, Lexicon verbObjDict, Lexicon verbAdverbDict,
	                  boolean baseTaggingOnly, boolean fullDisambiguation,
                      TriTagger hmmTagger, HmmModelType theModelType)
	{
		this.baseDict = baseDict;
		this.verbAdverbDictionary = verbAdverbDict;
		this.phrases = idioms;

		this.sentenceStart = sentenceStart;
		this.bigDictionary = mainDict;

		this.logger = log;
		this.fullDisambiguation = fullDisambiguation;
		this.baseTagging = baseTaggingOnly;
        this.triTagger = hmmTagger;

        boolean fullDisambiguationInAmbiguator = fullDisambiguation && triTagger==null;

        this.ambiguator = new IceDisambiguator( this.logger, verbPrepDict, verbObjDict, fullDisambiguationInAmbiguator );
		this.morpho = morphoAnalyzer;

        setHmmModelType(theModelType);
    }

    public void initStatistics()
    {
       numAmbiguousTokens = 0;
       totalTags = 0;
       totalTagsAmbiguous = 0;
    }

    public void setTriTagger(TriTagger tagger)
    {
        triTagger = tagger;
    }

    public void setSameTagForAllNumbers(boolean flag)
    {
        sameTagForAllNumbers = flag;
    }

    public void setNamedEntityRecognition(boolean flag)
    {
        namedEntityRecognition = flag;
    }

    public void setHmmModelType(HmmModelType theModelType)
    {
        modelType = theModelType;
        startWithHmmModel = (modelType == HmmModelType.start || modelType == HmmModelType.startend);
        endWithHmmModel = (modelType == HmmModelType.end || modelType == HmmModelType.startend);

        if (theModelType == HmmModelType.none)
            ambiguator.setFullDisambiguation(true);
        else
            ambiguator.setFullDisambiguation(false);
    }


    public boolean getEndWithHmmModel() { return endWithHmmModel;}
    public boolean getStartWithHmmModel() { return startWithHmmModel;}

    public int getNumAmbiguousTokens()
	{
		return numAmbiguousTokens;
	}

	public int getTotalTagsAmbiguous()
	{
		return totalTagsAmbiguous;
	}

	public int getTotalTags()
	{
		return totalTags;
	}

	public String getTagMaxFreq( IceTokenTags tok )
	{
		return morpho.maxFrequency( tok );
	}

    private void computeAmbiguity(IceTokenTags tok)
    {
            int numTags = tok.numTags();
			totalTags += numTags;
			if( numTags > 1 )
			{
				numAmbiguousTokens++;    // Increase the number of ambiguous words
				totalTagsAmbiguous += numTags;
			}
    }

    private void assignTagToNumber(IceTokenTags currToken)
    {
		dictionaryTokenLookup( currToken, false );    // Don't ignore case
		// Override the lexicon!
        if (sameTagForAllNumbers)
           currToken.setTag( IceTag.tagOrdinal );
        else {
		    if( currToken.lexeme.matches( "[0-9\\-\\/\\.,]+%" ) )
		        currToken.setTag( IceTag.tagPercentage );
		    else
			    currToken.setTag( IceTag.tagOrdinal );
        }
		currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
        //System.out.println(currToken.lexeme + ": " + currToken.allTagStrings());
    }

    private void assignTagToAbbreviation(IceTokenTags currToken)
    {
    	if( Character.isLowerCase( currToken.lexeme.charAt( 0 ) ) )
			dictionaryTokenLookup( currToken, true );     // Lookup ignore case
		else
		{ // Probably a proper noun abbreviation
			dictionaryTokenLookup( currToken, false );    // Lookup don't ignore case
			if( currToken.noTags() )                     // If still no tags
			{
				currToken.setTag( IceTag.tagProperNoun );  // Default proper noun
				currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
			}
		}
    }

    /**
     * * Looks up the given token in the dictionary and assigns the corresponding tag to the token if found
     * @param tok The token
     * @param ignoreCase  Ignore case or not?
     */
	private void dictionaryTokenLookup( IceTokenTags tok, boolean ignoreCase )
	{
		String tagStr;
		boolean specialDict = false;


		tagStr = baseDict.lookup( tok.lexeme, ignoreCase );
		if( tagStr != null )
			specialDict = true;

		if( tagStr == null )
			tagStr = bigDictionary.lookup( tok.lexeme, ignoreCase );
		// Add all possible tags; tags are separated by "_"
		if( tagStr != null )
		{
			tok.setUnknown( false );
			tok.addAllTags( tagStr );

            if( !specialDict )
				morpho.generateMissingTags( tok );

            computeAmbiguity(tok);
			tok.setUnknownType( IceTokenTags.UnknownType.none );
		}
		// else the word is unknown to the tagger
		else if( tok.noTags() )
		{
			tok.setUnknown( true );
		}
	}

	private void dictionaryLookupBaseLine()
	{
		ArrayList tokens = myTokens;
		IceTokenTags tok;
		String tagStr;

		for( int i = 0; i < tokens.size(); i++ )
		{
			tok = (IceTokenTags)tokens.get( i );

			tagStr = bigDictionary.lookup( tok.lexeme, false );
			// Add all possible tags; tags are separated by "_"
			if( tagStr != null )
			{
				tok.setUnknown( false );
				tok.addAllTags( tagStr );

				int numTags = tok.numTags();
				totalTags += numTags;
				if( numTags > 1 )
				{
					numAmbiguousTokens++;    // Increase the number of ambiguous words
					totalTagsAmbiguous += numTags;
				}
				tok.setUnknownType( IceTokenTags.UnknownType.none );
			}
			// else the word is unknown to the tagger
			else if( tok.noTags() )
			{
				tok.setUnknown( true );
				if( Character.isUpperCase( tok.lexeme.charAt( 0 ) ) )
					tok.setTag( IceTag.tagMostFrequentProperNoun );
				else
					tok.setTag( IceTag.tagMostFrequentNoun );
			}
		}
	}

	/**
	 *
	 * Dictionary lookup for all tokens.
	 * First check for proper noun, numeral or abbreviation.
	 * Checks if tag exist in dictionary.
	 */
	private void dictionaryLookup()
	{
		ArrayList tokens = myTokens;
		IceTokenTags prevToken = null, currToken;

		for( int i = 0; i < tokens.size(); i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );

			// Try to tag "obvious" things:
			// The tag of a punctuation character is the character itself
			if( currToken.isPunctuation() )
				currToken.setTag( currToken.lexeme );
			// A Cardinal: A token with tokenCode=tcNumber
			else if( currToken.tokenCode == Token.TokenCode.tcNumber )
                assignTagToNumber(currToken);
			// An abbreviation
			else if( currToken.tokenCode == Token.TokenCode.tcAbbrev )
                assignTagToAbbreviation(currToken);
			else if
				// ProperNoun: A token with a capital first letter and not appearing at the beginning of a sentence
				// and not already marked as an abbreviation
					( i > 0 && Character.isUpperCase( currToken.lexeme.charAt( 0 ) )
			          && currToken.tokenCode != Token.TokenCode.tcAbbrev
                      && prevToken != null
                      && prevToken.tokenCode != Token.TokenCode.tcDownQuote
			          && prevToken.tokenCode != Token.TokenCode.tcDoubleQuote )
			{
				dictionaryTokenLookup( currToken, false );    // Don't ignore case
                // HL - added 25.02.2008
                // There might be not be an appropriate sentence delimeter between sentences and thus
                // a uppercase word somewhere in a sentence might actually be the beginning of a sentence,
                // as is the case for the corpus "Ordasjodur"
                if (currToken.noTags() && sentenceStart == sentenceStartUpperCase && prevToken != null && !prevToken.isProperNoun())
                    dictionaryTokenLookup( currToken, true );    // Ignore case

                if( currToken.noTags() )
				{   // If still no tags
					currToken.setUnknownType( IceTokenTags.UnknownType.properNoun );
				}
			}

			else
			{
				// Use this if every sentence starts with an upper case
				if( ( i == 0 ) && ( sentenceStart == sentenceStartUpperCase ) )
				{
					dictionaryTokenLookup( currToken, true ); // First ignore case
					if( currToken.noTags() ) // The word then could be a proper noun
						dictionaryTokenLookup( currToken, false ); // Now don't ignore case
				}
				else // i > 0 || sentenceStart == sentenceStartLowerCase
				{
					// Use this if every sentence starts with a lower case except proper nouns
					dictionaryTokenLookup( currToken, false );   // Don't ignore case
					if( i == 0 && currToken.noTags() && Character.isUpperCase( currToken.lexeme.charAt( 0 ) ) )
						currToken.setUnknownType( IceTokenTags.UnknownType.properNoun );
				}
			}

			prevToken = currToken;
		}
	}

    /**
	* Makes sure that only the first SVO verb tag remains
	* Only done for known verbs
    */
	private void enforceOneTagSVOVerb()
	{
		ArrayList tokens = myTokens;
		IceTokenTags currToken;

		for( int i = 0; i < tokens.size(); i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( !currToken.isUnknown() && ( currToken.isSVOMainVerb() || currToken.isSVOVerb() ) )
				currToken.removeAllButFirstTag();
		}
	}

    /**
	* Removes subject/object markings for words that have adverb tags as their first tag but are
	* nevertheless marked as subject or object.
    */
	private void removeSubjectObjectMarkings()
	{
		ArrayList tokens = myTokens;
		IceTokenTags currToken;

		for( int i = 0; i < tokens.size(); i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			IceTag tag = (IceTag)currToken.getFirstTag();
			if( tag.isAdverb() && !currToken.isSVONone() )
				currToken.setSVOMark( IceTokenTags.SVOMark.svoNone );
		}
	}


	/**
	* Performs special cleaning before tags are printed out
	*/
	private void cleanTags()
	{
        ArrayList tokens = myTokens;
		IceTokenTags currToken;

		for( int i = 0; i < tokens.size(); i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
            currToken.cleanTags();
            if (!namedEntityRecognition)
                currToken.cleanProperNounTags();
        }
        removeSubjectObjectMarkings();
	}

	private void processNumericConstants()  // Numeric constant have the tag "ta" unless followed by a nominal
	{
		IceTokenTags currToken, nextToken;

		ArrayList tokens = myTokens;
		int lastIndex = tokens.size() - 1;
		for( int i = 0; i <= lastIndex; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( i < lastIndex )
				nextToken = (IceTokenTags)tokens.get( i + 1 );
			else
				nextToken = null;
            // "ta"
			if( !currToken.noTags() && currToken.getFirstTag().getTagStr().equals( IceTag.tagOrdinal ) &&
                    !currToken.isDate() && !currToken.isTime())   
			{
				if( nextToken != null )
				{
					//if( nextToken.isNumeral() )
                    if( nextToken.isNumeral() && !nextToken.getFirstTag().getTagStr().equals( IceTag.tagOrdinal ))
					{
						currToken.clearTags();
						currToken.setTag( IceTag.tagOrdinal2 );

					}
					else if( nextToken.isNominal() )
					{
						currToken.clearTags();
						if( currToken.lexeme.endsWith( "." ) )  {   // 3. kafli
							currToken.addAllTags( IceTag.tagAdjectivesSingular );
                        }
						else
							currToken.addAllTags( IceTag.tagCardinalsPlural );
					}
				}
			}
		}
	}

    
    private String phrasalVerbLookup( IceTokenTags verb, IceTokenTags adverb)
	{
		String value;
		String key = verb.lexeme + "_" + adverb.lexeme ;

		value = verbAdverbDictionary.lookup( key, true );
        return value;
	}


   private void phrasalVerbs()
	{
		IceTokenTags currToken, nextToken, nextToken2;
		String value;
		ArrayList tokens = myTokens;

        int lastIndex = tokens.size() - 1;
		for( int i = 0; i < lastIndex - 1; i++ )
		{
            currToken = (IceTokenTags)tokens.get( i );
			nextToken = (IceTokenTags)tokens.get( i + 1 );
			if( i < lastIndex - 2 )
				nextToken2 = (IceTokenTags)tokens.get( i + 2 );
			else
				nextToken2 = null;

			if( currToken.isVerbAny() && !currToken.isVerbBe() &&
                nextToken.isAdverb() && nextToken.isPreposition() &&
                nextToken2 != null && nextToken2.isNominal()
                )
			{
                value = phrasalVerbLookup( currToken, nextToken);
				if( value != null )
				{
                    if( logger != null )
				    	logger.log("Phrasal verb found, nominal: " + currToken.lexeme + "_" + nextToken.lexeme);
					nextToken.removeAllBut( IceTag.WordClass.wcAdverb );     // select the adverb
                }
            }

        }
	}

    /**
     * Forces agreement between prevToken and currToken using currToken as the correct one
     * @param prevToken Previous token
     * @param currToken Current token
     */
	public void forceAgreement( IceTokenTags prevToken, IceTokenTags currToken )
	{
		ArrayList prevTags = prevToken.getTags();
		ArrayList currTags = currToken.getTags();
		for( int i = 0; i < prevTags.size(); i++ )
		{
			IceTag prevTag = (IceTag)prevTags.get( i );
			if( !prevTag.genderNumberCaseMatch( currTags ) )
			{
				IceTag newTag = new IceTag( IceTag.tagNoun );
				newTag.setPersonGender( prevTag.getPersonGenderLetter() );
				newTag.setNumber( prevTag.getNumberLetter() );
				newTag.setCase( prevTag.getCaseLetter() );
                if (currToken.hasArticle())
                    newTag.addArticle();
				currToken.addTag( newTag );
				if( logger != null )
					logger.log( "ForceAgreement: Changed " + currToken.lexeme + " used: " + prevToken.lexeme );
			}
		}
	}

    /**
     * Checks tag assignment for unknown words tagged as nouns.
     */
	private void checkAssignment()
	{
		ArrayList tokens = myTokens;
		for( int i = 1; i < tokens.size(); i++ )
		{
			IceTokenTags prevTok = (IceTokenTags)tokens.get( i - 1 );
			IceTokenTags tok = (IceTokenTags)tokens.get( i );
			if( tok.isUnknown() && tok.isNoun() && prevTok.isOnlyWordClass( IceTag.WordClass.wcAdj ) && prevTok.numTags() == 1 )
			{
				tok.removeAllBut( IceTag.WordClass.wcNoun );
				forceAgreement( prevTok, tok );
			}
		}
	}

    /**
     * Performs disambiguation.
     */
	private void disambiguate()
	{
		ambiguator.setTokens( myTokens );
        if (!sameTagForAllNumbers)
		    processNumericConstants();
		phrases.findIdioms( myTokens );
		phrasalVerbs();

		ambiguator.disAmbiguateLocal();
		ambiguator.disAmbiguateGlobal();

        if (fullDisambiguation && (triTagger == null || modelType == HmmModelType.none))
        {
            for (int i=0; i<=myTokens.size()-1; i++)
            {
                IceTokenTags tok = (IceTokenTags)myTokens.get(i);
                tok.removeAllButFirstTag();
            }
        }
    }

    /**
     * Copies the IceTokenTags tokens into HmmTokenTags for TriTagger to process
     * @return An ArrayList
     */
    private ArrayList copyTokens()
    {
        IceTokenTags tok;
        HmmTokenTags newTok;
        IceTag tag;
        ArrayList tags;
        ArrayList wcTokens = new ArrayList();
        // Copy the tokens into a new ArrayList,
        for (int i=0; i<=myTokens.size()-1; i++)
        {
             tok = (IceTokenTags)myTokens.get(i);
             // Make a dummyToken and remove IceTagger specific marking
             IceTokenTags dummyToken = tok.makeCopy();
             dummyToken.cleanTags();

             newTok = new HmmTokenTags(dummyToken.lexeme, dummyToken.tokenCode);
             newTok.setUnknown(dummyToken.isUnknown());

            // Ending analysis in IceMorphy is not accurate.
            // Therefore, don't add tags for unknown words analysed by the endings component
            // This forces TriTagger to use suffix analysis for these words!

            if (!dummyToken.isUnknown() || (dummyToken.isUnknown() && dummyToken.isUnknownMorpho())) {
                tags = dummyToken.getTags();
                for (int j=0; j<=tags.size()-1; j++) {
                    tag = (IceTag)tags.get(j);
                    newTok.addTag(tag.getTagStr());

                }
             }
             //else
             //   System.out.println(dummyToken.lexeme + " " + dummyToken.allTagStrings());
             wcTokens.add(newTok);
        }
        return wcTokens;
    }

    /**
     * Tag using a trigram tagger.
     */
    private void tagWithTriTagger()
    {
        ArrayList copyOfTokens;
        HmmTokenTags wcTok;
        Tag wcTag;
        boolean invalidTags = false;


        copyOfTokens = copyTokens();
        // Tag using the trigram tagger.  Make TriTagger look up words that don't have a tag
        triTagger.tagTokens( copyOfTokens, true );
        // Now remove original tags not confirming to the proposed word class
        for (int i=0; i<=copyOfTokens.size()-1; i++)
        {
                wcTok = (HmmTokenTags)copyOfTokens.get(i);
                if (!wcTok.noTags()) {
                    wcTag = wcTok.getFirstTag();
                    Character wcLetter = wcTag.getFirstLetter();
                    IceTokenTags myTok = (IceTokenTags)myTokens.get(i);

                    // Remove tags from IceTagger not having the word class proposed by TriTagger
                    ArrayList myTags = myTok.getTags();
                    invalidTags = false;
                    for (int j=0; j<=myTags.size()-1; j++) {
                        IceTag myTag = (IceTag)myTags.get(j);
                        // Mark tag as invalid
                        if (wcLetter != myTag.getFirstLetter()) {
                            myTag.setValid(false);
                            invalidTags = true;
                        }
                    }
                    if (invalidTags)
                        myTok.removeInvalidTags();
                }
                //else
                //  System.err.println("No tag for: " + wcTok.lexeme);
        }
    }


    /**
     * Tags the supplied tokens.
     * @param tokens  The tokens
     */
    public void tagTokens( ArrayList tokens )
	{
        myTokens = tokens;
        if( baseTagging )
			dictionaryLookupBaseLine();
		else
		{
            dictionaryLookup();
			morpho.setTokens( myTokens );
			morpho.morphoAnalysis();
			checkAssignment();
            if( startWithHmmModel && triTagger != null ) {
                tagWithTriTagger();
            }
            disambiguate();
		}
		cleanTags();


        if( endWithHmmModel && triTagger != null )
        {
			enforceOneTagSVOVerb();        // We will assume that the first SVO verb is the correct one!
			triTagger.tagTokens( myTokens, false );
		}
	}

}
