package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.*;

%%

%include GenericTokenizer.inc
%class EnglishTokenizer

ShortURL        = [a-zA-Z]+ "." ( se | nu | net | com | org )
EnglishNumber   = -? [:digit:]+ ( [\.] [:digit:]+ )?
EnglishSimp     = ( ( ' s  ) )
EnglishWord     = ( {LatinOrDigit}+ - )* {LatinOrDigit}+ ( ' t )?
EnglishAbbr     = ( e \.? " "? g \.? ) |
                  ( i \.? " "? e \.? ) |
                  ( ( etc | mr | mrs | dr | prof ) \.? )
Ordinal         = ( [0-9]+ ( :? th)? ) | ( [0-9]* 1 ( :? st )? ) |
                  ( [0-9]* 2 ( :? nd )? ) | ( [0-9]* 2 ( :? rd )? )

%{
public ArrayList<Token> readSentence() throws IOException {
    ArrayList<Token> sentence = new ArrayList<Token>();
    Token token, lastNonSpace = null, lastSpace = null;

    while((token = yylex()) != null) {
        if(token.isSpace()) {
            if(token.type == Token.TOK_NEWLINES) {
                if(!sentence.isEmpty()) return sentence;
            }
            lastSpace = token;
        } else {
            if(!sentence.isEmpty()) {
                if(lastNonSpace != null &&
                   lastNonSpace.value.endsWith(".") &&
                   lastNonSpace.value.length() > 1 &&
                   token.isCapitalized())
                {
                    yypushback(yylength());
                    return sentence;
                } else if(token.type == Token.TOK_SENT_FINAL) {
                    if(lastNonSpace != null &&
                       lastNonSpace.value.length() == 1 &&
                       lastNonSpace.isCapitalized())
                    {
                    } else {
                        sentence.add(token);
                        return sentence;
                    }
                }
            }
            // I admit this is not pretty.
            if(token.type == Token.TOK_LATIN) {
                String textLower = token.value.toLowerCase();
                int length = token.value.length();
                if(textLower.endsWith("n't")) {
                    if(textLower.equals("can't")) {
                        sentence.add(new Token(
                            Token.TOK_LATIN, token.value.substring(0,3),
                            token.offset));
                        sentence.add(new Token(
                            Token.TOK_LATIN, token.value.substring(2),
                            token.offset+2));
                    } else {
                        sentence.add(new Token(
                            Token.TOK_LATIN,
                            token.value.substring(0,length-3),
                            token.offset));
                        sentence.add(new Token(
                            Token.TOK_LATIN,
                            token.value.substring(length-3),
                            token.offset+length-3));
                    }
                } else {
                    sentence.add(token);
                }
            } else {
                sentence.add(token);
            }
            lastNonSpace = token;
        }
    }
    if(sentence.isEmpty()) return null;
    return sentence;
}
%}

%%
{SentID}            { sentID = yytext().substring(6, yylength()-1); }
{URL}               {
    return(new Token(Token.TOK_URL,yytext(),yychar)); }
{ShortURL}          {
    return(new Token(Token.TOK_URL,yytext(),yychar)); }
{Email}             {
    return(new Token(Token.TOK_EMAIL,yytext(),yychar)); }
{Smiley}            {
    return(new Token(Token.TOK_SMILEY,yytext(),yychar)); }
{Ordinal}           {
    return(new Token(Token.TOK_NUMBER,yytext(),yychar)); }
{EnglishSimp}       {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{EnglishAbbr}       {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{EnglishWord}       {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{LatinWord}         {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{EnglishNumber}     {
    return(new Token(Token.TOK_NUMBER,yytext(),yychar)); }
{GreekWord}         {
    return(new Token(Token.TOK_GREEK,yytext(),yychar)); }
{ArabicWord}        {
    return(new Token(Token.TOK_ARABIC,yytext(),yychar)); }
{NagariWord}        {
    return(new Token(Token.TOK_NAGARI,yytext(),yychar)); }
{Hanzi}             {
    return(new Token(Token.TOK_HANZI,yytext(),yychar)); }
{Kana}              {
    return(new Token(Token.TOK_KANA,yytext(),yychar)); }
{Hangul}            {
    return(new Token(Token.TOK_HANGUL,yytext(),yychar)); }
{Number}            {
    return(new Token(Token.TOK_NUMBER,yytext(),yychar)); }
{SentFinal}         {
    return(new Token(Token.TOK_SENT_FINAL,yytext(),yychar)); }
{Symbols}           {
    return(new Token(Token.TOK_SYMBOL,yytext(),yychar)); }
{Symbol}            {
    return(new Token(Token.TOK_SYMBOL,yytext(),yychar)); }
{Newlines}          {
    return(new Token(Token.TOK_NEWLINES,yytext(),yychar)); }
{Newline}           {
    return(new Token(Token.TOK_NEWLINE,yytext(),yychar)); }
{Spaces}            {
    return(new Token(Token.TOK_SPACES,yytext(),yychar)); }
{Space}             {
    return(new Token(Token.TOK_SPACE,yytext(),yychar)); }
.                   {
    return(new Token(Token.TOK_UNKNOWN,yytext(),yychar)); }

