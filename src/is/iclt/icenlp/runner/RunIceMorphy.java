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
package is.iclt.icenlp.runner;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Tokenizer;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.utils.IceLog;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;

import java.io.*;
import java.util.Properties;

/**
 * Runs IceMorphy.
 * @author Hrafn Loftsson
 */
public class RunIceMorphy {
    private String inputFile=null, outputFile=null, logFile=null, modeStr=null;
    private String dictionary=null, dictionaryBase=null;
    private String prefixesDictPath=null;
    private String endingsBaseDictPath=null, endingsDictPath=null, endingsProperDictPath=null;
    private String tagFrequencyFile=null, tokenDict = null;
    private String separatorStr=null;
    private String tagSeparatorStr=null;
    private boolean allTags=false;
    private static final String guessedStr="<GUESSED>";
    private static final String morphoStr="<MORPHO>";
    private static final String endingStr="<ENDING>";
    private static final String compoundStr="<COMPOUND>";
    private IceMorphyLexicons morphLex=null;
    private Lexicon tokLex;

    private void analyzeUnknown(IceTokenTags tok, IceMorphy morpho)
    {
        if (Character.isUpperCase(tok.lexeme.charAt(0)))    // mark it as proper noun if appropriate
          tok.setUnknownType(IceTokenTags.UnknownType.properNoun);

        morpho.morphoAnalysisToken(tok, null);  // no knowledge of previous tag
    }


    private void analyzeFile(String unknownWordFile, String outputFile, IceMorphy morpho, String separator, String tagSeparator, boolean returnAllTags)
    throws IOException
    {
        BufferedReader input;
        BufferedWriter output;
        String line;
        String words[];
        int linesRead=0;
        String str;
        IceTokenTags tok;

        Tokenizer tokenizer = new Tokenizer( Tokenizer.typeIceTokenTags, true, tokLex);
        output = FileEncoding.getWriter(outputFile);
        input = FileEncoding.getReader(unknownWordFile);

        // Read the line which consists of only one unknown word.  If more than one word in line then use first token.
        line = input.readLine();
        while (line != null)
        {
            linesRead++;
            if( linesRead % 100 == 0 )
                System.out.print( "Reading line nr: " + Integer.toString(linesRead) + "\r");
            if (line.equals(""))
                output.newLine();
            else
            {
              tokenizer.tokenize( line );
              tok = (IceTokenTags)tokenizer.tokens.get(0);  // get the first word
              // The lexicon load failes for some punctuation characters => assume all punctuation characters are known
              if (tok.isPunctuation())
              {
                tok.addTag(tok.lexeme);
                tok.setUnknown(false);
              }
              else
              {
                morpho.dictionaryTokenLookup(tok, false); // do a lookup
                if (tok.noTags())
                    morpho.dictionaryTokenLookup(tok, true); // do a lookup

                if (tok.noTags())
                {
                    tok.setUnknown(true);
                    tok.setUnknownType(IceTokenTags.UnknownType.none);
                    tok.setCompound(false);
                    analyzeUnknown(tok, morpho);
                }
                else
                    tok.setUnknown(false);
              }
              tok.cleanTags();
              if (returnAllTags)
                output.write(tok.lexeme + separator + tok.allTagStringsWithSeparator(tagSeparator));
              // Else return the most likely tag
              else
              {
                String maxTag = morpho.maxFrequency(tok);
                if (maxTag != null)
                    output.write(tok.lexeme + separator + maxTag);
                else
                    output.write(tok.lexeme + " " + "ERROR: Frequency not found!");
              }
              if (tok.isUnknown()) {
                str = " *";
                if (tok.isUnknownMorpho())
                {
                    if (tok.isCompound())
                        str = str  + " " + compoundStr;
                    else
                        str = str  + " " + morphoStr;
                }
                else if (tok.isUnknownEnding())
                    str = str  + " " + endingStr;
                else if (tok.isUnknownGuessed())
                    str = str  + " " + guessedStr;

                //output.write(" *");
                output.write(str);
              }
              output.newLine();

              tok.clearTags();
            }
            line = input.readLine();
        }
        output.flush();
        input.close();
        output.close();
        System.out.println( "Reading line nr: " + Integer.toString(linesRead) + "\r");
        //System.out.println("Read " + linesRead + " lines");
    }

    public void generateMissingTags(String lexFile, String outputFile, IceMorphy morpho, String separator, String tagSeparator)
    throws IOException
    {
        BufferedReader input;
        BufferedWriter output;
        String line;
        String items[];
        int linesRead=0;

        IceTokenTags tok = new IceTokenTags();
        output = FileEncoding.getWriter(outputFile);
        input = FileEncoding.getReader(lexFile);
        String tags="";
        // Read the line which consists of <word=tags>
        line = input.readLine();
        while (line != null)
        {
            linesRead++;
            if( linesRead % 100 == 0 )
                System.out.print( "Reading line nr: " + Integer.toString(linesRead) + "\r");
            if (!line.matches("^="))
            {

                items = line.split(separator);    // split using "="
                if (separator.equals(" ") && tagSeparator.equals(" ")) {
                    tok.lexeme = items[0];
                    for (int i=1; i<items.length; i++)
                            tags = tags + " " + items[i];
                }
                // This case is possible $\delta$=÷106\prómill=tp
                else if (separator.equals("=") && items.length > 2)
                {
                    tok.lexeme = items[0];
                    for (int i=1; i<items.length-1;i++)
                        tok.lexeme = tok.lexeme + separator + items[i];
                    tags = items[items.length-1];
                }
                else {
                    tok.lexeme = items[0];      // the word
                    tags = items[1];
                }
                if (tags.length() > 0)
                {
                    tok.addAllTagsWithSeparator(tags,tagSeparator);   // the tags
                    morpho.generateMissingTags(tok);
                    String allTags = tok.allTagStringsWithSeparator(tagSeparator);
                    output.write(tok.lexeme + separator + allTags + "\n");
                }
                else
                  output.write(line + "\n");

                tok.clearTags();
            }
            else
              output.write(line + "\n");

            line = input.readLine();
        }
        output.flush();
        input.close();
        output.close();
        System.out.print( "Reading line nr: " + Integer.toString(linesRead) + "\r");
        //System.out.println("Read " + linesRead + " lines");
    }

    private void showParametersExit()
    {
       System.out.println("Arguments: ");
       System.out.println("-p <parameter file>");
       System.exit(0);
    }

    private void checkParameters()
    {
        boolean error=false;

        if (modeStr == null)
        { System.out.println("Parameter: " + "MODE" + " is missing"); error = true; }
        else {
            if (!(modeStr.equals("one") || modeStr.equals("all") || modeStr.equals("fill")))
            { System.out.println("Parameter: " + "MODE:" + modeStr + " illegal value "); error=true;}
        }
        if (inputFile == null)
        { System.out.println("Parameter: " + "INPUT_FILE" + " is missing"); error = true; }
        if (outputFile == null)
        { System.out.println("Parameter: " + "OUTPUT_FILE" + " is missing"); error = true; }

        if (separatorStr == null)
                { System.out.println("Parameter: " + "SEPARATOR" + " is missing"); error = true; }
            else {
                if (!(separatorStr.equals("space") || separatorStr.equals("equal")))
                    { System.out.println("Parameter: " + "SEPARATOR:" + separatorStr + " illegal value "); error=true;}
        }
        if (tagSeparatorStr == null)
                { System.out.println("Parameter: " + "TAGSEPARATOR" + " is missing"); error = true; }
            else {
                if (!(tagSeparatorStr.equals("space") || tagSeparatorStr.equals("underscore")))
                    { System.out.println("Parameter: " + "TAGSEPARATOR:" + tagSeparatorStr + " illegal value "); error=true;}
        }


        if (error) {
            System.err.println("Exiting!");
            System.exit(0);
        }
    }

    private void loadParameters (String filename)
    throws IOException
    {
        Properties parameters = new Properties();
	    BufferedInputStream in = new BufferedInputStream(
									new FileInputStream(filename));
	    parameters.load(in);
        modeStr = parameters.getProperty("MODE");
        inputFile = parameters.getProperty("INPUT_FILE");
        outputFile = parameters.getProperty("OUTPUT_FILE");
        logFile = parameters.getProperty("LOG_FILE");
        dictionary = parameters.getProperty("DICT");
        dictionaryBase = parameters.getProperty("BASE_DICT");
        endingsBaseDictPath = parameters.getProperty("ENDINGS_BASE");
        endingsDictPath = parameters.getProperty("ENDINGS_DICT");
        endingsProperDictPath = parameters.getProperty("ENDINGS_PROPER_DICT");
        prefixesDictPath = parameters.getProperty("PREFIXES_DICT");
        tagFrequencyFile = parameters.getProperty("TAG_FREQUENCY_FILE");
        separatorStr = parameters.getProperty("SEPARATOR");
        tagSeparatorStr = parameters.getProperty("TAGSEPARATOR");
        tokenDict = parameters.getProperty("TOKEN_DICT");

        checkParameters();
	    in.close();
    }

    private void printHeader()
    {
        System.out.println("****************************************************************");
        System.out.println("*  IceMorphy - A morphological analyzer                        *");
        System.out.println("*  Version 1.1                                                 *");
        System.out.println("*  Copyright (C) 2005-2010, Hrafn Loftsson                     *");
        System.out.println("*                                                              *");
        System.out.println("* The dictionaries used by IceMorphy are derived from the      *");
        System.out.println("* Icelandic Frequency Dictionary (IFD) corpus, and from        *");
        System.out.println("* a part of the Database of Modern Icelandic Inflections (BIN) *");
        System.out.println("* - Copyright © Árni Magnússon Institute for Icelandic Studies *");
        System.out.println("****************************************************************");
    }

    // Get the lexicons either from the resources or the parameters
    private void getIceMorphyLexicons() throws IOException
    {
        InputStream isDictionaryBase, isDictionary, isEndingsBase, isEndings, isEndingsProper,
                    isPrefixes, isTagFrequency;

        IceTaggerResources iceResources = new IceTaggerResources();

        isDictionaryBase = (dictionaryBase == null ? iceResources.isDictionaryBase : new BufferedInputStream(new FileInputStream( dictionaryBase )));
        isDictionary = (dictionary == null ? iceResources.isDictionary : new BufferedInputStream(new FileInputStream( dictionary )));
        isEndingsBase = (endingsBaseDictPath == null ? iceResources.isEndingsBase : new BufferedInputStream(new FileInputStream( endingsBaseDictPath )));
        isEndings = (endingsDictPath == null ? iceResources.isEndings : new BufferedInputStream(new FileInputStream( endingsDictPath )));
        isEndingsProper = (endingsProperDictPath == null ? iceResources.isEndingsProper : new BufferedInputStream(new FileInputStream( endingsProperDictPath )));
        isPrefixes = (prefixesDictPath == null ? iceResources.isPrefixes : new BufferedInputStream(new FileInputStream( prefixesDictPath )));
        isTagFrequency = (tagFrequencyFile == null ? iceResources.isTagFrequency : new BufferedInputStream(new FileInputStream( tagFrequencyFile )));

        morphLex = new IceMorphyLexicons(
                    isDictionary,
                    isDictionaryBase,
                    isEndingsBase,
                    isEndings,
                    isEndingsProper,
                    isPrefixes,
                    isTagFrequency);
    }

    private void run(boolean analyze, boolean generateMissing) throws IOException
    {
        IceLog log = new IceLog(logFile);

        TokenizerResources tokResources = new TokenizerResources();
        InputStream tokenDictIStream = (tokenDict == null ? tokResources.isLexicon : new BufferedInputStream(new FileInputStream( tokenDict )));
        tokLex = new Lexicon(tokenDictIStream);

        getIceMorphyLexicons();
        IceMorphy morpho =  new IceMorphy(morphLex.dict, morphLex.baseDict,
                        morphLex.endingsBase, morphLex.endings, morphLex.endingsProper,
                        morphLex.prefixes, morphLex.tagFrequency, log); // Morphological analyzer

        if (analyze) {
            System.out.println("Analyzing input file " + inputFile + " ...");
            System.out.println("Output file is " + outputFile);
            //System.out.println( "Default file encoding: " + FileEncoding.getEncoding());
            analyzeFile(inputFile, outputFile, morpho, separatorStr, tagSeparatorStr, allTags);
        }
        else if (generateMissing)
        {
           System.out.println("Generating missing tags from " + inputFile + " ...");
           System.out.println("Output file is " + outputFile);
           //System.out.println( "Default file encoding: " + FileEncoding.getEncoding());
           generateMissingTags(inputFile, outputFile, morpho, separatorStr, tagSeparatorStr);
        }
        log.close();
    }

    public static void main (String args[])
    throws IOException
    {
        String paramFile;
        boolean analyze, generateMissing;

        RunIceMorphy runner = new RunIceMorphy();

        if (args.length == 2)
        {
           if (args[0].equals("-p"))
           {
               paramFile = args[1];
               runner.loadParameters(paramFile);

           }
           else
               runner.showParametersExit();
        }
        else
           runner.showParametersExit();

        runner.printHeader();

        if (runner.modeStr.equals("all")) {
            analyze = true;
            runner.allTags = true;
            generateMissing=false;
        }
        else if (runner.modeStr.equals("one")) {
            analyze = true;
            runner.allTags = false;
            generateMissing=false;
        }
        else {
            generateMissing = true;
            analyze = false;
        }

        if (runner.separatorStr.equals("space"))
             runner.separatorStr = " ";
        else
            runner.separatorStr = "=";

        if (runner.tagSeparatorStr.equals("underscore"))
            runner.tagSeparatorStr = "_";
        else
            runner.tagSeparatorStr = " ";
        

        System.out.println("Loading lexicons...");

        runner.run(analyze, generateMissing);
    }
}
