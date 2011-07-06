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

import is.iclt.icenlp.core.apertium.ApertiumEntry;
import is.iclt.icenlp.core.apertium.ApertiumSegmentizer;
import is.iclt.icenlp.core.apertium.IceNLPTokenConverter;
import is.iclt.icenlp.core.apertium.LemmaGuesser;
import is.iclt.icenlp.core.apertium.LexicalUnit;
import is.iclt.icenlp.core.apertium.LtProcParser;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.IceTag;
import is.iclt.icenlp.core.utils.MappingLexicon;
import is.iclt.icenlp.core.utils.Word;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Runs IceParser producing Apertium output format.
 * 
 * @author Hrafn Loftsson
 */
public class RunIceTaggerApertium extends RunIceTagger
{
	// Member variables.
	private MappingLexicon mappingLexicon;
	private Lemmald lemmald;
	private boolean showSurfaceForm = false;

	public static void main(String args[]) throws Exception
	{
		RunIceTaggerApertium runner = new RunIceTaggerApertium();
		Date before = runner.initialize(args);
		
		// Lemmald is only used when icemorphy tokenizes, since we don't have the lemma there.
		// In apertium we get the lemma from lt-proc
		if(runner.externalAnalysis.equals("icenlp"))
		{
			runner.lemmald = Lemmald.getInstance();
		}

		// create new instance of the mapping lexicon.
		runner.mappingLexicon = new MappingLexicon(runner.tagMapFile, false, false, false, "<NOT MAPPED>", true);
		runner.mappingLexicon.setLeave_lexemes_of_length_one_unchanged(true);

		// Perform the tagging
		runner.tokenizer.dateHandling(true); // Group dates into a single lexeme
		runner.performTagging();
		runner.finish(before);
	}

	// we override getParameters
	protected void getParameters(String args[])
	{
		super.getParameters(args);
		
		// The parameter showSurfaceForm is only used in IceTaggerApertium
		for (int i = 0; i <= args.length - 1; i++)
		{
			if (args[i].equals("-sf"))
				showSurfaceForm = true;
		}
	}

	// we override loadParameters to get surface form from the parameter file
	protected void loadParameters(String filename) throws IOException
	{
		// Load the regular parameters
		super.loadParameters(filename);

		Properties parameters = new Properties();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
		parameters.load(in);

		String sf = parameters.getProperty("SURFACE_FORM");

		if (sf.equals("yes"))
		{
			showSurfaceForm = true;
		}
	}

	protected void performTagging() throws IOException
	{
		if (standardInputOutput)
		{
			BufferedWriter out = FileEncoding.getWriter(System.out);

			// External morpho analyzer
			if (externalAnalysis.equals("apertium"))
			{
				tagTextExternal(out);
			}
			else
			{
				tagText(out);
			}
		}
		else if (fileList == null)
		{
			BufferedWriter out = FileEncoding.getWriter(outputFile);

			// External morpho analyzer
			if (externalAnalysis.equals("apertium"))
			{
				tagTextExternal(out);
			}
			else
			{
				tagText(out);
			}
		}
		else
		{
			tagAllFiles();
		}

		logger.close();
	}

	// Tags a text using an external morpho analyzer
	protected void tagTextExternal(BufferedWriter outFile) throws IOException
	{
		ApertiumSegmentizer segmentizer;
		
		if(inputFile != null)
		{
			segmentizer = new ApertiumSegmentizer(inputFile);
		}
		else
		{
			segmentizer = new ApertiumSegmentizer(System.in);
		}
		
		LtProcParser lps;
		ArrayList<ApertiumEntry> entries;
		
		IceNLPTokenConverter converter;
		ArrayList<IceTokenTags> tokens;
		
		while(segmentizer.hasMoreSentences())
		{
			// Send the lt-proc string into the parser
			lps = new LtProcParser(segmentizer.getSentance());
			entries = lps.parse();

			// Create the appertium -> iceNLP converter
			converter = new IceNLPTokenConverter(entries, mappingLexicon);
			tokens = converter.convert();

			// Do the actual tagging
			tagger.tagExternalTokens(tokens);
			
			// Reverts some changes IceTagger makes
			converter.changeReflexivePronounTags(tokens);
			
			// Display the results
			printResultsExternal(outFile, tokens, entries, mappingLexicon);
			
			outFile.flush();
			
			segmentizer.processNextSentence();
		}

		outFile.close();
	}

	// we override showParameters
	protected void showParameters()
	{
		super.showParameters();
		System.out.println("  -sf (print surface form)");
	}

	// we override printResults.
	protected void printResultsExternal(BufferedWriter outFile, ArrayList<IceTokenTags> tokens, ArrayList<ApertiumEntry> entries, MappingLexicon mappingLexicon) throws IOException
	{
		String lexeme;
		List<Word> wordList = new LinkedList<Word>();
		LemmaGuesser guesser;
		
		// Nothing to tokenize		
		if(tokens.size() == 0)
		{
			return;
		}

		// create word objects and add them to the wordlist.
		for (IceTokenTags t : tokens)
		{
			// Strange place to count this. Can we move this somewhere else?
			numTokens++;
			
			// Unknown check
			boolean unknown = t.isUnknown();
			
			// Unknown checks
			if(!unknown)
			{
				// If word is unknown external (marked as unknown from ltproc
				// or if the word is marked as a foreign word
				// Then it is unknown
				if(t.isUnknownExternal() || ((IceTag)t.getFirstTag()).isForeign())
				{
					unknown = true;
				}
			}
			
			if (unknown)
			{
				numUnknowns++;
			}

			// Make sure we use lower case for lexemes before we ask for the lemma
			if (!t.isProperNoun() && Character.isUpperCase(t.lexeme.charAt(0)))
			{
				lexeme = t.lexeme.toLowerCase();
			}
			else
			{
				lexeme = t.lexeme;
			}
			
			String lemma = t.getFirstTag().getLemma();
			
			// If we have lost the lemma on the way
			// this usually happens when IceTagger is forced to guess the symbols
			// We don't concider external unknown words, since they display their lexeme as the lemma
			if(lemma == null && !t.isUnknownExternal())
			{
				guesser = new LemmaGuesser(lexeme, entries, t.getFirstTagStr(), mappingLexicon);
				lemma = guesser.guess();
			}

			wordList.add(new Word(t.lexeme, lemma, t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord, unknown));
		}

		// Maps back from the IceNLP tags to the Apertium tags
		this.mappingLexicon.processWordList(wordList);

		// Create output string that will be sent to the client.
		String output = "";

		for (Word word : wordList)
		{
			if (outputFormat == Segmentizer.tokenPerLine)
			{
				if (showSurfaceForm)
				{
					// We display unknown words in a different way
					if(word.isUnknown())
					{
						outFile.write("^" + word.getLexeme() + "/*" + word.getLexeme() + "$");
					}
					else
					{
						// Handling the printing of "space" characters
						// They have empty tags
						if(word.getTag().equals("") && word.getLemma() == null)
						{
							outFile.write("^" + word.getLexeme() + "/" + word.getLexeme() + "$");
						}
						else
						{
							outFile.write("^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$");
						}
					}
				}
				else
				{
					outFile.write("^" + word.getLemma() + word.getTag() + "$");
				}
				
				outFile.newLine();
			}
			else
			{
				if (!word.linkedToPreviousWord)
				{
					output = output + " ";
				}
					
				if (showSurfaceForm)
				{
					if(word.isUnknown())
					{
						output = output + "^" + word.getLexeme() + "/*" + word.getLexeme() + "$";
					}
					else
					{
						// Handling the printing of "space" characters
						// They have empty tags
						if(word.getTag().equals("") && word.getLemma() == null)
						{
							output = output + "^" + word.getLexeme() + "/" + word.getLexeme() + "$";
						}
						else
						{
							output = output + "^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$";
						}
					}
				}
				else
				{
					output = output + "^" + word.getLemma() + word.getTag() + "$";
				}
			}
		}
		
		if (outputFormat != Segmentizer.tokenPerLine)
		{
			// Remove the first char if it is a space.
			if (output.charAt(0) == ' ')
			{
				output = output.substring(1, output.length());
			}
			outFile.write(output);
		}
		outFile.newLine();
	}

	// we override printResults.
	protected void printResults(BufferedWriter outFile) throws IOException
	{
		String lexeme;
		List<Word> wordList = new LinkedList<Word>();
		
		// Nothing to tokenize		
		if(tokenizer.tokens.size() == 0)
		{
			return;
		}

		// create word objects and add them to the wordlist.
		for (Object o : tokenizer.tokens)
		{
			IceTokenTags t = ((IceTokenTags) o);
			// Strange place to count this. Can we move this somewhere else?
			numTokens++;
			if (t.isUnknown())
				numUnknowns++;

			// Make sure we use lower case for lexemes before we ask for the lemma
			if (!t.isProperNoun() && Character.isUpperCase(t.lexeme.charAt(0)))
			{
				lexeme = t.lexeme.toLowerCase();
			}
			else
			{
				lexeme = t.lexeme;
			}

			wordList.add(new Word(t.lexeme, this.lemmald.lemmatize(lexeme, t.getFirstTagStr()).getLemma(), t
					.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord));
		}

		this.mappingLexicon.processWordList(wordList);

		// Create output string that will be sent to the client.
		String output = "";

		for (Word word : wordList)
		{
			if (outputFormat == Segmentizer.tokenPerLine)
			{
				if (showSurfaceForm)
					outFile.write("^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$");
				else
					outFile.write("^" + word.getLemma() + word.getTag() + "$");
				outFile.newLine();
			}
			else
			{
				if (!word.linkedToPreviousWord)
					output = output + " ";
				if (showSurfaceForm)
					output = output + "^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$";
				else
					output = output + "^" + word.getLemma() + word.getTag() + "$";
			}
		}

		if (outputFormat != Segmentizer.tokenPerLine)
		{
			// Remove the first char if it is a space.
			if (output.charAt(0) == ' ')
				output = output.substring(1, output.length());
			outFile.write(output);
		}
		outFile.newLine();
	}
}
