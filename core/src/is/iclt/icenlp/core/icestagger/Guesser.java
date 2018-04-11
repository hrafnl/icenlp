package is.iclt.icenlp.core.icestagger;

import is.iclt.icenlp.core.icemorphy.IceMorphy;
import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;
import is.iclt.icenlp.core.icemorphy.IceMorphyResources;
import is.iclt.icenlp.core.icetagger.IceTaggerResources;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tritagger.FreqLexicon;
import is.iclt.icenlp.core.tritagger.FreqLexEntry;
import is.iclt.icenlp.core.utils.IceTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: hrafn
 * Date: 11/15/12
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Guesser {
    private static HashMap wordHash;   // Stores each word
    private static IceMorphy morphoAnalyzer;
    private static IceMorphyLexicons morphLex=null;
    
    public static void loadIceMorphy(String fold)
    {
        wordHash = new HashMap();
        try {
            System.err.println("Loading IceMorphy dictionaries ...");

            IceMorphyResources morphyResources = new IceMorphyResources();
            morphLex  = new IceMorphyLexicons(
                    morphyResources.isDictionaryBase,
                    morphyResources.isDictionary,
                    morphyResources.isEndingsBase,
                    morphyResources.isEndings,
                    morphyResources.isEndingsProper,
                    morphyResources.isPrefixes,
                    null);

            // The following should be used instead of the above when evaluating IceStagger on the different folds
            // In that case it is imperative that IceMorphy only uses the dictionaries derived from the training fold
            /*
            String dictPath = "/home/hrafn/nlp/java/icenlpDev/core/dictResearch/icetagger/otb/";
            String dictionaryBase = "baseDict.dict";
            String dictionary = ".dict";
            String endingsBaseDictionary = "baseEndings.dict";
            String endingsDictionary = ".endings.dict";
            String endingsProperDictionary = ".endingsProper.dict";
            String prefixesDictionary = "prefixes.dict";

            morphLex = new IceMorphyLexicons(
                    dictPath + dictionaryBase,
                    dictPath + fold + dictionary,
                    dictPath + endingsBaseDictionary,
                    dictPath + fold + endingsDictionary,
                    dictPath + fold + endingsProperDictionary,
                    dictPath + endingsBaseDictionary,
                    dictPath + prefixesDictionary);
            */
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Create the morphological analyzer
        morphoAnalyzer =  new IceMorphy(morphLex.dict, morphLex.baseDict,
                morphLex.endingsBase, morphLex.endings, morphLex.endingsProper,
                morphLex.prefixes, morphLex.tagFrequency, null);

    }

    public static boolean lookup(String lexeme, Lexicon posLexicon, TagSet tagSet, is.iclt.icenlp.core.utils.Lexicon lex)
    {
        boolean found=false;
        String tagStr = lex.lookup(lexeme, false);

        if (tagStr != null) // We found the lexeme with some tags
        {
            //System.err.println("Found tags in IceMorphy dict: " + lexeme + " " + tagStr);
            found = true;
            IceTokenTags myToken = new IceTokenTags();
            myToken.lexeme = lexeme;
            myToken.addAllTags(tagStr);
            myToken.cleanTags();    // Remove special markings used by IceMorphy/IceTagger
            ArrayList<IceTag> iceTags = myToken.getTags();

            for (IceTag tag : iceTags)
            {
                try {
                    int tagID =  tagSet.getTagID(tag.getTagStr());
                    posLexicon.addEntry(lexeme, lexeme, tagID, 0);
                }
                catch (Exception e) {
                    //System.err.println(e.getMessage() + " for word: " + lexeme);
                }
            }
        }
        return found;
    }

    public static void analyze(String lexeme, boolean firstWord, Lexicon posLexicon, TagSet tagSet, boolean onlyUnknowns)
    {
        if (wordHash.containsKey(lexeme))   // Then we have analyzed this word before
            return;

        Lexicon.Entry[] entries = posLexicon.getEntries(lexeme.toLowerCase());
        if (entries == null)    // Then an unknown word
        {
            if (!onlyUnknowns && lookup(lexeme, posLexicon, tagSet, morphLex.baseDict))
                return;
            //else if (lookup(lexeme, posLexicon, tagSet, BINlex))
            //    return;
            else
                analyzeUnknown(lexeme, firstWord, posLexicon, tagSet);
        }
        else
            if (!onlyUnknowns)// a known word => fill in the gaps in the tag profile
                getMissingTags(lexeme, firstWord, posLexicon, tagSet);

        wordHash.put(lexeme,null);    // Record that we have analyzed this word
    }


    public static void analyzeUnknown(String lexeme, boolean firstWord, Lexicon posLexicon, TagSet tagSet)
    {
        ArrayList<IceTag> tags;

        // Second argument: first word of the sentence?, Third argument: sentence starts with upper case?; Fourth argument: clean tags?
        tags = morphoAnalyzer.morphoAnalysisLexeme(lexeme, firstWord, false, true);


        // Add the IceTags into the Stagger lexicon
        for (int i=0; i<tags.size(); i++)
        {
            int tagID=-1;
            IceTag tag = (IceTag)tags.get(i);
            try {
                tagID =  tagSet.getTagID(tag.getTagStr());
                posLexicon.addEntry(lexeme, lexeme, tagID, 0);
            }
            catch (Exception e) {
                //System.err.println(e.getMessage() + " for word: " + lexeme);
                //System.exit(1);
            }
        }
    }

    public static void getMissingTags(String lexeme, boolean firstWord, Lexicon posLexicon, TagSet tagSet)
    {
        IceTokenTags myToken = new IceTokenTags();
        myToken.lexeme = lexeme;

        Lexicon.Entry[] entries = posLexicon.getEntries(lexeme.toLowerCase());

        for(Lexicon.Entry entry : entries) {
            try {
                String tagStr =  tagSet.getTagName(entry.tag);
                myToken.addTag(tagStr);
            }
            catch (TagNameException e) {
                System.err.println(e.getMessage() + " for word: " + lexeme);
                //System.exit(1);
            }
        }

        // Stagger does a lower case lookup for all words. It might have given a lower case word a proper noun tag!
        // Then the word must have been unknown, but was found in Stagger's lexicon because of an upper case equivalent
        if (Character.isLowerCase(lexeme.charAt(0)) && myToken.isOnlyWordClass(IceTag.WordClass.wcProperNoun))
        {
            analyzeUnknown(lexeme, firstWord, posLexicon, tagSet);
            //System.err.println("Found lower case proper noun: " + lexeme);
            return;
        }

        IceTokenTags myTokenWithMissingTags = myToken.makeCopy();
        // The morpho analyzer over-generates for verbs and Stagger accuracy decreases if this is allowed
        if (myTokenWithMissingTags.isVerbAny())
            return;
        else
            morphoAnalyzer.generateMissingTags(myTokenWithMissingTags);  // New tags are added at the end

        int start =  myToken.numTags();
        int end =  myTokenWithMissingTags.numTags();
        if (start < end)        // Then more tags after generation by IceMorphy
        {
            // Add the new tags to the posLexicon
            ArrayList<IceTag> iceTags = myTokenWithMissingTags.getTags();
            for (int i=start; i<end; i++) {          // Using the fact that new tags are added at the end
                IceTag tag = iceTags.get(i);
                try {
                    int tagID =  tagSet.getTagID(tag.getTagStr());
                    posLexicon.addEntry(lexeme, lexeme, tagID, 0);
                }
                catch (Exception e) {
                    //System.err.println(e.getMessage() + " for word: " + lexeme);
                }
            }
        }
    }
}
