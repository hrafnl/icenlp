package is.ru.icecache.icenlp.icetagger;

import java.io.IOException;

import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.TokenizerResources;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.ru.icecache.common.Configuration;

public class IceTagger implements IIceTagger 
{
	private IceTaggerFacade facade;
	public IceTagger() throws IceTaggerConfigrationException
	{
		try
		{
			// Let's check if any of the configuration are not set.
			// if so, let's read it from the context.
			// Let's check for the IceTaggerLexicons.
			IceTaggerLexicons iceLexicons = null;
			if(Configuration.iceLexiconsDir == null)
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
				iceLexicons = new IceTaggerLexicons(Configuration.iceLexiconsDir);
				System.out.println("[i] using IceTagger lexicon from " + Configuration.iceLexiconsDir);
			}
			
			// Let's check for the tokenizer lexicon.
			Lexicon tokLexicon = null;//new Lexicon(Configuration.tokenizerLexicon);
			if(Configuration.tokenizerLexicon == null)
			{
				TokenizerResources tokResources = new TokenizerResources();
		        if (tokResources.isLexicon == null) throw new Exception( "Could not locate token dictionary");
		        tokLexicon = new Lexicon(tokResources.isLexicon);
		        System.out.println("[i] Using Tokenizer lexicon from IceNLP resource.");
			}
			else
			{
				tokLexicon = new Lexicon(Configuration.iceLexiconsDir);
				System.out.println("[i] Using Tokenizer lexicon from " + Configuration.iceLexiconsDir);
			}
			
			// If the user wants to use the mapper lexicon we must build one.
			if(Configuration.mapperLexicon != null)
			{
				Lexicon mapper = new Lexicon(Configuration.mapperLexicon);
				System.out.println("The user want's to use mapperlexicon");
			}
			
			if(Configuration.lemmatize)
			{
				System.out.println("The user want's to lemmatize the output.");
			}
			
			//Lexicon mapper = new Lexicon(Configuration.mapperLexicon);
			//System.out.println("[i] Using Lexicon mapper from " + Configuration.mapperLexicon);
			
			//facade = new IceTaggerFacade(iceLexicons, tokLexicon,mapper, Configuration.appertiumOutput);
			
			// We will now create normal instance of IceNLP.
			facade = new IceTaggerFacade(iceLexicons, tokLexicon);
			
			// Let's check for the TriTagger
			TriTaggerLexicons triLexicons = null;//new TriTaggerLexicons(Configuration.tritaggerLexicon, true);
	        if(Configuration.tritaggerLexicon == null)
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
	        	triLexicons = new TriTaggerLexicons(Configuration.tritaggerLexicon, true);
	        	System.out.println("[i] Using Tritagger lexicon from " + Configuration.tritaggerLexicon);
	        }
	        
			facade.createTriTagger(triLexicons);
	        if(Configuration.tritagger)
	        {
	        	facade.useTriTagger(true);
	        	System.out.println("[i] Tritagger loaded");
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
		try 
		{
			return facade.tag(text).toString();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}
}
