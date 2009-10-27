package is.ru.icecache.icenlp.icetagger;

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
import is.ru.icecache.common.Configuration;
import is.ru.icecache.common.Pair;
import is.ru.icecache.common.Word;
import is.ru.icecache.icenlp.MapperLexicon;

public class IceTagger implements IIceTagger 
{
	private IceTaggerFacade facade;
	private Lemmald lemmald = null;
	private MapperLexicon mappingLexicon = null;
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
				{
					this.lemmatize = true;
				}
				
				System.out.println("[i] tagging output format: " + this.taggingOutputForamt);
			}
			
			// Loading IceTagger lexicons.
			IceTaggerLexicons iceLexicons = null;
			if(!Configuration.getInstance().containsKey("IceLexiconsDir"))
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
				iceLexicons = new IceTaggerLexicons(iceResources);
				System.out.println("[i] Using IceTagger lexicons from IceNLP resource.");
			}
			else
			{
				String iceLexiconDirs = Configuration.getInstance().getValue("IceLexiconsDir");
				iceLexicons = new IceTaggerLexicons(iceLexiconDirs);
				System.out.println("[i] using IceTagger lexicon from " + iceLexiconDirs);
			}
			
			// Loading tokenizer lexicon.
			Lexicon tokLexicon = null;//new Lexicon(Configuration.tokenizerLexicon);
			if(!Configuration.getInstance().containsKey("tokenizerlexicon"))
			{
				TokenizerResources tokResources = new TokenizerResources();
		        if (tokResources.isLexicon == null) throw new Exception( "Could not locate token dictionary");
		        tokLexicon = new Lexicon(tokResources.isLexicon);
		        System.out.println("[i] Using Tokenizer lexicon from IceNLP resource.");
			}
			else
			{
				String tokenLexicon = Configuration.getInstance().getValue("tokenizerlexicon");
				tokLexicon = new Lexicon(tokenLexicon);
				System.out.println("[i] Using Tokenizer lexicon from " + tokenLexicon);
			}
			
			// If the user wants to use the mapper lexicon we must build one.
			if(Configuration.getInstance().containsKey("mappinglexicon"))
			{
				String mappingLexicon = Configuration.getInstance().getValue("mappinglexicon");
				this.mappingLexicon = new MapperLexicon(mappingLexicon);
				System.out.println("[i] using mapping lexicon: " + mappingLexicon);
			}
			

			if(this.lemmatize)
			{
				System.out.println("[i] Creating instance of Lemmald.");
				this.lemmald = Lemmald.getInstance();
			}
				
			facade = new IceTaggerFacade(iceLexicons, tokLexicon);
			
			// Let's check for the TriTagger
			TriTaggerLexicons triLexicons = null;//new TriTaggerLexicons(Configuration.tritaggerLexicon, true);
	        if(!Configuration.getInstance().containsKey("tritaggerlexicon"))
	        {
	            TriTaggerResources triResources = new TriTaggerResources();
	    		if( triResources.isNgrams == null ) throw new Exception("Could not locate model ngram");
	    		if( triResources.isLambda == null ) throw new Exception( "Could not locate lambdas");
	    		if( triResources.isFrequency == null ) throw new Exception("Could not locate model frequency");
	    		triLexicons = new TriTaggerLexicons(triResources, true);
	    		System.out.println("[i] Using Tritagger lexicon from IceNLP resource");
	        }
	        else
	        {
	        	String tritaggerLexicon = Configuration.getInstance().getValue("tritaggerlexicon");
	        	triLexicons = new TriTaggerLexicons(tritaggerLexicon, true);
	        	System.out.println("[i] Using Tritagger lexicon from " + tritaggerLexicon);
	        }
	        
			facade.createTriTagger(triLexicons);
	        if(Configuration.getInstance().containsKey("tritagger"))
	        {
	        	if(Configuration.getInstance().getValue("tritagger").toLowerCase().equals("true"))
	        	{
	        		facade.useTriTagger(true);
	        		System.out.println("[i] Tritagger is used with IceTagger");
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
			
			// Let's check if there are any mapping exceptions that
			// we need to change.
			if(this.mappingLexicon != null)
			{
				for(Word word : wordList)
				{
					String lookupWord = word.getLemma();
	
					if(this.mappingLexicon.hasExceptionRuleForLemma(lookupWord))
					{
						List<Pair<String, String>> rules = this.mappingLexicon.getExceptionRuleForLexeme(lookupWord);
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
			
			// Let's create the output string.
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
			
			// if we have any tagging output set.
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}
}
