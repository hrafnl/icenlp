package is.iclt.icenlp.core.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.icetagger.IceTagger;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTaggerOutput;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tritagger.TriTagger;
import is.iclt.icenlp.core.utils.IceLog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic setup of IceTagger.
 */
public class IceTaggerWithIceMorphyTests
{
	private IceTagger tagger;
	private IceTaggerLexicons iceLex = null;
	private IceMorphy morphoAnalyzer;
	private IceLog logger;
	private IceTaggerOutput iceOutput = null;
	private TriTagger triTagger = null;
	private IceTagger.HmmModelType modelType = IceTagger.HmmModelType.none;

	@Before
	public void setUp() throws Exception
	{
		logger = new IceLog("");
		
		getIceTaggerLexicons();

		morphoAnalyzer = new IceMorphy(iceLex.morphyLexicons.dict, iceLex.morphyLexicons.baseDict,
				iceLex.morphyLexicons.endingsBase, iceLex.morphyLexicons.endings, iceLex.morphyLexicons.endingsProper,
				iceLex.morphyLexicons.prefixes, iceLex.morphyLexicons.tagFrequency, logger);

		tagger = new IceTagger(IceTagger.sentenceStartUpperCase, logger, morphoAnalyzer, iceLex.morphyLexicons.baseDict,
				iceLex.morphyLexicons.dict, iceLex.idioms, iceLex.verbPrep, iceLex.verbObj, iceLex.verbAdverb,
				false, true, triTagger, modelType);
		
		tagger.setSameTagForAllNumbers(true);
        tagger.setNamedEntityRecognition(false);
	}
	
	// Get the lexicons either from the resources or the parameters
    private void getIceTaggerLexicons() throws IOException
    {
        InputStream isDictionaryBase, isDictionary, isEndingsBase, isEndings, isEndingsProper,
                    isVerbPrep, isVerbObj, isVerbAdverb, isIdioms, isPrefixes, isTagFrequency;

        IceTaggerResources iceResources = new IceTaggerResources();

        isDictionaryBase = iceResources.isDictionaryBase;
        isDictionary = iceResources.isDictionary;
        isEndingsBase = iceResources.isEndingsBase;
        isEndings = iceResources.isEndings;
        isEndingsProper = iceResources.isEndingsProper;
        isVerbPrep = iceResources.isVerbPrep;
        isVerbObj = iceResources.isVerbObj;
        isVerbAdverb = iceResources.isVerbAdverb;
        isIdioms = iceResources.isIdioms;
        isPrefixes = iceResources.isPrefixes;
        isTagFrequency = iceResources.isTagFrequency;

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

	@After
	public void tearDown() throws Exception
	{
	}
	
	private ArrayList<IceTokenTags> simpleTokenTagCreator()
	{
		ArrayList<IceTokenTags> tokens = new ArrayList<IceTokenTags>();
		
		IceTokenTags hann = new IceTokenTags();
		hann.lexeme = "Hann";
		
		IceTokenTags er = new IceTokenTags();
		er.lexeme = "er";
		
		IceTokenTags godur = new IceTokenTags();
		godur.lexeme = "góður.";
		
		tokens.add(hann);
		tokens.add(er);
		tokens.add(godur);
		
		return tokens;
	}

	// TODO Fix 
	// java.lang.NullPointerException: InputStream was not initialized correctly (null)
	// at is.iclt.icenlp.core.utils.Lexicon.<init>(Unknown Source)
	@Test
	public void simpleTokenTag() throws Exception
	{
		ArrayList<IceTokenTags> tokens = simpleTokenTagCreator();
		
		tagger.tagTokens(tokens);
		
		IceTokenTags hann = tokens.get(0);
		IceTokenTags er = tokens.get(1);
		IceTokenTags godur = tokens.get(2);
		
		assertTrue("Lexeme not correct!", hann.lexeme.equals("Hann"));
		assertTrue("Lexeme not correct!", er.lexeme.equals("er"));
		assertTrue("Lexeme not correct!", godur.lexeme.equals("góður."));
		
		assertTrue("Tag not correct!", hann.getFirstTagStr().equals("fpken"));
		assertTrue("Tag not correct!", er.getFirstTagStr().equals("sfg3en"));
		assertTrue("Tag not correct!", godur.getFirstTagStr().equals("nven"));
	}
}