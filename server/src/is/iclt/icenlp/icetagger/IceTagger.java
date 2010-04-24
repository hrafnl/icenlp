package is.iclt.icenlp.icetagger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apertium.lttoolbox.process.FSTProcessor;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Token;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.iclt.icenlp.core.utils.MappingLexicon;
import is.iclt.icenlp.common.configuration.Configuration;

public class IceTagger implements IIceTagger {
	private IceTaggerFacade facade;
	private Lemmald lemmald = null;
	private MappingLexicon mapperLexicon = null;
	private String taggingOutputForamt = null;
	private boolean lemmatize = false;
	private boolean leave_not_found_tag_unchanged = false;
	private String not_found_tag = null;
	private Configuration configuration;
	private String punctuationSeparator = " ";
	private String taggingOutputSparator = " ";
	private FSTProcessor fstp = null;

	public IceTagger() throws IceTaggerConfigrationException {
		// Store the reference to the configuration in member to
		// minimize function calls to getInstance().
		this.configuration = Configuration.getInstance();

		if (this.configuration.containsKey("PunctuationSeparator"))
			this.punctuationSeparator = this.configuration.getValue("PunctuationSeparator");

		if (this.configuration.containsKey("TaggingOutputSparator"))
			this.taggingOutputSparator = this.configuration.getValue("TaggingOutputSparator");

		if (this.configuration.containsKey("compiled_bidix")) {
			String compiledBidixLocation = this.configuration.getValue("compiled_bidix");
			File file = new File(compiledBidixLocation);

			if (!file.exists() || !file.canRead()) 
				System.out.println("[!!] Unable to read compiled bidix "+ compiledBidixLocation + ".");
			else 
			{
				System.out.println("[i] Loading compiled bidix "+ compiledBidixLocation + ".");
				this.fstp = new FSTProcessor();
				try 
				{
					fstp.load(new BufferedInputStream(new FileInputStream(compiledBidixLocation)));
					System.out.println("[i] Compiled bidix loaded");
				} 
				catch (FileNotFoundException e) 
				{
					this.fstp = null;
					System.out.println("[!!] Unable to read compiled bidix "+ compiledBidixLocation + ".");
				} 
				catch (IOException e) 
				{
					System.out.println("[!!] Unable to read compiled bidix "+ compiledBidixLocation + ".");
					this.fstp = null;
				}
				fstp.initBiltrans();
				System.out.println("[i] FST for compiled bidix ready.");
			}

		}

		try {
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

				if (this.taggingOutputForamt.contains("[LEMMA]"))
					this.lemmatize = true;

				System.out.println("[i] tagging output format: "
						+ this.taggingOutputForamt + '.');
			}

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
					throw new Exception(
							"Could not locate verb adverb dictionary");
				System.out
						.println("[i] Reading IceTagger lexicons from IceNLP resource.");
				iceLexicons = new IceTaggerLexicons(iceResources);
			} else {
				String iceLexiconDirs = this.configuration
						.getValue("IceLexiconsDir");

				if (!iceLexiconDirs.endsWith("/"))
					iceLexiconDirs = iceLexiconDirs + '/';
				System.out.println("[i] Reading IceTagger lexicon from "
						+ iceLexiconDirs + '.');
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
				
				if(mappingLexicon.toLowerCase().equals("icenlp"))
				{
					System.out.println("[i] Reading mapping lexicon from IceNLP resource.");
					this.mapperLexicon = new MappingLexicon(true,
							this.leave_not_found_tag_unchanged, this.configuration
									.debugMode(), this.not_found_tag);	
				}
				else{
					System.out.println("[i] Reading mapping lexicon from: "+ mappingLexicon + '.');
					this.mapperLexicon = new MappingLexicon(mappingLexicon, true,
							this.leave_not_found_tag_unchanged, this.configuration
									.debugMode(), this.not_found_tag);
				}
			}

			if (this.lemmatize) {
				System.out.println("[i] Loading Lemmald.");
				this.lemmald = Lemmald.getInstance();
			}

			facade = new IceTaggerFacade(iceLexicons, tokLexicon);
			// This should be handed as an option in the config file!
			facade.dateHandling(true);  // Do special date handling

			// Let's check for the TriTagger
			if (this.configuration.containsKey("tritagger")) {
				if (this.configuration.getValue("tritagger").equals("true")) {
					TriTaggerLexicons triLexicons = null;
					if (!this.configuration.containsKey("tritaggerlexicon")) {
						TriTaggerResources triResources = new TriTaggerResources();
						if (triResources.isNgrams == null)
							throw new Exception("Could not locate model ngram");
						if (triResources.isLambda == null)
							throw new Exception("Could not locate lambdas");
						if (triResources.isFrequency == null)
							throw new Exception(
									"Could not locate model frequency");
						System.out
								.println("[i] Reading Tritagger lexicon from IceNLP resource.");
						triLexicons = new TriTaggerLexicons(triResources, true);
					} else {
						String tritaggerLexicon = this.configuration
								.getValue("tritaggerlexicon");
						System.out
								.println("[i] Reading Tritagger lexicon from "
										+ tritaggerLexicon + '.');
						triLexicons = new TriTaggerLexicons(tritaggerLexicon,
								true);

					}
					facade.createTriTagger(triLexicons);
					// This will make Tritagger perform initial word class selection and final disambiguation
					//facade.useTriTagger(true);   // equivalent to facade.setModelType(IceTagger.HmmModelType.startend)
					// This makes TriTagger only do final disambiguation
					facade.setModelType(is.iclt.icenlp.core.icetagger.IceTagger.HmmModelType.end);
					System.out.println("[i] Tritagger is ready.");
				}
			}
		}

		catch (Exception e) {
			throw new IceTaggerConfigrationException(e.getMessage(), e);
		}
	}

	private String getLemma(IceTokenTags t)
	{
        // Make sure we provide the lemmatizer with a lower case lexeme
        String lexeme, lemma;

        // The first letter is often capitalised at the beginning of a sentence
        if (!t.isProperNoun() && Character.isUpperCase( t.lexeme.charAt( 0 ) )) {
              lexeme = t.lexeme.toLowerCase();
        }
        else
            lexeme = t.lexeme;

        lemma = this.lemmald.lemmatize(lexeme,t.getFirstTagStr()).getLemma();
        return lemma;
    }


	public String tag(String text) {
		if(text.length() == 0) return "";
		
		List<Word> wordList = new LinkedList<Word>();
		try {

			Sentences sentences = facade.tag(text);

			for (Sentence s : sentences.getSentences()) {
				for (Token token : s.getTokens()) {
					IceTokenTags t = ((IceTokenTags) token);

					if (this.lemmatize) {
					    String lemma = getLemma(t);
                        wordList.add(new Word(t.lexeme, lemma, t.getFirstTagStr(),
							t.mweCode, t.tokenCode, t.linkedToPreviousWord));
                    }
		            else
					    wordList.add(new Word(t.lexeme, t.getFirstTagStr(),
							t.mweCode, t.tokenCode, t.linkedToPreviousWord));
				}
			}

			// Apply mapping rules to the word list.
			if (this.mapperLexicon != null)
				this.mapperLexicon.processWordList(wordList);

			// Create output string that will be sent to the client.
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
					String part = this.taggingOutputForamt.replace("[LEXEME]",word.getLexeme());
					part = part.replace("[TAG]", word.getTag());

					if (this.lemmatize)
						part = part.replace("[LEMMA]", word.getLemma());

					
					if(this.fstp != null)	
					{
						String check = "^" + word.getLemma() + word.getTag() + "$";
						String res = fstp.biltrans(check, true);
						if(res.startsWith("^@"))
						{
							if(this.configuration.debugMode())
								System.out.println("[debug] word " + word.getLemma() + " not found in bidix" );
							
							part = this.taggingOutputForamt.replace("[LEXEME]",word.getLexeme());
                            part = part.replace("[LEMMA]", "*" + word.getLexeme());
                            part = part.replace("[TAG]", "");
                            //part = part.replace("[TAG]", word.getTag());
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

		catch (IOException e) {
			System.out.println("[x] Error while generating output to client: "
					+ e.getMessage());
			return "error";
		}
	}
}
