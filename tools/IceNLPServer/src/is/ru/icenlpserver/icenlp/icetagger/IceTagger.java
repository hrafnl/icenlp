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
import is.iclt.icenlp.core.tokenizer.Token.MWECode;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tritagger.TriTaggerResources;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.Pair;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.ru.icenlpserver.common.Configuration;
import is.iclt.icenlp.core.utils.MapperLexicon;

public class IceTagger implements IIceTagger 
{
	private IceTaggerFacade facade;
	private Lemmald lemmald = null;
	private MapperLexicon mapperLexicon = null;
	private String taggingOutputForamt = null;
	private boolean lemmatize = false;
	private boolean leave_not_found_tag_unchanged = false;
	private String not_found_tag = null;
	private Configuration configuration;
	private boolean debugOutput = true;
	
	public IceTagger() throws IceTaggerConfigrationException
	{
		// Store the reference to the configuration in member to
		// minimize function calls to getInstance().
		this.configuration = Configuration.getInstance();
		this.debugOutput = this.configuration.debugMode();
		
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
			
			// Check for debug output.
			this.debugOutput = this.configuration.debugMode();
			
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
				this.mapperLexicon = new MapperLexicon(mappingLexicon, true, this.leave_not_found_tag_unchanged, this.configuration.debugMode(), this.not_found_tag);
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
			
			// let's go through the tag mapping and check if there is any TAGMAPPING for that word.
			if(this.mapperLexicon != null)
			{
				for(Word word : wordList)
				{
					String mappedTag = mapperLexicon.lookupTagmap(word.getTag(), false);
					if(mappedTag != null)
					{
						if(this.debugOutput)
							System.out.println("[debug] tagmapping rule applied: " + word.getTag() + " -> " + mappedTag);
						
						word.setTag(mappedTag);
					}
					
					else
					{
						if(!this.leave_not_found_tag_unchanged)
						{
							if(this.debugOutput)
								System.out.println("[debug] tagmapping rule applied: " + word.getTag() + " -> " + this.not_found_tag);
							word.setTag(this.not_found_tag);
						}
						else
						{
							if(this.debugOutput)
								System.out.println("[debug] tagmapping rule applied: Leaving " + word.getTag() + " unchanged.");	
						}
					}
				}
			
				// Go over Lemma Exception rules.
				for(Word word : wordList)
				{
					String lookupWord = word.getLemma();
	
					if(this.mapperLexicon.hasExceptionRulesForLemma(lookupWord))
					{
						List<Pair<String, String>> rules = this.mapperLexicon.getExceptionRulesForLemma(lookupWord);
						for(Pair<String, String> pair : rules)
						{
							if(word.getTag().matches(".*" +pair.one +".*"))
							{
								if(this.debugOutput)
									System.out.println("[debug] applied Lemma exception rule for the lemma " + word.getLemma());
								
								word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
							}
						}
					}
				}
				
				
				// Go over Lexeme Exception rules.
				for(Word word : wordList)
				{
					String lookupWord = word.getLexeme();
	
					if(this.mapperLexicon.hasExceptionRulesForLexeme(lookupWord))
					{
						List<Pair<String, String>> rules = this.mapperLexicon.getExceptionRulesForLexeme(lookupWord);
						for(Pair<String, String> pair : rules)
						{
							if(word.getTag().matches(".*" +pair.one +".*"))
							{
								if(this.debugOutput)
									System.out.println("[debug] applied Lexeme exception rule for the lexeme " + word.getLexeme());
								
								word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
							}
						}
					}
				}
				
		
				// Go over the MWE expression
				for(int i = 0; i < wordList.size(); i++)
				{
					if(wordList.get(i).mweCode == MWECode.begins)
					{
						// the index of the words begins at index begins:
						int begins = i;
						int ends = 0;
						
						String mweStr = wordList.get(i).getLexeme();
						int j = i;
						while(j < wordList.size())
						{	
							if(wordList.get(j).mweCode == MWECode.ends)
							{
								// The words ends at there.
								ends = j;
								i = j;
								break;
							}
							if(j+1 < wordList.size())
								mweStr += "_" + wordList.get(j+1).getLexeme();

							j += 1;	
						}
						
						if(this.mapperLexicon.hasMapForMWE(mweStr))
						{	
							if(this.debugOutput)
								System.out.println("[debug] applied MWE rule for the mwe " + mweStr);
							
							String lemma = "";
							String lexeme = "";
							
							for(i = (ends - begins); i>= 0; i--)
							{
								lemma += wordList.get(begins).getLexeme() + " ";
								wordList.remove(begins);
							}
							
							// Where we are working with MWE, we overwrite the lemma with the lexeme.
							Word w = new Word(lexeme.substring(0,lexeme.length()-1), this.mapperLexicon.getMapForMWE(mweStr), MWECode.none);
							w.setLemma(lexeme.substring(0,lexeme.length()-1));
							wordList.add(begins, w);
						}
					}
				}
				
				// Go over MWE-RENAME rules.
				for(Word word : wordList)
				{
					if(this.mapperLexicon.hasRenameRuleForLexeme(word.getLexeme()))
					{
						Pair<String, String> pair = this.mapperLexicon.getRenameRuleForLexeme(word.getLexeme());
						
						word.setLemma(pair.one.replace('_', ' '));
						word.setLemma(pair.one.replace('_', ' '));
						word.setTag(pair.two);
						
						if(this.debugOutput)
							System.out.println("[debug] applied MWE-RENAME rule to word " + word.getLexeme());
					}	
				}
			}
			
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