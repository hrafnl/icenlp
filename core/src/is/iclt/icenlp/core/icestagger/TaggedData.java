package is.iclt.icenlp.core.icestagger;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.TokenTags;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;

import java.io.*;
import java.util.*;

/**
 * Class managing I/O of tagged tokens.
 */
public class TaggedData implements Serializable {
    static final long serialVersionUID = -4803197852676591641L;

    private TagSet posTagSet;
    private TagSet neTagSet;
    private TagSet neTypeTagSet;
    private String language;

    // IDs in neTagSet
    static public final int NE_O = 0;
    static public final int NE_B = 1;
    static public final int NE_I = 2;
    static public final int NE_TAGS = 3;

    /**
     * Create a new TaggedData object.
     * The TagSet object for the NE segment values are initialized with values
     * corresponding to the NE_* constants in this class.
     * The "pos" and "netype" TagSet objects are created but left empty.
     */
    public TaggedData(String language) {
        this.language = language;
        try {
            posTagSet = new TagSet();
            neTagSet = new TagSet();
            neTagSet.addTag("O");
            neTagSet.addTag("B");
            neTagSet.addTag("I");
            neTypeTagSet = new TagSet();
        } catch(TagNameException e) {
            assert false;
        }
    }

    public String getLanguage() {
        return language;
    }

    public TagSet getPosTagSet() {
        return posTagSet;
    }

    public TagSet getNETagSet() {
        return neTagSet;
    }

    public TagSet getNETypeTagSet() {
        return neTypeTagSet;
    }

    /**
     * Writes an array of sentences.
     *
     * @param writer    somewhere to write the data
     * @param sentences array of sentences (arrays of tokens)
     * @param plain     use plain output rather than CoNLL?
     * @throws TagNameException if any tag value is invalid
     * @throws IOException from the writer
     */
    public void writeConll(
    Appendable writer, TaggedToken[][] sentences, boolean plain)
    throws TagNameException, IOException {
        for(TaggedToken[] tokens : sentences) {
            int i = 0;
            for(TaggedToken token : tokens) {
                writer.append(tokenToString(token, i, plain) + "\n");
                i++;
            }
            writer.append("\n");
        }
    }

    public void writeConllSentence(
    Appendable writer, TaggedToken[] sentence, boolean plain)
    throws TagNameException, IOException {
        TaggedToken[][] sentences = new TaggedToken[1][];
        sentences[0] = sentence;
        writeConll(writer, sentences, plain);
    }

    /**
     * Writes an array of tokens, and (if differing) corresponding gold data.
     *
     * @param writer    somewhere to write the data
     * @param tokens    tokens to write
     * @param goldTokens corresponding array of gold standard data
     * @param plain     use plain output rather than CoNLL?
     * @throws TagNameException if any tag value is invalid
     * @throws IOException from the writer
     */
    public void writeConllGold(
    Appendable writer, TaggedToken[] tokens, TaggedToken[] goldTokens,
    boolean plain)
    throws TagNameException, IOException {
        assert tokens.length == goldTokens.length;
        for(int i=0; i<tokens.length; i++) {
            TaggedToken token = tokens[i];
            TaggedToken gold = goldTokens[i];
            writer.append(tokenToString(token, i, plain) + "\n");
            if(!token.consistentWith(gold)) {
                writer.append("#" + tokenToString(gold, i, plain) + "\n");
            }
        }
        writer.append("\n");
    }

    /**
     * Converts a single token to a line in a CoNLL file.
     *
     * @param token     tokens to convert
     * @param idx       0-based index within the sentence
     * @param plain     use plain output rather than CoNLL?
     * @throws TagNameException if any tag value is invalid
     */
    private String tokenToString(TaggedToken token, int idx, boolean plain)
    throws TagNameException {
        if(plain) return token.token.value + "\t" +
                         posTagSet.getTagName(token.posTag);
        String[] pos = null;
        String neTag = null;
        String neType = null;
        if(token.posTag >= 0)
            pos = posTagSet.getTagName(token.posTag).split("\\|", 2);
        if(token.neTag >= 0)
            neTag = neTagSet.getTagName(token.neTag);
        if(token.neTypeTag >= 0)
            neType = neTypeTagSet.getTagName(token.neTypeTag);
        return
            (idx+1) + "\t" +
            token.token.value + "\t" +
            ((token.lf == null)? "" : token.lf) + "\t" +
            ((pos == null)? "_" : pos[0]) + "\t" +
            ((pos == null)? "_" : pos[0]) + "\t" +
            ((pos == null || pos.length < 2)? "_" : pos[1]) + "\t" +
            "_\t" +
            "_\t" +
            "_\t" +
            "_\t" +
            ((neTag == null)? "_" : neTag) + "\t" +
            ((neType == null)? "_" : neType) + "\t" +
            ((token.id == null)? "_" : token.id);
    }

    /**
     * Reads a number of .conll files.
     *
     * @param filenames names of files
     * @param extend    if true, unknown tags are created
     * @param plain     use plain format rather than CoNLL?
     * @return          array of sentences (arrays of TaggedToken)
     * @throws FormatException if the syntax is invalid
     * @throws TagNameException if any tag value is invalid
     * @throws IOException from the reader
     */
    public TaggedToken[][][] readConllFiles(
    String[] filenames, boolean extend, boolean plain)
    throws FormatException, TagNameException, IOException {
        int nFiles = filenames.length;
        TaggedToken[][][] files = new TaggedToken[nFiles][][];
        int fileIdx=0;
        for(String name : filenames) {
            String id = (new File(name)).getName().split("\\.")[0];
            files[fileIdx++] = readConll(name, id, extend, plain);
        }
        return files;
    }

    /**
     * Reads all sentences in the given file, until EOF.
     *
     * @param filename  name of the file to read
     * @param fileID    identifier of the file (used for token IDs)
     * @param extend    if true, unknown tags are created
     * @param plain     use plain format rather than CoNLL?
     * @return          null on EOF, otherwise array of tokens
     * @throws FormatException if the syntax is invalid
     * @throws TagNameException if any tag value is invalid
     * @throws IOException from the reader
     */
    public TaggedToken[][] readConll(
    String filename, String fileID, boolean extend, boolean plain)
    throws FormatException, TagNameException, IOException {
        if(fileID == null) {
            fileID = (new File(filename)).getName().split("\\.")[0];
        }
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(filename), "UTF-8"));
        TaggedToken[][] data = readConll(reader, fileID, extend, plain);
        reader.close();
        return data;
    }

    /**
     * Reads all sentences in the given file, until EOF.
     *
     * @param reader    BufferedReader to read from
     * @param fileID    identifier of the file (used for token IDs)
     * @param extend    if true, unknown tags are created
     * @param plain     use plain format rather than CoNLL?
     * @return          null on EOF, otherwise array of tokens
     * @throws FormatException if the syntax is invalid
     * @throws TagNameException if any tag value is invalid
     * @throws IOException from the reader
     */
    public TaggedToken[][] readConll(
    BufferedReader reader, String fileID, boolean extend, boolean plain)
    throws FormatException, TagNameException, IOException {
        ArrayList<TaggedToken[]> sentences = new ArrayList<TaggedToken[]>();
        ArrayList<TaggedToken> sentence = new ArrayList<TaggedToken>();
        Tokenizer tokenizer;
        if(language.equals("en"))
            tokenizer = new EnglishTokenizer(new StringReader(""));
        else
            tokenizer = new LatinTokenizer(new StringReader(""));
        String line;
        int sentIdx = 0;
        int tokIdx = 0;
        while((line = reader.readLine()) != null) {
            if(line.equals("")) {
                if(sentence.size() > 0) {
                    TaggedToken[] tokensArray =
                        new TaggedToken[sentence.size()];
                    sentences.add(sentence.toArray(tokensArray));
                    sentence = new ArrayList<TaggedToken>();
                    sentIdx++;
                    tokIdx = 0;
                }
                continue;
            }
            if(line.startsWith("#")) continue;
            String[] fields = (plain)? line.split("\\s+") : line.split("\t");
            String posString = null;
            String neString = null;
            String neTypeString = null;
            String tokenID = null;
            String text = null;
            String lf = null;
            int nFields = fields.length;
            if(plain) {
                if(nFields != 2) {
                    throw new FormatException(
                        "Expected 2 fields, found " + fields.length +
                        " in: " + line);
                }
                text = fields[0];
                posString = fields[1];
            } else {
                if(nFields < 6)
                    throw new FormatException(
                        "Expected at least 6 fields, found " + fields.length +
                        " in: " + line);
                text = fields[1];
                lf = fields[2];
                if(lf.equals("") || (lf.equals("_") && !text.equals("_")))
                    lf = null;
                if(!fields[3].equals("_")) {
                    if(!(fields[5].equals("") || fields[5].equals("_")))
                        posString = fields[3]+"|"+fields[5];
                    else
                        posString = fields[3];
                }
                if(nFields >= 12 && !fields[10].equals("_"))
                    neString = fields[10];
                if(nFields >= 12 && !fields[11].equals("_"))
                    neTypeString = fields[11];
                if(nFields >= 13 && !fields[12].equals("_"))
                    tokenID = fields[12];
            }
            if(tokenID == null) {
                tokenID = fileID + ":" + sentIdx + ":" + tokIdx;
            }
            TaggedToken token;
            // TODO: consider interpreting z as the offset if the ID is on the
            // form x:y:z
            if(tokenizer == null) {
                token = new TaggedToken(
                    new Token(Token.TOK_UNKNOWN, text, 0), tokenID);
            } else {
                // Use the tokenizer to find the token type of this file
                tokenizer.yyreset(new StringReader(text));
                Token subToken = tokenizer.yylex();
                // Note that only the first subtoken is used, in case the
                // token is complex.
                token = new TaggedToken(
                    new Token(subToken.type, text, 0), tokenID);
            }
            int posTag = -1, neTag = -1, neTypeTag = -1;
            if(posString != null)
                posTag = posTagSet.getTagID(posString, extend);
            if(neString != null) {
                if(neString.equals("U")) neString = "B";
                else if(neString.equals("L")) neString = "I";
                neTag = neTagSet.getTagID(neString, false);
            }
            if(neTypeString != null)
                neTypeTag = neTypeTagSet.getTagID(neTypeString, extend);
            token.lf = lf;
            token.posTag = posTag;
            token.neTag = neTag;
            token.neTypeTag = neTypeTag;
            sentence.add(token);

            tokIdx++;
        }
        // In case the last sentence was not followed by an empty line, make
        // sure to add it.
        if(sentence.size() > 0) {
            TaggedToken[] tokensArray =
                new TaggedToken[sentence.size()];
            sentences.add(sentence.toArray(tokensArray));
        }
        if(sentences.size() == 0) return null;
        TaggedToken[][] sentenceArray = new TaggedToken[sentences.size()][];
        return sentences.toArray(sentenceArray);
    }

}

