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

import java.util.regex.Pattern;

/**
 * Encapsulates tokens.
 * @author Hrafn Loftsson
 */


public class Token {
    public enum TokenCode {tcWord, tcNumber, tcEOS, tcComma, tcColon, tcSemicolon, tcHyphen, tcCurrency,
                tcDoubleQuote, tcDownQuote, tcUpQuote, tcLParen, tcRParen, tcLBracket, tcRBracket,
                tcWhitespace, tcAbbrev, tcUnknown, tcNone, tcExclamation, tcQuestion, tcPeriod,
                tcSlash,  tcBackSlash, tcUrl, tcLess, tcGreater, tcSingleQuote, tcMultiWord,
                tcLArrow, tcRArrow, tcPlus, tcStar, tcDollar, tcAnd, tcNumberSign, tcEqualSign,
                tcPlusMinus, tcUnderscore, tcArrow, tcLCurlyBracket, tcRCurlyBracket, tcPound,
                tcHat, tcWebAddress, tcBackQuote
                //, tcTwoSingleQuotes, tcTwoBackQuotes
                }
    
    public enum MWECode {begins, ends, none}
   
    public String lexeme;               // The lexeme
    public TokenCode tokenCode;         // The token code
    public MWECode mweCode;

    // hs added
    public boolean linkedToPreviousWord;
    public String preSpace = null;
    

    public Token()
    {
        this.lexeme = null;
        this.tokenCode = TokenCode.tcNone;
        this.linkedToPreviousWord = false;
        
    }

    public Token(String str, TokenCode tc)
    {
        this.lexeme = str;
        this.tokenCode = tc;
        this.mweCode = MWECode.none;
        this.linkedToPreviousWord = false;
    }

    public boolean isQuote()
    {
       switch (tokenCode) {
            case tcDownQuote:
            case tcUpQuote:
            case tcDoubleQuote:
            case tcSingleQuote:
            case tcLArrow:
            case tcRArrow:
               return true;
            default: return false;
       }
    }

    public boolean isPunctuation()
    {
     switch (tokenCode) {
        case tcEOS:
        case tcPeriod:
        case tcComma:
        case tcQuestion:
        case tcExclamation:
        case tcCurrency:
        case tcSlash:
        case tcBackSlash:
        case tcBackQuote:
        case tcDownQuote:
        case tcUpQuote:
        case tcDoubleQuote:
        case tcSingleQuote:
        case tcLParen:
        case tcRParen:
        case tcLBracket:
        case tcRBracket:
        case tcColon:
        case tcSemicolon:
        case tcHyphen:
        case tcLess:
        case tcGreater:
        case tcLArrow:
        case tcRArrow:
        case tcEqualSign:
        case tcUnderscore:
        case tcPlus:
        case tcPlusMinus:
        case tcStar:
        case tcArrow:
        case tcNumberSign:
        case tcDollar:
        case tcLCurlyBracket:
        case tcRCurlyBracket:
        case tcHat:
                    return true;
        default:    return false;

    }
   }

   public boolean isEOS()
   {
       return (tokenCode == TokenCode.tcEOS);
   }

    public boolean isMultiWord()
    {
        for (int i=0; i<lexeme.length(); i++)
            if (lexeme.charAt(i) == '_')
                return true;

        return false;
    }

    public boolean isDate() {
       // 20.12.2005
       return lexeme.matches("\\d+(\\.|/)\\d+(\\.|/)\\d+") || lexeme.matches(Tokenizer.datePatternStr);  

    }

    public boolean isTime() {
       return lexeme.matches("\\d+:\\d+"); // 20:25
    }

  /*
  *  Used if the token is a multiword or an abbreviaton
  */
    public String[] splitLexeme(String regex)
    {
       //String regex = "_";
       String[] strs;
       Pattern p = Pattern.compile(regex);
       // Get all the individual lexemes as strings
       strs = p.split(lexeme);

       return strs;
    }

public boolean isUnknown() {
    return false;
}
    
public String toStringWithCode()
{
    return lexeme + " <" + tokenCode.toString() + ">";
}

public String toString()
{
    return lexeme;
}

}
