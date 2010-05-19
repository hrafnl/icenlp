package is.iclt.icenlp.server.output;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import is.iclt.icenlp.IceParser.IIceParser;
import is.iclt.icenlp.IceParser.IceParser;
import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Token;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.MappingLexicon;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.iclt.icenlp.icetagger.IceTagger;
import is.iclt.icenlp.icetagger.IceTaggerConfigrationException;
import is.iclt.icenlp.icetagger.IceTaggerException;
import is.iclt.icenlp.lemmald.Lemmald;

import org.apertium.lttoolbox.process.FSTProcessor;

public class OutputGenerator {

	private Configuration configuration;
	private MappingLexicon mapperLexicon = null;
	
	private String taggingOutputForamt = null;
	private boolean lemmatize = false;
	private boolean leave_not_found_tag_unchanged = false;
	private String not_found_tag = null;
	
	private String punctuationSeparator = " ";
	private String taggingOutputSparator = " ";
	private FSTProcessor fstp = null;
	
	private IceTagger iceTagger;
	private IceParser iceParser;

	// TODO comment constructor.
	public OutputGenerator() throws Exception{
		
		// Get instance of icetagger.
		this.iceTagger = IceTagger.instance();
		
		// Store the reference to the configuration in member to
		// minimize function calls to getInstance().
		this.configuration = Configuration.getInstance();

		if (this.configuration.containsKey("PunctuationSeparator"))
			this.punctuationSeparator = this.configuration.getValue("PunctuationSeparator");

		if (this.configuration.containsKey("TaggingOutputSparator"))
			this.taggingOutputSparator = this.configuration.getValue("TaggingOutputSparator");

		if (this.configuration.containsKey("compiled_bidix")) 
		{
			String compiledBidixLocation = this.configuration.getValue("compiled_bidix");
			File file = new File(compiledBidixLocation);

			if (!file.exists() || !file.canRead())
				System.out.println("[!!] Unable to read compiled bidix "
						+ compiledBidixLocation + ".");
			else {
				System.out.println("[i] Loading compiled bidix "
						+ compiledBidixLocation + ".");
				this.fstp = new FSTProcessor();
				try {
					fstp.load(new BufferedInputStream(new FileInputStream(
							compiledBidixLocation)));
					System.out.println("[i] Compiled bidix loaded");
				} catch (FileNotFoundException e) {
					this.fstp = null;
					System.out.println("[!!] Unable to read compiled bidix "
							+ compiledBidixLocation + ".");
				} catch (IOException e) {
					System.out.println("[!!] Unable to read compiled bidix "
							+ compiledBidixLocation + ".");
					this.fstp = null;
				}
				fstp.initBiltrans();
				System.out.println("[i] FST for compiled bidix ready.");
			}

		}
		
		// check for not found tag. If there is no unfound_tag set in the
		// configuration file we use a default one: <NOT MAPPED>.
		if (this.configuration.containsKey("unfound_tag"))
			this.not_found_tag = this.configuration.getValue("unfound_tag");
		else
			this.not_found_tag = "<NOT MAPPED>";
		
		// Check for leave_not_found_tag_unchanged in the configuration
		// file.
		if (this.configuration.containsKey("leave_not_found_tag_unchanged")) {
			if (this.configuration
					.getValue("leave_not_found_tag_unchanged").equals(
							"true")) {
				this.leave_not_found_tag_unchanged = true;
				System.out
						.println("[i] unfound tags in tagmapping will be left unchanged if not found in mapping collection.");
			} else if (this.configuration.getValue(
					"leave_not_found_tag_unchanged").equals("false")) {
				this.leave_not_found_tag_unchanged = false;
				System.out
						.println("[i] unfound tags in tagmapping will be marked with "
								+ this.not_found_tag);
			} else {
				System.out
						.println("[x] leave_not_found_tag_unchanged can either be true or false. Set to default (true).");
				this.leave_not_found_tag_unchanged = true;
			}
		}
		
		// Check for the tagging output
		if (this.configuration.containsKey("taggingoutputformat")) {
			this.taggingOutputForamt = this.configuration
					.getValue("taggingoutputformat");

			if (this.taggingOutputForamt.contains("[LEMMA]")){
				this.lemmatize = true;
			}

			System.out.println("[i] tagging output format: "+ this.taggingOutputForamt + '.');
		}
		
		try {
			// Loading IceTagger lexicons.
			IceTaggerLexicons iceLexicons = null;
			if (!this.configuration.containsKey("icelexiconsdir")) {
				IceTaggerResources iceResources = new IceTaggerResources();
				if (iceResources.isDictionaryBase == null)
					throw new Exception("Could not locate base dictionary");
				if (iceResources.isDictionary == null)
					throw new Exception("Could not locate otb dictionary");
				if (iceResources.isEndingsBase == null)
					throw new Exception(
							"Could not locate endings base dictionary");
				if (iceResources.isEndings == null)
					throw new Exception("Could not locate endings dictionary");
				if (iceResources.isEndingsProper == null)
					throw new Exception(
							"Could not locate endings proper dictionary");
				if (iceResources.isPrefixes == null)
					throw new Exception("Could not locate prefixes dictionary");
				if (iceResources.isTagFrequency == null)
					throw new Exception(
							"Could not locate tag frequency dictionary");
				if (iceResources.isIdioms == null)
					throw new Exception("Could not locate idioms dictionary");
				if (iceResources.isVerbPrep == null)
					throw new Exception("Could not locate verb prep dictionary");
				if (iceResources.isVerbObj == null)
					throw new Exception("Could not locate verb obj dictionary");
				if (iceResources.isVerbAdverb == null)
					throw new Exception("Could not locate verb adverb dictionary");
				System.out.println("[i] Reading IceTagger lexicons from IceNLP resource.");
				iceLexicons = new IceTaggerLexicons(iceResources);
			} else {
				String iceLexiconDirs = this.configuration.getValue("IceLexiconsDir");

				if (!iceLexiconDirs.endsWith("/"))
					iceLexiconDirs = iceLexiconDirs + '/';
				System.out.println("[i] Reading IceTagger lexicon from " + iceLexiconDirs + '.');
				iceLexicons = new IceTaggerLexicons(iceLexiconDirs);
			}

			// Loading tokenizer lexicon.
			Lexicon tokLexicon = null;
			if (!this.configuration.containsKey("tokenizerlexicon")) {
				TokenizerResources tokResources = new TokenizerResources();
				if (tokResources.isLexicon == null)
					throw new Exception("Could not locate token dictionary");
				System.out
						.println("[i] Reading Tokenizer lexicon from IceNLP resource.");
				tokLexicon = new Lexicon(tokResources.isLexicon);
			} else {
				String tokenLexicon = this.configuration
						.getValue("tokenizerlexicon");
				System.out.println("[i] Reading Tokenizer lexicon from "
						+ tokenLexicon + '.');
				tokLexicon = new Lexicon(tokenLexicon);
			}

			// Check for a mappinglexicon lexicon configuration entry.
			if (this.configuration.containsKey("mappinglexicon")) {
				String mappingLexicon = this.configuration.getValue("mappinglexicon");

				if (mappingLexicon.toLowerCase().equals("icenlp")) {
					System.out.println("[i] Reading mapping lexicon from IceNLP resource.");
					this.mapperLexicon = new MappingLexicon(true,
							this.leave_not_found_tag_unchanged,
							this.configuration.debugMode(), this.not_found_tag);
				} else {
					System.out.println("[i] Reading mapping lexicon from: " + mappingLexicon + '.');
					this.mapperLexicon = new MappingLexicon(mappingLexicon,
							true, this.leave_not_found_tag_unchanged,
							this.configuration.debugMode(), this.not_found_tag);
				}

				// TODO Make this as an property in the server configuration
				// file.
				this.mapperLexicon.setLeave_lexemes_of_length_one_unchanged(true);

			}

			if (this.lemmatize) 
				this.iceTagger.lemmatize(true);
			
			this.iceParser = IceParser.instance();
			
			

		}

		catch (Exception e) {
			throw e;
		}

	}
	
	public String generateOutput(String text) {
		if (text.length() == 0 || !text.matches("\\w+"))
			return "";

		List<Word> wordList;
		try {
			wordList = this.iceTagger.tag(text);
		} catch (IceTaggerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

		
		// TODO: WE ARE TAGGING TWICE MUST FIX.
		//String strParse = this.iceParser.parse(text);
		
		/*
		try {

			
			// System.out.println(strParse);

			// Create a a word list from the tagging results.
			for (Sentence s : sentences.getSentences()) {
				for (Token token : s.getTokens()) {
					IceTokenTags t = ((IceTokenTags) token);

					if (this.lemmatize) {
						String lemma = getLemma(t);
						wordList.add(new Word(t.lexeme, lemma, t
								.getFirstTagStr(), t.mweCode, t.tokenCode,
								t.linkedToPreviousWord));
					} else
						wordList
								.add(new Word(t.lexeme, t.getFirstTagStr(),
										t.mweCode, t.tokenCode,
										t.linkedToPreviousWord));
				}
			}
			*/
		
		
			
			
			

			// go through the sentence list and place a number for it in the
			// parse list.
		/*	
		int i = 0;
			int lastIndex = 0;
			for (Word w : wordList) {
				int index = strParse.indexOf(w.getLexeme(), lastIndex);
				lastIndex = index;
				strParse = strParse
						.substring(0, index + w.getLexeme().length())
						+ "_"
						+ i
						+ " "
						+ strParse
								.substring(index + w.getLexeme().length() + 1);
				i = i + 1;
			}

			// Let's add the subj to correct words.
			for (String parseLine : strParse.split("\n")) {
				if (parseLine.contains("{*SUBJ")) {
					char arrow = parseLine.charAt(6);
					String[] parseLineTokens = parseLine.split(" ");
					// Search for the last word in the subj, that is the one
					// that
					// will get the subj to its tag.
					for (int j = parseLineTokens.length - 1; j >= 0; j--) {
						if (parseLineTokens[j].split("_").length >= 2) {

							String[] d = parseLineTokens[j].split("_");
							String wordIndexStr = d[d.length - 1];
							if (wordIndexStr.matches("[0-9]+")) {
								int ind = Integer.parseInt(wordIndexStr);
								if (ind > wordList.size())
									continue;
								if (arrow == '>')
									wordList.get(Integer
											.parseInt(d[d.length - 1])).parseString = "<@SUBJ→>";
								else
									wordList.get(Integer
											.parseInt(d[d.length - 1])).parseString = "<@SUBJ←>";
								break;
							}
						}
					}
				}
			}
			*/

			// System.out.println(strParse);

			// Apply mapping rules to the word list.
			if (this.mapperLexicon != null)
				this.mapperLexicon.processWordList(wordList);

			// Create output string that will be sent to the client.
			// TODO change for string builder.
			String output = "";

			// If we have not set any tagging output
			if (this.taggingOutputForamt == null) {
				for (Word word : wordList) {
					if (word.linkedToPreviousWord)
						output = output + punctuationSeparator
								+ word.getLexeme() + " " + word.getTag();

					else
						output = output + taggingOutputSparator
								+ word.getLexeme() + " " + word.getTag();
				}
			}

			// We have some tagging output set.
			else {
				for (Word word : wordList) {
					// add the parse tag to the of the tag.
					// TODO: move this into Word object.
					if (word.parseString != null)
						word.setTag(word.getTag() + word.parseString);

					String part = null;

					if (word.isOnlyOutputLexeme()) {
						part = word.getLexeme();
					} else {
						part = this.taggingOutputForamt.replace("[LEXEME]",
								word.getLexeme());
						part = part.replace("[TAG]", word.getTag());

						if (this.lemmatize)
							part = part.replace("[LEMMA]", word.getLemma());

						if (this.fstp != null && !word.isOnlyOutputLexeme()) {
							String check = "^" + word.getLemma()
									+ word.getTag() + "$";
							String res = fstp.biltrans(check, true);
							if (res.startsWith("^@")) {
								if (this.configuration.debugMode())
									System.out.println("[debug] word "
											+ word.getLemma()
											+ " not found in bidix");

								part = this.taggingOutputForamt.replace(
										"[LEXEME]", word.getLexeme());
								part = part.replace("[LEMMA]", "*"
										+ word.getLexeme());
								part = part.replace("[TAG]", "");
							}
						}
					}

					if (word.linkedToPreviousWord)
						output = output + punctuationSeparator + part;

					else
						output = output + taggingOutputSparator + part;
				}
			}

			// Remove the first char if it is a space.
			if (output.charAt(0) == ' ')
				output = output.substring(1, output.length());

			return output;
		}
	}

