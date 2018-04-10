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

import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.Lexicon;

import java.util.ArrayList;
import java.io.*;

/**
 * Runs Tokenizer.
 * @author Hrafn Loftsson
 */

public class RunTokenizer {
    private String inputFile=null, outputFile=null;
    private String lexiconFile=null;
    private String inputFormatString="3", outputFormatString="1";
    private BufferedWriter outWriter=null;
    private int outputFormat, numSentences=0;
    private boolean splitAbbreviations=false;
    private boolean strictTokenization=true;
    private boolean standardInputOutput=false;
    private boolean showMWEs=false;
    private int lineFormat=Segmentizer.otherDifferentFormat;
    private String showInputFormat="", showOutputFormat="";
    private Lexicon tokLex;

    private void showParametersExit()
    {
       System.out.println("Arguments: ");
       System.out.println("------------------------------------------");
       System.out.println("-help (shows this screen)");
       System.out.println("-i <input file>");
       System.out.println("-o <output file>");
       System.out.println("-if <input format> (0=one token/tag per line, 1=one token per line, 2=one sentence per line, 3=some other different format ");
       System.out.println("-of <output format> (1=one token per line, 2=one sentence per line");
       //System.out.println("-s <input string> (if no input file)");
       System.out.println("-c <count> (optional; quit after <count> sentences)");      
       System.out.println("-l <lexicon file> (optional)");
       System.out.println("-mwe (show multiword expressions; optional)");
       System.out.println("-sa (split abbreviations; optional)");
       System.out.println("-ns (not strict tokenization; optional)");
       System.out.println( "------------------------------------------" );
       System.out.println( "If the parameter -i is not provided then");
       System.out.println( "the tokenizer reads from standard input and writes to standard output");
       System.out.println( "------------------------------------------" );
       System.exit(0);
    }

    // Converts a file with one token per line to one sentence per line
    // Use to read in the Icelandic Frequency Dictionary and produce one sentence per line
    private void tokensToSentence()
    throws IOException
    {
       Segmentizer mySegmentizer = new Segmentizer(inputFile, lineFormat, tokLex);

       while (mySegmentizer.hasMoreSentences())
       {
           String sentence = mySegmentizer.getNextSentence();
           if (sentence != "")
           {
                outWriter.write(sentence);
                outWriter.newLine();
           }
       }
    }

    private void performTokenization(int inpFormat)
    throws IOException
    {

        Segmentizer mySegmentizer;
        TokenizerResources tokResources = new TokenizerResources();
        InputStream tokenDictIStream = (lexiconFile == null ? tokResources.isLexicon : new BufferedInputStream(new FileInputStream( lexiconFile )));
        tokLex = new Lexicon(tokenDictIStream);

        Tokenizer myTokenizer = new Tokenizer(Tokenizer.typeTokenTags, strictTokenization, tokLex);

        if (inputFile != null)
            mySegmentizer = new Segmentizer(inputFile, inpFormat, tokLex);
        else
            mySegmentizer = new Segmentizer(FileEncoding.getReader(System.in), Segmentizer.otherDifferentFormat, tokLex);

        TokenTags token;
        String sentence;
        int count=0;

        while (mySegmentizer.hasMoreSentences())
        {
           if (numSentences != 0 && count == numSentences)
              break;
            
           sentence = mySegmentizer.getNextSentence();
           if (sentence != "")
           {
               count++;
               if( !standardInputOutput && count % 100 == 0 )
                  System.out.print( "Tokenising sentence nr: " + Integer.toString(count) + "\r");

               if (inpFormat == Segmentizer.tokenPerLine)
                   myTokenizer.tokenizeSplit(sentence);
               else if (inpFormat == Segmentizer.tokenAndTagPerLine)
                   myTokenizer.tokensWithTags(sentence);
               else
                    myTokenizer.tokenize(sentence);

               if (splitAbbreviations)
                    myTokenizer.splitAbbreviations();

                ArrayList tokens = myTokenizer.tokens;
                for (int i=0; i<tokens.size(); i++)
                {
                    token = (TokenTags)tokens.get(i);

                    if (outputFormat == Segmentizer.sentencePerLine)
                    {
                      outWriter.write(token.lexeme);
                      if (inpFormat == Segmentizer.tokenAndTagPerLine)
                          outWriter.write(" " + token.goldTag);
                      if (i < tokens.size()-1)
                           outWriter.write(" ");
                    }
                    else // tokenPerLine
                    {
                      outWriter.write(token.lexeme);
                      if (inpFormat == Segmentizer.tokenAndTagPerLine)
                          outWriter.write("\t" + token.goldTag);
                      if (showMWEs) {
                          if (token.mweCode == Token.MWECode.begins)
                              outWriter.write("\t" + "MWE_begins");
                          else if (token.mweCode == Token.MWECode.ends)
                              outWriter.write("\t" + "MWE_ends");
                      }
                      outWriter.newLine();
                    }
                }
                outWriter.newLine();
           }
       }
       if (!standardInputOutput) {
         System.out.print( "Tokenising sentence nr: " + Integer.toString(count) + "\r");
         System.out.println();
         System.out.flush();
       }

    }

    private void printHeader()
{
    System.out.println("************************************************");
    System.out.println("*  Tokenization and sentence segmentation      *");
    System.out.println("*  Version 1.2                                 *");
    System.out.println("*  Copyright (C) 2004-2012, Hrafn Loftsson     *");
    System.out.println("************************************************");
}

    private void parseFormat()
    {
        if (inputFormatString != null) {
            lineFormat = Integer.parseInt(inputFormatString);
            if (lineFormat == Segmentizer.tokenAndTagPerLine)
                showInputFormat = "one token/tag per line";
            else if (lineFormat == Segmentizer.tokenPerLine)
              showInputFormat = "one token per line";
            else if (lineFormat == Segmentizer.sentencePerLine)
                showInputFormat = "one sentence per line";
            else
                showInputFormat = "unspecified";
        }
        if (outputFormatString != null)
        {
            outputFormat = Integer.parseInt(outputFormatString);
            if (outputFormat == Segmentizer.tokenPerLine)
              showOutputFormat = "one token per line";
            else if (outputFormat == Segmentizer.sentencePerLine)
                showOutputFormat = "one sentence per line";
        }
    }

    private void getParameters (String args[])
    {
        int arglen = args.length;
        for (int i=0; i<=arglen-1;i++)
        {
          if (args[i].equals("-help")) {
              printHeader();
              showParametersExit();
          }
          else if (args[i].equals("-i"))
            inputFile = args[i+1];
          else if (args[i].equals("-o"))
            outputFile = args[i+1];
          else if (args[i].equals("-if"))
            inputFormatString = args[i+1];
          else if (args[i].equals("-of"))
            outputFormatString = args[i+1];
          else if (args[i].equals("-l"))
            lexiconFile = args[i+1];
          else if (args[i].equals("-c"))
            numSentences = Integer.parseInt(args[i+1]);
          else if (args[i].equals("-sa"))
             splitAbbreviations = true;
          else if (args[i].equals("-ns"))
             strictTokenization = false;
          else if (args[i].equals("-mwe"))
             showMWEs = true;
        }
        parseFormat();
    }

    public static void main (String args[])
    throws IOException
    {
        RunTokenizer runner = new RunTokenizer();
        runner.getParameters(args);
        if (runner.inputFile == null) {
           runner.standardInputOutput=true;
           runner.outWriter = FileEncoding.getWriter(System.out);
        }
        else
            runner.outWriter = FileEncoding.getWriter(runner.outputFile);

        if (!runner.standardInputOutput) {
            runner.printHeader();
            if (runner.inputFile != null)
                System.out.println("Input: " + runner.inputFile + ", " + runner.showInputFormat);
            System.out.println("Output: " + runner.outputFile + ", " + runner.showOutputFormat);
        }
        runner.performTokenization(runner.lineFormat);
        runner.outWriter.flush();
        runner.outWriter.close();
	}
}

