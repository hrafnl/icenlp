/* The following code was generated by JFlex 1.4.1 on 6.8.2009 22:40 */

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

 /*This .flex file is a definition of a "rule compiler" which is used to generate */
 /* local rules used by IceTagger.  See readme.txt".  */

package is.iclt.icenlp.flex.icetagger;

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 6.8.2009 22:40 from the specification file
 * <tt>genIceRules.flex</tt>
 */
public class genIceRules {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\1\1\4\1\0\2\1\22\0\1\1\16\0\1\3\13\0"+
    "\1\2\5\0\1\7\1\16\1\11\1\15\1\13\1\21\1\17\1\0"+
    "\1\20\2\0\1\12\1\0\1\14\3\0\1\10\1\5\1\6\1\23"+
    "\2\0\1\22\uffa7\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\10\1\11\0\1\2\11\0\1\3\7\0\1\4"+
    "\2\0\1\5\7\0\1\6\1\0\1\7\2\0\1\10"+
    "\2\0\1\11\1\12";

  private static int [] zzUnpackAction() {
    int [] result = new int[57];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\24\0\50\0\74\0\120\0\144\0\170\0\214"+
    "\0\240\0\50\0\74\0\264\0\310\0\334\0\360\0\u0104"+
    "\0\u0118\0\u012c\0\24\0\u0140\0\u0154\0\u0168\0\u017c\0\u0190"+
    "\0\u01a4\0\u01b8\0\u01cc\0\u01e0\0\24\0\u01f4\0\u0208\0\u021c"+
    "\0\u0230\0\u0244\0\u0258\0\u026c\0\u0280\0\u0294\0\u02a8\0\24"+
    "\0\u02bc\0\u02d0\0\u02e4\0\u02f8\0\u030c\0\u0320\0\u0334\0\24"+
    "\0\u0348\0\u035c\0\u0370\0\u0384\0\24\0\u0398\0\u03ac\0\24"+
    "\0\24";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[57];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\2\1\4\1\3\1\5\2\2\1\6"+
    "\2\2\1\7\2\2\1\10\1\2\1\11\3\2\25\0"+
    "\1\12\1\0\1\13\1\12\22\0\1\14\26\0\1\15"+
    "\40\0\1\16\12\0\1\17\1\0\1\20\22\0\1\21"+
    "\31\0\1\22\2\0\4\14\1\23\17\14\7\0\1\24"+
    "\26\0\1\25\16\0\1\26\33\0\1\27\25\0\1\30"+
    "\5\0\1\31\2\0\1\31\6\0\1\32\20\0\1\33"+
    "\26\0\1\34\23\0\1\35\11\0\1\36\2\0\1\36"+
    "\4\0\1\37\32\0\1\40\3\0\1\41\1\31\1\0"+
    "\1\41\1\31\17\41\22\0\1\42\7\0\1\43\16\0"+
    "\1\44\2\0\1\44\17\0\1\45\1\36\1\0\1\45"+
    "\1\36\17\45\12\0\1\46\25\0\1\47\7\0\2\41"+
    "\1\50\21\41\20\0\1\51\14\0\1\52\12\0\1\53"+
    "\1\44\1\0\1\53\1\44\17\53\1\45\2\0\1\45"+
    "\1\0\17\45\7\0\1\54\15\0\1\55\2\0\1\55"+
    "\25\0\1\56\27\0\1\57\11\0\2\53\1\60\21\53"+
    "\5\0\1\61\16\0\1\62\1\55\1\0\1\62\1\55"+
    "\17\62\1\0\1\63\2\0\1\63\26\0\1\64\21\0"+
    "\1\65\16\0\1\62\2\0\1\62\1\0\17\62\1\66"+
    "\1\63\1\0\1\66\1\63\17\66\5\0\1\67\16\0"+
    "\2\66\1\70\21\66\5\0\1\71\16\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[960];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\7\1\11\0\1\11\11\0\1\11\7\0"+
    "\1\1\2\0\1\11\7\0\1\11\1\0\1\1\2\0"+
    "\1\11\2\0\2\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[57];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */
  String str;
  String[] strs;
  boolean firstRule=true;
  boolean ifExit=false;
  
  private static final String[] map = {	"PERSPRONOUN", "IceTag.WordClass.wcPersPronoun",
  					"POSSPRONOUN", "IceTag.WordClass.wcPossPronoun",
  					"DEMPRONOUN", "IceTag.WordClass.wcDemPronoun",
  					"REFLPRONOUN", "IceTag.WordClass.wcReflPronoun",
  					"PROPERNOUN", "IceTag.WordClass.wcProperNoun",
  					"ADJ", "IceTag.WordClass.wcAdj",
  					"ADVERB", "IceTag.WordClass.wcAdverb",
  					"NOUN", "IceTag.WordClass.wcNoun",
  					"PREP", "IceTag.WordClass.wcPrep",
  					"VERBINF", "IceTag.WordClass.wcVerbInf",
  					"INF", "IceTag.WordClass.wcInf",
  					"CONJREL", "IceTag.WordClass.wcConjRel",
  					"CONJ", "IceTag.WordClass.wcConj",
  					"ARTICLE", "IceTag.WordClass.wcArticle",
  					"NUMERAL", "IceTag.WordClass.wcNumeral",
  					"VERBPASTPART","IceTag.WordClass.wcVerbPastPart",
  					"VERB","IceTag.WordClass.wcVerb",
  					"NOMINATIVE", "IceTag.cNominative",
  					"ACCUSATIVE", "IceTag.cAccusative",
  					"DATIVE", "IceTag.cDative",
  					"GENITIVE", "IceTag.cGenitive",
  					"COMMA", "Token.TokenCode.tcComma",
  					"MASCULINE", "IceTag.cMasculine",
  					"FEMININE", "IceTag.cFeminine",
  					"NEUTER", "IceTag.cNeuter",
  					"prevTokenFirstTag", "((IceTag)prevToken.getFirstTag())",
  					"nextTokenFirstTag", "((IceTag)nextToken.getFirstTag())"
  					};
  
  private void errorExit(String errorStr)
  {
    	System.err.println(errorStr);
	System.exit(0);
  }
  
  private void printStartClass()
  {
  	System.out.println("package is.iclt.icenlp.flex.icetagger;");
  	System.out.println("import is.iclt.icenlp.core.tokenizer.*;");
  	System.out.println("import is.iclt.icenlp.core.utils.*;");
  	//System.out.println("import java.io.IOException;");
  	System.out.println();
	System.out.println("/**");
	System.out.println(" * Local reductionistic rules for Icelandic text.");
 	System.out.println(" * <br> Used by the IceTagger class.");
 	System.out.println(" * @author Hrafn Loftsson");
	System.out.println(" */");
  	System.out.println();
  	System.out.println("public class IceLocalRules {");
  	System.out.println("// This class is generated automatically from a .flex file");
	System.out.println("\tprivate IceLog logger=null;    // Logfile file");
	System.out.println("\tprivate boolean didDisambiguate=false;");
	System.out.println();
	System.out.println("\tpublic IceLocalRules(IceLog log)");
	System.out.println("\t{");
	System.out.println("\t\tlogger = log;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tpublic void setDisambiguateFlag(boolean flag)");
	System.out.println("\t{");
	System.out.println("\t\tdidDisambiguate=flag;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tpublic boolean getDisambiguateFlag()");
	System.out.println("\t{");
	System.out.println("\t\treturn didDisambiguate;");
	System.out.println("\t}");
	System.out.println();
	System.out.println("\tprivate void disAllowTag(IceTokenTags currToken, IceTag tag)");
	//System.out.println("\tthrows IOException");
	System.out.println("\t{");
	System.out.println("\t// Only disambiguate if more than one tag left");
	System.out.println("\t\tif (currToken.numTags() > 1)");
	System.out.println("\t\t{");
	System.out.println("\t\t\tString logStr = \"Local disambiguation: \" + currToken.toString();");
	System.out.println("\t\t\ttag.setValid(false);");
	System.out.println("\t\t\tlogStr = logStr + \" Disallowed \" + tag.getTagStr();");
	System.out.println("\t\t\tif (logger != null)");
	System.out.println("\t\t\t\tlogger.log(logStr);");
	System.out.println("\t\t\tdidDisambiguate = true;");
	System.out.println("\t\t}");
	System.out.println("\t}");
	System.out.println();
    }
  
  private void printEndClass()
  {
  	System.out.println("} // end class");
  }
  
  
  private void printFunctionStart(String func)
  {
  	System.out.println("public void check" + func + "(IceTag tag, IceTokenTags prevprevToken, IceTokenTags prevToken, IceTokenTags currToken, IceTokenTags nextToken, IceTokenTags nextnextToken)");
  	//System.out.println("throws IOException");
  	System.out.println("{");
  	printIfStart(false);
  }
  
  private void printFunctionEnd()
  {
  	printIfEnd();
  	System.out.println("}");
  	System.out.println();
  }
  
  private void printIfStart(boolean withElse)
  {
  	if (!withElse)
  		System.out.print("if(");
  	else
  		System.out.print("else if(");
  }
  
  private void printIfEnd()
  {
  	System.out.println();
    	System.out.println("\t)");
    	System.out.println(")");
    	if (ifExit)
    		System.out.println(")");
    	System.out.println("\tdisAllowTag(currToken, tag);");
    	if (ifExit)
    		System.out.println("}");
    	System.out.println();
  }
  
  private void printIfCondition(String str)
      {
      	System.out.println(str + " && ");
  }
  
  private void printIfExitCondition(String str)
  {
        	System.out.println(str + ")");
        	System.out.println("{");
        	printIfStart(false);
  }
  
  private String replace(String str)
  {
  	String key, value;
  	String changedStr = str;
  	for (int i=0; i<=map.length-2; i=i+2)
  	{
  		key = map[i];
  		value = map[i+1];
  		changedStr = changedStr.replace(key,value);
  	}
  	return changedStr;
  }
  
  private void printCode(String str)
  {
  	String codeStr;
  	codeStr = replace(str);
    	System.out.print(codeStr);
  }
  


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public genIceRules(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public genIceRules(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 66) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzPushbackPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead < 0) {
      return true;
    }
    else {
      zzEndRead+= numRead;
      return false;
    }
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 8: 
          { printEndClass();
          }
        case 11: break;
        case 2: 
          { System.out.print(yytext());
          }
        case 12: break;
        case 1: 
          { ;
          }
        case 13: break;
        case 7: 
          { str = yytext();
			strs = str.split("\\s");
			if (strs.length < 2)
			   errorExit("Missing function name after BEGIN");
			   
			String func = strs[1];
			printFunctionStart(func);
			firstRule = true;
          }
        case 14: break;
        case 3: 
          { printIfEnd();
			ifExit=false;
			printIfStart(true);
			firstRule = true;
          }
        case 15: break;
        case 6: 
          { str = yytext();
			str = str.replace("RULE","");
			str = str.replace(";","");
			
			if (firstRule) {
				printCode("\t(\n");
				printCode("\t(");
			}
			else {
			   	printCode(" ||\n");
				printCode("\t(");
			}
			printCode(str);
			printCode(")");
			
			firstRule = false;
          }
        case 16: break;
        case 9: 
          { str = yytext();
			str = str.replace("IFEXIT","");
			str = str.replace(";","");

			ifExit=true;
			printIfExitCondition(str);
          }
        case 17: break;
        case 5: 
          { str = yytext();
			str = str.replace("IF","");
			str = str.replace(";","");

			ifExit=false;
			printIfCondition(str);
          }
        case 18: break;
        case 10: 
          { printStartClass();
          }
        case 19: break;
        case 4: 
          { printFunctionEnd();	
			firstRule = false;
          }
        case 20: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return YYEOF;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }

  /**
   * Runs the scanner on input files.
   *
   * This is a standalone scanner, it will print any unmatched
   * text to System.out unchanged.
   *
   * @param argv   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String argv[]) {
    if (argv.length == 0) {
      System.out.println("Usage : java genIceRules <inputfile>");
    }
    else {
      for (int i = 0; i < argv.length; i++) {
        genIceRules scanner = null;
        try {
          scanner = new genIceRules( new java.io.FileReader(argv[i]) );
          while ( !scanner.zzAtEOF ) scanner.yylex();
        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+argv[i]+"\"");
        }
        catch (java.io.IOException e) {
          System.out.println("IO error scanning file \""+argv[i]+"\"");
          System.out.println(e);
        }
        catch (Exception e) {
          System.out.println("Unexpected exception:");
          e.printStackTrace();
        }
      }
    }
  }


}
