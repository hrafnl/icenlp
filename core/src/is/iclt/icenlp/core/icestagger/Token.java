package is.iclt.icenlp.core.icestagger;

public class Token {
    public final static int TOK_UNKNOWN     = 0;
    public final static int TOK_SPACE       = 1;
    public final static int TOK_SPACES      = 2;
    public final static int TOK_NEWLINE     = 3;
    public final static int TOK_NEWLINES    = 4;
    public final static int TOK_SYMBOL      = 5;
    public final static int TOK_NUMBER      = 6;
    public final static int TOK_EMAIL       = 7;
    public final static int TOK_URL         = 8;
    public final static int TOK_SMILEY      = 9;
    public final static int TOK_SENT_FINAL  = 10;
    public final static int TOK_LATIN       = 11;
    public final static int TOK_GREEK       = 12;
    public final static int TOK_ARABIC      = 13;
    public final static int TOK_HANZI       = 14;
    public final static int TOK_HANGUL      = 15;
    public final static int TOK_KANA        = 16;
    public final static int TOK_NAGARI      = 17;

    public final static int TOK_TYPES       = 18;

    public final int type;
    public final String value;
    public final int offset;
    private final boolean isCapitalizedFlag;

    public Token(int type, String value, int offset) {
        this.type = type;
        this.value = value;
        this.offset = offset;

        boolean capitalized = false;
        if(Character.isUpperCase(value.charAt(0))) {
            for(int i=1; i<value.length(); i++) {
                if(Character.isLowerCase(value.charAt(i))) {
                    capitalized = true;
                    break;
                }
            }            
        }
        isCapitalizedFlag = capitalized;
    }

    public boolean isSpace() {
        return (type >= TOK_SPACE && type <= TOK_NEWLINES);
    }

    /**
     * Returns true if the first letter is capitalized, but not all of them.
     */
    public boolean isCapitalized() {
        return isCapitalizedFlag;
    }
}

