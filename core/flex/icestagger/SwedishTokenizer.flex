package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.*;

%%

%include GenericTokenizer.inc
%class SwedishTokenizer

ShortURL        = [a-zA-Z]+ "." ( se | nu | net | com | org )
SwedishSymbol   = ":-"
SwedishNumber   = -? [:digit:]+ ( [,\.] [:digit:]+ )?
SwedishSuffix   = ( ' s ) |
                  ( : ( n | ns | en | ens | a | ar | ars | arna | arnas | s ) )
SwedishWord     = ( {LatinOrDigit}+ - )* {LatinOrDigit}+
                  ( {SwedishSuffix} | - )?
SwedishAbbr     = ( t \.? " "? ex \.? ) |
                  ( bl \.? " "? a \.? ) |
                  ( m \.? " "? fl \.? ) |
                  ( ( f | e ) \.? " "? kr \.? ) |
                  ( fr \.? " "? o  \.? " "? m \.? ) |
                  ( t \.? " "? o  \.? " "? m \.? ) |
                  ( o \.? " "? s \.? " "? v \.? ) |
                  ( fr \.? " "? o  \.? " "? m \.? ) |
                  ( ( etc | osv | ca ) \.? )
Ordinal         = ( [0-9]+ ( :? e)? ) | ( [0-9]*[12] ( :? a )? )

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
            sentence.add(token);
            lastNonSpace = token;
        }
    }
    if(sentence.isEmpty()) return null;
    return sentence;
}
%}

%%
{SentID}            { yychar = -yylength();
                      sentID = yytext().substring(6, yylength()-1); }
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
{SwedishAbbr}       {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{SwedishWord}       {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{LatinWord}         {
    return(new Token(Token.TOK_LATIN,yytext(),yychar)); }
{SwedishNumber}     {
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
{SwedishSymbol}     {
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

