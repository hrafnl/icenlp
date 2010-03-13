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
//import is.knowledge.lemmatizer.Lemmatizer;
//import is.knowledge.lemmatizer.LemmaResult;

import java.util.ArrayList;

/**
 * Heuristic methods for tagging Icelandic text.
 * <br>Based on the paper:
 * <br>Hrafn Loftsson. Tagging a morphologically complex language using heuristics. In T. Salakoski, F. Ginter, S. Pyysalo and T. Pahikkala (eds.), Advances in Natural Language Processing, 5th International Conference on NLP, FinTAL 2006, Proceedings. Turku, Finland.</li>
 * @author Hrafn Loftsson
 */
public class IceHeuristics
{
	private ArrayList tokens;
	private IceLog logger = null;  // Logfile file
    private Lexicon verbPrepLex;   // Lexicon for verb-prep cases
	private Lexicon verbObjLex;    // Lexicon for verb-obj cases
	private IceTokenTags dummyToken;
	private IceTag dummyTag;
	private IceTokenTags lastPluralSubject = null; // used for matching against pronouns "þeirra, þeim"
    //private Lemmatizer lemmatizer;

    public IceHeuristics( IceLog log, Lexicon verbPrepDict, Lexicon verbObjDict )
	{
		logger = log;
        verbPrepLex = verbPrepDict;
		verbObjLex = verbObjDict;
		dummyToken = new IceTokenTags( "", Token.TokenCode.tcNone );    // Dummy token
		dummyToken.lexeme = "dummyToken";
		dummyTag = new IceTag( "nken" );     // noun, masculine, singular, nominative
        //lemmatizer = new Lemmatizer("settings.txt");
    }


    public void setTokens( ArrayList sentence )
	{
		tokens = sentence;
	}

	public IceTokenTags getLastPluralSubject()
	{
		return lastPluralSubject;
	}

    /**
     * Disallows the given tag for the given token.
     * @param currToken  The current token
     * @param tag        The tag to remove
     */
	private void disAllowTag( IceTokenTags currToken, IceTag tag )
	{
		// Only disambiguate if more than one tag left
		if( currToken.numTags() > 1 )
		{
			tag.setValid( false );
			if( logger != null ) {
                String logStr = "SVO disambiguation: " + currToken.toString();
                logStr = logStr + " Disallowed " + tag.toString();
				logger.log( logStr );
            }
		}
	}

	public String verbPrepLookup( IceTokenTags verb, IceTokenTags prep )
	{
		String tagStr;
		String key = verb.lexeme + "_" + prep.lexeme;


        tagStr = verbPrepLex.lookup( key, true );
        /*if (tagStr == null) {
            String firstTag = verb.getFirstTagStr();
            LemmaResult lemma = lemmatizer.lemmatize(verb.lexeme,firstTag);
            String verbLemma = lemma.getLemma();
            verbLemma = fixLemma(verbLemma);
            tagStr = verbPrepLex.lookup( verbLemma, true );
        }*/

        return tagStr;
	}


    /*
    private String fixLemma(String lemma)
    {
        int index = lemma.indexOf('/'); // éta/eta
        if (index == -1)
            index = lemma.indexOf('$'); // sökkva$^1$
        if (index != -1) {
            lemma = lemma.substring(0,index);
        }
        return lemma;
    } */

    private String verbObjLookup( IceTokenTags verbToken )
	{
		String caseStr;
        caseStr = verbObjLex.lookup( verbToken.lexeme, true );
        /*if (caseStr == null) {
            String tagStr = verbToken.getFirstTagStr();
            LemmaResult lemma = lemmatizer.lemmatize(verbToken.lexeme,tagStr);
            String verbLemma = lemma.getLemma();
            verbLemma = fixLemma(verbLemma);
            caseStr = verbObjLex.lookup( verbLemma, true );
        }*/

        return caseStr;
	}

	/**
	 * Forces agreement between an adjective and a noun.
     * @param prevToken The previous token
     * @param nominal The nominal token
	 */
	public void forceAgreement( IceTokenTags prevToken, IceTokenTags nominal )
	{
		if( nominal.isOnlyWordClass( IceTag.WordClass.wcNoun ) && nominal.numTags() == 1 &&
		    prevToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
		{
			IceTag nominalTag = (IceTag)nominal.getFirstTag();
			IceTag tokenTag = (IceTag)prevToken.getFirstTag();

			if( !nominalTag.genderNumberCaseMatch( tokenTag ) )
			{
				tokenTag.setPersonGender( nominalTag.getPersonGenderLetter() );
				tokenTag.setNumber( nominalTag.getNumberLetter() );
				tokenTag.setCase( nominalTag.getCaseLetter() );
				if( logger != null )
					logger.log( "ForceAgreement: Changed " + prevToken.lexeme + " used: " + nominal.lexeme );
			}
		}
	}

    /**
     * Ambiguates adjectives.
     * @param from fromIndex
     * @param to   toIndex
     */
	public void ambiguateAdjectives( int from, int to )
	{
    // Since the morphological analyzer generates all possible adjective tags the accuracy of the tagger can be improved
	// by immediately removing inappropriate adjective tags when an adjective preceeds a noun
		IceTokenTags currToken, prevToken=null;

		for( int i = from; i <= to; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			// Remove wrong tags from adjectives.  Wrong tags might have been generated
			if( prevToken != null && !currToken.noTags() && !prevToken.noTags() &&
			    ( prevToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) || prevToken.isOnlyWordClass( IceTag.WordClass.wcIndefPronoun ) ||
			      prevToken.isOnlyWordClass( IceTag.WordClass.wcDemPronoun ) || prevToken.isOnlyWordClass( IceTag.WordClass.wcPossPronoun ) ) &&
			                                                                                                              currToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
				match( IceTokenTags.Match.genderNumberCase, prevToken, currToken );

			prevToken = currToken;
		}
	}

    /**
     * Checks for agreement between nominals
     * @param from fromIndex
     * @param to   toIndex
     */
	public void checkNominalAgreement( int from, int to )
	{
        // Makes sure there is an agreement in number, gender, case between noun/pronoun and the previous noun/adjectives/pronouns
		IceTokenTags  prevToken, nominal, nextToken, nextnextToken = null;
		// Check the words preceeding the noun/pers pronoun and make them agree with the noun/pronoun
		for( int i = to; i >= from; i-- )
		{
			nominal = (IceTokenTags)tokens.get( i );
			if( i > from )
				prevToken = (IceTokenTags)tokens.get( i - 1 );
			else
				prevToken = null;

			if( !nominal.isSVOMainVerb() && !nominal.isSVOVerb() &&
			    // DOn't look at genitive cases unless the previous token is also in genitive form
			    ( !( (IceTag)nominal.getFirstTag() ).isCase( IceTag.cGenitive ) || ( prevToken != null && prevToken.isCase( IceTag.cGenitive ) ) ) &&
			    ( ( (IceTag)nominal.getFirstTag() ).isNoun() || ( (IceTag)nominal.getFirstTag() ).isProperNoun() || ( (IceTag)nominal.getFirstTag() ).isAdjective() ||
			      nominal.isPersonalPronoun() || nominal.isIndefinitePronoun() || nominal.isPossessivePronoun() )
					)
			{
				boolean done = false;

				for( int j = i - 1; j >= from && !done; j-- )
				{
					prevToken = (IceTokenTags)tokens.get( j );
					// needs to cover phrases like "í þessu hreina og tæra lofti"

					if( prevToken.isSVOMainVerb() || prevToken.isSVOVerb() ||
					    ( prevToken.isOnlyWordClass( IceTag.WordClass.wcVerb ) && ( prevToken.isVerbSupine() ) ) ||
					    ( prevToken.isSVOPrepPhrase() && !nominal.isSVOPrepPhrase() ) )  // Don't step into a prepphrase)
						break;

					if( ( !nominal.isSVOPrepPhrase() || prevToken.isSVOPrepPhrase() ) &&   // not only one of them a prep phrase
					    (

							    prevToken.isAdjective() ||
							    prevToken.isOnlyWordClass( IceTag.WordClass.wcPossPronoun ) ||
							    ( prevToken.isOnlyWordClass( IceTag.WordClass.wcReflPronoun )
							    ) ||
							      ( prevToken.isOnlyWordClass( IceTag.WordClass.wcIndefPronoun ) || ( prevToken.isIndefinitePronoun() && prevToken.isAdjective() ) ) ||
							      ( prevToken.isOnlyWordClass( IceTag.WordClass.wcIntPronoun ) /*&& !prevToken.lexeme.equalsIgnoreCase("hvað")*/ ) || ( prevToken.isInterrogativePronoun() && prevToken.isIndefinitePronoun() ) ||
							      prevToken.isOnlyWordClass( IceTag.WordClass.wcDemPronoun ) || prevToken.isOnlyWordClass( IceTag.WordClass.wcArticle ) ||
							      //( prevToken.isArticle() && prevToken.isDemonstrativePronoun() ) || // hinum
                                  prevToken.isArticle()  || // hið                                        
                                  prevToken.isNumeral() ||
                                  ( ( prevToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) || prevToken.isOnlyWordClass( IceTag.WordClass.wcProperNoun ) ) && nominal.isPossessivePronoun() )
					    ) )
					{
						// Take special care of genitive case
						if( !nominal.isOnlyCase( IceTag.cGenitive ) || prevToken.isCase( IceTag.cGenitive ) )
						{
							match( IceTokenTags.Match.genderNumberCase, prevToken, nominal );
                            // Also try the other way around. 14.12.2008 /HL
                            match( IceTokenTags.Match.genderNumberCase, nominal, prevToken );
                        }
					}
					// löggu húfa
					else if( nominal.isNoun() && prevToken != null && prevToken.isNoun() &&
					         !nominal.isCase( IceTag.cGenitive ) &&
					         prevToken.isCase( IceTag.cGenitive ) )
						prevToken.removeAllButCase( IceTag.cGenitive );
						// landið allt
					else
					if( prevToken.isNoun() && !nominal.isNoun() && !nominal.isProperNoun() && !nominal.isPersonalPronoun()
					    && nominal.numTags() == 1 )
						match( IceTokenTags.Match.numberCase, prevToken, nominal );
						// 500 króna víxilláns
					else if( prevToken.isNoun() && nominal.isNoun() && nominal.isOnlyCase( IceTag.cGenitive ) )
						match( IceTokenTags.Match.numberCase, prevToken, nominal );
					else if( !prevToken.isQuote() ) //&& !prevToken.lexeme.equals("og")) // "nýja"
						done = true;
				}
			}


		}

		// Check the word after the noun/pronoun/adjective and make them agree with the previous word

		for( int i = from; i <= to; i++ )
		{
			IceTokenTags tok = (IceTokenTags)tokens.get( i );
			if( i < to )
			{
				nextToken = (IceTokenTags)tokens.get( i + 1 );
				if( i < to - 1 )
					nextnextToken = (IceTokenTags)tokens.get( i + 2 );

				if( !nextToken.isPreposition() && !nextToken.isSVOMainVerb() &&
				    !nextToken.isSVOVerb() )
				{
					if( tok.isOnlyWordClass( IceTag.WordClass.wcNoun ) || ( tok.isNoun() && tok.isAdjective() ) )
					{
						if( tok.lexeme.endsWith( "-" ) && nextToken.isConjunction() && nextnextToken != null && nextnextToken.isNoun() )  // vín- og fitublettir
							tok.setAllTags( nextnextToken.allTagStrings() );

						if( nextToken.isAdjective() || nextToken.isPossessivePronoun() || nextToken.isDemonstrativePronoun() || nextToken.isVerbPastPart() ) // hárið þykka, syni mínum, verslun þessi, börnin pínd
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
						else if( nextToken.isProperNoun() ) // frú Parker
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
					}
					else if( tok.isOnlyWordClass( IceTag.WordClass.wcProperNoun ) )
					{
						if( ( nextToken.isAdjective() || nextToken.isNoun() ) &&  // Ásgeirs gamla , Guðlaugur grjúpán
						    !tok.isSVOMainSubject() && !tok.isSVOSubject() )   // Gummi frekjuskjóða
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );   // Tjörnin kyrr
						else if( nextToken.isVerbPastPart() )
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );   // Sigga pínd
					}
					else if( tok.isOnlyWordClass( IceTag.WordClass.wcArticle ) )
					{
						if( nextToken.isAdjective() ) // sú eina sanna
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
					}

					else if( tok.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
					{
						if( nextToken.isNoun() || nextToken.isProperNoun() )
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
							// flestar þeirra
						else
						if( nextToken.isPersonalPronoun() || nextToken.isDemonstrativePronoun() ) //|| nextToken.isIndefinitePronoun())
							match( IceTokenTags.Match.personGenderNumber, nextToken, tok );
							// síðust allra
						else if( nextToken.isIndefinitePronoun() )
							match( IceTokenTags.Match.gender, nextToken, tok );
					}
					else if( tok.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) )
					{
						// sultan hennar ömmu
						if( nextToken.isReflexivePronoun() || nextToken.isNoun() || nextToken.isProperNoun() )
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
					}
					else if( tok.isPossessivePronoun() || tok.isDemonstrativePronoun() || tok.isIndefinitePronoun() )
					{
						if( nextToken.isNoun() || nextToken.isProperNoun() || nextToken.isAdjective() || nextToken.isNumeral() )
						{
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
						}
						if( tok.isOnlyWordClass( IceTag.WordClass.wcIndefPronoun ) && nextToken.isPersonalPronoun() )
							match( IceTokenTags.Match.gender, nextToken, tok );    // hvort þeirra
					}
					else if( tok.isOnlyWordClass( IceTag.WordClass.wcNumeral ) )
					{
						if( nextToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
						else if( nextToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
							match( IceTokenTags.Match.genderNumberCase, nextToken, tok );
					}
				}
			}
		}
	}

	private IceTokenTags getMatchedToken( IceTokenTags nextToken )
	{
		IceTokenTags matchTok = null;
		// Returns a token to be matched against a preposition.  If not found, return null
		// If next token has only one tag then use the corresponding case
		if( nextToken.numTags() == 1 )
			matchTok = nextToken;
			// If next token is only accusative or only dative
		else if( ( nextToken.isCase( IceTag.cAccusative ) && !nextToken.isCase( IceTag.cDative ) ) ||
		         ( nextToken.isCase( IceTag.cDative ) && !nextToken.isCase( IceTag.cAccusative ) ) )
			matchTok = nextToken;
		return matchTok;
	}


 /**
  * Makes sure the preposition has the correct case.
  * @param from fromIndex
  * @param to toIndex
  */
	public void checkCorrectPrep( int from, int to )
	{
		IceTokenTags prep, prevToken, nextToken, nextnextToken, matchTok;
        String tagStr;
		boolean found = false;

		for( int i = from; i < to; i++ )
		{
			matchTok = null;
			prep = (IceTokenTags)tokens.get( i );
			nextToken = (IceTokenTags)tokens.get( i + 1 );
			if( i < to - 1 )
				nextnextToken = (IceTokenTags)tokens.get( i + 2 );
			else
				nextnextToken = null;

			// If the prepostion has both accusative and dative case then we need to choose.  The choice depends on a verb found
			if( ( (IceTag)prep.getFirstTag() ).isPreposition() )
			{
				if( prep.numTags() > 1 )
				{
					if( logger != null )
						logger.log( "Correct prep: Removed all but prep " + prep.lexeme + ":" + prep.allTagStrings() );
					prep.removeAllBut( IceTag.WordClass.wcPrep );
				}
				if( prep.isCase( IceTag.cAccusative ) && prep.isCase( IceTag.cDative ) )
				{
					found = false;

					if( nextToken.isSVOPrepPhrase() )
						matchTok = getMatchedToken( nextToken );
						// eftir vel unnið verk
					else
					if( nextToken.isAdverb() && nextnextToken != null && nextnextToken.isSVOPrepPhrase() && !nextnextToken.isPreposition() )
						matchTok = getMatchedToken( nextnextToken );

					if( matchTok != null )
					{
						match( IceTokenTags.Match.aCase, prep, matchTok );
						if( logger != null )
							logger.log( "Correct prep1 " + matchTok.lexeme + ":" + matchTok.allTagStrings() + " " + prep.lexeme );
						found = true;
						break;
					}

					for( int j = i - 1; j >= from && i - j <= 5; j-- )
					{
						prevToken = (IceTokenTags)tokens.get( j );
						if( prevToken.isSVOPrepPhrase() )  // Don't look for verb if a prepphrase is in between
						{
							found = false;
							break;
						}

						if( ( prevToken.isVerb() && !prevToken.isVerbPresentPart() ) || prevToken.isVerbPastPart() || prevToken.isVerbInfinitive() )
						{
							tagStr = verbPrepLookup( prevToken, prep );
							if( tagStr != null )
							{
								found = true;
								IceTag correctTag = new IceTag( tagStr );
								dummyToken.setTag( correctTag );
								match( IceTokenTags.Match.aCase, prep, dummyToken );
								if( logger != null )
									logger.log( "Correct prep2 " + prevToken.lexeme + " " + prep.lexeme + ":" + tagStr );
							}
							else
								found = false;
                            break;      // break once a verb was found
						}

					}
					if( !found )
					{
						// Check next two words.  If they have only one tag then use the corresponding case for the prep
						for( int j = i + 1; j <= i + 2 && j <= to; j++ )
						{
							nextToken = (IceTokenTags)tokens.get( j );
							if( nextToken.isSVOPrepPhrase() && !nextToken.isPreposition() ) //&& nextToken.numTags() == 1)
							{
                                if( ( nextToken.isCase( IceTag.cAccusative ) && !nextToken.isCase( IceTag.cDative ) ) ||
								    ( nextToken.isCase( IceTag.cDative ) && !nextToken.isCase( IceTag.cAccusative ) ) )
								{
									match( IceTokenTags.Match.aCase, prep, nextToken );
									if( logger != null )
										logger.log( "Correct prep3 " + nextToken.lexeme + ":" + nextToken.allTagStrings() + " " + prep.lexeme );
									found = true;
									break;
								}
							}
						}
						if( !found )
						{
							if( logger != null )
								logger.log( "Correct prep4: Removed all but first " + nextToken.lexeme + ":" + nextToken.allTagStrings() + " " + prep.lexeme );
                            prep.removeAllButFirstTag();    // Then use the first tag
						}
					}
				}
				if( !found )
					prep.removeAllButFirstTag();
			}
		}
	}


	private boolean isEnumeration( IceTokenTags tok )
	{
		return ( tok.lexeme.equals( "," ) || tok.lexeme.equals( "og" ) ||
		         tok.lexeme.equals( "en" ) || tok.lexeme.equals( "eða" ) ); // upptalning
	}

    /**
     * Checks if the tokens from index idx comprise a subject noun phrase, like '[allar upplýsingar] lágu ...."
     * @param idx   fromIndex
     * @param to    toIndex
     * @return      boolean
     */
	private boolean isSubjectNounPhrase( int idx, int to )
	{
		IceTokenTags nextToken = null, nextnextToken;
		boolean found = false;
		int idxFound = -1;
		for( int i = idx; i <= to; i++ )
		{
			nextToken = (IceTokenTags)tokens.get( i );
			if( !( nextToken.isNominal() && nextToken.isCase( IceTag.cNominative ) ) )
				break;
			else if( ( nextToken.isNoun() || nextToken.isProperNoun() ) && nextToken.isCase( IceTag.cNominative ) )
			{
				found = true;
				idxFound = i;
				break;
			}
		}
		if( found && idxFound < to )
		{
			nextnextToken = (IceTokenTags)tokens.get( idxFound + 1 );
			// Make sure next token is a verb and an agreement exists
			if( !( nextnextToken.isOnlyVerbAny() && nextToken.personNumberMatch( nextnextToken ) ) )
				found = false;
			else
			{
				if( logger != null )
					logger.log( "SubjectNounPhrase: " + nextToken.lexeme + " " + nextnextToken.lexeme );
			}
		}
		else
			found = false;
		return found;
	}

    /**
     * Marks a prepositional phrase.
     * @param from fromIndex
     * @param to   toIndex
     */
	private void markPrepPhrase( int from, int to )
	{
		IceTokenTags prevToken = null, nextToken, nextnextToken;

		for( int i = from; i <= to; i++ )
		{
			IceTokenTags prep = (IceTokenTags)tokens.get( i );
			if( ( (IceTag)prep.getFirstTag() ).isPreposition() && i < to )
			{
				prep.setSVOMark( IceTokenTags.SVOMark.svoPrepPhrase );
				prep.removeAllBut( IceTag.WordClass.wcPrep );
				//char caseLetter = prep.getFirstTag().getCaseLetter();
				boolean done = false;
				for( int j = i + 1; j <= to && !done; j++ )
				{
					nextToken = (IceTokenTags)tokens.get( j );
					if( j < to )
						nextnextToken = (IceTokenTags)tokens.get( j + 1 );
					else
						nextnextToken = null;

					if( ( nextToken.isNominal() &&
					      ( prep.caseMatch( nextToken ) ||
					        ( nextnextToken != null && nextnextToken.isNoun() && prep.caseMatch( nextnextToken ) ) ) )
					    // eftir vel unnið starf
					    ||
					    ( nextToken.isAdverb() && nextnextToken != null && nextnextToken.isAdjective() &&
					      prep.caseMatch( nextnextToken ) ) )
					{
						// Quit if the token is a potential subject of the following verb
						// Við fórum svartar götur í (ao) vesturátt og LÍKIN HRÖNNUÐUST ...
						if( prevToken != null && isEnumeration( prevToken ) && isSubjectNounPhrase( j, to ) )
							done = true;
						else
						{
							nextToken.setSVOMark( IceTokenTags.SVOMark.svoPrepPhrase );

							if( ( ( !nextToken.isNoun() ||
							        // Don't quit if the nextnext token is a noun in the genitive and the prep is a genitive,
							        // "auk almennrar starfsemi sjóðakerfis"
							        ( nextnextToken != null && nextnextToken.isNoun() && nextnextToken.isCase( IceTag.cGenitive ) && prep.isCase( IceTag.cGenitive ) ) ) &&
							                                                                                                                                             ( !nextToken.isProperNoun() || ( nextnextToken != null && nextnextToken.isProperNoun() ) ) )
							    ||
							    ( nextnextToken != null &&
							      ( nextnextToken.lexeme.equals( "," ) || nextnextToken.lexeme.equals( "og" ) ||
							        nextnextToken.lexeme.equals( "eða" ) || nextnextToken.lexeme.equals( "en" ) ||
							        nextnextToken.isPossessivePronoun() ||
							        nextnextToken.isArticle() || // í annað sinn HIÐ sama
							        nextnextToken.isNumeral() )   // í öll skiptin þrjú
							    ) )
								done = false;
							else
								done = true;
						}
					}
					else if( !( nextToken.isOnlyWordClass( IceTag.WordClass.wcAdverb ) ||
					            //nextToken.lexeme.equals(",") || nextToken.lexeme.equals("og") || nextToken.lexeme.equals("en") || nextToken.lexeme.equals("eða") ||     // upptalning
					            isEnumeration( nextToken ) ||
					            nextToken.isQuote() ) )
						done = true;
					else
					if( prevToken != null && prevToken.isOnlyWordClass( IceTag.WordClass.wcAdverb ) && !nextToken.isNominal() )
						done = true;

					prevToken = nextToken;
				}
			}
		}
	}

    /**
     * Makes sure there is an agreement in case between preposition and the following nominals.
     * @param from fromIndex
     * @param to   toIndex
     */
	public void checkPrepAgreement( int from, int to )
	{
		IceTokenTags nextToken, prevToken = null, prevNominal, subj;

		// Check the words after the preposition
		for( int i = from; i <= to; i++ )
		{
			IceTokenTags prep = (IceTokenTags)tokens.get( i );

			if( ( (IceTag)prep.getFirstTag() ).isPreposition() && i < to )
			{
				boolean done = false;
				int nearestSubjectIndex = getNearestSubject( i );

				for( int j = i + 1; j <= to && !done; j++ )
				{
					nextToken = (IceTokenTags)tokens.get( j );
					if( !nextToken.isOnlyWordClass( IceTag.WordClass.wcAdverb ) )        // fyrir (ao) framan (aa) tvær gamlar konur
					{

						if( nextToken.isNominal() && nextToken.isSVOPrepPhrase() )
						{
							nextToken.removeVerbs();
							nextToken.removeWordClass( IceTag.WordClass.wcAdverb );   // Cannot be an adverb

							if( !prep.isCase( IceTag.cGenitive ) && nextToken.isOnlyCase( IceTag.cGenitive ) &&
							    prevToken != null && !prevToken.isPersonalPronoun() && prevToken.tokenCode != Token.TokenCode.tcComma )
								done = true;              // because of "þreif í öxl (þf) litla (ef) bróður (ef)
								// but ok is "þreif í hana Önnu"
							else
							{
								match( IceTokenTags.Match.aCase, nextToken, prep );
								nextToken.removeCase( IceTag.cNominative, false ); // a preposition can not govern nominative case
								// Match with nearest subject
								// hvarlaði að þeim (hvaða kyn er á þeim?)
								if( nextToken.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) && nextToken.hasGender() && nearestSubjectIndex >= 0 )
								{
									subj = (IceTokenTags)tokens.get( nearestSubjectIndex );
									match( IceTokenTags.Match.personGenderNumber, nextToken, subj );
								}
							}
							prevNominal = nextToken;
						}
						else if( !( nextToken.tokenCode == Token.TokenCode.tcComma || nextToken.lexeme.equalsIgnoreCase( "og" ) ||
						            nextToken.lexeme.equalsIgnoreCase( "eða" ) || nextToken.lexeme.equalsIgnoreCase( "en" ) ) )
							done = true;
					}
					prevToken = nextToken;
				}
			}
		}
	}

    /**
     * Marks the main verb of the sentence.
     * @param from fromIndex
     * @param to   toIndex
     * @return  the index of the verb
     */
	private int markMainVerb( int from, int to )
	{
		IceTokenTags currToken, nextToken;
		int verbIndex = -1;
		int rounds = 0;
		boolean okNoun = false;

		while( verbIndex < 0 && rounds < 2 )     // Loop twice if the first round does not produce a verb
		{
			rounds++;
			for( int i = from; i <= to; i++ )
			{
				currToken = (IceTokenTags)tokens.get( i );
				if( i < to )
					nextToken = (IceTokenTags)tokens.get( i + 1 );
				else
					nextToken = null;

				if( ( okNoun || ( !( (IceTag)currToken.getFirstTag() ).isNoun() && !( (IceTag)currToken.getFirstTag() ).isAdjective() ) ) &&
				    ( ( ( (IceTag)currToken.getFirstTag() ).isVerb() && !( (IceTag)currToken.getFirstTag() ).isVerbPresentPart() ) ||
				      ( (IceTag)currToken.getFirstTag() ).isVerbInfinitive() ) )
				{
					if( nextToken != null && nextToken.tokenCode == Token.TokenCode.tcEOS && !currToken.isOnlyVerbAny() )    // Don't allow last word to be main verb
						break;
					else
					{
						currToken.setSVOMark( IceTokenTags.SVOMark.svoMainVerb );
						currToken.removeAllButVerbs( false );
						verbIndex = i;
						break;
					}
				}
			}

			if( verbIndex == -1 )        // No verb found then try to find verbs allowing nouns and adjectives in the tags
				okNoun = true;
		}

		return verbIndex;
	}

    /**
     * Marks other verbs in the sentence.
     * @param from fromIndex
     * @param to   toIndex
     * @param mainSubjIndex the index of the main subject
     */
	private void markOtherVerbs( int from, int to, int mainSubjIndex )
	{
		IceTokenTags currToken, prevToken = null;
		for( int i = from; i <= to; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );

			if( !currToken.isSVOMainVerb() &&
			    ( ( currToken.isOnlyVerbAny() && !currToken.isVerbPresentPart() ) ||
			      ( ( ( ( (IceTag)currToken.getFirstTag() ).isVerb() && !( (IceTag)currToken.getFirstTag() ).isVerbPresentPart() ) || ( (IceTag)currToken.getFirstTag() ).isVerbInfinitive() ) &&
			        prevToken != null && ( prevToken.isNoun() || prevToken.isProperNoun() || prevToken.isPersonalPronoun() || prevToken.isConjunction() ) )
			    ) )

			{
				if( !currToken.isOnlyWordClass( IceTag.WordClass.wcVerbPastPart ) )
				{
					currToken.setSVOMark( IceTokenTags.SVOMark.svoVerb );
					currToken.removeAllButVerbs( false );
				}
				// Þau voru komin (komin is here an object)
				else if( prevToken != null && !prevToken.isVerbBe() )
				{
					currToken.setSVOMark( IceTokenTags.SVOMark.svoVerb );
					currToken.removeAllButVerbs( false );
				}
			}
			prevToken = currToken;
		}
	}

    /**
     * Forces an agreement between the subject and the verb
     * @param subj The subject
     * @param verb The verb
     * @return true if an agreement is found
     */
	private boolean subjectVerbMatch( IceTokenTags subj, IceTokenTags verb )
	{
		ArrayList tags = subj.getTags();
		// The verb token might have some other tags than verb tags
		dummyToken.setAllTags( verb.allTagStrings() );
		dummyToken.removeAllButVerbs( false );
		for( int i = 0; i < tags.size(); i++ )
		{
			IceTag tag = (IceTag)tags.get( i );
			if( tag.personGenderNumberMatch( dummyToken.getTags() ) )
				return true;
		}
		return false;
	}

    /**
     * Checks if a given token can be the subject of a sentence.
     * @param tok The token
     * @param verb The verb
     * @return boolean
     */
	private boolean isSubject( IceTokenTags tok, IceTokenTags verb )
	{
		if( !tok.isSVOPrepPhrase() && !tok.isSVOObject() &&
		    ( !( (IceTag)tok.getFirstTag() ).isWordClass( IceTag.WordClass.wcVerbPastPart ) &&
		      ( tok.isNoun() || tok.isProperNoun() || ( tok.isPronoun() && !tok.isReflexivePronoun() ) || tok.isInterrogativePronoun() )
		      && ( subjectVerbMatch( tok, verb ) )

		      // Either in nominative case or a special verb which accepts accusative/dative subjects
		      && ( ( tok.isCase( IceTag.cNominative ) && !verb.isVerbCaseMarking() ) ||
		           ( verb.isVerbCaseMarking() && verb.caseMatch( tok ) ) )
		    ) )
			return true;
		else
			return false;
	}

    /**
     * Marks a subject candidate given the lack of a verb.
     * @param from  fromIndex
     * @param to    toIndex
     * @return      the index of the subject
     */
	private int markSubjectNoVerb( int from, int to )
	{
		IceTokenTags prevToken = null, currToken, nextToken = null;
		int subjIndex = -1;

		// Drengurinn sem ... ,  Þessi sjúkdómur
		for( int i = from; i <= to; i++ )
		{
			if( i > from )
				prevToken = (IceTokenTags)tokens.get( i - 1 );
			currToken = (IceTokenTags)tokens.get( i );
			if( i + 1 <= to )
				nextToken = (IceTokenTags)tokens.get( i + 1 );

			if( prevToken != null && prevToken.tokenCode == Token.TokenCode.tcComma && nextToken != null && nextToken.tokenCode == Token.TokenCode.tcComma )
				break;

			if( currToken.getSVOMark() == IceTokenTags.SVOMark.svoNone && currToken.isNominal() )
			{
				if( ( nextToken == null || nextToken.isRelativeConjunction() ) || // Drengurinn sem ...
				    ( currToken.isCase( IceTag.cNominative ) ) )
				{
					if( ( currToken.isNoun() || currToken.isProperNoun() ||
					      currToken.isInterrogativePronoun() || currToken.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) ) ||
					     ( nextToken != null && !nextToken.isNoun() && !nextToken.isProperNoun() ) )
					{
						currToken.setSVOMark( IceTokenTags.SVOMark.svoMainSubject );
						subjIndex = i;
						break;
					}
				}
			}
		}
		return subjIndex;
	}

    /**
     * Tries to find a subject to the right of the given verb.
     * @param verbIndex  The index of the verb
     * @param subjType   The type of the subject
     * @return The index of the subject
     */
	private int nextTokenSubject( int verbIndex, IceTokenTags.SVOMark subjType )
	{
		int subjIndex = -1;
		IceTokenTags prevToken = null, verb, tokAfterVerb = null;
		// Check if the next token could be the subject
		verb = (IceTokenTags)tokens.get( verbIndex );
		if( verbIndex > 0 )
			prevToken = (IceTokenTags)tokens.get( verbIndex - 1 );
		if( verbIndex < tokens.size() - 1 )
			tokAfterVerb = (IceTokenTags)tokens.get( verbIndex + 1 );

		if( verbIndex > 0 && tokAfterVerb != null && prevToken != null )
		{
			if( !( verb.isVerbBe() && prevToken.isNominal() && prevToken.isCase( IceTag.cNominative ) ) &&
			    ( !prevToken.isNominal() || !prevToken.isOnlyCase( IceTag.cNominative ) ) &&
			    tokAfterVerb.getSVOMark() == IceTokenTags.SVOMark.svoNone &&
			    isSubject( tokAfterVerb, verb ) &&
			    ( tokAfterVerb.isOnlyCase( IceTag.cNominative ) ) )
			{
				tokAfterVerb.setSVOMark( subjType );
				subjIndex = verbIndex + 1;
				if( ( tokAfterVerb.isNoun() || tokAfterVerb.isProperNoun() ) && verbIndex < tokens.size() - 2 )
				{
					IceTokenTags next = (IceTokenTags)tokens.get( verbIndex + 2 );
					if( next.isPossessivePronoun() || next.isDemonstrativePronoun() )    // maðurinn minn, draumur þessi
						next.setSVOMark( subjType );
				}
			}
		}
		return subjIndex;
	}

	private void checkPluralSubject( IceTokenTags currToken )
	{
		if( ( currToken.isNoun() || currToken.isProperNoun() || currToken.isPersonalPronoun() ) && currToken.isOnlyNumber( IceTag.cPlural ) )
			lastPluralSubject = currToken;
	}

    /**
     * Marks a subject.
     * @param from fromIndex
     * @param verbIndex index of the verb
     * @param subjType  type of the subject
     * @return index of the subject
     */
	private int markSubject( int from, int verbIndex, IceTokenTags.SVOMark subjType )
	{
        // Tries to find a subject in the current phrase.
        // If not found then try previous phrase.
		IceTokenTags currToken,prevToken,verb,tokAfterVerb = null;
		boolean relFound = false, conjFound = false;
		int subjIndex = -1;

		verb = (IceTokenTags)tokens.get( verbIndex );
		if( verbIndex < tokens.size() - 1 )
			tokAfterVerb = (IceTokenTags)tokens.get( verbIndex + 1 );

		subjIndex = nextTokenSubject( verbIndex, subjType );
		if( subjIndex >= 0 )
			return subjIndex;

		// Search for subject
		for( int i = verbIndex - 1; i >= from; i-- )
		{
			currToken = (IceTokenTags)tokens.get( i );

			if( i > from )
				prevToken = (IceTokenTags)tokens.get( i - 1 );
			else
				prevToken = null;

			if( currToken.isRelativeConjunction() )      // sem átti - then no subject in this phrase
			{
				relFound = true;
				break;
			}
			if( i == verbIndex - 1 )
			{
				if( currToken.isConjunction() && currToken.lexeme.equalsIgnoreCase( "og" ) )      // og missti - then subject is not in the following word
				{
					conjFound = true;
					break;
				}
			}

			// Special case like "Það átti hann" here "hann" is the subject
			//if( !( tokAfterVerb != null && currToken.lexeme.equalsIgnoreCase( "það" ) && !tokAfterVerb.lexeme.equalsIgnoreCase( "það" ) && isSubject( tokAfterVerb, verb ) ) )
            if( !( tokAfterVerb != null && currToken.lexeme.equalsIgnoreCase( "það" ) && isSubject( tokAfterVerb, verb ) ) )
			{
				if( currToken.getSVOMark() == IceTokenTags.SVOMark.svoNone && !currToken.lexeme.equalsIgnoreCase( "hvað" ) && isSubject( currToken, verb ) )
				{

					if( currToken.isNoun() || currToken.isProperNoun() || currToken.isInterrogativePronoun() || currToken.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) || prevToken == null ||
					    ( !prevToken.isNoun() && !prevToken.isProperNoun() ) || !isSubject( prevToken, verb ) )
					{
						currToken.setSVOMark( subjType );
						subjIndex = i;
						checkPluralSubject( currToken );
						break;
					}
				}
			}
		}

		if( subjIndex < 0 )
		{
			if( !relFound && !conjFound )
			{
				subjIndex = markSubjectToRight( verbIndex, subjType ); // Check for subject directly to the right of the verb
			}
			if( subjIndex < 0 )  // If still not found then try previous phrase
				subjIndex = getSubjectPreviousPhrase( verbIndex, subjType );
		}

		if( subjIndex >= 0 )     // Remove all verb tags from subjects
		{
			currToken = (IceTokenTags)tokens.get( subjIndex );
			currToken.removeVerbs();
		}
		return subjIndex;
	}

    /**
     * Marks a subject to the right of a verb.
     * @param verbIndex The index of the verb
     * @param subjType  The type of the subject
     * @return the index of the subject
     */
	private int markSubjectToRight( int verbIndex, IceTokenTags.SVOMark subjType )
	{
		IceTokenTags currToken, prevToken = null, nextToken = null, beforeVerb;

		int subjIndex = -1;
		int distance = 2;
		int last = tokens.size() - 1;

		IceTokenTags verb = (IceTokenTags)tokens.get( verbIndex );

		// Immediately return if the previous word is a relative conjunction
		if( verbIndex > 0 )
		{
			beforeVerb = (IceTokenTags)tokens.get( verbIndex - 1 );
			if( beforeVerb.isRelativeConjunction() )
				return subjIndex;
		}
		for( int i = verbIndex + 1; i <= last && i <= verbIndex + distance; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( i < last )
				nextToken = (IceTokenTags)tokens.get( i + 1 );

			if( currToken.isSVOPrepPhrase() || currToken.isOnlyWordClass( IceTag.WordClass.wcAdverb ) )  // Don't look for subject inside a prepositional phrase
				break;

			if( isSubject( currToken, verb ) )
			{
				currToken.setSVOMark( subjType );
				subjIndex = i;
				if( prevToken != null && prevToken.isAdjective() )    // heyrðist tryllt garg
					prevToken.setSVOMark( subjType );
				// það vissu allar stelpur hver    , stelpur is SUBJECT
				// spurði sonur minn (sonur og minn are subjects), draumur þessi
				if( !currToken.isIndefinitePronoun() && ( nextToken == null || !nextToken.isPossessivePronoun() || !nextToken.isDemonstrativePronoun() ) )
					break;
			}
			else
			{
				// svaraði tónlaus rödd  , here rödd is the subject
				if( ( currToken.isNominal() && !currToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) ) ) //|| currToken.isPunctuation())
					break;
			}
			prevToken = currToken;
		}
		return subjIndex;
	}

    /**
     * Mark an object.
     * @param verbIndex The index of the verb
     * @param to        toIndex
     * @return          the index of the object
     */
	private int markObject( int verbIndex, int to )
	{
		IceTokenTags verb = null, prevToken = null, currToken, nextToken;
		int saveIndex = -1;
		boolean objFound = false;

		verb = (IceTokenTags)tokens.get( verbIndex );
		// Search for object
		for( int i = verbIndex + 1; i <= to; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( i < to )
				nextToken = (IceTokenTags)tokens.get( i + 1 );
			else
				nextToken = null;

			if( currToken.isSVOPrepPhrase() ||   // Don't look for object inside a prepositional phrase
			    ( (IceTag)currToken.getFirstTag() ).isRelativeConjunction() ||
			    ( currToken.isConjunction() && currToken.lexeme.equals( "að" ) ) ||
			    ( !objFound && ( (IceTag)currToken.getFirstTag() ).isConjunction() ) ||
			    // að trufla bæjarbúa (no, object), espandi (lo)
			    ( objFound && prevToken != null && prevToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) &&
			      currToken.isPunctuation() && nextToken != null && !( (IceTag)nextToken.getFirstTag() ).isNoun() )
					)
				break;

			if(
					currToken.getSVOMark() == IceTokenTags.SVOMark.svoNone &&
					( currToken.isNominal() || currToken.isOnlyWordClass( IceTag.WordClass.wcVerbPastPart ) ) &&
					!currToken.isVerbInfinitive()
					)
			{
				// Þeir borðuðu fallega: fallega is not an object
                if( !currToken.isOnlyCase( IceTag.cGenitive ) && !( currToken.lexeme.endsWith( "lega" ) && currToken.isAdverb() )
                  )
				{
					saveIndex = i;
					if( currToken.isPersonalPronoun() || nextToken == null || !nextToken.isNominal() ) // Don't mark if the next token is a nominal
						currToken.setSVOMark( IceTokenTags.SVOMark.svoObject );
					objFound = true;

					if( nextToken != null && !nextToken.isPunctuation() && !nextToken.isConjunction() &&
					    ( !nextToken.isOnlyWordClass( IceTag.WordClass.wcAdverb ) || nextToken.lexeme.equals( "hversu" ) )
							)
					{
						if( ( currToken.isOnlyWordClass( IceTag.WordClass.wcNoun )
						      && !nextToken.isOnlyWordClass( IceTag.WordClass.wcPossPronoun ) ) ||
						                                                              ( currToken.isOnlyWordClass( IceTag.WordClass.wcReflPronoun ) && ( currToken.lexeme.equals( "sig" ) || currToken.lexeme.equals( "sér" ) ) ) )
							// main object found
							break;
						else
						{
							if( // break if not a case match with next token
									( nextToken == null || !currToken.caseMatch( nextToken ) ) )  // break if not a case match with next token
								break;
						}
					}
				}
			}
			else if( !currToken.isAdverb() && !currToken.isSVOMainSubject() && !currToken.isSVOSubject()
			         && !currToken.lexeme.equals( "orðinn" ) &&
			         !( currToken.isConjunction() ) &&
			         !currToken.isPunctuation() )
				break;

			prevToken = currToken;
		}

		if( objFound && saveIndex > -1 )
		{
			currToken = (IceTokenTags)tokens.get( saveIndex );
			currToken.setSVOMark( IceTokenTags.SVOMark.svoObject );
		}
		return saveIndex;
	}

    /**
     * Marks an object to the left of a verb.
     * @param from  fromIndex
     * @param verbIndex  the index of the verb
     * @return  the index of the object
     */
	private int markObjectToLeft( int from, int verbIndex )
	{
		IceTokenTags prevToken = null, currToken;
		int objIndex = -1;

		// Search for object to the left
		for( int i = verbIndex - 1; i >= from; i-- )
		{
			currToken = (IceTokenTags)tokens.get( i );
            // If subject found, then quit
			if( currToken.getSVOMark() != IceTokenTags.SVOMark.svoNone )
				break;

			if( i > from )
				prevToken = (IceTokenTags)tokens.get( i - 1 );

			if( prevToken == null || !prevToken.isOnlyWordClass( IceTag.WordClass.wcPrep ) )
			{
				// Make sure object is not in genitive case
				if( ( currToken.isNoun() || currToken.isProperNoun() || currToken.isPersonalPronoun() ||
				      currToken.isDemonstrativePronoun() || currToken.isIndefinitePronoun() || currToken.isInterrogativePronoun() ||
				      ( currToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) ) ) &&   // only allow adjectives when searching for objects
				                                                      !currToken.isCase( IceTag.cGenitive )
						)
				{
					currToken.setSVOMark( IceTokenTags.SVOMark.svoObject );
					objIndex = i;
					break;
				}
			}
		}

		return objIndex;
	}



	private void removeInvalidObjects( int from, int to )
	{
        // Verbs can not be objects
		IceTokenTags currToken, prevToken = null;

		for( int i = from; i <= to; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( currToken.isSVOObject() &&
			    ( ( currToken.isVerb() && !currToken.isVerbPresentPart() && !currToken.isVerbSupine() ) ||
			      currToken.isVerbInfinitive()
			    ) )
			{
				if( logger != null )
					logger.log( "Object invalid: Verbs removed: " + currToken.lexeme + " , tag was: " + currToken.allTagStrings() );
				currToken.removeWordClass( IceTag.WordClass.wcVerb );
				currToken.removeWordClass( IceTag.WordClass.wcVerbInf );

			}
			prevToken = currToken;
		}
	}


	private void removeInvalidSubjects( int from, int to )
	{
        // Verbs can not be subjects
		IceTokenTags currToken;

		for( int i = from; i <= to; i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( currToken.isSVOMainSubject() || currToken.isSVOSubject() )
			{
				if( logger != null )
					logger.log( "Subject invalid: Verbs removed: " + currToken.lexeme + " , tag was: " + currToken.allTagStrings() );
				currToken.removeVerbs();
			}
		}
	}

    /**
     * Performs agreement match between two tokens
     * @param type   The type of agreement
     * @param tokenToCheck   The token whose tags will possibly be removed
     * @param tokenToMatch   The token for which tokenToCheck will be matched against
     * @return true if a match exists
     */
    public boolean match( IceTokenTags.Match type, IceTokenTags tokenToCheck, IceTokenTags tokenToMatch )
	{
		boolean matchExists = true;
		if( !tokenToCheck.isUnknownGuessed() && tokenToCheck.numTags() == 1 && tokenToMatch.numTags() > 1 )      // Then switch the order
			match( type, tokenToMatch, tokenToCheck );
        else
		{
			boolean removeGNC, remove, didRemove = false;
			ArrayList tags = tokenToCheck.getTags();

			// Check to see if a possible match exists
			switch( type )
			{
				case personGenderNumber:

					matchExists = ( tokenToCheck.personNumberMatch( tokenToMatch ) );
					if( !matchExists )
					{
						// sfg3fn_sng
						IceTag verbTag = (IceTag)tokenToCheck.getFirstTag();
						if( verbTag.isVerb() && verbTag.isNumber( IceTag.cPlural ) && verbTag.isGender( IceTag.cThirdPerson ) && tokenToCheck.isVerbInfinitive() )
							matchExists = true;
					}
					break;
				case genderNumberCase:
					matchExists = ( tokenToCheck.genderNumberCaseMatch( tokenToMatch ) );
					break;
				case aCase:
					matchExists = ( tokenToCheck.caseMatch( tokenToMatch ) );
					break;
				case numberCase:
					matchExists = ( tokenToCheck.numberCaseMatch( tokenToMatch ) );
					break;
				case number:
					matchExists = ( tokenToCheck.numberMatch( tokenToMatch ) );
					break;
				case gender:
					matchExists = ( tokenToCheck.genderMatch( tokenToMatch ) );
					break;
			}

			if( matchExists )
			{
				for( int i = 0; i < tags.size(); i++ )
				{
					removeGNC = false;
					remove = false;
					IceTag tag = (IceTag)tags.get( i );
					if( !tag.isVerbSupine() && !tag.isAdverb() )
					{
						switch( type )
						{
							case personGenderNumber:
								remove = !tag.personGenderNumberMatch( tokenToMatch.getTags() );
								break;
							case genderNumberCase:
								if( !tag.isVerbIndicativeForm() && !tag.isVerbSubjunctiveForm() )  // Don't touch verb tags
								{
									removeGNC = !tag.genderNumberCaseMatch( tokenToMatch.getTags() );
								}
								break;
							case numberCase:
								remove = !tag.numberCaseMatch( tokenToMatch.getTags() );
								break;
							case aCase:
								remove = !tag.caseMatch( tokenToMatch.getTags() );
								break;
							case number:
								remove = !tag.numberMatch( tokenToMatch.getTags() );
								break;
							case gender:
								remove = !tag.personGenderMatch( tokenToMatch.getTags() );
								break;
						}
						if( removeGNC )
						{
							// Don't remove if it results in loosing all nominal tags
							if( tokenToCheck.numNominals() > 1 )
							{
								disAllowTag( tokenToCheck, tag );
								didRemove = true;
							}
						}
						else if( remove )
						{
							disAllowTag( tokenToCheck, tag );
							didRemove = true;
						}
					}
				}
				tokenToCheck.removeInvalidTags();

				if( didRemove )
				{
					if( logger != null )
						logger.log( "SVO Disambiguation: Matched " + tokenToCheck.lexeme + " and " + tokenToMatch.lexeme );
				}
			}
		}
		return matchExists;
	}

    /**
     * Checks agreement between other verbs (than main verbs) and direct objects
     * @param from fromIndex
     * @param to   toIndex
     */
	private void checkOtherVerbObjectAgreement( int from, int to )
	{
		int objIndex = -1;
		IceTokenTags verb;
		boolean found = false;
		// Loop twice if no SVOVerb found first time then search for SVOMainVerb
		//for (int k=1; k<=2 && !found; k++ )
		for( int k = 1; k <= 1 && !found; k++ )
		{
			for( int i = from; i <= to; i++ )
			{
				verb = (IceTokenTags)tokens.get( i );
				if( ( k == 1 && verb.isSVOVerb() ) || ( k == 2 && verb.isSVOMainVerb() ) )
				{
					found = true;
					objIndex = markObject( i, to );
					if( objIndex > 0 )
						checkVerbObjectAgreement( i, objIndex );
				}
			}
		}
	}

	private void defaultObjectCase( IceTokenTags verb, IceTokenTags obj )
	{
        obj.removeCase( IceTag.cNominative, false );
		if( logger != null )
			logger.log( "SVO Verb Object: Nominative removed: " + verb.lexeme + " " + obj.lexeme + " , tag was: " + obj.allTagStrings() );
	}

    /**
     * Enforces an agreement between a verb and an object
     * @param verb  The verb
     * @param obj   The object
     * @return      boolean
     */
	private boolean verbObjectMatch( IceTokenTags verb, IceTokenTags obj )
	{
		boolean found = false, matchExists = false;
		String caseStr;

        if( obj.lexeme.endsWith( "andi" ))    // horfi hlæjandi, 14.12.2008 /HL
            return false;

        // Special case for "sé".  Could be "að vera" or "að sjá"
		if( verb.lexeme.equalsIgnoreCase( "sé" ) )
		{
			if( ( (IceTag)verb.getFirstTag() ).isVerbBe() )
				caseStr = Character.toString( IceTag.cNominative );
			else
				caseStr = Character.toString( IceTag.cAccusative );
		}
		else if( verb.isVerbBe() )
			caseStr = Character.toString( IceTag.cNominative );
		else
			caseStr = verbObjLookup( verb );

		if( caseStr != null )
			found = true;

		if( found )
		{
			char caseChar = caseStr.charAt( 0 );  // Get the case
			dummyTag.setCase( caseChar );         // Set the case of the dummy tag
			dummyToken.setTag( dummyTag );        // Set the tag of the dummy token
			dummyToken.lexeme = verb.lexeme;

			String tmpTags = obj.allTagStrings();
			matchExists = match( IceTokenTags.Match.aCase, obj, dummyToken );  // Use the dummyToken to match against the obj
			if( matchExists )
			{
				if( logger != null )
					logger.log( "SVO Verb Object: Case match: " + verb.lexeme + " " + obj.lexeme + " , tag was: " + tmpTags );
			}
		}
        else if( obj.isSVOObject() && !verb.isVerbMiddleForm() )    // Assume not nominative case for object
		{
				defaultObjectCase( verb, obj );
		}
        return found;
	}

    /**
     * Checks for an agreement between a verb and an object
     * @param verbIndex  The index of the verb
     * @param objIndex   The index of the object
     */
    private void checkVerbObjectAgreement( int verbIndex, int objIndex )
	{
		if( objIndex >= 0 && verbIndex >= 0 )
		{
			IceTokenTags verb = (IceTokenTags)tokens.get( verbIndex );
			if( objIndex > verbIndex )
			{
				for( int i = verbIndex + 1; i <= objIndex; i++ )       // Consecutive objects could have been marked
				{
					IceTokenTags obj = (IceTokenTags)tokens.get( i );
					if( obj.isSVOMainVerb() || obj.isSVOVerb() )
						break;
					else if( obj.isSVOObject() )
						verbObjectMatch( verb, obj );
				}
			}
			if( objIndex == verbIndex - 1 ) // Object to the left of the verb
			{
				IceTokenTags obj = (IceTokenTags)tokens.get( verbIndex - 1 );
				if( obj.isSVOObject() )
					verbObjectMatch( verb, obj );
			}
		}
	}

    /**
     * Finds a subject.
     * @param verbIndex The index of the verb
     * @param to        toIndex
     * @param mainSubjIndex The index of the main subject
     * @return  the index of the subject
     */
	private IceTokenTags findSubject( int verbIndex, int to, int mainSubjIndex )
	{
		IceTokenTags verb, subj = null, beforeVerb, next;
		boolean subjFound = false;

		if( verbIndex > 0 )
		{
			beforeVerb = (IceTokenTags)tokens.get( verbIndex - 1 ); // The token before the verb
			verb = (IceTokenTags)tokens.get( verbIndex );
			for( int i = verbIndex - 1; i >= 0 && i >= verbIndex - 1; i-- ) // Look back one word
			{
				subj = (IceTokenTags)tokens.get( i ); // Assume the subject is the preceeding word
				if( isSubject( subj, verb ) && subj.getSVOMark() == IceTokenTags.SVOMark.svoNone )
				{
					subj.setSVOMark( IceTokenTags.SVOMark.svoSubject );
					checkPluralSubject( subj );
					subjFound = true;
					break;
				}
			}

			if( !subjFound )
			{
				if( verbIndex + 1 <= to && !beforeVerb.isRelativeConjunction() ) // sem átti LEIÐ (leið is not a subject)
				{
					subj = (IceTokenTags)tokens.get( verbIndex + 1 );   // Then try the following word
					if( isSubject( subj, verb ) && subj.getSVOMark() == IceTokenTags.SVOMark.svoNone )
					{
						subj.setSVOMark( IceTokenTags.SVOMark.svoSubject );
						checkPluralSubject( subj );
						subjFound = true;
						if( verbIndex + 2 <= to )
						{
							next = (IceTokenTags)tokens.get( verbIndex + 2 );
							if( subj.isNoun() && next.isPossessivePronoun() )
								next.setSVOMark( IceTokenTags.SVOMark.svoSubject );
						}
					}
				}
			}
			// Look for a relative conjunction
			if( !subjFound )
			{
				for( int i = verbIndex - 1; i >= 0 && i >= verbIndex - 5; i-- ) // Look back 3 words
				{
					beforeVerb = (IceTokenTags)tokens.get( i );
					if( beforeVerb.isRelativeConjunction() && i > 0 )
					{
						subj = (IceTokenTags)tokens.get( i - 1 ); // Assume the subject is the preceeding word
						if( isSubject( subj, verb ) && subj.getSVOMark() == IceTokenTags.SVOMark.svoNone )
						{
							subj.setSVOMark( IceTokenTags.SVOMark.svoSubject );
							checkPluralSubject( subj );
							subjFound = true;
							break;
						}
					}
				}
			}
		}
		if( !subjFound && mainSubjIndex >= 0 )
			subj = (IceTokenTags)tokens.get( mainSubjIndex );

		return subj;
	}

	private void checkOtherVerbSubjectAgreement( int from, int to, int subjIndex )
	{
		IceTokenTags verb, subj, nextTok;

		// Find verbs which have not been marked as a SVO main verb
		for( int i = from; i <= to; i++ )
		{
			subj = null;
			verb = (IceTokenTags)tokens.get( i );

			if( i < to )
				nextTok = (IceTokenTags)tokens.get( i + 1 );

			if( verb.isSVOVerb() && ( !verb.isVerbSupine() || verb.isVerbMiddleForm() ) &&
			    !verb.isOnlyWordClass( IceTag.WordClass.wcVerbInf ) )     // Subject fylgir ekki nafnhætti
			{
				// Check the following:
				// Hann (mainsubject) lét (mainverb) hendurnar (object but not subject) liggja (verb)
				// Ok is however, henni (mainsubject) fannst (mainverb middle form) þau (subject) hafa (verb)
				if( i >= 3 )
				{
					IceTokenTags prevTok = (IceTokenTags)tokens.get( i - 1 );
					IceTokenTags prevprevTok = (IceTokenTags)tokens.get( i - 2 );
					IceTokenTags prevprevprevTok = (IceTokenTags)tokens.get( i - 3 );
					if( prevTok.isNominal() && !prevTok.isOnlyCase( IceTag.cNominative ) && !prevprevTok.isVerbMiddleForm() &&
					    ( ( prevprevTok.isSVOMainVerb() && prevprevprevTok.isSVOMainSubject() ) ||
					      ( prevprevTok.isSVOVerb() && prevprevprevTok.isSVOSubject() ) )
							)
					{
						if( logger != null )
							logger.log( "SVO Other Verb Subject: Subject discarded: " + prevprevprevTok.lexeme + " " + prevprevTok.lexeme + " " + prevTok.lexeme + " " + verb.lexeme );
						break;
					}
				}
				subj = findSubject( i, to, subjIndex );
				if( subj != null && subj.isNominal() )
				{
					boolean isMatch = match( IceTokenTags.Match.personGenderNumber, verb, subj );
					if( verb.isVerbCaseMarking() )
						isMatch = match( IceTokenTags.Match.aCase, subj, verb );
						// Subject is usually in nominative case
					else
					{
						if( logger != null )
							logger.log( "SVO Other Verb Subject: " + verb.lexeme + " " + subj.lexeme + " , tag is: " + subj.allTagStrings() );
						if( subj.isCase( IceTag.cNominative ) )
						{
							subj.removeCase( IceTag.cAccusative, false );
							subj.removeCase( IceTag.cDative, false );
							subj.removeCase( IceTag.cGenitive, false );
						}
					}


					if( !isMatch )
						removePersonsFromVerb( verb );
				}
			}
		}
	}


    /**
     * Returns the nearest subject to the left of the index.
     * @param index The supplied index
     * @return the index of the subject
     */
	private int getNearestSubject( int index )
	{
		IceTokenTags tok;

		for( int i = index - 1; i >= 0; i-- )
		{
			tok = (IceTokenTags)tokens.get( i );
			if( tok.isSVOMainSubject() || tok.isSVOSubject() )
				return i;
		}
		return -1;
	}

    /**
     * Tries to find a subject from the previuos phrase.
     * <b> Returns the index found, else -1.  Just look 10 tokens to the left.
     * @param verbIndex The index of the verb
     * @param subjType  The type of the subject
     * @return The index found
     */
	private int getSubjectPreviousPhrase( int verbIndex, IceTokenTags.SVOMark subjType )
	{
		IceTokenTags tok, tok2, verb;

		verb = (IceTokenTags)tokens.get( verbIndex );
		int i = verbIndex - 1;
		if( i >= 0 )
		{
			tok = (IceTokenTags)tokens.get( i );
			if( i > 0 && tok.lexeme.equals( "," ) )
			{
				tok2 = (IceTokenTags)tokens.get( i - 1 );
				// "síðarnefnda úrræðið, dagsektirnar, hafa ..."
				if( ( tok2.isNoun() || tok2.isProperNoun() ) && tok2.isCase( IceTag.cNominative ) )
				{
					if( tok2.getSVOMark() == IceTokenTags.SVOMark.svoNone )
						//tok2.setSVOMark(is.iclt.icenlp.core.tokenizer.IceTokenTags.svoMainSubject);
						tok2.setSVOMark( subjType );
					return i - 1;
				}
			}
		}

		for( i = verbIndex - 1; i >= 0; i-- )
		{
			tok = (IceTokenTags)tokens.get( i );
			// First check for relative conjunction
			if( tok.isRelativeConjunction() && tok.lexeme.equalsIgnoreCase( "sem" ) /*&& i>1*/ )
			{
				for( int j = i - 1; j >= i - 2 && j >= 0; j-- )     // comma could be found before the "sem"
				{
					tok2 = (IceTokenTags)tokens.get( j );
					if( tok2.isNoun() || tok2.isProperNoun() || tok2.isPersonalPronoun() ||
					    tok2.isDemonstrativePronoun() || tok2.isIndefinitePronoun() || tok2.isInterrogativePronoun() )
					{
						if( tok2.getSVOMark() == IceTokenTags.SVOMark.svoNone )
							tok2.setSVOMark( subjType );
						return j;
					}
				}
			}

			else if( ( tok.isSVOMainSubject() || tok.isSVOSubject() ) && isSubject( tok, verb ) )
				return i;
		}
		return -1;
	}


	private void removePersonsFromVerb( IceTokenTags verb )
	{
		if( verb.isGenderPerson( IceTag.cThirdPerson ) )
		{
			verb.removeGender( IceTag.cFirstPerson, false );
			verb.removeGender( IceTag.cSecondPerson, false );
			if( logger != null )
				logger.log( "Remove person, Third person assumed: " + verb.lexeme );
		}
	}

    /**
     *  Makes sure there is an agreement between subject and verb.
     * <b> And make sure subject is in nominative case.
     * @param verbIndex  The index of the verb
     * @param subjIndex  The index of the subject
     */
	private void checkVerbSubjectAgreement( int verbIndex, int subjIndex )
	{
		IceTokenTags verb, subj;
		if( verbIndex >= 0 )
		{
			verb = (IceTokenTags)tokens.get( verbIndex );
			if( subjIndex >= 0 )
			{
				subj = (IceTokenTags)tokens.get( subjIndex );
				boolean isMatch = match( IceTokenTags.Match.personGenderNumber, verb, subj );
				match( IceTokenTags.Match.personGenderNumber, subj, verb );
				// if a verb which demands subject in oblique cases
				if( verb.isVerbCaseMarking() )
					isMatch = match( IceTokenTags.Match.aCase, subj, verb );
					// Subject is usually in nominative case
				else if( subj.isCase( IceTag.cNominative ) )
				{
					subj.removeCase( IceTag.cAccusative, false );
					subj.removeCase( IceTag.cDative, false );
					subj.removeCase( IceTag.cGenitive, false );
					if( logger != null )
						logger.log( "Subject cases removed: " + subj.lexeme );
				}
				if( !isMatch )
					removePersonsFromVerb( verb );
				else
				{
					// if a token has a supine tag and a match existed with some other verb tag of the token and the subject,
					// then we can safely remove the supine tag
					if( verb.isVerbSupine() )
					{
						if( logger != null )
							logger.log( "SVO Disambiguation: Removed supine " + verb.lexeme + " and " + subj.lexeme );
						verb.removeVerbForm( IceTokenTags.Condition.condVerbSupine );
					}
				}
			}
			else
			{    // no subject found - assume infinitive
				removePersonsFromVerb( verb );
			}
		}
	}


    /**
     * Makes sure there is an agreement between subject and object.
     * @param subjIndex The index of the subject
     * @param verbIndex The index of the verb
     * @param objIndex  The infex of the object
     * @return true if an agreement was found
     */
	public boolean checkSubjectObjectAgreement( int subjIndex, int verbIndex, int objIndex )
	{
		IceTokenTags subj, verb=null, obj, objNext = null, afterSubject = null, afterSubject2 = null;
		boolean relativeObj = false;
		int i = objIndex;
		int max = tokens.size() - 1;
		boolean success = false;

		if( subjIndex >= 0 && objIndex >= 0)
		{
			subj = (IceTokenTags)tokens.get( subjIndex );
			if( subjIndex + 1 <= max )
				afterSubject = (IceTokenTags)tokens.get( subjIndex + 1 );
			if( subjIndex + 2 <= max )
				afterSubject2 = (IceTokenTags)tokens.get( subjIndex + 2 );

			// Check for relative conjunction.  In that case there does not have to be a case match between subj and obj
			if( afterSubject != null && ( afterSubject.isRelativeConjunction() ||
			                              ( afterSubject.tokenCode == Token.TokenCode.tcComma ) && afterSubject2 != null && afterSubject2.isRelativeConjunction() ) )
				relativeObj = true;

			obj = (IceTokenTags)tokens.get( objIndex );
            if (verbIndex >=0)
                verb = (IceTokenTags)tokens.get( verbIndex );


            while( obj.isSVOObject() && i <= max )   // Consecutive objects might have been marked
			{
				if(
						( subj.isOnlyWordClass( IceTag.WordClass.wcNoun ) ||
						  subj.isOnlyWordClass( IceTag.WordClass.wcProperNoun ) ||
						  subj.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) ||
						  subj.isOnlyWordClass( IceTag.WordClass.wcDemPronoun ) ||
						  ( subj.isPersonalPronoun() && subj.isDemonstrativePronoun() ) ||
						  subj.isInterrogativePronoun()
						) )
				{
					if( objIndex < max )
						objNext = (IceTokenTags)tokens.get( objIndex + 1 );

					if( objIndex > subjIndex &&
					    ( obj.isReflexivePronoun() ||    // Like "Flugvélin lækkaði sig"
					      ( ( ( (IceTag)obj.getFirstTag() ).isAdjective() || ( obj.isAdjective() && ( obj.isIndefinitePronoun() || obj.isVerbPastPart() ) ) ) &&
					        ( objNext == null || !objNext.isNoun() ) ) ||   // This condition ensures that "glampandi" is not processed in "Skyndilega beraði hann (subj) glampandi (obj) tennurnar (obj)
					        obj.isOnlyWordClass( IceTag.WordClass.wcVerbPastPart ) ||
					        ( subj.isInterrogativePronoun() && obj.isNoun() && verb != null && verb.isVerbBe() )
					    ) )
					{
						if( verb != null && verb.isVerbBe() && (subj.isOnlyWordClass( IceTag.WordClass.wcPersPronoun ) || ( subj.isPersonalPronoun() && subj.isDemonstrativePronoun()) ) )
						{
							// þú ert einstakur/einstök/einstakt
							if( !subj.hasGender() )
								match( IceTokenTags.Match.number, obj, subj );
							else    // hann er einstakur
								match( IceTokenTags.Match.personGenderNumber, obj, subj );
                            success = true;
                        }
						else
						if( relativeObj || obj.isReflexivePronoun() )  {
                            // Flugvélarnar lækkuðu sig
							match( IceTokenTags.Match.personGenderNumber, obj, subj );
                            success = true;
                        }
                        else
                        {
                            match( IceTokenTags.Match.genderNumberCase, obj, subj );
						    success = true;
                        }
                    }
				}
				i++;
				if( i <= max )
					obj = (IceTokenTags)tokens.get( i );
			}
		}
		return success;
	}


	private void checkOtherSubjectObjectAgreement( int from, int to, int mainSubjIndex, int verbIndex )
	{
		IceTokenTags subj, obj;
		boolean found;
		for( int i = to; i >= from; i-- )
		{
			obj = (IceTokenTags)tokens.get( i );
			if( obj.isSVOObject() )
			{
				found = false;
				for( int j = i - 1; j >= from; j-- )
				{
					subj = (IceTokenTags)tokens.get( j );
					if( subj.isSVOSubject() || subj.isCase( IceTag.cNominative ) )
					{
						found = checkSubjectObjectAgreement( j, verbIndex, i );
						if( found )
							break;
					}
				}
				if( !found && mainSubjIndex >= 0 && i > mainSubjIndex )  // then try matching against main subject
				{
					checkSubjectObjectAgreement( mainSubjIndex, verbIndex, i );
				}
			}
		}
	}


	private boolean isEnumerationEnd( IceTokenTags tok )
	{
        return tok.lexeme.equalsIgnoreCase("og") || tok.lexeme.equalsIgnoreCase("eða") ||
                tok.lexeme.equalsIgnoreCase("né");
	}


    /**
     * Makes sure there is an agreement in adjective enumeration phrases.
     * <b> For example: "hvernig langir, brúnir ..."
     */
	public void checkEnumeration()
	{
		IceTokenTags tok, tok2, tok3, tokNext, tokComma, tokAnd;
		boolean prevTokenIsSeparator;

		int last = tokens.size() - 1;
		for( int i = 0; i <= last; i++ )
		{
			tok = (IceTokenTags)tokens.get( i );
			if( i < last )
				tokComma = (IceTokenTags)tokens.get( i + 1 );
			else
				tokComma = null;

			if( tokComma != null && ( tokComma.lexeme.equals( "," ) ) &&
			    // (tokComma.lexeme.equals("og") || tokComma.lexeme.equals("eða")) &&
			    ( tok.isAdjective() || tok.isNoun() ) )
			{
				prevTokenIsSeparator = true;
				boolean done = false;
				for( int j = i + 2; j <= last && !done; j++ )
				{
					tok2 = (IceTokenTags)tokens.get( j );
					if( j < last )
						tok3 = (IceTokenTags)tokens.get( j + 1 );
					else
						tok3 = null;

					if( ( !tok.isSVOPrepPhrase() || tok2.isSVOPrepPhrase() ) && // not only one of them a prep phrase
					    ( tok2.isAdjective() || tok2.isNoun() ) )
					{
						// hreinum (tok), bleikum(tok2) fötum (tok3)
						if( tok.isOnlyWordClass( IceTag.WordClass.wcAdj ) && tok3 != null && tok3.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
							match( IceTokenTags.Match.genderNumberCase, tok, tok3 );

						if( tok2.isOnlyWordClass( IceTag.WordClass.wcAdj ) && tok.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
							match( IceTokenTags.Match.genderNumberCase, tok2, tok );
						else if( !tok2.isVerb() )
							match( IceTokenTags.Match.aCase, tok2, tok );

						if( logger != null )
							logger.log( "SVO: Enumeration: " + tok2.lexeme + " " + tok.lexeme );
						prevTokenIsSeparator = false;
					}
					else if( ( tok2.lexeme.equals( "," ) || tok2.lexeme.equals( "og" ) ) && !prevTokenIsSeparator )
						prevTokenIsSeparator = true;
					else
						done = true;
				}
			}
		}

		for( int i = last; i >= 0; i-- )
		{
			tok = (IceTokenTags)tokens.get( i );
			if( i > 0 )
				tokAnd = (IceTokenTags)tokens.get( i - 1 );
			else
				tokAnd = null;
			if( i < last )
				tokNext = (IceTokenTags)tokens.get( i + 1 );
			else
				tokNext = null;

			if(
					tok.getSVOMark() == IceTokenTags.SVOMark.svoNone &&
					( tok.isNoun() || tok.isProperNoun() || tok.isAdjective() || tok.isNumeral() ) &&
					tokAnd != null && isEnumerationEnd( tokAnd ) )
			{
				if( i - 2 >= 0 )
				{
					tok2 = (IceTokenTags)tokens.get( i - 2 );
					if( tok2.isNoun() || tok.isProperNoun() || tok2.isAdjective() || tok2.isNumeral() )
					{
						if( tok2.isOnlyWordClass( IceTag.WordClass.wcAdj ) && tok.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
						{
							match( IceTokenTags.Match.genderNumberCase, tok2, tok );
							if( logger != null )
								logger.log( "SVO: Enumeration2: " + tok2.lexeme + " " + tok.lexeme );
						}
						// Else if there is not a match between the tok and the next word then
						//else if (!tok2.isVerb() && !tok.isVerb() && tokNext != null && !tok.isAnyMatch(is.iclt.icenlp.core.tokenizer.IceTokenTags.matchGenderNumberCase, tokNext.getTags()))
						else if( !tok.isVerb() && tokNext != null && !tok.genderNumberCaseMatch( tokNext ) )
						{
							match( IceTokenTags.Match.aCase, tok2, tok );
							if( logger != null )
								logger.log( "SVO: Enumeration2: " + tok2.lexeme + " " + tok.lexeme );
						}
					}
				}
			}
		}

	}

    /**
     * Makes sure there is agreement in between reflexive pronouns and subjects.
     */
	public void checkReflexives()
	{
		IceTokenTags reflexive, subj=null, preceeding ;
		boolean subjFound;

		int last = tokens.size() - 1;

		for( int i = last; i >= 0; i-- )
		{
			subjFound = false;

			reflexive = (IceTokenTags)tokens.get( i );
			if( reflexive.isReflexivePronoun() &&
			    ( reflexive.lexeme.equalsIgnoreCase( "sig" ) || reflexive.lexeme.equalsIgnoreCase( "sér" ) || reflexive.lexeme.equalsIgnoreCase( "sín" ) ) )
			{
				preceeding = null;
				for( int j = i - 1; j >= 0; j-- )
				{
					subj = (IceTokenTags)tokens.get( j );
					if( subj.isSVOMainSubject() || subj.isSVOSubject() ||
					    ( subj.isNominal() && preceeding != null && preceeding.isRelativeConjunction() ) )
					{
						if( reflexive.personNumberMatch( subj ) )
						{
							subjFound = true;
							break;
						}
					}
					preceeding = subj;
				}
				if( subjFound )
					match( IceTokenTags.Match.personGenderNumber, reflexive, subj );
			}
		}
	}

    /**
     * Check if subjunctive mood should be selected for verbs.
     */
	public void checkSubjunctive()
	{
		IceTokenTags verb, tok;
		boolean subjunctiveFound;

		int last = tokens.size() - 1;

		for( int i = last; i >= 0; i-- )
		{
			verb = (IceTokenTags)tokens.get( i );
			if( verb.isSVOMainVerb() || verb.isSVOVerb() )
			{
				subjunctiveFound = false;
				for( int j = i - 1; j >= 0 && j >= i - 3 && !subjunctiveFound; j-- )    // search 3 words backwords
				{
					tok = (IceTokenTags)tokens.get( j );
					if( tok.lexeme.equalsIgnoreCase( "þó" ) || tok.lexeme.equalsIgnoreCase( "þótt" ) ||
					    tok.lexeme.equalsIgnoreCase( "ef" ) || tok.lexeme.equalsIgnoreCase( "æskilegt" ) || tok.isVerbSubjunctive() )
					{
						subjunctiveFound = true;
					}
				}
				if( !subjunctiveFound && ( (IceTag)verb.getFirstTag() ).isVerbSubjunctiveForm() && verb.isVerbIndicative() )
				{
					if( logger != null )
						logger.log( "CheckSubjunctive, removed: " + verb.lexeme + " " + verb.allTagStrings() );
					verb.removeVerbForm( IceTokenTags.Condition.condVerbSubjunctive );
				}
			}
		}
	}

    /**
     * The main function of the heuristics.
     * @param from fromIndex
     * @param to   toIndex
     */
	public void checkSVO( int from, int to )
	{
		int subjIndex = -1, objIndex = -1, verbIndex = -1;

		ambiguateAdjectives( from, to );
		markPrepPhrase( from, to );
		verbIndex = markMainVerb( from, to );
		if( verbIndex >= 0 )
		{
			subjIndex = markSubject( from, verbIndex, IceTokenTags.SVOMark.svoMainSubject );
			markOtherVerbs( from, to, subjIndex );

			checkVerbSubjectAgreement( verbIndex, subjIndex );
			checkOtherVerbSubjectAgreement( from, to, subjIndex );   // Other subjects are marked here

			if( subjIndex > verbIndex )  // subject to the right of the verb?
				objIndex = markObjectToLeft( from, verbIndex );  // Then try to find object to the left
			if( objIndex < 0 )
				objIndex = markObject( verbIndex, to );

			removeInvalidSubjects( from, to );
			removeInvalidObjects( from, to );

			checkSubjectObjectAgreement( subjIndex, verbIndex, objIndex );
			checkVerbObjectAgreement( verbIndex, objIndex );

			checkOtherVerbObjectAgreement( from, to );    // Other objects are marked here and
			checkOtherSubjectObjectAgreement( from, to, subjIndex, verbIndex );  // therefore this has to be done after checkOtherVerbObjectAgreement
		}
		else
		{
			subjIndex = markSubjectNoVerb( from, to );
		}

		checkNominalAgreement( from, to );    // Do before prepagreement to ensure agreement between nominals inside prep phrases
		checkCorrectPrep( from, to );
		checkPrepAgreement( from, to );
	}

}
