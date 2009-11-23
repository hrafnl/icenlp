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

import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;
import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.utils.Idioms;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.Lexicon;
//import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.tritagger.*;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ArrayList;

/**
 * Runs TriTagger.
 * @author Hrafn Loftsson
 */
public class RunTriTagger {
    private static TriTagger tagger;
    private static Segmentizer segmentizer;
    private static Tokenizer tokenizer;
    private static String inputFile, outputFile, model;
    private static String backupDictPath=null, idiomsDictPath=null, tokenDictPath=null;
    private static String ngramStr, lineFormatStr, outputFormatStr, sentenceStartStr, strictTokenizationStr, iceMorphyStr ;
    private static String morphoDictPath=null, morphoDictBasePath=null, prefixesDictPath=null;
    private static String endingsBaseDictPath=null, endingsDictPath=null, endingsProperDictPath=null;
    private static int sentenceStart;   // Sentences start with upper case or lower case letters?
    private static boolean useIceMorphy=false;
    private static boolean strictTokenization=true;
    private boolean changedDefaultInputFormat=false;
    private boolean changedDefaultOutputFormat=false;
    private static int lineFormat= Segmentizer.tokenPerLine;     // Default is one token per line
    private static int outputFormat=Segmentizer.tokenPerLine;    // Default is one word/tag per line
    private static int ngram=3;                                  // Default is trigrams
    private static int numTokens=0, numUnknowns=0;
    private static SimpleDateFormat dateFormatter;
    protected static TriTaggerOutput triOutput = null;
    private Lexicon tokLex;
    private TriTaggerLexicons triLex=null;
    private IceMorphyLexicons morphLex=null;
    private boolean standardInputOutput=false;

    private void checkParameters()
    {
        boolean error=false;

        if (inputFile == null)
        { System.out.println("Parameter: " + "INPUT_FILE" + " is missing"); error = true; }
        if (outputFile == null)
        { System.out.println("Parameter: " + "OUTPUT_FILE" + " is missing"); error = true; }
        /*if (model == null)
        { System.out.println("Parameter: " + "MODEL" + " is missing"); error = true; }*/
        if (ngramStr == null)
        { System.out.println("Parameter: " + "NGRAM" + " is missing"); error = true; }
        else
        {
            if (!ngramStr.matches("2|3"))
                { System.out.println("Parameter: " + "NGRAM" + " needs values 2|3"); error = true; }
        }
        if (sentenceStartStr == null)
        { System.out.println("Parameter: " + "SENTENCE_START" + " is missing"); error = true; }
        else
        {
            if (!(sentenceStartStr.matches("upper|lower")))
                { System.out.println("Parameter: " + "SENTENCE_START" + " needs values upper|lower"); error = true; }
        }
        if (iceMorphyStr == null)
        { System.out.println("Parameter: " + "ICEMORPHY" + " is missing"); error = true; }
        //if (logFile == null)
        //{ System.out.println("Parameter: " + "LOG_FILE" + " is missing"); error = true; };
        if (lineFormatStr == null)
        { System.out.println("Parameter: " + "LINE_FORMAT" + " is missing"); error = true; }
        else
        {
            if (!lineFormatStr.matches("1|2|3"))
                { System.out.println("Parameter: " + "LINE_FORMAT" + " needs values 1|2|3"); error = true; }
            else
                changedDefaultInputFormat = true;
        }
        if (outputFormatStr == null)
        { System.out.println("Parameter: " + "OUTPUT_FORMAT" + " is missing"); error = true; }
        else
        {
            if (!outputFormatStr.matches("1|2"))
                { System.out.println("Parameter: " + "OUTPUT_FORMAT" + " needs values 1|2"); error = true; }
            else
                 changedDefaultOutputFormat = true;
        }
        if (iceMorphyStr.equals("yes"))
        {
            if (morphoDictPath == null)
            { System.out.println("Parameter: " + "DICT" + " is missing"); error = true; }
            if (morphoDictBasePath == null)
            { System.out.println("Parameter: " + "BASE_DICT" + " is missing"); error = true; }
            if (endingsBaseDictPath == null)
            { System.out.println("Parameter: " + "ENDINGS_BASE" + " is missing"); error = true; }
            if (endingsDictPath == null)
            { System.out.println("Parameter: " + "ENDINGS_DICT" + " is missing"); error = true; }
            if (endingsProperDictPath == null)
            { System.out.println("Parameter: " + "ENDINGS_PROPER_DICT" + " is missing"); error = true; }
            if (prefixesDictPath == null)
            { System.out.println("Parameter: " + "PREFIXES_DICT" + " is missing"); error = true; }
        }
        if (error)   {
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
        //System.out.println("Loading parameters...");
	    parameters.load(in);
        inputFile = parameters.getProperty("INPUT_FILE");
        outputFile = parameters.getProperty("OUTPUT_FILE");
        model = parameters.getProperty("MODEL");
        ngramStr = parameters.getProperty("NGRAM");
        sentenceStartStr = parameters.getProperty("SENTENCE_START");
        //logFile = parameters.getProperty("LOG_FILE");
        lineFormatStr = parameters.getProperty("LINE_FORMAT");
        outputFormatStr = parameters.getProperty("OUTPUT_FORMAT");
        //sentenceStartStr = parameters.getProperty("SENTENCE_START");
        backupDictPath = parameters.getProperty("BACKUP_DICT");
        idiomsDictPath = parameters.getProperty("IDIOMS_DICT");
        morphoDictPath = parameters.getProperty("DICT");
        morphoDictBasePath = parameters.getProperty("BASE_DICT");
        endingsBaseDictPath = parameters.getProperty("ENDINGS_BASE");
        endingsDictPath = parameters.getProperty("ENDINGS_DICT");
        endingsProperDictPath = parameters.getProperty("ENDINGS_PROPER_DICT");
        prefixesDictPath = parameters.getProperty("PREFIXES_DICT");
        tokenDictPath = parameters.getProperty("TOKEN_DICT");
        strictTokenizationStr = parameters.getProperty("STRICT", "yes");
        iceMorphyStr = parameters.getProperty("ICEMORPHY", "no");

        checkParameters();
        getFormat();
        in.close();
    }

    private void showParametersExit()
    {
       System.out.println("Arguments: ");
       System.out.println( "-help (shows this info)" );
       System.out.println( "or: " );
       System.out.println("------------------------------------------");
       System.out.println("Either: ");
       System.out.println("-p <parameter file>");
       System.out.println("or: ");
       System.out.println("------------------------------------------");
       System.out.println("-m <model>");
       System.out.println("-i <input file>");
       System.out.println("-o <output file>");
       System.out.println("and the optional parameters:");
       System.out.println("  -lf <1|2|3> (line format)");
       System.out.println("  -of <1|2> (output format)");
       System.out.println("  -ss <upper|lower>" );
       System.out.println("  -b <backup dictionaryOtb>");
       System.out.println("  -p <idioms/phrases dictionary>");
       System.out.println("  -im (use IceMorphy)");
       System.out.println("  -d <dictionary>");
       System.out.println("  -db <base dictionary>");
       System.out.println("  -eb <endings base dictionary>");
       System.out.println("  -e <endings dictionary>");
       System.out.println("  -ep <endings proper nouns dictionary>");
       System.out.println("  -pr <prefixes dictionary>");
       System.out.println("  -td <tokenization dictionary>");
       System.out.println("  -ns (dont perform strict tokenization)");

       System.exit(0);
    }

    private void getFormat()
    {
        lineFormat = Integer.parseInt(lineFormatStr);
        outputFormat = Integer.parseInt(outputFormatStr);
        ngram = Integer.parseInt(ngramStr);
        strictTokenization = strictTokenizationStr.equals("yes");
        useIceMorphy = iceMorphyStr.equals("yes");
    }

    private void getParameters (String args[])
    {
        // Get the param
        for (int i=0; i<=args.length-1;i++)
        {
          if (args[i].equals("-help"))
          {
             printHeader();
             showParametersExit();
          }
          else if (args[i].equals("-i"))
            inputFile = args[i+1];
          else if (args[i].equals("-o"))
            outputFile = args[i+1];
          else if (args[i].equals("-m"))
            model = args[i+1];
          else if (args[i].equals("-n"))
            ngramStr = args[i+1];
          else if (args[i].equals("-lf")) {
            lineFormatStr = args[i+1];
            changedDefaultInputFormat = true;
          }
          else if (args[i].equals("-of")) {
            outputFormatStr = args[i+1];
            changedDefaultOutputFormat = true;
          }
          else if (args[i].equals("-ss"))
            sentenceStartStr = args[i+1];
          else if (args[i].equals("-p"))
            idiomsDictPath = args[i+1];
          else if (args[i].equals("-b"))
            backupDictPath = args[i+1];
          else if (args[i].equals("-td"))
            tokenDictPath = args[i+1];
          else if (args[i].equals("-d"))
            morphoDictPath = args[i+1];
          else if (args[i].equals("-db"))
            morphoDictBasePath = args[i+1];
          else if (args[i].equals("-eb"))
            endingsBaseDictPath = args[i+1];
          else if (args[i].equals("-e"))
            endingsDictPath = args[i+1];
          else if (args[i].equals("-ep"))
            endingsProperDictPath = args[i+1];
          else if (args[i].equals("-pr"))
            prefixesDictPath = args[i+1];
          else if (args[i].equals("-ns"))
            strictTokenizationStr = "no";
          else if (args[i].equals("-im"))
            iceMorphyStr = "yes";
        }
        getFormat();
    }

private void setDefaults ()
{
        lineFormatStr = "1";
        outputFormatStr = "1";
        sentenceStartStr = "upper";
        ngramStr = "3";
        tokenDictPath="../../dict/tokenizer/lexicon.txt";
        strictTokenizationStr = "no";
        iceMorphyStr = "no";
}

 private void printResults(BufferedWriter outFile)
 throws IOException
 {
   ArrayList tokens = tokenizer.tokens;
   int size = tokens.size();

   for (int i=0; i<size; i++)
   {
      TokenTags tok = (TokenTags)tokens.get(i);
      numTokens++;
      if (tok.isUnknown())
          numUnknowns++;

      String str = triOutput.buildOutput( tok, i, size );

      outFile.write(str);
      if (outputFormat == Segmentizer.tokenPerLine)
         outFile.newLine();
   }
   // En empty line between sentences
   outFile.newLine();
}

private void printInfoAfterTagging(int sentenceCount)
{
        int numAmbiguousTokens = tagger.getNumAmbiguousTokens();
        int totalTagsAmbiguous = tagger.getTotalTagsAmbiguous();
        int totalTags = tagger.getTotalTags();

        //System.out.println(strCount);
        System.out.println("Done!");
        double unknownRatio = 100.0*numUnknowns/numTokens;
        //double ambiguousRatio = 100.0*numAmbiguous/(numTokens-numUnknowns);
        //double ambiguityRate = 1.0*totalTagsAmbiguous/numAmbiguousTokens;
        double ambiguityRate = 1.0*totalTags/( numTokens - numUnknowns );
        DecimalFormat myFormatter = new DecimalFormat("###.##");

        System.out.println("Found " + Integer.toString(sentenceCount) + " sentences");
        System.out.println("Found " + Integer.toString(numTokens) + " tokens");
        String decimalOutput = myFormatter.format(unknownRatio);
        System.out.println("Found " + Integer.toString(numUnknowns) + " unknown words, " + decimalOutput + "%");
        //decimalOutput = myFormatter.format(ambiguousRatio);
        //System.out.println("Found " + Integer.toString(numAmbiguous) + " ambiguous words, " + decimalOutput + "%");
        decimalOutput = myFormatter.format(ambiguityRate);
        System.out.println("Ambiguity rate is " + decimalOutput);
        System.out.flush();
}

    private void tagText(BufferedWriter outFile)
    throws IOException
    {
         String sentence;
         int count = 0;
         String strCount = Integer.toString(count);
         //System.out.print("Tagging sentence nr 1: " + "\r");
         while (segmentizer.hasMoreSentences())
         {
           count++;

           strCount = Integer.toString(count);
           if (!standardInputOutput && count % 100 == 0)
                System.out.print("Tagging sentence nr: " + strCount + "\r");

           sentence = segmentizer.getNextSentence();

           if (!sentence.equals(""))
           {
             tokenizer.tokenize(sentence);

             if (tokenizer.tokens.size() > 0)
             {
               tokenizer.splitAbbreviations();
               tagger.tagTokens(tokenizer.tokens, true);
               printResults(outFile);
             }
           }
         }
        outFile.flush();
		outFile.close();

        if (!standardInputOutput) {
            System.out.print("Tagging sentence nr: " + strCount + "\n");
            printInfoAfterTagging(count);
        }
}

private void printHeader()
{
    System.out.println("************************************************");
    System.out.println("*  TriTagger - A HMM tagger (bi- or trigrams)  *");
    System.out.println("*  Version 1.1                                 *");
    System.out.println("*  Copyright (C) 2005-2009, Hrafn Loftsson     *" );
    System.out.println("************************************************");
}

private void printInfoBeforeTagging()
{
    System.out.println("Input: " + inputFile + ", " + Segmentizer.interpretLineFormat(lineFormat));
    System.out.println("Output: " + outputFile + ", " + Segmentizer.interpretLineFormat(outputFormat));

    if( sentenceStart == TriTagger.sentenceStartUpperCase )
        System.out.println( "Sentences start with an upper case letter" );
    else
        System.out.println( "Sentences start with a lower case letter" );
}

private void getTriTaggerLexicons() throws IOException
{
        if (model != null && !model.equals(""))
        {
            if (!standardInputOutput)
                System.out.println("Loading model " + model + " ...");
            triLex = new TriTaggerLexicons(model, true, true);
        }
        else {
            if (!standardInputOutput)
                System.out.println("Loading default model ...");
            TriTaggerResources triResources = new TriTaggerResources();
            triLex = new TriTaggerLexicons(triResources, true);
        }
}

// Get the lexicons either from the resources or the parameters
    private void getIceMorphyLexicons() throws IOException
    {
        InputStream isDictionaryBase, isDictionary, isEndingsBase, isEndings, isEndingsProper,
                    isPrefixes; //, isTagFrequency;

        IceTaggerResources iceResources = new IceTaggerResources();

        isDictionaryBase = (morphoDictBasePath == null ? iceResources.isDictionaryBase : new BufferedInputStream(new FileInputStream( morphoDictBasePath )));
        isDictionary = (morphoDictPath == null ? iceResources.isDictionary : new BufferedInputStream(new FileInputStream( morphoDictPath )));
        isEndingsBase = (endingsBaseDictPath == null ? iceResources.isEndingsBase : new BufferedInputStream(new FileInputStream( endingsBaseDictPath )));
        isEndings = (endingsDictPath == null ? iceResources.isEndings : new BufferedInputStream(new FileInputStream( endingsDictPath )));
        isEndingsProper = (endingsProperDictPath == null ? iceResources.isEndingsProper : new BufferedInputStream(new FileInputStream( endingsProperDictPath )));
        isPrefixes = (prefixesDictPath == null ? iceResources.isPrefixes : new BufferedInputStream(new FileInputStream( prefixesDictPath )));

        morphLex = new IceMorphyLexicons(
                    isDictionary,
                    isDictionaryBase,
                    isEndingsBase,
                    isEndings,
                    isEndingsProper,
                    isPrefixes,
                    null);
    }


private void createAllObjects(int sentenceStart) throws IOException
{
    getTriTaggerLexicons();

    TokenizerResources tokResources = new TokenizerResources();
    InputStream tokenDictIStream = (tokenDictPath == null ? tokResources.isLexicon : new BufferedInputStream(new FileInputStream( tokenDictPath )));
    tokLex = new Lexicon(tokenDictIStream);

    // Read from an input file or from standard input?
    if (inputFile != null)
        segmentizer = new Segmentizer(inputFile, lineFormat, tokLex);
    else if (standardInputOutput) {
        BufferedReader in = FileEncoding.getReader(System.in);
        segmentizer = new Segmentizer(in, lineFormat, tokLex);
    }

    tokenizer = new Tokenizer(Tokenizer.typeHmmTokenTags, strictTokenization, tokLex);
    tokenizer.findMultiWords(false);

    //System.out.println("Entropy=" + Double.toString(triLex.ngrams.getEntropy()));
    //System.out.println("Suffix theta " + Double.toString(myLexicon.getTheta()));
    FreqLexicon myBackupLexicon = null;
    if (!(backupDictPath == null) && !backupDictPath.equals(""))
    {
        System.out.println("Loading backup lexicon " + backupDictPath + " ...");
        myBackupLexicon = new FreqLexicon(backupDictPath, FreqLexicon.formatNoFrequency, false);
    }

    Idioms myPhrases=null;
    if (!(idiomsDictPath == null) && !idiomsDictPath.equals(""))
    {
        System.out.println("Loading idioms " + idiomsDictPath + " ...");
        myPhrases = new Idioms(idiomsDictPath);
    }
    IceMorphy myMorpho=null;
    if (useIceMorphy) {
        System.out.println("Loading IceMorphy...");
        getIceMorphyLexicons();
        //IceMorphyLexicons morphLex = new IceMorphyLexicons(morphoDictPath, morphoDictBasePath, endingsBaseDictPath, endingsDictPath, endingsProperDictPath, prefixesDictPath, null);

        myMorpho =  new IceMorphy(morphLex.dict, morphLex.baseDict,
                    morphLex.endingsBase, morphLex.endings, morphLex.endingsProper,
                    morphLex.prefixes, null, null); // Morphological analyzer
    }
    if (!standardInputOutput) {
        if (ngram==2)
            System.out.println("Using bigrams");
        else
            System.out.println("Using trigrams");
    }

    tagger = new TriTagger(sentenceStart, ngram, triLex.ngrams, triLex.freqLexicon, myBackupLexicon, myPhrases, myMorpho);
}

private void processParam(String args[]) throws IOException
{
        String paramFile;

        if( args.length >= 1 && args[0].equals( "-p" ) )
		{
				paramFile = args[1];
                loadParameters( paramFile );
		}
        else
		{
				setDefaults();
				getParameters( args );
                // If neither reading input from a file nor a filelist then read from standard input
                if (inputFile == null) {
                    standardInputOutput = true;
                    if (!changedDefaultInputFormat)
                        lineFormat = Segmentizer.sentencePerLine;   // Assume one sentence per line
                    if (!changedDefaultOutputFormat)
                        outputFormat = Segmentizer.sentencePerLine;
                }
        }
}

private Date initialize(String args[]) throws IOException
{
        processParam(args);
        if( sentenceStartStr.equals("upper"))
            sentenceStart = TriTagger.sentenceStartUpperCase;
        else
            sentenceStart = TriTagger.sentenceStartLowerCase;

        if (!standardInputOutput) {
            printHeader();
            printInfoBeforeTagging();
        }

        Date before = new Date();
        dateFormatter = new SimpleDateFormat();

        if (!standardInputOutput) {
            System.out.println( "Started at: " + dateFormatter.format( before ) );
        }

        createAllObjects(sentenceStart);

        return before;
}

protected void finish(Date before)
{
    Date after = new Date();
    double elapsed = (after.getTime () - before.getTime ());
    int msec = (int)(elapsed/1000);

    if (!standardInputOutput) {
        System.out.println("Finished at: " + dateFormatter.format(before));
        System.out.println("Tagging time: " + Integer.toString(msec) + " seconds");
    }
}

    public static void main (String args[])
    throws IOException
    {
        RunTriTagger runner = new RunTriTagger();
        Date before = runner.initialize(args);

        triOutput = new TriTaggerOutput(outputFormat);

        BufferedWriter out;
        if (runner.standardInputOutput)
            out = FileEncoding.getWriter(System.out);
        else
            out = FileEncoding.getWriter(outputFile);

        runner.tagText(out);
        runner.finish(before);
    }

}
