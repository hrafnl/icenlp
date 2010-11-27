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

import is.iclt.icenlp.core.icetagger.IceTagger;
import is.iclt.icenlp.core.icetagger.IceTaggerOutput;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.Tokenizer;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.utils.IceLog;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.tritagger.TriTagger;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Date;
/**
 * Runs IceTagger.
 * @author Hrafn Loftsson
 */
public class RunIceTagger
{
	private String inputFile;
    protected String outputFile;
    private String logFile;
    protected String fileList=null;
    private String tokenDictPath = null;
	private String baseDictPath, dictPath, prefixesDictPath, idiomsDictPath;
	private String verbPrepDictPath, verbObjDictPath, verbAdverbDictPath;
	protected String tagFrequencyFile, tagMapFile, endingsBasePath, endingsDictPath, endingsProperDictPath;
    private String lineFormatStr, outputFormatStr, sentenceStartStr, separatorStr;
    private String modelStr, modelTypeStr;
    private String fullOutputStr, lemmatizeStr;
    private Lexicon tokLex;
    protected boolean standardInputOutput=false;
    protected boolean fullOutput = false;
	private boolean baseTagging = false;
    private boolean changedDefaultInputFormat=false;
    private boolean changedDefaultOutputFormat=false;
	protected boolean fullDisambiguation = true;
	private boolean strictTokenization = true;
    protected boolean lemmatize=false;
    private IceTagger.HmmModelType modelType = IceTagger.HmmModelType.none;
    protected String separator;    // Separator between a word and its tag
    private int lineFormat = Segmentizer.tokenPerLine;    // Default is one token per line
    protected int outputFormat = Segmentizer.tokenPerLine;    // Default is one word/tag per line
    private int sentenceStart;   // Sentences start with upper case or lower case letters?
    private IceTagger tagger;
	private Segmentizer segmentizer;
	protected Tokenizer tokenizer;
	protected IceLog logger = null;               // Log object
	protected int numUnknowns = 0;              // Number of unknowns
	protected int numTokens = 0;                // Number of tokens
    private SimpleDateFormat dateFormatter;
    protected IceTaggerOutput iceOutput = null;
    private IceTaggerLexicons iceLex=null;
    private TriTaggerLexicons triLex=null;
    private boolean sameTagForAllNumbers = true;
    private boolean namedEntityRecognition = false;


    private void showParametersExit()
    {
       showParameters();
       showParametersFooter();
       System.exit( 0 );
    }

    private void showParametersFooter()
    {
        System.out.println( "------------------------------------------" );
        System.out.println( "If the parameters -p or -i/-o or -f are not provided then");
        System.out.println( "IceTagger reads from standard input and writes to standard output");
        System.out.println( "------------------------------------------" );
    }

    protected void showParameters()
	{
		System.out.println( "Arguments: " );
        System.out.println( "-help (shows this info)" );
        System.out.println( "or: " );
        System.out.println( "------------------------------------------" );
		System.out.println( "-p <parameter file> (with no additional parameters)" );
        System.out.println( "or: " );
        System.out.println( "------------------------------------------" );
		//System.out.println( "-im <input mode>" );
		System.out.println( "-i <input file>" );
		System.out.println( "-o <output file>" );
        System.out.println( "or: " );
        System.out.println( "-f <file list>" );
        System.out.println( "------------------------------------------" );
        System.out.println( "and the optional parameters:" );
		System.out.println( "  -lf <1|2|3> (line format)" );
		System.out.println( "  -of <1|2> (output format)" );
		System.out.println( "  -sep <space|underscore> (separator)" );
		System.out.println( "  -ss <upper|lower>" );
		System.out.println( "  -l <log file>" );
        System.out.println( "  -bd <base dictionary file>" );
        System.out.println( "  -d <dictionary file>" );
		System.out.println( "  -p <idioms/phrases dictionary>" );
		System.out.println( "  -vp <verbPrep dictionary>" );
		System.out.println( "  -vo <verbObj dictionary>" );
		System.out.println( "  -va <verbAdverb dictionary>" );
        System.out.println( "  -be <base endings dictionary>" );
        System.out.println( "  -e <endings dictionary>" );
		System.out.println( "  -ep <endings proper nouns dictionary>" );
		System.out.println( "  -pr <prefixes dictionary>" );
		System.out.println( "  -tf <tag frequency file>" );
        System.out.println( "  -tm <tag mapping file>" );
        System.out.println( "  -td <tokenization dictionary>" );
        System.out.println( "  -lem (lemmatize)" );
        System.out.println( "  -m  <HMM model>" );
        System.out.println( "  -mt <start|end|startend>" );
        System.out.println( "  -fo (full output)" );
		System.out.println( "  -bt (base tagging)" );
		System.out.println( "  -nf (do not perform full disambiguation)" );
		System.out.println( "  -ns (do not perform strict tokenization)" );
        System.out.println( "  -num (use various possible tags for numbers)" );
        System.out.println( "  -ner (named entity recognition for proper nouns)" );
	}

	private void checkParameters()
	{
		boolean error = false;

        if( inputFile == null && fileList == null)
        {
            System.out.println( "Parameter: " + "INPUT_FILE" + " is missing" );
            error = true;
        }
		if( outputFile == null && fileList == null )
		{
			System.out.println( "Parameter: " + "OUTPUT_FILE" + " is missing" );
			error = true;
		}
		if( lineFormatStr == null )
		{
			System.out.println( "Parameter: " + "LINE_FORMAT" + " is missing" );
			error = true;
		}
		else
		{
			if( !lineFormatStr.matches( "1|2|3" ) )
			{
				System.out.println( "Parameter: " + "LINE_FORMAT" + " needs values 1|2|3" );
				error = true;
			}
            else
                changedDefaultInputFormat = true;
		}
		if( outputFormatStr == null )
		{
			System.out.println( "Parameter: " + "OUTPUT_FORMAT" + " is missing" );
			error = true;
		}
		else
		{
			if( !outputFormatStr.matches( "1|2" ) )
			{
				System.out.println( "Parameter: " + "OUTPUT_FORMAT" + " needs values 1|2" );
				error = true;
			}
            else
                 changedDefaultOutputFormat = true;
		}
		if( separatorStr == null )
		{
			System.out.println( "Parameter: " + "SEPARATOR" + " is missing" );
			error = true;
		}
		else
		{
			if( !separatorStr.matches( "space|underscore" ) )
			{
				System.out.println( "Parameter: " + "SEPARATOR" + " needs values space|underscore" );
				error = true;
			}
		}
		if( sentenceStartStr == null )
		{
			System.out.println( "Parameter: " + "SENTENCE_START" + " is missing" );
			error = true;
		}
		else
		{
			if( !( sentenceStartStr.matches( "upper|lower" ) ) )
			{
				System.out.println( "Parameter: " + "SENTENCE_START" + " needs values upper|lower" );
				error = true;
			}
		}
        if( fullOutputStr == null )
		{
			System.out.println( "Parameter: " + "FULL_OUTPUT" + " is missing" );
			error = true;
		}
		if( error ) {
            System.err.println("Exiting!");
			System.exit( 0 );
        }
	}

	private void getFormat()
	{
		lineFormat = Integer.parseInt( lineFormatStr );
		outputFormat = Integer.parseInt( outputFormatStr );
		if( separatorStr.equals( "space" ) )
			separator = " ";
		else
			separator = "_";
        if (modelTypeStr != null) {
            if (modelTypeStr.equals("start"))
                modelType = IceTagger.HmmModelType.start;
            else if (modelTypeStr.equals("end"))
                modelType = IceTagger.HmmModelType.end;
            else if (modelTypeStr.equals("startend"))
                modelType = IceTagger.HmmModelType.startend;
        }

    }

	private void loadParameters( String filename )
			throws IOException
	{
		Properties parameters = new Properties();
		BufferedInputStream in = new BufferedInputStream(
				new FileInputStream( filename ) );
		//System.out.println( "Loading parameters..." );
		parameters.load( in );
		inputFile = parameters.getProperty("INPUT_FILE");
		outputFile = parameters.getProperty( "OUTPUT_FILE" );
        fileList  = parameters.getProperty("FILE_LIST");
        logFile = parameters.getProperty( "LOG_FILE" );
		lineFormatStr = parameters.getProperty( "LINE_FORMAT" );
		outputFormatStr = parameters.getProperty( "OUTPUT_FORMAT" );
		separatorStr = parameters.getProperty( "SEPARATOR" );
		sentenceStartStr = parameters.getProperty( "SENTENCE_START" );
        baseDictPath = parameters.getProperty( "BASE_DICT" );
        dictPath = parameters.getProperty( "DICT" );
		idiomsDictPath = parameters.getProperty( "IDIOMS_DICT" );
		modelStr = parameters.getProperty( "MODEL","" );
        modelTypeStr = parameters.getProperty( "MODEL_TYPE","");

        verbPrepDictPath = parameters.getProperty( "VERB_PREP_DICT" );
		verbObjDictPath = parameters.getProperty( "VERB_OBJ_DICT" );
		verbAdverbDictPath = parameters.getProperty( "VERB_ADVERB_DICT" );
        endingsBasePath = parameters.getProperty( "ENDINGS_BASE" );
        endingsDictPath = parameters.getProperty( "ENDINGS_DICT" );
		endingsProperDictPath = parameters.getProperty( "ENDINGS_PROPER_DICT" );
		prefixesDictPath = parameters.getProperty( "PREFIXES_DICT" );
		tokenDictPath = parameters.getProperty( "TOKEN_DICT" );
		tagFrequencyFile = parameters.getProperty( "TAG_FREQUENCY_FILE");
        tagMapFile =   parameters.getProperty( "TAG_MAP_DICT");
        lemmatizeStr =   parameters.getProperty( "LEMMATIZE");
        fullOutputStr = parameters.getProperty( "FULL_OUTPUT" );
		String fullDisambiguationStr = parameters.getProperty( "FULL_DISAMBIGUATION", "yes" );
		String baseTaggingStr = parameters.getProperty( "BASE_TAGGING", "no" );
		String strictTokenizationStr = parameters.getProperty( "STRICT", "yes" );

        checkParameters();
		fullOutput = fullOutputStr.equals("yes");
		fullDisambiguation = fullDisambiguationStr.equals("yes");
		baseTagging = baseTaggingStr.equals("yes");
		strictTokenization = strictTokenizationStr.equals("yes");
        lemmatize = lemmatizeStr.equals("yes");

        getFormat();
		in.close();
	}

	protected void getParameters( String args[] )
	{
		for( int i = 0; i <= args.length - 1; i++ )
		{
            if( args[i].equals( "-help" ) ) {
                printHeader();
                showParametersExit();
            }
            else if( args[i].equals( "-i" ) )
				inputFile = args[i + 1];
			else if( args[i].equals( "-o" ) )
				outputFile = args[i + 1];
            else if( args[i].equals( "-f" ) )
				fileList = args[i + 1];
            else if( args[i].equals( "-lf" ) ) {
				lineFormatStr = args[i + 1];
                changedDefaultInputFormat = true;
            }
			else if( args[i].equals( "-of" ) ) {
				outputFormatStr = args[i + 1];
                changedDefaultOutputFormat = true;
            }
			else if( args[i].equals( "-sep" ) )
				separatorStr = args[i + 1];
			else if( args[i].equals( "-ss" ) )
				sentenceStartStr = args[i + 1];
			else if( args[i].equals( "-l" ) )
				logFile = args[i + 1];
            else if( args[i].equals( "-bd" ) )
				baseDictPath = args[i + 1];
            else if( args[i].equals( "-d" ) )
				dictPath = args[i + 1];
			else if( args[i].equals( "-p" ) )
				idiomsDictPath = args[i + 1];
			else if( args[i].equals( "-vp" ) )
				verbPrepDictPath = args[i + 1];
			else if( args[i].equals( "-vo" ) )
				verbObjDictPath = args[i + 1];
			else if( args[i].equals( "-va" ) )
				verbAdverbDictPath = args[i + 1];
            else if( args[i].equals( "-be" ) )
				endingsBasePath = args[i + 1];
            else if( args[i].equals( "-e" ) )
				endingsDictPath = args[i + 1];
			else if( args[i].equals( "-ep" ) )
				endingsProperDictPath = args[i + 1];
			else if( args[i].equals( "-pr" ) )
				prefixesDictPath = args[i + 1];
			else if( args[i].equals( "-tf" ) )
				tagFrequencyFile = args[i + 1];
            else if( args[i].equals( "-tm" ) )
                tagMapFile = args[i + 1];
            else if( args[i].equals( "-td" ) )
                tokenDictPath = args[i + 1];
            else if( args[i].equals( "-lem" ) )
                lemmatize = true;
            else if( args[i].equals( "-bt" ) )
				baseTagging = true;
			else if( args[i].equals( "-nf" ) )
				fullDisambiguation = false;
			else if( args[i].equals( "-fo" ) )
				fullOutput = true;
			else if( args[i].equals( "-ns" ) )
				strictTokenization = false;
            else if( args[i].equals( "-num" ) )
				sameTagForAllNumbers = false;
            else if( args[i].equals( "-ner" ) )
				namedEntityRecognition = true;
            else if( args[i].equals( "-mt" ) )
				modelTypeStr = args[i + 1];
            else if( args[i].equals( "-m" ) )
				modelStr = args[i + 1];
        }
		getFormat();
	}

private void setDefaults()
	{
		logFile = "";
		modelStr = "";
        modelTypeStr = "";
		lineFormatStr = "1";   // token per line
		outputFormatStr = "1"; // token per line
		separatorStr = "space";
		sentenceStartStr = "upper";
    }



	protected void printResults(BufferedWriter outFile) throws IOException
	{
		ArrayList tokens = tokenizer.tokens;
		int size = tokens.size();

		for( int i = 0; i < size; i++ )
		{
			IceTokenTags tok = (IceTokenTags)tokens.get( i );
            // Strange place to count this.  Can we move this somewhere else?
			numTokens++;
            if( tok.isUnknown() )
			    numUnknowns++;

            String str = iceOutput.buildOutput( tok, i, size );
			outFile.write( str );
			if( outputFormat == Segmentizer.tokenPerLine )
				outFile.newLine();
		}
		// And empty line between sentences
		outFile.newLine();
	}
	
	

	private void printResultsBaseTagging(BufferedWriter outFile)
			throws IOException
	{
		ArrayList tokens = tokenizer.tokens;

		for( int i = 0; i < tokens.size(); i++ )
		{
			IceTokenTags tok = (IceTokenTags)tokens.get( i );
			numTokens++;
            if( tok.isUnknown() )
				numUnknowns++;

            String str = iceOutput.buildOutputBaseTagging( tok);
			outFile.write( str );
			outFile.newLine();
		}
		outFile.newLine();
	}

    private void printInfoAfterTagging(int sentenceCount)
    {
        int numAmbiguousTokens = tagger.getNumAmbiguousTokens();
		int totalTagsAmbiguous = tagger.getTotalTagsAmbiguous();
        int totalTags = tagger.getTotalTags();

        //strCount = Integer.toString(count);
		System.out.println( "Done!" );
		double unknownRatio = 100.0 * numUnknowns / numTokens;
		double ambiguousRatio = 100.0 * numAmbiguousTokens / ( numTokens - numUnknowns );
		double ambiguityRate = 1.0*totalTags/( numTokens - numUnknowns );
		//double ambiguityRate = 1.0 * totalTagsAmbiguous / numAmbiguousTokens;
		DecimalFormat myFormatter = new DecimalFormat( "###.##" );

		System.out.println( "Found " + Integer.toString(sentenceCount) + " sentences" );
		System.out.println( "Found " + Integer.toString( numTokens ) + " tokens" );
		String decimalOutput = myFormatter.format( unknownRatio );
		System.out.println( "Found " + Integer.toString( numUnknowns ) + " unknown words, " + decimalOutput + "%" );
		decimalOutput = myFormatter.format( ambiguousRatio );
		System.out.println( "Found " + Integer.toString( numAmbiguousTokens ) + " ambiguous words, " + decimalOutput + "%" );
		decimalOutput = myFormatter.format( ambiguityRate );
		System.out.println( "Ambiguity rate is " + decimalOutput );
		System.out.flush();
    }

    // Reads a list of files and tags each file separately
    protected void tagAllFiles()  throws IOException
    {
        BufferedWriter output;
        BufferedReader input = FileEncoding.getReader(fileList);
        String taggedOutputFile;
        // Read first line
		String currFile = input.readLine();
        while (currFile != null) {
           //segmentizer = new Segmentizer( currFile, lineFormat, tokenDictPath );
           segmentizer = new Segmentizer( currFile, lineFormat, tokLex );
           taggedOutputFile = currFile+".out";
           output = FileEncoding.getWriter(taggedOutputFile);

           System.out.println();
           System.out.println("Tagging file: " + currFile + "; output: " + taggedOutputFile);

           initStatistics();
           tagText(output);
           currFile = input.readLine();
        }
        input.close();
    }

    protected void tagText(BufferedWriter outFile)
			throws IOException
	{
		String sentence;
		int count = 0;
		String strCount = Integer.toString( count );
        if (!standardInputOutput)
            System.out.print( "Tagging sentence nr 1: " + "\r" );
		while( segmentizer.hasMoreSentences() )
		{
			count++;
			strCount = Integer.toString( count );
			// Step 1: Get next sentence
			if( !standardInputOutput && count % 100 == 0 )
            {
				System.out.print( "Tagging sentence nr: " + strCount + "\r" );
			}
			sentence = segmentizer.getNextSentence();

            if( !sentence.equals("") )
            {
				String output = strCount + ": " + sentence;
				logger.log( output );

				// Step 2: Tokenize the sentence
                if (lineFormat == Segmentizer.tokenPerLine)
                    tokenizer.tokenizeSplit( sentence );    // Only split on whitespace
                else
                    tokenizer.tokenize( sentence );     // Perform more intelligent tokenization

                if( tokenizer.tokens.size() > 0 )
				{
					tokenizer.splitAbbreviations();
					tagger.tagTokens( tokenizer.tokens );

					if( baseTagging )
					    printResultsBaseTagging(outFile);
					else
						printResults(outFile);
				}
			}
		}
        outFile.flush();
		outFile.close();

        if( !standardInputOutput) {
            System.out.print( "Tagging sentence nr: " + strCount + "\n" );
            printInfoAfterTagging(count);
        }
    }

	private void printHeader()
	{
		System.out.println("****************************************************************");
		System.out.println("* IceTagger - A linguistic rule-based tagger                   *");
        System.out.println("*  Version 1.3                                                 *");
        System.out.println("*  Copyright (C) 2005-2010, Hrafn Loftsson                     *");
        //System.out.println("*                                                              *");
        //System.out.println("* The dictionaries used by IceTagger are derived from the      *");
        //System.out.println("* Icelandic Frequency Dictionary (IFD) corpus, and from        *");
        //System.out.println("* a part of the Database of Modern Icelandic Inflections (BIN) *");
        //System.out.println("* - Copyright © Árni Magnússon Institute for Icelandic Studies *");
        System.out.println("****************************************************************");
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
                if (inputFile == null && fileList == null) {
                    standardInputOutput = true;
                    if (!changedDefaultInputFormat)
                        lineFormat = Segmentizer.sentencePerLine;   // Assume one sentence per line
                    if (!changedDefaultOutputFormat)
                        outputFormat = Segmentizer.sentencePerLine;
                }
        }
    }

    private void printInfoBeforeTagging()
    {
        System.out.println( "Input: " + inputFile + ", " + Segmentizer.interpretLineFormat(lineFormat) );
        System.out.println( "Output: " + outputFile + ", " + Segmentizer.interpretLineFormat(outputFormat) );
        //System.out.println( "Default file encoding: " + FileEncoding.getEncoding());
        if (dictPath != null && !dictPath.equals(""))
            System.out.println( "Main lexicon: " + dictPath );
        if( fullDisambiguation )
           System.out.println( "Performing full disambiguation" );
        else
           System.out.println( "Not using full disambiguation" );
        //if( !modelStr.equals( "" ) && fullDisambiguation ) {
        if( !(modelType == IceTagger.HmmModelType.none) && fullDisambiguation ) {
              if (modelType == IceTagger.HmmModelType.startend)
                  System.out.println("Using a HMM for initial and full disambiguation");
              else if (modelType == IceTagger.HmmModelType.start)
                  System.out.println("Using a HMM for initial disambiguation");
              else if (modelType == IceTagger.HmmModelType.end)
                  System.out.println( "Using a HMM for full disambiguation" );
        }

        if (lemmatize)
            System.out.println("Producing lemmata");
        if (tagMapFile != null)
            System.out.println("Mapping tags using " + tagMapFile);


        if( sentenceStart == IceTagger.sentenceStartUpperCase )
            System.out.println( "Sentences start with an upper case letter" );
        else
            System.out.println( "Sentences start with a lower case letter" );

        if (fileList != null)
            System.out.println("Tagging files from filelist " + fileList);
    }

    // Get the lexicons either from the resources or the parameters
    private void getIceTaggerLexicons() throws IOException
    {
        InputStream isDictionaryBase, isDictionary, isEndingsBase, isEndings, isEndingsProper,
                    isVerbPrep, isVerbObj, isVerbAdverb, isIdioms, isPrefixes, isTagFrequency;

        IceTaggerResources iceResources = new IceTaggerResources();

        isDictionaryBase = (baseDictPath == null ? iceResources.isDictionaryBase : new BufferedInputStream(new FileInputStream( baseDictPath )));
        isDictionary = (dictPath == null ? iceResources.isDictionary : new BufferedInputStream(new FileInputStream( dictPath )));
        isEndingsBase = (endingsBasePath == null ? iceResources.isEndingsBase : new BufferedInputStream(new FileInputStream( endingsBasePath )));
        isEndings = (endingsDictPath == null ? iceResources.isEndings : new BufferedInputStream(new FileInputStream( endingsDictPath )));
        isEndingsProper = (endingsProperDictPath == null ? iceResources.isEndingsProper : new BufferedInputStream(new FileInputStream( endingsProperDictPath )));
        isVerbPrep = (verbPrepDictPath == null ? iceResources.isVerbPrep : new BufferedInputStream(new FileInputStream( verbPrepDictPath )));
        isVerbObj = (verbObjDictPath == null ? iceResources.isVerbObj : new BufferedInputStream(new FileInputStream( verbObjDictPath )));
        isVerbAdverb = (verbAdverbDictPath == null ? iceResources.isVerbAdverb : new BufferedInputStream(new FileInputStream( verbAdverbDictPath )));
        isIdioms = (idiomsDictPath == null ? iceResources.isIdioms : new BufferedInputStream(new FileInputStream( idiomsDictPath )));
        isPrefixes = (prefixesDictPath == null ? iceResources.isPrefixes : new BufferedInputStream(new FileInputStream( prefixesDictPath )));
        isTagFrequency = (tagFrequencyFile == null ? iceResources.isTagFrequency : new BufferedInputStream(new FileInputStream( tagFrequencyFile )));

        iceLex = new IceTaggerLexicons(
                    isDictionaryBase,
                    isDictionary,
                    isEndingsBase,
                    isEndings,
                    isEndingsProper,
                    isVerbPrep,
                    isVerbObj,
                    isVerbAdverb,
                    isIdioms,
                    isPrefixes,
                    isTagFrequency);
    }

    private void getTriTaggerLexicons() throws IOException
    {
        if (modelStr != null && !modelStr.equals(""))
            triLex = new TriTaggerLexicons(modelStr, true, true);
        else {
            TriTaggerResources triResources = new TriTaggerResources();
            triLex = new TriTaggerLexicons(triResources, true);
        }
    }


    private void createAllObjects(int sentenceStart) throws IOException
    {
        getIceTaggerLexicons();

        TokenizerResources tokResources = new TokenizerResources();
        InputStream tokenDictIStream = (tokenDictPath == null ? tokResources.isLexicon : new BufferedInputStream(new FileInputStream( tokenDictPath )));
        tokLex = new Lexicon(tokenDictIStream);

        if (inputFile != null)
            segmentizer = new Segmentizer( inputFile, lineFormat, tokLex );
            //segmentizer = new Segmentizer( inputFile, lineFormat, tokenDictPath );
        // Reading from standard input?
        else if (standardInputOutput) {
             BufferedReader in = FileEncoding.getReader(System.in);
             segmentizer = new Segmentizer(in, lineFormat, tokLex);
             //segmentizer = new Segmentizer(in, lineFormat, tokenDictPath);
        }

        //tokenizer = new Tokenizer( Tokenizer.typeIceTokenTags, strictTokenization, tokenDictPath );
        tokenizer = new Tokenizer( Tokenizer.typeIceTokenTags, strictTokenization, tokLex );
		//tokenizer.findMultiWords( false );
		logger = new IceLog( logFile );

        /* Do we need a trigram tagger as well? */
        TriTagger triTagger=null;
        //if( fullDisambiguation && modelStr != null && !modelStr.equals( "" ) )
        if( fullDisambiguation && !(modelType == IceTagger.HmmModelType.none) )
		{
            if (!standardInputOutput) {
                if (modelStr != null && !modelStr.equals( "" ))
                    System.out.println("Loading HMM: " + modelStr);
                else
                    System.out.println("Loading default HMM ...");
            }
            getTriTaggerLexicons();
            triTagger = new TriTagger( sentenceStart, false, TriTagger.trigrams, triLex.ngrams, triLex.freqLexicon, null, null, null );
        }

        IceMorphy morphoAnalyzer = new IceMorphy(
                iceLex.morphyLexicons.dict,
                iceLex.morphyLexicons.baseDict,
                iceLex.morphyLexicons.endingsBase,
                iceLex.morphyLexicons.endings,
                iceLex.morphyLexicons.endingsProper,
                iceLex.morphyLexicons.prefixes,
                iceLex.morphyLexicons.tagFrequency,
                logger);

       tagger = new IceTagger(sentenceStart, logger, morphoAnalyzer,
               iceLex.morphyLexicons.baseDict,
               iceLex.morphyLexicons.dict,
               iceLex.idioms,
               iceLex.verbPrep,
               iceLex.verbObj,
               iceLex.verbAdverb,
               baseTagging, fullDisambiguation, triTagger, modelType);

        tagger.setSameTagForAllNumbers(sameTagForAllNumbers);
        tagger.setNamedEntityRecognition(namedEntityRecognition);
    }

    private void initStatistics() {
        numUnknowns = 0;
	    numTokens = 0;
        tagger.initStatistics();
    }

    protected void performTagging() throws IOException
    {
        if (standardInputOutput) {
            BufferedWriter out = FileEncoding.getWriter(System.out);
            tagText(out);
        }
        else if (fileList == null) {
            BufferedWriter out = FileEncoding.getWriter(outputFile);
            tagText(out);
        }
        else
            tagAllFiles();

        logger.close();
    }

    protected Date initialize(String args[]) throws IOException
    {
        processParam(args);
        if( sentenceStartStr.equals("upper"))
            sentenceStart = IceTagger.sentenceStartUpperCase;
        else
            sentenceStart = IceTagger.sentenceStartLowerCase;

        if (!standardInputOutput) {
            printHeader();
            printInfoBeforeTagging();
        }

        Date before = new Date();
        dateFormatter = new SimpleDateFormat();

        if (!standardInputOutput) {
            System.out.println( "Started at: " + dateFormatter.format( before ) );
            System.out.println( "Loading lexicons..." );
        }

        createAllObjects(sentenceStart);

        return before;
    }

    protected void finish(Date before)
    {
        Date after = new Date();
		double elapsed = ( after.getTime() - before.getTime() );
		int msec = (int)( elapsed / 1000 );
        if (fileList != null)
           System.out.println();
        if (!standardInputOutput) {
            System.out.println( "Finished at: " + dateFormatter.format( after ) );
		    System.out.println( "Tagging time: " + Integer.toString( msec ) + " seconds" );
        }
    }

    public static void main( String args[] ) throws Exception
	{
        RunIceTagger runner = new RunIceTagger();
        Date before = runner.initialize(args);

        // Instantiate an output class
        runner.iceOutput = new IceTaggerOutput(runner.outputFormat, runner.separator, runner.fullOutput, runner.fullDisambiguation, runner.tagMapFile, runner.lemmatize);
        // Perform the tagging
        runner.performTagging();

        runner.finish(before);
	}
}
