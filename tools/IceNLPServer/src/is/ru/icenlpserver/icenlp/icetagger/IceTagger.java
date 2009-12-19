package is.ru.icenlpserver.icenlp.icetagger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
import is.ru.icenlpserver.common.Configuration;
import is.iclt.icenlp.core.utils.MappingLexicon;

public class IceTagger implements IIceTagger 
{
	private IceTaggerFacade facade;
	private Lemmald lemmald = null;
	private MappingLexicon mapperLexicon = null;
	private String taggingOutputForamt = null;
	private boolean lemmatize = false;
	private boolean leave_not_found_tag_unchanged = false;
	private String not_found_tag = null;
	private Configuration configuration;
	
	public IceTagger() throws IceTaggerConfigrationException
	{
		// Store the reference to the configuration in member to
		// minimize function calls to getInstance().
		this.configuration = Configuration.getInstance();
		
		try
		{
			// check for not found tag. If there is no unfound_tag set in the
			// configuration file we use a default one: <NOT MAPPED>.
			if(this.configuration.containsKey("unfound_tag"))
				this.not_found_tag = this.configuration.getValue("unfound_tag");
			else
				this.not_found_tag = "<NOT MAPPED>";
			
			
			//Check for leave_not_found_tag_unchanged in the configuration file.
			if(this.configuration.containsKey("leave_not_found_tag_unchanged"))
			{
				if(this.configuration.getValue("leave_not_found_tag_unchanged").equals("true"))	
				{
					this.leave_not_found_tag_unchanged = true;
					System.out.println("[i] unfound tags in tagmapping will be left unchanged if not found in mapping collection.");
				}
				else if(this.configuration.getValue("leave_not_found_tag_unchanged").equals("false"))
				{
					this.leave_not_found_tag_unchanged = false;
					System.out.println("[i] unfound tags in tagmapping will be marked with " + this.not_found_tag);
				}
				else
				{
					System.out.println("[x] leave_not_found_tag_unchanged can either be true or false. Set to default (true).");
					this.leave_not_found_tag_unchanged = true;
				}
			}
			
			
			// Check for the tagging output
			if(this.configuration.containsKey("taggingoutputformat"))
			{
				this.taggingOutputForamt = this.configuration.getValue("taggingoutputformat");
				
				if(this.taggingOutputForamt.contains("[LEMMA]"))
					this.lemmatize = true;
				
				System.out.println("[i] tagging output format: " + this.taggingOutputForamt + '.');
			}
						
			// Loading IceTagger lexicons.
			IceTaggerLexicons iceLexicons = null;
			if(!this.configuration.containsKey("icelexiconsdir"))
			{
		        IceTaggerResources iceResources = new IceTaggerResources();
		        if( iceResources.isDictionaryBase == null ) throw new Exception("Could not locate base dictionary");
		        if( iceResources.isDictionary == null ) throw new Exception("Could not locate otb dictionary");
		        if( iceResources.isEndingsBase == null ) throw new Exception("Could not locate endings base dictionary");
				if( iceResources.isEndings == null ) throw new Exception("Could not locate endings dictionary");
				if( iceResources.isEndingsProper == null ) throw new Exception("Could not locate endings proper dictionary");
				if( iceResources.isPrefixes == null ) throw new Exception("Could not locate prefixes dictionary");
				if( iceResources.isTagFrequency == null ) throw new Exception("Could not locate tag frequency dictionary" );
				if( iceResources.isIdioms == null ) throw new Exception("Could not locate idioms dictionary" );
				if( iceResources.isVerbPrep == null ) throw new Exception("Could not locate verb prep dictionary" );
				if( iceResources.isVerbObj == null ) throw new Exception("Could not locate verb obj dictionary");
				if( iceResources.isVerbAdverb == null ) throw new Exception("Could not locate verb adverb dictionary" );
				System.out.println("[i] Reading IceTagger lexicons from IceNLP resource.");
				iceLexicons = new IceTaggerLexicons(iceResources);
			}
			else
			{
				String iceLexiconDirs = this.configuration.getValue("IceLexiconsDir");

				if(!iceLexiconDirs.endsWith("/"))
					iceLexiconDirs = iceLexiconDirs + '/';
				System.out.println("[i] Reading IceTagger lexicon from " + iceLexiconDirs + '.');
				iceLexicons = new IceTaggerLexicons(iceLexiconDirs);
			}
			
			// Loading tokenizer lexicon.
			Lexicon tokLexicon = null;
			if(!this.configuration.containsKey("tokenizerlexicon"))
			{
				TokenizerResources tokResources = new TokenizerResources();
		        if (tokResources.isLexicon == null) throw new Exception( "Could not locate token dictionary");
		        System.out.println("[i] Reading Tokenizer lexicon from IceNLP resource.");
		        tokLexicon = new Lexicon(tokResources.isLexicon);
			}
			else
			{
				String tokenLexicon = this.configuration.getValue("tokenizerlexicon");
				System.out.println("[i] Reading Tokenizer lexicon from " + tokenLexicon + '.');
				tokLexicon = new Lexicon(tokenLexicon);
			}
			
			// If the user wants to use the mapper lexicon we must build one.
			if(this.configuration.containsKey("mappinglexicon"))
			{
				String mappingLexicon = this.configuration.getValue("mappinglexicon");
				System.out.println("[i] Reading mapping lexicon from: " + mappingLexicon + '.');
				this.mapperLexicon = new MappingLexicon(mappingLexicon, true, this.leave_not_found_tag_unchanged, this.configuration.debugMode(), this.not_found_tag);
			}
			
			if(this.lemmatize)
			{
				System.out.println("[i] Loading Lemmald.");
				this.lemmald = Lemmald.getInstance();
			}
				
			facade = new IceTaggerFacade(iceLexicons, tokLexicon);
			
			// Let's check for the TriTagger
			if(this.configuration.containsKey("tritagger"))
			{
				if(this.configuration.getValue("tritagger").equals("true"))
				{
					TriTaggerLexicons triLexicons = null;
			        if(!this.configuration.containsKey("tritaggerlexicon"))
			        {
			            TriTaggerResources triResources = new TriTaggerResources();
			    		if( triResources.isNgrams == null ) throw new Exception("Could not locate model ngram");
			    		if( triResources.isLambda == null ) throw new Exception( "Could not locate lambdas");
			    		if( triResources.isFrequency == null ) throw new Exception("Could not locate model frequency");
			    		System.out.println("[i] Reading Tritagger lexicon from IceNLP resource.");
			    		triLexicons = new TriTaggerLexicons(triResources, true);
			        }
			        else
			        {
			        	String tritaggerLexicon = this.configuration.getValue("tritaggerlexicon");
			        	System.out.println("[i] Reading Tritagger lexicon from " + tritaggerLexicon + '.');
			        	triLexicons = new TriTaggerLexicons(tritaggerLexicon, true);
			        	
			        }
			        facade.createTriTagger(triLexicons);
			        facade.useTriTagger(true);
		        	System.out.println("[i] Tritagger is ready.");
				}
			}
		}
		
		catch (Exception e) 
		{
			throw new IceTaggerConfigrationException(e.getMessage(), e);
		}
	}
	

	public String tag(String text) 
	{
		List<Word> wordList = new LinkedList<Word>();
		try 
		{
			Sentences sentences = facade.tag(text);
			
			for(Sentence s : sentences.getSentences())
			{
				for(Token token : s.getTokens())
				{
					IceTokenTags t = ((IceTokenTags)token);
					wordList.add(new Word(t.lexeme, t.getFirstTagStr(), t.mweCode));
				}
			}
			
			if(this.lemmatize)
			{
				for(Word word: wordList)
					word.setLemma(this.lemmald.lemmatize(word.getLexeme(), word.getTag()).getLemma());
			}			
			
			// Apply mapping rules to the word list.s
			this.mapperLexicon.processWordList(wordList);
			
			
			// Create output string that will be sent to the client.
			String output = "";
			
			// If we have not set any tagging output
			if(this.taggingOutputForamt == null)
			{
				if(this.lemmatize)
				{
					for(Word word: wordList)
						output = output + word.getLemma() + " " + word.getTag()+ " ";
				}
				else
				{
					for(Word word: wordList)
						output = output + word.getLexeme() + " " + word.getTag()+ " ";
				}				
			}

			else
			{
					for(Word word: wordList)
					{
						String part = this.taggingOutputForamt.replace("[LEXEME]", word.getLexeme());
						part = part.replace("[TAG]", word.getTag());
						if(this.lemmatize)
							part = part.replace("[LEMMA]", word.getLemma());
						output = output + part;
					}	
			}

			return output;
		} 
		
		catch (IOException e) 
		{
			System.out.println("[x] Error while generating output to client: " + e.getMessage());
			return "error";
		}
	}
}
