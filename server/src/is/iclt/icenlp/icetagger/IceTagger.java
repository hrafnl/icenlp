package is.iclt.icenlp.icetagger;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.apertium.ApertiumEntry;
import is.iclt.icenlp.core.apertium.LemmaGuesser;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenSentences;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.IceTokenSentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.utils.IceTag;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.MappingLexicon;
import is.iclt.icenlp.core.utils.Pair;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.iclt.icenlp.lemmald.Lemmald;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IceTagger implements IIceTagger {
	
	private static IceTagger instance_ = null;
	
	public static IceTagger instance() throws IceTaggerException
	{
		if(instance_ == null)
			instance_ = new IceTagger();
		return instance_; 
	}
	
	// instance of the configuration.
	private Configuration configuration;
	private IceTaggerLexicons iceLexicons = null;
	private Lexicon tokLexicon = null;
	private IceTaggerFacade iceTaggerFacade;
	private boolean lemmatize = false;
	private Lemmald lemmald = null;

	protected IceTagger() throws IceTaggerException 
	{
		// Get instance of the configuration class.
		this.configuration = Configuration.getInstance();
		
		try
		{
			// Loading IceTagger lexicons.
			if (!this.configuration.containsKey("icelexiconsdir")) 
			{
				IceTaggerResources iceResources = new IceTaggerResources();
				if (iceResources.isDictionaryBase == null)
					throw new Exception("Could not locate base dictionary");
				
				if (iceResources.isDictionary == null)
					throw new Exception("Could not locate otb dictionary");
				
				if (iceResources.isEndingsBase == null)
					throw new Exception("Could not locate endings base dictionary");
				
				if (iceResources.isEndings == null)
					throw new Exception("Could not locate endings dictionary");
				
				if (iceResources.isEndingsProper == null)
					throw new Exception("Could not locate endings proper dictionary");
				
				if (iceResources.isPrefixes == null)
					throw new Exception("Could not locate prefixes dictionary");
				
				if (iceResources.isTagFrequency == null)
					throw new Exception("Could not locate tag frequency dictionary");
				
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
			} 
			else 
			{
				String iceLexiconDirs = this.configuration.getValue("IceLexiconsDir");
	
				if (!iceLexiconDirs.endsWith("/"))
					iceLexiconDirs = iceLexiconDirs + '/';
				System.out.println("[i] Reading IceTagger lexicon from " + iceLexiconDirs + '.');
				this.iceLexicons = new IceTaggerLexicons(iceLexiconDirs);
			}
			
			
			// Loading tokenizer lexicon.
			this.tokLexicon = null;
			if (!this.configuration.containsKey("tokenizerlexicon")) 
			{
				TokenizerResources tokResources = new TokenizerResources();
				
				if (tokResources.isLexicon == null)
					throw new Exception("Could not locate token dictionary");
				
				System.out.println("[i] Reading Tokenizer lexicon from IceNLP resource.");
				tokLexicon = new Lexicon(tokResources.isLexicon);
			} 
			else
			{
				String tokenLexicon = this.configuration.getValue("tokenizerlexicon");
				System.out.println("[i] Reading Tokenizer lexicon from " + tokenLexicon + '.');
				tokLexicon = new Lexicon(tokenLexicon);
			}
			
			iceTaggerFacade = new IceTaggerFacade(this.iceLexicons, this.tokLexicon);
			
			// TODO This should be handed as an option in the config file!
			iceTaggerFacade.dateHandling(true); // Do special date handling
			
			// TriTagger
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
							throw new Exception("Could not locate model frequency");
						System.out.println("[i] Reading Tritagger lexicon from IceNLP resource.");
						triLexicons = new TriTaggerLexicons(triResources, true);
					} else {
						String tritaggerLexicon = this.configuration.getValue("tritaggerlexicon");
						System.out.println("[i] Reading Tritagger lexicon from " + tritaggerLexicon + '.');
						triLexicons = new TriTaggerLexicons(tritaggerLexicon, true);

					}
					iceTaggerFacade.createTriTagger(triLexicons);
					// This will make Tritagger perform initial word class
					// selection and final disambiguation
					// facade.useTriTagger(true); // equivalent to
					// facade.setModelType(IceTagger.HmmModelType.startend)
					// This makes TriTagger only do final disambiguation
					iceTaggerFacade.setModelType(is.iclt.icenlp.core.icetagger.IceTagger.HmmModelType.end);
					System.out.println("[i] Tritagger is ready.");
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IceTaggerException(e.getMessage());
		}
	}
	
	public List<Word> tag(String text) throws IceTaggerException {
		List<Word> returnlist = new LinkedList<Word>();
		if (text.length() == 0)
			return returnlist;
		
		try 
		{
			Sentences sentences = this.iceTaggerFacade.tag(text);
			// Create a a word list from the tagging results.
			for (Sentence s : sentences.getSentences()) {
				for (Object token : s.getTokens()) {
					IceTokenTags t = ((IceTokenTags) token);

					Word word;
					if (this.lemmatize) {
						String lemma = this.lemmald.getLemma(t);
						word = new Word(t.lexeme, lemma, t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord);
						
						if (t.preSpace != null){
							word.preSpace = t.preSpace;
						}
							
						returnlist.add(word);
					} 
					else{
						word = new Word(t.lexeme, t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord);
						
						if (t.preSpace != null){
							word.preSpace = t.preSpace;
						}
						
						returnlist.add(word);
					}
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new IceTaggerException(e.getMessage(), e);
		}
		return returnlist; 
	}
	
	public List<Word> tagExternal(String text, MappingLexicon mappingLexicon) throws IceTaggerException
	{
		List<Word> returnlist = new LinkedList<Word>();
		String lexeme;
		LemmaGuesser guesser;
		
		if (text.length() == 0)
		{
			return returnlist;
		}
		
		try
		{
			Pair<IceTokenSentences, ArrayList<ApertiumEntry>> facadePairs = this.iceTaggerFacade.tagExternal(text, mappingLexicon);
			IceTokenSentences sentences = facadePairs.one;
			ArrayList<ApertiumEntry> entries = facadePairs.two;
			
			// Create a a word list from the tagging results.
			for (IceTokenSentence s : sentences.getSentences())
			{
				for(Object token : s.getTokens())
				{
					IceTokenTags t = (IceTokenTags)token;
					
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

					returnlist.add(new Word(t.lexeme, lemma, t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord, unknown));
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new IceTaggerException(e.getMessage(), e);
		}
		
		return returnlist;
	}
	
	public void lemmatize(boolean value)
	{	
		this.lemmatize = value;
		if(value)
			this.lemmald = Lemmald.instance();
	}
}
