package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.tokenizer.Token.MWECode;

/**
* Represents word within a sentence and keps
* information about it: Lexeme, part-of-speech tag
* and the lemma.
* @author hlynurs
*/

public class Word 
{
    // Member variables.
    private String lexeme;
    private String tag;
    private String lemma;
    public MWECode mweCode;

    // Constructor for the class.
    public Word(String lexeme, String tag, MWECode mweCode)
    {
        this.lexeme = lexeme;
        this.tag = tag;
        this.mweCode = mweCode;
    }
    
    public Word(String lexeme, String lemma, String tag, MWECode mweCode)
    {
        this.lexeme = lexeme;
        this.tag = tag;
        this.mweCode = mweCode;
        this.lemma = lemma;
    }
    
    /**
     * Getter function for the lexeme member variable.
     * @return String object that contains the lexeme of
     * of the word.
     */
    public String getLexeme()
    {
        return lexeme;
    }

    /**
     * Setter function for the lexeme member variable.
     * @param lexeme String object that contains the
     * lexeme of the word.
     */
    public void setLexeme(String lexeme)
    {
        this.lexeme = lexeme;
    }

    /**
     * Getter function for the tag member variable.
     * @return String object that contains the tag.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Setter function for the tag member variable.
     * @param tag String object that contains the tag.
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * Getter function for the lemma member variable.
     * @return String object that contains the lemma.
     */
    public String getLemma()
    {
        return lemma;
    }
    
    /**
     * Setter function for the lemma member variable.
     * @param lemma String object that contains the lemma.
     */
    public void setLemma(String lemma)
    {
        this.lemma = lemma;
    }

    /**
     * Setter function for the MWECode member
     * variable.
     * @return MWEcode.
     */
    public MWECode getMWECode()
    {
        return this.mweCode;
    }
}
