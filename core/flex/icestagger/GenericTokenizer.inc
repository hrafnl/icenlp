%public
%extends Tokenizer
%type Token
%unicode
%char
%caseless

SentID      = \[ "stid:" [a-zA-Z0-9\-_]+ \]

Space       = [ \t\u00A0\u2000-\u200d\u3000]
Spaces      = {Space} {Space}+
Newline     = {Space}* ( \r?\n | [\u0085\u2028\u2029] ) {Space}*
Newlines    = {Newline} {Newline}+
Number      = [:digit:]+

URL         = ( ( ( http | ftp ) s? "://" ) | www\. )
              [a-zA-Z0-9\-]+ ( \. [a-zA-Z0-9\-]+ )*
              ( ":" [0-9]+ )? ( \/ [a-zA-Z0-9:#@%;$()~_?\+\-=\\\.,&]* )*
Email       = [a-zA-Z0-9\._%+\-]+@[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,4}
Smiley      = ( [:;x] '? -? [\(\)DdPpSs\$]+ ) | ( [\(\)DdPpSs\$]+ -? [:;] ) |
              ( [:;] o ) | ( = [\(\)DdPp]+ ) | ( [\(\)DdPp]+ = ) |
              ( \^+ _* \^+ ) | ( o _ o ) | ( < 3+ ) | ( "(Y)" )
Symbols     = ( \/+ ) | ( <+ -+ ) | ( -+ >+ ) | ( ,+ )

SentFinal   = ( \.+ [!?]* ) | [!?]+
Symbol      = [\x21-\x2f\x3a-\x40\x5b-\x60\x7b-\x7e\u00a1-\u00bf×÷] |
              [\u2010-\u2044\u3000-\u301f]
LatinOrDigit= [0-9A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u017f]
LatinLetter = [A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u017f]
LatinWord   = {LatinLetter}+
GreekLetter = [\u0370-\u03ff\u1f00-\u1fff]
GreekWord   = {GreekLetter}+
ArabicLetter= [\u0600-\u06ff\u0750-\u077f\ufb50-\ufdff\ufe70-\ufeff]
ArabicWord  = {ArabicLetter}+
NagariLetter= [\u0900-\u097f\u1cd0-\u1cff\ua8e0-\ua8ff]
NagariWord  = {NagariLetter}+
Kana        = [\u3040-\u30ff\uff65-\uff9f]
Hangul      = [\u1100-\u11ff\u3130-\u318f\ua960-\ua97f\ud7b0-\ud7ff]
/* This is a really ugly way to include range 0x20000 to 0x2b73f (and more) */
Hanzi       = [\u3400-\u4dbf\u4e00-\u9fff] | [\ud840-\ud86f][\ud800-\udfff]

