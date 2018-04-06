package is.iclt.icenlp.core.icestagger;
import java.io.*;
import java.util.*;

public abstract class Tokenizer {
    abstract public Token yylex() throws IOException;
    abstract public void yyclose() throws IOException;
    abstract public ArrayList<Token> readSentence() throws IOException;
    abstract public void yyreset(java.io.Reader reader);
    public int yychar;
    public String sentID;
}
