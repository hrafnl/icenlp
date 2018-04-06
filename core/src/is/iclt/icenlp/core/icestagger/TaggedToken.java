package is.iclt.icenlp.core.icestagger;

/**
 * Class representing a (partially) tagged token.
 */
public class TaggedToken {
    public Token token;
    public String id;
    public String textLower;
    public String lf;
    public int posTag;
    public int neTag;
    public int neTypeTag;

    /**
     * Creates a TaggedToken given a Token, with unspecified tags.
     */
    public TaggedToken(Token token, String id) {
        this.token = token;
        this.id = id;
        textLower = token.value.toLowerCase();
        lf = null;
        posTag = -1;
        neTag = -1;
        neTypeTag = -1;
    }

    /**
     * Creates a TaggedToken that is a shallow copy of another TaggedToken.
     */
    public TaggedToken(TaggedToken taggedToken) {
        token = taggedToken.token;
        textLower = taggedToken.textLower;
        id = taggedToken.id;
        lf = taggedToken.lf;
        posTag = taggedToken.posTag;
        neTag = taggedToken.neTag;
        neTypeTag = taggedToken.neTypeTag;
    }

    /**
     * Returns true iff the two tokens do not contradict each other.
     */
    public boolean consistentWith(TaggedToken t) {
        if(posTag >= 0 && t.posTag >= 0 && posTag != t.posTag) return false;
        if(lf != null && t.lf != null && !lf.equals(t.lf)) return false;
        if(neTag >= 0 && t.neTag >= 0 && neTag != t.neTag) return false;
        if(neTypeTag >= 0 && t.neTypeTag >= 0 && neTypeTag != t.neTypeTag)
            return false;
        return true;
    }
}

