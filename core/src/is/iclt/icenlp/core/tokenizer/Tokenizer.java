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

import is.iclt.icenlp.core.tokenizer.Token.TokenCode;
import is.iclt.icenlp.core.utils.Lexicon;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Performs tokenization.
 * @author Hrafn Loftsson
 */
public class Tokenizer
{
	private static final int maxLexemeSize = 256;
	public static final int typeToken = 0;
	public static final int typeTokenTags = 1;
	public static final int typeIceTokenTags = 2;
	public static final int typeHmmTokenTags = 3;
    public static String datePatternStr = "[0-3]?[0-9]\\.? ?(janúar|febrúar|mars|apríl|maí|júní|júlí|ágúst|september|október|nóvember|desember)";

	private Lexicon lex;			// a lexicon
	private Token currToken;
	private String sentence;
	private int index;
	private char[] lexeme;
	private char[] letter;
    private Pattern webAddressPattern;
    private Pattern timePattern;
    private Pattern datePattern;
    private String lastSuperBlock = null;


	private int tokenType;
	private boolean strictTokenization; // Set to true if words and numbers cannot contain any special characters,
                                        // and single quotes are not part of other tokens
    private boolean dateHandling=false;
    //private boolean findMultiWords;
	public ArrayList tokens;


    public Tokenizer( int typeTok, boolean strict ) throws IOException
	{
		this.initialize( typeTok, strict );
	}

    public Tokenizer( int typeTok, boolean strict, String lexiconFile ) throws IOException
	{
		this.initialize( typeTok, strict );
		lex = new Lexicon();
		lex.load( lexiconFile);
	}

    public Tokenizer( int typeTok, boolean strict, InputStream lexiconFile ) throws IOException, NullPointerException
	{
		this.initialize( typeTok, strict );
		lex = new Lexicon( lexiconFile );
	}

	public Tokenizer( int typeIceTokenTags, boolean strictTokenization, Lexicon lexicon )
	{
		this.initialize( typeIceTokenTags, strictTokenization );
		this.lex = lexicon;
	}

	public void initialize( int typeTok, boolean strict )
	{
		tokenType = typeTok;
		switch( tokenType )
		{
			case typeToken:
				currToken = new Token();
				break;
			case typeTokenTags:
				currToken = new TokenTags();
				break;
			case typeIceTokenTags:
				currToken = new IceTokenTags();
				break;
			case typeHmmTokenTags:
				currToken = new HmmTokenTags();
				break;
			default:
				currToken = new Token();
				break;
		}

		strictTokenization = strict;
		//findMultiWords = false;
		sentence = "";
		index = 0;

		letter = new char[1];        // temporary array
		lexeme = new char[maxLexemeSize];      //  number of characters in a token
		tokens = new ArrayList();       // number of tokens in a sentence

        webAddressPattern = Pattern.compile("(ht|f)tps?://[-\\w]+(\\.\\w[-\\w]*)+");
        timePattern = Pattern.compile("\\d+:\\d+"); // 20:25
        datePattern = Pattern.compile(datePatternStr);
	}

	/*public void findMultiWords( boolean find )
	{
		findMultiWords = find;
	}*/

    public void dateHandling( boolean doSpecialDateHandling )
	{
		dateHandling = doSpecialDateHandling;
	}


	private boolean isLinkedToPrevious(TokenCode tokenCode)
	{
		if(tokenCode == TokenCode.tcEOS || tokenCode == TokenCode.tcComma || tokenCode == TokenCode.tcPeriod)
			return true;		
		return false;
	}
	
	/*
	private void clearLexeme()
	{
		for( int i = 0; i < lexeme.length; i++ )
			lexeme[i] = '\0';
	}
*/

	// Splits abbreviations that include more than one '.' into more than one token
	public void splitAbbreviations()
	{
		Token newToken;
		//Iterator iterator = tokens.iterator();
		//while (iterator.hasNext()) {
		for( int j = 0; j <= tokens.size() - 1; j++ )
		{
			//Token tok = (Token)tokens.elementAt( j );
            Token tok = (Token)tokens.get( j );

            if( tok.tokenCode == Token.TokenCode.tcAbbrev &&
			    !tok.lexeme.equals( "h.f." ) && !tok.lexeme.equals( "p.p.m." ) ) // h.f. put in temporarily because of inconsistency in data
			{
				char lastCharAbbrev = tok.lexeme.charAt( tok.lexeme.length() - 2 );
				if( Character.isLowerCase( lastCharAbbrev ) )  // else a Proper Noun abbreviation
				{
					String strs[] = tok.splitLexeme( "\\." );
					if( strs.length > 1 )    // Then more than one '.'
					{
						//tokens.removeElementAt( j );   // remove the abbreviation
                        tokens.remove(j);                // remove the abbreviation
                        for( int i = 0; i <= strs.length - 1; i++ ) // and add its individual parts
						{
							switch( tokenType )
							{
								case typeToken:
									newToken = new Token( strs[i] + ".", Token.TokenCode.tcAbbrev );
									break;
								case typeTokenTags:
									newToken = new TokenTags( strs[i] + ".", Token.TokenCode.tcAbbrev );
									break;
								case typeIceTokenTags:
									newToken = new IceTokenTags( strs[i] + ".", Token.TokenCode.tcAbbrev );
									break;
								case typeHmmTokenTags:
									newToken = new HmmTokenTags( strs[i] + ".", Token.TokenCode.tcAbbrev );
									break;
								default:
									newToken = new Token( strs[i] + ".", Token.TokenCode.tcAbbrev );
									break;
							}
							tokens.add( j + i, newToken );
						}
					}
				}
			}
		}

	}

    // Used if the original input had already been tokenized.
    // In that case, we only want to split into tokens using whitespace as a delimiter
    public void tokenizeSplit(String sentenc)
    {
        tokens.clear();
        sentence = sentenc;
        Token token;
        currToken = new Token("",Token.TokenCode.tcUnknown);

        String [] words = sentence.split("\\s+");  // split on white space
        for (int i=0; i < words.length; i++) {
            currToken.lexeme = words[i];
            //System.out.println(currToken.lexeme);
            // At this point we don't know the type of each token.
            // IceTagger relies on types of punctuations => set most important types
            boolean lastWord =  i==words.length-1;
            boolean nextToLastWord = words.length>1 && i==words.length-2;
            setCurrentTokenType(lastWord, nextToLastWord);
            switch( tokenType )
				{
					case typeToken:
						token = new Token( currToken.lexeme, currToken.tokenCode );
						break;
					case typeTokenTags:
						token = new TokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					case typeIceTokenTags:
						token = new IceTokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					case typeHmmTokenTags:
						token = new HmmTokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					default:
						token = new Token( currToken.lexeme, currToken.tokenCode );
						break;
				}
				tokens.add( token );
        }
        markMWEs();
    }

	// Used if the original input had already been tokenized and contains token/tag pairs
	// In that case, we only want to split into tokens using whitespace as a delimiter
	public void tokensWithTags(String sentenc)
	{
		TokenTags token;
		tokens.clear();
		sentence = sentenc;

		String [] words = sentence.split("\\s+");  // split on white space
		for (int i=0; i < words.length; i = i + 2) {

			token = new TokenTags(words[i], TokenCode.tcUnknown, words[i+1]);
			tokens.add( token );
		}
	}


    public void tokenize( String sentenc )
	{
		this.tokens.clear();
		this.index = 0;
		this.sentence = sentenc;
		
		Token token = null;

		while( index < sentence.length() )
		{
			nextToken();
			if( currToken.tokenCode != Token.TokenCode.tcWhitespace )
			{
				switch( tokenType )
				{
					case typeToken:
						token = new Token( currToken.lexeme, currToken.tokenCode );
						break;
					case typeTokenTags:
						token = new TokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					case typeIceTokenTags:
						token = new IceTokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					case typeHmmTokenTags:
						token = new HmmTokenTags( currToken.lexeme, currToken.tokenCode );
						break;
					default:
						token = new Token( currToken.lexeme, currToken.tokenCode );
						break;
				}
				
				if(this.isLinkedToPrevious(token.tokenCode))
					token.linkedToPreviousWord = true;

				if(this.lastSuperBlock != null) {
					token.preSpace = this.lastSuperBlock;
					this.lastSuperBlock = null;
				}
				tokens.add( token );				
			}
		}

        markMWEs();
		// Find multiwords
		/*if( findMultiWords )
		{
			boolean multiFound = true;
			while( multiFound )
				multiFound = combineMultiWords( 3 );
			multiFound = true;
			while( multiFound )
				multiFound = combineMultiWords( 2 );
		}*/
	}

	/*
	 *  Combines multiwords to one token
	 *  The parameter controls how many words are combined (2 or 3).
	 *  Returns true if multiWord in sentence is found, else false
	 */
	/*public boolean combineMultiWords( int number )
	{
		int count = tokens.size();
		Token first = null, second = null, third = null;

		boolean secondFound = false;
		boolean thirdFound = false;
		for( int i = 0; i < count; i++ )
		{
			first = (Token)tokens.get( i );
			String multiWord = first.lexeme;
			if( i < count - 1 )
			{
				second = (Token)tokens.get( i + 1 );
				secondFound = true;
				multiWord = multiWord + "_" + second.lexeme;

			}
			if( number == 3 && i < count - 2 )
			{
				third = (Token)tokens.get( i + 2 );
				thirdFound = true;
				multiWord = multiWord + "_" + third.lexeme;
			}


			if( (secondFound || thirdFound) && lex.lookup( multiWord, true ) != null )
			{
				first.lexeme = multiWord;
				first.tokenCode = Token.TokenCode.tcMultiWord;
				tokens.remove( i + 1 );
				if( thirdFound )
					tokens.remove( i + 1 );

				return secondFound;
			}
		}
		return false;
	}*/


    private void setMWECode(boolean trigram, boolean bigram, int idx)
    {
        Token tok;

        tok = (Token)tokens.get(idx);
        if (tok.mweCode == Token.MWECode.none)
           tok.mweCode = Token.MWECode.begins;

        if (trigram) {
                tok = (Token)tokens.get(idx+2);
                if (tok.mweCode == Token.MWECode.none)
                    tok.mweCode = Token.MWECode.ends;
        }
        else if (bigram) {
            tok = (Token)tokens.get(idx+1);
            if (tok.mweCode == Token.MWECode.none)
               tok.mweCode = Token.MWECode.ends;
        }
    }

    /* marks multiword expressions */
    private void markMWEs()
	{
		Token first, second, third;
        String mwe2=null, mwe3=null, tags;
        String mweStr;

        int count = tokens.size();

		boolean bigram = false;
		boolean trigram = false;

		for( int i = 0; i < count; i++ )
		{
            mweStr=null;
			first = (Token)tokens.get( i );
			mwe2 = first.lexeme;
			if( i < count - 1 )
			{
				second = (Token)tokens.get( i + 1 );
				bigram = true;
				mwe2 = mwe2 + "_" + second.lexeme;
			}
			else
				bigram = false;
			if( i < count - 2 )
			{
				third = (Token)tokens.get( i + 2 );
				trigram = true;
				mwe3 = mwe2 + "_" + third.lexeme;
			}
			else
				trigram = false;

			if( trigram )
				mweStr = lex.lookup( mwe3, true );
			if( mweStr == null || !mweStr.equalsIgnoreCase("MWE")) {
                trigram = false;
                if (bigram) {
                    mweStr = lex.lookup( mwe2, true );
                    if( mweStr == null || !mweStr.equalsIgnoreCase("MWE"))
                        bigram = false;
                }
            }

            if (trigram || bigram)
                setMWECode(trigram, bigram, i);
	    }
    }

	private boolean isAbbreviation( String word )
	{
		// If the first character of the abbreviation is upper case then assume upper case abbreviation
		//if (Character.isUpperCase(word.charAt(word.length()-2)))
		if( Character.isUpperCase( word.charAt( 0 ) ) )
		{
			String key = lex.lookup( word, false );
			if( key == null )
				key = lex.lookup( word, true );
			return (key != null);
		}
		else
			return (lex.lookup( word, true ) != null);
	}

	private void getWord( char c )
	{
		int i = 0;
		char ch = c;
		char lastch = c;
		char nextCh;
		boolean periodFound = false;
		boolean abbrevFound = false;
        boolean openBracket = false;

		while( Character.isLetterOrDigit( ch ) || ch == '-' || ch == '.' || ch == '_' || ch == '@'  ||
		      (!strictTokenization && (ch == '/' || ch == '$' || ch == '^' || ch == '\'' || ch == '’' || // Single quote
                                       ch == '[' || ch == '{' || ch == '(' ||
                                       ((ch == ']'  || ch == '}'  || ch == ')') && openBracket)
                                      ))
              )

		{
            if( i >= maxLexemeSize )
		    {
			    System.out.println( "FATAL ERROR - Word to long: " + (new String( lexeme, 0, i - 1 )) );
			    System.exit( 1 );
		    }

            if (!strictTokenization) {
                if (ch == '[' || ch == '{' || ch == '(')
                    openBracket = true;
                else if (ch == ']' || ch == '}' || ch == ')')
                    openBracket = false;
            }

            lexeme[i] = ch;
			lastch = ch;
			index++;
			i++;

			if( ch == '.' )
			{
				periodFound = true;
				if( index < sentence.length() - 1 )
				{
					nextCh = sentence.charAt( index );
					if( nextCh == '-' )         // A.-Grænland
						periodFound = false;
				}
			}

			if( index < sentence.length() )      // EOS char might be missing
			{
				ch = sentence.charAt( index );
				// More than one consecutive periods are disallowed
                // HL: Also break a period and a single quote away from the word, 10.02.2009
                // HL: Also break a period and a quote away from the word, 23.06.2009
                if( lastch == '.' && (ch == '.' || ch == '\'' || ch == '»' || ch == '"' || ch == '”' || ch == '‘'))
				{
					periodFound = false;
					index--;
					i--;
					break;
				}
			}
			else
				break;
		}

		currToken.lexeme = new String( lexeme, 0, i );
		if( periodFound )
		{
			//if (index == sentence.length())         // Then a period was found at the end of the sentence
			if( ch == '.' && index == sentence.length() )         // Then a period was found at the end of the sentence
			{
				if( isAbbreviation( currToken.lexeme ) )	// Is the lexeme an abbreviation appearing at
					// the end of a sentence
					abbrevFound = true;
				else
				{
					boolean decrement = true;
					int len = currToken.lexeme.length();
					if( len > 1 )
					{
						char chBeforePeriod = currToken.lexeme.charAt( currToken.lexeme.length() - 2 );
						//if (Character.isUpperCase(chBeforePeriod))    // The period is still a part of a word like "II."
						if( chBeforePeriod == 'I' )    // The period is still a part of a word like "II."   Temporary lausn!!!!
							decrement = false;
					}
					if( decrement )
					{
						currToken.lexeme = new String( lexeme, 0, i - 1 );    // The period is not part of the word
						index--;                                        // Read to far
					}
				}
			}
			else       // the period is not at the end of the sentence
			{
				if( ch != '.' && ch != '"' && ch != '«' && ch != ')' && ch != ']' )
					abbrevFound = false;          // just assume it is not an abbreviation
				else
				{
					if( isAbbreviation( currToken.lexeme ) )	// Is the lexeme an abbreviation appearing at
						// right before a quote ending
						abbrevFound = true;
					else    // not found
					{
						currToken.lexeme = new String( lexeme, 0, i - 1 );    // The period is not part of the word
						index--;                                        // Read to far
					}
				}
			}
		}

		if( abbrevFound )
			currToken.tokenCode = Token.TokenCode.tcAbbrev;
		else
			currToken.tokenCode = Token.TokenCode.tcWord;
	}

	private void getNumber( char c )
	{
		int i = 0;
        char ch = c;
        char lastch;

        while( Character.isDigit( ch ) || Character.isLetter( ch ) ||
		       ch == '½' || ch == '.' || ch == ',' || ch == '%' || ch == '$'  || ch == '£' || ch == '°' ||
		       ch == '-' || ch == '_' || ch == '÷' || ch == '*' || ch == '+' || ch == '±' || ch == '/' ||
		       (!strictTokenization && (
				       ch == '°' || ch == '^' || ch == '_' ||
				       ch == ':' || ch == '\'' ||
				       ch == '\\' || ch == '=' || ch == '{' || ch == '}' || ch == '\'')) )
		{
			lexeme[i] = ch;
            lastch = ch;
            index++;
			i++;

            if( index < sentence.length() ) {     // EOS char might be missing
                ch = sentence.charAt( index );
                // Handling cases like : 2001.»
                if (lastch == '.' && (ch == '»' || ch == '"' || ch == '”')) {
                    index--;
                    i--;
                }
            }
            else
			{
				if( ch == '.' ) // If period at end of sentence, e.g. "skildu jöfn, 1:1."
				{
					if( !sentence.matches( "^[0-9]+\\.([0-9]+\\.)*" ) && //  IF sentence = "1." or "1.3. then the period is a part of the number
					    !sentence.matches( ".+[0-9]\\.[0-9]\\.$" ) )     // endar á "í 3.1." hér ætti punkturinn ekki að vera með
					// gert vegna OTB
					{
						index--;                     // read to far
						i--;
					}
				}
				break;
			}

		}
		;

		// Cover cases like "fyrr, árið 1926, höfðu"...
		if( i > 0 && lexeme[i - 1] == ',' ) // read to far
		{
			index--;
			i--;
		}

		currToken.lexeme = new String( lexeme, 0, i );
		currToken.tokenCode = Token.TokenCode.tcNumber;
	}

	private void skipWhitespace( char c ) {
		// Collect the spaces.
		this.lastSuperBlock = "";
		char ch = c;
		
		
		boolean eofSentence = false;
		while( !eofSentence && Character.isWhitespace( ch ) ){
			index++;
			if( index < sentence.length() ){
				this.lastSuperBlock += ch;
				ch = sentence.charAt( index );
			}
			else
				eofSentence = true;
		}
		currToken.tokenCode = Token.TokenCode.tcWhitespace;
	}

	// For example: "..." is one token
	private void getPeriods()
	{
		boolean isLastChar = false;
		int i = 0;
		char ch = '.';
		while( ch == '.' )
		{
			lexeme[i] = ch;
			index++;
			i++;
			if( index < sentence.length() )      // EOS char might be missing
				ch = sentence.charAt( index );
			else
			{
				isLastChar = true;
				break;
			}
		}
		;
		// Read to far because isEOS assumes index points to current character
		index--;

		currToken.lexeme = new String( lexeme, 0, i );
		if( isEOS( isLastChar ) )
			currToken.tokenCode = Token.TokenCode.tcEOS;
		else
			currToken.tokenCode = Token.TokenCode.tcPeriod;
		index++;
	}

	private void setToken( char ch, Token.TokenCode tcCode )
	{
		letter[0] = ch;
		currToken.lexeme = new String( letter );
		currToken.tokenCode = tcCode;
		index++;
	}

	private boolean isEOS( boolean isLastChar )
	// Assumes index points to current character
	// . or ! or ? are not end of string markers if inside quotes
	{
		if( !isLastChar ) // if previous char were not the last character of the sentence
		{
			char ch = sentence.charAt( index + 1 );
			if( ch == '"' || ch == '«')
				return false;
		}
		return true;
	}

    public boolean isWebAddress(String str)
    {
        Matcher matcher = webAddressPattern.matcher(str);
        // Attempts to match str, starting at the beginning, against the pattern.
        // lookingAt does not require that the entire str be matched.
        if (matcher.lookingAt()) {
            int start = matcher.start();
            int end = matcher.end();
            // Now build the token
            currToken.lexeme = sentence.substring(index+start, index+end );
		    currToken.tokenCode = Token.TokenCode.tcWebAddress;
            // And update the global index
            index = index + end - start;
            return true;
        }
        return false;
    }

    public boolean isTime(String str)
    {
        Matcher matcher = timePattern.matcher(str);
        // Attempts to match str, starting at the beginning, against the pattern.
        // lookingAt does not require that the entire str be matched.
        if (matcher.lookingAt()) {
            int start = matcher.start();
            int end = matcher.end();
            // Now build the token
            currToken.lexeme = sentence.substring(index+start, index+end );
		    currToken.tokenCode = Token.TokenCode.tcNumber;
            // And update the global index
            index = index + end - start;
            return true;
        }
        return false;
    }

    public boolean isDate(String str)
    {
        Matcher matcher = datePattern.matcher(str);
        // Attempts to match str, starting at the beginning, against the pattern.
        // lookingAt does not require that the entire str be matched.
        if (matcher.lookingAt()) {
            int start = matcher.start();
            int end = matcher.end();
            // Now build the token
            currToken.lexeme = sentence.substring(index+start, index+end );
		    currToken.tokenCode = Token.TokenCode.tcNumber;
            // And update the global index
            index = index + end - start;
            return true;
        }
        return false;
    }

	public void nextToken()
	{
		char ch, chNext;
		boolean isLastChar = false;

		if( index < sentence.length() )
		{
			if( index == sentence.length() - 1 )
				isLastChar = true;

			ch = sentence.charAt( index );

            String restOfSentence = sentence.substring(index);
            // Contains a web address?
            if (isWebAddress(restOfSentence))
			    ;
            // Date pattern?  HL: Added because of Apertium 13/03/2010
            else if (dateHandling && isDate(restOfSentence))
                ;
            // Pattern 20:25?
            else if (isTime(restOfSentence))
                ;
			else if( Character.isLetter( ch ) ) getWord( ch );
			else if( Character.isDigit( ch ) )
			{ 
				getNumber( ch );
			}
			else if( Character.isWhitespace( ch ) )
			{
				skipWhitespace( ch );
			}
			else if( ch == '.' ) getPeriods();
			else
				// setToken increments index
				switch( ch )
				{
					case'!':
						if( isEOS( isLastChar ) )
							setToken( ch, Token.TokenCode.tcEOS );
						else
							setToken( ch, Token.TokenCode.tcExclamation );
						break;
					case'?':
						if( isEOS( isLastChar ) )
							setToken( ch, Token.TokenCode.tcEOS );
						else
							setToken( ch, Token.TokenCode.tcQuestion );
						break;
					case',':
						setToken( ch, Token.TokenCode.tcComma );
						break;
					case':':
						if( isLastChar )
							setToken( ch, Token.TokenCode.tcEOS );
						else
							setToken( ch, Token.TokenCode.tcColon );
						break;
					case';':
						setToken( ch, Token.TokenCode.tcSemicolon );
						break;
					case'"':
						if( isLastChar )
							setToken( ch, Token.TokenCode.tcEOS );
						else
							setToken( ch, Token.TokenCode.tcDoubleQuote );
						break;

					case'\'':
                        // HL added stricTokenization, 10.02.09
                        if( index == sentence.length() - 1 || strictTokenization)
							setToken( ch, Token.TokenCode.tcSingleQuote );
                        else {
                                if( Character.isDigit( sentence.charAt( index + 1 ) ) )  // '25
								    getNumber( ch );
							    else if( Character.isLetter( sentence.charAt( index + 1 ) ) )  // 'ann
								    getWord( ch );
                                // HL: 28.11.2009, because of the Tiger corpus
                                // May need this if Tiger corpus is not pretokenised
							    /*else if (sentence.charAt(index+1)== '\'') {
                                    currToken.lexeme = "''"; // two single quotes
		                            currToken.tokenCode = Token.TokenCode.tcTwoSingleQuotes;
                                    index++;
                                }*/
                                else
								    setToken( ch, Token.TokenCode.tcSingleQuote );
                        }
						break;

                    case '`':
                        /*if( index == sentence.length() - 1 || strictTokenization)
                            setToken( ch, Token.TokenCode.tcBackQuote );
                        // HL: 28.11.2009, because of the Tiger corpus
					    else if (sentence.charAt(index+1)== '`') {
                            currToken.lexeme = "``"; // two backquotes
		                    currToken.tokenCode = Token.TokenCode.tcTwoBackQuotes;
                            index++;
                        }
                        else*/
					        setToken( ch, Token.TokenCode.tcBackQuote );
                        break;

					case'(':
						setToken( ch, Token.TokenCode.tcLParen );
						break;
					case')':
						setToken( ch, Token.TokenCode.tcRParen );
						break;
					case'[':
						setToken( ch, Token.TokenCode.tcLBracket );
						break;
					case']':
						setToken( ch, Token.TokenCode.tcRBracket );
						break;
                    case'{':
						setToken( ch, Token.TokenCode.tcLCurlyBracket );
						break;
					case'}':
						setToken( ch, Token.TokenCode.tcRCurlyBracket );
						break;
                    case'-':
						if( index == sentence.length() - 1 )
							setToken( ch, Token.TokenCode.tcHyphen );
						else
						{
							if( Character.isDigit( sentence.charAt( index + 1 ) ) )  //-3.4
								getNumber( ch );
							else if( Character.isLetter( sentence.charAt( index + 1 ) ) )  //-verkbann
								getWord( ch );
							else if( index < sentence.length() - 1 )
							{
								chNext = sentence.charAt( index + 1 );
								if( chNext == '-' )      // The word "--"
									getWord( ch );
								else if( chNext == '>' )
								{
									currToken.lexeme = "->";
									currToken.tokenCode = Token.TokenCode.tcArrow;
									index = index + 2;
								}
								else
									setToken( ch, Token.TokenCode.tcHyphen );
							}
							else
								setToken( ch, Token.TokenCode.tcHyphen );
						}
						break;
					case'_':
						if( index == sentence.length() - 1 )
							setToken( ch, Token.TokenCode.tcUnderscore );
						else
						{
							if( Character.isDigit( sentence.charAt( index + 1 ) ) )  //_3.4
								getNumber( ch );
							else if( Character.isLetter( sentence.charAt( index + 1 ) ) )  //_verkbann
								getWord( ch );
							else
								setToken( ch, Token.TokenCode.tcUnderscore );
						}
						break;
                    case'*':
						setToken( ch, Token.TokenCode.tcStar );
						break;
                    case'+':
						if( index == sentence.length() - 1 )
							setToken( ch, Token.TokenCode.tcPlus );
						else
						{
							if( Character.isDigit( sentence.charAt( index + 1 ) ) )  //+3.4
								getNumber( ch );
							else
								setToken( ch, Token.TokenCode.tcPlus );
						}
						break;
					case'±':
						if( index == sentence.length() - 1 )
							setToken( ch, Token.TokenCode.tcPlusMinus );
						else
						{
							if( Character.isDigit( sentence.charAt( index + 1 ) ) )  //+3.4
								getNumber( ch );
							else
								setToken( ch, Token.TokenCode.tcPlusMinus );
						}
						break;
					case'$':
						if( index < sentence.length() - 1 )
						{
							chNext = sentence.charAt( index + 1 );
							if( Character.isDigit( chNext ) ||
							    chNext == '_' || chNext == '-' || chNext == '^' || chNext == '\\' )  //$3.4
								getNumber( ch );
							else
								setToken( ch, Token.TokenCode.tcDollar );
						}
						else
							setToken( ch, Token.TokenCode.tcDollar );
						break;
                    case'£':
						if( index < sentence.length() - 1 )
						{
							chNext = sentence.charAt( index + 1 );
							if( Character.isDigit( chNext ))
							    getNumber( ch );
							else
								setToken( ch, Token.TokenCode.tcPound );
						}
						else
							setToken( ch, Token.TokenCode.tcPound );
						break;
					case'&':
						setToken( ch, Token.TokenCode.tcAnd );
						break;
					case'#':
						setToken( ch, Token.TokenCode.tcNumberSign );
						break;
					case'=':
						setToken( ch, Token.TokenCode.tcEqualSign );
						break;
					case'/':
						setToken( ch, Token.TokenCode.tcSlash );
						break;
					case'\\':
						setToken( ch, Token.TokenCode.tcBackSlash );
						break;
					case'<':
						setToken( ch, Token.TokenCode.tcLess );
						break;
					case'>':
						setToken( ch, Token.TokenCode.tcGreater );
						break;
					case'«':
						setToken( ch, Token.TokenCode.tcLArrow );
						break;
					case'»':
						setToken( ch, Token.TokenCode.tcRArrow );
						break;
                    case '^':
                        setToken( ch, Token.TokenCode.tcHat );
						break;
					default:
						//System.out.println("ERROR: Don't recognize character:" + (new Character(ch)).toString());
						//int num = Character.getNumericValue(ch);
						setToken( ch, Token.TokenCode.tcUnknown );
						break;
				}
		}
	}

   // Set the type of the current token - important for IceTagger
    // Used when the input is pretokenized
    private void setCurrentTokenType(boolean lastInSentence, boolean nextToLastInSentence)
    {
        currToken.tokenCode = Token.TokenCode.tcUnknown;    // The default

        char ch = currToken.lexeme.charAt(0);   // Get the first character
        int len = currToken.lexeme.length();

        if (Character.isDigit(ch))
            currToken.tokenCode = Token.TokenCode.tcNumber;
        else {
            switch (ch) {
                case ',':   if (len==1) currToken.tokenCode = Token.TokenCode.tcComma;
                            break;
                case ';':   if (len==1) currToken.tokenCode = Token.TokenCode.tcSemicolon;
                            break;
                case '!':   if (len==1) {
                                if (lastInSentence || nextToLastInSentence)
                                    currToken.tokenCode = Token.TokenCode.tcEOS;
                                else
                                    currToken.tokenCode = Token.TokenCode.tcExclamation;
                            }
                            break;
                case '?':   if (len==1) {
                                if (lastInSentence || nextToLastInSentence) // To make it consistent with the tokenize method
                                    currToken.tokenCode = Token.TokenCode.tcEOS;
                                else
                                    currToken.tokenCode = Token.TokenCode.tcQuestion;
                            }
                            break;
                case '.':   if (len==1 || (currToken.lexeme.charAt(1) == '.')) {
                                if (lastInSentence || nextToLastInSentence) // To make it consistent with the tokenize method
                                    currToken.tokenCode = Token.TokenCode.tcEOS;
                                else
                                    currToken.tokenCode = Token.TokenCode.tcPeriod;
                            }
                            break;
                case ':':   if (len==1) {
                                if (lastInSentence)
                                    currToken.tokenCode = Token.TokenCode.tcEOS;
                                else
                                    currToken.tokenCode = Token.TokenCode.tcColon;
                            }
                            break;
                case'"':    if (len==1) {
                                if (lastInSentence)
                                    currToken.tokenCode = Token.TokenCode.tcEOS;
                                else
                                    currToken.tokenCode = Token.TokenCode.tcDoubleQuote;
                            }
                            break;
                case'\'':   if (len==1)
						            currToken.tokenCode = Token.TokenCode.tcSingleQuote;
                            else {
                                     if( Character.isDigit( currToken.lexeme.charAt(1))) // '25
                                        currToken.tokenCode = Token.TokenCode.tcNumber;
                                }
                            break;
                case'«':    if (len==1) currToken.tokenCode = Token.TokenCode.tcLArrow;
						    break;
				case'»':    if (len==1) currToken.tokenCode = Token.TokenCode.tcRArrow;
						    break;
                case'-':    if (len==1)
							    currToken.tokenCode = Token.TokenCode.tcHyphen;
						    else
							    if( Character.isDigit( currToken.lexeme.charAt(1)))  //-3.4
								    currToken.tokenCode = Token.TokenCode.tcNumber;
                            break;
                case'_':    if (len==1)
							    currToken.tokenCode = Token.TokenCode.tcUnderscore;
						    else
							    if( Character.isDigit( currToken.lexeme.charAt(1)))  //_3.4
								    currToken.tokenCode = Token.TokenCode.tcNumber;
                            break;
                case'(':    if (len==1) currToken.tokenCode = Token.TokenCode.tcLParen;
						    break;
				case')':    if (len==1) currToken.tokenCode = Token.TokenCode.tcRParen;
						    break;
				case'[':    if (len==1) currToken.tokenCode = Token.TokenCode.tcLBracket;
						    break;
			    case']':    if (len==1) currToken.tokenCode = Token.TokenCode.tcRBracket;
                            break;
                case'{':    if (len==1) currToken.tokenCode = Token.TokenCode.tcLCurlyBracket;
						    break;
			    case'}':    if (len==1) currToken.tokenCode = Token.TokenCode.tcRCurlyBracket;
                            break;
                case'*':    if (len==1) currToken.tokenCode = Token.TokenCode.tcStar;
						    break;
                case'&':    if (len==1) currToken.tokenCode = Token.TokenCode.tcAnd;
						    break;
				case'#':    if (len==1) currToken.tokenCode = Token.TokenCode.tcNumberSign;
						    break;
				case'=':    if (len==1) currToken.tokenCode = Token.TokenCode.tcEqualSign;
						    break;
			    case'/':    if (len==1) currToken.tokenCode = Token.TokenCode.tcSlash;
						    break;
				case'\\':   if (len==1) currToken.tokenCode = Token.TokenCode.tcBackSlash;
						    break;
				case'<':    if (len==1) currToken.tokenCode = Token.TokenCode.tcLess;
						    break;
				case'>':    if (len==1) currToken.tokenCode = Token.TokenCode.tcGreater;
						    break;
                case '^':   if (len==1) currToken.tokenCode = Token.TokenCode.tcHat;
                            break;
                case'+':    if (len==1)
							    currToken.tokenCode = Token.TokenCode.tcPlus;
						    else
							    if( Character.isDigit( currToken.lexeme.charAt(1)))  //+3.4
								    currToken.tokenCode = Token.TokenCode.tcNumber;
                            break;

				case'±':    if (len==1)
							    currToken.tokenCode = Token.TokenCode.tcPlusMinus;
						    else
							    if( Character.isDigit( currToken.lexeme.charAt(1)))  //±3.4
								    currToken.tokenCode = Token.TokenCode.tcNumber;
                            break;
				case'$':    if (len==1)
							    currToken.tokenCode = Token.TokenCode.tcDollar;
                            else
                                currToken.tokenCode = Token.TokenCode.tcNumber;

						    break;
            }
        }

    }

}
