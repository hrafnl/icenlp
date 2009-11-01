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
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.ru.icenlpserver.common.Configuration;
import is.ru.icenlpserver.common.Pair;
import is.ru.icenlpserver.common.Word;
import is.ru.icenlpserver.icenlp.MappingLexicon;

public class IceTagger implements IIceTagger 
{
	private IceTaggerFacade facade;
	private Lemmald lemmald = null;
	private MappingLexicon mappingLexicon = null;
	private String taggingOutputForamt = null;
	private boolean lemmatize = false;
	public IceTagger() throws IceTaggerConfigrationException
	{
		try
		{
			// Check for the tagging output
			if(Configuration.getInstance().containsKey("taggingoutputformat"))
			{
				this.taggingOutputForamt = Configuration.getInstance().getValue("taggingoutputformat");
				
				if(this.taggingOutputForamt.contains("[LEMMA]"))
					this.lemmatize = true;
				
				System.out.println("[i] tagging output format: " + this.taggingOutputForamt + '.');
			}
			
			// Loading IceTagger lexicons.
			IceTaggerLexicons iceLexicons = null;
			if(!Configuration.getInstance().containsKey("icelexiconsdir"))
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
				String iceLexiconDirs = Configuration.getInstance().getValue("IceLexiconsDir");

				if(!iceLexiconDirs.endsWith("/"))
					iceLexiconDirs = iceLexiconDirs + '/';
				System.out.println("[i] Reading IceTagger lexicon from " + iceLexiconDirs + '.');
				iceLexicons = new IceTaggerLexicons(iceLexiconDirs);
			}
			
			// Loading tokenizer lexicon.
			Lexicon tokLexicon = null;//new Lexicon(Configuration.tokenizerLexicon);
			if(!Configuration.getInstance().containsKey("tokenizerlexicon"))
			{
				TokenizerResources tokResources = new TokenizerResources();
		        if (tokResources.isLexicon == null) throw new Exception( "Could not locate token dictionary");
		        System.out.println("[i] Reading Tokenizer lexicon from IceNLP resource.");
		        tokLexicon = new Lexicon(tokResources.isLexicon);
			}
			else
			{
				String tokenLexicon = Configuration.getInstance().getValue("tokenizerlexicon");
				System.out.println("[i] Reading Tokenizer lexicon from " + tokenLexicon + '.');
				tokLexicon = new Lexicon(tokenLexicon);
			}
			
			// If the user wants to use the mapper lexicon we must build one.
			if(Configuration.getInstance().containsKey("mappinglexicon"))
			{
				String mappingLexicon = Configuration.getInstance().getValue("mappinglexicon");
				System.out.println("[i] Reading mapping lexicon from: " + mappingLexicon + '.');
				this.mappingLexicon = new MappingLexicon(mappingLexicon);
			}
			
			if(this.lemmatize)
			{
				System.out.println("[i] Loading Lemmald.");
				this.lemmald = Lemmald.getInstance();
			}
				
			facade = new IceTaggerFacade(iceLexicons, tokLexicon);
			
			// Let's check for the TriTagger
			if(Configuration.getInstance().containsKey("tritagger"))
			{
				if(Configuration.getInstance().getValue("tritagger").equals("true"))
				{
					TriTaggerLexicons triLexicons = null;
			        if(!Configuration.getInstance().containsKey("tritaggerlexicon"))
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
			        	String tritaggerLexicon = Configuration.getInstance().getValue("tritaggerlexicon");
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
	
	@Override
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
					wordList.add(new Word(t.lexeme, t.getFirstTagStr()));
				}
			}
			
			if(this.lemmatize)
			{
				for(Word word: wordList)
					word.setLemma(this.lemmald.lemmatize(word.getLexeme(), word.getTag()).getLemma());
			}
			
			// If there is any mapper then we will use it.
			if(this.mappingLexicon != null)
			{
				for(Word word : wordList)
				{
					String mappedTag = mappingLexicon.lookupTagmap(word.getTag(), false);
					if(mappedTag != null)
						word.setTag(mappedTag);
					else
						word.setTag("<NOT MAPPED>");
				}
			}
			
			// Go over Lemma Exception rules.
			if(this.mappingLexicon != null)
			{
				for(Word word : wordList)
				{
					String lookupWord = word.getLemma();
	
					if(this.mappingLexicon.hasExceptionRulesForLemma(lookupWord))
					{
						List<Pair<String, String>> rules = this.mappingLexicon.getExceptionRulesForLemma(lookupWord);
						for(Pair<String, String> pair : rules)
						{
							if(word.getTag().matches(".*" +pair.one +".*"))
							{
								word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
								break;
							}
						}
					}
				}
			}
			
			// Go over lexeme exception rules.
			if(this.mappingLexicon != null)
			{
				for(Word word : wordList)
				{
					String lookupWord = word.getLexeme();
	
					if(this.mappingLexicon.hasExceptionRulesForLexeme(lookupWord))
					{
						List<Pair<String, String>> rules = this.mappingLexicon.getExceptionRulesForLexeme(lookupWord);
						for(Pair<String, String> pair : rules)
						{
							if(word.getTag().matches(".*" +pair.one +".*"))
							{
								word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
								break;
							}
						}
					}
				}
			}
			
			// Create output string that will be sent to the client.
			String output ="";
			
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
